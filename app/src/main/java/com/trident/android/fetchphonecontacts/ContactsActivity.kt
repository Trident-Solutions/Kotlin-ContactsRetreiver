package com.trident.android.fetchphonecontacts

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.ContactsContract
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_contacts.*
import java.util.*

/**
 * @author SURYA DEVI
 */
class ContactsActivity : AppCompatActivity(), View.OnClickListener {

    internal lateinit var selectUsers: ArrayList<ContactsModel>
    internal lateinit var recyclerView: RecyclerView
    private var layoutManager: RecyclerView.LayoutManager? = null
    internal lateinit var fetchContactsAdapter: ContactsAdapter
    internal lateinit var phones: Cursor
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    private val PICK_CONTACT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_contacts)

        initializeView()

        getStartShimmer()

        setSearchFilter()

        img_clear.setOnClickListener(this)
    }

    private fun initializeView() {
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.title = getString(R.string.contacts)

        recyclerView = findViewById<RecyclerView>(R.id.contacts_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        selectUsers = ArrayList()
    }

    private fun setSearchFilter() {
        edt_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                img_clear.visibility = View.VISIBLE
                filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    private fun getStartShimmer() {
        shimmerLayout.startShimmer()

        Handler().postDelayed({
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
            showContacts()
        }, 1000)
    }

    private fun showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), PERMISSIONS_REQUEST_READ_CONTACTS)
        } else {
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)
            phones = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")!!
                val loadContact =
                   LoadContact(this)
                loadContact.execute()


        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.img_clear -> {
                edt_search.text!!.clear()
            }
        }
    }

    /**
     *    Case 1: User doesn't have permission
     *    Case 2: User has permission
     *    Case 3: User has never seen the permission Dialog
     *    Case 4: User has denied permission once but he din't clicked on "Never Show again" check box
     *    Case 5: User denied the permission and also clicked on the "Never Show again" check box.
     *    Case 6: User has allowed the permission
     *
     */

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // This is Case 2 (Permission is now granted)
                showContacts()
            } else {
                // This is Case 1 again as Permission is not granted by user

                //Now further we check if used denied permanently or not
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_CONTACTS
                    )
                ) {
                    // case 4 User has denied permission but not permanently

                    requestPermissions(
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        PERMISSIONS_REQUEST_READ_CONTACTS
                    )

                } else {
                    // case 5. Permission denied permanently.
                    // You can open Permission setting's page from here now.
                    val alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.setTitle(getString(R.string.permission_required))
                        .setMessage(getString(R.string.denied_msg) + getString(R.string.open_settings))
                        .setPositiveButton(getString(R.string.ok)) { _, _ ->
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts(getString(R.string.scheme), packageName, null)
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivityForResult(intent, PERMISSIONS_REQUEST_READ_CONTACTS)
                        }.setNegativeButton(getString(R.string.cancel)) { _, _ ->

                        }.show()
                }
            }
        }
    }

    /**
     *  By using this method,contacts list is searched by name or number
     */
    private fun filter(text: String) {
        //new array list that will hold the filtered data
        val filterdNames = ArrayList<ContactsModel>()

        //looping through existing elements
        for (s in selectUsers) {
            //if the existing elements contains the search input
            if (s.contactName!!.toLowerCase(Locale.getDefault()).contains(text.toLowerCase(Locale.getDefault()))) {
                //adding the element to filtered list
                filterdNames.add(s)
            } else if (s.contactNumber!!.contains(text)) {
                filterdNames.add(s)
            }
        }
        //calling a method of the adapter class and passing the filtered list
        fetchContactsAdapter.filterList(filterdNames)
    }

    /*
    To handle the back button to go back from this activity
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {

                var cursor: Cursor? = null
                try {
                    val phoneNo: String?
                    val uri = data!!.data!!
                    cursor = contentResolver.query(uri, null, null, null, null)
                    assert(cursor != null)
                    cursor!!.moveToFirst()
                    val phoneIndex =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    phoneNo = cursor.getString(phoneIndex)

                    edt_search.setText(phoneNo)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    cursor?.close()
                }
            }
        }
    }
}



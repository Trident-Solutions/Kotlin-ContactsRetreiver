package com.trident.android.fetchphonecontacts

import android.os.AsyncTask
import android.provider.ContactsContract
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import java.util.ArrayList
import java.util.HashSet

 class LoadContact internal constructor(val contactsActivity: ContactsActivity) : AsyncTask<Any, Any, Any>() {
     private val TAG = "Remove Contact"
     override fun doInBackground(vararg params: Any?): Any {
            val normalizedNumbersAlreadyFound = HashSet<String>()
            val indexOfNormalizedNumber =
                contactsActivity.phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)
            val indexOfDisplayName =
                contactsActivity.phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val indexOfDisplayNumber =
                contactsActivity.phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val indexOfDisplayPhoto =
                contactsActivity.phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)
            while (contactsActivity.phones.moveToNext()) {

                val normalizedNumber = contactsActivity.phones.getString(indexOfNormalizedNumber)

                if (normalizedNumbersAlreadyFound.add(normalizedNumber)) {
                    val name = contactsActivity.phones.getString(indexOfDisplayName)
                    val phoneNumber = contactsActivity.phones.getString(indexOfDisplayNumber)
                    val image_thumb = contactsActivity.phones.getString(indexOfDisplayPhoto)

                    val selectUser = ContactsModel()
                    selectUser.contactName = name
                    selectUser.contactNumber = phoneNumber
                    selectUser.contactImageUrl = image_thumb
                    contactsActivity.selectUsers.add(selectUser)
                }
            }

            return 0
        }

        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun onPostExecute(result: Any?) {
            super.onPostExecute(result)
            val removed = ArrayList<ContactsModel>()
            val contacts = ArrayList<ContactsModel>()
            val regex = "\\d+(?:\\.\\d+)?".toRegex()  //Regular expressions are used for text searching

            for (i in contactsActivity.selectUsers.indices) {
                val inviteFriendsProjo = contactsActivity.selectUsers[i]

                if (inviteFriendsProjo.contactName!!.matches(regex) || inviteFriendsProjo.contactName!!.trim().isEmpty()) {
                    removed.add(inviteFriendsProjo)
                    Log.d(TAG, Gson().toJson(inviteFriendsProjo))
                } else {
                    contacts.add(inviteFriendsProjo)
                }
            }
            contacts.addAll(removed)
            contactsActivity.selectUsers = contacts
            contactsActivity.fetchContactsAdapter =
                ContactsAdapter(contactsActivity, contactsActivity.selectUsers)
            contactsActivity.recyclerView.layoutManager =
                LinearLayoutManager(contactsActivity.applicationContext)
            contactsActivity.recyclerView.adapter = contactsActivity.fetchContactsAdapter

        }

}

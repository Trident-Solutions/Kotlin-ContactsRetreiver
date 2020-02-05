package com.trident.android.fetchphonecontacts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.ArrayList
import android.widget.LinearLayout


/**
 * @author SURYA DEVI
 */
class ContactsAdapter(
    val context: Context,
    var contactList: List<ContactsModel>
) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    private lateinit var list: ContactsModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsAdapter.ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.rowview_contacts_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: ContactsAdapter.ViewHolder, position: Int) {
        list = contactList.get(position)

        val name = list.contactName
        holder.title.setText(name)
        holder.phone.setText(list.contactNumber)
        Glide.with(context).load(list.contactImageUrl)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_contact)
            .into(holder.imageContacts)

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById(R.id.txt_contact_name) as TextView
        val phone = itemView.findViewById(R.id.txt_mobile_number) as TextView
        val imageContacts = itemView.findViewById(R.id.img_contact) as ImageView
    }

    fun filterList(filterdNames: ArrayList<ContactsModel>) {
        contactList = filterdNames
        notifyDataSetChanged()
    }

}
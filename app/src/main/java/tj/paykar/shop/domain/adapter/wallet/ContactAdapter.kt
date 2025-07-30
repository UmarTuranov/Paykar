package tj.paykar.shop.domain.adapter.wallet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tj.paykar.shop.R
import tj.paykar.shop.data.model.wallet.ContactModel
import tj.paykar.shop.databinding.WalletItemContactBinding

class ContactsAdapter(
    private val contacts: ArrayList<ContactModel>
) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = WalletItemContactBinding.bind(itemView)
        fun bind(contact: ContactModel) = with(binding){
            contactNameTitle.text = contact.name ?: "Без имени"
            contactPhoneTitle.text = contact.phone ?: ""
            initialsTitle.text = getInitials(contact.name)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.wallet_item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        return holder.bind(contacts[position])
    }

    override fun getItemCount(): Int = contacts.size

    private fun getInitials(name: String?): String {
        return name?.split(" ")?.mapNotNull { it.firstOrNull()?.toString() }
            ?.joinToString("")?.uppercase()?.take(2) ?: "??"
    }
}

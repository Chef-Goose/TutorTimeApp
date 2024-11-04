package com.example.tutorapp

import android.view.LayoutInflater
import android.view.View
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.Gravity

class MessageAdapter(private val messages: List<Message>, private val currentUserID: String) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.message_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.messageText.text = message.message
        Log.d("MessageAdapter", "Binding message: ${message.message}")

        if (message.senderID == currentUserID) {
            holder.messageText.setBackgroundResource(R.drawable.sent_message_background)
            val layoutParams = holder.messageText.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.marginStart = 500 // Align to the right
            layoutParams.marginEnd = 0
            holder.messageText.layoutParams = layoutParams
            holder.messageText.gravity = Gravity.END // Align text to the end
        } else {
            holder.messageText.setBackgroundResource(R.drawable.received_message_background)
            val layoutParams = holder.messageText.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.marginStart = 0
            layoutParams.marginEnd = 100 // Align to the left
            holder.messageText.layoutParams = layoutParams
            holder.messageText.gravity = Gravity.START // Align text to the start
        }
    }

    override fun getItemCount() = messages.size
}

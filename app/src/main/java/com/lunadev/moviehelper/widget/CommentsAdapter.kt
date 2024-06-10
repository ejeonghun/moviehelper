package com.lunadev.moviehelper.widget

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lunadev.moviehelper.databinding.ItemCommentBinding
import com.lunadev.moviehelper.model.ReplyElement
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CommentsAdapter : ListAdapter<ReplyElement, CommentsAdapter.CommentViewHolder>(DiffCallback()) {

    class CommentViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: ReplyElement) {
            binding.textViewCommentContent.text = comment.content
            val zonedDateTime = ZonedDateTime.parse(comment.created_at) // String -> ZonedDateTime 변환
            val localDateTime = zonedDateTime.toLocalDateTime() // ZonedDateTime -> LocalDateTime 변환
            val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm") // 포맷팅
            val formattedDate = localDateTime.format(formatter)

            binding.textViewCommentCreatedDate.text = formattedDate
            Log.d("CommentsAdapter", "댓글 바인딩 ${comment.content}, ${comment.created_at}")
        }
    }

    fun clear() {
        submitList(emptyList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        Log.d("CommentsAdapter", "뷰홀더 생성")
        return CommentViewHolder(binding)
    }



    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = getItem(position)
        holder.bind(comment)
    }

    class DiffCallback : DiffUtil.ItemCallback<ReplyElement>() {
        override fun areItemsTheSame(oldItem: ReplyElement, newItem: ReplyElement): Boolean {
            return oldItem.replyId == newItem.replyId
        }

        override fun areContentsTheSame(oldItem: ReplyElement, newItem: ReplyElement): Boolean {
            return oldItem == newItem
        }
    }
}
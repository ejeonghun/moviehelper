// ActorsAdapter.kt
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lunadev.moviehelper.databinding.ItemActorBinding
import com.lunadev.moviehelper.model.MovieInfo

class ActorsAdapter : ListAdapter<MovieInfo.Data.Result.Actors.Actor, ActorsAdapter.ActorViewHolder>(DiffCallback()) {

    class ActorViewHolder(private val binding: ItemActorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(actor: MovieInfo.Data.Result.Actors.Actor) {
            binding.textViewActorName.text = actor.actorNm ?: "Unknown Actor"
            binding.textViewActorEnglishName.text = actor.actorEnNm ?: "Unknown Actor"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder {
        val binding = ItemActorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActorViewHolder(binding)
    }

    fun clear() {
        submitList(emptyList())
    }

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        val actor = getItem(position)
        holder.bind(actor)
    }

    class DiffCallback : DiffUtil.ItemCallback<MovieInfo.Data.Result.Actors.Actor>() {
        override fun areItemsTheSame(oldItem: MovieInfo.Data.Result.Actors.Actor, newItem: MovieInfo.Data.Result.Actors.Actor): Boolean {
            return oldItem.actorId == newItem.actorId
        }

        override fun areContentsTheSame(oldItem: MovieInfo.Data.Result.Actors.Actor, newItem: MovieInfo.Data.Result.Actors.Actor): Boolean {
            return oldItem == newItem
        }
    }
}

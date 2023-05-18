package com.example.feelie.adapter

// import utilities and packages
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.feelie.R
import com.example.feelie.model.Mood

class MoodAdapter(val context: Context,
                  val moodList: ArrayList<Mood>):
    RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    // initialize recyclerview
    class MoodViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        // initialize variables
        var moodIcon: ImageView
        var moodTitle: TextView
        var moodDate: TextView
        var moodNote: TextView

        // connect variables to UI elements
        init {
            moodIcon = itemView.findViewById(R.id.entry_mood)
            moodTitle = itemView.findViewById(R.id.entry_emotion)
            moodDate = itemView.findViewById(R.id.entry_datetime)
            moodNote = itemView.findViewById(R.id.entry_notes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.mood_item_details, parent, false)

        // display list of moods
        return MoodViewHolder(view)
    }

    // get total number of moods in list
    override fun getItemCount(): Int {
        return moodList.size
    }

    // set variables to data entered in UI elements
    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood = moodList[position]

        holder.moodTitle.text = mood.moodTitle
        holder.moodIcon.setBackgroundResource(setMoodIcon(mood))
        holder.moodNote.text = mood.moodNotes
        holder.moodDate.text = mood.moodDate
    }

    // set mood icon based on emotion from content description
    private fun setMoodIcon(mood: Mood): Int {
        if (mood.moodTitle == "Neutral") {
            return R.drawable.neutral
        }
        if (mood.moodTitle == "Confused") {
            return R.drawable.confused
        }
        if (mood.moodTitle == "Happy") {
            return R.drawable.happy
        }
        if (mood.moodTitle == "Mad") {
            return R.drawable.mad
        }
        if (mood.moodTitle == "Sad") {
            return R.drawable.sad
        }
        if (mood.moodTitle == "Nervous") {
            return R.drawable.nervous
        }
        if (mood.moodTitle == "Shock") {
            return R.drawable.shock
        }
        if (mood.moodTitle == "Excited") {
            return R.drawable.excited
        }
        return if (mood.moodTitle == "Calm") {
            R.drawable.calm
        } else {
            R.drawable.neutral
        }
    }

}

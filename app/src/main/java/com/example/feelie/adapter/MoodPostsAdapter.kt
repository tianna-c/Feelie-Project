package com.example.feelie.adapter

// import utilities and packages
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.feelie.R
import com.example.feelie.model.Mood
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MoodPostsAdapter(val context: Context,
                       val moodList: ArrayList<Mood>):
    RecyclerView.Adapter<MoodPostsAdapter.MoodViewHolder>() {

    class MoodViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        // initialize variables
        var moodIcon: ImageView
        var moodTitle: TextView
        var moodDate: TextView
        var moodPublisher: TextView

        // connect variables to UI elements
        init {
            moodIcon = itemView.findViewById(R.id.entry_mood)
            moodTitle = itemView.findViewById(R.id.entry_emotion)
            moodDate = itemView.findViewById(R.id.entry_datetime)
            moodPublisher = itemView.findViewById(R.id.entry_username)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.mood_item, parent, false)

        // return view
        return MoodViewHolder(view)
    }

    // get total number of moods from list
    override fun getItemCount(): Int {
        return moodList.size
    }

    // get each mood post from list
    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood = moodList[position]

        // create authentication instance
        FirebaseAuth.getInstance()

        // call retrieve username function
        retrieveUsername(mood, holder)

        // set mood post data
        holder.moodTitle.text = mood.moodTitle
        holder.moodIcon.setBackgroundResource(setMoodIcon(mood))
        holder.moodDate.text = mood.moodDate
    }

    // retrieve username by mood publisher in mood post data
    private fun retrieveUsername(mood: Mood, holder: MoodViewHolder) {
        val dataRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(mood.moodPublisher)

        // edit most post text to display publisher's username
        dataRef.addListenerForSingleValueEvent(object: ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val username = snapshot.child("username").value as String
                    holder.moodPublisher.text = "$username is feeling "
                } else {
                    holder.moodPublisher.text = "Unknown"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Do nothing
            }
        })
    }

    // set mood icon
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

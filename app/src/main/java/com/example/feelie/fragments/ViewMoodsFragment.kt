package com.example.feelie.fragments

// import utilities and packages
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feelie.R
import com.example.feelie.adapter.MoodAdapter
import com.example.feelie.model.Mood
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewMoodsFragment : Fragment() {

    // initialize variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var moodAdapter: MoodAdapter
    private lateinit var moodList: ArrayList<Mood>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // create list of moods
        moodList = arrayListOf()

        // initialize view
        val view = inflater.inflate(R.layout.fragment_mood_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerview_mood_details)
        recyclerView.setHasFixedSize(true)

        // set layout to display posts in reverse order so the most recent posts are first
        val layoutDisplay = LinearLayoutManager(context)
        layoutDisplay.reverseLayout = true
        layoutDisplay.stackFromEnd = true
        recyclerView.layoutManager = layoutDisplay

        // initialize adapter
        moodAdapter = MoodAdapter(requireContext(), moodList)
        recyclerView.adapter = moodAdapter

        // call retrieve moods function
        retrieveMoods(moodAdapter)

        // return display
        return view
    }

    // retrieve moods function
    private fun retrieveMoods(moodAdapter: MoodAdapter) {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Moods")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // get current user
                val firebaseUser = FirebaseAuth.getInstance().currentUser!!

                // clear mood list
                moodList.clear()

                // if the mood publisher is the current user, display mood entries
                for (index in snapshot.children) {
                    val mood = index.getValue(Mood::class.java)
                    if (mood != null) {
                        if (mood.moodPublisher == firebaseUser.uid) {
                            moodList.add(mood)
                        }
                    }
                }

                moodAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Do nothing
            }
        })
    }
}
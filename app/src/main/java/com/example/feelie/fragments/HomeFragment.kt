package com.example.feelie.fragments

//import utilities and packages
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feelie.R
import com.example.feelie.adapter.MoodPostsAdapter
import com.example.feelie.model.Mood
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    // initialize variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var moodPostsAdapter: MoodPostsAdapter
    private lateinit var moodList: ArrayList<Mood>
    private var followingList: MutableList<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // create list of moods
        moodList = arrayListOf()

        // initialize display
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recyclerview_mood)
        recyclerView.setHasFixedSize(true)

        // set layout to display posts in reverse order so the most recent posts are first
        val layoutDisplay = LinearLayoutManager(context)
        layoutDisplay.reverseLayout = true
        layoutDisplay.stackFromEnd = true
        recyclerView.layoutManager = layoutDisplay

        // initialize adapter
        moodPostsAdapter = MoodPostsAdapter(requireContext(), moodList)
        recyclerView.adapter = moodPostsAdapter

        // call get following list function
        getFollowingList()

        // return display
        return view
    }

    // get following function
    private fun getFollowingList() {

        // initialize following list
        followingList = ArrayList()

        // get reference of Follow and Following database tables
        val followRef = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("Following")

        // update display when following and unfollowing users
        followRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    (followingList as ArrayList<String>).clear()

                    for (dataSnapshot in snapshot.children) {
                        dataSnapshot.key?.let {
                            (followingList as ArrayList<String>).add(it)
                        }
                    }

                    // call retrieve follower posts function
                    retrieveFollowPosts()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // do nothing
            }
        })
    }

    // retrieve follower posts
    private fun retrieveFollowPosts() {

        // get reference for Moods database table
        val moodPostRef = FirebaseDatabase.getInstance().reference.child("Moods")

        // listener for when new posts are entered
        moodPostRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    // clear list
                    moodList.clear()

                    // if current user is following another user, the other user's entries are visible
                    for(dataSnapshot in snapshot.children) {
                        val moodPost = dataSnapshot.getValue(Mood::class.java)

                        // add posts to list
                        for (id in (followingList as ArrayList<String>)) {
                            if (moodPost!!.moodPublisher == id)
                                moodList!!.add(moodPost)
                        }

                        moodPostsAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //do nothing
            }
        })
    }
}
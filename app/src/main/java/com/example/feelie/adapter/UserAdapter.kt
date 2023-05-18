package com.example.feelie.adapter

// import necessary utilities and packages
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.feelie.R
import com.example.feelie.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserAdapter(val context: Context,
                  val userList: ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // create firebase user object
    private lateinit var firebaseUser: FirebaseUser

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        // initialize variables
        var textUserName: TextView
        var textName: TextView
        var followButton: Button

        // connect variables to UI elements
        init {
            textUserName = itemView.findViewById(R.id.search_username)
            textName = itemView.findViewById(R.id.search_fullname)
            followButton = itemView.findViewById(R.id.follow_button)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        //val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        val view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false)

        // return list of users
        return UserViewHolder(view)
    }

    // returns total number of items in list
    override fun getItemCount(): Int {
        return userList.size
    }

    // called by the recyclerview to display data
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser!! // get current user

        // find current user from list of users
        val currentUser = userList[position]

        // set user data in holder elements
        holder.textUserName.text = currentUser.username
        holder.textName.text = currentUser.fullname

        // call fun to check if following users
        isFollowed(currentUser.uid!!, holder.followButton)

        // current user should not have a follow button beside their name
        if (currentUser.uid == firebaseUser.uid) {
            holder.followButton.visibility = View.GONE
        }

        // click listener for following and unfollowing users
        holder.followButton.setOnClickListener {
            // follow users
            if (holder.followButton.text == "Follow") {
                firebaseUser.uid.let {
                    FirebaseDatabase.getInstance().reference.child("Follow")
                        .child(it).child("Following").child(currentUser.uid!!)
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "You are now following ${currentUser.username}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            else {
                // unfollow users
                if (holder.followButton.text == "Following") {
                    firebaseUser.uid.let {
                        FirebaseDatabase.getInstance().reference.child("Follow")
                            .child(it).child("Following").child(currentUser.uid!!)
                            .removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "You unfollowed ${currentUser.username}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
            }
            }
        }

    // check if following users from database reference
    private fun isFollowed(uid: String, followButton: Button) {
        val followingRef: DatabaseReference = firebaseUser.uid.let {
            FirebaseDatabase.getInstance().reference.child("Follow").child(it).child("Following")
        }

        // change follow button's text
        followingRef.addValueEventListener(object: ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(uid).exists()) {
                    followButton.text = "Following"
                }
                else {
                    followButton.text = "Follow"
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Do nothing
            }
        })
    }
    }
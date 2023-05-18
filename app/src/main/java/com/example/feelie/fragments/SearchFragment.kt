package com.example.feelie.fragments

// import utilities and packages
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feelie.R
import com.example.feelie.adapter.UserAdapter
import com.example.feelie.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {

    // initialize variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList: ArrayList<User>
    private lateinit var searchBar: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // initialize list of users
        userList = arrayListOf()

        // display list of users using recyclerview
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView = view.findViewById(R.id.recyclerview_search)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        userAdapter = UserAdapter(requireContext(), userList)
        recyclerView.adapter = userAdapter

        // pair variable with UI element
        searchBar = view.findViewById(R.id.searchItem)

        // retrieve list of users
        retrieveUsers()

        // return display
        return view
    }

    // retrieve all registered users
    private fun retrieveUsers() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // clear list
                userList.clear()

                // add existing users to list
                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }

                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // leave empty
            }

        })
    }
}
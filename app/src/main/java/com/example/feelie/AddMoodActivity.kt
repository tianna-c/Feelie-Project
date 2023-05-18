package com.example.feelie

// import utilities and packages
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.DateFormat.getDateTimeInstance

class AddMoodActivity : Fragment() {

    // initialize variables
    private lateinit var moodTitle: String
    private lateinit var moodButtons: Array<ImageView>
    private lateinit var moodNotes: EditText
    private lateinit var logMoodButton: Button
    private var selectedIndex = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_add_mood, container, false)

        // this will keep track of which button has been selected
        val isSelected = mutableSetOf<Int>()

        // list of mood buttons
        moodButtons = arrayOf(
            view.findViewById(R.id.ic_confused),
            view.findViewById(R.id.ic_excited),
            view.findViewById(R.id.ic_neutral),
            view.findViewById(R.id.ic_calm),
            view.findViewById(R.id.ic_shock),
            view.findViewById(R.id.ic_nervous),
            view.findViewById(R.id.ic_happy),
            view.findViewById(R.id.ic_sad),
            view.findViewById(R.id.ic_mad)
        )

        // retrieve the mood based on the button that has been selected
        for (moodButton in moodButtons) {
            moodButton.setOnClickListener {
                moodTitle = moodButton.contentDescription as String

                selectedIndex = moodButtons.indexOf(moodButton)

                // Check if the image has been selected and change its color
                if (!isSelected.contains(selectedIndex)) {
                    isSelected.add(selectedIndex)
                    moodButton.setColorFilter(Color.BLUE)
                }
                // revert to default color
                else {
                    isSelected.remove(selectedIndex)
                    moodButton.setColorFilter(Color.BLACK)
                }
            }
        }

        // set notes to note UI element
        moodNotes = view.findViewById(R.id.mood_notes)

        // use the button click listener to call the store moods function
        logMoodButton = view.findViewById(R.id.log_mood_button)
        logMoodButton.setOnClickListener {
            storeMood() // call store moods function
        }

        return view
    }

    // function to store mood entry data into database
    private fun storeMood() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!
        val moodRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Moods")

        // if button has not been selected or if the Notes field is empty, prompt user to make a proper entry
        if (selectedIndex == -1 || moodNotes.text.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a mood and write a note first!", Toast.LENGTH_SHORT)
                .show()
        }
        else {

            // retrieve data
            val moodNote = moodNotes.text.toString()
            val date = System.currentTimeMillis()
            //val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val sdf = getDateTimeInstance()
            val moodDate = sdf.format(date)

            // create the map
            val moodMap = HashMap<String, Any>()
            moodMap["moodPublisher"] = currentUserId
            moodMap["moodTitle"] = moodTitle
            moodMap["moodNotes"] = moodNote
            moodMap["moodDate"] = moodDate

            // record moods and store in database
            moodRef.push().setValue(moodMap)
                .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Your mood has been recorded", Toast.LENGTH_SHORT).show()
                    moodMap.clear()
                }
                else {
                    val message = task.exception!!.toString()
                    Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
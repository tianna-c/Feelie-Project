package com.example.feelie.model

import java.io.Serializable

data class Mood(
    var moodTitle: String,
    var moodNotes: String,
    var moodPublisher: String,
    var moodDate: String
): Serializable {
    constructor() : this("", "", "", "")
}

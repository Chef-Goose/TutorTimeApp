package com.example.tutorapp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

data class Person(val personID: String? = null, val courseName: String? = null )


class TimesDatabase{
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("Times")

    fun writePersonForTime(time: String){
        val person = Person("Tom","Math")

        database.child("Monday1").child(time).setValue(person)
    }

}

// Create a dictionary of times, and each key's value would be a list
// of this class that contains each person that is available for that time.
// the class holds a person's name and the course they teach.
// For example, Dictionary Monday would have a first key of 8am,
// and that key has a value of a list of this class
// so the value of Monday[8:00am] could be [person1,person2],
// where each person would have a name and a course they want to teach
// The dictionary for each day of a month is then going to be written to the database
// For example the first monday of the month would have a
// dictionary called Mon1, and the second monday would be Mon2
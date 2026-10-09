package com.example.listycity

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CityRepository {
    private val db = Firebase.firestore
    private val citiesRef = db.collection("cities")
    private val _cities = mutableStateListOf(
        City("Edmonton", "AB"),
        City("Vancouver", "BC"),
        City("Toronto", "ON")
    )

    val cities: List<City>
        get() = _cities

    fun addCity(city: City) {
        citiesRef.document(city.name).set(city)
    }

    fun updateCity(oldCity: City, updatedCity: City) {
        citiesRef.document(oldCity.name).set(updatedCity)
    }

    //NOTE: Using just citiesRef.document(city.name).delete() was sometimes causing issue
    //deleting some cities because of a document ID mismatch
    //So I tried this method instead and it worked
    // instead of searching for the document ID, I search for a document where the name field matches
    //This made a little more bulletproof
    fun deleteCity(city: City) {
        citiesRef.whereEqualTo("name", city.name)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }
    }


    init{
        citiesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            _cities.clear()

            snapshot?.documents?.forEach { document ->
                val city = document.toObject(City::class.java)
                if (city != null) {
                    _cities.add(city)
                }
            }
        }

    }

}
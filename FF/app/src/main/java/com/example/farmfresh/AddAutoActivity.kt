package com.example.farmfresh

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.activity_addauto.*
import java.util.*

class AddAutoActivity : AppCompatActivity(){
    lateinit var placesClient: PlacesClient
    var placeFields = Arrays.asList(
        Place.Field.ID,
        Place.Field.NAME,
        Place.Field.ADDRESS)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addauto)

        initPlaces()
        setupPlacesAutocomplete()

    }

    private fun initPlaces(){
        Log.d("AddAutoActivity","Function : initplaces")
        Places.initialize(this,getString(R.string.api_key))
        placesClient = Places.createClient(this)
        Log.d("AutofillActivity","Inside AutoFill")
    }

    private  fun setupPlacesAutocomplete(){
        Log.d("AddAutoActivity","Function : setupplaces")
        val autocompleteFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_autofill) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(placeFields)
        autocompleteFragment.setOnPlaceSelectedListener(object: PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                Log.d("AddAutoActivity","Address selected succesfully")
                val returnIntent:Intent = Intent()
                returnIntent.putExtra("address","${p0.address}")
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }

            override fun onError(p0: Status) {
                Log.d("AutoFillActivity","Error occured during address select")
            }

        })
    }


}
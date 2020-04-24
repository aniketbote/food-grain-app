package com.example.farmfresh

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*

class AutoFillActivity : AppCompatActivity(){
    lateinit var placesClient: PlacesClient
    var placeFields = Arrays.asList(
        Place.Field.ID,
        Place.Field.NAME,
        Place.Field.ADDRESS)

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_autofill)
        Log.d("AutofillActivity","Inside AutoFill")

        initPlaces()
        setuoPlacesAutocomplete()

    }
    private fun initPlaces(){
        Places.initialize(this,getString(R.string.api_key))
        placesClient = Places.createClient(this)
        Log.d("AutofillActivity","Inside AutoFill")
    }
    private  fun setuoPlacesAutocomplete(){
        val autocompleteFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_autofill) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(placeFields)
        autocompleteFragment.setOnPlaceSelectedListener(object: PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                Log.d("Registeractivity","Success : $p0")
            }

            override fun onError(p0: Status) {
                Log.d("Registeractivity","Error : $p0")
            }

        } )
    }
}
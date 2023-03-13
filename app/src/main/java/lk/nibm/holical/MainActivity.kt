package lk.nibm.holical

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    // FusedLocationProviderClient is a class that provides access to the Fused Location Provider API.
    // The Fused Location Provider API is a system service that provides access to location data.
    // Declare a lateinit var of type FusedLocationProviderClient
    private lateinit var fusedLocation: FusedLocationProviderClient

    // Declare a variable for the view
    private lateinit var txtLocation: TextView
    private lateinit var btnCheckLocation: Button

    // Declare a boolean var and set it to false
    // This is used to check if the permission is granted or not
    var isPermissionGranted:Boolean = false

    // Declare a constant for the location request code and set it to 100 (any number will do)
    // This is used to check the result of the permission request
    private val LOCATION_REQUEST_CODE = 100

    private lateinit var spnActMainCountry: Spinner
    private lateinit var spnActMainYear : Spinner

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the Spinner and EditText
        spnActMainCountry = findViewById(R.id.spnActMainCountry)
        spnActMainYear = findViewById(R.id.spnActMainYear)

        // Set up the ArrayAdapter for the Spinner
        val countries = arrayOf("Sri Lanka", "United Kingdom", "United States", "Canada", "Australia")
        val years = arrayOf(2023, 2022, 2021, 2020, 2019, 2018, 2017, 2016, 2015)
        val countryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnActMainCountry.adapter = countryAdapter
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnActMainYear.adapter = yearAdapter

        // Set up the click listener for the button
        findViewById<Button>(R.id.btnActMainGetHolidays).setOnClickListener {
            // Get the selected country and year
            val country = spnActMainCountry.selectedItem.toString()
            var year = spnActMainYear.selectedItem.toString().toInt()

            var countryCode : String? = getCountryCode(country)

            // Send data to HolidayList Activity
            var intent1 = Intent(this@MainActivity, HolidayList::class.java)
            intent1.putExtra("country", countryCode)
            intent1.putExtra("year", year)
            startActivity(intent1)
        }

        // Initialize the fusedLocation var with the FusedLocationProviderClient class
        // The FusedLocationProviderClient class is used to request location updates and get the last known location.
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)

        // Call the checkLocationPermission() method to check if the app has the permission to access the location of the device
        // This method call when the app is first launched
        checkLocationPermission()
        getDevDateTime()

    }

    fun getWhetherData(){
        var loc = getLocation()
        var url = "https://api.openweathermap.org/data/2.5/weather?lat=${loc?.latitude}&lon=${loc?.longitude}&appid=882ee6bf596e3c46852c2c851073a175"
        val request = StringRequest(
            Request.Method.GET,
            url,
            Response.Listener{ response ->
                try {
                    val jsonObject = JSONObject(response).getJSONObject("response")
                } catch (e : Error){

                }
            },
            Response.ErrorListener{ error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        Volley.newRequestQueue(applicationContext).add(request)
    }

    private fun getCountryCode(countryName: String): String? {
        val locales = Locale.getAvailableLocales()

        for (locale in locales) {
            val name = locale.displayCountry
            if (name == countryName) {
                return locale.country
            }
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDevDateTime(){

        // codes to set current date and time from the device
        // Get current date and time
        val currentDateTime = LocalDateTime.now()

        // Set year to TextView
        val lblActMainCurrYear: TextView = findViewById(R.id.lblActMainCurrYear)
        lblActMainCurrYear.text = currentDateTime.year.toString()

        // Set month to TextView
        val lblActMainCurrMonth: TextView = findViewById(R.id.lblActMainCurrMonth)
        val monthFormatter = DateTimeFormatter.ofPattern("MMMM")
        lblActMainCurrMonth.text = currentDateTime.format(monthFormatter)

        // Set day to TextView
        val lblActMainCurrDay: TextView = findViewById(R.id.lblActMainCurrDay)
        val dayFormatter = DateTimeFormatter.ofPattern("dd")
        lblActMainCurrDay.text = currentDateTime.format(dayFormatter)

        // Set hour to TextView
        val lblActMainCurrHM: TextView = findViewById(R.id.lblActMainCurrHM)
        val hourFormatter = DateTimeFormatter.ofPattern("hh : mm")
        lblActMainCurrHM.text =  currentDateTime.format(hourFormatter)

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() : Location? {
        var lastLocation : Location? = null
        // Check if the permission is granted or not
        if (isPermissionGranted){
            // If the permission is granted, then get the last known location of the device
            // The last known location is the last location that the Fused Location Provider API has access to.
            // The last known location is not necessarily the last location that the device was at.
            val locationResult = fusedLocation.lastLocation

            // Add an OnCompleteListener to the locationResult
            // The OnCompleteListener is called when the task is complete and the result is available.
            locationResult.addOnCompleteListener(this){location ->
                // Check if the location is successful
                // If the location is successful, then get the last location of the device
                // The last location is the last location that the device was at.
                if (location.isSuccessful) {
                    // Get the last location of the device
                    lastLocation = location.result

                    // Set the text of the txtLocation to the latitude and longitude of the last location
                    // txtLocation.text = "Latitude: ${lastLocation.latitude}, Longitude: ${lastLocation.longitude}"
                }
            }
        }
        return lastLocation
    }

    private fun checkLocationPermission() {
        // Check if the app has the permission
        // If the app does not has the permission, then request the permission
        // If the app does has the permission, then do something
        // The request code used in ActivityCompat.requestPermissions() and returned in the
        // callback onRequestPermissionsResult() is used to identify the request.
        // It can be any integer value you like. The returned result contains the request code so that
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        isPermissionGranted = false

        // Check if the request code is the same as the request code used in ActivityCompat.requestPermissions()
        // If the request code is the same, then check if the permission is granted or not
        // If the permission is granted, then set the isPermissionGranted var to true
        when(requestCode){ LOCATION_REQUEST_CODE -> {
            // Check if the permission is granted or not and set the isPermissionGranted var to true or false
            // If the permission is granted, then set the isPermissionGranted var to true
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted = true
            } else {
                // If the permission is not granted, then set the isPermissionGranted var to false
                // and show a toast message to the user that the permission is denied and the app cannot get the location of the device without the permission
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}

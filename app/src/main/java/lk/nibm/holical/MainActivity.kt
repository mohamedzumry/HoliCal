package lk.nibm.holical

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocation: FusedLocationProviderClient
    private var isPermissionGranted:Boolean = false
    private val LOCATION_REQUEST_CODE = 100

    private lateinit var spnActMainCountry: Spinner
    private lateinit var spnActMainYear : Spinner

    private lateinit var lblTemperature : TextView
    private  lateinit var lblActMainPressure : TextView
    private lateinit var lblActMainHumidity : TextView
    private lateinit var lblActMainWeaType : TextView
    private lateinit var imgWeaIcon : ImageView
    private lateinit var lblActMainCityname : TextView
    private lateinit var lblActMainLatLon: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        // Initialize the fusedLocation var with the FusedLocationProviderClient class
        // The FusedLocationProviderClient class is used to request location updates and get the last known location.
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)

        lblActMainLatLon = findViewById(R.id.lblActMainLatLon)
        // Call the checkLocationPermission() method to check if the app has the permission to access the location of the device
        // This method call when the app is first launched
        checkLocationPermission()

        getDevDateTime()
        getLocation()

        // Initialize the Spinner and EditText
        spnActMainCountry = findViewById(R.id.spnActMainCountry)
        spnActMainYear = findViewById(R.id.spnActMainYear)

        // Initialize Weather card TextViews
        lblActMainCityname = findViewById(R.id.lblActMainCityname)
        lblTemperature = findViewById(R.id.lblTemperature)
        lblActMainWeaType = findViewById(R.id.lblActMainWeaType)
        lblActMainPressure = findViewById(R.id.lblActMainPressure)
        lblActMainHumidity= findViewById(R.id.lblActMainHumidity)
        imgWeaIcon= findViewById(R.id.imgWeaIcon)

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

    }

    fun getWeatherData(lat: Double, lon: Double) {

        lateinit var temperature :String
        lateinit var pressure :String
        lateinit var humidity : String
        lateinit var weaType : String
        lateinit var icon : String
        lateinit var cityName : String

        var url = "https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=882ee6bf596e3c46852c2c851073a175"
        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                try {
                    // Successfully received weather data from the API
                    // Parse the JSON response and update the UI with weather details
                    val weatherData = JSONObject(response)
                    val mainObject = weatherData.optJSONObject("main")
                    temperature = mainObject.getString("temp")
                    pressure = mainObject.getString("pressure")
                    humidity = mainObject.getString("humidity")

                    val weatherArray = weatherData.getJSONArray("weather")
                    val weatherObject = weatherArray.getJSONObject(0)
                    weaType = weatherObject.getString("main")
                    icon = weatherObject.getString("icon")

                    val sysObject = weatherData.optJSONObject("sys")
                    cityName = sysObject.getString("name")

                } catch (e: JSONException) {
                    Toast.makeText(this, "Error in API  : ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error in API  : ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        Volley.newRequestQueue(applicationContext).add(request)

        lblActMainCityname.text = cityName
        lblTemperature.text = temperature+" 'C"
        lblActMainWeaType.text = weaType
        lblActMainPressure.text = "Pressure : "+pressure+" hPa"
        lblActMainHumidity.text = "Humidity : "+humidity+" %"

        Glide.with(this)
            .load("https://openweathermap.org/img/w/$icon.png")
            .into(imgWeaIcon)
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
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
                    val lastLocation = location.result

                    lblActMainLatLon.text = "Latitude: ${lastLocation.latitude}, Longitude: ${lastLocation.longitude}"

                    getWeatherData(lastLocation.latitude, lastLocation.longitude)

                    // Set the text of the txtLocation to the latitude and longitude of the last location
                    // txtLocation.text = "Latitude: ${lastLocation.latitude}, Longitude: ${lastLocation.longitude}"
                }
            }
        }
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

        // Set Day, Month & year to TextView
        val lblActMainCurrDMY: TextView = findViewById(R.id.lblActMainCurrDMY)
        val monthFormatter = DateTimeFormatter.ofPattern("dd / MMMM / YYYY")
        lblActMainCurrDMY.text = "Today is -> "+currentDateTime.format(monthFormatter)

    }
}

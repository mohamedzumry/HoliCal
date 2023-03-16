package lk.nibm.holical

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONException
import org.json.JSONObject

class WeatherDetails : AppCompatActivity() {

    private lateinit var lblTemperature : TextView
    private  lateinit var lblActMainPressure : TextView
    private lateinit var lblActMainHumidity : TextView
    private lateinit var lblActMainWeaType : TextView
    private lateinit var imgWeaIcon : ImageView
    private lateinit var lblActMainCityname : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_details)

        var latitude = intent.getStringExtra("latitude")
        var longitude = intent.getStringExtra("longitude")
        Toast.makeText(this, "$longitude , $latitude", Toast.LENGTH_LONG).show()

        supportActionBar?.title = "Weather details"

        // Initialize Weather card TextViews
        lblActMainCityname = findViewById(R.id.lblActMainCityname)
        lblTemperature = findViewById(R.id.lblTemperature)
        lblActMainWeaType = findViewById(R.id.lblActMainWeaType)
        lblActMainPressure = findViewById(R.id.lblActMainPressure)
        lblActMainHumidity= findViewById(R.id.lblActMainHumidity)
        imgWeaIcon= findViewById(R.id.imgWeaIcon)

        getWeatherData(latitude, longitude)
    }

    fun getWeatherData(lat: String?, lon: String?) {

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

                    lblActMainCityname.text = cityName
                    lblTemperature.text = temperature+" 'C"
                    lblActMainWeaType.text = weaType
                    lblActMainPressure.text = "Pressure : "+pressure+" hPa"
                    lblActMainHumidity.text = "Humidity : "+humidity+" %"

                    Glide.with(this)
                        .load("https://openweathermap.org/img/w/$icon.png")
                        .into(imgWeaIcon)

                } catch (e: JSONException) {
                    Toast.makeText(this, "Error in API  : ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error in API  : ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        Volley.newRequestQueue(applicationContext).add(request)
    }
}
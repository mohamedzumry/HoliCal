package lk.nibm.holical

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
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

        getDevDateTime()

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
        lblActMainCurrHM.text = currentDateTime.format(hourFormatter)

    }


}

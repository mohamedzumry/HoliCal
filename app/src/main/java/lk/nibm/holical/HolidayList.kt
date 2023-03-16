package lk.nibm.holical

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class HolidayList : AppCompatActivity() {
    lateinit var RVHolidayList: RecyclerView
    var holidayDataArray = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_holiday_list)

        // Get the support action bar
        supportActionBar?.apply {
            // Set the background color
            setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@HolidayList, R.color.skyblue)))
        }

        RVHolidayList = findViewById(R.id.RVHolidayList)
        RVHolidayList.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        RVHolidayList.adapter = HolidayListAdapter()

        val country = intent.getStringExtra("country")
        val year = intent.getIntExtra("year", 0)

        supportActionBar?.title = "$country Holidays for $year"

        getHolidays(country, year)
    }

    fun getHolidays(country: String?, year: Int?) {

        val url = "https://calendarific.com/api/v2/holidays?api_key=e9890a1160f7533402c85658eeda0f5b1356d8d8&country=$country&year=$year"

        val request = StringRequest(
            Request.Method.GET,
            url,
            Response.Listener{ response ->
                try {
                    val jsonObject = JSONObject(response).getJSONObject("response")
                    holidayDataArray = jsonObject.getJSONArray("holidays")
                    RVHolidayList.adapter?.notifyDataSetChanged()
                } catch (e : Error){

                }
            },
            Response.ErrorListener{ error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        Volley.newRequestQueue(applicationContext).add(request)
    }

    inner class HolidayListAdapter() : RecyclerView.Adapter<HolidaysViewHolder>(){


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidaysViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.holiday_list_item, parent, false)
            return HolidaysViewHolder(view)
        }

        override fun getItemCount(): Int {
            return holidayDataArray.length()
        }

        override fun onBindViewHolder(holder: HolidaysViewHolder, position: Int) {
            try {
                var holiObject = holidayDataArray.getJSONObject(position)
                var holiName = holiObject.getString("name")
                var holiISO = holiObject.getJSONObject("date").getString("iso")
                var holiDate = holiObject.getJSONObject("date").getJSONObject("datetime").getString("day")
                var holiMonth = holiObject.getJSONObject("date").getJSONObject("datetime").getString("month")
                var holiYear = holiObject.getJSONObject("date").getJSONObject("datetime").getString("year")
                var holiDesc = holiObject.getString("description")
                var holiPriType = holiObject.getString("primary_type")
                var monthName = getMonthName(holiMonth.toInt())

                if (position == 0 || holiMonth.toInt() != holidayDataArray.getJSONObject(position - 1).getJSONObject("date").getJSONObject("datetime").getString("month").toInt()) {
                    holder.lblActHolidayListMonthHeader.visibility = View.VISIBLE
                } else {
                    holder.lblActHolidayListMonthHeader.visibility = View.GONE
                }

                holder.lblActHolidayListMonthHeader.text = monthName
                holder.holiName.text = holiName
                holder.holiDate.text = holiISO

                holder.card.setOnClickListener {
                    var intent1 = Intent(this@HolidayList, HolidayDetails::class.java)
                    intent1.putExtra("HoliName", holiName)
                    intent1.putExtra("HoliDate", holiDate)
                    intent1.putExtra("HoliMonth", holiMonth)
                    intent1.putExtra("HoliYear", holiYear)
                    intent1.putExtra("HoliISO", holiISO)
                    intent1.putExtra("HoliDesc", holiDesc)
                    intent1.putExtra("HoliPriType", holiPriType)
                    startActivity(intent1)
                }
            } catch (e : Error){
                Toast.makeText(applicationContext, "Error : ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        fun getMonthName(monthNumber: Int): String {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MONTH, monthNumber - 1)
            return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        }

    }

    inner class HolidaysViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val lblActHolidayListMonthHeader : TextView = itemView.findViewById(R.id.lblActHolidayListMonthHeader)
        val holiName : TextView = itemView.findViewById(R.id.txtLay_holiday_list_item_HoliName)
        val holiDate : TextView = itemView.findViewById(R.id.txtLay_holiday_list_item_HoliDate)
        val card : MaterialCardView = itemView.findViewById(R.id.holi_list_item_card)
    }

}

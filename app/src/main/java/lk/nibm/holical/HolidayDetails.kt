package lk.nibm.holical

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class HolidayDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_holiday_details)

        var holiName = intent.getStringExtra("HoliName")
        var holiISO = intent.getStringExtra("HoliISO")
        var holiDate = intent.getStringExtra("HoliDate")
//        var holiMonth = intent.getStringExtra("HoliMonth")
//        var holiYear = intent.getStringExtra("HoliYear")
        var holiDesc = intent.getStringExtra("HoliDesc")
        var holiPriType = intent.getStringExtra("HoliPriType")

        val txtLayHolidayDetailsHoliName = findViewById<TextView>(R.id.txtLay_holiday_details_HoliName)
        val txtLayHolidayDetailsHoliISO = findViewById<TextView>(R.id.txtLay_holiday_details_HoliISO)
        val txtLayHolidayDetailsHoliPriType = findViewById<TextView>(R.id.txtLay_holiday_details_HoliPriType)
        val txtLayHolidayDetailsHoliDesc = findViewById<TextView>(R.id.txtLay_holiday_details_Desc)

        txtLayHolidayDetailsHoliName.text = holiName
        txtLayHolidayDetailsHoliPriType.text = holiPriType
        txtLayHolidayDetailsHoliISO.text = holiISO
        txtLayHolidayDetailsHoliDesc.text = holiDesc
    }
}
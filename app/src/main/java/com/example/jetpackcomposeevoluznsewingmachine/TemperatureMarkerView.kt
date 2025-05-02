package com.example.jetpackcomposeevoluznsewingmachine

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class TemperatureMarkerView(
    context: Context, private val xData: List<String>,val unit:String
) : MarkerView(context, R.layout.marker_view) {

    private val tvContent: TextView = findViewById(R.id.tvContent)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            val day = xData.getOrNull(e.x.toInt()) ?: "Unknown"
            val value = e.y.toInt()
            tvContent.text = "$day: $value $unit"
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}

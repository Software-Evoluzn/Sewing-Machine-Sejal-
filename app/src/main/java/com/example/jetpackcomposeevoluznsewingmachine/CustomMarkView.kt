package com.example.jetpackcomposeevoluznsewingmachine

import android.content.Context
import android.widget.TextView


import com.example.jetpackcomposeevoluznsewingmachine.ModalClass.GraphDataModel
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class CustomMarkView(
    context: Context,
    layoutResource:Int,
    private val hourlyData: List<GraphDataModel>
): MarkerView(context,layoutResource) {
        private val tvContent:TextView=findViewById(R.id.tvContent)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val index = e?.x?.toInt() ?: 0
        if (index in hourlyData.indices) {
            val data = hourlyData[index]
            val text = "Hour: ${data.xLabel}\n" +
                    "Run: ${data.runTime/ 3600}h\n" +
                    "Idle: ${data.idleTime / 3600}h\n" +
                    "Cycles: ${data.cycleCount}"

            tvContent.text = text
        }
        super.refreshContent(e, highlight)
    }

        override fun getOffset(): MPPointF {
            return MPPointF(-(width / 2).toFloat(), -height.toFloat())
        }

}
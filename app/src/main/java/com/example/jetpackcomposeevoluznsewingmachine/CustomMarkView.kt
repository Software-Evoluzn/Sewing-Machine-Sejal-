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
            val runtimeHrs=data.runTime/3600f
            val idletimeHrs=data.idleTime/3600f
            val labelTitle = if(data.labelType == "hour"){
                             "hour"
            }else if(data.labelType =="Day"){
                            "Day"
            }else{
                            "Date"
            }

            val text = "$labelTitle: ${data.xLabel}\n" +
                    "Run: %.2f h\n".format(runtimeHrs) +
                    "Idle: %.2f h\n".format(idletimeHrs) +
                    "Cycles: ${data.cycleCount}"

            tvContent.text = text
        }
        super.refreshContent(e, highlight)
    }

        override fun getOffset(): MPPointF {
            return MPPointF(-(width / 2).toFloat(), -height.toFloat())
        }

}
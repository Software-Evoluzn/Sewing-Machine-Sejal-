package com.example.jetpackcomposeevoluznsewingmachine.ModalClass

 data class GraphDataModel(

     val xLabel:String,
     val runTime:Double,
     val idleTime:Double,
     val cycleCount:Int,
     val labelType: String = "hour"
 )

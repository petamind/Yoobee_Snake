package com.tungnd.yoobeesnake.utils

import android.graphics.PointF

class Maths {
     companion object {
         fun CosineSim(v1: PointF, v2: PointF): Double {
                 return (v1.x * v2.x + v1.y * v2.y) / (Math.sqrt((v1.x * v1.x + v1.y * v1.y).toDouble()) *
                     Math.sqrt((v2.x * v2.x + v2.y * v2.y).toDouble()))
         }
     }

}
package com.tungnd.yoobeesnake.utils

import android.graphics.PointF

class Maths {
     companion object {
         /**
          * Find similarity of two vector represented in PointF
          */
         fun CosineSim(v1: PointF, v2: PointF): Double {
             return (v1.x * v2.x + v1.y * v2.y) / (Math.sqrt((v1.x * v1.x + v1.y * v1.y).toDouble()) *
                     Math.sqrt((v2.x * v2.x + v2.y * v2.y).toDouble()))
         }

         /**
          * @param rotateVector vector to rotate from original cartesian coordinate
          * @param x: original x cooordinate
          * @param y original y coordinate
          * @return the transposed PointF containing new coordinates x, y
          */
         fun RotatedCoordinate(x: Float, y: Float, rotateVector: PointF): PointF {

             val diag = Math.sqrt(
                 (rotateVector.y * rotateVector.y +
                         rotateVector.x * rotateVector.x).toDouble()
             )
             val sinOfCurrentMoveVec = rotateVector.y / diag
             val cosOfCurrentMoveVec = rotateVector.x / diag

             val dxTransposed = x * cosOfCurrentMoveVec - y * sinOfCurrentMoveVec
             val dyTransposed = x * sinOfCurrentMoveVec + y * cosOfCurrentMoveVec
             return PointF(dxTransposed.toFloat(), dyTransposed.toFloat())
         }
     }

}
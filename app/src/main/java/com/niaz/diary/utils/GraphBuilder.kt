package com.niaz.diary.utils

import android.graphics.*
import com.niaz.diary.db.NoteEntity
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GraphBuilder @Inject constructor(){
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    fun buildGraph(noteEntities: List<NoteEntity>): Bitmap {
        val points = noteEntities.mapNotNull { note ->
            try {
                val date = note.date?.let { dateFormat.parse(it) } ?: return@mapNotNull null
                val value = note.note?.toFloatOrNull() ?: return@mapNotNull null
                date to value
            } catch (e: Exception) {
                null
            }
        }.sortedBy { it.first }

        if (points.isEmpty()) {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }

        val padding = 100
        val bottomOffset = 40f // Y offset
        val width = 1000
        val height = 600
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val paintAxis = Paint().apply {
            color = Color.BLACK
            strokeWidth = 4f
            style = Paint.Style.STROKE
        }

        val paintText = Paint().apply {
            color = Color.BLACK
            textSize = 28f
        }

        val paintLine = Paint().apply {
            color = Color.BLUE
            strokeWidth = 4f
            isAntiAlias = true
        }

        val paintPoint = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val minDate = points.first().first.time.toFloat()
        val maxDate = points.last().first.time.toFloat()
        val minValue = points.minOf { it.second }
        val maxValue = points.maxOf { it.second }

        fun mapX(date: Date): Float {
            val ratio = (date.time - minDate) / (maxDate - minDate)
            return padding + ratio * (width - 2 * padding)
        }

        fun mapY(value: Float): Float {
            val ratio = (value - minValue) / (maxValue - minValue)
            return height - padding - bottomOffset - ratio * (height - 2 * padding)
        }

        // axes
        canvas.drawLine(padding.toFloat(), height - padding.toFloat(), width - padding.toFloat(), height - padding.toFloat(), paintAxis)
        canvas.drawLine(
            padding.toFloat(),
            mapY(maxValue),
            padding.toFloat(),
            height - padding.toFloat(),
            paintAxis
        )

        // text on axes
        canvas.drawText(
            maxValue.toString(),
            10f,
            mapY(maxValue) + paintText.textSize / 2,
            paintText
        )

        canvas.drawText(
            minValue.toString(),
            10f,
            mapY(points.first().second) + paintText.textSize / 2,
            paintText
        )

        // Text of dates
        canvas.drawText(dateFormat.format(points.first().first), padding.toFloat(), height - 20f, paintText)
        canvas.drawText(dateFormat.format(points.last().first), width - padding.toFloat() - 100f, height - 20f, paintText)


        // Graph
        for (i in 0 until points.size - 1) {
            val (date1, value1) = points[i]
            val (date2, value2) = points[i + 1]

            val x1 = mapX(date1)
            val y1 = mapY(value1)
            val x2 = mapX(date2)
            val y2 = mapY(value2)

            canvas.drawLine(x1, y1, x2, y2, paintLine)
        }

        // Points on graph
        for ((date, value) in points) {
            canvas.drawCircle(mapX(date), mapY(value), 6f, paintPoint)
        }

        return bitmap
    }
}

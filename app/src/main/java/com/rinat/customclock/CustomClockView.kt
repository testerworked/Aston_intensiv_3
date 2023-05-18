package com.rinat.customclock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import java.util.*

class CustomClockView : View {
    private val TAG = "CustomClock"
    private var paintCirclePan
            : Paint? = null
    private var paintText
            : Paint? = null
    private var paintHour
            : Paint? = null
    private var paintMinute
            : Paint? = null
    private var paintSecond
            : Paint? = null
    private var paintCenterCir
            : Paint? = null
    private var radias
            = 0
    private var WidthScan // ширина экрана
            = 0
    private var HeightScan // высота экрана
            = 0
    private var mcalendar // Получить время
            : Calendar? = null
    private val easyHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                NEED_INVALIDATE -> {
                    // Получить время
                    mcalendar = Calendar.getInstance()
                    // Перерисовываем интерфейс
                    invalidate() // сообщаем основному потоку интерфейса перерисовать
                    // Отправить сообщение снова, вызвать его рекурсивно, снова отслеживать секундную стрелку
                    sendEmptyMessageDelayed(NEED_INVALIDATE, 1000)
                }
                else -> {}
            }
        }
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    override fun onDraw(canvas: Canvas) {
        initdata()
        drawCircle(canvas)
        drawCalibrationLine(canvas)
        drawLine(canvas)
    }

    // Рисуем руки
    private fun drawLine(canvas: Canvas) {
        paintHour!!.strokeWidth = 15f // временная шкала толщины линии
        paintMinute!!.strokeWidth = 10f // минутная отметка
        paintSecond!!.strokeWidth = 8f // секундная метка
        paintCenterCir!!.style = Paint.Style.FILL // Установить кисть на сплошной

        // Получить системное время
        var hour = mcalendar!![Calendar.HOUR]
        val minute = mcalendar!![Calendar.MINUTE]
        val second = mcalendar!![Calendar.SECOND]
        println(TAG + "Время набрало час:" + hour + " minute： " + minute + " second: " + second)
        if (hour > 12) {
            hour = hour - 12
        }
        val hourdregrees =
            hour / 12f * 360 + minute / 60f * 30 + 180 // Угол поворота часовой стрелки
        val minudegrees = minute / 60f * 360 + 180 // Угол поворота минутной стрелки
        val senconddegree = second / 60f * 360 + 180 // угол поворота секундной стрелки
        println(TAG + "Угол поворота" + hourdregrees + " minudegrees： " + minudegrees + " senconddegree: " + senconddegree)

        // Нарисовать движение часов
        canvas.drawCircle(
            (WidthScan / 2).toFloat(), (HeightScan / 2).toFloat(), 10f,
            paintCenterCir!!
        )

        // Переместить начальные координаты холста в центр круга
        // Нарисовать часовую стрелку
        canvas.save()
        canvas.rotate(
            hourdregrees,
            (WidthScan / 2).toFloat(),
            (HeightScan / 2).toFloat()
        ) // вращаем холст
        canvas.drawLine(
            (WidthScan / 2).toFloat(),
            (HeightScan / 2 - 30).toFloat(),
            (WidthScan / 2).toFloat(),
            (HeightScan / 2 + 150).toFloat(),
            paintHour!!
        ) // Рисуем отметки времени
        canvas.restore() // Объединить холст

        // Рисуем минутную стрелку
        canvas.save()
        canvas.rotate(
            minudegrees,
            (WidthScan / 2).toFloat(),
            (HeightScan / 2).toFloat()
        ) // вращаем холст
        canvas.drawLine(
            (WidthScan / 2).toFloat(),
            (HeightScan / 2 - 30).toFloat(),
            (WidthScan / 2).toFloat(),
            (HeightScan / 2 + 220).toFloat(),
            paintMinute!!
        ) // Рисуем отметки времени;
        canvas.restore() // Объединить холст


        // Нарисовать секундную стрелку
        canvas.save()
        canvas.rotate(
            senconddegree,
            (WidthScan / 2).toFloat(),
            (HeightScan / 2).toFloat()
        ) // вращаем холст
        canvas.drawLine(
            (WidthScan / 2).toFloat(),
            (HeightScan / 2 - 30).toFloat(),
            (WidthScan / 2).toFloat(),
            (HeightScan / 2 + 250).toFloat(),
            paintSecond!!
        ) // Рисуем отметки времени;
        canvas.restore() // Объединить холст
    }

    // Рисуем отметки и цифры выше
    private fun drawCalibrationLine(canvas: Canvas) {
        paintCirclePan!!.strokeWidth = 3f
        for (i in 0..59) {
            // Большая точка, 12 баллов 3 балла 6 баллов 9 баллов
            if (i == 0 || i == 15 || i == 30 || i == 45) {
                paintCirclePan!!.strokeWidth = 8f
                paintCirclePan!!.textSize = 60f // Установить размер шрифта
                canvas.drawLine(
                    (WidthScan / 2).toFloat(),
                    (HeightScan / 2 - radias).toFloat(),
                    (WidthScan / 2).toFloat(),
                    (HeightScan / 2 - radias + 60).toFloat(),
                    paintCirclePan!!
                ) // Установить начальную позицию отметок
                var degree = (i / 5).toString()
                if (i == 0) {
                    degree = "12"
                }
                canvas.drawText(
                    degree,
                    WidthScan / 2 - paintCirclePan!!.measureText(degree) / 2,
                    (HeightScan / 2 - WidthScan / 2 + 180).toFloat(),
                    paintCirclePan!!
                ) // Установить шрифт для галочки
            } else if (i % 5 == 0) { //// целые отметки и цифры
                paintCirclePan!!.strokeWidth = 6f
                paintCirclePan!!.textSize = 40f
                val degree = (i / 5).toString()
                canvas.drawLine(
                    (WidthScan / 2).toFloat(),
                    (HeightScan / 2 - radias).toFloat(),
                    (WidthScan / 2).toFloat(),
                    (HeightScan / 2 - radias + 40).toFloat(),
                    paintCirclePan!!
                )
                canvas.drawText(
                    degree,
                    WidthScan / 2 - paintCirclePan!!.measureText(degree) / 2,
                    (HeightScan / 2 - WidthScan / 2 + 160).toFloat(),
                    paintCirclePan!!
                )
            } else {
                paintCirclePan!!.strokeWidth = 3f
                paintCirclePan!!.textSize = 20f
                canvas.drawLine(
                    (WidthScan / 2).toFloat(),
                    (HeightScan / 2 - radias).toFloat(),
                    (WidthScan / 2).toFloat(),
                    (HeightScan / 2 - radias + 20).toFloat(),
                    paintCirclePan!!
                )
            }
            // Поворот холста на 3 градуса после завершения каждого рисунка
            canvas.rotate(6f, (WidthScan / 2).toFloat(), (HeightScan / 2).toFloat())
        }

        // Холст с циферблатом и шкалой
        canvas.save()
    }

    // Инициализируем данные
    private fun initdata() {
        WidthScan = this.width // Получить ширину экрана
        HeightScan = this.height // Получить высоту экрана
        paintCirclePan = Paint()
        paintHour = Paint()
        paintMinute = Paint()
        paintSecond = Paint()
        paintText = Paint()
        paintCenterCir = Paint()
        mcalendar = Calendar.getInstance() // Получить объект времени
        radias = Math.min(
            WidthScan,
            HeightScan
        ) / 2 // Выберите минимальное значение из высоты и ширины в качестве стандарта для радиуса
        radias = radias - 50 // Устанавливаем поля 50
        println(TAG + "Радиус круга равен:" + radias)

        // Отправить сообщение для мониторинга секундной стрелки
        easyHandler.sendEmptyMessage(NEED_INVALIDATE)
    }

    // Рисуем круг
    private fun drawCircle(canvas: Canvas) {
        println(TAG + "Нарисуй круг")
        paintCirclePan!!.reset()
        paintCirclePan!!.color = Color.BLACK
        paintCirclePan!!.strokeWidth = 5f
        paintCirclePan!!.style = Paint.Style.STROKE
        paintCirclePan!!.isAntiAlias = true
        canvas.drawCircle(
            (WidthScan / 2).toFloat(), (HeightScan / 2).toFloat(), radias.toFloat(),
            paintCirclePan!!
        )
    }

    companion object {
        const val NEED_INVALIDATE = 0x23
    }
}
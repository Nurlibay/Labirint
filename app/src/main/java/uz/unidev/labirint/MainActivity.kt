package uz.unidev.labirint

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private val sensor: Sensor by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    private lateinit var ballImage: ImageView
    private val repository: Repository = RepositoryImpl()
    private val map = repository.getMapByLevel(1)

    private var cWidth = 0
    private var cHeight = 0

    private var ballI = -1
    private var ballJ = -1

    private var _width: Int? = null
    private var _height: Int? = null

    private lateinit var container: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        container = findViewById(R.id.container)

        container.post {
            cWidth = container.width
            cHeight = container.height
            loadView()
        }
    }

    private fun loadView() {
        _width = cWidth / 30
        _height = cHeight / 20

        var bI = 0
        var bJ = 0

        map.forEachIndexed { i, rows ->
            rows.forEachIndexed { j, value ->
                if (value == 2) {
                    bI = i
                    bJ = j
                }

                if (value == 1) {
                    val img = ImageView(this)
                    img.scaleType = ImageView.ScaleType.FIT_XY
                    container.addView(img)
                    img.layoutParams.apply {
                        width = _width!!
                        height = _height!!
                    }
                    img.x = _width!! * j * 1f
                    img.y = _height!! * i * 1f
                    img.setImageResource(R.drawable.cube)
                }
            }
        }

        addBall(bI, bJ, _width!!, _height!!)
    }

    private fun addBall(i: Int, j: Int, _width: Int, _height: Int) {
        ballI = i
        ballJ = j
        ballImage = ImageView(this)
        ballImage.setImageResource(R.drawable.metallball)
        ballImage.scaleType = ImageView.ScaleType.FIT_XY
        container.addView(ballImage)
        ballImage.layoutParams.apply {
            width = _width
            height = _height
        }
        ballImage.x = _width * j * 1f
        ballImage.y = _width * i * 1f
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent?.let { event ->
            val y = event.values[0]
            val x = event.values[1]

            if (_width != null) {
                var ballNewX = 1f
                var ballNewY = 1f

                ballNewX = if (x > 0) {
                    if (map[ballI][ballJ + 1] == 1) {
                        ballImage.x
                    } else {
                        ballImage.x + _width!!.toFloat()
                    }
                } else {
                    if (map[ballI][ballJ - 1] == 1) {
                        ballImage.x
                    } else {
                        ballImage.x - _width!!.toFloat()
                    }
                }

                ballNewY = if (y > 0) {
                    if (map[ballI + 1][ballJ] == 1) {
                        ballImage.y
                    } else {
                        ballImage.y + _height!!.toFloat()
                    }
                } else {
                    if (map[ballI - 1][ballJ] == 1) {
                        ballImage.y
                    } else {
                        ballImage.y - _height!!.toFloat()
                    }
                }

                ballImage.x = ballNewX
                ballImage.y = ballNewY
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // onAccuracyChanged should be initialized here
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}
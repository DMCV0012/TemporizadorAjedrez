package com.example.myapplication

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var topTimer: TextView
    private lateinit var bottomTimer: TextView
    private lateinit var tapToStartTop: TextView
    private lateinit var tapToStartBottom: TextView
    private lateinit var startButton: Button
    private lateinit var pauseButton: Button
    private lateinit var resetButton: Button
    private var lastActiveTimer: String? = null

    // TextViews del GridLayout
    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    private var topTimerInstance: CountDownTimer? = null
    private var bottomTimerInstance: CountDownTimer? = null
    private var timeLeftInMillisTop: Long = 300000 // 5 minutos
    private var timeLeftInMillisBottom: Long = 300000 // 5 minutos
    private var isTopTimerRunning = false
    private var isBottomTimerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Vincular vistas
        topTimer = findViewById(R.id.top_timer)
        bottomTimer = findViewById(R.id.bottom_timer)
        tapToStartTop = findViewById(R.id.tap_to_start_top)
        tapToStartBottom = findViewById(R.id.tap_to_start_bottom)
        startButton = findViewById(R.id.button_1_top)
        pauseButton = findViewById(R.id.button_2_top)
        resetButton = findViewById(R.id.button_3_top)

        // Vincular los TextView del GridLayout
        firstTextView = findViewById(R.id.first_textview)
        secondTextView = findViewById(R.id.second_textview)

        // Mostrar el tiempo inicial en ambos temporizadores
        updateTimersText()

        // Configurar botón de inicio
        startButton.setOnClickListener { resumeTimer() }

        // Configurar eventos de toque en los TextView "Tap to start"
        tapToStartTop.setOnClickListener {
            pauseTopAndStartBottomTimer()
            changeTextViewColors() // Cambiar color cuando se toque
        }

        tapToStartBottom.setOnClickListener {
            pauseBottomAndStartTopTimer()
            changeTextViewColors() // Cambiar color cuando se toque
        }

        // Configurar botón de pausa
        pauseButton.setOnClickListener {
            pauseBothTimers()
        }

        // Configurar botón de reinicio
        resetButton.setOnClickListener { resetTimers() }
    }

    private fun resetTextViewColors() {
        firstTextView.setBackgroundColor(getColor(R.color.black)) // Arreglado
        secondTextView.setBackgroundColor(getColor(R.color.black)) // Arreglado
    }

    // Método para cambiar el color de los TextView en el GridLayout
    private fun changeTextViewColors() {
        firstTextView.setBackgroundColor(getColor(R.color.green)) // Cambiar a color verde
        secondTextView.setBackgroundColor(getColor(R.color.green)) // Cambiar a color verde
    }

    // Método para reanudar el temporizador correcto basado en el último activo
    private fun resumeTimer() {
        when (lastActiveTimer) {
            "TOP" -> startTopTimer()
            "BOTTOM" -> startBottomTimer()
            else -> startTopTimer() // Iniciar el superior si no hay uno activo
        }
    }

    private fun startTopTimer() {
        if (isTopTimerRunning || isBottomTimerRunning) return // Evitar múltiples inicios simultáneos

        // Deshabilitar el botón de inicio una vez que se ha iniciado el temporizador
        startButton.isEnabled = false

        resetTextViewColors()
        firstTextView.setBackgroundColor(getColor(R.color.green))

        // Reanudar el temporizador superior si estaba pausado
        topTimerInstance = object : CountDownTimer(timeLeftInMillisTop, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillisTop = millisUntilFinished
                updateTimersText()
            }

            override fun onFinish() {
                isTopTimerRunning = false
                startButton.isEnabled = true
            }
        }.start()

        isTopTimerRunning = true
        lastActiveTimer = "TOP" // Marcar el temporizador superior como activo
    }

    private fun startBottomTimer() {
        if (isBottomTimerRunning) return // Evitar múltiples inicios simultáneos

        resetTextViewColors()
        secondTextView.setBackgroundColor(getColor(R.color.green))

        // Reanudar el temporizador inferior si estaba pausado
        bottomTimerInstance = object : CountDownTimer(timeLeftInMillisBottom, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillisBottom = millisUntilFinished
                updateTimersText()
            }

            override fun onFinish() {
                isBottomTimerRunning = false
            }
        }.start()

        isBottomTimerRunning = true
        lastActiveTimer = "BOTTOM" // Marcar el temporizador inferior como activo
    }

    private fun pauseTopAndStartBottomTimer() {
        if (isTopTimerRunning) {
            topTimerInstance?.cancel()
            isTopTimerRunning = false

            // Cambiar colores: poner ambos TextView a verde
            resetTextViewColors()
            secondTextView.setBackgroundColor(getColor(R.color.green))
        }

        startBottomTimer()
    }

    private fun pauseBottomAndStartTopTimer() {
        if (isBottomTimerRunning) {
            bottomTimerInstance?.cancel()
            isBottomTimerRunning = false

            // Cambiar colores: poner ambos TextView a verde
            resetTextViewColors()
            firstTextView.setBackgroundColor(getColor(R.color.green))
        }

        startTopTimer()
    }

    private fun pauseBothTimers() {
        if (isTopTimerRunning) {
            topTimerInstance?.cancel()
            isTopTimerRunning = false
            lastActiveTimer = "TOP" // Guardar el temporizador superior como el último activo
        }
        if (isBottomTimerRunning) {
            bottomTimerInstance?.cancel()
            isBottomTimerRunning = false
            lastActiveTimer = "BOTTOM" // Guardar el temporizador inferior como el último activo
        }

        // Habilitar el botón de inicio para poder reanudar el temporizador
        startButton.isEnabled = true
    }

    private fun resetTimers() {
        topTimerInstance?.cancel()
        bottomTimerInstance?.cancel()

        timeLeftInMillisTop = 300000 // Reiniciar a 5 minutos
        timeLeftInMillisBottom = 300000 // Reiniciar a 5 minutos

        updateTimersText()
        isTopTimerRunning = false
        isBottomTimerRunning = false

        // Habilitar el botón de inicio después de reiniciar los temporizadores
        startButton.isEnabled = true

        // Restablecer los colores a su estado original
        resetTextViewColors()
    }

    private fun updateTimersText() {
        val minutesTop = (timeLeftInMillisTop / 1000) / 60
        val secondsTop = (timeLeftInMillisTop / 1000) % 60
        val timeFormattedTop = String.format("%02d:%02d", minutesTop, secondsTop)

        val minutesBottom = (timeLeftInMillisBottom / 1000) / 60
        val secondsBottom = (timeLeftInMillisBottom / 1000) % 60
        val timeFormattedBottom = String.format("%02d:%02d", minutesBottom, secondsBottom)

        // Actualizar ambos temporizadores
        topTimer.text = timeFormattedTop
        bottomTimer.text = timeFormattedBottom
    }
}

package com.dm.tutorialfullscreenactivity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.activity_fullscreen.*

/**
 * Un esempio di attività a schermo intero che mostra e nasconde l'interfaccia utente del sistema
 * (ovvero la barra di stato e la barra di navigazione / sistema) con l'interazione dell'utente.
 */
class FullscreenActivity : AppCompatActivity() {
    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Rimozione ritardata della barra di stato e della barra di navigazione

        // Notare che alcune di queste costanti sono nuove a partire dall'API 16 (Jelly Bean)
        // e dall'API 19 (KitKat). È sicuro usarli, poiché sono inline in fase di compilazione
        // e non fanno nulla sui dispositivi precedenti.
        fullscreen_content.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        // Visualizzazione ritardata degli elementi dell'interfaccia utente
        supportActionBar?.show()
        fullscreen_content_controls.visibility = View.VISIBLE
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }
    /**
     * Listener tattile da utilizzare per i controlli dell'interfaccia utente nel layout per
     * ritardare l'occultamento dell'interfaccia utente del sistema. Questo per evitare che il
     * comportamento stridente dei controlli vada via durante l'interazione con l'interfaccia
     * utente dell'attività.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fullscreen)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mVisible = true

        // Configura l'interazione dell'utente per mostrare o nascondere manualmente l'interfaccia utente del sistema.
        fullscreen_content.setOnClickListener { toggle() }

        // Dopo aver interagito con i controlli dell'interfaccia utente,
        // ritardare qualsiasi operazione hide () pianificata per evitare che il comportamento
        // stridente dei controlli scompaia durante l'interazione con l'interfaccia utente.
        dummy_button.setOnTouchListener(mDelayHideTouchListener)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Attiva l' hide() iniziale subito dopo la creazione dell'attività, per indicare
        // brevemente all'utente che i controlli dell'interfaccia utente sono disponibili.
        delayedHide(100)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Nascondi prima l'interfaccia utente
        supportActionBar?.hide()
        fullscreen_content_controls.visibility = View.GONE
        mVisible = false

        // Pianifica un runnable per rimuovere la barra di stato e la barra di navigazione dopo un ritardo
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Mostra la barra di sistema
        fullscreen_content.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Pianifica un runnable per visualizzare gli elementi dell'interfaccia utente dopo un ritardo
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Pianifica una chiamata a hide() in [delayMillis], annullamento di eventuali chiamate
     * programmate in precedenza.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Indica se l'interfaccia utente del sistema deve essere nascosta automaticamente dopo
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * Se [AUTO_HIDE] è impostato, il numero di millisecondi da attendere dopo l'interazione
         * dell'utente prima di nascondere l'interfaccia utente del sistema.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Alcuni dispositivi meno recenti richiedono un piccolo ritardo tra gli aggiornamenti
         * del widget dell'interfaccia utente e una modifica dello stato e della barra di navigazione.
         */
        private val UI_ANIMATION_DELAY = 300
    }
}

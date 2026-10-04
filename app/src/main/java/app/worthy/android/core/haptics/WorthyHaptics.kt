package app.worthy.android.core.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class WorthyHaptics(
    context: Context,
    private val isEnabled: () -> Boolean,
) {
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(VibratorManager::class.java).defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
    }

    fun toggleEnabled() = strongClick()
    fun goalCreated() = strongClick()
    fun contributionAdded() = strongClick()
    fun goalDeleted() = strongClick()

    fun cardSettled() {
        if (!canVibrate()) return
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                vibrator.vibrate(VibrationEffect.createOneShot(CARD_CLICK_DURATION_MS, preferredAmplitude(CARD_CLICK_AMPLITUDE)))
            }
            else -> vibrateLegacy(CARD_CLICK_DURATION_MS)
        }
    }

    fun goalCompleted() {
        if (!canVibrate()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = if (vibrator.hasAmplitudeControl()) {
                VibrationEffect.createWaveform(COMPLETION_TIMINGS, COMPLETION_AMPLITUDES, -1)
            } else {
                VibrationEffect.createWaveform(COMPLETION_TIMINGS, -1)
            }
            vibrator.vibrate(effect)
        } else {
            vibrateLegacy(COMPLETION_TIMINGS)
        }
    }

    private fun strongClick() {
        if (!canVibrate()) return
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                vibrator.vibrate(VibrationEffect.createOneShot(STRONG_CLICK_DURATION_MS, preferredAmplitude(STRONG_CLICK_AMPLITUDE)))
            }
            else -> vibrateLegacy(STRONG_CLICK_DURATION_MS)
        }
    }

    private fun preferredAmplitude(amplitude: Int): Int =
        if (vibrator.hasAmplitudeControl()) amplitude else VibrationEffect.DEFAULT_AMPLITUDE

    private fun canVibrate(): Boolean = shouldPerformWorthyHaptic(isEnabled(), vibrator.hasVibrator())

    @Suppress("DEPRECATION")
    private fun vibrateLegacy(durationMillis: Long) {
        vibrator.vibrate(durationMillis)
    }

    @Suppress("DEPRECATION")
    private fun vibrateLegacy(pattern: LongArray) {
        vibrator.vibrate(pattern, -1)
    }

    companion object {
        internal const val STRONG_CLICK_DURATION_MS = 45L
        internal const val STRONG_CLICK_AMPLITUDE = 210
        internal const val CARD_CLICK_DURATION_MS = 30L
        internal const val CARD_CLICK_AMPLITUDE = 180
        internal val COMPLETION_TIMINGS = longArrayOf(0L, 60L, 40L, 180L)
        internal val COMPLETION_AMPLITUDES = intArrayOf(0, 210, 0, 255)
    }
}

internal fun shouldPerformWorthyHaptic(enabled: Boolean, hasVibrator: Boolean): Boolean =
    enabled && hasVibrator

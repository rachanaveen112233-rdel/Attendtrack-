package com.attendtrack.telangana

import android.content.Context
import android.media.MediaPlayer

object Sounds {
    // File names in res/raw, without .mp3. Change them here if yours differ.
    private const val WTF = "fahhhh_sometimes_wtf"
    private const val VINE = "vine_boom"
    private const val FAH = "fahhhhh"
    private const val CHICKEN = "chicken"
    private const val ANIME = "anime"
    private const val YAH = "yahhhh"

    private var player: MediaPlayer? = null

    private fun pick(present: Boolean, pct: Double): String? =
        if (present) {
            if (pct >= 75) listOf(ANIME, YAH).random() else null
        } else when {
            pct > 75 -> FAH
            pct > 70 -> WTF
            pct > 65 -> VINE
            else -> CHICKEN
        }

    fun play(ctx: Context, present: Boolean, pct: Double) {
        val name = pick(present, pct) ?: return
        val id = ctx.resources.getIdentifier(name, "raw", ctx.packageName)
        if (id == 0) return
        player?.release()
        player = MediaPlayer.create(ctx, id)?.also { mp ->
            mp.setOnCompletionListener {
                it.release()
                if (player === it) player = null
            }
            mp.start()
        }
    }
}

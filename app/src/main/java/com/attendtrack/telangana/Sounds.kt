package com.attendtrack.telangana

import android.content.Context
import android.media.MediaPlayer

object Sounds {
    private var flip = false

    private fun play(ctx: Context, name: String) {
        val id = ctx.resources.getIdentifier(name, "raw", ctx.packageName)
        if (id == 0) return
        try {
            val mp = MediaPlayer.create(ctx, id) ?: return
            mp.setOnCompletionListener { it.release() }
            mp.start()
        } catch (e: Exception) {
        }
    }

    suspend fun afterTap(ctx: Context, dao: SubjectDao, present: Boolean) {
        val all = dao.allSubjects()
        val pct = Calculator.percent(all.sumOf { it.attended }, all.sumOf { it.held })
        val name: String? = if (present) {
            if (pct >= 75.0) {
                flip = !flip
                if (flip) "anime" else "yahhhh"
            } else null
        } else when {
            pct < 65.0 -> "chicken"
            pct < 70.0 -> "vine_boom"
            pct < 75.0 -> "fahhhhh_wtf"
            pct < 100.0 -> "fahhhhh"
            else -> null
        }
        if (name != null) play(ctx, name)
    }
}

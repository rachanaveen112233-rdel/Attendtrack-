package com.attendtrack.telangana

import java.time.DayOfWeek

object Timetable {
    private const val MATHS = "Engineering Mathematics-I"
    private const val CHEM = "Engineering Chemistry"
    private const val ENG = "Communicative English"
    private const val DLD = "DLD"
    private const val CLAB = "Chemistry Lab"
    private const val ELAB = "English Lab"
    private const val GRAPH = "Engineering Graphics"

    // Periods: 10-11, 11-12, 12-1, 2-3, 3-4, 4-5 (null = no class)
    val week: Map<DayOfWeek, List<String?>> = mapOf(
        DayOfWeek.MONDAY to listOf(ENG, DLD, MATHS, GRAPH, GRAPH, GRAPH),
        DayOfWeek.TUESDAY to listOf(CHEM, DLD, MATHS, DLD, ELAB, ELAB),
        DayOfWeek.WEDNESDAY to listOf(GRAPH, GRAPH, GRAPH, MATHS, ELAB, ELAB),
        DayOfWeek.THURSDAY to listOf(null, null, ENG, CLAB, CLAB, CLAB),
        DayOfWeek.FRIDAY to listOf(CHEM, CHEM, ENG, CLAB, CLAB, CLAB),
        DayOfWeek.SATURDAY to listOf(MATHS, MATHS, MATHS, CLAB, CLAB, CLAB)
    )
}

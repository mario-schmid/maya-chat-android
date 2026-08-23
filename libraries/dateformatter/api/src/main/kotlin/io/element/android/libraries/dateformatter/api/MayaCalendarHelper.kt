package io.element.android.libraries.dateformatter.api

import java.time.Instant
import java.time.ZoneId
import java.time.LocalDate
import io.element.android.libraries.dateformatter.api.R

object MayaCalendarHelper {
    private val strWinal = listOf("Pop'", "Wo'", "Sip'", "Sotz'", "Tz'ek", "Xul", "Yaxk'in", "Mol", "Ch'en", "Yax", "Sak", "Cej",
        "Mak", "Kank'in", "Muwan", "Pax", "K'ayab", "Kumk'u", "Wayeb")
    
    private val strTone = listOf("Jun", "Keb'", "Oxib'", "Kajib'", "Job'", "Waqib'", "Wuqub'", "Wajxaqib'", "B'elejeb'", "Lajuj", "Julajuj'", "Kab'lajuj", "Oxlajuj")
    
    private val strNahual = listOf("B'atz'", "E", "Aj", "Ix", "Tz'ikin", "Ajmaq", "No'j", "Tijax", "Kawoq", "Ajpu", "Imox", "Iq'", "Aq'ab'al", "K'at", "Kan", "Kame", "Kej", "Q'anil", "Toj", "Tz'i'")

    private val nahualResIds = listOf(
        R.drawable.n00_batz, R.drawable.n01_e, R.drawable.n02_aj, R.drawable.n03_ix,
        R.drawable.n04_tzikin, R.drawable.n05_ajmaq, R.drawable.n06_noj, R.drawable.n07_tijax,
        R.drawable.n08_kawoq, R.drawable.n09_ajpu, R.drawable.n10_imox, R.drawable.n11_iq,
        R.drawable.n12_aqabal, R.drawable.n13_kat, R.drawable.n14_kan, R.drawable.n15_kame,
        R.drawable.n16_kej, R.drawable.n17_qanil, R.drawable.n18_toj, R.drawable.n19_tzi
    )

    private val toneResIds = listOf(
        R.drawable.t01_green_curved_bottom, R.drawable.t02_green_curved_bottom, R.drawable.t03_green_curved_bottom,
        R.drawable.t04_green_curved_bottom, R.drawable.t05_green_curved_bottom, R.drawable.t06_green_curved_bottom,
        R.drawable.t07_green_curved_bottom, R.drawable.t08_green_curved_bottom, R.drawable.t09_green_curved_bottom,
        R.drawable.t10_green_curved_bottom, R.drawable.t11_green_curved_bottom, R.drawable.t12_green_curved_bottom,
        R.drawable.t13_green_curved_bottom
    )

    data class MayaDateResult(
        val day: Int,
        val winalName: String,
        val year: Int,
        val toneName: String,
        val nahualName: String,
        val toneResId: Int,
        val nahualResId: Int,
        val fullMayaText: String
    )

    fun getMayaDate(timestamp: Long): MayaDateResult {
        val localDate = Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

        val epochDays = localDate.toEpochDay()
        val days = 1860343 + epochDays
        
        val year = (days / 365).toInt() + 1
        val currDays = (days % 365).toInt()
        val winalIndex = currDays / 20
        val day = currDays % 20
        val winalName = strWinal.getOrElse(winalIndex) { winalIndex.toString() }
        
        val toneVal = (((days + 8) % 13) + 1).toInt()
        val nahualVal = ((days + 11) % 20).toInt()

        return MayaDateResult(
            day = day,
            winalName = winalName,
            year = year,
            toneName = strTone[toneVal - 1],
            nahualName = strNahual[nahualVal],
            toneResId = toneResIds[toneVal - 1],
            nahualResId = nahualResIds[nahualVal],
            fullMayaText = "$day $winalName $year"
        )
    }
}

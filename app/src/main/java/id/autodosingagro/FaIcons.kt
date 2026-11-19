package id.autodosingagro

import android.content.Context
import android.graphics.Typeface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Ikon Font Awesome Free Solid 6.5.2 — font dibundel lokal di
 * assets/fonts/fa-solid-900.ttf (offline, tanpa CDN).
 * Lisensi Font Awesome Free: ikon CC BY 4.0 — atribusi ada di layar Pengaturan.
 * Semua kode glif diverifikasi dari cmap font itu sendiri.
 */
object Fa {
    const val HOUSE = "\uF015"
    const val LEAF = "\uF06C"
    const val SEEDLING = "\uF4D8"
    const val CHART = "\uF201"
    const val USER = "\uF007"
    const val BELL = "\uF0F3"
    const val BOOK_OPEN = "\uF518"
    const val CALENDAR = "\uF073"
    const val SCALE = "\uF24E"
    const val CLIPBOARD = "\uF46D"
    const val BULLHORN = "\uF0A1"
    const val FOLDER = "\uF07C"
    const val CALC = "\uF1EC"
    const val FLASK = "\uF0C3"
    const val DROPLET = "\uF043"
    const val SUN = "\uF185"
    const val RAIN = "\uF73D"
    const val TRASH = "\uF2ED"
    const val PLUS = "\uF067"
    const val CHEV_R = "\uF054"
    const val CHEV_L = "\uF053"
    const val ARROW_R = "\uF061"
    const val CHECK = "\uF00C"
    const val SQ_CHECK = "\uF14A"
    const val SQUARE = "\uF0C8"
    const val INFO = "\uF05A"
    const val GEAR = "\uF013"
    const val UPDATE = "\uF2F1"
    const val DOWNLOAD = "\uF019"
    const val PEPPER = "\uF816"
    const val APPLE = "\uF5D1"
    const val CARROT = "\uF787"
    const val WHEAT = "\uF7D8"
    const val LIST_CHECK = "\uF0AE"
    const val RULER = "\uF548"
    const val NOTE = "\uF249"
    const val HAND = "\uF256"
    const val WARN = "\uF071"
    const val XMARK = "\uF00D"
    const val PEN = "\uF304"
    const val EYE = "\uF06E"
    const val LAYER = "\uF5FD"
    const val STETHO = "\uF0F1"
    const val COINS = "\uF51E"
    const val SHARE = "\uF1E0"
    const val BUG = "\uF188"
    const val WALLET = "\uF555"
    const val TREND = "\uE098"
    const val TEMP = "\uF2C9"
}

private object FaTypeface {
    @Volatile
    private var cached: Typeface? = null

    fun get(context: Context): Typeface =
        cached ?: synchronized(this) {
            cached ?: Typeface.createFromAsset(
                context.applicationContext.assets, "fonts/fa-solid-900.ttf"
            ).also { cached = it }
        }
}

/** Satu glif Font Awesome sebagai teks ber-font FA. */
@Composable
fun FaIcon(
    icon: String,
    modifier: Modifier = Modifier,
    size: TextUnit = 20.sp,
    color: Color = Color(0xFF12263A)
) {
    val context = LocalContext.current
    val typeface = remember { FaTypeface.get(context) }
    Text(
        icon,
        modifier = modifier,
        fontFamily = FontFamily(typeface),
        fontSize = size,
        color = color
    )
}

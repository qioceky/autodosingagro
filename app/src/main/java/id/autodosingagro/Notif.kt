package id.autodosingagro

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import id.autodosingagro.data.Store
import id.autodosingagro.data.TanamanDB
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/** Pengingat jadwal pupuk lewat notifikasi lokal (alarm jam 07.00 di hari aplikasi). */
object PengingatPupuk {
    const val CHANNEL_ID = "pengingat_pupuk"
    private const val PREF_CODES = "pengingat_codes"

    fun buatChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(NotificationManager::class.java)
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(CHANNEL_ID, "Pengingat Pupuk", NotificationManager.IMPORTANCE_DEFAULT).apply {
                        description = "Notifikasi saat jadwal pemupukan tanamanmu tiba"
                    }
                )
            }
        }
    }

    fun izinNotifAda(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < 33) return true
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    }

    private fun pendingIntent(context: Context, code: Int, judul: String, isi: String): PendingIntent {
        val intent = Intent(context, PupukReceiver::class.java).apply {
            putExtra("judul", judul); putExtra("isi", isi); putExtra("code", code)
        }
        return PendingIntent.getBroadcast(
            context, code, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** Batalkan semua alarm lama, lalu jadwalkan ulang dari data Tanaman Saya saat ini. */
    fun jadwalkanUlang(context: Context) {
        val store = Store(context)
        val am = context.getSystemService(AlarmManager::class.java)
        // batalkan alarm sebelumnya
        val lama = store.prefsCodes()
        lama.forEach { code ->
            val pi = PendingIntent.getBroadcast(
                context, code, Intent(context, PupukReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pi != null) { am.cancel(pi); pi.cancel() }
        }
        if (!store.pengingatAktif()) { store.simpanCodes(emptyList()); return }
        buatChannel(context)
        val codes = mutableListOf<Int>()
        val hariIni = LocalDate.now()
        store.loadTanamanSaya().forEach { ts ->
            val t = TanamanDB.byId(ts.tanamanId) ?: return@forEach
            t.jadwal.forEach { app ->
                val tanggal = ts.tanggalTanam.plusDays(app.hst.toLong())
                if (tanggal.isBefore(hariIni)) return@forEach
                var waktu = LocalDateTime.of(tanggal, LocalTime.of(7, 0))
                if (waktu.isBefore(LocalDateTime.now())) {
                    // hari ini tapi jam 07.00 sudah lewat — ingatkan sebentar lagi
                    waktu = LocalDateTime.now().plusMinutes(1)
                }
                val code = (ts.id % 100000).toInt() * 100 + (app.hst + 500)
                val judul = "Waktunya pupuk: ${ts.label}"
                val isi = "${t.nama} — ${app.label} (umur ${app.hst} HST): " +
                    app.pupuk.joinToString { p -> "${p.nama} ${"%.0f".format(p.kgPerHa)} kg/ha" }
                val trigger = waktu.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                try {
                    am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pendingIntent(context, code, judul, isi))
                    codes.add(code)
                } catch (e: Exception) { /* abaikan bila alarm gagal dipasang */ }
            }
        }
        store.simpanCodes(codes)
    }
}

class PupukReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!PengingatPupuk.izinNotifAda(context)) return
        PengingatPupuk.buatChannel(context)
        val judul = intent.getStringExtra("judul") ?: "Pengingat Pupuk"
        val isi = intent.getStringExtra("isi") ?: ""
        val code = intent.getIntExtra("code", 1)
        val bukaApp = PendingIntent.getActivity(
            context, code,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notif = NotificationCompat.Builder(context, PengingatPupuk.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle(judul)
            .setContentText(isi)
            .setStyle(NotificationCompat.BigTextStyle().bigText(isi))
            .setContentIntent(bukaApp)
            .setAutoCancel(true)
            .build()
        try { NotificationManagerCompat.from(context).notify(code, notif) } catch (e: Exception) { }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            PengingatPupuk.jadwalkanUlang(context)
        }
    }
}

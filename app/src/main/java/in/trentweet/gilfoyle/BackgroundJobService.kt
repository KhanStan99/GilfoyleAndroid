package `in`.trentweet.gilfoyle

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


class BackgroundJobService : JobService() {
    private var jobCancelled = false

    override fun onStartJob(params: JobParameters): Boolean {
        doBackgroundWork(params)
        return true
    }

    private fun doBackgroundWork(params: JobParameters) {
        Thread(Runnable {
            val queue = Volley.newRequestQueue(this)
            val url = "https://api.coindesk.com/v1/bpi/currentprice/USD.json"

            val stringRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener<JSONObject> { response ->
                    val bitConPrice =
                        response.optJSONObject("bpi")!!.optJSONObject("USD")!!.optDouble("rate_float")
                    sendNotification(bitConPrice)
                },
                Response.ErrorListener {
                    toast("Error Occurred! ${it.networkResponse.statusCode}")
                })
            queue.add(stringRequest)
            jobFinished(params, false)
        }).start()
    }

    private fun sendNotification(bitConPrice: Double) {
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val userPrice = SharedPref(this).getUserPrice()

        val channel: NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            channel = NotificationChannel(
                "AIO",
                "Basic",
                NotificationManager.IMPORTANCE_LOW
            )
            try {
                val notification =
                    Uri.parse("android.resource://" + packageName + "/" + R.raw.suffer12)
                val r = RingtoneManager.getRingtone(applicationContext, notification)
                r.play()
            } catch (e: Exception) {
                e.printStackTrace()
            }



            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0, intent, 0
        )

        val notificationBuilder = NotificationCompat.Builder(
            this, "AIO"
        )
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Bitcoin price changed to $$bitConPrice")
            .setContentText("Price you bought at: $$userPrice")
            .setAutoCancel(true)
            .setSound(null)
            .setContentIntent(pendingIntent)

        notificationManager.notify(
            1, notificationBuilder.build()
        )
    }

    override fun onStopJob(params: JobParameters): Boolean {
        jobCancelled = true
        return true
    }

}
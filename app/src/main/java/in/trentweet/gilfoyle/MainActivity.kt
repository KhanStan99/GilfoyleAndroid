package `in`.trentweet.gilfoyle

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var jobID = 1200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        et_user_price.setText(SharedPref(applicationContext).getUserPrice().toString())
    }

    fun scheduleJob(v: View) {
        if (getUserValue()) {
            SharedPref(applicationContext).saveUserPrice(et_user_price.text.toString().toFloat())
            val componentName = ComponentName(this, BackgroundJobService::class.java)
            val info = JobInfo.Builder(jobID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic((15 * 60 * 1000).toLong())
                .build()

            val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val resultCode = scheduler.schedule(info)
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                toast("Job scheduled")
            } else {
                toast("Job scheduling failed\"")
            }
        } else
            toast("Please enter a value in decimal")
    }

    private fun getUserValue(): Boolean {
        return try {
            et_user_price.text.toString().toFloat() > 0.0
        } catch (e: Exception) {
            false
        }
    }

    fun cancelJob(v: View) {
        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.cancel(jobID)
        toast("Job cancelled")
    }


}

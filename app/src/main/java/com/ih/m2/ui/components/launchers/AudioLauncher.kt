package com.ih.m2.ui.components.launchers

import android.content.res.Configuration
import android.media.MediaRecorder
import android.os.Build
import android.os.CountDownTimer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.ih.m2.R
import com.ih.m2.core.ui.functions.createAudioFile
import com.ih.m2.ui.pages.createcard.CardItemIcon
import com.ih.m2.ui.theme.M2androidappTheme

@Composable
fun AudioLauncher() {
    val context = LocalContext.current
    val mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else {
        MediaRecorder()
    }.apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setOutputFile(context.createAudioFile())
        }
        setMaxDuration(60)
    }


    AudioContent(
        onStart = {
            mediaRecorder.prepare()
            mediaRecorder.start()
        },
        onStop = {
            mediaRecorder.stop()
            mediaRecorder.release()
        }
    )

}

@Composable
fun AudioContent(
    onStart: () -> Unit,
    onStop: () -> Unit
) {

    var time by remember {
        mutableStateOf("")
    }

    val timer = timerCounter(
        time = 60,
        onTimeChange = {
            time = it
        })

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = time, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            CardItemIcon(icon = painterResource(id = R.drawable.ic_smart_display)) {
                timer.start()
                onStart()
            }

            CardItemIcon(icon = painterResource(id = R.drawable.ic_stop)) {
                timer.cancel()
                onStop()
            }
        }
    }
}


private fun timerCounter(
    time: Long,
    onTimeChange: (value: String) -> Unit
): CountDownTimer {
    return object : CountDownTimer((time * 1000), 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val timeValue = if ((millisUntilFinished / 1000) < 10) {
                "0${(millisUntilFinished / 1000)}"
            } else {
                (millisUntilFinished / 1000)
            }
            onTimeChange(timeValue.toString())
        }

        override fun onFinish() {}
    }
}


@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "dark")
@Preview(showBackground = true, name = "light")
fun AudioContentPreview() {
    M2androidappTheme {
        Surface {
            AudioContent(onStart = {}, onStop = {})
        }
    }
}
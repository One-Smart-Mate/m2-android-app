package com.ih.osm.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.io.File

@SuppressLint("MissingPermission")
@Composable
fun ModernAudioRecorderComponent(
    context: Context,
    maxDurationMillis: Long = 60000L,
) {
    var isRecording by remember { mutableStateOf(false) }
    var elapsedTimeMillis by remember { mutableLongStateOf(0L) }
    var recordingFilePath by remember { mutableStateOf<String?>(null) }
    var audioRecorder: AudioRecord? = null
    var timer: CountDownTimer? = null

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Elapsed Time: ${elapsedTimeMillis / 1000}s",
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isRecording) {
                    // Stop recording
                    isRecording = false
                    timer?.cancel() // Stop the timer
                    timer = null // Nullify the timer reference
                    elapsedTimeMillis = 0L // Reset elapsed time
                    audioRecorder?.apply {
                        stop()
                        release()
                    }
                    audioRecorder = null
                } else {
                    // Start recording
                    val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                    val outputFile = File.createTempFile("audio_", ".pcm", outputDir)
                    recordingFilePath = outputFile.absolutePath

                    audioRecorder =
                        AudioRecord.Builder()
                            .setAudioSource(MediaRecorder.AudioSource.MIC)
                            .setAudioFormat(
                                AudioFormat.Builder()
                                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                    .setSampleRate(44100)
                                    .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                                    .build(),
                            )
                            .setBufferSizeInBytes(
                                2 *
                                    AudioRecord.getMinBufferSize(
                                        44100,
                                        AudioFormat.CHANNEL_IN_MONO,
                                        AudioFormat.ENCODING_PCM_16BIT,
                                    ),
                            )
                            .build()

                    audioRecorder?.apply {
                        startRecording()
                        isRecording = true

                        timer =
                            object : CountDownTimer(maxDurationMillis, 1000L) {
                                override fun onTick(millisUntilFinished: Long) {
                                    elapsedTimeMillis = maxDurationMillis - millisUntilFinished
                                }

                                override fun onFinish() {
                                    isRecording = false
                                    stop()
                                    release()
                                    audioRecorder = null
                                    elapsedTimeMillis = 0L // Reset time when finished
                                }
                            }.start()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) Color.Red else Color.Green,
                ),
        ) {
            Text(text = if (isRecording) "Stop Recording" else "Start Recording")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isRecording && !recordingFilePath.isNullOrEmpty()) {
            Button(
                onClick = {
                    val file = File(recordingFilePath!!)
                    if (file.exists()) {
                        // Implement playback logic here
                        Log.d("AudioRecorder", "Playback file path: ${file.absolutePath}")
                    } else {
                        Log.e("AudioRecorder", "Audio file does not exist")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            ) {
                Text(text = "Play Recording")
            }
        }
    }
}

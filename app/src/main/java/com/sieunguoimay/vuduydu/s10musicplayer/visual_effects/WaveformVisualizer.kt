package com.sieunguoimay.vuduydu.s10musicplayer.visual_effects

import android.media.audiofx.Visualizer

class WaveformVisualizer {

    private var visualizer: Visualizer? = null
    private var started:Boolean = false
    var waveformView:WaveformView? = null
    fun startVisualizer(audioSessionId:Int){
        stopVisualizer()
        started = true
        visualizer = Visualizer(audioSessionId)

        visualizer?.setDataCaptureListener(object:Visualizer.OnDataCaptureListener{
            override fun onFftDataCapture(visualizer: Visualizer?, fft: ByteArray?, samplingRate: Int) {
            }

            override fun onWaveFormDataCapture(visualizer: Visualizer?, waveform: ByteArray?, samplingRate: Int) {
                if(waveform!=null){
                    waveformView?.updateWaveform(waveform)
                }
            }
        },Visualizer.getMaxCaptureRate(),true, false)

        visualizer?.captureSize = Visualizer.getCaptureSizeRange()[1]

        visualizer?.setEnabled(true)
    }
    fun stopVisualizer(){
        if(started) {
            visualizer?.release()
            started = false
        }
    }
}
package com.sakupintar.app.util

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import com.sakupintar.app.R

object AppAudioManager {

    private var mediaPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null

    private var soundClick: Int   = 0
    private var soundCorrect: Int = 0
    private var soundWrong: Int   = 0

    private var sudahInisialisasi = false

    fun inisialisasi(context: Context) {
        if (sudahInisialisasi) return

        //SoundPool untuk sound effect
        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .build()

        soundClick   = soundPool!!.load(context, R.raw.sfx_click, 1)
        soundCorrect = soundPool!!.load(context, R.raw.sfx_correct, 1)
        soundWrong   = soundPool!!.load(context, R.raw.sfx_wrong, 1)

        sudahInisialisasi = true
    }

    //Background music
    fun mulaiMusic(context: Context) {
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) return
        mediaPlayer?.release()
        mediaPlayer = null
        mediaPlayer = MediaPlayer.create(context, R.raw.bg_music).apply {
            isLooping = true
            setVolume(0.08f, 0.08f)
            start()
        }
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
    }

    fun resumeMusic() {
        mediaPlayer?.start()
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun setMute(mute: Boolean) {
        if (mute) {
            mediaPlayer?.setVolume(0f, 0f)
        } else {
            mediaPlayer?.setVolume(0.08f, 0.08f)
        }
    }

    //Sound effect
    fun playSfxClick() {
        soundPool?.play(soundClick, 0.7f, 0.7f, 1, 0, 1f)
    }

    fun playSfxCorrect() {
        soundPool?.play(soundCorrect, 1f, 1f, 1, 0, 1f)
    }

    fun playSfxWrong() {
        soundPool?.play(soundWrong, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        stopMusic()
        soundPool?.release()
        soundPool = null
        sudahInisialisasi = false
    }
}
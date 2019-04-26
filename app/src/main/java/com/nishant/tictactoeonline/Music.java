package com.nishant.tictactoeonline;

import android.media.MediaPlayer;

public class Music
{
    public static MediaPlayer mediaPlayer;
    public static MediaPlayer btnPressed;

    public Music(MediaPlayer mediaPlayer, MediaPlayer btnPressed)
    {
        this.mediaPlayer = mediaPlayer;
        this.btnPressed = btnPressed;
    }
}
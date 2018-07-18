package com.exo.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ExoPlayerManager playerManager;
    SurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        glSurfaceView = findViewById(R.id.id_SurfaceView);
        playerManager = ExoPlayerManager.create();

        initClick();
    }


    @Override
    protected void onResume() {
        super.onResume();
        String path = "http://flv2.bn.netease.com/videolib3/1604/28/fVobI0704/SD/fVobI0704-mobile.mp4";

        Map<String, String> map = new HashMap<>();
        map.put("X-Api-Key", "06fe3df1-214f-83e4-45c9");
        map.put("X-Api-Device", "imei-358351290605");

        //playerManager.init(this, path);
        playerManager.init(this, map, path);
        playerManager.setSurface(glSurfaceView.getHolder().getSurface());

        // 回调监听
        playerManager.setDebugListener(new VideoRendererEventListener() {
            @Override
            public void onVideoEnabled(DecoderCounters counters) {
                Log.e("EXOP", "onVideoEnabled: ");
            }

            @Override
            public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
                Log.e("EXOP", "onVideoDecoderInitialized: ");
            }

            @Override
            public void onVideoInputFormatChanged(Format format) {
                Log.e("EXOP", "onVideoInputFormatChanged: ");
            }

            @Override
            public void onDroppedFrames(int count, long elapsedMs) {
                Log.e("EXOP", "onVideoEnabled: ");
            }

            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                Log.e("EXOP", "onVideoSizeChanged: ");
            }

            @Override
            public void onRenderedFirstFrame(Surface surface) {
                Log.e("EXOP", "onRenderedFirstFrame: ");
            }

            @Override
            public void onVideoDisabled(DecoderCounters counters) {
                Log.e("EXOP", "onVideoDisabled: ");
            }
        });

        playerManager.setEventListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
                Log.e("EXOP", "onTimelineChanged: ");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.e("EXOP", "onTracksChanged: ");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.e("EXOP", "onLoadingChanged: " + isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.e("EXOP", "onPlayerStateChanged: " + playWhenReady + ",playbackState:" + playbackState);
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Log.e("EXOP", "onRepeatModeChanged: " + repeatMode);
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e("EXOP", "onPlayerError: ");
            }

            @Override
            public void onPositionDiscontinuity() {
                Log.e("EXOP", "onPositionDiscontinuity: ");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.e("EXOP", "onPlaybackParametersChanged: ");
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        playerManager.release();
    }


    /**
     * author zx
     * version 1.0
     * since 2018/7/5  .
     */
    protected void initClick() {
        findViewById(R.id.tv_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerManager.start();
            }
        });

        findViewById(R.id.tv_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerManager.pause();
            }
        });
    }


}

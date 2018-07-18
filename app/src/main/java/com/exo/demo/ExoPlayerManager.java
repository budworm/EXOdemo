/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.exo.demo;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author zx
 * version 1.0
 * since 2018/7/5  .
 */
final class ExoPlayerManager {

    private CookieManager cookieManager;
    private SimpleExoPlayer player;
    private long contentPosition;
    private VideoRendererEventListener debugListener;
    private Player.EventListener eventListener;

    public void setDebugListener(VideoRendererEventListener debugListener) {
        this.debugListener = debugListener;
        // 回调监听
        if (debugListener != null && player != null) {
            player.setVideoDebugListener(debugListener);
        }
    }

    public void setEventListener(Player.EventListener eventListener) {
        this.eventListener = eventListener;
        // 回调监听
        if (eventListener != null && player != null) {
            player.addListener(eventListener);
        }
    }


    public static ExoPlayerManager create() {
        return new ExoPlayerManager();
    }

    /**
     * author zx
     * version 1.0
     * since 2018/7/5  .
     */
    public void build(Context context) {
        // 创建默认的轨道选择器。
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        // 创建一个播放器实例。
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
    }


    /**
     * author zx
     * version 1.0
     * since 2018/7/5  .
     */
    @Deprecated
    public void setCookie(String contentUrl) {
        try {
            cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
            Map<String, List<String>> header = new HashMap<>();
            cookieManager.put(URI.create(contentUrl), header);

            CookieHandler.setDefault(cookieManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * author zx
     * version 1.0
     * since 2018/7/5  .
     */
    public void init(Context context, String contentUrl) {
        if (player == null) {
            build(context);
        }
        // 生成默认数据加载器。
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, context.getPackageName()));
        // 生成内容提取器。
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // 生成媒体资源
        MediaSource contentMediaSource = new ExtractorMediaSource(Uri.parse(contentUrl), dataSourceFactory, extractorsFactory, null, null);
        // Prepare the player with the source.
        player.seekTo(contentPosition);
        player.prepare(contentMediaSource);
        player.setPlayWhenReady(true);
        // 回调监听
        if (debugListener != null) {
            player.setVideoDebugListener(debugListener);
        }
        // 回调监听
        if (eventListener != null) {
            player.addListener(eventListener);
        }
    }


    /**
     * author zx
     * version 1.0
     * since 2018/7/5  .
     */
    public void init(Context context, Map<String, String> cookie, String contentUrl) {
        if (player == null) {
            build(context);
        }
        // 生成数据加载器。
        String userAgent = context.getPackageName();
        DefaultHttpDataSource defaultHttpDataSource = new DefaultHttpDataSource(userAgent, null, null);
        // 循环cookie
        if (cookie != null) {
            for (Map.Entry<String, String> entry : cookie.entrySet()) {
                defaultHttpDataSource.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null, defaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, defaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
        // 循环cookie
        if (cookie != null) {
            for (Map.Entry<String, String> entry : cookie.entrySet()) {
                httpDataSourceFactory.setDefaultRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, null, httpDataSourceFactory);
        // 生成默认数据加载器。
        //DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "yourApplicationName"));
        // 生成内容提取器。
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // 生成媒体资源
        MediaSource contentMediaSource = new ExtractorMediaSource(Uri.parse(contentUrl), dataSourceFactory, extractorsFactory, null, null);
        // Prepare the player with the source.
        player.seekTo(contentPosition);
        player.prepare(contentMediaSource);
        player.setPlayWhenReady(true);
        // 回调监听
        if (debugListener != null) {
            player.setVideoDebugListener(debugListener);
        }
        // 回调监听
        if (eventListener != null) {
            player.addListener(eventListener);
        }
    }


    public void setSurface(Surface surface) {
        if (player != null) {
            player.setVideoSurface(surface);
        }
    }


    public void start() {
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    public void pause() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }


    public void reset() {
        if (player != null) {
            contentPosition = player.getContentPosition();
            player.setPlayWhenReady(false);
            player.release();
            player = null;
        }
    }


    public void release() {
        if (player != null) {
            contentPosition = player.getContentPosition();
            player.setPlayWhenReady(false);
            player.release();
            player = null;
        }
    }


    public boolean isPlaying() {
        if (player != null) {
            return player.getPlayWhenReady();
        }
        return false;
    }


    public long getCurrentPosition() {
        if (player != null) {
            return player.getContentPosition();
        }
        return 0;
    }


    public long getDuration() {
        if (player != null) {
            return player.getDuration();
        }
        return 0;
    }


    public void seekTo(long positionMs) {
        if (player != null) {
            player.seekTo(positionMs);
        }
    }


}

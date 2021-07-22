package com.prometteur.sathiya.home;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;


import com.prometteur.sathiya.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * "Copyright © 2019 SUUSOFT"
 */

public class MusicService extends MediaBrowserServiceCompat  {
    private static final String TAG = MusicService.class.getSimpleName();

    public static final String ACTION_CMD = "com.android.music.ACTION_CMD";
    public static final String CMD_NAME = "CMD_NAME";
    public static final String CMD_PAUSE = "CMD_PAUSE";
    public static final String MEDIA_ID_EMPTY_ROOT = "__EMPTY_ROOT__";
    public static final String MEDIA_ID_ROOT = "__ROOT__";

    private static final int DELAY = 1000;

    public static MediaPlayer mPlayer;
    private boolean ringPhone = false;
    public int lengthSong;
    private Handler handler = new Handler();
//    private MediaNotificationManager mMediaNotificationManager;
    private MediaSessionCompat mSession;
    private PlaybackStateCompat.Builder mStateBuilder;
//    private QueueManager queueManager;

    // for first time click play button
    private boolean isPlayed;



    private MediaSessionCompat.Callback callback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            handlePrepare();
        }

        @Override
        public void onPlay() {
            if (isPlayed) {
                handlePlay();
            } else {
                handlePrepare();
            }
        }

        @Override
        public void onPause() {
            handlePause();
        }

        @Override
        public void onSkipToNext() {
//            handleNext();
        }

        @Override
        public void onSkipToPrevious() {
//            handlePrev();
        }

        @Override
        public void onSeekTo(long pos) {
//            handleSeekTo(pos);
        }

    };


    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
//        if (allowBrowsing(clientPackageName, clientUid)) {
//            return new BrowserRoot(MEDIA_ID_ROOT, null);
//        } else {
        return new BrowserRoot(MEDIA_ID_EMPTY_ROOT, null);
        // }
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        if (TextUtils.equals(MEDIA_ID_EMPTY_ROOT, parentId)) {
            result.sendResult(null);
            return;
        }
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList();
        if (MEDIA_ID_EMPTY_ROOT.equals(parentId)) {
            result.sendResult(mediaItems);
        } else {
            result.sendResult(mediaItems);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // checkCall();
        initQueueManager();
        initPlayer();
        initMediaSession();
        initNotification();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {
                 //   mPlayer.start();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int onStartCommand(@Nullable Intent startIntent, int flags, int startId) {
        if (startIntent != null) {
            String action = startIntent.getAction();
            String command = startIntent.getStringExtra(CMD_NAME);
            if (ACTION_CMD.equals(action)) {
                if (CMD_PAUSE.equals(command)) {
                    mPlayer.pause();
                }
            } else {
                // Try to handle the intent as a media button event wrapped by MediaButtonReceiver
                MediaButtonReceiver.handleIntent(mSession, startIntent);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mMediaNotificationManager.stopNotification();
        mSession.release();
        mPlayer.release();
        stopForeground(true);

    }


    private void initQueueManager() {
//        queueManager = QueueManager.getInstance();
//        queueManager.setListener(new QueueManager.IQueueChangeListener() {
//            @Override
//            public void onItemSet() {
//                mSession.setMetadata(queueManager.getMetadataCurrentItem(mPlayer.getDuration()));
//                notificationChangeSong();
//            }
//        });
    }

    private void initPlayer() {
        //if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setLooping(true);
            mPlayer.setVolume(0.05f, 0.05f);
            handlePrepare();
            handlePlay();
//            mPlayer.setShouldAutoPlay(false);
//            mPlayer.addEventListener(this);
       // }
    }

    private void initNotification() {
//        try {
//            mMediaNotificationManager = new MediaNotificationManager(this);
//        } catch (RemoteException e) {
//            throw new IllegalStateException("Could not create a MediaNotificationManager", e);
//        }
    }

    private void initMediaSession() {
        mSession = new MediaSessionCompat(this, TAG);
        setSessionToken(mSession.getSessionToken());

        mSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        updatePlaybackState();
        mSession.setCallback(callback);
    }

    private void updatePlaybackState() {
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(getAvailableActions());
//        int state = getState();
        long position = mPlayer != null ? mPlayer.getCurrentPosition() : 0;
        mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, position, 1.0f, SystemClock.elapsedRealtime());

        mSession.setPlaybackState(mStateBuilder.build());

//        if (state == PlaybackStateCompat.STATE_PLAYING ||
//                state == PlaybackStateCompat.STATE_PAUSED && mMediaNotificationManager != null) {
//            onNotificationRequired();
//        }
    }

    private long getAvailableActions() {
        long actions =
                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                        PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
        if (mPlayer.isPlaying()) {
            actions |= PlaybackStateCompat.ACTION_PAUSE;
        } else {
            actions |= PlaybackStateCompat.ACTION_PLAY;
        }
        return actions;
    }

    private void getMetaData() {

    }

/*
    public int getState() {
        if (mPlayer == null) {
            return PlaybackStateCompat.STATE_NONE;
        }

        switch (mPlayer.getState()) {
            case Player.STATE_IDLE:
                return PlaybackStateCompat.STATE_PAUSED;
            case Player.STATE_BUFFERING:
                return PlaybackStateCompat.STATE_BUFFERING;
            case Player.STATE_READY:
                return mPlayer.isPlaying()
                        ? PlaybackStateCompat.STATE_PLAYING
                        : PlaybackStateCompat.STATE_PAUSED;
            case Player.STATE_ENDED:
                return PlaybackStateCompat.STATE_PAUSED;
            default:
                return PlaybackStateCompat.STATE_NONE;
        }

    }
*/

   /* private void onNotificationRequired() {
        mMediaNotificationManager.startNotification();
    }

    private void notificationChangeSong() {
        EventBus.getDefault().post(new Global.NewSongSelected());
    }
*/
   int[] audArr=new int[]{};
    private void handlePrepare() {
        isPlayed = true;
        try {
           // mPlayer.setDataSource(this, Uri.parse("https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3"));
           // mPlayer.setDataSource(this, Uri.parse("https://www.adrive.com/public/aSNg6G/crowd-cheering.mp3"));
            // mPlayer.setDataSource(this, Uri.parse("https://www.adrive.com/public/q3Kdbe/wave.mp3"));
            Random random=new Random();
            int number = random.nextInt(3);

            mPlayer.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.audio));
//            mPlayer.setDataSource(this, Uri.parse("android.resource://" + getPackageName() + "/" + audArr[number]));
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }



//        Song song = queueManager.getCurrentItem();
//        notificationChangeSong();
//        if (song != null) {
//            startService(new Intent(getApplicationContext(), com.limitlessmind.player.MusicService.class));
//            File file = new File(Config.getPathDir() + "/" + song.title);
//            if (file.exists()) {
//                Song localSongData = DataStoreManager.isDownloadedSongLocalInfo(song.id);
//                if (localSongData != null) {
//                    if (localSongData.getEncrypt_code_bytes() != null) {
//                        if (!localSongData.getEncrypt_code_bytes().trim().equals("")) {
//                            song.isSongLocal = true;
//                            song.song_file = Config.getPathDir() + "/" + song.title;
//                            song.setEncrypt_code_bytes(localSongData.getEncrypt_code_bytes());
//                        }
//                    }
//                }
//            }
//            if (song.isSongLocal) {
//                mPlayer.prepare(song, song.getSong_file(), SuuPlayer.TYPE_OTHER);
//            } else {
//                mPlayer.prepare(song.getSong_file(), SuuPlayer.TYPE_OTHER);
//            }
//        }
    }


    private void handlePause() {
        mSession.setActive(false);
        stopForeground(false);
        mPlayer.pause();

    }

    private void handlePlay() {
//        mSession.setActive(true);
        startService(new Intent(getApplicationContext(), MusicService.class));
        mPlayer.start();
    }

/*
    private void handlePrev() {
        Song song = queueManager.getCurrentItem();
        File file = new File(Config.getPathDir(), song.getTitle() + ".mp3");
        boolean deleted;
        try {
            deleted = file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        queueManager.prevSong();
        handlePrepare();
        queueManager.setRepeat(true);
        mPlayer.play();

    }

    private void handleNext() {
        Song song = queueManager.getCurrentItem();
        File file = new File(Config.getPathDir(), song.getTitle() + ".mp3");
        boolean deleted;
        try {
            deleted = file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        queueManager.nextSong();
        handlePrepare();
        queueManager.setRepeat(true);
        mPlayer.play();
    }

    private void handleSeekTo(long pos) {
        mPlayer.seekTo(pos);
    }
*/


    private void checkCall() {
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    if (mPlayer != null)
                        if (mPlayer.isPlaying())
                            mPlayer.pause();
                    ringPhone = true;

                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    if (ringPhone) {
//                        if (mPlayer != null)
//                            if (!mPlayer.isPlaying() && played) {
//                                resumeSong();
//                            }
                        ringPhone = false;
                    }
                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    if (mPlayer != null)
                        if (mPlayer.isPlaying())
                            mPlayer.pause();
                    ringPhone = true;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    public String getLengSong() {
        return getTime(lengthSong);
    }

    private static String getTime(int millis) {
        long second = (millis / 1000) % 60;
        long minute = millis / (1000 * 60);
        return String.format("%02d:%02d", minute, second);
    }



//    @Override
//    public void onStared() {
//
//    }
//
//    @Override
//    public void onPlaying() {
//        isPlayed = true;
//        mSession.setMetadata(queueManager.getMetadataCurrentItem(mPlayer.getDuration()));
//        updatePlaybackState();
//
//    }
//
//    @Override
//    public void onPaused() {
//        updatePlaybackState();
//
//    }
//
//    @Override
//    public void onStopped() {
//
//    }
//
//    @Override
//    public void onCompletion() {
//        if (queueManager.isRepeat) {
//            Song song = queueManager.getCurrentItem();
//            File file = new File(Config.getPathDir(), song.getTitle() + ".mp3");
//            boolean deleted;
//            try {
//                deleted = file.delete();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            queueManager.mCurrentIndexRepeatCount = ++queueManager.mCurrentIndexRepeatCount;
////            Song song = queueManager.getCurrentItem();
//            if (song != null) {
//                if (song.getRepeat_count() <= queueManager.mCurrentIndexRepeatCount) {
//                    queueManager.setRepeat(false);
//                }
//                handlePrepare();
//            }
//        } else {
//            handleNext();
//        }
//    }
//
//    @Override
//    public void onFullScreen(boolean isFullScreen) {
//
//    }
//
//    @Override
//    public void onBuffering(boolean var1) {
//        if(mPlayer!=null)
//        mSession.setMetadata(queueManager.getMetadataCurrentItem(mPlayer.getDuration()));
//        else
//            mSession.setMetadata(queueManager.getMetadataCurrentItem(0));
//        updatePlaybackState();
//    }
//
//    @Override
//    public void onRepeatModeChanged(int repeatMode) {
//
//    }
//
//    @Override
//    public void onPlayerError(ExoPlaybackException error) {
//        updatePlaybackState();
//    }
}

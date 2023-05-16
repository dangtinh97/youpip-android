package org.youpip.app.views.fragment.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.squareup.picasso.Picasso
import org.youpip.app.MainActivity
import org.youpip.app.base.BaseFragment
import org.youpip.app.databinding.FragmentShortVideoBinding
import org.youpip.app.model.Video
import org.youpip.app.service.getVideo


class ShortVideoFragment(var list: ArrayList<Video>) : BaseFragment() {
    private lateinit var binding:FragmentShortVideoBinding
    private lateinit var thumbnail:ImageView
    private lateinit var playerView:StyledPlayerView
    private lateinit var player:ExoPlayer
    private lateinit var processBarVideo:RelativeLayout
    private lateinit var title:TextView
    private lateinit var back:ImageView
    private var dataSet:ArrayList<String> = arrayListOf()
    private var indexVideo = 0;
    override fun onViewCreateBase(view: View, savedInstanceState: Bundle?) {
        loadImage(list[0].thumbnail,true)
        playerView.player = player;
        playVideo(list[0])
        dataSet.add(list[0].video_id)
        handlerActionPlayer()
        back.setOnClickListener {
            player.stop()
            showNextNoAddStack(HomeFragment())
        }
    }

    private fun playVideo(video:Video){
        getVideo(requireContext()){

            val videoLink = it[1];
            val audio = it[2];
            var play = false;
            if (videoLink.isNotEmpty() && audio.isNotEmpty()) {
                play = true
                 val videoSource: MediaSource =
                     ProgressiveMediaSource.Factory(DefaultDataSourceFactory(requireContext()))
                         .createMediaSource(MediaItem.fromUri(videoLink))
                 val audioSource: MediaSource =
                     ProgressiveMediaSource.Factory(DefaultDataSourceFactory(requireContext()))
                         .createMediaSource(MediaItem.fromUri(audio))
                 val source = MergingMediaSource(videoSource, audioSource)
                 player.setMediaSource(source)
            }
            if(!play){
                val mediaItem: MediaItem = MediaItem.fromUri(it[0])
                player.setMediaItem(mediaItem);
            }

            player.prepare();
            player.play()
        }.extract("https://youtube.com/watch?v=${video.video_id}")
    }

    override fun onInitialized() {
        (mActivity as MainActivity).showNavigationBottom(false)
        playerView = binding.playerView
        player = ExoPlayer.Builder((mActivity as MainActivity).baseContext).build()
        thumbnail = binding.thumbnail
        processBarVideo = binding.progressBarVideoVertical
        title = binding.title
        back = binding.back
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::binding.isInitialized){
            binding = FragmentShortVideoBinding.inflate(inflater, container, false)
        }
        return binding.root
    }
    private fun handlerActionPlayer(){
        player.addListener(object : Player.Listener {
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                super.onPlayWhenReadyChanged(playWhenReady, reason)
            }

            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

                super.onPlayerStateChanged(playWhenReady, playbackState)

                when (playbackState) {
                    Player.STATE_ENDED -> {
                        Log.i("EventListenerState", "Playback ended!")
                    }
                    Player.STATE_READY -> {
                        Log.i("EventListenerState", "Playback State Ready!")
                        loadImage(null, false)
                    }
                    Player.STATE_BUFFERING -> {
                        Log.i("EventListenerState", "Playback buffering")
                        loadImage(null, true)
                    }
                    Player.STATE_IDLE -> {}
                }
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                super.onIsLoadingChanged(isLoading)
                loadImage(null, false)
            }

            @Deprecated("Deprecated in Java")
            override fun onLoadingChanged(isLoading: Boolean) {
                super.onLoadingChanged(isLoading)
            }

            override fun onPlayerErrorChanged(error: PlaybackException?) {
                super.onPlayerErrorChanged(error)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {

                super.onMediaItemTransition(mediaItem, reason)
                println("====>shortsvideo${mediaItem?.mediaId}-${reason}-${player.currentMediaItemIndex}")
                title.text = list[player.currentMediaItemIndex].title
                if(indexVideo>=list.size-1){
                    return;
                }
                indexVideo++;
                getVideo(requireContext()) {
                    val videoLink = it[1];
                    val audio = it[2];
                    var play = false;
                    if (videoLink.isNotEmpty() && audio.isNotEmpty()) {
                        play = true
                        val videoSource: MediaSource =
                            ProgressiveMediaSource.Factory(DefaultDataSourceFactory(requireContext()))
                                .createMediaSource(MediaItem.fromUri(videoLink))
                        val audioSource: MediaSource =
                            ProgressiveMediaSource.Factory(DefaultDataSourceFactory(requireContext()))
                                .createMediaSource(MediaItem.fromUri(audio))
                        val source = MergingMediaSource(videoSource, audioSource)
                        player.addMediaSource(indexVideo,source)

                    }
                }.extract("https://youtube.com/watch?v=" + list[indexVideo].video_id)
            }
        })
    }

    private fun loadImage(image:String?,loading:Boolean){
        println("====>EventListenerState${image},${loading}")
        if(image!=null){
            thumbnail.visibility = View.VISIBLE
            Picasso.get()
                .load(list[0].thumbnail)
                .into(thumbnail);
        }else{
            thumbnail.visibility = View.GONE
        }
        if(loading){
            processBarVideo.visibility = View.VISIBLE
        }else{
            processBarVideo.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        player.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.pause()
        (mActivity as MainActivity).showNavigationBottom(true)
    }
}
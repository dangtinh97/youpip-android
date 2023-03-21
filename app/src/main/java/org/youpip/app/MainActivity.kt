package org.youpip.app

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.*
import android.content.*
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.IBinder
import android.os.StrictMode
import android.util.DisplayMetrics
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.internal.LinkedTreeMap
import com.squareup.picasso.Picasso
import org.youpip.app.adapter.ViewPagerTabAdapter
import org.youpip.app.base.BaseActivity
import org.youpip.app.databinding.ActivityMainBinding
import org.youpip.app.model.Video
import org.youpip.app.network.RequiresApi
import org.youpip.app.service.MusicService
import java.util.*
import kotlin.system.exitProcess

private const val ACTION_PIP_CONTROL = "pip_control"
private const val CONTROL_TYPE = "control_type"
private const val CONTROL_TYPE_EXIT = "exit"
private const val CONTROL_TYPE_PLAY = "play"
private const val CONTROL_TYPE_PAUSE = "pause"

class MainActivity : BaseActivity(),ServiceConnection {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navigationTabBottom: BottomNavigationView
    private lateinit var viewPagerMain: ViewPager2
    private var positionTab: Int? = null

    private lateinit var playerView: PlayerView
    private var isPlay = false
    private var isFullScreen: Boolean = false
    private lateinit var btnFullScreen: ImageView
    private lateinit var titleVideo: TextView

    private var videoIsSmall: Boolean = false
    private var baseView: LayoutParams? = null

    // code custom video new
    private lateinit var layoutVideo: RelativeLayout
    private var widthScreen = 0;
    private lateinit var thumbnailVideo: ImageView
    private lateinit var progressBarVideoVertical: RelativeLayout
    private var videoStart: Boolean = false
    private lateinit var btnScrollToBottom: ImageView
    private lateinit var titleVideoVertical: TextView
    private lateinit var viewActionSmallVideo: RelativeLayout
    private lateinit var btnCloseVideo: ImageView
    private lateinit var titleVideoSmall:TextView
    private var  permissionPiP:Boolean = false
    private var tabCurrent:Int = 0
    private var listVideo = emptyList<Video>()
    private lateinit var video:Video
    private lateinit var btnAddList:ImageView
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var paramsPiP= PictureInPictureParams.Builder()

    companion object {
        var videoP:Video? = null
        var musicService: MusicService? = null
        var modePiPEnable:Boolean = false
        var newConfigMode:Configuration? = null
        lateinit var player: ExoPlayer
    }

    override fun setViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        navigationTabBottom = binding.navigationTabBottom
        viewPagerMain = binding.viewPagerMain
        viewPagerMain.offscreenPageLimit = 1
        setContentView(binding.root)
        setViewVideoBinding()
        val displayMetrics = DisplayMetrics()
        val windowsManager = this.getSystemService(WINDOW_SERVICE) as WindowManager
        windowsManager.defaultDisplay.getMetrics(displayMetrics)
        findViewById()
        setPictureInPictureParams(PictureInPictureParams.Builder().build())

        navigationTabBottom.setOnItemSelectedListener  { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_1 -> setTab(0)
                R.id.navigation_2 -> setTab(1)
//                R.id.navigation_3 -> setTab(2)
//                R.id.navigation_4 -> setTab(3)
//                R.id.navigation_5 -> setTab(5)
            }
            true
        }
        viewPagerMain.currentItem = 1

    }

    private fun setTab(tab:Int){
        if(tab == tabCurrent){
            return
        }
        tabCurrent = tab
        viewPagerMain.currentItem = tab
    }

    private fun setViewVideoBinding() {
        titleVideoVertical = binding.titleVideoVertical
        layoutVideo = binding.frameVideo
        thumbnailVideo = binding.thumbnailVideo
        playerView = binding.videoPlayView
        progressBarVideoVertical = binding.progressBarVideoVertical
        viewActionSmallVideo = binding.actionSmallVideo
        btnCloseVideo = binding.closeVideoSmall
        titleVideoSmall = binding.titleVideoSmall
        btnAddList = binding.addList

        viewActionSmallVideo.visibility = View.GONE
        progressBarVideoVertical.visibility = View.GONE
        thumbnailVideo.visibility = View.GONE
        playerView.visibility = View.INVISIBLE
        layoutVideo.visibility = View.GONE
        titleVideoVertical.visibility = View.GONE

    }

    private fun findViewById() {
        btnFullScreen = findViewById(R.id.exo_fullscreen)
        titleVideo = findViewById(R.id.title_video_screen)
        btnScrollToBottom = findViewById(R.id.smallVideo)
        titleVideo.visibility = View.GONE
        btnFullScreen.setOnClickListener {
            rotation()
        }
        btnScrollToBottom.setOnClickListener {
            smallVideo()
        }
        binding.frameVideo.setOnClickListener {
            if (videoIsSmall) {
                smallVideo()
            }
        }
    }

    fun playVideo(videoPlay: Video? = null) {
        permissionPiP = false
        videoStart = false
        playerView.visibility = View.INVISIBLE
        titleVideoVertical.visibility = View.VISIBLE
        navigationTabBottom.visibility = View.GONE
        setLayoutParamFullScreen()
        if (videoIsSmall) {
            smallVideo()
        }
        player.stop()
        if(videoPlay==null){
            return
        }
        video = videoPlay
        videoP = video

        setInfo(video)
        val api = callApi.findLink(token,video.video_id)
        RequiresApi.callApi(this,api){
            if(it==null || !it.status.equals(200)){
                return@callApi
            }
            val data = it.data as LinkedTreeMap<*, *>
            val url = data["url"].toString()
            val mediaItem: MediaItem = MediaItem.fromUri(url)

            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
            playerView.player = player
        }
    }

    private fun setLayoutParamFullScreen() {
        val layoutParamVideo = layoutVideo.layoutParams as LayoutParams
        layoutParamVideo.width = LayoutParams.MATCH_PARENT
        layoutParamVideo.height = LayoutParams.MATCH_PARENT
        layoutVideo.setLayoutParams(layoutParamVideo)
        layoutVideo.visibility = View.VISIBLE
    }

    private fun loadImage(url: String?, show: Boolean = true) {
        if (show && !url.equals(null)) {
            progressBarVideoVertical.visibility = View.VISIBLE
            thumbnailVideo.visibility = View.VISIBLE
            Picasso.get()
                .load(url)
                .into(thumbnailVideo)
            return
        }
        if (!show) {
            progressBarVideoVertical.visibility = View.GONE
            thumbnailVideo.visibility = View.GONE
        }

        if (show && url.equals(null)) {
            progressBarVideoVertical.visibility = View.VISIBLE
        }
    }

    override fun onCreateBase() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        viewPagerMain.adapter = ViewPagerTabAdapter(supportFragmentManager, lifecycle)
        viewPagerMain.isUserInputEnabled = false //Tắt chức năng vuốt để chuyển TAB
        navigationTabBottom.selectedItemId = R.id.navigation_1
        val displayMetrics = DisplayMetrics()
        val windowsManager = this.getSystemService(WINDOW_SERVICE) as WindowManager
        windowsManager.defaultDisplay.getMetrics(displayMetrics)
        widthScreen = displayMetrics.widthPixels
        onCreateBaseVideo()
        setupUI(binding.root)

        val intent = Intent(this,MusicService::class.java)
        bindService(intent,this, BIND_AUTO_CREATE)
        startService(intent)
    }

    private fun onCreateBaseVideo() {
        player = ExoPlayer.Builder(this).build()
        //set ratio of image thumbnail 16:9 = w:h
        val layoutParamThumbnail: ViewGroup.LayoutParams = thumbnailVideo.getLayoutParams()
        layoutParamThumbnail.width = widthScreen
        layoutParamThumbnail.height = widthScreen * 9 / 16
        thumbnailVideo.setLayoutParams(layoutParamThumbnail)
        val layoutParamPlayerView: ViewGroup.LayoutParams = playerView.getLayoutParams()
        layoutParamPlayerView.width = widthScreen
        layoutParamPlayerView.height = widthScreen * 9 / 16
        playerView.setLayoutParams(layoutParamPlayerView)
        btnCloseVideo.setOnClickListener {
            videoIsSmall = true
            isPlay = false
            player.stop()
            layoutVideo.visibility = View.GONE
        }
        btnAddList.setOnClickListener {
            showBottomSheet()
        }
        handlerActionPlayer()
        val filter = IntentFilter()
        filter.addAction(ACTION_PIP_CONTROL)
        registerReceiver(broadcastReceiver, filter)
    }

    private fun handlerActionPlayer(){
        player.addListener(object : Player.Listener {
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                permissionPiP = playWhenReady
                super.onPlayWhenReadyChanged(playWhenReady, reason)
            }

            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                println("====>listen-onPlayerStateChanged:${playWhenReady}")

                if(playWhenReady!=isPlay && modePiPEnable){
                    println("====>listen")
                    isPlay = playWhenReady
                    buildPip()
                }
                isPlay = playWhenReady
                permissionPiP = playWhenReady
                musicService!!.showNotification(playWhenReady)
                if (playWhenReady && !videoStart) {
                    videoStart = true
                    delay(1) {
                        loadImage(null, false)
                        playerView.visibility = View.VISIBLE
                    }
                }
                super.onPlayerStateChanged(playWhenReady, playbackState)
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                println("====>listen-onIsLoadingChanged:${isLoading}")
                super.onIsLoadingChanged(isLoading)
            }

            @Deprecated("Deprecated in Java")
            override fun onLoadingChanged(isLoading: Boolean) {
                println("====>listen-onLoadingChanged:${isLoading}")
                super.onLoadingChanged(isLoading)
            }

            override fun onPlayerErrorChanged(error: PlaybackException?) {
                println("====>listen-onPlayerErrorChanged:${error?.message}")
                permissionPiP = false
                super.onPlayerErrorChanged(error)
            }
        })
    }

    fun progressBar(show: Boolean) {
        if (show) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun rotation() {
        val layoutParamPlayerView: ViewGroup.LayoutParams = playerView.getLayoutParams()
        if (isFullScreen) { // video is full screen
            titleVideo.visibility = View.GONE
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            navigationTabBottom.visibility = View.VISIBLE
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            btnFullScreen.setBackgroundResource(R.drawable.ic_baseline_fullscreen_24)
            btnScrollToBottom.visibility = View.VISIBLE
            layoutParamPlayerView.width = widthScreen
            layoutParamPlayerView.height = widthScreen * 9 / 16
        } else {
            layoutParamPlayerView.width = MATCH_PARENT
            layoutParamPlayerView.height = MATCH_PARENT
            playerView.setLayoutParams(layoutParamPlayerView)
            btnScrollToBottom.visibility = View.GONE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
            titleVideo.visibility = View.VISIBLE
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            navigationTabBottom.visibility = View.GONE
            btnFullScreen.setBackgroundResource(R.drawable.ic_baseline_fullscreen_exit_24)
        }
        isFullScreen = !isFullScreen
    }

    private fun smallVideo() {
        val layoutParams: LayoutParams = layoutVideo.getLayoutParams() as LayoutParams
        if (baseView === null) {
            println("====>set${layoutParams}")
            baseView = layoutParams
        }
        if (videoIsSmall) {
            navigationTabBottom.visibility = View.GONE
            viewActionSmallVideo.visibility = View.GONE
            binding.frameVideo.fitsSystemWindows = true
            layoutParams.height = MATCH_PARENT
            layoutParams.width = MATCH_PARENT
            layoutVideo.setLayoutParams(layoutParams)
            btnScrollToBottom.visibility = View.VISIBLE
            playerView.useController = true
        } else {
            navigationTabBottom.visibility = View.VISIBLE
            btnCloseVideo.visibility = View.VISIBLE
            layoutParams.bottomToTop = R.id.navigationTabBottom
            layoutParams.topToTop = -1
            layoutParams.height = 200
            layoutParams.width = MATCH_PARENT
            layoutVideo.setLayoutParams(layoutParams)
            btnScrollToBottom.visibility = View.GONE
            playerView.useController = false
            viewActionSmallVideo.visibility = View.VISIBLE
        }
        videoIsSmall = !videoIsSmall
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (!permissionPiP) {
            return
        }
        if (videoIsSmall) {
            smallVideo()
        }
        buildPip()
        enterPictureInPictureMode(paramsPiP.build())
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        modePiPEnable = isInPictureInPictureMode
        bottomSheetDialog?.dismiss()
        if (lifecycle.currentState == Lifecycle.State.CREATED) {
            playerView.useController = true
        }
        else if (lifecycle.currentState == Lifecycle.State.STARTED){
            navigationTabBottom.visibility = View.GONE
            if (isInPictureInPictureMode) {
                btnCloseVideo.visibility = View.GONE
                playerView.useController = false
            }else{
                playerView.useController = true
            }
        }
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                hideSoftKeyboard(this@MainActivity)
                false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager: InputMethodManager = activity.getSystemService(
            INPUT_METHOD_SERVICE
        ) as InputMethodManager
        if(inputMethodManager.isAcceptingText()){
            showKeyboard(false)
        }
    }

    private fun showKeyboard(isShow: Boolean = false) {
        val view = this.currentFocus
        if (view === null) {
            return
        }
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (isShow) {
            imm.showSoftInput(view, 0)
        } else {
            view.clearFocus()
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun setInfo(video:Video){
        binding.tChanelVertical.setText(video.chanel_name)
        titleVideoVertical.setText(video.title)
        titleVideoSmall.setText(video.title)
        titleVideo.setText(video.title)
        loadImage(video.thumbnail)
    }

    private fun showBottomSheet(){
        bottomSheetDialog = BottomSheetDialog(this);
        bottomSheetDialog!!.setContentView(R.layout.bottom_sheet_add_video_lib);
        bottomSheetDialog!!.show()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        exitApp()
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            println("====>${intent?.getStringExtra(CONTROL_TYPE)}")
            when (intent?.getStringExtra(CONTROL_TYPE)) {
                CONTROL_TYPE_EXIT -> {
                    exitApp()
                }
                CONTROL_TYPE_PAUSE, CONTROL_TYPE_PLAY -> {
                    if(isPlay){
                        player.pause()
                    }else{
                        player.play()
                    }
                    buildPip()
                }
            }
        }
    }

    private fun exitApp(){
        stopService(Intent(this, MusicService::class.java))
        player.stop()
        finishAndRemoveTask()
        exitProcess(0)
    }

    private fun buildPip() {
        val actions = arrayListOf<RemoteAction>()

        if(!isPlay){
            actions.add(
                buildActionRemote(
                    CONTROL_TYPE_PLAY,
                    R.drawable.ic_baseline_play_circle_outline_24
                )
            )
        }else{
            actions.add(
                buildActionRemote(
                    CONTROL_TYPE_PAUSE,
                    R.drawable.ic_baseline_pause_circle_outline_24
                )
            )
        }
        actions.add(buildActionRemote(CONTROL_TYPE_EXIT, R.drawable.ic_baseline_exit_to_app_24))
        paramsPiP = PictureInPictureParams
            .Builder()
            .setActions(actions)

        setPictureInPictureParams(paramsPiP.build())
    }

    private fun buildActionRemote(
        controlType: String,
        resId: Int
    ): RemoteAction {
        val playActionIntent = Intent(ACTION_PIP_CONTROL).putExtra(CONTROL_TYPE, controlType)
        val playPendingIntent = getBroadcast(this, resId, playActionIntent, FLAG_IMMUTABLE)
        val playIcon = Icon.createWithResource(this, resId)
        return RemoteAction(playIcon, "Info${controlType}", "Video Info${controlType}", playPendingIntent);
    }
}
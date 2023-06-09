package org.youpip.app

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.*
import android.content.*
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.os.IBinder
import android.os.StrictMode
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.internal.LinkedTreeMap
import com.squareup.picasso.Picasso
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import org.youpip.app.adapter.ItemVideoMoreAdapter
import org.youpip.app.adapter.ViewPagerTabAdapter
import org.youpip.app.base.BaseActivity
import org.youpip.app.databinding.ActivityMainBinding
import org.youpip.app.dialog.MessageActivityNotification
import org.youpip.app.model.Video
import org.youpip.app.network.RequiresApi
import org.youpip.app.network.SOCKET_URL
import org.youpip.app.service.MusicService
import org.youpip.app.service.getVideo
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


private const val ACTION_PIP_CONTROL = "pip_control"
private const val CONTROL_TYPE = "control_type"
private const val CONTROL_TYPE_EXIT = "exit"
private const val CONTROL_TYPE_PLAY = "play"
private const val CONTROL_TYPE_PAUSE = "pause"

@Suppress("UNREACHABLE_CODE")
class MainActivity : BaseActivity(),ServiceConnection {
    private lateinit var binding: ActivityMainBinding
    lateinit var navigationTabBottom: BottomNavigationView
    private lateinit var viewPagerMain: ViewPager2
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
    private var paramsPiP = PictureInPictureParams.Builder()
    private lateinit var btnHideVideo:ImageView

    //
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemVideoMoreAdapter
    lateinit var btnOnlyAudio:ImageView
    lateinit var socket: Socket
    lateinit var dialogNotification: MessageActivityNotification

    //05-16
    var addVideoList:Boolean = false

    private var listAdded = ArrayList<Video>()
    private var videoIsLoaded:Boolean = false

    companion object {
        var videoP:Video? = null
        var musicService: MusicService? = null
        var modePiPEnable:Boolean = false
        lateinit var player: ExoPlayer
        var currentPositionMedia:Int = 0
    }

    override fun setViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        navigationTabBottom = binding.navigationTabBottom
        viewPagerMain = binding.viewPagerMain
        setContentView(binding.root)
        setViewVideoBinding()
        val displayMetrics = DisplayMetrics()
        val windowsManager = this.getSystemService(WINDOW_SERVICE) as WindowManager
        windowsManager.defaultDisplay.getMetrics(displayMetrics)
        findViewById()
        setPictureInPictureParams(PictureInPictureParams.Builder().build())
        dialogNotification = MessageActivityNotification(this)
        dialogNotification.window?.setGravity(Gravity.TOP)
        navigationTabBottom.setOnItemSelectedListener  { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_1 -> setTab(0)
                R.id.navigation_2 -> setTab(1)
                R.id.navigation_3 -> setTab(2)
                R.id.navigation_4 -> setTab(3)
            }
            true
        }
        viewPagerMain.currentItem = 1
        recyclerView = binding.sameVideo
        recyclerView.suppressLayout(true)
        customRecyclerView()
        navigationTabBottom.selectedItemId = R.id.navigation_1
        playWithShare()
    }

    private fun playWithShare() {
        val url = mySharePre.getString("YOUTUBE")
        if (url == null || url == "null") {
            return
        }
        mySharePre.remove("YOUTUBE")
        val api = callApi.detail(token, url = url.toString())
        RequiresApi.callApi(this, api) {
            if (it == null || it.status != 200) {
                return@callApi
            }
            val item = it.data as LinkedTreeMap<*, *>

            if (item["video_id"].toString() == "") {
                return@callApi
            }

            val model = Video(
                item["video_id"].toString(),
                item["title"].toString(),
                item["thumbnail"].toString(),
                item["published_time"].toString(),
                item["view_count_text"].toString(),
                item["chanel_name"].toString(),
                item["chanel_url"].toString(),
                item["time_text"].toString(),
            )
            playVideo(model)
        }
    }

    private fun setTab(tab:Int){
        if(tab == tabCurrent){
            return
        }
        showNavigationBottom(true)
        tabCurrent = tab
        viewPagerMain.currentItem = tab

        if(player.isPlaying){
            permissionPiP = true
            if(tab == 2){
                permissionPiP = false
                layoutVideo.visibility = View.GONE
            }else{
                layoutVideo.visibility = View.VISIBLE
            }
        }
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
        btnOnlyAudio =  binding.onlyAudio
        btnHideVideo = binding.hideVideo

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
        if(::video.isInitialized && videoPlay==video && videoIsLoaded){
            if (videoIsSmall) {
                smallVideo()
            }
            return;
        }

        if(videoPlay==null){
            return
        }

        resetLoadPlayer()
        player.stop()
        player.clearMediaItems();
        listAdded = arrayListOf<Video>()
        currentPositionMedia = 0
        video = videoPlay
        if(!videoPlay.video_id.contains("truyen-hinh")){
            playWithYoutube(videoPlay)
            suggestByVideoId(videoPlay.video_id)
        }
        if(videoPlay.video_id.contains("truyen-hinh")){
            urlPlayVtv(videoPlay.video_id)
        }
        videoP = video
        setInfo(video)
    }

    private fun urlPlayVtv(videoId:String) {
        adapter.setData(arrayListOf())
        loadImage(null,true)
        val api = callApi.linkPlay(token,videoId)
        RequiresApi.callApi(this,api){
            if(it==null || it.status != 200){
                alert("Hệ thống gián đoạn, vui lòng thử lại sau!")
                return@callApi
            }

            val data = it.data as LinkedTreeMap<*, *>
            val url = data["url"].toString()
            val dataSourceFactory: DataSource.Factory =
                DefaultDataSourceFactory(this, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36")
            val mediaItem: MediaItem = MediaItem.fromUri(url)
            val source =  HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
            player.setMediaSource(source)
            player.prepare()
            player.play()
            playerView.player = player
        }
    }

    private fun playWithYoutube(videoPlay:Video){
        listAdded.add(videoPlay)
        apiDetailVideo(videoPlay.video_id) {
            val all = it[0];
            val video = it[1];
            val audio = it[2];
            var play = false;
            if (video.isNotEmpty() && audio.isNotEmpty()) {
                play = true
                val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
                val videoSource: MediaSource =
                    ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(video))
                val audioSource: MediaSource =
                    ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(audio))
                player.setMediaSource(MergingMediaSource(videoSource, audioSource))
            }
            if (!play && all.isNotEmpty()) {
                play = true
                val mediaItem: MediaItem = MediaItem.fromUri(all)
                player.setMediaItem(mediaItem)
            }
            if (play) {
                player.prepare()
                player.play()
                playerView.player = player
            }else{
                alert("Không thể phát video: ${videoPlay.video_id}")
            }
        }
    }

    private fun apiDetailVideo(videoId:String,callback:(Array<String>) -> Unit){
        saveRecentlyView(videoId)
        val url = "https://youtube.com/watch?v=${videoId}"
        getVideo(this) {
            callback(it)
        }.extract(url)
    }

    private fun saveRecentlyView(videoId:String)
    {
        val api = callApi.findLink(token,videoId)
        RequiresApi.callApi(this,api){
            if(it==null || it.status != 200){
                return@callApi
            }
        }
    }

    private fun setLayoutParamFullScreen() {
        val layoutParamVideo = layoutVideo.layoutParams as LayoutParams
        layoutParamVideo.width = LayoutParams.MATCH_PARENT
        layoutParamVideo.height = LayoutParams.MATCH_PARENT
        layoutVideo.layoutParams = layoutParamVideo
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
        val displayMetrics = DisplayMetrics()
        val windowsManager = this.getSystemService(WINDOW_SERVICE) as WindowManager
        windowsManager.defaultDisplay.getMetrics(displayMetrics)
        widthScreen = displayMetrics.widthPixels
        onCreateBaseVideo()
        setupUI(binding.root)

        val intent = Intent(this,MusicService::class.java)
        bindService(intent,this, BIND_AUTO_CREATE)
        startService(intent)
//        connectSocket()
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
        player.repeatMode = Player.REPEAT_MODE_ALL
        btnOnlyAudio.setOnClickListener {
            permissionPiP = false
            val startMain = Intent(Intent.ACTION_MAIN)
            startMain.addCategory(Intent.CATEGORY_HOME)
            startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(startMain)
        }
    }

    private fun handlerActionPlayer(){
        player.addListener(object : Player.Listener {
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                super.onPlayWhenReadyChanged(playWhenReady, reason)
            }

            @Deprecated("Deprecated in Java")
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                playerView.visibility = View.VISIBLE
                if(playWhenReady!=isPlay && modePiPEnable){
                    isPlay = playWhenReady
                    buildPip()
                }
                isPlay = playWhenReady
                super.onPlayerStateChanged(playWhenReady, playbackState)

                when (playbackState) {
                    Player.STATE_ENDED -> {
                        player.playWhenReady = false
                    }
                    Player.STATE_READY -> {
                        loadImage(null, false)
                    }
                    Player.STATE_BUFFERING -> {
                        loadImage(null, true)
                    }
                    Player.STATE_IDLE -> {}
                }
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                super.onIsLoadingChanged(isLoading)
                if(!isLoading){
                    loadImage(null, false)
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onLoadingChanged(isLoading: Boolean) {
                super.onLoadingChanged(isLoading)
            }

            override fun onPlayerErrorChanged(error: PlaybackException?) {
                permissionPiP = false
                musicService!!.showNotification(false)
                loadImage(null, true)
                super.onPlayerErrorChanged(error)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                musicService!!.showNotification(isPlaying)
                permissionPiP = isPlaying
                if(isPlaying && !isPlay){
                    loadImage(null, false)
                }
                isPlay = isPlaying
                if(isPlaying){
                    if(!videoIsLoaded && !addVideoList){
                        addVideoToList()
                    }
                    videoIsLoaded = true
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                val current = player.currentMediaItemIndex;
                video = listAdded[current]
                saveRecentlyView(video.video_id)
                setInfo(video)
                videoP = video
                addVideoToList()
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

            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            btnFullScreen.setBackgroundResource(R.drawable.ic_baseline_fullscreen_24)
            btnScrollToBottom.visibility = View.VISIBLE
            layoutParamPlayerView.width = widthScreen
            layoutParamPlayerView.height = widthScreen * 9 / 16
        } else { // rotate video to horizontal
            layoutParamPlayerView.width = MATCH_PARENT
            layoutParamPlayerView.height = MATCH_PARENT
            playerView.layoutParams = layoutParamPlayerView
            btnScrollToBottom.visibility = View.GONE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
            titleVideo.visibility = View.VISIBLE
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            btnFullScreen.setBackgroundResource(R.drawable.ic_baseline_fullscreen_exit_24)
        }
        showNavigationBottom(false)
        isFullScreen = !isFullScreen
    }

    private fun smallVideo() {
        val layoutParams: LayoutParams = layoutVideo.getLayoutParams() as LayoutParams
        if (baseView === null) {
            baseView = layoutParams
        }
        val transition = ChangeBounds()
        TransitionManager.beginDelayedTransition(layoutVideo,transition)
        if (videoIsSmall) { // video is small
            viewActionSmallVideo.visibility = View.GONE
            binding.frameVideo.fitsSystemWindows = true
            layoutParams.height = MATCH_PARENT
            layoutParams.width = MATCH_PARENT
            layoutVideo.layoutParams = layoutParams
            btnScrollToBottom.visibility = View.VISIBLE
            playerView.useController = true
            showNavigationBottom(false)
        } else {
            btnCloseVideo.visibility = View.VISIBLE
            layoutParams.bottomToTop = R.id.navigationTabBottom
            layoutParams.topToTop = -1
            layoutParams.height = 200
            layoutParams.width = MATCH_PARENT
            layoutVideo.layoutParams = layoutParams
            btnScrollToBottom.visibility = View.GONE
            playerView.useController = false
            viewActionSmallVideo.visibility = View.VISIBLE
            showNavigationBottom(true)
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
            showNavigationBottom(false)
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

    fun showKeyboard(isShow: Boolean = false) {
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

    override fun onResume() {
        super.onResume()
        if(player.isPlaying && tabCurrent!=2){
            permissionPiP = true
        }

        playWithShare()
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

    private fun suggestByVideoId(videoId:String){
        val api = callApi.suggestByVideoId(token,videoId)
        RequiresApi.callApi(this,api){
            if(it==null || it.status != 200){
                return@callApi
            }
            val data = it.data as LinkedTreeMap<*, *>
            val list = data.get("list") as ArrayList<*>
            val dataItems = arrayListOf<Video>()
            val dataItems2 = arrayListOf<Video>()
            dataItems2.add(video)
            list.forEach { item->
                item as LinkedTreeMap<*, *>
                val model = Video(
                    item.get("video_id").toString(),
                    item.get("title").toString(),
                    item.get("thumbnail").toString(),
                    item.get("published_time").toString(),
                    item.get("view_count_text").toString(),
                    item.get("chanel_name").toString(),
                    item.get("chanel_url").toString(),
                    item.get("time_text").toString(),
                )
                dataItems.add(model)
                dataItems2.add(model)
            }
            adapter.setData(dataItems)
            listVideo = dataItems2
            if(videoIsLoaded){
                addVideoToList()
            }
        }
    }

    private fun customRecyclerView(){
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = ItemVideoMoreAdapter{
            playVideo(it)
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    fun showNavigationBottom(show:Boolean){
        val transition = ChangeBounds()
        TransitionManager.beginDelayedTransition(navigationTabBottom,transition)
        if(show){
            navigationTabBottom.visibility = View.VISIBLE
        }else{
            navigationTabBottom.visibility = View.GONE
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText && mySharePre.getString("SCREEN")!="ChatFragment") {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun connectSocket()
    {
        val map = mapOf<String,String>("token" to token.replace("Bearer ",""))
        // Set up the Socket.IO client
        val options = IO.Options().apply {
            hostname = SOCKET_URL
            port = 3003
            auth = map
            }

        socket = IO.socket("http://${options.hostname}:${options.port}", options)
        socket.on(Socket.EVENT_CONNECT, onConnect)

        socket.on(ESocket.Message.value+"_OTHER"){
            val data = it[0] as JSONObject
            runOnUiThread {
                if(mySharePre.getString("SCREEN")!="ChatFragment"){
                    dialogNotification.showMessage(data.getString("content"))
                }
            }
        }
        socket.connect()
        return
    }

    private val onConnect = Emitter.Listener {

    }

    private val onDisconnect = Emitter.Listener {
        Toast.makeText(this,"onDisconnect",Toast.LENGTH_SHORT).show()
    }

    private val onMessage = Emitter.Listener { args ->

    }

    private fun testUrl(){
        getVideo(this){

        }.extract("https://www.youtube.com/watch?v=u90pJLgWAqA")
    }

    fun closeVideo()
    {
        if(player.isPlaying){
            player.stop()
        }
        layoutVideo.visibility = View.GONE
    }

    private fun addVideoToList() {
        if(mySharePre.getBoolean("PASS_REVIEW") || listVideo.isEmpty() || listVideo.size <= currentPositionMedia){
            return;
        }
        addVideoList = true
        currentPositionMedia++
        val video:Video = listVideo[currentPositionMedia]
        apiDetailVideo(video.video_id) {
            val all = it[0];
            val urlVideo = it[1];
            val audio = it[2];
            var play = false;
            if (urlVideo.isNotEmpty() && audio.isNotEmpty()) {
                listAdded.add(video)
                play = true
                val videoSource: MediaSource =
                    ProgressiveMediaSource.Factory(DefaultDataSourceFactory(this))
                        .createMediaSource(MediaItem.fromUri(urlVideo))
                val audioSource: MediaSource =
                    ProgressiveMediaSource.Factory(DefaultDataSourceFactory(this))
                        .createMediaSource(MediaItem.fromUri(audio))
                val source = MergingMediaSource(videoSource, audioSource)
                player.addMediaSource(listAdded.size-1,source)
            }

            if (!play && all.isNotEmpty()) {
                listAdded.add(video)
                play = true
                val mediaItem: MediaItem =
                    MediaItem.Builder().setUri(all).setMediaId(listAdded.size.toString())
                        .build()
                player.addMediaItem(mediaItem)
            }
            if(play){
                alert("Video tiếp theo: ${video.title}")
            }else{
                addVideoToList()
            }
        }
    }

    private fun resetLoadPlayer(){
        currentPositionMedia = 0
        videoIsLoaded = false
        addVideoList = false
        listVideo = emptyList<Video>()
        permissionPiP = false
        videoStart = false
        playerView.visibility = View.INVISIBLE
        titleVideoVertical.visibility = View.VISIBLE
        showNavigationBottom(false)
        setLayoutParamFullScreen()
        if (videoIsSmall) {
            smallVideo()
        }
    }
}
package org.youpip.app

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.util.MimeTypes

class MainActivity : AppCompatActivity() {

    lateinit var player:ExoPlayer

    lateinit var view:PlayerView

    lateinit var mediaItem: MediaItem
    lateinit var mediaItem2: MediaItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var url = "https://rr2---sn-42u-i5ol6.googlevideo.com/videoplayback?expire=1678487954&ei=Ml0LZM3RKaWplQT41IO4Dw&ip=1.54.238.183&id=o-AEy6wLpBj1OXPaCfPBhlM3adRfMvtGIOTPIH8DK8BuwX&itag=396&aitags=133%2C134%2C135%2C136%2C137%2C160%2C242%2C243%2C244%2C247%2C248%2C271%2C278%2C313%2C394%2C395%2C396%2C397%2C398%2C399%2C400%2C401&source=youtube&requiressl=yes&mh=JJ&mm=31%2C26&mn=sn-42u-i5ol6%2Csn-un57enez&ms=au%2Conr&mv=m&mvi=2&pcm2cms=yes&pl=24&initcwndbps=1077500&vprv=1&mime=video%2Fmp4&ns=Hcw5UuMj3bNUpEDTjZsvwpML&gir=yes&clen=6910899&dur=226.133&lmt=1671240594199864&mt=1678466008&fvip=3&keepalive=yes&fexp=24007246&c=WEB&txp=5537434&n=jS0SY-JXMNEOJQ&sparams=expire%2Cei%2Cip%2Cid%2Caitags%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cdur%2Clmt&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpcm2cms%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRQIhAKehZc5HAhS75vQJdmXBwCxfVohlTmVq1aqJO44YvwQcAiBR3-IjePB6S1vykr1W9wYejjovS3dOU3nFhq-D7V9c9g%3D%3D&alr=yes&sig=AOq0QJ8wRAIgeO9LzDcqKflJGchjLBqBZHO5lxkXWsRWlPodPITiMDkCIC9lioBD0g5AVwGYEf29F6MMZ6Foe_gRAfeSce9ZWLIK&cpn=Za8QPZ0hxSqowRH_&cver=2.20230309.08.00-canary_experiment&range=0-95924&rn=1&rbuf=0&altitags=395%2C394"
//        url = "https://rr2---sn-42u-i5ol6.googlevideo.com/videoplayback?expire=1678487954&ei=Ml0LZM3RKaWplQT41IO4Dw&ip=1.54.238.183&id=o-AEy6wLpBj1OXPaCfPBhlM3adRfMvtGIOTPIH8DK8BuwX&itag=401&aitags=133%2C134%2C135%2C136%2C137%2C160%2C242%2C243%2C244%2C247%2C248%2C271%2C278%2C313%2C394%2C395%2C396%2C397%2C398%2C399%2C400%2C401&source=youtube&requiressl=yes&mh=JJ&mm=31%2C26&mn=sn-42u-i5ol6%2Csn-un57enez&ms=au%2Conr&mv=m&mvi=2&pcm2cms=yes&pl=24&initcwndbps=1077500&vprv=1&mime=video%2Fmp4&ns=Hcw5UuMj3bNUpEDTjZsvwpML&gir=yes&clen=297818246&dur=226.133&lmt=1671244382636743&mt=1678466008&fvip=3&keepalive=yes&fexp=24007246&c=WEB&txp=5532434&n=Qzn6bvRZpNcbvbXro&sparams=expire%2Cei%2Cip%2Cid%2Caitags%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cdur%2Clmt&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpcm2cms%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRQIgJUCzAMrdBxqBcKDGCVbCzsYIkKr1TdEBm4CQjnknh8ACIQDPu0j_eLL7gW-LSuK_8sc_5U5xo5uci09cMbh_-6xkew%3D%3D"
//        url = "https://rr2---sn-42u-i5oee.googlevideo.com/videoplayback?expire=1678489527&ei=V2MLZISABpellQSHu6foCQ&ip=1.54.238.183&id=o-AIHCD08ldCuNCznxVxMQAfhNMOjd-4novvQOgPhrDBUr&itag=137&aitags=133%2C134%2C135%2C136%2C137%2C160%2C242%2C243%2C244%2C247%2C248%2C278%2C394%2C395%2C396%2C397%2C398%2C399&source=youtube&requiressl=yes&mh=Az&mm=31%2C26&mn=sn-42u-i5oee%2Csn-un57enez&ms=au%2Conr&mv=m&mvi=2&pl=24&initcwndbps=888750&vprv=1&mime=video%2Fmp4&ns=So_25CgmnMeA0K0w6u-7K7cL&gir=yes&clen=331661303&dur=3705.833&lmt=1676879226808054&mt=1678467445&fvip=1&keepalive=yes&fexp=24007246&c=WEB&txp=5535434&n=FSgThhXlFDrelzqGM&sparams=expire%2Cei%2Cip%2Cid%2Caitags%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cdur%2Clmt&sig=AOq0QJ8wRQIhALZ-ciex3ebios8fqcWPhzi83Na6jD39D48oC-LHB5-VAiBCvcNYXLgLKldBZdOIPpiTnj29XatzyNaNgsZMB17Hxg%3D%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRQIgLP_XznTMpFIYB5DV6SJnp2CByN9LUxFCtDW8gusiJwECIQCpszlnOkE_C7oAhHFyfsAfPQFRJU4gyaXBEYDCsxmAuQ%3D%3D"
//        url = "https://rr2---sn-42u-i5oee.googlevideo.com/videoplayback?expire=1678489498&ei=OmMLZKPyJvubvcAP2YCykAk&ip=1.54.238.183&id=o-AGpc_n-wqJOWkqCRrQ04LWl5rqETcOkmxBAl1Jzkse_k&itag=251&source=youtube&requiressl=yes&mh=Az&mm=31%2C26&mn=sn-42u-i5oee%2Csn-un57enez&ms=au%2Conr&mv=m&mvi=2&pl=24&initcwndbps=888750&vprv=1&mime=audio%2Fwebm&ns=tDYdMk60gdCe5drgTS0PtYcL&gir=yes&clen=64989418&dur=3705.861&lmt=1676872945898780&mt=1678467445&fvip=1&keepalive=yes&fexp=24007246&c=WEB&txp=5532434&n=Ml2yiCJILcKXRg&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cdur%2Clmt&sig=AOq0QJ8wRQIhANEYEfAOPMYAY7ynU3fHWRkTxwq5nvf71C_8YZ_NJSrTAiAxgktVpnNhUlNDYAZe0cbHPouxmnGQk20b7NVDgD0N0Q%3D%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRAIgMfrqduyKi-TU5Q4TmH6jdMIqKwf1DklaAt6w2TkTxLsCIBv9wiUdkDUc9eyvWsYV7zJ6uoM9fgQekf_-wiyGwpm-&alr=yes&cpn=_5fRJtAA9DeJq0qK&cver=2.20230309.08.00-canary_experiment&range=0-6843&rn=2&rbuf=0"
        url = "https://rr3---sn-42u-i5oey.googlevideo.com/videoplayback?expire=1678522736&ei=EOULZJDuAfyUs8IPgJCjkAY&ip=1.54.238.183&id=o-ADne0NXXEZh79pt7URyevTzV8Poo33QWVkaLfY3wfyod&itag=248&aitags=133%2C134%2C135%2C136%2C137%2C160%2C242%2C243%2C244%2C247%2C248%2C278%2C394%2C395%2C396%2C397%2C398%2C399&source=youtube&requiressl=yes&mh=PT&mm=31%2C26&mn=sn-42u-i5oey%2Csn-un57snee&ms=au%2Conr&mv=m&mvi=3&pl=24&initcwndbps=2150000&vprv=1&mime=video%2Fwebm&ns=oxtTbl4ec-HVLtTJtjt7YnAL&gir=yes&clen=20696062&dur=244.368&lmt=1677415917478851&mt=1678500776&fvip=3&keepalive=yes&fexp=24007246&c=WEB&txp=5535434&n=DYLTjRnBZXINID_pv&sparams=expire%2Cei%2Cip%2Cid%2Caitags%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cdur%2Clmt&sig=AOq0QJ8wRAIgQTH6knDQJPOk6dCkV5xZIjYQ8QgJoQ_KrfHjTIesPUQCIECGAQuFWeMUXFoAm16NK6ym8lo19duJBgqeRR-djWjq&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRAIgOBT0wvqav8ut4KflZZ4bK0e6lMcCiH3X9SD_oz_HOpkCID_J_OLGN2_QfoX5Be8I0uQsfnj1mjKpeFOPYjPd9uP4"
        val urlMp3 = "https://rr3---sn-42u-i5oey.googlevideo.com/videoplayback?expire=1678522736&ei=EOULZJDuAfyUs8IPgJCjkAY&ip=1.54.238.183&id=o-ADne0NXXEZh79pt7URyevTzV8Poo33QWVkaLfY3wfyod&itag=140&source=youtube&requiressl=yes&mh=PT&mm=31%2C26&mn=sn-42u-i5oey%2Csn-un57snee&ms=au%2Conr&mv=m&mvi=3&pl=24&initcwndbps=2150000&vprv=1&mime=audio%2Fmp4&ns=oxtTbl4ec-HVLtTJtjt7YnAL&gir=yes&clen=3956734&dur=244.436&lmt=1677410140526342&mt=1678500776&fvip=3&keepalive=yes&fexp=24007246&c=WEB&txp=5532434&n=DYLTjRnBZXINID_pv&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cdur%2Clmt&sig=AOq0QJ8wRQIhAPqRq-kjvQCre4rQJJue5rxodfRFCggafABtx7tnfvTYAiBxh8L9m89Y0FX3dh7eTcq8DKwQ-6fV88aIqBffPktPzg%3D%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRAIgOBT0wvqav8ut4KflZZ4bK0e6lMcCiH3X9SD_oz_HOpkCID_J_OLGN2_QfoX5Be8I0uQsfnj1mjKpeFOPYjPd9uP4"

        setContentView(R.layout.activity_main)
        player = ExoPlayer.Builder(this)
            .build()
        view = findViewById(R.id.playerView)




        mediaItem = MediaItem.Builder()
            .setUri(url)
//            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()
        mediaItem2 = MediaItem.Builder()
            .setUri(urlMp3)
//            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()
//        player.setMediaItem(mediaItem);
//        player.setMediaItem(mediaItem2);

        val videoSource = ProgressiveMediaSource.Factory(FileDataSource.Factory())
            .createMediaSource(mediaItem)

        val audioSource = ProgressiveMediaSource.Factory(FileDataSource.Factory())
            .createMediaSource(mediaItem2)

        val mergeSource = MergingMediaSource(videoSource,audioSource)
//        player.setMediaSource(audioSource)
        player.setMediaItem(mediaItem2)
        player.prepare();
        player.play();
        view.player = player

    }
}
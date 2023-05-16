package org.youpip.app.service

import android.content.Context
import android.util.Log
import android.util.SparseArray
import androidx.core.util.forEach
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile

class getVideo(context: Context, val callBack: (Array<String>) -> Unit) : YouTubeExtractor(context) {
    override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, videoMeta: VideoMeta?) =
        if (ytFiles != null) {
            println("====>youpip${ytFiles}")
            var audioAndVideo = ""
            var audio = ""
            var video = ""

            //all file
            for(item:Int in arrayOf(22, 18, 43)){
                if(ytFiles[item]!=null && audioAndVideo.isEmpty()){
                    println("====>youpip:all:${item}")
                    audioAndVideo = ytFiles[item].url
                }
            }

            //video
            for (item: Int in arrayOf(244,247,302,135,136,298)) {
                if (ytFiles[item] != null && video.isEmpty()) {
                    println("====>youpip:video:${item}")
                    video = ytFiles[item].url
                }
            }

            //audio
            for (item: Int in arrayOf(140,171,250)) {
                if (ytFiles[item] != null && audio.isEmpty()) {
                    println("====>youpip:audio:${item}")
                    audio = ytFiles[item].url
                }
            }

            for (item:Int in arrayOf(91,92,93,94,95,96)){
                if (ytFiles[item] != null) {
                    println("====>youpip:hls:${item}:${ytFiles[item].url}")
                }
            }
            Log.i("YTB_DL","${audioAndVideo}  \n  ${video} \n  ${audio}")
            callBack(arrayOf(audioAndVideo,video,audio))

        } else {
            Log.i("YTB_DL","FAIL!")
            callBack(arrayOf("","",""))
        }
}
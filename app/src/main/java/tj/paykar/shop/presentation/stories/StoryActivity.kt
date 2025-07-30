package tj.paykar.shop.presentation.stories

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import tj.paykar.shop.data.model.home.StoriesModel
import tj.paykar.shop.data.storage.StoriesStorage
import tj.paykar.shop.databinding.ActivityStoryBinding
import java.io.File
import kotlin.math.abs

class StoryActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    lateinit var binding: ActivityStoryBinding
    var position = 0
    var stories = ArrayList<StoriesModel>()
    private lateinit var animator: ObjectAnimator
    private lateinit var gestureDetector: GestureDetector
    private lateinit var gestureDetector2: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(binding.root)

        gestureDetector2 = GestureDetector(this, this)
        stories = StoriesStorage(this).getStories()
        val bundle: Bundle? = intent.extras
        val storyId = bundle?.getInt("storyId") ?: 0
        val storyName = bundle?.getString("storyName") ?: ""
        val storyType = bundle?.getString("storyType") ?: ""
        position = bundle?.getInt("storyPosition") ?: 0

        startStory(storyId, storyName, storyType)
        setListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListener() {
        gestureDetector = GestureDetector(this, SingleTapConfirm())

        val touchListener = object  : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                gestureDetector2.onTouchEvent(event!!)

                if (gestureDetector.onTouchEvent(event)) {
                    if(v?.id == binding.rightPart.id){
                        Log.d("---D RightClick", "Next")
                        next()
                    } else if(v?.id == binding.leftPart.id){
                        Log.d("---D LeftClick", "Previous")
                        previous()
                    }
                    return true
                }
                else {
                    return when(event.action){

                        MotionEvent.ACTION_DOWN -> {
                            Log.d("---D DownClick", "Pause")
                            pause()
                            true
                        }

                        MotionEvent.ACTION_UP -> {
                            Log.d("---D UpClick", "Resume")
                            resume()
                            true
                        }

                        else -> false
                    }
                }
            }
        }

        binding.rightPart.setOnTouchListener(touchListener)
        binding.leftPart.setOnTouchListener(touchListener)
    }

    private inner class SingleTapConfirm : GestureDetector.SimpleOnGestureListener(){
        override fun onSingleTapUp(event: MotionEvent): Boolean {
            return true
        }
    }

    private fun startStory(storyId: Int, storyName: String, storyType: String){

        StoriesStorage(this).addSeen(storyId)
        binding.imageStory.isVisible = storyType == "Image"
        binding.videoStory.isVisible = storyType == "Video"

        if (storyType == "Image"){
            Picasso.get()
                .load("https://paykar.shop$storyName")
                .into(binding.imageStory, object : Callback {

                    override fun onSuccess() {
                        setAnimator(3000)
                    }

                    override fun onError(e: Exception?) {
                        Toast.makeText(this@StoryActivity,"Не удалось загрузить историю", Toast.LENGTH_LONG).show()
                        e?.printStackTrace()
                    }
                })
        }
        else if (storyType == "Video"){

            val videoFile = File(this.cacheDir, "videos/$storyName")
            Log.d("--D FileInfo", videoFile.absolutePath)

            if (videoFile.exists()) {
                Log.d("--D FromCache", "Yes")
                val videoUri = Uri.fromFile(videoFile)
                binding.videoStory.setVideoURI(videoUri)
            } else {
                Log.d("--D FromCache", "No")
                val str = "https://mobileapp.paykar.tj/management/func/image/upload/$storyName"
                val uri = Uri.parse(str)
                binding.videoStory.setVideoURI(uri)
            }

            binding.videoStory.requestFocus()
            binding.videoStory.start()

            binding.videoStory.setOnInfoListener(object : MediaPlayer.OnInfoListener {
                override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        // Here the video starts
                        setAnimator(binding.videoStory.duration)
                        return true
                    }
                    return false
                }
            })

            binding.videoStory.setOnErrorListener {_, what,_ ->
                Log.e("--D Error VideoView", what.toString())
                val str = "https://mobileapp.paykar.tj/management/func/image/upload/$storyName"
                val uri = Uri.parse(str)
                binding.videoStory.setVideoURI(uri)
                binding.videoStory.requestFocus()
                binding.videoStory.start()
                true
            }
        }
    }

    private fun setAnimator(animationDuration: Int){
        animator = ObjectAnimator.ofInt(binding.storyProgress, "progress", 0, 100)
        animator.duration = animationDuration.toLong()
        animator.interpolator = LinearInterpolator()
        animator.start()

        animator.addListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator) {}

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onAnimationEnd(animation: Animator) {
                binding.storyProgress.progress = 0
                if (stories.size - position > 1){
                    position += 1
                    //startStory(stories[position].id ?: 0, stories[position].name ?: "", stories[position].type ?: "")
                    startStory(stories[position].id ?: 0, stories[position].picture ?: "", "Image")
                }else{
                    finish()
                }
                Log.d("---D AnimEnd", "End")
            }

            override fun onAnimationCancel(animation: Animator) {
                Log.d("---D AnimCanceled", "Canceled")
            }
            override fun onAnimationRepeat(animation: Animator) {}

        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun next(){
        try {
            animator.pause()
            if (stories.size - position > 1){
                binding.storyProgress.progress = 0
                position += 1
                //startStory(stories[position].id ?: 0, stories[position].name ?: "", stories[position].type ?: "")
                startStory(stories[position].id ?: 0, stories[position].picture ?: "", "Image")
            }else{
                finish()
            }
        }catch (_:Exception){}
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun previous(){
        if (position != 0){
            try {
                animator.pause()
                binding.storyProgress.progress = 0
                position -= 1
                //startStory(stories[position].id ?: 0, stories[position].name ?: "", stories[position].type ?: "")
                startStory(stories[position].id ?: 0, stories[position].picture ?: "", "Image")
            }catch (_:Exception){}
        }
        else {
            finish()
        }
    }

    private fun pause(){
        try {
            animator.pause()
            binding.videoStory.pause()
        }catch (_:Exception){}
    }

    private fun resume(){
        try {
            animator.resume()
            binding.videoStory.start()
        }catch (_:Exception){}
    }

    override fun onDown(p0: MotionEvent): Boolean { return true }

    override fun onShowPress(p0: MotionEvent) {}

    override fun onSingleTapUp(p0: MotionEvent): Boolean { return true }
    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return true
    }

    //override fun onScroll(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean { return true }

    override fun onLongPress(p0: MotionEvent) {}
    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (e1?.x != null){
            val distanceX = e2.x.minus(e1.x)
            val distanceY = e2.y.minus(e1.y)

            if (abs(distanceX) < abs(distanceY)) {
                if (distanceY > 0) {
                    Log.d("--D Swipe", "Down")
                    finish()
                }
            }
        }

        return true
    }

//    override fun onFling(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean {
//        val distanceX = p1.x.minus(p0.x)
//        val distanceY = p1.y.minus(p0.y)
//
//        if (abs(distanceX) < abs(distanceY)) {
//            if (distanceY > 0) {
//                Log.d("--D Swipe", "Down")
//                finish()
//            }
//        }
//
//        return true
//    }
}
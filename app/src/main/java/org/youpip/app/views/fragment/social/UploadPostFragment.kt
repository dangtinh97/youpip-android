package org.youpip.app.views.fragment.social

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Base64
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.gson.internal.LinkedTreeMap
import org.youpip.app.MainActivity
import org.youpip.app.R
import org.youpip.app.adapter.PostMeAdapter
import org.youpip.app.databinding.FragmentUploadPostBinding
import org.youpip.app.dialog.DialogNotification
import org.youpip.app.model.PostModel
import org.youpip.app.network.ApiService
import org.youpip.app.network.RequiresApi
import org.youpip.app.utils.MySharePre
import java.io.ByteArrayOutputStream


class UploadPostFragment(val callApi: ApiService,val mySharePre: MySharePre) : BottomSheetDialogFragment() {

    private lateinit var binding:FragmentUploadPostBinding

    private val REQUEST_IMAGE = 100;

    private lateinit var btnClearImage: MaterialButton

    private lateinit var imagePost:ImageView
    private lateinit var postContent:EditText
    private var imageUri: Uri? = null

    private lateinit var adapter: PostMeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var closeUploadPost:ImageView
    private var idImage:String = "";
    private lateinit var btnSubmit:MaterialButton
    private lateinit var notification: DialogNotification
    private var lastOid:String? = null
    private var token:String=""
    private var postOidPendingDelete:String=""
    private var isLoadMore:Boolean = false
    private lateinit var layoutManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        token = mySharePre.getString("token").toString()
        super.onViewCreated(view, savedInstanceState)
        imagePost = binding.imagePost
        btnClearImage = binding.btnClearImage
        postContent = binding.postContent
        btnSubmit = binding.submit
        closeUploadPost = binding.closeUploadPost
        handlerOnClick()
        recyclerView = binding.recyclerView
        customRecyclerView()
        loadData()
        initScrollRecyclerView()
        notification = DialogNotification(requireContext())
    }

    private fun initScrollRecyclerView()
    {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
            {
                super.onScrolled(recyclerView, dx, dy)

                if (!isLoadMore)
                {
                    //findLastCompletelyVisibleItemPostition() returns position of last fully visible view.
                    ////It checks, fully visible view is the last one.
                    if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0)
                    {
                        isLoadMore = true
                        loadData()
                    }
                }
            }
        })
    }

    private fun handlerOnClick(){
        btnSubmit.setOnClickListener {
            val content = postContent.text.toString()
            if(content=="" && idImage==""){
                return@setOnClickListener
            }

            val apiUpload = callApi.createPost(
                token = token,
                content = content,
                attachmentId = idImage
            )

            RequiresApi.callApi(requireContext(),apiUpload){
                idImage = ""
                showWithImage(false)
                postContent.setText("")

                if(it==null){
                    return@callApi
                }
                val data = it.data as LinkedTreeMap<*, *>
                println("====>Post:${data}")
                Toast.makeText(requireContext(),"Đăng bài thành công!",Toast.LENGTH_SHORT).show()
                lastOid = null
                loadData()
            }
        }

        btnClearImage.setOnClickListener {
            imageUri = null
            idImage = ""
            showWithImage(false)
        }
        closeUploadPost.setOnClickListener {
            dialog?.dismiss()
        }
        binding.choiceImage.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, REQUEST_IMAGE)
        }
    }

    private fun customRecyclerView(){
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = PostMeAdapter { post: Array<String> ->
            val postOid = post[0]
            val action = post[1]
            if(action==="DELETE"){
                notification.showMessage("Bạn có muốn xoá bài viết này?") {
                    notification.dismiss()
                    deletePost(postOid)
                }
            }

            if(action==="COMMENT"){
                val bottomSheet = CommentFragment(postOid, callApi, mySharePre)
                fragmentManager?.let { it1 -> bottomSheet.show(it1, bottomSheet.tag) }
                return@PostMeAdapter
            }

        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun deletePost(postOid:String)
    {
        val deletePost = callApi.deletePost(
            token,
            postOid
        )

        RequiresApi.callApi(requireContext(),deletePost){
            if(it==null || it.status!=200){
                return@callApi
            }
            Toast.makeText(requireContext(),"Xoá bài viết thành công!",Toast.LENGTH_SHORT).show()
            lastOid = null
            loadData()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!this::binding.isInitialized){
            binding = FragmentUploadPostBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    private fun getScreenHeight(): Int {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE) {
            imageUri = data?.data
            imageUri?.let { imageEncode(it) }
            println("====>Uri${imageUri}")
            imagePost.setImageURI(imageUri)
            showWithImage(true)
        }
    }

    private fun imageEncode(imageUri:Uri){
        val input = activity?.getContentResolver()?.openInputStream(imageUri)
        val image = BitmapFactory.decodeStream(input , null, null)

        // Encode image to base64 string
        val baos = ByteArrayOutputStream()
        if(image==null){
            return
        }

        image.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val imageBytes = baos.toByteArray()
        val imageString = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
        val apiUpload = callApi.uploadFile(
            token,
            imageString
        )

        RequiresApi.callApi(requireContext(),apiUpload){
            idImage = ""
            println("====>${it}")
            if(it==null || it.status!=200){
                return@callApi
            }
            val data = it.data as LinkedTreeMap<*, *>
            idImage = data["attachment_id"].toString().replace(".0","")
        }
    }

    private fun showWithImage(show:Boolean){
        val transition = ChangeBounds()
        TransitionManager.beginDelayedTransition(binding.lShowImage,transition)
        if(show){
            imagePost.visibility = View.VISIBLE
            btnClearImage.visibility = View.VISIBLE
        }else{
            btnClearImage.visibility = View.GONE
            imagePost.visibility = View.GONE
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.setCanceledOnTouchOutside(false)
        super.onCreateDialog(savedInstanceState)
        val myDialog =
            object : Dialog(requireContext(), android.R.style.Theme_DeviceDefault_NoActionBar) {
                override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                    if (currentFocus != null) {
                        val imm =
                            activity!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                    }
                    return super.dispatchTouchEvent(ev)
                }
            }
        myDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation;
        val bottomSheet =
            dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = getScreenHeight()
        return myDialog
    }

    private fun loadData()
    {
        val token=mySharePre.getString("token").toString()
        val home = callApi.feed(token,lastOid)
        RequiresApi.callApi(requireContext(),home){
            if(it===null || it.status!=200){
                return@callApi
            }

            val data = it.data as LinkedTreeMap<*, *>
            val list = data["list"] as ArrayList<*>
            val listData = arrayListOf<PostModel>()
            list.forEach { item->
                item as LinkedTreeMap<*, *>
                val model = PostModel(
                    item["full_name"].toString(),
                    item["image"].toString(),
                    item["content"].toString(),
                    item["post_oid"].toString(),
                    item["time"].toString(),
                    item["liked"].toString().toBoolean(),
                )
                lastOid = item["post_oid"].toString()
                listData.add(model)
                if(isLoadMore){
                    adapter.appendData(model)
                }
            }
            if(listData.size>0 && !isLoadMore){
                adapter.setData(listData)
            }
        }
    }
}
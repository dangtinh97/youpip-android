package org.youpip.app.views.fragment.social

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.internal.LinkedTreeMap
import org.youpip.app.MainActivity
import org.youpip.app.R
import org.youpip.app.adapter.CommentAdapter
import org.youpip.app.adapter.ItemSuggestAdapter
import org.youpip.app.databinding.FragmentCommentBinding
import org.youpip.app.model.CommentModel
import org.youpip.app.network.ApiService
import org.youpip.app.network.RequiresApi
import org.youpip.app.utils.MySharePre
import org.youpip.app.views.fragment.search.VideoSearchFragment
import java.util.ArrayList


class CommentFragment(val postOid:String?, val callApi: ApiService, val mySharePre: MySharePre) : BottomSheetDialogFragment() {
    private lateinit var binding:FragmentCommentBinding
    private lateinit var btnClose:ImageView
    private lateinit var btnComment:ImageView
    private lateinit var inputComment:EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommentAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnClose = binding.close
        recyclerView = binding.recyclerView
        inputComment = binding.ipComment
        btnClose.setOnClickListener {
            dialog?.dismiss()
        }
        btnComment = binding.submit
        customRecyclerView()
        loadComment()
        btnComment.setOnClickListener {
            val content = inputComment.text.trim().toString()
            if(content==""){
                return@setOnClickListener
            }
            val comment = callApi.comment(
                token = mySharePre.getString("token").toString(),
                content = content,
                id = postOid.toString()
            )

            RequiresApi.callApi(requireContext(),comment){
                inputComment.setText("")
                if(it==null){
                    return@callApi
                }
                val data = it.data as LinkedTreeMap<*, *>
                println("====>Post:${data}")
                Toast.makeText(requireContext(),it.content, Toast.LENGTH_SHORT).show()
                loadComment()
            }
        }
    }

    private fun customRecyclerView()
    {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = CommentAdapter{

        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun loadComment()
    {
        val comment = callApi.getComment(
            token = mySharePre.getString("token").toString(),
            lastCommentOid = "",
            id = postOid.toString()
        )

        RequiresApi.callApi(requireContext(),comment){
            if(it==null || it.status!=200){
                return@callApi
            }
            val data = it.data as LinkedTreeMap<*, *>
            val list = data["list"] as ArrayList<*>
            val listData = arrayListOf<CommentModel>()
            list.forEach { item ->
                item as LinkedTreeMap<*, *>
                listData.add(
                    CommentModel(
                        comment_oid = item["action_oid"].toString(),
                        fullName = item["full_name"].toString(),
                        userId = item["user_id"].toString().replace(".0","").toInt(),
                        content = item["content"].toString()
                    )
                )
            }
            adapter.setData(listData)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized){
            binding = FragmentCommentBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.setCanceledOnTouchOutside(false)
        super.onCreateDialog(savedInstanceState)
        val myDialog =
            object : Dialog(requireContext(), android.R.style.Theme_DeviceDefault_NoActionBar) {
                override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                    if (currentFocus != null) {
                        val imm =
                            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

    private fun getScreenHeight(): Int {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
}
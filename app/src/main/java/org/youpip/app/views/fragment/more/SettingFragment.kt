package org.youpip.app.views.fragment.more

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.youpip.app.MainActivity
import org.youpip.app.base.BaseFragment
import org.youpip.app.databinding.FragmentSettingBinding
import org.youpip.app.network.BASE_URL


class SettingFragment : BaseFragment() {
    private lateinit var binding:FragmentSettingBinding
    private lateinit var contact:TextView
    override fun onViewCreateBase(view: View, savedInstanceState: Bundle?) {
        binding.shortUsername.setOnClickListener {
            val clipboard: ClipboardManager? =
                (mActivity as MainActivity).getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText("label", BASE_URL.replace("api/","?short-username=")+mySharePre.getString("short_username").toString())
            clipboard?.setPrimaryClip(clip)
            alert("đã sao chép!")
        }

        contact.setOnClickListener {
            val url ="https://docs.google.com/forms/d/e/1FAIpQLSe1gCQNpwge_eY2PFl7F91_sqh4uHqK41qVepgM2dwioet39Q/viewform"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }

    override fun onInitialized() {
        binding.shortUsername.text = mySharePre.getString("short_username").toString()
        binding.fullname.text = mySharePre.getString("full_name").toString()
        contact = binding.contact

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::binding.isInitialized){
            binding = FragmentSettingBinding.inflate(inflater, container, false)
        }
        return binding.root
    }
}
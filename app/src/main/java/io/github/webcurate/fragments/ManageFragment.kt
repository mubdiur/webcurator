package io.github.webcurate.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import io.github.webcurate.R
import io.github.webcurate.data.AuthManager
import io.github.webcurate.data.NetEvents
import io.github.webcurate.databinding.FragmentManageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ManageFragment : Fragment(R.layout.fragment_manage) {

    private var _binding: FragmentManageBinding? = null

    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        _binding = FragmentManageBinding.bind(view)

        NetEvents.authEvents.observe(requireActivity(), {
            if(it==NetEvents.TOKEN_READY) {
                CoroutineScope(Dispatchers.Main).launch {
                    NetEvents.authEvents.value = NetEvents.DEFAULT
                    binding.displayName.text = AuthManager.authInstance.currentUser?.displayName
                    binding.emailTextView.text = AuthManager.authInstance.currentUser?.email
                }
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        NetEvents.authEvents.removeObservers(requireActivity())
    }

}
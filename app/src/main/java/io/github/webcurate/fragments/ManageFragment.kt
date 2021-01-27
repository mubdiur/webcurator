package io.github.webcurate.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import io.github.webcurate.R
import io.github.webcurate.activities.authentication.LoginActivity
import io.github.webcurate.activities.manage.*
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


        /**
         * OnClicks!
         */

        binding.changeNameOption.setOnClickListener {
            startActivity(
                Intent(requireActivity(), ChangeNameActivity::class.java)
            )
        }
        binding.changePasswordOption.setOnClickListener {
            startActivity(
                Intent(requireActivity(), ChangePasswordActivity::class.java)
            )
        }
        binding.logoutOption.setOnClickListener {
            AuthManager.authInstance.signOut()
            startActivity(
                Intent(requireActivity(), LoginActivity::class.java)
            )
            requireActivity().finish()
        }
        binding.deleteAccountOption.setOnClickListener {
            startActivity(
                Intent(requireActivity(), DeleteAccountActivity::class.java)
            )
        }
        binding.notificationOption.setOnClickListener {
            startActivity(
                Intent(requireActivity(), FeedsNotificationActivity::class.java)
            )
        }
        binding.deleteFeeds.setOnClickListener {
            startActivity(
                Intent(requireActivity(), DeleteFeedsActivity::class.java)
            )
        }
        binding.helpOption.setOnClickListener {
            startActivity(
                Intent(requireActivity(), HelpActivity::class.java)
            )
        }
        binding.aboutOption.setOnClickListener {
            startActivity(
                Intent(requireActivity(), AboutActivity::class.java)
            )
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        NetEvents.authEvents.removeObservers(requireActivity())
    }

}
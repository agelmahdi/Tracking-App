package com.agelmahdi.trackingapp.UI.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.agelmahdi.trackingapp.Others.Constants
import com.agelmahdi.trackingapp.R
import com.agelmahdi.trackingapp.UI.MainActivity
import com.agelmahdi.trackingapp.databinding.ActivityMainBinding
import com.agelmahdi.trackingapp.databinding.FragmentSetupBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment() {

    private var _binding: FragmentSetupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var shardPref: SharedPreferences

    @set:Inject
    var isFirstLunch = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupBinding.inflate(inflater, container, false)
        val view = binding.root

        if (!isFirstLunch) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOptions
            )
        }
        binding.tvContinue.setOnClickListener {
            val success = checkUserShardPrefs()
            if (success) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            } else {
                Snackbar.make(requireView(), "Please enter your details", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
        return view
    }

    private fun checkUserShardPrefs(): Boolean {
        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()
        if (name.isEmpty() || weight.isEmpty()) {
            return false
        }

        shardPref.edit()
            .putString(Constants.PREFERENCE_USER_NAME, name)
            .putFloat(Constants.PREFERENCE_USER_WEIGHT, weight.toFloat())
            .putBoolean(Constants.PREFERENCE_FIRST_TIME_LUNCH, false)
            .apply()

        val toolbarText = "Let's go ${name}"

        (requireActivity() as MainActivity).binding.tvToolbarTitle.text = toolbarText

        return true

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
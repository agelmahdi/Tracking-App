package com.agelmahdi.trackingapp.UI.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.agelmahdi.trackingapp.Others.Constants.PREFERENCE_USER_NAME
import com.agelmahdi.trackingapp.Others.Constants.PREFERENCE_USER_WEIGHT
import com.agelmahdi.trackingapp.UI.MainActivity
import com.agelmahdi.trackingapp.databinding.FragmentSettingsBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    @Inject
    lateinit var shardPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root
        displayUserData()
        binding.btnApplyChanges.setOnClickListener {
            val success = changeUserShardPrefs()
            if (success) {
                Snackbar.make(requireView(), "Changes successfully saved", Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                Snackbar.make(requireView(), "Please enter your details", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        return view
    }

    private fun displayUserData() {
        val name = shardPref.getString(PREFERENCE_USER_NAME, "")
        val weight = shardPref.getFloat(PREFERENCE_USER_WEIGHT, 0f)
        binding.etName.setText(name)
        binding.etWeight.setText(weight.toString())
    }

    private fun changeUserShardPrefs(): Boolean {
        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()
        if (name.isEmpty() || weight.isEmpty()) {
            return false
        }

        shardPref.edit()
            .putString(PREFERENCE_USER_NAME, name)
            .putFloat(PREFERENCE_USER_WEIGHT, weight.toFloat())
            .apply()

        val toolbarText = "Let's go ${name}"

        (requireActivity() as MainActivity).binding.tvToolbarTitle.text = toolbarText

        return true

    }

}
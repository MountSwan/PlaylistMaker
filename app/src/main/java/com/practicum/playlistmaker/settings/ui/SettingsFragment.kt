package com.practicum.playlistmaker.settings.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSwitchThemeState(resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK))
        viewModel.observeSwitchThemeState().observe(viewLifecycleOwner) {
            binding.smThemeSwitcher.isChecked = it
        }

        binding.smThemeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (activity?.applicationContext as App).switchTheme(checked)
            viewModel.editSharedPrefs(checked)
        }

        binding.ivShare.setOnClickListener {
            viewModel.shareApp(getString(R.string.share_message))
        }

        binding.ivSupport.setOnClickListener {
            viewModel.getSupport()
        }

        binding.ivUserAgreement.setOnClickListener {
            viewModel.readUserAgreement()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
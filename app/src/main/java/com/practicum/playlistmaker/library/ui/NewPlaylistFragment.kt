package com.practicum.playlistmaker.library.ui

import android.Manifest.permission.CAMERA
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {

    companion object {
        fun newInstance() = NewPlaylistFragment()
    }

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val requester = PermissionRequester.instance()

    private val viewModel: NewPlaylistViewModel by viewModel()

    private var nameOfNewPlaylist = ""
    private var descriptionOfNewPlaylist = ""
    private var coverIsChosen = false
    private lateinit var coverImageUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeInsertingPlaylistIsComplete().observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(
                    requireActivity(),
                    "Новый плейлист создан",
                    Toast.LENGTH_LONG
                ).show()

                findNavController().navigateUp()
            }
        }

        binding.arrowBackImage.setOnClickListener {
            onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        })

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.cover.setImageURI(uri)
                    coverIsChosen = true
                    coverImageUri = uri
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "No media selected",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        val permissionProvided = ContextCompat.checkSelfPermission(requireActivity(), CAMERA)

        binding.cover.setOnClickListener {
            if (permissionProvided == PackageManager.PERMISSION_GRANTED) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else if (permissionProvided == PackageManager.PERMISSION_DENIED) {
                executePermissionRequester(pickMedia)
            }

        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                textSearch: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // empty
            }

            override fun onTextChanged(
                textSearch: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (binding.playlistName.hasFocus() && textSearch?.isEmpty() == true) {
                    binding.newPlaylistButton.isEnabled = false
                    binding.hintPlaylistName.isVisible = false
                    binding.playlistName.background = ResourcesCompat.getDrawable(
                        resources, R.drawable.rounded_rectangle, null
                    )
                    context?.let { binding.newPlaylistButton.setBackgroundColor(it.getColor(R.color.gray)) }
                    nameOfNewPlaylist = ""
                }
                if (binding.playlistName.hasFocus() && textSearch?.isEmpty() == false) {
                    binding.newPlaylistButton.isEnabled = true
                    binding.hintPlaylistName.isVisible = true
                    binding.playlistName.background = ResourcesCompat.getDrawable(
                        resources, R.drawable.rounded_rectangle_blue, null
                    )
                    context?.let { binding.newPlaylistButton.setBackgroundColor(it.getColor(R.color.blue)) }
                    nameOfNewPlaylist = textSearch.toString()
                }
                if (binding.playlistDescription.hasFocus() && textSearch?.isEmpty() == true) {
                    binding.hintPlaylistDescription.isVisible = false
                    binding.playlistDescription.background = ResourcesCompat.getDrawable(
                        resources, R.drawable.rounded_rectangle, null
                    )
                    descriptionOfNewPlaylist = ""
                }
                if (binding.playlistDescription.hasFocus() && textSearch?.isEmpty() == false) {
                    binding.hintPlaylistDescription.isVisible = true
                    binding.playlistDescription.background = ResourcesCompat.getDrawable(
                        resources, R.drawable.rounded_rectangle_blue, null
                    )
                    descriptionOfNewPlaylist = textSearch.toString()
                }
            }

            override fun afterTextChanged(textSearch: Editable?) {
                // empty
            }
        }
        binding.playlistName.addTextChangedListener(simpleTextWatcher)
        binding.playlistDescription.addTextChangedListener(simpleTextWatcher)

        binding.newPlaylistButton.setOnClickListener {

            if (coverIsChosen) {
                saveImageToPrivateStorage(coverImageUri, "${nameOfNewPlaylist}_cover.jpg")
            }

            viewModel.insertPlaylistInDatabase(nameOfNewPlaylist, descriptionOfNewPlaylist)

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun executePermissionRequester(pickMedia: ActivityResultLauncher<PickVisualMediaRequest>) {
        lifecycleScope.launch {
            requester.request(CAMERA).collect { result ->
                when (result) {
                    is PermissionResult.Granted -> {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }

                    is PermissionResult.Denied.DeniedPermanently -> {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.data = Uri.fromParts("package", context?.packageName, null)
                        context?.startActivity(intent)
                        Toast.makeText(
                            requireContext(),
                            "Выберите в разрешениях (Permissions) доступ к CAMERA",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is PermissionResult.Denied.NeedsRationale -> {
                        Toast.makeText(
                            requireContext(),
                            "Разрешение необходимо, чтобы выбрать обложку для плейлиста",
                            Toast.LENGTH_LONG
                        ).show()

                    }

                    is PermissionResult.Cancelled -> {
                        return@collect
                    }
                }
            }
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri, fileName: String) {
        val filePath =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, fileName)
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    private fun onBackPressed() {
        if (coverIsChosen || nameOfNewPlaylist.isNotEmpty() || descriptionOfNewPlaylist.isNotEmpty()) {
            context?.let { it1 ->
                MaterialAlertDialogBuilder(it1)
                    .setTitle("Завершить создание плейлиста?")
                    .setMessage("Все несохраненные данные будут потеряны")
                    .setNeutralButton("Отмена") { dialog, which ->
                    }
                    .setPositiveButton("Завершить") { dialog, which ->
                        findNavController().navigateUp()
                    }
                    .show()
            }
        } else {
            findNavController().navigateUp()
        }
    }

}
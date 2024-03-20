package com.practicum.playlistmaker.library.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.markodevcic.peko.PermissionRequester
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {

    companion object {
        fun newInstance() = NewPlaylistFragment()
        private const val COVER_IS_CHOSEN_KEY = "key_for_cover_is_chosen"
        private const val PLAYLIST_NAME_KEY = "key_for_playlist_name"
        private const val PLAYLIST_DESCRIPTION_KEY = "key_for_playlist_description"
    }

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val requester = PermissionRequester.instance()

    private val viewModel: NewPlaylistViewModel by viewModel()

    private var coverIsChosen = false
    private lateinit var coverImageUri: Uri

    private lateinit var callbackOnBackPressed: OnBackPressedCallback


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            binding.playlistName.setText(savedInstanceState.getString(PLAYLIST_NAME_KEY, ""))
            binding.playlistDescription.setText(
                savedInstanceState.getString(
                    PLAYLIST_DESCRIPTION_KEY,
                    ""
                )
            )
            if (binding.playlistName.text.isNotEmpty()) {
                binding.newPlaylistButton.isEnabled = true
                context?.let { binding.newPlaylistButton.setBackgroundColor(it.getColor(R.color.blue)) }

            }
            coverIsChosen = savedInstanceState.getBoolean(COVER_IS_CHOSEN_KEY, false)
            if (coverIsChosen) {
                val filePath =
                    File(
                        requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "myalbum"
                    )
                val file = File(filePath, "temporary_cover.jpg")
                coverImageUri = Uri.fromFile(file)
                binding.cover.setImageURI(coverImageUri)
            }
        }

        viewModel.observeInsertingPlaylistIsComplete().observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.new_playlist_is_created),
                    Toast.LENGTH_LONG
                ).show()

                findNavController().navigateUp()
            }
        }

        binding.arrowBackImage.setOnClickListener {
            onBackPressed()
        }

        callbackOnBackPressed =
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                onBackPressed()
            }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.cover.setImageURI(uri)
                    coverIsChosen = true
                    coverImageUri = uri
                    saveImageToPrivateStorage(coverImageUri, "temporary_cover.jpg")
                } else {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.no_media_selected),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        binding.cover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
                }
                if (binding.playlistName.hasFocus() && textSearch?.isEmpty() == false) {
                    binding.newPlaylistButton.isEnabled = true
                    binding.hintPlaylistName.isVisible = true
                    binding.playlistName.background = ResourcesCompat.getDrawable(
                        resources, R.drawable.rounded_rectangle_blue, null
                    )
                    context?.let { binding.newPlaylistButton.setBackgroundColor(it.getColor(R.color.blue)) }
                }
                if (binding.playlistDescription.hasFocus() && textSearch?.isEmpty() == true) {
                    binding.hintPlaylistDescription.isVisible = false
                    binding.playlistDescription.background = ResourcesCompat.getDrawable(
                        resources, R.drawable.rounded_rectangle, null
                    )
                }
                if (binding.playlistDescription.hasFocus() && textSearch?.isEmpty() == false) {
                    binding.hintPlaylistDescription.isVisible = true
                    binding.playlistDescription.background = ResourcesCompat.getDrawable(
                        resources, R.drawable.rounded_rectangle_blue, null
                    )
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
                saveImageToPrivateStorage(coverImageUri, "${binding.playlistName.text}_cover.jpg")
                Log.e("AAA", "${binding.playlistName.text}_cover.jpg")
            }
            viewModel.insertPlaylistInDatabase(
                binding.playlistName.text.toString(),
                binding.playlistDescription.text.toString()
            )
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PLAYLIST_NAME_KEY, binding.playlistName.text.toString())
        outState.putString(PLAYLIST_DESCRIPTION_KEY, binding.playlistDescription.text.toString())
        outState.putBoolean(COVER_IS_CHOSEN_KEY, coverIsChosen)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        callbackOnBackPressed.remove()
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
        if (coverIsChosen || binding.playlistName.text.isNotEmpty() || binding.playlistDescription.text.isNotEmpty()) {
            context?.let { it1 ->
                MaterialAlertDialogBuilder(it1)
                    .setTitle(getString(R.string.finish_creation))
                    .setMessage(getString(R.string.unsaved_data_will_be_lost))
                    .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                    }
                    .setPositiveButton(getString(R.string.finish)) { dialog, which ->
                        findNavController().navigateUp()
                    }
                    .show()
            }
        } else {
            findNavController().navigateUp()
        }
    }

}
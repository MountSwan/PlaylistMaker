package com.practicum.playlistmaker.library.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
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
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.library.domain.models.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.FileOutputStream

open class NewPlaylistFragment : Fragment() {

    companion object {
        fun newInstance() = NewPlaylistFragment()
        private const val COVER_IS_CHOSEN_KEY = "key_for_cover_is_chosen"
        private const val PLAYLIST_NAME_KEY = "key_for_playlist_name"
        private const val PLAYLIST_DESCRIPTION_KEY = "key_for_playlist_description"

        const val ARGS_PLAYLIST_ID = "playlistId"
        fun createArgs(playlistId: Int): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }

    private var _binding: FragmentNewPlaylistBinding? = null
    val binding get() = _binding!!

    private val viewModel: NewPlaylistViewModel by viewModel() {
        parametersOf(getPlaylistId())
    }

    private var coverIsChosen = false
    private lateinit var coverImageUri: Uri
    private var playlistId: Int = 0
    private var playlistName = ""

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
                val file = if (playlistId == 0) {
                    File(filePath, "temporary_cover.jpg")
                } else {
                    File(filePath, "${playlistName}_cover.jpg")
                }
                coverImageUri = Uri.fromFile(file)
                binding.cover.setImageURI(coverImageUri)
            }
        }

        viewModel.observePlaylistInfo().observe(viewLifecycleOwner) {
            drawScreen(it)
        }

        viewModel.observeInsertingPlaylistIsComplete().observe(viewLifecycleOwner) {
            if (it) {
                if (playlistId == 0) {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.new_playlist_is_created),
                        Toast.LENGTH_LONG
                    ).show()
                }
                findNavController().navigateUp()
            }
        }

        viewModel.getPlaylistInfoFromDatabase()

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
                    binding.cover.background = null
                    coverIsChosen = true
                    coverImageUri = uri
                    if (playlistId == 0) {
                        saveImageToPrivateStorage(coverImageUri, "temporary_cover.jpg")
                    } else {
                        saveImageToPrivateStorage(coverImageUri, "${playlistName}_cover.jpg")

                    }

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
                setEditTextState(textSearch.toString())
            }

            override fun afterTextChanged(textSearch: Editable?) {
                // empty
            }
        }
        binding.playlistName.addTextChangedListener(simpleTextWatcher)
        binding.playlistDescription.addTextChangedListener(simpleTextWatcher)

        binding.newPlaylistButton.setOnClickListener {
            if (coverIsChosen && playlistId == 0) {
                saveImageToPrivateStorage(coverImageUri, "${binding.playlistName.text}_cover.jpg")
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
        if (playlistId == 0) {
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
        } else {
            findNavController().navigateUp()
        }

    }

    private fun getPlaylistId(): Int {
        return requireArguments().getInt(ARGS_PLAYLIST_ID)
    }

    private fun drawScreen(playlist: Playlist) {
        getCoverImage(playlist.playlistName)
        if (coverIsChosen) {
            binding.cover.setImageURI(coverImageUri)
            binding.cover.background = null
        }
        binding.playlistName.setText(playlist.playlistName)
        binding.hintPlaylistName.isVisible = true
        binding.playlistName.background = ResourcesCompat.getDrawable(
            resources, R.drawable.rounded_rectangle_blue, null
        )
        binding.playlistDescription.setText(playlist.playlistDescription)
        if (playlist.playlistDescription.isNotEmpty()) {
            binding.hintPlaylistDescription.isVisible = true
            binding.playlistDescription.background = ResourcesCompat.getDrawable(
                resources, R.drawable.rounded_rectangle_blue, null
            )
        }
        binding.fragmentName.text = getString(R.string.edit)
        binding.newPlaylistButton.text = getString(R.string.save)
        binding.newPlaylistButton.isEnabled = true
        context?.let { binding.newPlaylistButton.setBackgroundColor(it.getColor(R.color.blue)) }
        playlistId = playlist.playlistId
        playlistName = playlist.playlistName
    }

    private fun getCoverImage(playlistName: String): File {
        val filePath =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        val file = File(filePath, "${playlistName}_cover.jpg")
        if (file.exists()) {
            coverIsChosen = true
            coverImageUri = Uri.fromFile(file)
        }
        return file
    }

    private fun setEditTextState(text: String) {
        if (binding.playlistName.hasFocus() && text.isEmpty()) {
            binding.newPlaylistButton.isEnabled = false
            binding.hintPlaylistName.isVisible = false
            binding.playlistName.background = ResourcesCompat.getDrawable(
                resources, R.drawable.rounded_rectangle, null
            )
            context?.let { binding.newPlaylistButton.setBackgroundColor(it.getColor(R.color.gray)) }
        }
        if (binding.playlistName.hasFocus() && text.isNotEmpty()) {
            binding.newPlaylistButton.isEnabled = true
            binding.hintPlaylistName.isVisible = true
            binding.playlistName.background = ResourcesCompat.getDrawable(
                resources, R.drawable.rounded_rectangle_blue, null
            )
            context?.let { binding.newPlaylistButton.setBackgroundColor(it.getColor(R.color.blue)) }
        }
        if (binding.playlistDescription.hasFocus() && text.isEmpty()) {
            binding.hintPlaylistDescription.isVisible = false
            binding.playlistDescription.background = ResourcesCompat.getDrawable(
                resources, R.drawable.rounded_rectangle, null
            )
        }
        if (binding.playlistDescription.hasFocus() && text.isNotEmpty()) {
            binding.hintPlaylistDescription.isVisible = true
            binding.playlistDescription.background = ResourcesCompat.getDrawable(
                resources, R.drawable.rounded_rectangle_blue, null
            )
        }
    }

}
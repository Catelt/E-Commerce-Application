package com.goldenowl.ecommerceapp.ui.setting

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goldenowl.ecommerceapp.R
import com.goldenowl.ecommerceapp.core.BaseFragment
import com.goldenowl.ecommerceapp.databinding.FragmentSettingBinding
import com.goldenowl.ecommerceapp.ui.changepassword.BottomSheetChangePassword
import com.goldenowl.ecommerceapp.ui.general.DatePickerFragment
import com.goldenowl.ecommerceapp.ui.general.DialogChooseImage
import com.goldenowl.ecommerceapp.ui.general.Permission
import com.goldenowl.ecommerceapp.ui.reviewrating.BottomAddReview
import com.goldenowl.ecommerceapp.ui.setting.SettingViewModel.Companion.FACEBOOK
import com.goldenowl.ecommerceapp.ui.setting.SettingViewModel.Companion.GOOGLE
import com.goldenowl.ecommerceapp.utilities.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>(
    FragmentSettingBinding::inflate
) {
    override val viewModel: SettingViewModel by viewModels()

    private lateinit var requestStore: Permission
    private lateinit var requestCamera: Permission

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestStore = Permission(this, {
            val pickPhoto = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(pickPhoto, REQUEST_PICK_IMAGE);
        }, {})

        requestCamera = Permission(this, {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, REQUEST_CAMERA)
        }, {})
    }

    override fun setUpObserve() {
        viewModel.apply {
            validNameLiveData.observe(viewLifecycleOwner) {
                alertName(it)
            }
            flat.observe(viewLifecycleOwner){
                if(it == 2){
                    flat.postValue(0)
                    toastMessage(SUCCESS)
                    binding.editTextFullName.clearFocus()
                }
            }
        }
    }

    override fun setUpViews() {
        binding.apply {
            appBarLayout.topAppBar.title = getString(R.string.settings)
            editTextFullName.setText(viewModel.userManager.getName())
            editTextDateOfBirth.setText(viewModel.userManager.getDOB())

            GlideDefault.userImage(
                requireContext(),
                viewModel.userManager.getAvatar(),
                imgAvatar
            )
            imgAvatar.setOnClickListener {
                touchImage(listOf(viewModel.userManager.getAvatar()))
            }


            if(viewModel.checkLoginWithFacebookOrGoogle() == FACEBOOK){
                layoutPassword.visibility = View.GONE
                txtLoginWith.visibility = View.VISIBLE
                txtLoginWith.text = getString(R.string.login_with_facebook)
            }
            else if(viewModel.checkLoginWithFacebookOrGoogle() == GOOGLE){
                layoutPassword.visibility = View.GONE
                txtLoginWith.visibility = View.VISIBLE
                txtLoginWith.text = getString(R.string.login_with_google)
            }
            else{
                layoutPassword.visibility = View.VISIBLE
                txtLoginWith.visibility = View.GONE
            }

            btnSave.setOnClickListener {
                viewModel.apply {
                    flat.postValue(0)
                    updateName(editTextFullName.text.toString()).observe(viewLifecycleOwner) {
                        if (it == 1) {
                            userManager.setName(editTextFullName.text.toString())
                            flat.postValue(flat.value?.plus(1))
                        }
                        if (it == -1) {
                            toastMessage(WARNING_SOMETHING_WRONG)
                        }
                    }
                    updateDOB(editTextDateOfBirth.text.toString()).observe(viewLifecycleOwner) {
                        if (it == 1) {
                            userManager.setDOB(editTextDateOfBirth.text.toString())
                            flat.postValue(flat.value?.plus(1))
                        }
                        if (it == -1) {
                            toastMessage(WARNING_SOMETHING_WRONG)
                        }
                    }
                }
            }

            editTextDateOfBirth.setOnClickListener {
                val newFragment =
                    DatePickerFragment(editTextDateOfBirth, viewModel.userManager.getDOB())
                newFragment.show(parentFragmentManager, "datePicker")
            }

            txtChangeAvatar.setOnClickListener {
                DialogChooseImage(this@SettingFragment, requestCamera, requestStore).show()
            }

            txtChange.setOnClickListener {
                val modalBottomSheet = BottomSheetChangePassword()
                modalBottomSheet.show(parentFragmentManager, BottomSheetChangePassword.TAG)
            }

            binding.appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_PICK_IMAGE -> {
                    selectCompressor(data)
                }
                REQUEST_CAMERA -> {
                    selectCompressor(data, true)
                }
            }
        }
    }

    private fun selectCompressor(data: Intent?, isCamera: Boolean = false) {
        if (data == null) {
            return
        }
        if (isCamera) {
            data.extras?.let { it ->
                FileUtil.getImageUri(
                    requireContext(),
                    it.get(BottomAddReview.DATA) as Bitmap
                )?.let {
                    loadImage(it)
                }
            }
        } else {
            val filePath = data.data
            filePath?.let {
                loadImage(it)
            }
        }
    }

    private fun loadImage(filePath: Uri) {
        binding.imgAvatar.setImageURI(filePath)
        viewModel.uploadImage(filePath, viewModel.userManager.getAccessToken())
        GlideDefault.userImage(
            requireContext(),
            filePath.toString(),
            binding.imgAvatar
        )
    }

    private fun alertName(alert: String) {
        alertEditText(alert, binding.txtLayoutFullName)
    }

    companion object{
        const val SUCCESS = "Update information success"
    }
}
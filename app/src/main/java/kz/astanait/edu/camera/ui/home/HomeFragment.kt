package kz.astanait.edu.camera.ui.home

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kz.astanait.edu.camera.databinding.FragmentHomeBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    lateinit var imageView: ImageView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        imageView = binding.iv

        val textView: TextView = binding.tvShowGallery

        textView.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 42)
        }

        binding.tvDeletePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED
            ) {

            }

            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 43)
        }

        return root
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)
        if (reqCode == 43) {
            if (resultCode == RESULT_OK) {
                try {
                    data?.data
//                    val path = Environment.getExternalStorageDirectory() data?.data.toString();
//                    val imageUri: Uri? = data?.data
                    val fdelete = getPath(data?.data)?.let { File(it) }
                    if (fdelete!!.exists()) {
                        if (fdelete.delete()) {
//                            System.out.println("file Deleted :$imageUri")
                        } else {
//                            System.out.println("file not Deleted :$imageUri")
                        }
                    } else {
                        Log.d("LOG", "File not exist")
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            } else {
            }
        }
        if (reqCode == 42) {
            if (resultCode == RESULT_OK) {
                try {
                    val imageUri: Uri? = data?.data
                    val imageStream: InputStream =
                        requireContext().contentResolver!!.openInputStream(imageUri!!)!!
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    imageView.setImageBitmap(selectedImage)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            } else {
            }
        }
    }

    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor =
            context?.contentResolver?.query(uri!!, projection, null, null, null) ?: return null
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s: String = cursor.getString(column_index)
        cursor.close()
        return s
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
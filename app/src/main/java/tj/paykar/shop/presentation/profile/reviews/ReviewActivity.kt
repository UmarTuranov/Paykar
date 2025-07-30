package tj.paykar.shop.presentation.profile.reviews

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toFile
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.model.PaykarReviewModel
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.databinding.ActivityReviewBinding
import tj.paykar.shop.domain.usecase.MainManagerService
import tj.paykar.shop.domain.usecase.ReviewFileManagerService
import tj.paykar.shop.domain.usecase.ReviewManagerService
import tj.paykar.shop.domain.usecase.URIPathHelper
import java.io.File
import java.io.FileOutputStream


class ReviewActivity : AppCompatActivity() {
    lateinit var binding: ActivityReviewBinding
    private var filePath: String? = null
    var file: File? = null
    private var fileName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = navigationBarInsets.left
                rightMargin = navigationBarInsets.right
                bottomMargin = navigationBarInsets.bottom
            }
            insets
        }
        setupView()
        checkMyPermissions()
    }

    private fun checkMyPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 101)
            return
        }
    }

    private fun setupView() {
        binding.apply {
            val titleItems = listOf("О мобильном приложении","О работе магазина", "О качестве продуктов", "О доставке товаров", "Другая тема")
            val shopItems = listOf("ул. Айни 16б, ТРК Баракат Плаза", "ул. Бухоро 27, ориентир Таможенный комитет", "ул. Яккачинор 148, ориентир Автовакзал", "ул. Айни 57, ориентир Городская клиническая больница скорой медицинской помощи", "пр. Рудаки 6б, ориентир ЦУМ", "ул. Бободжон Гафуров 10б")
            val titleAdapter = ArrayAdapter(this@ReviewActivity, R.layout.list_item, titleItems)
            val shopAdapter = ArrayAdapter(this@ReviewActivity, R.layout.list_item, shopItems)
            (reviewTitleLayout.editText as? AutoCompleteTextView)?.setAdapter(titleAdapter)
            (shopLayout.editText as? AutoCompleteTextView)?.setAdapter(shopAdapter)

            val user = UserStorageData(this@ReviewActivity).getUser()
            phone.text = user.phone?.toEditable()

            openFileExplorer.setOnClickListener {
                val intent = Intent()
                    .setType("image/*")
                    .setAction(Intent.ACTION_GET_CONTENT)

                startActivityForResult(Intent.createChooser(intent, "Select a file"), 777)
            }

            sendReview.setOnClickListener {
                val isOnline = MainManagerService().internetConnection(this@ReviewActivity)
                if (!isOnline) {
                    MaterialAlertDialogBuilder(this@ReviewActivity)
                        .setTitle("Нет интернета!")
                        .setMessage("Отсутствует подключение к интернету, включите интернет и повторите попытку")
                        .setPositiveButton("Продолжить"){_,_ ->}
                        .show()
                }else{
                    sendingProcess.isVisible = true
                    var compressedImageFile: File
                    val review = PaykarReviewModel(name.text.toString(), phone.text.toString(), cardNumber.text.toString(), shop.text.toString(), reviewTitle.text.toString(), reviewText.text.toString(), "")

                    try {
                        if (filePath != null && filePath != "null"){
                            val uri = Uri.parse(filePath)
                            Log.d("Your Uri is Ready", uri.toString())
                            file = uri.toFile()
                        }
                    }catch (e: Exception){
                        Log.d("Your File Error", e.toString())
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            Log.d("Your File", file.toString())
                            if (file != null){
                                Log.d("Your File", file.toString())
                                compressedImageFile = Compressor.compress(this@ReviewActivity, file!!) {
                                    quality(10)
                                    format(Bitmap.CompressFormat.JPEG)
                                    size(200_152)
                                }
                                val sendingFile = ReviewFileManagerService().uploadFile(compressedImageFile)
                                fileName = sendingFile.body()?.fileName
                                Log.d("Your Sending File", sendingFile.body().toString())
                            }
                            review.file_name = fileName
                            ReviewManagerService().sendReview(review)
                            withContext(Dispatchers.Main){
                                sendingProcess.isVisible = false
                                MaterialAlertDialogBuilder(this@ReviewActivity)
                                    .setTitle("Спасибо за отзыв!")
                                    .setPositiveButton("Продолжить"){_,_ ->
                                        startActivity(Intent(this@ReviewActivity, MainActivity::class.java))
                                    }
                                    .show()
                            }
                        }catch (e: Exception){
                            Log.d("Your Review Error", e.toString())
                            withContext(Dispatchers.Main){
                                sendingProcess.isVisible = false
                            }
                        }
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 777) {
            filePath = data?.data.toString()
            Log.d("Your file path", filePath.toString())
            if (filePath != null && filePath != "null"){
                binding.openFileExplorer.text = "Файл закреплён"
                val uriPath = URIPathHelper().getPath(this, Uri.parse(filePath))
                filePath = "file://$uriPath"
                Log.d("Your URI Path", filePath.toString())
            }else{
                binding.openFileExplorer.text = "Прикрепить файл"
                filePath = null
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null

        val tmpFile = File.createTempFile("jpg", null, context.cacheDir)
        tmpFile.deleteOnExit()

        val outputStream = FileOutputStream(tmpFile)

        try {
            inputStream.copyTo(outputStream)
        } finally {
            inputStream.close()
            outputStream.close()
        }

        return tmpFile
    }
    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

}

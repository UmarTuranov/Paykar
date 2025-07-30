package tj.paykar.shop.presentation.wallet

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import tj.paykar.shop.BuildConfig
import tj.paykar.shop.R
import tj.paykar.shop.data.model.wallet.IdentificationPerson
import tj.paykar.shop.data.model.wallet.RequestDeviceInfoModel
import tj.paykar.shop.data.model.wallet.RequestInfoModel
import tj.paykar.shop.data.storage.wallet.IpAddressStorage
import tj.paykar.shop.data.storage.wallet.UserStorage
import tj.paykar.shop.databinding.WalletActivityIdentificationBinding
import tj.paykar.shop.databinding.WalletBottomSheetIdentificationExampleBinding
import tj.paykar.shop.domain.usecase.wallet.CustomProgressDialog
import tj.paykar.shop.domain.usecase.wallet.DeviceInfo
import tj.paykar.shop.domain.usecase.wallet.HideViewWithAnimation
import tj.paykar.shop.domain.usecase.wallet.IdentificationManagerService
import tj.paykar.shop.domain.usecase.wallet.MainManagerService
import tj.paykar.shop.domain.usecase.wallet.functions.requestResultCodeAlert
import tj.paykar.shop.domain.usecase.wallet.functions.showViewWithAnimation
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class IdentificationActivity : AppCompatActivity() {
    private lateinit var binding: WalletActivityIdentificationBinding
    private var passport1: String? = null
    private var passport2: String? = null
    private var selfie: String? = null
    private var currentPhotoPath: String? = null
    private var currentRequestCode: Int = 0
    private var progressDialog: CustomProgressDialog? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    // ActivityResultLauncher для камеры
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && currentPhotoPath != null) {
            handleImageResult(currentRequestCode)
        } else {
            showErrorDialog("Не удалось захватить изображение")
        }
    }

    // ActivityResultLauncher для настроек
    private val settingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        openCamera(currentRequestCode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = WalletActivityIdentificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWindowInsets()
        setupView()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = navigationBarInsets.left
                rightMargin = navigationBarInsets.right
                bottomMargin = navigationBarInsets.bottom
            }
            insets
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupView() = with(binding) {
        frontIdCardCard.setOnClickListener {
            currentRequestCode = 1001
            openCamera(currentRequestCode)
        }

        backIdCardCard.setOnClickListener {
            currentRequestCode = 1002
            openCamera(currentRequestCode)
        }

        selfiAndFrontIdCardCard.setOnClickListener {
            currentRequestCode = 1003
            openCamera(currentRequestCode)
        }

        exampleButton.setOnClickListener {
            showExampleBottomSheet()
        }

        sendIdentificationBtn.setOnClickListener {
            when {
                passport1.isNullOrEmpty() -> showErrorDialog("Сфотографируйте лицевую сторону паспорта для идентификации")
                passport2.isNullOrEmpty() -> showErrorDialog("Сфотографируйте обратную сторону паспорта для идентификации")
                selfie.isNullOrEmpty() -> showErrorDialog("Сфотографируйте себя с лицевой стороной паспорта для идентификации")
                else -> sendIdentificationRequest()
            }
        }
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showExampleBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(this).apply {
            val bottomSheetBinding = WalletBottomSheetIdentificationExampleBinding.inflate(LayoutInflater.from(this@IdentificationActivity))
            setContentView(bottomSheetBinding.root)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
            bottomSheetBinding.apply {
                cancelBtn.setOnClickListener { dismiss() }
                moreDetailedButton.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://paykar.tj/bitrix/wallet_identification_example.pdf")))
                }
            }
            show()
        }
    }

    private fun showDatePicker(inputLayout: TextInputLayout, editText: TextInputEditText, callback: (String) -> Unit) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        editText.setOnClickListener {
            DatePickerDialog(this@IdentificationActivity, { _, myear, mmonth, mday ->
                val dayWith0 = if (mday < 10) "0$mday" else "$mday"
                val monthWith0 = if (mmonth < 9) "0${mmonth + 1}" else "${mmonth + 1}"
                val date = "$myear-$monthWith0-$dayWith0"
                editText.setText(MainManagerService().toEditable("$dayWith0.$monthWith0.$myear"))
                callback(date)
            }, year, month, day).show()
        }
    }

    private fun enableError(view: TextInputLayout) {
        view.error = "Обязательное поле"
        view.isErrorEnabled = true
    }

    private fun sendIdentificationRequest() = with(binding) {
        val ipAddress = IpAddressStorage(this@IdentificationActivity).ipAddress ?: ""
        val imei = DeviceInfo().getDeviceIMEI(this@IdentificationActivity)
        val customerId = UserStorage(this@IdentificationActivity).customerId ?: 0
        val token = UserStorage(this@IdentificationActivity).token

        if (customerId == 0 || token.isNullOrEmpty()) {
            showErrorDialog("Отсутствуют данные пользователя. Пожалуйста, войдите в систему.")
            return@with
        }

        if (!MainManagerService().internetConnection(this@IdentificationActivity)) {
            MainManagerService().noInternetAlert(this@IdentificationActivity)
            return@with
        }

        progressDialog = CustomProgressDialog(this@IdentificationActivity).apply { show() }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val deviceInfo = RequestDeviceInfoModel(BuildConfig.VERSION_NAME, token, imei)
                val requestInfo = RequestInfoModel("TJK", ipAddress, 3)
                val identificationPersonData = IdentificationPerson(passport1, passport2, selfie)
                val request = withTimeout(30000) {
                    IdentificationManagerService(this@IdentificationActivity).identify(
                        customerId, identificationPersonData, deviceInfo, requestInfo
                    )
                }
                withContext(Dispatchers.Main) {
                    progressDialog?.dismiss()
                    val response = request.body()
                    if (response?.ResultCode == 0) {
                        MaterialAlertDialogBuilder(this@IdentificationActivity)
                            .setTitle("Идентификация пользователя")
                            .setMessage("Ваша заявка принята на рассмотрение")
                            .setCancelable(false)
                            .setPositiveButton("Понятно") { _, _ ->
                                startActivity(Intent(this@IdentificationActivity, HomeActivity::class.java))
                                finish()
                            }
                            .show()
                    } else {
                        requestResultCodeAlert(response?.ResultCode ?: 0, this@IdentificationActivity, response?.ResultDesc ?: "")
                    }
                }
            } catch (e: TimeoutCancellationException) {
                withContext(Dispatchers.Main) {
                    progressDialog?.dismiss()
                    showErrorDialog("Превышено время ожидания ответа сервера")
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    progressDialog?.dismiss()
                    showErrorDialog("Ошибка сети. Попробуйте ещё раз!")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog?.dismiss()
                    showErrorDialog("Произошла ошибка: ${e.message}")
                    if (BuildConfig.DEBUG) {
                        Log.e("IdentificationActivity", "Error: ${e.message}", e)
                    }
                }
            }
        }
    }

    private fun handleImageResult(requestCode: Int) = with(binding) {
        val file = File(currentPhotoPath ?: return@with )
        val capturedUri = Uri.fromFile(file)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bitmap = Glide.with(this@IdentificationActivity)
                    .asBitmap()
                    .load(capturedUri)
                    .submit(800, 600) // Уменьшение размера изображения
                    .get() // Выполняется на фоновом потоке
                val base64String = convertBitmapToBase64(bitmap)
                withContext(Dispatchers.Main) {
                    when (requestCode) {
                        1001 -> {
                            HideViewWithAnimation().goneViewWithAnimation(iconPassport1)
                            showViewWithAnimation(imagePassport1)
                            passport1 = base64String
                            Glide.with(this@IdentificationActivity)
                                .load(capturedUri)
                                .centerCrop()
                                .into(imagePassport1)
                        }
                        1002 -> {
                            HideViewWithAnimation().goneViewWithAnimation(iconPassport2)
                            showViewWithAnimation(imagePassport2)
                            passport2 = base64String
                            Glide.with(this@IdentificationActivity)
                                .load(capturedUri)
                                .centerCrop()
                                .into(imagePassport2)
                        }
                        1003 -> {
                            HideViewWithAnimation().goneViewWithAnimation(iconPassport3)
                            showViewWithAnimation(imagePassport3)
                            selfie = base64String
                            Glide.with(this@IdentificationActivity)
                                .load(capturedUri)
                                .centerCrop()
                                .into(imagePassport3)
                        }

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showErrorDialog("Ошибка загрузки изображения")
                    if (BuildConfig.DEBUG) {
                        Log.e("IdentificationActivity", "Image load error: ${e.message}", e)
                    }
                }
            }
        }
    }

    private fun openCamera(requestCode: Int) {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (cameraPermission == PackageManager.PERMISSION_DENIED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                showPermissionExplanation("Доступ к камере необходим для съемки фотографий") {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            } else {
                if (!hasUserDeniedForever(Manifest.permission.CAMERA)) {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                } else {
                    redirectToAppSettings("Включите доступ к камере в настройках")
                }
            }
        } else {
            dispatchTakePictureIntent(requestCode)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            dispatchTakePictureIntent(currentRequestCode)
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                redirectToAppSettings("Включите доступ к камере в настройках")
            } else {
                showErrorDialog("Доступ к камере отклонен")
            }
        }
    }

    private fun hasUserDeniedForever(permission: String): Boolean {
        return !shouldShowRequestPermissionRationale(permission) &&
                ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED
    }

    private fun redirectToAppSettings(message: String) {
        MaterialAlertDialogBuilder(this)
            .setMessage(message)
            .setPositiveButton("Открыть настройки") { _, _ ->
                settingsLauncher.launch(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                })
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showPermissionExplanation(message: String, onProceed: () -> Unit) {
        MaterialAlertDialogBuilder(this)
            .setMessage(message)
            .setPositiveButton("Разрешить") { _, _ -> onProceed() }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent(requestCode: Int) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    showErrorDialog("Ошибка создания файла изображения")
                    if (BuildConfig.DEBUG) {
                        Log.e("IdentificationActivity", "File creation error: ${ex.message}", ex)
                    }
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "tj.paykar.shop.provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    takePictureLauncher.launch(photoURI)
                }
            } ?: showErrorDialog("Не найдено приложение камеры")
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

//    private fun convertBitmapToBase64(bitmap: Bitmap): String {
//        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 800, 600, true)
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream)
//        val byteArray = byteArrayOutputStream.toByteArray()
//        return Base64.encodeToString(byteArray, Base64.DEFAULT)
//    }

    private fun convertBitmapToBase64(bitmap: Bitmap, quality: Int = 100): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    override fun onPause() {
        super.onPause()
        progressDialog?.dismiss()
        bottomSheetDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog = null
        bottomSheetDialog = null
    }
}
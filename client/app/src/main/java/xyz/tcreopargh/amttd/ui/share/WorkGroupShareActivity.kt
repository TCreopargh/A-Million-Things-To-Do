package xyz.tcreopargh.amttd.ui.share

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.google.gson.reflect.TypeToken
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.BaseActivity
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.bean.request.ShareWorkGroupRequest
import xyz.tcreopargh.amttd.common.bean.response.ShareWorkGroupResponse
import xyz.tcreopargh.amttd.common.exception.AmttdException
import xyz.tcreopargh.amttd.util.gson
import xyz.tcreopargh.amttd.util.okHttpRequest
import xyz.tcreopargh.amttd.util.toJsonRequest
import java.util.*


class WorkGroupShareActivity : BaseActivity() {

    private lateinit var viewModel: WorkGroupShareViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_group_share)
        viewModel = ViewModelProvider(this).get(WorkGroupShareViewModel::class.java)

        val invitationCodeText = findViewById<TextView>(R.id.textInvitationCode)
        val qrCodeImage = findViewById<ImageView>(R.id.invitationQrCode)
        val copyToClipboard = findViewById<ImageButton>(R.id.copyButton)
        val confirmButton = findViewById<Button>(R.id.confirmButton)

        confirmButton.setOnClickListener {
            finish()
        }

        copyToClipboard.setOnClickListener {
            val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(
                getString(R.string.amttd_invitation_code),
                invitationCodeText.text
            )
            Toast.makeText(
                this@WorkGroupShareActivity,
                getString(R.string.copied_to_clipboard),
                Toast.LENGTH_SHORT
            ).show()
            clipboard.setPrimaryClip(clip)
        }

        viewModel.exception.observe(this) {
            it?.run {
                Toast.makeText(
                    this@WorkGroupShareActivity,
                    getString(R.string.error_occurred) + it.getLocalizedString(this@WorkGroupShareActivity),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.exception.value = null
            }
        }

        viewModel.invitationCode.observe(this) {
            if (it == null) {
                return@observe
            }
            invitationCodeText.text = it
            try {
                qrCodeImage.setImageBitmap(encodeAsBitmap(it, 512, 512))
            } catch (e: Exception) {
                Log.e(AMTTD.logTag, e.stackTraceToString())
                viewModel.exception.postValue(AmttdException.getFromException(e))
            }
        }

        try {
            val groupId = UUID.fromString(intent.getStringExtra("groupId"))
            val userId = UUID.fromString(intent.getStringExtra("userId"))
            val expirationTimeInDays = intent.getIntExtra("expirationTimeInDays", 1)
            Thread {
                try {
                    val request = okHttpRequest("/workgroups/share")
                        .post(
                            ShareWorkGroupRequest(
                                userId = userId,
                                groupId = groupId,
                                expirationTimeInDays = expirationTimeInDays
                            ).toJsonRequest()
                        )
                        .build()
                    val response = AMTTD.okHttpClient.newCall(request).execute()
                    val body = response.body?.string()
                    // Don't simplify this
                    val result: ShareWorkGroupResponse =
                        gson.fromJson(body, object : TypeToken<ShareWorkGroupResponse>() {}.type)
                    if (result.success != true) {
                        throw AmttdException.getFromErrorCode(result.error)
                    }
                    viewModel.invitationCode.postValue(result.invitationCode)
                } catch (e: Exception) {
                    Log.e(AMTTD.logTag, e.stackTraceToString())
                    viewModel.exception.postValue(AmttdException.getFromException(e))
                }
            }.start()
        } catch (e: Exception) {
            viewModel.exception.value = AmttdException.getFromException(e)
        }
    }


    @Throws(WriterException::class)
    fun encodeAsBitmap(source: String, width: Int, height: Int): Bitmap? {

        val result: BitMatrix = try {
            MultiFormatWriter().encode(source, BarcodeFormat.QR_CODE, width, height, null)
        } catch (e: Exception) {
            return null
        }

        val w = result.width
        val h = result.height
        val pixels = IntArray(w * h)

        for (y in 0 until h) {
            val offset = y * w
            for (x in 0 until w) {
                pixels[offset + x] = if (result[x, y]) Color.BLACK else Color.WHITE
            }
        }

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, w, h)

        return bitmap
    }
}
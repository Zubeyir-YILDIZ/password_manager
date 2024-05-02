import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.zbyr.mind.MainActivity
import com.zbyr.mind.SqLiteIslemleri
import java.util.concurrent.Executor

class Biyometrik(private val context: Context) {

    private var executor: Executor = ContextCompat.getMainExecutor(context)
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    init {
        biometricPrompt = BiometricPrompt(context as AppCompatActivity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    onAuthSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Giriş Yap")
            .setSubtitle("Biyometrik doğrulama kullanarak giriş yapın")
            .setNegativeButtonText("İptal")
            .build()
    }

    fun authenticate() {
         biometricPrompt.authenticate(promptInfo)
    }

    private fun onAuthSuccess() {
        MainActivity.biyo=true
    }
}

package com.zbyr.mind

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.widget.EditText
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.zbyr.mind.databinding.ActivityMainBinding
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    companion object{
        var AktifKullanici:Kullanici?=null
    }

    private lateinit var baglan:ActivityMainBinding
    private lateinit var sqLiteIslemleri: SqLiteIslemleri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baglan= ActivityMainBinding.inflate(layoutInflater)
        setContentView(baglan.root)
        sqLiteIslemleri= SqLiteIslemleri(this)
        if(giris()!=null)
        {
            AktifKullanici=giris()
            baglan.editTextEposta.setText(AktifKullanici!!._kMail)
            baglan.checkBoxBeniHatirla.isChecked=true
            biyometrik()
        }

        baglan.buttonGiris.setOnClickListener {
            var txtMail=baglan.editTextEposta
            var txtSifre=baglan.editTextSifre
            var liste=sqLiteIslemleri.getirKullanici()
            var kisi=dogrulama(liste,txtMail.text.toString(),txtSifre.text.toString())
            if(kisi!=null)
            {
                if(baglan.checkBoxBeniHatirla.isChecked)
                {
                    sqLiteIslemleri.kapatKullanici()
                    sqLiteIslemleri.acKullanici(kisi)
                }else
                {
                    sqLiteIslemleri.kapatKullanici()
                    baglan.editTextEposta.text.clear()
                }
                AktifKullanici=kisi
                val intent = Intent(this, AnasayfaActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this,"Eposta veya şifreniz hatalı",Toast.LENGTH_LONG).show()
            }
        }
        baglan.textViewUyeOl.setOnClickListener {
            val intent = Intent(this, UyeOlActivity::class.java)
            startActivity(intent)
        }
        baglan.textViewUnuttum.setOnClickListener {
            val inputDialog = AlertDialog.Builder(this)
            inputDialog.setTitle("Eposta adresinizi giriniz")
            val inputEditText = EditText(this)
            inputDialog.setView(inputEditText)
            inputDialog.setPositiveButton("Tamam") {dialog,which ->
                val sonuc = inputEditText.text.toString()
                if(sonuc.isNotEmpty() && sonuc==sqLiteIslemleri.getirKullaniciIleMail(sonuc)._kMail)
                {
                    var profil=sqLiteIslemleri.getirKullaniciIleMail(sonuc)
                    inputDialog.setTitle("Yeni şifrenizi giriniz")
                    val inputEditText2 = EditText(this)
                    inputDialog.setView(inputEditText2)
                    inputDialog.setPositiveButton("Tamam") {dialog2,which2 ->
                        val sonuc2 = inputEditText2.text.toString()
                        if(sonuc2.isNotEmpty())
                        {
                            profil._kSifre=sonuc2
                            sqLiteIslemleri.güncelleKullanici(profil)
                            Toast.makeText(this,"Şifreniz güncellendi",Toast.LENGTH_SHORT).show()
                        }else
                            Toast.makeText(this,"Şifre girmediniz",Toast.LENGTH_SHORT).show()
                    }
                    inputDialog.show()
                }else{
                    Toast.makeText(this,"Eposta adresi bulunamadı",Toast.LENGTH_LONG).show()
                }
            }
            inputDialog.show()
        }
        kaydirma()
        girisAnimasyon()
    }
    fun biyometrik()
    {
        var executor: Executor = ContextCompat.getMainExecutor(this)
        var biometricPrompt = BiometricPrompt(this as AppCompatActivity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                }
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                        girisYap()

                }
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                }
            })
        var promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Giriş Yap")
            .setSubtitle("Biyometrik doğrulama kullanarak giriş yapın")
            .setNegativeButtonText("İptal")
            .build()

        biometricPrompt.authenticate(promptInfo)

    }
    fun girisYap()
    {
        val intent = Intent(this, AnasayfaActivity::class.java)
        startActivity(intent)
    }
    fun giris():Kullanici?
    {   var acikHesap:Kullanici?=null
        val hatirla=sqLiteIslemleri.acikKullaniciGetir()
        if(hatirla.isNotEmpty())
             acikHesap=sqLiteIslemleri.getirKullaniciIleId(hatirla.toLong())
        return acikHesap
    }
    fun kaydirma()
    {
        val animation = AnimationUtils.loadAnimation(this, R.anim.giris_kayirma)
        baglan.constraintLayoutIcerik.startAnimation(animation)
    }
    fun dogrulama(mutableList: MutableList<Kullanici>,mail:String,sifre:String): Kullanici?
    {
        for(kisi in mutableList)
        {
            if(kisi._kMail==mail && kisi._kSifre==sifre)
            {
                return kisi
            }
        }
        return null
    }
    fun girisAnimasyon()
    {
        var simge=baglan.imageViewLogo

        val scaleAnimation = ScaleAnimation(
            1.0f, 1.5f,
            1.0f, 1.5f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnimation.duration = 1000
        scaleAnimation.repeatCount = Animation.REVERSE
        scaleAnimation.repeatMode = Animation.REVERSE

        val rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = 1000
        //rotateAnimation.repeatCount = Animation.REVERSE
        simge.scaleX=1.5f
        simge.scaleY=1.5f
        val animasyonlar = AnimationSet(true)
        animasyonlar.addAnimation(scaleAnimation)
        animasyonlar.addAnimation(rotateAnimation)

        simge.startAnimation(animasyonlar)
    }
    override fun onResume() {
        baglan.editTextSifre.text.clear()
        super.onResume()
    }

}
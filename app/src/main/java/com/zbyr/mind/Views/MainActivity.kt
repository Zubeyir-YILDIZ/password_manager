package com.zbyr.mind.Views

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.zbyr.mind.Models.Kullanici
import com.zbyr.mind.R
import com.zbyr.mind.Helpers.SqLiteIslemleri
import com.zbyr.mind.databinding.ActivityMainBinding
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    companion object{
        var AktifKullanici: Kullanici?=null
        var pref: SharedPreferences? =null
        var animasyonTercihi:Boolean=true
    }
    private lateinit var baglan:ActivityMainBinding
    private lateinit var sqLiteIslemleri: SqLiteIslemleri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baglan= ActivityMainBinding.inflate(layoutInflater)
        pref= PreferenceManager.getDefaultSharedPreferences(this)
        animasyonTercihi= pref!!.getBoolean("animasyon", false)
        setContentView(baglan.root)
        kaydirma()
        girisAnimasyon()
        sqLiteIslemleri= SqLiteIslemleri(this)

        if(giris()!=null)
        {
            AktifKullanici =giris()
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
                AktifKullanici =kisi
                val intent = Intent(this, AnasayfaActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this,getString(R.string.giris_sayfasi_hatali_giris_en),Toast.LENGTH_LONG).show()
            }
        }
        baglan.textViewUyeOl.setOnClickListener {
            val intent = Intent(this, UyeOlActivity::class.java)
            startActivity(intent)
        }
        baglan.textViewUnuttum.setOnClickListener {
            popupGirdi(baglan.root,getString(R.string.giris_sayfasi_eposta_gir_en))
        }
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
            .setTitle(getString(R.string.giris_sayfasi_giris_yap_en))
            .setSubtitle(getString(R.string.giris_sayfasi_biyometrik_dogrula_en))
            .setNegativeButtonText(getString(R.string.giris_sayfasi_iptal_en))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
    fun girisYap()
    {
        val intent = Intent(this, AnasayfaActivity::class.java)
        startActivity(intent)
    }
    fun giris(): Kullanici?
    {   var acikHesap: Kullanici?=null
        val hatirla=sqLiteIslemleri.acikKullaniciGetir()
        if(hatirla.isNotEmpty())
             acikHesap=sqLiteIslemleri.getirKullaniciIleId(hatirla.toLong())
        return acikHesap
    }
    fun kaydirma()
    {
        if(animasyonTercihi)
        {
            val animation = AnimationUtils.loadAnimation(this, R.anim.giris_kayirma)
            baglan.constraintLayoutIcerik.startAnimation(animation)
        }
    }
    fun kaydirmaTersine()
    {
        if(animasyonTercihi)
        {
            val animation2 = AnimationUtils.loadAnimation(this, R.anim.giris_ters_kaydirma)
            baglan.constraintLayoutIcerik.startAnimation(animation2)
        }
    }
    fun dogrulama(mutableList: MutableList<Kullanici>, mail:String, sifre:String): Kullanici?
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
        if(animasyonTercihi)
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

            simge.scaleX=1.5f
            simge.scaleY=1.5f
            val animasyonlar = AnimationSet(true)
            animasyonlar.addAnimation(scaleAnimation)
            animasyonlar.addAnimation(rotateAnimation)

            simge.startAnimation(animasyonlar)
        }
    }
    override fun onResume() {
        baglan.editTextSifre.text.clear()
        kaydirma()
        super.onResume()
    }
    fun popupGirdi(view: View, metin:String)
    {
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_layout,null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true)
        baglan.layoutMain.alpha=0.4f
        val txtGirdi=popupView.findViewById<EditText>(R.id.editTextPopupİstek)
        val buton=popupView.findViewById<Button>(R.id.buttonPopupGonder)

        txtGirdi.setHint(metin)
        txtGirdi.requestFocus()

        popupWindow.showAtLocation(baglan.root, Gravity.CENTER, 0, 0)

        buton.setOnClickListener {
            val girdi=txtGirdi.text.toString()
            if(girdi.isNotEmpty() && girdi==sqLiteIslemleri.getirKullaniciIleMail(girdi)._kMail)
            {
                var profil=sqLiteIslemleri.getirKullaniciIleMail(girdi)
                txtGirdi.text.clear()
                txtGirdi.setHint(getString(R.string.giris_sayfasi_sifre_gir_en))
                buton.setOnClickListener {
                    val sonuc2=txtGirdi.text.toString()
                    if(sonuc2.isNotEmpty())
                    {
                        profil._kSifre=sonuc2
                        sqLiteIslemleri.güncelleKullanici(profil)
                        Toast.makeText(this,getString(R.string.giris_sayfasi_sifre_guncellendi_en),Toast.LENGTH_SHORT).show()
                    }else
                        Toast.makeText(this,getString(R.string.giris_sayfasi_sifre_guncellenemedi_en),Toast.LENGTH_SHORT).show()
                    baglan.layoutMain.alpha=1f
                    popupWindow.dismiss()
                }
            }else{
                Toast.makeText(this,getString(R.string.giris_sayfasi_eposta_bulunamadi_en),Toast.LENGTH_LONG).show()
                baglan.layoutMain.alpha=1f
                popupWindow.dismiss()
            }
        }
        popupWindow.setOnDismissListener {
            baglan.layoutMain.alpha=1f
        }
    }
    override fun onPause() {
        kaydirmaTersine()
        super.onPause()
    }

}
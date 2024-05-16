package com.zbyr.mind.Views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.zbyr.mind.Helpers.FirebaseIslemleri
import com.zbyr.mind.Helpers.PeriyodikIslem
import com.zbyr.mind.Models.Kullanici
import com.zbyr.mind.R
import com.zbyr.mind.Models.Sifre
import com.zbyr.mind.Helpers.SqLiteIslemleri
import com.zbyr.mind.databinding.ActivityAyarlarBinding
import java.util.Locale
import java.util.concurrent.TimeUnit

class AyarlarActivity : AppCompatActivity() {
    private lateinit var bagla:ActivityAyarlarBinding
    private lateinit var firebaseIslemci: FirebaseIslemleri
    private lateinit var sqLiteIslemci: SqLiteIslemleri
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bagla=ActivityAyarlarBinding.inflate(layoutInflater)
        firebaseIslemci= FirebaseIslemleri(this)
        sqLiteIslemci= SqLiteIslemleri(this)
        setContentView(bagla.root)
        bagla.switchAnimasyonAyar.isChecked=MainActivity.animasyonTercihi
        bagla.editTextIsim.setText(MainActivity.AktifKullanici!!._kAdi.toString())
        bagla.editTextSoyisim.setText(MainActivity.AktifKullanici!!._kSoyadi.toString())
        bagla.editTextEpostaEdit.setText(MainActivity.AktifKullanici!!._kMail.toString())
        bagla.editTextSifreEdit.setText(MainActivity.AktifKullanici!!._kSifre.toString())
        val renk=getColor(R.color.uygulama_rengi)
        bagla.buttonDuzenle.setOnClickListener {
            if(bagla.buttonDuzenle.text==getString(R.string.ayarlar_buton_duzenle_en))
            {
                bagla.buttonDuzenle.setText(getString(R.string.ayarlar_buton_kaydet_en))
                bagla.editTextIsim.isEnabled=true
                bagla.editTextSoyisim.isEnabled=true
                bagla.editTextEpostaEdit.isEnabled=true
                bagla.editTextSifreEdit.isEnabled=true
                bagla.buttonDuzenle.backgroundTintList=ColorStateList.valueOf(renk)
            }else{
                bagla.buttonDuzenle.setText(getString(R.string.ayarlar_buton_duzenle_en))
                bagla.editTextIsim.isEnabled=false
                bagla.editTextSoyisim.isEnabled=false
                bagla.editTextEpostaEdit.isEnabled=false
                bagla.editTextSifreEdit.isEnabled=false
                bagla.buttonDuzenle.backgroundTintList=ColorStateList.valueOf(renk)

                var profil=sqLiteIslemci.getirKullaniciIleMail(bagla.editTextEpostaEdit.text.toString())
                profil._kAdi=bagla.editTextIsim.text.toString()
                profil._kSoyadi=bagla.editTextSoyisim.text.toString()
                profil._kMail=bagla.editTextEpostaEdit.text.toString()
                profil._kSifre=bagla.editTextSifreEdit.text.toString()

                sqLiteIslemci.güncelleKullanici(profil)
                MainActivity.AktifKullanici =profil
            }
        }
        bagla.buttonYedekle.setOnClickListener {
            sqLiteIslemci.aktifKullaniciSil(MainActivity.AktifKullanici!!)
            sqLiteIslemci.aktifKullaniciEkle(MainActivity.AktifKullanici!!,1)
            yedekle(this,7)
            var kl= Kullanici()
            kl._kId= MainActivity.AktifKullanici!!._kId
            kl._kAdi=sqLiteIslemci.sifrele(MainActivity.AktifKullanici!!._kAdi)
            kl._kSoyadi=sqLiteIslemci.sifrele(MainActivity.AktifKullanici!!._kSoyadi)
            kl._kMail=sqLiteIslemci.sifrele(MainActivity.AktifKullanici!!._kMail)
            kl._kSifre=sqLiteIslemci.sifrele(MainActivity.AktifKullanici!!._kSifre)
            firebaseIslemci.KullaniciEkle(kl)
            Toast.makeText(this,getString(R.string.ayarlar_toast_bilgi_yedeklendi_en),Toast.LENGTH_SHORT).show()
        }
        bagla.buttonSifreleriGetir.setOnClickListener {
            if(MainActivity.AktifKullanici !=null)
            {
                firebaseIslemci.SifreGetir(MainActivity.AktifKullanici!!)
                finish()
            }else
                Toast.makeText(this,getString(R.string.ayarlar_toast_bilgi_basarisiz_oldu),Toast.LENGTH_SHORT).show()
        }
        bagla.textViewSil.setOnClickListener {
            sqLiteIslemci.silKullanici(MainActivity.AktifKullanici!!._kMail)
            firebaseIslemci.kullaniciSil(MainActivity.AktifKullanici!!)
            MainActivity.AktifKullanici =null
            finish()
        }
        bagla.textViewYedekSil.setOnClickListener {
            WorkManager.getInstance(this).cancelAllWork()

            firebaseIslemci.sifreleriSil(MainActivity.AktifKullanici!!)
            firebaseIslemci.kullaniciSil(MainActivity.AktifKullanici!!)
            sqLiteIslemci.aktifKullaniciSil(MainActivity.AktifKullanici!!)
            Toast.makeText(this,getString(R.string.ayarlar_toast_bilgi_yedekleme_silindi),Toast.LENGTH_SHORT).show()
        }
        bagla.switchAnimasyonAyar.setOnCheckedChangeListener { buttonView, acikMi ->
            animasyonKapat(acikMi)
        }
    }
    fun yedekle(context: Context,aralik:Long) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val yedek =
            PeriodicWorkRequestBuilder<PeriyodikIslem>(aralik,TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "haftalik_yedek",
            ExistingPeriodicWorkPolicy.KEEP,
            yedek
        )
    }
    fun dilSecimi(dil:String)
    {
        val local=Locale(dil)
        val r=this.resources
        val c=r.configuration
        c.setLocale(Locale("Turkish"))
        r.updateConfiguration(c,r.displayMetrics)
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
    fun animasyonKapat(acikMi:Boolean)
    {
        MainActivity.pref!!.edit().putBoolean("animasyon",acikMi).apply()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}

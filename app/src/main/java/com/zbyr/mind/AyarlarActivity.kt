package com.zbyr.mind

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.database.collection.LLRBNode.Color
import com.zbyr.mind.databinding.ActivityAyarlarBinding
import java.sql.Time
import java.util.concurrent.TimeUnit

class AyarlarActivity : AppCompatActivity() {
    private lateinit var bagla:ActivityAyarlarBinding
    private lateinit var firebaseIslemci:FirebaseIslemleri
    private lateinit var sqLiteIslemci:SqLiteIslemleri

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bagla=ActivityAyarlarBinding.inflate(layoutInflater)
        firebaseIslemci=FirebaseIslemleri()
        sqLiteIslemci=SqLiteIslemleri(this)
        setContentView(bagla.root)

        bagla.editTextIsim.setText(MainActivity.AktifKullanici!!._kAdi.toString())
        bagla.editTextSoyisim.setText(MainActivity.AktifKullanici!!._kSoyadi.toString())
        bagla.editTextEpostaEdit.setText(MainActivity.AktifKullanici!!._kMail.toString())
        bagla.editTextSifreEdit.setText(MainActivity.AktifKullanici!!._kSifre.toString())
        val color=android.graphics.Color.argb(255,76,175,80)
        bagla.buttonDuzenle.setOnClickListener {
            if(bagla.buttonDuzenle.text=="Düzenle")
            {
                bagla.buttonDuzenle.setText("Kaydet")
                bagla.editTextIsim.isEnabled=true
                bagla.editTextSoyisim.isEnabled=true
                bagla.editTextEpostaEdit.isEnabled=true
                bagla.editTextSifreEdit.isEnabled=true
                bagla.buttonDuzenle.backgroundTintList=ColorStateList.valueOf(color)
            }else{
                bagla.buttonDuzenle.setText("Düzenle")
                bagla.editTextIsim.isEnabled=false
                bagla.editTextSoyisim.isEnabled=false
                bagla.editTextEpostaEdit.isEnabled=false
                bagla.editTextSifreEdit.isEnabled=false
                bagla.buttonDuzenle.backgroundTintList=ColorStateList.valueOf(color)

                var profil=sqLiteIslemci.getirKullaniciIleMail(bagla.editTextEpostaEdit.text.toString())
                profil._kAdi=bagla.editTextIsim.text.toString()
                profil._kSoyadi=bagla.editTextSoyisim.text.toString()
                profil._kMail=bagla.editTextEpostaEdit.text.toString()
                profil._kSifre=bagla.editTextSifreEdit.text.toString()

                sqLiteIslemci.güncelleKullanici(profil)
                MainActivity.AktifKullanici=profil
                bagla.buttonDuzenle.setBackgroundResource(R.drawable.ayarlar_text_alani)
            }
        }
        bagla.buttonYedekle.setOnClickListener {
            sqLiteIslemci.aktifKullaniciSil(MainActivity.AktifKullanici!!)
            sqLiteIslemci.aktifKullaniciEkle(MainActivity.AktifKullanici!!,1)
            yedekle(this,7)
            firebaseIslemci.KullaniciEkle(MainActivity.AktifKullanici!!)
        }
        bagla.buttonSifreleriGetir.setOnClickListener {
            if(MainActivity.AktifKullanici!=null)
            {
                firebaseIslemci.SifreGetir(MainActivity.AktifKullanici!!)
                var sifreler=MainActivity.sifreListesi

                for(sifre in sifreler)
                {
                    if(kontrol(sifre))
                    {
                        sqLiteIslemci.ekleSifre(sifre)
                    }
                }
                MainActivity.sifreListesi.clear()
                finish()
            }else
                Toast.makeText(this,"Başarısız oldu",Toast.LENGTH_SHORT).show()
        }
        bagla.textViewSil.setOnClickListener {
            sqLiteIslemci.silKullanici(MainActivity.AktifKullanici!!._kMail)
            firebaseIslemci.kullaniciSil(MainActivity.AktifKullanici!!)
            MainActivity.AktifKullanici=null
            finish()
        }
        bagla.textViewYedekSil.setOnClickListener {
            WorkManager.getInstance(this).cancelAllWork()
            firebaseIslemci.sifreleriSil(MainActivity.AktifKullanici!!)
            sqLiteIslemci.aktifKullaniciSil(MainActivity.AktifKullanici!!)
        }
    }
    fun ok(isDrawableUp:Boolean)
    {
        val drawableUp = ContextCompat.getDrawable(this, R.drawable.baseline_keyboard_double_arrow_up_24)
        val drawableDown = ContextCompat.getDrawable(this, R.drawable.baseline_keyboard_double_arrow_down_24)

        val drawable = if (isDrawableUp) drawableDown else drawableUp
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        bagla.textViewKisiselBilgiler.setCompoundDrawables(null, null, drawable, null)
    }
    fun kontrol(sifre: Sifre):Boolean
    {
        var sonuc=true
        var liste=sqLiteIslemci.getirSifre(MainActivity.AktifKullanici!!)
        if (liste != null) {
            for(_sifre in liste)
            {
                if(_sifre._sId == sifre._sId && _sifre._sKullanici!!._kId == sifre._sKullanici!!._kId)
                    sonuc=false
            }
        }
        return sonuc
    }
    fun yedekle(context: Context,aralik:Long) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val yedek =
            PeriodicWorkRequestBuilder<periyodikIslem>(aralik,TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "haftalik_yedek",
            ExistingPeriodicWorkPolicy.KEEP,
            yedek
        )
    }
}

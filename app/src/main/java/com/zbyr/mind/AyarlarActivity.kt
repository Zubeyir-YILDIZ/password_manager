package com.zbyr.mind

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.zbyr.mind.databinding.ActivityAyarlarBinding

class AyarlarActivity : AppCompatActivity() {
    private lateinit var bagla:ActivityAyarlarBinding
    private lateinit var firebaseIslemci:FirebaseIslemleri
    private lateinit var sqLiteIslemci:SqLiteIslemleri

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

        bagla.buttonDuzenle.setOnClickListener {
            if(bagla.buttonDuzenle.text=="Düzenle")
            {
                bagla.buttonDuzenle.setText("Kaydet")
                bagla.editTextIsim.isEnabled=true
                bagla.editTextSoyisim.isEnabled=true
                bagla.editTextEpostaEdit.isEnabled=true
                bagla.editTextSifreEdit.isEnabled=true
            }else{
                bagla.buttonDuzenle.setText("Düzenle")
                bagla.editTextIsim.isEnabled=false
                bagla.editTextSoyisim.isEnabled=false
                bagla.editTextEpostaEdit.isEnabled=false
                bagla.editTextSifreEdit.isEnabled=false

                var profil=sqLiteIslemci.getirKullaniciIleMail(bagla.editTextEpostaEdit.text.toString())
                profil._kAdi=bagla.editTextIsim.text.toString()
                profil._kSoyadi=bagla.editTextSoyisim.text.toString()
                profil._kMail=bagla.editTextEpostaEdit.text.toString()
                profil._kSifre=bagla.editTextSifreEdit.text.toString()

                sqLiteIslemci.güncelleKullanici(profil)
                MainActivity.AktifKullanici=profil
            }
        }
        bagla.buttonYedekle.setOnClickListener {
            var sifreler:MutableList<Sifre> = sqLiteIslemci.getirSifre()!!
            if(sifreler.isNotEmpty())
            {
                for(sifre in sifreler)
                {
                    firebaseIslemci.SifreEkle(sifre)
                }
                Toast.makeText(this,"Şifreleriniz yedeklendi",Toast.LENGTH_SHORT).show()
            }
            if(MainActivity.AktifKullanici!=null)
                firebaseIslemci.KullaniciEkle(MainActivity.AktifKullanici!!)
        }
        bagla.buttonSifreleriGetir.setOnClickListener {
            if(MainActivity.AktifKullanici!=null)
            {
                var kullanicilar=firebaseIslemci.KullaniciGetir(MainActivity.AktifKullanici!!)
                // eksik **
            }
        }
        bagla.textViewSil.setOnClickListener {
            sqLiteIslemci.silKullanici(MainActivity.AktifKullanici!!._kMail)
            MainActivity.AktifKullanici=null
            finish()
        }

    }
}
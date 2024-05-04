package com.zbyr.mind

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.zbyr.mind.databinding.ActivitySifreEkleBinding

class SifreEkleActivity : AppCompatActivity() {
    private lateinit var bagla:ActivitySifreEkleBinding
    private lateinit var firebaseIslemci:FirebaseIslemleri
    private lateinit var sqLiteIslemci:SqLiteIslemleri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseIslemci=FirebaseIslemleri(this)
        sqLiteIslemci= SqLiteIslemleri(this)
        bagla=ActivitySifreEkleBinding.inflate(layoutInflater)
        setContentView(bagla.root)
        bagla.constraintLayout.rotationX=90f
        bagla.editTextText.visibility=View.GONE
        acilisAnimasyonu()
        bagla.buttonSifreOlustur.setOnClickListener {
            val txtSifre=bagla.editTextSifreSifre
            val sifre:Sifre
            val txtSifreAdi=bagla.editTextSifreAdi
            if(txtSifre.text.isNotEmpty() && txtSifreAdi.text.isNotEmpty())
            {
                val seciliDeger:String=AnasayfaActivity.kategori
                val sifreTipi=SifreTip()
                val tip=sqLiteIslemci.tipDogrula(seciliDeger,MainActivity.AktifKullanici!!)
                if(tip!=null)
                {
                    sifreTipi._TipId= tip._TipId
                    sifreTipi._SifreTipi=tip._SifreTipi
                    if(bagla.switchAcKullaniciAdi.isChecked && bagla.editTextText.text.isNotEmpty())
                    {
                        val txtKullaniciAdi=bagla.editTextText
                        sifre=Sifre(txtSifre.text.toString(),sifreTipi, MainActivity.AktifKullanici!!,txtKullaniciAdi.text.toString(),txtSifreAdi.text.toString())
                    }else
                        sifre=Sifre(txtSifre.text.toString(),sifreTipi, MainActivity.AktifKullanici!!,txtSifreAdi.text.toString())

                    sqLiteIslemci.ekleSifre(sifre)
                    AnasayfaActivity.anahtar=true
                    finish()
                }else
                    Toast.makeText(applicationContext,"Kategori bulunamadı", Toast.LENGTH_LONG).show()
            }else
            {
                Toast.makeText(applicationContext,"Lütfen boş alanları dolduralım", Toast.LENGTH_LONG).show()
            }
        }
        bagla.buttonSifreOner.setOnClickListener {
            val intent=Intent(this,SifreOnerActivity::class.java)
            startActivity(intent)
        }
        bagla.switchAcKullaniciAdi.setOnClickListener {
            aktifMi()
        }
    }
    fun acilisAnimasyonu()
    {
        var layout=bagla.constraintLayout

        layout.animate().rotationX(0f).duration=1000
    }
    fun aktifMi()
    {
        if(bagla.switchAcKullaniciAdi.isChecked)
        {
            bagla.editTextText.visibility=View.VISIBLE
        }else
            bagla.editTextText.visibility=View.GONE
    }

}
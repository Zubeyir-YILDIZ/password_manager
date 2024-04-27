package com.zbyr.mind

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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

        firebaseIslemci=FirebaseIslemleri()
        sqLiteIslemci= SqLiteIslemleri(this)
        bagla=ActivitySifreEkleBinding.inflate(layoutInflater)
        setContentView(bagla.root)
        spinnerOlustur(bagla.spinnerSifreTurleri,tipleriListele(sqLiteIslemci.getirSifreTip()))

        bagla.buttonSifreOlustur.setOnClickListener {
            var txtSifre=bagla.editTextSifreSifre

            if(txtSifre.text.isNotEmpty())
            {
                var sifreTipi=SifreTip()
                if(sqLiteIslemci.tipDogrula(seciliDeger!!)!=null)
                    sifreTipi._TipId= sqLiteIslemci.tipDogrula(seciliDeger!!)!!._TipId as Long
                sifreTipi._SifreTipi=seciliDeger.toString()
                var sifre=Sifre(txtSifre.text.toString(),sifreTipi, MainActivity.AktifKullanici!!)
                sqLiteIslemci.ekleSifre(sifre)
                AnasayfaActivity.kategori=sifre._sTur?._TipId.toString()
                AnasayfaActivity.anahtar=true
                finish()
            }else
            {
                Toast.makeText(applicationContext,"Lütfen boş alanları dolduralım", Toast.LENGTH_LONG).show()
            }
        }
        bagla.textViewEkleTip.setOnClickListener {
            ekleTip(this,bagla.spinnerSifreTurleri)
        }
        bagla.buttonSifreOner.setOnClickListener {
            val intent=Intent(this,SifreOnerActivity::class.java)
            startActivity(intent)
        }
        bagla.textViewSilTip.setOnClickListener {
            if(sqLiteIslemci.tipDogrula(seciliDeger!!)!=null)
            {
                sqLiteIslemci.silTip(sqLiteIslemci.tipDogrula(seciliDeger!!)!!)
                spinnerOlustur(bagla.spinnerSifreTurleri,tipleriListele(sqLiteIslemci.getirSifreTip()))
            }
        }
    }
    var seciliDeger:String?=""
    fun spinnerOlustur(spinner: Spinner,sifreTipleri:ArrayList<String>)
    {
        val adapter=ArrayAdapter(this,android.R.layout.simple_spinner_item,sifreTipleri)
        spinner.adapter=adapter
        spinner.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                seciliDeger=sifreTipleri[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }
    fun ekleTip(context: Context,spinner: Spinner)
    {
        val inputDialog = AlertDialog.Builder(context)
        inputDialog.setTitle("Şifreniz için tür ismi giriniz")
        val inputEditText = EditText(context)
        inputDialog.setView(inputEditText)
        inputDialog.setPositiveButton("Tamam") {dialog,which ->
            val sonuc = inputEditText.text.toString()
            if(sonuc.isNotEmpty() && sqLiteIslemci.tipKarsilastir(sonuc))
            {
                sqLiteIslemci.ekleSifreTipi(SifreTip(sonuc))
                spinnerOlustur(spinner,tipleriListele(sqLiteIslemci.getirSifreTip()))
            }
        }
        inputDialog.show()
    }
    fun tipleriListele(mutableList: MutableList<SifreTip>):ArrayList<String>
    {
        var liste=ArrayList<String>()
        for(tip in mutableList)
        {
            liste.add(tip._SifreTipi.toString())
        }
        return liste
    }


}
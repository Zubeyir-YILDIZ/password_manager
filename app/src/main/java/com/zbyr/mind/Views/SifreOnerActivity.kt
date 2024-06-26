package com.zbyr.mind.Views

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.zbyr.mind.R
import com.zbyr.mind.databinding.ActivitySifreOnerBinding
import kotlin.random.Random

class SifreOnerActivity : AppCompatActivity() {
    private lateinit var bagla:ActivitySifreOnerBinding
    private var sonuc:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bagla= ActivitySifreOnerBinding.inflate(layoutInflater)
        setContentView(bagla.root)
        bagla.textViewSayiUzunluk.setText("8")
        bagla.buttonOlusturSifre.setOnClickListener {
            sonuc=RastgeleSifreOlustur()
            bagla.editTextBilgiSifre.setText(sonuc)
            if(sonuc.isNotEmpty())
                onayAl(getString(R.string.popup_onaylanacak_metin_sifre_doldur_en))
        }
        bagla.textViewKopyala.setOnClickListener {
            panoyaKopyala(bagla.editTextBilgiSifre.text.toString())
            finish()
        }
        bagla.seekBarSayiUzunluk.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                bagla.textViewSayiUzunluk.setText("" + i)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
        kaydirma()
    }
    fun kaydirma()
    {
        if(MainActivity.animasyonTercihi)
        {
            val animasyonBaslik = AnimationUtils.loadAnimation(this, R.anim.sifre_oner_kaydirma)
            bagla.layoutBaslik.startAnimation(animasyonBaslik)
            val animasyonSol = AnimationUtils.loadAnimation(this, R.anim.sifre_oner_kaydirma_sol)
            bagla.layoutSol.startAnimation(animasyonSol)
            val animasyonSag = AnimationUtils.loadAnimation(this, R.anim.sifre_oner_kaydirma_sag)
            bagla.layoutSag.startAnimation(animasyonSag)
        }
    }
    fun RastgeleSifreOlustur():String
    {
        val buyukHarfKontrol = bagla.checkBoxHarfTipi.isChecked
        val kucukHarfKontrol = bagla.checkBoxHarfTipi.isChecked
        val ozelKarakterKontrol = bagla.checkBoxOzelKarakter.isChecked
        val sayiKontrol = bagla.checkBoxSayi.isChecked
        val istenilenUzunluk =(bagla.textViewSayiUzunluk.text.toString()).toInt()

        val buyukHarf = "ABCDEFGHIİJKLMNOÖPQRSŞTUÜVWXYZ"
        val kucukHarf = "abcdefghıijklmnoöpqrsştuüvwxyz"
        val ozelKarakter = "!@#$%^&*()_+{}[]|;<>,.?/~"
        val sayi = "0123456789"

        val havuz = mutableListOf<Char>()

        if (buyukHarfKontrol)
            havuz.addAll(buyukHarf.toMutableList())
        if (kucukHarfKontrol)
            havuz.addAll(kucukHarf.toMutableList())
        if (ozelKarakterKontrol)
            havuz.addAll(ozelKarakter.toMutableList())
        if (sayiKontrol)
            havuz.addAll(sayi.toMutableList())
        var rastgeleSifre:String=""
        if(havuz.isNotEmpty())
        {
            rastgeleSifre = buildString {
                repeat(istenilenUzunluk) {
                    append(havuz[Random.nextInt(0,havuz.size)])
                }
            }
        }

        return rastgeleSifre
    }
    private fun panoyaKopyala(text: String) {
        val pano = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val metin = android.content.ClipData.newPlainText(getString(R.string.sifre_oner_pano_sifre_en), text)
        pano.setPrimaryClip(metin)
        Toast.makeText(this, getString(R.string.sifre_oner_pano_kopyalandi_en), Toast.LENGTH_SHORT).show()
    }
    @SuppressLint("MissingInflatedId")
    fun onayAl(metin:String)
    {
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_onay_alma_layout,null)

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true)

        bagla.sifreOnerLayout.alpha=0.4f

        val txtGirdi=popupView.findViewById<TextView>(R.id.textViewOnayMetin)
        txtGirdi.setText(metin)
        popupWindow.showAtLocation(bagla.root, Gravity.CENTER, 0, 0)
        val butonOnay=popupView.findViewById<Button>(R.id.buttonPopupOnayla)
        val butonIptal=popupView.findViewById<Button>(R.id.buttonPopupIptalEt)
        butonOnay.setOnClickListener {
            SifreEkleActivity.onerilenSifre=sonuc
            finish()
        }
        butonIptal.setOnClickListener {
            bagla.sifreOnerLayout.alpha=1f
            popupWindow.dismiss()
        }
        popupWindow.setOnDismissListener {
            bagla.sifreOnerLayout.alpha=1f
        }
    }



}
package com.zbyr.mind.Views

import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import com.zbyr.mind.R
import com.zbyr.mind.databinding.ActivitySifreOnerBinding
import kotlin.random.Random

class SifreOnerActivity : AppCompatActivity() {
    private lateinit var bagla:ActivitySifreOnerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bagla= ActivitySifreOnerBinding.inflate(layoutInflater)
        setContentView(bagla.root)
        bagla.textViewSayiUzunluk.setText("8")
        bagla.buttonOlusturSifre.setOnClickListener {
            var sonuc=RastgeleSifreOlustur()
            bagla.editTextBilgiSifre.setText(sonuc)
        }
        bagla.textViewKopyala.setOnClickListener {
                panoyaKopyala(bagla.editTextBilgiSifre.text.toString())
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
        val animasyonBaslık = AnimationUtils.loadAnimation(this, R.anim.sifre_oner_kaydirma)
        bagla.layoutBaslik.startAnimation(animasyonBaslık)
        val animasyonSol = AnimationUtils.loadAnimation(this, R.anim.sifre_oner_kaydirma_sol)
        bagla.layoutSol.startAnimation(animasyonSol)
        val animasyonSag = AnimationUtils.loadAnimation(this, R.anim.sifre_oner_kaydirma_sag)
        bagla.layoutSag.startAnimation(animasyonSag)
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
        val metin = android.content.ClipData.newPlainText("Şifre", text)
        pano.setPrimaryClip(metin)
        Toast.makeText(this, "Kopyalandı", Toast.LENGTH_SHORT).show()
    }




}
package com.zbyr.mind.Views

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.zbyr.mind.Adapters.KategoriAdapter
import com.zbyr.mind.R
import com.zbyr.mind.Adapters.SifreAdapter
import com.zbyr.mind.Models.SifreTip
import com.zbyr.mind.Helpers.SqLiteIslemleri
import com.zbyr.mind.databinding.ActivityAnasayfaBinding

class AnasayfaActivity : AppCompatActivity() {
    private lateinit var bagla:ActivityAnasayfaBinding
    private lateinit var sqLiteIslemleri: SqLiteIslemleri
    companion object{
        var anahtar:Boolean=false
        var kategori:String=""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bagla= ActivityAnasayfaBinding.inflate(layoutInflater)
        sqLiteIslemleri= SqLiteIslemleri(this)
        setContentView(bagla.root)
        yonCubugu()
        kategoriOlustur()

        bagla.textViewOncekiSayfa.setOnClickListener {
            kategoriOlustur()
            donusAnimasyonu()
        }
    }
    fun yonCubugu()
    {
        setSupportActionBar(bagla.yon)
        bagla.floatingButonSifreEkle.setOnClickListener {
            val intent=Intent(this, SifreEkleActivity::class.java)
            startActivity(intent)
        }
    }
    fun gecisAnimasyonu()
    {
        if(MainActivity.animasyonTercihi)
        {
            val rotationAnimation = ObjectAnimator.ofFloat(bagla.RecyclerViewSifreler, "rotation", 0f, 360f)
            rotationAnimation.duration = 500
            rotationAnimation.start()
        }
    }
    fun donusAnimasyonu()
    {
        if(MainActivity.animasyonTercihi)
        {
            val rotationAnimation = ObjectAnimator.ofFloat(bagla.RecyclerViewSifreler, "rotation", 0f, -360f)
            rotationAnimation.duration = 500
            rotationAnimation.start()
        }
    }
    fun sifreleriGetir(kategori:String)
    {
        val liste=sqLiteIslemleri.getirSifrelerTipIle(kategori, MainActivity.AktifKullanici!!)
        gecisAnimasyonu()
        supportActionBar?.title=sqLiteIslemleri.tipDogrula(kategori, MainActivity.AktifKullanici!!)!!._SifreTipi
        if(!bagla.floatingButonSifreEkle.isShown)
            bagla.floatingButonSifreEkle.show()
        bagla.textViewSifreleriniz.setText(R.string.anasayfa_baslik_sifre_en)
        bagla.textViewOncekiSayfa.visibility=View.VISIBLE
        bagla.RecyclerViewSifreler.layoutManager=LinearLayoutManager(this)
        val sifreAdapter= SifreAdapter(liste)
        gosterKategoriEkleSil(true)
        bagla.RecyclerViewSifreler.adapter=sifreAdapter
    }
    fun gosterKategoriEkleSil(deger:Boolean)
    {
        for(rrr in bagla.yon.menu.children)
        {
            if(rrr.title==getString(R.string.anasayfa_menu_kategori_ekle_en))
            {
                if(deger)
                    rrr.setTitle(getString(R.string.anasayfa_menu_kategori_sil_en))

            }
            if(rrr.title==getString(R.string.anasayfa_menu_kategori_sil_en))
            {
                if(!deger)
                    rrr.setTitle(getString(R.string.anasayfa_menu_kategori_ekle_en))
            }
        }
    }
    fun kategoriOlustur()
    {
        var kategoriler=sqLiteIslemleri.tipGetirkIdIle(MainActivity.AktifKullanici!!._kId)

        bagla.textViewSifreleriniz.setText(R.string.anasayfa_baslik_en)
        supportActionBar?.title= getString(R.string.anasayfa_selamlama_en) +" "+ MainActivity.AktifKullanici?._kAdi
        bagla.floatingButonSifreEkle.hide()
        bagla.textViewOncekiSayfa.visibility=View.GONE
        bagla.RecyclerViewSifreler.layoutManager=GridLayoutManager(this,2)
        val kategoriAdapter= KategoriAdapter(kategoriler)
        gosterKategoriEkleSil(false)
        bagla.RecyclerViewSifreler.adapter=kategoriAdapter
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cubuk,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId== R.id.yon_ayarlar)
        {
            val intent=Intent(this, AyarlarActivity::class.java)
            startActivity(intent)
        }
        if(item.itemId== R.id.yon_cikis)
        {
            MainActivity.AktifKullanici =null
            finish()
        }
        if(item.itemId== R.id.yon_sil)
        {
            if(item.title==getString(R.string.anasayfa_menu_kategori_sil_en))
            {
                if(sqLiteIslemleri.tipDogrula(kategori, MainActivity.AktifKullanici!!)!=null)
                {
                    onayAl(getString(R.string.popup_onaylanacak_metin_kategori_sil_en))
                }
            }else
            {
                popupGirdi(getString(R.string.anasayfa_popup_mesaj_en))
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onResume() {
        if(anahtar)
        {
            sifreleriGetir(kategori)
            anahtar =false
        }
        super.onResume()
        if(MainActivity.AktifKullanici ==null)
        {
            finish()
        }
    }
    @SuppressLint("MissingInflatedId")
    fun popupGirdi(metin:String)
    {
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_layout,null)

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true)

        bagla.layout.alpha=0.4f

        val txtGirdi=popupView.findViewById<EditText>(R.id.editTextPopupİstek)
        txtGirdi.setHint(metin)
        txtGirdi.requestFocus()
        popupWindow.showAtLocation(bagla.root, Gravity.CENTER, 0, 0)
        val buton=popupView.findViewById<Button>(R.id.buttonPopupGonder)
        buton.setOnClickListener {
            val girdi=txtGirdi.text.toString()

            if(sqLiteIslemleri.tipKarsilastir(girdi, MainActivity.AktifKullanici!!) && girdi.isNotEmpty())
            {
                sqLiteIslemleri.ekleSifreTipi(
                    SifreTip(girdi, MainActivity.AktifKullanici!!._kId),
                    MainActivity.AktifKullanici!!)
            }
            kategoriOlustur()
            bagla.layout.alpha=1f
            popupWindow.dismiss()
        }
        popupWindow.setOnDismissListener {
            bagla.layout.alpha=1f
        }
    }
    fun onayAl(metin:String)
    {
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_onay_alma_layout,null)

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true)

        bagla.layout.alpha=0.4f

        val txtGirdi=popupView.findViewById<TextView>(R.id.textViewOnayMetin)
        txtGirdi.setText(metin)
        popupWindow.showAtLocation(bagla.root, Gravity.CENTER, 0, 0)
        val butonOnay=popupView.findViewById<Button>(R.id.buttonPopupOnayla)
        val butonIptal=popupView.findViewById<Button>(R.id.buttonPopupIptalEt)
        butonOnay.setOnClickListener {
            var sifreler=sqLiteIslemleri.getirSifrelerTipIle(
                (sqLiteIslemleri.tipDogrula(kategori, MainActivity.AktifKullanici!!)!!)._TipId.toString(),
                MainActivity.AktifKullanici!!
            )
            sqLiteIslemleri.silSifreler(sifreler)
            sqLiteIslemleri.silTip(sqLiteIslemleri.tipDogrula(
                kategori,
                MainActivity.AktifKullanici!!)!!)
            popupWindow.dismiss()
            kategoriOlustur()
        }
        butonIptal.setOnClickListener {
            popupWindow.dismiss()
        }
        popupWindow.setOnDismissListener {
            bagla.layout.alpha=1f
        }
    }
}
package com.zbyr.mind

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.zbyr.mind.databinding.ActivityAnasayfaBinding
import com.zbyr.mind.databinding.ActivityMainBinding
import kotlinx.coroutines.delay

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
        sqLiteIslemleri=SqLiteIslemleri(this)
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
        bagla.floatingActionButton.setOnClickListener {
            val intent=Intent(this,SifreEkleActivity::class.java)
            intent.putExtra("kategori",bagla.root.context.toString())
            startActivity(intent)
        }
    }
    fun gecisAnimasyonu()
    {
        val rotationAnimation = ObjectAnimator.ofFloat(bagla.RecyclerViewSifreler, "rotation", 0f, 360f)
        rotationAnimation.duration = 500
        rotationAnimation.start()
    }
    fun donusAnimasyonu()
    {
        val rotationAnimation = ObjectAnimator.ofFloat(bagla.RecyclerViewSifreler, "rotation", 0f, -360f)
        rotationAnimation.duration = 500
        rotationAnimation.start()
    }
    fun sifreleriGetir(kategori:String)
    {
        val liste=sqLiteIslemleri.getirSifrelerTipIle(kategori,MainActivity.AktifKullanici!!)
        gecisAnimasyonu()
        supportActionBar?.title=sqLiteIslemleri.tipDogrula(kategori,MainActivity.AktifKullanici!!)!!._SifreTipi
        if(!bagla.floatingActionButton.isShown)
            bagla.floatingActionButton.show()
        bagla.textViewSifreleriniz.setText("Şifreleriniz")
        bagla.textViewOncekiSayfa.visibility=View.VISIBLE
        bagla.RecyclerViewSifreler.layoutManager=LinearLayoutManager(this)
        val sifreAdapter=SifreAdapter(liste)
        gösterKategoriEkleSil(true)
        bagla.RecyclerViewSifreler.adapter=sifreAdapter
    }
    fun gösterKategoriEkleSil(deger:Boolean)
    {
        for(rrr in bagla.yon.menu.children)
        {
            if(rrr.title=="Kategori Ekle")
            {
                if(deger)
                    rrr.setTitle("Kategori Sil")

            }
            if(rrr.title=="Kategori Sil")
            {
                if(!deger)
                    rrr.setTitle("Kategori Ekle")
            }
        }
    }
    fun kategoriOlustur()
    {
        var kategoriler=sqLiteIslemleri.tipGetirkIdIle(MainActivity.AktifKullanici!!._kId)

        bagla.textViewSifreleriniz.setText("Kategoriler")
        supportActionBar?.title="Hoşgeldin "+MainActivity.AktifKullanici?._kAdi
        bagla.floatingActionButton.hide()
        bagla.textViewOncekiSayfa.visibility=View.GONE
        bagla.RecyclerViewSifreler.layoutManager=GridLayoutManager(this,2)
        val kategoriAdapter=KategoriAdapter(kategoriler)
        gösterKategoriEkleSil(false)
        bagla.RecyclerViewSifreler.adapter=kategoriAdapter
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cubuk,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId==R.id.yon_ayarlar)
        {
            val intent=Intent(this,AyarlarActivity::class.java)
            startActivity(intent)
        }
        if(item.itemId==R.id.yon_cikis)
        {
            MainActivity.AktifKullanici=null
            finish()
        }
        if(item.itemId==R.id.yon_sil)
        {
            if(item.title=="Kategori Sil")
            {
                if(sqLiteIslemleri.tipDogrula(kategori,MainActivity.AktifKullanici!!)!=null)
                {
                    var sifreler=sqLiteIslemleri.getirSifrelerTipIle(
                        (sqLiteIslemleri.tipDogrula(kategori,MainActivity.AktifKullanici!!)!!)._TipId.toString(),
                        MainActivity.AktifKullanici!!
                    )
                    sqLiteIslemleri.silSifreler(sifreler)
                    sqLiteIslemleri.silTip(sqLiteIslemleri.tipDogrula(kategori,MainActivity.AktifKullanici!!)!!)
                    kategoriOlustur()
                }
            }else
            {
                popupGirdi("Yeni Kategori Giriniz")

            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onResume() {
        if(anahtar)
        {
            sifreleriGetir(kategori)
            anahtar=false
        }
        super.onResume()
        if(MainActivity.AktifKullanici==null)
        {
            finish()
        }
    }
    fun kategoriSor()
    {
        val inputDialog = AlertDialog.Builder(bagla.root.context)
        inputDialog.setTitle("Yeni kategorinizi giriniz")
        val inputEditText2 = EditText(bagla.root.context)
        inputDialog.setView(inputEditText2)
        inputDialog.setPositiveButton("Tamam") {dialog2,which2 ->
            val sonuc = inputEditText2.text.toString()
            if(sqLiteIslemleri.tipKarsilastir(sonuc,MainActivity.AktifKullanici!!) && sonuc.isNotEmpty())
            {
                sqLiteIslemleri.ekleSifreTipi(SifreTip(sonuc,MainActivity.AktifKullanici!!._kId),MainActivity.AktifKullanici!!)
            }
            kategoriOlustur()
        }
        inputDialog.show()
    }
    @SuppressLint("MissingInflatedId")
    fun popupGirdi(metin:String)
    {
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_layout,null)

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true)

        val animasyon=bagla.layout.animate().alpha(0.4f)
        animasyon.duration=1000
        animasyon.start()
        val txtGirdi=popupView.findViewById<EditText>(R.id.editTextPopupİstek)
        txtGirdi.setHint(metin)
        txtGirdi.requestFocus()
        popupWindow.showAtLocation(bagla.root, Gravity.CENTER, 0, 0)
        val buton=popupView.findViewById<Button>(R.id.buttonPopupGonder)
        buton.setOnClickListener {
            val girdi=txtGirdi.text.toString()

            if(sqLiteIslemleri.tipKarsilastir(girdi,MainActivity.AktifKullanici!!) && girdi.isNotEmpty())
            {
                sqLiteIslemleri.ekleSifreTipi(SifreTip(girdi,MainActivity.AktifKullanici!!._kId),MainActivity.AktifKullanici!!)
            }
            kategoriOlustur()
            bagla.layout.alpha=1f
            popupWindow.dismiss()
        }
        popupWindow.setOnDismissListener {
            bagla.layout.alpha=1f
        }

    }
}
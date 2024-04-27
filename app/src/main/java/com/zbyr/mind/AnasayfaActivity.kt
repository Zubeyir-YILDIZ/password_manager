package com.zbyr.mind

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.ActionBar
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.zbyr.mind.databinding.ActivityAnasayfaBinding
import com.zbyr.mind.databinding.ActivityMainBinding

class AnasayfaActivity : AppCompatActivity() {
    private lateinit var bagla:ActivityAnasayfaBinding
    private lateinit var sqLiteIslemleri: SqLiteIslemleri
    companion object{
        var anahtar:Boolean=false
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
        }

    }
    fun yonCubugu()
    {
        setSupportActionBar(bagla.yon)
        bagla.floatingActionButton.setOnClickListener {
            val intent=Intent(this,SifreEkleActivity::class.java)
            startActivity(intent)
        }
    }
    fun sifreleriGetir(kategori:String)
    {
        var liste=sqLiteIslemleri.getirSifrelerTipIle(kategori,MainActivity.AktifKullanici!!)
        supportActionBar?.title=""

        if(!bagla.floatingActionButton.isShown)
            bagla.floatingActionButton.show()
        bagla.textViewSifreleriniz.setText("Şifreleriniz")
        bagla.textViewOncekiSayfa.visibility=View.VISIBLE
        bagla.RecyclerViewSifreler.layoutManager=LinearLayoutManager(this)
        val sifreAdapter=SifreAdapter(liste)
        bagla.RecyclerViewSifreler.adapter=sifreAdapter
    }
    fun kategoriOlustur()
    {
        var kategoriler=sqLiteIslemleri.getirSifreTip()
        bagla.textViewSifreleriniz.setText("Kategoriler")
        supportActionBar?.title="Hoşgeldin "+MainActivity.AktifKullanici?._kAdi

        if(bagla.floatingActionButton.isShown)
            bagla.floatingActionButton.hide()
        bagla.textViewOncekiSayfa.visibility=View.GONE
        bagla.RecyclerViewSifreler.layoutManager=GridLayoutManager(this,3)
        val kategoriAdapter=KategoriAdapter(kategoriler)
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
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        //sifreleriGetir()
        super.onResume()
        if(MainActivity.AktifKullanici==null)
        {
            finish()
        }
    }
}
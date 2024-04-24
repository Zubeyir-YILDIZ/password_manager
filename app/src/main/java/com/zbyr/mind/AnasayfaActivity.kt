package com.zbyr.mind

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.zbyr.mind.databinding.ActivityAnasayfaBinding
import com.zbyr.mind.databinding.ActivityMainBinding

class AnasayfaActivity : AppCompatActivity() {
    private lateinit var bagla:ActivityAnasayfaBinding
    private lateinit var sqLiteIslemleri: SqLiteIslemleri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bagla= ActivityAnasayfaBinding.inflate(layoutInflater)
        sqLiteIslemleri=SqLiteIslemleri(this)
        setContentView(bagla.root)
        yonCubugu()
        sifreleriGetir()



    }
    fun yonCubugu()
    {
        setSupportActionBar(bagla.yon)
        bagla.floatingActionButton.setOnClickListener {
            val intent=Intent(this,SifreEkleActivity::class.java)
            startActivity(intent)
        }
    }
    fun sifreleriGetir()
    {
        var liste=sqLiteIslemleri.getirSifre(MainActivity.AktifKullanici!!)

        bagla.RecyclerViewSifreler.layoutManager=LinearLayoutManager(this)
        val sifreAdapter=SifreAdapter(liste!!)
        bagla.RecyclerViewSifreler.adapter=sifreAdapter
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
        sifreleriGetir()
        super.onResume()
        if(MainActivity.AktifKullanici==null)
        {
            finish()
        }
    }
}
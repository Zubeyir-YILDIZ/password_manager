package com.zbyr.mind

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.findNavController
import com.zbyr.mind.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object{
        var AktifKullanici:Kullanici?=null
        var sifreListesi:ArrayList<Sifre> = ArrayList<Sifre>()
    }
    private lateinit var baglan:ActivityMainBinding
    private lateinit var sqLiteIslemleri: SqLiteIslemleri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baglan= ActivityMainBinding.inflate(layoutInflater)
        setContentView(baglan.root)
        sqLiteIslemleri= SqLiteIslemleri(this)
        baglan.buttonGiris.setOnClickListener {
            var txtMail=baglan.editTextEposta
            var txtSifre=baglan.editTextSifre
            var liste=sqLiteIslemleri.getirKullanici()
            var kisi=dogrulama(liste,txtMail.text.toString(),txtSifre.text.toString())
            if(kisi!=null)
            {
                AktifKullanici=kisi
                val intent = Intent(this, AnasayfaActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this,"Eposta veya şifreniz hatalı",Toast.LENGTH_LONG).show()
            }
        }
        baglan.textViewUyeOl.setOnClickListener {
            val intent = Intent(this, UyeOlActivity::class.java)
            startActivity(intent)
        }
        baglan.textViewUnuttum.setOnClickListener {
            val inputDialog = AlertDialog.Builder(this)
            inputDialog.setTitle("Eposta adresinizi giriniz")
            val inputEditText = EditText(this)
            inputDialog.setView(inputEditText)
            inputDialog.setPositiveButton("Tamam") {dialog,which ->
                val sonuc = inputEditText.text.toString()
                if(sonuc==sqLiteIslemleri.getirKullaniciIleMail(sonuc)._kMail)
                {
                    var profil=sqLiteIslemleri.getirKullaniciIleMail(sonuc)
                    inputDialog.setTitle("Yeni şifrenizi giriniz")
                    val inputEditText2 = EditText(this)
                    inputDialog.setView(inputEditText2)
                    inputDialog.setPositiveButton("Tamam") {dialog2,which2 ->
                        val sonuc2 = inputEditText2.text.toString()
                        profil._kSifre=sonuc2
                        sqLiteIslemleri.güncelleKullanici(profil)
                    }
                    inputDialog.show()
                }else{
                    Toast.makeText(this,"Eposta adresi bulunamadı",Toast.LENGTH_LONG).show()
                }
            }
            inputDialog.show()
        }
    }
    fun dogrulama(mutableList: MutableList<Kullanici>,mail:String,sifre:String): Kullanici?
    {
        for(kisi in mutableList)
        {
            if(kisi._kMail==mail && kisi._kSifre==sifre)
            {
                return kisi
            }
        }
        return null
    }

}
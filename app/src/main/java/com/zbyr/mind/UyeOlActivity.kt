package com.zbyr.mind

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.zbyr.mind.databinding.ActivityUyeOlBinding

class UyeOlActivity : AppCompatActivity() {
    private lateinit var bagla:ActivityUyeOlBinding
    private lateinit var firebaseIslemci:FirebaseIslemleri
    private lateinit var sqLiteIslemci:SqLiteIslemleri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseIslemci=FirebaseIslemleri()
        sqLiteIslemci= SqLiteIslemleri(this)
        bagla=ActivityUyeOlBinding.inflate(layoutInflater)
        setContentView(bagla.root)


        bagla.buttonOlusturUyelik.setOnClickListener {
            var txtIsim=bagla.editTextOlusturIsim
            var txtSoyisim=bagla.editTextOlusturSoyisim
            var txtMail=bagla.editTextOlusturMail
            var txtSifre=bagla.editTextOlusturSifre
            if(txtIsim.text.isNotEmpty() && txtMail.text.isNotEmpty() && txtSifre.text.isNotEmpty() && txtSoyisim.text.isNotEmpty())
            {
                var kullanici=Kullanici(txtIsim.text.toString(),txtSoyisim.text.toString(),txtMail.text.toString(),txtSifre.text.toString())
                //firebaseIslemci.KullaniciEkle(kullanici)
                sqLiteIslemci.ekleKullanici(kullanici)
                finish()
            }else
            {
                Toast.makeText(applicationContext,"Lütfen boş alanları dolduralım", Toast.LENGTH_LONG).show()
            }


        }




    }
}
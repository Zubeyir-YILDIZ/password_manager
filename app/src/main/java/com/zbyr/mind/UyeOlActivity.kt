package com.zbyr.mind

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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

        girisAnimasyonu()
        bagla.buttonOlusturUyelik.setOnClickListener {
            var txtIsim=bagla.editTextOlusturIsim
            var txtSoyisim=bagla.editTextOlusturSoyisim
            var txtMail=bagla.editTextOlusturMail
            var txtSifre=bagla.editTextOlusturSifre
            if(txtIsim.text.isNotEmpty() && txtMail.text.isNotEmpty() && txtSifre.text.isNotEmpty() && txtSoyisim.text.isNotEmpty())
            {
                var kullanici=Kullanici(txtIsim.text.toString(),txtSoyisim.text.toString(),txtMail.text.toString(),txtSifre.text.toString())
                sqLiteIslemci.ekleKullanici(kullanici)
                finish()
            }else
            {
                Toast.makeText(applicationContext,"Lütfen boş alanları doldurunuz", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun girisAnimasyonu()
    {
        bagla.textViewBaslikUyeOl.alpha=0f
        bagla.editTextOlusturIsim.animate().rotationX(-360f).duration=1000
        bagla.editTextOlusturSoyisim.animate().rotationX(-360f).duration=1200
        bagla.editTextOlusturMail.animate().rotationX(-360f).duration=1400
        bagla.editTextOlusturSifre.animate().rotationX(-360f).duration=1600
        bagla.buttonOlusturUyelik.animate().rotationX(-360f).duration=2000
        bagla.textViewBaslikUyeOl.animate().alpha(1f).duration=3000
    }
}
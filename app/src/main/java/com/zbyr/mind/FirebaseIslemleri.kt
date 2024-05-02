package com.zbyr.mind

import android.app.Application
import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.zbyr.mind.databinding.ActivityAnasayfaBinding

class FirebaseIslemleri(var context: Context) {
    private var firebase: DatabaseReference = Firebase.database.reference
    var sqLiteIslemci=SqLiteIslemleri(context)
    fun KullaniciEkle(kullanici: Kullanici)
    {
        firebase.child("Kullanicilar").child(kullanici._kMail.toString()).setValue(kullanici)
    }
    fun SifreEkle(sifre: Sifre)
    {
        firebase.child("Sifreler").child(sifre._sKullanici!!._kMail.toString()).child(sifre._sId.toString()).setValue(sifre)
    }
    fun SifreGetir(kullanici: Kullanici)
    {
        firebase.child("Sifreler").child(kullanici._kMail.toString()).get().addOnSuccessListener {
            for(deger in it.children)
            {
                var sifre=Sifre()
                var tip=SifreTip()
                var _kullanici=Kullanici()
                for(sifreD in deger.children)
                {
                    if(sifreD.key.equals("_sHesapAdi"))
                    {
                        sifre._sHesapAdi=sifreD.value.toString()
                    }
                    if(sifreD.key.equals("_sId"))
                    {
                        var x=sifreD.value.toString()
                        sifre._sId=x.toInt()
                    }
                    if(sifreD.key.equals("_sSifre"))
                    {
                        sifre._sSifre=sifreD.value.toString()
                    }
                    if(sifreD.key.equals("_sTur"))
                    {
                        for(j in sifreD.children)
                        {
                            if(j.key.equals("_TipId"))
                                tip._TipId=j.value as Long
                            if(j.key.equals("_SifreTipi"))
                                tip._SifreTipi=j.value.toString()
                        }
                        sifre._sTur=tip
                    }
                    if(sifreD.key.equals("_sKullanici"))
                    {
                        for(j in sifreD.children)
                        {
                            if(j.key.equals("_kId"))
                                _kullanici._kId=j.value as Long
                            if(j.key.equals("_kAdi"))
                                _kullanici._kAdi=j.value.toString()
                            if(j.key.equals("_kSoyadi"))
                                _kullanici._kSoyadi=j.value.toString()
                            if(j.key.equals("_kSifre"))
                                _kullanici._kSifre=j.value.toString()
                            if(j.key.equals("_kMail"))
                                _kullanici._kMail=j.value.toString()
                        }
                        sifre._sKullanici=_kullanici
                    }
                }
                if(sqLiteIslemci.kontrolSifre(sifre))
                    sqLiteIslemci.ekleSifre(sifre)
            }
        }
    }
    fun sifreleriSil(kullanici: Kullanici)
    {
        firebase.child("Sifreler").child(kullanici._kMail.toString()).setValue(null)
    }
    fun kullaniciSil(kullanici: Kullanici)
    {
        firebase.child("Kullanicilar").child(kullanici._kMail.toString()).setValue(null)
        sifreleriSil(kullanici)
    }

}
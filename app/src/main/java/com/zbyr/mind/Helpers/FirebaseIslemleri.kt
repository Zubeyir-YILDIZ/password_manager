package com.zbyr.mind.Helpers

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.zbyr.mind.Models.Kullanici
import com.zbyr.mind.Models.Sifre
import com.zbyr.mind.Models.SifreTip

class FirebaseIslemleri(var context: Context) {
    private var firebase: DatabaseReference = Firebase.database.reference
    var sqLiteIslemci= SqLiteIslemleri(context)
    fun KullaniciEkle(kullanici: Kullanici)
    {
        firebase.child("Kullanicilar").child(kullanici._kId.toString()).setValue(kullanici)
    }
    fun SifreEkle(sifre: Sifre)
    {
        firebase.child("Sifreler").child(sifre._sKullanici!!._kId.toString()).child(sifre._sId.toString()).setValue(sifre)
    }
    fun SifreGetir(kullanici: Kullanici)
    {
        firebase.child("Sifreler").child(kullanici._kId.toString()).get().addOnSuccessListener {
            for(deger in it.children)
            {
                var sifre= Sifre()
                var tip= SifreTip()
                var _kullanici= Kullanici()
                for(sifreD in deger.children)
                {
                    if(sifreD.key.equals("_sHesapAdi"))
                    {
                        sifre._sHesapAdi= sqLiteIslemci.coz(sifreD.value.toString())
                    }
                    if(sifreD.key.equals("_sId"))
                    {
                        var x=sifreD.value.toString()
                        sifre._sId=x.toInt()
                    }
                    if(sifreD.key.equals("_sSifre"))
                    {
                        sifre._sSifre= sqLiteIslemci.coz(sifreD.value.toString())
                    }
                    if(sifreD.key.equals("_sSifreAdi"))
                    {
                        sifre._sSifreAdi= sqLiteIslemci.coz(sifreD.value.toString())
                    }
                    if(sifreD.key.equals("_sTur"))
                    {
                        for(j in sifreD.children)
                        {
                            if(j.key.equals("_TipId"))
                                tip._TipId=j.value as Long
                            if(j.key.equals("_SifreTipi"))
                                tip._SifreTipi= sqLiteIslemci.coz(j.value.toString())
                            if(j.key.equals("_kId"))
                                tip._kId=j.value.toString().toLong()
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
                                _kullanici._kAdi= sqLiteIslemci.coz(j.value.toString())
                            if(j.key.equals("_kSoyadi"))
                                _kullanici._kSoyadi= sqLiteIslemci.coz(j.value.toString())
                            if(j.key.equals("_kSifre"))
                                _kullanici._kSifre= sqLiteIslemci.coz(j.value.toString())
                            if(j.key.equals("_kMail"))
                                _kullanici._kMail= sqLiteIslemci.coz(j.value.toString())
                        }
                        sifre._sKullanici=_kullanici
                    }
                }
                if(sqLiteIslemci.kontrolKategori(sifre._sTur!!))
                {
                    sqLiteIslemci.ekleSifreTipiFirebaseden(sifre._sTur!!)
                }
                if(sqLiteIslemci.kontrolSifre(sifre))
                    sqLiteIslemci.ekleSifreFirebaseden(sifre)
            }
        }
    }
    fun sifreleriSil(kullanici: Kullanici)
    {
        firebase.child("Sifreler").child(kullanici._kId.toString()).setValue(null)
    }
    fun kullaniciSil(kullanici: Kullanici)
    {
        firebase.child("Kullanicilar").child(kullanici._kId.toString()).setValue(null)
        sifreleriSil(kullanici)
    }

}
package com.zbyr.mind

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseIslemleri {
    private var firebase: DatabaseReference = Firebase.database.reference
    fun KullaniciEkle(kullanici: Kullanici)
    {
        firebase.child("Kullanicilar").child(kullanici._kMail).setValue(kullanici)
    }
    fun SifreEkle(sifre: Sifre)
    {
        firebase.child("Sifreler").child(sifre._sKullanici.toString()).setValue(sifre)
    }
    fun KullaniciGetir():ArrayList<Kullanici>
    {
        var kullanicilar:ArrayList<Kullanici> =ArrayList<Kullanici>()
        //eksik
        return kullanicilar
    }
    fun KullaniciGetir(_kullanici: Kullanici):Kullanici
    {
        var kullanici=Kullanici()
        //eksik
        return kullanici
    }
    fun SifreleriGetir():ArrayList<Sifre>
    {
        var sifreler=ArrayList<Sifre>()
        //eksik
        return  sifreler
    }
    fun SifreGetir(_sifre: Sifre):Sifre
    {
        var sifre=Sifre()
        //eksik
        return sifre
    }
}
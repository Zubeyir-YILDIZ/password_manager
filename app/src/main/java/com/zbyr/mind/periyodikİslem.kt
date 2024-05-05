package com.zbyr.mind

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class periyodikIslem( private val context: Context,workerParams:WorkerParameters):Worker(context,workerParams)
{
    private var sqLiteIslemci=SqLiteIslemleri(context)
    private var firebaseIslemci=FirebaseIslemleri(context)
    override fun doWork(): Result {
        var firebase: DatabaseReference = Firebase.database.reference
        var list=sqLiteIslemci.aktifKullaniciGetir()
        for(kulllaniciId in list)
        {
            var kullanici=sqLiteIslemci.getirKullaniciIleId(kulllaniciId.toLong())
            var sifreler:MutableList<Sifre> = sqLiteIslemci.getirSifre(kullanici)!!
            if(sifreler.isNotEmpty())
            {
                for(sifre in sifreler)
                {
                    sifre._sSifreAdi=sqLiteIslemci.sifrele(sifre._sSifreAdi)
                    sifre._sHesapAdi=sqLiteIslemci.sifrele(sifre._sHesapAdi)
                    sifre._sKullanici?._kAdi=sqLiteIslemci.sifrele(sifre._sKullanici!!._kAdi)
                    sifre._sKullanici?._kSoyadi=sqLiteIslemci.sifrele(sifre._sKullanici!!._kSoyadi)
                    sifre._sKullanici?._kMail=sqLiteIslemci.sifrele(sifre._sKullanici!!._kMail)
                    sifre._sKullanici?._kSifre=sqLiteIslemci.sifrele(sifre._sKullanici!!._kSifre)
                    sifre._sTur?._SifreTipi=sqLiteIslemci.sifrele(sifre._sTur!!._SifreTipi)
                    sifre._sSifre=sqLiteIslemci.sifrele(sifre._sSifre)
                    firebaseIslemci.SifreEkle(sifre)
                }
            }
        }
        return Result.success()
    }
}



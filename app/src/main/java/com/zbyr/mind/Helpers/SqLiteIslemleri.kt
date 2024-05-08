package com.zbyr.mind.Helpers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.zbyr.mind.Models.Kullanici
import com.zbyr.mind.Models.Sifre
import com.zbyr.mind.Models.SifreTip
import com.zbyr.mind.R
import com.zbyr.mind.Views.MainActivity
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

const val database_adi="MindDb"

const val tablo_adi="Sifreler"
const val col_sId="SifreId"
const val col_hesapAdi="HesapAdi"
const val col_sifre="Sifre"
const val col_sifreAdi="SifreAdi"
const val col_kId="KullaniciId"
const val col_tId="TipId"

const val tablo_adi2="SifreTipi"
const val col_tip="SifreTipi"
const val col_shk="3123wrqT6534hjrtndgh8767hrtfvg45"
const val tablo_adi3="Kullanicilar"
const val col_kAdi="Isim"
const val col_kSoyadi="Soyisim"
const val col_kMail="Eposta"
const val col_kSifre="Sifre"

const val tablo_adi4="TipSifre"
private var hatali:String=""
private var basarili:String=""
private var zatenVar:String=""

class SqLiteIslemleri (var context: Context):SQLiteOpenHelper(context, database_adi,null,1){
    override fun onCreate(db: SQLiteDatabase?) {
        val olusturTablo3 = "  CREATE TABLE " + tablo_adi3 + "("+
                col_kId + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                col_kAdi + " VARCHAR(200)," +
                col_kSoyadi + " VARCHAR(50)," +
                col_kMail + " VARCHAR(200)," +
                col_kSifre + " VARCHAR(256))"
        db?.execSQL(olusturTablo3)

        val olusturTablo2 = " CREATE TABLE " + tablo_adi2 + "("+
                col_tId + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                col_tip + " VARCHAR(150), " +
                col_kId + " INTEGER, " +
                "FOREIGN KEY (" + col_kId + ") REFERENCES " + tablo_adi3 + "(" + col_kId + "))"
        db?.execSQL(olusturTablo2)

        val olusturTablo = "CREATE TABLE " + tablo_adi + "(" +
                col_sId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                col_hesapAdi + " VARCHAR(256)," +
                col_sifreAdi + " VARCHAR(250)," +
                col_sifre + " VARCHAR(256), " +
                col_tId + " INTEGER, " +
                col_kId + " INTEGER, " +
                "FOREIGN KEY (" + col_tId + ") REFERENCES " + tablo_adi2 + "(" + col_tId + "), " +
                "FOREIGN KEY (" + col_kId + ") REFERENCES " + tablo_adi3 + "(" + col_kId + "))"
        db?.execSQL(olusturTablo)

        val olusturTablo4 = " CREATE TABLE " + tablo_adi4 + "("+
                col_tId + " INTEGER," +
                col_sId + " INTEGER," + " PRIMARY KEY (" +
                col_tId + ", " + col_sId + ")," + " FOREIGN KEY " + "(" +
                col_tId + ")" + " REFERENCES " + tablo_adi2 + "(" +
                col_tId + ")," + " FOREIGN KEY " + "(" +
                col_sId + ")" + " REFERENCES " + tablo_adi + "(" +
                col_sId + "))"
        db?.execSQL(olusturTablo4)

        val olusturTablo5 = "CREATE TABLE AktifK(kId INTEGER PRIMARY KEY,kontrol INTEGER)" //yedekleme işlemleri için
        db?.execSQL(olusturTablo5)

        val olusturTablo6 = "CREATE TABLE AcikKullanici(kId INTEGER PRIMARY KEY)"
        db?.execSQL(olusturTablo6)

        hatali=context.getString(R.string.sql_hatali_ekleme_en)
        basarili=context.getString(R.string.sql_basarili_ekleme_en)
        zatenVar=context.getString(R.string.sql_zaten_var_en)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun encryptAES(text: String, secretKey: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val keySpec = SecretKeySpec(secretKey.toByteArray(), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encryptedBytes = cipher.doFinal(text.toByteArray())
        return android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT)
    }
    fun sifrele(metin:String):String
    {
        return encryptAES(metin, col_shk)
    }
    fun coz(metin: String):String
    {
        return decryptAES(metin, col_shk)
    }
    fun decryptAES(encryptedText: String, secretKey: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val keySpec = SecretKeySpec(secretKey.toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        val decryptedBytes = cipher.doFinal(android.util.Base64.decode(encryptedText, android.util.Base64.DEFAULT))
        return String(decryptedBytes)
    }


    fun ekleKullanici(kullanici: Kullanici)
    {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(col_kAdi,encryptAES(kullanici._kAdi, col_shk))
        cv.put(col_kSoyadi,encryptAES(kullanici._kSoyadi, col_shk))
        cv.put(col_kMail,encryptAES(kullanici._kMail, col_shk))
        cv.put(col_kSifre,encryptAES(kullanici._kSifre, col_shk))
        if(kontrolKullanici(kullanici))
        {
            var sonuc = db.insert(tablo_adi3,null,cv)
            if(sonuc==(-1).toLong())
            {
                Toast.makeText(context, hatali,Toast.LENGTH_SHORT).show()
            }else
            {
                Toast.makeText(context, basarili,Toast.LENGTH_SHORT).show()
            }
        }else
            Toast.makeText(context, zatenVar,Toast.LENGTH_SHORT).show()

    }
    fun ekleSifre(sifre: Sifre)
    {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(col_sifre,encryptAES(sifre._sSifre, col_shk))
        cv.put(col_kId,sifre._sKullanici?._kId)
        cv.put(col_tId,sifre._sTur?._TipId)
        cv.put(col_sifreAdi,encryptAES(sifre._sSifreAdi, col_shk))
        if(sifre._sHesapAdi.isNotEmpty())
            cv.put(col_hesapAdi,encryptAES(sifre._sHesapAdi, col_shk))

        val sonuc = db.insert(tablo_adi,null,cv)

        if(sonuc==(-1).toLong())
        {
            Toast.makeText(context, hatali,Toast.LENGTH_SHORT).show()
        }else
        {
            Toast.makeText(context, basarili,Toast.LENGTH_SHORT).show()
        }
    }
    fun ekleSifreFirebaseden(sifre: Sifre)
    {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(col_sId,sifre._sId)
        cv.put(col_sifre,encryptAES(sifre._sSifre, col_shk))
        cv.put(col_kId,sifre._sKullanici?._kId)
        cv.put(col_tId,sifre._sTur?._TipId)
        cv.put(col_sifreAdi,encryptAES(sifre._sSifreAdi, col_shk))
        if(sifre._sHesapAdi.isNotEmpty())
            cv.put(col_hesapAdi,encryptAES(sifre._sHesapAdi, col_shk))

        val sonuc = db.insert(tablo_adi,null,cv)

        if(sonuc==(-1).toLong())
        {
            Toast.makeText(context, hatali,Toast.LENGTH_SHORT).show()
        }else
        {
            Toast.makeText(context, basarili,Toast.LENGTH_SHORT).show()
        }
    }
    fun ekleSifreTipi(sifreTip: SifreTip, kullanici: Kullanici)
    {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(col_tip,encryptAES(sifreTip._SifreTipi, col_shk))
        cv.put(col_kId,kullanici._kId)

        var sonuc = db.insert(tablo_adi2,null,cv)
        if(sonuc==(-1).toLong())
        {
            Toast.makeText(context, hatali,Toast.LENGTH_SHORT).show()
        }else
        {
            Toast.makeText(context, basarili,Toast.LENGTH_SHORT).show()
        }
    }
    fun kontrolSifre(sifre: Sifre):Boolean
    {
        var sonuc=true
        var liste=getirSifre(MainActivity.AktifKullanici!!)
        if (liste != null) {
            for(_sifre in liste)
            {
                if(_sifre._sId == sifre._sId)
                    sonuc=false
            }
        }
        return sonuc
    }
    fun ekleSifreTipiFirebaseden(sifreTip: SifreTip)
    {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(col_tId,sifreTip._TipId)
        cv.put(col_tip,encryptAES(sifreTip._SifreTipi, col_shk))
        cv.put(col_kId,sifreTip._kId)

        var sonuc = db.insert(tablo_adi2,null,cv)
        if(sonuc==(-1).toLong())
        {
            Toast.makeText(context, hatali,Toast.LENGTH_SHORT).show()
        }else
        {
            Toast.makeText(context, basarili,Toast.LENGTH_SHORT).show()
        }
    }
    fun kontrolKategori(sifreTip: SifreTip):Boolean
    {
        var liste=getirSifreTip()
        for(list in liste)
        {
            if(list._TipId==sifreTip._TipId && list._kId==sifreTip._kId && list._SifreTipi==sifreTip._SifreTipi)
            {
                return false
            }
        }
        return true
    }
    fun kontrolKullanici(kullanici1: Kullanici):Boolean
    {
        var mail=encryptAES(kullanici1._kMail, col_shk)
        var liste:MutableList<Kullanici> = ArrayList()
        val db=this.readableDatabase
        var sorgu = "SELECT * FROM "+ tablo_adi3 + " WHERE $col_kMail = '$mail'"
        var sonuc=db.rawQuery(sorgu,null)
        var kullanici= Kullanici()
        if(sonuc.moveToFirst())
        {
            kullanici._kId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kId)).toLong()
            kullanici._kAdi= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_kAdi)).toString(), col_shk)
            kullanici._kSoyadi= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_kSoyadi)).toString(), col_shk)
            kullanici._kMail= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_kMail)).toString(), col_shk)
            kullanici._kSifre= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_kSifre)).toString(), col_shk)
        }
        else
            return true
        sonuc.close()
        db.close()
        return false
    }
    fun getirKullanici():MutableList<Kullanici>
    {
        var liste:MutableList<Kullanici> = ArrayList()
        val db=this.readableDatabase
        var sorgu = "SELECT * FROM "+ tablo_adi3
        var sonuc=db.rawQuery(sorgu,null)
        if(sonuc.moveToFirst())
        {
            do {
                var kullanici= Kullanici()
                kullanici._kId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kId)).toLong()
                kullanici._kAdi= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_kAdi)).toString(), col_shk)
                kullanici._kSoyadi= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(
                    col_kSoyadi
                )).toString(), col_shk
                )
                kullanici._kMail= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_kMail)).toString(), col_shk)
                kullanici._kSifre= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_kSifre)).toString(), col_shk)
                liste.add(kullanici)
            }while (sonuc.moveToNext())
        }
        sonuc.close()
        db.close()
        return liste
    }
    fun getirKullaniciIleId(id:Long): Kullanici
    {
        var kullanici= Kullanici()
        val db=this.readableDatabase
        var sorgu = "SELECT * FROM "+ tablo_adi3 + " WHERE $col_kId=$id"
        var sonuc=db.rawQuery(sorgu,null)
        if(sonuc.moveToFirst())
        {
            do {
                kullanici._kId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kId)).toLong()
                kullanici._kAdi= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_kAdi)).toString(), col_shk)
                kullanici._kSoyadi= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(
                    col_kSoyadi
                )).toString(), col_shk
                )
                kullanici._kMail= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_kMail)).toString(), col_shk)
                kullanici._kSifre= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_kSifre)).toString(), col_shk)
            }while (sonuc.moveToNext())
        }
        sonuc.close()
        db.close()
        return kullanici
    }
    fun getirSifre(kullanici: Kullanici):ArrayList<Sifre>?
    {
        var s= encryptAES(kullanici._kMail, col_shk)
        var liste:ArrayList<Sifre> = ArrayList()
        val db=this.readableDatabase
        var sorgu = "SELECT s.SifreId,s.Sifre,s.HesapAdi,s.SifreAdi,s.TipId,s.KullaniciId,t.TipId,t.SifreTipi FROM "+ tablo_adi +
                " AS s JOIN "+ tablo_adi2 + " AS t ON s.TipId=t.TipId Where s.KullaniciId=(SELECT k.KullaniciId FROM Kullanicilar AS k WHERE k.Eposta='$s')"
        var sonuc=db.rawQuery(sorgu,null)
        if(sonuc.moveToFirst())
        {
            do {
                var sifre= Sifre()
                sifre._sId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_sId)).toInt()
                sifre._sSifre= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_sifre)), col_shk)
                sifre._sHesapAdi= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(
                    col_hesapAdi
                )), col_shk
                )
                sifre._sSifreAdi= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(
                    col_sifreAdi
                )), col_shk
                )
                var sifreTip= SifreTip()
                sifreTip._TipId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_tId)).toLong()
                sifreTip._SifreTipi= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_tip)), col_shk)
                sifreTip._kId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kId)).toLong()
                sifre._sTur=sifreTip
                sifre._sKullanici=kullanici

                liste.add(sifre)
            }while (sonuc.moveToNext())
        }
        sonuc.close()
        db.close()
        return liste
    }
    fun acKullanici(kullanici: Kullanici)
    {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put("kId",kullanici._kId)

        var sonuc = db.insert("AcikKullanici",null,cv)
        if(sonuc==(-1).toLong())
        {
            Toast.makeText(context, hatali,Toast.LENGTH_SHORT).show()
        }else
        {
            Toast.makeText(context, basarili,Toast.LENGTH_SHORT).show()
        }
    }
    fun acikKullaniciGetir():String
    {
        var st:String=""
        val db=this.readableDatabase
        var sorgu = "SELECT * FROM AcikKullanici"
        var sonuc=db.rawQuery(sorgu,null)
        if(sonuc.moveToFirst())
        {
            do {
                st=(sonuc.getString(sonuc.getColumnIndexOrThrow("kId")))
            }while (sonuc.moveToNext())
        }
        sonuc.close()
        db.close()
        return st
    }
    fun kapatKullanici()
    {
        val db=this.writableDatabase
        var sonuc=db.delete("AcikKullanici",null, null)
        db.close()
    }
    fun aktifKullaniciEkle(kullanici: Kullanici, kontrol:Int)
    {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put("kId",kullanici._kId)
        cv.put("kontrol",kontrol)

        var sonuc = db.insert("AktifK",null,cv)
        if(sonuc==(-1).toLong())
        {
            Toast.makeText(context, hatali,Toast.LENGTH_SHORT).show()
        }else
        {
            Toast.makeText(context, basarili,Toast.LENGTH_SHORT).show()
        }
    }
    fun aktifKullaniciSil(kullanici: Kullanici)
    {
        val db=this.writableDatabase
        var sonuc=db.delete("AktifK","kId=? ", arrayOf(kullanici._kId.toString()))
        db.close()
    }
    fun aktifKullaniciGetir():ArrayList<String>
    {
        var liste:ArrayList<String> = ArrayList()
        val db=this.readableDatabase
        var sorgu = "SELECT * FROM AktifK"
        var sonuc=db.rawQuery(sorgu,null)
        if(sonuc.moveToFirst())
        {
            do {
                liste.add(sonuc.getString(sonuc.getColumnIndexOrThrow("kId")))
            }while (sonuc.moveToNext())
        }
        sonuc.close()
        db.close()
        return liste
    }
    fun getirSifreTip():MutableList<SifreTip>
    {
        var liste:MutableList<SifreTip> = ArrayList()
        val db = this.readableDatabase
        var sorgu = "SELECT * FROM "+ tablo_adi2
        var sonuc =  db.rawQuery(sorgu,null)
        if(sonuc.moveToFirst())
        {
            do{
                var sifreTip= SifreTip()
                sifreTip._TipId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_tId)).toLong()
                sifreTip._SifreTipi= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_tip)).toString(), col_shk)
                sifreTip._kId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kId)).toLong()
                liste.add(sifreTip)
            }while (sonuc.moveToNext())
        }
        sonuc.close()
        db.close()
        return liste
    }
    fun getirSifreTipIleId(tip:Long): SifreTip
    {
        var sifreTip= SifreTip()
        val db = this.readableDatabase
        var sorgu = "SELECT * FROM $tablo_adi2 WHERE $col_tId=$tip"
        var sonuc =  db.rawQuery(sorgu,null)

        if(sonuc.moveToFirst())
        {
            sifreTip._TipId=sonuc.getLong(sonuc.getColumnIndexOrThrow(col_tId))
            sifreTip._SifreTipi= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_tip)).toString(), col_shk)
            sifreTip._kId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kId)).toLong()
        }

        sonuc.close()
        db.close()
        return sifreTip
    }
    fun güncelleKullanici(kullanici: Kullanici)
    {
        val db=this.readableDatabase
        var sorgu = "SELECT * FROM "+ tablo_adi3
        var sonuc=db.rawQuery(sorgu,null)
        if(sonuc.moveToFirst())
        {
            do {
                var cv=ContentValues()
                cv.put(col_kAdi,encryptAES(kullanici._kAdi, col_shk))
                cv.put(col_kSoyadi, encryptAES(kullanici._kSoyadi, col_shk))
                cv.put(col_kMail,encryptAES(kullanici._kMail, col_shk))
                cv.put(col_kSifre,encryptAES(kullanici._kSifre, col_shk))
                db.update(
                    tablo_adi3,cv,"$col_kId=? ",
                    arrayOf(kullanici._kId.toString())) //!!!!!!!!!!!!!!!!!!!!!!
            }while (sonuc.moveToNext())
        }
        sonuc.close()
        db.close()
    }
    fun güncelleSifre(sifre: Sifre)
    {
        val db=this.readableDatabase
        val sorgu="SELECT * FROM "+ tablo_adi
        val sonuc=db.rawQuery(sorgu,null)
        if(sonuc.moveToFirst())
        {
            do {
                var cv=ContentValues()
                cv.put(col_sId,sifre._sId)
                cv.put(col_sifre,encryptAES(sifre._sSifre, col_shk))
                cv.put(col_sifreAdi,encryptAES(sifre._sSifreAdi, col_shk))
                cv.put(col_tId, sifre._sTur!!._TipId)
                cv.put(col_kId,sifre._sKullanici!!._kId)
                cv.put(col_hesapAdi,encryptAES(sifre._sHesapAdi, col_shk))
                db.update(
                    tablo_adi,cv,"$col_sId=?",
                    arrayOf(sifre._sId.toString()))
            }while (sonuc.moveToNext())
        }
        sonuc.close()
        db.close()
    }
    fun getirKullaniciIleMail(mail:String): Kullanici
    {
        var kullanici= Kullanici()
        val db=this.readableDatabase
        var sorgu = "SELECT * FROM "+ tablo_adi3 +" WHERE $col_kMail= '${encryptAES(mail, col_shk)}'"
        var sonuc=db.rawQuery(sorgu,null)
        if(sonuc.moveToFirst())
        {
            kullanici._kId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kId)).toLong()
            kullanici._kAdi= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_kAdi)).toString(), col_shk)
            kullanici._kSoyadi= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_kSoyadi)).toString(), col_shk)
            kullanici._kMail= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_kMail)).toString(), col_shk)
            kullanici._kSifre= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_kSifre)).toString(), col_shk)
        }
        sonuc.close()
        db.close()
        return kullanici
    }
    fun silKullanici(mail: String):Boolean
    {
        var kullanici=getirKullaniciIleMail(mail)
        val db=this.writableDatabase
        var sonuc=db.delete(tablo_adi3,"$col_kMail='?'", arrayOf(encryptAES(kullanici._kMail, col_shk)))
        db.close()

        return sonuc !=-1
    }
    fun silTip(tip: SifreTip):Boolean
    {
        var sifre=getirSifreTipIleId(tip._TipId)
        val db=this.writableDatabase
        var sonuc=db.delete(tablo_adi2,"$col_tId=?", arrayOf(sifre._TipId.toString()))
        db.close()

        return sonuc !=-1
    }
    fun silSifre(sifre: Sifre)
    {
        val db=this.writableDatabase
        var sonuc=db.delete(tablo_adi,"$col_sId=?", arrayOf(sifre._sId.toString()))
        db.close()
    }
    fun silSifreler(sifreler:ArrayList<Sifre>)
    {
        for(sifre in sifreler)
            silSifre(sifre)
    }
    fun tipKarsilastir(tip:String,kullanici: Kullanici):Boolean
    {
        var liste=tipGetirkIdIle(kullanici._kId)
        for(_tip in liste)
        {
            if(_tip._SifreTipi==tip)
            {
                return false
            }
        }
        return true
    }
    fun tipGetirkIdIle(kId:Long):ArrayList<SifreTip>
    {
        var liste:ArrayList<SifreTip> = ArrayList()
        val db = this.readableDatabase
        var sorgu = "SELECT * FROM "+ tablo_adi2 +" WHERE $col_kId = "+kId
        var sonuc =  db.rawQuery(sorgu,null)
        if(sonuc.moveToFirst())
        {
            do{
                var sifreTip= SifreTip()
                sifreTip._TipId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_tId)).toLong()
                sifreTip._SifreTipi= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_tip)).toString(), col_shk)
                sifreTip._kId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kId)).toLong()
                liste.add(sifreTip)
            }while (sonuc.moveToNext())
        }
        sonuc.close()
        db.close()
        return liste
    }
    fun tipDogrula(tip:String,kullanici: Kullanici): SifreTip?
    {
        var liste=tipGetirkIdIle(kullanici._kId)
        for(_tip in liste)
        {
            if(_tip._TipId==tip.toLong())
                return _tip
        }
        return null
    }
    fun getirSifrelerTipIle(sifreTip: String,kullanici: Kullanici):ArrayList<Sifre>
    {
        var liste=ArrayList<Sifre>()
        val db = this.readableDatabase
        var sorgu = "SELECT * FROM $tablo_adi WHERE $col_tId= "+sifreTip +" AND $col_kId= "+kullanici._kId
        var sonuc =  db.rawQuery(sorgu,null)

        if(sonuc.moveToFirst())
        {
            do {
                var sifre= Sifre()
                sifre._sId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_sId)).toInt()
                sifre._sSifre= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(col_sifre)), col_shk)
                sifre._sHesapAdi= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(
                    col_hesapAdi
                )), col_shk
                )
                sifre._sSifreAdi= decryptAES(sonuc.getString(sonuc.getColumnIndexOrThrow(
                    col_sifreAdi
                )), col_shk
                )
                sifre._sTur=getirSifreTipIleId(sifreTip.toLong())
                sifre._sKullanici=kullanici

                liste.add(sifre)
            }while (sonuc.moveToNext())
        }
        sonuc.close()
        db.close()
        return liste
    }


}
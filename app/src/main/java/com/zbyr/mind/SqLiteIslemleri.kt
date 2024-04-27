package com.zbyr.mind

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

const val database_adi="MindDb"

const val tablo_adi="Sifreler"
const val col_sId="SifreId"
const val col_hesapAdi="HesapAdi"
const val col_sifre="Sifre"
const val col_kId="KullaniciId"
const val col_tId="TipId"

const val tablo_adi2="SifreTipi"
const val col_tip="SifreTipi"

const val tablo_adi3="Kullanicilar"
const val col_kAdi="Isim"
const val col_kSoyadi="Soyisim"
const val col_kMail="Eposta"
const val col_kSifre="Sifre"

const val tablo_adi4="TipSifre"

class SqLiteIslemleri (var context: Context):SQLiteOpenHelper(context, database_adi,null,1){
    override fun onCreate(db: SQLiteDatabase?) {

        val olusturTablo2 = " CREATE TABLE " + tablo_adi2 + "("+
                col_tId + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                col_tip + " VARCHAR(150))"
        db?.execSQL(olusturTablo2)

        val olusturTablo3 = "  CREATE TABLE " + tablo_adi3 + "("+
                col_kId + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                col_kAdi + " VARCHAR(200)," +
                col_kSoyadi + " VARCHAR(50)," +
                col_kMail + " VARCHAR(200)," +
                col_kSifre + " VARCHAR(256))"
        db?.execSQL(olusturTablo3)

        val olusturTablo = "CREATE TABLE " + tablo_adi + "(" +
                col_sId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                col_hesapAdi + "VARCHAR(256)," +
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

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun ekleKullanici(kullanici: Kullanici)
    {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(col_kAdi,kullanici._kAdi)
        cv.put(col_kSoyadi,kullanici._kSoyadi)
        cv.put(col_kMail,kullanici._kMail)
        cv.put(col_kSifre,kullanici._kSifre)
        if(kontrolKullanici(kullanici))
        {
            var sonuc = db.insert(tablo_adi3,null,cv)
            if(sonuc==(-1).toLong())
            {
                Toast.makeText(context,"Hatalı ekleme",Toast.LENGTH_SHORT).show()
            }else
            {
                Toast.makeText(context,"Kayıt başarılı",Toast.LENGTH_SHORT).show()
            }
        }else
            Toast.makeText(context,"Kayıt zaten var",Toast.LENGTH_SHORT).show()

    }
    fun ekleSifre(sifre: Sifre)
    {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(col_sifre,sifre._sSifre)
        cv.put(col_kId,sifre._sKullanici?._kId)
        cv.put(col_tId,sifre._sTur?._TipId)
        if(sifre._sHesapAdi.isNotEmpty())
            cv.put(col_hesapAdi,sifre._sHesapAdi)

        val sonuc = db.insert(tablo_adi,null,cv)

        if(sonuc==(-1).toLong())
        {
            Toast.makeText(context,"Hatalı ekleme",Toast.LENGTH_SHORT).show()
        }else
        {
            Toast.makeText(context,"Kayıt başarılı",Toast.LENGTH_SHORT).show()
        }
    }
    fun ekleSifreTipi(sifreTip: SifreTip)
    {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(col_tip,sifreTip._SifreTipi)

        var sonuc = db.insert(tablo_adi2,null,cv)
        if(sonuc==(-1).toLong())
        {
            Toast.makeText(context,"Hatalı ekleme",Toast.LENGTH_SHORT).show()
        }else
        {
            Toast.makeText(context,"Kayıt başarılı",Toast.LENGTH_SHORT).show()
        }
    }
    fun kontrolSifre(sifre: Sifre):Boolean
    {
        var donus=true
        var sifreler=getirSifre(sifre._sKullanici!!)
        if (sifreler != null) {
            for(_sifre in sifreler)
            {
                if(_sifre._sId==sifre._sId && _sifre._sKullanici!!._kId==sifre._sKullanici!!._kId)
                    donus=false
            }
        }

        return donus
    }
    fun kontrolKullanici(kullanici1: Kullanici):Boolean
    {
        var mail=kullanici1._kMail
        var liste:MutableList<Kullanici> = ArrayList()
        val db=this.readableDatabase
        var sorgu = "SELECT * FROM "+ tablo_adi3 + " WHERE $col_kMail = '$mail'"
        var sonuc=db.rawQuery(sorgu,null)
        var kullanici=Kullanici()
        if(sonuc.moveToFirst())
        {
            kullanici._kId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kId)).toLong()
            kullanici._kAdi=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kAdi)).toString()
            kullanici._kSoyadi=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kSoyadi)).toString()
            kullanici._kMail=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kMail)).toString()
            kullanici._kSifre=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kSifre)).toString()
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
                var kullanici=Kullanici()
                kullanici._kId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kId)).toLong()
                kullanici._kAdi=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kAdi)).toString()
                kullanici._kSoyadi=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kSoyadi)).toString()
                kullanici._kMail=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kMail)).toString()
                kullanici._kSifre=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kSifre)).toString()
                liste.add(kullanici)
            }while (sonuc.moveToNext())
        }
        sonuc.close()
        db.close()
        return liste
    }
    fun getirSifre(kullanici: Kullanici):ArrayList<Sifre>?
    {
        var s=kullanici._kMail
        var liste:ArrayList<Sifre> = ArrayList()
        val db=this.readableDatabase
        var sorgu = "SELECT s.SifreId,s.Sifre,s.TipId,s.KullaniciId,t.TipId,t.SifreTipi FROM "+ tablo_adi+
                " AS s JOIN "+ tablo_adi2 + " AS t ON s.TipId=t.TipId Where s.KullaniciId=(SELECT k.KullaniciId FROM Kullanicilar AS k WHERE k.Eposta='$s')"
        var sonuc=db.rawQuery(sorgu,null)
        if(sonuc.moveToFirst())
        {
            do {
                var sifre=Sifre()
                sifre._sId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_sId)).toInt()
                sifre._sSifre=sonuc.getString(sonuc.getColumnIndexOrThrow(col_sifre))
                var sifreTip=SifreTip()
                sifreTip._TipId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_tId)).toLong()
                sifreTip._SifreTipi=sonuc.getString(sonuc.getColumnIndexOrThrow(col_tip))
                sifre._sTur=sifreTip
                sifre._sKullanici=kullanici

                liste.add(sifre)
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
                var sifreTip=SifreTip()
                sifreTip._TipId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_tId)).toLong()
                sifreTip._SifreTipi=sonuc.getString(sonuc.getColumnIndexOrThrow(col_tip)).toString()
                liste.add(sifreTip)
            }while (sonuc.moveToNext())
        }
        sonuc.close()
        db.close()
        return liste
    }
    fun getirSifreTipIleId(tip:Long):SifreTip
    {
        var sifreTip=SifreTip()
        val db = this.readableDatabase
        var sorgu = "SELECT * FROM $tablo_adi2 WHERE $col_tId=$tip"
        var sonuc =  db.rawQuery(sorgu,null)

        if(sonuc.moveToFirst())
        {
            sifreTip._TipId=sonuc.getLong(sonuc.getColumnIndexOrThrow(col_tId))
            sifreTip._SifreTipi=sonuc.getString(sonuc.getColumnIndexOrThrow(col_tip)).toString()
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
                cv.put(col_kAdi,kullanici._kAdi)
                cv.put(col_kSoyadi,kullanici._kSoyadi)
                cv.put(col_kMail,kullanici._kMail)
                cv.put(col_kSifre,kullanici._kSifre)
                db.update(tablo_adi3,cv,"$col_kId=? AND $col_kMail='${kullanici._kMail}'",
                    arrayOf(kullanici._kId.toString()))
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
                cv.put(col_sifre,sifre._sSifre)
                cv.put(col_tId, sifre._sTur!!._TipId)
                cv.put(col_kId,sifre._sKullanici!!._kId)
                db.update(tablo_adi,cv,"$col_sId=?",
                    arrayOf(sifre._sId.toString()))
            }while (sonuc.moveToNext())
        }
        sonuc.close()
        db.close()
    }
    fun getirKullaniciIleMail(mail:String):Kullanici
    {
        var kullanici=Kullanici()
        val db=this.readableDatabase
        var sorgu = "SELECT * FROM "+ tablo_adi3+" WHERE $col_kMail= '$mail'"
        var sonuc=db.rawQuery(sorgu,null)
        if(sonuc.moveToFirst())
        {
            kullanici._kId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kId)).toLong()
            kullanici._kAdi=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kAdi)).toString()
            kullanici._kSoyadi=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kSoyadi)).toString()
            kullanici._kMail=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kMail)).toString()
            kullanici._kSifre=sonuc.getString(sonuc.getColumnIndexOrThrow(col_kSifre)).toString()
        }
        sonuc.close()
        db.close()
        return kullanici
    }
    fun silKullanici(mail: String):Boolean
    {
        var kullanici=getirKullaniciIleMail(mail)
        val db=this.writableDatabase
        var sonuc=db.delete(tablo_adi3,"$col_kMail='?'", arrayOf(kullanici._kMail))
        db.close()

        return sonuc !=-1
    }
    fun silTip(tip:SifreTip):Boolean
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
    fun tipKarsilastir(tip:String):Boolean
    {
        var liste=getirSifreTip()
        for(_tip in liste)
        {
            if(_tip._SifreTipi==tip)
            {
                return false
            }
        }
        return true
    }
    fun tipDogrula(tip:String):SifreTip?
    {
        var liste=getirSifreTip()
        for(_tip in liste)
        {
            if(_tip._SifreTipi==tip)
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
                var sifre=Sifre()
                sifre._sId=sonuc.getString(sonuc.getColumnIndexOrThrow(col_sId)).toInt()
                sifre._sSifre=sonuc.getString(sonuc.getColumnIndexOrThrow(col_sifre))
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
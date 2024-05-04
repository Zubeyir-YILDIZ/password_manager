package com.zbyr.mind

class Sifre {
    var _sId:Int=0
    var _sSifre:String=""
    var _sTur:SifreTip?=null
    var _sKullanici: Kullanici? =null
    var _sHesapAdi:String="-"
    var _sSifreAdi:String=""

    constructor(sSifre: String,sTur:SifreTip,sKullanici: Kullanici,sSifreAdi:String)
    {
        this._sSifre=sSifre
        this._sTur=sTur
        this._sKullanici=sKullanici
        this._sSifreAdi=sSifreAdi
    }
    constructor()
    {

    }
    constructor(sSifre: String,sTur:SifreTip,sKullanici: Kullanici,sHesapAdi:String,sSifreAdi: String)
    {
        this._sSifre=sSifre
        this._sTur=sTur
        this._sKullanici=sKullanici
        this._sHesapAdi=sHesapAdi
        this._sSifreAdi=sSifreAdi
    }
}
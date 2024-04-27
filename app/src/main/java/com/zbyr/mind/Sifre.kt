package com.zbyr.mind

class Sifre {
    var _sId:Int=0
    var _sSifre:String=""
    var _sTur:SifreTip?=null
    var _sKullanici: Kullanici? =null
    var _sHesapAdi:String=""

    constructor(sSifre: String,sTur:SifreTip,sKullanici: Kullanici)
    {
        this._sSifre=sSifre
        this._sTur=sTur
        this._sKullanici=sKullanici
    }
    constructor()
    {

    }
    constructor(sSifre: String,sTur:SifreTip,sKullanici: Kullanici,sHesapAdi:String)
    {
        this._sSifre=sSifre
        this._sTur=sTur
        this._sKullanici=sKullanici
        this._sHesapAdi=sHesapAdi
    }
}
package com.zbyr.mind

class Sifre {
    var _sId:Int=0
    var _sSifre:String=""
    var _sTur:SifreTip?=null
    var _sKullanici: Kullanici? =null

    constructor(sId:Int,sSifre: String,sTur:SifreTip,sKullanici: Kullanici)
    {
        this._sId=sId
        this._sSifre=sSifre
        this._sTur=sTur
        this._sKullanici=sKullanici
    }
    constructor()
    {

    }
}
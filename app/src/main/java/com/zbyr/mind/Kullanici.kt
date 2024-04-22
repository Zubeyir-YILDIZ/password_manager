package com.zbyr.mind

class Kullanici {
    var _kId:Long=0
    var _kAdi:String=""
    var _kSoyadi:String=""
    var _kMail:String=""
    var _kSifre:String=""

    constructor(kId: Long,kAdi: String,kSoyadi:String,kMail:String,kSifre:String)
    {
        this._kId=kId
        this._kAdi=kAdi
        this._kSoyadi=kSoyadi
        this._kMail=kMail
        this._kSifre=kSifre
    }
    constructor()
    {

    }
}
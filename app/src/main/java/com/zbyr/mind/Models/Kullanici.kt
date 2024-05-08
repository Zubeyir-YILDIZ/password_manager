package com.zbyr.mind.Models

class Kullanici {
    var _kId:Long=0
    var _kAdi:String=""
    var _kSoyadi:String=""
    var _kMail:String=""
    var _kSifre:String=""

    constructor(kAdi: String,kSoyadi:String,kMail:String,kSifre:String)
    {
        this._kAdi=kAdi
        this._kSoyadi=kSoyadi
        this._kMail=kMail
        this._kSifre=kSifre
    }
    constructor()
    {

    }
}
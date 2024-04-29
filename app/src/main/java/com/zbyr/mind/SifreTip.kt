package com.zbyr.mind

class SifreTip {
    var _TipId:Long=0
    var _SifreTipi:String=""
    var _kId:Long=0

    constructor(sifreTipi:String,kId:Long)
    {
        this._SifreTipi=sifreTipi
        this._kId=kId
    }
    constructor()
    {

    }
}
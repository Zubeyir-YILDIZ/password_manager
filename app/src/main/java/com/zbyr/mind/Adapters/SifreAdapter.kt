package com.zbyr.mind.Adapters

import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.zbyr.mind.Helpers.SqLiteIslemleri
import com.zbyr.mind.Models.Sifre
import com.zbyr.mind.Models.SifreTip
import com.zbyr.mind.R
import com.zbyr.mind.Views.AnasayfaActivity
import com.zbyr.mind.Views.MainActivity
import com.zbyr.mind.databinding.RecyclerItemBinding

class SifreAdapter(val sifreler:ArrayList<Sifre>) :RecyclerView.Adapter<SifreAdapter.SifreHolder>() {

    private lateinit var bagla:RecyclerItemBinding
    private lateinit var kAdi:String
    private lateinit var sifreAdi:String
    private lateinit var _Bsifre:String
    private lateinit var _tik:String
    private lateinit var girdiAl:String
    private lateinit var onay:String
    private lateinit var layoutInflater: LayoutInflater
    class SifreHolder(val binding: RecyclerItemBinding) : RecyclerView.ViewHolder(binding.root)
    {

    }
    private lateinit var sqLiteIslemleri: SqLiteIslemleri
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SifreHolder {
        bagla = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        layoutInflater = LayoutInflater.from(parent.context)
        sqLiteIslemleri= SqLiteIslemleri(bagla.root.context)
        kAdi=parent.context.getString(R.string.rec_item_kullanici_adi_en)
        sifreAdi=parent.context.getString(R.string.rec_item_sifre_adi_en)
        _Bsifre=parent.context.getString(R.string.rec_item_sifre_en)
        _tik=parent.context.getString(R.string.adapter_tiklayiniz_en)
        girdiAl=parent.context.getString(R.string.adapter_yeni_sifre_en)
        onay=parent.context.getString(R.string.popup_buton_tamam_en)

        return SifreHolder(bagla)
    }
    override fun getItemCount(): Int {
        return sifreler.count()
    }
    override fun onBindViewHolder(holder: SifreHolder, position: Int) {
        holder.binding.textViewRecItem.visibility=View.GONE
        holder.binding.textViewRecItem2.visibility=View.GONE
        if(sifreler.get(position)._sHesapAdi!="-")
            holder.binding.textViewRecItem2.setText("$kAdi  ${sifreler.get(position)._sHesapAdi}")
        else
            holder.binding.textViewRecItem2.visibility=View.GONE

        holder.binding.textViewRecItem.setText("$_Bsifre  ${sifreler.get(position)._sSifre}")
        holder.binding.textViewRecItem3.setText("${sifreler.get(position)._sSifreAdi}\n$_tik")

        holder.binding.constraintLayout.setOnClickListener {
            if(holder.binding.textViewRecItem3.visibility==View.VISIBLE)
            {
                holder.binding.textViewRecItem3.visibility=View.GONE
                holder.binding.textViewRecItem.visibility=View.VISIBLE
                if(holder.binding.textViewRecItem2.text.isNotEmpty())
                    holder.binding.textViewRecItem2.visibility=View.VISIBLE
                holder.binding.constraintLayout.layoutParams.height=ViewGroup.LayoutParams.MATCH_PARENT

            }else
            {
                holder.binding.textViewRecItem3.visibility=View.VISIBLE
                holder.binding.textViewRecItem.visibility=View.GONE
                holder.binding.textViewRecItem2.visibility=View.GONE
                holder.binding.constraintLayout.layoutParams.height=ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }
        holder.binding.textViewRecItem.setOnClickListener {
            var sifre=holder.binding.textViewRecItem.text.split("  ")[1]
            panoyaKopyala(sifre)
        }
        holder.binding.textViewRecItem2.setOnClickListener {
            var hesapBilgisi=holder.binding.textViewRecItem2.text.split("  ")[1]
            panoyaKopyala(hesapBilgisi)
        }
        holder.binding.floatingActionButtonSil.setOnClickListener {
            sqLiteIslemleri.silSifre(sifreler.get(position))

            (holder.itemView.context as AnasayfaActivity).sifreleriGetir(sifreler.get(position)._sTur?._TipId.toString())
        }
        holder.binding.floatingActionButtonDuzenle.setOnClickListener {
            popupGirdi(girdiAl,layoutInflater,position)
        }
    }
    private fun panoyaKopyala(text: String) {
        val pano = bagla.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val metin = android.content.ClipData.newPlainText(_Bsifre, text)
        pano.setPrimaryClip(metin)
    }
    fun popupGirdi(metin:String,li:LayoutInflater,position:Int)
    {
        val inflater: LayoutInflater = li
        val popupView = inflater.inflate(R.layout.popup_layout,null)

        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true)

        val txtGirdi=popupView.findViewById<EditText>(R.id.editTextPopupİstek)
        txtGirdi.setHint(metin)
        txtGirdi.requestFocus()
        popupWindow.showAtLocation(bagla.root, Gravity.CENTER, 0, 0)
        val buton=popupView.findViewById<Button>(R.id.buttonPopupGonder)
        buton.setOnClickListener {
            val sonuc=txtGirdi.text.toString()
            if(sonuc.isNotEmpty())
            {
                var sifre= Sifre()
                sifre._sHesapAdi=sifreler.get(position)._sHesapAdi
                sifre._sSifreAdi=sifreler.get(position)._sSifreAdi
                sifre._sId=sifreler.get(position)._sId
                sifre._sSifre=sonuc
                sifre._sTur=sifreler.get(position)._sTur
                sifre._sKullanici=sifreler.get(position)._sKullanici
                sifreler[position]=sifre
                sqLiteIslemleri.güncelleSifre(sifre)
            }
            (bagla.root.context as AnasayfaActivity).sifreleriGetir(sifreler.get(position)._sTur?._TipId.toString())
            popupWindow.dismiss()
        }
    }
}
package com.zbyr.mind

import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.zbyr.mind.databinding.RecyclerItemBinding

class SifreAdapter(val sifreler:ArrayList<Sifre>) :RecyclerView.Adapter<SifreAdapter.SifreHolder>() {

    private lateinit var bagla:RecyclerItemBinding
    class SifreHolder(val binding: RecyclerItemBinding) : RecyclerView.ViewHolder(binding.root)
    {

    }
    private lateinit var sqLiteIslemleri: SqLiteIslemleri
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SifreHolder {
        bagla = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        sqLiteIslemleri=SqLiteIslemleri(bagla.root.context)
        return SifreHolder(bagla)
    }
    override fun getItemCount(): Int {
        return sifreler.count()
    }
    override fun onBindViewHolder(holder: SifreHolder, position: Int) {
        holder.binding.textViewRecItem.visibility=View.GONE
        holder.binding.textViewRecItem2.visibility=View.GONE
        if(sifreler.get(position)._sHesapAdi!="-")
            holder.binding.textViewRecItem2.setText("Kullanıcı Adı::"+sifreler.get(position)._sHesapAdi)
        else
            holder.binding.textViewRecItem2.visibility=View.GONE

        holder.binding.textViewRecItem.setText("Şifre::"+sifreler.get(position)._sSifre)
        holder.binding.textViewRecItem3.setText("Sifre İsmi::"+sifreler.get(position)._sSifreAdi+"\n(Tıklayınız)")

        holder.binding.constraintLayout.setOnClickListener {
            if(holder.binding.textViewRecItem3.visibility==View.VISIBLE)
            {
                holder.binding.textViewRecItem3.visibility=View.GONE
                holder.binding.textViewRecItem.visibility=View.VISIBLE
                if(holder.binding.textViewRecItem2.text.isNotEmpty())
                    holder.binding.textViewRecItem2.visibility=View.VISIBLE
            }else
            {
                holder.binding.textViewRecItem3.visibility=View.VISIBLE
                holder.binding.textViewRecItem.visibility=View.GONE
                holder.binding.textViewRecItem2.visibility=View.GONE
            }
        }
        holder.binding.textViewRecItem.setOnClickListener {
            var sifre=holder.binding.textViewRecItem.text.split("::")[1]
            panoyaKopyala(sifre)
        }
        holder.binding.textViewRecItem2.setOnClickListener {
            var hesapBilgisi=holder.binding.textViewRecItem2.text.split("::")[1]
            panoyaKopyala(hesapBilgisi)
        }
        holder.binding.floatingActionButtonSil.setOnClickListener {
            sqLiteIslemleri.silSifre(sifreler.get(position))

            (holder.itemView.context as AnasayfaActivity).sifreleriGetir(sifreler.get(position)._sTur?._TipId.toString())
        }
        holder.binding.floatingActionButtonDuzenle.setOnClickListener {
            val inputDialog = AlertDialog.Builder(holder.itemView.context)
            inputDialog.setTitle("Yeni şifrenizi giriniz")
            val inputEditText2 = EditText(holder.itemView.context)
            inputDialog.setView(inputEditText2)
            inputDialog.setPositiveButton("Tamam") {dialog2,which2 ->
                val sonuc = inputEditText2.text.toString()
                if(sonuc.isNotEmpty())
                {
                    var sifre=Sifre()
                    sifre._sId=sifreler.get(position)._sId
                    sifre._sSifre=sonuc
                    sifre._sTur=sifreler.get(position)._sTur
                    sifre._sKullanici=sifreler.get(position)._sKullanici
                    sifreler[position]=sifre
                    sqLiteIslemleri.güncelleSifre(sifre)
                }
                (holder.itemView.context as AnasayfaActivity).sifreleriGetir(sifreler.get(position)._sTur?._TipId.toString())
            }
            inputDialog.show()
        }
    }
    private fun panoyaKopyala(text: String) {
        val pano = bagla.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val metin = android.content.ClipData.newPlainText("Şifre", text)
        pano.setPrimaryClip(metin)
    }
}
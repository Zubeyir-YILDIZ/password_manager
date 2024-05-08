package com.zbyr.mind.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zbyr.mind.Helpers.SqLiteIslemleri
import com.zbyr.mind.Models.SifreTip
import com.zbyr.mind.Views.AnasayfaActivity
import com.zbyr.mind.Views.MainActivity
import com.zbyr.mind.databinding.ActivityAnasayfaBinding
import com.zbyr.mind.databinding.RecyclerKategorilerBinding

class KategoriAdapter(val kategorilar:MutableList<SifreTip>):RecyclerView.Adapter<KategoriAdapter.KategoriHolder>() {

    private lateinit var sqLiteIslemci: SqLiteIslemleri
    private lateinit var bind:ActivityAnasayfaBinding
    private lateinit var bagla: RecyclerKategorilerBinding
    class KategoriHolder(val bagla :RecyclerKategorilerBinding) :RecyclerView.ViewHolder(bagla.root)
    {

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KategoriHolder {
        bagla=RecyclerKategorilerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        sqLiteIslemci= SqLiteIslemleri(bagla.root.context)
        var layoutInflater=LayoutInflater.from(bagla.root.context)
        bind=ActivityAnasayfaBinding.inflate(layoutInflater)
        return KategoriHolder(bagla)
    }
    override fun getItemCount(): Int {
        return kategorilar.count()
    }
    override fun onBindViewHolder(holder: KategoriHolder, position: Int) {
        var sifreAdeti=sqLiteIslemci.getirSifrelerTipIle(kategorilar.get(position)._TipId.toString(),
            MainActivity.AktifKullanici!!).count()
        var metin=kategorilar.get(position)._SifreTipi
        if(metin.length>11)
        {
            metin=metin.removeRange(10,metin.length)
            metin+=".."
        }
        metin+="\n adet:"+sifreAdeti
        holder.bagla.textViewKategorilar.setText(metin)
        holder.bagla.textViewKategorilar.setOnClickListener {
            (holder.itemView.context as AnasayfaActivity).sifreleriGetir(kategorilar.get(position)._TipId.toString())
            AnasayfaActivity.kategori=kategorilar.get(position)._TipId.toString()
            notifyItemInserted(position)
        }
    }

}
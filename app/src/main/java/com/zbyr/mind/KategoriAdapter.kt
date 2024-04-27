package com.zbyr.mind

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zbyr.mind.databinding.ActivityAnasayfaBinding
import com.zbyr.mind.databinding.RecyclerItemBinding
import com.zbyr.mind.databinding.RecyclerKategorilerBinding

class KategoriAdapter(val kategorilar:MutableList<SifreTip>):RecyclerView.Adapter<KategoriAdapter.KategoriHolder>() {

    private lateinit var sqLiteIslemci:SqLiteIslemleri
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
        holder.bagla.textViewKategorilar.setText(kategorilar.get(position)._SifreTipi)
        holder.bagla.textViewKategorilar.setOnClickListener {

            (holder.itemView.context as AnasayfaActivity).sifreleriGetir(kategorilar.get(position)._TipId.toString())

        }

    }

    @SuppressLint("SuspiciousIndentation")
    fun kategoriSor(holder: KategoriHolder, position:Int)
    {
        val inputDialog = AlertDialog.Builder(holder.itemView.context)
        inputDialog.setTitle("Yeni kategorinizi giriniz")
        val inputEditText2 = EditText(holder.itemView.context)
        inputDialog.setView(inputEditText2)
        inputDialog.setPositiveButton("Tamam") {dialog2,which2 ->
            val sonuc = inputEditText2.text.toString()
                if(sqLiteIslemci.tipKarsilastir(sonuc))
                {
                    var textViewKategori=TextView(holder.bagla.root.context)
                    textViewKategori.setText(position)

                }



            //(holder.itemView.context as AnasayfaActivity).sifreleriGetir()
        }
        inputDialog.show()
    }


}
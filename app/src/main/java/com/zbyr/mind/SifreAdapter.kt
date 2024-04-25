package com.zbyr.mind

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.zbyr.mind.databinding.RecyclerItemBinding

class SifreAdapter(val sifreler:ArrayList<Sifre>) :RecyclerView.Adapter<SifreAdapter.SifreHolder>() {


    class SifreHolder(val binding: RecyclerItemBinding) : RecyclerView.ViewHolder(binding.root)
    {

    }
    private lateinit var sqLiteIslemleri: SqLiteIslemleri
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SifreHolder {
        val binding = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        sqLiteIslemleri=SqLiteIslemleri(binding.root.context)
        return SifreHolder(binding)
    }

    override fun getItemCount(): Int {
        return sifreler.count()
    }

    override fun onBindViewHolder(holder: SifreHolder, position: Int) {
            holder.binding.textViewRecItem.setText("Şifre: "+sifreler.get(position)._sSifre)
            holder.binding.textViewRecItem2.setText("Türü: "+sifreler.get(position)._sTur?._SifreTipi)

            holder.binding.floatingActionButtonSil.setOnClickListener {
                sqLiteIslemleri.silSifre(sifreler.get(position))

                (holder.itemView.context as AnasayfaActivity).sifreleriGetir()
            }
            holder.binding.floatingActionButtonDuzenle.setOnClickListener {
                val inputDialog = AlertDialog.Builder(holder.itemView.context)
                inputDialog.setTitle("Yeni şifrenizi giriniz")
                val inputEditText2 = EditText(holder.itemView.context)
                inputDialog.setView(inputEditText2)
                inputDialog.setPositiveButton("Tamam") {dialog2,which2 ->
                    val sonuc = inputEditText2.text.toString()
                    var sifre=Sifre()
                    sifre._sId=sifreler.get(position)._sId
                    sifre._sSifre=sonuc
                    sifre._sTur=sifreler.get(position)._sTur
                    sifre._sKullanici=sifreler.get(position)._sKullanici
                    sifreler[position]=sifre
                    sqLiteIslemleri.güncelleSifre(sifre)
                    (holder.itemView.context as AnasayfaActivity).sifreleriGetir()
                }
                inputDialog.show()
            }
    }
}
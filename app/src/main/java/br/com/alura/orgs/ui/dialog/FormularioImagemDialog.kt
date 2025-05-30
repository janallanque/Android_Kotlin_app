package br.com.alura.orgs.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import br.com.alura.orgs.databinding.FormularioImagemBinding.inflate
import br.com.alura.orgs.extensions.tentaCarregarImagem
import br.com.alura.orgs.ui.activity.ImagensFrutasTesteActivity

class FormularioImagemDialog(private val context: Context) {

    fun mostra(
        urlPadrao: String? = null,
        quandoImagemCarregada: (imagem: String) -> Unit
    ) {

        val binding = inflate(LayoutInflater.from(context)).apply {
            urlPadrao?.let {
                formularioImagemImageview.tentaCarregarImagem(it)
                formularioImagemUrl.setText(it)
            }
            formularioImagemBotaoCarregar.setOnClickListener {
                val entrada = formularioImagemUrl.text.toString().trim()

                val imagemDaLista = ImagensFrutasTesteActivity().buscarImagemPorNome(entrada).firstOrNull()

                if (imagemDaLista != null) {
                    // É um nome de fruta conhecido
                    formularioImagemImageview.tentaCarregarImagem(imagemDaLista)
                    formularioImagemUrl.setText(imagemDaLista) // Preenche com URL real
                } else {
                    // Não encontrou fruta, tenta como URL direta
                    formularioImagemImageview.tentaCarregarImagem(entrada)
                }
            }
        }

        AlertDialog.Builder(context)
            .setView(binding.root)
            .setPositiveButton("Confirmar") { _, _ ->
                val url = binding.formularioImagemUrl.text.toString()
                Log.i("FormularioImagemDialog", "mostra: $url")
                quandoImagemCarregada(url)
            }
            .setNegativeButton("Cancelar") { _, _ ->

            }
            .show()
    }
}
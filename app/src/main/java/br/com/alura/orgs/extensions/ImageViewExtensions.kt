package br.com.alura.orgs.extensions

import android.widget.ImageView
import br.com.alura.orgs.R
import coil.load


fun ImageView.tentaCarregarImagem(
    url: String? = null,
    fallback: Int = R.drawable.produto_1,

    ) {
    this.load(url) {
        apply {
            crossfade(true) // Anima a transição da imagem
            placeholder(R.drawable.placeholder)
            fallback(fallback) // Se a URL for nula
            error(R.drawable.erro) // Se der erro no carregamento
        }
    }
}



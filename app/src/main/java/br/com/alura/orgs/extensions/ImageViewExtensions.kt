package br.com.alura.orgs.extensions

import android.widget.ImageView
import br.com.alura.orgs.R
import coil.load


fun ImageView.tentaCarregarImagem(
    url: String? = null,
    fallback: Int = R.drawable.montanha3,

    ) {
    this.load(url) {
        apply {
            placeholder(R.drawable.placeholder)
            fallback(fallback)
            error(R.drawable.erro)
        }
    }
}



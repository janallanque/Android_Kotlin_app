package br.com.alura.orgs.extensions

import android.widget.ImageView
import br.com.alura.orgs.R
import coil3.ImageLoader
import coil3.load
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder

fun ImageView.tentaCarregarImagem(
    url: String? = null,
    fallback: Int = R.drawable.produto_1,

) {
    this.load(url) {
        apply {
            fallback(fallback)
            placeholder(R.drawable.placeholder)
            error(R.drawable.erro)

        }
    }
}


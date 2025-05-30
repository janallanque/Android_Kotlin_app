package br.com.alura.orgs.ui.activity

class ImagensFrutasTeste {

    @Deprecated("Use getCrunchPngs() instead", ReplaceWith("getCrunchPngs()"))
    fun getCrunchImages(): List<String> {
        return getCrunchPngs()
    }

    fun getCrunchPngs(): List<String> {
        return imagensPorFruta.values.flatten()
    }

    // Busca imagens pelo nome (ex: "melancia")
    fun buscarImagemPorNome(nome: String): List<String> {
        val chave = nome.trim().lowercase()
        return imagensPorFruta.entries
            .firstOrNull { chave in it.key.lowercase() }
            ?.value.orEmpty()
    }

    // Lista organizada por fruta
    private val imagensPorFruta = mapOf(
        "laranja" to listOf(
            "https://agro.estadao.com.br/app/uploads/2025/01/LaranjaHamlin.png"
        ),
        "abacaxi" to listOf(
            "https://www.gebon.com.br/image/media/_00200/251/picole-gefrutti-abacaxi.png"
        ),
        "pera" to listOf(
            "https://s3-sa-east-1.amazonaws.com/superimg/img.produtos/645183/645/img_500_1.png"
        ),
        "kiwi" to listOf(
            "https://imagensempng.com.br/wp-content/uploads/2021/07/03-16.png"
        ),
        "maca" to listOf(
            "https://static.vecteezy.com/system/resources/thumbnails/023/290/773/small_2x/fresh-red-apple-isolated-on-transparent-background-generative-ai-png.png"
        ),
        "maca_verde" to listOf(
            "https://static.vecteezy.com/system/resources/thumbnails/040/350/603/small/ai-generated-fresh-green-apple-leaf-free-png.png"
        ),
        "morango" to listOf(
            "https://images.pexels.com/photos/4038803/pexels-photo-4038803.jpeg"
        ),
        "melancia" to listOf(
            "https://phygital-files.mercafacil.com/fazfeira/uploads/produto/melancia_mini_magali_2807a3db-1aae-43bf-ba93-5e3519521aba.png"
        ),
        "melao" to listOf(
            "https://acdn-us.mitiendanube.com/stores/174/441/products/melao-amarelo-fcaa53d71fe209ff0415122895212213-640-0.jpg"
        ),
        "mamao" to listOf(
            "https://www.saudeemdia.com.br/wp-content/uploads/mamao_widexl.jpg"
        ),
        "banana" to listOf(
            "https://cloudfront-us-east-1.images.arcpublishing.com/estadao/CU6HU7WMEVF2THSPFT7RBQXFWM.jpeg"
        ),
        "ameixa" to listOf(
            "https://phygital-files.mercafacil.com/fazfeira/uploads/produto/ameixa_fresca_nacional_kg_e0d3197a-d1c6-4503-9fab-6eca5855d47c.jpg"
        ),
        "salada-de-fruta" to listOf(
            "https://www.sumerbol.com.br/uploads/images/2018/11/salada-de-frutas-888-1542050460.jpg"
        )
    )
}

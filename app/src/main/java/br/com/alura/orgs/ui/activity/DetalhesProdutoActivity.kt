package br.com.alura.orgs.ui.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.databinding.ActivityDetalhesProdutoBinding
import br.com.alura.orgs.extensions.formataParaMoedaBrasileira
import br.com.alura.orgs.extensions.tentaCarregarImagem
import br.com.alura.orgs.model.Produto

class DetalhesProdutoActivity : AppCompatActivity() {

    private var produtoId: Long = 0L
    private var produto: Produto? = null
    private val binding by lazy {
        ActivityDetalhesProdutoBinding.inflate(layoutInflater)
    }

    val produtoDao by lazy {
        AppDatabase.instancia(this).produtoDao()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        tentaCarregarProduto()

        binding.botaoEditar.setOnClickListener {
            Log.i(TAG, "Clique no botão Editar")
            val intent = Intent(this, FormularioProdutoActivity::class.java).apply {
                putExtra(CHAVE_PRODUTO_ID, produtoId)
            }
            startActivity(intent)
        }
        binding.botaoRemover.setOnClickListener {
            if (produto != null) {
                Log.i(TAG, "Clique no botão Remover")
                produto?.let {
                    produtoDao.remove(it)
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        buscaProduto()
    }

    private fun buscaProduto() {
        produto = produtoDao.buscaPorId(produtoId)
        produto?.let {
            preencheCampos(it)
        } ?: finish()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun tentaCarregarProduto() {
        produtoId = intent.getLongExtra(CHAVE_PRODUTO_ID, 0L)
    }

    private fun preencheCampos(produtoCarregado: Produto) {
        with(binding) {
            activityDetalhesProdutoImagem.tentaCarregarImagem(produtoCarregado.imagem)
            activityDetalhesProdutoNome.text = produtoCarregado.nome
            activityDetalhesProdutoDescricao.text = produtoCarregado.descricao
            activityDetalhesProdutoValor.text = produtoCarregado.valor.formataParaMoedaBrasileira()
        }
    }
}


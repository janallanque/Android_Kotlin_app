package br.com.alura.orgs.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import br.com.alura.orgs.R
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.databinding.ActivityListaProdutosBinding
import br.com.alura.orgs.extensions.vaiPara
import br.com.alura.orgs.model.Produto
import br.com.alura.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "ListaProdutosActivity"

class ListaProdutosActivity : UsuarioBaseActivity() {

    private val adapter = ListaProdutosAdapter(context = this, produtos = emptyList())
    private val binding by lazy {
        ActivityListaProdutosBinding.inflate(layoutInflater)
    }
    private val produtoDao by lazy {
        val db = AppDatabase.instancia(this)
        db.produtoDao()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            buscaProdutoUsuario(
                usuarioId = usuario.value?.id ?: "Usuário não encontrado"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)
        configuraRecyclerView()
        configuraFab()
        lifecycleScope.launch {
            launch {
                usuario
                    .filterNotNull()
                    .collect { usuario ->
                        buscaProdutoUsuario(usuario.id)
                    }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lista_produtos, menu)
        menuInflater.inflate(R.menu.menu_lista_produtos_ordenar, menu)
        menuInflater.inflate(R.menu.menu_busca_produto_por_nome, menu)

        val searchItem = menu?.findItem(R.id.menu_busca_produto_por_nome)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.queryHint = "Buscar por nome..."

        searchView?.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    lifecycleScope.launch {
                        buscaProdutoPorNome(it)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    lifecycleScope.launch {
                        buscaProdutoPorNome(it)
                    }
                }
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_lista_produtos_perfil_usuario -> {
                vaiPara(PerfilUsuarioActivity::class.java)
            }

            R.id.menu_busca_produto_por_nome -> {
                true
            }

            R.id.menu_lista_produtos_todos_produtos -> {
                vaiPara(TodosProdutosActivity::class.java)
            }

            R.id.menu_lista_produtos_ordenar_nome_asc,
            R.id.menu_lista_produtos_ordenar_nome_desc,
            R.id.menu_lista_produtos_ordenar_valor_asc,
            R.id.menu_lista_produtos_ordenar_valor_desc,
            R.id.menu_lista_produtos_ordenar_descricao_asc,
            R.id.menu_lista_produtos_ordenar_descricao_desc -> {
                handleOrdenarProdutos(item)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun buscaProdutoPorNome(nome: String) {
        val usuarioId = usuario.value?.id
        if (usuarioId != null) {
            val produtos = withContext(Dispatchers.IO) {
                produtoDao.buscaPorNome(usuarioId, nome).first()
            }
            adapter.atualiza(produtos)
        } else {
            Log.e("ListaProdutosActivity", "Usuário não encontrado ao buscar produtos por nome.")
            adapter.atualiza(emptyList())
        }
    }

    private fun handleOrdenarProdutos(item: MenuItem) {
        lifecycleScope.launch(Dispatchers.IO) {
            val usuarioId = usuario.value?.id
            if (usuarioId != null) {
                val produtosOrdenado = produtoOrdenado(item, usuarioId)
                produtosOrdenado?.let {
                    withContext(Dispatchers.Main) {
                        adapter.atualiza(it)
                    }
                }
            } else {
                Log.e(TAG, "Usuário não encontrado ao ordenar produtos.")
                withContext(Dispatchers.Main) {
                    adapter.atualiza(emptyList())
                }
            }
        }
    }

    private suspend fun buscaProdutoUsuario(usuarioId: String) {
        produtoDao.buscaTodosDoUsuario(usuarioId).collect { produtos ->
            adapter.atualiza(produtos)
        }
    }

    private fun getOrdemFuncoes(usuarioId: String): Map<Int, suspend () -> Flow<List<Produto>>> {
        return mapOf(
            R.id.menu_lista_produtos_ordenar_nome_asc to {
                produtoDao.buscaTodosOrdenadorPorNomeAsc(
                    usuarioId
                )
            },
            R.id.menu_lista_produtos_ordenar_nome_desc to {
                produtoDao.buscaTodosOrdenadorPorNomeDesc(
                    usuarioId
                )
            },
            R.id.menu_lista_produtos_ordenar_valor_asc to {
                produtoDao.buscaTodosOrdenadorPorValorAsc(
                    usuarioId
                )
            },
            R.id.menu_lista_produtos_ordenar_valor_desc to {
                produtoDao.buscaTodosOrdenadorPorValorDesc(
                    usuarioId
                )
            },
            R.id.menu_lista_produtos_ordenar_descricao_asc to {
                produtoDao.buscaTodosOrdenadorPorDescricaoAsc(
                    usuarioId
                )
            },
            R.id.menu_lista_produtos_ordenar_descricao_desc to {
                produtoDao.buscaTodosOrdenadorPorDescricaoDesc(
                    usuarioId
                )
            }
        )
    }

    private suspend fun produtoOrdenado(item: MenuItem, usuarioId: String): List<Produto>? {
        return getOrdemFuncoes(usuarioId)[item.itemId]?.invoke()?.first()
    }

    private fun configuraFab() {
        val fab = binding.activityListaProdutosFab
        fab.setOnClickListener {
            vaiParaFormularioProduto()
        }
    }

    private fun vaiParaFormularioProduto() {
        val intent = Intent(this, FormularioProdutoActivity::class.java)
        startActivity(intent)
    }

    private fun configuraRecyclerView() {
        val recyclerView = binding.activityListaProdutosRecyclerView
        recyclerView.adapter = adapter
        adapter.quandoClicaNoItem = {
            val intent = Intent(
                this,
                DetalhesProdutoActivity::class.java
            ).apply {
                putExtra(CHAVE_PRODUTO_ID, it.id)
            }
            startActivity(intent)
        }
        adapter.quandoClicaEmEditar = {
            Log.i(TAG, "configuraAcoesDoAdapter: $it")
        }
        adapter.quandoClicaEmRemover = {
            Log.i(TAG, "configuraAcoesDoAdapter: $it")
        }
    }
}
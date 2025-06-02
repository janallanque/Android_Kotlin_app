package br.com.alura.orgs.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import br.com.alura.orgs.R
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.databinding.ActivityListaProdutosBinding
import br.com.alura.orgs.extensions.vaiPara
import br.com.alura.orgs.model.Produto
import br.com.alura.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import kotlinx.coroutines.Dispatchers
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
            buscaProdutoUsuario()
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
                    .collect {
                        Log.i("ListaProdutos", "onCreate: $it")
                        buscaProdutoUsuario()
                    }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lista_produtos, menu)
        menuInflater.inflate(R.menu.menu_lista_produtos_ordenar, menu)
        menuInflater.inflate(R.menu.menu_busca_produto_por_nome, menu)

        val searchItem = menu?.findItem(R.id.menu_busca_produto_por_nome)
        val searchView = searchItem?.actionView as? androidx.appcompat.widget.SearchView

        searchView?.queryHint = "Buscar por nome..."

        searchView?.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
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
                handleBuscaProdutoPorNome(item)
                true
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
        val produtos = withContext(Dispatchers.IO) {
            produtoDao.buscaPorNome(nome).first()
        }
        adapter.atualiza(produtos)
    }



    private fun handleBuscaProdutoPorNome(item: MenuItem) {
        lifecycleScope.launch(Dispatchers.IO) {
            val buscaPorNome = buscaPorNome(item)
            buscaPorNome?.let {
                withContext(Dispatchers.Main) {
                    adapter.atualiza(it)
                }
            }
        }
    }

    private fun handleOrdenarProdutos(item: MenuItem) {
        lifecycleScope.launch(Dispatchers.IO) {
            val produtosOrdenado = produtoOrdenado(item)
            produtosOrdenado?.let {
                withContext(Dispatchers.Main) {
                    adapter.atualiza(it)
                }
            }
        }
    }

    private suspend fun buscaProdutoUsuario() {
        produtoDao.buscaTodos().collect { produtos ->
            adapter.atualiza(produtos)
        }
    }

    private val ordemFuncoes = mapOf(
        R.id.menu_lista_produtos_ordenar_nome_asc to { produtoDao.buscaTodosOrdenadorPorNomeAsc() },
        R.id.menu_lista_produtos_ordenar_nome_desc to { produtoDao.buscaTodosOrdenadorPorNomeDesc() },
        R.id.menu_lista_produtos_ordenar_valor_asc to { produtoDao.buscaTodosOrdenadorPorValorAsc() },
        R.id.menu_lista_produtos_ordenar_valor_desc to { produtoDao.buscaTodosOrdenadorPorValorDesc() },
        R.id.menu_lista_produtos_ordenar_descricao_asc to { produtoDao.buscaTodosOrdenadorPorDescricaoAsc() },
        R.id.menu_lista_produtos_ordenar_descricao_desc to { produtoDao.buscaTodosOrdenadorPorDescricaoDesc() }
    )

    private fun produtoOrdenado(item: MenuItem): List<Produto>? {
        return ordemFuncoes[item.itemId]?.invoke()
    }

    private fun buscaPorNome(item: MenuItem): List<Produto>? {
        return ordemFuncoes[item.itemId]?.invoke()
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
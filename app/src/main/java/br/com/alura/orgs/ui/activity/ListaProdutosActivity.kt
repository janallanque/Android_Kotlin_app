package br.com.alura.orgs.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import br.com.alura.orgs.R
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.databinding.ActivityListaProdutosBinding
import br.com.alura.orgs.extensions.vaiPara
import br.com.alura.orgs.model.Produto
import br.com.alura.orgs.preferences.dataStore
import br.com.alura.orgs.preferences.usuarioLogadoPreferences
import br.com.alura.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "ListaProdutosActivity"

class ListaProdutosActivity : AppCompatActivity() {

    private val adapter = ListaProdutosAdapter(context = this, produtos = emptyList())
    private val binding by lazy {
        ActivityListaProdutosBinding.inflate(layoutInflater)
    }

    private val produtoDao by lazy {
        val db = AppDatabase.instancia(this)
        db.produtoDao()
    }

    private val usuarioDao by lazy {
        AppDatabase.instancia(this).usuarioDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)
        configuraRecyclerView()
        configuraFab()
        lifecycleScope.launch {
            launch {
                verificaUsuarioLogado()
            }
        }
    }

    private suspend fun verificaUsuarioLogado() {
        dataStore.data.collect { preferences ->
            preferences[usuarioLogadoPreferences]?.let { usuarioId ->
                buscaUsuario(usuarioId)
            } ?: vaiParaLogin()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lista_produtos, menu)
        menuInflater.inflate(R.menu.menu_lista_produtos_ordenar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_lista_produtos_sair_do_app -> {
                handleSairDoApp()
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
    }

    private fun handleSairDoApp() {
        lifecycleScope.launch {
            deslogaUsuario()
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

    private fun buscaUsuario(usuarioId: String) {
        lifecycleScope.launch {
            usuarioDao.buscaPorId(usuarioId)
                .firstOrNull()?.let {
                    launch {
                        buscaProdutoUsuario()
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

    private suspend fun deslogaUsuario() {
        dataStore.edit { preferences ->
            preferences.remove(usuarioLogadoPreferences)
        }
    }

    private fun vaiParaLogin() {
        vaiPara(LoginActivity::class.java) {
        }
        finish()
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
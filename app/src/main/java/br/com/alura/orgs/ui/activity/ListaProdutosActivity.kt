package br.com.alura.orgs.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.alura.orgs.R
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.databinding.ActivityListaProdutosBinding
import br.com.alura.orgs.preferences.dataStore
import br.com.alura.orgs.preferences.usuarioLogadoPreferences
import br.com.alura.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import kotlinx.coroutines.Dispatchers
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
                produtoDao.buscaTodos().collect { produtos ->
                    adapter.atualiza(produtos)
                }
            }
            dataStore.data.collect { preferences ->
                preferences[usuarioLogadoPreferences]?.let { usuarioId ->
                    usuarioDao.buscaPorId(usuarioId).collect {
                        Log.i("ListaProdutos", "onCreate: $it")
                    }
                }
            }
        }
    }

    // Criação do menu suspenso de ordenar
    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            produtoDao.buscaTodos().collect { produtos ->
                adapter.atualiza(produtos)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lista_produtos, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        lifecycleScope.launch(Dispatchers.IO) {
            val produtosOrdenado = when (item.itemId) {
                R.id.menu_lista_produtos_ordenar_nome_asc -> produtoDao.buscaTodosOrdenadorPorNomeAsc()
                R.id.menu_lista_produtos_ordenar_nome_desc -> produtoDao.buscaTodosOrdenadorPorNomeDesc()
                R.id.menu_lista_produtos_ordenar_valor_asc -> produtoDao.buscaTodosOrdenadorPorValorAsc()
                R.id.menu_lista_produtos_ordenar_valor_desc -> produtoDao.buscaTodosOrdenadorPorValorDesc()
                R.id.menu_lista_produtos_ordenar_descricao_asc -> produtoDao.buscaTodosOrdenadorPorDescricaoAsc()
                R.id.menu_lista_produtos_ordenar_descricao_desc -> produtoDao.buscaTodosOrdenadorPorDescricaoDesc()
                else -> null
            }

            produtosOrdenado?.let {
                // Trocar de volta para a Main Thread para atualizar a UI
                withContext(Dispatchers.Main) {
                    adapter.atualiza(it)
                }
            }
        }
        return true
    }
    //Final do bloco do menu suspenso de ordenar

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
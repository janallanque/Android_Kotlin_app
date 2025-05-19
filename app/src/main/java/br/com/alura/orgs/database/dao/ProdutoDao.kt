package br.com.alura.orgs.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.alura.orgs.model.Produto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDao {

    @Query("SELECT * FROM Produto")
    fun buscaTodos(): Flow<List<Produto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salva(vararg produto: Produto?)

    @Delete  //novo
    suspend fun remove(produto: Produto)

    @Query("SELECT * FROM Produto WHERE id = :id") //novo
    fun buscaPorId(id: Long): Flow<Produto?>

    @Query("SELECT * FROM Produto ORDER BY nome ASC")
    fun buscaTodosOrdenadorPorNomeAsc(): List<Produto>

    @Query("SELECT * FROM Produto ORDER BY nome DESC")
    fun buscaTodosOrdenadorPorNomeDesc(): List<Produto>

    @Query("SELECT * FROM Produto ORDER BY descricao ASC")
    fun buscaTodosOrdenadorPorDescricaoAsc(): List<Produto>

    @Query("SELECT * FROM Produto ORDER BY descricao DESC")
    fun buscaTodosOrdenadorPorDescricaoDesc(): List<Produto>

    @Query("SELECT * FROM Produto ORDER BY valor ASC")
    fun buscaTodosOrdenadorPorValorAsc(): List<Produto>

    @Query("SELECT * FROM Produto ORDER BY valor DESC")
    fun buscaTodosOrdenadorPorValorDesc(): List<Produto>
}
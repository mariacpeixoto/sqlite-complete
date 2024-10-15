package com.example.appsqlitecomplete.viewModel

import com.example.appsqlitecomplete.roomDB.DatabasePessoa
import com.example.appsqlitecomplete.roomDB.Pessoa

class Repository(private val db : DatabasePessoa) {
    suspend fun  upsertPessoa(pessoa: Pessoa){
        db.daoPessoa().upsertPessoa(pessoa)
    }

    suspend fun deletePessoa(pessoa: Pessoa){
        db.daoPessoa().deletePessoa(pessoa)
    }

    fun getAllPessoa() = db.daoPessoa().getAllPessoa()
}
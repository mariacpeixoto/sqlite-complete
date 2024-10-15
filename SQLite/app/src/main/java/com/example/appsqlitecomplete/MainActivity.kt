package com.example.appsqlitecomplete

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.appsqlitecomplete.roomDB.DatabasePessoa
import com.example.appsqlitecomplete.roomDB.Pessoa
import com.example.appsqlitecomplete.ui.theme.AppSQLiteCompleteTheme
import com.example.appsqlitecomplete.viewModel.Repository
import com.example.appsqlitecomplete.viewModel.ViewModelPessoa
import android.annotation.SuppressLint as SuppressLint1

class MainActivity : ComponentActivity() {

    // Inicializa o banco de dados de forma lazy (só será criado quando necessário)
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            DatabasePessoa::class.java,
            "pessoa.db"
        ).build()
    }

    private val viewModel by viewModels<ViewModelPessoa> {
        // Configuração do ViewModel com a Factory para passar o repositório do banco de dados
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ViewModelPessoa(Repository(db)) as T
            }
        }
    }

    @SuppressLint1("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppSQLiteCompleteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    App(viewModel, this)
                }
            }
        }
    }
}

@Composable
fun App(viewModel: ViewModelPessoa, mainActivity: MainActivity) {
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }

    var pessoaList by remember { mutableStateOf(listOf<Pessoa>()) }

    viewModel.getPessoa().observe(mainActivity) { pessoas ->
        pessoaList = pessoas
    }

    Column(
        Modifier
            .background(Color.Black)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "App Database",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome:") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = telefone,
            onValueChange = { telefone = it },
            label = { Text("Telefone:") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (nome.isNotEmpty() && telefone.isNotEmpty()) {
                    viewModel.upsertPessoa(Pessoa(nome, telefone))
                    nome = ""
                    telefone = ""
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Cadastrar")
        }

        Spacer(modifier = Modifier.height(50.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Nome", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(text = "Telefone", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(text = "Ações", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.width(50.dp))
        }

        HorizontalDivider(color = Color.Gray)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(pessoaList) { pessoa ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = pessoa.nome,
                        color = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp) // Espaço entre nome e telefone
                    )
                    Text(
                        text = pessoa.telefone,
                        color = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                    )
                    IconButton(
                        onClick = { viewModel.deletePessoa(pessoa) },
                        modifier = Modifier.size(24.dp) // Controla o tamanho do ícone
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }
                HorizontalDivider(color = Color.Gray)
            }
        }
    }
}

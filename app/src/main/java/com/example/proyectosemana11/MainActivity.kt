package com.example.proyectosemana11

import android.net.http.HttpException
import android.os.Build
import android.os.Bundle
import android.os.Message
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appservice.model.Post
import com.example.appservice.network.RetrofitlInstance
import kotlinx.coroutines.launch
import okio.IOException

class PostViewModel: ViewModel(){
    private val _uiState= mutableStateOf<UiState>(UiState.loading)
    val uiState : State<UiState> get() = _uiState
    init { fetchPosts()}
    private fun fetchPosts(){
        viewModelScope.launch {
            _uiState.value=try{
                val  response= RetrofitlInstance.apiService.getPosts()
                if(response.isSuccessful){
                    UiState.Success(response.body()?: emptyList())
                }
                else{ UiState.Error("Error: ${response.code()}")}
            } catch (e: IOException){
                UiState.Error(" Network Error: ${e.message}")
            } catch (e: HttpException){
                UiState.Error("HTTP Error: ${e.message()}")
            }
        }
    }
}

sealed class UiState{
    object loading: UiState()
    data class Success (val posts: List<Post>): UiState()
    data class Error(val message: String): UiState()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
           myApp(PostViewModel())
        }
    }
}




@Composable
fun myApp(viewModel: PostViewModel){
    val state by viewModel.UiState
    when(state){
        is UiState.loading -> {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center){
                CircularProgressIndicator()
            }
        }
        is UiState.Success ->{ PostList(posts = (state as UiState.Success).posts)}
        is UiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text("Error: ${(state as UiState.Error).message}")
            }
        }
    }
}

@Composable
fun PostList(posts: List<Post>){
    LazyColumn (modifier = Modifier.padding(16.dp)) {
        items(posts) { post ->
            PostItem(post = post)
        }
    }
}

@Composable
fun PostItem(post: Post){
    Card (modifier = Modifier.padding(8.dp)){
        Column (modifier = Modifier.padding(16.dp)) {
            Text(text = post.tittle, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height((8.dp)))
            Text(text = post.body)
        }
    }
}
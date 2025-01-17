package com.gxd.demo.compose.case

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.gxd.demo.compose.data.Repo
import com.gxd.demo.compose.request.service
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun SimpleRetrofitRequest(user: String = "gxd523") {
    Column {
        val scope = rememberCoroutineScope()
        var repoList by remember { mutableStateOf<List<Repo>>(emptyList()) }
        Button(onClick = {
            scope.launch { repoList = service.listRepos(user) }
        }) {
            Text("获取 $user 的所有Repo")
        }
        LazyColumn {
            items(repoList.size) { index ->
                val repo = repoList[index]
                Text("${repo.name}: ${repo.size}")
                HorizontalDivider()
            }
        }
    }
}
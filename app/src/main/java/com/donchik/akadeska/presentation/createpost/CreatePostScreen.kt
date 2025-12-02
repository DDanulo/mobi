package com.donchik.akadeska.presentation.createpost


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.donchik.akadeska.domain.model.PostType
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    vm: CreatePostViewModel,
    onCreated: (String) -> Unit
) {
    val state by vm.state.collectAsState()
    val ctx = LocalContext.current

    // picker zdjęcia (Android 13+ Photo Picker – działa wstecz na PlayServices)
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            imageUri = uri
            val bytes = uri?.let { u ->
                ctx.contentResolver.openInputStream(u)?.use(InputStream::readBytes)
            }
            vm.setImage(bytes)
        }
    )

    LaunchedEffect(state.createdId) {
        state.createdId?.let(onCreated)
    }

    Column(Modifier.padding(16.dp)) {
        // typ
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = state.type.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Type") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                PostType.values().forEach { t ->
                    DropdownMenuItem(
                        text = { Text(t.name) },
                        onClick = { vm.setType(t); expanded = false }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.title, onValueChange = vm::setTitle,
            label = { Text("Title") }, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.body, onValueChange = vm::setBody,
            label = { Text("Body") }, modifier = Modifier.fillMaxWidth(), minLines = 4
        )
        if (state.type == PostType.LISTING) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.price, onValueChange = vm::setPrice,
                label = { Text("Price (zł)") }, modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }) { Text(if (imageUri == null) "Add image" else "Change image") }

        imageUri?.let {
            Spacer(Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
        }

        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(state.error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = vm::submit,
            enabled = !state.loading,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (state.loading) "Saving..." else "Publish (pending)") }
    }
}
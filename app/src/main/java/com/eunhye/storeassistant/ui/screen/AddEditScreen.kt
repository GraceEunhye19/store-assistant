package com.eunhye.storeassistant.ui.screen

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.CameraAlt
//import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.eunhye.storeassistant.data.Product
import com.eunhye.storeassistant.ProductViewModel
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.eunhye.storeassistant.ImageUtils
import com.eunhye.storeassistant.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddEditProductScreen(
    productId: Int? = null,
    onNavigateBack: () -> Unit,
    viewModel: ProductViewModel = viewModel()
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var item by remember {mutableStateOf("")}
    var price by remember {mutableStateOf("")}
    var quantity by remember {mutableStateOf("")}
    var imgUri by remember {mutableStateOf<Uri?>(null)}
    var imagePath by remember { mutableStateOf<String?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    var itemError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    var quantityError by remember { mutableStateOf(false) }

    val isEditMode = productId != null

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val galleryPermissionState = rememberPermissionState(
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    )

    LaunchedEffect(productId) {
        if (productId != null){
            viewModel.getProductById(productId)?.let{
                product ->
                item = product.item
                price = product.price.toString()
                quantity = product.quantity.toString()
                product.imgPath?.let {path -> imgUri = path.toUri()}
            }
        }
    }

//    val galleryLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? -> uri?.let{imgUri = it}}
//
//    val cameraLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.TakePicture()
//    ) { success -> if (success){} }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imgUri = it
            imagePath = ImageUtils.saveImageToInternalStorage(context, it)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            imgUri = tempCameraUri
            imagePath = tempCameraUri.toString().replace("content://", "").let {
                context.filesDir.absolutePath + "/product_images/" + it.substringAfterLast("/")
            }
        }
    }

    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            showPermissionDialog = true
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            tempCameraUri = ImageUtils.createImageUri(context)
            cameraLauncher.launch(tempCameraUri!!)
        } else {
            showPermissionDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) stringResource(R.string.edit_item_title) else stringResource(
                            R.string.add_item_title
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ){
        paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.tertiary)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            OutlinedTextField(
                value = item,
                onValueChange = {
                    item = it
                    itemError = false
                },
                label = {
                    Text(
                        "Item Name*",
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isError = itemError,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = price,
                onValueChange = {
                    if (it.isEmpty()||it.matches(Regex("^\\d*\\.?\\d*$"))){
                        price = it
                        priceError = false
                    }
                },
                label = {
                    Text(
                        "Item Price*",
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Text(
                        "₦",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isError = priceError,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = quantity,
                onValueChange = {
                    if (it.isEmpty()||it.matches(Regex("^\\d+$"))){
                        quantity = it
                        quantityError = false
                    }
                },
                label = {
                    Text(
                        "Quantity In Stock*",
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isError = quantityError,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Text(
                    text = "Product Image",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            // showImagePickerDialog = true
                        },
                    contentAlignment = Alignment.Center
                ){
                    if(imgUri != null){
                        AsyncImage(
                            model = imgUri,
                            contentDescription = "Product Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Image(
                                painter = painterResource(R.drawable.no_img),
                                contentDescription = "Add Image",
                                modifier = Modifier.size(48.dp),
                            )
                            Text(
                                text = stringResource(R.string.add_image),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ){
                                //camera
                                OutlinedButton(
                                    onClick = {
                                        when {
                                            cameraPermissionState.status.isGranted -> {
                                                tempCameraUri = ImageUtils.createImageUri(context)
                                                cameraLauncher.launch(tempCameraUri!!)
                                            }
                                            cameraPermissionState.status.shouldShowRationale -> {
                                                // Show rationale and request permission
                                                showPermissionDialog = true}
                                            else -> {
                                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text("Camera")
                                }

                                //gallery
                                OutlinedButton(
                                    onClick = {
                                        when {
                                            galleryPermissionState.status.isGranted -> {
                                                galleryLauncher.launch("image/*")
                                            }
                                            galleryPermissionState.status.shouldShowRationale ->{
                                                showPermissionDialog = true
                                            }
                                            else -> {
                                                galleryPermissionLauncher.launch(
                                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU){
                                                        Manifest.permission.READ_MEDIA_IMAGES
                                                    } else {
                                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                                    }
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    )
                                ) { Text ("Gallery")}
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            //save/update button
                            Button (
                                onClick = {
                                    var isValid = true

                                    if(item.isBlank()){itemError = true; isValid = false}
                                    if(price.isBlank()){priceError = true ; isValid = false}
                                    if(quantity.isBlank()||quantity.toIntOrNull() == null){quantityError = true; isValid = false}

                                    if(isValid){
                                        scope.launch{
                                            val product = Product(
                                                id = productId ?: 0,
                                                item = item,
                                                price = price.toDouble(),
                                                quantity = quantity.toInt(),
                                                imgPath = imagePath
                                            )
                                            if(isEditMode){
                                                viewModel.updateProduct(product)
                                            } else {
                                                viewModel.insertProduct(product)
                                            }

                                            onNavigateBack()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp)
                            ){
                                Text(
                                    text = if (isEditMode) "Update" else "Save",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPermissionDialog){
        AlertDialog(
            onDismissRequest = {showPermissionDialog = false},
            title = {
                Text(
                    text="Permisson Required",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This app needs permission to access photos. Please grant permission in settings."
                )
            },
            confirmButton = {
                TextButton(onClick = {showPermissionDialog = false}) {
                    Text(text = "OK")
                }
            }
        )
    }
}
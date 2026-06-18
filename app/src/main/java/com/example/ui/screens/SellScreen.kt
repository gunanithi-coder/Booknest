package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellScreen(
    viewModel: BookViewModel,
    onPublishSuccess: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()

    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Academic Books") }
    var language by remember { mutableStateOf("English") }
    var condition by remember { mutableStateOf("Like New") }
    var priceText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Cover customisations
    var selectedColorHex by remember { mutableStateOf("#4F46E5") } // Default Indigo
    var selectedGradientIndex by remember { mutableStateOf(1) }

    val categories = listOf(
        "Academic Books", "Programming Books", "Self-Help Books", 
        "Rare and Collectible Books", "Novels", "Comics", "College Books"
    )
    val conditions = listOf("New", "Like New", "Good", "Fair", "Worn")

    val coverColors = listOf(
        "#4F46E5" to "Indigo",
        "#065F46" to "Forest Green",
        "#7F1D1D" to "Crimson Red",
        "#D97706" to "Amber",
        "#1F2937" to "Charcoal",
        "#1E3A8A" to "Navy"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("sell_screen")
    ) {
        // Welcome and intro
        Text(
            text = "Publish Book Listing",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Your listing will instantly register on the local BookNest index and be visible to matching local searchers.",
            fontSize = 12.sp,
            color = InkMuted,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (currentUser == null) {
            // Guest alert
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = "You must be signed in to list books. Please sign in or register in the Profile tab first.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Core Listing Form
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "1. Book Visual Design Preview",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Cover Visual Preview directly inside the creator sheet
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BookCoverVisual(
                            title = if (title.isBlank()) "Your Book Title" else title,
                            author = if (author.isBlank()) "Your Author Name" else author,
                            category = category,
                            coverColorHex = selectedColorHex,
                            gradientIndex = selectedGradientIndex,
                            modifier = Modifier
                                .size(width = 110.dp, height = 160.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Cover Color Band",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = InkMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            // Color preset nodes
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                for ((hex, _) in coverColors) {
                                    val isSelected = selectedColorHex == hex
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(
                                                Color(android.graphics.Color.parseColor(hex)),
                                                CircleShape
                                            )
                                            .border(
                                                width = if (isSelected) 2.dp else 0.dp,
                                                color = if (isSelected) BookGold else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable { selectedColorHex = hex }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Luster Shaders",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = InkMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            // Shaders type selection
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                for (idx in 1..4) {
                                    val isSelected = selectedGradientIndex == idx
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable { selectedGradientIndex = idx }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Shader $idx",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Core Info Card Form
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "2. Catalog Specifications",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Book Title") },
                        modifier = Modifier.fillMaxWidth().testTag("title_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Author / Editor") },
                        modifier = Modifier.fillMaxWidth().testTag("author_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = isbn,
                            onValueChange = { isbn = it },
                            label = { Text("ISBN-13 Number") },
                            modifier = Modifier.weight(1.2f).testTag("isbn_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("Price ($)") },
                            modifier = Modifier.weight(0.8f).testTag("price_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }

                    // Genre Selector chips
                    Text(
                        text = "Book Genre / Category",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkMuted
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (cat in categories) {
                            val isSelected = category == cat
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                        RoundedCornerShape(30.dp)
                                    )
                                    .clickable { category = cat }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Condition selection
                    Text(
                        text = "Physical Surface Condition",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkMuted
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (cond in conditions) {
                            val isSelected = condition == cond
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { condition = cond }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cond,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Describe Cover, Edition, Notes...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("description_input"),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 4
                    )

                    // Contact Check
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = InkMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Registered Location: ${currentUser?.city}. Edit city details inside your Profile Settings to adjust match zones.",
                                fontSize = 11.sp,
                                color = InkMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            val priceParsed = priceText.toDoubleOrNull()
                            if (title.isBlank() || author.isBlank() || priceParsed == null || priceParsed <= 0) {
                                Toast.makeText(context, "Please complete Book Title, Author and a valid positive Price.", Toast.LENGTH_LONG).show()
                            } else {
                                viewModel.publishBook(
                                    title = title,
                                    author = author,
                                    isbn = if (isbn.isBlank()) "0000000000000" else isbn,
                                    category = category,
                                    language = language,
                                    description = description,
                                    condition = condition,
                                    price = priceParsed,
                                    coverGradientIndex = selectedGradientIndex,
                                    coverColorHex = selectedColorHex,
                                    onSuccess = {
                                        Toast.makeText(context, "Book listed successfully!", Toast.LENGTH_SHORT).show()
                                        // Reset fields
                                        title = ""
                                        author = ""
                                        isbn = ""
                                        priceText = ""
                                        description = ""
                                        onPublishSuccess()
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("publish_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Publish Direct Listing",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}

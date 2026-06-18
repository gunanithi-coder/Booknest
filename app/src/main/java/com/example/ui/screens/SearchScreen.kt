package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookEntity
import com.example.ui.theme.*
import com.example.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: BookViewModel,
    onSelectBook: (BookEntity) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedCondition by viewModel.selectedCondition.collectAsState()
    val maxPrice by viewModel.maxPriceFilter.collectAsState()
    val books by viewModel.books.collectAsState()

    // AI Chat Panel overlay state
    var isChatOpen by remember { mutableStateOf(false) }
    var chatInputText by remember { mutableStateOf("") }
    val aiMessages by viewModel.aiMessages.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    val categories = listOf(
        "All Genres", "Academic Books", "Programming Books", 
        "Self-Help Books", "Rare and Collectible Books", "Novels", "Comics", "College Books"
    )
    val conditions = listOf("All Conditions", "New", "Like New", "Good", "Fair", "Worn")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("search_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Header Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Catalog Inventory",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Input search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text("Title, author, or ISBN...", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onPrimary)
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                            cursorColor = MaterialTheme.colorScheme.onPrimary,
                            focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // Category Chips Scroller
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (cat in categories) {
                    val isSelected = (cat == "All Genres" && selectedCategory.isEmpty()) || (cat == selectedCategory)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (cat == "All Genres") {
                                viewModel.updateSelectedCategory("")
                            } else {
                                viewModel.updateSelectedCategory(cat)
                            }
                        },
                        label = { Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            // Filters Drawer Summary (Condition chips & budget slider)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Refine Listing Indexes",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Condition Scroller
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (cond in conditions) {
                            val isSelected = (cond == "All Conditions" && selectedCondition.isEmpty()) || (cond == selectedCondition)
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        if (cond == "All Conditions") {
                                            viewModel.updateSelectedCondition("")
                                        } else {
                                            viewModel.updateSelectedCondition(cond)
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = cond,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Price Slider
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Max Price: $${maxPrice.toInt()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = InkMuted,
                            modifier = Modifier.width(90.dp)
                        )
                        Slider(
                            value = maxPrice.toFloat(),
                            onValueChange = { viewModel.updateMaxPriceFilter(it.toDouble()) },
                            valueRange = 10f..200f,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Results Headline
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${books.size} listings found",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkMuted
                )
                
                if (selectedCategory.isNotEmpty() || selectedCondition.isNotEmpty() || searchQuery.isNotEmpty()) {
                    Text(
                        text = "Reset All",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.clickable {
                            viewModel.updateSearchQuery("")
                            viewModel.updateSelectedCategory("")
                            viewModel.updateSelectedCondition("")
                            viewModel.updateMaxPriceFilter(150.0)
                        }
                    )
                }
            }

            // Lazy List of Books
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                if (books.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = InkMuted.copy(alpha = 0.5f),
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "No Books Found",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = FontFamily.Serif
                            )
                            Text(
                                text = "Try adjusting your genre filters, pricing slider, or type a different title term.",
                                fontSize = 12.sp,
                                color = InkMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 4.dp)
                            )
                        }
                    }
                } else {
                    items(books) { book ->
                        BookVerticalRow(book = book, onClick = { onSelectBook(book) })
                    }
                }
            }
        }

        // FLOATING ACTION TRIGGERS: AI Assistant Toggle & Quick Sell Button
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // AI Assistant Round FAB button
            ExtendedFloatingActionButton(
                onClick = { isChatOpen = !isChatOpen },
                containerColor = BookGold,
                contentColor = DeepEspresso,
                shape = CircleShape,
                modifier = Modifier.shadow(8.dp, CircleShape),
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    imageVector = if (isChatOpen) Icons.Default.Close else Icons.Default.Star,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Chat AI", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        // SLIDER DRAWER PANEL: AI Literary Curator
        AnimatedVisibility(
            visible = isChatOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .align(Alignment.BottomCenter)
                .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                // Header of Drawer panel
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(BookGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = DeepEspresso,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "BookNest AI Advisor",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Ask about textbooks, pricing, or review matches",
                                fontSize = 10.sp,
                                color = InkMuted
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = { viewModel.clearChat() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Clear Chat", tint = InkMuted)
                        }
                        IconButton(onClick = { isChatOpen = false }) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Close", tint = InkMuted)
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                // Scrollable Chat Message nodes
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    items(aiMessages) { (text, isUser) ->
                        ChatBubbleNode(text = text, isUser = isUser)
                    }

                    if (isAiLoading) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Analyzing curriculum catalog databases...",
                                    fontSize = 11.sp,
                                    color = InkMuted
                                )
                            }
                        }
                    }
                }

                // Chat text input footer toolbar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = chatInputText,
                            onValueChange = { chatInputText = it },
                            placeholder = { Text("Search academic guides, optimize list...", fontSize = 12.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ai_input"),
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 2,
                            singleLine = true
                        )

                        IconButton(
                            onClick = {
                                if (chatInputText.isNotBlank()) {
                                    viewModel.sendUserMessage(chatInputText)
                                    chatInputText = ""
                                }
                            },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                .size(40.dp),
                            enabled = chatInputText.isNotBlank() && !isAiLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubbleNode(
    text: String,
    isUser: Boolean
) {
    val containerColor = if (isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
    }

    val textColor = if (isUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    val arrangement = if (isUser) {
        Arrangement.End
    } else {
        Arrangement.Start
    }

    val shape = if (isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 2.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 2.dp, bottomEnd = 16.dp)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = arrangement
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(containerColor, shape)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

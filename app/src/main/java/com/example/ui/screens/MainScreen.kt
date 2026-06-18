package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
fun MainScreen(
    viewModel: BookViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current

    var selectedTabIndex by remember { mutableStateOf(0) }
    var selectedDetailBook by remember { mutableStateOf<BookEntity?>(null) }
    
    // Contact Simulated Dialogue State
    var contactSimulationOpen by remember { mutableStateOf(false) }
    var simMessageText by remember { mutableStateOf("") }

    // If there is no authenticated session, force AuthScreen first
    if (currentUser == null) {
        AuthScreen(viewModel = viewModel, onAuthSuccess = {
            Toast.makeText(context, "Logon Secure! Welcome to BookNest.", Toast.LENGTH_SHORT).show()
            selectedTabIndex = 0
        })
    } else {
        Scaffold(
            bottomBar = {
                // Editorial Custom Bottom Tab Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp)
                        .testTag("bottom_nav_bar")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .height(72.dp)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Home Item
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTabIndex = 0 }
                                .padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home",
                                tint = if (selectedTabIndex == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.40f),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "HOME",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = if (selectedTabIndex == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.40f)
                            )
                        }

                        // Search Item
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTabIndex = 1 }
                                .padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = if (selectedTabIndex == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.40f),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "SEARCH",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = if (selectedTabIndex == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.40f)
                            )
                        }

                        // Elevated Sell Item
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTabIndex = 2 }
                                .offset(y = (-10).dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .shadow(4.dp, CircleShape)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Sell",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "SELL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Profile Item
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTabIndex = 3 }
                                .padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = if (selectedTabIndex == 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.40f),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "PROFILE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = if (selectedTabIndex == 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.40f)
                            )
                        }
                    }
                }
            },
            contentWindowInsets = WindowInsets.navigationBars
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Crossfade transaction between active screens
                Crossfade(targetState = selectedTabIndex, label = "tab_switches") { index ->
                    when (index) {
                        0 -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToSearch = { selectedTabIndex = 1 },
                            onNavigateToSell = { selectedTabIndex = 2 },
                            onSelectBook = { selectedDetailBook = it }
                        )
                        1 -> SearchScreen(
                            viewModel = viewModel,
                            onSelectBook = { selectedDetailBook = it }
                        )
                        2 -> SellScreen(
                            viewModel = viewModel,
                            onPublishSuccess = { selectedTabIndex = 3 } // Move to profile listings on success
                        )
                        3 -> ProfileScreen(
                            viewModel = viewModel,
                            onLogout = { selectedTabIndex = 0 }
                        )
                    }
                }

                // HIGH-FIDELITY OVERLAY DIALOGUE: Full Book Specifications Details Sheet
                AnimatedVisibility(
                    visible = selectedDetailBook != null,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    selectedDetailBook?.let { book ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { selectedDetailBook = null }, // Tap background to dismiss
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            // Specifications Modal container
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.85f)
                                    .clickable(enabled = false) { /* stop propagation */ }
                                    .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    // Header controls bar
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Listing Details",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Serif,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        IconButton(
                                            onClick = { selectedDetailBook = null },
                                            modifier = Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), CircleShape)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }

                                    // Visual 3D Book Layout Section
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.04f))
                                            .padding(vertical = 24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        BookCoverVisual(
                                            title = book.title,
                                            author = book.author,
                                            category = book.category,
                                            coverColorHex = book.coverColorHex,
                                            gradientIndex = book.coverGradientIndex,
                                            modifier = Modifier
                                                .size(width = 150.dp, height = 220.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    }

                                    // Metadata fields Specifications
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        // Title and Author
                                        Column {
                                            Text(
                                                text = book.category.uppercase().replace(" BOOKS", ""),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BookGold,
                                                letterSpacing = 2.sp
                                            )
                                            Text(
                                                text = book.title,
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontFamily = FontFamily.Serif,
                                                color = MaterialTheme.colorScheme.primary,
                                                lineHeight = 26.sp
                                            )
                                            Text(
                                                text = "Written by ${book.author}",
                                                fontSize = 14.sp,
                                                color = InkMuted,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }

                                        // Badges Row (Condition / Price / City)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            BadgeSpec(label = "Price Tag", value = "$${String.format("%.2f", book.price)}", color = MaterialTheme.colorScheme.primary)
                                            BadgeSpec(label = "Condition", value = book.condition, color = BookGold)
                                            BadgeSpec(label = "Trading Zone", value = book.city, color = CobaltIntellect)
                                        }

                                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                                        // Book Description
                                        Column {
                                            Text(
                                                text = "PUBLISHER'S NOTES",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.primary,
                                                letterSpacing = 1.sp
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = if (book.description.isBlank()) "No secondary specifications provided by the seller." else book.description,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                                lineHeight = 18.sp
                                            )
                                        }

                                        // Details Table (ISBN / Language / Registered)
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(14.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                SpecsTableRow(label = "ISBN-13 Barcode", value = book.isbn)
                                                SpecsTableRow(label = "Edition Language", value = book.language)
                                                SpecsTableRow(label = "Registered Book Depot", value = "${book.city}, USA")
                                                SpecsTableRow(label = "Listing Status", value = "ACTIVE CATALOG INDEX")
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // SELLER SPECS & INTERACTIVE DIRECT SECURE CONTACT FORM
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = BookGold.copy(alpha = 0.08f)),
                                            shape = RoundedCornerShape(12.dp),
                                            border = CardDefaults.outlinedCardBorder()
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(40.dp)
                                                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = book.sellerName.split(" ").map { it.take(1) }.joinToString("").uppercase(),
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.onPrimary
                                                        )
                                                    }

                                                    Spacer(modifier = Modifier.width(12.dp))

                                                    Column {
                                                        Text(
                                                            text = "Listed by ${book.sellerName}",
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                        Text(
                                                            text = "Top Listing Partner • Broker rating 5.0 ★",
                                                            fontSize = 11.sp,
                                                            color = InkMuted
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(14.dp))

                                                if (contactSimulationOpen) {
                                                    // Simulation Contact Dialogue Input Box
                                                    Text(
                                                        text = "Construct SMS to ${book.sellerName}",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    OutlinedTextField(
                                                        value = simMessageText,
                                                        onValueChange = { simMessageText = it },
                                                        placeholder = { Text("Hello! Is '${book.title}' still available for exchange? I can trade in ${book.city} tomorrow.", fontSize = 12.sp) },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Button(
                                                            onClick = {
                                                                contactSimulationOpen = false
                                                                simMessageText = ""
                                                            },
                                                            modifier = Modifier.weight(1f),
                                                            shape = RoundedCornerShape(8.dp),
                                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                                                        ) {
                                                            Text("Abort", fontSize = 12.sp)
                                                        }

                                                        Button(
                                                            onClick = {
                                                                Toast.makeText(context, "Secure message dispatched successfully to ${book.sellerName}!", Toast.LENGTH_LONG).show()
                                                                contactSimulationOpen = false
                                                                simMessageText = ""
                                                            },
                                                            modifier = Modifier.weight(1.5f),
                                                            shape = RoundedCornerShape(8.dp),
                                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                                        ) {
                                                            Text("Transmit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                } else {
                                                    // Core Call Interaction actions
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Button(
                                                            onClick = { contactSimulationOpen = true },
                                                            modifier = Modifier.weight(1.2f),
                                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                                            shape = RoundedCornerShape(8.dp)
                                                        ) {
                                                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text("Compose Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                        }

                                                        Button(
                                                            onClick = {
                                                                Toast.makeText(context, "Broker secure phone index requested: Dialing ${book.sellerPhone}.", Toast.LENGTH_LONG).show()
                                                            },
                                                            modifier = Modifier.weight(0.8f),
                                                            colors = ButtonDefaults.buttonColors(containerColor = BookGold, contentColor = DeepEspresso),
                                                            shape = RoundedCornerShape(8.dp)
                                                        ) {
                                                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text("Call Seller", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(40.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BadgeSpec(label: String, value: String, color: Color) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(label, fontSize = 9.sp, color = InkMuted, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SpecsTableRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = InkMuted, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
    }
}

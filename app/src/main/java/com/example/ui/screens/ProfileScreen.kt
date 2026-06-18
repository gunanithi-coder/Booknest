package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookEntity
import com.example.ui.theme.*
import com.example.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: BookViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val userListings by viewModel.userListings.collectAsState()

    var bioText by remember { mutableStateOf("") }
    var phoneText by remember { mutableStateOf("") }
    var cityText by remember { mutableStateOf("") }
    var isEditingProfile by remember { mutableStateOf(false) }

    // Synchronize editing variables upon loading profile state
    LaunchedEffect(currentUser, isEditingProfile) {
        currentUser?.let {
            if (!isEditingProfile) {
                bioText = it.bio
                phoneText = it.phone
                cityText = it.city
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("profile_screen")
    ) {
        if (currentUser == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A serious routing anomaly has occurred. Please authenticate again.",
                    color = InkMuted
                )
            }
        } else {
            val user = currentUser!!

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Header Profile Hero Banner
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // User Avatar initials
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.name.split(" ").map { it.take(1) }.joinToString("").uppercase(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = user.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = user.email,
                            fontSize = 12.sp,
                            color = InkMuted
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // User Metrics Row (Earnings / Listings / Star Rating)
                        val activeCount = userListings.count { it.status == "ACTIVE" }
                        val soldCount = userListings.count { it.status == "SOLD" }
                        val estimatedEarnings = userListings.filter { it.status == "SOLD" }.sumOf { it.price }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MetricNode(label = "Listings", valText = "${activeCount} Open")
                            Divider(modifier = Modifier.height(30.dp).width(1.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            MetricNode(label = "Sold Books", valText = "$soldCount items")
                            Divider(modifier = Modifier.height(30.dp).width(1.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            MetricNode(label = "Total Gained", valText = "$${String.format("%.2f", estimatedEarnings)}")
                        }
                    }
                }

                // Profile Configuration Options / Editing
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Trader Specifications",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontFamily = FontFamily.Serif
                                )

                                Text(
                                    text = if (isEditingProfile) "Save Details" else "Edit Core",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clickable {
                                            if (isEditingProfile) {
                                                if (phoneText.isBlank() || cityText.isBlank()) {
                                                    Toast.makeText(context, "Phone and City cannot be blank.", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    viewModel.updateProfile(bioText, phoneText, cityText)
                                                    isEditingProfile = false
                                                    Toast.makeText(context, "Profile updated securely!", Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                isEditingProfile = true
                                            }
                                        }
                                        .padding(4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (isEditingProfile) {
                                OutlinedTextField(
                                    value = bioText,
                                    onValueChange = { bioText = it },
                                    label = { Text("Trader Bio / Notes") },
                                    modifier = Modifier.fillMaxWidth().testTag("edit_bio"),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = cityText,
                                        onValueChange = { cityText = it },
                                        label = { Text("Trade City") },
                                        modifier = Modifier.weight(1f).testTag("edit_city"),
                                        shape = RoundedCornerShape(10.dp),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = phoneText,
                                        onValueChange = { phoneText = it },
                                        label = { Text("Phone") },
                                        modifier = Modifier.weight(1f).testTag("edit_phone"),
                                        shape = RoundedCornerShape(10.dp),
                                        singleLine = true
                                    )
                                }
                            } else {
                                // Static representation
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    ProfileSpecRow(icon = Icons.Default.Info, label = "Bio Notebook", valText = user.bio)
                                    ProfileSpecRow(icon = Icons.Default.Place, label = "Active Node", valText = user.city)
                                    ProfileSpecRow(icon = Icons.Default.Phone, label = "Match Contact", valText = user.phone)
                                }
                            }
                        }
                    }
                }

                // Active User Listings Title Header
                item {
                    Text(
                        text = "Manage Your Listings",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                if (userListings.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "You haven't listed any books on BookNest yet. Click Sell at the bottom to list one!",
                                fontSize = 12.sp,
                                color = InkMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                    }
                } else {
                    items(userListings) { book ->
                        UserListingControlRow(
                            book = book,
                            onToggleSold = {
                                if (book.status == "ACTIVE") {
                                    viewModel.markAsSold(book.id)
                                    Toast.makeText(context, "Listing marked as Sold! Great trade!", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.relistBook(book.id)
                                    Toast.makeText(context, "Listing cataloged as Active again!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onDelete = {
                                viewModel.deleteListing(book)
                                Toast.makeText(context, "Listing index deleted.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                // Logout Call triggers
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            viewModel.logout()
                            onLogout()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out Securely", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricNode(label: String, valText: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valText,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = InkMuted
        )
    }
}

@Composable
fun ProfileSpecRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, valText: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp).offset(y = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = label, fontSize = 10.sp, color = InkMuted, fontWeight = FontWeight.SemiBold)
            Text(text = valText, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun UserListingControlRow(
    book: BookEntity,
    onToggleSold: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BookCoverVisual(
                title = book.title,
                author = book.author,
                category = book.category,
                coverColorHex = book.coverColorHex,
                gradientIndex = book.coverGradientIndex,
                modifier = Modifier
                    .size(width = 46.dp, height = 66.dp)
                    .clip(RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "$${String.format("%.2f", book.price)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Render status badge
                Box(
                    modifier = Modifier
                        .background(
                            if (book.status == "ACTIVE") MintSuccess.copy(alpha = 0.15f) else InkMuted.copy(alpha = 0.15f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (book.status == "ACTIVE") "ACTIVE MATCH" else "SOLD / EXCHANGED",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (book.status == "ACTIVE") MintSuccess else MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Controls actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mark sold / Mark active
                IconButton(onClick = onToggleSold) {
                    Icon(
                        imageVector = if (book.status == "ACTIVE") Icons.Default.CheckCircle else Icons.Default.Refresh,
                        contentDescription = "Toggle Sold",
                        tint = if (book.status == "ACTIVE") MintSuccess else MaterialTheme.colorScheme.primary
                    )
                }

                // Delete
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

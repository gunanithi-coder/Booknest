package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
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
fun HomeScreen(
    viewModel: BookViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToSell: () -> Unit,
    onSelectBook: (BookEntity) -> Unit
) {
    val books by viewModel.books.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    
    val categories = listOf(
        "Academic Books", "Programming Books", "Self-Help Books", 
        "Rare and Collectible Books", "Novels", "Comics", "College Books"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .testTag("home_screen")
    ) {
        // ------------------ BRAND HEADER (Editorial Style) ------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BOOKNEST.",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                letterSpacing = (-1.5).sp,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Hello Bubble & Mini Notification Dot
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Hello, ${currentUser?.name?.split(" ")?.firstOrNull() ?: "Reader"}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    letterSpacing = 0.5.sp
                )
                
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant, // #F2EFE8 Soft Sand bg
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            shape = CircleShape
                        )
                        .clickable { /* Profile or Quick Action */ }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // ------------------ FEATURED HERO (Editorial Style - Asymmetric) ------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant // #F2EFE8 WarmSand
            ),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Column: Text & CTA Button
                Column(
                    modifier = Modifier.weight(1.2f),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "EDITOR'S CHOICE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            letterSpacing = 1.8.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "The Art of Focus",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            lineHeight = 28.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "by J.M. Aris",
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            fontFamily = FontFamily.Serif,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // High contrast black pill CTA Button
                    Button(
                        onClick = onNavigateToSell,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary, // #1A1A1A Ink
                            contentColor = MaterialTheme.colorScheme.onPrimary // #FFFFFF White
                        ),
                        shape = RoundedCornerShape(26.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
                        modifier = Modifier.testTag("sell_featured_button")
                    ) {
                        Text(
                            text = "Sell Books — Earn Cash",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Right Column: Asymmetric Decorative Book Placement
                Box(
                    modifier = Modifier
                        .weight(0.8f)
                        .height(130.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    // Overlapping book background element
                    Box(
                        modifier = Modifier
                            .width(82.dp)
                            .height(115.dp)
                            .graphicsLayer(rotationZ = -6f)
                            .shadow(12.dp, RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 8.dp, bottomEnd = 8.dp))
                            .background(
                                color = MaterialTheme.colorScheme.primary, // #1A1A1A Ink/Charcoal
                                shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 8.dp, bottomEnd = 8.dp)
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(2.dp)
                                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                            )
                        }
                    }
                }
            }
        }

        // Custom Search Shortcut (Editorial Minimalist style)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .offset(y = (-10).dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(16.dp)
                )
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .clickable { onNavigateToSearch() }
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Search by title, author, or ISBN...",
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Go",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Horizontal Category Browser (Editorial lightweight chips)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (cat in categories) {
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable {
                            viewModel.updateSelectedCategory(cat)
                            onNavigateToSearch()
                        }
                        .padding(horizontal = 16.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Headline 1: Premium AI Book Curator Hook
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            colors = CardDefaults.cardColors(
                containerColor = BookGold.copy(alpha = 0.12f)
            ),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldenAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI LITERARY CURATOR DESK",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 2.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Consult our server-side agent about local curriculum textbooks and listings.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }
                
                Button(
                    onClick = {
                        viewModel.updateSearchQuery("")
                        onNavigateToSearch()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ask AI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Headline 2: Featured Vault Listings (Grid Horizontal Scroll)
        Text(
            text = "Featured Listings",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val featuredList = books.take(4)
            if (featuredList.isEmpty()) {
                Card(
                    modifier = Modifier.width(280.dp).padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No active books match filtering.", fontSize = 12.sp, color = InkMuted)
                    }
                }
            } else {
                for (book in featuredList) {
                    BookFeaturedCard(book = book, onClick = { onSelectBook(book) })
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Headline 3: Browse Library (Vertical list / recent arrivals)
        Text(
            text = "Recently Added Matches",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 12.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            val recentList = books.drop(4).take(4)
            val fullList = if (books.size > 4) recentList else books
            
            if (fullList.isEmpty() && books.isNotEmpty()) {
                // if books has < 4 elements, they are all in featured, so we show a hint
                Text(
                    text = "You've caught up with all listings! Check filters.",
                    fontSize = 12.sp,
                    color = InkMuted,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                )
            } else if (books.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "The BookNest marketplace index is clear. Click Sell to add a book!",
                        fontSize = 12.sp,
                        color = InkMuted,
                        modifier = Modifier.padding(20.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                for (book in fullList) {
                    BookVerticalRow(book = book, onClick = { onSelectBook(book) })
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

// 3D Rendering of Physical Book Covers using canvas offsets and subtle gradients
@Composable
fun BookCoverVisual(
    title: String,
    author: String,
    category: String,
    coverColorHex: String,
    gradientIndex: Int,
    modifier: Modifier = Modifier
) {
    val baseColor = try {
        Color(android.graphics.Color.parseColor(coverColorHex))
    } catch (e: Exception) {
        Color(0xFF8B4513)
    }

    // Gradient selections matched to literary genre
    val gradientBrush = when (gradientIndex) {
        1 -> Brush.verticalGradient(colors = listOf(baseColor, baseColor.copy(alpha = 0.8f), Color(0xFF1E1B4B))) // Cosmic Indigo
        2 -> Brush.verticalGradient(colors = listOf(baseColor, baseColor.copy(alpha = 0.85f), Color(0xFF022C22))) // Emerald Ivy
        3 -> Brush.verticalGradient(colors = listOf(baseColor, baseColor.copy(alpha = 0.82f), Color(0xFF450A0A))) // Crimson Burgundy
        4 -> Brush.verticalGradient(colors = listOf(baseColor, Color(0xFF78350F), Color(0xFF451A03))) // Amber Editorial
        5 -> Brush.horizontalGradient(colors = listOf(baseColor, baseColor.copy(alpha = 0.75f), Color(0xFF111827))) // Cyber Carbon
        else -> Brush.verticalGradient(colors = listOf(baseColor, baseColor.copy(alpha = 0.88f), Color(0xFF1C1917))) // Espresso Dark
    }

    Box(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 8.dp, bottomEnd = 8.dp))
            .background(gradientBrush, RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 8.dp, bottomEnd = 8.dp))
    ) {
        // Spine Side highlight shadow for realistic book-fold depth
        Canvas(modifier = Modifier.fillMaxHeight().width(12.dp).align(Alignment.CenterStart)) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.45f),
                        Color.Black.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                )
            )
        }

        // Inner page outlines or borders
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Gold horizontal bands on cover if rare
            if (category == "Rare and Collectible Books") {
                drawLine(
                    color = BookGold.copy(alpha = 0.6f),
                    start = Offset(0f, size.height * 0.15f),
                    end = Offset(size.width, size.height * 0.15f),
                    strokeWidth = 3f
                )
                drawLine(
                    color = BookGold.copy(alpha = 0.6f),
                    start = Offset(0f, size.height * 0.85f),
                    end = Offset(size.width, size.height * 0.85f),
                    strokeWidth = 3f
                )
            }
        }

        // Front Cover Typography Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 12.dp, end = 10.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = category.uppercase().replace(" BOOKS", ""),
                    fontSize = 7.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BookGold,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = FontFamily.Serif,
                    lineHeight = 13.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = author,
                fontSize = 8.sp,
                color = Color.White.copy(alpha = 0.75f),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BookFeaturedCard(
    book: BookEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            BookCoverVisual(
                title = book.title,
                author = book.author,
                category = book.category,
                coverColorHex = book.coverColorHex,
                gradientIndex = book.coverGradientIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                text = book.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "By ${book.author}",
                fontSize = 11.sp,
                color = InkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${String.format("%.2f", book.price)}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )

                // Condition Pill
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = book.condition,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun BookVerticalRow(
    book: BookEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    .size(width = 66.dp, height = 94.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.category.uppercase().replace(" BOOKS", ""),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = BookGold,
                    letterSpacing = 1.sp
                )
                
                Text(
                    text = book.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Author: ${book.author}",
                    fontSize = 12.sp,
                    color = InkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = InkMuted,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = book.city,
                        fontSize = 11.sp,
                        color = InkMuted
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "$${String.format("%.2f", book.price)}",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )

                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = book.condition,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

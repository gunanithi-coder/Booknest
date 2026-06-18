package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class BookRepository(private val db: AppDatabase) {
    private val bookDao = db.bookDao()
    private val userDao = db.userDao()

    val allBooks: Flow<List<BookEntity>> = bookDao.getAllBooks()

    fun searchBooks(query: String): Flow<List<BookEntity>> {
        return if (query.isBlank()) {
            bookDao.getAllBooks()
        } else {
            bookDao.searchBooks(query)
        }
    }

    fun getBooksBySeller(sellerId: Int): Flow<List<BookEntity>> {
        return bookDao.getBooksBySeller(sellerId)
    }

    suspend fun getBookById(id: Int): BookEntity? = withContext(Dispatchers.IO) {
        bookDao.getBookById(id)
    }

    suspend fun insertBook(book: BookEntity) = withContext(Dispatchers.IO) {
        bookDao.insertBook(book)
    }

    suspend fun updateBook(book: BookEntity) = withContext(Dispatchers.IO) {
        bookDao.updateBook(book)
    }

    suspend fun deleteBook(book: BookEntity) = withContext(Dispatchers.IO) {
        bookDao.deleteBook(book)
    }

    // --- User Auth Methods ---
    suspend fun getUserByEmail(email: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserByEmail(email)
    }

    suspend fun getUserById(id: Int): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserById(id)
    }

    suspend fun registerUser(user: UserEntity): UserEntity = withContext(Dispatchers.IO) {
        val id = userDao.insertUser(user)
        user.copy(id = id.toInt())
    }

    suspend fun updateUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.updateUser(user)
    }

    // --- Seeding ---
    suspend fun seedDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        if (bookDao.getBookCount() == 0) {
            // Create Sellers / Users
            val user1 = UserEntity(
                id = 1,
                name = "Sarah Jenkins",
                email = "sarah@booknest.db",
                passwordHash = "123456",
                city = "Boston",
                phone = "617-555-0192",
                bio = "Avid computer science graduate student selling my college textbooks."
            )
            val user2 = UserEntity(
                id = 2,
                name = "Alex Rivera",
                email = "alex@booknest.db",
                passwordHash = "123456",
                city = "Seattle",
                phone = "206-555-0143",
                bio = "Tech professional and lifelong collector of rare sci-fi and classic novels."
            )
            val user3 = UserEntity(
                id = 3,
                name = "Marcus Vance",
                email = "marcus@booknest.db",
                passwordHash = "123456",
                city = "Denver",
                phone = "303-555-0181",
                bio = "Owner of Vance Comic Desk. Re-selling pristine graphic novels and rare collectible books."
            )

            // Ignore conflict or handle insert
            try { userDao.insertUser(user1) } catch (e: Exception) {}
            try { userDao.insertUser(user2) } catch (e: Exception) {}
            try { userDao.insertUser(user3) } catch (e: Exception) {}

            // Seed Books
            val seedBooks = listOf(
                BookEntity(
                    id = 1,
                    title = "Cracking the Coding Interview",
                    author = "Gayle Laakmann McDowell",
                    isbn = "9780984782857",
                    category = "Programming Books",
                    language = "English",
                    description = "The ultimate software engineering technical interview preparation bible. Contains 189 programming questions, detailed solution walks, and system design breakdowns. Perfect condition; no notes or highlights.",
                    condition = "Like New",
                    price = 29.99,
                    coverGradientIndex = 0,
                    coverColorHex = "#1E293B", // Dark grey slate
                    sellerId = 1,
                    sellerName = "Sarah Jenkins",
                    sellerEmail = "sarah@booknest.db",
                    sellerPhone = "617-555-0192",
                    city = "Boston"
                ),
                BookEntity(
                    id = 2,
                    title = "Kotlin in Action",
                    author = "Dmitry Jemerov & Svetlana Isakova",
                    isbn = "9781617293290",
                    category = "Programming Books",
                    language = "English",
                    description = "Excellent comprehensive guide explaining Kotlin basics, coroutines, DSL building, and Java interoperability. Written by Kotlin team members. A must-have for modern Android developers.",
                    condition = "New",
                    price = 38.50,
                    coverGradientIndex = 1,
                    coverColorHex = "#4F46E5", // Elegant Indigo
                    sellerId = 1,
                    sellerName = "Sarah Jenkins",
                    sellerEmail = "sarah@booknest.db",
                    sellerPhone = "617-555-0192",
                    city = "Boston"
                ),
                BookEntity(
                    id = 3,
                    title = "The Hobbit (Collector's Leatherbound)",
                    author = "J.R.R. Tolkien",
                    isbn = "9780395177112",
                    category = "Rare and Collectible Books",
                    language = "English",
                    description = "Splendid collector's edition of the masterpiece hobbit tale. Covered in rich green faux leather with gold gilt details, red maps, and J.R.R. Tolkien's original full-color illustrations. Kept inside plastic protection; pristine state.",
                    condition = "New",
                    price = 115.00,
                    coverGradientIndex = 2,
                    coverColorHex = "#065F46", // Emerald Forest Green
                    sellerId = 2,
                    sellerName = "Alex Rivera",
                    sellerEmail = "alex@booknest.db",
                    sellerPhone = "206-555-0143",
                    city = "Seattle"
                ),
                BookEntity(
                    id = 4,
                    title = "Principles of Computer Architecture",
                    author = "David Patterson & John Hennessy",
                    isbn = "9780123744937",
                    category = "Academic Books",
                    language = "English",
                    description = "Crucial college text defining RISC architectures, memory pipelining, and multicore processors. Slightly worn corner from college backpack carrying, but completely clean pages inside.",
                    condition = "Good",
                    price = 45.00,
                    coverGradientIndex = 3,
                    coverColorHex = "#7F1D1D", // Burgundy Crimson
                    sellerId = 1,
                    sellerName = "Sarah Jenkins",
                    sellerEmail = "sarah@booknest.db",
                    sellerPhone = "617-555-0192",
                    city = "Boston"
                ),
                BookEntity(
                    id = 5,
                    title = "Atomic Habits",
                    author = "James Clear",
                    isbn = "9780735211292",
                    category = "Self-Help Books",
                    language = "English",
                    description = "An easy and proven way to build good habits and break bad ones. The legendary self-improvement handbook detailing the framework of compounding 1% gains daily. Brand new condition.",
                    condition = "New",
                    price = 15.00,
                    coverGradientIndex = 4,
                    coverColorHex = "#D97706", // Literary Amber / Gold
                    sellerId = 2,
                    sellerName = "Alex Rivera",
                    sellerEmail = "alex@booknest.db",
                    sellerPhone = "206-555-0143",
                    city = "Seattle"
                ),
                BookEntity(
                    id = 6,
                    title = "Batman: The Dark Knight Returns",
                    author = "Frank Miller",
                    isbn = "9781563893421",
                    category = "Comics",
                    language = "English",
                    description = "Deluxe master comic book standard. Redefined the dark superhero comic trend. Standard comic print, soft cover with gorgeous, gritty drawings. Light spine crease.",
                    condition = "Good",
                    price = 18.25,
                    coverGradientIndex = 5,
                    coverColorHex = "#1F2937", // Cyber Charcoal
                    sellerId = 3,
                    sellerName = "Marcus Vance",
                    sellerEmail = "marcus@booknest.db",
                    sellerPhone = "303-555-0181",
                    city = "Denver"
                ),
                BookEntity(
                    id = 7,
                    title = "Calculus: Early Transcendentals",
                    author = "James Stewart",
                    isbn = "9780538497909",
                    category = "College Books",
                    language = "English",
                    description = "8th Edition. Perfect companion text for college Calculus I, II, & III. Some minor pencil notations in Chapter 2, otherwise neat. Price reduced for quick sell.",
                    condition = "Fair",
                    price = 52.00,
                    coverGradientIndex = 6,
                    coverColorHex = "#1E3A8A", // Deep Navy Blue
                    sellerId = 1,
                    sellerName = "Sarah Jenkins",
                    sellerEmail = "sarah@booknest.db",
                    sellerPhone = "617-555-0192",
                    city = "Boston"
                )
            )

            // Insert seeded books
            for (book in seedBooks) {
                bookDao.insertBook(book)
            }
        }
    }
}

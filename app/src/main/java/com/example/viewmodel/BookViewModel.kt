package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiHelper
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = BookRepository(database)

    // Ensure database is seeded with beautiful premium books on launch
    init {
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
        }
    }

    // --- Authentication State ---
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    // --- Book State Flow ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedCondition = MutableStateFlow("")
    val selectedCondition: StateFlow<String> = _selectedCondition.asStateFlow()

    private val _maxPriceFilter = MutableStateFlow<Double>(150.0)
    val maxPriceFilter: StateFlow<Double> = _maxPriceFilter.asStateFlow()

    // Reactive list of filtered books matching search parameters
    val books: StateFlow<List<BookEntity>> = combine(
        repository.allBooks,
        _searchQuery,
        _selectedCategory,
        _selectedCondition,
        _maxPriceFilter
    ) { bookList, query, cat, cond, price ->
        bookList.filter { book ->
            val matchesQuery = query.isBlank() || 
                    book.title.contains(query, ignoreCase = true) || 
                    book.author.contains(query, ignoreCase = true) || 
                    book.isbn.contains(query, ignoreCase = true)
            
            val matchesCat = cat.isBlank() || book.category == cat
            val matchesCond = cond.isBlank() || book.condition == cond
            val matchesPrice = book.price <= price
            val matchesStatus = book.status == "ACTIVE" // Only browse active ones
            
            matchesQuery && matchesCat && matchesCond && matchesPrice && matchesStatus
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current logged-in user's active listings
    val userListings: StateFlow<List<BookEntity>> = combine(
        repository.allBooks,
        _currentUser
    ) { bookList, user ->
        if (user == null) emptyList()
        else bookList.filter { it.sellerId == user.id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- AI Assistant Chat Flow ---
    private val _aiMessages = MutableStateFlow<List<Pair<String, Boolean>>>(
        listOf("Hello! I am BookNest's AI Assistant. Ask me to recommend textbooks, check valuations, or optimize your book description!" to false)
    )
    val aiMessages: StateFlow<List<Pair<String, Boolean>>> = _aiMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // --- Actions ---

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun updateSelectedCondition(cond: String) {
        _selectedCondition.value = cond
    }

    fun updateMaxPriceFilter(price: Double) {
        _maxPriceFilter.value = price
    }

    // --- Authentication ---
    fun login(email: String, passwordText: String, onSuccess: () -> Unit) {
        if (email.isBlank() || passwordText.isBlank()) {
            _authError.value = "Email and Password cannot be blank"
            return
        }

        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            val user = repository.getUserByEmail(email)
            if (user != null && user.passwordHash == passwordText) {
                _currentUser.value = user
                _authLoading.value = false
                onSuccess()
            } else {
                _authError.value = "Invalid credentials. Try Sarah Jenkins (email: sarah@booknest.db, password: '123456') or register a new account!"
                _authLoading.value = false
            }
        }
    }

    fun register(name: String, email: String, passwordText: String, city: String, phone: String, onSuccess: () -> Unit) {
        if (name.isBlank() || email.isBlank() || passwordText.isBlank() || city.isBlank() || phone.isBlank()) {
            _authError.value = "All fields are required"
            return
        }

        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            val existing = repository.getUserByEmail(email)
            if (existing != null) {
                _authError.value = "Email is already registered. Please login instead."
                _authLoading.value = false
            } else {
                val newUser = UserEntity(
                    name = name,
                    email = email,
                    passwordHash = passwordText,
                    city = city,
                    phone = phone
                )
                val registered = repository.registerUser(newUser)
                _currentUser.value = registered
                _authLoading.value = false
                onSuccess()
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _authError.value = null
    }

    fun updateProfile(bio: String, phone: String, city: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updatedUser = user.copy(bio = bio, phone = phone, city = city)
            repository.updateUser(updatedUser)
            _currentUser.value = updatedUser
        }
    }

    // --- Book Listing Publication ---
    fun publishBook(
        title: String,
        author: String,
        isbn: String,
        category: String,
        language: String,
        description: String,
        condition: String,
        price: Double,
        coverGradientIndex: Int,
        coverColorHex: String,
        onSuccess: () -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val newBook = BookEntity(
                title = title,
                author = author,
                isbn = isbn,
                category = category,
                language = language,
                description = description,
                condition = condition,
                price = price,
                coverGradientIndex = coverGradientIndex,
                coverColorHex = coverColorHex,
                sellerId = user.id,
                sellerName = user.name,
                sellerEmail = user.email,
                sellerPhone = user.phone,
                city = user.city
            )
            repository.insertBook(newBook)
            onSuccess()
        }
    }

    fun markAsSold(bookId: Int) {
        viewModelScope.launch {
            val book = repository.getBookById(bookId)
            if (book != null) {
                val updated = book.copy(status = "SOLD")
                repository.updateBook(updated)
            }
        }
    }

    fun relistBook(bookId: Int) {
        viewModelScope.launch {
            val book = repository.getBookById(bookId)
            if (book != null) {
                val updated = book.copy(status = "ACTIVE")
                repository.updateBook(updated)
            }
        }
    }

    fun deleteListing(book: BookEntity) {
        viewModelScope.launch {
            repository.deleteBook(book)
        }
    }

    // --- AI Assistant Interactions ---
    fun sendUserMessage(msg: String) {
        if (msg.isBlank()) return
        val currentLogs = _aiMessages.value.toMutableList()
        currentLogs.add(msg to true)
        _aiMessages.value = currentLogs

        viewModelScope.launch {
            _isAiLoading.value = true
            val systemInstruction = """
                You are BookNest AI, a clever startup book concierge advisor built inside the BookNest marketplace app.
                Keep responses concise, warm, highly helpful, under 3 paragraphs, using markdown tables or bullet lists.
                Respond expertly regarding study recommendations (Calculus, Java, competitive exams), book pricing benchmarks, or listing hooks.
            """.trimIndent()
            
            val aiResponse = GeminiHelper.getBookAdvice(msg, systemInstruction)
            val updatedLogs = _aiMessages.value.toMutableList()
            updatedLogs.add(aiResponse to false)
            _aiMessages.value = updatedLogs
            _isAiLoading.value = false
        }
    }

    fun clearChat() {
        _aiMessages.value = listOf(
            "Hello! I am BookNest's AI Assistant. Ask me to recommend textbooks, check valuations, or optimize your book description!" to false
        )
    }
}

package pt.ipleiria.travelbook.Viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ResponseStoppedException
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pt.ipleiria.travelbook.Models.Location
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pt.ipleiria.travelbook.Repositories.LocationRepository
import java.text.SimpleDateFormat
import java.util.Locale

class LocationViewModel(
    private val repo: LocationRepository = LocationRepository()
) : ViewModel() {

    var locations by mutableStateOf<List<Location>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var lastError by mutableStateOf<String?>(null)
        private set

    var aiSuggestions by mutableStateOf<List<String>>(emptyList())
        private set

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    init {
        observeLocations()
    }

    private fun observeLocations() {
        isLoading = true
        repo.observeLocations(
            onChange = {
                locations = it
                isLoading = false
            }
        )
    }

    private suspend fun ensureUserSignedIn(): FirebaseUser {
        val auth = FirebaseAuth.getInstance()
        return auth.currentUser ?: auth.signInAnonymously().await().user
        ?: throw Exception("Anonymous sign-in failed")
    }

    fun generativeModel() : GenerativeModel {
        val config = generationConfig {
            candidateCount = 1
            maxOutputTokens = 750
            temperature = 0.8f
            topK = 30
            topP = 0.8f
        }
        return Firebase.ai().generativeModel("gemini-2.5-flash", config)
    }

    private suspend fun fetchAiSuggestions(location: String, country: String?,
                                           startDateText: String?, endDateText: String?): String {
        val user = ensureUserSignedIn()
        val token = user.getIdToken(true).await().token
        
        val prompt = """
            You are an expert travel guide.
            Generate a simple, concise bullet list (up to 5 items) of travel activities in $location 
            ${if (!country.isNullOrBlank()) ", $country" else ""}.
            ${if (!startDateText.isNullOrBlank()) "\nThe voyage starting date is $startDateText." else ""}.
            ${if (!endDateText.isNullOrBlank()) "\nThe voyage ending date is $endDateText." else ""}.
            ${if (!endDateText.isNullOrBlank() || !endDateText.isNullOrBlank()) "\nThe temporary gap shouldn't be " +
                "limitative, only use if specific, worthy activities occur in these dates in $location" else ""}.
            
            The list should include key attractions, food, or cultural experiences.
            Use only short phrases, names of places, foods, or activities — no extra descriptions.
            
            Each item should fit on one line.
            
            If "$location" is not a known or valid travel destination, reply only with:
            ⚠️ No travel suggestions available for this location.
            
            Response should be in english no matter the location chosen.
            Output only the bullet list or the warning message, nothing else.
        """.trimIndent()

        return try {
            val response = generativeModel().generateContent(prompt = prompt)
            response.text.orEmpty()
        } catch (e: ResponseStoppedException) {
            Log.e("AI", "AI generation failed", e)
            "Ai response was too extensive, try again"
        } catch (e: Exception) {
            Log.e("AI", "AI generation failed", e)
            "Ai failed generation suggestions, try again"
        }
    }

    fun getOneSuggestion(location: String, country: String?, startDate: Long?, endDate: Long?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val startText = startDate?.let { dateFormatter.format(it) }
                val endText = endDate?.let { dateFormatter.format(it) }

                val suggestionText = fetchSingleSuggestion(location, country, startText, endText)

                aiSuggestions = listOf(suggestionText) // For screen, you can expose directly the item
                Log.i("AI", "New suggestion: $suggestionText")

            } catch (e: Exception) {
                Log.e("AI", "Error fetching suggestion", e)
            }
        }
    }

    private suspend fun fetchSingleSuggestion(location: String, country: String?, startDateText: String?, endDateText: String?)
        : String {
        val prompt = """
        Provide exactly ONE short travel activity suggestion for $location 
        ${if (!country.isNullOrBlank()) ", $country" else ""}.
        It must be a single bullet-style short phrase (no more than 10 words).
        Do not output a list, only one suggestion.
        No extra text.
    """.trimIndent()

        val response = generativeModel().generateContent(prompt = prompt)
        return response.text.orEmpty().removePrefix("•").trim()
    }

    fun getSuggestionsFor(location: String, country: String? = null, startDate: Long? = null, endDate: Long? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dateFormatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val startText = startDate?.let { dateFormatter.format(it) }
                val endText = endDate?.let { dateFormatter.format(it) }

                val suggestionsText = fetchAiSuggestions(location, country, startText, endText)
                val suggestionsList = suggestionsText
                    .split("\n")
                    .filter { it.isNotBlank() }
                    .map { it.removePrefix("•").trim() }

                aiSuggestions = suggestionsList
                Log.i("AI", "✅ Suggestions: $suggestionsList")
            } catch (e: Exception) {
                Log.e("AI", "Error fetching suggestions", e)
            }
        }
    }

    fun addLocation(location: Location) {
        viewModelScope.launch {
            repo.addLocation(location)
        }
    }

    fun updateLocation(location: Location) {
        viewModelScope.launch {
            repo.updateLocation(location)
        }
    }

    fun deleteLocation(id: String, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                repo.deleteLocation(id)
                onDone?.invoke()
            } catch (t: Throwable) {
                lastError = t.message
            }
        }
    }
}
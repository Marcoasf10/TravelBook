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

    var aiSuggestion by mutableStateOf<String?>(null)
        private set

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    init {
        observeLocations()
    }

    fun clearAiSuggestion() {
        aiSuggestion = null
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

    fun getSuggestion(location: String, country: String?, startDate: Long?, endDate: Long?, existingSuggestions: List<String> = emptyList()) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val startText = startDate?.let { dateFormatter.format(it) }
                val endText = endDate?.let { dateFormatter.format(it) }

                val suggestionText = fetchSuggestion(location, country, startText, endText, existingSuggestions)

                aiSuggestion = suggestionText
                Log.i("AI", "New suggestion: $suggestionText")

            } catch (e: Exception) {
                Log.e("AI", "Error fetching suggestion", e)
            }
        }
    }

    private suspend fun fetchSuggestion(location: String, country: String?, startDateText: String?, endDateText: String?,
                                        existingSuggestions: List<String>)
        : String {
        val prompt = """
            You are an expert travel guide.
            Provide exactly ONE short travel activity suggestion for $location 
            ${if (!country.isNullOrBlank()) ", $country" else ""}.
            ${if (!startDateText.isNullOrBlank()) "\nThe voyage starting date is $startDateText." else ""}.
            ${if (!endDateText.isNullOrBlank()) "\nThe voyage ending date is $endDateText." else ""}.
            ${if (!endDateText.isNullOrBlank() || !endDateText.isNullOrBlank()) "\nThe temporary gap shouldn't be " +
                    "limitative, only use if specific, worthy activities occur in these dates in $location" else ""}.
                    
            The suggestion may be a key attraction, food or cultural experience.
            Use only short phrases, names of places, foods, or activities — no extra descriptions.
            It must be a single bullet-style short phrase (no more than 10 words).
            
            ${if (existingSuggestions.isNotEmpty()) "\nDo NOT suggest one of the following suggestions: " +
                existingSuggestions.joinToString(", ") else ""}
            
            If "$location" is not a known or valid travel destination, reply only with:
            ⚠️ No travel suggestions available for this location.
            
            Do not output a list, only one suggestion.
            No extra text.
        """.trimIndent()

        val response = generativeModel().generateContent(prompt = prompt)

        return response.text?.trim() ?: """⚠️ No travel suggestions available for this location."""
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
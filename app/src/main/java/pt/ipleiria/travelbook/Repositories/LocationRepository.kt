package pt.ipleiria.travelbook.Repositories

import pt.ipleiria.travelbook.Models.Location
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LocationRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collection = firestore.collection("locations")

    suspend fun addLocation(location: Location) {
        collection.document(location.id).set(location).await()
    }

    suspend fun updateLocation(location: Location) {
        collection.document(location.id).set(location).await()
    }

    suspend fun deleteLocation(id: String) {
        collection.document(id).delete().await()
    }

    fun observeLocations(onChange: (List<Location>) -> Unit, onError: ((Throwable) -> Unit)? = null) {
        collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onError?.invoke(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val locations = snapshot.documents.mapNotNull { it.toObject(Location::class.java) }
                onChange(locations)
            }
        }
    }
}
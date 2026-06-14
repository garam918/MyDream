package com.garam.mydream.core.data.firebase

import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseFirestoreInternal.FIRFirestore
import cocoapods.FirebaseFirestoreInternal.FIRQueryDocumentSnapshot
import com.garam.mydream.core.database.DreamAnalysisEntity
import com.garam.mydream.core.database.UserDataEntity
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.Foundation.NSNumber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class)
class FirebaseDataSourceImpl : FirebaseDataSource {

    private val firestore = FIRFirestore.firestore()
    private val userCollectionPath = "Users"
    private val dreamContentCollectionPath = "DreamContent"

    override suspend fun setUserData(userDataEntity: UserDataEntity) {
        val uid = FIRAuth.auth().currentUser()?.uid() ?: return

        firestore.collectionWithPath(userCollectionPath)
            .documentWithPath(uid)
            .setDataAwait(userDataEntity.toFirestoreMap())
    }

    override suspend fun saveDreamData(dreamData: DreamAnalysisEntity) {
        val uid = FIRAuth.auth().currentUser()?.uid() ?: return

        firestore.collectionWithPath(userCollectionPath)
            .documentWithPath(uid)
            .collectionWithPath(dreamContentCollectionPath)
            .documentWithPath(dreamData.id)
            .setDataAwait(dreamData.copy(uid = uid).toFirestoreMap())
    }

    override suspend fun getDreamData(): List<DreamAnalysisEntity> {
        val uid = FIRAuth.auth().currentUser()?.uid() ?: return emptyList()

        return suspendCancellableCoroutine { continuation ->
            firestore.collectionWithPath(userCollectionPath)
                .documentWithPath(uid)
                .collectionWithPath(dreamContentCollectionPath)
                .queryWhereField("uid", isEqualTo = uid)
                .getDocumentsWithCompletion { snapshot, error ->
                    if (error != null) {
                        continuation.resumeWithException(error.toException())
                        return@getDocumentsWithCompletion
                    }

                    val entities = snapshot?.documents
                        ?.mapNotNull { document ->
                            val data = (document as? FIRQueryDocumentSnapshot)?.data()
                            data?.toDreamAnalysisEntity()
                        }
                        .orEmpty()

                    continuation.resume(entities)
                }
        }
    }

    override suspend fun deleteDreamData(id: String) {
        val uid = FIRAuth.auth().currentUser()?.uid() ?: return

        suspendCancellableCoroutine { continuation ->
            firestore.collectionWithPath(userCollectionPath)
                .documentWithPath(uid)
                .collectionWithPath(dreamContentCollectionPath)
                .documentWithPath(id)
                .deleteDocumentWithCompletion { error ->
                    if (error != null) {
                        continuation.resumeWithException(error.toException())
                    } else {
                        continuation.resume(Unit)
                    }
                }
        }
    }

    private suspend fun cocoapods.FirebaseFirestoreInternal.FIRDocumentReference.setDataAwait(
        data: Map<Any?, Any?>
    ) {
        suspendCancellableCoroutine { continuation ->
            setData(data) { error ->
                if (error != null) {
                    continuation.resumeWithException(error.toException())
                } else {
                    continuation.resume(Unit)
                }
            }
        }
    }

    private fun UserDataEntity.toFirestoreMap(): Map<Any?, Any?> = mapOf(
        "uid" to uid,
        "email" to email,
        "loginType" to loginType,
        "usageCount" to usageCount,
        "rewardedChanceUsed" to rewardedChanceUsed,
        "paid" to paid,
        "lastUseDate" to lastUseDate
    )

    private fun DreamAnalysisEntity.toFirestoreMap(): Map<Any?, Any?> = mapOf(
        "id" to id,
        "uid" to uid,
        "title" to title,
        "score" to score,
        "analysis" to analysis,
        "energy_label" to energy_label,
        "energy_percent" to energy_percent,
        "good_points" to good_points,
        "warn_points" to warn_points,
        "lucky_item" to lucky_item,
        "lucky_color" to lucky_color,
        "analysisDate" to analysisDate
    )

    private fun Map<Any?, *>.toDreamAnalysisEntity(): DreamAnalysisEntity? {
        val id = stringValue("id") ?: return null
        val uid = stringValue("uid") ?: ""
        val title = stringValue("title") ?: return null
        val score = intValue("score") ?: return null
        val analysis = stringValue("analysis") ?: return null
        val energyLabel = stringValue("energy_label") ?: return null
        val energyPercent = intValue("energy_percent") ?: return null
        val luckyItem = stringValue("lucky_item") ?: return null
        val luckyColor = stringValue("lucky_color") ?: return null
        val analysisDate = stringValue("analysisDate") ?: return null

        return DreamAnalysisEntity(
            id = id,
            uid = uid,
            title = title,
            score = score,
            analysis = analysis,
            energy_label = energyLabel,
            energy_percent = energyPercent,
            good_points = stringListValue("good_points"),
            warn_points = stringListValue("warn_points"),
            lucky_item = luckyItem,
            lucky_color = luckyColor,
            analysisDate = analysisDate
        )
    }

    private fun Map<Any?, *>.stringValue(key: String): String? =
        this[key] as? String

    private fun Map<Any?, *>.intValue(key: String): Int? {
        val value = this[key]
        return when (value) {
            is Int -> value
            is Long -> value.toInt()
            is Double -> value.toInt()
            is Float -> value.toInt()
            is NSNumber -> value.intValue
            else -> null
        }
    }

    private fun Map<Any?, *>.longValue(key: String): Long? {
        val value = this[key]
        return when (value) {
            is Int -> value.toLong()
            is Long -> value
            is Double -> value.toLong()
            is Float -> value.toLong()
            is NSNumber -> value.longLongValue
            else -> null
        }
    }

    private fun Map<Any?, *>.stringListValue(key: String): List<String> {
        val value = this[key]
        return (value as? List<*>)
            ?.mapNotNull { it as? String }
            .orEmpty()
    }

    private fun NSError.toException(): Exception {
        return Exception(localizedDescription)
    }
}

package com.garam.mydream.data.firebase

import com.garam.mydream.data.local.DreamAnalysisEntity
import com.garam.mydream.data.local.DreamReportEntity
import com.garam.mydream.data.local.UserDataEntity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseDataSourceImpl : FirebaseDataSource {

    private val firestore = FirebaseFirestore.getInstance()
    private val userCollectionPath = "Users"
    private val dreamContentCollectionPath = "DreamContent"
    private val dreamReportCollectionPath = "DreamReport"

    override suspend fun setUserData(userDataEntity: UserDataEntity) {
        val currentUser = Firebase.auth.currentUser
        val uid = currentUser?.uid.toString()

        firestore.collection(userCollectionPath)
            .document(uid).set(userDataEntity)
    }

    override suspend fun saveDreamData(dreamData: DreamAnalysisEntity) {

        val currentUser = Firebase.auth.currentUser
        val uid = currentUser?.uid.toString()

        firestore.collection(userCollectionPath)
            .document(uid).collection(dreamContentCollectionPath).document(dreamData.id).set(dreamData)

    }

    override suspend fun saveDreamReportData(dreamReportEntity: DreamReportEntity) {
        val currentUser = Firebase.auth.currentUser
        val uid = currentUser?.uid.toString()

        firestore.collection(userCollectionPath)
            .document(uid).collection(dreamReportCollectionPath).document(dreamReportEntity.id).set(dreamReportEntity)

    }

    override suspend fun getDreamData(): List<DreamAnalysisEntity> {
        val currentUser = Firebase.auth.currentUser
        val uid = currentUser?.uid.toString()

        return firestore.collection(userCollectionPath)
            .document(uid).collection(dreamContentCollectionPath)
            .get().await().toObjects(DreamAnalysisEntity::class.java)

    }

    override suspend fun getDreamReportData(): List<DreamReportEntity> {
        val currentUser = Firebase.auth.currentUser
        val uid = currentUser?.uid.toString()

        return firestore.collection(userCollectionPath)
            .document(uid).collection(dreamReportCollectionPath)
            .get().await().toObjects(DreamReportEntity::class.java)
    }

    override suspend fun deleteDreamData(id: String) {
        val currentUser = Firebase.auth.currentUser
        val uid = currentUser?.uid.toString()

        firestore.collection(userCollectionPath).document(uid)
            .collection(dreamContentCollectionPath)
            .document(id).delete()
    }
}
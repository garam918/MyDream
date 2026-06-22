package com.garam.mydream.core.auth

import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseAuth.FIRGoogleAuthProvider
import cocoapods.FirebaseAuth.FIROAuthProvider
import cocoapods.FirebaseAuth.FIRUser
import cocoapods.FirebaseAuth.FIRUserInfoProtocol
import cocoapods.FirebaseFirestoreInternal.FIRFirestore
import com.garam.mydream.core.database.UserDataEntity
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential
import platform.AuthenticationServices.ASAuthorizationAppleIDProvider
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationScopeEmail
import platform.AuthenticationServices.ASAuthorizationScopeFullName
import platform.CoreCrypto.CC_SHA256
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@OptIn(ExperimentalForeignApi::class)
class AuthRepositoryImpl : AuthRepository {

    private var appleAuthDelegate: NSObject? = null

    override fun isLoggedIn(): Boolean = FIRAuth.auth().currentUser() != null

    override suspend fun isExistAccount(uid: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            FIRFirestore.firestore().collectionWithPath("Users")
                .documentWithPath(uid).getDocumentWithCompletion { document, error ->

                    if (document?.exists == true) continuation.resume(true) {}
                    else continuation.resume(false) {}


                }
        }
    }

    override suspend fun signInAnonymously(): UserDataEntity? {
        return suspendCancellableCoroutine { cont ->
            FIRAuth.auth().signInAnonymouslyWithCompletion { result, error ->

                if (error == null) {
                    val user = result?.user()
                    val localUserData = UserDataEntity(
                        uid = user?.uid().toString(),
                        email = "Guest",
                        loginType = "anonymous"
                    )

                    cont.resume(localUserData) {}
                } else cont.resume(null) {}
            }
        }
    }

    override suspend fun signInWithGoogle(
        idToken: String,
        accessToken: String
    ): UserDataEntity? {
        return suspendCancellableCoroutine { cont ->
            if (idToken.isBlank() || accessToken.isBlank()) {
                println("iOS Google login token is empty")
                cont.resume(null) {}
                return@suspendCancellableCoroutine
            }

            val credential = FIRGoogleAuthProvider.credentialWithIDToken(idToken, accessToken)

            val currentUser = FIRAuth.auth().currentUser()

            if(currentUser != null && currentUser.isAnonymous()) {
                currentUser.linkWithCredential(credential) { result, error ->

                    if(error != null) {
                        println("link error ${error.localizedDescription}")
                        FIRAuth.auth().signInWithCredential(credential) { result, error ->


                            if (result != null) {
                                val user = result.user()
                                val email = user.email()
                                val uid = user.uid()
                                val loginType = "google"

                                println("ios uid : ${uid}")
                                println("ios email : ${email}")

                                println("ios loginType : ${user.providerIdOrEmpty()}")


                                val userData = UserDataEntity(email = email, uid = uid, loginType = loginType)

                                cont.resume(userData) {}

                            } else cont.resume(null) {}

                        }

                    }
                    else {

                        if (result != null) {

                            val user = result.user()
                            val email = user.email()
                            val uid = user.uid()
                            val loginType = "google"
                            val userData = UserDataEntity(email = email, uid = uid, loginType = loginType)

                            cont.resume(userData) {
                                println(it.message)
                            }
                        }
                    }

                }


            }
            else FIRAuth.auth().signInWithCredential(credential) { result, error ->


                if (result != null) {
                    val user = result.user()
                    val email = user.email()
                    val uid = user.uid()
                    val loginType = "google"

                    println("ios uid : ${uid}")
                    println("ios email : ${email}")

                    println("ios loginType : ${user.providerIdOrEmpty()}")


                    val userData = UserDataEntity(email = email, uid = uid, loginType = loginType)

                    cont.resume(userData) {}

                } else cont.resume(null) {}

            }

        }
    }

    private var globalAppleDelegate: Any? = null


    override suspend fun linkInWithApple(): UserDataEntity? = suspendCancellableCoroutine { continuation ->

        val rawNonce = randomNonceString()
        val hashedNonce = sha256(rawNonce)
        val presentationWindow = UIApplication.sharedApplication.keyWindow
            ?: UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow

        if (presentationWindow == null) {
            println("Apple login presentation window is empty")
            continuation.resume(null) {}
            return@suspendCancellableCoroutine
        }
//            val provider = ASAuthorizationAppleIDProvider()
//            val request = provider.createRequest().apply {
//                requestedScopes = listOf(ASAuthorizationScopeEmail, ASAuthorizationScopeFullName)
//                // 2. 요청에 해싱된 Nonce 설정
//                nonce = hashedNonce
//            }

        val provider = ASAuthorizationAppleIDProvider()
        val request = provider.createRequest().apply {
            requestedScopes = listOf(ASAuthorizationScopeEmail, ASAuthorizationScopeFullName)
            nonce = hashedNonce
        }


        val controller = ASAuthorizationController(listOf(request))
//            val delegate
//            strongAuthDelegate
        appleAuthDelegate = object : NSObject(),
            ASAuthorizationControllerDelegateProtocol,
            ASAuthorizationControllerPresentationContextProvidingProtocol {

            override fun presentationAnchorForAuthorizationController(controller: ASAuthorizationController): UIWindow {
//                    return UIApplication.sharedApplication.keyWindow
//                        ?: UIApplication.sharedApplication.windows.first() as UIWindow
                return presentationWindow
            }
//                = platform.UIKit.UIApplication.sharedApplication.keyWindow!!

            override fun authorizationController(
                controller: ASAuthorizationController,
                didCompleteWithAuthorization: ASAuthorization
            ) {
                val credential =
                    didCompleteWithAuthorization.credential as? ASAuthorizationAppleIDCredential
                val idTokenData: NSData? = credential?.identityToken
                val idToken =
                    idTokenData?.let { NSString.create(it, NSUTF8StringEncoding) }
                val idTokenString = idToken?.toString()

                if (idTokenString.isNullOrBlank()) {
                    println("Apple login idToken is empty")
                    continuation.resume(null) {}
                    return
                }

                println("idToken : ${idTokenString}")
                val firebaseCredential =
                    FIROAuthProvider.appleCredentialWithIDToken(
                        idTokenString,
                        rawNonce,
                        null
                    )

//                    val firebaseCredential = FIROAuthProvider.credentialWithProviderID("apple.com",idToken.toString(), null)

                val currentUser = FIRAuth.auth().currentUser()

                if(currentUser != null && currentUser.isAnonymous()) {

                    currentUser.linkWithCredential(firebaseCredential) { result, error ->

                        if(error != null) {

                            continuation.resume(null) {
                                println(it.message)
                            }
                        }
                        else {

                            if (result != null) {

                                val user = result.user()
                                val email = user.email()
                                val uid = user.uid()
                                val loginType = "apple"
                                val userData =
                                    UserDataEntity(email = email, uid = uid, loginType = loginType)

                                continuation.resume(userData) {
                                    println(it.message)
                                }
                            }
                        }


                    }
                }
                else FIRAuth.auth().signInWithCredential(firebaseCredential) { result, error ->

                    if (result != null) {
                        val user = result.user()
                        val email = user.email()
                        val uid = user.uid()
                        val loginType = "apple"

                        println("ios uid : ${uid}")
                        println("ios email : ${email}")

                        println("ios loginType : ${user.providerIdOrEmpty()}")


                        val userData =
                            UserDataEntity(email = email, uid = uid, loginType = loginType)

                        continuation.resume(userData) {
                            println(it.message)
                        }

                    } else {
                        println(error?.localizedDescription)
                        continuation.resume(null) {}
                    }
                }


            }

            override fun authorizationController(
                controller: ASAuthorizationController,
                didCompleteWithError: NSError
            ) {
                println("Apple login error: ${didCompleteWithError.localizedDescription}")
                continuation.resume(null) {}
            }
        }

        globalAppleDelegate = appleAuthDelegate

        controller.delegate = appleAuthDelegate as ASAuthorizationControllerDelegateProtocol
        controller.presentationContextProvider =
            appleAuthDelegate as ASAuthorizationControllerPresentationContextProvidingProtocol

        continuation.invokeOnCancellation {
            globalAppleDelegate = null
        }

        dispatch_async(dispatch_get_main_queue()) {
            controller.performRequests()
        }
    }

    override suspend fun signInWithApple(): UserDataEntity? = suspendCancellableCoroutine { continuation ->

        val rawNonce = randomNonceString()
        val hashedNonce = sha256(rawNonce)
        val presentationWindow = UIApplication.sharedApplication.keyWindow
            ?: UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow

        if (presentationWindow == null) {
            println("Apple login presentation window is empty")
            continuation.resume(null) {}
            return@suspendCancellableCoroutine
        }

//            val provider = ASAuthorizationAppleIDProvider()
//            val request = provider.createRequest().apply {
//                requestedScopes = listOf(ASAuthorizationScopeEmail, ASAuthorizationScopeFullName)
//                // 2. 요청에 해싱된 Nonce 설정
//                nonce = hashedNonce
//            }

        val provider = ASAuthorizationAppleIDProvider()
        val request = provider.createRequest().apply {
            requestedScopes = listOf(ASAuthorizationScopeEmail, ASAuthorizationScopeFullName)
            nonce = hashedNonce
        }


        val controller = ASAuthorizationController(listOf(request))
//            val delegate
//            strongAuthDelegate
        appleAuthDelegate = object : NSObject(),
            ASAuthorizationControllerDelegateProtocol,
            ASAuthorizationControllerPresentationContextProvidingProtocol {

            override fun presentationAnchorForAuthorizationController(controller: ASAuthorizationController): UIWindow {
//                    return UIApplication.sharedApplication.keyWindow
//                        ?: UIApplication.sharedApplication.windows.first() as UIWindow
                return presentationWindow
            }
//                = platform.UIKit.UIApplication.sharedApplication.keyWindow!!

            override fun authorizationController(
                controller: ASAuthorizationController,
                didCompleteWithAuthorization: ASAuthorization
            ) {
                val credential =
                    didCompleteWithAuthorization.credential as? ASAuthorizationAppleIDCredential
                val idTokenData: NSData? = credential?.identityToken
                val idToken =
                    idTokenData?.let { NSString.create(it, NSUTF8StringEncoding) }
                val idTokenString = idToken?.toString()

                if (idTokenString.isNullOrBlank()) {
                    println("Apple login idToken is empty")
                    continuation.resume(null) {}
                    return
                }

                println("idToken : ${idTokenString}")
                val firebaseCredential =
                    FIROAuthProvider.appleCredentialWithIDToken(
                        idTokenString,
                        rawNonce,
                        null
                    )

//                    val firebaseCredential = FIROAuthProvider.credentialWithProviderID("apple.com",idToken.toString(), null)

//                     val currentUser = FIRAuth.auth().currentUser()

//                    if(currentUser != null && currentUser.isAnonymous()) {
//
//                        currentUser.linkWithCredential(firebaseCredential) { result, error ->
//
//                            if(error != null) {
//
//                                continuation.resume(null) {
//                                    println(it.message)
//                                }
//                            }
//                            else {
//
//                                if (result != null) {
//
//                                    val user = result.user()
//                                    val email = user.email()
//                                    val uid = user.uid()
//                                    val loginType = "apple"
//                                    val userData =
//                                        LocalUserData(email = email, uid = uid, loginType = loginType)
//
//                                    continuation.resume(userData) {
//                                        println(it.message)
//                                    }
//                                }
//                            }
//
//
//                        }
//                    }
//                    else
                FIRAuth.auth().signInWithCredential(firebaseCredential) { result, error ->

                    if (result != null) {
                        val user = result.user()
                        val email = user.email()
                        val uid = user.uid()
                        val loginType = "apple"

                        println("ios uid : ${uid}")
                        println("ios email : ${email}")

                        println("ios loginType : ${user.providerIdOrEmpty()}")


                        val userData =
                            UserDataEntity(email = email, uid = uid, loginType = loginType)

                        continuation.resume(userData) {
                            println(it.message)
                        }

                    } else {
                        println(error?.localizedDescription)
                        continuation.resume(null) {}
                    }
                }


            }

            override fun authorizationController(
                controller: ASAuthorizationController,
                didCompleteWithError: NSError
            ) {
                println("Apple login error: ${didCompleteWithError.localizedDescription}")
                continuation.resume(null) {}
            }
        }

        globalAppleDelegate = appleAuthDelegate

        controller.delegate = appleAuthDelegate as ASAuthorizationControllerDelegateProtocol
        controller.presentationContextProvider =
            appleAuthDelegate as ASAuthorizationControllerPresentationContextProvidingProtocol

        continuation.invokeOnCancellation {
            globalAppleDelegate = null
        }

        dispatch_async(dispatch_get_main_queue()) {
            controller.performRequests()
        }
    }

    override suspend fun signOut() {
        FIRAuth.auth().signOut(error = null)
    }

    override suspend fun reAuthenticate(
        idToken: String,
        accessToken: String
    ): Boolean {
        return suspendCancellableCoroutine { cont ->
            val credential = FIRGoogleAuthProvider.credentialWithIDToken(idToken, accessToken)

            FIRAuth.auth().signInWithCredential(credential) { result, error ->


                if (result != null) cont.resume(true) {}
                else {
                    println("iOS reAuthenticate failed: ${error?.localizedDescription}")
                    cont.resume(false) {}
                }
            }
        }
    }

    override suspend fun deleteAccount() {
        val currentUser = FIRAuth.auth().currentUser()

        FIRFirestore.firestore().collectionWithPath("Users")
            .documentWithPath(currentUser?.uid().toString()).deleteDocument()

        FIRAuth.auth().currentUser()?.deleteWithCompletion {

        }
    }

    override suspend fun currentUser(): UserDataEntity? {

        val user = FIRAuth.auth().currentUser() ?: return null

        if (user.isAnonymous()) {
            return UserDataEntity(uid = user.uid(), email = user.email(), loginType = "anonymous")
        }

        val storedUser = getStoredUserData(user.uid())
        return buildResolvedUser(user, storedUser)
    }

    override suspend fun updateUserInfo(userInfo: UserDataEntity) {
        TODO("Not yet implemented")
    }

    fun randomNonceString(length: Int = 32): String {
        val charset = "0123456789ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvwxyz-._"
        val result = StringBuilder()
        var remainingLength = length

        while (remainingLength > 0) {
            val random = (0..charset.lastIndex).random() // 간단한 랜덤 (보안성을 높이려면 SecRandomCopyBytes 권장)
            result.append(charset[random])
            remainingLength--
        }
        return result.toString()
    }

    @OptIn(ExperimentalForeignApi::class)
    fun sha256(input: String): String {
        val data = input.encodeToByteArray()
        val hash = UByteArray(CC_SHA256_DIGEST_LENGTH)

        data.usePinned { pinned ->
            hash.usePinned { pinnedHash ->
                CC_SHA256(
                    pinned.addressOf(0),
                    data.size.toUInt(),
                    hash.refTo(0)
                ) // -1u는 null terminator 제외
            }

        }

        return hash.joinToString("") { byte ->
            // UByte를 Int로 변환
            val value = byte.toInt()

            // 1. 상위 4비트(High Nibble) 추출: 첫 번째 16진수 자릿수
            val highNibble = (value shr 4) and 0xF

            // 2. 하위 4비트(Low Nibble) 추출: 두 번째 16진수 자릿수
            val lowNibble = value and 0xF

            // HEX_CHARS에서 해당하는 문자를 찾아 문자열로 합칩니다.
            // 이는 "%02x"와 동일한 결과를 냅니다.
            "${HEX_CHARS[highNibble]}${HEX_CHARS[lowNibble]}"
        }
    }

    private val HEX_CHARS = "0123456789abcdef"

    private fun cocoapods.FirebaseAuth.FIRUser.providerIdOrEmpty(): String {
        return (providerData().firstOrNull() as? FIRUserInfoProtocol)?.providerID().orEmpty()
    }

    private suspend fun getStoredUserData(uid: String): UserDataEntity? {
        return suspendCancellableCoroutine { continuation ->
            FIRFirestore.firestore().collectionWithPath("Users")
                .documentWithPath(uid)
                .getDocumentWithCompletion { document, error ->
                    if (error != null) {
                        println("iOS currentUser load failed: ${error.localizedDescription}")
                        continuation.resume(null) {}
                        return@getDocumentWithCompletion
                    }

                    val userData = document?.data()?.toUserDataEntity(uid)
                    continuation.resume(userData) {}
                }
        }
    }

    private fun buildResolvedUser(user: FIRUser, storedUser: UserDataEntity?): UserDataEntity {
        val resolvedLoginType = storedUser?.loginType
            ?.takeUnless { it.isBlank() || it == "anonymous" }
            ?: resolveLoginType(user)

        val resolvedEmail = storedUser?.email
            ?.takeUnless { it.isBlank() || it == "Guest" }
            ?: user.email()

        return UserDataEntity(
            uid = user.uid(),
            email = resolvedEmail,
            loginType = resolvedLoginType,
            usageCount = storedUser?.usageCount ?: 2,
            rewardedChanceUsed = storedUser?.rewardedChanceUsed ?: false,
            paid = storedUser?.paid ?: false,
            lastUseDate = storedUser?.lastUseDate ?: UserDataEntity().lastUseDate
        )
    }

    private fun resolveLoginType(user: FIRUser): String {
        return when (user.providerIdOrEmpty()) {
            "google.com" -> "google"
            "apple.com" -> "apple"
            else -> ""
        }
    }

    private fun Map<Any?, *>.toUserDataEntity(currentUid: String): UserDataEntity {
        return UserDataEntity(
            uid = stringValue("uid")?.takeIf { it.isNotBlank() } ?: currentUid,
            email = stringValue("email"),
            loginType = stringValue("loginType").orEmpty(),
            usageCount = intValue("usageCount") ?: 2,
            rewardedChanceUsed = boolValue("rewardedChanceUsed") ?: false,
            paid = boolValue("paid") ?: false,
            lastUseDate = stringValue("lastUseDate") ?: UserDataEntity().lastUseDate
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

    private fun Map<Any?, *>.boolValue(key: String): Boolean? {
        val value = this[key]
        return when (value) {
            is Boolean -> value
            is NSNumber -> value.boolValue
            else -> null
        }
    }
}

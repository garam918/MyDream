import SwiftUI
import FirebaseCore
import ComposeApp

@main
struct iOSApp: App {

    init() {

        FirebaseApp.configure()
        KoinKt.doInitKoinIos()

    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

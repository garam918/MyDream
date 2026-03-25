import SwiftUI
import FirebaseCore
import ComposeApp

@main
struct iOSApp: App {

    init() {

        KoinKt.doInitKoinIos()
        FirebaseApp.configure()

    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

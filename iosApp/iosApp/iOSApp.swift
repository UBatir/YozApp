import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        AppInitKt.initKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

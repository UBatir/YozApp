package uz.yozapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yandex.mapkit.MapKitFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import uz.yozapp.core.data.di.androidDataModule
import uz.yozapp.di.appModules

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("YOUR_YANDEX_MAPS_API_KEY")
        MapKitFactory.initialize(this)
        startKoin {
            androidContext(this@MainActivity)
            modules(appModules + androidDataModule)
        }
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

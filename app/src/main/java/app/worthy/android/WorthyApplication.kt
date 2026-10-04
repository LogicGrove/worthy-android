package app.worthy.android

import android.app.Application
import app.worthy.android.di.AppContainer
import app.worthy.android.di.DefaultAppContainer

class WorthyApplication : Application() {
    val container: AppContainer by lazy { DefaultAppContainer(this) }
}

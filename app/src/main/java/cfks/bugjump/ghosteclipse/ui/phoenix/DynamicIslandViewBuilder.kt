package cfks.bugjump.ghosteclipse.ui.phoenix

import android.app.Service
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.math.roundToInt
import cfks.bugjump.ghosteclipse.ui.phoenix.DynamicIslandView
import cfks.bugjump.ghosteclipse.ui.phoenix.DynamicIslandState
import cfks.bugjump.ghosteclipse.ui.phoenix.rememberDynamicIslandState

class ServiceLifecycleOwner : LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    fun performRestore(savedState: Bundle?) { savedStateRegistryController.performRestore(savedState) }
    fun handleLifecycleEvent(event: Lifecycle.Event) = lifecycleRegistry.handleLifecycleEvent(event)
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
}

class DynamicIslandViewBuilder {
    private lateinit var composeView: ComposeView
    private var dynamicIslandState: DynamicIslandState? = null
    private var _scale = mutableStateOf(0.7f)
    private val scale by _scale
    private lateinit var ctx: Context
    private val lifecycleOwner = ServiceLifecycleOwner()

    fun build(ctx: Context): ComposeView {
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        return ComposeView(ctx).apply {
            setViewTreeLifecycleOwner(lifecycleOwner); setViewTreeViewModelStoreOwner(lifecycleOwner); setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            setContent {
                val isDarkTheme = isSystemInDarkTheme()
                val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                } else {
                    if (isDarkTheme) darkColorScheme() else lightColorScheme()
                }
                
                MaterialTheme(colorScheme = colorScheme) {
                    val state = rememberDynamicIslandState()
                    LaunchedEffect(state) { 
                        this@DynamicIslandViewBuilder.dynamicIslandState = state 
                    }
                    
                    DynamicIslandView(state = state, scale = scale)
                }
            }
        }
    }
    
    fun onDestroy(){
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
    }

    fun updateText(text: String) {
        dynamicIslandState?.persistentText = text
    }

    fun updateScale(scale: Float = 0.7f) {
        _scale.value = scale
    }

    fun showSwitch(name: String, state: Boolean) {
        dynamicIslandState?.addSwitch(name, state)
    }

    fun showProgress(
        identifier: String,
        title: String,
        subtitle: String,
        progress: Float,
        duration: Long = 5000L,
        resId: Int = -1
    ) {
        val iconDrawable = resId.takeIf { it != -1 }?.let { resId -> 
            runCatching { ContextCompat.getDrawable(ctx, resId) }.getOrNull() 
        }
        dynamicIslandState?.addOrUpdateProgress(
            identifier, 
            title, 
            subtitle, 
            iconDrawable, 
            progress, 
            duration
        )
    }
}
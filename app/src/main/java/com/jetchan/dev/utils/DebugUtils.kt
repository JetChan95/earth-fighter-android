package com.jetchan.dev.utils

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.jetchan.dev.BuildConfig
import timber.log.Timber
import kotlin.collections.ArrayList

class NavStackTracker(private val navController: NavController, who: String) {
    private val stack = ArrayList<NavDestination>()

    init {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (BuildConfig.DEBUG) {
//                stack.clear()
//                stack.add(destination)
//                printNavStack(who)
                Timber.d("Navigated to: ${destination.label}")
                val backQueueField = NavController::class.java.getDeclaredField("backQueue")
                backQueueField.isAccessible = true
                val backQueue = backQueueField.get(navController) as List<*>
                Timber.d("Back queue: $backQueue")

            }
        }
    }

    private fun printNavStack(whoPrint: String) {
        val stackInfo = stack.mapIndexed { index, destination ->
            val destinationClassName = try {
                destination.javaClass.name
            } catch (e: Exception) {
                "unknown"
            }
            val label = destination.label?.toString() ?: "No label"

            val arguments = destination.arguments.keys.toString()
            "Index $index: ID = ${destination.id}, Label = ${label}, Class = $destinationClassName, Arguments = $arguments, WhoPrint = $whoPrint"
        }
        Timber.d("Navigation stack details:\n${stackInfo.joinToString("\n")}")
    }
}

fun getCurrentFunctionName(): String {
    val stackTrace = Throwable().stackTrace
    if (stackTrace.size >= 2) {
        return stackTrace[1].methodName
    }
    return ""
}
package com.ekdorn.silentium.utils

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.reflect.KClass
import kotlin.reflect.KFunction2


fun Fragment.observe(vararg contracts: ContractClass) = Observer(contracts, ::registerForActivityResult).apply { lifecycle.addObserver(this) }

fun AppCompatActivity.observe(vararg contracts: ContractClass) = Observer(contracts, ::registerForActivityResult).apply { lifecycle.addObserver(this) }

class Observer(private val contracts: Array<out ContractClass>, private val regFunc: RegistrationFunction) : DefaultLifecycleObserver {
    private val holder = mutableMapOf<String, Token<Any, Any>>()

    override fun onCreate(owner: LifecycleOwner) {
        contracts.forEach { cl ->
            @Suppress("UNCHECKED_CAST")
            holder[cl.java.simpleName] = Token<Any, Any>().also {
                it.launcher = regFunc.invoke(cl.java.newInstance() as ActivityResultContract<Any, Any>) { result -> it.lambda.lambda(result) }
            }
        }
    }

    internal fun <G: ActivityResultContract<P, C>, P, C> launch (cl: KClass<G>, param: P, callback: (C?) -> Unit = {}) {
        if (!holder.contains(cl.java.simpleName)) throw TokenNotRegisteredException(cl.java.simpleName, "fragment")
        holder[cl.java.simpleName]?.apply {
            @Suppress("UNCHECKED_CAST")
            this.lambda.lambda = callback as (Any?) -> Unit
            if (this.launcher != null) this.launcher!!.launch(param) else throw TokenNotRegisteredException(this.toString(), "fragment")
        }
    }
}

private typealias ContractClass = KClass<out ActivityResultContract<*, *>>
private typealias RegistrationFunction = KFunction2<ActivityResultContract<Any, Any>, ActivityResultCallback<Any>, ActivityResultLauncher<Any>>

internal class UnitLambdaWrapper <T> (var lambda: (T?) -> Unit = {})
internal class Token <T, L> (var launcher: ActivityResultLauncher<T>? = null, var lambda: UnitLambdaWrapper<L> = UnitLambdaWrapper())

private class TokenNotRegisteredException (token: String, instance: String): RuntimeException("Token exception: Token $token is not registered for this $instance!")


package com.sm.uni.os.hooker.base

import androidx.annotation.CallSuper
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.param.HookParam
import io.luckypray.dexkit.DexKitBridge
import io.luckypray.dexkit.builder.MethodInvokingArgs
import io.luckypray.dexkit.descriptor.member.DexMethodDescriptor
import java.lang.reflect.Method

abstract class YukiBaseHookerWithDexKit: YukiBaseHooker() {
    companion object {
        var isDexKitInit = false
    }

    abstract fun onFindMembers(bridge: DexKitBridge)

    @CallSuper
    override fun onHook() {
        if (!isDexKitInit) {
            System.loadLibrary("dexkit")
            isDexKitInit = true
        }

        DexKitBridge.create(appInfo.sourceDir)?.use { bridge ->
            onFindMembers(bridge)
        }
    }

    /**
     * 对给定 [Method] 执行 beforeHook 方法
     *
     * @param block 要执行的 [Unit]
     */
    fun Method.beforeHook(block: HookParam.() -> Unit) {
        declaringClass.hook {
            injectMember {
                members(this@beforeHook)
                beforeHook {
                    block()
                }
            }
        }
    }

    /**
     * 对给定 [Method] 执行 afterHook 方法
     *
     * @param block 要执行的 [Unit]
     */
    fun Method.afterHook(block: HookParam.() -> Unit) {
        declaringClass.hook {
            injectMember {
                members(this@afterHook)
                afterHook {
                    block()
                }
            }
        }
    }

    /**
     * 通过 [DexMethodDescriptor] 获取方法实例, 传入参数默认为 [appClassLoader]
     *
     * @return [Method]
     */
    fun DexMethodDescriptor.getMethodInstance() = getMethodInstance(appClassLoader)

    /**
     * 查找给定方法的调用函数, 如果有多个查找到的函数, 则会抛出异常
     *
     * @throws [IllegalArgumentException]
     * @return [Method]
     */
    fun DexKitBridge.uniqueFindMethodInvoking(builder: MethodInvokingArgs.Builder.() -> Unit): Method {
        val invokingList = findMethodInvoking(builder)
        val flatMap = invokingList.flatMap { it.value }

        require(flatMap.size == 1) { "uniqueFindMethodInvoking() Error: invokingList must contain exactly one item; Data: $invokingList" }

        return flatMap.first().getMethodInstance()
    }
}
package com.sm.uni.os.hooker.recorder

import android.content.Context
import android.media.AudioManager
import com.sm.uni.os.hooker.base.YukiBaseHookerWithDexKit
import com.highcapable.yukihookapi.hook.type.java.IntArrayType
import com.sm.uni.os.util.toast
import io.luckypray.dexkit.DexKitBridge
import io.luckypray.dexkit.enums.MatchType
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object RecorderHooker: YukiBaseHookerWithDexKit() {

    // Methods
    private lateinit var mSetBitRateValue: Method
    private lateinit var mSetFrameValue: Method
    private lateinit var mTransferNormalAudioDataToEncoder: Method
    private lateinit var mAmplifyPCMData: Method

    // Fields
    private lateinit var vBitRateLevel: Field
    private lateinit var vFrameLevel: Field

    override fun onFindMembers(bridge: DexKitBridge) {
        bridge.batchFindMethodsUsingStrings {
            addQuery("bitrate", setOf("defaultBitRate"))
            addQuery("frame", setOf("Error when set frame value", "defaultFrames"))
            addQuery("transferNormalAudioDataToEncoder", setOf("exception duration", "count", "AudioRecord read failed"))
            matchType = MatchType.CONTAINS
        }.let { methodsResultMap ->
            // 获得传输音频数据方法 Method
            mTransferNormalAudioDataToEncoder = methodsResultMap["transferNormalAudioDataToEncoder"]!!.first {
                it.parameterTypesSig == "[Ljava/nio/ByteBuffer;Z" && it.returnTypeSig == "V"
            }.getMethodInstance()
            // 获得设置码率方法 Method
            mSetBitRateValue = methodsResultMap["bitrate"]!!.first {
                it.parameterTypesSig == "II" && it.returnTypeSig == "V"
            }.getMethodInstance()
            // 获得设置帧率方法 Method
            mSetFrameValue = methodsResultMap["frame"]!!.first {
                it.parameterTypesSig == "II" && it.returnTypeSig == "V"
            }.getMethodInstance()
        }

        //查找amplifyPCMData方法并hook
        mAmplifyPCMData = bridge.uniqueFindMethodInvoking {
            methodDeclareClass = mTransferNormalAudioDataToEncoder.declaringClass.name
            methodName = mTransferNormalAudioDataToEncoder.name
            methodReturnType = "V"
            beInvokedMethodParameterTypes = arrayOf("[B", "I", "[B", "I", "F")
            beInvokedMethodReturnType = "I"
        }

        // 获得码率和帧率变量 field
        if (mSetBitRateValue.declaringClass == mSetFrameValue.declaringClass) {
            // mSetBitRateValue 和 mSetFrameValue 在同一个类中
            val fields = mSetBitRateValue.declaringClass.declaredFields

            for (field in fields) {
                field.isAccessible = true

                when {
                    !Modifier.isStatic(field.modifiers) || field.type != IntArrayType -> continue

                    !RecorderHooker::vBitRateLevel.isInitialized && (field.get(null) as IntArray).contentEquals(intArrayOf(200, 100, 50, 32, 24, 16, 8, 6, 4, 1)) ->
                        vBitRateLevel = field
                    !RecorderHooker::vFrameLevel.isInitialized && (field.get(null) as IntArray).contentEquals(intArrayOf(15, 24, 30, 48, 60, 90)) ->
                        vFrameLevel = field
                }

                if (RecorderHooker::vBitRateLevel.isInitialized && RecorderHooker::vFrameLevel.isInitialized) break
            }

        } else {
            // mSetBitRateValue 和 mSetFrameValue 不在同一个类中
            vBitRateLevel = mSetBitRateValue.declaringClass.declaredFields.first {
                it.isAccessible = true
                Modifier.isStatic(it.modifiers) && it.type == IntArrayType && (it.get(null) as IntArray).contentEquals(intArrayOf(200, 100, 50, 32, 24, 16, 8, 6, 4, 1))
            }
            vFrameLevel = mSetFrameValue.declaringClass.declaredFields.first {
                it.isAccessible = true
                Modifier.isStatic(it.modifiers) && it.type == IntArrayType && (it.get(null) as IntArray).contentEquals(intArrayOf(15, 24, 30, 48, 60, 90))
            }
        }

    }

    override fun onHook() {
        super.onHook()

        "com.miui.screenrecorder.activity.ScreenRecorderSettingActivity".hook {
            injectMember {
                method {
                    name = "onCreate"
                }
                afterHook {
                    appContext?.toast("录屏设置已解锁！")
                }
            }
        }

        mAmplifyPCMData.beforeHook {
            //获取当前设备音量值
            val audioManager = appContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            if (currentVolume == 0) {
                return@beforeHook
            }
            //获取当前设备最大音量值
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            //获取当前设备音量百分比的倒数
            val volumeRatio = (maxVolume / currentVolume).toFloat()
            args(4).set(volumeRatio * 2)
        }

        mSetBitRateValue.beforeHook {
            vBitRateLevel.set(null, intArrayOf(200, 175, 150, 125, 105, 100, 50, 32, 24, 16, 8, 6, 4, 1))
            args(0).set(200)
            args(1).set(1)
        }

        mSetFrameValue.beforeHook {
            vFrameLevel.set(null, intArrayOf(15, 24, 30, 48, 60, 90, 120, 144))
            args(0).set(144)
            args(1).set(15)
        }
    }

}
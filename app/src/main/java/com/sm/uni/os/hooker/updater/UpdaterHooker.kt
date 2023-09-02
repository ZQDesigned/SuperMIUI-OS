package com.sm.uni.os.hooker.updater

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass

object UpdaterHooker: YukiBaseHooker() {

    override fun onHook() {
        "miui.util.FeatureParser".hook {

            injectMember {
                method {
                    name = "hasFeature"
                    param(StringClass, IntType)
                }
                beforeHook {
                    if (args(0).string() == "support_ota_validate")
                        result = false
                }
            }

        }
    }
}
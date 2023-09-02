package com.sm.uni.os.hooker.recorder

import android.os.Build
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

object FrameworkHooker: YukiBaseHooker() {

    override fun onHook() {

        findClass("miui.util.FeatureParser").hook {

            /**
             *内录声音限制解除
             */

            injectMember {
                method {
                    name = "getInteger"
                    paramCount(2)
                }
                beforeHook {
                    if (args(0).string() == "support_inner_record")
                        result = 1
                }
            }

            /**
             *蓝牙内录声音限制解除
             */

            injectMember {
                method {
                    name = "getBoolean"
                    paramCount(2)
                }
                beforeHook {
                    if (args(0).string() == "support_a2dp_inner_record")
                        result = true
                    if (args(0).string() == "support_record_param")
                        result = true
                }
            }

        }

        /**
         * 禁用FLAG_SECURE
         */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            findClass("com.android.server.wm.WindowState").hook {
                injectMember {
                    method {
                        name = "isSecureLocked"
                        emptyParam()
                    }
                    replaceToFalse()
                }
            }
        } else {
            findClass("com.android.server.wm.WindowManagerService").hook {
                injectMember {
                    method {
                        name = "isSecureLocked"
                        emptyParam()
                    }
                    replaceToFalse()
                }
            }
        }

    }

}
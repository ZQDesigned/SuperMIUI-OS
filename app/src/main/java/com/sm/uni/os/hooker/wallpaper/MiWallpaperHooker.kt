package com.sm.uni.os.hooker.wallpaper

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType


object MiWallpaperHooker: YukiBaseHooker() {

    override fun onHook() {

        "com.miui.miwallpaper.utils.OTAUtils".hook {
            injectMember {
                method {
                    name = "isLoopVideoWallpaper"
                    param(ContextClass, BooleanType)
                }
                replaceToTrue()
            }
        }

        "com.miui.miwallpaper.maml.video.VideoStrategy".hook {
            injectMember {
                method {
                    name = "isLooping"
                    emptyParam()
                }
                replaceToTrue()
            }
        }

        "com.miui.miwallpaper.MiuiWallpaperManager".hook {
            injectMember {
                method {
                    name = "setMiuiVideoWallpaper"
                    param { it.last() == BooleanType }
                }.all()
                replaceToTrue()
            }
        }

        "com.miui.miwallpaper.server.MiuiWallpaperData".hook {
            injectMember {
                method {
                    name = "getLoop"
                    emptyParam()
                }
                replaceToTrue()
            }
        }

    }

}
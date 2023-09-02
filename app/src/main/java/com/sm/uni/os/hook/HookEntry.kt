package com.sm.uni.os.hook

import com.highcapable.yukihookapi.YukiHookAPI.Configs.isDebug
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.log.YukiHookLogger.Configs.tag
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.sm.uni.os.BuildConfig
import com.sm.uni.os.hooker.recorder.FrameworkHooker
import com.sm.uni.os.hooker.recorder.RecorderHooker
import com.sm.uni.os.hooker.updater.UpdaterHooker
import com.sm.uni.os.hooker.wallpaper.MiWallpaperHooker

@InjectYukiHookWithXposed
class HookEntry: IYukiHookXposedInit {

    override fun onInit() {
        isDebug = BuildConfig.DEBUG
        tag = "SuperMIUI-OS"
    }

    override fun onHook() = encase {
        //更新
        loadApp("com.android.updater", UpdaterHooker)
        //录屏
        loadSystem(FrameworkHooker)
        loadApp("com.miui.screenrecorder", RecorderHooker)
        //壁纸
        loadApp("com.miui.miwallpaper", MiWallpaperHooker)
    }
}
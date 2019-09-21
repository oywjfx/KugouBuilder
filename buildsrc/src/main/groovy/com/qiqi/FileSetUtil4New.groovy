package com.qiqi

import com.qiqi.util.AntBaseUtil
import org.apache.tools.ant.taskdefs.Jar
import org.apache.tools.ant.types.FileSet
import org.apache.tools.ant.types.ZipFileSet
import org.gradle.api.Project

/**
 * Created by siganid on 2016/5/16.
 */
public class FileSetUtil4New {

    static String getReleasePath(Project project) {
        String buildPath = project.getBuildDir().absolutePath
        String dir = buildPath + "\\" + "intermediates\\classes\\release"
        return dir;
    }

    static String[] mergeStringArray(String[]... arrays) {
        if (arrays == null) {
            return new String[0];
        }
        List<String> list = new ArrayList<>();
        for (String[] sa : arrays) {
            for (String file : sa) {
                list.add(file)
            }
        }
        return (String[]) list.toArray();
    }


    public static class CommonProj {

        public static final String[] ClassesForDexMainMust = [
                "com/kugou/crash/**",
                "com/kugou/common/app/BootTimeMonitor*.class",
                "com/kugou/common/app/KGCommonApplication*.class",
                "com/kugou/common/app/KGCommonAppImpl*.class",
                "com/kugou/common/constant/GlobalEnv*.class",
                "com/kugou/common/utils/ACache*.class",
                "com/kugou/common/utils/IOUtils*.class",
                "com/kugou/common/utils/MD5Util*.class",
                "com/kugou/common/utils/DateUtil*.class",
                "com/kugou/common/utils/UrlEncoderUtil*.class",
                "com/kugou/common/utils/FileMD5Util*.class",
                "com/kugou/common/entity/CrashType*.class",
                "com/kugou/common/entity/NetworkType*.class",
                "com/kugou/common/entity/LogInfo*.class",
                "com/kugou/common/entity/SoftInfo*.class",
                "com/kugou/common/preferences/provider/CommonSharedPreferencesProvider*.class",
                "com/kugou/common/preferences/provider/PreferencesConstants/SettingKeyMap*.class",
                "com/kugou/common/environment/provider/GlobalVariableProvider*.class",
                "com/kugou/common/database/CommonProvider*.class",
                "com/kugou/common/database/AbstractKGContentProvider*.class",
                "com/kugou/android/support/**",
                "com/kugou/common/dynamic/**",
                "net/wequick/**",
                "com/kugou/common/app/KGTinkerApplication*.class",
                "com/kugou/common/app/KGApplication*.class",
                "com/kugou/common/app/KgTinkerResultService*.class",
                "com/kugou/common/app/KgUncaughtHandlerIntinker*.class",
                "com/kugou/common/relinker/**"
        ]

        public static final String[] ClassesForDexMainExtra = [
                "android/support/annotation",
                "de/greenrobot/event/**",
                "org/java_websocket/**",

                "com/kugou/dto/sing",
//                "com/kugou/android/**",
                "com/kugou/common/**",
//                "com/kugou/framework",

                // 在dex4中查漏补缺时自动加入
//                "com/kugou/fanxing/**",
//                "com/kugou/game/**",
//                "com/kugou/ktv/**",
        ]

        public static final String[] ClassesForDex7 = [
                "com/kugou/common/utils/GrayPackageUtil*.class"
        ]

        public static final String[] ClassesForDiscard = [
                "com/kugou/common/R*.class"
        ]


        /**
         * common库代码in classes.dex >> main
         */
        static FileSet fileSetForDexMain(Project commonProject) {
            String dir = FileSetUtil4New.getReleasePath(commonProject);

            String[] includes = FileSetUtil4New.mergeStringArray(
                    ClassesForDexMainMust, ClassesForDexMainExtra)
            String[] excludes = FileSetUtil4New.mergeStringArray(
                    ClassesForDex7, ClassesForDiscard)

            FileSet fileSet = AntBaseUtil.makeFileSet(dir, includes, excludes);
            return fileSet;
        }

        /**
         * common库代码in classes4.dex 查漏补缺
         */
        static FileSet fileSetForDex4(Project commonProject) {
            String dir = FileSetUtil4New.getReleasePath(commonProject);

            String[] includes = [
                    "*/**"
            ];
            String[] excludes = FileSetUtil4New.mergeStringArray(
                    ClassesForDexMainMust, ClassesForDexMainExtra,
                    ClassesForDex7, ClassesForDiscard, TestUtil.ClassesForDex8Com)

            FileSet fileSet = AntBaseUtil.makeFileSet(dir, includes, excludes);
            return fileSet;
        }

        /**
         * common库代码in classes7.dex
         */
        static FileSet fileSetForDex7(Project commonProject) {
            String[] includes = ClassesForDex7
            FileSet fileSet = AntBaseUtil.makeFileSet(
                    FileSetUtil4New.getReleasePath(commonProject), includes, null)
            return fileSet
        }
    }




    public static class KugouProj {

        public static final String[] ClassesForMainDexMust = [
                "com/kugou/android/app/KGApplication*.class",
                "com/kugou/android/app/KGAppImpl*.class",
                "com/kugou/common/R*.class",
                "com/kugou/android/launcher/LauncherProvider*.class",
                "com/kugou/android/launcher/LauncherSettings*.class",
                "com/kugou/android/launcher/LauncherAppState*.class",
                "com/kugou/android/launcher/compat/UserHandleCompat*.class",
                "com/kugou/android/launcher/compat/UserManagerCompat*.class",
                "com/kugou/android/launcher/config/ProviderConfig*.class",
                "com/kugou/android/launcher/util/ManagedProfileHeuristic*.class",
        ]

        public static final String[] ClassesForDex2 = [
                "com/kugou/modulefm/R*.class",
                "com/kugou/framework/**",

                "com/kugou/android/ads/**",
                "com/kugou/android/advertise/**",
                "com/kugou/android/app/**",
                "com/kugou/android/apprecommand/**",
                "com/kugou/android/appwidget/**",
                "com/kugou/android/audioidentify/**",
                "com/kugou/android/chinanet/**",
                "com/kugou/android/common/**",
                "com/kugou/android/concerts/**",
                "com/kugou/android/cpm/**",
                "com/kugou/android/desktoplyric/**",
                "com/kugou/android/dlna1/**",
                "com/kugou/android/download/**",
                "com/kugou/android/dynamicproxy/**",
                "com/kugou/android/friend/**",
                "com/kugou/android/fx/**",
                "com/kugou/android/listeningdata/**",
                "com/kugou/android/lyric/**",
//                "com/kugou/android/mediatransfer/**"  ,
//                "com/kugou/android/monthlyproxy/**"  ,
//                "com/kugou/android/msgcenter/**"  ,
//                "com/kugou/android/musiccircle/**"  ,
//                "com/kugou/android/musiccloud/**"  ,
//                "com/kugou/android/musiczone/**",
        ]

        public static final String[] ClassesForDex3 = [
                "com/kugou/android/mv/**",
                "com/kugou/android/mymusic/**",
                "com/kugou/android/netmusic/**",
                "com/kugou/android/notify/**"  ,
                "com/kugou/android/overlay_notice/**"  ,
                "com/kugou/android/recentweek/**"  ,
                "com/kugou/android/ringtonesarea/**",
                "com/kugou/android/RxUtils/**",
                "com/kugou/android/scan/**",
                "com/kugou/android/share/**",
                "com/kugou/android/sharelyric/**",
                "com/kugou/android/skin/**",
                "com/kugou/android/splash/**",
                "com/kugou/android/tool/**" ,
                "com/kugou/android/ugc/**" ,
                "com/kugou/android/update/**" ,
                "com/kugou/android/useraccount/**" ,
                "com/kugou/android/userCenter/**" ,
                "com/kugou/android/wishsongs/**" ,
                "com/kugou/android/wxapi/**",
        ]

        public static final String[] ClassesForDex7 = [
                "com/kugou/android/setting/util/WandoujiaUpdater**.class" ,
                "com/kugou/android/setting/util/YYBNotifyConfirmDialog**.class" ,
                "com/kugou/android/setting/util/YYBNotifyManager**.class" ,
                "com/kugou/android/setting/util/YYBUpdater**.class" ,
                "com/kugou/android/specialchannel/GoogleAdsDelegate**.class",
                "com/kugou/android/specialchannel/YYBUpdaterDelegate**.class"
        ]

        static FileSet fileSetForDexMain(Project kugouProject) {
            String dir = FileSetUtil4New.getReleasePath(kugouProject);
            String[] includes = ClassesForMainDexMust;
            FileSet fileSet = AntBaseUtil.makeFileSet(dir, includes, null);
            return fileSet;
        }

        static FileSet fileSetForDex2(Project kugouProject) {
            String dir = FileSetUtil4New.getReleasePath(kugouProject);
            String[] includes = ClassesForDex2
            String[] excludes = ClassesForMainDexMust
            FileSet fileSet = AntBaseUtil.makeFileSet(dir, includes, excludes);
            return fileSet;
        }

        static FileSet fileSetForDex3(Project kugouProject) {
            String dir = FileSetUtil4New.getReleasePath(kugouProject);
            String[] includes = ClassesForDex3
            FileSet fileSet = AntBaseUtil.makeFileSet(dir, includes, null);
            return fileSet;
        }

        static FileSet fileSetForDex4(Project kugouProject) {
            String dir = FileSetUtil4New.getReleasePath(kugouProject);
            String[] includes = [
                    "com/kugou/**"
            ];
            String[] excludes = FileSetUtil4New.mergeStringArray(
                    ClassesForMainDexMust, ClassesForDex2,
                    ClassesForDex3, ClassesForDex7, TestUtil.ClassesForDex8Com)

            FileSet fileSet = AntBaseUtil.makeFileSet(dir, includes, excludes);
            return fileSet;
        }

        static FileSet fileSetForDex7(Project kugouProject) {
            String dir = FileSetUtil4New.getReleasePath(kugouProject);
            String[] includes = ClassesForDex7
            FileSet fileSet = AntBaseUtil.makeFileSet(dir, includes, null);
            return fileSet;
        }

    }


    static class Others {
        /**
         * <!-- tinker jar包 -->
         */
        static FileSet[] fileSetOfTinker(Project commonProject) {
            String tinkerLibDir = "${commonProject.getBuildDir().absolutePath}/intermediates/exploded-aar/tinker-android-lib-release/jars/";
            String tinkerLoaderDir = "${commonProject.getBuildDir().absolutePath}/intermediates/exploded-aar/tinker-android-loader-release/jars/";

            String[] includes = [
                    tinkerLibDir + "classes.jar",
                    tinkerLibDir + "libs/" + "aosp-dexutils-1.8.1.jar",
                    tinkerLibDir + "libs/" + "bsdiff-util-1.8.1.jar",
                    tinkerLibDir + "libs/" + "tinker-commons-1.8.1.jar",
                    tinkerLoaderDir + "classes.jar"
            ]

            List<ZipFileSet> fileSets = new ArrayList<>();
            for (String s : includes) {
                ZipFileSet fileSet = new ZipFileSet();
                File file = new File(s);
                if (file.exists()) {
                    fileSet.src = file;
                    fileSets.add(fileSet)
                }
            }
            return (FileSet[]) fileSets.toArray();
        }


     static FileSet[] fileSetOfThirdPartyJars(Project commonProject) {
        String dir = "${commonProject.projectDir.absolutePath}/jars/";
        String[] includes = [
                "commons-httpclient.jar",
                "lcore.jar",
                "libammsdk.jar",
                "BaiduLBS_Android.jar",
                "MMAndroid_v1.3.1.jar",
                "android-support-v4_r22_custom.jar",
                "support-annotations-23.4.0.jar",
                "alipaysdk.jar",
                "alipaysecsdk.jar",
                "alipayutdid.jar",
                "android-async-http-1.4.3.jar",
                "cmmusic_lightweight_1.7.2.jar",
                "commons-codec.jar",
                "doreso.jar",
                "gson-2.2.4.jar",
                "jzlib.jar",
                "karaoke_media.jar",
                "libbdcooperation.jar",
                "mta-sdk-1.0.0.jar",
                "open_sdk.jar",
                "pinyin4j-2.5.0.jar",
                "qq-httpmime-4.1.3.jar",
                "QWeiboSDK_1.0.1.jar",
                "UPPayAssistEx.jar",
                "UPPayPluginExStd.jar",
                "weiboSDKCore_3.1.2.jar",
                "zing_core.jar",
                "AMap3D_V4.1.4.jar",
                "AMap_Location_V3.3.0.jar",
                "AMap_Services_V5.0.0.jar",
                "recyclerview-v7-23.0.0-kg.jar",
                "gridlayout-v7-23.1.1.jar",
                "paltette-v7-21.0.3.jar",
                "rxandroid-1.1.0.jar",
                "rxjava-1.2.3.jar"]

            List<ZipFileSet> fileSets = new ArrayList<>();
            for (String s1 : includes) {
                ZipFileSet fileSet = new ZipFileSet();
                File file = new File("${dir}" + s1);
                if (file.exists()) {
                    fileSet.src = file;
                    fileSets.add(fileSet)
                }
            }
            return fileSets.toArray()
        }

    }





    public void fillDexMainJar(Jar jar, Project commonProject, Project kugouProject) {
        jar.addFileset(CommonProj.fileSetForDexMain(commonProject));
        jar.addFileset(KugouProj.fileSetForDexMain(kugouProject));
        FileSet[] tinkerFiles = Others.fileSetOfTinker(commonProject)
        for (FileSet fileset : tinkerFiles) {
            jar.addFileset(fileset);
        }
    }

    public void fillDex2Jar(Jar jar, Project commonProject, Project kugouProject) {
        jar.addFileset(KugouProj.fileSetForDex2(kugouProject));
    }

    public void fillDex3Jar(Jar jar, Project commonProject, Project kugouProject) {
        jar.addFileset(KugouProj.fileSetForDex3(kugouProject));
    }

    public void fillDex4Jar(Jar jar, Project commonProject, Project kugouProject) {
        jar.addFileset(CommonProj.fileSetForDex4(commonProject))
        jar.addFileset(KugouProj.fileSetForDex4(kugouProject))
    }

    /**
     * <!-- 酷狗第三方jar包 -->
     */
    public void fillDex5Jar(Jar jar, Project commonProject) {
        FileSet[] thirdJarFiles = Others.fileSetOfThirdPartyJars(commonProject)
        for (FileSet fileset : thirdJarFiles) {
            jar.addFileset(fileset);
        }
    }


    public void fillDex7Jar(Jar jar, Project commonProject, Project kugouProject) {
        String commonDir = "${commonProject.projectDir.absolutePath}/jars/";
        String[] commonIncludes = [
                "UpdateLib_360_2.0.jar",
                "WandoujiaUpgradeSDK.jar"
        ]

        ArrayList<String> result = new ArrayList<>();
        for (String s : commonIncludes) {
            result.add(s);
        }
        String formal = ChangeCodeToBuildModel.getFormal(commonProject);
        if (formal != null) {
            switch (formal) {
                case "qihoo360":
                    result.remove("UpdateLib_360_2.0.jar");
                    break;
                case "wandoujiaqihoo360":
                    result.remove("UpdateLib_360_2.0.jar");
                    result.remove("WandoujiaUpgradeSDK.jar");
                    break;
                case "googlesdk":
                    result.add("GoogleConversionTrackingSdk-2.2.1.jar");
                    break;
                case "yingyongbao":
//                    result.add("jce.jar");
                    result.add("TMAssistantSDK_selfUpdate_201407240950.jar");
                    break;
            }
        }
        for (String s1 : result) {
            ZipFileSet commonFileSet = new ZipFileSet();
            File file = new File("${commonDir}" + s1);
            if (file.exists()) {
                commonFileSet.src = file;
                jar.addFileset(commonFileSet);
            }
        }

        FileSet commFileSet = CommonProj.fileSetForDex7(commonProject)
        jar.addFileset(commFileSet);

        FileSet kugoufileSet = KugouProj.fileSetForDex7(kugouProject)
        jar.addFileset(kugoufileSet);

    }

}
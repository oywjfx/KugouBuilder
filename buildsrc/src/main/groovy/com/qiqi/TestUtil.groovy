package com.qiqi

import com.qiqi.util.AntBaseUtil
import org.apache.tools.ant.taskdefs.Jar
import org.apache.tools.ant.types.FileSet
import org.apache.tools.ant.types.ZipFileSet
import org.gradle.api.Project

public class TestUtil {

    public static final String[] ClassesForDex8 = [
//            "com/kugou/android/mediatransfer/**"  ,
//                "com/kugou/android/monthlyproxy/**"  ,
//                "com/kugou/android/msgcenter/**"  ,
//                "com/kugou/android/musiccircle/**"  ,
//                "com/kugou/android/musiccloud/**"  ,
                "com/kugou/android/musiczone/**",
    ]

    public static final String[] ClassesForDex8Com = [

            "com/kugou/android/**",
            "com/kugou/framework",
    ]

    static public void fillDex8Jar(Jar jar, Project commonProject, Project kugouProject) {
        jar.addFileset(fileSetForDex8com(commonProject))
        jar.addFileset(fileSetForDex8(kugouProject))
        FileSet[] thirdJarFiles = fileSetOfThirdPartyJars(commonProject)
        for (FileSet fileset : thirdJarFiles) {
            jar.addFileset(fileset);
        }
    }

    static FileSet fileSetForDex8(Project kugouProject) {
        String dir = FileSetUtil4New.getReleasePath(kugouProject);
        String[] includes = ClassesForDex8
        FileSet fileSet = AntBaseUtil.makeFileSet(dir, includes, null);
        return fileSet;
    }


    static FileSet fileSetForDex8com(Project commonProject) {
        String dir = FileSetUtil4New.getReleasePath(commonProject);
        String[] includes = ClassesForDex8Com
        FileSet fileSet = AntBaseUtil.makeFileSet(dir, includes, null);
        return fileSet;
    }


    static FileSet[] fileSetOfThirdPartyJars(Project commonProject) {
        String dir = "${commonProject.projectDir.absolutePath}/jars/";
        String[] includes = [
                "monitorcore_1.3.jar",
                "MiPush_SDK_Client_3_5_2.jar",
                "kggson-2.7.jar",
                "HMS_SDK_2.5.3.302.jar",
                "protobuf-java-3.2.0.jar",
                "gdx.jar",
                "gdx-backend-android.jar",
                "hooker.jar",
                "VerifySDK.jar",
                "tmsdual_3.8.3.3099_2_no_wup.jar",
                "jce.jar"]

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

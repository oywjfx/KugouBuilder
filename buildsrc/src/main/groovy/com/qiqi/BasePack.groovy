package com.qiqi

import com.qiqi.util.AntBaseUtil
import com.qiqi.util.FileUitl
import org.gradle.api.Project

/**
 * Created by siganid on 2016/5/16.
 */
public class BasePack {
    //C:\Users\mingzhihuang.KUGOU\AppData\Local\Android\sdk
//    public static String sdkDir = "D:\\Develop\\Android\\SDK";
    public static String sdkDir = "D:\\develop\\software\\Android\\sdk";
    File jarTempFile = null;
    File dexTempFile = null;
    public static boolean shouleProguard = true;
    public static boolean fastBuild = false;
    public static boolean BuildHasLog = true;
    File moudleDexFile = null;
    public static boolean proguardRes = false;
    File proguardTempFile = null;
    File patchFile = null;
    File moduleTotleMapping;
    public static Project commonProject;
    Project smallProject;
    public static Project kugouProject;
    Project fanxingProject;
    Project tinkerLoaderProject;
    Project tinkerLibProject;
    File dex_methodsFile;
    org.apache.tools.ant.Project antProject = new org.apache.tools.ant.Project();
    File proGuardResInputDir = null;
    File proGuardResOutputDir = null;

    File noProGuardResOutputDir = null;

    File patchDir=null;
    File patchOutDir=null;
    String proguardPath
    String smallLibs
    FileSetUtil4New fileSetUtil4New;
    File outFile;
    //复制apk文件到release
    File releaseFolder;
    File unzippath;

    public BasePack() {
        fileSetUtil4New= new FileSetUtil4New();
    }

    protected void initDir(Project project) {
        commonProject = project.getRootProject().project(TestPlugin.ANDROIDCOMMONPROJECT);
        kugouProject = project.getRootProject().project(TestPlugin.ANDROIDKUGOU);
//        smallProject = project.getRootProject().project(HookProjectTask.SMALL);
//        tinkerLoaderProject = project.getRootProject().project(HookProjectTask.TINKERANDROIDLOADER);
//        tinkerLibProject = project.getRootProject().project(HookProjectTask.TINKERANDROIDLIB);
        //fanxingProject = project.getRootProject().project(HookProjectTask.MODULE_FANXING);

        jarTempFile = AntBaseUtil.makePath(project, "jar");
        dexTempFile = AntBaseUtil.makePath(project, "hostDex");
        moudleDexFile = AntBaseUtil.makePath(project, "moudleDexFile");
        proguardTempFile = AntBaseUtil.makePath(project, "proguard");
        proGuardResInputDir = AntBaseUtil.makePath(project, "proguardResInput");
        proGuardResOutputDir = AntBaseUtil.makePath(project, "proguardResOutPut");

        noProGuardResOutputDir= AntBaseUtil.makePath(project, "noProguardResOutPut");
        patchFile = AntBaseUtil.makePath(project, "patchFile");
        outFile = AntBaseUtil.makePath(project, "out");
        patchDir = AntBaseUtil.makePath(project, "patchDir");
        patchOutDir = AntBaseUtil.makePath(project, "patchOutDir");
        unzippath = AntBaseUtil.makePath(project, "unzip");
        dex_methodsFile = new File(outFile, "dex-methods");
        if (dex_methodsFile.exists()) {
            dex_methodsFile.delete();
        }
        dex_methodsFile.mkdir();
        moduleTotleMapping = new File(kugouProject.projectDir.absolutePath, "moduleTotleMapping.txt");
        if (!moduleTotleMapping.exists()) {
            moduleTotleMapping.createNewFile();
        }
        releaseFolder = new File(kugouProject.projectDir.absolutePath, "release");
        if (!releaseFolder.exists()) {
            releaseFolder.mkdirs();
        }

        proguardPath = kugouProject.projectDir.absolutePath + "\\AndResGuard-master-v2";
        smallLibs = kugouProject.projectDir.absolutePath + "\\smallLibs\\armeabi";
//        initProperties()
    }

    private void initProperties() {
        if (kugouProject.hasProperty("sdk.dir")) {
            sdkDir = kugouProject.properties["sdk.dir"];

            String localSdkDir = getPropertiesFromLocalProperties("sdk.dir")
            if (localSdkDir.length() > 0) {
                sdkDir = localSdkDir
            }
            proguardRes = Boolean.getBoolean(getPropertiesFromLocalProperties("proguardres"));

            BuildHasLog = kugouProject.properties["log"];

            //shouleProguard = kugouProject.properties["ProguardRes"];
            fastBuild = !shouleProguard;
            Log.Config("sdkDir:${sdkDir} ")
            Log.Config("shouleProguard:${shouleProguard} ")
            Log.Config("proguardRes:${proguardRes} ")
            Log.Config("fastBuild:${fastBuild} ")
            Log.Config("BuildHasLog :${BuildHasLog} ")
        } else {
            throw NullPointerException("no sdk found in Property")
        }
    }

    private String getPropertiesFromLocalProperties(String key) {
        Properties pps = new Properties();
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(kugouProject.getProjectDir().absolutePath + "/local.properties"));
        pps.load(inputStream);
        String value = pps.getProperty(key);
        return value;
    }

    /**
     * 资源混淆
     * @param inputPath
     * @param outPutPath
     * @param isRootProject
     */
    protected void proguardRes(String inputPath, String outPutPath, boolean isRootProject) {
        println "proguardRes jar ";
        List<String> proguardArgs = new ArrayList<>();
        proguardArgs.add("java");
        proguardArgs.add("-jar")
        if (isRootProject) {
            proguardArgs.add("${proguardPath}\\resourcesproguard.jar");
        } else {
            proguardArgs.add("${proguardPath}\\AndResGuard-cli.jar");
        }

        proguardArgs.add(inputPath);

        proguardArgs.add("-config");
        proguardArgs.add("${proguardPath}\\config.xml");

        proguardArgs.add("-out");
        proguardArgs.add(outPutPath);

        String cmd = "";
        for (String s : proguardArgs) {
            cmd = cmd + "${s} ";
        }
        AntBaseUtil.commandExec(cmd)
    }


    public void signAndCopyApk(String inputPath, String outPutPath) {
//        <!--flag--><path value ="D:/webplatform-ci/bin/cfgs/androidKugou.keystore" />
//        <!--storepass-->
//        <storepass value="123456789" />
//        <!--keypass-->
//        <keypass value="123456789" />
//        <!--alias-->
//        <alias value="androidkugou" />
        //    jarsigner -verbose -keystore abc.keystore -signedjar 123x.apk 123.apk abc.keystore

        // jarsgner-verbose-keystore[keystorePath]-singnedjar [apkOut] [apkln] [alias]
        List<String> signArgs = new ArrayList<>();
        signArgs.add("jarsigner");
        signArgs.add("-verbose")

        signArgs.add("-keystore")
        signArgs.add("D:/webplatform-ci/bin/cfgs/androidKugou.keystore");

        signArgs.add("-storepass")
        signArgs.add("123456789");

        signArgs.add("-keypass")
        signArgs.add("123456789");

        signArgs.add("-signedjar")
        signArgs.add(outPutPath);
        signArgs.add(inputPath);

        signArgs.add("androidkugou");
        String cmd = "";
        for (String s : signArgs) {
            cmd = cmd + "${s} ";
        }
        AntBaseUtil.commandExec(cmd)

    }

    public void getIdsAndPublicxml(String inputPath){
        List<String> signArgs = new ArrayList<>();
        signArgs.add("java");
        signArgs.add("-jar")
        signArgs.add(getKugouProject().getRootProject().projectDir.absolutePath+"/unapk/apktool_2.1.1.jar")
        signArgs.add("d")
        signArgs.add("-f");
        signArgs.add("-s");
        signArgs.add("-o")
        signArgs.add(unzippath.absolutePath);
        signArgs.add(inputPath)
        String cmd = "";
        for (String s : signArgs) {
            cmd = cmd + "${s} ";
        }
        AntBaseUtil.commandExec(cmd)
    }

    public void copyIdsAndPublicXml(String inputPath){
        List<String> signArgs = new ArrayList<>();
        signArgs.add("java");
        signArgs.add("-jar")
        signArgs.add(getKugouProject().getRootProject().projectDir.absolutePath+"/unapk/apktool_2.1.1.jar")
        signArgs.add("d")
        signArgs.add("-f");
        signArgs.add("-s");
        signArgs.add("-o")
        signArgs.add(unzippath.absolutePath);
        signArgs.add(inputPath)
        String cmd = "";
        for (String s : signArgs) {
            cmd = cmd + "${s} ";
        }
        AntBaseUtil.commandExec(cmd)
    }


    /**
     * 添加hostfix标签
     * @param project
     * @param jarfile
     */
    protected void addHotFixTag(Project project, String jarfile) {
        println "addHotFixTag jar ${project.name}";

        File jarFile = new File(jarfile);
        File jarFileBackJar = new File(jarfile + "_back.jar");
        FileUitl.copyFile(jarFile, jarFileBackJar, false);

        List<String> proguardArgs = new ArrayList<>();
        proguardArgs.add("java");
        proguardArgs.add("-jar")

        proguardArgs.add("${kugouProject.getProjectDir().absolutePath}\\pack-related\\antilazyLoad\\antilazyLoad.jar");
        proguardArgs.add("-input");
        proguardArgs.add(jarFileBackJar.absolutePath);

        proguardArgs.add("-output");
        proguardArgs.add(jarFile.absolutePath);

        proguardArgs.add("-excludes");
        proguardArgs.add("${kugouProject.getProjectDir().absolutePath}\\anitilazyload_excludes.txt");

        proguardArgs.add("-temppath");
        proguardArgs.add("${kugouProject.getProjectDir().absolutePath}\\antilazyloadtemp${project.name + jarFile.getName()}");

        String cmd = "";
        for (String s : proguardArgs) {
            cmd = cmd + "${s} ";
        }
        AntBaseUtil.commandExec(cmd);
        if (!jarFile.exists()) {
            throw new Exception("${project.name} addHotFixTag error")
        }
        println "addHotFixTag jar ${project.name} success";
    }

    public void kgJarToDex(String jarfile, String dexOutPutname, boolean nolocals) {
        print "Making jar ( ${jarfile} ) to dex";
        File dxFile = new File(sdkDir + "\\build-tools\\23.0.1\\dx.bat");
//        if (!dxFile.exists()) {
//            //这是线上的版本
//            dxFile = new File(sdkDir + "\\build-tools\\build-tools-23.0.0\\dx.bat");
//        }
        List<String> dexargs = new ArrayList<>();
        dexargs.add(dxFile.absolutePath);
        dexargs.add("--dex");
        dexargs.add("--no-locals")
        dexargs.add("--force-jumbo");
        // 添加此标志以规避dx.jar对"java/"、"javax/"的检查
        dexargs.add("--core-library")
        // disableDexMerger="${dex.disable.merger}"

        dexargs.add("--output=${dexTempFile.absolutePath}/${dexOutPutname}");
        dexargs.add("${jarfile}")
        String cmd = "";
        for (String s : dexargs) {
            cmd = cmd + "${s} ";
        }
        AntBaseUtil.commandExec(cmd)
    }


    public void zipalignAndCopy(File inputApkFile, File outPutApkFile) {
        File zipalign = new File(sdkDir + "\\build-tools\\23.0.1\\zipalign.exe");
//        if (!zipalign.exists()) {
//            //这是线上的版本
//            zipalign = new File(sdkDir + "\\build-tools\\build-tools-23.0.0\\zipalign.exe");
//        }
        List<String> zipalignArgs = new ArrayList<>();
        zipalignArgs.add(zipalign.getAbsolutePath());
        zipalignArgs.add("-v");
        zipalignArgs.add("4");
        zipalignArgs.add(inputApkFile.getAbsolutePath());
        zipalignArgs.add(outPutApkFile.getAbsolutePath());
        String cmd = "";
        for (String s : zipalignArgs) {
            cmd = cmd + "${s} ";
        }
        AntBaseUtil.commandExec(cmd)
    }

    protected void countDexMethod(File dexFile, File outPutFile) {
        if (!dexFile.exists()) {
            println("file is not exit");
            throw new ClassFormatError("Build failed with an error, dex file '"
                    + dexFile + "' does not exist. See the FileSetUtil4New.groovy");
        }
        String jarPath = "${kugouProject.projectDir.absolutePath}/pack-related/dex_method_count/dex-method-counts.jar";
        List<String> args = new ArrayList<>();
        args.add("java");
        args.add("-jar");
        args.add(jarPath);
        args.add(dexFile.absolutePath);
        args.add("-output");
        args.add(outPutFile.absolutePath);

        String cmd = "";
        for (String s : args) {
            cmd = cmd + "${s} ";
        }
        AntBaseUtil.commandExec(cmd, outPutFile);
    }

    protected String getLibInJarFromCommon() {
        File commonFile = new File(commonProject.getProjectDir().absolutePath + "\\jars");
        File[] jars = null;
        if (commonFile.exists()) {
            jars = commonFile.listFiles();
        }
        ArrayList<String> cmdCode = new ArrayList<>();
        for (File file : jars) {
            if (file.exists()) {
                cmdCode.add("-libraryjars");
                cmdCode.add(file.absolutePath);
            }
        }
        String cmd = "";
        for (String s : cmdCode) {
            if (s.length() > 0) {
                cmd = cmd + s + " ";
            }
        }
        return cmd;
    }

}

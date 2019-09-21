package com.qiqi

import com.qiqi.util.AntBaseUtil
import com.qiqi.util.FileUitl
import com.qiqi.util.Log
import com.qiqi.util.ThreadUtil
import org.apache.tools.ant.taskdefs.Jar
import org.gradle.api.Project
import org.gradle.wrapper.Download
import org.gradle.wrapper.Logger

/**
 * Created by siganid on 2016/5/16.
 */
public class HostPackImpl extends BasePack {

    public HostPackImpl() {
        super();
    }

    public void initMainJar(Project project) {
        long start = System.currentTimeMillis();
        makeMainjar(project);
        packKugou2Jar(project);
        packKugou3Jar(project);
        packKugou8Jar(project);
        packKugou4Jar(project);
        packKugou5Jar(project);
        packKugou6Jar(project);
        long devideDexTime= System.currentTimeMillis();
        Log.Time("分主dex包时间："+(devideDexTime-start))
//        if (!fastBuild) {
//            addHostJarHotFixTag(project);
//        }
        long addHotFixTime= System.currentTimeMillis();
        Log.Time("添加标签时间："+(addHotFixTime-devideDexTime))
//        proguardJar(project);

        long  proguardTime= System.currentTimeMillis();
        Log.Time("混淆时间："+(proguardTime-addHotFixTime))
        AntBaseUtil.baseCopy("${proguardTempFile.absolutePath}/mapping.txt", "${outFile.absolutePath}/mapping.txt")

        long  patchTime= System.currentTimeMillis();
        Log.Time("Patch时间："+(patchTime-proguardTime))
    }

    /**
     * 把dex包复制到gradle 的打包目录
     */
    public void copyDexToPackageFolder() {
        String[] outputdexName = ["classes.dex", "classes2.dex", "classes3.dex", "classes4.dex", "classes5.dex", "classes6.dex", "classes7.dex"];
        String outPath = kugouProject.getBuildDir().absolutePath + "\\intermediates\\transforms\\dex\\release\\folders\\1000\\1f\\main";

        for (int i= 0;i<outputdexName.length;i++){
            String classes = "${dexTempFile.absolutePath}/${outputdexName[i]}";
            String toClasses = "${outPath}/${outputdexName[i]}";
            AntBaseUtil.baseCopy(classes, toClasses);
        }
        Log.Stute("複製包到主package成功");
    }

    public void doProguardKugouRes(Project project) {

        initDir(project);
        String srcFilePath = kugouProject.getBuildDir().absolutePath + "\\outputs\\apk\\androidkugou-release-unsigned.apk";
        File srcFile = new File(srcFilePath);
        if (!srcFile.exists()) {
            srcFile = new File(kugouProject.getBuildDir().absolutePath + "\\outputs\\apk\\androidkugou-release.apk");
        }
        String inputPathForResProguard = kugouProject.getBuildDir().absolutePath + "\\outputs\\apk\\kugou-release-no-x86.apk";
        FileUitl.copyFile(srcFile, new File(inputPathForResProguard), false);

        File kugouProGuardResOut = new File(kugouProject.projectDir.absolutePath + "/AndResGuard-master-v2", "out");
        if (!kugouProGuardResOut.exists()) {
            kugouProGuardResOut.mkdirs();
        }
        proguardRes(inputPathForResProguard, kugouProGuardResOut.absolutePath, true);

        //把渠道号文件写进apk
        if (!ChangeCodeToBuildModel.isBuildChannel(project)) {
            makeChannelFile(project);
        }

        try {
            makeHotfixJar();
        } catch (Exception e) {
            e.printStackTrace()
        }
        copyAllBuildFileToDest(project);

    }

    private void makeChannelFile(Project project) {
        List<String> makeChannelFile = new ArrayList<>();
        makeChannelFile.add("python");
        makeChannelFile.add("${kugouProject.getProjectDir().absolutePath}/make_channel_apk.py")
        makeChannelFile.add("-c");
        makeChannelFile.add("${kugouProject.getProjectDir().absolutePath}/kgchannel_201");
        makeChannelFile.add("-a");
        makeChannelFile.add("${kugouProject.projectDir.absolutePath}/AndResGuard-master-v2/out/kugou-release-no-x86_signed.apk");
        makeChannelFile.add("-i201");

        def prop = new Properties()
        prop.load(kugouProject.file('local.properties').newDataInputStream())
        String gitVersion = prop.getProperty('app.version.git')
        makeChannelFile.add("-g");
        makeChannelFile.add(gitVersion);

        String cmd = "";
        for (String s : makeChannelFile) {
            cmd = cmd + "${s} ";
        }
        AntBaseUtil.commandExec(cmd)
    }


    static String[] outJarName = ["kugoumain4dex.jar", "kugou2nd4dex.jar", "kugou3rd4dex.jar",'kugou4th4dex.jar','kugou5th4dex.jar', "kugou6th4dex.jar", 'kugou8th4dex.jar'];
//    static String[] outJarName = ['kugou8th4dex.jar','kugou4th4dex.jar'];
    static String[] outProguardJarName = ["obf_main4dex.jar", "obf_kugou2nd.jar", "obf_3rd.jar",'obf_4th.jar','obf_5th.jar', "obf_dlna.jar", 'obf_6th.jar'];
    static String[] outputdexName = ["classes.dex", "classes2.dex", "classes3.dex", "classes4.dex", "classes5.dex", "classes6.dex", "classes7.dex"];
    String[] outputMethodFileName = ["classes.txt", "classes2.txt", "classes3.txt", "classes4.txt", "classes5.txt", "classes6.txt", "classes7.txt"];

    public void transFormJarToDex(Project project) {
        initDir(project);
        String baseDir = dexTempFile.absolutePath;
        for (int i = 0; i < outJarName.length; i++) {
            String jarPath = "${jarTempFile.absolutePath}/${outJarName[i]}";
            kgJarToDex(jarPath, outputdexName[i], false);
            File inputFile = new File(baseDir + "/" + outputdexName[i]);
            File outputFile = new File(dex_methodsFile.absolutePath + "/" + outputMethodFileName[i]);
            if (!fastBuild) {
                countDexMethod(inputFile, outputFile);
            }

        }
    }


    private void addHostJarHotFixTag(Project project) {
        List<Runnable> runnableList = new ArrayList<>();
        String[] jarNames = ['kugoumain4dex.jar','kugou2nd4dex.jar','kugou3rd4dex.jar','kugou4th4dex.jar','kugou5th4dex.jar']
       for (int i=0;i<jarNames.length;i++){
           final String name = jarNames[i];
           runnableList.add(new Runnable() {
               @Override
               void run() {
                   addHotFixTag(project, jarTempFile.absolutePath + "/"+name);
               }
           })
       }
        ThreadUtil.syncRun(runnableList);
    }


    String[] proguardJarsInName = ['kugoumain4dex.jar','kugou2nd4dex.jar','kugou3rd4dex.jar','kugou4th4dex.jar','kugou5th4dex.jar', 'androiddlna.jar', 'kugou6th4dex.jar']
    String[] proguardJarsOutName = ['obf_main4dex.jar','obf_kugou2nd.jar','obf_3rd.jar','obf_4th.jar','obf_5th.jar', "obf_dlna.jar", 'obf_6th.jar']

    private void copyFileWhenNotProguardJar(Project project){
        for (int i=0;i<proguardJarsInName.length;i++){
            String inJarName = proguardJarsInName[i];
            String outJarName = proguardJarsOutName[i];
            try {
                FileUitl.copyFile(new File("${jarTempFile.absolutePath}/${inJarName}"),
                        new File("${proguardTempFile.absolutePath}/${outJarName}"), false);
            }catch (Exception e){
                e.printStackTrace()
            }

        }
    }

    private void copyPluginFileWhenNotProguardJar() {
//        for (int i = 0; i < ProjectInfo.pluginProjectInfolists.length; i++) {
//            ProjectInfo projectInfo = ProjectInfo.pluginProjectInfolists[i];
//
//            String inJarName = projectInfo.hostJarName+".jar";
//            String outJarName = projectInfo.proguardJarName;
//
//            Log.Stute("inJarName:"+inJarName)
//            Log.Stute("outJarName:"+outJarName)
//
//            try {
//                FileUitl.copyFile(new File("${jarTempFile.absolutePath}/${inJarName}"),
//                        new File("${proguardTempFile.absolutePath}/${outJarName}"), false);
//            } catch (Exception e) {
//                e.printStackTrace()
//            }
//
//        }
    }


    private  String getProguardString4Miandex(Project project){
        initDir(project)
        ArrayList<String> cmdCode = new ArrayList<>();
        for (int i=0;i<proguardJarsInName.length;i++){
        String inJarName =jarTempFile.absolutePath+"/"+ proguardJarsInName[i];
        String outJarName =proguardTempFile.absolutePath+"/"+ proguardJarsOutName[i];
            cmdCode.add("-injars");
            cmdCode.add(inJarName);
            cmdCode.add("-outjars");
            cmdCode.add(outJarName);
        }
        String cmd = "";
        for (String s : cmdCode) {
            cmd = cmd + s + " ";
        }
        Log.Command(cmd);
        return cmd;
    }


    private void proguardJar(Project project) {
//        if (!shouleProguard) {
            copyFileWhenNotProguardJar(project)
            copyPluginFileWhenNotProguardJar()
//            return;
//        }

        initDir(project);
        String proguardJarPath = "${sdkDir}/tools/proguard/lib/proguard.jar";
        String proguardCfgPath = kugouProject.getProjectDir().absolutePath + "/proguard.cfg";
        String libraryjars = kugouProject.getBuildDir().absolutePath + "\\intermediates\\classes\\release";
        String outPutDump = "${proguardTempFile.absolutePath}/dump.txt";
        String printseeds = "${proguardTempFile.absolutePath}/seeds.txt";
        String printusage = "${proguardTempFile.absolutePath}/usage.txt";
        String printmapping = "${proguardTempFile.absolutePath}/mapping.txt";
        String proguardTxt = "${proguardTempFile.absolutePath}/proguard.txt";
        String[] cmdCode = [
                "java", "-jar", "-Xms1024m -Xmx2048m -Xss512m", proguardJarPath,
                "-include", proguardCfgPath,
                //"-include",proguardTxt,
                getProguardString4Miandex(project),
//                getProguardJarString(project,ModulePackImpl.moduleJar),

                "-libraryjars", libraryjars,
                "-libraryjars", commonProject.getRootProject().getProjectDir().absolutePath + "\\libs_game",
                "-libraryjars", commonProject.getBuildDir().absolutePath + "\\intermediates\\classes\\release",
                getLibInJarFromCommon(),
                "-libraryjars", sdkDir + "\\platforms\\android-21\\android.jar",
                "-dump", outPutDump,
                "-printseeds", printseeds,
                "-printusage", printusage,
                "-printmapping", printmapping
        ]

        String cmd = "";
        for (String s : cmdCode) {
            if (s.length() > 0) {
                cmd = cmd + s + " ";
            }
        }
        AntBaseUtil.commandExec(cmd)
    }

    public String getProguardJarString(Project project,ArrayList<File> jars) {
        initDir(project)
        ArrayList<String> cmdCode = new ArrayList<>();
        for (File file : jars) {
            String jarPath = file.getAbsolutePath();
            File jarFile = new File(jarPath);
            File outPutFile = new File(proguardTempFile, getProguardOutPutName(jarFile));
            cmdCode.add("-injars");
            cmdCode.add(jarFile.absolutePath);
            cmdCode.add("-outjars");
            cmdCode.add(outPutFile.absolutePath);
        }
        String cmd = "";
        for (String s : cmdCode) {
            cmd = cmd + s + " ";
        }
        Log.Command(cmd);
        return cmd;
    }

    public void makeHotfixJar() {
        String baseProject = kugouProject.getRootProject().getProjectDir().absolutePath;
        Properties properties = DepenAndVersionUtil.getProperties(kugouProject);
        String path = properties.getProperty("patch_old_apk_path");
        if (path == null || path.length() == 0) {
            return;
        }
        Log.Stute("path:" + path);

        File oldApkFile = new File(patchDir, "old.apk");
        new Download(new Logger(false),"","").download(new URI(path), oldApkFile);
        if (!oldApkFile.exists() || oldApkFile.length() == 0) {
            Log.Stute("文件下载失败")
        }
        //java -jar F:\tinker\TestContentProvider\tinker-build\tinker-patch-cli\build\libs\tinker-patch-cli-1.18.jar
        // -old old.apk
        // -new F:\tinker\TestContentProvider\app\build\outputs\apk\app-debug.apk -config
        // E:\\tinker_config.xml -out E:\\temp
        String jarPath = "${baseProject}/tinker/hotfixTools/tinker-patch-cli-1.8.1.jar";
        List<String> args = new ArrayList<>();
        args.add("java");
        args.add("-jar");
        args.add(jarPath);
        args.add("-old");
        args.add(patchDir.absolutePath + "/old.apk");
        args.add("-new");
        args.add("${kugouProject.projectDir.absolutePath}/AndResGuard-master-v2/out/kugou-release-no-x86_signed.apk");
        args.add("-config");
        args.add("${baseProject}/tinker/hotfixTools/tool_output/tinker_config.xml");
        args.add("-out");
        args.add(patchOutDir.absolutePath);
        String cmd = "";

        for (String s : args) {
            cmd = cmd + "${s} ";
        }
        AntBaseUtil.commandExec(cmd)

    }

    public void copyKeepResApk() {
        //复制keep res apk文件
        File srcKeepResApkFile = new File(kugouProject.getBuildDir().absolutePath + "\\outputs\\apk\\androidkugou-release-unsigned.apk")
        if (!srcKeepResApkFile.exists()) {
            srcKeepResApkFile = new File(kugouProject.getBuildDir().absolutePath + "\\outputs\\apk\\androidkugou-release.apk")
        }
        File releaseKeepResApkFile = new File("${releaseFolder.absolutePath}/kugou-release-no-x86-keep-res.apk")

        addSoFileToApk();

        File desKeepResApkFile = new File(noProGuardResOutputDir, "kugou-release-no-x86-keep-res.apk")
        signAndCopyApk(desKeepResApkFile.absolutePath, releaseKeepResApkFile.absolutePath)
        sleep(5000)

        makePairApkFile(releaseKeepResApkFile)
    }

    void makePairApkFile(File desKeepResApkFile) {
        File releaseKeepResApkFileForPairTemp = new File(releaseFolder, "kugou-release-no-x86-keep-res-pairtemp.apk")
        FileUitl.copyFile(desKeepResApkFile, releaseKeepResApkFileForPairTemp, false);



        File desClass8DexFile = new File(releaseFolder.absolutePath, "classes8.dex");
        File srcPairDexFile = new File(dexTempFile.absolutePath, "androidpair.dex");
        FileUitl.copyFile(srcPairDexFile, desClass8DexFile, false);

        String cmd = "${kugouProject.getProjectDir().getAbsolutePath()}\\tools\\buildSpecialApk\\7z a kugou-release-no-x86-keep-res-pairtemp.apk classes8.dex";
        Log.Stute("cmd：" + cmd);

        sleep(1000)
        Runtime r = Runtime.getRuntime();
        Process p = r.exec(cmd, null, releaseFolder);
        BufferedReader br = new BufferedReader(new InputStreamReader(p
                .getInputStream()));
        StringBuffer sb = new StringBuffer();
        String inline;
        while (null != (inline = br.readLine())) {
            sb.append(inline).append("\n");
        }
        System.out.println(sb.toString());
        sleep(1000)

        File releaseKeepResApkFileForPair = new File(releaseFolder, "kugou-release-no-x86-keep-res-pair.apk")
        signAndCopyApk(releaseKeepResApkFileForPairTemp.absolutePath, releaseKeepResApkFileForPair.absolutePath);

        releaseKeepResApkFileForPairTemp.delete()
        desClass8DexFile.delete()
    }



    void addSoFileToApk() {
        File lib = new File(noProGuardResOutputDir, "/lib/armeabi/")
        lib.mkdirs();

        noProGuardResOutputDir.listFiles().each { file ->
            if (file.getName().endsWith(".so")) {
                FileUitl.copyFile(file, new File(lib, file.getName()), false);
            }
        }

        File srcKeepResApkFile = new File(kugouProject.getBuildDir().absolutePath + "\\outputs\\apk\\androidkugou-release-unsigned.apk")
        File desKeepResApkFile = new File(noProGuardResOutputDir, "kugou-release-no-x86-keep-res.apk")
        FileUitl.copyFile(srcKeepResApkFile, desKeepResApkFile, false);


        String cmd = "${kugouProject.getProjectDir().getAbsolutePath()}\\tools\\buildSpecialApk\\7z u kugou-release-no-x86-keep-res.apk lib";
        Log.Stute("cmd：" + cmd);


        sleep(1000)
        Runtime r = Runtime.getRuntime();
        Process p = r.exec(cmd, null, noProGuardResOutputDir);
        BufferedReader br = new BufferedReader(new InputStreamReader(p
                .getInputStream()));
        StringBuffer sb = new StringBuffer();
        String inline;
        while (null != (inline = br.readLine())) {
            sb.append(inline).append("\n");
        }
        System.out.println(sb.toString());
        sleep(1000)



    }

    void addFileInApk(String srcApkFile, String filePath) {
        AntBaseUtil.commandExec("${kugouProject.projectDir.absolutePath}/tools/buildSpecialApk/7z a ${srcApkFile} ${filePath}")
        sleep(1000)
    }

    /**
     * 打包完成复制所有的输出文件到out目录下
     * @param project
     */
    private void copyAllBuildFileToDest(Project project) {
        initDir(project);
        //复制apk文件
        File srcApkFile = new File("${kugouProject.projectDir.absolutePath}/AndResGuard-master-v2/out/kugou-release-no-x86_signed.apk")
        File releaseApkFile = new File("${releaseFolder.absolutePath}/kugou-release-no-x86.apk")
        zipalignAndCopy(srcApkFile, releaseApkFile);

        copyKeepResApk()

        //getIdsAndPublicxml(srcKeepResApkFile.absolutePath);

//        //复制id.xml和public.xml
//        File srcIdsFile = new File("${unzippath.absolutePath}/res/values/ids.xml")
//        File destIdsFile = new File("${releaseFolder.absolutePath}/ids.xml")
//        FileUitl.copyFile(srcIdsFile, destIdsFile, false);
//        File srcPublicFile = new File("${unzippath.absolutePath}/res/values/public.xml")
//        File destPublicFile = new File("${releaseFolder.absolutePath}/public.xml")
//        FileUitl.copyFile(srcPublicFile, destPublicFile, false);

        //复制mapping文件
        File srcMappingFile = new File("${proguardTempFile.absolutePath}/mapping.txt")
        File destMappingFile = new File("${releaseFolder.absolutePath}/kugou-mapping.txt")
        FileUitl.copyFile(srcMappingFile, destMappingFile, false);

        //复制R.txt文件
        File srcRFile = new File("${kugouProject.buildDir.absolutePath}/intermediates/symbols/release/R.txt")
        File destRFile = new File("${releaseFolder.absolutePath}/R.txt")
        FileUitl.copyFile(srcRFile, destRFile, false);


        //复制patch.jar文件
        File srcPatchFile = new File("${patchOutDir.absolutePath}/patch_signed.apk")
        File destPatchFile = new File("${releaseFolder.absolutePath}/patch.jar")
        try {
            FileUitl.copyFile(srcPatchFile, destPatchFile, false);
        } catch (Exception e) {
            e.printStackTrace()
        }

        //复制方法数文件
        copyMethodOutFile();

        //复制插件包到release目录
        File plugInFile = new File("${releaseFolder.absolutePath}/PlugIn");
        if (!plugInFile.exists()) {
            plugInFile.mkdirs();
        }
        try {
            FileUitl.copyDirectory(new File(kugouProject.projectDir, "smallLibs"), plugInFile);
        } catch (Exception e) {
            e.printStackTrace()
        }

        mergeKtvSecondToKtv()

        File gameVerFile = new File(plugInFile, "PlugVerFile.txt");
        gameVerFile.setText("游戏模块版本号:${ChangeCodeToBuildModel.getGameVer(project)}")

        // pageIds.txt 文件复制到目标目录
        File srcPageIdsFile = new File("${kugouProject.buildDir.absolutePath}/generated/pageIds.txt")
        File destPageIdsFile = new File("${releaseFolder.absolutePath}/pageIds.txt")
        try {
            FileUitl.copyFile(srcPageIdsFile, destPageIdsFile, false);
        } catch (Exception e) {
            e.printStackTrace()
        }

        // LoadingTypes 文件复制到目标目录
        File srcLoadingTypesFile = new File("${kugouProject.buildDir.absolutePath}/generated/loadingTypes.txt")
        File destLoadingTypesFile = new File("${releaseFolder.absolutePath}/loadingTypes.txt")
        try {
            FileUitl.copyFile(srcLoadingTypesFile, destLoadingTypesFile, false);
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    void mergeKtvSecondToKtv() {
        File releasePluginFile = new File("${releaseFolder.absolutePath}/PlugIn/armeabi")

        File ktvSecondFile = new File(releasePluginFile, "libandroidktvsecond.so");
        File ktvFile = new File(releasePluginFile, "libandroidktv.so");
        File ktvOriginFile = new File(releasePluginFile, "libandroidktvOrigin.so");
        FileUitl.copyFile(ktvFile, ktvOriginFile, false);

        File desKtvSecondLib = new File("${releaseFolder.absolutePath}/PlugIn/armeabi/lib")
        desKtvSecondLib.mkdirs()
        File desKtvSecond = new File(desKtvSecondLib, "libandroidktvsecond.so")
        FileUitl.copyFile(ktvSecondFile, desKtvSecond, false);


        String cmd = "${kugouProject.getProjectDir().getAbsolutePath()}\\tools\\buildSpecialApk\\7z a libandroidktv.so lib";
        Log.Stute("cmd：" + cmd);

        sleep(1000)
        Runtime r = Runtime.getRuntime();
        Process p = r.exec(cmd, null, releasePluginFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(p
                .getInputStream()));
        StringBuffer sb = new StringBuffer();
        String inline;
        while (null != (inline = br.readLine())) {
            sb.append(inline).append("\n");
        }
        System.out.println(sb.toString());
        sleep(1000)

        desKtvSecondLib.delete()
    }

    /**
     * 复制方法数文件
     */
    private void copyMethodOutFile() {
//        String[] inputFileName = ["classes.txt", "classes2.txt", "classes3.txt"];
//        String[] outPutFileName = ["kugoumain.txt", "kugou2nd.txt", "kugou3rd.txt"];
//        String baseDir = dexTempFile.absolutePath;
//        for (int i = 0; i < inputFileName.length; i++) {
//            File inputFile = new File(baseDir + "/" + inputFileName[i]);
//            File outputFile = new File(dex_methodsFile.absolutePath + "/" + outPutFileName[i]);
//            FileUitl.copyFile(inputFile, outputFile, false);
//        }
        File releaseDexMethodsFolder = new File(releaseFolder, "dex-methods");
        releaseDexMethodsFolder.deleteOnExit();
        releaseDexMethodsFolder.mkdirs();
        FileUitl.copyDirectory(dex_methodsFile, releaseDexMethodsFolder);
    }

    //<!-- 酷狗主dex的jar包 -->
    private void makeMainjar(Project project) {
        print("--->makeMainjar")
        initDir(project);
        Jar jar = new Jar();
        jar.setProject(antProject);
        print(jarTempFile.absolutePath + "/kugoumain4dex.jar")
        jar.destFile = new File(jarTempFile.absolutePath + "/kugoumain4dex.jar");
        fileSetUtil4New.fillDexMainJar(jar, commonProject, kugouProject)
        jar.execute();
    }

    //  <!-- 酷狗2nd dex的jar包 -->
    private void packKugou2Jar(Project project) {
        println("--->packKugou2Jar")
        Jar jar = new Jar();
        jar.setProject(antProject);
        jar.destFile = new File(jarTempFile.absolutePath + "/kugou2nd4dex.jar");
        fileSetUtil4New.fillDex2Jar(jar, commonProject, kugouProject)
        jar.execute();
    }

    /**
     *  <!-- 酷狗的其他第三方jar包 -->
     *      输出为classes3
     */
    private void packKugou3Jar(Project project) {
        print("--->packKugou3rdJar")
        Jar jar = new Jar();
        jar.setProject(antProject);
        jar.destFile = new File(jarTempFile.absolutePath + "/kugou3rd4dex.jar");
        fileSetUtil4New.fillDex3Jar(jar, commonProject, kugouProject)
        jar.execute();
    }

    /**
     * 其他包
     * @param project
     */
    private void packKugou8Jar(Project project) {
        print("--->packKugou3rdJar")
        Jar jar = new Jar();
        jar.setProject(antProject);
        jar.destFile = new File(jarTempFile.absolutePath + "/kugou8th4dex.jar");
        TestUtil.fillDex8Jar(jar, commonProject, kugouProject)
        jar.execute();
    }

    /**
     *  <!-- 酷狗的其他第三方jar包 -->
     *      输出为classes4
     */
    private void packKugou4Jar(Project project) {
        print("--->packKugou3rdJar")
        Jar jar = new Jar();
        jar.setProject(antProject);
        jar.destFile = new File(jarTempFile.absolutePath + "/kugou4th4dex.jar");
        fileSetUtil4New.fillDex4Jar(jar, commonProject, kugouProject)
        jar.execute();
    }

    /**
     *  <!-- 酷狗的其他第三方jar包 -->
     *      输出为classes5
     */
    private void packKugou5Jar(Project project) {
        print("--->packKugou3rdJar")
        Jar jar = new Jar();
        jar.setProject(antProject);
        jar.destFile = new File(jarTempFile.absolutePath + "/kugou5th4dex.jar");
        fileSetUtil4New.fillDex5Jar(jar, commonProject)

        jar.execute();
    }

    /**
     * classes7.dex
     */
    private void packKugou6Jar(Project project) {
        print("--->packKugou6rdJar")
        Jar jar = new Jar();
        jar.setProject(antProject);
        jar.destFile = new File(jarTempFile.absolutePath + "/kugou6th4dex.jar");
        fileSetUtil4New.fillDex7Jar(jar, commonProject, kugouProject)

        jar.execute();
    }


}

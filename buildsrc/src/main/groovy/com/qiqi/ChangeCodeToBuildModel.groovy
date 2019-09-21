package com.qiqi

import org.apache.tools.ant.taskdefs.Zip
import org.apache.tools.ant.types.ZipFileSet
import org.gradle.api.Project
import org.gradle.wrapper.Download
import org.gradle.wrapper.Logger
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * Created by siganid on 2016/5/16.
 */
public class ChangeCodeToBuildModel extends BasePack {

    def curVersionCode;

    public void fixCode(Project project) {
        initDir(project);
        fixIp();
        fixLog();
        changeOther();
        changeGradleFileForGameAndConmmon();
        changeGradleFileForFangxing();
        changeGradleFileForKTV();
        changeGradleFileForKtvSecond();
        changeGradleFileForFanxingMedia();
        changeGradleFileForKuqun();
        changeGradleFileForLyricMaker();
        changeGradleFileForDataShow();
        changeGradleFileForZego();

        changeGradleFileForVoiceHelper();

        changeGradleFileForModuleH5();

        fixKugouDependCode();
        fixNetworkTestCode();
        dealX86SO();
        // deleteFile(project);
        copyKeyStoreFile();
        changePlugInVer(project)


        fixIdsAndPublicXmp();
        changeTinkerIdByVersion();

        changePatchCode(project);
        deleteGameSoInKugou();
        copySoToReleaseDelFromHost();
//        HostPackImpl hostPack = new HostPackImpl();
//        hostPack.initDir(project);
//        hostPack.makeHotfixJar();
//        HostPackImpl hostPack = new HostPackImpl()
//        hostPack.initDir(project)
//        hostPack.copyKeepResApk()

        //没有热修复资源所以暂时不需要
        // genIdxAndPublic(project);
    }

    void deleteGameSoInKugou() {
        try {
            File gameSoFile = new File(kugouProject.getProjectDir().absolutePath + "/smallLibs/armeabi/libmodulegame.so");
            if (gameSoFile.exists()) {
                gameSoFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    private void dealX86SO() {
        boolean hasX86 = kugouProject.properties["hasX86"];
        if (hasX86) {
            return;
        }
        File x86File = new File(commonProject.getProjectDir().absolutePath + "/libs/x86");
        if (!x86File.exists()) {
            return;
        }
        org.apache.tools.ant.Project antProject = new org.apache.tools.ant.Project();
        ZipFileSet zipFileSet = new ZipFileSet();
        zipFileSet.dir = new File(commonProject.getProjectDir().absolutePath + "/libs/x86");
        Zip zip = new Zip();
        zip.setProject(antProject)
        zip.destFile = new File(releaseFolder.absolutePath + "/x86_so.zip");
        zip.addZipfileset(zipFileSet);
        zip.execute();

        zipFileSet.dir.deleteDir();

    }


//    private void copySoToReleaseDelFromHost() {
//        ProjectInfo[] projectInfos = [ProjectInfo.Zego, ProjectInfo.DataShow, ProjectInfo.moduleH5,ProjectInfo.moduleVoiceHelper]
//
//        projectInfos.each { projectInfo ->
//            String baseName = projectInfo.doProguardResOutName.split(".apk")[0];
//            File src = new File("${smallLibs}\\${baseName + ".so"}");
//            if (!src.exists()) {
//                Log.Stute("Zego文件不存在")
//                return;
//            }
//            File destFolder = new File("${releaseFolder.absolutePath}/PlugIn/armeabi");
//            destFolder.mkdirs();
//            File dest = new File(destFolder, baseName + ".so");
//            try {
//                FileUitl.copyFile(src, dest, false);
//                Log.Stute("复制文件成功:" + dest.absolutePath);
//                src.delete();
//                src.deleteOnExit()
//            } catch (Exception e) {
//                e.printStackTrace()
//                Log.Stute("复制文件失败:" + src.absolutePath);
//            }
//        }
//    }

    private void copyKeyStoreFile() {

        def prop = new Properties()
        prop.load(kugouProject.file('local.properties').newDataInputStream())
        String krystorePath = prop.getProperty('key.store')
        Log.Stute("krystorePath :" + krystorePath);
        File srcFile = new File(krystorePath);
        File destFile = new File(kugouProject.getRootProject().getProjectDir().absolutePath, "keystore");
        FileUitl.copyFile(srcFile, destFile, false);
    }

    private void changeGradleFileForGameAndConmmon() {
//        File gameGradleBuildFile = kugouProject.getRootProject().project(ProjectInfo.Game.name).getBuildFile();
//        File gameGradleSrcFile = new File(kugouProject.getRootProject().getProjectDir(), "GameGradleBuildFile.txt");
//        gameGradleBuildFile.setText("");
//        gameGradleBuildFile.setText(gameGradleSrcFile.getText());

//        File commonGradleBuildFile = kugouProject.getRootProject().project(HookProjectTask.ANDROIDCOMMONPROJECT).getBuildFile();
//        File commonGradleSrcFile = new File(kugouProject.getRootProject().getProjectDir(), "CommonGradleBuildFile.txt");
//        commonGradleBuildFile.setText("");
//        commonGradleBuildFile.setText(commonGradleSrcFile.getText());
    }

    private void changeGradleFileForKuqun() {
        File fanxingMediaGradleBuildFile = kugouProject.getRootProject().project(ProjectInfo.Kuqun.name).getBuildFile();
        File fanxingMediaGradleSrcFile = new File(kugouProject.getRootProject().getProjectDir(), "KuqunGradleBuildFile.txt");
        fanxingMediaGradleBuildFile.setText("");
        fanxingMediaGradleBuildFile.setText(fanxingMediaGradleSrcFile.getText());
    }

    private void changeGradleFileForDataShow() {
        File lyricmakerGradleBuildFile = kugouProject.getRootProject().project(ProjectInfo.DataShow.name).getBuildFile();
        File lyricmakerGradleSrcFile = new File(kugouProject.getRootProject().getProjectDir(), "DataShowGradleBuildFile.txt");
        lyricmakerGradleBuildFile.setText("");
        lyricmakerGradleBuildFile.setText(lyricmakerGradleSrcFile.getText());
    }

    private void changeGradleFileForLyricMaker() {
        File lyricmakerGradleBuildFile = kugouProject.getRootProject().project(ProjectInfo.LyricMaker.name).getBuildFile();
        File lyricmakerGradleSrcFile = new File(kugouProject.getRootProject().getProjectDir(), "LyricMakerGradleBuildFile.txt");
        lyricmakerGradleBuildFile.setText("");
        lyricmakerGradleBuildFile.setText(lyricmakerGradleSrcFile.getText());
    }

    private void changeGradleFileForZego() {
        File zegoGradleBuildFile = kugouProject.getRootProject().project(ProjectInfo.Zego.name).getBuildFile();
        File zegoGradleSrcFile = new File(kugouProject.getRootProject().getProjectDir(), "ZegoGradleBuildFile.txt");
        zegoGradleBuildFile.setText("");
        zegoGradleBuildFile.setText(zegoGradleSrcFile.getText());
    }

    private void changeGradleFileForModuleH5() {
        File zegoGradleBuildFile = kugouProject.getRootProject().project(ProjectInfo.moduleH5.name).getBuildFile();
        File zegoGradleSrcFile = new File(kugouProject.getRootProject().getProjectDir(), "ModuleH5GradleBuildFile.txt");
        zegoGradleBuildFile.setText("");
        zegoGradleBuildFile.setText(zegoGradleSrcFile.getText());
    }

    private void changeGradleFileForKtvSecond() {
        File ktvSecondGradleBuildFile = kugouProject.getRootProject().project(ProjectInfo.KtvSecond.name).getBuildFile();
        File ktvSecondGradleSrcFile = new File(kugouProject.getRootProject().getProjectDir(), "KtvSecondGradleBuildFile.txt");
        ktvSecondGradleBuildFile.setText("");
        ktvSecondGradleBuildFile.setText(ktvSecondGradleSrcFile.getText());
    }

    private void changeGradleFileForFanxingMedia() {
        File fanxingMediaGradleBuildFile = kugouProject.getRootProject().project(ProjectInfo.FanXingMedia.name).getBuildFile();
        File fanxingMediaGradleSrcFile = new File(kugouProject.getRootProject().getProjectDir(), "FanxingMediaGradleBuildFile.txt");
        fanxingMediaGradleBuildFile.setText("");
        fanxingMediaGradleBuildFile.setText(fanxingMediaGradleSrcFile.getText());
    }


    private void changeGradleFileForFangxing() {
        File fangXingGradleBuildFile = kugouProject.getRootProject().project(ProjectInfo.FanXing.name).getBuildFile();
        File fangXingGradleSrcFile = new File(kugouProject.getRootProject().getProjectDir(), "FanxingGradleBuildFile.txt");
        fangXingGradleBuildFile.setText("");
        fangXingGradleBuildFile.setText(fangXingGradleSrcFile.getText());
    }

    private void changeGradleFileForVoiceHelper() {
        File voiceGradleBuildFile = kugouProject.getRootProject().project(ProjectInfo.moduleVoiceHelper.name).getBuildFile();
        File voiceGradleSrcFile = new File(kugouProject.getRootProject().getProjectDir(), "VoiceHelperGradleBuildFile.txt");
        voiceGradleBuildFile.setText("");
        voiceGradleBuildFile.setText(voiceGradleSrcFile.getText());
    }

    private void changeTinkerIdByVersion() {
        String path = kugouProject.getProjectDir().absolutePath;
        File manifest = new File(path + "/AndroidManifest.xml");
        println("修改路径："+manifest.absolutePath)
        //修改isBuildedByAS为fasle
        ChangeCodeVo changeTinkerId = new ChangeCodeVo();
        changeTinkerId.curCode = "android:name=\"TINKER_ID\" android:value=\"tinker_id_(.*)\""
        changeTinkerId.replaceCode = "android:name=\"TINKER_ID\" android:value=\"tinker_id_${curVersionCode}\""
        changeTinkerId.filePath = manifest.absolutePath;
        changeCode(changeTinkerId)
    }

    private void changeGradleFileForKTV() {
        File KTVGradleBuildFile = kugouProject.getRootProject().project(ProjectInfo.KTV.name).getBuildFile();
        File KTVGradleSrcFile = new File(kugouProject.getRootProject().getProjectDir(), "KTVGradleBuildFile.txt");
        KTVGradleBuildFile.setText("");
        KTVGradleBuildFile.setText(KTVGradleSrcFile.getText());
    }


    private void genIdxAndPublic(Project project) {

        String baseProject = kugouProject.getRootProject().getProjectDir().absolutePath;
        Properties properties = DepenAndVersionUtil.getProperties(kugouProject);
        String path = properties.getProperty("patch_old_apk_path");
        Log.Stute("path:" + path);
        if (path == null || path.length() == 0) {
            Log.Stute("patch_old_apk_path : no value");
            return
        }
        File rFile = new File(kugouProject.getProjectDir().absolutePath + "/R.txt");
        new Download(new Logger(false), "", "").download(new URI(path), rFile);
        if (!oldApkFile.exists() || oldApkFile.length() == 0) {
            Log.Stute("文件下载失败")
        }

        if (rFile == null || !rFile.exists() || rFile.length() == 0) {
            return;
        }

        String resDir = kugouProject.projectDir.absolutePath + "\\res\\values"
        String jarPath = "${project.getRootProject().projectDir.absolutePath}/tinker/hotfixTools/tinker-patch-cli-1.7.8.jar";
        List<String> args = new ArrayList<>();
        args.add("java");
        args.add("-jar");
        args.add(jarPath);
        args.add("-old");
        args.add(patchDir.absolutePath + "/old.apk");
        args.add("-new");
        args.add("${kugouProject.projectDir.absolutePath}/AndResGuard-master-v2/out/kugou-release-no-x86_signed.apk");
        args.add("-config");
        args.add("${project.getRootProject().projectDir.absolutePath}/tinker/hotfixTools/tool_output/tinker_config.xml");
        args.add("-out");
        args.add(patchOutDir.absolutePath);
        args.add("-resdir");
        args.add(resDir);
        args.add("-resmapping");
        args.add(rFile.absolutePath);
        String cmd = "";
        for (String s : args) {
            cmd = cmd + "${s} ";
        }
        AntBaseUtil.commandExec(cmd)

    }

    /**
     * 修改打包前的适配
     */
    private void changeOther() {
        List<ChangeCodeVo> list = new ArrayList<>();

        //修改isBuildedByAS为fasle
        ChangeCodeVo changeIsDebug = new ChangeCodeVo();
        changeIsDebug.curCode = "final boolean isBuildedByAS = (.*);"
        changeIsDebug.replaceCode = "final boolean isBuildedByAS = false;"
        changeIsDebug.filePath = commonProject.getProjectDir().absolutePath + "/src/com/kugou/common/app/KGCommonApplication.java";
        list.add(changeIsDebug);

        //删除KGCommonApplication下有关debugApplication的使用
        changeIsDebug = new ChangeCodeVo();
        changeIsDebug.curCode = "debugApplication = new DebugApplication();"
        changeIsDebug.replaceCode = " "
        changeIsDebug.filePath = commonProject.getProjectDir().absolutePath + "/src/com/kugou/common/app/KGCommonApplication.java";
        list.add(changeIsDebug);

        //删除KGCommonApplication下有关debugApplication的使用
        changeIsDebug = new ChangeCodeVo();
        changeIsDebug.curCode = "debugApplication.attachBaseContext(this);"
        changeIsDebug.replaceCode = " "
        changeIsDebug.filePath = commonProject.getProjectDir().absolutePath + "/src/com/kugou/common/app/KGCommonApplication.java";
        list.add(changeIsDebug);

        //删除KGCommonApplication下有关debugApplication的使用
        changeIsDebug = new ChangeCodeVo();
        changeIsDebug.curCode = "debugApplication.leakWatch(obj);"
        changeIsDebug.replaceCode = " "
        changeIsDebug.filePath = commonProject.getProjectDir().absolutePath + "/src/com/kugou/common/app/KGCommonApplication.java";
        list.add(changeIsDebug);

        //删除kugouProject下moduledebug的关联
        changeIsDebug = new ChangeCodeVo();
        changeIsDebug.curCode = "compile project(':androidkugou:moduledebug')"
        changeIsDebug.replaceCode = " "
        changeIsDebug.filePath = kugouProject.getBuildFile().absolutePath;
        list.add(changeIsDebug);

        //删除moduleGame下moduledebug的关联
//        changeIsDebug = new ChangeCodeVo();
//        changeIsDebug.curCode = "compile project(':androidkugou:moduledebug')"
//        changeIsDebug.replaceCode = " "
//        changeIsDebug.filePath = kugouProject.getRootProject().project("moduleGame").getBuildFile().absolutePath;
//        list.add(changeIsDebug);

        for (ChangeCodeVo changeCodeVo : list) {
            changeCode(changeCodeVo);
        }
    }

    private void fixIp() {
        changeFanxingUrl(IpMode.Online.fxNewurl);
        changeKtvUrl(IpMode.Online.ktvNewurl);
    }

    private void fixNetworkTestCode() {
        ChangeCodeVo changeCodeVo = new ChangeCodeVo();
        changeCodeVo.filePath = kugouProject.getRootProject().project("moduleNetworkTest").getProjectDir().absolutePath + "/AndroidManifest.xml";

        changeCodeVo.curCode = "android:allowBackup=\"true\"";
        changeCodeVo.replaceCode = "";
        changeCode(changeCodeVo);

        changeCodeVo.curCode = "android:icon=\"@drawable/ic_launcher\"";
        changeCodeVo.replaceCode = "";
        changeCode(changeCodeVo);

        changeCodeVo.curCode = "android:label=\"@string/app_name\"";
        changeCodeVo.replaceCode = "";
        changeCode(changeCodeVo);

        changeCodeVo.curCode = "android:theme=\"@style/AppTheme\"";
        changeCodeVo.replaceCode = "";
        changeCode(changeCodeVo);

        changeCodeVo.curCode = "package=\"com.kugou.android\"";
        changeCodeVo.replaceCode = "package=\"com.kugou.networktest\"";
        changeCode(changeCodeVo);

        changeCodeVo.filePath = kugouProject.getRootProject().project("moduleNetworkTest").getProjectDir().absolutePath + "/src/com/kugou/networktest/NetworkTestActivity.java";
        changeCodeVo.curCode = "import com.kugou.android.R;";
        changeCodeVo.replaceCode = "import com.kugou.networktest.R;";
        changeCode(changeCodeVo);


    }

    private void fixIdsAndPublicXmp() {
        ArrayList<String> oldNames = new ArrayList<>();
        File oldidsXml = new File(commonProject.getProjectDir().absolutePath + "/res/values/comm_ids.xml");
        if (oldidsXml.exists() && oldidsXml.length() > 0) {
            oldNames.addAll(findAllOIdslds(oldidsXml));
        }

        File idsXml = new File(kugouProject.projectDir.absolutePath, "ids.xml");
        if (idsXml.exists() && idsXml.length() > 0) {
            mergeAllIds(idsXml, oldNames);
        }

        File inpublic = new File(kugouProject.projectDir.absolutePath, "public.xml");
        File outpublic = new File(kugouProject.projectDir.absolutePath + "/res/values/public.xml");
        if (inpublic.exists() && inpublic.length() > 0) {
            FileUitl.copyFile(inpublic, outpublic, false);
        }

    }

    public void mergeAllIds(File inputFile, List<String> oldNameList) throws Exception {
        InputStream is = new FileInputStream(inputFile);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);
        NodeList nodeList = doc.getElementsByTagName("item");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            String nameInLastBuild = node.getAttributes().getNamedItem("name").getNodeValue();

            for (int j = 0; j < oldNameList.size(); j++) {
                String nameIncommon = oldNameList.get(j);
                if (nameInLastBuild.equals(nameIncommon)) {
                    Node node1 = node.getParentNode().removeChild(node);
                    System.out.println("相同resource:" + nameInLastBuild);
                }
            }
        }

        TransformerFactory transformerFactory = TransformerFactory
                .newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        // 把Document对象又重新写入到一个XML文件中。
        transformer.transform(new DOMSource(doc), new StreamResult(
                kugouProject.projectDir.absolutePath + "/res/values/ids.xml"));
    }

    //
    private List<String> findAllOIdslds(File file) {
        List<String> list = new ArrayList<>();

        try {
            InputStream is = new FileInputStream(file);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            NodeList nodeList = doc.getElementsByTagName("item");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                String name = node.getAttributes().getNamedItem("name").getNodeValue();
                list.add(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }


    private void fixKugouDependCode() {
        ChangeCodeVo changecodeVo = new ChangeCodeVo();
        changecodeVo.filePath = kugouProject.getRootProject().project(HookProjectTask.ANDROIDKUGOU).getBuildFile().absolutePath;
        changecodeVo.curCode = "//    compile project(':androidkugou:moduleFanxing')"
        changecodeVo.replaceCode = "    compile project(':moduleFanxing')"
        changeCode(changecodeVo);

        changecodeVo.curCode = "//    compile project(':androidkugou:modulefm')"
        changecodeVo.replaceCode = "    compile project(':modulefm')"
        changeCode(changecodeVo);

        changecodeVo.curCode = "//    compile project(':androidkugou:moduleNetworkTest')"
        changecodeVo.replaceCode = "    compile project(':moduleNetworkTest')"
        changeCode(changecodeVo);

        changecodeVo.curCode = "//    compile project(':androidkugou:modulemediatransfer')"
        changecodeVo.replaceCode = "    compile project(':modulemediatransfer')"
        changeCode(changecodeVo);

        changecodeVo.curCode = "//    compile project(':androidkugou:moduleDlna')"
        changecodeVo.replaceCode = "    compile project(':moduleDlna')"
        changeCode(changecodeVo);

        changecodeVo.curCode = "//    compile project(':androidkugou:moduleRingtone')"
        changecodeVo.replaceCode = "    compile project(':moduleRingtone')"
        changeCode(changecodeVo);

        changecodeVo.curCode = "//    compile project(':androidkugou:modulePair')"
        changecodeVo.replaceCode = "    compile project(':modulePair')"
        changeCode(changecodeVo);

        changecodeVo.curCode = "//  compile project(':androidkugou:moduleKtv')"
        changecodeVo.replaceCode = "    compile project(':moduleKtv')"
        changeCode(changecodeVo);

        changecodeVo.curCode = "signingConfigs \\{(\\s*)}"
        changecodeVo.replaceCode = "signingConfigs {\n" +
                "        configqq {\n" +
                "            keyAlias 'androidkugou'\n" +
                "            keyPassword '123456789'\n" +
                "            storeFile file('D:/webplatform-ci/bin/cfgs/androidKugou.keystore')\n" +
                "            storePassword '123456789'\n" +
                "        }\n" +
                "    }\n" +
                " buildTypes { release { multiDexEnabled true \n signingConfig signingConfigs.configqq }}"
        changeCode(changecodeVo);

        def prop = new Properties()
        prop.load(kugouProject.file('local.properties').newDataInputStream())
        String versionName = prop.getProperty('app.version.name')
        Log.Stute("versionName :" + versionName);
        String versionCode = prop.getProperty('app.version.code')
        curVersionCode=versionCode;
        Log.Stute("versionCode :" + versionCode);
        String gitVersion = prop.getProperty('app.version.git')
        Log.Stute("gitVersion :" + gitVersion);
        changecodeVo.curCode = "versionCode (.*)"
        changecodeVo.replaceCode = "versionCode ${versionCode}"
        changeCode(changecodeVo);
        changecodeVo.curCode = "versionName (.*)"
        changecodeVo.replaceCode = "versionName \"${versionName}\""
        changeCode(changecodeVo);
        changecodeVo.curCode = "buildConfigField\\s*\"int\"\\s*,\\s*\"GIT_VERSION\".*"
        changecodeVo.replaceCode = "buildConfigField \"int\", \"GIT_VERSION\", \"0x${gitVersion}\""
        changeCode(changecodeVo);
    }

//    public String changePlugInVer(Project project) {
//        for (ProjectInfo pluginProjectInfo:ProjectInfo.pluginProjectInfolists){
//            ChangeCodeVo changeCodeVo = new ChangeCodeVo();
//            changeCodeVo.setCurCode("versionCode (.*)");
//            changeCodeVo.setReplaceCode("versionCode " + DepenAndVersionUtil.getPlugInVer(kugouProject.getRootProject().project(pluginProjectInfo.name)))
//            changeCodeVo.setFilePath(project.getRootProject().project(pluginProjectInfo.name).getProject().getProjectDir().absolutePath + "/build.gradle");
//            changeCode(changeCodeVo);
//        }
//    }


    public  String changePatchCode(Project project) {
        def prop = new Properties()
        prop.load(project.getRootProject().project(HookProjectTask.ANDROIDKUGOU).file('local.properties').newDataInputStream())
        String patchCode = prop.getProperty('patchCode')
        ChangeCodeVo changeCodeVo = new ChangeCodeVo();

        int patchCodeValue = 0;
        try {
            patchCodeValue = Integer.parseInt(patchCode);
        } catch (Exception e) {
            e.printStackTrace()
        }
        changeCodeVo.setCurCode("static public int versionCode = (.*)");
        changeCodeVo.setReplaceCode("static public int versionCode = ${patchCodeValue};")
        changeCodeVo.setFilePath(project.getRootProject().project(HookProjectTask.ANDROIDCOMMONPROJECT).getProjectDir().getAbsolutePath() + "/src/com/kugou/android/support/multidex/PatchUseVersion.java");
        changeCode(changeCodeVo);
    }
//
//    public static String getGameVer(Project project) {
//        def prop = new Properties()
//        prop.load(project.getRootProject().project(HookProjectTask.ANDROIDKUGOU).file('local.properties').newDataInputStream())
//        String gameVer = prop.getProperty('gameVer')
//        return gameVer
//    }

    public static String getFormal(Project project) {
        def prop = new Properties()
        prop.load(project.getRootProject().project(TestPlugin.ANDROIDKUGOU).file('local.properties').newDataInputStream())
        String formal = prop.getProperty('formal')
        return formal;
    }

    /**
     * 是否编译渠道包
     * @return
     */
    public static boolean isBuildChannel(Project project) {
        String formal = getFormal(project);
        return (formal != null && formal.length() > 0)
    }

    public void deleteFile(Project project) {
        File publicFile = new File(project.rootProject.project("lib.style").getProjectDir(), "public.txt");
        Log.Stute("delete publicFile " + publicFile.exists())
        if (publicFile.exists()) {
            publicFile.delete();
        }

        File buildSmall = new File(project.getRootProject().getProjectDir(), "build-small");
        Log.Stute("delete buildSmall " + buildSmall.exists())
        if (buildSmall.exists()) {
            buildSmall.deleteDir();
            buildSmall.mkdirs();
        }

    }

    private void fixLog() {
        boolean log = kugouProject.properties["log"];
        File file = new File(commonProject.getProjectDir().absolutePath + "/src/com/kugou/common/utils/KGLog.java");
        String regexp = "private static boolean isDebug = (.*);";
        String substitution = "private static boolean isDebug = false;";
        ChangeCodeVo changeCodeVo = new ChangeCodeVo(file.absolutePath, regexp, substitution);
        changeCode(changeCodeVo);
        Log.Stute("修改log：old：${regexp} new ：${substitution}")
    }

    private void changeFanxingUrl(String newUrl) {
        File file = new File(commonProject.getProjectDir().absolutePath + "/src/com/kugou/fanxing/pro/base/ProConstant.java");
        String regexp = "public static String BASE_URL = (.*);";
        String substitution = "public static String BASE_URL = ${newUrl};";
        ChangeCodeVo changeCodeVo = new ChangeCodeVo(file.absolutePath, regexp, substitution);
        changeCode(changeCodeVo);
        Log.Stute("修改changeFanxingUrl：old：${regexp} new ：${substitution}")
    }


    private void changeKtvUrl(String newUrl) {
        File file = new File(commonProject.getProjectDir().absolutePath + "/src/com/kugou/ktv/android/common/constant/KtvConstant.java");
        String regexp = "public static int ENV_TAG = (.*);";
        String substitution = "public static int ENV_TAG = ${newUrl};";
        ChangeCodeVo changeCodeVo = new ChangeCodeVo(file.absolutePath, regexp, substitution);
        changeCode(changeCodeVo);
        //Log.Stute("修改changeKtvUrl：old：${regexp} new ：${substitution}")
    }

    private void changeCode(ChangeCodeVo changeCodeVo) {
        File file = new File(changeCodeVo.getFilePath());
        String content = file.getText("utf-8");
        String newContent = content.replaceAll(changeCodeVo.getCurCode(), changeCodeVo.getReplaceCode());
        if (newContent.equals(content)) {
            newContent = content.replace(changeCodeVo.getCurCode(), changeCodeVo.getReplaceCode());
        }
        if (newContent.equals(content)) {
            Log.Stute("change fail：" + changeCodeVo.getCurCode() + "   " + changeCodeVo.getReplaceCode())
        } else {
            Log.Stute("change success：" + changeCodeVo.getCurCode() + "   " + changeCodeVo.getReplaceCode())
        }
        file.setText(newContent, "utf-8")
    }


    public static class ChangeCodeVo {
        String filePath;
        String curCode;
        String replaceCode;
        ArrayList<String> curCodeList;

        ChangeCodeVo() {}

        public void setCurCodeList(ArrayList<String> curCodeList) {
            this.curCodeList = curCodeList
        }

        ArrayList<String> getCurCodeList() {
            return curCodeList
        }

        ChangeCodeVo(String filePath, String curCode, String replaceCode) {
            this.filePath = filePath
            this.curCode = curCode
            this.replaceCode = replaceCode
        }

    }

    public enum IpMode {
        DevIp("set-dev-ip", "DEV_BASE_URL", "ENV_OUTER_TEST"),
        TestIp("set-test-ip", "ONLINE_BASE_URL", "ENV_OUTER_TEST"),
        OnlineTestIp("set-online-test-ip", "ONLINE_BASE_URL", "ENV_OUTER_TEST"),
        Online("set-online-ip", "ONLINE_BASE_URL", "ENV_OUTER")
        public String setDevIp;
        public String fxNewurl;
        public String ktvNewurl;

        IpMode(String setDevIp, String fxNewurl, String ktvNewurl) {
            this.setDevIp = setDevIp
            this.fxNewurl = fxNewurl
            this.ktvNewurl = ktvNewurl
        }
        static IpMode[] lists = [DevIp, TestIp, Online, OnlineTestIp];
    }

}

package com.qiqi.my

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Project

/**
 * Created by tong on 17/3/14.
 */
class BuildUtils {
    public static String getBuildPath(Project project) {
        String buildPath = project.getBuildDir().absolutePath
        return buildPath;
    }

    public static String getBuildMyPath(Project project) {
        String buildPath = project.getBuildDir().absolutePath
        return buildPath + "\\my";
    }


    public static String getDebugPath(Project project) {
        String buildPath = project.getBuildDir().absolutePath
        String dir = buildPath + "\\" + "intermediates\\classes\\debug"
        return dir;
    }

    public static String getReleasePath(Project project) {
        String buildPath = project.getBuildDir().absolutePath
        String dir = buildPath + "\\" + "intermediates\\classes\\release"
        return dir;
    }

    public static String getClassPath(Project project) {
        String buildPath = project.getBuildDir().absolutePath
        String dir = buildPath + "\\" + "intermediates\\classes"
        return dir;
    }

    public static String getBuildMyClassPath(Project project) {
        String buildPath = project.getBuildDir().absolutePath
        return buildPath + "\\my\\classes"
    }

    public static String getPath(Project project) {
        String buildPath = project.getProjectDir().absolutePath
        return buildPath
    }
    /**
     * 获取sdk路径
     * @param project
     * @return
     */
    static final String getSdkDirectory(Project project) {
        String sdkDirectory = project.android.getSdkDirectory()
        if (sdkDirectory.contains("\\")) {
            sdkDirectory = sdkDirectory.replace("\\", "/")
        }
        return sdkDirectory
    }

    /**
     * 获取dx命令路径
     * @param project
     * @return
     */
    static final String getDxCmdPath(Project project) {
        File dx = new File(BuildUtils.getSdkDirectory(project), "build-tools${File.separator}${project.android.buildToolsVersion.toString()}${File.separator}dx")
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return "${dx.absolutePath}.bat"
        }
        return dx.getAbsolutePath()
    }

    /**
     * 获取aapt命令路径
     * @param project
     * @return
     */
    static final String getAaptCmdPath(Project project) {
        File aapt = new File(BuildUtils.getSdkDirectory(project), "build-tools${File.separator}${project.android.buildToolsVersion.toString()}${File.separator}aapt")
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return "${aapt.absolutePath}.exe"
        }
        return aapt.getAbsolutePath()
    }

    /**
     * 获取adb命令路径
     * @param project
     * @return
     */
    static final String getAdbCmdPath(Project project) {
        File adb = new File(BuildUtils.getSdkDirectory(project), "platform-tools${File.separator}adb")
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return "${adb.absolutePath}.exe"
        }
        return adb.getAbsolutePath()
    }

    /**
     * 获取当前jdk路径
     * @return
     */
    static final String getCurrentJdk() {
        String javaHomeProp = System.properties.'java.home'
        if (javaHomeProp) {
            int jreIndex = javaHomeProp.lastIndexOf("${File.separator}jre")
            if (jreIndex != -1) {
                return javaHomeProp.substring(0, jreIndex)
            } else {
                return javaHomeProp
            }
        } else {
            return System.getenv("JAVA_HOME")
        }
    }

    /**
     * 获取java命令路径
     * @return
     */
    static final String getJavaCmdPath() {
        StringBuilder cmd = new StringBuilder(getCurrentJdk())
        if (!cmd.toString().endsWith(File.separator)) {
            cmd.append(File.separator)
        }
        cmd.append("bin${File.separator}java")
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            cmd.append(".exe")
        }
        return new File(cmd.toString()).absolutePath
    }

    /**
     * 获取javac命令路径
     * @return
     */
    static final String getJavacCmdPath() {
        StringBuilder cmd = new StringBuilder(getCurrentJdk())
        if (!cmd.toString().endsWith(File.separator)) {
            cmd.append(File.separator)
        }
        cmd.append("bin${File.separator}javac")
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            cmd.append(".exe")
        }
        return new File(cmd.toString()).absolutePath
    }

//    /**
//     * 是否存在dex缓存
//     * @param project
//     * @param variantName
//     * @return
//     */
//    static boolean hasDexCache(Project project, String variantName) {
//        File cacheDexDir = getDexCacheDir(project, variantName)
//        if (!FileUtils.dirExists(cacheDexDir.absolutePath)) {
//            return false
//        }
//
//        FindDexFileVisitor visitor = new FindDexFileVisitor()
//        Files.walkFileTree(cacheDexDir.toPath(), visitor)
//        return visitor.hasDex
//    }
//
//    /**
//     * 获取fastdex的build目录
//     * @param project
//     * @return
//     */
//    static final File getBuildDir(Project project) {
//        File file = new File(project.getBuildDir(), Constants.BUILD_DIR)
//        return file
//    }

    /**
     * 获取fastdex指定variantName的build目录
     * @param project
     * @return
     */
    static final File getBuildDir(Project project, String variantName) {
        File file = new File(getBuildDir(project), variantName)
        return file
    }

    /**
     * 获取fastdex指定variantName的work目录
     * @param project
     * @return
     */
    static final File getWorkDir(Project project, String variantName) {
        File file = new File(getBuildDir(project, variantName), "work")
        return file
    }

    /**
     * 获取dex目录
     * @param project
     * @param variantName
     * @return
     */
    static getDexDir(Project project, String variantName) {
        File file = new File(getBuildDir(project, variantName), "dex")
        return file
    }

    /**
     * 获取指定variantName的dex缓存目录
     * @param project
     * @return
     */
    static final File getDexCacheDir(Project project, String variantName) {
        File file = new File(getDexDir(project, variantName), "cache")
        return file
    }

    /**
     * 获取指定variantName的已合并的补丁dex目录
     * @param project
     * @return
     */
    static final File getMergedPatchDexDir(Project project, String variantName) {
        File file = new File(getDexDir(project, variantName), "merged-patch")
        return file
    }

//    /**
//     * 获取指定variantName的已合并的补丁dex
//     * @param project
//     * @return
//     */
//    static final File getMergedPatchDexFile(Project project, String variantName) {
//        File file = new File(getMergedPatchDexDir(project, variantName), Constants.CLASSES_DEX)
//        return file
//    }
//
//    /**
//     * 获取指定variantName的补丁dex目录
//     * @param project
//     * @return
//     */
//    static final File getPatchDexDir(Project project, String variantName) {
//        File file = new File(getDexDir(project, variantName), "patch")
//        return file
//    }
//
//    /**
//     * 获取指定variantName的补丁dex文件
//     * @param project
//     * @return
//     */
//    static final File getPatchDexFile(Project project, String variantName) {
//        File file = new File(getPatchDexDir(project, variantName), Constants.CLASSES_DEX)
//        return file
//    }
//
//    /**
//     * 获取指定variantName的补丁merged-dex文件
//     * @param project
//     * @param variantName
//     * @return
//     */
//    static final File getMergedPatchDex(Project project, String variantName) {
//        File file = new File(getMergedPatchDexDir(project, variantName), Constants.CLASSES_DEX)
//        return file
//    }
//
//    /**
//     * 获取指定variantName的源码目录快照
//     * @param project
//     * @return
//     */
//    static final File getSourceSetSnapshootFile(Project project, String variantName) {
//        File file = new File(getBuildDir(project, variantName), Constants.SOURCESET_SNAPSHOOT_FILENAME)
//        return file
//    }
//
//    /**
//     * 清空所有缓存
//     * @param project
//     * @param variantName
//     * @return
//     */
//    static boolean cleanCache(Project project, String variantName) {
//        File dir = getBuildDir(project, variantName)
//        project.logger.error("==fastdex clean dir: ${dir}")
//        return FileUtils.deleteDir(dir)
//    }
//
//    /**
//     * 清空指定variantName缓存
//     * @param project
//     * @param variantName
//     * @return
//     */
//    static boolean cleanAllCache(Project project) {
//        File dir = getBuildDir(project)
//        project.logger.error("==fastdex clean dir: ${dir}")
//        return FileUtils.deleteDir(dir)
//    }

//    /**
//     * 获取资源映射文件
//     * @param project
//     * @param variantName
//     * @return
//     */
//    static File getResourceMappingFile(Project project, String variantName) {
//        File resourceMappingFile = new File(getBuildDir(project, variantName), "symbols/" + Constants.R_TXT)
//        return resourceMappingFile
//    }

    static File getResourceDir(Project project, String variantName) {
        File resDir = new File(getBuildDir(project, variantName), "res")
        return resDir
    }

//    static File getResourcesApk(Project project, String variantName) {
//        File resourcesApk = new File(getResourceDir(project, variantName), ShareConstants.RESOURCE_APK_FILE_NAME)
//        return resourcesApk
//    }
//
//    /**
//     * 获取缓存的idx.xml文件
//     * @param project
//     * @param variantName
//     * @return
//     */
//    static File getIdxXmlFile(Project project, String variantName) {
//        File idxXmlFile = new File(getResourceKeepDir(project, variantName), Constants.RESOURCE_IDX_XML)
//        return idxXmlFile
//    }
//
//    /**
//     * 获取缓存的public.xml文件
//     * @param project
//     * @param variantName
//     * @return
//     */
//    static File getPublicXmlFile(Project project, String variantName) {
//        File publicXmlFile = new File(getResourceKeepDir(project, variantName), Constants.RESOURCE_PUBLIC_XML)
//        return publicXmlFile
//    }

    static File getResourceKeepDir(Project project, String variantName) {
        return new File(getBuildDir(project, variantName), "res-keep")
    }

//    /**
//     * 获取全量打包时的依赖列表
//     * @param project
//     * @param variantName
//     * @return
//     */
//    static File getCachedDependListFile(Project project, String variantName) {
//        File cachedDependListFile = new File(getBuildDir(project, variantName), Constants.DEPENDENCIES_FILENAME)
//        return cachedDependListFile
//    }
//
//    static File getAndroidManifestStatFile(Project project, String variantName) {
//        File file = new File(getBuildDir(project, variantName), Constants.ANDROID_MANIFEST_FILENAME)
//        return file
//    }
//
//    static File getMetaInfoFile(Project project, String variantName) {
//        File cachedDependListFile = new File(getBuildDir(project, variantName), Constants.META_INFO_FILENAME)
//        return cachedDependListFile
//    }
//
//    /**
//     * 获取缓存的java文件对比结果文件
//     * @param project
//     * @param variantName
//     * @return
//     */
//    static File getDiffResultSetFile(Project project, String variantName) {
//        File diffResultFile = new File(getBuildDir(project, variantName), Constants.LAST_DIFF_RESULT_SET_FILENAME)
//        return diffResultFile
//    }

    /**
     * 获取源码目录集合
     * @param project
     * @param sourceSetKey
     * @return
     */
    static LinkedHashSet<File> getSrcDirs(Project project, String sourceSetKey) {
        def srcDirs = new LinkedHashSet()
        def sourceSetsValue = project.android.sourceSets.findByName(sourceSetKey)
        if (sourceSetsValue) {
            srcDirs.addAll(sourceSetsValue.java.srcDirs.asList())
        }
        return srcDirs
    }

    static File getManifestFile(Project project, String sourceSetKey) {
        def sourceSetsValue = project.android.sourceSets.findByName(sourceSetKey)
        if (sourceSetsValue) {
            return sourceSetsValue.manifest.srcFile
        }
        return null
    }

    static boolean isDataBindingEnabled(Project project) {
        return project.android.dataBinding && project.android.dataBinding.enabled
    }

//    /**
//     * 递增指定目录中的dex
//     *
//     * classes.dex   => classes2.dex
//     * classes2.dex  => classes3.dex
//     * classesN.dex  => classes(N + 1).dex
//     *
//     * @param dexDir
//     */
//    static void incrementDexDir(File dexDir, int dsize) {
//        if (dsize <= 0) {
//            throw new RuntimeException("dsize must be greater than 0!")
//        }
//        //classes.dex  => classes2.dex.tmp
//        //classes2.dex => classes3.dex.tmp
//        //classesN.dex => classes(N + 1).dex.tmp
//
//        String tmpSuffix = ".tmp"
//        File classesDex = new File(dexDir, Constants.CLASSES_DEX)
//        if (FileUtils.isLegalFile(classesDex)) {
//            classesDex.renameTo(new File(dexDir, "classes${dsize + 1}.dex${tmpSuffix}"))
//        }
//        int point = 2
//        File dexFile = new File(dexDir, "${Constants.CLASSES}${point}${Constants.DEX_SUFFIX}")
//        while (FileUtils.isLegalFile(dexFile)) {
//            new File(dexDir, "classes${point}.dex").renameTo(new File(dexDir, "classes${point + dsize}.dex${tmpSuffix}"))
//            point++
//            dexFile = new File(dexDir, "classes${point}.dex")
//        }
//
//        //classes2.dex.tmp => classes2.dex
//        //classes3.dex.tmp => classes3.dex
//        //classesN.dex.tmp => classesN.dex
//        point = dsize + 1
//        dexFile = new File(dexDir, "classes${point}.dex${tmpSuffix}")
//        while (FileUtils.isLegalFile(dexFile)) {
//            dexFile.renameTo(new File(dexDir, "classes${point}.dex"))
//            point++
//            dexFile = new File(dexDir, "classes${point}.dex${tmpSuffix}")
//        }
//    }
//
//    /**
//     * 使用buildCache全量打包时hook dex输出目录
//     * @param dexOutputDir
//     */
//    static File mergeDexOutputDir(File dexOutputDir, int dsize) {
//        final HashSet<File> dexDirSet = new HashSet<>()
//        Files.walkFileTree(dexOutputDir.toPath(), new SimpleFileVisitor<Path>() {
//            @Override
//            FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                if (file.toFile().getName().endsWith(Constants.DEX_SUFFIX)) {
//                    dexDirSet.add(file.getParent().toFile())
//                }
//                return FileVisitResult.CONTINUE
//            }
//        })
//
//        File result = null
//        int maxClassesDexIndex = 0
//        for (File dir : dexDirSet) {
//            if (result == null) {
//                result = dir
//                incrementDexDir(dir, dsize)
//                maxClassesDexIndex = BuildUtils.getMaxClassesDexIndex(dir)
//            } else {
//                incrementDexDir(dir, maxClassesDexIndex)
//                maxClassesDexIndex += FileUtils.moveDir(dir, result, ShareConstants.DEX_SUFFIX)
//
//            }
//        }
//        return result
//    }

    /**
     * 使用buildCache全量打包时hook dex输出目录
     * @param dexOutputDir
     */
    static File mergeDexOutputDir(File dexOutputDir) {
        mergeDexOutputDir(dexOutputDir, 1)
    }

    /**
     * 获取目录中所有classesN.dex中N的最大值
     * @param dexDir
     * @return
     */
    static int getMaxClassesDexIndex(File dexDir) {
        return getClassesDexBoundary(dexDir)[1]
    }

//    /**
//     * 获取目录中所有classesN.dex中N的范围
//     * @param dexDir
//     * @return
//     */
//    static int[] getClassesDexBoundary(File dexDir) {
//        int[] boundary = new int[2]
//
//        if (dexDir.listFiles() == null) {
//            return boundary
//        }
//        def prefix = ShareConstants.CLASSES
//        def suffix = ShareConstants.DEX_SUFFIX
//
//        dexDir.listFiles().each {
//            def filename = it.name
//            int index = 0
//
//            if (filename == ShareConstants.CLASSES_DEX) {
//                index = 1
//            } else if (filename.startsWith(prefix) && filename.endsWith(suffix)) {
//                filename = filename.substring(prefix.length())
//                //println "filename: ${filename}"
//                filename = filename.substring(0, filename.length() - suffix.length())
//
//                //println "filename: ${filename}"
//                try {
//                    index = Integer.parseInt(filename)
//                } catch (Throwable e) {
//
//                }
//            }
//            if (index > boundary[1]) {
//                boundary[1] = index
//            }
//
//            if (boundary[0] == 0 || index < boundary[0]) {
//                boundary[0] = index
//            }
//        }
//        return boundary
//    }

    /**
     * 清理dex输出目录，除了classes*.dex其它的文件全删掉，防止package任务把别的文件当成dex打进aak里面
     * @param dexOutputDir
     */
    static void clearDexOutputDir(File dexOutputDir) {
        if (dexOutputDir == null) {
            return
        }
        dexOutputDir.listFiles().each {
//            if (it.isFile() && !(it.name.startsWith(ShareConstants.CLASSES) && it.name.endsWith(ShareConstants.DEX_SUFFIX))) {
//                it.delete()
//            }

            //避免删除3.0.0以后的__content__.json
            if (it.isFile() && it.isHidden()) {
                it.delete()
            }
        }
    }

    /**
     * 执行命令
     * @param cmdArgs
     */
    static void runCommand(Project project, List<String> cmdArgs) {
        runCommand(project, cmdArgs, false)
    }

    /**
     * 执行命令
     * @param cmdArgs
     */
    static void runCommand(Project project, List<String> cmdArgs, boolean background) {
        runCommand(project, cmdArgs, null, background)
    }

    /**
     * 执行命令
     * @param cmdArgs
     */
    static void runCommand(Project project, List<String> cmdArgs, File directory, boolean background) {
        if (!background) {
            StringBuilder cmd = new StringBuilder()
            for (int i = 0; i < cmdArgs.size(); i++) {
                if (i != 0) {
                    cmd.append(" ")
                }
                cmd.append(cmdArgs.get(i))
            }
            project.logger.error("\n${cmd}")
        }

        int status = -1
        try {
            ProcessBuilder processBuilder = new ProcessBuilder((String[]) cmdArgs.toArray())
            if (directory != null) {
                processBuilder.directory(directory)
            }
            def process = processBuilder.start()
            InputStream is = process.getInputStream()
            BufferedReader reader = new BufferedReader(new InputStreamReader(is))
            String line = null
            while ((line = reader.readLine()) != null) {
                println(line)
            }
            reader.close()
            status = process.waitFor()
            reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))
            while ((line = reader.readLine()) != null) {
                System.out.println(line)
            }
            reader.close()
            process.destroy()
        } catch (Throwable e) {

        }
        if (status != 0 && !background) {
            throw new Exception("Command exec fail....")
        }
    }
}
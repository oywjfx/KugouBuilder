package com.qiqi.util

import java.nio.channels.FileChannel
/**
 * Created by siganid on 2016/5/16.
 */
public class FileUitl {

    /**
     * The number of bytes in a kilobyte.
     */
    public static final long ONE_KB = 1024;

    /**
     * The number of bytes in a megabyte.
     */
    public static final long ONE_MB = ONE_KB * ONE_KB;

    /**
     * The file copy buffer size (10 MB) （原为30MB，为更适合在手机上使用，将其改为10MB，by
     * Geek_Soledad)
     */
    private static final long FILE_COPY_BUFFER_SIZE = ONE_MB * 10;

    /**
     * <p>
     * 将目录及其以下子目录拷贝到一个新的位置，并且保留文件日期。
     * <p>
     * 如果目标目录不存在，则被创建。 如果目标目录已经存在，则将会合并两个文件夹的内容，若有冲突则替换掉目标目录中的文件。
     * <p>
     *
     * @param srcDir 一个存在的源目录，不能为null。
     * @param destDir 新的目录，不能为null。
     * @throws NullPointerException 如果源目录或目标目录为null。
     * @throws IOException 如果源目录或目标目录无效。
     * @throws IOException 如果拷贝中出现IO错误。
     */
    public static void copyDirectory(File srcDir, File destDir) throws IOException {
        copyDirectory(srcDir, destDir, true);
    }

    /**
     * 拷贝目录到一个新的位置。
     * <p>
     * 该方法将拷贝指定的源目录的所有内容到一个新的目录中。
     * </p>
     * <p>
     * 如果目标目录不存在，则被创建。 如果目标目录已经存在，则将会合并两个文件夹的内容，若有冲突则替换掉目标目录中的文件。
     * </p>
     *
     * @param srcDir 一个存在的源目录，不能为null。
     * @param destDir 新的目录，不能为null。
     * @throws NullPointerException 如果源目录或目标目录为null。
     * @throws IOException 如果源目录或目标目录无效。
     * @throws IOException 如果拷贝中出现IO错误。
     */
    public static void deleteDir(String path){
        File file = new File(path);
        file.deleteDir();
       // AntBaseUtil.commandExec("cmd /c rd /s /q ${path}");
    }


    public static void copyDirectory(File srcDir, File destDir, boolean preserveFileDate)
            throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (srcDir.exists() && srcDir.isDirectory() == false) {
            throw new IllegalArgumentException("Source '" + destDir + "' is not a directory");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (destDir.exists() && destDir.isDirectory() == false) {
            throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
        }
        if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
            throw new IOException("Source '" + srcDir + "' and destination '" + destDir
                    + "' are the same");
        }

        // 为满足当目标目录在源目录里面的情况。
        List<String> exclusionList = null;
        if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
            File[] srcFiles = srcDir.listFiles();
            if (srcFiles != null && srcFiles.length > 0) {
                exclusionList = new ArrayList<String>(srcFiles.length);
                for (File srcFile : srcFiles) {
                    File copiedFile = new File(destDir, srcFile.getName());
                    exclusionList.add(copiedFile.getCanonicalPath());
                }
            }
        }

        doCopyDirectory(srcDir, destDir, preserveFileDate, exclusionList);
    }

    /**
     * <p>
     * 将一个目录下的文件全部拷贝到另一个目录里面，并且保留文件日期。
     * </p>
     * <p>
     * 如果目标目录不存在，则被创建。 如果目标目录已经存在，则将会合并两个文件夹的内容，若有冲突则替换掉目标目录中的文件。
     * </p>
     *
     * @param srcDir 源目录，不能为null且必须存在。
     * @param destDir 目标目录，不能为null。
     * @throws NullPointerException 如果源目录或目标目录为null。
     * @throws IOException 如果源目录或目标目录无效。
     * @throws IOException 如果拷贝中出现IO错误。
     */
    public static void copyDirectoryToDirectory(File srcDir, File destDir) throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (srcDir.exists() && srcDir.isDirectory() == false) {
            throw new IllegalArgumentException("Source '" + destDir + "' is not a directory");
        }
        if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (destDir.exists() && destDir.isDirectory() == false) {
            throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
        }
        copyDirectory(srcDir, new File(destDir, srcDir.getName()), true);
    }

    /**
     * Internal copy directory method.
     *
     * @param srcDir the validated source directory, must not be
     *            <code>null</code>
     * @param destDir the validated destination directory, must not be
     *            <code>null</code>
     * @param filter the filter to apply, null means copy all directories and
     *            files
     * @param preserveFileDate whether to preserve the file date
     * @param exclusionList List of files and directories to exclude from the
     *            copy, may be null
     * @throws IOException if an error occurs
     * @since Commons IO 1.1
     */
    private static void doCopyDirectory(File srcDir, File destDir, boolean preserveFileDate,
                                        List<String> exclusionList) throws IOException {
        // recurse
        File[] srcFiles = srcDir.listFiles();
        if (srcFiles == null) { // null if abstract pathname does not denote a
            // directory, or if an I/O error occurs
            throw new IOException("Failed to list contents of " + srcDir);
        }
        if (destDir.exists()) {
            if (destDir.isDirectory() == false) {
                throw new IOException("Destination '" + destDir + "' exists but is not a directory");
            }
        } else {
            if (!destDir.mkdirs() && !destDir.isDirectory()) {
                throw new IOException("Destination '" + destDir + "' directory cannot be created");
            }
        }
        if (destDir.canWrite() == false) {
            throw new IOException("Destination '" + destDir + "' cannot be written to");
        }
        for (File srcFile : srcFiles) {
            File dstFile = new File(destDir, srcFile.getName());
            if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
                if (srcFile.isDirectory()) {
                    doCopyDirectory(srcFile, dstFile, preserveFileDate, exclusionList);
                } else {
                    doCopyFile(srcFile, dstFile, preserveFileDate);
                }
            }
        }

        // Do this last, as the above has probably affected directory metadata
        if (preserveFileDate) {
            destDir.setLastModified(srcDir.lastModified());
        }
    }
    public static void copyFile(File srcFile, File destFile, boolean preserveFileDate){
        doCopyFile(srcFile,destFile,preserveFileDate);
    }

    /**
     * Internal copy file method.
     *
     * @param srcFile the validated source file, must not be <code>null</code>
     * @param destFile the validated destination file, must not be
     *            <code>null</code>
     * @param preserveFileDate whether to preserve the file date
     * @throws IOException if an error occurs
     */
    private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate)
            throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel input = null;
        FileChannel output = null;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            input = fis.getChannel();
            output = fos.getChannel();
            long size = input.size();
            long pos = 0;
            long count = 0;
            while (pos < size) {
                count = (size - pos) > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : (size - pos);
                pos += output.transferFrom(input, pos, count);
            }
        } finally {
            fis.close();
            output.close();
            fos.close();
            fis.close();
        }

        if (srcFile.length() != destFile.length()) {
            throw new IOException("Failed to copy full contents from '" + srcFile + "' to '"
                    + destFile + "'");
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }

    /**
     * Opens a {@link FileInputStream} for the specified file, providing better
     * error messages than simply calling <code>new FileInputStream(file)</code>
     * .
     * <p>
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     * <p>
     * An exception is thrown if the file does not exist. An exception is thrown
     * if the file object exists but is a directory. An exception is thrown if
     * the file exists but cannot be read.
     *
     * @param file the file to open for input, must not be <code>null</code>
     * @return a new {@link FileInputStream} for the specified file
     * @throws FileNotFoundException if the file does not exist
     * @throws IOException if the file object is a directory
     * @throws IOException if the file cannot be read
     * @since Commons IO 1.3
     */
    public static FileInputStream openInputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canRead() == false) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileInputStream(file);
    }



}

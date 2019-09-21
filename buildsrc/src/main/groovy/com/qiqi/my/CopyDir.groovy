package com.qiqi.my; /**
 * 文件夹拷贝(文件内含有文件和文件夹)
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CopyDir {
    //主入口
    public static void main(String[] args) throws Exception {
        test();

    }

    public static void test() throws Exception {
        //复制方法
        copy("D:\\develop\\project\\kugou\\KugouBuilder\\app\\build\\classes",
                "D:\\develop\\project\\kugou\\KugouBuilder\\app\\build\\intermediates\\classes");
        //打印完成
        System.out.println("文件拷贝完成!");
    }

    //复制方法
    public static void copy(String src, String des) throws Exception {
        //初始化文件复制
        File file1 = new File(src);
        //把文件里面内容放进数组
        File[] fs = file1.listFiles();
        //初始化文件粘贴
        File file2 = new File(des);
        //判断是否有这个文件有不管没有创建
        if (!file2.exists()) {
            file2.mkdirs();
        }
        //遍历文件及文件夹
        for (File f : fs) {
            if (f.isFile()) {
                //文件
                fileCopy(f.getPath(), des + "\\" + f.getName()); //调用文件拷贝的方法
            } else if (f.isDirectory()) {
                //文件夹
                copy(f.getPath(), des + "\\" + f.getName());//继续调用复制方法      递归的地方,自己调用自己的方法,就可以复制文件夹的文件夹了
            }
        }

    }

    /**
     * 文件复制的具体方法
     */
    public static void fileCopy(String src, String des) throws Exception {
        File file = new File(des);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs()
        }

        //io流固定格式
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des));
        int i = -1;//记录获取长度
        byte[] bt = new byte[2014];//缓冲区
        while ((i = bis.read(bt)) != -1) {
            bos.write(bt, 0, i);
        }
        bis.close();
        bos.close();
        //关闭流
    }

}
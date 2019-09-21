package com.qiqi.my;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileScanHelper {


    public static void main(String[] args) {
        add();
    }

    private static void add() {
        long start = System.currentTimeMillis();

        File directory = new File("");//设定为当前文件夹
        String outPath = directory.getAbsolutePath() + "\\File\\temp.txt";

        Map<String, FileInfo> firthList = readFile(outPath);

        FileScanHelper helper = new FileScanHelper();
        helper.scan(new File("D:\\develop\\project\\kugou\\kugou_fifth\\androidkugou\\androidcommon\\src"));
        helper.scan(new File("D:\\develop\\project\\kugou\\kugou_fifth\\androidkugou\\src"));

        for (FileInfo info : helper.pathList) {
            FileInfo search = firthList.get(info.path);
            if (search == null) {
                System.out.println("增加 " + info.path);
            } else if (!search.eq(info)) {
                System.out.println("修改 " + info.path);
            }
        }

        System.out.println("时间：" + (System.currentTimeMillis() - start));
    }

    private static void first() {
        File directory = new File("");//设定为当前文件夹
        String outPath = directory.getAbsolutePath() + "\\File\\temp.txt";

        FileScanHelper helper = new FileScanHelper();
        long start = System.currentTimeMillis();
        helper.scan(new File("D:\\develop\\project\\kugou\\kugou_fifth\\androidkugou\\androidcommon\\src"));
        helper.scan(new File("D:\\develop\\project\\kugou\\kugou_fifth\\androidkugou\\src"));
        writeFile(helper.pathList, outPath);

        System.out.println("时间：" + (System.currentTimeMillis() - start));
        System.out.println("数量：" + helper.pathList.size());
    }


    public List<FileInfo> pathList = new ArrayList<>();

    public List<String> pathStringList = new ArrayList<>();

    public void scan(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    scan(f);
                }
            } else {
                pathStringList.add(file.getAbsolutePath());
                pathList.add(new FileInfo(file.getAbsolutePath(), file.lastModified(), file.length()));
            }
        }
    }




    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    public static Map<String, FileInfo> readFile(String dataPath) {
        InputStream is;
        BufferedReader reader;
        Map<String, FileInfo> map = new HashMap<>();
        try {
            is = new FileInputStream(dataPath);

            String line;
            reader = new BufferedReader(new InputStreamReader(is));
            line = reader.readLine();
            int count = 0;
            while (line != null) {
                if (line.equals("")) {
                    line = reader.readLine();
                    continue;
                }
                String[] data = line.split(",");
                if (data.length == 3) {
                    map.put(data[0], new FileInfo(data[0], Long.valueOf(data[1]), Long.valueOf(data[2])));
                }
                line = reader.readLine();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }


    public static void writeFile(List<FileInfo> list, String out) {
        if (isEmpty(list)) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        for (FileInfo info : list) {
            sb.append(info.path).append(",")
                    .append(info.lastModified).append(",")
                    .append(info.length).append("\n");
        }
        writeFile(out, sb.toString());
    }


    public static void writeFile(String out, String s) {
        FileOutputStream file = null;
        try {
            file = new FileOutputStream(out);
            file.write(s.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class FileInfo {
        public String path;
        public long lastModified;
        public long length;

        public FileInfo(String path, long lastModified, long length) {
            this.path = path;
            this.lastModified = lastModified;
            this.length = length;
        }

        public boolean eq(FileInfo info) {
            return this.path.equals(info.path) && this.lastModified == info.lastModified && this.length == info.length;
        }
    }


}

package com.wangyang.common.utils;

import com.google.common.base.Joiner;
import com.wangyang.common.CmsConst;
import com.wangyang.common.exception.ObjectException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class FileUtils {

    private static  String pattern = "<!--#include file=\"(.*?)\"-->";
    private static String varPattern = "<!--\\{\\{(.*?)}}-->";
    // 创建 Pattern 对象
    private static Pattern r = Pattern.compile(pattern);
    private static Pattern rv = Pattern.compile(varPattern);

    /**
     * Copies folder.
     *
     * @param source source path must not be null
     * @param target target path must not be null
     */
    public static void copyFolder(@NonNull Path source, @NonNull Path target) throws IOException {
        Assert.notNull(source, "Source path must not be null");
        Assert.notNull(target, "Target path must not be null");

        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path current = target.resolve(source.relativize(dir).toString());
                Files.createDirectories(current);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }


    public static Path getJarResources(String resourceName) {
        try {
            Path source ;
            URI templateUri = ResourceUtils.getURL("classpath:"+resourceName).toURI();

            if ("jar".equalsIgnoreCase(templateUri.getScheme())) {
                // Create new file system for jar
                FileSystem fileSystem = getFileSystem(templateUri);
                source = fileSystem.getPath("/BOOT-INF/classes/" + resourceName);
            } else {
                source = Paths.get(templateUri);
            }
            return source;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    private static FileSystem getFileSystem(@NonNull URI uri) throws IOException {
        Assert.notNull(uri, "Uri must not be null");

        FileSystem fileSystem;

        try {
            fileSystem = FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
        }

        return fileSystem;
    }



    public static  void saveFile(File file,String content){
        FileOutputStream fileOutputStream=null;
        try {
            fileOutputStream  = new FileOutputStream(file);

            fileOutputStream.write(content.getBytes());
            log.info("写入文件：{}",file.getPath().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fileOutputStream!=null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String openFile(File file){
        FileInputStream fileInputStream=null;
        try {
            if(file.exists()){
                fileInputStream = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
                List<String> list = reader.lines().collect(Collectors.toList());
                String content = Joiner.on("\n").join(list);
                return content;
            }else {
                return "Page is not found!!";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "Page is not found!!";
        }finally {
            if(fileInputStream!=null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static String openFile(String path){
        File file = new File(path);
        return openFile(file);
    }
    public static String convertByString(String content, HttpServletRequest request){
        // 现在创建 matcher 对象
        Matcher m = r.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
//            System.out.println("Found value: " + m.group(0) );
//            System.out.println("Found value: " + m.group(1) );
            String components = m.group(1);
            String componentsContent = openFile( CmsConst.WORK_DIR+"/html/"+components);
            componentsContent = Matcher.quoteReplacement(componentsContent);
            m.appendReplacement(sb,componentsContent);
        }

        m.appendTail(sb);
        String result = sb.toString();
        Matcher matcher = rv.matcher(result);
        while (matcher.find()){
            String attr = matcher.group(1);
//            System.out.println(attr);
            String attrS = (String) request.getAttribute(attr);
//            System.out.println(attrS);
            if(attrS!=null){
                result = matcher.replaceAll(attrS);
            }else {
                result = matcher.replaceAll("");
            }
        }
        return  result;
    }

    public static String convertByString(String content){
        // 现在创建 matcher 对象
        Matcher m = r.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
//            System.out.println("Found value: " + m.group(0) );
//            System.out.println("Found value: " + m.group(1) );
            String components = m.group(1);
            String componentsContent = openFile( CmsConst.WORK_DIR+"/html/"+components);
            componentsContent = Matcher.quoteReplacement(componentsContent);
            m.appendReplacement(sb,componentsContent);
        }

        m.appendTail(sb);
        String result = sb.toString();

        return  result;
    }



    public static String convert(File file, HttpServletRequest request){
        String content = openFile(file);
        return  convertByString(content,request);
    }
    public static String convert(File file){
        String content = openFile(file);
        return  convertByString(content);
    }

    public static String convert(String viewPath, HttpServletRequest request){
        File file = new File(CmsConst.WORK_DIR+"/html/"+viewPath);
        return  convert(file,request);
    }


    public  static boolean remove(String path) {
        File file = new File(path);
        return remove(file);
    }
//    public  static boolean remove(File file) {
//
//        if (!file.exists()) {
//            return false;
//        }
//        if (file.isFile()) {
//            return file.delete();
//        }
//
//        Arrays.asList(file.listFiles()).forEach(f->{
//            f.delete();
//        });
//        return file.delete();
//    }

    public static boolean remove(File dirFile) {
        // 如果dir对应的文件不存在，则退出
        if (!dirFile.exists()) {
            return false;
        }
        if (dirFile.isFile()) {
            return dirFile.delete();
        } else {
            for (File file : dirFile.listFiles()) {
                remove(file);
            }
        }
        return dirFile.delete();
    }
    public static List<String> getFileNames( String fileStr) {
        File file = new File(fileStr);
        if(!file.exists()){
            throw new ObjectException(fileStr+"文件不存在！！");
        }
        return  getFileNames(file);
    }
    /**
     * 得到文件名称
     *
     * @param path 路径
     * @return {@link List}<{@link String}>
     */
    public static List<String> getFileNames( File file) {
        if (!file.exists()) {
            return null;
        }
        List<String> fileNames = new ArrayList<>();
        return getFileNames(file, fileNames);
    }

    /**
     * 得到文件名称
     *
     * @param file      文件
     * @param fileNames 文件名
     * @return {@link List}<{@link String}>
     */
    private static List<String> getFileNames(File file, List<String> fileNames) {
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                getFileNames(f, fileNames);
            } else {
                fileNames.add(f.getName());
            }
        }
        return fileNames;
    }

    public static void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }

        byte[] buffer = new byte[1024];
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipInputStream.getNextEntry();

        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath));

                int bytesRead;
                while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
            } else {
                File dir = new File(filePath);
                dir.mkdir();
            }

            zipInputStream.closeEntry();
            entry = zipInputStream.getNextEntry();
        }

        zipInputStream.close();
    }

}

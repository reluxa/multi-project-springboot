package org.reluxa;

import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Runner {

    public static void main(String args[]) throws Exception {
        File explodeRoot = prepareParentDir();

        startApp("services/helloboot-service/build/distributions/helloboot-service-1.0.1.zip", "org.reluxa.helloboot.App", "helloboot", explodeRoot);
        startApp("services/hellospring-service/build/distributions/hellospring-service-1.0.1.zip", "org.reluxa.hellospring.App", "hellospring", explodeRoot);

        FileUtils.forceDeleteOnExit(explodeRoot);
    }

    private static File prepareParentDir() throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File explodeRoot = new File(tempDir, "explode-runner");
        System.out.println(explodeRoot.getAbsolutePath());
        FileUtils.deleteDirectory(explodeRoot);
        FileUtils.forceMkdir(explodeRoot);
        return explodeRoot;
    }

    private static void startApp(String path, String mainClassName, String threadName, File explodeRoot) throws Exception {
        File destination = new File(explodeRoot, mainClassName);
        FileUtils.forceMkdir(destination);
        System.out.println(new File(path).getAbsolutePath());
        ZipFile zipFile = new ZipFile(path);
        zipFile.extractAll(destination.getAbsolutePath());
        Collection<File> files = FileUtils.listFiles(destination, new SuffixFileFilter(".jar"), TrueFileFilter.INSTANCE);
        List<URL> jars = files.stream()
                .map(Runner::convert)
                .collect(Collectors.toList());
        URLClassLoader cl = URLClassLoader.newInstance(jars.toArray(new URL[]{}));
        Class mainClass = cl.loadClass(mainClassName);
        Thread thread = new Thread(create(mainClass, cl), threadName);
        thread.setContextClassLoader(cl);
        thread.start();
    }

    @SuppressWarnings("unchecked")
    private static Runnable create(Class mainClass, ClassLoader cl) {
        return () -> {
            try {
                disableTomactUrlStreamHandlerFactory(cl);
                Method main = mainClass.getMethod("main", String[].class);
                String[] params = new String[]{};
                main.invoke(null, (Object)params);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static void disableTomactUrlStreamHandlerFactory(ClassLoader cl) {
        try {
            Class<?> tomcatURLStreamHandlerFactoryClass = cl.loadClass("org.apache.catalina.webresources.TomcatURLStreamHandlerFactory");
            Method disable = tomcatURLStreamHandlerFactoryClass.getMethod("disable");
            disable.invoke(null);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private static URL convert(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


}

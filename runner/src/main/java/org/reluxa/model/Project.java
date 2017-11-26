package org.reluxa.model;

import org.reluxa.RunnerURLClassloader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class Project {

    public static final Set<String> BLACKLIST = new HashSet<>(Arrays.asList(
  //          "logback", "slf4j", "spring-boot-starter-logging"
    ));

    public final String applicationJarLocation;
    public final String classPathString;
    public final String mainClassName;
    public final int priority;

    public Project(String applicationJarLocation, String classPathString, String mainClassName, int priority) {
        this.applicationJarLocation = applicationJarLocation;
        this.classPathString = classPathString;
        this.mainClassName = mainClassName;
        this.priority = priority;
    }

    public Set<String> classPathElements() {
        return new HashSet<>(Arrays.asList(classPathString.split(File.pathSeparator)));
    };

    public RunnerURLClassloader getClassLoader(RunnerURLClassloader parent) {
        Set<String> classPathElements = classPathElements();
        classPathElements.removeAll(parent.jarLocations);
        classPathElements.add(applicationJarLocation);

        BLACKLIST.stream().forEach(black -> {
            classPathElements.removeIf(e-> e.contains(black));
        });

        return new RunnerURLClassloader(classPathElements, parent);
    }

    @SuppressWarnings("unchecked")
    public Runnable create(RunnerURLClassloader parent, CountDownLatch countDownLatch) {
        return () -> {
            try {
                RunnerURLClassloader classLoader = getClassLoader(parent);
                classLoader.jarLocations.forEach( loc -> System.out.println("\t Added to classloader of :" + mainClassName + ": " + loc));
                Thread.currentThread().setContextClassLoader(classLoader.urlClassLoader);
                Class<?> mainClass = classLoader.urlClassLoader.loadClass(mainClassName);
                disableTomactUrlStreamHandlerFactory(classLoader.urlClassLoader);
                Method main = mainClass.getMethod("main", String[].class);
                String[] params = new String[]{};
                main.invoke(null, (Object) params);
                System.err.println("\t" + mainClassName + " started");
                countDownLatch.countDown();
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
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


}

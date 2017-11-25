package org.reluxa;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RunnerURLClassloader {

    public final URLClassLoader urlClassLoader;
    public final Set<String> jarLocations;

    public RunnerURLClassloader(Set<String> jarLocations) {
        this.urlClassLoader = new URLClassLoader(convert(jarLocations));
        this.jarLocations = jarLocations;
    }

    public RunnerURLClassloader(Set<String> jarLocations, RunnerURLClassloader parent) {
        this.urlClassLoader = new URLClassLoader(convert(jarLocations), parent.urlClassLoader);
        this.jarLocations = jarLocations;
    }

    private URL[] convert(Set<String> jarLocations) {
        List<URL> urls = jarLocations.stream()
                .map(File::new)
                .map(this::toURL)
                .collect(Collectors.toList());
        return urls.toArray(new URL[]{});
    }

    private URL toURL(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


}

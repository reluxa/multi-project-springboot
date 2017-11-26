package org.reluxa.model;

import org.apache.commons.io.FileUtils;
import org.reluxa.RunnerURLClassloader;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProjectDependencies {

    public final List<Project> projectList;

    public static final Set<String> BLACKLIST = new HashSet<>(Arrays.asList(
//             "logback", "slf4j", "spring-boot-starter-logging"
    ));

    public ProjectDependencies(String projectDependenciesFileLocation) throws IOException {
        List<String> deps = FileUtils.readLines(new File(projectDependenciesFileLocation));
        projectList = deps.stream().map(l -> {
                    String[] depItem = l.split("\\|");
                    String jarFileName = depItem[0];
                    String mainClassName = depItem[1];
                    int priority = Integer.parseInt(depItem[2]);
                    String classPath = depItem[3];
                    return new Project(jarFileName, classPath, mainClassName, priority);
                }
        ).collect(Collectors.toList());
    }

    public RunnerURLClassloader getParentClassloader() {
        Set<String> reduced = projectList.stream()
                .map(Project::classPathElements)
                .reduce(projectList.get(0).classPathElements(), (a, b) -> {
                    //a.retainAll(b);
                    a.addAll(b);
                    return a;
                });
        BLACKLIST.stream().forEach(black -> {
                reduced.removeIf(e-> e.contains(black));
        });
        return new RunnerURLClassloader(reduced);
    }

}

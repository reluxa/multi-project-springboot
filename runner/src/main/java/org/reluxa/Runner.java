package org.reluxa;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multimaps;
import org.reluxa.model.Project;
import org.reluxa.model.ProjectDependencies;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Runner {

    public static void main(String args[]) throws Exception {
        if (args[0] != null && args[0].length() > 0) {
            ProjectDependencies projectDependencies = new ProjectDependencies(args[0]);
            RunnerURLClassloader parentClassloader = projectDependencies.getParentClassloader();
            parentClassloader.jarLocations.forEach( l -> System.out.println("\t Added to ParentClassloader: " + l));
            ImmutableListMultimap<Integer, Project> priorityMap = Multimaps.index(projectDependencies.projectList, p -> p.priority);
            ImmutableSortedSet<Integer> ascending = ImmutableSortedSet.copyOf(priorityMap.keySet());
            ascending.forEach( i -> startAll(priorityMap.get(i), parentClassloader, i));
        } else {
            System.out.println("Missing parameter, runner must be started by passing the project.depenencies file as argument!\n");
        }
    }

    public static void startAll(List<Project> projectList, RunnerURLClassloader parentClassloader, int priority) {
        CountDownLatch countDownLatch = new CountDownLatch(projectList.size());
        projectList.forEach(project -> {
                    Thread thread = new Thread(project.create(parentClassloader, countDownLatch), project.mainClassName);
                    thread.start();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.err.println("\tAll sub projects with priority " + priority + " have been started");
    }

}

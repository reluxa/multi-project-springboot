package org.reluxa;

import org.reluxa.model.ProjectDependencies;

public class Runner {

    public static void main(String args[]) throws Exception {
        if (args[0] != null && args[0].length() > 0) {
            ProjectDependencies projectDependencies = new ProjectDependencies(args[0]);
            RunnerURLClassloader parentClassloader = projectDependencies.getParentClassloader();
            System.out.println("ParentClassloader: ");
            parentClassloader.jarLocations.forEach(
                    System.out::println
            );
            projectDependencies.projectList.forEach(project -> {
                            Thread thread = new Thread(project.create(parentClassloader), project.mainClassName);
                            thread.start();
                    }
            );
        } else {
            System.out.println("Missing parameter, runner must be started by passing the project.depenencies file as argument!\n");
        }
    }

}

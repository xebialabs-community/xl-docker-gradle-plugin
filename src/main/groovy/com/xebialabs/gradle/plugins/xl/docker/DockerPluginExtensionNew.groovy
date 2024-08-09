package com.xebialabs.gradle.plugins.xl.docker

import org.gradle.api.Project

class DockerPluginExtensionNew {
    String runVersion;
    String runImage;
    String runPortMapping;
    List<DownloadNew> downloads
    Project project

    DockerPluginExtensionNew(Project project) {
        this.project = project
        this.downloads = new ArrayList<Download>()
    }

    DownloadNew download(String name, String user, String password, Closure closure) {
        println "Adding download section $name with user $user"
        DownloadNew download = new Download(name, user, password, closure)
        downloads.add(download)
        return download
    }

    DownloadNew download(String name, Closure closure) {
        println "Adding download section $name"
        DownloadNew download = new Download(name, closure)
        downloads.add(download)
        return download
    }
}

class DownloadNew {
    Closure closure
    String name
    String user
    String password
    DownloadNew(String name, String user=null, String password=null, Closure closure) {
        this.closure = closure
        this.name = name
        this.user = user
        this.password = password
    }
}

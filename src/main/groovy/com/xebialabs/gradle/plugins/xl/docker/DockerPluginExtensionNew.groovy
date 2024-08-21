/*
 * Copyright 2024 XEBIALABS
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.xebialabs.gradle.plugins.xl.docker

import org.gradle.api.Project

class DockerPluginExtensionNew {
    String runVersion;
    String runImage;
    String runPortMapping;
    String runRelativeResourcesPath;
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

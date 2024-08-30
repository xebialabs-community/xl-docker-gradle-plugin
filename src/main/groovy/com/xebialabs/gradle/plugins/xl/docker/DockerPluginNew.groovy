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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Exec
import de.undercouch.gradle.tasks.download.Download

class DockerPluginNew implements Plugin<Project> {
    public static final String RUN_DOCKER_TASK_NAME = "runDockerNew"
    public static final String COPY_DOWNLOADS_TASK_NAME = "copyDownloadResources"
    public static final String CLEAN_DOWNLOAD_CACHE_TASK_NAME = "cleanDownloadCache"
    public static final String DOWNLOAD_RESOURCES_TASK_NAME = "downloadResources"
    public static final String STOP_CONTAINERS_TASK_NAME = "stopContainers"
    public static final String START_DOCKER_COMPOSE_NAME = "runDockerCompose"
    public static final String STOP_DOCKER_COMPOSE_TASK_NAME = "stopDockerCompose"

    @Override
    void apply(Project project) {
        // Apply the base plugin to get cleaning behaviour
        project.apply plugin: "base"
        project.apply plugin: 'de.undercouch.download'

        DockerPluginExtensionNew dockerPluginExtension = project.extensions.create("xlDockerNew", DockerPluginExtensionNew, project)

        project.afterEvaluate {
            defineDownloadTasks(project, dockerPluginExtension)

            String licenseText
            def (registry, product) = dockerPluginExtension.runImage.tokenize('/')
            // if (dockerPluginExtension.runImage == "xebialabs/xl-deploy")
            if (product == "xl-deploy")
                licenseText = new File(System.getProperty("user.home") + "/xl-licenses/deployit-license.lic").text
            // if (dockerPluginExtension.runImage == "xebialabs/xl-release")
            if (product == "xl-release")
                licenseText = new File(System.getProperty("user.home") + "/xl-licenses/xl-release-license.lic").text
            String b64License = licenseText.bytes.encodeBase64().toString()

            Task runTask = createDockerTask(project, RUN_DOCKER_TASK_NAME, ["run", "--rm", "-d", "-e", "ADMIN_PASSWORD=admin", "-e", "XL_LICENSE=$b64License", "-p", dockerPluginExtension.runPortMapping, "--mount", "type=bind,source=" + project.getRootDir().absolutePath + "/" + dockerPluginExtension.runRelativeResourcesPath + ",target=/opt/xebialabs/$product-server/ext", dockerPluginExtension.runImage + ":" +dockerPluginExtension.runVersion])

            if (project.file("src/test/resources/docker/docker-compose.yml").exists()) {
                def stopTask = createDockerComposeTask(project, STOP_CONTAINERS_TASK_NAME, ["stop"], dockerPluginExtension)
                def stopDockerComposeTask = createDockerComposeTask(project, STOP_DOCKER_COMPOSE_TASK_NAME, ["rm", "--force"], dockerPluginExtension)
                stopDockerComposeTask.dependsOn stopTask
                def runDockerComposeTask = createDockerComposeTask(project, START_DOCKER_COMPOSE_NAME, ["up", "-d", "--no-recreate"], dockerPluginExtension)
                runDockerComposeTask.dependsOn compileTask
            }
        }

        createCleanDownloadCacheTask(project)
    }

    private Task createCleanDownloadCacheTask(Project project) {
        return project.tasks.create(CLEAN_DOWNLOAD_CACHE_TASK_NAME, Delete).configure {
            delete project.fileTree(dir: 'src/downloads/plugins', exclude: '**/*.gitignore')
        }
    }

    private static Task createDockerTask(Project project, String taskName, Iterable<?> taskArgs) {
        return project.tasks.create(taskName, Exec).configure {
            executable "docker"
            args(taskArgs)
            workingDir project.getProjectDir()
        }
    }

    private Task defineDownloadTasks(Project project, DockerPluginExtensionNew dockerPluginExtension) {
        def Task lastDownloadTask
        def Task firstDownloadTask
        dockerPluginExtension.downloads.each() { download ->
            def downloadTask = project.task("${DOWNLOAD_RESOURCES_TASK_NAME}_${download.name}", type: DownloadNew).configure {
                overwrite false
                username download.user
                password download.password
            }

            downloadTask.configure(download.closure)
            if (firstDownloadTask == null) {
                firstDownloadTask = downloadTask
            }

            if (lastDownloadTask != null) {
                lastDownloadTask.dependsOn downloadTask
            }
            lastDownloadTask = downloadTask
        }

        if (firstDownloadTask != null) {
            def copyDownloadsTask = project.task("${COPY_DOWNLOADS_TASK_NAME}", type: Copy).configure {
                from('src') {
                    include 'downloads/**/*'
                }
                into "$project.buildDir"
            }

            copyDownloadsTask.dependsOn firstDownloadTask
            firstDownloadTask = copyDownloadsTask
        }
        firstDownloadTask
    }

    private Task createDockerComposeTask(Project project, String taskName, Iterable<?> taskArgs, DockerPluginExtension dockerPluginExtension) {
        return project.tasks.create(taskName, Exec).configure {
            executable "docker-compose"
            args(taskArgs)
            workingDir "${project.file("src/test/resources/docker").absolutePath}"
        }
    }

}

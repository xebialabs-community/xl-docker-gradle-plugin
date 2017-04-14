# CI status #

[![Build Status][xl-docker-gradle-plugin-travis-image] ][xl-docker-gradle-plugin-travis-url]
[![Codacy Badge][xl-docker-gradle-plugin-codacy-image] ][xl-docker-gradle-plugin-codacy-url]
[![Code Climate][xl-docker-gradle-plugin-code-climate-image] ][xl-docker-gradle-plugin-code-climate-url]
[![License: MIT][xl-docker-gradle-plugin-license-image] ][xl-docker-gradle-plugin-license-url]

[xl-docker-gradle-plugin-travis-image]: https://travis-ci.org/xebialabs-community/xl-docker-gradle-plugin.svg?branch=master
[xl-docker-gradle-plugin-travis-url]: https://travis-ci.org/xebialabs-community/xl-docker-gradle-plugin
[xl-docker-gradle-plugin-codacy-image]: https://api.codacy.com/project/badge/Grade/c44ec3c885b64340a7af03c015391800
[xl-docker-gradle-plugin-codacy-url]: https://www.codacy.com/app/joris-dewinne/xl-docker-gradle-plugin
[xl-docker-gradle-plugin-code-climate-image]: https://codeclimate.com/github/xebialabs-community/xl-docker-gradle-plugin/badges/gpa.svg
[xl-docker-gradle-plugin-code-climate-url]: https://codeclimate.com/github/xebialabs-community/xl-docker-gradle-plugin
[xl-docker-gradle-plugin-license-image]: https://img.shields.io/badge/License-MIT-yellow.svg
[xl-docker-gradle-plugin-license-url]: https://opensource.org/licenses/MIT



# Overview #

The xl-docker-gradle-plugin allows to startup a Docker container with the XL Tool installed to compile the plugin.
Also you can use `runDocker` to start an XL Tool container with your plugin preloaded.

# Requirements #

* **Gradle** 2.13+

# Installation #

Define on top of the `build.gradle` file:

```
plugins {
  id "com.xebialabs.xl.docker" version "1.1.0"
}
```


For the latest version of the plugin have a look at:
[xl-docker-gradle-plugin](https://plugins.gradle.org/plugin/com.xebialabs.xl.docker)

# Usage #

You can make use of the following gradle tasks

* `compileDocker`
    * `compileImage`: Specifies which image to use for compilation (`xebialabs/xlr_dev_compile`, `xebialabs/xld_dev_compile` or `xebialabs/xltv-dev-compile`)
    * `compileVersion`: Specifies which version of the image to use. See [Docker hub](https://hub.docker.com/u/xebialabs/dashboard/)
    * `download`: Specifies any additional plugins to be downloaded.
* `runDocker`
    * `compileImage`: Specifies which image to use for compilation (`xebialabs/xlr_dev_compile`, `xebialabs/xld_dev_compile` or `xebialabs/xltv-dev-compile`)
    * `compileVersion`: Specifies which version of the image to use. See [Docker hub](https://hub.docker.com/u/xebialabs/dashboard/)
    * `runImage`: Specifies which image to use for running the XL Tool (`xebialabs/xlr_dev_run`, `xebialabs/xld_dev_run` or `xebialabs/xltv-dev-run`)
    * `runVersion`: Specifies which version of the image to use. See [Docker hub](https://hub.docker.com/u/xebialabs/dashboard/)
    * `runPortMapping`: Specifies which port mapping to use. For example `4516:4516`
    * `download`: Specifies any additional plugins to be downloaded.
    * The `src/main/resources` folder will be linked into the XL `ext` folder (so you don't have to restart on script changes)
    * This task depends on `compileDocker`
* `runDockerCompose`
    * A `docker-compose.yml` should be present under `src/test/resources/docker`
    * This task depends on `compileDocker`
* `stopDockerCompose`

# Example #

```
xlDocker {
  compileImage = 'xebialabs/xlr_dev_compile'
  compileVersion = 'v6.0.0.1'
  runImage = 'xebialabs/xlr_dev_run'
  runVersion = 'v6.0.0.1'
  runPortMapping = '5516:5516'
  download("xlr_community_plugins") {
    src(["https://github.com/xebialabs-community/xlr-xldeploy-plugin/releases/download/v2.1.5/xlr-xldeploy-plugin-2.1.5.jar",
         "https://github.com/xebialabs-community/xlr-xltestview-plugin/releases/download/v2.1.1/xlr-xltestview-plugin-2.1.1.jar"])
    dest file("src/downloads/plugins")
    acceptAnyCertificate true
  }
}
```


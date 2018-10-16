# DiscordSRV-Core [![Test Status](https://travis-ci.org/DiscordSRV/DiscordSRV-Core.svg?branch=master)](https://travis-ci.org/DiscordSRV/DiscordSRV-Core) [![Build Status](https://ci.scarsz.me/httpAuth/app/rest/builds/id:20/statusIcon.svg)](https://ci.scarsz.me/project.html?projectId=DiscordSRVCore&tab=projectOverview) [![GitHub issues](https://img.shields.io/github/issues/DiscordSRV/DiscordSRV-Core.svg)](https://github.com/DiscordSRV/DiscordSRV-Core/issues) [![GitHub forks](https://img.shields.io/github/forks/DiscordSRV/DiscordSRV-Core.svg)](https://github.com/DiscordSRV/DiscordSRV-Core/network) [![GitHub license](https://img.shields.io/github/license/DiscordSRV/DiscordSRV-Core.svg)](https://github.com/DiscordSRV/DiscordSRV-Core/blob/master/LICENSE)


## Installing as a dependency

### Gradle

We suggest using Gradle for DSRV2.

```groovy
repositories {
    jcenter()
    mavenCentral()
    maven { url 'https://maven.discordsrv.com' }
}

dependencies {
    compile group: 'com.discordsrv', name: 'DiscordSRV-Core', version: '2.0'
}
```

### Maven

```mxml
<repositories>
    <repositories>
        <repository>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <id>central</id>
            <name>bintray</name>
            <url>http://jcenter.bintray.com</url>
        </repository>
    </repositories>
    <pluginRepositories>
        <pluginRepository>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <id>central</id>
            <name>bintray-plugins</name>
            <url>http://jcenter.bintray.com</url>
        </pluginRepository>
    </pluginRepositories>
    <repository>
        <id>discordsrv</id>
        <url>https://maven.discordsrv.com</url>
    </repository>
    ...
</repositories>


<dependencies>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>2.0</version>
    </dependency>
    ...
</dependencies>

```

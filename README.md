# DiscordSRV2-Core [![Build Status](https://travis-ci.org/DiscordSRV/DiscordSRV2-Core.svg?branch=master)](https://travis-ci.org/DiscordSRV/DiscordSRV2-Core) [![GitHub issues](https://img.shields.io/github/issues/DiscordSRV/DiscordSRV2-Core.svg)](https://github.com/DiscordSRV/DiscordSRV2-Core/issues) [![GitHub forks](https://img.shields.io/github/forks/DiscordSRV/DiscordSRV2-Core.svg)](https://github.com/DiscordSRV/DiscordSRV2-Core/network) [![GitHub license](https://img.shields.io/github/license/DiscordSRV/DiscordSRV2-Core.svg)](https://github.com/DiscordSRV/DiscordSRV2-Core/blob/master/LICENSE)


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
    implementation group: 'com.discordsrv', project: 'DiscordSRV2-Core', version: '2.0-SNAPSHOT'
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
        <version>2.0-SNAPSHOT</version>
    </dependency>
    ...
</dependencies>

```

<sub>This maven repository is likely temporary until a DSRV-specific maven repo is established.</sub>

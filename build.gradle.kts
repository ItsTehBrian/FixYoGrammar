plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("xyz.jpenilla.run-paper") version "1.0.6"
}

group = "xyz.tehbrian"
version = "1.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/") {
        name = "papermc"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")

    implementation("com.google.inject:guice:5.0.1")
    implementation("org.languagetool:language-en:5.2")

    implementation("cloud.commandframework:cloud-paper:1.4.0")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.4.0")

    implementation("net.kyori:adventure-text-minimessage:4.0.0-SNAPSHOT")
}

tasks {
    processResources {
        expand("version" to project.version)
    }

    shadowJar {
        archiveBaseName.set("FixYoGrammar")
    }

    runServer {
        minecraftVersion("1.17.1")
    }
}

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
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
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")

    implementation("com.google.inject:guice:5.0.1")
    implementation("org.languagetool:language-en:5.5")

    implementation("cloud.commandframework:cloud-paper:1.6.1")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.6.1")

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

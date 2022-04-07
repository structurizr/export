plugins {
    java
    `maven-publish`
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    signing
}

description = "Structurizr Export"
group = "com.structurizr"
version = "1.5.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.structurizr:structurizr-client:1.12.2")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        val configuration = project.configurations.getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)
        val classpath = configuration.files.joinToString(" ") { it.name }
        val createdBy = "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})"

        attributes(
            "Class-Path" to classpath,
            "Created-By" to createdBy,
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set(project.name)
                description.set(project.description)
                url.set("https://github.com/structurizr/export")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("simon")
                        name.set("Simon Brown")
                        email.set("help@structurizr.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/structurizr/export.git")
                    developerConnection.set("scm:git:git@github.com:structurizr/export.git")
                    url.set("https://github.com/structurizr/export")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/"))
            snapshotRepositoryUrl.set(uri("https://oss.sonatype.org/content/repositories/snapshots/"))
            username.set(project.property("ossrhUsername") as String)
            password.set(project.property("ossrhPassword") as String)
        }
    }
}

signing {
    setRequired {
        project.property("signing.keyId") != "123456768"
    }
    sign(publishing.publications.getByName("maven"))
}

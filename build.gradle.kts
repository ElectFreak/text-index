plugins {
    kotlin("jvm") version "1.9.22"
    application
}
group = "me.electfreak"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-csv:1.8")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("com.github.ajalt.clikt:clikt:3.0.1")

    val junitVersion = "5.6.2"
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    maxHeapSize = "1024m"
}

application {
    mainClass.set("me.electfreak.MainKt")
}
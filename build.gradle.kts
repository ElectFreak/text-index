import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
}
group = "me.electfreak"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}
dependencies {
    val junitVersion = "5.6.2"
    implementation("org.junit.jupiter:junit-jupiter:5.4.2")
    implementation("org.apache.commons:commons-csv:1.8")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("com.github.ajalt.clikt:clikt:3.0.1")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    testImplementation(kotlin("test-junit"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation(kotlin("test"))
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    maxHeapSize = "1024m"
}

application {
    mainClassName = "me.electfreak.MainKt"
}
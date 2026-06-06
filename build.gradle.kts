plugins {
    kotlin("jvm") version "2.4.0"
    application
}

group = "io.github.camilyed"
version = "0.1.0"

kotlin {
    jvmToolchain(25)
}

application {
    mainClass.set("io.github.camilyed.contextparameters.DemoKt")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

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
    testImplementation(platform("org.junit:junit-bom:6.1.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.strikt:strikt-core:0.35.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

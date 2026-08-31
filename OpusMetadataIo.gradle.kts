plugins {
    kotlin("jvm")
    id("com.ncorti.ktfmt.gradle")
}

kotlin {
    jvmToolchain(21)
}

group = "org.sunsetware.omio"
version = "0.1.0"

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("commons-io:commons-io:2.22.0")
    testImplementation("com.jayway.jsonpath:json-path:3.0.0")
    // for suppressing jsonpath log warnings
    testImplementation("org.slf4j:slf4j-nop:2.0.18")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    testLogging { events("passed", "skipped", "failed") }
}

ktfmt { kotlinLangStyle() }

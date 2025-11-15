plugins {
    id("java")
}

group = "com.bluebed"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "lunarclient"
        url = uri("https://repo.lunarclient.dev")
    }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("net.minestom:minestom:2025.10.31-1.21.10")
    implementation("com.lunarclient:apollo-minestom:1.2.0")
    implementation("com.github.TogAr2:MinestomFluids:master-SNAPSHOT")
    implementation("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
}

tasks.test {
    useJUnitPlatform()
}
plugins {
    java
    application
}

group = "com.demo"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass.set("com.demo.Main")
}

repositories {
    mavenCentral()
}

dependencies {
    // Lombok for reducing boilerplate
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    
    // SLF4J for logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    
    // Testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.mockito:mockito-core:5.8.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// Add task to run performance tests
tasks.register<Test>("performanceTest") {
    description = "Runs performance tests"
    group = "verification"
    
    filter {
        includeTestsMatching("*PerformanceTest")
    }
    
    maxHeapSize = "2g"
    
    systemProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4")
}
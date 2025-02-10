plugins {
	java
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.poc"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
	maven {
		url = uri("https://packages.confluent.io/maven/")
	}
}

dependencies {
	implementation("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.apache.kafka:kafka-streams")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("com.fasterxml.jackson.core:jackson-databind")
	implementation("org.apache.kafka:kafka-clients")
	implementation("org.slf4j:slf4j-api")
	implementation("ch.qos.logback:logback-classic")
	implementation("io.confluent:kafka-json-serializer:7.5.1")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.bootBuildImage {
	builder = "paketobuildpacks/builder-jammy-base:latest"
}

// Add task to run TestDataGenerator
tasks.register<JavaExec>("generateTestData") {
	description = "Run the TestDataGenerator to produce test events"
	mainClass.set("kafkastreams.TestDataGenerator")
	classpath = sourceSets["test"].runtimeClasspath
}

// Add task to load product metadata
tasks.register<JavaExec>("loadProductMetadata") {
	description = "Load sample product metadata into Kafka"
	mainClass.set("kafkastreams.ProductMetadataGenerator")
	classpath = sourceSets["test"].runtimeClasspath
}

// Add task for performance testing
tasks.register<JavaExec>("generateLoadTest") {
	description = "Run high-volume performance test"
	mainClass.set("kafkastreams.LoadTestGenerator")
	classpath = sourceSets["test"].runtimeClasspath
}

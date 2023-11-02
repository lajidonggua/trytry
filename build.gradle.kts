import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.6"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"
}

group = "com.yummy"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	maven { url = uri("maven.aliyun.com/nexus/content/groups/public/") }
	mavenCentral()
}
subprojects{
	repositories {
		maven { url = uri("maven.aliyun.com/nexus/content/groups/public/") }
		mavenCentral()
	}
	apply{
		plugin("java")
		plugin("org.springframework.boot")
		plugin("io.spring.dependency-management")
		plugin("org.jetbrains.kotlin.jvm")
		plugin("org.jetbrains.kotlin.plugin.spring")
	}

	dependencies {
		// kotlin coroutines
		implementation("org.springframework:spring-context-support:6.0.6")
		implementation("org.springframework.boot:spring-boot-starter:3.1.0")
		implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
		implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
		implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")

		testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.0")
		if(project.name != "logging"){
			implementation(project(":base:logging"))
		}
	}
	configurations{
		all{
			// 排除logging, 和 log4j2 冲突了
			exclude("org.springframework.boot", "spring-boot-starter-logging")
		}
	}
	java.sourceCompatibility = JavaVersion.VERSION_17


	tasks.withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs = listOf("-Xjsr305=strict")
			jvmTarget = "17"
		}
	}
	tasks.withType<Test> {
		useJUnitPlatform()
	}
}
tasks.withType<Test> {
	useJUnitPlatform()
}

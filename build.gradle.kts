import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.protobuf.gradle.*

plugins {
	id("org.springframework.boot") version "2.4.1"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	kotlin("jvm") version "1.4.21"
	kotlin("plugin.spring") version "1.4.21"
	idea
	id("com.google.protobuf") version "0.8.8"
}

group = "io.mkth.security"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

val protoc_version by extra ("3.6.1")
val grpc_version by extra ("1.19.0")

dependencies {

	//GRPC Server
	implementation("net.devh:grpc-server-spring-boot-starter:2.10.1.RELEASE")

	implementation("com.salesforce.servicelibs:reactor-grpc-stub:1.0.1")

	implementation("io.grpc:grpc-netty:1.34.0")
	implementation("io.grpc:grpc-netty-shaded:1.34.0")
	implementation("io.grpc:grpc-stub:1.34.0")
	implementation("io.grpc:grpc-protobuf:1.34.0")

	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:${protoc_version}"
	}
	plugins {
		id("grpc") {
			artifact = "io.grpc:protoc-gen-grpc-java:${grpc_version}"
		}
		id("reactor") {
			artifact = "com.salesforce.servicelibs:reactor-grpc:1.0.1"
		}
	}
	generateProtoTasks {
		ofSourceSet("main").forEach {
			it.plugins {
				id("grpc")

				id("reactor")
			}
		}
	}
}

sourceSets {
	getByName("main").java.srcDirs("generated/source/proto/main/grpc")
	getByName("main").java.srcDirs("generated/source/proto/main/reactor")
	getByName("main").java.srcDirs("generated/source/proto/main/java")
	getByName("main").java.srcDirs("generated/source/proto/main/kotlin")
}

idea {
	module {
		generatedSourceDirs.plusAssign(file("build/generated/source/proto/main/grpc"))
		generatedSourceDirs.plusAssign(file("build/generated/source/proto/main/reactor"))
		generatedSourceDirs.plusAssign(file("build/generated/source/proto/main/java"))
		generatedSourceDirs.plusAssign(file("build/generated/source/proto/main/kotlin"))
	}
}
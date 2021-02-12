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

	//Consul all:3.3.1")
	implementation("org.springframework.cloud:spring-cloud-starter-consul-discovery:3.0.1")

	implementation("org.springframework.boot:spring-boot-starter-actuator")

	//GRPC Server
	implementation("net.devh:grpc-server-spring-boot-starter:2.10.1.RELEASE")
	implementation("com.salesforce.servicelibs:reactor-grpc-stub:1.0.1")

	//protobuf("io.mkth.grpc.authentication.protobuf:authentication-protobuf:1.0.0-SNAPSHOT")

	protobuf(files("/Users/mateuslimafonseca/Projects/grpc-authentication/protobuf/src/main/resources/authentication/LoginService.proto"))

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

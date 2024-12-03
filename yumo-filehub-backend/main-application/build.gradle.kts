plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "filehub.uncleyumo.cn"
version = "2.0.0"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	maven("https://maven.aliyun.com/repository/public/")
	maven("https://maven.aliyun.com/repository/spring/")
	mavenLocal()
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("cn.uncleyumo.utils:print-plus-kotlin:1.1.0")
	implementation("org.springframework.boot:spring-boot-starter-quartz")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-validation")  // 对于参数校验
	implementation("org.springframework.boot:spring-boot-starter-log4j2")  // 日志

	implementation("com.alibaba.fastjson2:fastjson2-extension-spring5:2.0.53")
//	implementation("com.alibaba.fastjson2:fastjson2:2.0.53")  // 对于 JSON序列化 fastjson2
//	implementation("com.alibaba:fastjson2-spring-boot-starter:1.0.23")

//	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")  // 添加 jackson-datatype-jsr310 依赖
//	implementation("com.fasterxml.jackson.module:jackson-module-kotlin") // 对于 JSON序列化

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

configurations.all {
	exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

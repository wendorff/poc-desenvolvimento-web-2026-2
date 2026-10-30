import com.github.gradle.node.npm.task.NpmTask

plugins {
	java
	id("org.springframework.boot") version "4.1.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.github.node-gradle.node") version "7.1.0"
}

group = "br.edu.ufcspa"
version = "0.0.1-SNAPSHOT"
description = "Sistema de Gestao de Farmacia Hospitalar"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-security")

	testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb-test")
	testImplementation("org.springframework.boot:spring-boot-starter-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-thymeleaf-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Baixa um Node próprio do projeto (em .gradle/nodejs) e roda o build do Vite antes de
// empacotar, então ./gradlew continua sendo o único comando necessário
node {
	version = "24.22.2"
	download = true
	npmInstallCommand = "ci"
}

tasks.npmInstall {
	args.addAll("--no-audit", "--no-fund")
}

val buildFrontend = tasks.register<NpmTask>("buildFrontend") {
	description = "Empacota o frontend com o Vite em build/frontend/static/assets."
	dependsOn(tasks.npmInstall)
	args = listOf("run", "build")
	inputs.dir("src/main/frontend")
	inputs.files("package.json", "package-lock.json", "vite.config.js")
	outputs.dir(layout.buildDirectory.dir("frontend"))
}

// O bundle do Vite entra nos recursos da aplicação, então vai para o classpath e para o jar
sourceSets.main {
	resources.srcDir(buildFrontend)
}

tasks.bootRun {
	// Lê os recursos direto das pastas de origem, então o que o `npm run watch` recompila
	// aparece só recarregando a página, sem reiniciar a aplicação
	sourceResources(sourceSets.main.get())
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// Só baixa as dependências da aplicação, sem compilar nada; o Dockerfile usa para deixá-las
// numa camada própria, como o dependency:go-offline do Maven
tasks.register("downloadDependencies") {
	description = "Baixa as dependências de compilação e de execução da aplicação."
	val classpaths = files(configurations.compileClasspath, configurations.runtimeClasspath)
	doLast { classpaths.files }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-stream:4.0.1")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka:4.0.1")
    api("org.springframework.cloud:spring-cloud-starter-stream-kafka:4.0.2"){
        exclude("org.yaml", "snakeyaml")
    }
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.datatype/jackson-datatype-jsr310
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    project(":base:logging")
}
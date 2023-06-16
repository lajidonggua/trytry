dependencies {
    //implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka")
    api("org.springframework.cloud:spring-cloud-starter-stream-kafka:4.0.2"){
        exclude("org.yaml", "snakeyaml")
    }
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-stream:4.0.1")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka:4.0.1")
    api("org.springframework.cloud:spring-cloud-starter-stream-kafka:4.0.1"){
        exclude("org.yaml", "snakeyaml")
    }
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.datatype/jackson-datatype-jsr310
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    implementation("com.squareup.okhttp3:okhttp:3.0.0")

}
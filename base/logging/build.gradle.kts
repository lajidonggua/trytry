dependencies{
    api("org.springframework.boot:spring-boot-starter-log4j2")

    // micrometer
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
}
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-graphql:3.0.4")
    implementation("org.springframework.boot:spring-boot-starter-data-redis:3.0.4")
    implementation("com.expediagroup:graphql-kotlin-schema-generator:6.4.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.14.2")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(project(":base:spring-kafka"))
}


plugins {
    java
}

dependencies {
    implementation(files("../libs/game.jar"))
    implementation(files("../libs/fabric-loader-0.16.7-fat.jar"))
    implementation(files("../libs/gson-2.11.0.jar"))
    implementation(files("../libs/guava-21.0.jar"))
    implementation(files("../libs/mixinextras-fabric-0.4.1.jar"))
    implementation(files("../libs/sponge-mixin-0.15.3+mixin.0.8.7.jar"))
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = 8
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

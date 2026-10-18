plugins {
    application
}
repositories {
    mavenCentral()
}
dependencies {
    implementation("org.apache.commons:commons-text:1.10.0")
}
application {
    mainClass.set("App")
}
dependencyLocking {
    lockAllConfigurations()
}

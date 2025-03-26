plugins {
	java
	application
	id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
	mavenCentral()
}

val javaFXModules = listOf(
	"base",
	"controls",
	"fxml",
	"swing",
	"graphics"
)

val supportedPlatforms = listOf("linux", "mac", "win")

dependencies {
	compileOnly("com.github.spotbugs:spotbugs-annotations:4.9.0")

	val javaFxVersion = 15
	for (platform in supportedPlatforms) {
		for (module in javaFXModules) {
			implementation("org.openjfx:javafx-$module:$javaFxVersion:$platform")
		}
	}

	val jUnitVersion = "5.11.4"
	testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

application {
	mainClass.set("it.unibo.bazinga.App")
}

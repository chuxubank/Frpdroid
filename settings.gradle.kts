dependencyResolutionManagement {
    val user: String by settings
    val token: String by settings
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.github.com/chuxubank/frplib") {
            credentials {
                username = user
                password = token
            }
        }
    }
}
rootProject.name = "Frpdroid"
include(":app")

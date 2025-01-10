pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven(url = "https://jitpack.io")
        maven(url = "https://get.videolan.org/vlc-android/maven/")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")


//        maven {
//            url = uri("https://google.bintray.com/exoplayer/")
//        }
    }
}

rootProject.name = "HD Video Player - All in One"
include(":app")

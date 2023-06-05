object KtorServerResources: default.Ktor2Defaults.ServerDefaults {
    val resources get() = dependency("ktor-server-resources")
}

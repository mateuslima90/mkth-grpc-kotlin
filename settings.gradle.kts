rootProject.name = "authentication"


include(
        ":authentication-protobuf",
        ":authentication-service"
)

project(":authentication-protobuf").projectDir   = file("protobuf")
project(":authentication-service").projectDir    = file("service")
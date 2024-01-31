package com.zeyad.securefileaccess.controllers;

import com.zeyad.securefileaccess.dto.request.SigninRequestDTO;
import com.zeyad.securefileaccess.dto.request.SignupRequestDTO;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class AuthControllerIntegrationTest {
//    private static final Network network = Network.newNetwork();
//    @Container
//    private static final GenericContainer<?> postgresContainer =
//            new GenericContainer<>(DockerImageName.parse("postgres:alpine3.18"))
//                    .withEnv("POSTGRES_USER", "zeyad")
//                    .withEnv("POSTGRES_PASSWORD", "password")
//                    .withEnv("PGDATA", "/data/postgres")
//                    .withExposedPorts(5432)
//                    .withNetwork(network)
//                    .withFileSystemBind("postgres", "/data/postgres") // Bind a volume to persist data
//                    .withStartupTimeout(Duration.ofSeconds(60))
//                    .withReuse(true); // Reuse container between tests
//
//    @Container
//    private static final GenericContainer<?> pgadminContainer =
//            new GenericContainer<>(DockerImageName.parse("dpage/pgadmin4"))
//                    .withEnv("PGADMIN_DEFAULT_EMAIL", "admin@postgres.com")
//                    .withEnv("PGADMIN_DEFAULT_PASSWORD", "password")
//                    .withEnv("PGADMIN_CONFIG_SERVER_MODE", "False")
//                    .withExposedPorts(80)
//                    .withNetworkAliases("pgadmin")
//                    .withNetwork(network)
//                    .withReuse(true); // Reuse container between tests
//
    @Container
    private static final KeycloakContainer keycloakContainer =
            new KeycloakContainer()
                    .withRealmImportFile("realm-export.json")
                    .withAdminUsername("admin")
                    .withAdminPassword("password");
//                    .withEnv("KEYCLOAK_DATABASE_HOST", "postgres")
//                    .withEnv("KEYCLOAK_DATABASE_USER", "zeyad")
//                    .withEnv("KEYCLOAK_DATABASE_PASSWORD", "password")
//                    .withEnv("KEYCLOAK_DATABASE_NAME", "secure_file_system")
//                    .withEnv("KEYCLOAK_DATABASE_SCHEMA", "bitnami")
//                    .withExposedPorts(8080)
//                    .withNetworkAliases("keycloak")
//                    .withNetwork(network)
//                    .waitingFor(Wait.forHttp("/auth"))
//                    .withStartupTimeout(Duration.ofSeconds(300))
//                    .withReuse(true);
    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloakContainer.getAuthServerUrl() + "/realms/Secure-File-System");
        registry.add("keycloak.serverUrl" ,()-> keycloakContainer.getAuthServerUrl());
        registry.add("keycloak.realm", ()-> "Secure-File-System");
        registry.add("keycloak.clientId",()-> "secure-file-system-CLI");
        registry.add("keycloak.username",()-> "client-admin");
        registry.add("keycloak.password", ()-> "password");
        registry.add("keycloak.clientSecret",()-> "NH9NwlM7pahNEFIDOILWAjC0hYRixwNp");
        registry.add("keycloak.loginUrl",()-> keycloakContainer.getAuthServerUrl() + "/realms/Secure-File-System/protocol/openid-connect/token");
    }
    @LocalServerPort
    private int port;

    private String authServerUrl;

    @BeforeEach
    void setup() {
        //authServerUrl = keycloakContainer.getAuthServerUrl() + "/realms/Secure-File-System/protocol/openid-connect/token";
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.config = RestAssuredConfig.config().sslConfig(SSLConfig.sslConfig().allowAllHostnames());
    }
    @Test
    void testSignupUser_whenUserRegistered_shouldReturn409Conflict() {
        SignupRequestDTO signupRequestDTO = new SignupRequestDTO(
                "client-admin", "firstName", "lastName","email@test.com",
                "password", "password", "user"
        );
        var response = given().contentType(ContentType.JSON).body(signupRequestDTO)
                .when().post("/api/v1/auth/signup");
        assertEquals(409, response.getStatusCode() );
    }
    @Test
    void testSignupUser_whenNewUserRegistered_shouldReturn200ok() {
        SignupRequestDTO signupRequestDTO = new SignupRequestDTO(
                "new user", "firstName", "lastName","email@test.com",
                "password", "password", "user"
        );
        var response = given().contentType(ContentType.JSON).body(signupRequestDTO)
                .when().post("/api/v1/auth/signup");
        assertEquals(200, response.getStatusCode() );
    }

    @Test
    public void testSigninUser_whenValidCredentials_shouldReturn200Ok() {
        SigninRequestDTO signinRequestDTO = new SigninRequestDTO("client-admin", "password");
        var response =RestAssured.given()
                .contentType(ContentType.JSON).body(signinRequestDTO)
                .when()
                .post("/api/v1/auth/signin");
        assertEquals(200, response.getStatusCode());
    }
    @Test
    public void testSigninUser_whenInvalidCredentials_shouldReturn401unauthorized() {
        SigninRequestDTO signinRequestDTO = new SigninRequestDTO("invalid-admin", "password");
        var response =RestAssured.given()
                .contentType(ContentType.JSON).body(signinRequestDTO)
                .when()
                .post("/api/v1/auth/signin");
        assertEquals(401, response.getStatusCode());
    }
}
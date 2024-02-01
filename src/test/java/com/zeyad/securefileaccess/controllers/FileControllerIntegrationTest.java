package com.zeyad.securefileaccess.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeyad.securefileaccess.dao.FileDAO;
import com.zeyad.securefileaccess.dao.FileEntityRepository;
import com.zeyad.securefileaccess.dao.UserEntityDAO;
import com.zeyad.securefileaccess.dto.request.FileRequestDTO;
import com.zeyad.securefileaccess.dto.request.GrantAccessRequestDTO;
import com.zeyad.securefileaccess.dto.request.SignupRequestDTO;
import com.zeyad.securefileaccess.dto.response.FileResponseDTO;
import com.zeyad.securefileaccess.entity.FileEntity;
import com.zeyad.securefileaccess.entity.UserEntity;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManager;
import org.checkerframework.checker.units.qual.C;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DirtiesContext
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE )
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileControllerIntegrationTest {
    static Network network = Network.newNetwork();
    @Container
    @ServiceConnection
    private static PostgreSQLContainer postgreSQLContainer =  new PostgreSQLContainer<>("postgres:alpine3.18")
            .withDatabaseName("postgres")
            .withPassword("password")
            .withUsername("zeyad")
            .withNetwork(network)
            .withNetworkAliases("postgres")
            .withCopyFileToContainer(MountableFile.forClasspathResource("dump.sql"), "/docker-entrypoint-initdb.d/init.sql")
            .withExposedPorts(5432);
    @Container
    static KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:23.0")
            .withRealmImportFile("realm-export.json")
            .withExposedPorts(8080)
            .withNetwork(network)
            .withNetworkAliases("x")
            .dependsOn(postgreSQLContainer)
            .withEnv("KC_DB", "postgres")
            .withEnv("KC_DB_PASSWORD","password")
            .withEnv("KC_DB_SCHEMA", "bitnami")
            .withEnv("KC_DB_URL","jdbc:postgresql://postgres:5432/postgres")
            .withEnv("KC_DB_USERNAME","zeyad")
            .withEnv("KC_DB_URL_PORT","5432")
            .withEnv("KC_DB_URL_HOST", "postgres");
//    @Container
//    static GenericContainer<?> pgadminContainer = new GenericContainer<>(DockerImageName.parse("dpage/pgadmin4:latest"))
//            .withNetwork(network)
//            .withExposedPorts(80)
//            .withNetworkAliases("pgadmin")
//            .withEnv("PGADMIN_DEFAULT_EMAIL", "admin@postgres.com")
//            .withEnv("PGADMIN_DEFAULT_PASSWORD", "password")
//            .withEnv("PGADMIN_CONFIG_SERVER_MODE", "False");

    @Autowired
    private FileDAO fileDAO;
    @Autowired
    private UserEntityDAO userEntityDAO;
    @Autowired
    private FileEntityRepository fileEntityRepository;
    List<UserEntity> userEntityList;
    List<FileEntity> fileEntityList;
    @BeforeAll
    static void init() throws Exception{
        try{
            postgreSQLContainer.start();
            keycloakContainer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
        assertTrue(postgreSQLContainer.isCreated());
        assertTrue(postgreSQLContainer.isRunning());
        assertTrue(keycloakContainer.isCreated());
        assertTrue(keycloakContainer.isRunning());
    }

    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
//        //postgres configuration #very important note that when using @serviceConnection it connects to postgres database not the oe
        //you provide as it's geenrated by default thus u need to configure keycloak to the same database secure_file_system -> postgres
//        int postgresPort = postgreSQLContainer.getFirstMappedPort();
//        String databaseUrl = "jdbc::postgresql://localhost:" + postgresPort+"/secure_file_system";
//        registry.add("spring.jpa.hibernate.ddl-auto", ()-> "update");
//        registry.add("spring.datasource.url", ()-> databaseUrl);
//        registry.add("spring.datasource.username", ()-> "zeyad");
//        registry.add("spring.datasource.password", ()-> "password");
//        registry.add("spring.jpa.show-sql", ()-> "true");
        //keycloak configuration
        int keyCloakPort = keycloakContainer.getFirstMappedPort();
        String authUrl = "http://localhost:"+String.valueOf(keyCloakPort);
        System.out.println(authUrl);
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> authUrl + "/realms/Secure-File-System");
        registry.add("keycloak.serverUrl" ,()-> authUrl);
        registry.add("keycloak.realm", ()-> "Secure-File-System");
        registry.add("keycloak.clientId",()-> "secure-file-system-CLI");
        registry.add("keycloak.username",()-> "client-admin");
        registry.add("keycloak.password", ()-> "password");
        registry.add("keycloak.clientSecret",()-> "NH9NwlM7pahNEFIDOILWAjC0hYRixwNp");
        registry.add("keycloak.loginUrl",()-> authUrl + "/realms/Secure-File-System/protocol/openid-connect/token");
    }
    @LocalServerPort
    private int port;

    private int file1Id, file2Id;
    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.config = RestAssuredConfig.config().sslConfig(SSLConfig.sslConfig().allowAllHostnames());
    }

    @Test
    void testGetFiles_whenUser1LogsSuccessfully_shouldReturnFilesThatUserHaveAuthorityForFile1() throws JSONException, JsonProcessingException {
        Map<String, Object> authRequestBody = new HashMap<>();
        authRequestBody.put("username", "username1");
        authRequestBody.put("password", "password");
        var authResponse = given()
                .contentType(ContentType.JSON).body(authRequestBody)
                .when()
                .post("/api/v1/auth/signin");
        assertEquals(HttpStatus.OK.value(), authResponse.getStatusCode());
        var access_token = new JSONObject(authResponse.asString()).getString("access_token");
        var requestHeader = new HashMap<String,Object>();
        requestHeader.put("Authorization", "Bearer " + access_token);
        var response = RestAssured.given().headers(requestHeader).when().get("/api/v1/files");
        ObjectMapper objectMapper = new ObjectMapper();
        var files = objectMapper.readValue(response.asString(), new TypeReference<List<FileResponseDTO>>(){});
        var actualFileNames = files.stream().map(x-> x.getName()).toList();
        var expectedFileNames = List.of("file1");
        assertEquals(expectedFileNames, actualFileNames );
    }

    @Test
    void getFileById() {
    }
    @Order(1)
    @Test
    void testAddFile_whenUser1LogsSuccessfully_shouldReturnFile1ResponseDto() throws JSONException, JsonProcessingException {
        Map<String, Object> authRequestBody = new HashMap<>();
        authRequestBody.put("username", "username1");
        authRequestBody.put("password", "password");
        var authResponse = given()
                .contentType(ContentType.JSON).body(authRequestBody)
                .when()
                .post("/api/v1/auth/signin");
        assertEquals(HttpStatus.OK.value(), authResponse.getStatusCode());
        var access_token = new JSONObject(authResponse.asString()).getString("access_token");
        var requestHeader = new HashMap<String,Object>();
        requestHeader.put("Authorization", "Bearer " + access_token);
        ObjectMapper objectMapper = new ObjectMapper();
        var body = Map.of("name","file1",
                          "content","content");
        var response = RestAssured.given().headers(requestHeader).contentType(ContentType.JSON)
                .body(body)
                .when().post("/api/v1/files");
        var file = objectMapper.readValue(response.asString(), FileResponseDTO.class);
        assertEquals(200, response.getStatusCode());
    }
    @Order(2)
    @Test
    void testAddFile_whenUser2LogsSuccessfully_shouldReturnFile2ResponseDto() throws JSONException, JsonProcessingException {
        Map<String, Object> authRequestBody = new HashMap<>();
        authRequestBody.put("username", "username2");
        authRequestBody.put("password", "password");
        var authResponse = given()
                .contentType(ContentType.JSON).body(authRequestBody)
                .when()
                .post("/api/v1/auth/signin");
        assertEquals(HttpStatus.OK.value(), authResponse.getStatusCode());
        var access_token = new JSONObject(authResponse.asString()).getString("access_token");
        var requestHeader = new HashMap<String,Object>();
        requestHeader.put("Authorization", "Bearer " + access_token);
        ObjectMapper objectMapper = new ObjectMapper();
        var body = Map.of("name","file2",
                "content","content");
        var response = RestAssured.given().headers(requestHeader).contentType(ContentType.JSON)
                .body(body)
                .when().post("/api/v1/files");
        var file = objectMapper.readValue(response.asString(), FileResponseDTO.class);
        assertEquals(200, response.getStatusCode());
    }
    @Order(3)
    @Test
    void testGrantAccess_whenUser2GivesAccessToUser1_shouldReturn200ok() throws JSONException, JsonProcessingException {
        Map<String, Object> authRequestBody = new HashMap<>();
        authRequestBody.put("username", "username2");
        authRequestBody.put("password", "password");
        var authResponse = given()
                .contentType(ContentType.JSON).body(authRequestBody)
                .when()
                .post("/api/v1/auth/signin");
        assertEquals(HttpStatus.OK.value(), authResponse.getStatusCode());
        var access_token = new JSONObject(authResponse.asString()).getString("access_token");
        var requestHeader = new HashMap<String,Object>();
        requestHeader.put("Authorization", "Bearer " + access_token);
        ObjectMapper objectMapper = new ObjectMapper();
        var body = objectMapper.writeValueAsString(new GrantAccessRequestDTO(Set.of("username1")));
        String path = "/api/v1/files/2/grant-access";
        RestAssured.given().headers(requestHeader).contentType(ContentType.JSON)
                .body(body)
                .when().post(path)
                .then().assertThat().statusCode(equalTo(200));
    }
    @Order(4)
    @Test
    void testGrantAccess_whenUser2GivesAccessToUser1OnFileDoesNotMeetUser2Authority_shouldReturn401Unauthorized() throws JSONException, JsonProcessingException {
        Map<String, Object> authRequestBody = new HashMap<>();
        authRequestBody.put("username", "username2");
        authRequestBody.put("password", "password");
        var authResponse = given()
                .contentType(ContentType.JSON).body(authRequestBody)
                .when()
                .post("/api/v1/auth/signin");
        assertEquals(HttpStatus.OK.value(), authResponse.getStatusCode());
        var access_token = new JSONObject(authResponse.asString()).getString("access_token");
        var requestHeader = new HashMap<String,Object>();
        requestHeader.put("Authorization", "Bearer " + access_token);
        ObjectMapper objectMapper = new ObjectMapper();
        var body = objectMapper.writeValueAsString(new GrantAccessRequestDTO(Set.of("username1")));
        String path = "/api/v1/files/1/grant-access";
        RestAssured.given().headers(requestHeader).contentType(ContentType.JSON)
                .body(body)
                .when().post(path)
                .then().assertThat().statusCode(equalTo(401));
    }
    @Test
    void deleteFile() {
    }

    @Test
    void updateFile() {
    }
}
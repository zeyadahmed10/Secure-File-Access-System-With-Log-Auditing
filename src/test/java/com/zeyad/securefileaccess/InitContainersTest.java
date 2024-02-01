package com.zeyad.securefileaccess;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE )
public class InitContainersTest {
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
    @Container
    static GenericContainer<?> pgadminContainer = new GenericContainer<>(DockerImageName.parse("dpage/pgadmin4:latest"))
            .withNetwork(network)
            .withExposedPorts(80)
            .withNetworkAliases("pgadmin")
            .withEnv("PGADMIN_DEFAULT_EMAIL", "admin@postgres.com")
            .withEnv("PGADMIN_DEFAULT_PASSWORD", "password")
            .withEnv("PGADMIN_CONFIG_SERVER_MODE", "False");


    @BeforeAll
    static void init() throws Exception{
        try{
            postgreSQLContainer.start();
            keycloakContainer.start();
            pgadminContainer.start();
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
    @Test
    void test(){
        String x = "lol";
        System.out.println(x);

    }
}

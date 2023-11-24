package bo.ucb.edu.mabfileuploader.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class GlobalSecurityConfiguration {

    private final KeycloakJwtTokenConverter keycloakJwtTokenConverter;

    private final Logger logger = LoggerFactory.getLogger(GlobalSecurityConfiguration.class);

    private final SecurityConstraintsProperties securityConstraintsProperties;
    public GlobalSecurityConfiguration(TokenConverterProperties properties, SecurityConstraintsProperties securityConstraintsProperties) {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter
                = new JwtGrantedAuthoritiesConverter();
        this.keycloakJwtTokenConverter
                = new KeycloakJwtTokenConverter(
                jwtGrantedAuthoritiesConverter,
                properties);
        this.securityConstraintsProperties = securityConstraintsProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        List<SecurityConstraint> securityConstraints = securityConstraintsProperties.getConstraints();

        http.authorizeHttpRequests( (authorizeHttpRequests) -> {
            securityConstraints.forEach( (constraint) -> {
                try {
                    List<String> authRoles = constraint.getAuthRoles();
                    List<SecurityCollection> securityCollections = constraint.getSecurityCollections();

                    securityCollections.forEach(collection -> {
                        String name = collection.getName();
                        List<String> patterns = collection.getPatterns();
                        List<String> methods = collection.getMethods();

                        List<HttpMethod> httpMethods = new ArrayList<>();

                        for (String method: methods
                             ) {
                            switch (method) {
                                case "GET":
                                    httpMethods.add(HttpMethod.GET);
                                    break;
                                case "POST":
                                    httpMethods.add(HttpMethod.POST);
                                    break;
                                case "PUT":
                                    httpMethods.add(HttpMethod.PUT);
                                    break;
                                case "DELETE":
                                    httpMethods.add(HttpMethod.DELETE);
                                    break;
                                case "PATCH":
                                    httpMethods.add(HttpMethod.PATCH);
                                    break;
                                default:
                                    break;
                            }
                        }


                        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry response = null;

                        if (httpMethods.isEmpty()) {
                            if(authRoles.size() == 1){
                                String role = authRoles.get(0);
                                if(role.equals("permitAll")){
                                    logger.info("name: " + name + " patterns: " + patterns + " configuration: permitAll with no methods");
                                    authorizeHttpRequests
                                            .requestMatchers(patterns.toArray(new String[0]))
                                            .permitAll();
                                }
                            }else{
                                logger.info("name: " + name + " patterns: " + patterns + " configuration:" + authRoles + "with no methods");
                                authorizeHttpRequests
                                        .requestMatchers(patterns.toArray(new String[0]))
                                        .hasAnyRole(authRoles.toArray(new String[0]));
                            }
                        }else{
                            if (authRoles.size() == 1) {
                                String role = authRoles.get(0);
                                if (role.equals("permitAll")) {
                                    logger.info("name: " + name + " patterns: " + patterns + " configuration: permitAll with methods: " + httpMethods);
                                    for (HttpMethod httpMethod: httpMethods
                                    ) {
                                        authorizeHttpRequests
                                                .requestMatchers(httpMethod, patterns.toArray(new String[0]))
                                                .permitAll();
                                    }
                                }
                            }else{
                                logger.info("name: " + name + " patterns: " + patterns + " configuration:" + authRoles + "with methods: " + httpMethods);
                                for (HttpMethod httpMethod: httpMethods
                                ) {
                                   authorizeHttpRequests
                                            .requestMatchers(httpMethod, patterns.toArray(new String[0]))
                                            .hasAnyRole(authRoles.toArray(new String[0]));
                                }
                            }
                        }

                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            authorizeHttpRequests.anyRequest().denyAll();
            /*authorizeHttpRequests
                    .requestMatchers("/api/v1/registry/patient").permitAll()
                    .requestMatchers("/api/v1/country-cities").permitAll()
                    .requestMatchers("/api/v1/request").hasAnyRole("doctorJefe", "doctor")
                    .requestMatchers("/api/v1/doctor/assign/**").hasRole("doctorJefe")
                    .requestMatchers("/api/v1/doctor/**").hasAnyRole("doctorJefe", "doctor")
                    .requestMatchers("/api/v1/cycle").hasRole("doctorJefe")
                    .anyRequest()
                    .denyAll();*/
        });
        http.oauth2ResourceServer( (oauth2) -> {
            oauth2.jwt( (jwt) -> jwt.jwtAuthenticationConverter(keycloakJwtTokenConverter));
        });
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
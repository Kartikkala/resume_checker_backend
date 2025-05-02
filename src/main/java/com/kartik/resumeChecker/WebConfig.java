package com.kartik.resumeChecker;

import com.kartik.authentication.Authenticator;
import com.kartik.authentication.Login;
import com.kartik.authentication.interfaces.AuthenticationStratergy;
import com.kartik.authentication.jwt.JwtStratergy;
import com.kartik.authentication.AuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class WebConfig implements WebMvcConfigurer {

    private final Authenticator authenticator;
    private final Login login;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public WebConfig(Authenticator authenticator, Login login, JwtStratergy jwtStratergy, AuthenticationManager authenticationManager) {
        this.authenticator = authenticator;
        this.login = login;
        this.authenticationManager = authenticationManager;
        this.authenticationManager.addStrategy((AuthenticationStratergy) jwtStratergy);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source= new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.exceptionHandling(ex->ex.disable())
                .sessionManagement(sess -> sess.disable())
                .securityContext(ctx -> ctx.disable())
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();  // Return the SecurityFilterChain
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticator).addPathPatterns("/**");
        registry.addInterceptor(login).addPathPatterns("/**");
    }
}

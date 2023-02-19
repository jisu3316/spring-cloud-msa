package com.example.userservice.security;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig  {

    private final UserService userService;
    private final Environment env;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String IP = "172.30.1.15";
        http.csrf().disable();
        http.authorizeRequests(request -> request.antMatchers("/actuator/**", "/error/**").permitAll());
        http.authorizeRequests(request ->
                request.antMatchers("/**").access("hasIpAddress('" + IP + "')"));
//                        .hasIpAddress("172.30.1.15"));
        http.addFilter(getAuthenticationFilter());

        http.headers().frameOptions().disable();
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return this.authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(), userService, env);
//        authenticationFilter.setAuthenticationManager(authenticationManager());
        return authenticationFilter;
    }


    //    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable();
//        http.authorizeRequests()
//                .antMatchers("/error").permitAll()
//                .antMatchers("/**")
//                .access("hasIpAddress('" + "172.30.1.15" + "')")
//                .and()
//                .addFilter(getAuthenticationFilter());
//        http.headers().frameOptions().disable();
//    }

    //    private AuthenticationFilter getAuthenticationFilter() throws Exception{
//        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(), userService, env);
////        authenticationFilter.setAuthenticationManager(authenticationManager());
//
//        return authenticationFilter;
//    }

    // select pwd from users where email ->
    // db_pwd(encrypted) == input_pwd(encrypted)
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
//    }


}

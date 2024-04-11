package com.luv2code.springboot.cruddemo.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setUsersByUsernameQuery(
            "select user_id, pw, active from members where user_id=?"
        );

        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
                "select user_id, role from roles where user_id=?"
        );

        return jdbcUserDetailsManager;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request -> 
            request.requestMatchers("/employees/**").hasRole("EMPLOYEE")           
                    .anyRequest().authenticated()
        )
        .formLogin(login -> 
            login.loginPage("/loginPage")
            .loginProcessingUrl("/authenticateTheUser")
            .permitAll()  
        )
        .logout(logout -> 
            logout.permitAll()
        );
        
        return httpSecurity.build();
    }

}

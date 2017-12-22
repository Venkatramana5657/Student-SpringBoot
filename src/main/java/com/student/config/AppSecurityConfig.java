package com.student.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	DataSource dataSource; 
	
	@Bean
	public AuthenticationSuccessHandler successHandler() {
		SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
		handler.setUseReferer(true);
	    return handler;
	}
	 
	 
    @Override
    protected void configure(HttpSecurity http) throws Exception {  
        http
            .authorizeRequests()
                .antMatchers("/rest/**","/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
            	.loginPage("/login")
                .usernameParameter("username").passwordParameter("password")
                .permitAll()
                .defaultSuccessUrl("/",true)
                .and()
            .logout()
                .permitAll()
                .logoutSuccessUrl("/")
             .and()
                .csrf().disable();
    } 

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource).usersByUsernameQuery("SELECT USERNAME,PASSWORD, ENABLED FROM USERS WHERE USERNAME=?").authoritiesByUsernameQuery("SELECT USERNAME, ROLE FROM USERS WHERE USERNAME=?");
    }
}
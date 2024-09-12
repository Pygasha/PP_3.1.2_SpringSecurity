package ru.kata.spring.boot_security.demo.configs;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.spring.boot_security.demo.services.MyUserDetailsService;

/


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final MyUserDetailsService personDetailsService;
    private final SuccessUserHandler successUserHandler;

    @Autowired
    public WebSecurityConfig(MyUserDetailsService personDetailsService, SuccessUserHandler successUserHandler) {
        this.personDetailsService = personDetailsService;
        this.successUserHandler = successUserHandler;
    }

    //Настраиваем аутентификацию
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(personDetailsService)
                .passwordEncoder(getPasswordEncoder());
    }

    // Конфигурируем сам Spring Security
    // Конфигурируем авторизацию
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()


//                .antMatchers("/admin","/admin/showAllUser","/admin/addNewUser","/admin/saveUser","/admin/edit"
//                        ,"/admin/delete").hasRole("ADMIN")
                .antMatchers("/admin/**").hasRole("ADMIN")

                //// СДЕЛАТЬ РИКВЕСТ МАППИНГ , ОСТАВИТЬ ТОЛЬКО АДМИН(АДРЕС) ЗДЕСЬ


                .antMatchers("/homePage","/auth/login","/auth/registration","/error").permitAll()
                .anyRequest().hasAnyRole("USER","ADMIN")

                .and()

                .formLogin().loginPage("/auth/login")
                .loginProcessingUrl("/process_login")
                .successHandler(successUserHandler)
                .failureUrl("/auth/login?error")

                .and()

                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/auth/login");
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
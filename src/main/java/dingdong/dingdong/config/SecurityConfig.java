package dingdong.dingdong.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CharacterEncodingFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final HttpLogoutSuccessHandler logoutSuccessHandler;
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
            .antMatchers("/h2-console/**", "/favicon.ico", "/api/v1/chat")
            .mvcMatchers("/node_modules/**", "/api/v1/chat")
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .mvcMatchers("/console", "/webjars/**/**", "/ws-stomp/**", "/api/v1/auth",
                "/api/v1/auth/send-sms", "/api/v1/auth/reissue", "/docs/**", "/api/v1/chat").permitAll()
            .anyRequest().hasAuthority("REGULAR");

        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        http.addFilterBefore(filter, CsrfFilter.class);

        http.cors()
            .disable();
        http.csrf()
            .disable();
        http.headers()
            .frameOptions().sameOrigin();

        http.formLogin()
            .disable();

        http.logout()
            .permitAll()
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessHandler(logoutSuccessHandler)
            .invalidateHttpSession(true);

        http.exceptionHandling()
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler);

        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.apply(new JwtSecurityConfig(tokenProvider));

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }


}



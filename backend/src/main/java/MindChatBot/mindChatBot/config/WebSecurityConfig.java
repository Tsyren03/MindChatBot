    package MindChatBot.mindChatBot.config;

    import MindChatBot.mindChatBot.service.UserDetailService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
    import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;

    @Configuration
    @EnableWebSecurity
    @RequiredArgsConstructor
    public class WebSecurityConfig {

        private final UserDetailService userDetailService;

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        // 정적 리소스 제외
        @Bean
        WebSecurityCustomizer configure() {
            return web -> web.ignoring().requestMatchers("/static/**");
        }

        // Spring Security 6 방식
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/login", "/signup").permitAll()
                            .requestMatchers("/api/chat").permitAll() // Allow unauthenticated access to /api/chat
                            .requestMatchers("/admin/**").hasRole("ADMIN") // ROLE_ADMIN만 접근
                            .requestMatchers("/user").hasAnyRole("USER", "ADMIN") // 사용자, 관리자 모두 접근
                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form
                            .loginPage("/login")
                            .usernameParameter("email") // 🔄 이메일을 username처럼 사용
                            .defaultSuccessUrl("/user", true)
                            .permitAll()
                    )
                    .logout(logout -> logout
                            .logoutSuccessUrl("/login")
                            .invalidateHttpSession(true)
                            .permitAll()
                    )
                    .build();
        }
    }

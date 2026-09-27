package com.verf_system.verfS.configuration;

import com.verf_system.verfS.database.repository.IFuncionarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFIlter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final IFuncionarioRepository funcionarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if (token != null) {
            var email = tokenService.validateToken(token);
            UserDetails funcionario= funcionarioRepository.findByEmail(email);

            var authentication = new UsernamePasswordAuthenticationToken(funcionario, null, funcionario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
    private String recoverToken(HttpServletRequest request){
        var header = request.getHeader("Authorization");
        if(header == null){
            return null;
        }
        return header.replace("Bearer ", "");
    }
}

package br.edu.ufcspa.sgfh.web.advice;

import br.edu.ufcspa.sgfh.web.dto.NavItem;
import br.edu.ufcspa.sgfh.web.security.AuthenticatedUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * Monta o menu a partir das próprias regras do SecurityConfig, então a tela só mostra
 * o que o papel do usuário pode acessar no servidor.
 */
@ControllerAdvice
public class NavigationAdvice {

    private static final List<NavItem> MODULES = List.of(
            new NavItem("Usuários", "/users", "Cadastro de usuários e do papel de cada um"),
            new NavItem("Papéis", "/roles", "Níveis de acesso usados nas permissões do sistema"),
            new NavItem("Integrações", "/integrations", "Consultas à ANVISA, ao RxNav e à BrasilAPI"));

    private final WebInvocationPrivilegeEvaluator privileges;

    public NavigationAdvice(WebInvocationPrivilegeEvaluator privileges) {
        this.privileges = privileges;
    }

    @ModelAttribute("currentUser")
    public AuthenticatedUser currentUser(@AuthenticationPrincipal AuthenticatedUser user) {
        return user;
    }

    @ModelAttribute("navItems")
    public List<NavItem> navItems() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return List.of();
        }
        return MODULES.stream()
                .filter(item -> privileges.isAllowed(item.path(), authentication))
                .toList();
    }
}

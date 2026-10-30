package br.edu.ufcspa.sgfh.web.security;

import br.edu.ufcspa.sgfh.application.service.RoleService;
import br.edu.ufcspa.sgfh.application.service.UserService;
import br.edu.ufcspa.sgfh.domain.model.Role;
import br.edu.ufcspa.sgfh.domain.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAccountDetailsService implements UserDetailsService {

    private final UserService userService;
    private final RoleService roleService;

    public UserAccountDetailsService(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        Role role = roleService.findById(user.roleId());
        return new AuthenticatedUser(
                user.id(),
                user.name(),
                user.email(),
                user.passwordHash(),
                role.name(),
                user.active());
    }
}

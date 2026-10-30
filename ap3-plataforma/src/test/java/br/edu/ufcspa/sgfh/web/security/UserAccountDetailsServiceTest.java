package br.edu.ufcspa.sgfh.web.security;

import br.edu.ufcspa.sgfh.application.service.RoleService;
import br.edu.ufcspa.sgfh.application.service.UserService;
import br.edu.ufcspa.sgfh.domain.model.Role;
import br.edu.ufcspa.sgfh.domain.model.User;
import br.edu.ufcspa.sgfh.testdouble.FakePasswordHasher;
import br.edu.ufcspa.sgfh.testdouble.InMemoryRoleRepository;
import br.edu.ufcspa.sgfh.testdouble.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserAccountDetailsServiceTest {

    private UserService userService;
    private String roleId;
    private UserAccountDetailsService detailsService;

    @BeforeEach
    void setUp() {
        InMemoryUserRepository users = new InMemoryUserRepository();
        InMemoryRoleRepository roles = new InMemoryRoleRepository();
        userService = new UserService(users, roles, new FakePasswordHasher());
        roleId = roles.save(new Role(null, Role.PHARMACIST, "desc", null)).id();
        detailsService = new UserAccountDetailsService(userService, new RoleService(roles, users));
    }

    @Test
    @DisplayName("Carrega o usuario pelo e-mail com o papel como autoridade")
    void loadsUserWithRoleAuthority() {
        userService.create(new User(null, "Maria", "maria@sgfh.local", null, roleId, true, null), "senha123");

        UserDetails details = detailsService.loadUserByUsername("MARIA@sgfh.local");

        assertEquals("maria@sgfh.local", details.getUsername());
        assertEquals("hashed:senha123", details.getPassword());
        assertTrue(details.isEnabled());
        assertEquals(List.of("ROLE_FARMACEUTICO"),
                details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }

    @Test
    @DisplayName("Usuario inativo e carregado como desabilitado")
    void inactiveUserIsDisabled() {
        userService.create(new User(null, "Maria", "maria@sgfh.local", null, roleId, false, null), "senha123");

        assertFalse(detailsService.loadUserByUsername("maria@sgfh.local").isEnabled());
    }

    @Test
    @DisplayName("E-mail desconhecido gera UsernameNotFoundException")
    void unknownEmailFails() {
        assertThrows(UsernameNotFoundException.class,
                () -> detailsService.loadUserByUsername("ninguem@sgfh.local"));
    }
}

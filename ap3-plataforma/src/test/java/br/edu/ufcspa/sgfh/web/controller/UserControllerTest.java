package br.edu.ufcspa.sgfh.web.controller;

import br.edu.ufcspa.sgfh.application.port.out.PasswordHasher;
import br.edu.ufcspa.sgfh.application.service.RoleService;
import br.edu.ufcspa.sgfh.application.service.UserService;
import br.edu.ufcspa.sgfh.domain.exception.BusinessException;
import br.edu.ufcspa.sgfh.domain.model.Role;
import br.edu.ufcspa.sgfh.domain.model.User;
import br.edu.ufcspa.sgfh.web.mapper.RoleWebMapper;
import br.edu.ufcspa.sgfh.web.mapper.UserWebMapper;
import br.edu.ufcspa.sgfh.web.security.AuthenticatedUser;
import br.edu.ufcspa.sgfh.web.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, UserWebMapper.class, RoleWebMapper.class})
class UserControllerTest {

    private static final RequestPostProcessor ADMIN = user(new AuthenticatedUser(
            "admin-id", "Admin", "admin@sgfh.local", "hash", Role.ADMINISTRATOR, true));

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private PasswordHasher passwordHasher;

    @BeforeEach
    void setUp() {
        when(roleService.findAll()).thenReturn(List.of(
                new Role("r1", "FARMACEUTICO", "Farmacêutico", null),
                new Role("r2", "TECNICO", "Técnico", null)));
        when(userService.findAll()).thenReturn(List.of(
                new User("u1", "José Conceição", "jose@sgfh.local", "hash", "r1", true, null),
                new User("u2", "Maria Silva", "maria@sgfh.local", "hash", "r2", false, null)));
    }

    @Test
    @DisplayName("Busca filtra a tabela de usuarios ignorando acentos")
    void searchFiltersTable() throws Exception {
        mvc.perform(get("/users").param("q", "jose").with(ADMIN))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("jose@sgfh.local"),
                        not(containsString("maria@sgfh.local")))));
    }

    @Test
    @DisplayName("Edicao carrega os dados do usuario no formulario")
    void editFillsForm() throws Exception {
        when(userService.findById("u2")).thenReturn(
                new User("u2", "Maria Silva", "maria@sgfh.local", "hash", "r2", false, null));

        mvc.perform(get("/users/u2/edit").with(ADMIN))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("value=\"Maria Silva\""),
                        containsString("value=\"r2\" selected=\"selected\""),
                        containsString("Salvar alterações"))));
    }

    @Test
    @DisplayName("Cadastro valido redireciona com aviso de sucesso")
    void validSaveRedirectsWithNotice() throws Exception {
        mvc.perform(post("/users").with(ADMIN).with(csrf())
                        .param("name", "Ana Souza")
                        .param("email", "ana@sgfh.local")
                        .param("password", "segura123")
                        .param("roleId", "r1")
                        .param("active", "true"))
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attribute("notice", "Usuário Ana Souza cadastrado."));
        verify(userService).create(any(User.class), eq("segura123"));
    }

    @Test
    @DisplayName("Campo invalido volta marcado, com o resumo de erro e uma mensagem por campo")
    void invalidSaveHighlightsField() throws Exception {
        mvc.perform(post("/users").with(ADMIN).with(csrf())
                        .param("id", "")
                        .param("name", "")
                        .param("email", "ana@sgfh.local")
                        .param("roleId", "r1"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("Não foi possível salvar o usuário."),
                        containsString("aria-invalid=\"true\""),
                        containsString("O nome é obrigatório"),
                        not(containsString("O nome deve ter entre")))))
                .andExpect(content().string(allOf(
                        containsString("Novo usuário"),
                        not(containsString("Editar usuário")))));
    }

    @Test
    @DisplayName("Exclusao devolve a tabela e o aviso fora da area trocada (htmx out-of-band)")
    void deleteReturnsTableWithToast() throws Exception {
        mvc.perform(delete("/users/u2").with(ADMIN).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("id=\"table-area\""),
                        containsString("hx-swap-oob=\"beforeend:#toast-region\""),
                        containsString("Usuário excluído."))));
        verify(userService).deleteById("u2", "admin-id");
    }

    @Test
    @DisplayName("Exclusao recusada pela regra de negocio vira aviso de erro")
    void refusedDeleteReturnsErrorToast() throws Exception {
        doThrow(new BusinessException("Você não pode excluir o próprio usuário"))
                .when(userService).deleteById("admin-id", "admin-id");

        mvc.perform(delete("/users/admin-id").with(ADMIN).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("toast-error"),
                        containsString("Você não pode excluir o próprio usuário"))));
    }
}

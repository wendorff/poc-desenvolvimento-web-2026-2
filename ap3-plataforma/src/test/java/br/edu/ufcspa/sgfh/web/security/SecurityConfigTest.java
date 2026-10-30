package br.edu.ufcspa.sgfh.web.security;

import br.edu.ufcspa.sgfh.application.port.out.PasswordHasher;
import br.edu.ufcspa.sgfh.application.service.ReferenceDataService;
import br.edu.ufcspa.sgfh.application.service.RoleService;
import br.edu.ufcspa.sgfh.application.service.UserService;
import br.edu.ufcspa.sgfh.domain.model.Role;
import br.edu.ufcspa.sgfh.web.mapper.RoleWebMapper;
import br.edu.ufcspa.sgfh.web.mapper.UserWebMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({SecurityConfig.class, UserWebMapper.class, RoleWebMapper.class})
class SecurityConfigTest {

    private static final String SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private ReferenceDataService referenceDataService;

    @MockitoBean
    private PasswordHasher passwordHasher;

    private static RequestPostProcessor as(String roleName) {
        return user(new AuthenticatedUser("id", "Demo", "demo@sgfh.local", "hash", roleName, true));
    }

    @Test
    @DisplayName("Visitante e redirecionado para o login")
    void anonymousIsRedirectedToLogin() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Pagina de login e publica")
    void loginPageIsPublic() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name=\"email\"")));
    }

    @Test
    @DisplayName("Apenas ADMINISTRADOR acessa usuarios e papeis")
    void onlyAdministratorManagesUsersAndRoles() throws Exception {
        mvc.perform(get("/users").with(as(Role.ADMINISTRATOR))).andExpect(status().isOk());
        mvc.perform(get("/roles").with(as(Role.ADMINISTRATOR))).andExpect(status().isOk());
        mvc.perform(get("/users").with(as(Role.PHARMACIST))).andExpect(status().isForbidden());
        mvc.perform(get("/roles").with(as(Role.TECHNICIAN))).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Integracoes liberadas para ADMINISTRADOR e FARMACEUTICO")
    void integrationsForAdministratorAndPharmacist() throws Exception {
        mvc.perform(get("/integrations").with(as(Role.ADMINISTRATOR))).andExpect(status().isOk());
        mvc.perform(get("/integrations").with(as(Role.PHARMACIST))).andExpect(status().isOk());
        mvc.perform(get("/integrations").with(as(Role.AUDITOR))).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Menu mostra apenas os modulos que o papel pode acessar")
    void menuFollowsAccessRules() throws Exception {
        mvc.perform(get("/").with(as(Role.ADMINISTRATOR)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/users\"")));
        mvc.perform(get("/").with(as(Role.TECHNICIAN)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("href=\"/users\""))))
                .andExpect(content().string(not(containsString("href=\"/integrations\""))));
    }

    @Test
    @DisplayName("Formulario sem token CSRF e recusado")
    void postWithoutCsrfIsRejected() throws Exception {
        mvc.perform(post("/roles").param("name", "NOVO").with(as(Role.ADMINISTRATOR)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Arquivos do frontend sao publicos, para a tela de login carregar CSS e JS")
    void assetsArePublic() throws Exception {
        mvc.perform(get("/assets/app.css"))
                .andExpect(header().doesNotExist("Location"));
    }

    @Test
    @DisplayName("Toda resposta envia Content-Security-Policy sem codigo inline")
    void responsesCarryContentSecurityPolicy() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(header().string("Content-Security-Policy", containsString("script-src 'self'")))
                .andExpect(header().string("Content-Security-Policy", containsString("style-src 'self'")))
                .andExpect(header().string("Content-Security-Policy", not(containsString("unsafe-inline"))));
    }

    @Test
    @DisplayName("Requisicao htmx sem sessao recebe HX-Redirect para o login em vez da tela de login")
    void htmxRequestWithoutSessionIsRedirectedByHeader() throws Exception {
        mvc.perform(get("/integrations/cnpj").param("cnpj", "19131243000197").header("HX-Request", "true"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("HX-Redirect", "/login"))
                .andExpect(request().sessionAttributeDoesNotExist(SAVED_REQUEST));
    }

    @Test
    @DisplayName("Navegacao com hx-boost sem sessao volta para a mesma pagina depois do login")
    void boostedNavigationWithoutSessionIsResumedAfterLogin() throws Exception {
        mvc.perform(get("/roles").header("HX-Request", "true").header("HX-Boosted", "true"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("HX-Redirect", "/login"))
                .andExpect(request().sessionAttribute(SAVED_REQUEST, notNullValue()));
    }
}

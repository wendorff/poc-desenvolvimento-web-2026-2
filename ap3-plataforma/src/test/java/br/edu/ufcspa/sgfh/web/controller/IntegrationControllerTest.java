package br.edu.ufcspa.sgfh.web.controller;

import br.edu.ufcspa.sgfh.application.port.out.PasswordHasher;
import br.edu.ufcspa.sgfh.application.service.ReferenceDataService;
import br.edu.ufcspa.sgfh.domain.model.DrugConcept;
import br.edu.ufcspa.sgfh.domain.model.Role;
import br.edu.ufcspa.sgfh.web.security.AuthenticatedUser;
import br.edu.ufcspa.sgfh.web.security.SecurityConfig;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IntegrationController.class)
@Import(SecurityConfig.class)
class IntegrationControllerTest {

    private static final RequestPostProcessor PHARMACIST = user(new AuthenticatedUser(
            "id", "Farmacêutica", "farma@sgfh.local", "hash", Role.PHARMACIST, true));

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ReferenceDataService service;

    @MockitoBean
    private PasswordHasher passwordHasher;

    @Test
    @DisplayName("Tipo de termo conhecido ganha a dica com o significado")
    void knownTermTypeHasTooltip() throws Exception {
        when(service.findDrugConcepts("amoxicillin")).thenReturn(List.of(
                new DrugConcept("723", "amoxicillin", "IN")));

        mvc.perform(get("/integrations/rxnav").param("drugName", "amoxicillin").with(PHARMACIST))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("data-tooltip=\"tty-723\""),
                        containsString("aria-describedby=\"tty-723\""),
                        containsString("Princípio ativo"),
                        containsString("Ex.: Fluoxetine"))));
    }

    @Test
    @DisplayName("Tipo de termo desconhecido aparece sem dica")
    void unknownTermTypeIsPlain() throws Exception {
        when(service.findDrugConcepts("novo")).thenReturn(List.of(
                new DrugConcept("999", "novo conceito", "XYZ")));

        mvc.perform(get("/integrations/rxnav").param("drugName", "novo").with(PHARMACIST))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString(">XYZ<"),
                        not(containsString("data-tooltip")))));
    }
}

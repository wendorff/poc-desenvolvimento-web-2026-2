package br.edu.ufcspa.sgfh.application.service;

import br.edu.ufcspa.sgfh.domain.exception.BusinessException;
import br.edu.ufcspa.sgfh.domain.exception.NotFoundException;
import br.edu.ufcspa.sgfh.domain.model.CompanyRegistration;
import br.edu.ufcspa.sgfh.domain.model.DrugConcept;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReferenceDataServiceTest {

    private final ReferenceDataService service = new ReferenceDataService(
            name -> List.of(),
            term -> List.of(new DrugConcept("723", term, "IN")),
            cnpj -> Optional.empty());

    @Test
    @DisplayName("Recusa busca com menos de 3 caracteres")
    void rejectsShortTerm() {
        BusinessException error = assertThrows(BusinessException.class,
                () -> service.searchLeaflets(" am "));

        assertTrue(error.getMessage().contains("ao menos 3"));
    }

    @Test
    @DisplayName("Remove espacos do termo antes de consultar")
    void trimsTerm() {
        assertEquals("amoxicilina", service.findDrugConcepts("  amoxicilina ").get(0).name());
    }

    @Test
    @DisplayName("Recusa CNPJ invalido sem consultar a BrasilAPI")
    void rejectsInvalidCnpj() {
        assertThrows(BusinessException.class, () -> service.findCompany("123"));
    }

    @Test
    @DisplayName("CNPJ valido sem cadastro na Receita gera NotFoundException")
    void unknownCnpjIsNotFound() {
        NotFoundException error = assertThrows(NotFoundException.class,
                () -> service.findCompany("19.131.243/0001-97"));

        assertTrue(error.getMessage().contains("19.131.243/0001-97"));
    }

    @Test
    @DisplayName("Empresa com situacao diferente de ATIVA nao pode fornecer")
    void onlyActiveCompaniesCanSupply() {
        CompanyRegistration suspended = new CompanyRegistration(null, "X", null, "SUSPENSA", null, null);

        assertFalse(suspended.isActive());
    }
}

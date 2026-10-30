package br.edu.ufcspa.sgfh.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RxNormTermTypeTest {

    @Test
    @DisplayName("Reconhece o tipo de termo devolvido pelo RxNav")
    void findsKnownCode() {
        RxNormTermType type = RxNormTermType.fromCode("SCDG").orElseThrow();

        assertEquals("Grupo clínico", type.label());
        assertEquals("Semantic Clinical Drug Group", type.officialName());
    }

    @Test
    @DisplayName("Tipo desconhecido ou ausente nao tem explicacao")
    void unknownCodeIsEmpty() {
        assertTrue(RxNormTermType.fromCode("XYZ").isEmpty());
        assertTrue(RxNormTermType.fromCode("scdg").isEmpty());
        assertTrue(RxNormTermType.fromCode(null).isEmpty());
    }

    @Test
    @DisplayName("Todo tipo tem nome, nome oficial, descricao e exemplo")
    void everyTypeIsDocumented() {
        for (RxNormTermType type : RxNormTermType.values()) {
            assertFalse(type.label().isBlank(), type.name());
            assertFalse(type.officialName().isBlank(), type.name());
            assertFalse(type.description().isBlank(), type.name());
            assertFalse(type.example().isBlank(), type.name());
        }
    }

    @Test
    @DisplayName("Conceito expoe o significado do proprio tipo")
    void conceptExposesItsTermType() {
        assertEquals(RxNormTermType.IN, new DrugConcept("723", "amoxicillin", "IN").termTypeDetails().orElseThrow());
        assertTrue(new DrugConcept("1", "x", "NOVO").termTypeDetails().isEmpty());
    }
}

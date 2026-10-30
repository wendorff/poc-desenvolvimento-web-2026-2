package br.edu.ufcspa.sgfh.web.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserViewTest {

    private final UserView user = new UserView("1", "José Conceição", "jose@sgfh.local", "FARMACEUTICO", true);

    @Test
    @DisplayName("Busca ignora maiusculas e acentos no nome")
    void matchesNameIgnoringCaseAndAccents() {
        assertTrue(user.matches("jose"));
        assertTrue(user.matches("CONCEICAO"));
        assertTrue(user.matches("  Conceição "));
    }

    @Test
    @DisplayName("Busca tambem encontra pelo e-mail e pelo papel")
    void matchesEmailAndRole() {
        assertTrue(user.matches("@sgfh.local"));
        assertTrue(user.matches("farmac"));
    }

    @Test
    @DisplayName("Busca vazia mostra todos os usuarios")
    void blankTermMatchesEveryone() {
        assertTrue(user.matches(""));
        assertTrue(user.matches("   "));
        assertTrue(user.matches(null));
    }

    @Test
    @DisplayName("Busca sem correspondencia nao encontra o usuario")
    void unrelatedTermDoesNotMatch() {
        assertFalse(user.matches("maria"));
    }
}

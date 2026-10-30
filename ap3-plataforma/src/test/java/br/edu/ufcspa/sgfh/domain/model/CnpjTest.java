package br.edu.ufcspa.sgfh.domain.model;

import br.edu.ufcspa.sgfh.domain.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CnpjTest {

    @Test
    @DisplayName("Aceita CNPJ com mascara e guarda apenas os caracteres")
    void parsesFormattedCnpj() {
        Cnpj cnpj = Cnpj.parse("19.131.243/0001-97");

        assertEquals("19131243000197", cnpj.value());
        assertEquals("19.131.243/0001-97", cnpj.formatted());
    }

    @Test
    @DisplayName("Aceita o CNPJ alfanumerico do exemplo da Receita Federal")
    void parsesAlphanumericCnpj() {
        Cnpj cnpj = Cnpj.parse("12.abc.345/01de-35");

        assertEquals("12ABC34501DE35", cnpj.value());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"19131243000198", "1913124300019", "00000000000000", "12ABC34501DE36", "12ABC34501DEAB"})
    @DisplayName("Recusa CNPJ vazio, com tamanho errado, repetido ou com digito verificador errado")
    void rejectsInvalidCnpj(String input) {
        BusinessException error = assertThrows(BusinessException.class, () -> Cnpj.parse(input));

        assertEquals("CNPJ inválido", error.getMessage());
    }
}

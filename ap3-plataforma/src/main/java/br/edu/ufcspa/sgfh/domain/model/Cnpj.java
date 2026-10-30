package br.edu.ufcspa.sgfh.domain.model;

import br.edu.ufcspa.sgfh.domain.exception.BusinessException;

/**
 * CNPJ validado pelos dígitos verificadores. Aceita o formato numérico e o alfanumérico
 * da Receita Federal (IN RFB 2.229/2024): os 12 primeiros caracteres podem ter letras e
 * cada caractere vale seu código ASCII menos 48, o que mantém o cálculo antigo para dígitos.
 */
public record Cnpj(String value) {

    private static final int[] FIRST_WEIGHTS = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] SECOND_WEIGHTS = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    public Cnpj {
        if (value == null || !value.matches("[0-9A-Z]{12}[0-9]{2}") || hasCheckDigitError(value)) {
            throw new BusinessException("CNPJ inválido");
        }
    }

    public static Cnpj parse(String input) {
        String cleaned = input == null ? "" : input.toUpperCase().replaceAll("[^0-9A-Z]", "");
        return new Cnpj(cleaned);
    }

    public String formatted() {
        return value.substring(0, 2) + "." + value.substring(2, 5) + "." + value.substring(5, 8)
                + "/" + value.substring(8, 12) + "-" + value.substring(12);
    }

    private static boolean hasCheckDigitError(String value) {
        if (value.chars().distinct().count() == 1) {
            return true;
        }
        int first = checkDigit(value, FIRST_WEIGHTS);
        int second = checkDigit(value, SECOND_WEIGHTS);
        return value.charAt(12) - '0' != first || value.charAt(13) - '0' != second;
    }

    private static int checkDigit(String value, int[] weights) {
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += (value.charAt(i) - '0') * weights[i];
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}

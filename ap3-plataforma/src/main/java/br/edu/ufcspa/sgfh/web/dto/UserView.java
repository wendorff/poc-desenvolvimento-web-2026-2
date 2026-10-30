package br.edu.ufcspa.sgfh.web.dto;

import java.text.Normalizer;
import java.util.Locale;

public record UserView(
        String id,
        String name,
        String email,
        String roleName,
        boolean active
) {
    /**
     * Busca da listagem: compara nome, e-mail e papel sem diferenciar maiúsculas nem acentos,
     * então "jose" encontra "José".
     */
    public boolean matches(String term) {
        String needle = normalize(term);
        return needle.isEmpty()
                || normalize(name).contains(needle)
                || normalize(email).contains(needle)
                || normalize(roleName).contains(needle);
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .strip();
    }
}

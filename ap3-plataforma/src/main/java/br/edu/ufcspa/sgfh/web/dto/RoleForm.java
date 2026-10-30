package br.edu.ufcspa.sgfh.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoleForm(
        String id,

        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 40, message = "O nome deve ter entre 3 e 40 caracteres")
        String name,

        @Size(max = 160, message = "A descrição deve ter no máximo 160 caracteres")
        String description
) {
    /**
     * Campo enviado em branco vira null: o formulário de cadastro não volta como edição
     * (id ""), e o @Size não repete o erro do @NotBlank no nome vazio.
     */
    public RoleForm {
        id = blankToNull(id);
        name = blankToNull(name);
    }

    public static RoleForm empty() {
        return new RoleForm(null, null, null);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}

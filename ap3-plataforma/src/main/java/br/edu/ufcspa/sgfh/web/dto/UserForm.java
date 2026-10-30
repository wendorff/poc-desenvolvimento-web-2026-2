package br.edu.ufcspa.sgfh.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserForm(
        String id,

        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 120, message = "O nome deve ter entre 3 e 120 caracteres")
        String name,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Informe um e-mail válido")
        String email,

        String password,

        @NotBlank(message = "Selecione um papel")
        String roleId,

        Boolean active
) {
    /**
     * Campo enviado em branco vira null: o formulário de cadastro não volta como edição
     * (id ""), e o @Size não repete o erro do @NotBlank no nome vazio.
     */
    public UserForm {
        id = blankToNull(id);
        name = blankToNull(name);
    }

    public static UserForm empty() {
        return new UserForm(null, null, null, null, null, Boolean.TRUE);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    public boolean isActive() {
        return active != null && active;
    }
}

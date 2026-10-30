package br.edu.ufcspa.sgfh.web.dto;

public record UserView(
        String id,
        String name,
        String email,
        String roleName,
        boolean active
) {
}

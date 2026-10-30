package br.edu.ufcspa.sgfh.domain.model;

public record CompanyRegistration(
        Cnpj cnpj,
        String legalName,
        String tradeName,
        String registrationStatus,
        String city,
        String state
) {
    public boolean isActive() {
        return "ATIVA".equalsIgnoreCase(registrationStatus);
    }
}

package br.edu.ufcspa.sgfh.domain.model;

public record MedicineLeaflet(
        String productName,
        String company,
        String registrationNumber,
        String processNumber,
        String professionalLeafletUrl,
        String patientLeafletUrl
) {
}

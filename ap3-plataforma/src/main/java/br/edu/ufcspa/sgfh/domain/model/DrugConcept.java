package br.edu.ufcspa.sgfh.domain.model;

public record DrugConcept(
        String rxcui,
        String name,
        String termType
) {
}

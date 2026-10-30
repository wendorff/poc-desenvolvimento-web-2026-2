package br.edu.ufcspa.sgfh.domain.model;

import java.util.Optional;

public record DrugConcept(
        String rxcui,
        String name,
        String termType
) {
    /** Significado do tipo de termo; vazio se o RxNorm devolver um tipo que não está mapeado. */
    public Optional<RxNormTermType> termTypeDetails() {
        return RxNormTermType.fromCode(termType);
    }
}

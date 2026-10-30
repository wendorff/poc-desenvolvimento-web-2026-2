package br.edu.ufcspa.sgfh.application.port.out;

import br.edu.ufcspa.sgfh.domain.model.DrugConcept;

import java.util.List;

public interface DrugTerminology {

    List<DrugConcept> findConcepts(String term);
}

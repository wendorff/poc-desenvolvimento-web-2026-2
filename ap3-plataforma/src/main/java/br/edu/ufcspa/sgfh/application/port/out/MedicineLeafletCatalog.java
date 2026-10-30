package br.edu.ufcspa.sgfh.application.port.out;

import br.edu.ufcspa.sgfh.domain.model.MedicineLeaflet;

import java.util.List;

public interface MedicineLeafletCatalog {

    List<MedicineLeaflet> searchByProductName(String productName);
}

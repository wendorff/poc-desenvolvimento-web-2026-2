package br.edu.ufcspa.sgfh.application.service;

import br.edu.ufcspa.sgfh.application.port.out.CompanyRegistry;
import br.edu.ufcspa.sgfh.application.port.out.DrugTerminology;
import br.edu.ufcspa.sgfh.application.port.out.MedicineLeafletCatalog;
import br.edu.ufcspa.sgfh.domain.exception.BusinessException;
import br.edu.ufcspa.sgfh.domain.exception.NotFoundException;
import br.edu.ufcspa.sgfh.domain.model.Cnpj;
import br.edu.ufcspa.sgfh.domain.model.CompanyRegistration;
import br.edu.ufcspa.sgfh.domain.model.DrugConcept;
import br.edu.ufcspa.sgfh.domain.model.MedicineLeaflet;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReferenceDataService {

    private static final int MINIMUM_TERM_LENGTH = 3;

    private final MedicineLeafletCatalog leaflets;
    private final DrugTerminology terminology;
    private final CompanyRegistry companies;

    public ReferenceDataService(MedicineLeafletCatalog leaflets,
                                DrugTerminology terminology,
                                CompanyRegistry companies) {
        this.leaflets = leaflets;
        this.terminology = terminology;
        this.companies = companies;
    }

    public List<MedicineLeaflet> searchLeaflets(String productName) {
        return leaflets.searchByProductName(requireTerm(productName));
    }

    public List<DrugConcept> findDrugConcepts(String name) {
        return terminology.findConcepts(requireTerm(name));
    }

    public CompanyRegistration findCompany(String cnpj) {
        Cnpj parsed = Cnpj.parse(cnpj);
        return companies.findByCnpj(parsed)
                .orElseThrow(() -> new NotFoundException("CNPJ " + parsed.formatted()
                        + " não encontrado na Receita Federal"));
    }

    private String requireTerm(String term) {
        String trimmed = term == null ? "" : term.trim();
        if (trimmed.length() < MINIMUM_TERM_LENGTH) {
            throw new BusinessException("Informe ao menos " + MINIMUM_TERM_LENGTH + " caracteres para a busca");
        }
        return trimmed;
    }
}

package br.edu.ufcspa.sgfh.application.port.out;

import br.edu.ufcspa.sgfh.domain.model.Cnpj;
import br.edu.ufcspa.sgfh.domain.model.CompanyRegistration;

import java.util.Optional;

public interface CompanyRegistry {

    Optional<CompanyRegistration> findByCnpj(Cnpj cnpj);
}

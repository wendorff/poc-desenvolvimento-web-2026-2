package br.edu.ufcspa.sgfh.infrastructure.integration;

import br.edu.ufcspa.sgfh.domain.exception.ExternalServiceException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.function.Supplier;

final class ExternalCall {

    private ExternalCall() {
    }

    static <T> T run(String service, Supplier<T> call) {
        try {
            return call.get();
        } catch (RestClientResponseException e) {
            throw new ExternalServiceException(service + " recusou a consulta (HTTP "
                    + e.getStatusCode().value() + ")", e);
        } catch (RestClientException e) {
            throw new ExternalServiceException(service + " está indisponível no momento", e);
        }
    }
}

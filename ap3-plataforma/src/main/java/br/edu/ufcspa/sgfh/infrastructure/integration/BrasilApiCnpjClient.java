package br.edu.ufcspa.sgfh.infrastructure.integration;

import br.edu.ufcspa.sgfh.application.port.out.CompanyRegistry;
import br.edu.ufcspa.sgfh.domain.model.Cnpj;
import br.edu.ufcspa.sgfh.domain.model.CompanyRegistration;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

public class BrasilApiCnpjClient implements CompanyRegistry {

    private static final String SERVICE = "BrasilAPI";

    private final RestClient http;

    public BrasilApiCnpjClient(RestClient.Builder builder, String baseUrl) {
        this.http = builder.baseUrl(baseUrl).build();
    }

    @Override
    public Optional<CompanyRegistration> findByCnpj(Cnpj cnpj) {
        return ExternalCall.run(SERVICE, () -> {
            try {
                CnpjResponse response = http.get()
                        .uri("/api/cnpj/v1/{cnpj}", cnpj.value())
                        .retrieve()
                        .body(CnpjResponse.class);
                return Optional.ofNullable(response).map(r -> toDomain(cnpj, r));
            } catch (HttpClientErrorException.NotFound e) {
                return Optional.empty();
            }
        });
    }

    private CompanyRegistration toDomain(Cnpj cnpj, CnpjResponse response) {
        return new CompanyRegistration(
                cnpj,
                response.legalName(),
                response.tradeName(),
                response.registrationStatus(),
                response.city(),
                response.state());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CnpjResponse(
            @JsonProperty("razao_social") String legalName,
            @JsonProperty("nome_fantasia") String tradeName,
            @JsonProperty("descricao_situacao_cadastral") String registrationStatus,
            @JsonProperty("municipio") String city,
            @JsonProperty("uf") String state
    ) {
    }
}

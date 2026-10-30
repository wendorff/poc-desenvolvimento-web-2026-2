package br.edu.ufcspa.sgfh.infrastructure.integration;

import br.edu.ufcspa.sgfh.application.port.out.DrugTerminology;
import br.edu.ufcspa.sgfh.domain.model.DrugConcept;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RxNavClient implements DrugTerminology {

    private static final String SERVICE = "RxNav";
    private static final int MAX_CONCEPTS = 5;

    private final RestClient http;

    public RxNavClient(RestClient.Builder builder, String baseUrl) {
        this.http = builder.baseUrl(baseUrl).build();
    }

    /**
     * A busca aproximada aceita grafias diferentes (inclusive em português) e devolve
     * os RxCUIs candidatos; o nome canônico de cada um vem do endpoint de propriedades.
     */
    @Override
    public List<DrugConcept> findConcepts(String term) {
        return ExternalCall.run(SERVICE, () -> candidateIds(term).stream()
                .limit(MAX_CONCEPTS)
                .map(this::findConcept)
                .flatMap(Optional::stream)
                .toList());
    }

    private List<String> candidateIds(String term) {
        ApproximateResponse response = http.get()
                .uri(uri -> uri.path("/REST/approximateTerm.json")
                        .queryParam("term", "{term}")
                        .queryParam("maxEntries", 10)
                        .queryParam("option", 1)
                        .build(term))
                .retrieve()
                .body(ApproximateResponse.class);
        if (response == null || response.approximateGroup() == null
                || response.approximateGroup().candidate() == null) {
            return List.of();
        }
        return response.approximateGroup().candidate().stream()
                .map(Candidate::rxcui)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private Optional<DrugConcept> findConcept(String rxcui) {
        PropertiesResponse response = http.get()
                .uri("/REST/rxcui/{rxcui}/properties.json", rxcui)
                .retrieve()
                .body(PropertiesResponse.class);
        return Optional.ofNullable(response)
                .map(PropertiesResponse::properties)
                .map(p -> new DrugConcept(p.rxcui(), p.name(), p.tty()));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ApproximateResponse(ApproximateGroup approximateGroup) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ApproximateGroup(List<Candidate> candidate) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Candidate(String rxcui) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PropertiesResponse(Properties properties) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Properties(String rxcui, String name, String tty) {
    }
}

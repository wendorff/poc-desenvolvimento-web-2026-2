package br.edu.ufcspa.sgfh.infrastructure.integration;

import br.edu.ufcspa.sgfh.application.port.out.MedicineLeafletCatalog;
import br.edu.ufcspa.sgfh.domain.model.MedicineLeaflet;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class AnvisaBularioClient implements MedicineLeafletCatalog {

    private static final String SERVICE = "ANVISA Bulário";
    private static final int PAGE_SIZE = 10;
    private static final String LEAFLET_PATH = "/api/consulta/medicamentos/arquivo/bula/parecer/";

    private final RestClient http;
    private final String baseUrl;

    public AnvisaBularioClient(RestClient.Builder builder, String baseUrl) {
        // A API pública do Bulário identifica o acesso anônimo com o valor fixo "Guest"
        this.http = builder.baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Guest")
                .build();
        this.baseUrl = baseUrl;
    }

    @Override
    public List<MedicineLeaflet> searchByProductName(String productName) {
        BularioPage page = ExternalCall.run(SERVICE, () -> http.get()
                .uri(uri -> uri.path("/api/consulta/bulario")
                        .queryParam("count", PAGE_SIZE)
                        .queryParam("page", 1)
                        .queryParam("filter[nomeProduto]", "{name}")
                        .build(productName))
                .retrieve()
                .body(BularioPage.class));
        if (page == null || page.content() == null) {
            return List.of();
        }
        return page.content().stream().map(this::toDomain).toList();
    }

    private MedicineLeaflet toDomain(BularioItem item) {
        return new MedicineLeaflet(
                item.nomeProduto(),
                item.razaoSocial(),
                item.numeroRegistro(),
                item.numProcesso(),
                leafletUrl(item.idBulaProfissionalProtegido()),
                leafletUrl(item.idBulaPacienteProtegido()));
    }

    private String leafletUrl(String protectedId) {
        if (protectedId == null || protectedId.isBlank()) {
            return null;
        }
        return baseUrl + LEAFLET_PATH
                + UriUtils.encodePathSegment(protectedId, StandardCharsets.UTF_8) + "/?Authorization=";
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BularioPage(List<BularioItem> content) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BularioItem(
            String nomeProduto,
            String razaoSocial,
            String numeroRegistro,
            @JsonAlias("processo") String numProcesso,
            String idBulaProfissionalProtegido,
            String idBulaPacienteProtegido
    ) {
    }
}

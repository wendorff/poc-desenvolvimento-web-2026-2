package br.edu.ufcspa.sgfh.infrastructure.integration;

import br.edu.ufcspa.sgfh.domain.exception.ExternalServiceException;
import br.edu.ufcspa.sgfh.domain.model.Cnpj;
import br.edu.ufcspa.sgfh.domain.model.CompanyRegistration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BrasilApiCnpjClientTest {

    private static final Cnpj CNPJ = Cnpj.parse("19131243000197");

    private MockRestServiceServer server;
    private BrasilApiCnpjClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new BrasilApiCnpjClient(builder, "https://brasilapi.test");
    }

    @Test
    @DisplayName("Mapeia os dados cadastrais da empresa")
    void mapsCompanyRegistration() {
        server.expect(requestTo("https://brasilapi.test/api/cnpj/v1/19131243000197"))
                .andRespond(withSuccess("""
                        { "cnpj": "19131243000197",
                          "razao_social": "OPEN KNOWLEDGE BRASIL",
                          "nome_fantasia": "REDE PELO CONHECIMENTO LIVRE",
                          "descricao_situacao_cadastral": "ATIVA",
                          "municipio": "SAO PAULO",
                          "uf": "SP",
                          "qsa": [] }
                        """, MediaType.APPLICATION_JSON));

        CompanyRegistration company = client.findByCnpj(CNPJ).orElseThrow();

        assertEquals("OPEN KNOWLEDGE BRASIL", company.legalName());
        assertEquals("REDE PELO CONHECIMENTO LIVRE", company.tradeName());
        assertEquals("SAO PAULO", company.city());
        assertEquals("SP", company.state());
        assertTrue(company.isActive());
        server.verify();
    }

    @Test
    @DisplayName("CNPJ inexistente devolve vazio")
    void notFoundReturnsEmpty() {
        server.expect(requestTo("https://brasilapi.test/api/cnpj/v1/19131243000197"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\":\"CNPJ não encontrado\",\"type\":\"not_found\"}"));

        assertFalse(client.findByCnpj(CNPJ).isPresent());
    }

    @Test
    @DisplayName("Indisponibilidade vira ExternalServiceException")
    void serviceUnavailableBecomesExternalServiceException() {
        server.expect(requestTo("https://brasilapi.test/api/cnpj/v1/19131243000197"))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThrows(ExternalServiceException.class, () -> client.findByCnpj(CNPJ));
    }
}

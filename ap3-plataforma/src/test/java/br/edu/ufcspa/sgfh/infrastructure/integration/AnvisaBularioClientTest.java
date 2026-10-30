package br.edu.ufcspa.sgfh.infrastructure.integration;

import br.edu.ufcspa.sgfh.domain.exception.ExternalServiceException;
import br.edu.ufcspa.sgfh.domain.model.MedicineLeaflet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AnvisaBularioClientTest {

    private static final String BASE_URL = "https://anvisa.test";

    private MockRestServiceServer server;
    private AnvisaBularioClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new AnvisaBularioClient(builder, BASE_URL);
    }

    @Test
    @DisplayName("Busca pelo nome do produto e monta os links das bulas")
    void searchesByProductName() {
        server.expect(requestTo(containsString("/api/consulta/bulario?count=10&page=1&filter%5BnomeProduto%5D=amoxicilina")))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Guest"))
                .andRespond(withSuccess("""
                        { "content": [ {
                            "numProcesso": "25351.123456/2015-11",
                            "nomeProduto": "AMOXICILINA",
                            "razaoSocial": "LABORATORIO EXEMPLO LTDA",
                            "numeroRegistro": "1000000000000",
                            "idBulaProfissionalProtegido": "NDk5",
                            "idBulaPacienteProtegido": null,
                            "campoNaoMapeado": true } ],
                          "totalElements": 1 }
                        """, MediaType.APPLICATION_JSON));

        List<MedicineLeaflet> result = client.searchByProductName("amoxicilina");

        assertEquals(1, result.size());
        MedicineLeaflet leaflet = result.get(0);
        assertEquals("AMOXICILINA", leaflet.productName());
        assertEquals("LABORATORIO EXEMPLO LTDA", leaflet.company());
        assertEquals("1000000000000", leaflet.registrationNumber());
        assertEquals("25351.123456/2015-11", leaflet.processNumber());
        assertEquals(BASE_URL + "/api/consulta/medicamentos/arquivo/bula/parecer/NDk5/?Authorization=",
                leaflet.professionalLeafletUrl());
        assertNull(leaflet.patientLeafletUrl());
        server.verify();
    }

    @Test
    @DisplayName("Resposta sem conteudo vira lista vazia")
    void emptyContentReturnsEmptyList() {
        server.expect(requestTo(containsString("/api/consulta/bulario")))
                .andRespond(withSuccess("{ \"content\": [], \"totalElements\": 0 }", MediaType.APPLICATION_JSON));

        assertTrue(client.searchByProductName("inexistente").isEmpty());
    }

    @Test
    @DisplayName("Bloqueio do firewall da ANVISA vira ExternalServiceException")
    void forbiddenBecomesExternalServiceException() {
        server.expect(requestTo(containsString("/api/consulta/bulario")))
                .andRespond(withStatus(HttpStatus.FORBIDDEN).contentType(MediaType.TEXT_HTML).body("<html>bloqueado</html>"));

        ExternalServiceException error = assertThrows(ExternalServiceException.class,
                () -> client.searchByProductName("amoxicilina"));

        assertTrue(error.getMessage().contains("HTTP 403"));
    }
}

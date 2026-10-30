package br.edu.ufcspa.sgfh.infrastructure.integration;

import br.edu.ufcspa.sgfh.domain.exception.ExternalServiceException;
import br.edu.ufcspa.sgfh.domain.model.DrugConcept;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class RxNavClientTest {

    private MockRestServiceServer server;
    private RxNavClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new RxNavClient(builder, "https://rxnav.test");
    }

    @Test
    @DisplayName("Normaliza o nome em portugues para o RxCUI e o nome canonico")
    void normalizesTermToConcept() {
        server.expect(requestTo("https://rxnav.test/REST/approximateTerm.json?term=amoxicilina&maxEntries=10&option=1"))
                .andRespond(withSuccess("""
                        {"approximateGroup":{"inputTerm":null,"candidate":[
                          {"rxcui":"723","rxaui":"8693615","score":"9.89","rank":"1","name":"Amoxicilina","source":"DRUGBANK"},
                          {"rxcui":"723","rxaui":"10282462","score":"9.89","rank":"1","source":"MMSL"}]}}
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://rxnav.test/REST/rxcui/723/properties.json"))
                .andRespond(withSuccess("""
                        {"properties":{"rxcui":"723","name":"amoxicillin","synonym":"","tty":"IN","language":"ENG"}}
                        """, MediaType.APPLICATION_JSON));

        List<DrugConcept> concepts = client.findConcepts("amoxicilina");

        assertEquals(List.of(new DrugConcept("723", "amoxicillin", "IN")), concepts);
        server.verify();
    }

    @Test
    @DisplayName("Termo sem candidatos devolve lista vazia")
    void unknownTermReturnsEmptyList() {
        server.expect(requestTo(containsString("approximateTerm.json")))
                .andRespond(withSuccess("{\"approximateGroup\":{\"inputTerm\":null}}", MediaType.APPLICATION_JSON));

        assertTrue(client.findConcepts("dipirona").isEmpty());
    }

    @Test
    @DisplayName("Erro do servidor vira ExternalServiceException")
    void serverErrorBecomesExternalServiceException() {
        server.expect(requestTo(containsString("approximateTerm.json"))).andRespond(withServerError());

        assertThrows(ExternalServiceException.class, () -> client.findConcepts("amoxicilina"));
    }
}

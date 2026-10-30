package br.edu.ufcspa.sgfh.web.controller;

import br.edu.ufcspa.sgfh.application.service.ReferenceDataService;
import br.edu.ufcspa.sgfh.domain.exception.BusinessException;
import br.edu.ufcspa.sgfh.domain.exception.ExternalServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/integrations")
public class IntegrationController {

    private static final String RESULTS = "integrations/results :: ";

    private final ReferenceDataService service;

    public IntegrationController(ReferenceDataService service) {
        this.service = service;
    }

    @GetMapping
    public String index() {
        return "integrations/index";
    }

    @GetMapping("/anvisa")
    public String anvisa(@RequestParam(defaultValue = "") String productName, Model model) {
        lookup(model, () -> model.addAttribute("leaflets", service.searchLeaflets(productName)));
        return RESULTS + "anvisa";
    }

    @GetMapping("/rxnav")
    public String rxnav(@RequestParam(defaultValue = "") String drugName, Model model) {
        lookup(model, () -> model.addAttribute("concepts", service.findDrugConcepts(drugName)));
        return RESULTS + "rxnav";
    }

    @GetMapping("/cnpj")
    public String cnpj(@RequestParam(defaultValue = "") String cnpj, Model model) {
        lookup(model, () -> model.addAttribute("company", service.findCompany(cnpj)));
        return RESULTS + "cnpj";
    }

    private void lookup(Model model, Runnable query) {
        try {
            query.run();
        } catch (BusinessException | ExternalServiceException e) {
            model.addAttribute("error", e.getMessage());
        }
    }
}

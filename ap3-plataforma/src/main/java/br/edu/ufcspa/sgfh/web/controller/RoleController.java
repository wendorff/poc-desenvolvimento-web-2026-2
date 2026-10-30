package br.edu.ufcspa.sgfh.web.controller;

import br.edu.ufcspa.sgfh.application.service.RoleService;
import br.edu.ufcspa.sgfh.domain.exception.BusinessException;
import br.edu.ufcspa.sgfh.web.dto.RoleForm;
import br.edu.ufcspa.sgfh.web.mapper.RoleWebMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/roles")
public class RoleController {

    private static final String LIST_VIEW = "roles/list";
    private static final String TABLE_FRAGMENT = "roles/table :: table";

    private final RoleService service;
    private final RoleWebMapper mapper;

    public RoleController(RoleService service, RoleWebMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("roles", mapper.toViewList(service.findAll()));
        model.addAttribute("roleForm", RoleForm.empty());
        return LIST_VIEW;
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model) {
        model.addAttribute("roles", mapper.toViewList(service.findAll()));
        model.addAttribute("roleForm", mapper.toForm(service.findById(id)));
        return LIST_VIEW;
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("roleForm") RoleForm form,
                       BindingResult binding,
                       Model model) {
        if (!binding.hasErrors()) {
            try {
                if (form.id() == null || form.id().isBlank()) {
                    service.create(mapper.toDomain(form));
                } else {
                    service.update(form.id(), mapper.toDomain(form));
                }
                return "redirect:/roles";
            } catch (BusinessException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        model.addAttribute("roles", mapper.toViewList(service.findAll()));
        return LIST_VIEW;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id, Model model) {
        try {
            service.deleteById(id);
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("roles", mapper.toViewList(service.findAll()));
        return TABLE_FRAGMENT;
    }
}

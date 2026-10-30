package br.edu.ufcspa.sgfh.web.controller;

import br.edu.ufcspa.sgfh.application.service.RoleService;
import br.edu.ufcspa.sgfh.application.service.UserService;
import br.edu.ufcspa.sgfh.domain.exception.BusinessException;
import br.edu.ufcspa.sgfh.web.dto.UserForm;
import br.edu.ufcspa.sgfh.web.mapper.RoleWebMapper;
import br.edu.ufcspa.sgfh.web.mapper.UserWebMapper;
import br.edu.ufcspa.sgfh.web.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/users")
public class UserController {

    private static final String LIST_VIEW = "users/list";
    private static final String TABLE_FRAGMENT = "users/table :: table";

    private final UserService service;
    private final RoleService roleService;
    private final UserWebMapper mapper;
    private final RoleWebMapper roleMapper;

    public UserController(UserService service,
                          RoleService roleService,
                          UserWebMapper mapper,
                          RoleWebMapper roleMapper) {
        this.service = service;
        this.roleService = roleService;
        this.mapper = mapper;
        this.roleMapper = roleMapper;
    }

    private void addListData(Model model) {
        model.addAttribute("users", mapper.toViewList(service.findAll(), roleService.findAll()));
        model.addAttribute("roles", roleMapper.toViewList(roleService.findAll()));
    }

    @GetMapping
    public String list(Model model) {
        addListData(model);
        model.addAttribute("userForm", UserForm.empty());
        return LIST_VIEW;
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model) {
        addListData(model);
        model.addAttribute("userForm", mapper.toForm(service.findById(id)));
        return LIST_VIEW;
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("userForm") UserForm form,
                       BindingResult binding,
                       Model model) {
        if (!binding.hasErrors()) {
            try {
                if (form.id() == null || form.id().isBlank()) {
                    service.create(mapper.toDomain(form), form.password());
                } else {
                    service.update(form.id(), mapper.toDomain(form), form.password());
                }
                return "redirect:/users";
            } catch (BusinessException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        addListData(model);
        return LIST_VIEW;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id,
                         @AuthenticationPrincipal AuthenticatedUser currentUser,
                         Model model) {
        try {
            service.deleteById(id, currentUser.id());
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
        }
        addListData(model);
        return TABLE_FRAGMENT;
    }
}

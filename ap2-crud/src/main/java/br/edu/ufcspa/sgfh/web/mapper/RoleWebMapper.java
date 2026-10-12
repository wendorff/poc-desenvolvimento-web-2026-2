package br.edu.ufcspa.sgfh.web.mapper;

import br.edu.ufcspa.sgfh.domain.model.Role;
import br.edu.ufcspa.sgfh.web.dto.RoleForm;
import br.edu.ufcspa.sgfh.web.dto.RoleView;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleWebMapper {

    public Role toDomain(RoleForm form) {
        return new Role(form.id(), form.name(), form.description(), null);
    }

    public RoleForm toForm(Role role) {
        return new RoleForm(role.id(), role.name(), role.description());
    }

    public RoleView toView(Role role) {
        return new RoleView(role.id(), role.name(), role.description());
    }

    public List<RoleView> toViewList(List<Role> roles) {
        return roles.stream().map(this::toView).toList();
    }
}

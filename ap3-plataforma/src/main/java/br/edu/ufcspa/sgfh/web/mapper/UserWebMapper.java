package br.edu.ufcspa.sgfh.web.mapper;

import br.edu.ufcspa.sgfh.domain.model.Role;
import br.edu.ufcspa.sgfh.domain.model.User;
import br.edu.ufcspa.sgfh.web.dto.UserForm;
import br.edu.ufcspa.sgfh.web.dto.UserView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserWebMapper {

    public User toDomain(UserForm form) {
        return new User(
                form.id(),
                form.name(),
                form.email(),
                null,
                form.roleId(),
                form.isActive(),
                null);
    }

    public UserForm toForm(User user) {
        return new UserForm(
                user.id(),
                user.name(),
                user.email(),
                null,
                user.roleId(),
                user.active());
    }

    public UserView toView(User user, String roleName) {
        return new UserView(user.id(), user.name(), user.email(), roleName, user.active());
    }

    public List<UserView> toViewList(List<User> users, List<Role> roles) {
        Map<String, String> roleNames = roles.stream()
                .collect(Collectors.toMap(Role::id, Role::name, (a, b) -> a));
        return users.stream()
                .map(user -> toView(user, roleNames.getOrDefault(user.roleId(), "-")))
                .toList();
    }
}

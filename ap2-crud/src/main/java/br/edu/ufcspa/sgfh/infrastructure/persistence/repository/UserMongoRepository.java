package br.edu.ufcspa.sgfh.infrastructure.persistence.repository;

import br.edu.ufcspa.sgfh.infrastructure.persistence.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserMongoRepository extends MongoRepository<UserDocument, String> {

    Optional<UserDocument> findByEmailIgnoreCase(String email);

    List<UserDocument> findAllByOrderByNameAsc();

    long countByRoleId(String roleId);
}

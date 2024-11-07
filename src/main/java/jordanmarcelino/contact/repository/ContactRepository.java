package jordanmarcelino.contact.repository;

import jordanmarcelino.contact.entity.Contact;
import jordanmarcelino.contact.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long>, JpaSpecificationExecutor<Contact> {

    Optional<Contact> findByUserAndId(User user, Long id);
}

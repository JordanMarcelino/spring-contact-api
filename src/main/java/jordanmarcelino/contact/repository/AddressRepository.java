package jordanmarcelino.contact.repository;

import jordanmarcelino.contact.entity.Address;
import jordanmarcelino.contact.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findAllByContact(Contact contact);

    Optional<Address> findFirstByContactAndId(Contact contact, Long id);
}

package jordanmarcelino.contact.repository;

import jordanmarcelino.contact.entity.Address;
import jordanmarcelino.contact.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByContact(Contact contact);

}

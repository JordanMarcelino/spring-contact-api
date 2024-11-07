package jordanmarcelino.contact.service;

import jordanmarcelino.contact.dto.*;
import jordanmarcelino.contact.entity.Contact;
import jordanmarcelino.contact.repository.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    private final ValidationService validationService;

    @Override
    public List<ContactResponse> search(SearchContactRequest searchContactRequest) {
        return List.of();
    }

    @Override
    public ContactResponse get(GetContactRequest request) {
        return null;
    }

    @Override
    @Transactional
    public ContactResponse save(CreateContactRequest request) {
        validationService.validate(request);

        Contact contact = new Contact();
        contact.setUser(request.getUser());
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contactRepository.save(contact);

        return new ContactResponse(contact.getId(), contact.getFirstName(), contact.getLastName(), contact.getEmail(),
                contact.getPhone());
    }

    @Override
    public ContactResponse update(UpdateContactRequest request) {
        return null;
    }

    @Override
    public void delete(DeleteContactRequest request) {

    }
}

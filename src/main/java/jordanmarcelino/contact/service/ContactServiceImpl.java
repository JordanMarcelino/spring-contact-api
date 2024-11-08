package jordanmarcelino.contact.service;

import jordanmarcelino.contact.dto.*;
import jordanmarcelino.contact.entity.Contact;
import jordanmarcelino.contact.exception.NotFoundException;
import jordanmarcelino.contact.repository.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final String CONTACT_NOT_FOUND = "contact not found";

    private final ContactRepository contactRepository;

    private final ValidationService validationService;

    private ContactResponse toContactResponse(Contact contact) {
        return new ContactResponse(contact.getId(), contact.getFirstName(), contact.getLastName(), contact.getEmail(),
                contact.getPhone());
    }

    @Override
    public List<ContactResponse> search(SearchContactRequest searchContactRequest) {
        return List.of();
    }

    @Override
    public ContactResponse get(GetContactRequest request) {
        Contact contact = contactRepository.findByUserAndId(request.getUser(), request.getId()).orElse(null);
        if (Objects.isNull(contact)) {
            throw new NotFoundException(CONTACT_NOT_FOUND);
        }

        return toContactResponse(contact);
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

        return toContactResponse(contact);
    }

    @Override
    @Transactional
    public ContactResponse update(UpdateContactRequest request) {
        validationService.validate(request);

        Contact contact = contactRepository.findByUserAndId(request.getUser(), request.getId()).orElse(null);
        if (Objects.isNull(contact)) {
            throw new NotFoundException(CONTACT_NOT_FOUND);
        }

        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contactRepository.save(contact);

        return toContactResponse(contact);
    }

    @Override
    @Transactional
    public void delete(DeleteContactRequest request) {
        Contact contact = contactRepository.findByUserAndId(request.getUser(), request.getId()).orElse(null);
        if (Objects.isNull(contact)) {
            throw new NotFoundException(CONTACT_NOT_FOUND);
        }

        contactRepository.delete(contact);
    }
}

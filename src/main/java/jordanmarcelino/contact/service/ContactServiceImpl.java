package jordanmarcelino.contact.service;

import jakarta.persistence.criteria.Predicate;
import jordanmarcelino.contact.dto.*;
import jordanmarcelino.contact.entity.Contact;
import jordanmarcelino.contact.exception.NotFoundException;
import jordanmarcelino.contact.repository.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public Page<ContactResponse> search(SearchContactRequest request) {
        validationService.validate(request);

        Specification<Contact> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("user"), request.getUser()));

            if (Objects.nonNull(request.getName())) {
                predicates.add(
                        builder.or(
                                builder.like(root.get("firstName"), "%" + request.getName() + "%"),
                                builder.like(root.get("lastName"), "%" + request.getName() + "%")
                        )
                );
            }

            if (Objects.nonNull(request.getEmail())) {
                predicates.add(
                        builder.like(root.get("email"), "%" + request.getEmail() + "%")
                );
            }

            if (Objects.nonNull(request.getPhone())) {
                predicates.add(
                        builder.like(root.get("phone"), "%" + request.getPhone() + "%")
                );
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Contact> contacts = contactRepository.findAll(specification, pageable);
        List<ContactResponse> contactResponses = contacts.getContent().stream().map(this::toContactResponse).toList();

        return new PageImpl<>(contactResponses, pageable, contacts.getTotalElements());
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

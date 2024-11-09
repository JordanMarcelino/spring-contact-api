package jordanmarcelino.contact.service;

import jordanmarcelino.contact.dto.*;
import jordanmarcelino.contact.entity.Address;
import jordanmarcelino.contact.entity.Contact;
import jordanmarcelino.contact.exception.NotFoundException;
import jordanmarcelino.contact.repository.AddressRepository;
import jordanmarcelino.contact.repository.ContactRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final String CONTACT_NOT_FOUND = "contact not found";

    private final String ADDRESS_NOT_FOUND = "address not found";

    private AddressRepository addressRepository;

    private ContactRepository contactRepository;

    private ValidationService validationService;

    private AddressResponse toAddressResponse(Address address) {
        return new AddressResponse(address.getId(), address.getCountry(), address.getProvince(), address.getCity(),
                address.getStreet(), address.getPostalCode());
    }

    @Override
    public List<AddressResponse> findAll(GetAddressRequest request) {
        return List.of();
    }

    @Override
    public AddressResponse save(CreateAddressRequest request) {
        validationService.validate(request);

        Contact contact = contactRepository.findByUserAndId(request.getUser(), request.getContactId()).orElse(null);

        if (Objects.isNull(contact)) {
            throw new NotFoundException(CONTACT_NOT_FOUND);
        }

        Address address = new Address();
        address.setContact(contact);
        address.setCountry(request.getCountry());
        address.setCity(request.getCity());
        address.setStreet(request.getStreet());
        address.setPostalCode(request.getPostalCode());
        addressRepository.save(address);

        return toAddressResponse(address);
    }

    @Override
    public AddressResponse get(GetAddressRequest request) {
        return null;
    }

    @Override
    public AddressResponse update(UpdateAddressRequest request) {
        validationService.validate(request);

        Contact contact = contactRepository.findByUserAndId(request.getUser(), request.getContactId()).orElseThrow(() -> new NotFoundException(CONTACT_NOT_FOUND));
        Address address = addressRepository.findByContact(contact).orElseThrow(() -> new NotFoundException(ADDRESS_NOT_FOUND));

        address.setCity(request.getCity());
        if (Objects.nonNull(request.getProvince())) {
            address.setProvince(request.getProvince());
        }
        if (Objects.nonNull(request.getCountry())) {
            address.setCountry(request.getCountry());
        }
        if (Objects.nonNull(request.getStreet())) {
            address.setStreet(request.getStreet());
        }
        if (Objects.nonNull(request.getPostalCode())) {
            address.setPostalCode(request.getPostalCode());
        }

        addressRepository.save(address);

        return toAddressResponse(address);
    }

    @Override
    public void delete(DeleteAddressRequest request) {
        Contact contact = contactRepository.findByUserAndId(request.getUser(), request.getContactId()).orElseThrow(() -> new NotFoundException(CONTACT_NOT_FOUND));

        Address address = addressRepository.findByContact(contact).orElseThrow(() -> new NotFoundException(ADDRESS_NOT_FOUND));

        addressRepository.delete(address);
    }
}

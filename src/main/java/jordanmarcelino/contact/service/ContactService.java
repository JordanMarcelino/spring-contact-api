package jordanmarcelino.contact.service;

import jordanmarcelino.contact.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface ContactService {

    Page<ContactResponse> search(SearchContactRequest request);

    ContactResponse get(GetContactRequest request);

    ContactResponse save(CreateContactRequest request);

    ContactResponse update(UpdateContactRequest request);

    void delete(DeleteContactRequest request);
}

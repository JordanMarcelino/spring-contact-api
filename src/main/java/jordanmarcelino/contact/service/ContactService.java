package jordanmarcelino.contact.service;

import jordanmarcelino.contact.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ContactService {

    List<ContactResponse> search(SearchContactRequest searchContactRequest);

    ContactResponse get(GetContactRequest request);

    ContactResponse save(CreateContactRequest request);

    ContactResponse update(UpdateContactRequest request);

    void delete(DeleteContactRequest request);
}

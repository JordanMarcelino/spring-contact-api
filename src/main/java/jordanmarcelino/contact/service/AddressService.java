package jordanmarcelino.contact.service;

import jordanmarcelino.contact.dto.*;

import java.util.List;

public interface AddressService {

    List<AddressResponse> findAll(GetAddressRequest request);

    AddressResponse save(CreateAddressRequest request);

    AddressResponse get(GetAddressRequest request);

    AddressResponse update(UpdateAddressRequest request);

    void delete(DeleteAddressRequest request);
}

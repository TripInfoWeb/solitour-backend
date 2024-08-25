package solitour_backend.solitour.place.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.place.dto.mapper.PlaceMapper;
import solitour_backend.solitour.place.dto.request.PlaceModifyRequest;
import solitour_backend.solitour.place.dto.request.PlaceRegisterRequest;
import solitour_backend.solitour.place.dto.response.PlaceResponse;
import solitour_backend.solitour.place.entity.Place;
import solitour_backend.solitour.place.exception.PlaceNotExistsException;
import solitour_backend.solitour.place.repository.PlaceRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceMapper placeMapper;

    @Transactional(readOnly = true)
    public PlaceResponse getPlace(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new PlaceNotExistsException("해당 하는 id의 Place가 존재하지 않습니다"));

        return placeMapper.mapToPlaceResponse(place);
    }


    public PlaceResponse savePlace(PlaceRegisterRequest placeRegisterRequest) {
        Place place = new Place(
                placeRegisterRequest.getSearchId(),
                placeRegisterRequest.getName(),
                placeRegisterRequest.getXAxis(),
                placeRegisterRequest.getYAxis(),
                placeRegisterRequest.getAddress()
        );

        Place savedPlace = placeRepository.save(place);

        return placeMapper.mapToPlaceResponse(savedPlace);
    }


    public PlaceResponse updatePlace(Long id, PlaceModifyRequest placeModifyRequest) {
        Place savedPlace = placeRepository.findById(id)
                .orElseThrow(() -> new PlaceNotExistsException("해당 하는 id의 Place가 존재하지 않습니다"));

        savedPlace.setSearchId(placeModifyRequest.getSearchId());
        savedPlace.setName(placeModifyRequest.getName());
        savedPlace.setXaxis(placeModifyRequest.getXAxis());
        savedPlace.setYaxis(placeModifyRequest.getYAxis());
        savedPlace.setAddress(placeModifyRequest.getAddress());

        return placeMapper.mapToPlaceResponse(savedPlace);
    }

    public void deletePlace(Long id) {
        if (!placeRepository.existsById(id)) {
            throw new PlaceNotExistsException("해당 하는 id의 Place가 존재하지 않습니다");
        }
        placeRepository.deleteById(id);
    }
}

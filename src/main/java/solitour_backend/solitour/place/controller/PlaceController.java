package solitour_backend.solitour.place.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import solitour_backend.solitour.error.Utils;
import solitour_backend.solitour.place.dto.request.PlaceModifyRequest;
import solitour_backend.solitour.place.dto.request.PlaceRegisterRequest;
import solitour_backend.solitour.place.dto.response.PlaceResponse;
import solitour_backend.solitour.place.service.PlaceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService placeService;


    @GetMapping("/{id}")
    public ResponseEntity<PlaceResponse> getPlace(@PathVariable("id") Long id) {
        PlaceResponse placeResponse = placeService.getPlace(id);

        return ResponseEntity.status(HttpStatus.OK).body(placeResponse);
    }

    @PostMapping
    public ResponseEntity<PlaceResponse> registerPlace(
            @Valid @RequestBody PlaceRegisterRequest placeRegisterRequest,
            BindingResult bindingResult) {
        Utils.validationRequest(bindingResult);
        PlaceResponse placeResponse = placeService.savePlace(placeRegisterRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(placeResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaceResponse> modifyPlace(@PathVariable("id") Long id,
                                                     @Valid @RequestBody PlaceModifyRequest placeModifyRequest,
                                                     BindingResult bindingResult) {
        Utils.validationRequest(bindingResult);
        PlaceResponse placeResponse = placeService.updatePlace(id, placeModifyRequest);

        return ResponseEntity.status(HttpStatus.OK).body(placeResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable("id") Long id) {
        placeService.deletePlace(id);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }


}

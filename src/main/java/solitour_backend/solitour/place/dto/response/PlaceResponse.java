package solitour_backend.solitour.place.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;


@Getter
@AllArgsConstructor
public class PlaceResponse {

  private String searchId;
  private String name;
  private BigDecimal xaxis;
  private BigDecimal yaxis;
  private String address;
}
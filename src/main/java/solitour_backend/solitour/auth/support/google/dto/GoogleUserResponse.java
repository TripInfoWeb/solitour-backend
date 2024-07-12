package solitour_backend.solitour.auth.support.google.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUserResponse {

  private String id;
  private String name;
  private String email;
}

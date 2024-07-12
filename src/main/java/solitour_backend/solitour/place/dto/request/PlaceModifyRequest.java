package solitour_backend.solitour.place.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import solitour_backend.solitour.error.exception.RequestValidationFailedException;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class PlaceModifyRequest {

  @Nullable
  @Size(min = 1, max = 30)
  private String searchId;

  @NotBlank
  @Size(min = 1, max = 30)
  private String name;

  @Nullable
  @Digits(integer = 10, fraction = 6)
  private BigDecimal xAxis;

  @Nullable
  @Digits(integer = 10, fraction = 6)
  private BigDecimal yAxis;

  @NotBlank
  @Size(min = 1, max = 50)
  private String address;

  public boolean validate() {
    boolean searchIdIsNull = this.searchId == null;
    boolean xAxisIsNull = this.xAxis == null;
    boolean yAxisIsNull = this.yAxis == null;

    if (searchIdIsNull && (xAxisIsNull || yAxisIsNull)) {
      return true;
    }

    if (!searchIdIsNull && (!xAxisIsNull || !yAxisIsNull)) {
      return true;
    }

    return false;
  }
}

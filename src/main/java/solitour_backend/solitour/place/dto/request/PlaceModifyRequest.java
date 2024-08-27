package solitour_backend.solitour.place.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaceModifyRequest {

    @Nullable
    @Size(min = 1, max = 30)
    private String searchId;

    @NotBlank
    @Size(min = 1, max = 30)
    private String name;

    @NotNull
    @JsonDeserialize(using = NumberDeserializers.BigDecimalDeserializer.class)
    @Digits(integer = 10, fraction = 6)
    private BigDecimal xAxis;

    @NotNull
    @JsonDeserialize(using = NumberDeserializers.BigDecimalDeserializer.class)
    @Digits(integer = 10, fraction = 6)
    private BigDecimal yAxis;

    @NotBlank
    @Size(min = 1, max = 50)
    private String address;
}

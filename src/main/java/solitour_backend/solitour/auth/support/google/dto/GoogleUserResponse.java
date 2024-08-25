package solitour_backend.solitour.auth.support.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUserResponse {

    @JsonProperty("resourceName")
    private String resourceName;

    @Data
    public static class Name {

        @JsonProperty("displayName")
        private String displayName;

        @JsonProperty("familyName")
        private String familyName;

        @JsonProperty("givenName")
        private String givenName;
    }

    @Data
    public static class EmailAddress {

        @JsonProperty("value")
        private String value;

        @JsonProperty("type")
        private String type;
    }

    @Data
    public static class PhoneNumber {

        @JsonProperty("value")
        private String value;

        @JsonProperty("type")
        private String type;
    }

    @Data
    public static class Gender {

        @JsonProperty("value")
        private String value;
    }

    @Data
    public static class Birthday {

        @JsonProperty("date")
        private Date date;

        @Data
        public static class Date {

            @JsonProperty("year")
            private int year;

            @JsonProperty("month")
            private int month;

            @JsonProperty("day")
            private int day;
        }
    }

    @JsonProperty("names")
    private List<Name> names;

    @JsonProperty("emailAddresses")
    private List<EmailAddress> emailAddresses;

    @JsonProperty("phoneNumbers")
    private List<PhoneNumber> phoneNumbers;

    @JsonProperty("genders")
    private List<Gender> genders;

    @JsonProperty("birthdays")
    private List<Birthday> birthdays;

    public PhoneNumber getMobilePhoneNumber() {
        if (phoneNumbers != null) {
            for (PhoneNumber phoneNumber : phoneNumbers) {
                if ("mobile".equalsIgnoreCase(phoneNumber.getType())) {
                    return phoneNumber;
                }
            }
        }
        return null;
    }

}

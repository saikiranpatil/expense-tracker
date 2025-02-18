package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.example.entity.UserInfo;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoDto {
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("phone_number")
    private Long phoneNumber;
    @JsonProperty("email")
    private String email;
    @JsonProperty("profile_url")
    private String profileUrl;

    public UserInfo transformToUserInfo() {
        return UserInfo.builder()
                .firstName(firstName)
                .lastName(lastName)
                .userId(userId)
                .email(email)
                .profileUrl(profileUrl)
                .phoneNumber(phoneNumber)
                .build();
    }
}

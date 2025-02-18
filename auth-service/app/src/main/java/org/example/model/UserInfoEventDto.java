package org.example.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.data.mapping.model.PropertyNameFieldNamingStrategy;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInfoEventDto {
    private String userId;
    private String firstName;
    private String lastName;
    private Long phoneNumber;
    private String email;
    private String profileUrl;
}

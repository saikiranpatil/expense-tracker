package org.example.model;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoEventDto {
    private String userId;
    private String firstName;
    private String lastName;
    private Long phoneNumber;
    private String email;
    private String profileUrl;
}

package org.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.sql.Timestamp;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpenseDto {
    private Long id;
    @JsonProperty("external_id")
    private String externalId;
    @JsonProperty("user_id")
    private String userId;
    @NonNull
    private Integer amount;
    @Builder.Default
    private String currency="INR";
    private String account;
    private String payee;
    @JsonProperty("txn_type")
    private String txnType = "Other";
    private Date date;
    @JsonProperty("created_at")
    private Timestamp createdAt;
    private String description;
    private String category;
}

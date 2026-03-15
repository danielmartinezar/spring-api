package ey.daniel.spring_test.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AuthDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "El correo es requerido")
        private String email;

        @NotBlank(message = "La contraseña es requerida")
        private String password;
    }

    @Getter
    @AllArgsConstructor
    public static class LoginResponse {
        private UUID id;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;
        @JsonProperty("modified_at")
        private LocalDateTime modifiedAt;

        @JsonProperty("last_login")
        private LocalDateTime lastLogin;

        private String token;

        @JsonProperty("is_active")
        private Boolean isActive;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SignupRequest {

        @NotBlank(message = "El nombre es requerido")
        private String name;

        @NotBlank(message = "El correo es requerido")
        @Pattern(regexp = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$", message = "Formato de correo inválido")
        private String email;

        @NotBlank(message = "La contraseña es requerida")
        @Pattern(regexp = "^(?=(?:[^A-Z]*[A-Z]){1}[^A-Z]*$)(?=.*[a-z])(?=(?:[^0-9]*[0-9]){2}[^0-9]*$)[a-zA-Z0-9]+$", message = "Formato de contraseña inválido")
        private String password;

        @NotEmpty(message = "Se requiere al menos un teléfono")
        @Valid
        private List<PhoneRequest> phones;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PhoneRequest {
        @NotBlank(message = "El número es requerido")
        private String number;

        @NotBlank(message = "El código de ciudad es requerido")
        @JsonProperty("city_code")
        private String cityCode;

        @NotBlank(message = "El código de país es requerido")
        @JsonProperty("country_code")
        private String countryCode;
    }

    @Getter
    @AllArgsConstructor
    public static class SignupResponse {
        private UUID id;
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
        @JsonProperty("modified_at")
        private LocalDateTime modifiedAt;

        @JsonProperty("last_login")
        private LocalDateTime lastLogin;

        private String token;

        @JsonProperty("is_active")
        private Boolean isActive;
    }
}

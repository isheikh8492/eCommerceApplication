package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

	@NotEmpty(message = "Username must not be null or blank.")
	@JsonProperty
	private String username;

	@NotEmpty(message = "password must not be null or blank.")
	@JsonProperty
	private String password;

	@NotEmpty(message = "confirmPassword must not be null or blank.")
	@JsonProperty
	private String confirmPassword;

	@Override
	public String toString() {
		return "CreateUserRequest{" +
				"username='" + username + '\'' +
				'}';
	}
}
package io.kall.jellymatter.jellymatterrelay.mattermost;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CustomStatus {
	
	private String emoji;
	private String text;
	@JsonProperty("expires_at")
	private Instant expiresAt;
	
}

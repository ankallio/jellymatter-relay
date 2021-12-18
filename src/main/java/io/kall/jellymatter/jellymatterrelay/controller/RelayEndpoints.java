package io.kall.jellymatter.jellymatterrelay.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.kall.jellymatter.jellymatterrelay.mattermost.CustomStatus;
import io.kall.jellymatter.jellymatterrelay.mattermost.MattermostApi;
import io.kall.jellymatter.jellymatterrelay.mattermost.MattermostApiFactory;
import io.kall.jellymatter.jellymatterrelay.mattermost.OkResponse;
import lombok.Data;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RestController
public class RelayEndpoints {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MattermostApiFactory apiFactory;
	
	@PostMapping("/customstatus")
	public void receiveJellyfinWebhook(@RequestBody JellyfinWebhook webhook) {
		String mattermostAccessToken = webhook.getAccessToken();
		String emoji = webhook.getEmoji();
		String text = webhook.getText();
		
		Duration runtime = Optional.ofNullable(webhook.getRunTimeTicks()).map(l -> l / 100L).map(Duration::ofNanos).orElse(null);
		Duration position = Optional.ofNullable(webhook.getPlaybackPositionTicks()).map(l -> l / 100L).map(Duration::ofNanos).orElse(Duration.ZERO);
		Instant expiresAt = runtime != null ? Instant.now().plus(runtime).minus(position) : null;
		
		MattermostApi api = apiFactory.buildApi(mattermostAccessToken);
		
		CustomStatus status = new CustomStatus();
		status.setEmoji(emoji);
		status.setText(text);
		status.setExpiresAt(expiresAt);
		api.customStatusUpdate("me", status).enqueue(new Callback<OkResponse>() {
			
			@Override
			public void onResponse(Call<OkResponse> call, Response<OkResponse> response) {
				if (!response.isSuccessful()) {
					logger.warn("CustomStatus update not successful: {}", response);
				}
			}
			
			@Override
			public void onFailure(Call<OkResponse> call, Throwable t) {
				logger.error("CustomStatus update call failed.", t);
			}
		});
		
	}
	
	@Data
	public static class JellyfinWebhook {
		@JsonProperty("access_token")
		private String accessToken;
		private String emoji;
		private String text;
		@JsonProperty("run_time_ticks")
		private Long runTimeTicks;
		@JsonProperty("playback_position_ticks")
		private Long playbackPositionTicks;
	}
}

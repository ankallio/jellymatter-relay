package io.kall.jellymatter.jellymatterrelay.controller;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import okhttp3.ResponseBody;
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
		
		if ("PlaybackStart".equals(webhook.getNotificationType())) {
			onPlaybackStarted(webhook);
		}
		
		else if ("PlaybackStop".equals(webhook.getNotificationType())) {
			onPlaybackStopped(webhook);
		}
		
	}

	private void onPlaybackStarted(JellyfinWebhook webhook) {
		String mattermostAccessToken = webhook.getAccessToken();
		String emoji = webhook.getEmoji();
		String text = webhook.getText();

		logger.debug("Received webhook. Text={} runtime={} position={}", text, webhook.getRunTimeTicks(), webhook.getPlaybackPositionTicks());
		
		Duration runtime = Optional.ofNullable(webhook.getRunTimeTicks()).map(l -> l / 10_000L).map(Duration::ofMillis).orElse(null);
		Duration position = Optional.ofNullable(webhook.getPlaybackPositionTicks()).map(l -> l / 10_000L).map(Duration::ofMillis).orElse(Duration.ZERO);
		Instant expiresAt = runtime != null ? Instant.now().plus(runtime).minus(position).truncatedTo(ChronoUnit.MILLIS) : null;
		
		MattermostApi api = apiFactory.buildApi(mattermostAccessToken);
		
		CustomStatus status = new CustomStatus();
		status.setEmoji(emoji);
		status.setText(text);
		status.setExpiresAt(expiresAt);
		
		api.customStatusUpdate(status).enqueue(new Callback<OkResponse>() {
			
			@Override
			public void onResponse(Call<OkResponse> call, Response<OkResponse> response) {
				if (!response.isSuccessful()) {
					logger.warn("CustomStatus update not successful: {}", response);
					try (ResponseBody errorBody = response.errorBody()) {
						logger.warn("Error: {}", errorBody.string());
					} catch (IOException e) {
						logger.error("Error", e);
					}
				}
			}
			
			@Override
			public void onFailure(Call<OkResponse> call, Throwable t) {
				logger.error("CustomStatus update call failed.", t);
			}
		});
	}

	private void onPlaybackStopped(JellyfinWebhook webhook) {
		String mattermostAccessToken = webhook.getAccessToken();
		
		MattermostApi api = apiFactory.buildApi(mattermostAccessToken);
		
		api.customStatusDelete().enqueue(new Callback<Void>() {
			
			@Override
			public void onResponse(Call<Void> call, Response<Void> response) {
				if (!response.isSuccessful()) {
					logger.warn("CustomStatus update not successful: {}", response);
					try (ResponseBody errorBody = response.errorBody()) {
						logger.warn("Error: {}", errorBody.string());
					} catch (IOException e) {
						logger.error("Error", e);
					}
				}
			}
			
			@Override
			public void onFailure(Call<Void> call, Throwable t) {
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
		@JsonProperty("notification_type")
		private String notificationType;
	}
}

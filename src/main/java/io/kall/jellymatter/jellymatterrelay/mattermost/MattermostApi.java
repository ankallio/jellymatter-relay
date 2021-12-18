package io.kall.jellymatter.jellymatterrelay.mattermost;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.PUT;

public interface MattermostApi {
	
	@PUT("users/me/status/custom")
	Call<OkResponse> customStatusUpdate(@Body CustomStatus status);
	
	@DELETE("users/me/status/custom")
	Call<Void> customStatusDelete();
	
}

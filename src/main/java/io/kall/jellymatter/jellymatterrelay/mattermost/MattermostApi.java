package io.kall.jellymatter.jellymatterrelay.mattermost;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MattermostApi {
	
	@PUT("users/{userId}/status/custom")
	Call<OkResponse> customStatusUpdate(@Path("userId") String userId, @Body CustomStatus status);
	
}

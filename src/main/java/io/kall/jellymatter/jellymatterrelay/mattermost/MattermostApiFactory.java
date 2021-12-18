package io.kall.jellymatter.jellymatterrelay.mattermost;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Service
public class MattermostApiFactory {
	
	private final ObjectMapper objectMapper;
	
	@Value("${mattermost.baseurl}")
	private String baseUrl;
	
	public MattermostApiFactory(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	public MattermostApi buildApi(String mattermostAccessToken) {
		OkHttpClient client = new OkHttpClient.Builder()
				.addInterceptor(chain -> chain.proceed(chain.request().newBuilder().addHeader("Authorization", "Bearer " + mattermostAccessToken).build()))
				.build();

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.client(client)
				.addConverterFactory(JacksonConverterFactory.create(objectMapper))
				.build();
		return retrofit.create(MattermostApi.class);
	}
	
}

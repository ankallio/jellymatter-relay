package io.kall.jellymatter.jellymatterrelay.mattermost;

import lombok.Data;

@Data
public class OkResponse {
	private String status;
	
	public final boolean isOk() {
		return "OK".equals(status);
	}
	
}

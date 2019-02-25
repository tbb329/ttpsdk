package com.zlst.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("ttpsdk.call.uip")
public class CallUipConfig {

	private String invokIpOrName;
	
	private String invokPort;

	public String getInvokIpOrName() {
		return invokIpOrName;
	}

	public void setInvokIpOrName(String invokIpOrName) {
		this.invokIpOrName = invokIpOrName;
	}

	public String getInvokPort() {
		return invokPort;
	}

	public void setInvokPort(String invokPort) {
		this.invokPort = invokPort;
	}
	
}

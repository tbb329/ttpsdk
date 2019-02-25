package com.zlst.common.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zlst.common.kafka.DemoListener;
import com.zlst.param.Result;

/**
 * 通过RestTemplate方式调用外部服务工具类
 * @author xiong.jie/170123
 * @create 2019年1月7日
 */
@Component
public class ExternalServiceCallUtil {

	private static final Logger log= LoggerFactory.getLogger(ExternalServiceCallUtil.class);
	
	public static final int DEFAULT_TIMEOUT = 15000;

	@Autowired
	private RestTemplate restTemplate;
	
	public String sendPostWithJson(String url, String para) {
		log.debug("ExternalServiceCallUtil.sendPostWithJson, url:{}, para:{}", url, para);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		// map 转换为json对象
		HttpEntity<String> formEntity = new HttpEntity<String>(para, headers);
		String result = restTemplate.postForObject(url, formEntity, String.class);
		headers.clear();
		return result;
	}
	
	public Result sendPostWithObject(String url, Object para) throws Exception {
		return restTemplate.postForObject(url, para, Result.class);
	}
	
	public ResponseEntity<Result> sendDeleteWithParam(String url, Object... para) throws Exception {
		return restTemplate.exchange(url, HttpMethod.DELETE, null, Result.class, para);
	}

	public Result sendGetWithParam(String url, Object... para) throws Exception {
		return restTemplate.getForObject(url, Result.class, para);
	}

	public Result sendGetWithParam(String url) throws Exception {
		return restTemplate.getForObject(url, Result.class);
	}

	public String sendPostWithForm(String url, MultiValueMap<String, Object> parts) {
		log.debug("ExternalServiceCallUtil.sendPostWithForm, url:{}, parts:{}", url, JSON.toJSONString(parts));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity(parts, headers);
		String result = restTemplate.postForObject(url, requestEntity, String.class);
		headers.clear();
		return result;
	}

	public String sendPostWithForm(String url, String para) throws Exception {
		log.debug("ExternalServiceCallUtil.sendPostWithForm, url:{}, para:{}", url, para);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<String> requestEntity = new HttpEntity(para, headers);
		String result = restTemplate.postForObject(url, requestEntity, String.class);
		headers.clear();
		return result;
	}

	public ResponseEntity<String> sendHttpRequest(String url, HttpMethod method) {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(DEFAULT_TIMEOUT);// 设置超时
		requestFactory.setReadTimeout(DEFAULT_TIMEOUT);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		HttpHeaders headers = new HttpHeaders();
		return restTemplate.exchange(url, method, new HttpEntity<String>(headers), String.class);
	}

	// 获取请求体中的字符串(POST)
	public String getBodyData(HttpServletRequest request) {
		StringBuffer data = new StringBuffer();
		String line = null;
		BufferedReader reader = null;
		try {
			reader = request.getReader();
			while (null != (line = reader.readLine()))
				data.append(line);
		} catch (IOException e) {
		} finally {
		}
		return data.toString();
	}

	public String getHeadData(HttpServletRequest request) {
		JSONObject json = new JSONObject();
		Enumeration<String> headerNames = request.getParameterNames();
		while (headerNames.hasMoreElements()) {
			String thisName = headerNames.nextElement().toString();
			String thisValue = request.getParameter(thisName);
			if ((null != thisValue) && !thisValue.equals("null")) {
				json.put(thisName, thisValue);
			}
		}
		return json.toJSONString();
	}

}

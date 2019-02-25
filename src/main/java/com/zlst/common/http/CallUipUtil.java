package com.zlst.common.http;

import com.zlst.common.config.GlobalCacheManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.zlst.common.config.CallUipConfig;
import com.zlst.common.config.GlobalConstants;
import com.zlst.param.Result;

import java.text.MessageFormat;

/**
 * 封装调用uip服务，以便统一管理
 * @author xiong.jie/170123
 * @create 2019年1月8日
 */
@Component
public class CallUipUtil {

	private static final Logger log = LoggerFactory.getLogger(CallUipUtil.class);
	
	@Autowired
	private ExternalServiceCallUtil externalServiceCallUtil;
	
	@Autowired
	private CallUipConfig callUipConfig;
	
	public StringBuffer getUrl(){
		return  new StringBuffer(GlobalConstants.CALL_UIP_HTTP_PREFIX)
				.append(callUipConfig.getInvokIpOrName())
				.append(":")
				.append(callUipConfig.getInvokPort());
	}

	// 调用发布接口
	public Result callSearchEqBySerCode() throws Exception{
		StringBuffer urlBuffer = getUrl();
		urlBuffer.append(GlobalConstants.ACTUATOR_EQUIPANDCS_INFO);
		log.info("CallUipUtil.callSearchEqBySerCode, url:{}", urlBuffer.toString());
		return externalServiceCallUtil.sendGetWithParam(urlBuffer.toString());
	}

	//调用获取采集任务接口
	public Result callQueryMemberByServCode(String serviceName) throws Exception {
		StringBuffer url = getUrl();
		url.append(GlobalConstants.DIS_PUBLISH_TASK)
				.append(MessageFormat.format("?serviceCode={0}", serviceName));
		log.info("CallUipUtil.getColTask, url:{}", url.toString());
		return externalServiceCallUtil.sendGetWithParam(url.toString());
	}

	// 调用获取设备与TOPIC关联信息
	public Result callSearchDevTopicInfo() throws Exception{
		StringBuffer urlBuffer = getUrl();
		urlBuffer.append(GlobalConstants.ACTUATOR_DEVTOPIC_INFO);
		log.info("CallUipUtil.callSearchDevTopicInfo, url:{}", urlBuffer.toString());
		return externalServiceCallUtil.sendGetWithParam(urlBuffer.toString());
	}

}

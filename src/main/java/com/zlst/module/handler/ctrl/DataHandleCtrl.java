package com.zlst.module.handler.ctrl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zlst.common.config.GlobalConstants;
import com.zlst.module.handler.DataOutputPrcService;
import com.zlst.module.handler.bean.DevAndPlcDto;
import com.zlst.module.handler.bean.DevAndTopicDto;
import com.zlst.param.ObjectToResult;
import com.zlst.param.Result;

import cn.hutool.core.collection.CollUtil;

/**
 * 调度服务
 * @author 
 * @version 
 * CREATE BY AUTOJPA 
 */
@RestController
@RequestMapping("/api/caas/dataOutput")
public class DataHandleCtrl {

	@Autowired
	private DataOutputPrcService dataOutputPrcService;
	
	@PostMapping(value = "/bindDevPlc")
    public Result bindDevPlc(@RequestBody List<DevAndPlcDto> devPlcList) throws Exception {
       
		if(CollUtil.isEmpty(devPlcList)){
			return ObjectToResult.getResult(ObjectToResult.FAIL_CODE, "devPlcList参数为空");
		}
		
		
		devPlcList.forEach(dto -> {
			dataOutputPrcService.savePlcDevRel(dto.getCollectSourceId() + GlobalConstants.HTTP_SEPARATOR + dto.getStandCode(), dto.getEquipmentId() + GlobalConstants.HTTP_SEPARATOR + dto.getEquipParamCode());
		});
		
		
		return ObjectToResult.getResult(ObjectToResult.SUCCESS_CODE, "数据构造成功");
		
    }
	
	@PostMapping(value = "/bindDevTopic")
    public Result bindDevTopic(@RequestBody List<DevAndTopicDto> devTopicList) throws Exception {
       
		if(CollUtil.isEmpty(devTopicList)){
			return ObjectToResult.getResult(ObjectToResult.FAIL_CODE, "devTopicList参数为空");
		}
		
		devTopicList.forEach(dto->{
			dataOutputPrcService.saveDevTopicRel(dto.getDevId(), dto.getTopicName());
		});
		
		return ObjectToResult.getResult(ObjectToResult.SUCCESS_CODE, "数据构造成功");
		
    }
}
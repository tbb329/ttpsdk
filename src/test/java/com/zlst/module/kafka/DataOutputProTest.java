package com.zlst.module.kafka;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.zlst.ZlstApplication;
import com.zlst.common.config.GlobalConstants;
import com.zlst.module.handler.DataOutputPrcService;
import com.zlst.module.handler.bean.DataOutputBean;
import com.zlst.module.handler.bean.PlcToDevDto;
import com.zlst.module.handler.kafka.KafkaHandler;

import cn.hutool.core.collection.CollUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZlstApplication.class)
public class DataOutputProTest {

    @Autowired
    private DataOutputPrcService dataOutputPrcService;

    @Autowired
    private KafkaHandler kafkaHandler;

    @Test
    public void createTopic(){

    }

    @Test
    public void delTopic(){
    	Boolean delTopic = kafkaHandler.deleteTopic("custopic2");
    	System.err.println("delTopic--->" + delTopic);
    }

    @Before
    public void init(){
    	dataOutputPrcService.savePlcDevRel("plc10001" + GlobalConstants.HTTP_SEPARATOR + "A00",  "devD9991" + GlobalConstants.HTTP_SEPARATOR +  "YL");
    	dataOutputPrcService.savePlcDevRel("plc10001" + GlobalConstants.HTTP_SEPARATOR + "A01",  "devD9991" + GlobalConstants.HTTP_SEPARATOR +  "WD");
    	dataOutputPrcService.savePlcDevRel("plc10001" + GlobalConstants.HTTP_SEPARATOR + "A02",  "devD9991" + GlobalConstants.HTTP_SEPARATOR +  "DL");
    	dataOutputPrcService.savePlcDevRel("plc10001" + GlobalConstants.HTTP_SEPARATOR + "A03",  "devD9991" + GlobalConstants.HTTP_SEPARATOR +  "FH");
    	dataOutputPrcService.savePlcDevRel("plc10001" + GlobalConstants.HTTP_SEPARATOR + "A04",  "devD9991" + GlobalConstants.HTTP_SEPARATOR +  "SL");
    	dataOutputPrcService.savePlcDevRel("plc10001" + GlobalConstants.HTTP_SEPARATOR + "A05",  "devD9991" + GlobalConstants.HTTP_SEPARATOR +  "FH1");
    	dataOutputPrcService.savePlcDevRel("plc10001" + GlobalConstants.HTTP_SEPARATOR + "A06",  "devD9991" + GlobalConstants.HTTP_SEPARATOR +  "SL1");
    	dataOutputPrcService.savePlcDevRel("plc10001" + GlobalConstants.HTTP_SEPARATOR + "A07",  "devD9991" + GlobalConstants.HTTP_SEPARATOR +  "FH1");
    	dataOutputPrcService.savePlcDevRel("plc10001" + GlobalConstants.HTTP_SEPARATOR + "A08",  "devD9991" + GlobalConstants.HTTP_SEPARATOR +  "SL1");
    	dataOutputPrcService.savePlcDevRel("plc10001" + GlobalConstants.HTTP_SEPARATOR + "A09",  "devD9991" + GlobalConstants.HTTP_SEPARATOR +  "FH1");
    	dataOutputPrcService.saveDevTopicRel("devD9991", "custopic2");
    }

	public static final SimpleDateFormat format = new SimpleDateFormat("hh:mm:ssS");


    @Test
    public void sendMsg(){
    	while(true){
			List<PlcToDevDto> list = new ArrayList<>();
			Date nowTime = new Date();
			System.err.println("sendmsg, nowTime: " + format.format(nowTime));
			for (int idx = 0; idx < 1; idx++) {
				PlcToDevDto plcTovDto = new PlcToDevDto("plc10001", "A00", format.format(nowTime));
				list.add(plcTovDto);
			}
    		dataOutputPrcService.processData(list);

        	try {
				Thread.sleep(2000L);
			} catch (InterruptedException e) {

			}
    	}
    }

	@Test
	public  void test(){
		System.err.println("test");
	}

    @Test
	public  void testSendMsg(){
		DataOutputBean dataOutputBean = new DataOutputBean("40288cb4685ab65201685ae3590e0014", "001", "11.21", DateUtil.date());
    	dataOutputPrcService.sendMsg(dataOutputBean);
	}
}


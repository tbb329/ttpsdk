package com.zlst.module.kafka;

import com.zlst.ZlstApplication;
import com.zlst.module.handler.cache.DevRealDataCache;
import com.zlst.module.task.dto.AgreementDto;
import com.zlst.module.task.dto.CollectSourceDto;
import com.zlst.module.task.dto.ConnectDto;
import com.zlst.module.task.dto.DispatchDto;
import com.zlst.module.task.service.CollectQueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 缓存功能测试
 * @author xiong.jie/170123
 * @create 2019-02-20 15:51
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZlstApplication.class)
public class DataCacheTest {

    @Autowired
    private CollectQueryService collectQueryService;

    @Test
    public void testopc() throws Exception {

        DispatchDto dispatchDto = new DispatchDto();
        AgreementDto agreementDto = new AgreementDto();
        agreementDto.setAgreementContent("{\"username\":\"administrator\",\"password\":\"123\",\"prgId\":\"ArchestrA.DASSim.1\",\"groupInfo\":[{\"groupName\":\"group1\",\"periodTime\":\"1000\",\"pointInfo\":[{\"pointname\":\"New_PORT_TEST_000.New_PLC_Demo_AI.ai_aqhb_wpfq_nohw\",\"code\":\"wd\",\"key\":0},{\"pointname\":\"New_PORT_TEST_000.New_PLC_Demo_AI.ai_aqhb_wpfq_so2\",\"code\":\"yl\",\"key\":1}],\"key\":0}]\n" +
                "}");
        agreementDto.setAgreementTypeName("opcDA");

        CollectSourceDto colSrcInfoToExeDto = new CollectSourceDto();
        colSrcInfoToExeDto.setCollectSourceId("plc001");
        ConnectDto connectToExeDto = new ConnectDto();
        connectToExeDto.setIp("192.168.10.122");
        connectToExeDto.setCollectSourceId("plc001");
        dispatchDto.setAgreementToExeDto(agreementDto);
        dispatchDto.setColSrcInfoToExeDto(colSrcInfoToExeDto);
        dispatchDto.setConnectToExeDto(connectToExeDto);
        collectQueryService.publishedQuery(dispatchDto, true);
        while(true){
            //collectQueryService.
          //  collectQueryService.getstatus(connectToExeDto.getCollectSourceId());

          //  Thread.sleep(2000);
        }

    }


}

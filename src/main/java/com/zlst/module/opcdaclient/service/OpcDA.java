package com.zlst.module.opcdaclient.service;

import com.zlst.module.core.SdkInterface;
import com.zlst.module.handler.DataOutputPrcService;
import com.zlst.module.handler.bean.PlcToDevDto;
import com.zlst.module.opcdaclient.bean.OpcPointBean;
import com.zlst.module.task.bean.AgreementInfo;
import com.zlst.module.task.dto.ConnectDto;
import com.zlst.module.task.dto.GroupDto;
import com.zlst.module.task.service.CollectQueryService;
import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.*;
import org.openscada.opc.lib.list.ServerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 * Created by 170095 on 2019/2/19.
 */
@Component
public class OpcDA implements SdkInterface {

    private static final Logger logger = LoggerFactory.getLogger(OpcDA.class);
    private ConnectDto connectToExeDto;
    private AgreementInfo agreementInfo;
    private List<GroupDto> groupDtoList;
    private AutoReconnectController autos;
    private Server server;
    private boolean connStatus = false;
    private static DataOutputPrcService dataOutputPrcService;
    private String threadName;

    public OpcDA() {
    }

    public OpcDA(ConnectDto connectToExeDto, AgreementInfo agreementInfo, List<GroupDto> groupDtoList, String threadName) {
        this.connectToExeDto = connectToExeDto;
        this.agreementInfo = agreementInfo;
        this.groupDtoList = groupDtoList;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        final ConnectionInformation ci = new ConnectionInformation();
        String host = this.connectToExeDto.getIp();
        String user = this.agreementInfo.getAgreementMap().get("username");
        String password = this.agreementInfo.getAgreementMap().get("password");
        String progId = this.agreementInfo.getAgreementMap().get("prgId");
        String domain = "";

        try {
            ServerList serverList = new ServerList(host, user, password, domain);
            ci.setClsid(serverList.getClsIdFromProgId(progId));
            ci.setHost(host);
            ci.setUser(user);
            ci.setPassword(password);
            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            server = new Server(ci, exec);
            autos = new AutoReconnectController(server);
            autos.addListener ((AutoReconnectState state) -> {
                if ( state == AutoReconnectState.CONNECTED ) {
                    logger.info("采集源ID:{}与 OPC Server 连接成功",connectToExeDto.getCollectSourceId());
                    this.connStatus = true;
                    CollectQueryService.statusMap.put(threadName, this);
                    groupGetData(server, groupDtoList);
                }else {
                    this.connStatus = false;
                    logger.info("采集源ID:{}与OPC Server已经断开......",connectToExeDto.getCollectSourceId());
                }
            }
            );
            autos.connect();
            logger.info("采集源ID:{}开始连接OPC Server......",connectToExeDto.getCollectSourceId());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (JIException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void disconnect() {
        if (server == null) {
            connStatus = false;
            return;
        }

        autos.disconnect();
        logger.info("OPC Server已经断开......");
        connStatus = false;
        CollectQueryService.statusMap.put(threadName, this);

    }

    @Override
    public void setParam(ConnectDto connectToExeDto, AgreementInfo agreementInfo, List<GroupDto> groupDtoList, String threadName) {
        this.connectToExeDto = connectToExeDto;
        this.agreementInfo = agreementInfo;
        this.groupDtoList = groupDtoList;
        this.threadName = threadName;
    }

    @Override
    public boolean send() {
        return false;
    }

    @Override
    public boolean getStatus() {
        return this.connStatus;
    }

    public void groupGetData(Server server, List<GroupDto> groupDtoList) {
        List<OpcPointBean> opcPointBeanList = new ArrayList<>();
        long periodTime = 1000;
        String srcId = connectToExeDto.getCollectSourceId();
        Group group;
        for (GroupDto groupDto : groupDtoList) {
            try {
                periodTime = groupDto.getPeriodTime();
                group = server.addGroup(groupDto.getGroupName());
                for (Map<String, String> pointMapTemp : groupDto.getPointInfoList()) {
                    String pointName = pointMapTemp.get("pointname");
                    String dataCode = pointMapTemp.get("code");
                    Item tempItem = group.addItem(pointName);
                    tempItem.setActive(true);
                    opcPointBeanList.add(new OpcPointBean(pointName, dataCode, tempItem));
                }
            } catch (NotConnectedException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (JIException e) {
                e.printStackTrace();
            } catch (DuplicateGroupException e) {
                e.printStackTrace();
            } catch (AddFailedException e) {
                e.printStackTrace();
            }
        }
/*        dataOutputPrcService.saveDevTopicRel("devId001", "opctest1");
        dataOutputPrcService.savePlcDevRel("plc001:wd", "devId001:wd");
        dataOutputPrcService.savePlcDevRel("plc001:yl", "devId001:yl");*/
        while (connStatus) {
            List<PlcToDevDto> plcToDevDtos = new ArrayList<>(opcPointBeanList.size());
            if (opcPointBeanList != null || opcPointBeanList.size() != 0) {
                for (OpcPointBean opcPointBeanTemp : opcPointBeanList) {
                    plcToDevDtos.add(new PlcToDevDto(srcId, opcPointBeanTemp.getDataCode(), dumpItem(opcPointBeanTemp.getPointItem())));
                }
                dataOutputPrcService.processData(plcToDevDtos);
            }
            try {
                Thread.sleep(periodTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Autowired
    public void init(DataOutputPrcService dataOutputPrcService) {
        OpcDA.dataOutputPrcService = dataOutputPrcService;
    }

    public String dumpItem(Item item) {
        String value = null;
        try {
            value = item.read(false).getValue().toString().replace("[", "").replace("]", "");
        } catch (JIException e) {
            e.printStackTrace();
        }
        return value;

    }
}

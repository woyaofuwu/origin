
package com.asiainfo.veris.crm.order.soa.group.internetofthings;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class ChgIotGprsPondStateBean extends GroupBean
{
    private static final String iotGrpMainSvcId = "9013";

    private String OperCode; // 操作码: 01-开通，02-关闭

    private String TradeTypeCode;

    private String userId;

    private String serviceId;

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * 其它台帐处理-重点
     */
    @Override
    public void actTradeSub() throws Exception
    {
        // 登记tf_b_trade_svc
        infoRegDataSvc();

        // 登记tf_b_trade_svcstate
        actTradeSvcState();

        super.actTradeSub();
    }

    /**
     * 处理用户的服务
     * 
     * @param data
     * @throws Exception
     */
    public void infoRegDataSvc() throws Exception
    {

        IDataset svcDatas = new DatasetList();

        IDataset iotGrpMain = UserSvcInfoQry.getSvcUserId(userId, iotGrpMainSvcId);
        if (IDataUtil.isNotEmpty(iotGrpMain))
        {
            IData mainSvc = new DataMap();
            mainSvc.putAll(iotGrpMain.getData(0));
            mainSvc.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            mainSvc.put("OPER_CODE", "08"); // 2，08-用户信息变更
            mainSvc.put("IS_NEED_PF", "1");
            svcDatas.add(mainSvc);
        }

        IDataset iotChgStateSvc = UserSvcInfoQry.getSvcUserId(userId, serviceId);
        if (IDataUtil.isNotEmpty(iotChgStateSvc))
        {
            IData iotSvc = new DataMap();
            iotSvc.putAll(iotChgStateSvc.getData(0));
            iotSvc.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            iotSvc.put("OPER_CODE", OperCode); // 操作类型(01-添加本业务, 02-删除本业务)
            iotSvc.put("IS_NEED_PF", "1");
            svcDatas.add(iotSvc);
        }

        if (svcDatas.size() > 0)
        {
            addTradeSvc(svcDatas);
        }
    }

    /**
     * 处理 服务状态
     * 
     * @param data
     * @throws Exception
     */
    protected void actTradeSvcState() throws Exception
    {

        IDataset svcDataset = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, serviceId);

        IDataset svcstateSet = new DatasetList();
        if (IDataUtil.isNotEmpty(svcDataset))
        {
            for (int i = 0; i < svcDataset.size(); i++)
            {
                IData map = svcDataset.getData(i);
                map.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                map.put("END_DATE", getAcceptTime());
                svcstateSet.add(map);
            }
        }

        IData map = new DataMap();
        if ("01".equals(OperCode))
        {
            map.put("STATE_CODE", "0");
        }
        else if ("02".equals(OperCode))
        {
            map.put("STATE_CODE", "E");
        }
        map.put("USER_ID", userId);
        map.put("SERVICE_ID", serviceId);
        map.put("MAIN_TAG", "0");
        map.put("START_DATE", getAcceptTime());
        map.put("END_DATE", SysDateMgr.getTheLastTime());
        map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        map.put("INST_ID", SeqMgr.getInstId()); // 实例ID
        svcstateSet.add(map);

        if (IDataUtil.isNotEmpty(svcstateSet))
        {
            addTradeSvcstate(svcstateSet);
        }
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

    }

    @Override
    protected void makInit(IData data) throws Exception
    {
        super.makInit(data);
        TradeTypeCode = data.getString("TRADE_TYPE_CODE");
        userId = data.getString("USER_ID");
        OperCode = data.getString("OPER_CODE");
        serviceId = data.getString("SERVICE_ID");
    }

    @Override
    protected void makUca(IData data) throws Exception
    {
        makUcaForGrpNormal(data);
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return TradeTypeCode;

    }

}

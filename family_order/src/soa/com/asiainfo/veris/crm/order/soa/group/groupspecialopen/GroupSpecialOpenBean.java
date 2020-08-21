
package com.asiainfo.veris.crm.order.soa.group.groupspecialopen;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class GroupSpecialOpenBean extends ChangeUserElement
{
    protected GroupSpecialOpenReqData reqData = null;

    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 处理服务表
        infoRegDataSvc();

        // 处理服务状态表
        infoRegDataSvcState();

        // 处理集团用户平台服务子表
        infoRegDataPlatsvc();

        // 插入信用特开日志表接口
        InsertSpecialOpen();
    }

    public void InsertSpecialOpen() throws Exception
    {
        String userId = reqData.getUca().getUserId();
        String openHours = reqData.getNarmalTime();
        String remark = "集团业务特殊开机";
        CreditCall.vipAssureOpen(userId, openHours, remark);
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new GroupSpecialOpenReqData();
    }

    public void infoRegDataPlatsvc() throws Exception
    {
        IDataset platsvcList = new DatasetList();
        String userId = reqData.getUca().getUserId();
        String services = reqData.getserverid();
        String[] service_id = services.split(",");

        IData param = new DataMap();
        param.put("USER_ID", userId);

        IDataset platsvcListDataset = UserGrpPlatSvcInfoQry.getUserAttrByUserIda(param);
        if (IDataUtil.isNotEmpty(platsvcListDataset))
        {
            for (int i = 0; i < platsvcListDataset.size(); i++)
            {
                IData platData = platsvcListDataset.getData(i);
                for (int j = 0; j < service_id.length; j++)
                {
                    String serviceId = service_id[j];
                    if (serviceId.equals(platData.getString("SERVICE_ID")))
                    {
                        platData.put("BIZ_STATE_CODE", "A");
                        platData.put("OPER_STATE", "05");
                        platData.put("PLAT_SYNC_STATE", "1");
                        platData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        platsvcList.add(platData);
                    }
                }
            }
            addTradeGrpPlatsvc(platsvcList);
        }
    }

    public void infoRegDataSvc() throws Exception
    {
        IDataset serviceList = new DatasetList();
        String userId = reqData.getUca().getUserId();
        String services = reqData.getserverid();
        String[] service_id = services.split(",");

        IDataset svcDataset = UserSvcInfoQry.qryUserSvcByUserId(userId);
        if (IDataUtil.isNotEmpty(svcDataset))
        {
            for (int i = 0; i < svcDataset.size(); i++)
            {
                IData svcData = svcDataset.getData(i);
                for (int j = 0; j < service_id.length; j++)
                {
                    String serviceId = service_id[j];
                    if (serviceId.equals(svcData.getString("SERVICE_ID")))
                    {
                        svcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        svcData.put("ELEMENT_ID", svcData.getString("SERVICE_ID"));
                        serviceList.add(svcData);
                    }
                }
                if ("1".equals(svcData.getString("MAIN_TAG")))
                {
                    // 当所有业务服务暂停的时候,要暂停主体服务
                    if (service_id.length == svcDataset.size() - 1)
                    {
                        svcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        svcData.put("ELEMENT_ID", svcData.getString("SERVICE_ID"));
                        serviceList.add(svcData);
                    }
                }
            }
            addTradeSvc(serviceList);
        }
    }

    public void infoRegDataSvcState() throws Exception
    {
        String services = reqData.getserverid();
        String[] service_id = services.split(",");//100321
        IDataset svctateList = new DatasetList();
        String userId = reqData.getUca().getUserId();
        IDataset svcStateDataset = UserSvcStateInfoQry.getUserNowSvcStateByUserIdNow(userId, Route.CONN_CRM_CG);
        if (IDataUtil.isNotEmpty(svcStateDataset))
        {
            for (int i = 0; i < svcStateDataset.size(); i++)
            {
                IData svcStateData = svcStateDataset.getData(i);
                String svcID = svcStateData.getString("SERVICE_ID");

                IData svcStateData2 = new DataMap();
                IData svcStateData1 = new DataMap();

                for (int j = 0; j < service_id.length; j++)
                {
                    String serviceId = service_id[j];
                    if (serviceId.equals(svcID))
                    {
                        svcStateData1.putAll(svcStateData);
                        svcStateData1.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        svcStateData1.put("STATE_CODE", "0");
                        svcStateData1.put("RSRV_STR4", "0");// 预留字段4标识是特殊开机恢复
                        svcStateData1.put("START_DATE", getAcceptTime());
                        svcStateData1.put("INST_ID", SeqMgr.getInstId());

                        svcStateData2.putAll(svcStateData);
                        svcStateData2.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        svcStateData2.put("END_DATE", getAcceptTime());

                        svctateList.add(svcStateData2);
                        svctateList.add(svcStateData1);
                    }
                }
                if ("1".equals(svcStateData.getString("MAIN_TAG")))
                {
                    // 当所有业务服务暂停的时候,要暂停主体服务
                    if (service_id.length == svcStateDataset.size() - 1)
                    {
                        svcStateData1.putAll(svcStateData);
                        svcStateData1.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        svcStateData1.put("STATE_CODE", "0");
                        svcStateData1.put("RSRV_STR4", "0");// 预留字段4标识是特殊开机恢复
                        svcStateData1.put("INST_ID", SeqMgr.getInstId());
                        svcStateData1.put("START_DATE", getAcceptTime());

                        svcStateData2.putAll(svcStateData);
                        svcStateData2.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        svcStateData2.put("END_DATE", getAcceptTime());

                        svctateList.add(svcStateData2);
                        svctateList.add(svcStateData1);
                    }
                }
            }
        }
        super.addTradeSvcstate(svctateList);
        infoRegDataUser();
    }

    public void infoRegDataUser() throws Exception
    {
        IData datauser = new DataMap();
        String userId = reqData.getUca().getUserId();
        datauser.put("USER_ID", userId);
        datauser.put("REMOVE_TAG", "0");

        IDataset userDataset = UserInfoQry.getUserInfo(datauser);

        if (userDataset != null && userDataset.size() == 1)
        {
            IData userData = new DataMap();
            userData.putAll(userDataset.getData(0));
            userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            userData.put("USER_STATE_CODESET", "0");
            super.addTradeUser(userData);
        }
        else
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (GroupSpecialOpenReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
        reqData.setserverid(map.getString("sercheck"));
        reqData.setSerialNumber(map.getString("SERIAL_NUMBER"));
        reqData.setNarmalTime(map.getString("NORMAL_TIME"));
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUcaForGrpNormal(map);
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        String productId = reqData.getUca().getProductId();
        BizCtrlInfo bizCtrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.ChangeUserDis);
        String tradeTypeCode = bizCtrlInfo.getTradeTypeCode();
        return tradeTypeCode;
    }

}

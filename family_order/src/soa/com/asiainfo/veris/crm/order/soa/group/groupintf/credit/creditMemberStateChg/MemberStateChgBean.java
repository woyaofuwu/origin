
package com.asiainfo.veris.crm.order.soa.group.groupintf.credit.creditMemberStateChg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

public class MemberStateChgBean extends MemberBean
{
    private String dealFlag = "";

    protected MemberStateChgReqData reqData = null;

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegDataSvcState();
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new MemberStateChgReqData();
    }

    public void infoRegDataSvcState() throws Exception
    {
        String userId = reqData.getUca().getUserId(); // 成员用户
        String userIdA = reqData.getGrpUca().getUserId(); // 集团用户
        String serviceId = "20";
        dealFlag = reqData.getDealFlag();

        IDataset svcDataset = UserSvcInfoQry.getUserProductSvc(userId, userIdA, null);

        int svcCount = UserSvcInfoQry.getUserSvcForModify45(userId, serviceId);

        IDataset svcStates = new DatasetList();
        IDataset svcs = new DatasetList();

        if (IDataUtil.isNotEmpty(svcDataset))
        {
            for (int i = 0; i < svcDataset.size(); i++)
            {
                IDataset temp = new DatasetList();
                IData svcData = svcDataset.getData(i);
                String svc = svcData.getString("SERVICE_ID");
                svcData.put("ELEMENT_ID", svcData.getString("SERVICE_ID"));
                if ("910".equals(svc) || ("20".equals(svc) && svcCount == 1))
                {
                    if (dealFlag.equals("back"))
                    {
                        addSvcState(svcData, temp);
                    }
                    else if (dealFlag.equals("stop"))
                    {
                        delSvcState(svcData, temp);
                    }
                    svcStates.addAll(temp);
                    svcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    svcs.add(svcData);
                }
            }
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_710);

        }
        super.addTradeSvc(svcs);
        super.addTradeSvcstate(svcStates);
    }

    /**
     * 恢复服务状态
     * 
     * @param svcData
     * @param result
     * @return
     * @throws Exception
     */
    public void addSvcState(IData svcData, IDataset result) throws Exception
    {
        String stateCode = "0";

        // 查询该服务是否已在状态表中有记录
        String userId = reqData.getUca().getUserId();
        String serviceId = svcData.getString("ELEMENT_ID");
        IDataset svcDataset = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, serviceId);
        for (int i = 0; i < svcDataset.size(); i++)
        {
            IData map = svcDataset.getData(i);
            if (stateCode.equals(map.getString("STATE_CODE")))
            {
                CSAppException.apperr(GrpException.CRM_GRP_711);
            }
            map.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            map.put("END_DATE", SysDateMgr.getSysTime());

            IData map1 = new DataMap();
            map1.putAll(map);
            map1.put("STATE_CODE", "0"); // 正常
            map1.put("SERVICE_ID", svcData.getString("ELEMENT_ID"));
            map1.put("MAIN_TAG", svcData.getString("MAIN_TAG"));
            map1.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            map1.put("START_DATE", SysDateMgr.getSysTime());
            map1.put("INST_ID", SeqMgr.getInstId());
            map1.put("END_DATE", SysDateMgr.END_DATE_FOREVER);

            result.add(map);
            result.add(map1);
        }

    }

    /**
     * 暂停服务状态
     * 
     * @param svcData
     * @param result
     * @return
     * @throws Exception
     */
    public void delSvcState(IData svcData, IDataset result) throws Exception
    {

        String userId = reqData.getUca().getUserId();
        String serviceId = svcData.getString("ELEMENT_ID");
        IDataset results = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, serviceId);

        for (int i = 0; i < results.size(); i++)
        {
            IData map = results.getData(i);
            if ("5".equals(map.getString("STATE_CODE")))
            {
                CSAppException.apperr(GrpException.CRM_GRP_712);
            }
            map.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            map.put("END_DATE", SysDateMgr.getSysTime());

            IData map1 = new DataMap();
            map1.putAll(map);
            map1.put("STATE_CODE", "5");// 暂停
            map1.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            map1.put("START_DATE", SysDateMgr.getSysTime());
            map1.put("INST_ID", SeqMgr.getInstId());
            map1.put("END_DATE", SysDateMgr.END_DATE_FOREVER);

            result.add(map);
            result.add(map1);
        }
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (MemberStateChgReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        dealFlag = map.getString("DEAL_FLAG");
        reqData.setDealFlag(dealFlag);

    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUcaForMebNormal(map);
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "2958";
    }

    @Override
    protected void chkTradeBefore(IData map) throws Exception
    {

    }

}

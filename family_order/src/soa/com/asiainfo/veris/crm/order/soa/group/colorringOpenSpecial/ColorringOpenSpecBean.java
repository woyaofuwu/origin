
package com.asiainfo.veris.crm.order.soa.group.colorringOpenSpecial;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

public class ColorringOpenSpecBean extends MemberBean
{
    protected ColorringOpenSpecReqData reqData = null;

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegDataSvcState();

        // 调用信控流程，做特殊开机应用，暂时屏蔽（原流程TCC_IntoSpecOpenKeepGroup）
        // callSpecOpenKeep();

    }

    public void callSpecOpenKeep() throws Exception
    {
        String endDate = reqData.getEndDate();
        int hours = Integer.parseInt(endDate);

        String execTime = getOtherHoursOfSysDate(hours);

        IDataset paramSet = new DatasetList();
        IData info = new DataMap();
        info.put("USER_ID", reqData.getUca().getUserId());
        info.put("EXEC_TIME", execTime);
        info.put("REMARK", "特殊开集团彩铃");
        info.put("TRADE_ID", getTradeId());
        info.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        info.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        paramSet.add(info);

        Dao.insert("TI_O_CREDIT_WORK_GROUP", paramSet);

    }

    /**
     * 获取当前时间往后的几个小时
     * 
     * @param hours
     * @return
     * @throws Exception
     */
    private String getOtherHoursOfSysDate(int hours) throws Exception
    {
        String date = getAcceptTime();
        String rulst = "";
        rulst = SysDateMgr.getAddHoursDate(date, hours);
        return rulst;
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new ColorringOpenSpecReqData();
    }

    /**
     * 修改用户服务状态表
     */
    public void infoRegDataSvcState() throws Exception
    {
        String userId = reqData.getUca().getUserId();
        String user_id_a = reqData.getGrpUca().getUserId();

        IDataset svcDataset = UserSvcInfoQry.getUserProductSvc(userId, user_id_a, null);

        String service_id = "20";

        int svcCount = UserSvcInfoQry.getUserSvcForModify45(userId, service_id);

        IDataset svcState = new DatasetList();
        IDataset svcs = new DatasetList();

        for (int i = 0; i < svcDataset.size(); i++)
        {
            IData svcData = svcDataset.getData(i);
            String svc = svcData.getString("SERVICE_ID");
            if ("910".equals(svc) || ("20".equals(svc) && svcCount == 1))
            {
                String svcId = svcData.getString("SERVICE_ID");
                IDataset results = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, svcId);
                for (int j = 0; j < results.size(); j++)
                {
                    IData map = new DataMap();
                    IData map1 = new DataMap();
                    map.putAll(results.getData(j));
                    map1.putAll(results.getData(j));
                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    map.put("END_DATE", getAcceptTime());

                    map1.put("START_DATE", getAcceptTime());
                    map1.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                    map1.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    map1.put("INST_ID", SeqMgr.getInstId()); // 实例标识
                    map1.put("STATE_CODE", "0");

                    svcState.add(map1);
                    svcState.add(map);
                }
                svcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                svcs.add(svcData);
            }
            super.addTradeSvcstate(svcState);
            super.addTradeSvc(svcs);
            svcState.clear();
            svcs.clear();
        }
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (ColorringOpenSpecReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
        reqData.setSerialNumber(map.getString("SERIAL_NUMBER"));
        reqData.setEndDate(map.getString("END_DATE"));
        reqData.setRemark(map.getString("REMARK"));
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUcaForMebNormal(map);
    }

    @Override
    protected void setTradeBase() throws Exception
    {
        super.setTradeBase();

        IData tradeData = bizData.getTrade();

        tradeData.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
        tradeData.put("PRODUCT_ID", reqData.getUca().getProductId());
        tradeData.put("EPARCHY_CODE", reqData.getUca().getUserEparchyCode());
        tradeData.put("BRAND_CODE", reqData.getUca().getBrandCode());
        tradeData.put("CUST_ID", reqData.getUca().getCustId());
        tradeData.put("CUST_NAME", reqData.getUca().getCustomer().getCustName());
        tradeData.put("PSPT_TYPE_CODE", reqData.getUca().getCustomer().getPsptTypeCode()); // 证件类型
        tradeData.put("PSPT_ID", reqData.getUca().getCustomer().getPsptId()); // 证件号码
        tradeData.put("CUST_ID_B", "-1"); // 客户标识B：关联业务中的B客户标识，通常为一集团客户或虚拟客户。对于非关联业务填-1。
        tradeData.put("USER_ID_B", reqData.getGrpUca().getUser().getUserId()); // 用户标识B：关联业务中的B用户标识，通常为一集团用户或虚拟用户。对于非关联业务填-1。
        tradeData.put("SERIAL_NUMBER_B", reqData.getGrpUca().getUser().getSerialNumber()); // 服务号码B
        tradeData.put("ACCT_ID_B", "-1");
        tradeData.put("ACCT_ID", reqData.getUca().getAcctId());
        tradeData.put("REMARK", reqData.getRemark());
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


package com.asiainfo.veris.crm.order.soa.group.grppaymark;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;



public class TradeBreakGrpPayMarkBean extends GroupBean
{
    
    protected BreakGrpPayMarkReqData reqData = null;
    
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        infoRegDataOtherAdd();

    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new BreakGrpPayMarkReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (BreakGrpPayMarkReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        
        reqData.setMarkFlag(map.getString("MARK_FLAG"));
        reqData.setStartDates(map.getString("START_DATES"));
        reqData.setEndDates(map.getString("END_DATES"));
        reqData.setRemarkOther(map.getString("REMARK_OTHER"));
        
    }

    @Override
    protected final void makUca(IData map) throws Exception
    {
        makUcaForGrpNormal(map);
    }
    
    /**
     * 处理OTHER表
     * @throws Exception
     */
    private void infoRegDataOtherAdd() throws Exception
    {
        String markFlag = reqData.getMarkFlag();
        String userId = reqData.getUca().getUserId();
        String productId = reqData.getUca().getProductId();
        
        IDataset otherInfos = UserOtherInfoQry.getOtherInfoByCodeUserId(userId,"SGPR");
        if(IDataUtil.isNotEmpty(otherInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该" + 
                    userId + "已经存在欠费不截止代付关系,不可再新增!");
        }
        
        if(StringUtils.isNotBlank(markFlag) 
                && StringUtils.equals("1", markFlag) )
        {
            
            IData tradeInfo = new DataMap();
            tradeInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            tradeInfo.put("USER_ID", userId);
            tradeInfo.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
            tradeInfo.put("START_DATE", reqData.getStartDates());
            tradeInfo.put("END_DATE", reqData.getEndDates());
            tradeInfo.put("RSRV_VALUE_CODE", "SGPR");
            tradeInfo.put("RSRV_VALUE", productId);
            tradeInfo.put("INST_ID", SeqMgr.getInstId());
            String remark = "";
            remark = reqData.getRemarkOther();
            tradeInfo.put("REMARK", remark);
            super.addTradeOther(tradeInfo);
        }

    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "3906";
    }

}

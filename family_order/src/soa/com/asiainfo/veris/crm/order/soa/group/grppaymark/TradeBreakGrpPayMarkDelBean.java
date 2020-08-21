
package com.asiainfo.veris.crm.order.soa.group.grppaymark;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;



public class TradeBreakGrpPayMarkDelBean extends GroupBean
{
    
    protected BreakGrpPayMarkReqData reqData = null;
    
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        infoRegDataOther();

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
    private void infoRegDataOther() throws Exception
    {
        String userId = reqData.getUca().getUserId();
        IDataset otherInfos = UserOtherInfoQry.getOtherInfoByCodeUserId(userId,"SGPR");
        if(IDataUtil.isEmpty(otherInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该" + 
                    userId + "的欠费不截止代付关系已经不存在,不可再删除!");
        }
        
        IData otherInfo = otherInfos.getData(0);
        otherInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        otherInfo.put("END_DATE", reqData.getEndDates()!=null ? reqData.getEndDates() : SysDateMgr.getSysTime());
        otherInfo.put("REMARK", reqData.getRemarkOther()!=null ? reqData.getRemarkOther() : "");
        super.addTradeOther(otherInfo);
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "3907";
    }

}


package com.asiainfo.veris.crm.order.soa.group.yunmas;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;


public class YunmasynBean extends CreateGroupUser
{
    protected YunmasynReqData reqData = null;

    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    public void actTradeSub() throws Exception
    {
        //super.actTradeSub();
        this.addTradeGrpPlatsvc(reqData.getgrpPlatsvc());
        

        // 成员号码统付
    }
    protected void makUserAcctDay() throws Exception
    {
        
    }



    protected BaseReqData getReqData() throws Exception
    {
        return new YunmasynReqData();
    }
    
    protected void chkTradeAfter() throws Exception
    {
        
    }
    
    protected void setTradeBase() throws Exception
    {
        super.setTradeBase();

        IData map = bizData.getTrade();
        map.put("USER_ID", reqData.getgrpPlatsvc().getString("USER_ID"));
        map.put("ACCT_IS_ADD", true);
    }



    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData =  (YunmasynReqData) getBaseReqData();
    }
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        reqData.setgrpPlatsvc(map);
    }
    @Override
    protected String setTradeTypeCode() throws Exception
    {
        // 设置业务类型
        return "4698";
    }

    @Override
    protected String setOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "4698";
    }
    @Override
    protected String setTradeId() throws Exception
    {

        // 生成业务流水号
        String id = SeqMgr.getTradeId();

        return id;
    }
}

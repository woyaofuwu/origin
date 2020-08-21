
package com.asiainfo.veris.crm.order.soa.group.pocVpnService;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;


public class PocMebRegistVpnBean extends MemberBean
{
    protected PocMebRegistVpnReqData reqData = null;

    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    public void actTradeSub() throws Exception
    {
        //super.actTradeSub();
        this.addTradeSvc(reqData.getSvc());
    }
    
    protected BaseReqData getReqData() throws Exception
    {
        return new PocMebRegistVpnReqData();
    }
    
    protected void setTradeBase() throws Exception
    {
        super.setTradeBase();
    }
    
    protected void chkTradeBefore(IData map) throws Exception
    {

    }
    
    protected void makUca(IData map) throws Exception
    {
       makUcaForMeb(map);
    }
    
    protected  void makUcaForMeb(IData map) throws Exception
    {
        String serialNumber = map.getString("SERIAL_NUMBER");

        UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);

        if (uca == null)
        {

            uca = UcaDataFactory.getNormalUcaForGrp(serialNumber);
        }

        reqData.setUca(uca);
        IData grpData = new DataMap();
        grpData.put("USER_ID", map.getString("EC_USER_ID"));
        UcaData grpUCA = UcaDataFactory.getNormalUcaByUserIdForGrp(grpData);
        reqData.setGrpUca(grpUCA);

    }



    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData =  (PocMebRegistVpnReqData) getBaseReqData();
    }
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        reqData.setSvc(map);
    }
    @Override
    protected String setTradeTypeCode() throws Exception
    {
        // 设置业务类型
        return "4700";
    }

    @Override
    protected String setOrderTypeCode() throws Exception
    {
        return "4700";
    }
    @Override
    protected String setTradeId() throws Exception
    {

        // 生成业务流水号
        String id = SeqMgr.getTradeId();

        return id;
    }
}

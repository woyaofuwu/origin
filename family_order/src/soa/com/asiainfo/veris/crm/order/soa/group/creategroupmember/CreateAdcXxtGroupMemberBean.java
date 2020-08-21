
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.BizData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateAdcXxtGroupMemberReqData;

/**
 * 处理ADC校讯通成员新增信息
 */
public class CreateAdcXxtGroupMemberBean extends MemberBean
{
    protected CreateAdcXxtGroupMemberReqData reqData = null;

    public CreateAdcXxtGroupMemberBean()
    {

    }

    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        this.regTradeOutSnBlackWhite();
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new CreateAdcXxtGroupMemberReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CreateAdcXxtGroupMemberReqData) getBaseReqData();
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUca(map);
        super.makUcaForMebNormal(map);
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        List<BizData> gbd = DataBusManager.getDataBus().getGrpBizData();
        IData mainTrade = gbd.get(0).getTrade();
        reqData.setMainTrade(mainTrade);
        reqData.setBlackWhithOut(map);
    }

    @Override
    protected void makUserAcctDay() throws Exception
    {

    }

    @Override
    protected void regTrade() throws Exception
    {
        IData tradeData = bizData.getTrade();

        IData outData = reqData.getBlackWhithOut();
        IData tradeMain = reqData.getMainTrade();

        if (IDataUtil.isEmpty(outData))
        {
            return;
        }
        tradeData.putAll(tradeMain);

        // 登记其它台账要修改的数据
        tradeData.put("USER_ID", outData.getString("OUT_USER_ID")); // 用户标识
        tradeData.put("SERIAL_NUMBER", outData.getString("OUT_SN")); // 服务号码
    }

    /*
     * @method 登记校讯通业务,主号码绑定的其他号码的blackwhite表
     */
    public void regTradeOutSnBlackWhite() throws Exception
    {
        IData data = reqData.getBlackWhithOut();
        if (IDataUtil.isEmpty(data))
        {
            return;
        }
        data.put("USER_ID", data.getString("OUT_USER_ID"));
        data.put("SERIAL_NUMBER", data.getString("OUT_SN"));
        super.addTradeBlackwhite(data);
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "3644";
    }

}

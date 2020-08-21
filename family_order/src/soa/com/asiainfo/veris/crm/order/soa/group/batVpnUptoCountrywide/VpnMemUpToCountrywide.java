
package com.asiainfo.veris.crm.order.soa.group.batVpnUptoCountrywide;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class VpnMemUpToCountrywide extends ChangeMemElement
{
    private static transient Logger logger = Logger.getLogger(VpnMemUpToCountrywide.class);

    private VpnMemUpToCountrywideReqData reqData = null;

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new VpnMemUpToCountrywideReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (VpnMemUpToCountrywideReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        reqData.setIsChange(true);
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
        reqData.setTradeTypeCode(map.getString("TRADE_TYPE_CODE", ""));
    }

    public VpnMemUpToCountrywide()
    {

    }

    /**
     * 生成其它台帐数据（生成台帐后）
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        super.setTradeTypeCode();
        // 得到业务类型
        String tradeTypeCode = reqData.getTradeTypeCode();

        // 设置业务类型
        return tradeTypeCode;
    }

}


package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.nfcp;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.requestdata.PlatReqData;

/**
 * 向NFCP平台进行确认
 * 
 * @author bobo
 */
public class NFCPConfirmAction implements IProductModuleAction
{

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {

        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        PlatOfficeData officeData = PlatOfficeData.getInstance(pstd.getElementId());
        PlatReqData req = (PlatReqData) btd.getRD();
        if (StringUtils.equals(req.getIsConfirm(), "true"))
        {
            // 发送业务受理确认报文
            IData inParam = new DataMap();
            inParam.put("KIND_ID", "BIP2B781_T2001782_0_0");// 鉴权交易唯一标识
            inParam.put("ROUTETYPE", "00");
            inParam.put("ROUTEVALUE", "000");

            inParam.put("ID_TYPE", "01");// 标识类型:01-手机号码
            inParam.put("SERIAL_NUMBER", uca.getSerialNumber());// 标识值:手机号码
            inParam.put("OPR_NUMB", pstd.getIntfTradeId());
            inParam.put("BIZ_ORDER_RESULT", "0000");
            inParam.put("SP_CODE", officeData.getSpCode());
            inParam.put("BIZ_CODE", officeData.getBizCode());

            IBossCall.dealInvokeUrl("BIP2B781_T2001782_0_0", "IBOSS", inParam);
        }
    }

}

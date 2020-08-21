
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.simcard.WriteCardBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.LocalDecPreData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.LocalDecPreDataRsp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.WebServiceClient;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.selfhelpcard.KIFunc;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.SimCardBean;
import org.apache.commons.lang.StringUtils;

public class ChangeCardRegSVC extends OrderService
{

    public String getOrderTypeCode() throws Exception
    {
        return input.getString("ORDER_TYPE_CODE", "142");
    }

    public String getTradeTypeCode() throws Exception
    {
        return input.getString("TRADE_TYPE_CODE", "142");
    }

    /**
     *  补卡接口REPLACE_CARD_SVCNUM
     *  为实名认证平台提供
     * @return IData
     * @exception
     */
    public IData replaceCard(IData input) throws Exception
    {
        if (log.isDebugEnabled()) {
            log.debug("补卡接口>>>" + input.toString());
        }
        IData result = new DataMap();

        chkParam(input, "TRANSACTION_ID");
        chkParam(input, "SERIAL_NUMBER");
        chkParam(input, "KI");
        chkParam(input, "OPC");
        chkParam(input, "IMSI");
        chkParam(input, "ICCID");
        //利用加密机对KI,OPC加密一次
        transKI(input);
        //先跨区写卡回写
        SimCardBean cardBean = (SimCardBean) BeanManager.createBean(SimCardBean.class);
        cardBean.remoteWriteUpdateByIntf(input);
        //设定SIM卡不占用
        input.put("SIM_NO_OCCUPY_TAG", "1");
        //KI,OPC省内规则加密
        String encKi = input.getString("KI");
        String encOpc = input.getString("OPC");
        KIFunc kifunc = new KIFunc();
        String ki = kifunc.EncryptKI(encKi);
        String opc = kifunc.EncryptKI(encOpc);
        input.put("KI", ki);
        input.put("OPC", opc);

        //最后走普通补卡逻辑
        IDataset resultds =  tradeReg(input);

        String orderId = resultds.getData(0).getString("ORDER_ID","");
        if(orderId.isEmpty()){
            result = resultds.getData(0);
        }else{
            result.put("TRANSACTION_ID", input.getString("TRANSACTION_ID"));
            result.put("RETURN_CODE", "0000");
            result.put("RETURN_MESSAGE", "SUCCESS");
            result.put("IS_SUC", "1");
        }

        return result;
    }

    public IData replaceCardTest(IData input) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("补卡接口>>>" + input.toString());
        }

//        chkParam(input, "TRANSACTION_ID");
//        chkParam(input, "SERIAL_NUMBER");
//        chkParam(input, "KI");
//        chkParam(input, "OPC");
//        chkParam(input, "IMSI");
//        chkParam(input, "ICCID");
        //利用加密机对KI,OPC加密一次
//        transKI(input);
        //先跨区写卡回写
//        SimCardBean cardBean = (SimCardBean) BeanManager.createBean(SimCardBean.class);
//        cardBean.remoteWriteUpdateByIntf(input);
//        //设定SIM卡不占用
//        input.put("SIM_NO_OCCUPY_TAG", "1");
//        //KI,OPC省内规则加密
//        String encKi = input.getString("KI");
//        String encOpc = input.getString("OPC");
//        KIFunc kifunc = new KIFunc();
//        String ki = kifunc.EncryptKI(encKi);
//        String opc = kifunc.EncryptKI(encOpc);
//        input.put("KI", ki);
//        input.put("OPC", opc);
        //最后走普通补卡逻辑
        IDataset resultds = tradeReg(input);
        String orderId = resultds.getData(0).getString("ORDER_ID", "");
        String tradeId = resultds.getData(0).getString("TRADE_ID", "");

        IData returnMap = new DataMap();
        IData object = new DataMap();
        returnMap.put("object", object);
        IDataset result = new DatasetList();
        object.put("result", result);
        IData resultItem = new DataMap();
        result.add(resultItem);
        returnMap.put("rtnCode", "0");
        returnMap.put("rtnMsg", "成功");
        object.put("respCode", "0");
        object.put("respDesc", "success");
        resultItem.put("transactionID", input.getString("TRANSACTION_ID"));
        resultItem.put("isSuc", "1");
        resultItem.put("order_id", orderId);
        resultItem.put("trade_id", tradeId);
        return returnMap;
    }

    /**
     * 异地写卡结果回传及申请开通
     * 为跨区补卡A方案漫入省提供 对应交易编码：BIP2B021_T2000022_1_0
     * @return IData
     * @exception
     */
    public IData replaceCardL2F(IData input) throws Exception
    {
        if (log.isDebugEnabled()) {
            log.debug("异地写卡结果回传及申请开通接口>>>" + input.toString());
        }
        IData result = new DataMap();

        chkParam(input, "SEQ");
        chkParam(input, "SERIAL_NUMBER");
        chkParam(input, "IMSI");
        chkParam(input, "RESULT");

        if (StringUtils.equals("0", input.getString("RESULT"))) {
            chkParam(input, "ENCK");
            chkParam(input, "ENCOPC");
            //chkParam(input, "SIGNATURE");
            //利用加密机对KI,OPC加密一次
            IData encData = new DataMap();
            encData.put("KI", input.getString("ENCK"));
            encData.put("OPC", input.getString("ENCOPC"));
            encData.put("SIGNATURE", input.getString("SIGNATURE"));
            encData.put("LOCAL_PROVCODE", input.getString("LOCAL_PROVCODE"));
            transKI(encData);
            input.put("KI", encData.getString("KI"));
            input.put("OPC", encData.getString("OPC"));
            //先跨区写卡回写

            WriteCardBean writeBean = (WriteCardBean) BeanManager.createBean(WriteCardBean.class);
            IData simDataInfo = writeBean.getSimDataInfo(input.getString("IMSI"));
            if (IDataUtil.isNotEmpty(simDataInfo)) {
            	String cModeType = simDataInfo.getString("CARD_MODE_TYPE","");
            	if (StringUtils.equals(cModeType, "4")) {//CARD_MODE_TYPE=4,个性化资料上传到了实名认证平台
                    //因为卡为在线公司的卡，不会有占用流程（跨区写卡B方案）
                    input.put("SIM_NO_OCCUPY_TAG", "1");
            	}
            }

            SimCardBean cardBean = (SimCardBean) BeanManager.createBean(SimCardBean.class);
            cardBean.remoteWriteUpdateByIntf(input);

            //KI,OPC省内规则加密
            String encKi = input.getString("KI");
            String encOpc = input.getString("OPC");
            KIFunc kifunc = new KIFunc();
            String ki = kifunc.EncryptKI(encKi);
            String opc = kifunc.EncryptKI(encOpc);
            input.put("KI", ki);
            input.put("OPC", opc);
            
            //最后走普通补卡逻辑
            IDataset resultds =  tradeReg(input);

            String orderId = resultds.getData(0).getString("ORDER_ID","");
            if(orderId.isEmpty()){
                result = resultds.getData(0);
            }else{
                result.put("SEQ", input.getString("SEQ"));
                result.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
                result.put("X_RESULTCODE", "0000");
            }
        } else {
            result.put("SEQ", input.getString("SEQ"));
            result.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            result.put("X_RESULTCODE", "5009");
        }

        return result;

    }
    /**
     * 参数校验
     */
    private String chkParam(IData data, String strColName) throws Exception
    {
        String strParam = data.getString(strColName);

        if (StringUtils.isEmpty(strParam))
        {
            StringBuilder strError = new StringBuilder("-1:接口参数检查: 输入参数[");
            strError.append(strColName).append("]不存在或者参数值为空");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, strError);
        }
        return strParam;
    }
    private void transKI(IData input) throws Exception {
        // 加密K和OPC，并写卡参数回传
        LocalDecPreData local = new LocalDecPreData();
        String seqNo = SeqMgr.getTradeId().substring(6);
        local.setSeqNo(seqNo);
        local.setEncPresetDataK(input.getString("KI", ""));
        local.setEncPresetDataOPc(input.getString("OPC", ""));
        local.setLocalProvCode(input.getString("LOCAL_PROVCODE", "898"));
        local.setSignature(input.getString("SIGNATURE", ""));

        WebServiceClient wsc = new WebServiceClient();
        LocalDecPreDataRsp rsp = new LocalDecPreDataRsp();
        try
        {
            rsp = wsc.decPreData(local);

            if ("0".equals(rsp.getResultCode()))
            {
            	String ki = rsp.getPresetData().getK();
            	String opc = rsp.getPresetData().getOPC();
                input.put("KI", ki);
                input.put("OPC", opc);

            } else {
            	String strError = "加密机加解密错误，" + rsp.getResultMessage();
            	CSAppException.apperr(CrmCommException.CRM_COMM_103, strError);
            }
        }
        catch (Exception e)
        {
                log.error(e);
        	String strError = "加密机加解密错误，" + rsp.getResultMessage();
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, strError);

        }
    }
}

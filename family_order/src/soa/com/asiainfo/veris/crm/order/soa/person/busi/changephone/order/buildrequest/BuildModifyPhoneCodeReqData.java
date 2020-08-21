
package com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.requestdata.ModifyPhoneCodeReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardBaseReqData;

public class BuildModifyPhoneCodeReqData extends BaseBuilder implements IBuilder// extends BaseBuildRequestData
{
    public void buildBusiRequestData(IData idata, BaseReqData brd) throws Exception
    {
        ModifyPhoneCodeReqData reqData = (ModifyPhoneCodeReqData) brd;
        reqData.setNewSerialNumber(idata.getString("NEW_SERIAL_NUMBER", ""));
        reqData.setNewSimCardInfo(getSimInfo(idata.getString("NEW_IMSI")));
        reqData.setInherit(idata.getString("INHERIT"));
        reqData.setOldSimCardInfo(getSimInfo(idata.getString("OLD_IMSI")));
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        BaseReqData sard = new ModifyPhoneCodeReqData();
        return sard;
    }

    private SimCardBaseReqData getSimInfo(String imsi) throws Exception
    {
        IDataset simInfos = ResCall.getSimCardInfo("1", "", imsi, "");
        if (IDataUtil.isEmpty(simInfos))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_10);
        }
        IData simInfo = simInfos.getData(0);
        SimCardBaseReqData simCardInfo = new SimCardBaseReqData();
        simCardInfo.setImsi(imsi);
        simCardInfo.setKi(simInfo.getString("KI"));
        simCardInfo.setOpc(simInfo.getString("OPC"));
        simCardInfo.setSimCardNo(simInfo.getString("SIM_CARD_NO"));
        simCardInfo.setResTypeCode(simInfo.getString("RES_TYPE_CODE"));
        simCardInfo.setResKindCode(simInfo.getString("RES_KIND_CODE"));
        simCardInfo.setEmptyCardId(simInfo.getString("EMPTY_CARD_ID", ""));
        simCardInfo.setAgentSaleTag(simInfo.getString("RSRV_STR3", ""));
        simCardInfo.setSaleMoney(simInfo.getString("RSRV_NUM1", ""));
        if (StringUtils.isNotEmpty(simInfo.getString("EMPTY_CARD_ID", "")) && !"1U".equals(simInfo.getString("RES_TYPE_CODE")) && !"1X".equals(simInfo.getString("RES_TYPE_CODE")))
        {
        	IDataset cardInfoSet = ResCall.getEmptycardInfo(simInfo.getString("EMPTY_CARD_ID"), "", "");
            if (IDataUtil.isEmpty(cardInfoSet)){
                CSAppException.apperr(CrmCardException.CRM_CARD_246,simInfo.getString("EMPTY_CARD_ID"));
            }
            IData cardInfo = cardInfoSet.getData(0);
            simCardInfo.setKi(cardInfo.getString("KI"));
            simCardInfo.setOpc(cardInfo.getString("OPC"));
            simCardInfo.setResTypeCode(cardInfo.getString("RES_TYPE_CODE"));
            simCardInfo.setResKindCode(cardInfo.getString("RES_KIND_CODE"));
            String rsrvTag1 = cardInfo.getString("RSRV_TAG1","");
            if("3".equals(rsrvTag1)){
            	simCardInfo.setAgentSaleTag("1");
            	simCardInfo.setSaleMoney(cardInfo.getString("RSRV_NUM1", ""));
            }
        }
        simCardInfo.setSimCardPasswd(simInfo.getString("PASSWORD", ""));// 密文
        simCardInfo.setSimCardPasswdKey(simInfo.getString("KIND", ""));// 密钥
        //simCardInfo.setFeeTag(simInfo.getString("FEE_TAG", "0"));// 0为未买断，1为新买断
        simCardInfo.setFlag4G("0");// 23G卡
        IDataset reSet = ResCall.qrySimCardTypeByTypeCode(simCardInfo.getResTypeCode());
        if (IDataUtil.isNotEmpty(reSet))
        {
        	String typeCode = reSet.getData(0).getString("NET_TYPE_CODE");
        	if("01".equals(typeCode)){
        		simCardInfo.setFlag4G("1");// 4G卡
        	}
        }
        return simCardInfo;
    }

}

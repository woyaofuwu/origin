
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.simcard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;

public class SimCardCheckBean extends CSBizBean
{

    /**
     * 校验白卡
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkEmptyCard(IData input) throws Exception
    {
        String newEmptyCardId = input.getString("EMPTY_CARD_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        IDataset emptyCardInfo = ResCall.getEmptycardInfo(newEmptyCardId, "", "");
        if (IDataUtil.isEmpty(emptyCardInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取白卡信息错误！");
        }
        String cardCityCode = emptyCardInfo.getData(0).getString("CITY_CODE", "");
        String userCityCode = userInfo.getString("CITY_CODE", "");
        if (!cardCityCode.equals(userCityCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "当前用户业务区：" + userCityCode + "，与卡片归属区：" + cardCityCode + "，不同，请更换卡片！");
        }
        return input;
    }

    /**
     * 校验SIM卡
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkSimCard(IData input) throws Exception
    {
        String resNo = input.getString("SIM_CARD_NO", "");

        IDataset simSet = new DatasetList();
        String netTypeCode = input.getString("NET_TYPE_CODE", "");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isNotEmpty(userInfo))
        {
            netTypeCode = userInfo.getString("NET_TYPE_CODE");
        }
        simSet = ResCall.getSimCardInfo("0", resNo, "", "0", netTypeCode);
        if ("18".equals(netTypeCode) || "07".equals(netTypeCode))
        {
            IData reData = simSet.getData(0);
            reData.put("WRITE_TAG", "1");
            return reData;
        }
        if (IDataUtil.isEmpty(simSet))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "SIM卡信息获取错误！"+resNo);
        }

        IData simCardInfo = simSet.getData(0);
        String newResTypeCode = simCardInfo.getString("RES_TYPE_CODE", "");
        String newEmptyCardId = simCardInfo.getString("EMPTY_CARD_ID", "");

        // 如果SIM卡表中EMPTY_CARD_ID字段不为空，表明该卡由白卡写成，到白卡表中取卡类型
        // output.put("NEW_EMPTY_CARD_FLAG", false); // 由白卡写成
        simCardInfo.put("WRITE_TAG", "1");// 熟卡
        if (!newEmptyCardId.equals("") && !"1U".equals(newResTypeCode) && !"1X".equals(newResTypeCode))
        {
            IDataset emptyCardInfo = ResCall.getEmptycardInfo(newEmptyCardId, "", "");
            if (IDataUtil.isNotEmpty(emptyCardInfo))
            {
                simCardInfo.put("RES_TYPE_CODE", emptyCardInfo.getData(0).getString("RES_TYPE_CODE", ""));
                // output.put("NEW_EMPTY_CARD_FLAG", true);
                simCardInfo.put("WRITE_TAG", "0");// 白卡写成
            }
        }
        simCardInfo.put("IS_4G", "0");
        IDataset reSet = ResCall.qrySimCardTypeByTypeCode(simCardInfo.getString("RES_TYPE_CODE"));
        if (IDataUtil.isNotEmpty(reSet))
        {
        	String typeCode = reSet.getData(0).getString("NET_TYPE_CODE");
        	if("01".equals(typeCode)){
        		simCardInfo.put("IS_4G", "1");// 4G卡
        	}
        }
        return simCardInfo;
    }

    /**
     * 选占SIM卡
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData preOccupySimCard(IData input) throws Exception
    {
        String sn = input.getString("SERIAL_NUMBER");
        String simCardNo = input.getString("SIM_CARD_NO", "");
        String writeTag = input.getString("WRITE_TAG");// 0，白卡 1，熟卡
        String remoteMode = "";
        if ("0".equals(writeTag))
        {
            remoteMode = "2";
        }
        // SIM卡选占
        preOccupySimCard(sn, simCardNo, remoteMode, "");
        return input;
    }

    /**
     * SIM卡选占
     * 
     * @param serialNumber
     * @param simCardNo
     * @param remoteMode
     *            0-异地SIM卡写卡；1-异地USIM卡写卡；2-本地写卡；不填默认为成卡开户
     * @throws Exception
     */
    public void preOccupySimCard(String serialNumber, String simCardNo, String remoteMode, String isNotRelease) throws Exception
    {
        String isNp = "0";
        IData uData = MsisdnInfoQry.getCrmMsisonBySerialnumber(serialNumber);
        if (IDataUtil.isNotEmpty(uData))
        {
            String asp = uData.getString("ASP", "");
            if (!"1".equals(asp))
            {
            	isNp = "1";
            }
        }
        String netTypeCode = "";
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isNotEmpty(userInfo)){
        	netTypeCode = userInfo.getString("NET_TYPE_CODE","");
        }
        // SIM卡选占
        ResCall.checkResourceForSim(simCardNo, serialNumber, "0", isNotRelease, "", remoteMode, "1", isNp, "", netTypeCode);
    }
    
    /**
     * SIM卡选占
     * 
     * @param serialNumber
     * @param simCardNo
     * @param remoteMode
     *            0-异地SIM卡写卡；1-异地USIM卡写卡；2-本地写卡；不填默认为成卡开户
     * @param occupyTimeCode 预占时间
     * @throws Exception
     */
    public void preOccupySimCard(String serialNumber, String simCardNo, String remoteMode, String isNotRelease, String occupyTimeCode) throws Exception
    {
        String isNp = "0";
        IData uData = MsisdnInfoQry.getCrmMsisonBySerialnumber(serialNumber);
        if (IDataUtil.isNotEmpty(uData))
        {
            String asp = uData.getString("ASP", "");
            if (!"1".equals(asp))
            {
            	isNp = "1";
            }
        }
        String netTypeCode = "";
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isNotEmpty(userInfo)){
        	netTypeCode = userInfo.getString("NET_TYPE_CODE","");
        }
        // SIM卡选占
        ResCall.checkResourceForSim(simCardNo, serialNumber, "0", isNotRelease, "", remoteMode, "1", isNp, "", netTypeCode, occupyTimeCode);
    }

}


package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class SimCardCheckBean extends CSBizBean
{
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
     * 校验SIM卡信息
     * 
     * @params PageData,TradeData
     * @return IData
     * @exception
     */
    public IData verifySimCard(IData input) throws Exception
    {
        IData output = new DataMap();
        String writeTag = input.getString("WRITE_TAG", "");
        IData newSimCardInfo = new DataMap(input.getString("NEW_SIM_CARD_INFO", "{}"));
        String newSimCardNo = newSimCardInfo.getString("SIM_CARD_NO");

        IData oldSimCardInfo = new DataMap(input.getString("OLD_SIM_CARD_INFO", "{}"));
        String serialNumber = input.getString("SERIAL_NUMBER");

        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);

        output.putAll(newSimCardInfo);
        String newResTypeCode = newSimCardInfo.getString("RES_TYPE_CODE", "");
        // 添加USIM卡判断
        // String oldSimOPC = oldSimCardInfo.getString("OPC", "");
        String newSimOPC = newSimCardInfo.getString("OPC", "");
        // IDataset oldUsim4G = ResParaInfoQry.checkUser4GUsimCard(oldSimCardInfo.getString("RES_TYPE_CODE",
        // "").substring(1));
        IDataset newUsim4G = ResCall.qrySimCardTypeByTypeCode(newResTypeCode);
        // if (StringUtils.isNotEmpty(oldSimOPC) && IDataUtil.isNotEmpty(oldUsim4G) && (StringUtils.isEmpty(newSimOPC)
        // || IDataUtil.isEmpty(newUsim4G)))
        // {
        // // common.error("用户为LTE-4GUSIM卡用户，不能换为其他类型的卡");
        // CSAppException.apperr(CrmCardException.CRM_CARD_239);
        //
        // }
        if(IDataUtil.isEmpty(newUsim4G)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据新SIM卡的资源类型编码获取不到资源类型名称");
        }
        output.put("IS_4G", "0");
        if (StringUtils.isNotEmpty(newSimOPC) && "01".equals(newUsim4G.getData(0).getString("NET_TYPE_CODE")))
        {
        	output.put("IS_4G", "1");// 4G卡
            IDataset userSvc = UserSvcInfoQry.getSvcUserId(userInfo.getString("USER_ID"), "22");
/*            if (IDataUtil.isEmpty(userSvc))
            {
                // common.error("为了保证号码正常使用，办理USIM卡换卡业务前，请先开通GPRS功能。");
                CSAppException.apperr(CrmCardException.CRM_CARD_240);
            }*/

            boolean lowDiscnt = false;
            String lowDiscntCode = "";
            // 与与4GUSIM卡互斥的卡互斥的优惠
            IDataset userDiscntInfo = new DatasetList();
            IDataset userDiscntInfoTmp = UserDiscntInfoQry.getAllValidDiscntByUserId(userInfo.getString("USER_ID"));
            for (int i = 0; i < userDiscntInfoTmp.size(); i++)
            {
                String discntCode = userDiscntInfoTmp.getData(i).getString("DISCNT_CODE");
                IDataset discntSet = CommparaInfoQry.getCommparaInfoBy5("CSM", "8550", "4G", discntCode, "ZZZZ", null);
                if (IDataUtil.isNotEmpty(discntSet))
                {
                    userDiscntInfo.add(userDiscntInfoTmp.getData(i));
                }
            }

            if (IDataUtil.isNotEmpty(userDiscntInfo))
            {
                lowDiscnt = true;
                for (int i = 0; i < userDiscntInfo.size(); i++)
                {
                    IData userDiscnt = userDiscntInfo.getData(i);
                    lowDiscntCode += "".equals(lowDiscntCode) ? userDiscnt.getString("DISCNT_CODE", "") : ',' + userDiscnt.getString("DISCNT_CODE", "");
                }

            }

            if (lowDiscnt)
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_241, lowDiscntCode);
                // common.error("该号码已办理的优惠中，与USIM卡换卡业务存在互斥，存在互斥的优惠编码为["+lowDiscntCode+"]");
            }
        }
//        if ("01".equals(newUsim4G.getData(0).getString("NET_TYPE_CODE")))
//        {
//            IDataset tipInfo = ResParaInfoQry.checkUser4GUsimCard(oldSimCardInfo.getString("RES_TYPE_CODE","").substring(1));
//            if(IDataUtil.isNotEmpty(tipInfo)){
//            	output.put("NOTICE_CONTENT", newUsim4G.getData(0).getString("PARA_VALUE8", ""));
//            }
//        }

        // 查询是否要卡费
        SimCardBean cBean = BeanManager.createBean(SimCardBean.class);
        String netTypeCode = userInfo.getString("NET_TYPE_CODE");
        String tradeTypeCode = "142";
        if ("18".equals(netTypeCode))
        {
            tradeTypeCode = "3821";
        }
        IData pData = cBean.getSimCardPrice(oldSimCardInfo.getString("SIM_CARD_NO"), newSimCardInfo.getString("SIM_CARD_NO"), serialNumber, tradeTypeCode);
        output.put("FEE_DATA", pData);

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("SIM_CARD_NO", newSimCardNo);
        param.put("WRITE_TAG", writeTag);
        preOccupySimCard(param);
        return output;
    }
    
    
    public IDataset checkEmptyCard(IData input) throws Exception
    {
    	IDataset empInfos = ResCall.getEmptycardInfo(input.getString("EMPTY_CARD_ID",""), "", "IDLE");
    	if(IDataUtil.isEmpty(empInfos)){
    		CSAppException.apperr(CrmCardException.CRM_CARD_144);
    	}
    	String resTypeCode = empInfos.getData(0).getString("RES_TYPE_CODE","");
    	IDataset newUsim4G = ResCall.qrySimCardTypeByTypeCode(resTypeCode);
    	if(IDataUtil.isEmpty(newUsim4G)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据SIM卡的资源类型编码获取不到资源类型名称");
    	}
    	if ("01".equals(newUsim4G.getData(0).getString("NET_TYPE_CODE")))
    	{
    		String sn = input.getString("SERIAL_NUMBER","");
    		IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
    		IDataset userSvc = UserSvcInfoQry.getSvcUserId(userInfo.getString("USER_ID"), "22");
    		String errorInfo = "";
/*    		if (IDataUtil.isEmpty(userSvc))
    		{
    			errorInfo += " 办理USIM卡换卡业务前，请先开通GPRS功能。 ";
    			//CSAppException.apperr(CrmCardException.CRM_CARD_240);
    		}*/

    		boolean lowDiscnt = false;
    		String lowDiscntCode = "";
    		// 与4GUSIM卡互斥的优惠
    		IDataset userDiscntInfo = new DatasetList();
    		IDataset userDiscntInfoTmp = UserDiscntInfoQry.getAllValidDiscntByUserId(userInfo.getString("USER_ID"));
    		for (int i = 0; i < userDiscntInfoTmp.size(); i++)
    		{
    			String discntCode = userDiscntInfoTmp.getData(i).getString("DISCNT_CODE");
    			IDataset discntSet = CommparaInfoQry.getCommparaInfoBy5("CSM", "8550", "4G", discntCode, "ZZZZ", null);
    			if (IDataUtil.isNotEmpty(discntSet))
    			{
    				userDiscntInfo.add(userDiscntInfoTmp.getData(i));
    			}
    		}

    		if (IDataUtil.isNotEmpty(userDiscntInfo))
    		{
    			lowDiscnt = true;
    			for (int i = 0; i < userDiscntInfo.size(); i++)
    			{
    				IData userDiscnt = userDiscntInfo.getData(i);
    				String discntName =StaticUtil.getStaticValue(getVisit(), "TD_B_DISCNT","DISCNT_CODE" , "DISCNT_NAME",userDiscnt.getString("DISCNT_CODE",""))+"|"+userDiscnt.getString("DISCNT_CODE","");
    				lowDiscntCode += "".equals(lowDiscntCode) ? discntName : '，' + discntName;
    			}
    		}

    		if (lowDiscnt)
    		{
    			//该号码已办理的优惠中，与USIM卡换卡业务存在互斥，存在互斥的优惠编码为
    			String discntInfo = " 该号码已办理的优惠中，与USIM卡换卡业务存在互斥，优惠名称为：【"+lowDiscntCode+"】";
    			errorInfo += discntInfo;
    			//CSAppException.apperr(CrmCardException.CRM_CARD_241, lowDiscntCode);
    		}
    		if(StringUtils.isNotEmpty(errorInfo)){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,errorInfo);
    		}
    	}
        return empInfos;
    }

}

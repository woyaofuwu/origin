
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate;

import org.apache.log4j.Logger;

import com.ailk.bizservice.base.CSAppCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.ChangeSvcStateRegSVC;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeSvcStateIntfSVC.java
 * @Description: ChangeSvcStateIntfSVC
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-6-16 上午9:46:02
 */
public class ChangeSvcStateIntfSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    private static transient final Logger logger = Logger.getLogger(ChangeSvcStateIntfSVC.class);

    /**
     * 挂失接口：ITF_CRM_ModifyLostReg
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset createLostReg(IData input) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>进入挂失接口>>>>>>>>>>>>>>入参为：" + input.toString());
        }
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        // 外围接口入参，modify_tag=0: 申请挂失，modify_tag=1: 解挂
        String modifyTag = input.getString("MODIFY_TAG", "");
        if (StringUtils.isNotEmpty(modifyTag))
        {
            if (StringUtils.equals("0", modifyTag))// 挂失
            {
                input.put("TRADE_TYPE_CODE", "132");
            }
            else if (StringUtils.equals("1", modifyTag))// 加挂
            {
                input.put("TRADE_TYPE_CODE", "133");
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_248);
            }
        }
        else
        {
            input.put("TRADE_TYPE_CODE", "132");
        }
        return this.tradeReg(input);
    }

    /**
     * 局方停开机 ITF_CRM_ModifyOfficeStopOpenReg
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset createOfficeStopOpenReg(IData input) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>进入局方停开机接口>>>>>>>>>>>>>>入参为：" + input.toString());
        }
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "TRADE_TYPE_CODE");
        /*
         * if(StringUtils.equals("136", input.getString("TRADE_TYPE_CODE"))) { input.put("TRADE_TYPE_CODE", "136");
         * }else { input.put("TRADE_TYPE_CODE", "126"); }
         */
        return this.tradeReg(input);
    }
    
    /**
     * 物联网开机接口
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset WlwOpenReg(IData input) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>进入物联网开机接口>>>>>>>>>>>>>>入参为：" + input.toString());
        }
        String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
        String strStaffId = IDataUtil.chkParam(input, "STAFF_ID");
        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
        IDataset idsInfos = RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(uca.getUserId(), "9A", null);
        if(IDataUtil.isEmpty(idsInfos))
        {
        	CSAppException.apperr(TradeException.CRM_TRADE_95, "该手机号码不是集团产品成员。");
        }
        
        IDataset idsWlwStopReg = CommparaInfoQry.getCommPkInfo("CSM", "1665", "WlwStopReg", "0898");
        if(IDataUtil.isEmpty(idsWlwStopReg))
        {
        	CSAppException.apperr(TradeException.CRM_TRADE_95, "该工号与集团产品编码不对应。");
        }
        
        boolean isGroup = false;
		for (int k = 0; k < idsInfos.size(); k++) 
        {
        	IData idInfos = idsInfos.getData(k);
        	String strSn_A = idInfos.getString("SERIAL_NUMBER_A");
        	for (int i = 0; i < idsWlwStopReg.size(); i++) 
            {
        		IData idWlwStopReg = idsWlwStopReg.getData(i);
            	String strP1 = idWlwStopReg.getString("PARA_CODE1", "");
            	String strGsn = idWlwStopReg.getString("PARA_CODE2", "");
            	 if(strSn_A.equals(strGsn) && strStaffId.equals(strP1))
                 {
            		 isGroup = true;
                 }
            }
        }
    	
        if(!isGroup)
        {
        	CSAppException.apperr(TradeException.CRM_TRADE_95, "该号码与工号下的集团产品编码不对应。");
        }
        return this.createOpenReg(input);
    }

    /**
     * 开机接口：ITF_CRM_ModifyOpenReg
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset createOpenReg(IData input) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>进入开机接口>>>>>>>>>>>>>>入参为：" + input.toString());
        }
        String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
        String inModeCode = getVisit().getInModeCode();
        // 一级BOSS落地方异地停开机密码证件较验
        if (("6".equalsIgnoreCase(inModeCode) || "N".equalsIgnoreCase(inModeCode)) && input.getBoolean("IS_CHECK_PSPT", true))
        {
            IData idata = new DataMap();
            idata.put("SERIAL_NUMBER", serialNumber);
            idata.put("PSPT_TYPE_CODE", input.getString("IDCARDTYPE"));
            idata.put("PSPT_ID", input.getString("IDCARDNUM"));
            idata.put("USER_PASSWD", input.getString("USER_PASSWD"));
            idata.put("X_TAG", "0");
            idata.put("IDTYPE", "01");
            VipSpeOpenIntfBean bean = new VipSpeOpenIntfBean();
            bean.checkPsptVipPwd(idata);
        }
        input.put("TRADE_TYPE_CODE", "133");
        return this.tradeReg(input);
    }

    /**
     * 大客户特殊开机 ITF_CRM_VipSpecialOpen
     * 
     * @Function: createSpecOpenReg
     * @Description:
     * @date Jul 17, 2014 4:11:09 PM
     * @param input
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset createSpecOpenReg(IData input) throws Exception
    {
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        if (StringUtils.isBlank(tradeTypeCode))
        {
            input.put("TRADE_TYPE_CODE", "7304");
        }

        String xManageMode = input.getString("X_MANAGEMODE");
        if (StringUtils.isBlank(xManageMode))
        {
            input.put("X_MANAGEMODE", "1");
        }

        VipSpeOpenIntfBean vipbean = new VipSpeOpenIntfBean();
        return IDataUtil.idToIds(vipbean.VipSpenOpen(input));
    }
    
    /**
     * 物联网停机接口
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset WlwStopReg(IData input) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>进入物联网停机接口>>>>>>>>>>>>>>入参为：" + input.toString());
        }
        String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
        String strStaffId = IDataUtil.chkParam(input, "STAFF_ID");
        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
        IDataset idsInfos = RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(uca.getUserId(), "9A", null);
        if(IDataUtil.isEmpty(idsInfos))
        {
        	CSAppException.apperr(TradeException.CRM_TRADE_95, "该手机号码不是集团产品成员。");
        }
        
        IDataset idsWlwStopReg = CommparaInfoQry.getCommPkInfo("CSM", "1665", "WlwStopReg", "0898");
        if(IDataUtil.isEmpty(idsWlwStopReg))
        {
        	CSAppException.apperr(TradeException.CRM_TRADE_95, "该工号与集团产品编码不对应。");
        }
        
        boolean isGroup = false;
		for (int k = 0; k < idsInfos.size(); k++) 
        {
        	IData idInfos = idsInfos.getData(k);
        	String strSn_A = idInfos.getString("SERIAL_NUMBER_A");
        	for (int i = 0; i < idsWlwStopReg.size(); i++) 
            {
        		IData idWlwStopReg = idsWlwStopReg.getData(i);
            	String strP1 = idWlwStopReg.getString("PARA_CODE1", "");
            	String strGsn = idWlwStopReg.getString("PARA_CODE2", "");
            	 if(strSn_A.equals(strGsn) && strStaffId.equals(strP1))
                 {
            		 isGroup = true;
                 }
            }
        }
		
        if(!isGroup)
        {
        	CSAppException.apperr(TradeException.CRM_TRADE_95, "该号码与工号下的集团产品编码不对应。");
        }
        return this.createStopReg(input);
    }

    /**
     * 停机接口 ：ITF_CRM_ModifyStopReg
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset createStopReg(IData input) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>进入停机接口>>>>>>>>>>>>>>入参为：" + input.toString());
        }
        String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
        String inModeCode = getVisit().getInModeCode();
        if ((StringUtils.equals("6", inModeCode) || StringUtils.equals("N", inModeCode)) && input.getBoolean("IS_CHECK_PSPT", true))
        {
            IData idata = new DataMap();
            idata.put("SERIAL_NUMBER", serialNumber);
            idata.put("PSPT_TYPE_CODE", input.getString("IDCARDTYPE"));
            idata.put("PSPT_ID", input.getString("IDCARDNUM"));
            idata.put("USER_PASSWD", input.getString("USER_PASSWD"));
            idata.put("X_TAG", "0");
            idata.put("IDTYPE", "01");
            VipSpeOpenIntfBean bean = new VipSpeOpenIntfBean();
            bean.checkPsptVipPwd(idata);
        }

        input.put("TRADE_TYPE_CODE", "131");
        return this.tradeReg(input);
    }

    /**
     * @methodName: createVipAssureOpenReg
     * @Description: 大客户担保开机接口，老系统接口ITF_CRM_VipAssureOpen
     * @version: v1.0.0
     * @author: xiaozb
     * @date: 2014-6-16 上午9:48:38
     */
    public IDataset createVipAssureOpenReg(IData input) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>大客户担保开机接口>>>>>>>>>>>>>>入参为：" + input.toString());
        }
        String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");// 开机号码
        IDataUtil.chkParam(input, "RSRV_STR1"); // 担保开机大客户号码
        String vipAssureTime = IDataUtil.chkParam(input, "RSRV_STR2"); // 担保开机时间（小时）

        VipSpeOpenIntfBean bean = new VipSpeOpenIntfBean();
        IData data = bean.checkVipAssureOpenReg(input);

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("GUATANTEE_USER_ID", data.getString("GUATANTEE_USER_ID"));
        param.put("OPEN_HOURS", vipAssureTime);
        param.put("REMARK", "大客户担保开机接口");
        param.put("TRADE_TYPE_CODE", "492");

        return this.tradeReg(param);
    }

    /**
     * 大客户停开机接口ITF_CRM_VipStopOpenReg
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     * @author xiaozb
     */
    public IDataset createVipStopOpenReg(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "BIND_SERIAL_NUMBER");
        String tradeTypeCode = IDataUtil.chkParam(input, "TRADE_TYPE_CODE");

        if (!StringUtils.equals("131", tradeTypeCode) // 大客户报停
                && !StringUtils.equals("133", tradeTypeCode)) // 大客户报开
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "无法识别业务操作类型(报停/报开)");
        }

        VipSpeOpenIntfBean vipbean = new VipSpeOpenIntfBean();
        // 检查大客户信息和客户经理信息
        vipbean.checkCustAndManger(input);
        return tradeReg(input);
    }

    /**
     * 最终调到服务状态变更类的业务登记服务上
     * 
     * @param input
     * @return
     * @throws Exception
     */
    private IDataset tradeReg(IData input) throws Exception
    {
        ChangeSvcStateRegSVC regSvc = new ChangeSvcStateRegSVC();
        return regSvc.tradeReg(input);
    }
    
    /**
     * 转换传入接口入参
     */
    public void setTrans(IData input){
    	if ("6".equals(getVisit().getInModeCode())){  //渠道 ：热线
        	if (!"".equals(input.getString("MSISDN", ""))){
        		input.put("SERIAL_NUMBER", input.getString("MSISDN", ""));
                return;
        	}
        }
    }
    
    /**
     * 查询用户一次收费套餐
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryFPOneTimePay(IData input) throws Exception
    {
    	String strSerialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");// 手机号码
		
		IData idParam = new DataMap();
		idParam.put("SERIAL_NUMBER", strSerialNumber);
		//调用账务接口
		IDataset idsQryFPOneTimePay = CSAppCall.call("AM_CRM_QryFPOneTimePay", idParam);
		
		return idsQryFPOneTimePay;
    }
    
}

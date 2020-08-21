
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.acct.UAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UVipClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeSvcStateSVC.java
 * @Description: 服务状态变更服务类
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 20140318 下午5:14:39
 */
public class ChangeSvcStateSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;
    
    /**
     * IMS家庭固话类型
     */
    public static final String RELATION_TYPE_CODE="MS"; //关系类型

    /**
     * @methodName: checkGuaranteeUser
     * @Description: 客户担保开机校验担保用户的担保资格，细节如下： 查询担保客户上次的担保记录,不存在,则本次担保校验通过 否,则查询上次被担保客户的服务号,校验是否正常开通,是通过,并结束与本次担保客户的担保关系
     *               否,则查询上次担保期间,是否仍在欠费,否,通过 是,则本次校验失败.本次担保客户不能担保
     * @version: v1.0.0
     * @author: xiaozb
     * @throws Exception
     * @date: 2014-3-28 上午11:17:47
     */
    private boolean checkGuaranteeUser(String guaranteeUserId) throws Exception
    {
        // TODO Auto-generated method stub
        // IDataset custVipDataset = CustVipInfoQry.getCustVipByUserId(guaranteeUserId, "0",
        // CSBizBean.getTradeEparchyCode());
        // if (IDataUtil.isEmpty(custVipDataset))
        // {
        // CSAppException.apperr(CustException.CRM_CUST_138);
        // }
        // 查找上次的担保记录是否存在,存在走后续校验,不存在结束本次校验.
        IDataset otherInfoDataset = UserOtherInfoQry.getUserOtherInfoByAll(guaranteeUserId, "GUAT");
        if (IDataUtil.isEmpty(otherInfoDataset))
        {
            return true;
        }
        DataHelper.sort(otherInfoDataset, "START_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        IData otherInfoDataLast = otherInfoDataset.getData(0);
        // 如果上次担保已结束返回true
        if (StringUtils.equals("1", otherInfoDataLast.getString("RSRV_TAG1")))
        {
            return true;
        }
        String serialNumber = otherInfoDataLast.getString("RSRV_VALUE");
        IData snInfoData = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(snInfoData))
        {
            CSAppException.apperr(CustException.CRM_CUST_1001);
        }
        String userId = snInfoData.getString("USER_ID");
        IDataset svcStateDataset = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, "0", CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isEmpty(svcStateDataset))
        {
            CSAppException.apperr(CustException.CRM_CUST_1001);
        }
        else
        {
            String stateCode = svcStateDataset.getData(0).getString("STATE_CODE");
            // 校验上次被担保客户的服务号是否正常开通,是通过,结束与本次担保客户的担保关系
            if (StringUtils.equals("0", stateCode))
            {
                IData param = new DataMap();
                param.put("INST_ID", svcStateDataset.getData(0).getString("INST_ID"));
                param.put("RSRV_TAG1", "1");
                Dao.update("TF_F_USER_OTHER", param);
                return true;
            }
        }

        // 需要跟账管谈接口，查询上次担保期间,被担保用户是否仍在欠费,否,通过；是，不通过
        IDataset acctInfo = UAcctInfoQry.qryAcctInfoByCustId(snInfoData.getString("CUST_ID"));
        if (IDataUtil.isEmpty(acctInfo))
        {
            CSAppException.apperr(CustException.CRM_CUST_1001);
        }

        // IData billBalance = AcctCall.queryOweBillBalanceByCycleId(userId, acctInfo.getData(0).getString("ACCT_ID"),
        // cycleId);
        return false;
    }

    /**
     * 查询担保开机用户相关信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryGuaranteeInfo(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        // 查询用户信息
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
        }
        else
        {
            if (!StringUtils.equals("0", userInfo.getString("USER_STATE_CODESET")))
            {
                // 状态不为正常状态，不能担保开机
                CSAppException.apperr(CrmUserException.CRM_USER_771, serialNumber);
            }
        }
        String userId = userInfo.getString("USER_ID");

        // 查询用户等级是否符合开机条件
        IData userCreditInfo = CreditCall.queryUserCreditInfos(userId);
        if (IDataUtil.isEmpty(userCreditInfo))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "该用户的信用等级异常,无法办理该业务！");
        }
        int creditClass = userCreditInfo.getInt("CREDIT_CLASS", -1);
        // IDataset classIdParam = CommparaInfoQry.getCommparaByCodeCode1(
        // "CSM", "8846", "KAIJI", creditClass);
        if (creditClass <= 4)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1020, "5星级");
        }
        IDataset eparchyLimit = CommparaInfoQry.queryCommparaByCodeAndName("496", "GOSE", String.valueOf(creditClass), "CSM", "4900");
        if (IDataUtil.isNotEmpty(eparchyLimit))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "本地市限制此星级办理紧急开机业务！");
        }

        IDataset commparaInfo = CommparaInfoQry.queryCommparaByCodeAndName("496", "GOSC", String.valueOf(creditClass), "CSM", "4900");
        if (IDataUtil.isEmpty(commparaInfo))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "获取开机时间异常,无法办理该业务！");
        }

        // 查询本月是否已经给其他用户担保开机
        IDataset otherInfos = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "GUAT");
        if (IDataUtil.isNotEmpty(otherInfos))
        {
            // 当月已经存在给用户【】担保开机，本月不能再次担保开机。
            CSAppException.apperr(TradeException.CRM_TRADE_324, otherInfos.getData(0).getString("RSRV_VALUE"));
        }
        // 校验用户的担保资格
        if (!checkGuaranteeUser(userId))
        {
            CSAppException.apperr(CustException.CRM_CUST_1002);
        }
        // 查询客户信息
        IData custInfo = UcaInfoQry.qryCustInfoByCustId(userInfo.getString("CUST_ID"));
        if (IDataUtil.isEmpty(custInfo))
        {
            CSAppException.apperr(CustException.CRM_CUST_13, serialNumber);
        }

        IData data = new DataMap();
        data.put("CREDIT_CLASS", creditClass);
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("USER_ID", userInfo.getString("USER_ID"));
        data.put("CUST_NAME", custInfo.getString("CUST_NAME"));
        data.putAll(userCreditInfo);
        data.put("OPEN_HOURS", commparaInfo.getData(0).getString("PARA_CODE2"));
        return IDataUtil.idToIds(data);
    }

    /**
     * 查询用户紧急开机相关信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryUserEmergencyInfo(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        IData userCreditInfo = CreditCall.queryUserCreditInfos(userId);
        // userCreditInfo.put("CREDIT_CLASS", "4");
        // userCreditInfo.put("CREDIT_CLASS_NAME", "4星");
        if (IDataUtil.isNotEmpty(userCreditInfo))
        {
            String creditClass = userCreditInfo.getString("CREDIT_CLASS", "-1");

            IDataset eparchyLimit = CommparaInfoQry.queryCommparaByCodeAndName("497", "EOSE", creditClass, "CSM", "4900");
            if (IDataUtil.isNotEmpty(eparchyLimit))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "本地市限制此星级办理紧急开机业务！");
            }
            if (Integer.parseInt(creditClass) <= 3)
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "该用户的信用等级不符合条件，三星及三星以下客户不能办理该业务！");
            }

            IDataset infos = CommparaInfoQry.queryCommparaByCodeAndName("497", "EOSC", creditClass, "CSM", "4900");
            String openHours = "",openAmount= "";
            if (IDataUtil.isNotEmpty(infos))
            {
                openHours = infos.getData(0).getString("PARA_CODE2");
                openAmount = infos.getData(0).getString("PARA_CODE4");
            }
            if (StringUtils.isEmpty(openHours))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "获取开机时间异常,无法办理该业务！");
            }
            if (StringUtils.isEmpty(openAmount))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "获取开机额度异常,无法办理该业务！");
            }
            userCreditInfo.put("OPEN_HOURS", openHours);// 紧急开机时间
            userCreditInfo.put("OPEN_AMOUNT", openAmount);// 紧急开机额度
            
            //如果具有UNLIMIT_EMGENCYOPEN权限的工号，额度为20元@tanzheng@20171016	 	 
            if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "UNLIMIT_EMGENCYOPEN")){ 	 	 
            	 userCreditInfo.put("OPEN_AMOUNT", 20);// 紧急开机额度
            }	 	 
            //如果具有UNLIMIT_EMGENCYOPEN权限的工号，额度为20元@tanzheng@20171016	 	 
            
            IDataset usersvc = UserSvcStateInfoQry.queryUserSvcStateInfo(userId);
            if (IDataUtil.isEmpty(usersvc))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "该用户的状态不符合紧急开机条件！(欠费半停，欠费停机，高额半停，高额停机)");
            }
        }
        else
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "该用户的信用等级异常,无法办理该业务！");
        }

        IDataset res = new DatasetList();
        res.add(userCreditInfo);
        return res;
    }

    /**
     * @methodName: queryVipAssureSnInfo
     * @Description: 查询大客户担保信息
     * @version: v1.0.0
     * @author: xiaozb
     * @date: 2014-3-25 上午9:12:18
     */
    public IDataset queryVipAssureSnInfo(IData input) throws Exception
    {
        String vipSerialNumber = input.getString("SERIAL_NUMBER");
        IData vipUserData = UcaInfoQry.qryUserInfoBySn(vipSerialNumber);
        if (IDataUtil.isEmpty(vipUserData))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, vipSerialNumber);
        }
        String userId = vipUserData.getString("USER_ID");
        IData vipSnInfoData = new DataMap();
        IDataset qryCustVipInfos = CustVipInfoQry.getCustVipByUserId(userId, "0", CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isEmpty(qryCustVipInfos))
        {
            CSAppException.apperr(CustException.CRM_CUST_225, vipSerialNumber);
        }
        IData vipCustData = UcaInfoQry.qryCustInfoByCustId(vipUserData.getString("CUST_ID"));
        if (IDataUtil.isEmpty(vipCustData))
        {
            CSAppException.apperr(CustException.CRM_CUST_134, vipSerialNumber);
        }

        IData custVipInfo = qryCustVipInfos.getData(0);
        vipSnInfoData.put("GUATANTEE_SERIAL_NUMBER", vipSerialNumber);
        vipSnInfoData.put("GUATANTEE_USER_ID", userId);
        vipSnInfoData.put("VIP_CUST_NAME", custVipInfo.getString("CUST_NAME"));
        vipSnInfoData.put("CITY_CODE", custVipInfo.getString("CITY_CODE"));
        vipSnInfoData.put("CUST_MANAGER_ID", custVipInfo.getString("CUST_MANAGER_ID"));
        String vipClassId = custVipInfo.getString("VIP_CLASS_ID");
        // 检验该大客户类型是否可以为其他客户办理担保开机业务
        IDataset vipClassInfos = CommparaInfoQry.getCommparaCode1("CSM", "1028", vipClassId, CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isEmpty(vipClassInfos))
        {
            // 该大客户不能办理为其他客户担保开机业务
            CSAppException.apperr(CustException.CRM_CUST_140);
        }
        String vipClassName = UVipClassInfoQry.getVipClassNameByVipTypeCodeClassId(custVipInfo.getString("VIP_TYPE_CODE"), vipClassId);
        vipSnInfoData.put("VIP_CLASS_ID", vipClassId);
        vipSnInfoData.put("VIP_CLASS_NAME", vipClassName);
        // 查询客户经理信息
        IData staffData = UStaffInfoQry.qryStaffAreaInfoByStaffId(custVipInfo.getString("CUST_MANAGER_ID"));
        if (IDataUtil.isNotEmpty(staffData))
        {
            vipSnInfoData.put("MANAGER_NAME", staffData.getString("STAFF_NAME", ""));
            vipSnInfoData.put("MANAGER_PHONE", staffData.getString("SERIAL_NUMBER", ""));
        }
        // 查询本月担保开机次数
        IDataset userOtherInfos = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "DBKJ");
        if (IDataUtil.isNotEmpty(userOtherInfos))
        {
            vipSnInfoData.put("ASSURE_COUNT", userOtherInfos.getData(0).getString("RSRV_VALUE", "1"));
        }
        else
        {
            vipSnInfoData.put("ASSURE_COUNT", "0");
        }
        IData retData = new DataMap();
        retData.put("BUSI_INFO", vipSnInfoData);

        // 根据参数配置，查询大客户担保开机的有效时长
        IDataset commParamDataset = CommparaInfoQry.getCommparaByParaAttr("CSM", "1027", CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(commParamDataset))
        {
            DataHelper.sort(commParamDataset, "PARA_CODE1", IDataset.TYPE_INTEGER, IDataset.ORDER_ASCEND);
            retData.put("OPEN_HOURS", commParamDataset);
        }
        return IDataUtil.idToIds(retData);
    }
    
    /**
     * 手机报停 鉴权后信息查询
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData loadChildInfo(IData input) throws Exception
    {
        IData retrunData = new DataMap();
       
        //是否开通了宽带
        String isWideUser = "N";
        
        String serialNumber = input.getString("SERIAL_NUMBER");
        
        String KDSerialNumber = "KD_" + serialNumber;
        //add by zhangxing for BUG20180413095846关于宽带未装机完工手机号码能报停问题优化
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE","");
        if("131".equals(tradeTypeCode)){
	        IDataset ids = TradeInfoQry.getMainTradeBySN(KDSerialNumber,"600");
	    	if (IDataUtil.isNotEmpty(ids))
	    	{
	    		CSAppException.apperr(TradeException.CRM_TRADE_95, "该用户存在宽带开户未完工工单，不能办理报停！");
	    	}
        }
      //add by zhangxing for BUG20180413095846关于宽带未装机完工手机号码能报停问题优化--end
        
        IData wideInfo = UcaInfoQry.qryUserInfoBySn(KDSerialNumber);
        if (IDataUtil.isNotEmpty(wideInfo))
        {
            isWideUser = "Y";
        }
        
        retrunData.put("IS_WIDE_USER", isWideUser);
        
        IData userInfoData = UcaInfoQry.qryUserInfoBySn(serialNumber);       
        String userId =  userInfoData.getString("USER_ID","").trim();
        IData cond = new DataMap();
        cond.put("USER_ID", userId);    
        
        IDataset ds = CSAppCall.call("AM_CRM_QryDiscntDepositMinCharge", cond);
        
        if (DataSetUtils.isNotBlank(ds)) {   
            String isMinCharge = ds.getData(0).getString("IS_MIN_CHARGE","0").trim();
              
            retrunData.put("IS_MIN_CHARGE", isMinCharge);//1 有最低消费  0 无 
        }else{
            retrunData.put("IS_MIN_CHARGE", "0");//1 有最低消费  0 无 
        }
        /**
         * REQ201708240014_家庭IMS固话开发需求
         * @author zhuoyingzhi
         * @date 20171219
         */
        String  isImsUser="N";
        if("133".equals(tradeTypeCode)){
        	//手机报开
        	IData data = new DataMap();
        	data.put("SERIAL_NUMBER", serialNumber);
        	IData ImsInfo=getIMSInfoBySerialNumber(data);
        	if(IDataUtil.isNotEmpty(ImsInfo)){
        		//存在IMS家庭固话
        		isImsUser="Y";
        	}
        }
        retrunData.put("IS_IMS_USER", isImsUser);
        
        return retrunData;
    }
    
    /**
     * 手机报停关联停宽带校验
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkStopWide(IData input) throws Exception
    {
        IData retrunData = new DataMap();
       
        //0：校验通过，1：校验报错
        String resultCode = "0";
        
        String serialNumber = input.getString("SERIAL_NUMBER");
        
        String KDSerialNumber = "KD_" + serialNumber;

        IData wideInfo = UcaInfoQry.qryUserInfoBySn(KDSerialNumber);
        if (IDataUtil.isNotEmpty(wideInfo))
        {
            IDataset userAllDiscntInfos = UserDiscntInfoQry.getDiscntByUserId(wideInfo.getString("USER_ID"));
            
            for(int i = 0 ; i < userAllDiscntInfos.size() ; i++)
            {
                String discntId = userAllDiscntInfos.getData(i).getString("DISCNT_CODE");
                IDataset commparaInfos = CommparaInfoQry.getCommparaByCodeCode1("CSM","532","600",discntId);
               
                if(DataSetUtils.isNotBlank(commparaInfos))
                {
                    resultCode = "-1";
                    break;
                }
            }
            
            //如果没有办理包年套餐，则判断是否有包年营销活动
            if ("0".equals(resultCode))
            {
                IData userInfoData = UcaInfoQry.qryUserInfoBySn(serialNumber);
                
                IDataset saleActives = UserSaleActiveInfoQry.queryUserSaleActiveByTag(userInfoData.getString("USER_ID"));
                
                if (IDataUtil.isNotEmpty(saleActives))
                {
                    //add by zhangxing for REQ201803280021办理候鸟宽带用户未限制办理关联宽带报停优化
                	for (int i = 0; i < saleActives.size(); i++)
                    {
                		if ("66000602".equals(saleActives.getData(i).getString("PRODUCT_ID","")) || "69908016".equals(saleActives.getData(i).getString("PRODUCT_ID","")))
                		{                          
                			retrunData.put("RESULT_CODE", "-2");
                	        return retrunData;
                		}
                		//add by zhangxing3 for REQ201807310009有手机号码候鸟宽带资费
                		if ("66002202".equals(saleActives.getData(i).getString("PRODUCT_ID","")))
                		{                          
                			retrunData.put("RESULT_CODE", "-3");
                	        return retrunData;
                		}
                		//add by zhangxing3 for REQ201807310009有手机号码候鸟宽带资费
                		//BUS202004170006优化度假宽带2019业务规则
                		if ("66004809".equals(saleActives.getData(i).getString("PRODUCT_ID","")))
                		{                          
                			retrunData.put("RESULT_CODE", "-3");
                	        return retrunData;
                		}
                		//BUS202004170006优化度假宽带2019业务规则
                    }
                	
                	//add by zhangxing for REQ201803280021办理候鸟宽带用户未限制办理关联宽带报停优化 end
                	
                	//宽带包年营销活动产品ID配置
                    IDataset wideYearActiveCommparas = CommparaInfoQry.getCommpara("CSM", "532", "WIDE_YEAR_ACTIVE",getTradeEparchyCode());
                    
                    if (IDataUtil.isNotEmpty(wideYearActiveCommparas))
                    {
                        IData saleActive = null;
                        IData wideYearActiveCommpara = null;
                        
                        for (int i = 0; i < saleActives.size(); i++)
                        {
                            saleActive = saleActives.getData(i);
                            
                            if (StringUtils.equals("-1", resultCode))
                            {
                                break;
                            }
                            
                            for (int j = 0; j < wideYearActiveCommparas.size(); j++)
                            {
                                wideYearActiveCommpara = wideYearActiveCommparas.getData(j);
                                
                                if (StringUtils.equals(saleActive.getString("PRODUCT_ID"), wideYearActiveCommpara.getString("PARA_CODE1")))
                                {
                                    resultCode = "-1";
                                    
                                    break;
                                }
                            }
                        }
                    }
                    
                    
                }
            }
        }
        
        retrunData.put("RESULT_CODE", resultCode);
        
        return retrunData;
    }
    
    /**
     * 手机报开关联开宽带校验
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkOpenWide(IData input) throws Exception
    {
        IData retrunData = new DataMap();
       
        //0：校验通过，1：校验报错
        retrunData.put("RESULT_CODE", "0");
        retrunData.put("RESULT_INFO", "校验正常！");
        
        String serialNumber = input.getString("SERIAL_NUMBER");
        
        String KDSerialNumber = "KD_" + serialNumber;

        IData wideInfo = UcaInfoQry.qryUserInfoBySn(KDSerialNumber);
        if (IDataUtil.isNotEmpty(wideInfo))
        {
            IDataset userAllDiscntInfos = UserDiscntInfoQry.getDiscntByUserId(wideInfo.getString("USER_ID"));
            if("5".equals(wideInfo.getString("USER_STATE_CODESET"))||"05".equals(wideInfo.getString("USER_STATE_CODESET"))){
            	retrunData.put("RESULT_CODE", "-1");
                retrunData.put("RESULT_INFO", "信控停机状态，不能关联开机！");
            }  
        }
        
        return retrunData;
    }
    
    /**
     * 根据手机号码或userid查询用户是否存在IMS家庭固话
     * 手机号码为主号
     * <br/>
     * 没有存在返回   null
     * @param param
     * @return
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20171030
     */
    public IData getIMSInfoBySerialNumber(IData param) throws Exception{
    	
         String  serialNumber=param.getString("SERIAL_NUMBER","");
         String userIdB="";
         if(!"".equals(serialNumber) && serialNumber !=null){
        	 IData userInfoData = UcaInfoQry.qryUserInfoBySn(serialNumber); 
        	 userIdB =  userInfoData.getString("USER_ID","").trim();
         }else{
        	 //如果手机号码为空则用userid
        	 userIdB=param.getString("USER_ID","");
         }
         //获取主号信息
         IDataset iDataset=RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userIdB, RELATION_TYPE_CODE, "1");
         if(IDataUtil.isNotEmpty(iDataset)){
        	 //获取虚拟号
        	 String userIdA=iDataset.getData(0).getString("USER_ID_A", "");
        	 //通过虚拟号获取关联的IMS家庭固话号码信息
        	 IDataset userBInfo=RelaUUInfoQry.getRelationsByUserIdA(RELATION_TYPE_CODE, userIdA, "2");
        	 
        	 if(IDataUtil.isNotEmpty(userBInfo)){
        		 return userBInfo.getData(0);
        	 }
         }
    	 //不存在IMS家庭固话
    	 return null;
    }
    
	/**
	 * REQ201708240014_家庭IMS固话开发需求
	 * <br/>
	 * 判断IMS用户是否是手机报停状态,进来这里一定是IMS家庭固话了
	 * @param input
	 * @return
	 * @throws Exception
	 */
    public IData checkOpenIMS(IData input) throws Exception
    {
        IData retrunData = new DataMap();
       
        //0：校验通过，1：校验报错
        retrunData.put("RESULT_CODE", "0");
        retrunData.put("RESULT_INFO", "校验正常！");
        
        String serialNumber = input.getString("SERIAL_NUMBER");
        
        IData  param=new DataMap();
        	   param.put("SERIAL_NUMBER", serialNumber);
        //获取ims帐号信息	   
        IData  userBInfo=getIMSInfoBySerialNumber(param);
        if(IDataUtil.isNotEmpty(userBInfo)){
        	//获取IMS手机号码
        	String serialNumberIMS = userBInfo.getString("SERIAL_NUMBER_B","");
        	IData imsInfo = UcaInfoQry.qryUserInfoBySn(serialNumberIMS);
			if (!"1".equals(imsInfo.getString("USER_STATE_CODESET"))) {
				retrunData.put("RESULT_CODE", "-1");
				retrunData.put("RESULT_INFO", "不是手机停机状态，不能关联开机！");
			}
        }
        return retrunData;
    }    
    
    /**
     * 根据IMS固话或userid查询用户手机号码
     * @param param
     * @return
     * @throws Exception
     * @author zhengkai5
     */
    public IData getSerialNumberInfoByIMSInfo(IData param) throws Exception{
         String  serialNumber=param.getString("SERIAL_NUMBER","");
         String IMSuserId="";
         if(StringUtils.isNotEmpty(serialNumber)){
        	 IData userInfoData = UcaInfoQry.qryUserInfoBySn(serialNumber); 
        	 if(IDataUtil.isNotEmpty(userInfoData))
        	 {
        		 IMSuserId =  userInfoData.getString("USER_ID","").trim();
        		 //获取主号信息
        		 IDataset userBInfo = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(IMSuserId, RELATION_TYPE_CODE, "2");
        		 
        		 if(IDataUtil.isNotEmpty(userBInfo)){
        			 //获取虚拟号
        			 String userIdA =userBInfo.getData(0).getString("USER_ID_A", "");
        			 
        			 //通过虚拟号获取关联的手机号码信息
        			 IDataset userBInfo1=RelaUUInfoQry.getUserRelationRole2(userIdA,RELATION_TYPE_CODE,"1");
        			 
        			 if(IDataUtil.isNotEmpty(userBInfo1)){
        				 return userBInfo1.getData(0);
        			 }
        		 }
        	 }
         }
    	 //不存在IMS家庭固话
    	 return null;
    }
    
    /**
     * 查询是否有 IMS固话拆机
     * @param param
     * @return
     * @throws Exception
     * @author zhengkai5
     */
    public IData getDestroyIMSInfoBySn(IData data) throws Exception
    {
    	IData result = new DataMap();
    	String serialNumber = data.getString("SERIAL_NUMBER");
    	String IMSVSerialNumber = "IMS_"+serialNumber;
    	IDataset destroyUserInfo = UserInfoQry.getUserInfoBySnNetTypeCode2(IMSVSerialNumber,"2");
    	if(IDataUtil.isNotEmpty(destroyUserInfo))
    	{
    		String IMSVUserIdA = destroyUserInfo.getData(0).getString("USER_ID");
    		IDataset destroyRelaUU = RelaUUInfoQry.qryAllRelaUUByUserIdA(IMSVUserIdA,"MS","2");
    		if(IDataUtil.isNotEmpty(destroyRelaUU))
    		{
    			String IMSSerialNumber = destroyRelaUU.getData(0).getString("SERIAL_NUMBER_B");
    			IDataset destroyIMSUserInfo = UserInfoQry.getUserInfoBySnNetTypeCode2(IMSSerialNumber,"2");
    			if(IDataUtil.isNotEmpty(destroyIMSUserInfo))
    			{
    				result.put("IMS_TAG", "1");
    			}else {
    				result.put("IMS_TAG", "2");
				}
    		}else {
    			result.put("IMS_TAG", "2");
			}
    	}else {
    		result.put("IMS_TAG", "0");
		}
    	return result;
    }
    
    /**
     * 查询是否有 宽带拆机
     * @param param
     * @return
     * @throws Exception
     * @author zhengkai5
     */
    public IData getDestroyWidenetInfoBySn(IData data) throws Exception
    {
    	IData result = new DataMap();
    	String serialNumber = data.getString("SERIAL_NUMBER");
    	String KVSerialNumber = "KD_"+serialNumber;
    	IDataset destroyUserInfo = UserInfoQry.getUserInfoBySnNetTypeCode2(KVSerialNumber,"2");
    	IDataset UserInfo = UserInfoQry.getUserInfoBySnNetTypeCode2(KVSerialNumber,"0");
    	if(IDataUtil.isNotEmpty(destroyUserInfo) && IDataUtil.isEmpty(UserInfo) )
    	{
    		result.put("WIDENET_TAG", "1");
    	}else {
    		result.put("WIDENET_TAG", "2");
		}
    	return result;
    }
    
    /**     
     * 查询IMS固话状态     
     * @param param IMS家庭固话SERIAL_NUMBER     
     * @return     
     * @throws Exception     
     * @author zhengkai5     
     */     
    public IData getImsStaInfoByIMS(IData data) throws Exception     
    {     
        IData result = new DataMap();     
        String serialNumber = data.getString("SERIAL_NUMBER");     
        if(StringUtils.isNotBlank(serialNumber)){     
            IData wideInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);     
            String userId = wideInfo.getString("USER_ID");     
            if(StringUtils.isNotBlank(userId)){     
                //获取用户的主服务信息     
                IDataset userSvcStateInfo= UserSvcStateInfoQry.getUserMainState(userId);     
                if(IDataUtil.isNotEmpty(userSvcStateInfo)){     
                     //获取当前IMS家庭固话主服务状态     
                    //String stateCode=userSvcStateInfo.getData(0).getString("STATE_CODE", "");     
                    result = userSvcStateInfo.getData(0);     
                }     
            }     
                 
        }     
        return result;     
    } 
    
    /**
     * 如果IMS固话号码为吉祥号码，则不允许拆机
     * @param param
     * @return
     * @throws Exception
     */
    public IData getIMSInfoByJxSn(IData param) throws Exception{
    	String  serialNumber=param.getString("SERIAL_NUMBER","");
    	String  userID=param.getString("USER_ID","");
    	
    	IData result = new DataMap();
    	IDataset dataSet =ResCall.getMphonecodeInfo(serialNumber);
		 if (IDataUtil.isNotEmpty(dataSet)) {
			IData mphonecodeInfo = dataSet.first();
			String beautifulTag = mphonecodeInfo.getString("BEAUTIFUAL_TAG");
			if (StringUtils.equals("1", beautifulTag)){
				IDataset DiscntList = getAllDiscntByUserId(userID);
				if (IDataUtil.isNotEmpty(DiscntList)) {
					for (int i = 0; i < DiscntList.size(); i++) {
						String discntCode =DiscntList.getData(i).getString("DISCNT_CODE");
						if (StringUtils.equals("6070", discntCode)) {
							result.put("CODE1", "-1");
							return result;
						}else {
							result.put("CODE1", "0");
						}
					}
				}
			}
		 }
		 return result;
   }
    
    
    

    /***
	 * 根据用户ID查询用户优惠
	 */
	public IDataset getAllDiscntByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_WLAN_BY_USERID_NOW", param);
	}
    
}

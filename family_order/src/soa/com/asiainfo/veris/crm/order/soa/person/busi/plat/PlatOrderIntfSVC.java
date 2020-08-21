
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import java.security.Key;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.PlatUtils;
import com.asiainfo.veris.crm.order.soa.person.common.util.FuzzyPsptUtil;

/**
 * 平台业务外围接口
 * 
 * @author xiekl
 */
public class PlatOrderIntfSVC extends CSBizService
{

    /**
     * 手机支付销户/过户鉴权
     * 
     * @param pd
     * @param data
     *            01-手机号码 IDTYPE;手机号码 IDVALUE；54－手机支付业务 BIZ_TYPE；01-销户；02-过户 OPR_CODE
     * @return
     * @throws Exception
     */
    public IData accountPayDestroyCheck(IData data) throws Exception
    {
        String serialNumber = IDataUtil.chkParam(data, "IDVALUE");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        String userId = userInfo.getString("USER_ID");

        IDataset userPlatSvcList = UserPlatSvcInfoQry.queryUserPlatSvcByUserIdAndServiceId(userId, "99166951");

        if (IDataUtil.isEmpty(userPlatSvcList))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_0913);
        }

        IData param = new DataMap();
        param.put("IN_MODE_CODE", "6");
        param.put("BIZ_TYPE_CODE", "54");
        param.put("SP_CODE", "698000");
        param.put("BIZ_CODE", "00000001");
        param.put("OPR_SOURCE", "1");
        param.put("OPER_CODE", "02");
        param.put("SERIAL_NUMBER", serialNumber);

        IData result = new DataMap();
        IDataset resultList = CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", param);
        result.putAll(resultList.getData(0));
        result.put("IDVALUE", serialNumber);

        return result;
    }

    /**
     * 手机支付开户鉴权
     * 
     * @param pd
     * @param data
     *            01-手机号码IDTYPE；手机号码 IDVALUE； 54－手机支付业务 BIZ_TYPE_CODE；用户在省BOSS中的客服密码 PASSWD
     * @return
     * @throws Exception
     */
    public IData accountPayOpenCheck(IData data) throws Exception
    {
        IData result = new DataMap();
        String serialNumber = IDataUtil.chkParam(data, "IDVALUE");
        data.put("SERIAL_NUMBER", data.getString("IDVALUE"));

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        String userId = userInfo.getString("USER_ID");
        String brandCode = userInfo.getString("BRAND_CODE");
        String userStatus = userInfo.getString("USER_STATE_CODESET");
        String acctTag = userInfo.getString("ACCT_TAG");
        String openDate = userInfo.getString("OPEN_DATE", SysDateMgr.getSysTime());

        IData custInfo = UcaInfoQry.qryCustInfoByCustId(userInfo.getString("CUST_ID"));
        if (IDataUtil.isEmpty(custInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_397);
        }
        String custName = custInfo.getString("CUST_NAME");

        // 判断 主号码
        String otherStatus = "00";
        // 如果是一卡双号的副卡
        IDataset relaUUList = RelaUUInfoQry.getUUInfoByUserIdAB("", userId, "30", "2");//副卡的ROLE_CODE_B为2
        if (IDataUtil.isNotEmpty(relaUUList))
        {
            otherStatus = "59";
        }

        if ("G001".equals(brandCode))
        {
            brandCode = "01";
        }
        else if ("G002".equals(brandCode))
        {
            brandCode = "02";
        }
        else if ("G010".equals(brandCode))
        {
            brandCode = "03";
        }
        else
        {
            brandCode = "09";
        }

        String custLevel = "0";
        IDataset custVipList = CustVipInfoQry.getCustVipByUserId(userId);
        if (IDataUtil.isNotEmpty(custVipList))
        {
            custLevel = custVipList.getData(0).getString("VIP_CLASS_ID");
            if ("2".equals(custLevel))
            {
                custLevel = "1";
            }
            else if ("3".equals(custLevel))
            {
                custLevel = "2";
            }
            else if ("4".equals(custLevel))
            {
                custLevel = "3";
            }
            else
            {
                custLevel = "0";
            }
        }

        if ("0".equals(acctTag))
        {
            if ("0".equals(userStatus) || "N".equals(userStatus))
            {
                userStatus = "00";
            }
            else if ("A".equals(userStatus) || "B".equals(userStatus) || "G".equals(userStatus) || "R".equals(userStatus))
            {
                userStatus = "01";
            }
            else if ("1".equals(userStatus) || "2".equals(userStatus) || "3".equals(userStatus) || "4".equals(userStatus) || "5".equals(userStatus) || "7".equals(userStatus) || "I".equals(userStatus) || "S".equals(userStatus)
                    || "Y".equals(userStatus))
            {
                userStatus = "02";
            }
            else if ("8".equals(userStatus) || "F".equals(userStatus))
            {
                userStatus = "03";
            }
            else if ("6".equals(userStatus) || "9".equals(userStatus) || "X".equals(userStatus) || "Z".equals(userStatus))
            {
                userStatus = "04";
            }
            else
            {
                userStatus = "99";
            }
        }
        else
        {
            userStatus = "99";
        }

        result.put("X_RESULTCODE", "00");
        result.put("X_RESULTINFO", "OK");
        result.put("IDVALUE", data.getString("IDVALUE"));
        result.put("USER_STATE_CODESET", userStatus);// 用户状态代码
        result.put("OTHERSTATUS", otherStatus);// 00－正常号码59－副号码，如果鉴权的号码是一卡多号中的副号码，平台不能为一卡多号中的副号码办理手机支付业务
        result.put("BRAND_CODE", brandCode);// 01：全球通；02：神州行；03：动感地带；09：其他品牌
        result.put("CUST_LEVEL", custLevel);// 0－普通用户（动感地带用户为普通用户） 1－银卡 2－金卡 3－钻石卡
        result.put("OPR_TIME", openDate.replace("-", "").replace(":", "").replace(" ", ""));// 入网时间YYYYMMDD24HHMISS：YYYY年，MM月，DD日，HH时，MI分，SS秒
        
        String bip_code = data.getString("BIPCODE","");
        String activity_code = data.getString("ACTIVITYCODE","");
        if("T3000003".equals(activity_code)&&"BIP2B093".equals(bip_code)){
        	
        	IDataset attrResult = new DatasetList();
            IData data101 = new DataMap();
            data101.put("INFO_CODE", "101");
            data101.put("INFO_VALUE", encryptMode(custName));
            attrResult.add(data101);
            
            IData data103 = new DataMap();
            data103.put("INFO_CODE", "103");
            data103.put("INFO_VALUE", custInfo.getString("PSPT_TYPE_CODE", ""));
            attrResult.add(data103);
            
            IData data104 = new DataMap();
            data104.put("INFO_CODE", "104");
            data104.put("INFO_VALUE", encryptMode(custInfo.getString("PSPT_ID", "")));
            attrResult.add(data104);
            
            result.putAll(attrResult.toData());
        }else{
        	result.put("INFO_CODE", "101");
            result.put("INFO_VALUE", FuzzyPsptUtil.fuzzyName(custName));
        }
        return result;
    }
    
    /**
   	 * 3des加密
   	 * @param sdata  待加密数据
   	 * @return
   	 * @throws Exception
   	 */
    public static String encryptMode(String sdata) throws Exception {
    	IDataset keys = CommparaInfoQry.getCommparaAllCol("CSM", "2018", "ENCRYPT_MODE_KEY", null);
    	if(IDataUtil.isEmpty(keys)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"TD_S_COMMPARA中未配置ENCRYPT_MODE_KEY");    		
    	}
    	String skey = keys.getData(0).getString("PARA_CODE1","");
	    byte[] key = new BASE64Decoder().decodeBuffer(skey);
	    byte[] data = sdata.getBytes("UTF-8");

	    Key deskey = null;
	    DESedeKeySpec spec = new DESedeKeySpec(key);
	    SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
	    deskey = keyfactory.generateSecret(spec);

	    Cipher cipher = Cipher.getInstance("desede/ECB/PKCS5Padding");

	    cipher.init(1, deskey);
	    byte[] bOut = cipher.doFinal(data);

	    return new BASE64Encoder().encode(bOut);
	  }	

    /**
     * 激活139邮箱的预约受理
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData activeMail139PreOrder(IData param) throws Exception
    {
        IData result = new DataMap();
        String serialNumber = param.getString("SERIAL_NUMBER");

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "-1:未找到用户资料");
        }
        String userId = userInfo.getString("USER_ID");

        IDataset tradeList = TradeInfoQry.queryTradeNotFinish("3739", userId, null);
        if (!IDataUtil.isEmpty(tradeList))
        {
            IData trade = tradeList.getData(0);
            // 如果受理时间再当前时间的前三天内，可以激活
            if (SysDateMgr.getAddHoursDate(trade.getString("ACCEPT_DATE"), 3 * 24).compareTo(SysDateMgr.getSysTime()) > 0)
            {
                /*trade.put("EXEC_TIME", SysDateMgr.getSysTime());
                trade.put("UPDATE_TIME", SysDateMgr.getSysTime());
                trade.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                trade.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                trade.put("REMARK", "139邮箱激活短信未找到台帐记录");
                Dao.update("TF_B_TRADE", trade);*/
                TradeInfoQry.updateTradeByTradeId(trade.getString("TRADE_ID"), trade.getString("ACCEPT_MONTH"), trade.getString("CANCEL_TAG"), CSBizBean.getVisit().getStaffId(), CSBizBean.getVisit().getDepartId(), "139邮箱激活短信未找到台帐记录");

                result.put("X_RESULTCODE", "0");
                result.put("X_RESULTINFO", "Trade OK!");
            }
            else
            {
                IData sendInfo = new DataMap();
                sendInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
                sendInfo.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
                sendInfo.put("RECV_OBJECT", serialNumber);
                sendInfo.put("RECV_ID", "0");
                sendInfo.put("SMS_PRIORITY", "50");
                sendInfo.put("NOTICE_CONTENT", "尊敬的客户，您办理的139邮箱，激活的时间已经超过3天，办理失败！");
                sendInfo.put("REMARK", "139邮箱激活短信未找到台帐记录");
                sendInfo.put("FORCE_OBJECT", "10086");
                SmsSend.insSms(sendInfo);

                result.put("X_RESULTCODE", "2998");
                result.put("X_RESULTINFO", "-1:在TF_B_TRADE未找到139邮箱预约受理记录,TRADE_TYPE_CODE:3739,USER_ID:" + userId);
                result.put("X_RSPTYPE", "2");// add by ouyk
                result.put("X_RSPCODE", "2998");// add by ouyk
            }
        }
        else
        {
            IData sendInfo = new DataMap();
            sendInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
            sendInfo.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
            sendInfo.put("RECV_OBJECT", serialNumber);
            sendInfo.put("RECV_ID", "0");
            sendInfo.put("SMS_PRIORITY", "50");
            sendInfo.put("NOTICE_CONTENT", "尊敬的客户，您办理的139邮箱，激活的时间已经超过3天，办理失败！");
            sendInfo.put("REMARK", "139邮箱激活短信未找到台帐记录");
            sendInfo.put("FORCE_OBJECT", "10086");
            SmsSend.insSms(sendInfo);

            result.put("X_RESULTCODE", "2998");
            result.put("X_RESULTINFO", "-1:在TF_B_TRADE未找到139邮箱预约受理记录,TRADE_TYPE_CODE:3739,USER_ID:" + userId);
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
        }

        return result;
    }

    /**
     * 校验平台业务是否能够退订
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData checkPlatCancel(IData param) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER");
        IData result = new DataMap();
        if (StringUtils.isBlank(serialNumber))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "手机号码不能为空");
        }

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        String userId = userInfo.getString("USER_ID");

        // 取将要退订的服务集合
        JSONArray dealElementArray = JSONArray.fromObject(StringUtils.upperCase(param.getString("ELEMENTS", "[]")));
        IDataset dealElements = new DatasetList();
        for (int count = 0; count < dealElementArray.size(); count++)
        {
            IData data = new DataMap(dealElementArray.getString(count));
            dealElements.add(data);
        }

        // 循环标记所有需要退订的服务
        for (int i = 0; i < dealElements.size(); i++)
        {
            IData dealElement = dealElements.getData(i);
            String serviceId = dealElement.getString("SERVICE_ID");

            // 查询依赖于当前退订服务的用户服务
            IDataset userLimits = UserPlatSvcInfoQry.queryRelaPlatSvcByDel(userId, serviceId);

            // 取依赖服务和将删除服务的差集
            IDataset tempUserLimits = new DatasetList();
            tempUserLimits.addAll(userLimits);
            if (tempUserLimits != null && tempUserLimits.size() > 0)
            {
                for (int j = 0; j < tempUserLimits.size(); j++)
                {
                    IData userTmpData = tempUserLimits.getData(j);
                    String tmpSvcId = userTmpData.getString("SERVICE_ID_L");
                    for (int k = 0; k < dealElements.size(); k++)
                    {
                        if (dealElements.getData(k).getString("SERVICE_ID").equals(tmpSvcId))
                        {
                            tempUserLimits.remove(j);
                            j--;
                        }
                    }
                }
            }

            if (tempUserLimits == null || tempUserLimits.size() == 0)
            {
                // 如果不存在有被依赖的服务，则标记为0-可退订
                if ("".equals(dealElement.getString("DEAL_TAG", "")))
                {
                    dealElement.put("DEAL_TAG", "0");
                }
                String serviceIdLimit0 = "";
                String serviceNameLimit0 = "";
                for (int j = 0; j < userLimits.size(); j++)
                {
                    IData tmpUserLimit = userLimits.getData(j);
                    serviceIdLimit0 = serviceIdLimit0.concat(tmpUserLimit.getString("SERVICE_ID_L"));
                    serviceNameLimit0 = serviceNameLimit0.concat(tmpUserLimit.getString("SERVICE_NAME_L"));
                    if (j < userLimits.size() - 1)
                    {
                        serviceIdLimit0 = serviceIdLimit0.concat(",");
                        serviceNameLimit0 = serviceNameLimit0.concat(",");
                    }
                }
                dealElement.put("SERVICE_ID_L", serviceIdLimit0);
                dealElement.put("SERVICE_NAME_L", serviceNameLimit0);
            }
            else
            {
                // 如果存在有被依赖的服务，则标记为2-不可退订
                dealElement.put("DEAL_TAG", "2");
                String serviceIdLimit2 = "";
                String serviceNamelimit2 = "";
                for (int j = 0; j < tempUserLimits.size(); j++)
                {
                    serviceIdLimit2 = serviceIdLimit2.concat(tempUserLimits.getData(j).getString("SERVICE_ID_L"));
                    serviceNamelimit2 = serviceNamelimit2.concat(tempUserLimits.getData(j).getString("SERVICE_NAME_L"));
                    if (j < tempUserLimits.size() - 1)
                    {
                        serviceIdLimit2 = serviceIdLimit2.concat(",");
                        serviceNamelimit2 = serviceNamelimit2.concat(",");
                    }
                }
                dealElement.put("SERVICE_ID_L", serviceIdLimit2);
                dealElement.put("SERVICE_NAME_L", serviceNamelimit2);
            }

            for (int j = 0; j < userLimits.size(); j++)
            {
                IData userLimit = userLimits.getData(j);
                for (int k = 0; k < dealElements.size(); k++)
                {
                    IData tmpDealElement = dealElements.getData(k);
                    if (tmpDealElement.getString("SERVICE_ID").equals(userLimit.getString("SERVICE_ID_L")))
                    {
                        // 如果依赖的服务被标记为0-可退订，则被依赖的服务标记为1-连带退订
                        if ("0".equals(dealElement.getString("DEAL_TAG")))
                        {
                            tmpDealElement.put("DEAL_TAG", "1");
                        }// 如果依赖的服务被标记为2-不可退订，则被依赖的服务标记为0-可退订
                        else if ("2".equals(dealElement.getString("DEAL_TAG")))
                        {
                            tmpDealElement.put("DEAL_TAG", "0");
                        }
                    }
                }
            }
        }
        result.putAll(param);
        result.put("ELEMENTS", dealElements);
        return result;
    }

    /**
     * 服务开通参数回写接口
     * 
     * @return
     * @throws Exception
     */
    public IData crmDealAfterPf(IData data) throws Exception
    {
        IData result = new DataMap();
        if ("".equals(data.getString("TRADE_ID", "")))
        {
            result.put("X_RESULTCODE", "2998");
            result.put("X_RESULTINFO", "工单流水号不能为空！");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            result.put("X_RESULTCODE", "2998");
            result.put("X_RESULTINFO", "手机号码不能为空！");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }
        // dao.insert("TI_B_PF_RESULT", data);
        // 海南服务开通都配的开环，需要直接搬资料
        String serialNumber = data.getString("SERIAL_NUMBER");
        IData userInfo = UserInfoQry.getUserInfoBySN(serialNumber);

        IData other = new DataMap();
        String user_id = userInfo.getString("USER_ID");
        other.put("PARTITION_ID", user_id.substring(user_id.length() - 4));
        other.put("USER_ID", user_id);
        other.put("RSRV_VALUE_CODE", "NET_USER_INFO");
        other.put("RSRV_VALUE", data.getString("FIELDS1"));
        other.put("TRADE_ID", data.getString("TRADE_ID"));
        other.put("START_DATE", SysDateMgr.getSysDate());
        other.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
        other.put("REMARK", "互联网通行证号-IBOSS返回");
        other.put("INST_ID", SeqMgr.getInstId());
        boolean dealFlag = Dao.save("TF_F_USER_OTHER", other, new String[]
        { "PARTITION_ID", "USER_ID", "RSRV_VALUE_CODE" });
        if (!dealFlag)
        {
            Dao.insert("TF_F_USER_OTHER", other);
        }
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "OK！");
        return result;
    }

    /**
     * 飞信大包月续订
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData fetionOrderBook(IData data) throws Exception
    {
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");

        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
        if (uca == null)
        {
            CSAppException.apperr(PlatException.CRM_PLAT_1001_5);
        }

        IData result = new DataMap();
        IDataset set = UserPlatSvcInfoQry.queryFetionDuePlatSvc(uca.getUserId(), uca.getUserEparchyCode());
        if (IDataUtil.isEmpty(set))
        {
            result.put("X_RESULTCODE", "2998");
            result.put("X_RESULTINFO", "用户不存在到期的飞信大包月业务，续订失败！");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
        }
        else
        {
            IData fetionSvc = set.getData(0);
            IData param = new DataMap();
            param.put("OPER_CODE", PlatConstants.OPER_ORDER); // 续订
            param.put("SERIAL_NUMBER", serialNumber);
            param.put("SERVICE_ID", fetionSvc.getString("SERVICE_ID"));
            // 
            param.put("START_DATE", fetionSvc.getString("END_DATE"));

            IDataset dataset = CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", param);
            result = dataset.getData(0);
        }
        return result;
    }

    /**
     * WLAN流量赠送营销活动接口
     * 
     * @return
     * @throws Exception
     */
    public IData giftWlan(IData data) throws Exception
    {
        IData result = new DataMap();
        IData param = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "OK！");

        if (!"".equals(data.getString("CHANNEL_ID", "")))
        {
            data.put("IN_MODE_CODE", "9");// 短厅以CHANNEL_ID字段区分
        }
        // IN_MODE_CODE区分渠道 9：短厅 8：经分
        if ("".equals(data.getString("IN_MODE_CODE", "")))
        {
            result.put("X_RESULTCODE", "2998");
            result.put("X_RESULTINFO", "操作渠道不能为空！");
            return result;
        }

        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }

        String userId = userInfo.getString("USER_ID");
        // 查询用户目标群

        IDataset set = UserPlatInfoQry.getWlanGiftUser(serialNumber);
        if (set.size() > 0)
        {
            data.put("USER_TYPE", set.getData(0).getString("USER_TYPE"));
        }
        else
        {
            String content = "您好！您本月未获得中国移动WLAN免费流量赠送资格，感谢您的关注。中国移动。";
            PlatUtils.insertSms(serialNumber, userId, content, CSBizBean.getUserEparchyCode(), "WLAN免费流量赠送提醒");
            return result;
        }
        // 99001001:WLAN流量150M体验包
        // 99001002:WLAN流量280M体验包
        // 99001003:WLAN流量500M体验包
        // 99001004:WLAN流量2G体验包
        // 99001005:WLAN流量5G体验包
        if ("1".equals(data.getString("USER_TYPE")) || "2".equals(data.getString("USER_TYPE")))
        {
            data.put("ELEMENT_ID", "99001005");
            data.put("DATA_NUM", "5G");
        }
        else
        {
            // 查询用户订购20或30或50或100元移动数据流量套餐（套餐编码：2002、5972、2003、2004）
            IDataset discntSet = new DatasetList();
            param.put("USER_ID", userId);

            IDataset tempSet = new DatasetList();
            IData temp = new DataMap();
            temp.put("DISCNT_CODE", "2002");
            temp.put("RELA_DISCNT_CODE", "99001001");
            temp.put("DATA_NUM", "150M");
            tempSet.add(temp);
            temp.put("DISCNT_CODE", "5972");
            temp.put("RELA_DISCNT_CODE", "99001002");
            temp.put("DATA_NUM", "280M");
            tempSet.add(temp);
            temp.put("DISCNT_CODE", "2003");
            temp.put("RELA_DISCNT_CODE", "99001003");
            temp.put("DATA_NUM", "500M");
            tempSet.add(temp);
            temp.put("DISCNT_CODE", "2004");
            temp.put("RELA_DISCNT_CODE", "99001004");
            temp.put("DATA_NUM", "2G");
            tempSet.add(temp);
            for (int i = 0; i < tempSet.size(); i++)
            {
                discntSet = UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode(userId, tempSet.getData(i).getString("DISCNT_CODE"));
                ;
                if (discntSet != null && discntSet.size() > 0)
                {
                    data.put("ELEMENT_ID", tempSet.getData(i).getString("RELA_DISCNT_CODE"));
                    data.put("DATA_NUM", tempSet.getData(i).getString("DATA_NUM"));
                    break;
                }
            }
        }
        if ("".equals(data.getString("ELEMENT_ID", "")))
        {
            String content = "您好！您本月未获得中国移动WLAN免费流量赠送资格，感谢您的关注。中国移动。";
            PlatUtils.insertSms(serialNumber, userId, content, CSBizBean.getUserEparchyCode(), "WLAN免费流量赠送提醒");
            return result;
        }
        // 查询用户是否开通WLAN功能
        IDataset userPlatSvcList = UserPlatSvcInfoQry.queryUserPlatSvcByUserIdAndServiceId(userId, "98002401");

        if (userPlatSvcList != null && userPlatSvcList.size() > 0)
        {
            data.put("MODIFY_TAG", "0");
            data.put("ELEMENT_TYPE_CODE", "D");
            data.put("BOOKING_TAG", "0"); // 非预约
            IDataset dataset = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", data);
            dataset.getData(0).putAll(result);
        }
        else
        {
            data.put("OPER_CODE", "06");
            data.put("SERVICE_ID", "98002401");
            data.put("GIFT_DISCNT", data.getString("ELEMENT_ID"));// 在pkg_plat_discnt中绑定优惠时使用
            // result = platSVCOperE(pd,data);
            IDataset dataset = CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", data);
            dataset.getData(0).putAll(result);
        }
        return result;
    }

    /**
     * WLAN流量赠送营销活动接口
     * 
     * @return
     * @throws Exception
     */
    public IData giftWlanGetUserType(IData data) throws Exception
    {
        IData result = new DataMap();
        IData param = new DataMap();
        data.put("IN_MODE_CODE", "9");
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "OK！");

        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }

        String userId = userInfo.getString("USER_ID");
        String userType = "0";
        String iS5G = "N";
        String iS2G = "N";
        String iS500M = "N";
        String iS280M = "N";
        String iS150M = "N";
        String iS200M = "N";

        // 查询用户目标群

        IDataset set = UserPlatInfoQry.getWlanGiftUser(serialNumber);
        if (set.size() > 0)
        {
            userType = set.getData(0).getString("USER_TYPE");
        }
        // 99001001:WLAN流量150M体验包
        // 99001002:WLAN流量280M体验包
        // 99001003:WLAN流量500M体验包
        // 99001004:WLAN流量2G体验包
        // 99001005:WLAN流量5G体验包
        // 99001005:WLAN流量5G体验包
        // 99001006:WLAN流量200M体验包
        IDataset discntSet = new DatasetList();
        param.put("USER_ID", userId);
        if ("1".equals(userType) || "2".equals(userType))
        {
            discntSet = UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode(userId, "99001005");
            if (discntSet != null && discntSet.size() > 0)
            {
                iS5G = "Y";
            }
        }
        else if ("3".equals(userType))
        {
            IDataset tempSet = new DatasetList();
            IData temp = new DataMap();
            temp.put("DISCNT_CODE", "99001001");
            tempSet.add(temp);
            temp.put("DISCNT_CODE", "99001002");
            tempSet.add(temp);
            temp.put("DISCNT_CODE", "99001003");
            tempSet.add(temp);
            temp.put("DISCNT_CODE", "99001004");
            tempSet.add(temp);
            for (int i = 0; i < tempSet.size(); i++)
            {
                discntSet = UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode(userId, tempSet.getData(i).getString("DISCNT_CODE"));
                if (discntSet != null && discntSet.size() > 0)
                {
                    if ("99001001".equals(param.getString("DISCNT_CODE")))
                    {
                        iS150M = "Y";
                    }
                    if ("99001002".equals(param.getString("DISCNT_CODE")))
                    {
                        iS280M = "Y";
                    }
                    if ("99001003".equals(param.getString("DISCNT_CODE")))
                    {
                        iS500M = "Y";
                    }
                    if ("99001004".equals(param.getString("DISCNT_CODE")))
                    {
                        iS2G = "Y";
                    }
                    break;
                }
            }
        }
        else if ("4".equals(userType))
        {// 首次使用“随e行”手机APP方式登录成功 的用户
            discntSet = UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode(userId, "99001003");
            if (discntSet != null && discntSet.size() > 0)
            {
                iS500M = "Y";
            }
        }
        else if ("5".equals(userType))
        {// 当月使用“随e行”手机APP方式至少认证成功一次 的用户
            discntSet = UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode(userId, "99001005");
            if (discntSet != null && discntSet.size() > 0)
            {
                iS200M = "Y";
            }
        }
        result.put("USER_TYPE", userType);
        result.put("IS5G", iS5G);
        result.put("IS2G", iS2G);
        result.put("IS500M", iS500M);
        result.put("IS280M", iS280M);
        result.put("IS150M", iS150M);
        result.put("IS200M", iS200M);
        return result;
    }

    /**
     * 一级boss的批量处理结果 主要是处理默认开通的
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData platBatchReg(IData param) throws Exception
    {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "OK");
        result.put("BRAND_CODE", "09");
        IDataset dataset = new DatasetList();
        IDataset resultSet = new DatasetList();

        int totalCount = dataset.size();
        int sucessfulCount = 0;
        int failedCount = 0;

        param.put("IN_MODE_CODE", "6");
        param.put("SRC_SVCNAME", "SS.PlatBatchIBossRegIntfSVC.tradeReg");
        IData operResult = new DataMap();

        JSONArray spCodes = new JSONArray();
        if (param.getString("SP_CODE", "").startsWith("["))
        {
            spCodes = JSONArray.fromObject(param.getString("SP_CODE")); // sp服务商编码
        }
        else
        {
            JSONObject spCode = new JSONObject();
            spCode.put("SP_CODE", param.getString("SP_CODE"));
            spCodes.add(spCode);
        }

        try
        {
            IDataset resultList = CSAppCall.call("SS.PlatBatchRegIntfSVC.tradeReg", param);
            IData detail = resultList.getData(0);

            result.put("BRAND_CODE", detail.getString("BRAND_CODE", "09"));
            result.put("UMCP_BRAND", detail.getString("BRAND_CODE", "09"));

            for (int i = 0; i < spCodes.size(); i++)
            {
                operResult.put("X_RESULTCODE_DETAIL", detail.getString("X_RESULTCODE", "2998"));
                operResult.put("X_RESULTINFO_DETAIL", detail.getString("X_RESULTINFO", "其他错误"));
                resultSet.add(operResult);
            }

        }
        catch (Exception e)
        {
            failedCount++;
            e.printStackTrace();
            result.put("BRAND_CODE", "09");
            result.put("UMCP_BRAND", "09");
            for (int i = 0; i < spCodes.size(); i++)
            {
                operResult.put("X_RESULTCODE_DETAIL", "2998");
                operResult.put("X_RESULTINFO_DETAIL", e.getMessage());
                resultSet.add(operResult);
            }

        }

        // 再调用iboss接口通知 --TODO 有点问题
        if ("BIP2B548_T2101548_1_0".equals(param.getString("KIND_ID")))
        {
            IData inParam = new DataMap();
            inParam.put("KIND_ID", "BIP2B548_T2101549_0_0");// 默认开通通知
            inParam.put("ROUTETYPE", "00");
            inParam.put("ROUTEVALUE", "000");

            inParam.put("TRANS_ID", param.getString("INTF_TRADE_ID"));
            inParam.put("OPER_TIME", SysDateMgr.getSysTime());

            inParam.put("RECEIVEDSUM", totalCount);
            inParam.put("SUCCSUM", sucessfulCount);
            inParam.put("FAILEDSUM", failedCount);
            inParam.putAll(resultSet.toData());

            IBossCall.dealInvokeUrl("BIP2B548_T2101549_0_0", "IBOSS", inParam);
        }

        result.putAll(param);
        result.putAll(resultSet.toData());

        return result;
    }

    /**
     * 飞信支付账户信息查询接口，提供给账务
     * 
     * @return
     * @throws Exception
     */
    public IData qryFetionAccountInfo(IData data) throws Exception
    {
        IData result = new DataMap();
        result.put("ACCOUNT", "0");
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "OK！");
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            result.put("X_RESULTCODE", "2998");
            result.put("X_RESULTINFO", "手机号码不能为空！");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        param.put("OPTTYPE", "1");
        IDataset set = PlatInfoQry.getUserFetionInfo(param);
        if (set.size() > 0)
        {
            result.put("ACCOUNT", "1");
            result.putAll(set.getData(0));
        }
        return result;
    }

    @Override
    public final void setTrans(IData input)
    {
        if ("6".equals(this.getVisit().getInModeCode()))
        {
            if (!"".equals(input.getString("SERIAL_NUMBER", "")))
            {
                return;
            }
            else if (!"".equals(input.getString("IDVALUE", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("IDVALUE"));
                return;
            }
            else if (!"".equals(input.getString("MSISDN", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("MSISDN", ""));
                return;
            }
            else if (!"".equals(input.getString("ID_VALUE", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("ID_VALUE", ""));
                return;
            }
            else if (!"".equals(input.getString("IDITEMRANGE", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("IDITEMRANGE", ""));
                return;
            }
        }
    }

    /**
     * 设置呼叫转移接口
     * 
     * @return
     * @throws Exception
     */

    public IData setVEMLAttr(IData data) throws Exception
    {
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        String operCode = IDataUtil.chkParam(data, "OPER_CODE");// 操作代码
        String operType = IDataUtil.chkParam(data, "OPER_TYPE");// 呼转设置类型
        String oprNumb = IDataUtil.chkParam(data, "OPR_NUMB");

        IData result = new DataMap();
        result.put("ID_TYPE", "01");
        result.put("ID_VALUE", serialNumber);
        result.put("OPR_NUMB", oprNumb);

        String serviceId = "";
        String attrCode = "";
        String modifyTag = "";
        // 01：呼转设置02：呼转取消

        if (StringUtils.equals("01", operCode))
        {
            modifyTag = "0";
            data.put("IS_PLAT_ORDER", "true");
        }
        if (StringUtils.equals("02", operCode))
        {
            modifyTag = "1";
        }

        // 1：无条件呼转 2：遇忙呼转 3：无应答转 4：不可及转
        IData set = StaticInfoQry.getStaticInfoByTypeIdDataId("VEML_SVC_CODE", operType);// getStaticList("VEML_SVC_CODE",
        // operType);
        if (IDataUtil.isNotEmpty(set))
        {
            serviceId = set.getString("DATA_NAME");
            attrCode = set.getString("PDATA_ID");
        }
        else
        {
            result.put("X_RESULTCODE", "2998");
            result.put("X_RESULTINFO", "TD_S_STATIC表VEML_SVC_CODE参数配置错误！");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }

        data.put("IN_MODE_CODE", "6");
        data.put("ELEMENT_TYPE_CODE", "S");
        data.put("ELEMENT_ID", serviceId);
        data.put("MODIFY_TAG", modifyTag);
        data.put("BOOKING_TAG", "0");
        data.put("ATTR_STR1", attrCode);
        data.put("ATTR_STR2", "12599");
        
        
        try
        {	
        	   String userId=null;
        	   IDataset userSet=UserInfoQry.getUserInfoBySn(serialNumber, "0");
        	   if(userSet!=null&&userSet.size()>0){
        		   userId=userSet.getData(0).getString("USER_ID");
        	   }else{
        		   result.put("X_RESULTCODE", "2998");
                   result.put("X_RESULTINFO", serialNumber+"已经失效或者不存在！");
                   result.put("X_RSPTYPE", "2");// add by ouyk
                   result.put("X_RSPCODE", "2998");// add by ouyk
                   
                   return result;
        	   }
        	   
        	   //查询用户是否办理此服务
        	   IDataset sevSet=BofQuery.getUserSvc(userId, serviceId, Route.CONN_CRM_CG);
   
        	   if(modifyTag.equals("0")){	//新增，会验证
        		   if(sevSet!=null&&sevSet.size()>0){
        			   result.put("X_RESULTCODE", "2000");
                       result.put("X_RESULTINFO", "用户已经存在此服务，无法重复办理！");
                       result.put("X_RSPTYPE", "2");
                       result.put("X_RSPCODE", "2998");
                       
                       return result;
        		   }
        		   
        	   }else if(modifyTag.equals("1")){		//取消
        		   if(!(sevSet!=null&&sevSet.size()>0)){
        			   result.put("X_RESULTCODE", "2001");
                       result.put("X_RESULTINFO", "用户不存在此服务，无需取消！");
                       result.put("X_RSPTYPE", "2");// add by ouyk
                       result.put("X_RSPCODE", "2998");// add by ouyk
                       
                       return result;
        		   }	   
        	   }
        	   
        	   IDataset dataset = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", data);
        	   result.putAll(dataset.getData(0)); 
        	   
        }catch(Exception e)
        {
        	  String error =  Utility.parseExceptionMessage(e); 
              String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
              result.put("X_RSPTYPE", "2");
              result.put("X_RESULTCODE", errorArray[0] );
              result.put("X_RSPCODE", errorArray[0]);
              result.put("X_RESULTINFO", errorArray[1]);
        }
     
        return result;
    }

    /**
     * 飞信支付账户信息同步接口
     * 
     * @return
     * @throws Exception
     */
    public IDataset synFetionAccountInfo(IData data) throws Exception
    {
        IData result = new DataMap();
        IDataset rs = new DatasetList();
        result.putAll(data);
        result.put("RSPDATE", SysDateMgr.decodeTimestamp(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND_SHORT));
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "OK！");
        if ("".equals(data.getString("SERVID", "")))
        {
            result.put("X_RESULTCODE", "2998");
            result.put("X_RESULTINFO", "IBSS创建的唯一用户标识不能为空！");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            rs.add(result);
            return rs;
        }
        if ("".equals(data.getString("MOBILE", "")))
        {
            result.put("X_RESULTCODE", "2998");
            result.put("X_RESULTINFO", "手机号码不能为空！");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            rs.add(result);
            return rs;
        }
        String serialNumber = IDataUtil.chkParam(data, "MOBILE");
        String sysfeinnoId = IDataUtil.chkParam(data, "SYSFEINNOID");

        data.put("SERIAL_NUMBER", serialNumber);
        PlatOrderIntfBean bean = new PlatOrderIntfBean();
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        bean.getTradeInfo(userInfo.getString("EPARCHY_CODE"));

        String stateCodeset = userInfo.getString("USER_STATE_CODESET");
        if ("-A-B-G-1-2-3-4-5-7-E-F-I-J-K-L-M-O-P-Q-".indexOf(stateCodeset) >= 0)
        {
            if ("1".equals(data.getString("OPTTYPE", "")))
            {
                result.put("X_RESULTCODE", "2005");
                result.put("X_RSPTYPE", "2");// add by ouyk
                result.put("X_RSPCODE", "2998");// add by ouyk
            }
            result.put("X_RESULTINFO", "用户已停机");

            rs.add(result);
            return rs;
        }

        IDataset set1 = PlatInfoQry.queryFetionInfo(serialNumber, sysfeinnoId);

        if ("1".equals(data.getString("OPTTYPE", "")) && set1.size() > 0)
        {
            PlatInfoQry.deleteFetionInfo(serialNumber, sysfeinnoId);

            Dao.insert("TF_F_USER_FETIONINFO", data);
            result.put("X_RESULTCODE", "2000");
            result.put("X_RESULTINFO", "用户已存在账户信息！");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            rs.add(result);
            return rs;
        }

        boolean dealFlag = Dao.save("TF_F_USER_FETIONINFO", data);
        if (!dealFlag)
        {
            Dao.insert("TF_F_USER_FETIONINFO", data);
        }

        rs.add(result);
        return rs;
    }

    /**
     * 梦网自由退订
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset tradeForDreamNet(IData param) throws Exception
    {
        String serviceType = param.getString("SERVICE_TYPE", "Z");
        if ("Z".equals(serviceType))
        {

            if (StringUtils.isBlank(param.getString("SERIAL_NUMBER")))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "手机号码不能为空");
            }

            if (param.getString("BIZ_TYPE_CODE", "").equals("16") || param.getString("BIZ_TYPE_CODE", "").equals("23"))
            {
                param.put("OPR_SOURCE", "11");
            }
            else
            {
                param.put("OPR_SOURCE", "08");
            }

            //咪咕无线音乐会员：老会员退订老服务，新会员退订新服务
            //if(param.getString("BIZ_TYPE_CODE","").equals("81") && param.getString("SERVICE_ID").equals("80012675"))
            if(param.getString("BIZ_TYPE_CODE","").equals("81") && param.getString("BIZ_CODE","").equals("698039018080000003") && param.getString("SP_CODE","").equals("698039"))
            {

                if(param.getString("OPER_CODE","").equals("02")){
                	UcaData uca = UcaDataFactory.getNormalUca(param.getString("SERIAL_NUMBER"));
                    List<PlatSvcTradeData> memberPlatSvcs = uca.getUserPlatSvcByServiceId("98001901");
                    if(memberPlatSvcs != null && memberPlatSvcs.size() > 0)
                    {
                        PlatSvcTradeData memberPlatSvc = memberPlatSvcs.get(0);
                        List<AttrTradeData> memberLevelList = uca.getUserAttrsByRelaInstId(memberPlatSvc.getInstId());
                        AttrTradeData memberLevel = memberLevelList.get(0);
                        if ("302".equals(memberLevel.getAttrCode()))
                        {
                            param.put("BIZ_TYPE_CODE", "19");
                            param.put("SERVICE_ID", "98001901");
                            param.put("SP_CODE", "REG_SP");
                            param.put("BIZ_CODE", "REG_SP");
                        }
                    }
                }
            }

            return CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", param);
        }
        else
        {
            if (!"560".equals(param.getString("SERVICE_ID")))
            {
                IData changeProductParam = new DataMap();
                changeProductParam.putAll(param);
                changeProductParam.put("ELEMENT_TYPE_CODE", "S");
                changeProductParam.put("ELEMENT_ID", param.getString("SERVICE_ID"));
                changeProductParam.put("MODIFY_TAG", "1");
                changeProductParam.put("BOOKING_TAG", "0"); // 非预约
                return CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", changeProductParam);
            }
            else
            {
                IData callFiterParam = new DataMap();
                callFiterParam.putAll(param);
                callFiterParam.put("OPER_TYPE", "2");
                callFiterParam.put("X_TAGCHAR", "0");
                IDataset result = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", callFiterParam);
                if (null == result || !"0".equals(result.getData(0).getString("X_RESULTCODE", "")))
                {
                    CSAppException.apperr(PlatException.CRM_PLAT_74, "取消来电拒接失败！");
                }
                return result;
            }
        }
    }

    /**
     * 校园季前营销二次确认回复
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData twoCheckBackForSchoolSale(IData param) throws Exception
    {
        IData result = new DataMap();
        String serialNumber = param.getString("SERIAL_NUMBER");
        String serviceId = param.getString("SERVICE_ID");
        String noticeContent = param.getString("NOTICE_CONTENT", "").trim();

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);

        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }

        String userId = userInfo.getString("USER_ID");
        String reSmsTag = "0"; // 回复短信内容
        if ("是".equals(noticeContent))
        {
            reSmsTag = "1";
        }
        //增加和留言回复内容判断
        if("40223723".equals(serviceId)&& "Y".equals(noticeContent) )
        {
        	reSmsTag = "1";
        } 
        if (StringUtils.isBlank(serviceId))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "-1:未传入SERVICE_ID");
        }

        if (StringUtils.isBlank(serialNumber))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "-1:未传入SERIAL_NUMBER");
        }

        IDataset userPlatSvcList = UserPlatSvcInfoQry.querySchoolSaleDuePlatSvc(serviceId, userId);

        if (IDataUtil.isEmpty(userPlatSvcList))
        {
            result.put("X_RESULTCODE", "980001");
            result.put("X_RESULTINFO", "未查询到相关记录");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
        }
        else
        {
            String endSmsDate = userPlatSvcList.getData(0).getString("END_SMS_DATE");
            if (SysDateMgr.getSysTime().compareTo(endSmsDate) > 0)
            {
                result.put("X_RESULTCODE", "980002");
                result.put("X_RESULTINFO", "回复的时限已超过24小时");
                result.put("X_RSPTYPE", "2");// add by ouyk
                result.put("X_RSPCODE", "2998");// add by ouyk
            }
            else
            {
                if (reSmsTag.equals("1"))
                {// 回复“是”
                    result.put("X_RESULTCODE", "0");
                    result.put("X_RESULTINFO", "Trade OK!");
                }
                else
                {
                    result.put("X_RESULTCODE", "980003");
                    result.put("X_RESULTINFO", "回复其他信息");
                    result.put("X_RSPTYPE", "2");// add by ouyk
                    result.put("X_RSPCODE", "2998");// add by ouyk
                }

                IData queryParam = new DataMap();
                queryParam.put("RESMS_DATE", SysDateMgr.getSysTime());
                queryParam.put("RESMS_TAG", reSmsTag);
                queryParam.put("NOTICE_CONTENT", noticeContent);
                queryParam.put("REMARK", "用户二次确认短信返回");
                queryParam.put("SERVICE_ID", serviceId);
                queryParam.put("USER_ID", userId);
                StringBuilder sql = new StringBuilder();
                sql.append(" UPDATE TF_F_USER_PLATSVC_SHL ");
                sql.append("    SET RESMS_DATE       = TO_DATE(:RESMS_DATE,'yyyy-MM-dd hh24:mi:ss'), ");
                sql.append("        resms_tag        = :RESMS_TAG,");
                sql.append("        resms_content    = :NOTICE_CONTENT, ");
                sql.append("        REMARK           = :REMARK ");
                sql.append("  WHERE USER_ID = :USER_ID ");
                sql.append("    AND SERVICE_ID = :SERVICE_ID ");
                Dao.executeUpdate(sql, queryParam);
            }

        }
        return result;
    }

    /**
     * 通行证变更
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset updateNetInfo(IData data) throws Exception
    {
        IData result = new DataMap();
        String OPR_NUMB = data.getString("OPR_NUMB");// 发起方的操作流水号
        String OPERATE_DATE = data.getString("OPERATE_DATE");// 操作时间
        String PASS_ID = data.getString("PASS_ID");// 通行证号
        String ACTION_REASON = data.getString("ACTION_REASON");// 操作原因
        String SERIAL_NUMBER = data.getString("SERIAL_NUMBER");// 使用用户标识
        String check = "";
        if (null == OPR_NUMB || "".equals(OPR_NUMB))
        {
            check += "OPR_NUMB IS NULL,";
        }
        if (null == OPERATE_DATE || "".equals(OPERATE_DATE))
        {
            check += "OPERATE_DATE IS NULL,";
        }
        if (null == PASS_ID || "".equals(PASS_ID))
        {
            check += "PASS_ID IS NULL,";
        }
        if (null == ACTION_REASON || "".equals(ACTION_REASON))
        {
            check += "ACTION_REASON IS NULL,";
        }
        if (null == SERIAL_NUMBER || "".equals(SERIAL_NUMBER))
        {
            check += "SERIAL_NUMBER IS NULL,";
        }
        if ("".equals(check))
        {
            PlatOrderIntfBean bean = new PlatOrderIntfBean();
            IData userInfo = UcaInfoQry.qryUserInfoBySn(SERIAL_NUMBER);
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1);
            }
            data.put("USER_ID", userInfo.getString("USER_ID"));
            result = bean.updateInfo(data);
        }
        else
        {
            result.put("X_RESULTCODE", "2999");
            result.put("X_RESULTINFO", check);
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
        }
        result.put("UMCP_OPR_NUMB", OPR_NUMB);
        result.put("OPERATE_DATE", OPERATE_DATE);
        IDataset rs = new DatasetList();
        rs.add(result);
        return rs;
    }
   
    public IData synFamilyCircle(IData data) throws Exception
    {
    	return PlatOrderIntfBean.synFamilyCircle(data);
    }
    
    public IData synSafeGroup(IData data) throws Exception
    {
    	return PlatOrderIntfBean.synSafeGroupInfo(data);
    }
}

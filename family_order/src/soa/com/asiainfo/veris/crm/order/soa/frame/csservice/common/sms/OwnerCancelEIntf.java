
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.SmsPlatCommparaQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.SpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserChargeRemindInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.PlatUtils;

public class OwnerCancelEIntf extends CSBizService
{

    /**
	 *
	 */
    private static final long serialVersionUID = 1L;

    static Logger logger = Logger.getLogger(OwnerCancelEIntf.class.getName());

    /**
     * 退订集团黑莓
     *
     * @return
     * @throws Exception
     */
    private IData cancelGroupBlackBerry(String serialNumber, String userId, String spCode, String seq, String content) throws Exception
    {
        IData result = new DataMap();
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("GROUP_ID", spCode);// 集团客户编码
        params.put("PRODUCT_ID", "7091");// 集团彩铃产品ID
        params.put("MODIFY_TAG", "1");// 操作类型 "1"－集团产品成员退订
        params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        params.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        params.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        params.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        params.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        IDataset resultList = CSAppCall.call("SS.TcsGrpIntfSVC.processMemProduct", params);
        result = resultList.getData(0);
        if (StringUtils.isNotBlank(result.getString("ORDER_ID")) || StringUtils.isNotBlank(result.getString("TRADE_ID")))
        {
            UserChargeRemindInfoQry.updateUserChargeStatus(seq, serialNumber, "1", "0", content);
            StringBuilder sendSmsContent = new StringBuilder();
            sendSmsContent.append("尊敬的客户，您好！我们已为您成功取消中国移动的集团Blackberry业务的订购，未收取您任何费用。如有疑问，请咨询10086。中国移动");
            PlatUtils.insertSms(serialNumber, userId, sendSmsContent.toString(), CSBizBean.getTradeEparchyCode(), "扣费提醒下发短信提醒");
        }
        result.put("X_INTFTYPE", "2");

        return result;
    }

    /**
     * 退订集团彩铃
     *
     * @return
     * @throws Exception
     */
    private IData cancelGroupColorRing(String serialNumber, String userId, String spCode, String seq, String content) throws Exception
    {
        IData result = new DataMap();
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("GROUP_ID", spCode);// 集团客户编码
        params.put("PRODUCT_ID", "6200");// 集团彩铃产品ID
        params.put("MODIFY_TAG", "1");// 操作类型 "1"－集团产品成员退订
        params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        params.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        params.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        params.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        params.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        // ProcessGrpMemCancel().processGrpMemberInfo
        IDataset resultList = CSAppCall.call("SS.TcsGrpIntfSVC.processMemProduct", params);
        result = resultList.getData(0);
        if (StringUtils.isNotBlank(result.getString("ORDER_ID")) || StringUtils.isNotBlank(result.getString("TRADE_ID")))
        {
            UserChargeRemindInfoQry.updateUserChargeStatus(seq, serialNumber, "1", "0", content);
            StringBuilder sendSmsContent = new StringBuilder();
            sendSmsContent.append("尊敬的客户，您好！我们已为您成功取消中国移动的集团彩铃业务的订购，未收取您任何费用。如有疑问，请咨询10086。中国移动");
            PlatUtils.insertSms(serialNumber, userId, sendSmsContent.toString(), CSBizBean.getTradeEparchyCode(), "扣费提醒下发短信提醒");
        }
        result.put("X_INTFTYPE", "2");

        return result;
    }

    /**
     * 退订pushMail成员
     *
     * @return
     * @throws Exception
     */
    private IData cancelGroupPushMail(String serialNumber, String userId, String spCode, String seq, String content) throws Exception
    {
        IData result = new DataMap();
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("GROUP_ID", spCode);// 集团客户编码
        params.put("PRODUCT_ID", "7090");// 集团彩铃产品ID
        params.put("MODIFY_TAG", "1");// 操作类型 "1"－集团产品成员退订
        params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        params.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        params.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        params.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        params.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        IDataset resultList = CSAppCall.call("SS.TcsGrpIntfSVC.processMemProduct", params);
        result = resultList.getData(0);
        if (StringUtils.isNotBlank(result.getString("ORDER_ID")) || StringUtils.isNotBlank(result.getString("TRADE_ID")))
        {
            UserChargeRemindInfoQry.updateUserChargeStatus(seq, serialNumber, "1", "0", content);
            StringBuilder sendSmsContent = new StringBuilder();
            sendSmsContent.append("尊敬的客户，您好！我们已为您成功取消中国移动的集团手机邮箱业务的订购，未收取您任何费用。如有疑问，请咨询10086。中国移动");
            PlatUtils.insertSms(serialNumber, userId, sendSmsContent.toString(), CSBizBean.getTradeEparchyCode(), "扣费提醒下发短信提醒");
        }
        result.put("X_INTFTYPE", "2");

        return result;
    }

    /**
     * 退订集团V网
     *
     * @return
     * @throws Exception
     */
    private IData cancelGroupVNet(String serialNumber, String userId, String seq, String content) throws Exception
    {
        IData result = new DataMap();
        // 集团V网成员退订接口入参
        IData indata = new DataMap();
        indata.put("X_CUST_TYPE", "SHORT_MESSAGE_PORTAL");
        indata.put("X_SUBTRANS_CODE", "ProcessGrpMem");
        indata.put("PRODUCT_ID", "8000");// 集团V网产品ID
        indata.put("SERIAL_NUMBER", serialNumber);
        indata.put("MODIFY_TAG", "1");
        indata.put("REMOVE_TAG", "0");
        indata.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        indata.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        indata.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        indata.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        indata.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        IDataset resultList = CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", indata);
        result = resultList.getData(0);
        if (StringUtils.isNotBlank(result.getString("ORDER_ID")) || StringUtils.isNotBlank(result.getString("TRADE_ID")))
        {
            UserChargeRemindInfoQry.updateUserChargeStatus(seq, serialNumber, "1", "0", content);

            StringBuilder sendSmsContent = new StringBuilder();
            sendSmsContent.append("尊敬的客户，您好！我们已为您成功取消中国移动的集团VPMN业务的订购，未收取您任何费用。如有疑问，请咨询10086。中国移动");

            PlatUtils.insertSms(serialNumber, userId, sendSmsContent.toString(), CSBizBean.getTradeEparchyCode(), "扣费提醒下发短信提醒");
        }

        result.put("X_INTFTYPE", "2");
        return result;
    }

    /**
     * 退订平台业务
     *
     * @param serialNumber
     * @param userId
     * @param eparchyCode
     * @param spCode
     * @param serviceCode
     * @param bizTypeCode
     * @param seq
     * @param content
     * @return
     * @throws Exception
     */
    private IData cancelPlatSvc(String serialNumber, String userId, String eparchyCode, String spCode, String serviceCode, String rsrvStr3, String seq, String content) throws Exception
    {
    	IDataset commparaList =SmsPlatCommparaQry.querySmsPlatInfo(spCode, serviceCode, rsrvStr3);
    	String bizTypeCode = "";
    	if(IDataUtil.isNotEmpty(commparaList))
    	{
    		IData commpara = commparaList.getData(0);
    		if("1".equals(commpara.getString("INTF_TYPE","X")))
    		{
    			bizTypeCode =commpara.getString("PARA_CODE4","XXXX");
    		}
    	}

        IData result = new DataMap();
        IDataset platSvcLsit = BofQuery.getPlatInfoByBizTypeCode(bizTypeCode, spCode, serviceCode);
        if (IDataUtil.isEmpty(platSvcLsit))
        {
            CSAppException.apperr(BofException.CRM_BOF_016);
        }

        IData platSvcParam = new DataMap();
        platSvcParam.put("OPER_CODE", "07");
        platSvcParam.put("SP_CODE", spCode);
        platSvcParam.put("BIZ_CODE", serviceCode);
        platSvcParam.put("BIZ_TYPE_CODE", bizTypeCode);
        platSvcParam.put("SERIAL_NUMBER", serialNumber);
        platSvcParam.put("RSRV_STR8", "FEE_NOTICE");

        try
         {
             IDataset resultList = CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", platSvcParam);
             result = resultList.getData(0);
         }
        catch (Exception e)
         {     //e抛错信息： com.ailk.common.BaseException: CRM_PLAT_0913`用户未订购该业务或已退订       	业务受理前条件判断：重复订购校验
        	int number_int = e.toString().indexOf("CRM_PLAT") ;
        	String X_RESULTCODE ="0913";
        	if (number_int > 0){
        				X_RESULTCODE = e.toString().substring(number_int+9, number_int+13);
        	}
            IData result_data = new DataMap();
        	result_data.put("X_RESULTCODE", "0913");  //跟短厅及uip接口确定 用户未订购该业务或已退订 默认返回0913
            result_data.put("X_RSPDESC", "用户未订购该业务或已退订");
       	    if( !"0913".equals(X_RESULTCODE)){
       	    			result_data.put("X_RESULTCODE", "0922");
       	    			result_data.put("X_RSPDESC", e.toString()); //抛出具体错误
       	    }
        	result_data.put("X_INTFTYPE", "2");
        	return result_data;
         }
        if (StringUtils.isNotBlank(result.getString("ORDER_ID")) || StringUtils.isNotBlank(result.getString("TRADE_ID")))
        {
            UserChargeRemindInfoQry.updateUserChargeStatus(seq, serialNumber, "1", "0", content);

            IDataset spBizList = SpInfoQry.querySpBizInfo(spCode, serviceCode);
            if (IDataUtil.isEmpty(spBizList))
            {
                CSAppException.apperr(BofException.CRM_BOF_016);
            }
            IData spBiz = spBizList.getData(0);
            String serviceName = spBiz.getString("BIZ_NAME", "");
            StringBuilder sendSmsContent = new StringBuilder();
            sendSmsContent.append("尊敬的客户，您好！我们已为您成功取消中国移动的");
            sendSmsContent.append(serviceName);
            sendSmsContent.append("业务的订购，未收取您任何费用。如有疑问，请咨询10086。中国移动");

            PlatUtils.insertSms(serialNumber, userId, sendSmsContent.toString(), eparchyCode, "扣费提醒下发短信提醒");
			result.put("X_RESULTCODE", "0");
        }

        result.put("X_INTFTYPE", "2");


        return result;
    }

    /**
     * 退订服务优惠
     *
     * @param serialNumber
     * @param userId
     * @param eparchyCode
     * @param serviceCode
     * @param seq
     * @param content
     * @return
     * @throws Exception
     */
    private IData cancelProduct(String serialNumber, String userId, String eparchyCode, String serviceCode, String seq, String content) throws Exception
    {
        IData result = new DataMap();
        IData changeProductParam = new DataMap();
        changeProductParam.put("ELEMENT_TYPE_CODE", "S");
        changeProductParam.put("ELEMENT_ID", serviceCode);
        changeProductParam.put("MODIFY_TAG", "1");
        changeProductParam.put("BOOKING_TAG", "0");
        changeProductParam.put("SERIAL_NUMBER", serialNumber);

        IDataset resultList = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", changeProductParam);
        result = resultList.getData(0);
        if (StringUtils.isNotBlank(result.getString("ORDER_ID")) || StringUtils.isNotBlank(result.getString("TRADE_ID")))
        {
            UserChargeRemindInfoQry.updateUserChargeStatus(seq, serialNumber, "1", "0", content);

            String serviceName = USvcInfoQry.getSvcNameBySvcId(serviceCode);
            StringBuilder sendSmsContent = new StringBuilder("尊敬的客户，您好！我们已为您成功取消中国移动的");
            sendSmsContent.append(serviceName);
            sendSmsContent.append("业务的订购，未收取您任何费用。如有疑问，请咨询10086。中国移动");
            PlatUtils.insertSms(serialNumber, userId, sendSmsContent.toString(), eparchyCode, "扣费提醒下发短信");
        }

        result.put("X_INTFTYPE", "1");
        return result;
    }

    /**
     * 退订和校园
     *
     * @return
     * @throws Exception
     */
    private IData cancelSchoolMsg(String serialNumber, String userId, IData userChargeRemind, String seq, String content) throws Exception
    {
        IData result = new DataMap();
        // 集团V网成员退订接口入参
        IData indata = new DataMap();
        indata.put("ORIGDOMAIN", "PADC");
        indata.put("HOMEDOMAIN", "BOSS");
        indata.put("BIPCODE", "BIP4B248");
        indata.put("ACTIVITYCODE", "T2101709");
        indata.put("TESTFLAG", "0");
        indata.put("X_TRANS_CODE", "INTF_BBOSS_2_CRM_ACTION");
        indata.put("KIND_ID", "BIP4B248_T2101709_1_0");
        indata.put("BUSI_SIGN", "BIP4B248_T2101709_1_0");
        indata.put("BIZ_CODE", "AHI3911601");
        indata.put("OPR_CODE", "02");
        indata.put("SERV_CODE", userChargeRemind.getString("SERVICE_CODE"));
        indata.put("ECID", userChargeRemind.getString("SP_CODE"));
        indata.put("SPECIAL_TAG", "xxtUnsubcribed");
        indata.put("MOB_NUM", serialNumber);
        indata.put("SERV_CODE_PROP", userChargeRemind.getString("RSRV_STR2"));
        indata.put("SERVICE_ID", "574201");
        indata.put("DISCNT_CODE", userChargeRemind.getString("RSRV_STR3"));
        indata.put("EC_USER_ID", userChargeRemind.getString("RSRV_STR6"));
        indata.put("RELATION_TYPE_CODE", "C4");

        indata.put("EFFT_T", SysDateMgr.getSysDateYYYYMMDD());
        indata.put("TAG", "normalPf");

        // -  INTF_BBOSS_2_CRM_ACTION 集团liaolc
        IDataset resultList = CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", indata);
        result = resultList.getData(0);
        if (StringUtils.isNotBlank(result.getString("ORDER_ID")) || StringUtils.isNotBlank(result.getString("TRADE_ID")))
        {
            UserChargeRemindInfoQry.updateUserChargeStatus(seq, serialNumber, "1", "0", content);

            StringBuilder sendSmsContent = new StringBuilder();
            sendSmsContent.append("尊敬的客户，您好！我们已为您成功取消中国移动的和校园业务的订购，未收取您任何费用。如有疑问，请咨询10086。中国移动");

            PlatUtils.insertSms(serialNumber, userId, sendSmsContent.toString(), CSBizBean.getTradeEparchyCode(), "扣费提醒下发短信提醒");
        }

        result.put("X_INTFTYPE", "2");
        return result;
    }

    /**
     * 扣费提醒提供给电子渠道接口
     *
     * @param pd
     * @return
     * @throws Throwable
     */
    public IData platSVCCancelE(IData data) throws Throwable
    {
        IData result = new DataMap();
        String noticeContent = data.getString("NOTICE_CONTENT");
        String strSeq = data.getString("FLOW_ID", "");
        String strSerialNumber = data.getString("SERIAL_NUMBER");

        IData userInfo = UcaInfoQry.qryUserInfoBySn(strSerialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        String userId = userInfo.getString("USER_ID");
        String eparchyCode = userInfo.getString("EPARCHY_CODE");

        if (!strSeq.startsWith("100860") || strSeq.length() != 10)
        {
            result.put("X_RESULTCODE", "0901");
            result.put("X_RESULTINFO", "接口调用错误");
            return result;
        }
        else
        {
            strSeq = strSeq.substring(strSeq.length() - 4, strSeq.length());
        }

        IDataset userChargeRemindInfos = UserChargeRemindInfoQry.queryUserChargeRemind(strSeq, strSerialNumber); // 查询扣费提醒表
        if (IDataUtil.isEmpty(userChargeRemindInfos))
        {
            result.put("X_RESULTCODE", "0900");
            result.put("X_RESULTINFO", "原有业务不存在或已经处理");
            return result;
        }
        else
        {
            if (!("否".equals(noticeContent))) // 如果短信内容中不包含"否"，则更新扣费提醒表状态为"2":用户回复其它后直接返回
            {
                UserChargeRemindInfoQry.updateUserChargeStatus(strSeq, strSerialNumber, "1", "1", noticeContent);
                result.put("X_RESULTCODE", "0");
                result.put("X_RESULTINFO", "Trade ok!");
                return result;
            }
            else
            // 短信内容中包含"否"，则进行sp业务的退订处理
            {
                IData userChargeRemind = userChargeRemindInfos.getData(0);
                String serviceCode = userChargeRemind.getString("SERVICE_CODE"); // 普通服务的service_id或者是平台服务的biz_code
                String spCode = userChargeRemind.getString("SP_CODE");
                String rsrvStr3 = userChargeRemind.getString("RSRV_STR3", "");//
                String operCode = userChargeRemind.getString("OPER_CODE");
                String acctMode = userChargeRemind.getString("ACT_MODE", "0"); // 服务类型
                char charAcctMode = acctMode.charAt(0);
                IData serviceSPcodeDataset = StaticInfoQry.getStaticInfoByTypeIdDataId("TP_F_USER_CHARGEREMIND", "SP_CODE");
                if (null == serviceSPcodeDataset)
                {
                    logger.error("请检查TD_S_STATIC表的配置");
                }
                String configSpCode = serviceSPcodeDataset.getString("DATA_NAME", "aaaa");
                IData cancelResult = null;
                // 如果是普通服务优惠退订
                if (configSpCode.equals(spCode))
                {
                    cancelResult = cancelProduct(strSerialNumber, userId, eparchyCode, serviceCode, strSeq, noticeContent);
                }
                else
                {

                    switch (charAcctMode)
                    {
                        case '7':
                            cancelResult = cancelSchoolMsg(strSerialNumber, userId, userChargeRemind, strSeq, noticeContent);
                        case '8':
                            cancelResult = cancelGroupVNet(strSerialNumber, userId, strSeq, noticeContent);
                        case '9':
                            cancelResult = cancelGroupColorRing(strSerialNumber, userId, spCode, strSeq, noticeContent);
                        case 'A':
                            cancelResult = cancelGroupPushMail(strSerialNumber, userId, spCode, strSeq, noticeContent);
                        case 'B':
                            cancelResult = cancelGroupBlackBerry(strSerialNumber, userId, spCode, strSeq, noticeContent);
                        default:
//                          cancelResult = cancelPlatSvc(strSerialNumber, userId, eparchyCode, spCode, serviceCode, rsrvStr3, strSeq, noticeContent);
                        	cancelResult = cancelPlatSvc(strSerialNumber, userId, eparchyCode, spCode, operCode, rsrvStr3, strSeq, noticeContent);//actmode=0,operCode存放biz_code
                    }
                }

                result.putAll(cancelResult);
            }
        }
        return result;
    }

}


package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.FeeException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MSpBizQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class PlatQryIntfSVC extends CSBizService
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IDataset PlatSvcAttrQry(IData data) throws Exception
    {
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IDataset result = UserPlatSvcInfoQry.queryUserPlatAttr(serialNumber);
//        IData rst = result.toData();
        IData rst = new DataMap();
        if (result == null || result.size() == 0)
        {
        	//新咪咕音乐特级会员会员级别赋值 ---add by huangzl3
        	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        	String userid = userInfo.getString("USER_ID");
        	IDataset newmusic =  UserPlatSvcInfoQry.qryPlatSvcByUserIdServiceId(userid,"80012675");
        	if(newmusic.size()>0){
        		rst.put("SVC_LEVEL", "3");
        	}else{
        		rst.put("SVC_LEVEL", "0");
        	}        	
        }
        else
        {
            rst = result.getData(0);
            String SVC_LEVEL = result.getData(0).getString("ATTR_VALUE");
            rst.put("SVC_LEVEL", SVC_LEVEL);
        }

        IDataset results = new DatasetList();
        results.add(rst);
        return results;

    }
    
    
    /**
     * 查询用户的WLAN服务的订购情况
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData queryUserWlan(IData param) throws Exception
    {
        IData result = new DataMap();
        String serialNumber = param.getString("SERIAL_NUMBER", param.getString("IDVALUE"));
        String bizTypeCode = param.getString("BIZ_TYPE_CODE","92");
        String serviceId = null;
        
        if("02".equals(bizTypeCode))
        {
         serviceId = "98002401";
        }else
        {
         serviceId = "98009201";
        }
        
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        String userId = userInfo.getString("USER_ID");
        IDataset svcList = UserPlatSvcInfoQry.queryUserPlatSvcByUserIdAndServiceId(userId, serviceId);
        if (!IDataUtil.isEmpty(svcList))
        {
            IData svc = svcList.getData(0);
            result.put("BIZ_TYPE_CODE", svc.getString("BIZ_TYPE_CODE"));
            if (PlatConstants.STATE_OK.equals(svc.getString("BIZ_STATE_CODE")))
            {
                result.put("BIZ_STATE_CODE", "0");
            }
            else if (PlatConstants.STATE_PAUSE.equals(svc.getString("BIZ_STATE_CODE")))
            {
                result.put("BIZ_STATE_CODE", "1");
            }
            else
            {
                result.put("BIZ_STATE_CODE", "2");
            }
        }
        else
        {
            result.put("BIZ_TYPE_CODE", bizTypeCode);
            result.put("BIZ_STATE_CODE", "2");
        }
        return result;
    }

    
    

    /**
     * 手机邮箱类型判断查询 接口名 ITF_CRM_MailBizCodeQry
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IData qryMailBizCode(IData param) throws Exception
    {
        IData result = new DataMap();
        String serialNumber = IDataUtil.chkParam(param, "SERIAL_NUMBER");
        // String bizCode = IDataUtil.chkParam(param, "BIZ_CODE");

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }

        String userId = userInfo.getString("USER_ID");
        IDataset userMailSvc = UserPlatSvcInfoQry.query139MailSvc(userId);

        if (IDataUtil.isEmpty(userMailSvc))
        {
            result.put("MAIL_TYPE", "0");
            result.put("X_RESULTCODE", "0");
            result.put("X_RESULTINFO", "用户未开通手机邮箱");
            return result;
        }

        String mailBizCode = userMailSvc.getData(0).getString("BIZ_CODE");

        if (mailBizCode.equals("+MAILMF"))
        {
            result.put("MAIL_TYPE", "1"); // MAIL_TYPE，与电子渠道约定的返回邮箱类型编码
            result.put("X_RESULTCODE", "0");
            result.put("X_RESULTINFO", "免费版邮箱");
        }
        else if (mailBizCode.equals("+MAILBZ"))
        {
            result.put("MAIL_TYPE", "2");
            result.put("X_RESULTCODE", "0");
            result.put("X_RESULTINFO", "5元版邮箱");
        }
        else if (mailBizCode.equals("+MAILVIP"))
        {
            result.put("MAIL_TYPE", "3");
            result.put("X_RESULTCODE", "0");
            result.put("X_RESULTINFO", "20元版邮箱");
        }
        else
        {
            result.put("MAIL_TYPE", "4");
            result.put("X_RESULTCODE", "0");
            result.put("X_RESULTINFO", "其他邮箱类型");
        }

        return result;
    }

    /**
     * 客服根据biz_code,sp_code,biz_type_code查询服务信息
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */

    public IData qryPlatSvcByAll(IData param) throws Exception
    {
        String SP_CODE = param.getString("SP_CODE", "");
        String BIZ_CODE = param.getString("BIZ_CODE", "");
        String BIZ_TYPE_CODE = param.getString("BIZ_TYPE_CODE", "");
        if ("".equals(SP_CODE))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_257);
        }
        if ("".equals(BIZ_CODE))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_315);
        }
        if ("".equals(BIZ_TYPE_CODE))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_320);
        }
        IDataset platSvcs = PlatSvcInfoQry.qryPlatSvcByAll(SP_CODE, BIZ_CODE, BIZ_TYPE_CODE);
        if (null == platSvcs || platSvcs.size() == 0)
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_152);
        }

        IData platSvc = platSvcs.getData(0);
        return platSvc;
    }

    /**
     * 客服根据service_id查询服务信息
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryPlatSvcByCustService(IData param) throws Exception
    {
        String serviceId = IDataUtil.chkParam(param, "SERVICE_ID");
        IDataset platSvcs = PlatInfoQry.getPlatsvcCustSvc(serviceId);
        if (IDataUtil.isEmpty(platSvcs))
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_137);
        }

        return platSvcs;
    }

    /**
     * ITF_CRM_ESPInfoQry
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData qrySPInfo(IData data) throws Exception
    {

        IDataset result = new DatasetList();
        IData inparams = new DataMap();
        String SP_CODE = data.getString("SP_ID", "");
        String BIZ_CODE = data.getString("SP_SVC_ID", "");
        String SP_NAME = data.getString("SP_NAME", "");
        String BIZ_NAME = data.getString("BIZ_TYPE", "");
        /* 0-根据SP企业代码获取；1-根据SP服务代码获取;2-同时根据SP企业代码和SP服务代码获取;3-模糊查询SP的服务信息和企业信息 */
        String X_GETMODE = data.getString("X_GETMODE");
        inparams.put("SP_CODE", SP_CODE);
        inparams.put("BIZ_CODE", BIZ_CODE);
        inparams.put("SP_NAME", SP_NAME);
        inparams.put("BIZ_NAME", BIZ_NAME);

        if ("0".equals(X_GETMODE))
        {
            if ("".equals("SP_CODE"))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "0000" + ": " + "须输入企业代码！");
            }
            result = PlatInfoQry.getSPInfo0(inparams);
        }
        if ("1".equals(X_GETMODE))
        {
            if ("".equals("BIZ_CODE"))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "0001" + ": " + "须输入SP服务代码！");
            }
            result = PlatInfoQry.getSPInfo1(inparams);
        }
        if ("2".equals(X_GETMODE))
        {
            if ("".equals("SP_CODE"))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "0000" + ": " + "须输入企业代码！");
            }
            if ("".equals("BIZ_CODE"))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "0001" + ": " + "须输入SP服务代码！");
            }
            if ("".equals("SP_CODE") && "".equals("BIZ_CODE"))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "0004" + ": " + "须输入企业代码和SP服务代码！");
            }
            result = PlatInfoQry.getSPInfo2(inparams);
        }
        if ("3".equals(X_GETMODE))
        {

            result = PlatInfoQry.getSPInfo3(inparams);
        }

        if (result == null || result.size() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "0005" + ": " + "未查到匹配的SP服务信息!");
        }

        return result.toData();
    }

    /**
     * 业务平台业务的开关状态查询接口
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset qrySwitchIntfs(IData data) throws Exception
    {
        IData result = new DataMap();
        IDataset results = new DatasetList();
        // 获取手机号码
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IData param = new DataMap();
        IData userInfos = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (!StringUtils.equals("00", userInfos.getString("NET_TYPE_CODE")))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }

        if (userInfos == null || userInfos.size() == 0)
        {
            result.put("X_RESULTCODE", "-1");
            result.put("X_RESULTINFO", "根据手机号码[" + serialNumber + "],没有找到相应的用户资料!");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            results.add(result);
            return results;
        }

        String userId = userInfos.getString("USER_ID");
        IDataset userSwitchInfos = UserPlatSvcInfoQry.queryUserSwitchinfos(userId);
        if (userSwitchInfos == null || userSwitchInfos.size() == 0)
        {
            result.put("X_RESULTCODE", "-1");
            result.put("X_RESULTINFO", "根据手机号码[" + serialNumber + "],没有找到相应的服务开关信息!");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            results.add(result);
            return results;
        }
        return userSwitchInfos;
    }

    /**
     * 视频会议预约查询，接口名 ITF_CRM_VideoMeetingBookQry
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IData qryVideoMeetingBooking(IData data) throws Exception
    {
        IData result = new DataMap();
        IDataset BookingResult = new DatasetList();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "手机号码不能为空");
        }
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));// 手机号码
        // 通过手机号码找到id
        IData user = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
        String userId = IDataUtil.isNotEmpty(user) ? user.getString("USER_ID", "") : "";
       /* param.put("USER_ID", userId);

        param.put("BIZ_TYPE_CODE", "32");*/
        IDataset platSvcSet = UserPlatSvcInfoQry.queryPlatSvcInfo(userId, "32");
        if (platSvcSet == null || platSvcSet.size() == 0)
        {
            result.put("OPENED_CONF", "1"); // 约定的输出参数,标志是否已开通视频会议
            result.put("X_RESULTCODE", "0");
            result.put("X_RESULTINFO", "用户未开通基本业务");
            return result;
        }

        IDataset bookingSet = PlatInfoQry.getUserPlatSvc1(param);
        if (bookingSet == null || bookingSet.size() == 0)
        {
            result.put("OPENED_CONF", "0"); // 约定的输出参数,标志是否已开通视频会议
            result.put("X_RESULTCODE", "0");
            result.put("X_RESULTINFO", "没有用户预约信息");
            return result;
        }
        result = bookingSet.toData();
        result.put("OPENED_CONF", "0"); // 约定的输出参数,标志是否已开通视频会议
        return result;
    }

    /**
     * 视频留言属性信息查询, 接口名 ITF_CRM_VideoMessageQry
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IData qryVideoMessage(IData param) throws Exception
    {
        IData result = new DataMap();
        String serialNumber = IDataUtil.chkParam(param, "SERIAL_NUMBER");

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }

        String userId = userInfo.getString("USER_ID");

        IDataset svcList = UserPlatSvcInfoQry.queryUserPlatSvcByUserIdAndServiceId(userId, "3005");
        if (IDataUtil.isEmpty(svcList))
        {
            result.put("OPENED_MESSAGE", "1"); // 约定的输出参数,标志是否已开通视频留言
            result.put("X_RESULTCODE", "0");
            result.put("X_RESULTINFO", "用户未开通视频留言业务");
            return result;
        }

        IData usePlatSvc = svcList.getData(0);

        IDataset userAttrList = UserAttrInfoQry.qryUserAttrByUserRelaInstId(userId, usePlatSvc.getString("INST_ID"));
        ;
        if (IDataUtil.isEmpty(userAttrList))
        {
            result.put("OPENED_MESSAGE", "0");
            result.put("X_RESULTCODE", "0");
            result.put("X_RESULTINFO", "没有获取到视频留言属性信息");
            return result;
        }

        // for(int i=0;i<userAttrList.size();i++)
        // {
        // IData userAttr = userAttrList.getData(i);
        // userAttr.put("INFO_CODE", userAttr.getString("ATTR_CODE",""));
        // userAttr.put("INFO_VALUE", userAttr.getString("ATTR_VALUE",""));
        // }

        result = userAttrList.toData();
        result.put("OPENED_MESSAGE", "0");
        return result;
    }

    /**
     * @Function: queryDsmpOrder
     * @Description: 查询DSMp订单信息
     * @param: @param data
     * @param: @return
     * @param: @throws Exception
     * @return：IDataset
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 9:14:59 AM Jul 30, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------*
     */
    public IDataset queryDsmpOrder(IData data) throws Exception
    {
        String serialNumber = data.getString("SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        String userId = userInfo.getString("USER_ID");
        IDataset spOrder = PlatSvcInfoQry.querySpOrderByUserId(userId, data.getString("BIZ_CODE"), data.getString("SP_CODE"));

        StringBuilder notice = new StringBuilder();
        if (IDataUtil.isEmpty(spOrder))
        {
            notice.append("尊敬的用户，您目前尚未订购任何SP服务。中国移动");
        }
        else
        {
            notice.append("尊敬的用户，您目前正在使用的SP服务有：");
            for (int i = 0; i < spOrder.size(); i++)
            {
                IData order = spOrder.getData(i);
                String spName = order.getString("SP_NAME");
                String bizType = order.getString("BIZ_TYPE");
                String bizName = order.getString("BIZ_NAME");
                Double price = Double.parseDouble(order.getString("PRICE"));

                // 查询并解析计费类型
                String billFlg = order.getString("BILL_TYPE");
                String billType = StaticUtil.getStaticValue(getVisit(), "TD_S_COMMPARA", new String[]
                { "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE", "PARA_CODE1" }, "PARA_CODE2", new String[]
                { "CSM", "3699", billFlg, "DSMP" });
                if (StringUtils.isBlank(billType))
                {
                    CSAppException.apperr(FeeException.CRM_FEE_8);// 计费类型为空
                }
                String strPrice = Math.round(price / 1000.0) + billType;

                String content = "";
                if (i == spOrder.size() - 1)
                {
                    content = i + 1 + "." + spName + "，" + bizType + "，" + bizName + "，" + strPrice + "" + "。";
                }
                else
                {
                    content = i + 1 + "." + spName + "，" + bizType + "，" + bizName + "，" + strPrice + "" + "；";
                }
                notice.append(content);
            }
            notice.append("中国移动");
        }

        String remark = "平台业务短信通知";
        String inModeCode = data.getString("IN_MODE_CODE", "6");

        IData param = new DataMap();
        param.put("TRADE_EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        param.put("IN_MODE_CODE", inModeCode);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("USER_ID", userId);
        param.put("NOTICE_CONTENT", notice.toString());
        param.put("PRIORITY", "1000");
        param.put("TRADE_STAFF_ID", "ITF00000");
        param.put("TRADE_DEPART_ID", "ITF00");
        param.put("REMARK", remark);
        param.put("TRADE_TYPE_CODE", "3700");
        param.put("RECV_OBJECT", serialNumber);
        SmsSend.insSms(param);
        return null;
    }

    /**
     * 查询局数据
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset querySpBizInfo(IData param) throws Exception
    {
        IDataset result = null;
        String spCode = param.getString("SP_ID", "");
        String bizCode = param.getString("SP_SVC_ID", "");
        String spName = param.getString("SP_NAME", "");
        String bizName = param.getString("BIZ_TYPE", "");

        // 0-根据SP企业代码获取；1-根据SP服务代码获取;2-同时根据SP企业代码和SP服务代码获取;3-模糊查询SP的服务信息和企业信息
        String getMode = param.getString("X_GETMODE");
        if ("3".equals(getMode))
        {
            if (StringUtils.isEmpty(spName) || StringUtils.isEmpty(bizName))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "0002:服务名称和企业名称不能同时为空!");
            }
            result = MSpBizQry.queryBizInfoBySpNameBizName(spName, bizName);

            if (IDataUtil.isEmpty(result))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "0005:未查到匹配的SP服务信息!");
            }

            return result;
        }

        if ("0".equals(getMode))
        {
            if ("".equals(spCode))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "0000:须输入企业代码！");
            }
        }
        if ("1".equals(getMode))
        {
            if ("".equals(bizCode))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "0001:须输入SP服务代码！");
            }
        }
        if ("2".equals(getMode))
        {
            if ("".equals(spCode))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "0000:须输入企业代码！");
            }
            if ("".equals(bizCode))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "0001:须输入SP服务代码！");
            }
        }

        result = MSpBizQry.queryBizInfoBySpcodeBizCode(spCode, bizCode);

        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "0005:未查到匹配的SP服务信息!");
        }

        return result;
    }

    /**
     * 查询用户的手机支付平台服务的订购情况
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData queryUserMobilePay(IData param) throws Exception
    {
        IData result = new DataMap();
        String serialNumber = param.getString("SERIAL_NUMBER", param.getString("IDVALUE"));
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }

        String userId = userInfo.getString("USER_ID");
        IDataset svcList = UserPlatSvcInfoQry.queryUserPlatSvcByUserIdAndServiceId(userId, "99166951");
        if (!IDataUtil.isEmpty(svcList))
        {
            IData svc = svcList.getData(0);
            result.put("IDVALUE", param.getString("SERIAL_NUMBER", ""));
            result.put("MSISDN", param.getString("SERIAL_NUMBER", ""));
            result.put("BIZ_TYPE_CODE", svc.getString("BIZ_TYPE_CODE"));

            if (PlatConstants.STATE_OK.equals(svc.getString("BIZ_STATE_CODE")))
            {
                result.put("BIZ_STATE_CODE", "0");
            }
            else if (PlatConstants.STATE_PAUSE.equals(svc.getString("BIZ_STATE_CODE")))
            {
                result.put("BIZ_STATE_CODE", "1");
            }
            else
            {
                result.put("BIZ_STATE_CODE", "2");
            }
        }
        else
        {
            result.put("IDVALUE", param.getString("SERIAL_NUMBER", ""));
            result.put("MSISDN", param.getString("SERIAL_NUMBER", ""));
            result.put("BIZ_TYPE_CODE", "54");
            result.put("BIZ_STATE_CODE", "2");
        }

        return result;
    }
}

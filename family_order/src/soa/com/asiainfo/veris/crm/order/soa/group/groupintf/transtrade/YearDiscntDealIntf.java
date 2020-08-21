
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.template.GroupSmsTemplateBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

/**
 * 集团业务包年套餐续订接口
 * 
 * @author sungq3
 */
public class YearDiscntDealIntf
{
    /**
     * 集团业务包年套餐续
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset dealYearDiscnt(IData inparam) throws Exception
    {
        String tradeRoute = inparam.getString("TRADE_ROUTE_VALUE", "0898");

        // 返回参数
        IData result = new DataMap();

        // 续订包年套餐
        IData data = orderYearDiscnt(inparam);

        // 续订成功标识
        boolean flag = data.getBoolean("FLAG", true);

        if (flag)
        {
            // 设置返回参数({ "0", "受理成功" })
            result.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
            result.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        }
        else
        {
            // 下发订购通知短信
            sendOrderErrorSMS(inparam.getString("SERIAL_NUMBER"), data.getString("USER_ID"), inparam.getString("SMS_SEQ"), tradeRoute);

            // 设置返回参数({ "800105", "其他错误" })
            result.put("X_RESULTCODE", IntfField.OTHER_ERR[0]);
            result.put("X_RESULTINFO", data.getString("X_RESULTINFO"));
        }

        return IDataUtil.idToIds(result);
    }

    /**
     * 续订包年套餐
     * 
     * @param param
     *            (SMS_SEQ：800/801+user_id的后九位；SERIAL_NUMBER：集团联系人号码；USER_TAG：Y)
     * @return
     * @throws Exception
     */
    private static IData orderYearDiscnt(IData param) throws Exception
    {
        // 返回参数
        IData result = new DataMap();
        result.put("FLAG", true);

        String sms_seq = param.getString("SMS_SEQ");
        if (StringUtils.isEmpty(sms_seq))
        {
            // SMS_SEQ不能为空！
            CSAppException.apperr(GrpException.CRM_GRP_817);
        }
        String serial_number = param.getString("SERIAL_NUMBER");
        if (StringUtils.isEmpty(serial_number))
        {
            // SERIAL_NUMBER不能为空！
            CSAppException.apperr(GrpException.CRM_GRP_818);
        }
        String user_tag = param.getString("USER_TAG");
        if (StringUtils.isEmpty(user_tag))
        {
            // USER_TAG不能为空！
            CSAppException.apperr(GrpException.CRM_GRP_819);
        }
        else if (!"Y".equals(user_tag))
        {
            // 回复的不是Y，业务受理结束！
            CSAppException.apperr(GrpException.CRM_GRP_820);
        }

        // 获取用户id
        String userId = "0";
        IDataset userDataset = UserInfoQry.getUserInfoBySN(serial_number, "0", "00");
        if (IDataUtil.isNotEmpty(userDataset))
        {
            userId = userDataset.getData(0).getString("USER_ID");
        }

        // 判断产品是否支持续订包年套餐
        IDataset attrBizDataset = AttrBizInfoQry.getBizAttr("1", "D", "Year", sms_seq.substring(0, 3), null);
        if (IDataUtil.isEmpty(attrBizDataset))
        {
            // 不支持该产品续订包年套餐！
            CSAppException.apperr(GrpException.CRM_GRP_821);
        }

        // 产品标识
        String productId = attrBizDataset.getData(0).getString("ATTR_VALUE");

        // 产品名称
        String productName = attrBizDataset.getData(0).getString("ATTR_NAME");

        // 根据集团联系人号码查询集团信息
        IDataset grpCustDataset = GrpInfoQry.getGroupCustInfoByGrpMgrSN(serial_number);
        if (IDataUtil.isEmpty(grpCustDataset))
        {
            // 根据集团联系人号码查询不到所对应的集团！
            CSAppException.apperr(GrpException.CRM_GRP_822);
        }

        // 集团客户名称
        String custName = "";
        // 客户经理编码
        String custMgrId = "";

        String orderId = "";

        // 是否获取到集团用户信息
        boolean isGrpUser = false;

        for (int i = 0, size = grpCustDataset.size(); i < size; i++)
        {
            IData grpCustData = grpCustDataset.getData(i);

            // 集团客户名称
            custName = grpCustData.getString("CUST_NAME", "");

            // 客户经理编码
            custMgrId = grpCustData.getString("CUST_MANAGER_ID", "");

            // 获取集团用户信息
            IDataset grpUserDataset = UserInfoQry.qryUserInfoByCustIdProdIdUserIdLike(grpCustData.getString("CUST_ID"), productId, sms_seq.substring(3));
            if (IDataUtil.isEmpty(grpUserDataset))
            {
                continue;
            }
            isGrpUser = true;

            IData grpUserData = grpUserDataset.getData(0);
            String grpUserId = grpUserData.getString("USER_ID");

            // 设置服务入参
            IData inparam = new DataMap();
            inparam.put("SMS_SEQ", sms_seq);
            inparam.put("SERIAL_NUMBER", serial_number);
            inparam.put("USER_TAG", user_tag);
            inparam.put("PRODUCT_ID", productId);
            inparam.put("PRODUCT_NAME", productName);
            inparam.put("EPARCHY_CODE", inparam.getString("TRADE_ROUTE_VALUE", "0898"));
            inparam.put(Route.USER_EPARCHY_CODE, inparam.getString("TRADE_ROUTE_VALUE", "0898"));
            inparam.put("USER_ID", userId); // 集团联系人用户id
            inparam.put("GRP_USER_ID", grpUserId); // 集团用户id
            inparam.put("CUST_NAME", custName);
            inparam.put("CUST_MANAGER_ID", custMgrId);
            inparam.put("TRADE_STAFF_ID", "IBOSS000");
            inparam.put("TRADE_DEPART_ID", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_ID", "DEPART_ID", "IBOSS000"));

            IDataset data = new DatasetList();

            try
            {
                // 调用包年套餐续订服务
                data = CSAppCall.call("SS.ChangeYearDiscntUserElementSVC.crtTrade", inparam);
                orderId = data.get(0, "ORDER_ID").toString();
                break;
            }
            catch (Throwable e)
            {
                e = Utility.getBottomException(e);
                result.put("X_RESULTINFO", e.getMessage());
                result.put("FLAG", false);
                break;
            }
        }
        if (!isGrpUser)
        {
            result.put("X_RESULTINFO", "根据传入的SMS_SEQ未查到有效的集团用户资料！");
            result.put("FLAG", false);
        }

        // 集团管理员手机号码
        result.put("SERIAL_NUMBER", serial_number);
        // 集团管理员用户id
        result.put("USER_ID", userId);

        // 产品名称
        result.put("PRODUCT_NAME", productName);

        result.put("ORDER_ID", orderId);

        return result;
    }

    /**
     * 下发订购通知短信
     * 
     * @param serialNumber
     *            集团联系人号码
     * @param userId
     *            集团联系人用户id
     * @param eparchyCode
     *            地州编码
     * @throws Exception
     */
    private static void sendOrderErrorSMS(String serialNumber, String userId, String smsSeq, String eparchyCode) throws Exception
    {
        IData sms_param = new DataMap();

        // 地州编码
        sms_param.put("EPARCHY_CODE", eparchyCode);

        // 接入方式
        sms_param.put("IN_MODE_CODE", "6");

        // 手机号(集团联系人)
        sms_param.put("RECV_OBJECT", serialNumber);

        // 集团客户经理用户id
        sms_param.put("RECV_ID", userId);

        // 员工ID
        sms_param.put("REFER_STAFF_ID", "IBOSS000");

        // 部门ID
        sms_param.put("REFER_DEPART_ID", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_ID", "DEPART_ID", "IBOSS000"));

        // 处理状态，0：已处理，15未处理
        sms_param.put("DEAL_STATE", "15");

        // 备注
        sms_param.put("REMARK", "包年套餐到期提醒");

        sms_param.put("NOTICE_CONTENT", getSMSTemplate(smsSeq, eparchyCode));

        SmsSend.insSms(sms_param);
    }

    /**
     * 获取短信模板
     * 
     * @param sms_seq
     * @param eparchy_code
     * @return
     * @throws Exception
     */
    private static String getSMSTemplate(String sms_seq, String eparchy_code) throws Exception
    {
        IData cfgData = new DataMap();
        cfgData.put("PRODUCT_ID", "-1");// 子类覆盖

        cfgData.put("BRAND_CODE", "-1");
        cfgData.put("EPARCHY_CODE", eparchy_code);
        cfgData.put("IN_MODE_CODE", "6");
        cfgData.put("CANCEL_TAG", "0");

        String seq = sms_seq.substring(0, 3);
        if ("800".equals(seq))
        { // 企业建站
            cfgData.put("TRADE_TYPE_CODE", "2861");
        }
        else if ("801".equals(seq))
        { // 利客发
            cfgData.put("TRADE_TYPE_CODE", "3631");
        }

        // 查询模板
        IDataset templateIds = GroupSmsTemplateBean.getTemplate(cfgData);

        String template = "";

        for (int i = 0, size = templateIds.size(); i < size; i++)
        {
            IData sucSms = templateIds.getData(i);
            String isFalse = sucSms.getString("OBJ_CODE");
            if ("isFalse".equals(isFalse))
            {
                String templateId = sucSms.getString("TEMPLATE_ID");
                template = TemplateBean.getTemplate(templateId);
                break;
            }

        }

        return !"".equals(template) ? template : "尊敬的客户，您好！业务办理失败，详情请咨询分管客户经理查询。";
    }

}

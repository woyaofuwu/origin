
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.DMBusiException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class RemoteQueryCustInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    static transient final Logger logger = Logger.getLogger(RemoteQueryCustInfoSVC.class);

    public String decodeIdType(String IdType)
    {
        String iBossTdType = null;

        if ("0".equals(IdType))
        {
            iBossTdType = "00";
        }
        else if ("1".equals(IdType))
        {
            iBossTdType = "01";
        }
        else if ("A".equals(IdType))
        {
            iBossTdType = "02";
        }
        else if ("C".equals(IdType))
        {
            iBossTdType = "04";
        }
        else if ("K".equals(IdType))
        {
            iBossTdType = "05";
        }
        else
        {
            iBossTdType = "99";
        }

        return iBossTdType;
    }

    public String encodeIdType(String IdType)
    {
        String lanuchTdType = null;

        if ("00".equals(IdType))
        {
            lanuchTdType = "0";
        }
        else if ("01".equals(IdType))
        {
            lanuchTdType = "1";
        }
        else if ("02".equals(IdType))
        {
            lanuchTdType = "A";
        }
        else if ("04".equals(IdType))
        {
            lanuchTdType = "C";
        }
        else if ("05".equals(IdType))
        {
            lanuchTdType = "K";
        }
        else
        {
            lanuchTdType = "Z";
        }

        return lanuchTdType;
    }

    public IDataset getCustInfo(IData param) throws Exception
    {
        IDataset dataset = new DatasetList();
        String idType = param.getString("IDTYPE");
        String idValue = param.getString("IDVALUE");
        String userPasswd = param.getString("USER_PASSWD");
        String idCardType = decodeIdType(param.getString("IDCARDTYPE"));
        String idCardNum = param.getString("IDCARDNUM");
        String routeType = param.getString("ROUTETYPE");
        String mobileNum = param.getString("MOBILENUM");
        String startDate = param.getString("START_DATE");
        String endDate = param.getString("END_DATE");

        IData data = new DataMap();
        IData baseData = IBossCall.remoteBillTransferGetCustInfoIBOSS("0", idType, idValue, userPasswd, idCardType, idCardNum, routeType, mobileNum, startDate, endDate);
        data.putAll(baseData);

        IData custData = IBossCall.remoteBillTransferGetCustInfoIBOSS("6", idType, idValue, userPasswd, idCardType, idCardNum, routeType, mobileNum, startDate, endDate);
        data.putAll(custData);

        IData scoreData = IBossCall.remoteBillTransferGetCustInfoIBOSS("8", idType, idValue, userPasswd, idCardType, idCardNum, routeType, mobileNum, startDate, endDate);
        data.putAll(scoreData);

        IData openData = IBossCall.remoteBillTransferGetCustInfoIBOSS("5", idType, idValue, userPasswd, idCardType, idCardNum, routeType, mobileNum, startDate, endDate);
        data.putAll(openData);

        if (logger.isDebugEnabled())
            logger.debug("-----IBOSS接口(BIP1A001_T1000002_0_0)---返回数据-------" + data);
        if (IDataUtil.isEmpty(data))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_146);
            return null;
        }
        else
        {
            if ("0000".equals(data.getString("X_RSPCODE")))
            {
                IData ret = transData(data);
                dataset.add(ret);
                return dataset;
            }
            else
            {
                CSAppException.apperr(DMBusiException.CRM_DM_146);
                return null;
            }
        }

    }

    public IData transData(IData data) throws Exception
    {
        String lanuchTdType = "";
        String province_code = "";

        if (StringUtils.isNotBlank(data.getString("IDCARDTYPE")))
        {
            lanuchTdType = encodeIdType(data.getString("IDCARDTYPE"));
        }

        if (StringUtils.isNotBlank(data.getString("PROVINCE_CODE")))
        {
            province_code = data.getString("PROVINCE_CODE");
        }

        data.put("IDCARDTYPE", lanuchTdType);
        data.put("PROVINCE_CODE", province_code);

        String level = "";
        if (StringUtils.isNotBlank(data.getString("CUST_LEVLE")))
        {
            level = data.getString("CUST_LEVLE");
            if ("100".equals(level))
                level = "普通客户";
            if ("200".equals(level))
                level = "重要客户";
            if ("201".equals(level))
                level = "党政机关客户";
            if ("202".equals(level))
                level = "军、警、安全机关客户";
            if ("203".equals(level))
                level = "联通合作伙伴客户";
            if ("204".equals(level))
                level = "英雄、模范、名星类客户";
            if ("300".equals(level))
                level = "普通大客户";
            if ("301".equals(level))
                level = "钻石卡大客户";
            if ("302".equals(level))
                level = "金卡大客户";
            if ("303".equals(level))
                level = "银卡大客户";
            if ("304".equals(level))
                level = "贵宾卡大客户";
        }
        data.put("CUST_LEVLE", level);

        String class_id = "";
        if (StringUtils.isNotBlank(data.getString("CLASS_ID")))
        {
            class_id = data.getString("CLASS_ID");
            if ("100".equals(class_id))
                class_id = "普通客户";
            if ("200".equals(class_id))
                class_id = "重要客户";
            if ("201".equals(class_id))
                class_id = "党政机关客户";
            if ("202".equals(class_id))
                class_id = "军、警、安全机关客户";
            if ("203".equals(class_id))
                class_id = "联通合作伙伴客户";
            if ("204".equals(class_id))
                class_id = "英雄、模范、名星类客户";
            if ("300".equals(class_id))
                class_id = "普通大客户";
            if ("301".equals(class_id))
                class_id = "钻石卡大客户";
            if ("302".equals(class_id))
                class_id = "金卡大客户";
            if ("303".equals(class_id))
                class_id = "银卡大客户";
            if ("304".equals(class_id))
                class_id = "贵宾卡大客户";
        }
        data.put("CLASS_ID", class_id);

        String cust_class_type = "";
        if (StringUtils.isNotBlank(data.getString("CUST_CLASS_TYPE")))
        {
            cust_class_type = data.getString("CUST_CLASS_TYPE");
            if ("100".equals(cust_class_type))
                cust_class_type = "普通客户";
            if ("200".equals(cust_class_type))
                cust_class_type = "重要客户";
            if ("201".equals(cust_class_type))
                cust_class_type = "党政机关客户";
            if ("202".equals(cust_class_type))
                cust_class_type = "军、警、安全机关客户";
            if ("203".equals(cust_class_type))
                cust_class_type = "联通合作伙伴客户";
            if ("204".equals(cust_class_type))
                cust_class_type = "英雄、模范、名星类客户";
            if ("300".equals(cust_class_type))
                cust_class_type = "普通大客户";
            if ("301".equals(cust_class_type))
                cust_class_type = "钻石卡大客户";
            if ("302".equals(cust_class_type))
                cust_class_type = "金卡大客户";
            if ("303".equals(cust_class_type))
                cust_class_type = "银卡大客户";
            if ("304".equals(cust_class_type))
                cust_class_type = "贵宾卡大客户";
        }
        data.put("CUST_CLASS_TYPE", cust_class_type);

        String user_state = "";
        if (StringUtils.isNotBlank(data.getString("USER_STATE")))
        {
            user_state = data.getString("USER_STATE");
            if ("00".equals(user_state))
                user_state = "正常";
            if ("01".equals(user_state))
                user_state = "单向停机";
            if ("02".equals(user_state))
                user_state = "停机";
            if ("03".equals(user_state))
                user_state = "预销户";
            if ("04".equals(user_state))
                user_state = "销户";
            if ("05".equals(user_state))
                user_state = "过户";
            if ("06".equals(user_state))
                user_state = "改号";
            if ("90".equals(user_state))
                user_state = "神州行用户";
            if ("99".equals(user_state))
                user_state = "此号码不存在";
        }

        data.put("USER_STATE", user_state);
        if (StringUtils.isNotBlank(data.getString("USERSTATUS")))
        {
            user_state = data.getString("USERSTATUS");
            if ("00".equals(user_state))
                user_state = "正常";
            if ("01".equals(user_state))
                user_state = "单向停机";
            if ("02".equals(user_state))
                user_state = "停机";
            if ("03".equals(user_state))
                user_state = "预销户";
            if ("04".equals(user_state))
                user_state = "销户";
            if ("05".equals(user_state))
                user_state = "过户";
            if ("06".equals(user_state))
                user_state = "改号";
            if ("90".equals(user_state))
                user_state = "神州行用户";
            if ("99".equals(user_state))
                user_state = "此号码不存在";
        }
        data.put("USERSTATUS", user_state);

        String marry = "";
        if (StringUtils.isNotBlank(data.getString("MARRIAGE")))
        {
            marry = data.getString("MARRIAGE");
            if ("0".equals(marry))
                marry = "未婚";
            if ("1".equals(marry))
                marry = "已婚";
            if ("2".equals(marry))
                marry = "其他";
        }
        data.put("MARRIAGE", marry);

        String tag_code = "";
        if (StringUtils.isNotBlank(data.getString("TAG_CODE")))
        {
            tag_code = data.getString("TAG_CODE");
            if ("0".equals(tag_code))
                tag_code = "未曾进入保留档案库";
            if ("1".equals(tag_code))
                tag_code = "曾经进入保留档案库";
            if ("2".equals(tag_code))
                tag_code = "其它";
        }
        data.put("TAG_CODE", tag_code);

        String brand = "";
        if (StringUtils.isNotBlank(data.getString("BRAND")))
        {
            brand = UBrandInfoQry.getBrandNameByBrandCode(data.getString("BRAND"));
        }
        data.put("BRAND", brand);

        return data;
    }

}

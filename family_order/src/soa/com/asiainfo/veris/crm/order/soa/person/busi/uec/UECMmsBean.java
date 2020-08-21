
package com.asiainfo.veris.crm.order.soa.person.busi.uec;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.UECMmsException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.UecMmsQry;

public class UECMmsBean extends CSBizBean
{

    public IData sendMMS(IData data) throws Exception
    {
        IDataUtil.chkParam(data, "IN_MODE_CODE");
        IDataUtil.chkParam(data, "RECV_OBJECT");
        IDataUtil.chkParam(data, "MMS_TEMPLATE_ID");
        IDataUtil.chkParam(data, "NOTICE_CONTENT_SUBJECT");
        IDataUtil.chkParam(data, "FORCE_OBJECT");

        IDataset templateInfos = new DatasetList();
        try
        {
            templateInfos = UecMmsQry.queryTemplateInfos(data);// dao.queryList(parser);
        }
        catch (Exception e)
        {
            CSAppException.apperr(UECMmsException.CRM_UECMMS_1, data.get("MMS_TEMPLATE_ID"));
            // common.error("100000", "彩信模板：" + data.get("MMS_TEMPLATE_ID") +" 不存在");
        }
        if (templateInfos.size() == 0)
        {
            CSAppException.apperr(UECMmsException.CRM_UECMMS_1, data.get("MMS_TEMPLATE_ID"));
            // common.error("100000", "彩信模板：" + data.get("MMS_TEMPLATE_ID") +" 不存在");
        }

        IDataset templateParams = new DatasetList();
        if (data.get("MMS_TEMPLATE_PARAMS") != null)
        {
            templateParams = (IDataset) data.get("MMS_TEMPLATE_PARAMS");
            for (int i = 0; i < templateParams.size(); i++)
            {
                IData templateParam = templateParams.getData(i);
                String paramName = templateParam.getString("PARAM_NAME", "");
                if ("".equals(paramName))
                {
                    CSAppException.apperr(UECMmsException.CRM_UECMMS_2);
                    // common.error("100001", "输入参数MMS_TEMPLATE_PARAMS配置出错，未找到PAEAM_NAME");
                }
                boolean isExist = false;
                for (int j = 0; j < templateInfos.size(); j++)
                {
                    IData templateInfo = templateInfos.getData(j);
                    String infoParamName = templateInfo.getString("MMS_ANNEX_TEXT", "");
                    if (infoParamName.equals("$" + paramName + "$"))
                    {
                        isExist = true;
                        templateInfos.remove(templateInfo);
                        break;
                    }

                }
                if (!isExist)
                {
                    CSAppException.apperr(UECMmsException.CRM_UECMMS_3, data.get("MMS_TEMPLATE_ID"), paramName);
                    // common.error("100002", "彩信模板：" + data.get("MMS_TEMPLATE_ID") +" 未配置" + paramName + "参数");
                }
            }
        }
        for (int i = 0; i < templateInfos.size(); i++)
        {
            IData templateInfo = templateInfos.getData(i);
            String infoParamName = templateInfo.getString("MMS_ANNEX_TEXT", "");
            if (!"".equals(infoParamName))
            {
                infoParamName = infoParamName.replace("$", "");
                IData templateParam = new DataMap();
                templateParam.put("PARAM_NAME", infoParamName);
                templateParam.put("PARAM_VALUE", " ");
                templateParams.add(templateParam);
            }
        }
        // 获取彩信序列
        // SEQ_MMS_ID
        String mmsNoticeId = UecMmsQry.getMmsNoticeId();
        if ("0".equals(mmsNoticeId))
        {
            CSAppException.apperr(UECMmsException.CRM_UECMMS_4);
            // common.error("100003", "获取彩信流水号失败");
        }
        // 补充参数
        supplyUserInfo(data);

        if (data.get("CHAN_ID") == null || "".equals(data.get("CHAN_ID")))
        {
            data.put("CHAN_ID", "E002");
        }
        if (data.get("RECV_OBJECT_TYPE") == null || "".equals(data.get("RECV_OBJECT_TYPE")))
        {
            data.put("RECV_OBJECT_TYPE", "00");
        }
        if (data.get("MMS_TYPE_CODE") == null || "".equals(data.get("MMS_TYPE_CODE")))
        {
            data.put("MMS_TYPE_CODE", "00");
        }
        if (data.get("MMS_KIND_CODE") == null || "".equals(data.get("MMS_KIND_CODE")))
        {
            data.put("MMS_KIND_CODE", "01");
        }
        if (data.get("MMS_PRIORITY") == null || "".equals(data.get("MMS_PRIORITY")))
        {
            data.put("MMS_PRIORITY", "0");
        }
        if (data.get("NOTICE_CONTENT_TYPE") == null || "".equals(data.get("NOTICE_CONTENT_TYPE")))
        {
            data.put("NOTICE_CONTENT_TYPE", "0");
        }
        if (data.get("FORCE_REFER_COUNT") == null || "".equals(data.get("FORCE_REFER_COUNT")))
        {
            data.put("FORCE_REFER_COUNT", "1");
        }

        data.put("MMS_NOTICE_ID", mmsNoticeId);
        data.put("PARTITION_ID", mmsNoticeId.substring(mmsNoticeId.length() - 3));
        data.put("REFERED_COUNT", "0");
        data.put("DEAL_STAFFID", data.getString("REFER_STAFF_ID", ""));
        data.put("DEAL_DEPARTID", data.getString("REFER_DEPART_ID", ""));
        data.put("DEAL_STATE", "15");
        data.put("REFER_TIME", SysDateMgr.getSysDate());
        data.put("DEAL_TIME", data.getString("REFER_TIME"));
        data.put("MONTH", StrUtil.getAcceptMonthById(mmsNoticeId));
        data.put("DAY", mmsNoticeId.substring(6, 8));

        IData result = new DataMap();
        ;
        try
        {
            // 彩信参数表
            for (int i = 0; i < templateParams.size(); i++)
            {
                IData templateParam = templateParams.getData(i);
                templateParam.put("ORDER_ID", 0);
                templateParam.put("MMS_NOTICE_ID", mmsNoticeId);
                templateParam.put("MMS_TEMPLATE_ID", data.getString("MMS_TEMPLATE_ID"));
                templateParam.put("MONTH", StrUtil.getAcceptMonthById(mmsNoticeId));
                Dao.insert("TI_O_MMS_PARAMS", templateParam, "uec");
            }
            // 彩信表
            Dao.insert("TI_O_MMS", data, "uec");

            result.put("X_RESULTCODE", "0");
            result.put("X_RESULTINFO", "插入彩信下发表成功");
            result.put("MMS_NOTICE_ID", mmsNoticeId);
        }
        catch (Exception e)
        {
            result.put("X_RESULTCODE", "100004");
            result.put("X_RESULTINFO", e.getMessage());
            result.put("X_RSPTYPE", "2");
            result.put("X_RSPCODE", "2998");
        }
        return result;
    }

    private void supplyUserInfo(IData data) throws Exception
    {
        IData userInfo = UcaInfoQry.qryUserInfoBySn(data.getString("RECV_OBJECT"));
        if (userInfo == null || userInfo.isEmpty())
        {
            CSAppException.apperr(UECMmsException.CRM_UECMMS_5, data.getString("RECV_OBJECT"));
        }
        data.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        data.put("BRAND_CODE", userInfo.getString("BRAND_CODE"));
    }

}

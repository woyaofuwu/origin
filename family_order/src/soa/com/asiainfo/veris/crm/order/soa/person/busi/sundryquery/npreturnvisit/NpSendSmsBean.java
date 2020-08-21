
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.npreturnvisit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class NpSendSmsBean extends CSBizBean
{
    public IDataset getNpOutInfos(IData param, Pagination page) throws Exception
    {

        String staffEparchyCode = getVisit().getCityCode();
        String areaCode = param.getString("AREA_CODE", "").trim();

        if ("HNSJ".equals(staffEparchyCode) || "HNYD".equals(staffEparchyCode) || "HNKF".equals(staffEparchyCode))
        {
            if ("0898".equals(areaCode) || "HNSJ".equals(areaCode) || "HNYD".equals(areaCode) || "HNKF".equals(areaCode))
            {
                areaCode = "HAIN";
            }

        }
        else
        {
            if (!areaCode.equals(staffEparchyCode))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "你归属区域[" + staffEparchyCode + "]，你没有权限查询区域[" + areaCode + "]的数据！");
            }
        }

        String strSubsysCode = "CSM";
        String iParamAttr = "171";
        String strParamCode = areaCode;
        String strEparchyCode = "0898";
        IDataset ids = null;

        // AND( param_code=:PARAM_CODE or 'HAIN'=:PARAM_CODE)")以前条件，拆成两条sql
        ids = CommparaInfoQry.getNpSend(strSubsysCode, iParamAttr, strParamCode, strEparchyCode, page);

        return ids;
    }

    public IData upDateNpOutInfo(IData param) throws Exception
    {
        String str = param.getString("DATAS");
        String staffId = getVisit().getStaffId();
        String time = SysDateMgr.getSysDate();
        IData m = new DataMap();
        if (StringUtils.isNotBlank(str))
        {
            IDataset ids = new DatasetList(str);
            int len = ids.size();
            for (int i = 0; i < len; i++)
            {
                IData data = ids.getData(i);
                String tag = data.getString("tag");
                if ("0".equals(tag))
                {
                    data.put("SUBSYS_CODE", "CSM");
                    data.put("PARAM_ATTR", "171");
                    data.put("PARAM_NAME", "携号发送号码维护");
                    data.put("PARAM_CODE", data.get("CITY_CODE"));
                    data.put("EPARCHY_CODE", "0898");
                    data.put("PARA_CODE1", data.get("SERIAL_NUMBER"));
                    data.put("START_DATE", time);
                    data.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                    data.put("UPDATE_STAFF_ID", staffId);
                    data.put("UPDATE_TIME", time);
                    Dao.insert("TD_S_COMMPARA", data);
                }
                if ("1".equals(tag))
                {
                    data.put("SUBSYS_CODE", "CSM");
                    data.put("PARAM_ATTR", "171");
                    data.put("PARAM_NAME", "携号发送号码维护");
                    data.put("PARAM_CODE", data.get("CITY_CODE"));
                    data.put("EPARCHY_CODE", "0898");
                    data.put("PARA_CODE1", data.get("SERIAL_NUMBER"));
                    data.put("START_DATE", data.get("START_DATE"));
                    data.put("END_DATE", time);
                    data.put("UPDATE_STAFF_ID", staffId);
                    data.put("UPDATE_TIME", time);
                    // Dao.update("TD_S_COMMPARA", data);
                    Dao.executeUpdateByCodeCode("TD_S_COMMPARA", "UPD_NP_SENDSMS", data);
                }
            }
        }
        m.put("MSG", "提交成功！");
        return m;
    }
}

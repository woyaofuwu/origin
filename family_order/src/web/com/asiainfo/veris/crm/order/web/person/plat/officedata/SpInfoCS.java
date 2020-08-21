
package com.asiainfo.veris.crm.order.web.person.plat.officedata;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class SpInfoCS extends CSBasePage
{

    private static String PROV_CODE = "898";
    
    /**
     * @category 本省企业服务代码信息导入
     * @author yanwu
     * @param cycle
     * @throws Exception
     */
    public void importData(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("PROV_CODE", PROV_CODE);// 代码维护省
        IData set = null;
        IDataset sets = CSViewCall.call(this, "CS.BatDealSVC.importSpInfoCSData", data);
        if (sets.size() > 0)
        {
            set = sets.getData(0);
            setAjax(set);
        }
    }

    public void addSpInfoCS(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("PROV_CODE", PROV_CODE);// 代码维护省
        IDataset dataset = CSViewCall.call(this, "SS.SpInfoCSSVC.querySpInfoCSByPkRecordStatus", data);
        String reportTime=data.getString("REPORT_TIME","").equals("")?"":data.getString("REPORT_TIME","").substring(0,10);
        data.put("REPORT_TIME",reportTime);

        if ((dataset != null) && (dataset.size() > 0))
        {
            CSViewException.apperr(PlatException.CRM_PLAT_23);// 此企业信息已存在!
        }

        IDataset res = CSViewCall.call(this, "SS.SpInfoCSSVC.insertSpInfoCS", data);
        setAjax(addStaticValue(res.getData(0)));
    }

    public IData addStaticValue(IData data) throws Exception
    {
        data.put("SP_TYPE_DIS", StaticUtil.getStaticValue("SPINFO_CS_SP_TYPE", data.getString("SP_TYPE")));
        data.put("SP_ATTR_DIS", StaticUtil.getStaticValue("SPINFO_CS_SP_ATTR", data.getString("SP_ATTR")));
        data.put("SP_STATUS_DIS", StaticUtil.getStaticValue("SPINFO_CS_SP_STATUS", data.getString("SP_STATUS")));
        data.put("PROVINCE_NO_DIS", StaticUtil.getStaticValue("SYMTHESIS_PROVINCE_CODE", data.getString("PROVINCE_NO")));
        data.put("RECORD_STATUS_DIS", StaticUtil.getStaticValue("SPINFO_CS_RECORD_STATUS", data.getString("RECORD_STATUS")));
        return data;
    }

    public void deleteSpInfoCS(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("PROV_CODE", PROV_CODE);

        IDataset dataset = CSViewCall.call(this, "SS.SpInfoCSSVC.querySpInfoCSByRowID", data);

        if ((dataset == null) || (dataset.size() == 0))
        {
            CSViewException.apperr(PlatException.CRM_PLAT_24);// 此企业信息不存在!
        }
        else
        {
            String bizCode = dataset.getData(0).getString("BIZ_CODE", "");
            String spCode = dataset.getData(0).getString("SP_CODE", "");
            String record_status = dataset.getData(0).getString("RECORD_STATUS", "");
            String record_date = dataset.getData(0).getString("RECORD_DATE", "");
            data.put("RECORD_DATE", record_date);
            String reportTime=data.getString("REPORT_TIME","").equals("")?"":data.getString("REPORT_TIME","").substring(0,10);
            data.put("REPORT_TIME",reportTime);
            if (!bizCode.equals(data.getString("BIZ_CODE")))
            {
                CSViewException.apperr(ParamException.CRM_PARAM_313);// 选择记录进行删除操作时不能修改服务代码，请重新选择记录操作！
            }

            if (!spCode.equals(data.getString("SP_CODE")))
            {
                CSViewException.apperr(ParamException.CRM_PARAM_314);// 选择记录进行删除操作时不能修改企业代码，请重新选择记录操作！
            }

            if (record_status.equals("03"))
            {
                CSViewException.apperr(PlatException.CRM_PLAT_26);// 已删除的企业信息不允许重复删除，请重新录入!
            }

        }
        CSViewCall.call(this, "SS.SpInfoCSSVC.deleteSpInfoCS", data);
    }

    public void queryList(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        data.put("PROV_CODE", PROV_CODE);
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.SpInfoCSSVC.querySpInfoCS", data, getPagination("queryNav"));
        setCount(dataCount.getDataCount());
        setInfos(dataCount.getData());
    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

    public abstract void setSubmitInfo(IData submitInfo);

    public void updateSpInfoCS(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("PROV_CODE", PROV_CODE);
        IDataset dataset = CSViewCall.call(this, "SS.SpInfoCSSVC.querySpInfoCSByRowID", data);

        if ((dataset == null) || (dataset.size() == 0))
        {
            CSViewException.apperr(PlatException.CRM_PLAT_24);// 此企业信息不存在!
        }
        else
        {
            String bizCode = dataset.getData(0).getString("BIZ_CODE", "");
            String spCode = dataset.getData(0).getString("SP_CODE", "");
            String record_status = dataset.getData(0).getString("RECORD_STATUS", "");

            if (!bizCode.equals(data.getString("BIZ_CODE")))
            {
                CSViewException.apperr(ParamException.CRM_PARAM_311);// 选择记录进行更新操作时不能修改服务代码，请重新选择记录操作！
            }
            if (!spCode.equals(data.getString("SP_CODE")))
            {
                CSViewException.apperr(ParamException.CRM_PARAM_312);// 选择记录进行更新操作时不能修改企业代码，请重新选择记录操作
            }
            if (record_status.equals("03"))
            {
                CSViewException.apperr(PlatException.CRM_PLAT_25);// 已删除的企业信息不允许修改，请重新录入
            }
        }
        String reportTime=data.getString("REPORT_TIME","").equals("")?"":data.getString("REPORT_TIME","").substring(0,10);
        data.put("REPORT_TIME",reportTime);
        IDataset res = CSViewCall.call(this, "SS.SpInfoCSSVC.updateSpInfoCS", data);
        setAjax(addStaticValue(res.getData(0)));
    }
}

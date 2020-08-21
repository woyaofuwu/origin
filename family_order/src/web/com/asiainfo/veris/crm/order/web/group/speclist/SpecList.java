
package com.asiainfo.veris.crm.order.web.group.speclist;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class SpecList extends CSBasePage
{

    public void addSpeclist(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	IData param = new DataMap();
        param.put("USER_MOBILE", data.getString("USER_MOBILE"));
        param.put("SPECIAL_TYPE", data.getString("SPECIAL_TYPE"));
        param.put("REMOVE_TAG", "0");
    	IDataset userinfo = CSViewCall.call(this, "SS.SpecListSVC.queryUserInfo", data);
    	if ((userinfo != null) && (userinfo.size() > 0)){
            IDataset dataset = CSViewCall.call(this, "SS.SpecListSVC.querySpeclist", param);
            if ((dataset != null) && (dataset.size() > 0))
            {
                CSViewException.apperr(PlatException.CRM_PLAT_3033);
            }
            CSViewCall.call(this, "SS.SpecListSVC.addSpeclist", data);
            setAjax(addStaticValue(data));
    	}else{
    		CSViewException.apperr(PlatException.CRM_PLAT_1001_5,data.getString("USER_MOBILE"));
    	}
    }

    public IData addStaticValue(IData data) throws Exception
    {
        data.put("TYPE_NAME_DIS", StaticUtil.getStaticValue("SPECIAL_TYPE", data.getString("TYPE_NAME")));
        data.put("ISBOLD_DIS", StaticUtil.getStaticValue("ISBOLD", data.getString("ISBOLD")));
        data.put("REMOVE_TAG_DIS", StaticUtil.getStaticValue("AREA_VALIDFLAG", data.getString("REMOVE_TAG")));
        return data;
    }

    public void deleteSpeclist(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if(data.getString("USER_MOBILE").equals(data.getString("USER_MOBILE_OLE"))){
        	CSViewException.apperr(PlatException.CRM_PLAT_3037);
        }
        IData param = new DataMap();
        param.put("USER_MOBILE", data.getString("USER_MOBILE"));
        param.put("SPECIAL_TYPE", data.getString("SPECIAL_TYPE_OLD"));
        param.put("REMOVE_TAG", "0");
        IDataset dataset = CSViewCall.call(this, "SS.SpecListSVC.querySpeclist", param);

        if ((dataset == null) || (dataset.size() == 0))
        {
            CSViewException.apperr(PlatException.CRM_PLAT_3034);
        }
        else
        {
            String removeTag = dataset.getData(0).getString("REMOVE_TAG", "");
            if (removeTag.equals("1"))
            {
                CSViewException.apperr(PlatException.CRM_PLAT_3036);// 已删除的企业信息不允许重复删除，请重新录入!
            }

        }
        data.put("INSERT_TIME", TimeUtil.format("yyyy-MM-dd HH:mm:ss", dataset.getData(0).getString("INSERT_TIME")));
        CSViewCall.call(this, "SS.SpecListSVC.deleteSpeclist", data);
    }

    public void queryList(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.SpecListSVC.querySpeclist", data, getPagination("queryNav"));
        setCount(dataCount.getDataCount());
        setInfos(dataCount.getData());
    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

    public abstract void setSubmitInfo(IData submitInfo);

    public void updateSpeclist(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if(data.getString("USER_MOBILE").equals(data.getString("USER_MOBILE_OLE"))){
        	CSViewException.apperr(PlatException.CRM_PLAT_3037);
        }
        IData param = new DataMap();
        param.put("USER_MOBILE", data.getString("USER_MOBILE"));
        param.put("SPECIAL_TYPE", data.getString("SPECIAL_TYPE_OLD"));
        param.put("REMOVE_TAG", "0");
        IDataset dataset = CSViewCall.call(this, "SS.SpecListSVC.querySpeclist", param);

        if ((dataset == null) || (dataset.size() == 0))
        {
            CSViewException.apperr(PlatException.CRM_PLAT_3034);
        }
        else
        {
            String removeTag = dataset.getData(0).getString("REMOVE_TAG", "");
            if (removeTag.equals("1"))
            {
                CSViewException.apperr(PlatException.CRM_PLAT_3035);
            }
        }
        data.put("INSERT_TIME", TimeUtil.format("yyyy-MM-dd HH:mm:ss", dataset.getData(0).getString("INSERT_TIME")));
        CSViewCall.call(this, "SS.SpecListSVC.updateSpeclist", data);
        setAjax(addStaticValue(data));
    }
}

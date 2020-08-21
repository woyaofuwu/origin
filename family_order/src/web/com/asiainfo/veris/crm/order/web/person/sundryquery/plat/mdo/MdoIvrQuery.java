
package com.asiainfo.veris.crm.order.web.person.sundryquery.plat.mdo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class MdoIvrQuery extends PersonBasePage
{

    /**
     * IVR拨打记录查询
     * 
     * @param cycle
     * @throws Exception
     * @author xiekl
     */
    public void queryMdoIvrInfo(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData("cond", true);
        IData params = new DataMap();
        params.put("X_GETMODE", 0);
        params.put("SERIAL_NUMBER", condParams.getString("SERIAL_NUMBER", ""));
        params.put("TRADE_TYPE_CODE", "3700");
        // 查询用户信息
        IDataset ucaInfos = CSViewCall.call(this, "CS.GetInfosSVC.getUCAInfos", params);
        params.clear();
        if (null != ucaInfos && ucaInfos.size() > 0)
        {
            IData ucaInfo = ucaInfos.getData(0);
            // 查询客户信息
            params.clear();
            // params.put("X_GETMODE", 8);//根据服务号码获取客户信息
            params.put("SERIAL_NUMBER", condParams.getString("SERIAL_NUMBER", ""));
            params.put("CUST_ID", ucaInfo.getData("USER_INFO").getString("CUST_ID"));
            setCustInfo(ucaInfo.getData("CUST_INFO"));
            condParams.put("USER_ID", ucaInfo.getData("USER_INFO").getString("USER_ID"));
            condParams.put("CUST_NAME", ucaInfo.getData("CUST_INFO").getString("CUST_NAME"));
            IDataOutput output = CSViewCall.callPage(this, "SS.QueryMdoIvrInfoSVC.queryUserMdoIvrInfo", condParams, getPagination("mdoIvrInfoNav"));

            if (IDataUtil.isEmpty(output.getData()))
            {
                CSViewException.apperr(PlatException.CRM_PLAT_74, "没有查询到改用户的IVR拨打记录");
            }

            setInfos(output.getData());
            setMdoIvrCount(output.getDataCount());
        }
        setCondition(getData("cond", true));
    }

    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setMdoIvrCount(long mdoIvrCount);
}

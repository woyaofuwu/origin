
package com.asiainfo.veris.crm.order.web.person.sundryquery.custcontact;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryIntegrateCustContact extends PersonBasePage
{

    /**
     * 修改客户接触信息,并返回修改后的结果
     * 
     * @param cycle
     * @throws Exception
     * @author chenhao 2009-3-11
     */
    public void modifyIntegrateCustContact(IRequestCycle cycle) throws Exception
    {

        IData pageData = getData();
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", pageData.getString("SERIAL_NUMBER", ""));
        inparam.put("CUST_CONTACT_ID", pageData.getString("CUST_CONTACT_ID2", ""));
        inparam.put("MODIFY_DESC", pageData.getString("MODIFY_DESC", ""));
        inparam.put("REMARK", pageData.getString("REMARK", ""));
        inparam.put("ACCEPT_MONTH", pageData.getString("ACCEPT_MONTH", ""));
        inparam.put("MODIFY_TAG", "2");
        IDataset dataset = CSViewCall.call(this, "SS.QueryIntegrateCustContactSVC.modifyIntegrateCustContact", inparam);
        // String result = dataset.getData(0).getString("TIP");

        setAjax(dataset);

        // 重新查询一次
        // IData param2 = new DataMap(pageData.getString("LAST_COND", ""));
        // qryInit(cycle);
    }

    public void qryInit(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        String lastMonth = SysDateMgr.getTodayLastMonth();
        String sysDate = SysDateMgr.getSysDate();

        data.put("START_DATE", lastMonth);
        data.put("END_DATE", sysDate);
        // 初始化值
        IDataset custContactInit = CSViewCall.call(this, "SS.QueryIntegrateCustContactSVC.custContactInit", data, getPagination("page"));
        data.put("isReadOnly", this.hasPriv("CSM_MODIFYCUSTCONTACT"));
        data.put("pvstr", custContactInit.getData(0).toString());
        setCond(data);
    }

    /**
     * 客户接触综合查询
     * 
     * @author xj
     * @create_time：Feb 17, 2009 3:59:23 PM
     */
    public void queryIntegrateCustContact(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        String pvsstr = data.getString("pvstr", "");
        data.remove("pvstr");
        data.putAll(new DataMap(pvsstr));

        Pagination pagination = this.getPagination("page");
        IDataOutput out = CSViewCall.callPage(this, "SS.QueryIntegrateCustContactSVC.queryCustContact", data, pagination);
        setInfos(out.getData());

        setCustContactCount(out.getDataCount());

    }

    public abstract void setCond(IData cond);

    public abstract void setCustContactCount(long custContactCount);

    public abstract void setCustinfo(IData custinfo);

    public abstract void setInfos(IDataset infos);

    public abstract void setLastcond(IData lastcond);
}

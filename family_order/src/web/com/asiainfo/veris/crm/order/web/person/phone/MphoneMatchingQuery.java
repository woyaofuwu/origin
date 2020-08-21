
package com.asiainfo.veris.crm.order.web.person.phone;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class MphoneMatchingQuery extends PersonBasePage
{
    /**
     * 导入excel文件内容
     * 
     * @throws Exception
     */
    public void importClick(IRequestCycle cycle) throws Exception
    {
        // TODO Auto-generated method stub

        IData data = getData();
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        String fileId = data.getString("FILE_PATH");
        IData array = ImpExpUtil.beginImport(null, fileId, ExcelConfig.getSheets("import/bat/MphoneMatchingImport.xml"));
        IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
        IDataset[] err = (IDataset[]) array.get("error");// 解析失败的数据
        IDataset importData = suc[0];

        setEditList(importData);
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData result = CSViewCall.callone(this, "SS.MphoneMatchingSVC.mphoneSubmit", data);
        setAjax(result);

    }

    public abstract void setCondition(IData idata);

    public abstract void setEditList(IDataset list);

    public abstract void setInfos(IDataset serviceList);

    public abstract void setSelectList(IDataset list);

}

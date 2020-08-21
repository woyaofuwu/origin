
package com.asiainfo.veris.crm.order.web.person.familytradeoptimal;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class DestroyFamily extends PersonBasePage
{

    public void loadInfo(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IData fmyParam = new DataMap();

        IData loadInfo = CSViewCall.call(this, "SS.FamilyCreateSVC.loadDestroyFamilyInfo", pageData).getData(0);
        fmyParam.putAll(loadInfo);
        IDataset productList = loadInfo.getDataset("PRODUCT_LIST");
        setProductList(productList);

        if (productList.size() == 1)
        {
            String productId = productList.getData(0).getString("PRODUCT_ID");
            fmyParam.put("PRODUCT_ID", productId);

            IDataset discntList = loadInfo.getDataset("DISCNT_LIST");
            setDiscntList(discntList);

            if (discntList.size() == 1)
            {
                String discntCode = discntList.getData(0).getString("DISCNT_CODE");
                fmyParam.put("DISCNT_CODE", discntCode);
            }

            setAppDiscntList(loadInfo.getDataset("APP_DISCNT_LIST"));
        }

        if (StringUtils.isNotBlank(loadInfo.getString("PRODUCT_ID")))
        {
            fmyParam.put("PRODUCT_ID", loadInfo.getString("PRODUCT_ID"));
        }

        if (StringUtils.isNotBlank(loadInfo.getString("DISCNT_CODE")))
        {
            fmyParam.put("DISCNT_CODE", loadInfo.getString("DISCNT_CODE"));
        }

        setFmyParam(fmyParam);
        setViceInfos(loadInfo.getDataset("MEB_LIST"));
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        pageData.putAll(getData("FMY"));
        pageData.put("IN_TAG", "0");// 0表示前台办理
        IDataset rtDataset = CSViewCall.call(this, "SS.DestroyFamilyRegSVC.tradeReg", pageData);
        this.setAjax(rtDataset);
    }

    public abstract void setAppDiscntList(IDataset appDiscntList);

    public abstract void setDiscntList(IDataset discntList);

    public abstract void setFmyParam(IData fmyParam);

    public abstract void setProductList(IDataset productList);

    public abstract void setViceDiscntList(IDataset viceDiscntList);

    public abstract void setViceInfo(IData viceInfo);

    public abstract void setViceInfos(IDataset viceInfos);
}

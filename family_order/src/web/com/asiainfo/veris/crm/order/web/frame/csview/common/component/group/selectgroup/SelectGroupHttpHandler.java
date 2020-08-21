
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectgroup;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public class SelectGroupHttpHandler extends CSBizHttpHandler
{

    public void getGroupBaseInfo() throws Exception
    {

        IData conParams = getData("cond", true);
        String groupId = conParams.getString("GROUP_ID");
        String custId = conParams.getString("CUST_ID");
        IData result = null;

        String ttGrp = getData().getString("IS_TTGRP", "false");
        if (ttGrp.equals("true"))
        {

            if (StringUtils.isNotEmpty(custId))
                result = UCAInfoIntfViewUtil.qryTTGrpCustInfoByGrpCustId(this, custId, true);
            else
                result = UCAInfoIntfViewUtil.qryTTGrpCustInfoByGrpId(this, groupId, true);

        }
        else
        {

            if (StringUtils.isNotEmpty(custId))
                result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);
            else
                result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        }

        this.setAjax(result);

    }

    /**
     * 集团搜索组件中搜索集团客户信息
     * 
     * @throws Exception
     */
    public void searchGroupInfos() throws Exception
    {
        IData param = this.getData();
        String searchText = param.getString("CUST_NAME");
        if (StringUtils.isNotBlank(searchText) && searchText.length() >= 2)
        {
            SearchResponse resp = SearchClient.search("TF_F_CUST_GROUP", searchText, 0, 10);
            if (resp == null)
                return;
            IDataset datas = resp.getDatas();
            if (IDataUtil.isNotEmpty(datas))
            {
                for (int i = 0, size = datas.size(); i < size; i++)
                {
                    IData data = datas.getData(i);
                    data.put("CUST_NAME", data.getString("GROUP_ID", "") + "|" + data.getString("CUST_NAME", ""));
                }
            }
            this.setAjax(datas);
        }
    }
}

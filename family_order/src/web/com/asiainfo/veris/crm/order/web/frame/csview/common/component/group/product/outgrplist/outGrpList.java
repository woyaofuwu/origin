
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.product.outgrplist;

import java.util.Vector;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class outGrpList extends CSBizTempComponent
{
    private String resTypesStr;

    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
        {
            resTypesStr = null;
        }
    }

    public abstract IDataset getRelawwList();

    @Override
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle)
    {
        if (cycle.isRewinding())
            return;

        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/product/outgrp/outGrp.js");

        try
        {
            IDataset relaww = getRelawwList();
            IData param = new DataMap();

            IDataset oGrpList = CSViewCall.call(this, "CS.OtherInfoQrySVC.getOtherInfoByUserId", param);

            String ogrp_num = String.valueOf(oGrpList.size());
            setOrgpNum(ogrp_num);
            resTypesStr = oGrpList.toString();
            Vector relawwVc = new Vector();
            setOutGrpList(oGrpList);
            IData wwuu = new DataMap();
            if (relaww != null)
            {
                for (int i = 0; i < relaww.size(); i++)
                {
                    IData item = relaww.getData(i);
                    String uid_b = item.getString("USER_ID_B");
                    String out_no = item.getString("ROLE_CODE_B");
                    wwuu.put("USER_ID_B", uid_b);
                    wwuu.put("ROLE_CODE_B", out_no);

                    relawwVc.add(wwuu);
                }
                setRelawwStr(relaww.toString());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        StringBuilder init_script = new StringBuilder();
        init_script.append("$(document).ready(function(){\r\n");
        init_script.append("\t initRelaww(); \r\n");
        init_script.append("});\r\n");

        getPage().addScriptBeforeBodyEnd("outGrp", init_script.toString());
        // super.renderComponent(writer, cycle);
    }

    public abstract void setOgrpData(IData id);

    public abstract void setOrgpNum(String ogrpNum);

    public abstract void setOutGrpList(IDataset ids);

    public abstract void setRelawwStr(String relastr);

}

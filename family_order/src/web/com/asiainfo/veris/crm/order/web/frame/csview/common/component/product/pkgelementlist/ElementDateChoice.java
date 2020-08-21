
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.product.pkgelementlist;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class ElementDateChoice extends CSBizTempComponent
{
    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);

        this.setInfo(null);
        this.setItemIndex(null);
    }

    @Override
    public void renderComponent(StringBuilder builder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        addResourceFile(writer, "scripts/csserv/component/product/pkgelementlist/elementdatechoice.js");
        IData param = this.getPage().getData();
        if (StringUtils.isBlank(param.getString("ITEM_INDEX")))
        {
            return;
        }

        String editMode = param.getString("EDIT_MODE");
        IData info = new DataMap();
        if (StringUtils.isNotBlank(editMode))
        {
            info.put("EDIT_DISABLED", "true");
            info.put("EDIT_MODE", editMode);
        }
        if ("1".equals(editMode))
        {
            // 先写死，实在是懒得想得全面了
            IDataset enableList = new DatasetList();

            IData now = new DataMap();
            now.put("TEXT", "立即生效");
            now.put("VALUE", param.getString("NOW_DAY"));
            enableList.add(now);

            IData next = new DataMap();
            next.put("TEXT", "下账期生效");
            next.put("VALUE", param.getString("NEXT_ACCT_DAY"));
            enableList.add(next);

            info.put("ENABLELIST", enableList);
        }
        else
        {
            String endDate = param.getString("END_DATE", SysDateMgr.getSysTime());
            info.put("END_DATE", endDate);
        }

        // 防止报错
        if (!info.containsKey("ENABLELIST"))
        {
            info.put("ENABLELIST", new DatasetList());
        }

        setInfo(info);

        setItemIndex(param.getString("ITEM_INDEX"));

        addScriptContent(writer, "elementDateChoice.changeEditMode();");
    }

    public abstract void setInfo(IData info);

    public abstract void setItemIndex(String itemIndex);
}

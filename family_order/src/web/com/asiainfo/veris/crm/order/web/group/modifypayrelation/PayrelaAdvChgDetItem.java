
package com.asiainfo.veris.crm.order.web.group.modifypayrelation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 高级付费关系变更-帐目明细
 * 
 * @author linyl3
 */
public abstract class PayrelaAdvChgDetItem extends PersonBasePage
{

    public void initChildPage(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        data.put("PARENT_NOTE_ITEM_CODE", getData().getString("PARENT_NOTE_ITEM_CODE", ""));
        data.put("PARENT_NOTE_ITEM", getData().getString("PARENT_NOTE_ITEM", ""));
        setDetInfo(data);
    }

    /**
     * 高级付费关系变更-获取帐目明细
     * 
     * @param cycle
     * @throws Exception
     * @author linyl3
     */
    public void queryPayrelaAdvChgDetItem(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        data.put("PARENT_NOTE_ITEM_CODE", getData().getString("PARENT_NOTE_ITEM_CODE", ""));
        data.put("PARENT_NOTE_ITEM", getData().getString("PARENT_NOTE_ITEM", ""));
        setDetInfo(data);

        data.put("PARENT_ITEM_CODE", getData().getString("PARENT_NOTE_ITEM_CODE"));
        IDataset infos = CSViewCall.call(this, "CS.NoteItemInfoQrySVC.queryNoteItems2", data);

        if (IDataUtil.isEmpty(infos))
        {
            setHintInfo("没有符合查询条件的【帐目明细】数据~");
        }

        setInfos(infos);

    }

    public abstract void setDetInfo(IData detInfo);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfos(IDataset infos);
}

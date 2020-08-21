
package com.asiainfo.veris.crm.order.web.group.modifypayrelation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

/**
 * 高级付费关系变更-帐目明细
 * 
 * @author xj
 */
public abstract class PayrelaDetItem extends GroupBasePage
{

    /**
     * 取账目标编码
     * 
     * @description
     * @author xiaozp
     * @date Sep 30, 2009
     * @version 1.0.0
     * @throws Exception
     */
    private String[] getItemCode(String itemCode) throws Exception
    {

        if (itemCode == null || "".equals(itemCode) || itemCode.length() <= 1)
            return null;
        if (itemCode.length() > 1)
            itemCode = itemCode.substring(1);
        // CSAppEntity dao = new
        // CSAppEntity(ConnMgr.CONN_CRMCEN);
        // IData data=new DataMap();
        // SQLParser parser = new SQLParser(data);
        // parser.addSQL("select sub_item_id from td_b_compitem note where 1 =
        // 1");
        // parser.addSQL(" and note.item_id='" + itemCode + "'");
        // parser.addSQL(" order by note.SUB_ITEM_ID ASC");
        // IDataset subItem = callDaoqryByParse( parser,XXXXparser);
        //
        // if(subItem==null&&subItem.size()<=0)
        // return null;
        IData iparam = new DataMap();
        iparam.put("ITEM_CODE", itemCode);
        IDataset dataset = CSViewCall.call(this, "SS.ModifyMemDataSVC.getSubItemCode", iparam);
        String subItems = "";
        if (dataset != null && dataset.size() > 0)
        {
            subItems = dataset.getData(0).getString("RESULT");
        }

        // for (int i = 0; i < subItem.size(); i++ ) {
        // if ("".equals(subItems))
        // subItems = subItems
        // + subItem.getData(i).getString("SUB_ITEM_ID");
        // else
        // subItems = subItems + ","
        // + subItem.getData(i).getString("SUB_ITEM_ID");
        // }
        if ("".equals(subItems))
            return null;
        else
            return subItems.split(",");
    }

    /**
     * 高级付费关系变更-获取帐目明细
     * 
     * @param cycle
     * @throws Exception
     * @author xj
     */
    public void queryPayrelaAdvChgDetItem(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();

        data.put("PARENT_NOTE_ITEM_CODE", getData().getString("PARENT_NOTE_ITEM_CODE"));
        data.put("PARENT_NOTE_ITEM", getData().getString("PARENT_NOTE_ITEM"));
        setDetInfo(data);

        String items = getData().getString("NOTE_PAYITEM_CODE"); // getParameter("NOTE_PAYITEM_CODE");

        String[] itemStrs = null;
        if (items != null && items.length() > 0)
            itemStrs = items.split(",");

        // if() PayRelaBean bean = new PayRelaBean(); bean.queryPayrelaAdvChgDetItem
        IDataset dateset = CSViewCall.call(this, "CS.NoteItemInfoQrySVC.queryDetItemByPageForGrp", data); // PayRelaBean.queryPayrelaAdvChgDetItem
        setCheckTag(dateset, getItemCode(items));

        data.clear();
        data.put("PARENT_NOTE_ITEM_CODE", getData().getString("PARENT_NOTE_ITEM_CODE"));
        if (dateset != null)
            data.put("SIZE", dateset.size());
        setDetInfo(data);
        setInfos(dateset);
    }

    /**
     * 设置选择中标记
     * 
     * @description
     * @author xiaozp
     * @date Sep 29, 2009
     * @version 1.0.0
     * @param dateset
     */

    private void setCheckTag(IDataset dateset, String[] itemStrs) throws Exception
    {

        if (itemStrs != null && itemStrs.length > 0)
        {
            for (int i = 0; i < dateset.size(); i++)
            {
                dateset.getData(i).put("TAG", "0");
                for (int j = 0; j < itemStrs.length; j++)
                {
                    if (dateset.getData(i).getString("NOTE_ITEM_CODE").equals(itemStrs[j]))
                    {
                        dateset.getData(i).put("TAG", "1");
                        continue;
                    }
                }
            }
        }
    }

    public abstract void setDetInfo(IData detInfo);

    public abstract void setInfos(IDataset infos);

}

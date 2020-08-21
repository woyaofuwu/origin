/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata;

import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.ITreeNodeInfo;

public class SoProductAttrBean extends DataMap implements ITreeNodeInfo
{

    private static final long serialVersionUID = 1L;

    // protected int m_rowIndex = -1;
    // protected int m_level = -1;
    // protected int m_childCount =-1;
    // protected String m_childRowIndexs ="";

    public int getChildCount()
    {
        return getInt("m_childCount");
    }

    public String getChildRowIndexs()
    {
        return getString("m_childRowIndexs");
    }

    public String getId()
    {
        return this.getString("ITEM_ID");
    }

    public int getLevel()
    {
        return getInt("m_level");
    }

    public String getParentId()
    {
        return this.getString("PARENT_ITEM_ID");
    }

    public int getRowIndex()
    {
        return getInt("m_rowIndex");
    }

    public void setChildCount(int paramInt)
    {
        this.put("m_childCount", paramInt);
    }

    public void setChildRowIndexs(String paramString)
    {
        this.put("m_childRowIndexs", paramString);
    }

    public void setIsFold(boolean isFold)
    {
        this.put("m_isFold", isFold);
    }

    public void setLevel(int paramInt)
    {
        this.put("m_level", paramInt);
    }

    public void setRowIndex(int mRowIndex)
    {
        this.put("m_rowIndex", mRowIndex);
    }
}

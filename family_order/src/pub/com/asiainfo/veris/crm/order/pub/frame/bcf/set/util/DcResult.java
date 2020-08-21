/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.util;

import com.ailk.biz.view.BizPage;
import com.ailk.biz.view.PageUtil;
import com.ailk.common.data.IDataset;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DcResult.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:34:48 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class DcResult implements AIResult
{

    public PageUtil pageutil;

    private IDataset dataList;

    private int m_index = -1;

    private int m_lastnode_of_root = -1;

    public DcResult(IDataset dataList, BizPage bizPage)
    {
        this.pageutil = new PageUtil(bizPage);
        this.dataList = dataList;
    }

    public void close() throws Exception
    {
    }

    public int getChildCount()
    {
        if (this.dataList.getData(m_index).containsKey("m_childCount"))
        {
            return this.dataList.getData(m_index).getInt("m_childCount");
        }
        throw new RuntimeException("非树表不能调用该函数!");
    }

    public String getChildRowIndexs()
    {
        if (this.dataList.getData(m_index).containsKey("m_childRowIndexs"))
        {
            return this.dataList.getData(m_index).getString("m_childRowIndexs");
        }
        throw new RuntimeException("非树表不能调用该函数!");
    }

    public String getId()
    {
        if (this.dataList.getData(m_index).containsKey("ITEM_ID"))
        {
            return this.dataList.getData(m_index).getString("ITEM_ID");
        }
        throw new RuntimeException("非树表不能调用该函数!");
    }

    public int getLevel()
    {
        if (this.dataList.getData(m_index).containsKey("m_level"))
        {
            return this.dataList.getData(m_index).getInt("m_level");
        }
        throw new RuntimeException("非树表不能调用该函数!");
    }

    public int getLineNum()
    {
        int lineNum = 0;
        for (int i = this.m_index; i < this.dataList.size(); ++i)
        {
            if (this.dataList.getData(m_index).getInt("m_level") - this.dataList.getData(i).getInt("m_level") > lineNum)
            {
                lineNum = this.dataList.getData(m_index).getInt("m_level") - this.dataList.getData(i).getInt("m_level");
            }
        }
        return lineNum;
    }

    public String getParentId()
    {
        if (this.dataList.getData(m_index).containsKey("PARENT_ITEM_ID"))
        {
            return this.dataList.getData(m_index).getString("PARENT_ITEM_ID");
        }
        throw new RuntimeException("非树表不能调用该函数!");
    }

    public String getValue(String name) throws Exception
    {
        if (this.dataList.getData(this.m_index).containsKey(name))
        {
            return this.dataList.getData(this.m_index).getString(name);
        }
        return "";
    }

    public boolean ifLastChild()
    {
        boolean flag = false;
        if (this.m_index + 1 < this.dataList.size())
        {
            if ((this.dataList.getData(m_index).getInt("m_childCount") == 0) && (this.dataList.getData(m_index).getInt("m_level") != this.dataList.getData(m_index + 1).getInt("m_level")))
            {
                return true;
            }
            flag = true;
            for (int i = this.m_index; i + 1 < this.dataList.size(); ++i)
            {
                if (this.dataList.getData(m_index).getInt("m_level") == this.dataList.getData(i + 1).getInt("m_level"))
                {
                    flag = false;
                    break;
                }
            }
        }
        else
        {
            flag = true;
        }
        return flag;
    }

    public boolean ifOffspringOfRoot()
    {
        boolean flag = false;
        if (this.m_lastnode_of_root == -1)
        {
            for (int i = 0; i < this.dataList.size(); ++i)
            {
                if (this.dataList.getData(i).getInt("m_level") == 1)
                {
                    for (int j = i + 1; j < this.dataList.size(); ++j)
                    {
                        if (this.dataList.getData(i).getInt("m_level") == this.dataList.getData(j).getInt("m_level"))
                        {
                            break;
                        }
                        if (j == this.dataList.size() - 1)
                            this.m_lastnode_of_root = i;
                    }
                }
                if (this.m_lastnode_of_root != -1)
                {
                    break;
                }
            }
        }
        if (this.m_lastnode_of_root == -1)
        {
            flag = false;
        }
        else if (this.m_index >= this.m_lastnode_of_root)
        {
            flag = true;
        }
        return flag;
    }

    public boolean isFold()
    {
        return this.getChildCount() > 0 ? true : false;
        // if (this.dataList.getData(m_index).containsKey("m_isFold"))
        // {
        // return this.dataList.getData(m_index).getBoolean("m_isFold");
        // }
        // return false;
    }

    public boolean lastNode()
    {
        return (this.m_index + 1 == this.dataList.size());
    }

    public boolean next() throws Exception
    {
        if (this.dataList == null)
            return false;
        if (this.m_index < this.dataList.size() - 1)
        {
            this.m_index += 1;
            return true;
        }
        return false;
    }

}

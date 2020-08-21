/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: SortTreeNode.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:35:11 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class SortTreeNodeNew<T extends ITreeNodeInfo>
{
    protected List<SortTreeNodeNew<T>> m_list = new ArrayList<SortTreeNodeNew<T>>();

    protected T m_node;

    protected SortTreeNodeNew<T> m_parent;

    protected int m_range = -1;

    protected int m_level = -1;

    protected int m_rowIndex = -1;

    public SortTreeNodeNew()
    {
    }

    public SortTreeNodeNew(T node)
    {
        this.m_node = node;
    }

    public void addChild(SortTreeNodeNew<T> node)
    {
        if (this.m_list == null)
        {
            this.m_list = new ArrayList<SortTreeNodeNew<T>>();
        }
        this.m_list.add(node);
    }

    public SortTreeNodeNew<T> buildTree(List<T> aList)
    {
        // List<ITreeNodeInfo> list = new ArrayList<ITreeNodeInfo>();
        Map<String, SortTreeNodeNew<T>> tmpHashMap = new HashMap<String, SortTreeNodeNew<T>>();
        List<SortTreeNodeNew<T>> tmpList = new ArrayList<SortTreeNodeNew<T>>();
        for (int i = 0; i < aList.size(); i++)
        {
            T nodeInfo = aList.get(i);
            SortTreeNodeNew<T> sortTreeNode = new SortTreeNodeNew<T>(nodeInfo);
            tmpList.add(sortTreeNode);
            tmpHashMap.put(nodeInfo.getId(), sortTreeNode);
        }
        SortTreeNodeNew<T> root = new SortTreeNodeNew<T>();
        Iterator<SortTreeNodeNew<T>> it = tmpList.iterator();
        while (it.hasNext())
        {
            SortTreeNodeNew<T> sortTreeNode = it.next();
            String parentId = sortTreeNode.getSortNodeInfo().getParentId();
            SortTreeNodeNew<T> tmpTreeNode = tmpHashMap.get(parentId);
            if (tmpTreeNode != null)
            {
                tmpTreeNode.addChild(sortTreeNode);
                sortTreeNode.setParent(tmpTreeNode);
            }
            else
            {
                root.addChild(sortTreeNode);
                sortTreeNode.setParent(root);
            }
        }
        root.computerLevel(-1);
        root.computerRange();
        root.computerRowIndex(0);
        return root;
    }

    public void clear()
    {
        this.m_range = 0;
        this.m_node = null;
        this.m_parent = null;
        if (this.m_list == null)
        {
            this.m_list.clear();
        }
        this.m_list = null;
    }

    public void computerLevel(int parentLevel)
    {
        this.m_level = (parentLevel + 1);
        if (this.m_list != null)
        {
            for (Iterator<SortTreeNodeNew<T>> it = this.m_list.iterator(); it.hasNext();)
            {
                it.next().computerLevel(this.m_level);
            }
        }
    }

    public int computerRange()
    {
        if ((this.m_list == null) || (this.m_list.size() == 0))
        {
            this.m_range = 1;
        }
        else
        {
            int tmpRange = 0;
            for (Iterator<SortTreeNodeNew<T>> it = this.m_list.iterator(); it.hasNext();)
            {
                tmpRange += it.next().computerRange();
            }
            this.m_range = tmpRange;
        }
        return this.m_range;
    }

    public int computerRowIndex(int rowIndex)
    {
        this.m_rowIndex = rowIndex;
        if (this.m_list != null)
        {
            for (Iterator<SortTreeNodeNew<T>> it = this.m_list.iterator(); it.hasNext();)
            {
                rowIndex += 1;
                rowIndex = it.next().computerRowIndex(rowIndex);
            }
        }
        return rowIndex;
    }

    public SortTreeNodeNew<T> getChildAt(int childIndex)
    {
        if (this.m_list == null)
        {
            return null;
        }
        return this.m_list.get(childIndex);
    }

    public int getChildCount()
    {
        if (this.m_list == null)
        {
            return 0;
        }
        return this.m_list.size();
    }

    public List<SortTreeNodeNew<T>> getChildren()
    {
        return this.m_list;
    }

    public String getChildRowIndexs()
    {
        if ((this.m_list == null) || (this.m_list.size() == 0))
        {
            return "";
        }
        StringBuilder result = new StringBuilder();
        boolean isFirst = true;
        for (Iterator<SortTreeNodeNew<T>> it = this.m_list.iterator(); it.hasNext();)
        {
            if (isFirst == true)
            {
                isFirst = false;
            }
            else
            {
                result.append(",");
            }
            result.append(it.next().getRowIndex());
        }
        return result.toString();
    }

    public int getIndex(SortTreeNodeNew<T> node)
    {
        if (this.m_list == null)
        {
            return -1;
        }
        return this.m_list.indexOf(node);
    }

    public int getLevel()
    {
        return this.m_level;
    }

    public List<SortTreeNodeNew<T>> getListByLevel(int level)
    {
        List<SortTreeNodeNew<T>> result = new ArrayList<SortTreeNodeNew<T>>();
        getListByLevel(level, result);
        return result;
    }

    protected void getListByLevel(int level, List<SortTreeNodeNew<T>> aList)
    {
        if (this.m_list != null)
        {
            if (level == 0)
            {
                aList.addAll(this.m_list);
            }
            else
            {
                for (int i = 0; i < this.m_list.size(); ++i)
                {
                    this.m_list.get(i).getListByLevel(level - 1, aList);
                }
            }
        }
    }

    public SortTreeNodeNew<T> getParent()
    {
        return this.m_parent;
    }

    public int getRowIndex()
    {
        return this.m_rowIndex;
    }

    public T getSortNodeInfo()
    {
        return this.m_node;
    }

    public SortTreeNodeNew<T> getTreeNode(int row, int level) throws Exception
    {
        int count = 0;
        SortTreeNodeNew<T> result = null;
        for (int i = 0; i < this.m_list.size(); ++i)
        {
            result = this.m_list.get(i);
            if (row < count + result.m_range)
            {
                break;
            }
            count += result.m_range;
        }
        if (level == 0)
        {
            return result;
        }
        return result.getTreeNode(row - count, level - 1);
    }

    public boolean isLeaf()
    {
        return this.m_list == null || this.m_list.size() == 0;
    }

    public void setParent(SortTreeNodeNew<T> parent)
    {
        this.m_parent = parent;
    }

    public List<SortTreeNodeNew<T>> toArray()
    {
        List<SortTreeNodeNew<T>> result = new ArrayList<SortTreeNodeNew<T>>();
        toArray(result);
        return result;
    }

    protected void toArray(List<SortTreeNodeNew<T>> list)
    {
        if (this.m_list == null)
        {
            return;
        }
        for (Iterator<SortTreeNodeNew<T>> it = this.m_list.iterator(); it.hasNext();)
        {
            SortTreeNodeNew<T> tmpNode = it.next();
            list.add(tmpNode);
            tmpNode.toArray(list);
        }
    }

    public List<T> toArrayOfData()
    {
        List<T> result = new ArrayList<T>();
        toArrayOfData(result);
        return result;
    }

    protected void toArrayOfData(List<T> list)
    {
        if (this.m_list == null)
        {
            return;
        }
        SortTreeNodeNew<T> tmpNode = null;
        for (Iterator<SortTreeNodeNew<T>> it = this.m_list.iterator(); it.hasNext();)
        {
            tmpNode = it.next();
            tmpNode.getSortNodeInfo().setChildCount(tmpNode.getChildCount());
            tmpNode.getSortNodeInfo().setLevel(tmpNode.getLevel());
            tmpNode.getSortNodeInfo().setChildRowIndexs(tmpNode.getChildRowIndexs());
            list.add(tmpNode.getSortNodeInfo());
            tmpNode.toArrayOfData(list);
        }
    }

    public String toString()
    {
        return toString(new StringBuilder(), 0, "  ").toString();
    }

    protected StringBuilder toString(StringBuilder buffer, int level, String falg)
    {
        if (this.m_node != null)
        {
            buffer.append(this.m_rowIndex).append(":");
            for (int i = 0; i < level; ++i)
            {
                buffer.append(falg);
            }
        }
        if (this.m_list != null)
        {
            for (Iterator<SortTreeNodeNew<T>> it = this.m_list.iterator(); it.hasNext();)
            {
                it.next().toString(buffer, level + 1, falg);
            }
        }
        return buffer;
    }
}

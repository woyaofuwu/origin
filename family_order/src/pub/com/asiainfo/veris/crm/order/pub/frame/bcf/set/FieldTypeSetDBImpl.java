/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: FieldTypeSetDBImpl.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:33:03 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class FieldTypeSetDBImpl implements FieldTypeSetDB
{

    private FieldTypeDB[] fieldArray = null;

    private String m_fileName = "";

    private String name = "";

    private String mainField = "";

    public FieldTypeSetDBImpl()
    {
        this.m_fileName = "";
        this.name = "";
        this.mainField = "";
    }

    @SuppressWarnings("unchecked")
    public boolean addField(FieldTypeDB aField)
    {
        ArrayList tmpList;
        if (this.fieldArray != null)
        {
            tmpList = new ArrayList(Arrays.asList(this.fieldArray));
        }
        else
        {
            tmpList = new ArrayList();
        }
        boolean aFlag = true;
        if (this.fieldArray != null)
            for (int i = 0; i < this.fieldArray.length; ++i)
                if (this.fieldArray[i].getName().equalsIgnoreCase(aField.getName()))
                {
                    aFlag = false;
                    break;
                }
        if (aFlag)
        {
            tmpList.add(aField);
            this.fieldArray = ((FieldTypeDB[]) (FieldTypeDB[]) tmpList.toArray(new FieldTypeDB[0]));
            return true;
        }
        return false;
    }

    public FieldTypeDB getField(int aIndex)
    {
        if (this.fieldArray == null)
            return null;
        if ((aIndex >= this.fieldArray.length) || (aIndex < 0))
            return null;
        return this.fieldArray[aIndex];
    }

    public FieldTypeDB getField(String aName)
    {
        if (this.fieldArray == null)
            return null;
        for (int i = 0; i < this.fieldArray.length; ++i)
        {
            if (this.fieldArray[i].getName().equalsIgnoreCase(aName))
                return this.fieldArray[i];
        }
        return null;
    }

    public int getFieldCount()
    {
        if (this.fieldArray == null)
            return 0;
        return this.fieldArray.length;
    }

    public FieldTypeDB[] getFieldList()
    {
        return this.fieldArray;
    }

    public String getm_fileName()
    {
        return this.m_fileName;
    }

    public String getMainField()
    {
        return this.mainField;
    }

    public String getName()
    {
        String sRe = this.name;
        if ((this.name != null) && (this.name.indexOf(".") != -1))
            sRe = sRe.substring(sRe.lastIndexOf(".") + 1, sRe.length());
        return sRe;
    }

    @SuppressWarnings("unchecked")
    public FieldTypeDB[] getPkField()
    {
        if (this.fieldArray == null)
            return null;
        FieldTypeDB[] reFields = null;
        ArrayList tmpList = new ArrayList();
        for (int i = 0; i < this.fieldArray.length; ++i)
        {
            if (this.fieldArray[i].getIsPk())
                tmpList.add(this.fieldArray[i]);
        }
        if (tmpList.size() > 0)
            reFields = (FieldTypeDB[]) (FieldTypeDB[]) tmpList.toArray(new FieldTypeDB[0]);
        return reFields;
    }

    public String getSinglem_fileName()
    {
        String sName = "";
        if (this.m_fileName.lastIndexOf("/") == -1)
            sName = this.m_fileName;
        else
        {
            sName = this.m_fileName.substring(this.m_fileName.lastIndexOf("/") + 1, this.m_fileName.length());
        }
        if (sName.lastIndexOf(".") != -1)
            sName = sName.substring(0, sName.lastIndexOf("."));

        return sName;
    }

    public boolean isFKFieldExist()
    {
        if (this.fieldArray == null)
            return false;
        boolean reFlag = false;
        for (int i = 0; i < this.fieldArray.length; ++i)
        {
            if ((this.fieldArray[i].getDisplayName() == null) || (this.fieldArray[i].getDisplayName().equals("")))
            {
                continue;
            }
            reFlag = true;
            break;
        }
        return reFlag;
    }

    @SuppressWarnings("unchecked")
    public boolean removeField(FieldTypeDB aField)
    {
        if ((this.fieldArray == null) || (this.fieldArray.length == 0))
            return false;
        try
        {
            ArrayList tmpList = new ArrayList(Arrays.asList(this.fieldArray));
            tmpList.remove(aField);
            this.fieldArray = ((FieldTypeDB[]) (FieldTypeDB[]) tmpList.toArray(new FieldTypeDB[0]));
            return true;
        }
        catch (Exception e)
        {
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean removeField(int aIndex)
    {
        if ((this.fieldArray == null) || (this.fieldArray.length == 0))
            return false;
        try
        {
            ArrayList tmpList = new ArrayList(Arrays.asList(this.fieldArray));
            tmpList.remove(aIndex);
            this.fieldArray = ((FieldTypeDB[]) (FieldTypeDB[]) tmpList.toArray(new FieldTypeDB[0]));
            return true;
        }
        catch (Exception e)
        {
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean removeField(String aFieldName)
    {
        if ((this.fieldArray == null) || (this.fieldArray.length == 0))
            return false;
        try
        {
            ArrayList tmpList = new ArrayList(Arrays.asList(this.fieldArray));

            for (int i = 0; i < this.fieldArray.length; ++i)
                if (this.fieldArray[i].getName().equalsIgnoreCase(aFieldName))
                {
                    tmpList.remove(i);
                    break;
                }
            this.fieldArray = ((FieldTypeDB[]) (FieldTypeDB[]) tmpList.toArray(new FieldTypeDB[0]));
            return true;
        }
        catch (Exception e)
        {
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean removeFieldList()
    {
        if ((this.fieldArray == null) || (this.fieldArray.length == 0))
            return false;
        try
        {
            ArrayList tmpList = new ArrayList(Arrays.asList(this.fieldArray));
            tmpList.clear();
            this.fieldArray = ((FieldTypeDB[]) (FieldTypeDB[]) tmpList.toArray(new FieldTypeDB[0]));
            return true;
        }
        catch (Exception e)
        {
        }
        return false;
    }

    public boolean saveToDB()
    {
        return true;
    }

    public void setAlias(String aAlias)
    {
    }

    @SuppressWarnings("unchecked")
    public boolean setField(FieldTypeDB aField, boolean isCreate, String aOldFieldName)
    {
        if ((this.fieldArray == null) || (this.fieldArray.length == 0))
        {
            if (isCreate)
            {
                return addField(aField);
            }

            return false;
        }

        int tmpIndex = -1;

        String strOldName = "";
        for (int i = 0; i < this.fieldArray.length; ++i)
        {
            if (aOldFieldName.equals(""))
                strOldName = aOldFieldName;
            else
                strOldName = aField.getName();
            if (this.fieldArray[i].getName().equalsIgnoreCase(strOldName))
            {
                tmpIndex = i;
                break;
            }
        }
        if (tmpIndex >= 0)
        {
            try
            {
                ArrayList tmpList = new ArrayList(Arrays.asList(this.fieldArray));
                tmpList.set(tmpIndex, aField);
                this.fieldArray = ((FieldTypeDB[]) (FieldTypeDB[]) tmpList.toArray(new FieldTypeDB[0]));
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }
        if (isCreate)
        {
            return addField(aField);
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean setField(int aIndex, FieldTypeDB aField)
    {
        if ((this.fieldArray == null) || (this.fieldArray.length == 0))
            return false;
        ArrayList tmpList = new ArrayList(Arrays.asList(this.fieldArray));
        tmpList.set(aIndex, aField);
        this.fieldArray = ((FieldTypeDB[]) (FieldTypeDB[]) tmpList.toArray(new FieldTypeDB[0]));
        return true;
    }

    public boolean setFieldList(FieldTypeDB[] aFL)
    {
        this.fieldArray = aFL;
        return true;
    }

    public boolean setm_fileName(String aName)
    {
        this.m_fileName = aName;
        return true;
    }

    public boolean setMainField(String aMF)
    {
        this.mainField = aMF;
        return true;
    }

    public boolean setName(String aName)
    {
        this.name = aName;
        return true;
    }

    public void sortFieldArray(FieldTypeDB[] aFieldArray)
    {
        FieldTypeDB[] tmpFieldArray = aFieldArray;
        int iCompareTimes = 0;
        for (int i = 0; i < tmpFieldArray.length; ++i)
            for (int j = i; j < tmpFieldArray.length; ++j)
            {
                int iSeq = tmpFieldArray[i].getDisplaySeq();
                int iSeq2 = tmpFieldArray[j].getDisplaySeq();
                if (iSeq2 < iSeq)
                {
                    FieldTypeDB tmpSwap = tmpFieldArray[j];
                    tmpFieldArray[j] = tmpFieldArray[i];
                    tmpFieldArray[i] = tmpSwap;
                }

                ++iCompareTimes;
            }
    }

}

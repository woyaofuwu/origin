
package com.asiainfo.veris.crm.order.web.frame.csview.group.opengroupmember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.FeeException;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBaseView;

public abstract class PreviewInfo extends GroupBasePage
{

    private void checkElementForEsop(String newElementStr, String oldElementStr) throws Exception
    {

        IDataset newElements = new DatasetList(newElementStr);
        IDataset oldElements = new DatasetList(oldElementStr);
        for (int i = 0; i < newElements.size(); i++)
        {
            IData newElement = newElements.getData(i);
            IDataset newElementElements = newElement.getDataset("ELEMENTS");
            String packageId = newElement.getString("PACKAGE_ID");
            boolean flag = false;
            for (int j = 0; j < newElementElements.size(); j++)
            {
                IData newElementElement = newElementElements.getData(j);
                String state = newElementElement.getString("STATE");
                String elementId = newElementElement.getString("ELEMENT_ID");
                String elementTypeCode = newElementElement.getString("ELEMENT_TYPE_CODE");
                IDataset servParams = new DatasetList();
                IDataset disParams = new DatasetList();
                if ("D".equals(elementTypeCode))
                {
                    if (newElementElement.containsKey("DISCNT_PARAM"))
                    {
                        disParams = newElementElement.getDataset("DISCNT_PARAM");
                    }
                }
                else if ("S".equals(elementTypeCode))
                {
                    if (newElementElement.containsKey("SERV_PARAM"))
                    {
                        servParams = newElementElement.getDataset("SERV_PARAM");
                    }
                }
                for (int m = 0; m < oldElements.size(); m++)
                {
                    IData oldElement = oldElements.getData(m);
                    IDataset oldElementElements = oldElement.getDataset("ELEMENTS");
                    String oldPackageId = oldElement.getString("PACKAGE_ID");
                    if (packageId.equals(oldPackageId))
                    {
                        for (int n = 0; n < oldElementElements.size(); n++)
                        {
                            IData oldElementElement = oldElementElements.getData(n);
                            String stateOld = oldElementElement.getString("STATE");
                            String elementIdOld = oldElementElement.getString("ELEMENT_ID");
                            String elementTypeCodeOld = oldElementElement.getString("ELEMENT_TYPE_CODE");
                            if (stateOld.equals(state) && elementIdOld.equals(elementId) && elementTypeCode.equals(elementTypeCodeOld))
                            {
                                IDataset servParamOlds = new DatasetList();
                                IDataset disParamOlds = new DatasetList();
                                if ("D".equals(elementTypeCodeOld))
                                {
                                    if (oldElementElement.containsKey("DISCNT_PARAM"))
                                    {
                                        disParamOlds = oldElementElement.getDataset("DISCNT_PARAM");
                                        if (null == disParamOlds || disParamOlds.isEmpty())
                                        {
                                            if (null == disParams || disParams.isEmpty())
                                            {
                                                flag = true;
                                                break;
                                            }
                                            else
                                            {
                                                CSViewException.apperr(FeeException.CRM_FEE_47);
                                            }
                                        }
                                        else
                                        {
                                            for (int ki = 0; ki < disParamOlds.size(); ki++)
                                            {
                                                IData disParamOld = disParamOlds.getData(ki);
                                                boolean flagA = false;
                                                if (disParamOlds.size() != disParams.size())
                                                {
                                                    CSViewException.apperr(FeeException.CRM_FEE_5);
                                                }
                                                for (int kj = 0; kj < disParams.size(); kj++)
                                                {
                                                    IData disParam = disParams.getData(kj);
                                                    String disParamAttrCode = disParam.getString("ATTR_CODE", "");
                                                    String disParamAttrValue = disParam.getString("ATTR_VALUE", "");
                                                    String disParamAttrCodeOld = disParamOld.getString("ATTR_CODE", "");
                                                    String disParamAttrValueOld = disParamOld.getString("ATTR_VALUE", "");
                                                    if (disParamAttrCode.equals(disParamAttrCodeOld) && disParamAttrValue.equals(disParamAttrValueOld))
                                                    {
                                                        flagA = true;
                                                        break;
                                                    }
                                                }
                                                if (!flagA)
                                                {
                                                    CSViewException.apperr(FeeException.CRM_FEE_47);
                                                }
                                            }
                                            flag = true;
                                        }
                                    }
                                    else
                                    {
                                        if (null == disParams || disParams.isEmpty())
                                        {
                                            flag = true;
                                            break;
                                        }
                                    }
                                }
                                else if ("S".equals(elementTypeCodeOld))
                                {
                                    if (oldElementElement.containsKey("SERV_PARAM"))
                                    {
                                        servParamOlds = oldElementElement.getDataset("SERV_PARAM");
                                        if (null == servParamOlds || servParamOlds.isEmpty())
                                        {
                                            if (null == servParams || servParams.isEmpty())
                                            {
                                                flag = true;
                                                break;
                                            }
                                            else
                                            {
                                                CSViewException.apperr(FeeException.CRM_FEE_5);
                                            }
                                        }
                                        else
                                        {
                                            for (int ki = 0; ki < servParamOlds.size(); ki++)
                                            {
                                                IData servParamOld = servParamOlds.getData(ki);
                                                boolean flagA = false;
                                                if (servParamOlds.size() != servParams.size())
                                                {
                                                    CSViewException.apperr(FeeException.CRM_FEE_47);
                                                }
                                                for (int kj = 0; kj < servParams.size(); kj++)
                                                {
                                                    IData serParam = servParams.getData(kj);
                                                    String servParamAttrCode = serParam.getString("ATTR_CODE", "");
                                                    String servParamAttrValue = serParam.getString("ATTR_VALUE", "");
                                                    String servParamAttrCodeOld = servParamOld.getString("ATTR_CODE", "");
                                                    String servParamAttrValueOld = servParamOld.getString("ATTR_VALUE", "");
                                                    if (servParamAttrCode.equals(servParamAttrCodeOld) && servParamAttrValue.equals(servParamAttrValueOld))
                                                    {
                                                        flagA = true;
                                                        break;
                                                    }
                                                }
                                                if (!flagA)
                                                {
                                                    CSViewException.apperr(FeeException.CRM_FEE_5);
                                                }
                                            }
                                            flag = true;
                                        }
                                    }
                                    else
                                    {
                                        if (null == servParams || servParams.isEmpty())
                                        {
                                            flag = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
                if (!flag)
                {
                    CSViewException.apperr(FeeException.CRM_FEE_5);
                }
            }
        }
    }

    /**
     * 初始化页面 initial
     * 
     * @author
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {

        String esopTag = getData().getString("ESOP_TAG");
        if ("ESOP_MEM".equals(esopTag))
        {
            String selectedE = getParameter("SELECTED_ELEMENTS");
            String selectedEO = getParameter("OLD_SELECTED_ELEMENTS");
            checkElementForEsop(selectedE, selectedEO);
        }

        // 预览基本信息
        IData memCustInfo = new DataMap(getData().getString("PMEM_CUST_INFO"));
        IData memUserInfo = new DataMap(getData().getString("PMEM_USER_INFO"));
        IData memAcctInfo = new DataMap(getData().getString("PMEM_ACCT_INFO"));

        String resNumber = getData().getString("SERIAL_NUMBER");
        String groupId = getData().getString("GROUP_ID");

        setMemCustInfo(memCustInfo);
        setMemUserInfo(memUserInfo);
        setMemAcctInfo(memAcctInfo);

        setSerialNumber(resNumber);
        setGroupId(groupId);

        // 预览成员产品信息
        IData memberPackElements = new DataMap();// 成员产品元素
        IDataset packElements = new DatasetList(getData().getString("SELECTED_ELEMENTS"));
        GroupBaseView.processProductElements(this, packElements, memberPackElements);
        setMemberProduct(memberPackElements);
    }

    public abstract void setAcctInfo(IData acctInfo);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setGroupId(String groupId);

    public abstract void setInfo(IData info);

    public abstract void setMemAcctInfo(IData mAcctInfo);

    public abstract void setMemberProduct(IData memberProduct);

    public abstract void setMemCustInfo(IData mCustInfo);

    public abstract void setMemUserInfo(IData mUserInfo);

    public abstract void setSerialNumber(String serialNumber);

    public abstract void setUserInfo(IData userInfo);

}

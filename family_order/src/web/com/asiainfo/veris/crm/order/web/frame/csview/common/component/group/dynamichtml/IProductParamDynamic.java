
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml;

import java.util.Iterator;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userattrinfo.UserAttrInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attriteminfo.AttrItemInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupEsopUtilView;

public class IProductParamDynamic
{

    private IData attrItem;

    private IDataset attrItemSet;

    public IData getAttrItem()
    {

        return attrItem;
    }
    
    public IDataset getAttrItemSource(String key)
    {
        IData data = (IData) attrItem.get(key);

        if ((data == null) || (data.size() == 0))
            return null;
        else
            return data.getDataset("DATA_VAL");
    }

    public IDataset getAttrItemSet()
    {

        return attrItemSet;
    }

    public String getAttrItemValue(String key, String type)
    {
        // @chenyi 此处测试才注释掉
        IData data = (IData) attrItem.get(key);

        if ((data == null) || (data.size() == 0))
            return "";
        else
            return data.getString(type);
    }

    public String getLoginLogId()
    {
        // TODO Auto-generated method stub
        return "";
    }

    public String getMenuId()
    {
        // TODO Auto-generated method stub
        return "";
    }

    public String getPageName()
    {
        // TODO Auto-generated method stub
        return "";
    }

    public IData initChgMb(IBizCommon bp, IData data) throws Throwable
    {
        String mebUserId = data.getString("MEB_USER_ID", "");
        String grpUserId = data.getString("GRP_USER_ID", "");
        String mebEparchyCode = data.getString("MEB_EPARCHY_CODE");
        IDataset dataset = UserAttrInfoIntfViewUtil.qryMebProductAttrInfosByUserIdAndUserIdA(bp, mebUserId, grpUserId, mebEparchyCode);
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IData user_attrida = (IData) dataset.get(i);
                String byFeeParam = user_attrida.getString("ATTR_CODE");
                if (!"".equals(byFeeParam) && byFeeParam.length() > 3 && byFeeParam.substring(0, 3).equals("FEE"))
                {
                    String serParamStr = user_attrida.getString("ATTR_VALUE", "0");
                    user_attrida.put("ATTR_VALUE", Integer.parseInt(serParamStr) / 100);
                    dataset.set(i, user_attrida);
                }
            }
        }
        attrItemSet = dataset;
        if(IDataUtil.isNotEmpty(dataset))
        {
            attrItem = IDataUtil.hTable2STable(dataset, "ATTR_CODE", "ATTR_VALUE", "ATTR_VALUE");
        }
        else
        {
            attrItem = new DataMap();
        }
        
        IDataset pzAttrItems = AttrItemInfoIntfViewUtil.qryMebProductItemAInfosByGrpProductIdAndEparchyCode(bp, data.getString("PRODUCT_ID", ""), data.getString("USER_EPARCHY_CODE"));

        if(IDataUtil.isNotEmpty(pzAttrItems))
        {
            IData pzAttrItem = IDataUtil.hTable2STable(pzAttrItems, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
            // 方便前台取下拉框选项值
            transComboBoxValue(attrItem, pzAttrItem);
        }

        IData result = new DataMap();
        IData info = new DataMap();
        info.put("PRODUCT_ID", data.getString("PRODUCT_ID", ""));
        result.put("PARAM_INFO", info);
        result.put("ATTRITEM", attrItem);
        result.put("ATTRITEMSET", attrItemSet);
        return result;
    }

    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IDataset dataset = UserAttrInfoIntfViewUtil.qryGrpProductAttrInfosByUserId(bp, data.getString("USER_ID", ""));
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IData user_attrida = (IData) dataset.get(i);
                String byFeeParam = user_attrida.getString("ATTR_CODE");
                if (!"".equals(byFeeParam) && byFeeParam.length() > 3 && byFeeParam.substring(0, 3).equals("FEE"))
                {
                    String serParamStr = user_attrida.getString("ATTR_VALUE", "0");
                    user_attrida.put("ATTR_VALUE", Integer.parseInt(serParamStr) / 100);
                    dataset.set(i, user_attrida);
                }

            }
        }
        attrItemSet = dataset;
        if(IDataUtil.isNotEmpty(dataset))
        {
            attrItem = IDataUtil.hTable2STable(dataset, "ATTR_CODE", "ATTR_VALUE", "ATTR_VALUE");
        }
        else
        {
            attrItem = new DataMap();
        }
        
        IDataset pzAttrItems = AttrItemInfoIntfViewUtil.qryGrpProductItemAInfosByGrpProductIdAndEparchyCode(bp, data.getString("PRODUCT_ID", ""), data.getString("USER_EPARCHY_CODE"));

        if(IDataUtil.isNotEmpty(pzAttrItems))
        {
            IData pzAttrItem = IDataUtil.hTable2STable(pzAttrItems, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
            // 方便前台取下拉框选项值
            transComboBoxValue(attrItem, pzAttrItem);
        }
        
        IData result = new DataMap();
        IData info = new DataMap();
        info.put("PRODUCT_ID", data.getString("PRODUCT_ID", ""));
        IData esopParam = GroupEsopUtilView.getEsopParams(bp, data, true);
        if (IDataUtil.isNotEmpty(esopParam))
        {
            attrItem.putAll(IDataUtil.iDataA2iDataB(esopParam, "ATTR_VALUE"));
            info.putAll(esopParam);
        }

        result.put("PARAM_INFO", info);
        result.put("ATTRITEM", attrItem);
        result.put("ATTRITEMSET", attrItemSet);
        return result;
    }

    public IData initCrtMb(IBizCommon bp, IData data) throws Throwable
    {
        IDataset dataset = AttrItemInfoIntfViewUtil.qryMebProductItemAInfosByGrpProductIdAndEparchyCode(bp, data.getString("PRODUCT_ID", ""), data.getString("USER_EPARCHY_CODE"));

        if(IDataUtil.isNotEmpty(dataset))
        {
            attrItem = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
        }
        else
        {
            attrItem = new DataMap();
        }
        IData result = new DataMap();
        IData info = new DataMap();
        info.put("PRODUCT_ID", data.getString("PRODUCT_ID", ""));
        result.put("PARAM_INFO", info);
        result.put("ATTRITEM", attrItem);
        return result;
    }

    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {

        IDataset dataset = AttrItemInfoIntfViewUtil.qryGrpProductItemAInfosByGrpProductIdAndEparchyCode(bp, data.getString("PRODUCT_ID", ""), data.getString("USER_EPARCHY_CODE"));

        if(IDataUtil.isNotEmpty(dataset))
        {
            attrItem = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
        }
        else
        {
            attrItem = new DataMap();
        }
        IData result = new DataMap();
        IData info = new DataMap();
        info.put("PRODUCT_ID", data.getString("PRODUCT_ID", ""));

        IData esopParam = GroupEsopUtilView.getEsopParams(bp, data, true);
        if (IDataUtil.isNotEmpty(esopParam))
        {
            attrItem.putAll(IDataUtil.iDataA2iDataB(esopParam, "ATTR_VALUE"));
            info.putAll(esopParam);
        }
        result.put("PARAM_INFO", info);
        result.put("ATTRITEM", attrItem);
        return result;

    }

    public IData initOpnMb(IBizCommon bp, IData param) throws Throwable
    {
        IData result = new DataMap();
        IData info = new DataMap();
        String eosstr = param.getString("EOS", "");
        IData eosData = new DataMap();
        attrItem = new DataMap();
        attrItemSet = new DatasetList();
        if (!StringUtils.isEmpty(eosstr) && !eosstr.equals("null"))
        {
            IDataset eosList = new DatasetList(eosstr);
            if (eosList != null && eosList.size() > 0)
            {
                eosData = eosList.getData(0);
                IData esopParam = GroupEsopUtilView.getLocalDlineEsopData(bp, eosData);
                if (IDataUtil.isNotEmpty(esopParam))
                {
                    attrItem.putAll(IDataUtil.iDataA2iDataB(esopParam, "ATTR_VALUE"));
                    info.putAll(esopParam);
                }
            }
        }
        result.put("PARAM_INFO", info);
        result.put("ATTRITEM", attrItem);
        result.put("ATTRITEMSET", attrItemSet);
        return result;
    }

    public void initPparam(IBizCommon bp, IData data) throws Exception
    {

        // 查询数据
        IDataset dataset = AttrItemInfoIntfViewUtil.qryAttrItemAInfosByIdAndIdTypeAttrObjEparchyCode(bp, data.getString("PRODUCT_ID", ""), "P", "2", data.getString("USER_EPARCHY_CODE"));
        // 属性默认值自动匹配
        // getMatchItemValue( dataset, "initCrtMb");

        setAttrItem(IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE"));

    }

    public void setAttrItem(IData attrItem)
    {

        this.attrItem = attrItem;
    }

    public void setAttrItemSet(IDataset attrItemSet)
    {

        this.attrItemSet = attrItemSet;
    }
    
    /**
     * 
     * @Title: transComboBoxValue  
     * @Description: 方便前台变更页面取下拉框选项值
     * @param @param userAttrItem
     * @param @param pzAttrItem
     * @param @throws Exception    设定文件  
     * @return void    返回类型  
     * @throws
     */
    public void transComboBoxValue(IData userAttrItem, IData pzAttrItem) throws Exception
    {
        if (IDataUtil.isEmpty(pzAttrItem))
        {
            return;
        }
        else if (IDataUtil.isEmpty(userAttrItem)) 
        {
            userAttrItem.putAll(pzAttrItem);
        }
        else 
        {
            for (Iterator iterator = pzAttrItem.keySet().iterator(); iterator.hasNext();)
            {
                String datakey = (String) iterator.next();
                IData tempData = userAttrItem.getData(datakey);
                IData tempData2 = pzAttrItem.getData(datakey);
                if (IDataUtil.isEmpty(tempData))
                {
                    tempData = tempData2;
                    userAttrItem.put(datakey, tempData);
                }
                else if (IDataUtil.isNotEmpty(tempData2))
                {
                    tempData.put("DATA_VAL", tempData2.getDataset("DATA_VAL"));
                }
            }
        }
    }
    
}

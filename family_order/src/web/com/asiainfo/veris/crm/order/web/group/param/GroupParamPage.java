
package com.asiainfo.veris.crm.order.web.group.param;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userattrinfo.UserAttrInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attriteminfo.AttrItemInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GroupParamPage extends GroupBasePage
{

    private IData attrItem;

    private IDataset attrItemSet;

    public IData getAttrItem()
    {

        return attrItem;
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

    // 属性默认值自动匹配
    public void getMatchItemValue(IDataset ids, String match) throws Exception
    {

        if (ids == null)
        {
            return;
        }

        String strMatchFlag = "";
        String strMatchAttrCode = "";
        String strMatchAttrValue = "";

        IData idsMatch = new DataMap();

        // 设置匹配规则参数值
        if ("initCrtMb".equals(match))
        {
            idsMatch.put("USER_ID_B", getParameter("MEM_USER_ID", "")); // 成员用户标识

            idsMatch.put("PRODUCT_ID", getParameter("PRODUCT_ID", "")); // 集团产品ID
            idsMatch.put("USER_ID", getParameter("USER_ID", "")); // 集团用户标识
        }

        // 循环取下面的所有itema
        for (int row = 0; row < ids.size(); row++)
        {
            IData data = ids.getData(row);

            // 取自动匹配标记
            strMatchFlag = data.getString("RSRV_STR1", "");

            // 是否自动匹配，1自动匹配
            if ("1".equals(strMatchFlag))
            {
                strMatchAttrCode = data.getString("ATTR_CODE", "");
                idsMatch.put("MATCH_ATTR_CODE", strMatchAttrCode);
                // @chenyi svn没写
                // call 规则进行费用匹配
                // j2ee-ly callviewtobean("CS.SaleActiveMgr.getFixFeeMatch", idsMatch);

                // 取匹配后的费用
                strMatchAttrValue = idsMatch.getString("MATCH_ATTR_INIT_VALUE", "");

                // 是否已匹配
                if (!"".equals(strMatchAttrValue) && !"-1".equals(strMatchAttrValue))
                {
                    // 如果匹配到，将默认值替换为匹配值
                    data.put("ATTR_INIT_VALUE", strMatchAttrValue);
                }
            }
        }
    }

    public void initChgMb(IRequestCycle cycle) throws Throwable
    {
        String mebUserId = getParameter("MEB_USER_ID", "");
        String grpUserId = getParameter("GRP_USER_ID", "");
        String mebEparchyCode = getParameter("MEB_EPARCHY_CODE");
        IDataset dataset = UserAttrInfoIntfViewUtil.qryMebProductAttrInfosByUserIdAndUserIdA(this, mebUserId, grpUserId, mebEparchyCode);

        setAttrItemSet(dataset);
        setAttrItem(IDataUtil.hTable2STable(dataset, "ATTR_CODE", "ATTR_VALUE", "ATTR_VALUE"));
    }

    public void initChgUs(IRequestCycle cycle) throws Throwable
    {
        IDataset dataset = UserAttrInfoIntfViewUtil.qryGrpProductAttrInfosByUserId(this, getParameter("USER_ID", ""));
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
        setAttrItemSet(dataset);
        setAttrItem(IDataUtil.hTable2STable(dataset, "ATTR_CODE", "ATTR_VALUE", "ATTR_VALUE"));
    }

    public void initCrtMb(IRequestCycle cycle) throws Throwable
    {
        IDataset dataset = AttrItemInfoIntfViewUtil.qryMebProductItemAInfosByGrpProductIdAndEparchyCode(this, getParameter("PRODUCT_ID", ""), getParameter("USER_EPARCHY_CODE"));
        // 属性默认值自动匹配
        // getMatchItemValue( dataset, "initCrtMb");

        setAttrItem(IDataUtil.hTable2STable(dataset, "ATTR_CODE", "ATTR_INIT_VALUE", "ATTR_VALUE"));
        // initPparam();// 改为产品级
    }

    public void initCrtUs(IRequestCycle cycle) throws Throwable
    {
        IDataset dataset = AttrItemInfoIntfViewUtil.qryGrpProductItemAInfosByGrpProductIdAndEparchyCode(this, getParameter("PRODUCT_ID", ""), getParameter("USER_EPARCHY_CODE"));
        setAttrItem(IDataUtil.hTable2STable(dataset, "ATTR_CODE", "ATTR_INIT_VALUE", "ATTR_VALUE"));
    }

    public void initPparam() throws Exception
    {

        // 查询数据
        IDataset dataset = AttrItemInfoIntfViewUtil.qryAttrItemAInfosByIdAndIdTypeAttrObjEparchyCode(this, getParameter("PRODUCT_ID", ""), "P", "2", getParameter("USER_EPARCHY_CODE"));
        // 属性默认值自动匹配
        getMatchItemValue(dataset, "initCrtMb");

        setAttrItem(IDataUtil.hTable2STable(dataset, "ATTR_CODE", "ATTR_INIT_VALUE", "ATTR_VALUE"));

    }

    public void setAttrItem(IData attrItem)
    {

        this.attrItem = attrItem;
    }

    public void setAttrItemSet(IDataset attrItemSet)
    {

        this.attrItemSet = attrItemSet;
    }
}

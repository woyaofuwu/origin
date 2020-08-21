/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.multioffer;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.ITreeNodeInfo;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.SortTreeNode;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.SoProductAttrBean;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.builder.impl.RoleProdOperDataBuilderImpl;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean.PackageOperData;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean.ProductOperData;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean.RoleOperData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: SoRoleProdTableService.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-29 上午10:11:15 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-29 chengxf2 v1.0.0 修改原因
 */

public class SoRoleProdTableService extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-29 上午10:11:54 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-29 chengxf2 v1.0.0 修改原因
     */
    public IDataset buildRoleProdTable(IData input) throws Exception
    {
        RoleProdOperDataBuilderImpl builderImpl = new RoleProdOperDataBuilderImpl();
        RoleOperData roleOperData = builderImpl.buildPageOperData(input);
        List<ITreeNodeInfo> proAttrList = buildSoPackageBean(roleOperData, input);
        SortTreeNode sortTreeNode = SortTreeNode.buildTree(proAttrList);
        proAttrList = sortTreeNode.toArrayOfData();
        return new DatasetList(JSONArray.fromObject(proAttrList).toString());
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-24 下午04:10:36 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-24 chengxf2 v1.0.0 修改原因
     */
    private List<ITreeNodeInfo> buildSoPackageBean(RoleOperData roleOperData, IData input)
    {
        List<ITreeNodeInfo> packAttrList = new ArrayList<ITreeNodeInfo>();
        List<PackageOperData> packOperList = roleOperData.getPackOperList();
        for (int i = 0; i < packOperList.size(); i++)
        {
            PackageOperData packOperData = packOperList.get(i);
            SoProductAttrBean soPackAttr = new SoProductAttrBean();
            soPackAttr.setIsFold(true); // 目录
            soPackAttr.put("PARENT_ITEM_ID", roleOperData.getRoleId());
            soPackAttr.put("ITEM_ID", packOperData.getPackageId());
            soPackAttr.put("ITEM_NAME", packOperData.getPackageName());
            soPackAttr.put("ITEM_TYPE", packOperData.getItemType());
            packAttrList.add(soPackAttr);
            packAttrList.addAll(buildSoProductBean(packOperData, input));
        }
        return packAttrList;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-24 下午04:59:31 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-24 chengxf2 v1.0.0 修改原因
     */
    private List<ITreeNodeInfo> buildSoProductBean(PackageOperData packOperData, IData input)
    {
        List<ITreeNodeInfo> eleAttrList = new ArrayList<ITreeNodeInfo>();
        List<ProductOperData> eleOperList = packOperData.getProdOperList();
        for (int i = 0; i < eleOperList.size(); i++)
        {
            ProductOperData prodOperData = eleOperList.get(i);
            SoProductAttrBean soEleAttr = new SoProductAttrBean();
            soEleAttr.put("PARENT_ITEM_ID", packOperData.getPackageId());
            soEleAttr.put("ITEM_ID", prodOperData.getProductId());
            soEleAttr.put("ITEM_NAME", prodOperData.getProductName());
            soEleAttr.put("ITEM_TYPE", prodOperData.getItemType());
            eleAttrList.add(soEleAttr);
        }
        return eleAttrList;
    }

}

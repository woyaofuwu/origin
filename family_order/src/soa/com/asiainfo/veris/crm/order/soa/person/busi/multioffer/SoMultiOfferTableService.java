/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.multioffer;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.ITreeNodeInfo;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.SortTreeNode;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bm.BmFrameInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.SoProductAttrBean;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.builder.impl.ProRoleOperDataBuilderImpl;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean.ProductOperData;
import com.asiainfo.veris.crm.order.soa.person.busi.multioffer.pagedata.valuebean.RoleOperData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: SoProdAttrTableService.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-9 上午10:15:10 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-9 chengxf2 v1.0.0 修改原因
 */

public class SoMultiOfferTableService extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-25 上午09:10:32 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-25 chengxf2 v1.0.0 修改原因
     */
    private IData buildMenuParam(RoleOperData roleOperData, IData param)
    {
        IData menuParam = new DataMap();
        menuParam.putAll(param);
        menuParam.put("ROLE_CODE", roleOperData.getRoleId());
        menuParam.put("ROLE_NAME", roleOperData.getRoleName());
        menuParam.put("NET_TYPE_CODE", roleOperData.getNetTypeCode());
        menuParam.put("IS_GROUP_ROLE", roleOperData.getIsGroupRole());
        menuParam.put("IS_MAIN_ROLE", roleOperData.getIsMainRole());
        return menuParam;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-9 上午10:16:52 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-9 chengxf2 v1.0.0 修改原因
     */
    public IDataset buildMultiOfferTable(IData input) throws Exception
    {
        ProRoleOperDataBuilderImpl builderImpl = new ProRoleOperDataBuilderImpl();
        ProductOperData proOperData = builderImpl.buildPageOperData(input);
        List<ITreeNodeInfo> proAttrList = buildSoProdAttrBean(proOperData, input);
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
     * @date: 2014-4-10 上午08:11:24 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-10 chengxf2 v1.0.0 修改原因
     */
    private List<ITreeNodeInfo> buildSoProdAttrBean(ProductOperData proOperData, IData param) throws Exception
    {
        List<ITreeNodeInfo> proAttrList = new ArrayList<ITreeNodeInfo>();
        SoProductAttrBean soProdAttr = new SoProductAttrBean();
        soProdAttr.setIsFold(true); // 目录
        soProdAttr.put("PARENT_ITEM_ID", -1);
        soProdAttr.put("ITEM_ID", proOperData.getProductId());
        soProdAttr.put("ITEM_NAME", proOperData.getProductName());
        soProdAttr.put("ITEM_TYPE", proOperData.getItemType());
        StringBuilder rowSpeStatus = new StringBuilder();
        rowSpeStatus.append("checked=true;displayed=false;enabled=false");
        soProdAttr.put("ROW_SPE_STATUS", rowSpeStatus.toString());
        proAttrList.add(soProdAttr);
        proAttrList.addAll(buildSoRoleAttrBean(proOperData, param));
        return proAttrList;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-9 下午01:06:49 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-9 chengxf2 v1.0.0 修改原因
     */
    private List<ITreeNodeInfo> buildSoRoleAttrBean(ProductOperData proOperData, IData param) throws Exception
    {
        List<ITreeNodeInfo> proAttrList = new ArrayList<ITreeNodeInfo>();
        List<RoleOperData> roleOperList = proOperData.getRoleOperList();
        for (int i = 0; i < roleOperList.size(); i++)
        {
            RoleOperData roleOperData = roleOperList.get(i);
            SoProductAttrBean soRoleAttr = new SoProductAttrBean();
            soRoleAttr.setIsFold(true); // 目录
            soRoleAttr.put("PARENT_ITEM_ID", proOperData.getProductId());
            soRoleAttr.put("ITEM_ID", roleOperData.getRoleId());
            soRoleAttr.put("ITEM_NAME", roleOperData.getRoleName());
            soRoleAttr.put("ITEM_TYPE", roleOperData.getItemType());
            IData menCountLimtMap = new DataMap();
            menCountLimtMap.put("MAX_MEN_COUNT", roleOperData.getMaxNumber());
            menCountLimtMap.put("MIN_MEN_COUNT", roleOperData.getMinNumber());
            soRoleAttr.put("ORD_COUNT", menCountLimtMap.toString());
            StringBuilder rowSpeStatus = new StringBuilder("checked=true;displayed=false");
            if (StringUtils.equals("1", roleOperData.getIsMainRole()))
            {
                rowSpeStatus.append(";isMainRole=true");
            }
            else
            {
                rowSpeStatus.append(";isMainRole=false");
            }
            if (StringUtils.equals("1", roleOperData.getIsGroupRole()))
            {
                rowSpeStatus.append(";isMgrRole=true");
            }
            else
            {
                rowSpeStatus.append(";isMgrRole=false");
            }
            soRoleAttr.put("ROW_SPE_STATUS", rowSpeStatus.toString());
            soRoleAttr.put("CONTEXT_MENU", getRoleContextMenu(roleOperData, param));
            proAttrList.add(soRoleAttr);
            proAttrList.addAll(busiSoMemAttrBean(roleOperData, param));
        }
        return proAttrList;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2013-10-24 下午03:41:10 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-10-24 chengxf2 v1.0.0 修改原因
     */
    private List<ITreeNodeInfo> busiSoMemAttrBean(RoleOperData roleOperData, IData param) throws Exception
    {
        List<ITreeNodeInfo> proAttrList = new ArrayList<ITreeNodeInfo>();
        return proAttrList;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-4-11 上午09:15:06 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-11 chengxf2 v1.0.0 修改原因
     */
    private String getRoleContextMenu(RoleOperData roleOperData, IData param) throws Exception
    {
        IDataset contextMenuList = new DatasetList();
        IData contextMenu = new DataMap();
        String frameId = param.getString("FRAME_ID");
        String roleCode = roleOperData.getRoleId();
        IDataset roleBusiBtnList = BmFrameInfoQry.getRoleBusiBtnList(frameId, roleCode);
        for (int i = 0; i < roleBusiBtnList.size(); i++)
        {
            contextMenu = new DataMap();
            contextMenu.put("MENU_GROUP", roleCode);
            contextMenu.put("MENU_NAME", roleBusiBtnList.getData(i).getString("BUTTON_TEXT"));
            contextMenu.put("MENU_FN", roleBusiBtnList.getData(i).getString("EVENT_CLICK"));
            contextMenu.put("MENU_PARAM", buildMenuParam(roleOperData, param));
            contextMenuList.add(contextMenu);
        }
        return contextMenuList.toString();
    }

}

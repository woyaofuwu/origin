
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.bbossattrgroup;

import java.util.Iterator;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.bounddatainfo.BoundDataInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.changeuserelement.ProductInfo;

/*********************** 案例分析 START ******************************/
/*
 * 400呼叫阻截产品中属性组呼叫阻截省和呼叫阻截地市，其中呼叫阻截省为主属性，呼叫阻截地市为从属性，集团受理过程中用户录入了两组呼叫阻截信息[{湖南，长沙}
 * {湖北，武汉}]，getAttrInfoListByAttrCode中的参数bbossAttrInfo表示TD_S_BBOSS_ATTR呼叫阻截省的配置信息，attrInfoList表示主属性的值信息，
 * 本案例中表示呼叫阻截省的值信息[{湖南}{湖北}]，该方法返回的结果集中应该对应有4条记录，且该4条记录根据属性组组号由小到大的顺序可以分为两组，
 * 每组属性根据showIndex由小到大的顺序包含两条记录，其结果可以表示为[{湖南}{长沙}{湖北}{武汉}]
 */
/*********************** 案例分析 END *********************************/

/*
 * @description 该类主要用于放置BBOSS公用方法（例如VIEW层对属性组的处理）
 * @auhtor xunyl
 * @date 2013-11-12
 */
public abstract class GroupBBossUtilCommon extends ProductInfo
{
    /*
     * @description 初始参数处理(主要功能是过滤操作中不可见参数以及对可见、可视、可编辑性进行处理)
     * @author xunyl
     * @date 2013-11-17
     */
    protected void dealBBossAttr(String productOperType, IDataset bbossAttrInfoList) throws Exception
    {
        for (int i = 0; i < bbossAttrInfoList.size(); i++)
        {
            IData bbossAttrInfo = bbossAttrInfoList.getData(i);
            // 根据产品操作编号来判定属性是否可见
            String attrVisiable = bbossAttrInfo.getString("VISIABLE");
            bbossAttrInfo.put("VISIABLE", 1);// 默认为不可见
            if (!StringUtils.isBlank(attrVisiable))
            {
                String[] visiable = attrVisiable.split(",");
                for (int j = 0; j < visiable.length; j++)
                {
                    if (productOperType.equals(visiable[j]))
                    {
                        bbossAttrInfo.put("VISIABLE", 0);
                        break;
                    }
                }
            }
            if (!"0".equals(bbossAttrInfo.getString("VISIABLE", "")))
            {
                bbossAttrInfoList.remove(i);
                i--;
                continue;
            }

            // 根据产品操作编号来判定属性是否可编辑
            String attrReadOnly = bbossAttrInfo.getString("READONLY");
            bbossAttrInfo.put("READONLY", 1);// 默认为不可编辑
            if (!StringUtils.isBlank(attrReadOnly))
            {
                String[] readOnly = attrReadOnly.split(",");
                for (int j = 0; j < readOnly.length; j++)
                {
                    if (productOperType.equals(readOnly[j]))
                    {
                        bbossAttrInfo.put("READONLY", 0);
                        break;
                    }
                }
            }

            // 根据产品操作编号来判定属性是否必填
            String attrMandatory = bbossAttrInfo.getString("MANDATORY");
            bbossAttrInfo.put("MANDATORY", 1);// 默认为不必填
            if (!StringUtils.isBlank(attrMandatory))
            {
                String[] mandatory = attrMandatory.split(",");
                for (int j = 0; j < mandatory.length; j++)
                {
                    if (productOperType.equals(mandatory[j]))
                    {
                        bbossAttrInfo.put("MANDATORY", 0);
                        break;
                    }
                }
            }

            // 根据GROUPATTR的值判断该属性是不是属于主属性，属性组为0，主属性为1，非属性组为2
            String groupAttr = bbossAttrInfo.getString("GROUPATTR");
            if (groupAttr != null && groupAttr.contains("_") && bbossAttrInfo.getString("ATTR_CODE").equals(groupAttr.split("_")[0]))
            {
                bbossAttrInfo.put("GROUPATTRFLAG", 1);
                bbossAttrInfo.put("ATTR_GROUP", "1");
            }
            else if (groupAttr != null)
            {
                bbossAttrInfo.put("GROUPATTRFLAG", 0);
                bbossAttrInfo.put("ATTR_GROUP", "1");
            }
            else
            {
                bbossAttrInfo.put("GROUPATTRFLAG", 2);
            }

            // 判断属性类型是否为下拉列表类型
            if (("SELECTION").equals(bbossAttrInfo.getString("EDIT_TYPE")))
            {
                // 根据产品属性获取下拉列表的值
                String paramCode = bbossAttrInfo.getString("ATTR_CODE");
                // 参数值,用于填充下拉列表
                IDataset dsValues = querySeletionData(paramCode);
                bbossAttrInfo.put("VALUE_LIST", dsValues);
            }

            // 属性赋默认值
            String defaultValue = bbossAttrInfo.getString("DEFAULT_VALUE");
            if (!StringUtils.isBlank(defaultValue))
            {
                bbossAttrInfo.put("ATTR_VALUE", defaultValue);
            }

            // 属性赋初始值(典型场景:资料表没改属性，变更时需要新增该属性，但该属性有默认值，比较时就会当作资料表有值得情况处理)
            bbossAttrInfo.put("OLD_PARAM_VALUE", "");
        }
    }

    /**
     * @param 本地产品ID
     *            ，产品操作阶段（预受理、受理、管理流程节点等）
     * @desciption 获取对应操作阶段该产品的属性情况
     * @author fanti
     * @version 创建时间：2014年8月29日 下午10:12:13
     */
    public IDataset getAfterDealBbossAttrInfo(String productId, String productOperType) throws Exception
    {
        String productSpecCode = StaticUtil.getStaticValue(getVisit(), "TD_B_ATTR_BIZ", new String[]
        { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
        { "1", "B", productId, "PRO" });

        // 获取对应产品编码下TD_S_BBOSS_ATTR表集团级数据,并进行相关处理
        IData bbossAttrParam = new DataMap();
        bbossAttrParam.put("PRODUCTSPECNUMBER", productSpecCode);
        bbossAttrParam.put("BIZ_TYPE", "1"); // TD_S_BOSS_ATTR.biz_type 业务类型：1-集团业务， 2-成员业务

        IDataset bbossAttrInfoList = CSViewCall.call(this, "CS.BBossAttrQrySVC.qryBBossAttrByPospecBiztype", bbossAttrParam);

        if (IDataUtil.isEmpty(bbossAttrInfoList))
        {
            return new DatasetList();
        }
        else
        {
            // 对不同产品操作类型 属性是否可见、是否可编辑 进行处理
            this.dealBBossAttr(productOperType, bbossAttrInfoList);
        }

        return bbossAttrInfoList;
    }

    /*
     * @description 根据属性编号获取对应属性组下的全部属性
     * @auhtor xunyl
     * @date 2013-11-12
     */
    protected IDataset getAtrrGroupInfoList(String attrCode, String bizType) throws Exception
    {
        IData inparam = new DataMap();
        String groupAttrValue = attrCode + "_" + ProvinceUtil.getProvinceCodeGrpCorp();
        inparam.put("GROUP_ATTR", groupAttrValue);
        inparam.put("BIZ_TYPE", bizType);// 业务类型：1-集团业务，2-成员业务
        IDataset bbossAttrList = CSViewCall.call(this, "CS.BBossAttrQrySVC.qryBBossAttrByGroupAttrBizType", inparam);
        return bbossAttrList;
    }

    /*
     * @description 根据参数编号和组编号(ATTR_GROUP)查询参数值
     * @author xunyl
     * @date 2013-06-03
     */
    protected IData getAttrInfoByGroupCode(String paramCode, String attrGroup, IDataset attrInfoValueList) throws Exception
    {
        IData attrInfo = new DataMap();

        for (int i = 0; i < attrInfoValueList.size(); i++)
        {
            IData attrInfoValue = attrInfoValueList.getData(i);
            if (StringUtils.equals(paramCode, attrInfoValue.getString("ATTR_CODE")) && StringUtils.equals(attrGroup, attrInfoValue.getString("ATTR_GROUP")))
            {
                attrInfo.putAll(attrInfoValue);
                break;
            }
        }

        return attrInfo;
    }

    /**
     * @param
     * @desciption 对不同的bboss_stage阶段做不同的处理，获取产品的属性的值；通用的工具方法，使用弱类型参数，方便扩展
     * @author fanti
     * @version 创建时间：2014年8月29日 下午10:49:24
     */
    protected IDataset getAttrInfoLis(IData productParam, IData productGoodInfo) throws Exception
    {
        // 1- 定义参数值信息
        IDataset attrInfoList = new DatasetList();

        // BBOSS阶段码（1-缓存，2-集团受理，3-集团变更，4-预受理转正式受理，5-管理节点）
        String stage = productParam.getString("BBOSS_STAGE", "");
        // 产品ID
        String productId = productParam.getString("PRODUCT_ID", "");
        // 多个产品的情况，标识产品序列（product_productIndex）
        String productIndex = productParam.getString("PRODUCT_INDEX", "");
        // 产品用户ID
        String productUserId = productParam.getString("PRODUCT_USER_ID", "");
        // 产品TRADE_ID
        String tradeId = productParam.getString("TRADE_ID", "");

        // 2- 获取参数值信息
        // 缓存
        if (StringUtils.equals("1", stage))
        {
            attrInfoList = this.getAttrInfoListFromCache(productId, productIndex, productGoodInfo);
        }
        // 集团受理
        else if (StringUtils.equals("2", stage))
        {

        }
        // 集团变更
        else if (StringUtils.equals("3", stage))
        {
            attrInfoList = this.getAttrInfoListFromUserTab(productUserId);
        }
        // 预受理转正式受理
        else if (StringUtils.equals("4", stage))
        {
            attrInfoList = this.getAttrInfoListFromTradeTab(tradeId);
        }
        // 管理节点
        else if (StringUtils.equals("5", stage))
        {
            attrInfoList = this.getAttrInfoListFromAllTab(tradeId, productUserId);
        }

        // 3- 返回参数值信息
        return attrInfoList;
    }

    /*
     * @description 管理节点时，台帐表与资料表获取产品参数信息
     * @author xunyl
     * @date 2013-11-14
     */
    protected IDataset getAttrInfoListFromAllTab(String tradeId, String productUserId) throws Exception
    {
        // 1- 定义参数值信息
        IDataset attrInfoList = new DatasetList();

        // 2- 获取台帐信息
        IData inparam = new DataMap();
        inparam.put("TRADE_ID", tradeId);
        inparam.put("INST_TYPE", "P");
        IDataset tradeAttrInfoList = CSViewCall.call(this, "CS.TradeAttrInfoQrySVC.getTradeAttrInfoByInstType", inparam);

        // 3- 获取资料信息
        inparam.clear();
        inparam.put("USER_ID", productUserId);
        IDataset userAttrInfoList = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.queryUserAllAttrs", inparam);

        // 4- 如果台帐表与资料表都为空，抛出异常
        if (IDataUtil.isEmpty(tradeAttrInfoList) && IDataUtil.isEmpty(userAttrInfoList))
        {
            CSViewException.apperr(CrmUserException.CRM_USER_825);
        }

        // 5- 资料表与台帐表其中之一为空
        if (IDataUtil.isEmpty(tradeAttrInfoList))
        {
            attrInfoList.addAll(userAttrInfoList);
        }
        else if (IDataUtil.isEmpty(userAttrInfoList))
        {
            attrInfoList.addAll(tradeAttrInfoList);
        }

        // 6- 比较台帐表与资料表信息，获取最新的参数信息
        if (IDataUtil.isEmpty(attrInfoList))
        {
            attrInfoList = this.getNewestAttrInfoList(tradeAttrInfoList, userAttrInfoList);
        }
        if (IDataUtil.isNotEmpty(attrInfoList))
        {
            for (int i = 0; i < attrInfoList.size(); i++)
            {
                IData attrInfo = attrInfoList.getData(i);
                attrInfo.put("ATTR_GROUP", attrInfo.getString("RSRV_STR4"));
            }
        }

        // 7- 返回参数值信息
        return attrInfoList;
    }

    /*
     * @description 缓存中获取参数值信息
     * @author xunyl
     * @date 2013-11-14
     */
    protected IDataset getAttrInfoListFromCache(String productId, String index, IData productGoodInfo) throws Exception
    {
        IDataset attrInfoList = new DatasetList();

        String productIndex = productId + "_" + index;
        IData merchParamMap = productGoodInfo.getData("PRODUCT_PARAM");
        IData productParamsMap = merchParamMap.getData(productIndex);

        if (IDataUtil.isNotEmpty(productParamsMap))
        {
            Iterator<String> iterator = productParamsMap.keySet().iterator();
            while (iterator.hasNext())
            {
                String key = iterator.next();
                IData param = productParamsMap.getData(key);
                if (!StringUtils.equals("DEL", param.getString("STATE")))
                {
                    attrInfoList.add(param);
                }
            }
        }

        return attrInfoList;
    }
    
    /*
     * @descripiton 集团变更中，重复打开参数页面时，用于比较的老数据从资料表中获取
     * @author cheny
     * @date 2015-06-12
     */
    public  IDataset getOldAttrInfoListFromUserTab(String productUserId) throws Exception
    {
        IDataset attrInfoList = new DatasetList();

        IData inparam = new DataMap();
        inparam.put("USER_ID", productUserId);
        attrInfoList = CSViewCall.call(this,"CS.UserAttrInfoQrySVC.queryUserAllAttrs", inparam);

        if (IDataUtil.isNotEmpty(attrInfoList))
        {
            for (int i = 0; i < attrInfoList.size(); i++)
            {
                IData attrInfo = attrInfoList.getData(i);
                attrInfo.put("ATTR_GROUP", attrInfo.getString("RSRV_STR4"));
                attrInfo.put("ATTR_NAME", attrInfo.getString("RSRV_STR3"));
                attrInfo.put("OLD_PARAM_VALUE", attrInfo.getString("ATTR_VALUE"));
            }
        }

        return attrInfoList;
    }

    /*
     * @description 预受理转正式受理时，台帐表获取产品参数值信息
     * @author xunyl
     * @date 2013-11-14
     */
    protected IDataset getAttrInfoListFromTradeTab(String tradeId) throws Exception
    {
        IDataset attrInfoList = new DatasetList();

        IData inparam = new DataMap();
        inparam.put("TRADE_ID", tradeId);
        inparam.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        attrInfoList = CSViewCall.call(this, "CS.TradeAttrInfoQrySVC.getAttrByTradeID", inparam);

        if (IDataUtil.isNotEmpty(attrInfoList))
        {
            for (int i = 0; i < attrInfoList.size(); i++)
            {
                IData attrInfo = attrInfoList.getData(i);
                attrInfo.put("ATTR_GROUP", attrInfo.getString("RSRV_STR4"));
            }
        }

        return attrInfoList;
    }

    /*
     * @descripiton 集团变更中，资料表获取产品参数值信息
     * @author xunyl
     * @date 2013-11-14
     */
    protected IDataset getAttrInfoListFromUserTab(String productUserId) throws Exception
    {
        IDataset attrInfoList = new DatasetList();

        IData inparam = new DataMap();
        inparam.put("USER_ID", productUserId);
        attrInfoList = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.queryUserAllAttrs", inparam);

        if (IDataUtil.isNotEmpty(attrInfoList))
        {
            for (int i = 0; i < attrInfoList.size(); i++)
            {
                IData attrInfo = attrInfoList.getData(i);
                attrInfo.put("ATTR_GROUP", attrInfo.getString("RSRV_STR4"));
            }
        }

        return attrInfoList;
    }

    /*
     * @description 根据产品参数编号获取对应的产品参数值列表
     * @author xunyl
     * @date 2013-11-15
     */
    protected IDataset getAttrInfoValueListByAttrCode(String attrCode, IDataset attrInfoValueList) throws Exception
    {
        IDataset attrValueList = new DatasetList();

        for (int i = 0; i < attrInfoValueList.size(); i++)
        {
            IData attrInfoValue = attrInfoValueList.getData(i);
            if (StringUtils.equals(attrCode, attrInfoValue.getString("ATTR_CODE")))
            {
                // 查询bboss_attr表前后缀是否有，有的话需要把前后缀截取掉
                IData inparam = new DataMap();
                inparam.put("ATTR_CODE", attrCode);
                IDataset attrInfoDataset = CSViewCall.call(this, "CS.BBossAttrQrySVC.qryBBossAttrByAttrCode", inparam);
                if (IDataUtil.isNotEmpty(attrInfoDataset))
                {
                    IData attrInfoData = attrInfoDataset.getData(0);
                    String frontPart = attrInfoData.getString("FRONT_PART");
                    String afterPart = attrInfoData.getString("AFTER_PART");
                    String attrValue = attrInfoValue.getString("ATTR_VALUE");
                    if (StringUtils.isNotEmpty(frontPart))
                    {
                        attrValue = attrValue.substring(frontPart.length());
                    }
                    if (StringUtils.isNotEmpty(afterPart))
                    {
                        attrValue = attrValue.substring(0, attrValue.length() - afterPart.length());
                    }
                    attrInfoValue.put("ATTR_VALUE", attrValue);
                }

                attrValueList.add(attrInfoValue);
            }
        }
        this.sortDataset(attrValueList);

        return attrValueList;
    }

    /*
     * @description 产品参数赋值,获取最终用于显示的产品参数列表
     * @author xunyl
     * @date 2013-11-15
     */
    protected IDataset getLastAttrInfoList(String productOperType, IDataset bbossAttrInfoList, IDataset attrInfoValueList) throws Exception
    {
        // 1- 定义返回参数列表
        IDataset productAttrInfoList = new DatasetList();

        // 2- 根据产品参数编号获取对应的产品参数值列表
        for (int i = 0; i < bbossAttrInfoList.size(); i++)
        {
            IData bbossAttrInfo = bbossAttrInfoList.getData(i);
            String attrCode = bbossAttrInfo.getString("ATTR_CODE");

            IDataset attrValueList = this.getAttrInfoValueListByAttrCode(attrCode, attrInfoValueList);
            if (IDataUtil.isEmpty(attrValueList))
            {
                productAttrInfoList.add(bbossAttrInfo);
            }
            else if (attrValueList.size() == 1)
            {// 可能为属性组也可能为非属性组，但处理方式一样
                bbossAttrInfo.put("ATTR_VALUE", attrValueList.getData(0).getString("ATTR_VALUE", ""));
                bbossAttrInfo.put("OLD_PARAM_VALUE", attrValueList.getData(0).getString("ATTR_VALUE", ""));
                bbossAttrInfo.put("ATTR_GROUP", attrValueList.getData(0).getString("ATTR_GROUP", ""));// 属性组编号
                productAttrInfoList.add(bbossAttrInfo);
            }
            else
            {// 肯定为属性组
                IDataset initAttrGroupInfoList = this.getAtrrGroupInfoList(attrCode, "1");
                if (null == initAttrGroupInfoList || initAttrGroupInfoList.size() == 0)
                {
                    CSViewException.apperr(CrmUserException.CRM_USER_1161);
                }
                this.dealBBossAttr(productOperType, initAttrGroupInfoList);
                for (int j = 0; j < attrValueList.size(); j++)
                {
                    // 拼装主属性信息
                    String maxGroup = attrValueList.getData(attrValueList.size() - 1).getString("ATTR_GROUP");
                    IData mainAttrInfo = this.getProductAttrInfo(bbossAttrInfo, attrValueList.getData(j), maxGroup);
                    productAttrInfoList.add(mainAttrInfo);

                    // 拼装属性组的从属性信息
                    for (int k = 1; k < initAttrGroupInfoList.size(); k++)
                    {
                        IData initAttrGroupInfo = initAttrGroupInfoList.getData(k);
                        String otherAttrGroup = mainAttrInfo.getString("ATTR_GROUP");
                        String otherAttrCode = initAttrGroupInfo.getString("ATTR_CODE");
                        IData otherAttrValueInfo = this.getAttrInfoByGroupCode(otherAttrCode, otherAttrGroup, attrInfoValueList);
                        IData otherAttrInfo = this.getProductAttrInfo(initAttrGroupInfo, otherAttrValueInfo, null);
                        productAttrInfoList.add(otherAttrInfo);
                    }
                }
                i = i + (initAttrGroupInfoList.size() - 1);
            }
        }

        // 返回参数列表
        return productAttrInfoList;
    }

    /*
     * @description 管理节点中台帐表的值与资料表的值相互之间进行比比较，获取最新参数值
     * @author xunyl
     * @date 2013-11-14
     */
    protected IDataset getNewestAttrInfoList(IDataset tradeAttrInfoList, IDataset userAttrInfoList) throws Exception
    {
        IDataset attrInfoList = new DatasetList();

        for (int i = 0; i < tradeAttrInfoList.size(); i++)
        {
            IData tradeAttrInfo = tradeAttrInfoList.getData(i);
            for (int j = 0; j < userAttrInfoList.size(); j++)
            {
                // 用户资料表和台帐表同时出现，表明该参数已经做过修改，显示台帐表数据即可
                IData userAttrDInfo = userAttrInfoList.getData(j);
                if (StringUtils.equals(tradeAttrInfo.getString("ATTR_CODE"), userAttrDInfo.getString("ATTR_CODE")) && StringUtils.equals(tradeAttrInfo.getString("RSRV_STR4"), userAttrDInfo.getString("RSRV_STR4")))
                {
                    userAttrInfoList.remove(j);
                    j--;
                }
            }

            // 台帐表状态为DEL，则说明该参数已经被删除，页面无需显示
            String modifyTag = tradeAttrInfo.getString("MODIFY_TAG");
            if (StringUtils.equals(TRADE_MODIFY_TAG.DEL.getValue(), modifyTag))
            {
                tradeAttrInfoList.remove(i);
                i--;
            }
        }

        attrInfoList.addAll(tradeAttrInfoList);
        attrInfoList.addAll(userAttrInfoList);

        return attrInfoList;
    }

    /*
     * @descripiton 拼装属性(拼装完后的参数信息符合界面规则)
     * @author xunyl
     * @date 2013-11-13
     */
    protected IData getProductAttrInfo(IData bbossAttrInfo, IData attrValueInfo, String maxGroup) throws Exception
    {
        // 1- 定义属性对象
        IData mianAttr = new DataMap();
        mianAttr.putAll(bbossAttrInfo);

        // 2- 属性赋值
        mianAttr.put("ATTR_VALUE", attrValueInfo.getString("ATTR_VALUE", ""));

        // 3- 属性赋老值
        mianAttr.put("OLD_PARAM_VALUE", attrValueInfo.getString("ATTR_VALUE", ""));

        // 4- 属性名称赋值(多组属性时，属性名称、属性名称2、属性名称3...,ATTR_GROUP下标是从1开始的)
        String attrGroup = attrValueInfo.getString("ATTR_GROUP", "");
        if (!StringUtils.equals(attrGroup, "1"))
        {
            mianAttr.put("ATTR_NAME", mianAttr.getString("ATTR_NAME") + attrGroup);
        }

        // 5- 属性属性组编号赋值
        mianAttr.put("ATTR_GROUP", attrGroup);

        // 6- 属性最大属性组编号赋值
        if (!StringUtils.isBlank(maxGroup))
        {
            mianAttr.put("MAX_GROUP_NUM", maxGroup);
        }

        // 7- 返回属性对象
        return mianAttr;
    }

    /**
     * @param 产品参数
     *            、商品参数
     * @desciption 前台参数处理总入口,查询不同阶段的产品属性
     * @author fanti
     * @version 创建时间：2014年8月29日 下午9:06:14
     */
    public IDataset getProductPlusInfo(IData productParam, IData productGoodInfo) throws Exception
    {
        // 1- 初始话弹出页面属性集变量
        IDataset productPlusInfoList = new DatasetList();

        // 2- 获取对应产品编码下TD_S_BBOSS_ATTR表处理后的集团级数据,比如预受理阶段属性是否可见、是否可编辑,对应ATTR_CODE
        IDataset bbossAttrInfoList = getAfterDealBbossAttrInfo(productParam.getString("PRODUCT_ID", ""), productParam.getString("PRODUCT_OPER_TYPE", ""));

        // 3- 某些业务参数的特殊处理譬如集团客户一点支付业务的配合省反馈属性组的属性组编号要改为与配合省范围的属性组编号一致
        String tradeId = productParam.getString("TRADE_ID", "");
        modifyParamBySpecialBiz(tradeId,bbossAttrInfoList);
        
        // 4- 拼装参数值来源,对应ATTR_VALUE
        IDataset attrInfoValueList = this.getAttrInfoLis(productParam, productGoodInfo);

        // 5- 产品参数赋值(包括属性组拼装及参数赋值) ATTR_CODE = ATTR_VALUE
        productPlusInfoList = this.getLastAttrInfoList(productParam.getString("PRODUCT_OPER_TYPE", ""), bbossAttrInfoList, attrInfoValueList);
        
        // 6- 附件属性，添加附件对应的FILEID
        setFileIdforParam(productPlusInfoList);

        // 7- 返回产品参数对象
        return productPlusInfoList;
    }

    /*
     * @author:xunyl
     * @description:查询下拉列表的值
     * @2012-01-25
     */
    protected IDataset querySeletionData(String paramCode) throws Exception
    {
        return BoundDataInfoIntfViewUtil.qryBoundDataInfosByParamCode(this, paramCode);
    }

    /*
     * @description 将List对象按照ATTR_GROUP由小到大排序(冒泡法)
     * @author xunyl
     * @date 2013-06-05
     */
    protected void sortDataset(IDataset pValue) throws Exception
    {
        if (IDataUtil.isEmpty(pValue))
        {
            return;
        }

        // 循环需要排序的数据集，冒泡法调整数据集顺序
        for (int i = 0; i < pValue.size() - 1; i++)
        {
            if (StringUtils.isEmpty(pValue.getData(i).getString("ATTR_GROUP")))
            {
                return;
            }
            for (int j = i + 1; j < pValue.size(); j++)
            {
                if (pValue.getData(i).getInt("ATTR_GROUP") > pValue.getData(j).getInt("ATTR_GROUP"))
                {
                    IData tempData = pValue.getData(i);
                    pValue.set(i, pValue.getData(j));
                    pValue.set(j, tempData);
                }
            }
        }
    }
    
    /**
     * @descripiton 某些业务参数的特殊处理譬如集团客户一点支付业务的配合省反馈属性组的属性组编号要改为与配合省范围的属性组编号一致
     * @author xunyl
     * @date 2014-12-01
     */    
    private void modifyParamBySpecialBiz(String tradeId,IDataset bbossAttrInfoList)throws Exception{
        //1- 如果tradeId为空，表示非管理直接，直接退出
        if(StringUtils.isEmpty(tradeId)){
            return;
        }
        
        //2- 根据台帐编号查询merch表，如果该业务属于集团客户一点支付业务，则进行一点支付业务的特殊处理
        IData inparam = new DataMap();
        inparam.put("TRADE_ID", tradeId);
        IDataset tradeGrpMerchpInfoList = CSViewCall.call(this, "CS.TradeGrpMerchpInfoQrySVC.qryMerchpInfoByTradeId", inparam);
        if(IDataUtil.isEmpty(tradeGrpMerchpInfoList)){
            return;
        }
        IData tradeGrpMerchpInfo = tradeGrpMerchpInfoList.getData(0);
        String merchSpecCode = tradeGrpMerchpInfo.getString("MERCH_SPEC_CODE");
        if(StringUtils.equals(merchSpecCode, "010190002")){
            modifyOnePayMemParam(tradeId,bbossAttrInfoList);
        }
        
    }
    
    /**
     * @description 处理集团客户一点支付业务的初始化参数
     * @author xunyl
     * @date 2014-12-01
     */
    private void modifyOnePayMemParam(String tradeId,IDataset bbossAttrInfoList)throws Exception{
        //1- 查询TRADE_ATTR表，查询当前反馈省对应的属性组编号(999033719为配合省范围的属性编号)
        IData inparam = new DataMap();
        inparam.put("TRADE_ID", tradeId);
        inparam.put("ATTR_CODE", "999033719");
        IDataset tradeAttrInfoList =CSViewCall.call(this, "CS.TradeAttrInfoQrySVC.getTradeAttrByTradeIDandAttrCode", inparam);
        if(IDataUtil.isEmpty(tradeAttrInfoList)){
            return;
        }
        String attrGroup = "";
        for(int i=0;i<tradeAttrInfoList.size();i++){
            IData tradeAttrInfo = tradeAttrInfoList.getData(i);
            String attrValue = tradeAttrInfo.getString("ATTR_VALUE");
            if(StringUtils.equals(attrValue, BizEnv.getEnvString("crm.grpcorp.provincecode"))){
                attrGroup = tradeAttrInfo.getString("RSRV_STR4");
                break;
            }
        }
        
        //2- 循环参数列表，将对应的反馈明细参数的属性组编号初始化为当前反馈省对应的属性组编号
        for(int j=0;j<bbossAttrInfoList.size();j++){
            IData bbossAttrInfo = bbossAttrInfoList.getData(j);
            String attrCode = bbossAttrInfo.getString("ATTR_CODE");
            if(StringUtils.equals(attrCode, "999033720") || StringUtils.equals(attrCode, "999033721")
                    || StringUtils.equals(attrCode, "999033722") || StringUtils.equals(attrCode, "999033723")
                    || StringUtils.equals(attrCode, "999033724") || StringUtils.equals(attrCode, "999033725")
                    || StringUtils.equals(attrCode, "999033726") || StringUtils.equals(attrCode, "999033727")
                    || StringUtils.equals(attrCode, "999033728") || StringUtils.equals(attrCode, "999033729")
                    || StringUtils.equals(attrCode, "999033730") || StringUtils.equals(attrCode, "999033731")
                    || StringUtils.equals(attrCode, "999033732") || StringUtils.equals(attrCode, "999033733")
                    || StringUtils.equals(attrCode, "999033734") || StringUtils.equals(attrCode, "999033735")){
                bbossAttrInfo.put("ATTR_GROUP", attrGroup);
            }
        } 
    }
    
    /**
     * @description 添加附件属性的FILEID(附件总共有三类，一类入成员附件，该属性不带文件后缀，二类为前台导入文件，该文件以fileId+后缀命名，三类
     * 为后台落地文件，该文件以文件名+后缀命名)
     * @author xunyl
     * @date 2015-03-27
     */
    private void setFileIdforParam(IDataset productPlusInfoList)throws Exception{
        for(int i=0;i<productPlusInfoList.size();i++){
            IData productPlusInfo = productPlusInfoList.getData(i);
            String eidtType = productPlusInfo.getString("EDIT_TYPE");
            String attrCode = productPlusInfo.getString("ATTR_CODE");
            if(StringUtils.equals("999033717",attrCode) || StringUtils.equals("UPLOAD",eidtType)){
                String attrValue = productPlusInfo.getString("ATTR_VALUE");
                if(StringUtils.isBlank(attrValue)){
                    continue;
                }
                //一点支付的成员附件由IBOSS处理后带上了后缀.xls,因此这里要带上附件查询
                if(StringUtils.equals("999033717",attrCode)){
                    attrValue = attrValue+".xls";
                }
                //根据文件名称查找对应的fileId
                IData inParam = new DataMap();
                inParam.put("FILE_NAME",attrValue);
                IDataset fileInfoList = CSViewCall.call(this, "CS.MFileInfoQrySVC.qryFileInfoListByFileName", inParam);
                if(IDataUtil.isNotEmpty(fileInfoList)){
                    String fileId = fileInfoList.getData(0).getString("FILE_ID");
                    String realName = fileInfoList.getData(0).getString("FILE_NAME");
                    productPlusInfo.put("FILE_ID", fileId);
                    productPlusInfo.put("REAL_NAME", realName);
                    continue;
                }
                                                
                //前台主动上传的附件，附件名称已然被修改成fileId+后缀的形式（可以肯定后缀前的部分为int型）
                String[] fileNameList = attrValue.split("\\.");
                boolean isNum = fileNameList[0].matches("[0-9]+");
                if(isNum==false){
                    continue;
                }
                inParam.clear();
                inParam.put("FILE_ID", fileNameList[0]);
                fileInfoList = CSViewCall.call(this, "CS.MFileInfoQrySVC.qryFileInfoListByFileID", inParam);
                if(IDataUtil.isNotEmpty(fileInfoList)){
                    String fileId = fileInfoList.getData(0).getString("FILE_ID");
                    String realName = fileInfoList.getData(0).getString("FILE_NAME");
                    productPlusInfo.put("FILE_ID", fileId);
                    productPlusInfo.put("REAL_NAME", realName);
                    continue;
                }
            }           
        }
    }
}

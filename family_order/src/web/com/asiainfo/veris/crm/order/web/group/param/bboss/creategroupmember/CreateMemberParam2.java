
package com.asiainfo.veris.crm.order.web.group.param.bboss.creategroupmember;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userattrinfo.UserAttrInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userproductinfo.UserProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.bounddatainfo.BoundDataInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productmebinfo.ProductMebInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

/**
 * @author shixb
 */
public abstract class CreateMemberParam2 extends GroupBasePage
{

    // isReOpen作为标记用，true表示参数页面已经开启过，false表示参数页面初次加载
    boolean isReOpen = false;

    protected String productOperType;

    /**
     * @description 初始化全局变量(page类对应的全局变量一般不会自动销毁，需要重新服务器才能销毁)
     * @author chenkh
     * @date 2015-03-19
     */
    @Override
    public void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        isReOpen = false;
        productOperType = null;
    }
    
    /**
     * 处理BBOSS侧的参数数据，原因是前台需要的显示值和参数表中的配置值存在差异
     * 
     * @param plus
     * @throws Exception
     */
    private void dealBBossAttr(IData plus) throws Exception
    {

        // 根据产品操作编号来判定属性是否可见
        if (plus.getString("VISIABLE") != null)
        {
            String[] visiable = plus.getString("VISIABLE").split(",");
            plus.put("VISIABLE", 1);// 先默认不可见
            for (int j = 0; j < visiable.length; j++)
            {
                if (productOperType.equals(visiable[j]))
                {
                    plus.put("VISIABLE", 0);
                    break;
                }
            }
        }
        else
        {
            plus.put("VISIABLE", 1);// 默认为不可见
        }

        // 根据产品操作编号来判定属性是否可编辑
        if (plus.getString("READONLY") != null)
        {
            String[] readOnly = plus.getString("READONLY").split(",");

            plus.put("READONLY", 1);// 先默认不可读
            for (int j = 0; j < readOnly.length; j++)
            {
                if (productOperType.equals(readOnly[j]))
                {
                    plus.put("READONLY", 0);
                    break;
                }
            }
        }
        else
        {
            plus.put("READONLY", 1);// 默认为不可编辑
        }

        // 根据产品操作编号来判定属性是否必填
        if (plus.getString("MANDATORY") != null)
        {
            String[] mandatory = plus.getString("MANDATORY").split(",");
            plus.put("MANDATORY", 1);// 先默认不必填
            for (int j = 0; j < mandatory.length; j++)
            {
                if (productOperType.equals(mandatory[j]))
                {
                    plus.put("MANDATORY", 0);
                    break;
                }
            }
        }
        else
        {
            plus.put("MANDATORY", 1);// 默认为不必填
        }

        // 判断属性类型是否为下拉列表类型
        if (("SELECTION").equals(plus.getString("EDIT_TYPE")))
        {
            // 根据产品属性获取下拉列表的值
            String paramCode = plus.getString("ATTR_CODE");
            // 参数值,用于填充下拉列表
            IDataset dsValues = querySeletionData(paramCode);
            plus.put("VALUE_LIST", dsValues);
        }

        // 属性赋默认值
        String defaultValue = plus.getString("DEFAULT_VALUE");
        if (null != defaultValue && !"".equals(defaultValue))
        {
            plus.put("ATTR_VALUE", defaultValue);
        }
    }

    /**
     * 处理产品参数
     * 
     * @param cycle
     * @param productPlus
     * @return
     * @throws Exception
     */
    public IDataset dealProductAttr(IDataset productPlus) throws Exception
    {

        // 校验是否是新装操作，不是新装操作则为true
        boolean isExist = false;
        IDataset productAttrList = new DatasetList();// 赋值后的产品参数集,用于变更操作的情况
        if ("true".equals(getData().getString("IS_EXIST")))
        {
            isExist = true;
        }

        // 如果产品下面没有参数，则直接返回
        if (productPlus == null || productPlus.size() <= 0)
        {
            return productAttrList;
        }

        IDataset paramValueList = getUserAttrByUserIdProductId();
        for (int k = 0; k < paramValueList.size(); k++)
        {
            IData oldParam = paramValueList.getData(k);
            oldParam.put("ATTR_NAME", oldParam.getString("RSRV_STR3"));
            // 由于参数配置表的属性编号与实际入表的属性编号不一致，需要将用户表数据恢复成配置表数据
            StringBuilder attrCode = new StringBuilder("");
            attrCode.append(productPlus.getData(0).getString("PRODUCT_ID"));
            attrCode.append(oldParam.getString("ATTR_CODE"));
            oldParam.put("ATTR_CODE", attrCode.toString());
            oldParam.put("OLD_PARAM_VALUE", oldParam.getString("ATTR_VALUE", ""));
        }

        IDataset unVisiableList = new DatasetList();// 存储不可见属性
        // 有参数的情况，给参数赋值
        for (int i = 0; i < productPlus.size(); i++)
        {
            IData plus = productPlus.getData(i);
            // 处理BBOSS侧的参数数据，原因是前台需要的显示值和参数表中的配置值存在差异
            this.dealBBossAttr(plus);
            // 处理不可见的属性，如果不可见，则直接不拼这个参数

            if (!"0".equals(plus.getString("VISIABLE", "")))
            {
                unVisiableList.add(plus);
                continue;
            }

            // 判断该页面是否是再次打开，如果是再次打开则不直接从数据库读取参数值
            String key = CacheKey.getBossProductInfoKey(getVisit().getStaffId(), getParameter("MEM_USER_ID"));
            IData productGoodInfo = new DataMap(SharedCache.get(key).toString());
            String grpUserId = getData().getString("GRP_USER_ID");
            IData isReOpenMap = productGoodInfo.getData("IS_REOPEN");
            if (IDataUtil.isNotEmpty(isReOpenMap))
            {
                isReOpen = isReOpenMap.getBoolean(grpUserId);// 是否再次打开标志
            }
            if (isReOpen == true)
            {
                IDataset pValue = getAttrValueFromSavedData(productGoodInfo, plus.getString("ATTR_CODE"));
                if (null == pValue || pValue.isEmpty())// 如果查询的结果不存在，不做处理
                {
                    productAttrList.add(plus);
                }
                else
                {
                    plus.put("ATTR_VALUE", pValue.getData(0).getString("ATTR_VALUE", ""));
                    productAttrList.add(plus);
                }
                continue;
            }

            // 成员变更的情况，查出产品受理时添加的参数,设置参数值
            if (isExist == true)
            {
                this.setParamValue(plus, paramValueList);
            }
            productAttrList.add(plus);
        }

        // 过滤paramValueList里面不可显示的属性
        this.delUnVisiableInfo(paramValueList, unVisiableList);

        // 设置老值
        setOldMemberParams(paramValueList);
        return productAttrList;
    }

    /**
     * 过滤user_attr 值里面不需要显示的值
     * 
     * @param paramValueList
     * @param unVisiableList
     */
    private void delUnVisiableInfo(IDataset paramValueList, IDataset unVisiableList)
    {
        if (IDataUtil.isNotEmpty(paramValueList) && IDataUtil.isNotEmpty(unVisiableList))
        {

            for (int i = 0, sizeI = unVisiableList.size(); i < sizeI; i++)
            {

                IData plus = unVisiableList.getData(i);
                String attrCode = plus.getString("ATTR_CODE");

                for (int j = paramValueList.size(); j > 0; j--)
                {

                    IData valueData = paramValueList.getData(j - 1);
                    if (StringUtils.equals(attrCode, valueData.getString("ATTR_CODE")))
                    {

                        paramValueList.remove(j - 1);
                    }
                    ;

                }

            }
        }

    }

    /**
     * 如果页面是再次打开时，参数值数据从保存的对象中读取，不读表
     * 
     * @param productGoodInfo
     * @param paramCode
     * @return
     * @throws Exception
     */
    protected IDataset getAttrValueFromSavedData(IData productGoodInfo, String paramCode) throws Exception
    {

        // 1 获取productGoodInfo对象中的属性
        IData merchParamMap = productGoodInfo.getData("PRODUCT_PARAM");
        IData productParamsMap = merchParamMap.getData(getData().getString("GRP_USER_ID"));

        // 2 根据参数编号筛选属性状态为ADD的对象
        IDataset pValue = new DatasetList();
        Iterator<String> iterator = productParamsMap.keySet().iterator();
        while (iterator.hasNext())
        {
            String key = iterator.next();
            IData param = productParamsMap.getData(key);
            if (paramCode.equals(param.getString("ATTR_CODE")))
            {

                pValue.add(param);
            }
        }

        // 3 筛选结果按照CGOUP从小到大排序
        this.sortDataset(pValue);

        // 4 返回筛选结果
        return pValue;
    }

    /**
     * @description 设置成员的操作类型
     * @author weixb3
     * @version 创建时间：2013-6-28
     */
    protected IDataset getMebOperCode(String meb_status) throws Exception
    {

        String status = meb_status;
        IDataset mebOpers = new DatasetList();// 成员能够使用的操作类型

        if (status != null)
        {
            if ("N".equals(status))
            {
                // 如果是暂停状态

                IData oper = new DataMap();
                oper.put("OPER_CODE", "4");
                oper.put("OPER_NAME", "恢复");
                mebOpers.add(oper);
            }
            else
            {
                IDataset operTypeInfoList = AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(this, this.productId, "P", "0", "CHGMEMBEROPTYPE");
                if (IDataUtil.isEmpty(operTypeInfoList))
                {
                    return mebOpers;
                }

                String operTypelist = operTypeInfoList.getData(0).getString("ATTR_VALUE");
                String[] operTypeArr = operTypelist.split(",");
                for (int i = 0; i < operTypeArr.length; i++)
                {
                    IData oper = new DataMap();
                    String operType = operTypeArr[i];
                    String operName = getOperNameByOperType(operType);

                    oper.put("OPER_CODE", operType);
                    oper.put("OPER_NAME", operName);
                    mebOpers.add(oper);
                }
            }
        }
        return mebOpers;
    }

    /**
     * 根据操作code确定操作名
     * 
     * @param operType
     * @return
     * @author chenkh 2014年9月26日
     */
    public String getOperNameByOperType(String operType)
    {
        String operName = "";
        if ("0".equals(operType))
        {
            operName = "删除";
        }
        else if ("6".equals(operType))
        {
            operName = "变更成员扩展属性";
        }
        else if ("3".equals(operType))
        {
            operName = "暂停";
        }
        return operName;
    }

    /**
     * 获取成员类型
     * 
     * @param memUserId
     * @param grpUserId
     * @author xunyl
     * @date 2013-04-28
     * @return
     * @throws Exception
     */
    public String getMebType(String memUserId, String grpUserId) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("USER_ID", memUserId);
        inparams.put("GRP_USER_ID", grpUserId);
        inparams.put(Route.ROUTE_EPARCHY_CODE, getParameter("MEB_EPARCHY_CODE"));
        IDataset merchMebs = CSViewCall.call(this, "CS.UserGrpMerchInfoQrySVC.getSEL_BY_USERID_USERIDA", inparams);
        if (merchMebs != null && merchMebs.size() > 0)
        {
            return merchMebs.getData(0).getString("RSRV_TAG1");// 成员类型
        }
        return "";
    }

    /**
     * 查出成员新增时时添加的参数
     * 
     * @author shixb
     * @version 创建时间：2009-5-30 下午10:07:22
     */
    public IDataset getUserAttrByUserIdProductId() throws Exception
    {
        String userId = getParameter("MEM_USER_ID", "");
        if (!userId.equals(""))
        {
            IDataset Insts = getUserProductInst();
            if (IDataUtil.isEmpty(Insts))
            {
                return new DatasetList();
            }
            return UserAttrInfoIntfViewUtil.qryUserAttrInfosByUserIdAndInstTypeRelaInstId(this, userId, "P", Insts.getData(0).getString("INST_ID"), getData().getString("MEB_EPARCHY_CODE"));
        }
        return null;
    }

    /**
     * 查出产品的INST_ID
     * 
     * @author hud
     * @version
     */
    public IDataset getUserProductInst() throws Exception
    {
        String productId = getParameter("PRODUCT_ID");
        String userId = getParameter("MEM_USER_ID", "");
        String userIdA = getParameter("GRP_USER_ID");
        String routeEparchyCode = getParameter("MEB_EPARCHY_CODE");
        IDataset productBList = ProductMebInfoIntfViewUtil.qryProductMebInfosByProductId(this, productId);
        if (IDataUtil.isNotEmpty(productBList))
        {
            String productIdB = productBList.getData(0).getString("PRODUCT_ID_B", "");
            return UserProductInfoIntfViewUtil.qryUserProductInfsByUserIdAndUserIdAProductId(this, userId, userIdA, productIdB, routeEparchyCode);
        }
        return null;

    }

    public void initChgMb(IRequestCycle cycle) throws Exception
    {
        // 1- 获取成员操作类型(供参数显示用)
        productOperType = getParameter("PRODUCT_OPER_TYPE");

        // 2- 获取并设置产品编号
        this.productId = getParameter("PRODUCT_ID");
        setMemProductId(productId);

        // 3- 获取成员产品参数
        queryMemberParams(cycle);

        // 4- 设置成员产品元素信息
        initialChgDisParam(cycle);

        // 5- 获取并设置商产品信息
        String key = CacheKey.getBossProductInfoKey(getVisit().getStaffId(), getParameter("MEM_USER_ID"));
        IData productGoodInfos = new DataMap(SharedCache.get(key).toString());
        setProductGoodInfos(productGoodInfos);

        // 6- 获取并设置集团用户编号
        String grpUserId = getParameter("GRP_USER_ID");
        setUserId(grpUserId);

        // 7- 设置产品状态
        String productStatusCode = getData().getString("PRODUCT_STATUS_CODE");
        String productStatus = productStatusCode.equals("N") ? "暂停" : "正常";
        setProductStatus(productStatus);

        // 8- 设置成员操作类型
        IDataset memOptypes = getMebOperCode(productStatusCode);
        setMebOpers(memOptypes);

        // 9- 设置成员类型
        this.setChgMebTypes();
    }

    public void initCrtMb(IRequestCycle cycle) throws Exception
    {
        // 1- 获取成员操作类型(供参数显示用)
        productOperType = getParameter("PRODUCT_OPER_TYPE");

        // 2- 获取并设置产品编号
        this.productId = getParameter("PRODUCT_ID");
        setMemProductId(productId);

        // 3- 获取成员产品参数
        queryMemberParams(cycle);

        // 4- 设置成员产品元素信息
        initCrtMebDisParam(cycle);

        // 5- 获取并设置商产品信息
        String key = CacheKey.getBossProductInfoKey(getVisit().getStaffId(), getParameter("MEM_USER_ID"));
        IData productGoodInfos = new DataMap(SharedCache.get(key).toString());
        setProductGoodInfos(productGoodInfos);

        // 6- 获取并设置集团用户编号
        String grpUserId = getParameter("GRP_USER_ID");
        setUserId(grpUserId);

        // 7- 设置成员操作类型
        IData oper = new DataMap();
        oper.put("OPER_CODE", "1");
        oper.put("OPER_NAME", "新增");
        IDataset memOptypes = new DatasetList();
        memOptypes.add(oper);
        setMebOpers(memOptypes);

        // 8- 设置成员类型
        this.setCrtMebTypes();
    }

    /*
     * @description 成员新增初始化服务与资费服务参数
     * @author xunyl
     * @date 2013-08-09
     */
    protected void initCrtMebDisParam(IRequestCycle cycle) throws Exception
    {
        // 1- 设置缓存信息
        IData cond = this.setSavedElement(cycle);

        // 2- 设置产品用户编号
        cond.put("GRP_USER_ID", getParameter("GRP_USER_ID"));

        // 3- 设置产品编号
        cond.put("PRODUCT_ID", productId);

        // 4- 设置路由编号
        cond.put("EPARCHY_CODE", getParameter("MEB_EPARCHY_CODE"));

        // 5- 设置成员用户编号
        cond.put("MEB_USER_ID", getParameter("MEM_USER_ID"));

        // 6- 设置服务路由(供服务设置路由用)
        cond.put(Route.ROUTE_EPARCHY_CODE, getParameter("MEB_EPARCHY_CODE"));

        // 6- 设置用户路由(供设置路由用)
        cond.put(Route.USER_EPARCHY_CODE, getParameter("MEB_EPARCHY_CODE"));

        // 7- 设置生效时间(新增是默认为立即生效)
        cond.put("EFFECT_NOW", true);

        // 8- 添加服务与资费服务参数
        setCond(cond);
    }

    /**
     * 作用：产品信息的初始化
     * 
     * @author luojh 2009-07-29
     * @param cycle
     * @throws Exception
     */
    public void initialChgDisParam(IRequestCycle cycle) throws Exception
    {
        // 1- 设置缓存信息
        IData cond = this.setSavedElement(cycle);

        // 2- 设置产品用户编号
        cond.put("GRP_USER_ID", getParameter("GRP_USER_ID"));

        // 3- 设置产品编号
        cond.put("PRODUCT_ID", productId);

        // 4- 设置路由编号
        cond.put("EPARCHY_CODE", getParameter("MEB_EPARCHY_CODE"));

        // 5- 设置成员用户编号
        cond.put("MEB_USER_ID", getParameter("MEM_USER_ID"));

        // 6- 设置服务路由(供服务设置路由用)
        cond.put(Route.ROUTE_EPARCHY_CODE, getParameter("MEB_EPARCHY_CODE"));

        // 6- 设置用户路由
        cond.put(Route.USER_EPARCHY_CODE, getParameter("MEB_EPARCHY_CODE"));

        // 7- 设置用户编号(事实上就是成员用户编号,服务中重复定义)
        cond.put("USER_ID", getParameter("MEM_USER_ID"));

        // 8- 添加服务与资费服务参数
        setCond(cond);
    }

    /**
     * 查成员能够使用的通用参数
     * 
     * @author shixb
     * @version 创建时间：2009-5-11 下午04:35:25
     */
    public void queryMemberParams(IRequestCycle cycle) throws Exception
    {

        String productCode = getParameter("PRODUCT_ID");

        productCode = StaticUtil.getStaticValue(getVisit(), "TD_B_ATTR_BIZ", new String[]
        { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
        { "1", "B", productCode, "PRO" });

        IData inparam = new DataMap();

        inparam.put("BIZ_TYPE", "2");// 业务类型：1-集团业务，2-成员业务

        inparam.put("PRODUCTSPECNUMBER", productCode);
        // 查询产品属性信息
        IDataset mem_param = CSViewCall.call(this, "CS.BBossAttrQrySVC.qryBBossAttrByPospecBiztype", inparam);

        // 经过纠正后的数据集
        IDataset filterDs = new DatasetList();
        filterDs = dealProductAttr(mem_param);

        // 设置订购信息的属性
        setMemberParams(filterDs);
    }

    /**
     * 查询下拉列表的值
     * 
     * @param paramCode
     * @return
     * @throws Exception
     */
    public IDataset querySeletionData(String paramCode) throws Exception
    {
        return BoundDataInfoIntfViewUtil.qryBoundDataInfosByParamCode(this, paramCode);
    }

    /*
     * @descripiton 根据产品编号获取并设置新增成员变更中的成员类型
     * @author xunyl
     * @date 2013-08-09
     */
    protected void setChgMebTypes() throws Exception
    {
        String mebType = this.getMebType(getParameter("MEM_USER_ID"), getParameter("GRP_USER_ID"));

        // 设置成员类型IDATESET
        IDataset mebTypeSet = StaticUtil.getList(getVisit(), "TD_B_ATTR_BIZ", "ATTR_VALUE", "ATTR_NAME", new java.lang.String[]
        { "ID", "ID_TYPE", "ATTR_OBJ", "ATTR_CODE" }, new java.lang.String[]
        { "1", "B", "MTYPE", productId });
        if (mebTypeSet.size() == 0)
        {
            mebTypeSet = new DatasetList();
            IData temp = new DataMap();
            temp.put("ATTR_VALUE", "1");
            temp.put("ATTR_NAME", "签约关系");
            mebTypeSet.add(temp);
            mebType = "1";
        }
        setMebType(mebType);
        setMebTypeSet(mebTypeSet);
    }

    public abstract void setCond(IData info);

    /*
     * @descripiton 根据产品编号获取并设置新增成员操作中的成员类型
     * @authro xunyl
     * @date 2013-08-09
     */
    protected void setCrtMebTypes() throws Exception
    {
        IDataset mebTypeSet = StaticUtil.getList(getVisit(), "TD_B_ATTR_BIZ", "ATTR_VALUE", "ATTR_NAME", new java.lang.String[]
        { "ID", "ID_TYPE", "ATTR_OBJ", "ATTR_CODE" }, new java.lang.String[]
        { "1", "B", "MTYPE", productId });
        String mebType = "";
        if (mebTypeSet != null && mebTypeSet.size() == 1)
        {
            mebType = mebTypeSet.getData(0).getString("ATTR_VALUE");

        }
        else
        {
            mebTypeSet = new DatasetList();
            IData temp = new DataMap();
            temp.put("ATTR_VALUE", "1");
            temp.put("ATTR_NAME", "签约关系");
            mebTypeSet.add(temp);
            mebType = "1";

        }
        setMebType(mebType);
        setMebTypeSet(mebTypeSet);
    }

    public abstract void setMebOpers(IDataset mebOpers);// 设置成员操作类型

    public abstract void setMebType(String mebType);// 成员类型

    public abstract void setMebTypeSet(IDataset mebTypeName);// 成员类型名称

    public abstract void setMemberParams(IDataset memberParams);// 设置成员扩展属性

    public abstract void setMemProductId(String productId);// 产品编号

    public abstract void setOldMemberParams(IDataset oldMemberParams);// 设置成员扩展属性

    /*
     * @description 根据属性编号查询该参数是否已经订购，已定购则赋值
     * @author xunyl
     * @date 2013-08-09
     */
    protected void setParamValue(IData plus, IDataset paramValueList) throws Exception
    {
        // 1- 如果用户之前没有订购任何属性，则直接返回
        if (null == paramValueList || paramValueList.isEmpty())// 如果查询的结果不存在，不做处理
        {
            return;
        }

        // 2- 获取成员参数编号
        String attrCode = plus.getString("ATTR_CODE");

        // 3- 循环获取属性值
        for (int i = 0; i < paramValueList.size(); i++)
        {
            IData oldParam = paramValueList.getData(i);
            if (attrCode.equals(oldParam.getString("ATTR_CODE")))
            {
                plus.put("ATTR_VALUE", oldParam.getString("ATTR_VALUE"));
                return;
            }
        }
    }

    public abstract void setProductGoodInfos(IData productGoodInfos);// 商产品信息

    public abstract void setProductStatus(String productStatus);// 设置产品状态

    /*
     * @description 设置缓存元素信息
     * @author xunyl
     * @date 2013-08-09
     */
    protected IData setSavedElement(IRequestCycle cycle) throws Exception
    {
        // 1- 定义返回变量
        IData cond = new DataMap();

        // 2- 获取再次打开标志
        String key = CacheKey.getBossProductInfoKey(getVisit().getStaffId(), getParameter("MEM_USER_ID"));
        IData productGoodInfos = new DataMap(SharedCache.get(key).toString());
        String grpUserId = getParameter("GRP_USER_ID");
        boolean isReOpen = false;
        if (IDataUtil.isNotEmpty(productGoodInfos.getData("IS_REOPEN")))
        {
            isReOpen = productGoodInfos.getData("IS_REOPEN").getBoolean(grpUserId);// 是否再次打开标志
        }

        // 3- 如果该参数页面为再次打开，则设置缓存中的元素信息
        cond.put("IS_REOPEN", isReOpen);
        if (isReOpen == true)
        {
            IData tempselectedElements = productGoodInfos.getData("TEMP_PRODUCTS_ELEMENT");
            // 根据productIndex获取该产品对应的元素信息
            IDataset productElements = tempselectedElements.getDataset(productId);
            cond.put("TEMP_PRODUCTS_ELEMENT", productElements);
        }

        // 4- 返回结果
        return cond;
    }

    public abstract void setUserId(String userId);// 集团用户编号
    /**
     * 将List对象按照ATTR_GROUP由小到大排序(冒泡法)
     * 
     * @param pValue
     * @throws Exception
     */
    private void sortDataset(IDataset pValue) throws Exception
    {

        // 循环需要排序的数据集，冒泡法调整数据集顺序
        for (int i = 0; i < pValue.size() - 1; i++)
        {
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

}

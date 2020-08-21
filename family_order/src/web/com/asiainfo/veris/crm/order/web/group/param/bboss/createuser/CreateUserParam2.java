
package com.asiainfo.veris.crm.order.web.group.param.bboss.createuser;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.bbossattrgroup.GroupBBossUtilCommon;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userattrinfo.UserAttrInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.seqinfo.SeqMgrInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupEsopUtilView;

public abstract class CreateUserParam2 extends GroupBBossUtilCommon
{
    // 产品操作类型
    protected String productOperType = "";

    // BBOSS商品省BOSS内对应的产品ID
    String productIdA = "";

    // isReOpen作为标记用，true表示参数页面已经开启过，false表示参数页面初次加载
    boolean isReOpen = false;

    // 定义BBOSS商产品信息

    IData productGoodInfo = new DataMap();

    /**
     * @description 初始化全局变量(page类对应的全局变量一般不会自动销毁，需要重新服务器才能销毁)
     * @author xunyl
     * @date 2013-07-08
     */
    @Override
    public void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        isReOpen = false;
        productOperType = null;
        productIdA = "";
        productGoodInfo = new DataMap();
    }

    /**
     * @param
     * @desciption 集团受理弹出框页面初始化
     * @author fanti
     * @version 创建时间：2014年8月30日 上午9:50:36
     */
    public void initCrtUs(IRequestCycle cycle) throws Exception
    {
        // 1- 页面参数中获取商品编号，用户编号，产品操作类型,集团编号，产品下标

        productId = getData().getString("PRODUCT_ID");
        String userId = getData().getString("BBOSS_USER_ID");
        productOperType = getData().getString("producOperType");
        String groupId = getData().getString("GROUP_ID");
       
        //0-设置路由、业务类型
        String grpUserEparchycode = getData().getString("GRP_USER_EPARCHYCODE");
        setGrpUserEparchycode(grpUserEparchycode);
        
        String tradeTypeCode =  getData().getString("TRADE_TYPE_CODE");
        setTradeTypeCode(tradeTypeCode);
        
        // 2- 缓存中获取商产品信息(商产品信息过大，只能放在MC中传递)
        String key = CacheKey.getBossProductInfoKey(getVisit().getStaffId(), groupId);
        productGoodInfo = new DataMap(SharedCache.get(key).toString());

        // 3- 设置用户信息和商品编号
        setCurrentProductId(productId);
        if (!"".equals(userId))
        {
            setForcetag(getData().getString("FORCE_TAG"));
        }

        // 4- 设置服务与优惠组件参数
        IData svcDcnCond = this.getSvcDcnCond(userId);
        setCond(svcDcnCond);

        // 5- 设置产品序列
        IData condition = new DataMap();
        condition.put("PRODUCT_INDEX", getData().getString("PRODUCT_INDEX", ""));
        condition.put("BBOSS_USER_ID", getData().getString("BBOSS_USER_ID", ""));
        condition.put("STAFF_ID", this.getVisit().getStaffId());
        setCondition(condition);

        // 6- 设置产品属性
        qryProdPlus(cycle);

        // 7- 400保底消费控制
        init400BaseDiscntConfig(cycle);

        // 8- 获取并设置商产品信息
        setProductGoodInfos(productGoodInfo);

        // 9- 当前产品操作类型设置，产品受理为false,产品变更为true
        if (isReOpen == true)
        {
            setIsExist("true");
        }
        else
        {
            setIsExist("false");
        }

        // 10- 集团定制
        String useTag = ProductCompInfoIntfViewUtil.qryUseTagStrByProductId(this, productId);
        setUseTag(GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue().equals(useTag) ? "true" : "false");
        setGrpPackageInfoList();
    }

    /**
     * @param
     * @desciption 集团变更弹出框页面初始化
     * @author fanti
     * @version 创建时间：2014年8月30日 上午9:51:13
     */
    public void initChgUs(IRequestCycle cycle) throws Exception
    {
        // 1- 页面参数中获取商品编号，子产品编号，用户编号，产品操作类型,集团编号，产品下标

        productId = getData().getString("PRODUCT_ID");
        productIdA = getData().getString("PRODUCT_ID_A");
        String grpUserEparchycode = getData().getString("GRP_USER_EPARCHYCODE");
        String userId = getData().getString("BBOSS_USER_ID");
        productOperType = getData().getString("producOperType");
        String groupId = getData().getString("GROUP_ID");
        String index = getData().getString("PRODUCT_INDEX", "");

        setGrpUserEparchycode(grpUserEparchycode);

        // 2- 缓存中获取商产品信息(商产品信息过大，只能放在MC中传递)
        String key = CacheKey.getBossProductInfoKey(getVisit().getStaffId(), groupId);
        productGoodInfo = new DataMap(SharedCache.get(key).toString());

        // 3- 设置用户信息和商品编号
        setCurrentProductId(productId);
        if (!"".equals(getData().getString("BBOSS_USER_ID", "")))
        {
            setForcetag(getData().getString("FORCE_TAG"));
        }

        // 4- 设置服务与优惠组件参数
        IData svcDcnCond = this.getSvcDcnCond(userId);
        setCond(svcDcnCond);

        // 5- 设置产品属性
        qryProdPlus(cycle);

        // 6- 设置产品序列
        IData condition = new DataMap();
        condition.put("PRODUCT_INDEX", index);
        condition.put("BBOSS_USER_ID", userId);
        condition.put("STAFF_ID", this.getVisit().getStaffId());
        setCondition(condition);

        // 7- 400保底消费控制
        init400BaseDiscntConfig(cycle);
        // 8- 获取并设置商产品信息
        setProductGoodInfos(productGoodInfo);
        // 9- 当前产品操作类型设置，产品受理为false,产品变更为true
        setIsExist("true");

        // 10- 集团定制
        String useTag = ProductCompInfoIntfViewUtil.qryUseTagStrByProductId(this, productId);
        setUseTag(GroupBaseConst.GroupDesignFlag.GroupDesignYes.getValue().equals(useTag) ? "true" : "false");
        setGrpPackageInfoList();
    }

    /**
     * @description 设置服务于资费参数
     * @author xunyl
     * @date 2013-07-02
     */
    protected IData getSvcDcnCond(String userId) throws Exception
    {
        // 1- 定义服务与资费参数对象
        IData svcDcnCond = new DataMap();

        // 2- 添加产品操作编号
        svcDcnCond.put("PRODUCT_OPER_TYPE", productOperType);

        // 3- 添加产品编号
        svcDcnCond.put("PRODUCT_ID", productId);
        
        
        // 4- 添加用户编号
        svcDcnCond.put("USER_ID", userId);

        // 5- 添加路由编号

        svcDcnCond.put("EPARCHY_CODE", getTradeEparchyCode());
        svcDcnCond.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());

        // 5- 判断是否再次打开
        String index = getData().getString("PRODUCT_INDEX", "");
        String productIndex = productId + "_" + index;
        if (null != productGoodInfo.getData("IS_REOPEN"))
        {
            isReOpen = productGoodInfo.getData("IS_REOPEN").getBoolean(productIndex);// 是否再次打开标志
        }
        svcDcnCond.put("IS_REOPEN", isReOpen);

        // 6- 再次打开的场合添加缓存中的服务于资费信息
        if (isReOpen == true)
        {
            IData tempselectedElements = productGoodInfo.getData("TEMP_PRODUCTS_ELEMENT");
            // 变更场合下isReOpen即使为true也并不能表示加载过资费与服务信息(第一次打开的是产品属性修改页面)
            if (null != tempselectedElements)
            {
                IDataset productElements = tempselectedElements.getDataset(productIndex);
                svcDcnCond.put("TEMP_PRODUCTS_ELEMENT", productElements);
            }
            else
            {

                svcDcnCond.put("IS_REOPEN", false);
            }
        }

        // 7- 添加资费与服务的生失效时间
        svcDcnCond.put("EFFECT_NOW", true);// 新增是默认为立即生效

        // 8- 返回服务与资费对象

        return svcDcnCond;
    }

    /**
     * @desciption 设置弹出框的属性
     * @author fanti
     * @version 创建时间：2014年8月29日 下午8:45:57
     */
    public void qryProdPlus(IRequestCycle cycle) throws Exception
    {
        // 产品属性列表
        IDataset poProductPlusInfoList = new DatasetList();
        // 设置产品级入参
        IData productParam = new DataMap();

        String productId = getData().getString("PRODUCT_ID");// 产品ID
        String productIndex = getData().getString("PRODUCT_INDEX", ""); // 多个产品的情况，标识产品序列（product_productIndex）
        String productUserId = getData().getString("BBOSS_USER_ID", ""); // 产品用户ID

        if (isReOpen == true)
        {
            productParam.put("BBOSS_STAGE", "1"); // 阶段编码（1-缓存，2-集团受理，3-集团变更，4-预受理转正式受理，5-管理节点）
            productParam.put("PRODUCT_INDEX", productIndex);
            productParam.put("PRODUCT_ID", productId);
            productParam.put("PRODUCT_OPER_TYPE", productOperType);
            poProductPlusInfoList = getProductPlusInfo(productParam, productGoodInfo);
            //变更取old数据
            if("9".equals(productOperType)){
               IDataset popOldParam=getOldAttrInfoListFromUserTab(productUserId);
               setParamOldValue(popOldParam);
            }      
        }
        else if ("true".equals(getData().getString("IS_EXIST")) && !"".equals(getData().getString("BBOSS_USER_ID", "")))
        {
            productParam.put("BBOSS_STAGE", "3"); // 阶段编码（1-缓存，2-集团受理，3-集团变更，4-预受理转正式受理，5-管理节点）
            productParam.put("PRODUCT_USER_ID", productUserId);
            productParam.put("PRODUCT_ID", productId);
            productParam.put("PRODUCT_OPER_TYPE", productOperType);
            poProductPlusInfoList = getProductPlusInfo(productParam, productGoodInfo);
            //保存未变更前的old值
            setParamOldValue(poProductPlusInfoList);
        }
        else
        {
            productParam.put("BBOSS_STAGE", "2"); // 阶段编码（1-缓存，2-集团受理，3-集团变更，4-预受理转正式受理，5-管理节点）
            productParam.put("PRODUCT_ID", productId);
            productParam.put("PRODUCT_OPER_TYPE", productOperType);
            poProductPlusInfoList = getProductPlusInfo(productParam, productGoodInfo);
            //保存未变更前的old值
            setParamOldValue(poProductPlusInfoList);
        }

        // 查询并添加ESOP录入的属性值
        qryEsopParams(poProductPlusInfoList);

        // 设置订购信息的属性
        setPOProductPlus(poProductPlusInfoList);

        // 设置产品操作类型
        setProductOperType(productOperType);
    }

    /**
     * @description 查询并添加ESOP录入的属性值
     * @author xunyl
     * @date 2013-09-22
     */
    protected void qryEsopParams(IDataset filterDs) throws Exception
    {
        // 1- 获取ESOP侧录入的参数
        IData inparams = new DataMap();
        inparams.put("OPER_CODE", "11");

        // 缓存取EOS数据
        String key = CacheKey.getBBossESOPInfoKey(getVisit().getStaffId(), "EOS_" + getData().getString("GROUP_ID"));

        Object eosObject = SharedCache.get(key);

        if (eosObject != null)
        {
            String eosString = eosObject.toString();

            if (StringUtils.isNotEmpty(eosString) && !"{}".equals(eosString))
            {
                IDataset eoss = new DatasetList(eosString);

                if (null != eoss && !IDataUtil.isEmpty(eoss))
                {
                    IData eos = eoss.getData(0);
                    inparams.put("IBSYSID", eos.getString("IBSYSID"));
                    inparams.put("SUB_IBSYSID", eos.getString("SUB_IBSYSID"));
                    inparams.put("NODE_ID", eos.getString("NODE_ID"));
                    inparams.put("EOS", eoss);
                    inparams.put("BRAND_CODE", "BOSG");
                }

            }
        }

        String rowIndex = getData().getString("rowIndex");
        if (rowIndex != null && !rowIndex.equals(""))
        {
            inparams.put("index", Integer.parseInt(rowIndex) - 1);
        }
        IData esopParams = GroupEsopUtilView.getEsopParams(this, inparams, true);

        // 2- CRM侧产品参数赋值（服务与资费需要重新录入）
        if (esopParams != null && !esopParams.isEmpty() && filterDs != null && filterDs.size() > 0)
        {
            String paramCode = "";
            for (int i = 0; i < filterDs.size(); i++)
            {
                IData productAttrInfo = filterDs.getData(i);
                paramCode = productAttrInfo.getString("ATTR_CODE", "");
                String attrGroup = productAttrInfo.getString("GROUPATTR", "");
                Iterator iterator = esopParams.keySet().iterator();
                String tempKey = "";
                while (iterator.hasNext())
                {
                    tempKey = (String) iterator.next();
                    if (!"".equals(paramCode) && tempKey.contains(paramCode))
                    {
                        String esopParamValue = esopParams.getString(tempKey, "");
                        if (null == attrGroup || ("").equals(attrGroup))
                        {// 非属性组直接赋值
                            productAttrInfo.put("ATTR_VALUE", esopParamValue);
                            productAttrInfo.put("PARA_CODE14", "1");// 锁定, （应端到端要求不锁定，因为他们的字段很多没有校验）
                        }
                        else
                        {// 属性组属性，需要拆分工单
                            filterDs.remove(i);
                            String[] paramValueList = esopParamValue.split(";");
                            for (int j = 0; j < paramValueList.length; j++)
                            {
                                IData cgroupParamInfo = new DataMap();
                                cgroupParamInfo.putAll(productAttrInfo);
                                cgroupParamInfo.put("ATTR_VALUE", paramValueList[j]);
                                cgroupParamInfo.put("ATTR_GROUP", j + 1);
                                cgroupParamInfo.put("PARA_CODE14", "1");// 锁定,（应端到端要求不锁定，因为他们的字段很多没有校验）
                                filterDs.add(i + j, cgroupParamInfo);
                            }
                            i = i + (paramValueList.length - 1);
                        }
                        esopParams.remove(paramCode);
                    }
                }
            }
        }
    }

    /**
     * @description 设置定制信息
     * @author xunyl
     * @date 2014-07-24
     */
    public void setGrpPackageInfoList() throws Exception
    {
        IData grpPackageInfo = productGoodInfo.getData("GRP_PACKAGE_INFO");
        if (IDataUtil.isEmpty(grpPackageInfo))
        {
            return;
        }
        String productId = getData().getString("PRODUCT_ID");
        String productIndex = getData().getString("PRODUCT_INDEX", "");
        String productGrpPackageInfoStr = grpPackageInfo.getString(productId + "_" + productIndex);
        if (StringUtils.isBlank(productGrpPackageInfoStr))
        {
            return;
        }
        IDataset productGrpPackageInfoList = new DatasetList(productGrpPackageInfoStr);
        setCacheGrpPackageInfoList(productGrpPackageInfoList);
    }

    /**
     * @description 根据全网产品编号获取省内产品编号(initParam.js中用到)
     * @author xunyl
     * @param 2014-06-26
     */
    public void getProProductId(IRequestCycle cycle) throws Throwable
    {
        // 1- 获取全网产品编号
        String allnetProductId = getData().getString("ALLNET_PRODUCT_ID");

        // 2- 根据全网产品编号获取省内产品编号
        String proProductId = StaticUtil.getStaticValue(getVisit(), "TD_B_ATTR_BIZ", new String[]
        { "ID", "ID_TYPE", "ATTR_VALUE", "ATTR_OBJ" }, "ATTR_CODE", new String[]
        { "1", "B", allnetProductId, "PRO" });

        // 3- 返回省内产品编号
        IData idata = new DataMap();
        idata.put("result", proProductId);
        setAjax(idata);
    }

    /**
     * @description 根据400语音产品的用户编号和400号码的属性编号获取400号码值(initParam.js中用到)
     * @author xunyl
     * @date 2014-06-27
     */
    public void get400NumByUserIdAttrCode(IRequestCycle cycle) throws Throwable
    {
        // 1- 获取400语音产品的用户编号
        String userId = getData().getString("USER_ID");

        // 2- 获取400号码的属性编号
        String attrCode = getData().getString("ATTR_CODE");

        // 3- 调用服务获取400号码参数信息
        IDataset paramInfoList = UserAttrInfoIntfViewUtil.qryGrpProductAttrInfosByUserIdAndInstTypeAndAttrCode(this, userId, "P", attrCode);

        // 4- 获取400号码
        String attrValue = "";
        if (IDataUtil.isNotEmpty(paramInfoList))
        {
            attrValue = paramInfoList.getData(0).getString("ATTR_VALUE");
        }

        // 5-返回400号码
        IData idata = new DataMap();
        idata.put("result", attrValue);
        setAjax(idata);
    }
    
    /*
     * 根据MAS 基本接入号属性，查询基本接入号列表
     */
    public void getMasEcCodeListByA(IRequestCycle cycle) throws Throwable
    {
        String strCustId = "";
        String grpCustInfoValue = "";
        String ecInCodeValue = "";
        IData data = getData();
        String strCodeA = data.getString("EC_BASE_IN_CODE_A");
        if (!strCodeA.equals("02"))
            strCodeA = "01";
        String strGroupId = data.getString("GROUP_ID");

        String bizTypeCode = data.getString("BIZ_TYPE_CODE");
        IData param = new DataMap();
        // 根据group_id获取cust_id
        IData result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, strGroupId);
        if (IDataUtil.isNotEmpty(result))
        {
            strCustId = result.getString("CUST_ID");
            if ("002".equals(bizTypeCode))
            {
                grpCustInfoValue = result.getString("RSRV_STR8");
            }
            else
            {
                grpCustInfoValue = result.getString("RSRV_STR3");

            }
        }

        param.put("CUST_ID", strCustId);
        param.put("BNUM_KIND_NEW", strCodeA);
        if ("002".equals(bizTypeCode))
        {
            param.put("EXTEND_TAG", "BNUMBMMS");
        }
        else
        {
            param.put("EXTEND_TAG", "BNUMB");
        }

        IDataset EcInCode = CSViewCall.call(this, "CS.GrpExtInfoQrySVC.getEcInCodeListByECA", param);

        if (IDataUtil.isNotEmpty(EcInCode))
        {
            ecInCodeValue = EcInCode.getData(0).getString("RSRV_STR1");

        }
        IData idata = new DataMap();
        idata.put("EC_BASE_IN_CODE", ecInCodeValue);
        idata.put("SP_CODE", grpCustInfoValue);
        idata.put("EC_BASE_IN_CODE_A", strCodeA);
        setAjax(idata);
    }

    /**
     * 查询400保底消费控制配置信息
     */
    public void init400BaseDiscntConfig(IRequestCycle cycle) throws Exception
    {
        IDataset configs = AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdAndIdTypeAttrObj(this, "0", "D", "400");
        if (IDataUtil.isNotEmpty(configs))
        {
            DataHelper.sort(configs, "ATTR_CODE", 1);

            StringBuilder buf = new StringBuilder("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
            buf.append("<ROWS>");
            for (int i = 0; i < configs.size(); i++)
            {
                IData c = configs.getData(i);
                buf.append("<ROW>");
                buf.append("<SEQ>" + c.getString("ATTR_CODE") + "</SEQ>");
                buf.append("<RULENAME>" + c.getString("ATTR_NAME") + "</RULENAME>");
                buf.append("<REGEX><![CDATA[" + c.getString("ATTR_VALUE") + "]]></REGEX>");
                buf.append("<DISCNTDESC>" + c.getString("REMARK") + "</DISCNTDESC>");
                buf.append("<DISCNTCODE>" + c.getString("RSRV_STR1") + "</DISCNTCODE>");
                buf.append("</ROW>");
            }
            buf.append("</ROWS>");
        }
    }
    
    /**
     * @description 获取一点支付业务成员附件等参数导入的批次号
     * @author xunyl
     * @date 2015-10-28
     */
    public void getBatchId(IRequestCycle cycle)throws Exception{
        //1- 序列生成批次号
        String batTaskId = SeqMgrInfoIntfViewUtil.qryBatchId(this,getTradeEparchyCode());
        
        //2- 返回序列号
        IData idata = new DataMap();
        idata.put("result", batTaskId);
        setAjax(idata);
    }
    
    /**
     * @description 获取一点支付业务成员附件的参数生成的文件名称
     * @param xunyl
     * @date 2015-10-28
     */
    public void getFileName(IRequestCycle cycle)throws Exception{
        //1- 获取批次号
        String batchTaskId= getData().getString("BATCH_TASK_ID");
        
        //2- 根据批次号获取文件名称
        String key = CacheKey.getBossBatchInfoKey(batchTaskId);
        String fileName = SharedCache.get(key).toString();
        
        //3- 返回文件名
        IData idata = new DataMap();
        idata.put("result", fileName);
        setAjax(idata);
    }
    
    /**
     * @description 根据省行业网关云MAS短流程产品的服务代码查询TF_F_USER_GRP_PLATSVC、TF_B_TRADE_GRP_PLATSVC
     * 表中是否已经存在(initParam.js中用到)
     * @author songxw
     * @date 2017-11-13
     */
    public void getMasServCodeByParam(IRequestCycle cycle) throws Throwable
    {
        String servCode = getData().getString("SERV_CODE");

        // 调用服务获取云MAS短流程服务代码信息
        IData param = new DataMap();
        param.put("SERV_CODE", servCode);
        IDataset servCodeList = CSViewCall.call(this, "CS.UserGrpInfoQrySVC.getServCodeByajax", param);
        IData idata = new DataMap();
        if (IDataUtil.isNotEmpty(servCodeList)){
        	 idata.put("result", "1");
        }else{
        	idata.put("result", "0");
        }

        setAjax(idata);
    }
    
    public abstract void setTradeTypeCode(String tradeTypeCode);// 保存业务类型
    
    // 商产品信息
    public abstract void setProductGoodInfos(IData productGoodInfos);

    // 当前产品信息
    public abstract void setCurrentProductId(String productId);

    // 产品的参数信息
    public abstract void setPOProductPlus(IDataset pOProductPlus);

    // 当前操作类型，产品受理为false,产品变更为true

    public abstract void setIsExist(String isExist);

    public abstract void setForcetag(String na);
    
    public abstract void setParamOldValue(IDataset pOProductPlus);

    public abstract void setGrpUserEparchycode(String grpUserEparchycode);

    public abstract void setInfo(IData info);

    public abstract void setProductOperType(String productOperType);

    public abstract void setUseTag(String use_tag);

    public abstract void setCacheGrpPackageInfoList(IDataset cacheGrpPackageInfoList);

    public abstract void setCondition(IData condition);

    public abstract IData getInfo();

}

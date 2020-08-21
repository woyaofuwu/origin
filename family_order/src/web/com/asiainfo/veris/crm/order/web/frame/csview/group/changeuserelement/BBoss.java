
package com.asiainfo.veris.crm.order.web.frame.csview.group.changeuserelement;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationbbinfo.RelationBBInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userattrinfo.UserAttrInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.usergrpmerchinfo.UserGrpMerchInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcomprelainfo.ProductCompRelaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupEsopUtilView;

public abstract class BBoss extends GroupBasePage
{
    IData productGoodInfos = new DataMap();// 产商产品信息

    /*
     * @description 初始化全局变量(page类对应的全局变量一般不会自动销毁，需要重新服务器才能销毁)
     * @author xunyl
     * @date 2013-07-08
     */
    public void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        productGoodInfos = new DataMap();// 产商产品信息
    }

    /*
     * @description 获取ESOP接口的商品操作类型
     * @author xunyl
     * @date 2013-10-08
     */
    protected String getMerchOpTypeFromEsop(IData esopData) throws Exception
    {
        // 1- 定义商品操作类型
        String opType = "";

        // 2- 获取产品操作类型(如果产品操作类型超过一种则抛出异常)
        IData productGoodInfo = esopData.getData("PRODUCT_GOOD_INFO");
        IDataset productInfoList = productGoodInfo.getDataset("PRODUCT_INFO");
        if (IDataUtil.isEmpty(productInfoList))
        {
            CSViewException.apperr(GrpException.CRM_GRP_93);
        }
        String productOpType = "";
        for (int i = 0; i < productInfoList.size(); i++)
        {
            IData productInfo = productInfoList.getData(i);
            if (StringUtils.isEmpty(productOpType))
            {
                productOpType = productInfo.getString("PRODUCT_OPER_CODE");
            }
            else if (!productOpType.equals(productInfo.getString("PRODUCT_OPER_CODE")))
            {
                CSViewException.apperr(CrmUserException.CRM_USER_785);
            }
        }

        // 3- 根据产品操作类型获取商品操作类型(产品新增、产品删除、参数变更和资费变更、取消商品订购)
        // 如果ESOP发起注销操作，则有可能为注销，也有可能为预注销，根据产品进行区分
        if ("cancelAccept".equals(esopData.getString("NODE_ID")) || "bossCancel".equals(esopData.getString("NODE_ID")))
        {
            if ("22000248".equals(productId))
            {// 400商品只能进行预取消操作
                opType = GroupBaseConst.MERCH_STATUS.MERCH_PREDESTORY.getValue();
            }
            else
            {
                opType = GroupBaseConst.MERCH_STATUS.MERCH_CANCLE.getValue();
            }
        }
        else
        {
            if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue().equals(productOpType) || GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLE.getValue().equals(productOpType))
            {
                opType = GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_GROUP.getValue();
            }
            else
            {// 需要根据参数来判断是产品参数变更还是资费变更
                IData paramInfo = esopData.getData("PRODUCT_PARAM");
                IData elementInfo = esopData.getData("PRODUCTS_ELEMENT");
                // 资费变更的场合
                if (IDataUtil.isEmpty(paramInfo) && !IDataUtil.isEmpty(elementInfo))
                {
                    opType = GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_DISCNT.getValue();
                }
                else if (!IDataUtil.isEmpty(paramInfo))
                {// 产品参数变更的场合
                    opType = GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue();
                }
            }
        }

        // 4- 返回商品操作类型
        return opType;
    }

    /**
     * @description 根据商品操作类型编码获取商品操作类型名称
     * @author xunyl
     * @date 2014-07-10
     */
    private String getOperNameByOperType(String operType) throws Exception
    {
        String operTypeName = "";
        if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_GROUP.getValue().equals(operType))
        {
            operTypeName = "修改订购商品组成关系";
        }
        else if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_DISCNT.getValue().equals(operType))
        {
            operTypeName = "修改商品资费";
        }
        else if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_LOCALDISCNT.getValue().equals(operType))
        {
            operTypeName = "修改商品本地资费";
        }
        else if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue().equals(operType))
        {
            operTypeName = "修改商品属性";
        }
        else if (GroupBaseConst.MERCH_STATUS.MERCH_PASTE.getValue().equals(operType))
        {
            operTypeName = "商品暂停";
        }
        else if (GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE.getValue().equals(operType))
        {
            operTypeName = "商品恢复";
        }
        else if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_MEB.getValue().equals(operType))
        {
            operTypeName = "变更成员";
        }
        else if (GroupBaseConst.MERCH_STATUS.MERCH_PREDESTORY.getValue().equals(operType))
        {
            operTypeName = "预取消商品订购";
        }
        else if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PROV.getValue().equals(operType))
        {
            operTypeName = "业务开展省新增或删除";
        }else if("21".equals(operType)){
            operTypeName = "合同变更";
        }
        
        return operTypeName;
    }

    /**
     * 查出产品受理时添加的参数
     * 
     * @author shixb
     * @version 创建时间：2009-7-8 下午10:07:22
     */
    public IDataset getUserAttrByUserIdInstId(String userId, String productId) throws Exception
    {
        // params.put("INST_ID", productCode); 替换方法时，发现用的productId做为RELA_INST_ID ，记录下来，留给开发人员确认
        return UserAttrInfoIntfViewUtil.qryGrpUserAttrInfosByUserIdAndInstTypeRelaInstId(this, userId, "P", productId);
    }

    /**
     * @author shixb
     * @version 创建时间：2009-8-13 下午08:43:06
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        // 1- 处理ESOP的数据集
        String eos = getData().getString("EOS");
        String ibSysId = "";
        if (!StringUtils.isEmpty(eos) && !"{}".equals(eos))
        {
            IDataset eosInfoList = new DatasetList(eos);
            if (!eosInfoList.isEmpty())
            {
                IData eosInfo = eosInfoList.getData(0);
                ibSysId = eosInfo.getString("IBSYSID", "");
            }
        }

        // 2- 获取产品编号
        this.productId = getData().getString("GRP_PRODUCT_ID", "");

        // 3- 设置集团编号和集团用户编号,传递给下一页面使用
        String groupId = getData().getString("GROUP_ID");
        setGroupId(groupId);
        String strUserID = getData().getString("GRP_USER_ID");
        setGrpUserId(strUserID);

        // 4- 设置商品状态和产品状态信息
        IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, strUserID);

        String temp = userInfo.getString("RSRV_STR5", "A");
        String status = "A".equals(temp) ? "正常" : "D".equals(temp) ? "预销" : "暂停";
        setGoodsStatus(status);
        initOperTypesChg(status);

        // 5- 设置产品控制信息
        IData productCtrlInfo = AttrBizInfoIntfViewUtil.qryChgUsProductCtrlInfoByProductId(this, productId);
        IData goodInfo = new DataMap();// 商品信息
        goodInfo.put("BASE_PRODUCT", productId);
        productGoodInfos.put("GOOD_INFO", goodInfo);
        productGoodInfos.put("PRODUCT_CTRL_INFO", productCtrlInfo);
        
        queryUserProducts(cycle);
        
        setProductGoodInfos(productGoodInfos);

        // 6- 设置集团虚拟号码,传递给下一页面使用
        String grpSn = getData().getString("GRP_SN");
        setGrpSn(grpSn);

        // 7- 设置业务保障等级
        IData result = UserGrpMerchInfoIntfViewUtil.qryUserGrpMerchInfoByUserId(this, strUserID);
        IData condi = new DataMap();
        condi.put("BUS_NEED_DEGREE", result.getString("RSRV_STR5"));
        setCondition(condi);

        // 8- 处理ESOP数据
        if (!"".equals(ibSysId))
        {
            // initialEosp(cycle);
        }
        
        //9 - 设置组合包
        String offerName = UpcViewCall.queryOfferNameByOfferId(this, "P", productId);
        setOfferName(offerName);
        
        //5.1-设置业务类型
        String tradeTypeCode = productCtrlInfo.getData("TradeTypeCode").getString("ATTR_VALUE");
        setTradeTypeCode(tradeTypeCode);
    }

    /*
     * @description ESOP页面初始化
     * @author xunyl
     * @date 2013-10-08
     */
    protected void initialEosp(IRequestCycle cycle) throws Exception
    {
        // 1- 获取ESOP数据
        String eos = getData().getString("EOS");
        IDataset eosInfoList = new DatasetList(eos);
        setEOS(eosInfoList);
        IData eosInfo = eosInfoList.getData(0);

        IData inData = new DataMap();
        inData.put("X_TRANS_CODE", "ITF_EOS_QcsGrpBusi");
        inData.put("X_SUBTRANS_CODE", "GetEosInfo");
        inData.put("NODE_ID", getData().getString("NODE_ID", "busiChange"));
        inData.put("IBSYSID", getData().getString("IBSYSID", ""));
        inData.put("SUB_IBSYSID", getData().getString("SUB_IBSYSID", ""));
        inData.put("OPER_CODE", "11");
        IData httpResult = new DataMap();
        httpResult = CSViewCall.callHttp(this, "ITF_EOS_QcsGrpBusi", inData);
        if (!"0".equals(httpResult.getString("X_RSPCODE")))
        {
            CSViewException.apperr(GrpException.CRM_GRP_508, httpResult.getString("X_RSPDESC"));
        }
        eosInfo.putAll(httpResult);

        // 2- 封装ESOP数据
        String userId = getData().getString("GRP_USER_ID");
        eosInfo.put("PRODUCT_ID", productId);
        eosInfo.put("USER_ID", userId);
        IData esopData = GroupEsopUtilView.paraserEsopString(this, eosInfo);
        setEsopProductInfo(esopData);

        // 3- 设置商品操作类型
        String merchOpType = getMerchOpTypeFromEsop(esopData);
        IData info = new DataMap();
        info.put("PRODUCTOPERTYPE", merchOpType);
        setInfo(info);
    }

    /**
     * @author hudie
     * @作用 初始化操作类型 商品操作类型
     */
    public void initOperTypesChg(String status) throws Exception
    {
        IDataset operTypes = new DatasetList();
        IData opertype = new DataMap();
        if ("正常".equals(status))
        {
            IDataset operTypeInfoList = AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(this, this.productId, "P", "0", "CHGMERCHOPTYPE");
            if (IDataUtil.isEmpty(operTypeInfoList))
            {
                return;
            }

            String operType = operTypeInfoList.getData(0).getString("ATTR_VALUE");
            String[] operTypeArray = operType.split(",");
            for (int i = 0; i < operTypeArray.length; i++)
            {
                operType = operTypeArray[i];
                String operTypeName = getOperNameByOperType(operType);
                IData operTypeInfo = new DataMap();
                operTypeInfo.put("OPER_TYPE", operType);
                operTypeInfo.put("OPER_NAME", operTypeName);
                operTypes.add(operTypeInfo);
            }
        }

        if("暂停".equals(status)){
            opertype = new DataMap();
            opertype.put("OPER_TYPE", GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE.getValue());
            opertype.put("OPER_NAME", "商品恢复");
            operTypes.add(opertype);
        }else if("预销".equals(status)){
            opertype = new DataMap();
            opertype.put("OPER_TYPE", GroupBaseConst.MERCH_STATUS.MERCH_CANCLEPREDESTORY.getValue());
            opertype.put("OPER_NAME", "冷冻期恢复商品订购");
            operTypes.add(opertype);
        }else{
            IDataset result = AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(this, "1", "B", "PREDESTORY", productId);
            if (IDataUtil.isEmpty(result))
            {
                opertype = new DataMap();
                opertype.put("OPER_TYPE", GroupBaseConst.MERCH_STATUS.MERCH_CANCLE.getValue());
                opertype.put("OPER_NAME", "取消商品订购");
                operTypes.add(opertype);
            }
            else
            {
                opertype = new DataMap();
                opertype.put("OPER_TYPE", GroupBaseConst.MERCH_STATUS.MERCH_PREDESTORY.getValue());
                opertype.put("OPER_NAME", "预取消商品订购");
                operTypes.add(opertype);
            }
        }
  
        setOperTypes(operTypes);
    }

    /**
     * @description 判断产品是否为必选产品
     * @author xunyl
     * @date 2013-08-05
     */
    public String isMustSelect(String productId) throws Exception
    {
        // 1- 根据产品编号查询TD_B_PRODUCT_COMP_RELA表
        IDataset productInfoList = ProductCompRelaInfoIntfViewUtil.qryProductCompRelaInfosByProductIdBRelationTypeCodeAndForceTag(this, productId, "4", "1");
        IDataset fstOpenInfoList = AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdAndIdTypeAttrObjAttrValue(this, "1", "B", "FSTOPEN", productId);
        // 3- 如果productInfoList或者fstOpenInfoList不为空，则表示必选产品
        if ((IDataUtil.isNotEmpty(productInfoList)) || (IDataUtil.isNotEmpty(fstOpenInfoList)))
        {
            return "true";
        }
        else
        {
            return "false";
        }
    }

    public IDataset queryExist(IRequestCycle cycle) throws Exception
    {
        // 现在已经存在BB关系的BBOSS子用户        String userIdA = getData().getString("GRP_USER_ID");
        String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, productId);
        IDataset relationBBInfoList = RelationBBInfoIntfViewUtil.qryRelaBBInfoByRoleCodeBForGrp(this, userIdA, relationTypeCode, "0");
        if (IDataUtil.isEmpty(relationBBInfoList))
        {
            CSViewException.apperr(GrpException.CRM_GRP_195);
        }

        IDataset users = new DatasetList();
        for (int i = 0; i < relationBBInfoList.size(); i++)
        {
            IData d = relationBBInfoList.getData(i);

            IData temp = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, d.getString("USER_ID_B"), false);
            if (IDataUtil.isEmpty(temp))
            {
                continue;
            }
            if ("BOSG".equals(temp.getString("BRAND_CODE")))
            {
                users.add(temp);
            }
        }

        return users;
    }

    /**
     * 查询用户必选的产品 将用户已经订购的产品和订购时选择的产品参数放到tradeData里去
     * 
     * @author shixb
     * @version 创建时间：2009-5-11 下午08:52:48
     */
    public void queryUserProducts(IRequestCycle cycle) throws Exception
    {
        
        IDataset ds = ProductCompRelaInfoIntfViewUtil.qryProductCompRelaInfosByProductIdARelationTypeCodeAndForceTag(this, productId, "4", null);
        IDataset users = queryExist(cycle);
        IDataset userProducts = new DatasetList();// 用户已经订购的产品
        productGoodInfos.put("PRODUCT_INFO_LIST", userProducts);
        IDataset multiUsers = new DatasetList();// 重复订购的产品
        for (int i = 0; i < ds.size(); i++)
        {
            IData d = ds.getData(i);
            String productId = d.getString("PRODUCT_ID_B");
            String productSpecCode =StaticUtil.getStaticValue(getVisit(), "TD_B_ATTR_BIZ", new String[]
            { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
            { "1", "B", productId, "PRO" });
            if (initMulti(d.getString("PRODUCT_ID_B")) && StringUtils.equals("9001202", productSpecCode))
            {
                d.put("CAN_MANY", "muti");
            }
            for (int j = 0; j < users.size(); j++)
            {
                IData user = users.getData(j);
                // 初始化操作类型
                int index = j + 1;
                if (user.getString("PRODUCT_ID").equals(d.getString("PRODUCT_ID_B")) && !"true".equals(d.getString("IS_EXIST")))
                {
                    d.put("IS_EXIST", "true");
                    d.put("PRODUCT_STATUS", "N".equals(user.getString("RSRV_STR5")) ? "暂停" : ("D".equals(user.getString("RSRV_STR5")) ? "预销" : "正常"));
                    d.put("USER_ID", user.getString("USER_ID"));
                    d.put("PRODUCT_SPEC_CODE", user.getString("PRODUCT_ID"));
                    d.put("PRODUCT_ID", user.getString("PRODUCT_ID"));
                    d.put("PRODUCT_OPER_CODE", "EXIST");// 默认的操作类型
                    d.put("PRODUCT_INDEX", index);
                    d.put("IS_MUST_SELECT", isMustSelect(user.getString("PRODUCT_ID")));
                    userProducts.add(d);

                    IDataset userAttr = getUserAttrByUserIdInstId(user.getString("USER_ID"), user.getString("PRODUCT_ID"));
                    // 初始化的时候，将订购时选择的参数放到tradeData中去
                    IData idata = new DataMap();
                    idata.put(user.getString("PRODUCT_ID") + "_" + index, IDataUtil.hTable2STable(userAttr, "ATTR_CODE", "ATTR_VALUE", "ATTR_VALUE"));
                    productGoodInfos.put("PRODUCT_PARAM", idata);
                    continue;
                }

                if (user.getString("PRODUCT_ID").equals(d.getString("PRODUCT_ID_B")) && "true".equals(d.getString("IS_EXIST", "")))
                {
                    IData multiUser = new DataMap();
                    multiUser.putAll(d);
                    if (initMulti(d.getString("PRODUCT_ID_B")) && StringUtils.equals("9001202", productSpecCode))
                    {
                        multiUser.put("CAN_MANY", "");
                    }
                    multiUser.put("PRODUCT_STATUS", "N".equals(user.getString("RSRV_STR5")) ? "暂停" : ("D".equals(user.getString("RSRV_STR5")) ? "预销" : "正常"));
                    multiUser.put("USER_ID", user.getString("USER_ID"));
                    multiUser.put("PRODUCT_INDEX", index);
                    d.put("IS_MUST_SELECT", isMustSelect(d.getString("PRODUCT_ID")));
                    multiUsers.add(multiUser);
                }
            }

            if ("".equals(d.getString("PRODUCT_INDEX", "")))
            {
                d.put("PRODUCT_INDEX", "1");
            }
        }
        ds.addAll(multiUsers);
        userProducts.addAll(multiUsers);
        setProducts(ds);

        // 将产品列表信息和BBOSS商产品信息保存至缓存
        String groupId = getData().getString("GROUP_ID");
        this.saveInitInfo(groupId, ds);
    }
    
    /*
     * @description 针对单个产品预注销的页面选择，把不支持预注销的电子流量卡充值产品的默认选择框取消
     * @author wangzc7
     * @date 2017-03-10
     */
    public void checkEleFlowDeposit(IRequestCycle cycle) throws Throwable
    {
        // 1- 获取集团编号
        String groupId = getData().getString("GROUP_ID");

        // 2- 获取缓存中的产品列表信息和BBOSS商产品信息
        String key = CacheKey.getBossInitInfoKey(getVisit().getStaffId(), groupId);
        IData initInfo = new DataMap(SharedCache.get(key).toString());
        IDataset productInfoList = initInfo.getDataset("PRODUCT_INFO_LIST");

        // 3- 判断传递过来的参数中，是否有电子流量卡充值产品
        IData data = new DataMap();//返回给JS回调函数的参数，包含电子充值卡的产品ID
        data.put("PRODUCT_ID_B", ""); 
        
        IData inparams = new DataMap();//查询电子充值卡全网编码的入参
        inparams.put("ID", "1");
        inparams.put("ID_TYPE", "B");
        inparams.put("ATTR_OBJ", "PRO");
        for (int i = 0,j = productInfoList.size(); i < j; i++) {
            inparams.put("ATTR_CODE", productInfoList.getData(i).getString("PRODUCT_ID_B"));
            IDataset result = CSViewCall.call(this, "CS.AttrBizInfoQrySVC.getBizAttr", inparams);
            String poNumber = "";
            if (result != null && result.size() > 0)
            {
                poNumber = result.getData(0).getString("ATTR_VALUE", "");
            }
			if("9001402".equals(poNumber)){
				data.put("PRODUCT_ID_B", productInfoList.getData(i).getString("PRODUCT_ID_B"));
			}
		}

        setAjax(data);
    }

    /*
     * @description 商品操作变更时，刷新产品列表
     * @author xunyl
     * @date 2013-07-07
     */
    public void refreshProducts(IRequestCycle cycle) throws Throwable
    {
        // 1- 获取集团编号
        String groupId = getData().getString("GROUP_ID");

        // 2- 获取缓存中的产品列表信息和BBOSS商产品信息
        String key = CacheKey.getBossInitInfoKey(getVisit().getStaffId(), groupId);
        IData initInfo = new DataMap(SharedCache.get(key).toString());
        IDataset productInfoList = initInfo.getDataset("PRODUCT_INFO_LIST");
        IData productGoodInfos = initInfo.getData("PRODUCT_GOOD_INFO");

        // 3- 重新设置产品列表信息和BBOSS商产品信息的值
        setProducts(productInfoList);
        setProductGoodInfos(productGoodInfos);
    }

    /*
     * @description 将产品列表信息和BBOSS商产品信息保存至缓存，商品操作变更时用
     * @author xunyl
     * @date 2013-07-08
     */
    protected void saveInitInfo(String groupId, IDataset productInfoList) throws Exception
    {
        // 1- 定义保存信息
        IData initInfo = new DataMap();

        // 2- 添加产品列表信息
        initInfo.put("PRODUCT_INFO_LIST", productInfoList);

        // 3- 添加集团商产品信息
        initInfo.put("PRODUCT_GOOD_INFO", productGoodInfos);

        // 4- 将需要保存的对象保存至缓存中
        String key = CacheKey.getBossInitInfoKey(getVisit().getStaffId(), groupId);
        SharedCache.set(key, initInfo, 1200);
    }
    public abstract void setTradeTypeCode(String tradeTypeCode);// 保存业务类型
    
    public abstract void setOfferName(String offerName);// 组合包
    
    public abstract void setCondition(IData info);

    public abstract void setEOS(IDataset EOS);// 初始化的ESOP参数信息

    public abstract void setEsopProductInfo(IData esopData);// 整理后的产品和产品参数信息

    public abstract void setGoodsStatus(String goodsStatus);

    public abstract void setGroupId(String custId);// 上产品信息，升级前是存放在tradeData中的

    public abstract void setGrpSn(String serialNumber);// 保存集团虚拟号码

    public abstract void setGrpUserId(String grpUserId);// 保存集团用户编号

    public abstract void setInfo(IData info);

    public abstract void setOperTypes(IDataset OperTypes);

    public abstract void setProductGoodInfos(IData productGoodInfos);// 上产品信息，升级前是存放在tradeData中的

    public abstract void setProducts(IDataset products);// 必选的产品

    /*
     * @description 传递商产品信息至产品参数页面
     * @author xunyl
     * @date 2013-06-29
     */
    public void transProductGoodInfos(IRequestCycle cycle) throws Throwable
    {
        // 1- 获取集团编号(该集团编号作为商产品信息的key值，用来保证缓存信息的唯一性)
        String groupId = getData().getString("GROUP_ID");

        // 2- 获取商产品信息
        String productGoodInfos = getData().getString("productGoodInfos");

        // 3- 将商产品信息保存至缓存中
        String key = CacheKey.getBossProductInfoKey(getVisit().getStaffId(), groupId);
        SharedCache.set(key, productGoodInfos, 1200);
    }

    public boolean initMulti(String productId) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("ID", "1");
        inparams.put("ID_TYPE", "B");
        inparams.put("ATTR_CODE", productId);
        inparams.put("ATTR_OBJ", "MULTI");
        IDataset result = CSViewCall.call(this, "CS.AttrBizInfoQrySVC.getBizAttr", inparams);
        if (result == null || result.size() == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}

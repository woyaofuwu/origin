
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeMember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.DbException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.MebDisAttrTransBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

public class changeBBossRevsMemDataBean extends GroupBean
{
    private static final long serialVersionUID = 1L;

    protected static boolean isOutNetSnExist = true;// 默认为往外号码存在

    protected static String productUserId = "";// 产品用户编号

    /**
     * chenyi 2014-4-10 获取流量叠加包
     * 
     * @param map
     * @return
     * @throws Exception
     */
    protected static IDataset getElementInfo(IData map, String productID) throws Exception
    {
        IDataset elemeDataset = new DatasetList();

        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(productID);// 获取成员产品编码

        IDataset memberNumberSet = IDataUtil.getDataset("MEMBER_NUMBER", map); // 成员号码
        IDataset memberOrderRateSet = IDataUtil.getDataset("MEMBER_ORDER_RATE", map); // 成员订购套餐ID

        int characterCount = memberNumberSet.size();
        if (characterCount != memberNumberSet.size() || characterCount != memberOrderRateSet.size())
        {

            CSAppException.apperr(GrpException.CRM_GRP_458, IntfField.OTHER_ERR[0], IntfField.OTHER_ERR[1]);
        }
        for (int i = 0, sizeI = memberOrderRateSet.size(); i < sizeI; i++)
        {
            String memberOrderRate = memberOrderRateSet.get(i).toString();

            // 获取本地资费编码
            IDataset discntInfos = AttrBizInfoQry.getBizAttr(productID, "D", "FluxPay", memberOrderRate, null);
            if (IDataUtil.isEmpty(discntInfos))
            {
                CSAppException.apperr(GrpException.CRM_GRP_633);
            }
            ;
            String element_id = discntInfos.getData(0).getString("ATTR_VALUE");

            IData packData = PkgElemInfoQry.getDiscntsByDiscntCode(element_id, baseMemProduct, null);
            IData elementData = newDisData(baseMemProduct, null, packData.getString("PACKAGE_ID"), element_id);
            elemeDataset.add(elementData);
        }
        return elemeDataset;
    }

    /*
     * @description 获取成员用户编号
     * @author xunyl
     * @date 2013-07-11
     */
    protected static String getMemberUserId(IData map) throws Exception
    {
        // 1- 定义成员用户编号
        String memUserId = "";

        // 2- 获取成员手机号码
        String memSerialNumber = map.getString("SERIAL_NUMBER", "");

        // 3- 根据成员手机号获取成员用户信息
        IDataset memberUserInfoList = UserGrpInfoQry.getMemberUserInfoBySn(memSerialNumber);

        // 4- 如果成员用户信息不存在(无论是移动号还是铁通号只要是省内号码都因该抛出异常)
        if (memberUserInfoList == null || memberUserInfoList.size() == 0)
        {
            // 4-1 判断成员号码是否为本地铁通号码，如果为省内铁通号码则抛出异常
            if ("0".equals(memSerialNumber.substring(0, 1)) && ParamInfoQry.isExistLocalSerialnumber(memSerialNumber))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_211, IntfField.IAGWGrpMemBizRet.ERR39[0], "该成员号码[", memSerialNumber);
            }

            // 4-2 成员号码如果为省内移动号码则抛出异常
            IData result = RouteInfoQry.getMofficeInfoBySn(memSerialNumber);
            if (IDataUtil.isNotEmpty(result))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_321, IntfField.IAGWGrpMemBizRet.ERR39[0], "该成员号码[", memSerialNumber);
            }

            // 4-3 外省号码(三户资料不存在则建虚拟三户信息，存在则直接用已有的三户信息)
            String[] connNames = Route.getAllCrmDb();
            if (connNames == null)
            {
                CSAppException.apperr(DbException.CRM_DB_9);
            }
            memberUserInfoList = searchMemUserInfoFromCrm(memSerialNumber, connNames);

        }

        // 5- 获取商品用户编号
        if (null != memberUserInfoList || memberUserInfoList.size() == 0)
        {
            memUserId = memberUserInfoList.getData(0).getString("USER_ID");
        }
        else
        {
            isOutNetSnExist = false;
        }

        // 5- 返回结果
        return memUserId;
    }

    /*
     * @description 获取产品属性信息
     * @author xunyl
     * @date 2013-07-12
     */
    protected static IDataset getProductParamInfo(IData map, String productId) throws Exception
    {
        // 1- 定义产品属性对象集
        IDataset productParamInfoList = new DatasetList();

        // 2- 添加成员扩展属性至属性对象集
        IDataset characterIdSet = IDataUtil.getDataset("RSRV_STR11", map);// 成员扩展属性 属性编码
        IDataset characterNameSet = IDataUtil.getDataset("RSRV_STR12", map);// 成员扩展属性 属性名
        IDataset characterValueSet = IDataUtil.getDataset("RSRV_STR13", map);// 成员扩展属性 属性值
        int characterCount = characterIdSet.size();
        if (characterCount != characterNameSet.size() || characterCount != characterValueSet.size())
        {
            CSAppException.apperr(GrpException.CRM_GRP_458, IntfField.OTHER_ERR[0], IntfField.OTHER_ERR[1]);
        }
        if (characterIdSet.size() > 0)
        {
            characterIdSet = IDataUtil.modiIDataset(characterIdSet.get(0), "RSRV_STR11");
            characterNameSet = IDataUtil.modiIDataset(characterNameSet.get(0), "RSRV_STR12");
            characterValueSet = IDataUtil.modiIDataset(characterValueSet.get(0), "RSRV_STR13");

            IDataset dsParams = new DatasetList();
            for (int i = 0; i != characterIdSet.size(); ++i)
            {
                // 1- 新增记录
                IData tmpData = new DataMap();
                tmpData.put("ATTR_CODE", characterIdSet.get(i));
                tmpData.put("ATTR_VALUE", characterValueSet.get(i));
                tmpData.put("ATTR_NAME", characterNameSet.get(i));
                tmpData.put("STATE", GroupBaseConst.MEB_ATTR_STATUS_DESC.ATTR_ADD.getValue());
                dsParams.add(tmpData);

                // 2- 删除记录
                IData tmpData1 = new DataMap();
                tmpData1.put("ATTR_CODE", characterIdSet.get(i));
                tmpData1.put("ATTR_VALUE", characterValueSet.get(i));
                tmpData1.put("ATTR_NAME", characterNameSet.get(i));
                tmpData1.put("STATE", GroupBaseConst.MEB_ATTR_STATUS_DESC.ATTR_DEL.getValue());
                dsParams.add(tmpData1);
            }
            IData productParamObj = new DataMap();// 放置成员基础产品编号和对应的产品参数

            productParamObj.put("PRODUCT_PARAM", dsParams);

            productParamObj.put("PRODUCT_ID", productId);// 成员基础产品编号

            productParamInfoList.add(productParamObj);
        }

        // 2- 返回产品属性对象
        return productParamInfoList;
    }

    /*
     * @description 拼装操作类型对应为变更成员扩展属性、重置序列号的产品数据
     * @author xunyl
     * @date 2013-07-15
     */
    protected static void makeChgMemAttrData(String oSubTypeID, IData map, IData merchpInfo) throws Exception
    {
        // 1- 添加产品用户编号
        merchpInfo.put("USER_ID", productUserId);

        // 2- 添加手机号码
        String memSerialNumber = map.getString("SERIAL_NUMBER", "");
        merchpInfo.put("SERIAL_NUMBER", memSerialNumber);

        // 3- 添加产品编号
        IData productUserInfo = UserInfoQry.getGrpUserInfoByUserId(productUserId, "0", Route.CONN_CRM_CG);
        merchpInfo.put("PRODUCT_ID", productUserInfo.getString("PRODUCT_ID"));

        // 4- 添加扩展属性信息
        IDataset productParamInfoList = getProductParamInfo(map, productUserInfo.getString("PRODUCT_ID"));
        merchpInfo.put("PRODUCT_PARAM_INFO", productParamInfoList);

        // 4- 添加BBOSS侧产品信息
        IData productInfo = new DataMap();
        productInfo.put("MEB_TYPE", map.getString("USER_TYPE", ""));
        productInfo.put("MEB_OPER_CODE", oSubTypeID);
        productInfo.put("USER_ID", productUserId);
        merchpInfo.put("PRODUCT_INFO", productInfo);

        // 5- 添加反向受理标记(反向受理不发服务开通)
        merchpInfo.put("IN_MODE_CODE", "6");
    }

    /*
     * @description 拼装操作类型对应为变更成员类型、暂停成员、恢复成员的产品数据
     * @author xunyl
     * @date 2013-07-15
     */
    protected static void makeChgMemDate(String oSubTypeID, IData map, IData merchpInfo) throws Exception
    {
        // 1- 添加产品用户编号
        merchpInfo.put("USER_ID", productUserId);

        // 2- 添加手机号码
        String memSerialNumber = map.getString("SERIAL_NUMBER", "");
        merchpInfo.put("SERIAL_NUMBER", memSerialNumber);

        // 3- 添加产品编号
        IData productUserInfo = UserInfoQry.getGrpUserInfoByUserId(productUserId, "0", Route.CONN_CRM_CG);
        merchpInfo.put("PRODUCT_ID", productUserInfo.getString("PRODUCT_ID"));

        // 4- 添加BBOSS侧产品信息
        IData productInfo = new DataMap();
        productInfo.put("MEB_TYPE", map.getString("USER_TYPE", ""));
        productInfo.put("MEB_OPER_CODE", oSubTypeID);
        productInfo.put("USER_ID", productUserId);
        merchpInfo.put("PRODUCT_INFO", productInfo);

        // 5- 添加反向受理标记(反向受理不发服务开通)
        merchpInfo.put("IN_MODE_CODE", "6");
    }

    /**
     * chenyi 2014-4-10
     * 
     * @param map
     * @param merchpInfo
     * @throws Exception
     */
    protected static void makeChgMemElementData(IData map, IData merchpInfo) throws Exception
    {
        // 1- 添加产品用户编号
        merchpInfo.put("USER_ID", productUserId);

        // 2- 添加手机号码
        String memSerialNumber = map.getString("SERIAL_NUMBER", "");
        merchpInfo.put("SERIAL_NUMBER", memSerialNumber);

        // 3- 添加产品编号
        IData productUserInfo = UserInfoQry.getGrpUserInfoByUserId(productUserId, "0", Route.CONN_CRM_CG);
        merchpInfo.put("PRODUCT_ID", productUserInfo.getString("PRODUCT_ID"));

        // 4- 添加流量叠加包
        // add chenyi
        IDataset elemtDataset = getElementInfo(map, productUserInfo.getString("PRODUCT_ID"));
        if (null == elemtDataset || elemtDataset.size() == 0)
        {
            merchpInfo.put("ELEMENT_INFO", "");
        }
        else
        {
            merchpInfo.put("ELEMENT_INFO", elemtDataset);
        }

        // 4- 添加BBOSS侧产品信息
        IData productInfo = new DataMap();
        productInfo.put("MEB_TYPE", map.getString("USER_TYPE", ""));
        productInfo.put("MEB_OPER_CODE","6");//订购流量叠加包默是成员属性变更模式
        productInfo.put("USER_ID", productUserId);
        productInfo.put("MEMBER_ORDER_NUMBER", map.getString("MEMBER_ORDER_NUMBER", ""));
        merchpInfo.put("PRODUCT_INFO", productInfo);

        // 5- 添加反向受理标记(反向受理不发服务开通)
        merchpInfo.put("IN_MODE_CODE", "6");

    }

    /*
     * @description 将一级BOSS传递过来的数据拼装成符合集团产品受理基类处理的数据集
     * @author xunyl
     * @date 2013-07-11
     */
    public static IData makeData(IData map) throws Exception
    {
        // 1- 定义返回数据
        IData returnVal = new DataMap();

        // 2- 拼装商品数据
        makeMerchInfoData(map, returnVal);

        // 3- 拼装产品数据
        makeMerchPInfoData(map, returnVal);

        // 4- 返回结果
        return returnVal;
    }

    public static IData makeJKDTData(IData map) throws Exception
    {
        // 1- 定义返回数据
        IData returnVal = new DataMap();

        // 2- 拼装商品数据
        makeJKDTMerchInfoData(map, returnVal);

        // 3- 拼装产品数据
        makeJKDTMerchPInfoData(map, returnVal);

        // 4- 返回结果
        return returnVal;
    }

    /*
     * @description 拼装商品数据
     * @author xunyl
     * @date 2013-07-11
     * @remark 变更过程中无需拼商品与成员的BB关系,但需要做基本的校验
     */
    protected static void makeMerchInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 根据产品订购关系编号获取BBOSS侧产品信息为空，抛出异常
        String productOfferId = map.getString("PRODUCTID", "");

        IDataset userGrpMerchInfoList;
        userGrpMerchInfoList = UserGrpMerchpInfoQry.qryMerchpInfoByProductOfferId(productOfferId);

        if (IDataUtil.isEmpty(userGrpMerchInfoList))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_141, IntfField.BbossGrpMemBizRet.ERR11[0], IntfField.BbossGrpMemBizRet.ERR11[1], productOfferId);
        }

        // 2- 根据产品用户编号无法获取到产品用户信息，抛出异常
        IData userGrpMerchInfo = userGrpMerchInfoList.getData(0);
        productUserId = userGrpMerchInfo.getString("USER_ID");
        IData productUserInfo = UserInfoQry.getGrpUserInfoByUserId(productUserId, "0", Route.CONN_CRM_CG);
        if (null == productUserInfo)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }

        // 3- 根据产品编号无法获取到商产品BB关系，抛出异常
        String merchSpecCode = userGrpMerchInfo.getString("MERCH_SPEC_CODE");// BBOSS侧商品用户编码
        String merchId = merchToProduct(merchSpecCode, 0);
        String relationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchId, "", true);
        IDataset relaBBInfoList = RelaBBInfoQry.qryRelaBBInfoByUserIdBRelaTypeCode(productUserId, relationTypeCode, Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(relaBBInfoList))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_215);
        }

        // 4- 根据商品用户编号无法获取到商品用户信息，抛出异常
        IData relationBB = relaBBInfoList.getData(0);
        String merchUserId = relationBB.getString("USER_ID_A");
        IData merchUserInfo = UserInfoQry.getGrpUserInfoByUserId(merchUserId, "0", Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(merchUserInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }

        // 5- 获取成员用户信息，如果网外号码不存在的场合，目前的处理抛出异常(老系统为变更转为新增处理，但是考虑有些成员有参数而且是必填参数)
        String memUserId = getMemberUserId(map);
        if (!isOutNetSnExist && "".equals(memUserId))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_541);
        }

        // 6- 定义商品对象
        IData merchInfo = new DataMap();

        // 7-成员特殊属性资费互换
        MebDisAttrTransBean.attrDataToDisData(merchInfo);

        // 8- 将商品信息添加至返回结果集
        returnVal.put("MERCH_INFO", merchInfo);
    }

    protected static void makeJKDTMerchInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 根据产品订购关系编号获取BBOSS侧产品信息为空，抛出异常
        String productOfferId = map.getString("PRODUCTID", "");

        IDataset userGrpMerchInfoList;
        userGrpMerchInfoList =UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(productOfferId);

        if (IDataUtil.isEmpty(userGrpMerchInfoList))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_141, IntfField.BbossGrpMemBizRet.ERR11[0], IntfField.BbossGrpMemBizRet.ERR11[1], productOfferId);
        }

        // 2- 根据产品用户编号无法获取到产品用户信息，抛出异常
        IData userGrpMerchInfo = userGrpMerchInfoList.getData(0);
        productUserId = userGrpMerchInfo.getString("USER_ID");
        IData productUserInfo = UserInfoQry.getGrpUserInfoByUserId(productUserId, "0", Route.CONN_CRM_CG);
        if (null == productUserInfo)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }

        // 3- 根据产品编号无法获取到商产品BB关系，抛出异常
        String merchSpecCode = userGrpMerchInfo.getString("MERCH_SPEC_CODE");// BBOSS侧商品用户编码
        String merchId = GrpCommonBean.merchJKDTToProduct(merchSpecCode, 0, null);
        String relationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchId, "", true);
        IDataset relaBBInfoList = RelaBBInfoQry.qryRelaBBInfoByUserIdBRelaTypeCode(productUserId, relationTypeCode, Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(relaBBInfoList))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_215);
        }

        // 4- 根据商品用户编号无法获取到商品用户信息，抛出异常
        IData relationBB = relaBBInfoList.getData(0);
        String merchUserId = relationBB.getString("USER_ID_A");
        IData merchUserInfo = UserInfoQry.getGrpUserInfoByUserId(merchUserId, "0", Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(merchUserInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }

        // 5- 获取成员用户信息，如果网外号码不存在的场合，目前的处理抛出异常(老系统为变更转为新增处理，但是考虑有些成员有参数而且是必填参数)
        String memUserId = getMemberUserId(map);
        if (!isOutNetSnExist && "".equals(memUserId))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_541);
        }

        // 6- 定义商品对象
        IData merchInfo = new DataMap();

        // 7-成员特殊属性资费互换
        MebDisAttrTransBean.attrDataToDisData(merchInfo);

        // 8- 将商品信息添加至返回结果集
        returnVal.put("MERCH_INFO", merchInfo);
    }

    /*
     * @description 拼装产品数据
     * @author xunyl
     * @date 2013-07-11
     * @remark 成员变更包括变更成员类型，成员暂停，成员恢复，激活，变更成员扩展属性，重置序列号，目前不考虑激活
     */
    protected static void makeMerchPInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 获取成员变更的操作类型
        String oSubTypeID = map.getString("ACTION", "");

        // 2- 定义产品对象
        IData merchpInfo = new DataMap();

        // 获取流量叠加包字段，如果没有则是普通的反向成员变更，如果有则是反向资费变更，重新拼merchpInfo字段 add chenyi
        String memberOrderRate = map.getString("MEMBER_ORDER_RATE");

        // 3- 根据变更的不同类型拼产品数据
        if (("2".equals(oSubTypeID) && StringUtils.isEmpty(memberOrderRate)) || ("3".equals(oSubTypeID) && StringUtils.isEmpty(memberOrderRate)) || ("4".equals(oSubTypeID) && StringUtils.isEmpty(memberOrderRate)))
        {// 变更成员类型、暂停成员、成员恢复
            makeChgMemDate(oSubTypeID, map, merchpInfo);
        }
        else if ("5".equals(oSubTypeID))
        {// 激活

        }
        else if ("6".equals(oSubTypeID) || "7".equals(oSubTypeID))
        {// 变更成员扩展属性、重置序列号
            makeChgMemAttrData(oSubTypeID, map, merchpInfo);

        }
        else if (StringUtils.isNotEmpty(memberOrderRate))
        {
            // 新增接口，流量叠加包 add chenyi
         
            makeChgMemElementData(map, merchpInfo);
        }
        else
        {
            CSAppException.apperr(TradeException.CRM_TRADE_2002);
        }

        // 4-成员特殊属性资费互换
        MebDisAttrTransBean.attrDataToDisData(merchpInfo);

        // 5- 将产品信息添加至返回结果集
        returnVal.put("ORDER_INFO", IDataUtil.idToIds(merchpInfo));
    }

    /**
     *@description 商品规格转产品模型数据
     *@author xunyl
     *@date 2013-06-21
     */
    public static String merchToProduct(String elementId, int mode) throws Exception
    {
        // 1- 定义查询的元素类型
        String attrObj = "";
        if (mode == 0)
        {
            attrObj = "PRO";// 商品、产品ID
        }
        else if (mode == 1)
        {
            attrObj = "DIS";// 商品、产品资费，商品套餐
        }

        // 根据商品编号查询产品信息
        IDataset datas = AttrBizInfoQry.getBizAttrByAttrValue("1", "B", attrObj, elementId, null);
        if (datas != null && datas.size() > 0)
        {
            IData data = datas.getData(0);
            return data.getString("ATTR_CODE", "");
        }
        else
        {
            CSAppException.apperr(CrmUserException.CRM_USER_900, elementId);
        }
        return "";
    }

    /**
     * chenyi 2014-4-10 拼写新的资费信息
     * 
     * @return
     * @throws Exception
     */
    private static IData newDisData(String product_id, IDataset attr_param, String package_id, String element_id) throws Exception
    {
        IData newDisnct = new DataMap();
        newDisnct.put("START_DATE", SysDateMgr.getSysTime());
        newDisnct.put("ELEMENT_TYPE_CODE", "D");
        newDisnct.put("PRODUCT_ID", product_id);
        newDisnct.put("END_DATE", SysDateMgr.getLastDateThisMonth());
        newDisnct.put("INST_ID", "");
        newDisnct.put("ATTR_PARAM", attr_param);
        newDisnct.put("PACKAGE_ID", package_id);
        newDisnct.put("ELEMENT_ID", element_id);
        newDisnct.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        return newDisnct;
    }

    /*
     * @description 根据网外成员手机号到各CRM库查找三户信息
     * @auhtor xunyl
     * @date 2013-07-11
     */
    protected static IDataset searchMemUserInfoFromCrm(String memSerialNumber, String[] connNames) throws Exception
    {
        // 1- 定义成员用户信息
        IDataset memUserInfo = new DatasetList();

        // 2- CRM库查找网外成员的三户信息
        for (int i = 0; i < connNames.length; i++)
        {
            String connName = connNames[i];
            if (connName.indexOf("crm") >= 0)
            {
                memUserInfo = UserInfoQry.getUserInfoBySn(memSerialNumber, "0", "06", connName);
                if (null != memUserInfo && memUserInfo.size() != 0)
                {
                    break;
                }
            }
        }

        // 3- 返回成员用户信息
        return memUserInfo;
    }


    protected static void makeJKDTMerchPInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 获取成员变更的操作类型
        String oSubTypeID = map.getString("ACTION", "");

        // 2- 定义产品对象
        IData merchpInfo = new DataMap();

        // 获取流量叠加包字段，如果没有则是普通的反向成员变更，如果有则是反向资费变更，重新拼merchpInfo字段 add chenyi
        String memberOrderRate = map.getString("MEMBER_ORDER_RATE");

        // 3- 根据变更的不同类型拼产品数据
        if (("2".equals(oSubTypeID) && StringUtils.isEmpty(memberOrderRate)) || ("3".equals(oSubTypeID) && StringUtils.isEmpty(memberOrderRate)) || ("4".equals(oSubTypeID) && StringUtils.isEmpty(memberOrderRate)))
        {// 变更成员类型、暂停成员、成员恢复
            makeChgMemDate(oSubTypeID, map, merchpInfo);
        }
        else if ("5".equals(oSubTypeID))
        {// 激活

        }
        else if ("6".equals(oSubTypeID) || "7".equals(oSubTypeID))
        {// 变更成员扩展属性、重置序列号
            makeChgMemAttrData(oSubTypeID, map, merchpInfo);

        }
        else if (StringUtils.isNotEmpty(memberOrderRate))
        {
            // 新增接口，流量叠加包 add chenyi

            makeChgMemElementData(map, merchpInfo);
        }
        else
        {
            CSAppException.apperr(TradeException.CRM_TRADE_2002);
        }

        // 4-成员特殊属性资费互换
        MebDisAttrTransBean.attrDataToDisData(merchpInfo);

        // 5- 将产品信息添加至返回结果集
        returnVal.put("ORDER_INFO", IDataUtil.idToIds(merchpInfo));
    }
}

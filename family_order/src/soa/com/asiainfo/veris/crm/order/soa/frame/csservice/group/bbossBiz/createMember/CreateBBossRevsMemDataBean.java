
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createMember;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.exception.UUException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.MebDisAttrTransBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.MebCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ProductUtil;

public class CreateBBossRevsMemDataBean extends GroupBean
{

    protected static String memUserId = "";// 成员用户编号

    protected static String merchId = "";// 省内商品编号

    protected static String productId = "";// 省内产品编号

    protected static String productUserId = "";// 产品用户编号

    /*
     * @description 处理时间(沿用之前的处理)
     * @author chenlei
     */
    protected static String dealDate(String date, String key) throws Exception
    {

        if (date != null && (date.length() == 8 || date.length() == 14))
        {
            StringBuilder sb = new StringBuilder(date);
            sb.insert(4, '-');
            sb.insert(7, '-');
            if (date.length() == 14)
            {
                sb.insert(10, ' ');
                sb.insert(13, ':');
                sb.insert(16, ':');
            }
            else
            {
                sb.append(SysDateMgr.getFirstTime00000());
            }
            return sb.toString();
        }
        else if (date != null && date.length() == 19)
        {
            if (date.charAt(4) == '-' && date.charAt(7) == '-' && date.charAt(10) == ' ' && date.charAt(13) == ':' && date.charAt(16) == ':')
            {
                return date;
            }
            else
            {
                return null;
            }
        }
        else if ("".equals(date))
        {
            return date;
        }
        else
        {
            return null;
        }
    }

    /*
     * @description 获取成员基本产品的产品编号
     * @author xunyl
     * @date 2013-07-12
     */
    protected static String getBaseMemProdId() throws Exception
    {
        // 1- 定义成员基本产品的产品编号
        String memProductId = "";

        // 2- 获取集团产品对应的成员产品
        IDataset memProductSet = ProductUtil.getMebProduct(productId);

        // 3- 循环成员产品找到对应的基本成员产品
        for (int row = 0; row < memProductSet.size(); row++)
        {
            IData memProduct = (IData) memProductSet.get(row);
            // 成员附加基本产品
            if (memProduct.getString("FORCE_TAG").equals("1"))
            {
                memProductId = memProduct.getString("PRODUCT_ID_B");
                break;
            }
        }

        // 4- 返回成员产品编号
        return memProductId;
    }

    /*
     * @description 拼装必选元素信息
     * @auhtor xunyl
     * @date 2013-06-27
     */
    protected static void getElementInfo(IData forceElement, IData elementInfo) throws Exception
    {
        // 1- 添加资费实例ID，受理时默认为""
        elementInfo.put("INST_ID", "");

        // 2- 添加元素类型，资费类型对应为"D"，服务类型对应为"S"
        elementInfo.put("ELEMENT_TYPE_CODE", forceElement.getString("ELEMENT_TYPE_CODE"));

        // 3- 添加资费状态，受理时默认为"0"
        elementInfo.put("MODIFY_TAG", "0");

        // 4- 添加产品产品编号
        elementInfo.put("PRODUCT_ID", forceElement.getString("PRODUCT_ID"));

        // 5- 添加元素ID
        elementInfo.put("ELEMENT_ID", forceElement.getString("ELEMENT_ID"));

        // 6- 添加包信息
        elementInfo.put("PACKAGE_ID", forceElement.getString("PACKAGE_ID"));

        // 9- 添加开始时间
        elementInfo.put("START_DATE", SysDateMgr.getSysTime());

        // 10- 添加结束时间
        elementInfo.put("END_DATE", SysDateMgr.getTheLastTime());

        // 11- 添加参数信息
        IDataset attrItemAList = AttrItemInfoQry.getElementItemA(forceElement.getString("ELEMENT_TYPE_CODE"), forceElement.getString("ELEMENT_ID"), CSBizBean.getUserEparchyCode());
        IDataset attrParams = new DatasetList();
        for (int i = 0; i < attrItemAList.size(); i++)
        {
            IData attrParam = new DataMap();
            attrParam.put("ATTR_CODE", attrItemAList.getData(i).getString("ATTR_CODE"));
            attrParam.put("ATTR_NAME", attrItemAList.getData(i).getString("ATTR_LABLE"));
            attrParam.put("ATTR_VALUE", attrItemAList.getData(i).getString("ATTR_INIT_VALUE"));
            attrParams.add(attrParam);
        }
        if (null == attrItemAList || attrItemAList.size() == 0)
        {
            elementInfo.put("ATTR_PARAM", "");
        }
        else
        {
            elementInfo.put("ATTR_PARAM", attrParams);
        }

    }

    /*
     * @description 获取商品用户编号
     * @author xunyl
     * @date 2013-07-11
     */
    protected static String getMerchUserId(IData map) throws Exception
    {
        // 1- 定义商品用户编号
        String merchUserId = "";

        // 2- 根据产品订购关系编码查找产品用户信息
        String productOfferId = map.getString("PRODUCTID", "");

        IDataset productUserInfoList = UserGrpMerchpInfoQry.qryMerchpInfoByProductOfferId(productOfferId);


        if (productUserInfoList == null || productUserInfoList.size() == 0)
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_141, IntfField.BbossGrpMemBizRet.ERR11[0], IntfField.BbossGrpMemBizRet.ERR11[1], productOfferId);
        }
        IData productUserInfo = productUserInfoList.getData(0);

        // 3- 获取商品关系类型编码
        String merchSpecCode = productUserInfo.getString("MERCH_SPEC_CODE");// BBOSS侧商品用户编码
        merchId = merchToProduct(merchSpecCode, 0);
        String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchId, "", true);

        // 4- 获取产品用户编号
        String productSpecCode = productUserInfo.getString("PRODUCT_SPEC_CODE");// BBOSS侧商品用户编码
        productId = merchToProduct(productSpecCode, 0);// 此处取产品编号在该方法没有什么作用，是为了后续的代码用
        productUserId = productUserInfo.getString("USER_ID");

        // 5- 根据产品用户编号和商品关系类型编码查询商产品用户关系
        IDataset relaBBInfoList = RelaBBInfoQry.qryRelaBBInfoByUserIdBRelaTypeCode(productUserId, merchRelationTypeCode, Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(relaBBInfoList))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_215);
        }
        merchUserId = relaBBInfoList.getData(0).getString("USER_ID_A");

        // 3- 返回商品用户编号
        return merchUserId;
    }

    protected static String getJKDTMerchUserId(IData map) throws Exception
    {
        // 1- 定义商品用户编号
        String merchUserId = "";

        // 2- 根据产品订购关系编码查找产品用户信息
        String productOfferId = map.getString("PRODUCTID", "");

        IDataset productUserInfoList = UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(productOfferId);

        if (productUserInfoList == null || productUserInfoList.size() == 0)
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_141, IntfField.BbossGrpMemBizRet.ERR11[0], IntfField.BbossGrpMemBizRet.ERR11[1], productOfferId);
        }
        IData productUserInfo = productUserInfoList.getData(0);

        // 3- 获取商品关系类型编码
        String merchSpecCode = productUserInfo.getString("MERCH_SPEC_CODE");// BBOSS侧商品用户编码
        merchId = GrpCommonBean.merchJKDTToProduct(merchSpecCode, 0, null);
        String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchId, "", true);

        // 4- 获取产品用户编号
        String productSpecCode = productUserInfo.getString("PRODUCT_SPEC_CODE");// BBOSS侧商品用户编码
        productId = GrpCommonBean.merchJKDTToProduct(productSpecCode, 2, null);// 此处取产品编号在该方法没有什么作用，是为了后续的代码用
        productUserId = productUserInfo.getString("USER_ID");

        // 5- 根据产品用户编号和商品关系类型编码查询商产品用户关系
        IDataset relaBBInfoList = RelaBBInfoQry.qryRelaBBInfoByUserIdBRelaTypeCode(productUserId, merchRelationTypeCode, Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(relaBBInfoList))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_215);
        }
        merchUserId = relaBBInfoList.getData(0).getString("USER_ID_A");

        // 3- 返回商品用户编号
        return merchUserId;
    }

    /*
     * @description 获取产品属性信息
     * @author xunyl
     * @date 2013-07-12
     */
    protected static IDataset getProductParamInfo(IData map) throws Exception
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
            CSAppException.apperr(ProductException.CRM_PRODUCT_202);
        }
        if (characterIdSet.size() > 0)
        {
            characterIdSet = IDataUtil.modiIDataset(characterIdSet.get(0), "RSRV_STR11");
            characterNameSet = IDataUtil.modiIDataset(characterNameSet.get(0), "RSRV_STR12");
            characterValueSet = IDataUtil.modiIDataset(characterValueSet.get(0), "RSRV_STR13");

            IDataset dsParam = new DatasetList();
            for (int i = 0; i != characterIdSet.size(); ++i)
            {
                IData tmpData = new DataMap();
                tmpData.put("ATTR_CODE", characterIdSet.get(i));
                tmpData.put("ATTR_VALUE", characterValueSet.get(i));
                tmpData.put("ATTR_NAME", characterNameSet.get(i));
                tmpData.put("STATE", "ADD");
                dsParam.add(tmpData);
            }
            IData productParamObj = new DataMap();
            productParamObj.put("PRODUCT_PARAM", dsParam);
            productParamObj.put("PRODUCT_ID", productId);// 成员基础产品编号

            productParamInfoList.add(productParamObj);
        }

        // 2- 返回产品属性对象
        return productParamInfoList;
    }

    /*
     * @description 根据(商品/产品)用户编号查询(商品/产品)与成员间是否存在BB关系信息
     * @author xunyl
     * @date 2013-07-11
     */
    protected static boolean isBBRelExist(String productUserId, String memUserId) throws Exception
    {
        // 1- 定义返回结果
        boolean isExist = false;

        // 2- 查询台帐表
        IDataset merchMemTradeBBs = TradeRelaBBInfoQry.qryRelaBBInfoListByuserIdAB(productUserId, memUserId, BizRoute.getRouteId());
        if (IDataUtil.isNotEmpty(merchMemTradeBBs))
        {
            IData merchMemTradeBB = merchMemTradeBBs.getData(0);
            if (merchMemTradeBB.getString("MODIFY_TAG").equals("0"))
            {
                isExist = true;
            }
        }

        // 3- 查询资料表
        if (!isExist)
        {
            String productId = GrpCommonBean.getProductIdByUserId(productUserId);
            // 此处实际上既有商品的查询也有产品的查询，统一当作产品查询不影响结果
            String relationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(productId, "", true);
            IDataset merchMemBBs = RelaBBInfoQry.getBBByUserIdAB(productUserId, memUserId, "1", relationTypeCode);
            if (IDataUtil.isNotEmpty(merchMemBBs))
            {
                isExist = true;
            }
        }

        // 4- 返回结果
        return isExist;
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
     */
    protected static void makeMerchInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 定义商品对象
        IData merchInfo = new DataMap();

        // 2- 获取商品用户编号
        String merchUserId = getMerchUserId(map);

        // 3- 获取成员用户编号
        memUserId = MebCommonBean.getMemberUserId(map);

        // 3- 根据商品用户编号查询商品与成员间是否存在BB关系信息
        boolean isBBRelExist = false;
        if (!"".equals(memUserId))
        {
            isBBRelExist = isBBRelExist(merchUserId, memUserId);
        }

        // 4- 如果商品与成员间的BB关系信息不存在，则商品和产品都需要与成员创建BB关系
        if (!isBBRelExist)
        {
            // 4-1 添加用户信息
            merchInfo.put("USER_ID", merchUserId);

            // 4-2 添加虚拟手机号码信息
            String memSerialNumber = map.getString("SERIAL_NUMBER", "");
            merchInfo.put("SERIAL_NUMBER", memSerialNumber);

            // 4-3 添加角色编号(0:BBOSS商品,1:BBOSS成员)
            merchInfo.put("MEM_ROLE_B", "1");

            // 4-4 添加商品编号
            merchInfo.put("PRODUCT_ID", merchId);

            // 4-5 添加成员的期望生效时间(生成虚拟散户创建付费关系时有用到)
            merchInfo.put("EFF_DATE", dealDate(map.getString("EFFDATE", ""), "EFFDATE"));

            // 4-6 添加反向受理标记(反向受理不发服务开通)
            merchInfo.put("IN_MODE_CODE", "6");
        }

        MebDisAttrTransBean.attrDataToDisData(merchInfo);// 成员特殊属性资费互换

        // 5- 将商品信息添加至返回结果集
        returnVal.put("MERCH_INFO", merchInfo);
    }

    protected static void makeJKDTMerchInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 定义商品对象
        IData merchInfo = new DataMap();

        // 2- 获取商品用户编号
        String merchUserId = getJKDTMerchUserId(map);

        // 3- 获取成员用户编号
        memUserId = MebCommonBean.getJKDTMemberUserId(map);

        // 3- 根据商品用户编号查询商品与成员间是否存在BB关系信息
        boolean isBBRelExist = false;
        if (!"".equals(memUserId))
        {
            isBBRelExist = isJKDTBBRelExist(merchUserId, memUserId);
        }

        // 4- 如果商品与成员间的BB关系信息不存在，则商品和产品都需要与成员创建BB关系
        if (!isBBRelExist)
        {
            // 4-1 添加用户信息
            merchInfo.put("USER_ID", merchUserId);

            // 4-2 添加虚拟手机号码信息
            String memSerialNumber = map.getString("SERIAL_NUMBER", "");
            merchInfo.put("SERIAL_NUMBER", memSerialNumber);

            // 4-3 添加角色编号(0:BBOSS商品,1:BBOSS成员)
            merchInfo.put("MEM_ROLE_B", "1");

            // 4-4 添加商品编号
            merchInfo.put("PRODUCT_ID", merchId);

            // 4-5 添加成员的期望生效时间(生成虚拟散户创建付费关系时有用到)
            merchInfo.put("EFF_DATE", dealDate(map.getString("EFFDATE", ""), "EFFDATE"));

            // 4-6 添加反向受理标记(反向受理不发服务开通)
            merchInfo.put("IN_MODE_CODE", "6");
        }

        MebDisAttrTransBean.attrDataToDisData(merchInfo);// 成员特殊属性资费互换

        // 5- 将商品信息添加至返回结果集
        returnVal.put("MERCH_INFO", merchInfo);
    }

    /*
     * @description 拼装产品数据
     * @author xunyl
     * @date 2013-07-11
     */
    protected static void makeMerchPInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 检查成员和产品间的BB关系是否存在，存在需要抛出异常
        boolean isBBRelExist = false;
        if (!"".equals(memUserId))
        {
            isBBRelExist = isBBRelExist(productUserId, memUserId);
        }
        if (isBBRelExist)
        {
            CSAppException.apperr(UUException.CRM_UU_88);
        }

        // 2- 定义产品对象
        IData merchpInfo = new DataMap();

        // 3- 添加产品用户编号
        merchpInfo.put("USER_ID", productUserId);

        // 4- 添加虚拟手机号
        String memSerialNumber = map.getString("SERIAL_NUMBER", "");
        merchpInfo.put("SERIAL_NUMBER", memSerialNumber);

        // 5- 添加角色编号(0:BBOSS商品,1:BBOSS成员)
        merchpInfo.put("MEM_ROLE_B", "1");

        // 6- 添加产品编号
        merchpInfo.put("PRODUCT_ID", productId);

        // 7-　添加产品属性
        IDataset productParamInfoList = getProductParamInfo(map);
        merchpInfo.put("PRODUCT_PARAM_INFO", productParamInfoList);

        // 8- 添加产品元素信息
        String memProductId = getBaseMemProdId();
        IDataset elementInfo = new DatasetList();
        IDataset forceDiscnts = ProductInfoQry.getProductForceDiscnt(memProductId);
        for (int i = 0; i < forceDiscnts.size(); i++)
        {
            IData forceDct = forceDiscnts.getData(i);
            IData dctElement = new DataMap();
            getElementInfo(forceDct, dctElement);
            elementInfo.add(dctElement);
        }
        if (null == elementInfo || elementInfo.size() == 0)
        {
            merchpInfo.put("ELEMENT_INFO", "");
        }
        else
        {
            merchpInfo.put("ELEMENT_INFO", elementInfo);
        }

        // 9- 添加BBOSS侧的产品信息
        IData productInfo = new DataMap();
        productInfo.put("MEB_TYPE", map.getString("USER_TYPE", ""));
        productInfo.put("EFF_DATE", dealDate(map.getString("EFFDATE", ""), "EFFDATE"));
        merchpInfo.put("PRODUCT_INFO", productInfo);

        // 10- 添加反向受理标记(反向受理不发服务开通)
        merchpInfo.put("IN_MODE_CODE", "6");
        // 11成员特殊属性处理
        MebDisAttrTransBean.attrDataToDisData(merchpInfo);// 成员特殊属性资费互换

        // 12- 将产品对象添加至返回数据
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

    protected static boolean isJKDTBBRelExist(String productUserId, String memUserId) throws Exception
    {
        // 1- 定义返回结果
        boolean isExist = false;

        // 2- 查询台帐表
        IDataset merchMemTradeBBs = TradeRelaBBInfoQry.qryJKDTRelaBBInfoListByuserIdAB(productUserId, memUserId, BizRoute.getRouteId());
        if (IDataUtil.isNotEmpty(merchMemTradeBBs))
        {
            IData merchMemTradeBB = merchMemTradeBBs.getData(0);
            if (merchMemTradeBB.getString("MODIFY_TAG").equals("0"))
            {
                isExist = true;
            }
        }

        // 3- 查询资料表
        if (!isExist)
        {
            String productId = GrpCommonBean.getProductIdByUserId(productUserId);
            // 此处实际上既有商品的查询也有产品的查询，统一当作产品查询不影响结果
            String relationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(productId, "", true);
            IDataset merchMemBBs = RelaBBInfoQry.getBBByUserIdAB(productUserId, memUserId, "1", relationTypeCode);
            if (IDataUtil.isNotEmpty(merchMemBBs))
            {
                isExist = true;
            }
        }

        // 4- 返回结果
        return isExist;
    }

    protected static void makeJKDTMerchPInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 检查成员和产品间的BB关系是否存在，存在需要抛出异常
        boolean isBBRelExist = false;
        if (!"".equals(memUserId))
        {
            isBBRelExist = isJKDTBBRelExist(productUserId, memUserId);
        }
        if (isBBRelExist)
        {
            CSAppException.apperr(UUException.CRM_UU_88);
        }

        // 2- 定义产品对象
        IData merchpInfo = new DataMap();

        // 3- 添加产品用户编号
        merchpInfo.put("USER_ID", productUserId);

        // 4- 添加虚拟手机号
        String memSerialNumber = map.getString("SERIAL_NUMBER", "");
        merchpInfo.put("SERIAL_NUMBER", memSerialNumber);

        // 5- 添加角色编号(0:BBOSS商品,1:BBOSS成员)
        merchpInfo.put("MEM_ROLE_B", "1");

        // 6- 添加产品编号
        merchpInfo.put("PRODUCT_ID", productId);

        // 7-　添加产品属性
        IDataset productParamInfoList = getProductParamInfo(map);
        merchpInfo.put("PRODUCT_PARAM_INFO", productParamInfoList);

        // 8- 添加产品元素信息
        String memProductId = getBaseMemProdId();
        IDataset elementInfo = new DatasetList();
        IDataset forceDiscnts = ProductInfoQry.getProductForceDiscnt(memProductId);
        for (int i = 0; i < forceDiscnts.size(); i++)
        {
            IData forceDct = forceDiscnts.getData(i);
            IData dctElement = new DataMap();
            getElementInfo(forceDct, dctElement);
            elementInfo.add(dctElement);
        }
        if (null == elementInfo || elementInfo.size() == 0)
        {
            merchpInfo.put("ELEMENT_INFO", "");
        }
        else
        {
            merchpInfo.put("ELEMENT_INFO", elementInfo);
        }

        // 9- 添加BBOSS侧的产品信息
        IData productInfo = new DataMap();
        productInfo.put("MEB_TYPE", map.getString("USER_TYPE", ""));
        productInfo.put("EFF_DATE", dealDate(map.getString("EFFDATE", ""), "EFFDATE"));
        merchpInfo.put("PRODUCT_INFO", productInfo);

        // 10- 添加反向受理标记(反向受理不发服务开通)
        merchpInfo.put("IN_MODE_CODE", "6");
        // 11成员特殊属性处理
        MebDisAttrTransBean.attrDataToDisData(merchpInfo);// 成员特殊属性资费互换

        // 12- 将产品对象添加至返回数据
        returnVal.put("ORDER_INFO", IDataUtil.idToIds(merchpInfo));
    }

}

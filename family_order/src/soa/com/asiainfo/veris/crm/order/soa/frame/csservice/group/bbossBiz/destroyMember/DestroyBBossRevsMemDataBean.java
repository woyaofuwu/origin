
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.destroyMember;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.DbException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.exception.UUException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

public class DestroyBBossRevsMemDataBean extends GroupBean
{

    protected static boolean isOutNetSnExist = true;// 默认为往外号码存在

    protected static String memUserId = "";// 成员用户编号

    protected static String productUserId = "";// 产品用户编号

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

        String receptionHallMem=map.getString("RECEPTIONHALLMEM","");//集客大厅受理标记
        IDataset productUserInfoList;
        productUserInfoList = UserGrpMerchpInfoQry.qryMerchpInfoByProductOfferId(productOfferId);

        if (productUserInfoList == null || productUserInfoList.size() == 0)
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_141, IntfField.BbossGrpMemBizRet.ERR11[0], IntfField.BbossGrpMemBizRet.ERR11[1], productOfferId);
        }
        IData productUserInfo = productUserInfoList.getData(0);

        // 3- 获取商品关系类型编码
        String merchSpecCode = productUserInfo.getString("MERCH_SPEC_CODE");// BBOSS侧商品用户编码
        String merchId = merchToProduct(merchSpecCode, 0);
        String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchId, "", true);

        // 4- 获取产品用户编号
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

        IDataset productUserInfoList;
        productUserInfoList =UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(productOfferId);

        if (productUserInfoList == null || productUserInfoList.size() == 0)
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_141, IntfField.BbossGrpMemBizRet.ERR11[0], IntfField.BbossGrpMemBizRet.ERR11[1], productOfferId);
        }
        IData productUserInfo = productUserInfoList.getData(0);

        // 3- 获取商品关系类型编码
        String merchSpecCode = productUserInfo.getString("MERCH_SPEC_CODE");// BBOSS侧商品用户编码
        String merchId = GrpCommonBean.merchJKDTToProduct(merchSpecCode, 0, null);
        String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchId, "", true);

        // 4- 获取产品用户编号
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
     * @description 循环商产品BB关系判断成员是否为商品下的最后一个成员
     * @author xunyl
     * @date 2013-07-15
     */
    protected static boolean isLastMem(String memUserId, String memEparchCode, IDataset relationBBInfoList) throws Exception
    {
        // 1- 定义返回数据
        boolean isLastMem = false;

        // 2- 循环BB关系，获取产品和成员间的BB关系记录数
        int count = 0;
        for (int i = 0; i < relationBBInfoList.size(); i++)
        {
            String userIdA = relationBBInfoList.getData(i).getString("USER_ID_B");
            // 查询台账表数据
            if (!productUserId.equals(userIdA))
            {
                IDataset productMemberTradeBBs = TradeRelaBBInfoQry.qryRelaBBInfoListByuserIdAB(userIdA, memUserId, memEparchCode);
                if (IDataUtil.isNotEmpty(productMemberTradeBBs))
                {
                    for (int j = 0; j < productMemberTradeBBs.size(); j++)
                    {
                        if (productMemberTradeBBs.getData(j).getString("MODIFY_TAG").equals("0"))
                        {
                            count++;
                        }
                        else if (productMemberTradeBBs.getData(j).getString("MODIFY_TAG").equals("1"))
                        {
                            count--;
                        }
                    }
                }
            }

            // 查询资料表数据
            String merchpId = GrpCommonBean.getProductIdByUserId(userIdA);
            String merchpRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId("", merchpId, false);
            IDataset productMemberBBInfoList = RelaBBInfoQry.getBBByUserIdAB(userIdA, memUserId, "1", merchpRelationTypeCode);
            if (IDataUtil.isNotEmpty(productMemberBBInfoList))
            {
                count++;
            }
        }
        // 如果BB关系记录数为1则说明是商品下的最后一个成员
        if (count == 1)
        {
            isLastMem = true;
        }

        // 3- 返回结果
        return isLastMem;
    }

    /*
     * @description 检查成员是否为商品下的最后一个成员
     * @author xunyl
     * @date 2013-07-15
     */
    protected static boolean isLastMem(String merchUserId, String memUserId, String memEparchCode) throws Exception
    {
        // 1- 定义返回数据
        boolean isLastMem = false;

        // 2- 根据商品用户编号获取客户编号
        IData productUserInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(merchUserId, "0");
        String custId = productUserInfo.getString("CUST_ID");

        // 3- 根据台帐表与资料表的数据获取返回值
        String merchId = GrpCommonBean.getProductIdByUserId(merchUserId);
        String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchId, "", true);
        IDataset relationBBInfoList = RelaBBInfoQry.qryRelaBBByInfoByUserIdBCustId(merchUserId, custId, merchRelationTypeCode);
        isLastMem = isLastMem(memUserId, memEparchCode, relationBBInfoList);

        // 4- 返回结果
        return isLastMem;
    }

    /*
     * @description 根据(商品/产品)用户编号查询(商品/产品)与成员间是否存在BB关系信息
     * @author xunyl
     * @date 2013-07-11
     */
    protected static boolean isRelaBBExist(String productUserId, String memUserId) throws Exception
    {
        // 1- 定义返回结果
        boolean isExist = false;

        // 2- 查询台帐表
        IDataset merchMemTradeBBInfoList = TradeRelaBBInfoQry.qryRelaBBInfoListByuserIdAB(productUserId, memUserId, BizRoute.getRouteId());
        if (IDataUtil.isNotEmpty(merchMemTradeBBInfoList))
        {
            IData merchMemTradeBBInfo = merchMemTradeBBInfoList.getData(0);
            if (merchMemTradeBBInfo.getString("MODIFY_TAG").equals("0"))
            {
                isExist = true;
            }
        }

        // 3- 查询资料表
        if (!isExist)
        {
            String productId = GrpCommonBean.getProductIdByUserId(productUserId);
            // 这里传入的productId有可能为商品编号，也可能为产品编号，统一当作商品编号处理，不影响程序
            String merchRealtionTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(productId, "", true);
            IDataset merchMemBBInfoList = RelaBBInfoQry.getBBByUserIdAB(productUserId, memUserId, "1", merchRealtionTypeCode);
            if (IDataUtil.isNotEmpty(merchMemBBInfoList))
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
        // 1- 查询成员信息是否存在，如果不存在，则抛出异常
        memUserId = getMemberUserId(map);
        if ("".equals(memUserId) && isOutNetSnExist == false)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_541);
        }

        // 2- 定义商品对象
        IData merchInfo = new DataMap();

        // 3- 添加商品用户编号
        String merchUserId = getMerchUserId(map);
        merchInfo.put("USER_ID", merchUserId);

        // 4- 添加成员手机号
        String memSerialNumber = map.getString("SERIAL_NUMBER", "");
        merchInfo.put("SERIAL_NUMBER", memSerialNumber);

        // 5- 添加产品用户编号(网外号做注销时，有可能需要删除虚拟三户信息)
        merchInfo.put("PRODUCT_USER_ID", productUserId);

        // 6- 添加反向受理标记(反向受理不发服务开通)
        merchInfo.put("IN_MODE_CODE", "6");

        // 7- 检查成员与商品间的BB关系是否存在，不存在需要抛出异常
        boolean isRelaBBExist = false;
        isRelaBBExist = isRelaBBExist(productUserId, memUserId);
        if (!isRelaBBExist)
        {
            CSAppException.apperr(UUException.CRM_UU_89);
        }

        // 8- 检查该成员是否为该商品下的最后一个成员，非最后一个成员无需拼商品数据(无需解除商品与成员的BB关系)
        IData memUserInfoList = UcaInfoQry.qryUserMainProdInfoBySn(memSerialNumber, RouteInfoQry.getEparchyCodeBySnForCrm(memSerialNumber));
        if (null == memUserInfoList || memUserInfoList.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_97);
        }
        String memUserId = memUserInfoList.getString("USER_ID");
        String memEparchCode = memUserInfoList.getString("EPARCHY_CODE");
        boolean isLastMem = false;
        isLastMem = isLastMem(merchUserId, memUserId, memEparchCode);
        if (!isLastMem)
        {
            merchInfo = new DataMap();
        }

        // 9- 将商品信息添加至返回结果集
        returnVal.put("MERCH_INFO", merchInfo);
    }

    protected static void  makeJKDTMerchInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 查询成员信息是否存在，如果不存在，则抛出异常
        memUserId = getMemberUserId(map);
        if ("".equals(memUserId) && isOutNetSnExist == false)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_541);
        }

        // 2- 定义商品对象
        IData merchInfo = new DataMap();

        // 3- 添加商品用户编号
        String merchUserId = getJKDTMerchUserId(map);
        merchInfo.put("USER_ID", merchUserId);

        // 4- 添加成员手机号
        String memSerialNumber = map.getString("SERIAL_NUMBER", "");
        merchInfo.put("SERIAL_NUMBER", memSerialNumber);

        // 5- 添加产品用户编号(网外号做注销时，有可能需要删除虚拟三户信息)
        merchInfo.put("PRODUCT_USER_ID", productUserId);

        // 6- 添加反向受理标记(反向受理不发服务开通)
        merchInfo.put("IN_MODE_CODE", "6");

        // 7- 检查成员与商品间的BB关系是否存在，不存在需要抛出异常
        boolean isRelaBBExist = false;
        isRelaBBExist = isJKDTRelaBBExist(productUserId, memUserId);
        if (!isRelaBBExist)
        {
            CSAppException.apperr(UUException.CRM_UU_89);
        }

        // 8- 检查该成员是否为该商品下的最后一个成员，非最后一个成员无需拼商品数据(无需解除商品与成员的BB关系)
        IData memUserInfoList = UcaInfoQry.qryUserMainProdInfoBySn(memSerialNumber, RouteInfoQry.getEparchyCodeBySnForCrm(memSerialNumber));
        if (null == memUserInfoList || memUserInfoList.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_97);
        }
        String memUserId = memUserInfoList.getString("USER_ID");
        String memEparchCode = memUserInfoList.getString("EPARCHY_CODE");
        boolean isLastMem = false;
        isLastMem = isLastMem(merchUserId, memUserId, memEparchCode);
        if (!isLastMem)
        {
            merchInfo = new DataMap();
        }

        // 9- 将商品信息添加至返回结果集
        returnVal.put("MERCH_INFO", merchInfo);
    }

    /*
     * @description 拼装产品数据
     * @author xunyl
     * @date 2013-07-11
     */
    protected static void makeMerchPInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 检查成员和产品间的BB关系是否存在，不存在需要抛出异常
        boolean isRelaBBExist = false;
        if (!"".equals(memUserId))
        {
            isRelaBBExist = isRelaBBExist(productUserId, memUserId);
        }
        if (!isRelaBBExist)
        {
            CSAppException.apperr(UUException.CRM_UU_90);
        }

        // 2- 定义产品对象
        IData merchpInfo = new DataMap();

        // 3- 添加产品用户编号
        merchpInfo.put("USER_ID", productUserId);

        // 4- 添加成员手机号
        String memSerialNumber = map.getString("SERIAL_NUMBER", "");
        merchpInfo.put("SERIAL_NUMBER", memSerialNumber);

        // 5- 添加反向受理标记(反向受理不发服务开通)
        merchpInfo.put("IN_MODE_CODE", "6");

        // 6- 将产品信息添加至返回结果集
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
        // 1- 检查成员和产品间的BB关系是否存在，不存在需要抛出异常
        boolean isRelaBBExist = false;
        if (!"".equals(memUserId))
        {
            isRelaBBExist = isJKDTRelaBBExist(productUserId, memUserId);
        }
        if (!isRelaBBExist)
        {
            CSAppException.apperr(UUException.CRM_UU_90);
        }

        // 2- 定义产品对象
        IData merchpInfo = new DataMap();

        // 3- 添加产品用户编号
        merchpInfo.put("USER_ID", productUserId);

        // 4- 添加成员手机号
        String memSerialNumber = map.getString("SERIAL_NUMBER", "");
        merchpInfo.put("SERIAL_NUMBER", memSerialNumber);

        // 5- 添加反向受理标记(反向受理不发服务开通)
        merchpInfo.put("IN_MODE_CODE", "6");

        // 6- 将产品信息添加至返回结果集
        returnVal.put("ORDER_INFO", IDataUtil.idToIds(merchpInfo));
    }

    protected static boolean isJKDTRelaBBExist(String productUserId, String memUserId) throws Exception
    {
        // 1- 定义返回结果
        boolean isExist = false;

        // 2- 查询台帐表
        IDataset merchMemTradeBBInfoList = TradeRelaBBInfoQry.qryJKDTRelaBBInfoListByuserIdAB(productUserId, memUserId, BizRoute.getRouteId());
        if (IDataUtil.isNotEmpty(merchMemTradeBBInfoList))
        {
            IData merchMemTradeBBInfo = merchMemTradeBBInfoList.getData(0);
            if (merchMemTradeBBInfo.getString("MODIFY_TAG").equals("0"))
            {
                isExist = true;
            }
        }

        // 3- 查询资料表
        if (!isExist)
        {
            String productId = GrpCommonBean.getJKDTProductIdByUserId(productUserId);
            // 这里传入的productId有可能为商品编号，也可能为产品编号，统一当作商品编号处理，不影响程序
            String merchRealtionTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(productId, "", true);
            IDataset merchMemBBInfoList = RelaBBInfoQry.getBBByUserIdAB(productUserId, memUserId, "1", merchRealtionTypeCode);
            if (IDataUtil.isNotEmpty(merchMemBBInfoList))
            {
                isExist = true;
            }
        }

        // 4- 返回结果
        return isExist;
    }
}

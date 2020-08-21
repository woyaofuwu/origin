
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeMember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.MebDisAttrTransBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class ChangeBBossMemDataBean extends GroupBean
{
    private static final long serialVersionUID = 1L;

    protected static IData productGoodInfos = new DataMap();// BBOSS侧的商产品信息

    protected static String productUserId = "";

    /*
     * 根据成员操作类型，分情况处理订单
     */
    protected static IData dealChgBBossMebBiz(String memberOperType, IData idata, IData productInfo) throws Exception
    {
        IData returnVal = new DataMap();
        if (memberOperType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_ADD.getValue()))
        {// 成员新增

            returnVal = dealNewMebBiz(idata, productInfo);

        }
        else if (memberOperType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_MODIFY.getValue()) || memberOperType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_PASTE.getValue())
                || memberOperType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_CONTINUE.getValue()) || memberOperType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_MODIFY_PARAM.getValue()))
        {// 成员变更

            returnVal = dealChgMebBiz(idata, productInfo);

        }
        else if (memberOperType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_CANCLE.getValue()))
        {// 成员注销

            returnVal = dealDelMebBiz(idata, productInfo);

        }

        return returnVal;
    }

    /*
     * 拼装成员变更的数据(暂停,恢复，变更成员类型，变更成员扩展属性)
     * @remark 本次改造后变更的数据中只涉及产品数据，不涉及商品数据，即商品数据不进行变更
     */
    protected static IData dealChgMebBiz(IData idata, IData productInfo) throws Exception
    {
        // 1- 定义返回数据对象
        IData lastMap = new DataMap();

        // 2- 添加操作类型标志
        lastMap.put("DEAL_TYPE", GroupBaseConst.MEM_CHANGE_DEAL_TYPE.OPER_CHANGE_MEM_CHANGE.getValue());

        // 3- 添加商品信息
        IData merch = new DataMap();
        lastMap.put("MERCH_INFO", merch);

        // 4- 添加产品信息
        IData merchp = getChgOpChgMemData(idata, productInfo);
        lastMap.put("ORDER_INFO", IDataUtil.idToIds(merchp));

        // 5- 返回结果
        return lastMap;
    }

    /*
     * 拼装成员注销的数据
     */
    protected static IData dealDelMebBiz(IData idata, IData productInfo) throws Exception
    {
        // 1- 定义返回数据对象
        IData lastMap = new DataMap();

        // 2- 添加操作类型标志
        lastMap.put("DEAL_TYPE", GroupBaseConst.MEM_CHANGE_DEAL_TYPE.OPER_CHANGE_MEM_DEL.getValue());

        // 3- 根据成员手机号码获取成员用户
        String memSerialNumber = idata.getString("SERIAL_NUMBER", "");
        IData memUserInfoList = UcaInfoQry.qryUserMainProdInfoBySn(memSerialNumber, RouteInfoQry.getEparchyCodeBySnForCrm(memSerialNumber));
        if (null == memUserInfoList || memUserInfoList.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_97);
        }
        String memUserId = memUserInfoList.getString("USER_ID");
        String memEparchCode = memUserInfoList.getString("EPARCHY_CODE");

        // 3- 添加商品信息
        IData merchInfo = new DataMap();
        String merchUserId = idata.getString("USER_ID", "");
        productUserId = productInfo.getString("USER_ID");
        boolean isLastMem = false;
        isLastMem = isLastMem(merchUserId, memUserId, memEparchCode);
        if (isLastMem)
        {
            merchInfo.put("USER_ID", merchUserId);
            merchInfo.put("SERIAL_NUMBER", memSerialNumber);
        }
        lastMap.put("MERCH_INFO", merchInfo);

        // 4- 添加产品信息
        IData merchpInfo = new DataMap();
        merchpInfo.put("USER_ID", productUserId);
        merchpInfo.put("SERIAL_NUMBER", idata.getString("SERIAL_NUMBER", ""));
        lastMap.put("ORDER_INFO", IDataUtil.idToIds(merchpInfo));

        // 5- 返回结果
        return lastMap;
    }

    /*
     * 拼装成员新增的数据
     * @remark 因为在成员变更操作中新增成员，商品与成员之间的关系是之前已经建立过的，因此只需要拼装当前订购的成员产品信息
     */
    protected static IData dealNewMebBiz(IData idata, IData productInfo) throws Exception
    {
        // 1- 定义返回数据对象
        IData lastMap = new DataMap();

        // 2- 添加操作类型标志
        lastMap.put("DEAL_TYPE", GroupBaseConst.MEM_CHANGE_DEAL_TYPE.OPER_CHANGE_MEM_ADD.getValue());

        // 3- 添加商品信息
        IData merch = new DataMap();
        lastMap.put("MERCH_INFO", merch);

        // 4- 添加产品信息
        IData merchP = getChgOpAddMemData(idata, productInfo);
        lastMap.put("ORDER_INFO", IDataUtil.idToIds(merchP));

        // 5- 返回结果
        return lastMap;
    }

    /*
     * @description 拼装成员变更操作中成员新增的产品与成员数据
     * @author xunyl
     * @date 2013-07-18
     */
    protected static IData getChgOpAddMemData(IData idata, IData productInfo) throws Exception
    {
        // 1- 定义返回的产品数据对象
        IData merchP = new DataMap();

        // 2- 添加用户编号
        String userId = productInfo.getString("USER_ID");
        merchP.put("USER_ID", userId);

        // 3- 添加成员产品参数信息
        IDataset productParamList = getProductInfo(userId, productInfo);
        merchP.put("PRODUCT_PARAM_INFO", productParamList);

        // 4- 添加成员产品元素信息
        IData productElementMap = productGoodInfos.getData("PRODUCTS_ELEMENT");
        IDataset productElementInfo = productElementMap.getDataset(userId);
        merchP.put("ELEMENT_INFO", productElementInfo);

        // 5- 添加成员手机号码
        merchP.put("SERIAL_NUMBER", idata.getString("SERIAL_NUMBER", ""));

        // 6- 添加成员角色(0为BBOSS商品角色，1为BBOSS成员角色)
        merchP.put("MEM_ROLE_B", "1");

        // 7- 添加成员产品编号
        merchP.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID"));

        // 8- 添加BBOSS侧产品信息（成员类型和期望生效时间）
        merchP.put("PRODUCT_INFO", productInfo);

        // 9- 返回数据
        return merchP;
    }

    /*
     * @description 拼装成员暂停、恢复、变更扩展属性、变更成员类型、重置序列号等操作类型的产品与成员数据
     * @author xunyl
     * @date 2013-07-18
     */
    protected static IData getChgOpChgMemData(IData idata, IData productInfo) throws Exception
    {
        // 1- 定义返回的产品数据对象
        IData merchP = new DataMap();

        // 2- 添加用户编号
        String userId = productInfo.getString("USER_ID");
        merchP.put("USER_ID", userId);

        // 3- 添加成员产品参数信息
        IDataset productParamList = getProductInfo(userId, productInfo);
        merchP.put("PRODUCT_PARAM_INFO", productParamList);

        // 4- 添加成员产品元素信息
        IData productElementMap = productGoodInfos.getData("PRODUCTS_ELEMENT");
        IDataset productElementInfo = productElementMap.getDataset(userId);
        merchP.put("ELEMENT_INFO", productElementInfo);

        // 5- 添加成员手机号码
        merchP.put("SERIAL_NUMBER", idata.getString("SERIAL_NUMBER", ""));

        // 6- 添加成员角色(0为BBOSS商品角色，1为BBOSS成员角色)
        merchP.put("REMARK", idata.getString("REMARK"));

        // 7- 添加成员产品编号
        merchP.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID"));

        // 8- 添加BBOSS侧产品信息（成员操作类型、成员类型和期望生效时间）
        merchP.put("PRODUCT_INFO", productInfo);

        // 9- 返回数据
        return merchP;
    }

    /*
     * 根据商品用户编号获取商品用户数据
     */
    protected static IData getMerchUserInfo(String userId) throws Exception
    {
        IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        if (userInfo == null && userInfo.size() < 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        return userInfo;
    }

    /*
     * 根据产品用户ID获取产品参数信息
     */
    protected static IDataset getProductInfo(String userId, IData productInfo) throws Exception
    {
        IData productParamMap = productGoodInfos.getData("PRODUCT_PARAM");
        IData productParam = productParamMap.getData(userId);// 获取用户对应的成员属性
        String productId = productInfo.getString("PRODUCT_ID");
        IDataset merchPParams = GrpCommonBean.merchPParamsToDataset(productParam);
        IDataset productParamList = new DatasetList();// 这里用List来传递成员产品参数其实只是为了符合框架结构，因为List里面只有一个Map
        IData productParamObj = new DataMap();// 放置成员基础产品编号和对应的产品参数
        productParamObj.put("PRODUCT_PARAM", merchPParams);
        productParamObj.put("PRODUCT_ID", productId);// 成员基础产品编号
        productParamList.add(productParamObj);
        return productParamList;
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
            IDataset productMemberBBs = RelaBBInfoQry.getBBByUserIdAB(userIdA, memUserId, "1", merchpRelationTypeCode);
            if (IDataUtil.isNotEmpty(productMemberBBs))
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

    public static IData makeData(IData map) throws Exception
    {
        // 1- 定义返回数据集
        IData returnVal = new DataMap();

        // 2- 拼装返回数据集(一次成员操作只能做一笔业务，例如同一成员不能同时与同一商品的两个或者多个成员产品解除成员关系或者新增成员关系)
        productGoodInfos = map.getData("BBOSS_INFO");
        IDataset productInfoList = productGoodInfos.getDataset("PRODUCT_INFO_LIST");
        IData productInfo = productInfoList.getData(0);
        String memberOperType = productInfo.getString("MEB_OPER_CODE");
        returnVal = dealChgBBossMebBiz(memberOperType, map, productInfo);

        MebDisAttrTransBean.disDataToAttrData(returnVal.getData("MERCH_INFO"));// 成员特殊属性资费互换 商品
        MebDisAttrTransBean.disDataToAttrData(returnVal.getDataset("ORDER_INFO").getData(0));// 成员特殊属性资费互换 商品
        // 3- 返回数据集
        return returnVal;
    }

    public static IData makeJKDTData(IData map) throws Exception
    {
        // 1- 定义返回数据集
        IData returnVal = new DataMap();

        // 2- 拼装返回数据集(一次成员操作只能做一笔业务，例如同一成员不能同时与同一商品的两个或者多个成员产品解除成员关系或者新增成员关系)
        productGoodInfos = map.getData("BBOSS_INFO");
        IDataset productInfoList = productGoodInfos.getDataset("PRODUCT_INFO_LIST");
        IData productInfo = productInfoList.getData(0);
        String memberOperType = productInfo.getString("MEB_OPER_CODE");
        returnVal = dealChgBBossMebBiz(memberOperType, map, productInfo);

        MebDisAttrTransBean.disDataToAttrData(returnVal.getData("MERCH_INFO"));// 成员特殊属性资费互换 商品
        MebDisAttrTransBean.disDataToAttrData(returnVal.getDataset("ORDER_INFO").getData(0));// 成员特殊属性资费互换 商品
        // 3- 返回数据集
        return returnVal;
    }
}

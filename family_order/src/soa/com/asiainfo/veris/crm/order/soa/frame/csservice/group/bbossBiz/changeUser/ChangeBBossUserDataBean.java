
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeUser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.DealBBossDiscntDateBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpGenSn;

/*
 * @description 该类专门用于处理各接口发过来的数据包，封装成商产品数据结构一致Map
 * @date 2013-05-02
 * @author xunyl
 */
public class ChangeBBossUserDataBean extends GroupBean
{

    /*
     * @description 校验变更时是否将商品对应的产品信息全部注销
     * @author xunyl
     * @date 2013-07-02
     */
    protected static void checkDelAllProduct(int delProductCount, IData map, IDataset productInfoList) throws Exception
    {
        // 1- 获取商品用户编号
        String userId = map.getString("USER_ID");

        // 2- 获取产品关系类型
        String merchId = map.getString("PRODUCT_ID");
        String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchId, "", true);
        IDataset relaBBInfo = RelaBBInfoQry.qryRelaBBInfoByRoleCodeBForGrp(userId, merchRelationTypeCode, "0");

        // 2- 子产品集的大小和取消产品订购的记录数不一致(说明商品对应的产品信息并非全部注销)
        if (delProductCount != productInfoList.size())
        {
            return;
        }

        // 3- BB关系条数与被注销子产品的数据集大小一致，则说明此次操作将所有子产品订购都取消了，抛出异常
        if (relaBBInfo.size() == delProductCount)
        {
            // 抛出异常
            CSAppException.apperr(CrmUserException.CRM_USER_786);
        }
    }

    /*
     * @description 修改商品的处理，此处又得分情况处理，新增产品订购，修改产品，取消产品订购及其它操作，其中前三种为经典场景
     * @author xunyl
     * @date 2013-05-02
     */
    protected static IData dealModifyMerch(IData map) throws Exception
    {
        // 1- 定义返回数据
        IData returnVal = new DataMap();

        // 2- 校验产品信息是否为空（修改商品的情况下，产品信息不允许为空或者没有）
        IData bbossInfo = map.getData("BBOSS_INFO");// BBOSS信息
        IDataset productInfos = bbossInfo.getDataset("PRODUCT_INFO_LIST");
        if (null == productInfos || productInfos.size() < 1)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_784);
        }

        // 3- 校验是否存在有多种商品操作类型
        isMultiMerchOperType(productInfos);

        // 4- 拼装商品数据
        setMerchInfo(map, returnVal);

        // 5- 拼装产品信息
        setMerchPInfo(map, returnVal, productInfos);

        // 6- 返回数据
        return returnVal;
    }

    /*
     * @description 商品暂停与恢复等操作类型，此种场景比较简单，只涉及商品层面的操作，不涉及产品操作
     * @author xunyl
     * @date 2013-05-02
     */
    protected static IData dealOtherMerch(IData map, String merchOperType) throws Exception
    {
        // 1- 定义返回数据集
        IData lastMap = new DataMap();

        // 2- 添加商品信息
        setMerchInfo(map, lastMap);

        // 3- 获取产品信息并将其添加至返回数据
        IDataset orderInfo = makeOtherOpData(map, merchOperType);
        lastMap.put("ORDER_INFO", orderInfo);

        // 4- 返回数据
        return lastMap;
    }

    /*
     * @description 添加商品信息，子产品处理BBOSS侧的资费表用
     * @author xunyl
     * @Date 2013-05-08
     */
    public static IData getChgMerchData(String userId) throws Exception
    {
        IData merchOutData = new DataMap();// 子产品处理BBOSS侧的资费表用
        merchOutData.put("BBOSS_TAG", "BBOSS_TAG");

        // 将商品规格编号
        // 根据商品用户编号查询BBOSS侧的商品表
        IDataset grpMerchInfo=null;
        grpMerchInfo = UserGrpMerchInfoQry.qryMerchInfoByUserIdMerchSpecStatus(userId, null, null, null);
        if(grpMerchInfo.size()==0 || grpMerchInfo==null){
        	grpMerchInfo = UserGrpMerchInfoQry.qryMerchInfoByUserIdMerchSpecStatusJkdt(userId, null, null, null);	
        }
        if (grpMerchInfo == null || grpMerchInfo.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_540);
        }
        String merchSpecCode = grpMerchInfo.getData(0).getString("MERCH_SPEC_CODE");// 商品规格编号
        merchOutData.put("MERCH_SPEC_CODE", merchSpecCode);

        // 返回商品信息
        return merchOutData;
    }

    /*
     * @description 添加商品信息，子产品新增创建BB关系时需要用到
     * @param userId 商品用户编号
     * @author xunyl
     * @date 2013-05-07
     */
    public static IData getCrtMerchData(String userId, String serialNum, String acctId) throws Exception
    {
        IData merchOutData = new DataMap();// 子产品创建时需要的商品信息
        merchOutData.put("BBOSS_TAG", "BBOSS_TAG");

        // 将商品用户编号，虚拟手机号，创建BB关系用
        merchOutData.put("USER_ID", userId);
        merchOutData.put("SERIAL_NUMBER", serialNum);

        // 将商品账户编号返回,子产品账户信息用
        merchOutData.put("ACCT_ID", acctId);

        // 将商品规格编号和支付省信息返回，BBOSS侧产品表用（TF_F_USER_GRP_MERCHP）
        // 根据用户编号查询BBOSS侧的商品表
        IDataset grpMerchInfo = UserGrpMerchInfoQry.qryMerchInfoByUserIdMerchSpecStatus(userId, null, null, null);
        if (grpMerchInfo == null || grpMerchInfo.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_540);
        }
        String merchSpecCode = grpMerchInfo.getData(0).getString("MERCH_SPEC_CODE");// 商品规格编号
        String hostCompany = grpMerchInfo.getData(0).getString("HOST_COMPANY");// 支付省信息
        merchOutData.put("MERCH_SPEC_CODE", merchSpecCode);
        merchOutData.put("HOST_COMPANY", hostCompany);
        
        // 将业务开展模式返回，判断BBOSS业务是否计费用
        String bizMode = grpMerchInfo.getData(0).getString("HOST_COMPANY"); 
        merchOutData.put("BIZ_MODE", bizMode);

        // 返回商品信息
        return merchOutData;
    }

    /*
     * @description 添加商品信息，子产品修改BB关系时需要用到
     * @param userId 商品用户编号
     * @author xunyl
     * @date 2013-05-07
     */
    public static IData getDelMerchData(String userId) throws Exception
    {
        IData merchOutData = new DataMap();// 子产品创建时需要的商品信息
        merchOutData.put("BBOSS_TAG", "BBOSS_TAG");
        merchOutData.put("USER_ID", userId);

        // 返回商品信息
        return merchOutData;
    }

    /*
     * @description 校验是否存在有多种商品操作类型(系统只支持一笔订单对应一种操作类型)
     * @author xunyl
     * @date 2013-07-02
     */
    protected static void isMultiMerchOperType(IDataset productInfos) throws Exception
    {
        // 1- 定义产品操作类型集合
        IDataset productOperTypes = new DatasetList();

        // 2- 检查修改产品属性类型是否和新增产品或者删除产品类型同时存在(产品新增和产品删除都属于修改商品组合关系，可以同时存在)，同时存在抛出异常
        for (int i = 0; i < productInfos.size(); i++)
        {
            String productOperType = productInfos.getData(i).getString("PRODUCT_OPER_CODE");
            // 如果产品未做变更，直接退出
            if ("EXIST".equals(productOperType))
            {
                continue;
            }

            if (productOperTypes.size() > 0 && !productOperTypes.contains(productOperType))
            {
                if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PARAM.getValue().equals(productOperType) || GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_DISCNT.getValue().equals(productOperType)
                        || GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_LOCALDISCNT.getValue().equals(productOperType) || productOperTypes.contains(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PARAM.getValue())
                        || productOperTypes.contains(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_DISCNT.getValue()) || productOperTypes.contains(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_LOCALDISCNT.getValue()))
                {

                    CSAppException.apperr(CrmUserException.CRM_USER_785);

                }

            }
            productOperTypes.add(productOperType);
        }
    }

    /*
     * 封装产品操作为取消产品订购的数据
     * @author xunyl
     * @Date 2013-05-03
     */
    protected static void makeCancelOpData(IData map, IData productInfo, IDataset merchPInfoList) throws Exception
    {
        // 1- 定义符合基类处理的产品信息
        IData merchpInfo = new DataMap();

        // 2- 添加产品用户编号
        String userId = productInfo.getString("USER_ID");
        merchpInfo.put("USER_ID", userId);

        // 3- 添加预约标志(BBOSS侧没有预约)
        merchpInfo.put("IF_BOOKING", "false");

        // 4- 添加取消订购原因
        merchpInfo.put("REASON_CODE", "");

        // 5- 添加备注信息
        merchpInfo.put("REMARK", "");

        // 6- 添加处理标志(商品变更，产品注销)
        merchpInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_CANCEL.getValue());

        // 7- 添加商品信息，子产品修改BB关系时需要用到
        IData merchOutData = getDelMerchData(map.getString("USER_ID"));
        merchpInfo.put("OUT_MERCH_INFO", merchOutData);

        // 8-拼装特殊参数数据
        IData bbossParamMapData = map.getData("BBossParamInfo");
        if (IDataUtil.isNotEmpty(bbossParamMapData))
        {
            IData bbossParamInfo = new DataMap();
            bbossParamInfo.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID"));
            bbossParamInfo.put("PRODUCT_PARAM", bbossParamMapData.getDataset(productInfo.getString("PRODUCT_ID")));
            merchpInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(bbossParamInfo));
        }

        // 8- 添加单条产品至产品数据集
        merchPInfoList.add(merchpInfo);
    }

    /*
     * 封装产品操作为修改订购产品资费的数据
     * @author xunyl
     * @Date 2013-05-03
     */
    protected static void makeChgDisOpData(IData map, IData productInfo, IDataset merchPInfoList, String productOperType) throws Exception
    {
        // 1- 定义符合基类处理的产品信息
        IData merchpInfo = new DataMap();

        // 2- 添加产品用户编号
        String userId = productInfo.getString("USER_ID");
        merchpInfo.put("USER_ID", userId);

        // 3- 添加产品编号
        String productId = productInfo.getString("PRODUCT_ID");
        merchpInfo.put("PRODUCT_ID", productId);

        // 4- 添加处理标志(商品变更，产品属性变更)
        merchpInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_CHANGE.getValue());

        // 5- 添加元素信息
        String productIndex = productInfo.getString("PRODUCT_INDEX");
        IData bbossInfo = map.getData("BBOSS_INFO");
        IData productElement = bbossInfo.getData("PRODUCTS_ELEMENT");
        String payMode = map.getData("BBOSS_INFO").getData("GOOD_INFO").getString("PAY_MODE");
        IDataset merchPElements = productElement.getDataset(productId + "_" + productIndex);
        merchPElements = DealBBossDiscntDateBean.dealDiscntStartDate(merchPElements, productInfo, payMode);
        merchpInfo.put("ELEMENT_INFO", merchPElements);

        // 6- 添加定制信息
        IData productGoodInfos = map.getData("BBOSS_INFO");
        IDataset grpPackageInfoList = new DatasetList();
        IData grpPackageInfo = productGoodInfos.getData("GRP_PACKAGE_INFO");
        if (IDataUtil.isNotEmpty(grpPackageInfo))
        {
            String strGrpPackageInfo = grpPackageInfo.getString(productId + "_" + productIndex);
            if (StringUtils.isNotBlank(strGrpPackageInfo))
            {
                grpPackageInfoList = new DatasetList(strGrpPackageInfo);
            }
        }
        merchpInfo.put("GRP_PACKAGE_INFO", grpPackageInfoList);

        // 6- 添加产品操作类型
        merchpInfo.put("PRODUCT_OPER_TYPE", productOperType);

        // 7- 添加商品信息，子产品处理BBOSS侧的资费表用
        IData merchOutData = getChgMerchData(map.getString("USER_ID"));
        merchpInfo.put("OUT_MERCH_INFO", merchOutData);

        // 8-拼装特殊参数数据
        IData bbossParamMapData = map.getData("BBossParamInfo");
        if (IDataUtil.isNotEmpty(bbossParamMapData))
        {
            IData bbossParamInfo = new DataMap();
            bbossParamInfo.put("PRODUCT_ID", productId);
            bbossParamInfo.put("PRODUCT_PARAM", bbossParamMapData.getDataset(productId));
            merchpInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(bbossParamInfo));
        }

        // 9- 添加单条产品至产品数据集
        merchPInfoList.add(merchpInfo);
    }

    /*
     * 封装产品操作为修改订购产品属性的数据
     * @author xunyl
     * @Date 2013-05-03
     */
    protected static void makeChgParamOpData(IData map, IData productInfo, IDataset merchPInfoList) throws Exception
    {
        // 1- 定义符合基类处理的产品信息
        IData merchpInfo = new DataMap();

        // 2- 添加产品用户编号
        String userId = productInfo.getString("USER_ID");
        merchpInfo.put("USER_ID", userId);

        // 3- 添加产品编号
        String productId = productInfo.getString("PRODUCT_ID");
        merchpInfo.put("PRODUCT_ID", productId);

        // 4- 添加处理标志(商品变更，产品属性变更)
        merchpInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_CHANGE.getValue());

        // 6- 产品参数信息
        IData bbossInfo = map.getData("BBOSS_INFO");
        IData bbossProductParam = bbossInfo.getData("PRODUCT_PARAM");
        String productIndex = productInfo.getString("PRODUCT_INDEX");
        IData paramMap = bbossProductParam.getData(productId + "_" + productIndex);
        IDataset merchPParams = GrpCommonBean.merchPParamsToDataset(paramMap);
        IData bbossParamInfo = new DataMap();
        bbossParamInfo.put("PRODUCT_ID", productId);
        bbossParamInfo.put("PRODUCT_PARAM", merchPParams);
        merchpInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(bbossParamInfo));

        // 7- 添加产品操作类型
        merchpInfo.put("PRODUCT_OPER_TYPE", productInfo.getString("PRODUCT_OPER_CODE"));

        // 8- 添加商品信息，子产品处理BBOSS侧的资费表用
        IData merchOutData = getChgMerchData(map.getString("USER_ID"));
        merchpInfo.put("OUT_MERCH_INFO", merchOutData);

        // 9- 立即生失效标记
        merchpInfo.put("EFFECT_NOW", map.getString("EFFECT_NOW"));

        // 10- 添加单条产品至产品数据集
        merchPInfoList.add(merchpInfo);
    }

    /*
     * @description 封装商品操作类型为恢复的产品数据
     * @param merchOperType 商品操作类型
     * @author xunyl
     * @date 2013-08-23
     */
    protected static void makeContinueOpData(IData map, IData productInfo, IDataset merchPInfoList) throws Exception
    {
        // 1- 定义符合基类处理的产品信息
        IData merchpInfo = new DataMap();

        // 2- 添加产品用户编号
        String userId = productInfo.getString("USER_ID");
        merchpInfo.put("USER_ID", userId);

        // 3- 添加产品编号
        String productId = productInfo.getString("PRODUCT_ID");
        merchpInfo.put("PRODUCT_ID", productId);

        // 4- 添加处理标志(商品变更，产品属性变更)
        merchpInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_CHANGE.getValue());

        // 6- 添加产品操作类型
        merchpInfo.put("PRODUCT_OPER_TYPE", GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CONTINUE.getValue());

        // 7-拼装特殊参数数据 待测试

        IData bbossParamMapData = map.getData("BBossParamInfo");
        if (IDataUtil.isNotEmpty(bbossParamMapData))
        {
            IData bbossParamInfo = new DataMap();
            bbossParamInfo.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID"));

            bbossParamInfo.put("PRODUCT_PARAM", bbossParamMapData.getDataset(productInfo.getString("PRODUCT_ID")));
            merchpInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(bbossParamInfo));
        }
        
        // 8- 添加单条产品至产品数据集
        merchPInfoList.add(merchpInfo);
    }

    /*
     * 封装产品操作为新装的数据
     * @author xunyl
     * @Date 2013-05-03
     */
    protected static void makeCrtOpData(IData map, IData productInfo, IDataset merchPInfoList) throws Exception
    {
        // 1- 定义符合基类处理的产品信息
        IData merchpInfo = new DataMap();

        // 2- 添加客户编号
        String userId = map.getString("USER_ID");// 商品用户编号
        IData merchUserInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        if (IDataUtil.isEmpty(merchUserInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_80);
        }
        String custId = merchUserInfo.getString("CUST_ID");
        merchpInfo.put("CUST_ID", custId);

        // 3- 添加产品编号
        String productId = productInfo.getString("PRODUCT_ID");
        merchpInfo.put("PRODUCT_ID", productId);

        // 4- 添加是否新增帐户信息(产品直接用商品的帐户信息，因此这里默认为不产生帐户)
        merchpInfo.put("ACCT_IS_ADD", "false");

        // 5- 添加帐户编号
        IData inparams = new DataMap();
        inparams.put("ID", userId);
        IData payRelation = PayRelaInfoQry.getPayRelation(inparams);
        String acctId = payRelation.getString("ACCT_ID");
        merchpInfo.put("ACCT_ID", acctId);

        // 6- 添加处理标志(商品变更，产品新增)

        merchpInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_ADD.getValue());

        // 7- 添加元素信息
        String productIndex = productInfo.getString("PRODUCT_INDEX");
        IData bbossInfo = map.getData("BBOSS_INFO");
        IData productElement = bbossInfo.getData("PRODUCTS_ELEMENT");
        IDataset merchPElements = productElement.getDataset(productId + "_" + productIndex);
        merchpInfo.put("ELEMENT_INFO", merchPElements);

        // 8- 添加产品参数信息
        IData bbossProductParam = bbossInfo.getData("PRODUCT_PARAM");
        IData paramMap = bbossProductParam.getData(productId + "_" + productIndex);
        IDataset merchPParams = GrpCommonBean.merchPParamsToDataset(paramMap);
        IData bbossParamInfo = new DataMap();
        bbossParamInfo.put("PRODUCT_ID", productId);
        bbossParamInfo.put("PRODUCT_PARAM", merchPParams);
        merchpInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(bbossParamInfo));

        // 9- 添加虚拟手机号
        IData idGen = new DataMap();
        idGen.put("GROUP_ID", UcaInfoQry.qryGrpInfoByCustId(custId).getString("GROUP_ID"));
        idGen.put("PRODUCT_ID", productId);
        String serialNumber = GrpGenSn.genGrpSn(idGen).getString("SERIAL_NUMBER");
        serialNumber = GrpCommonBean.dealSerialNumber(productId, serialNumber, merchPParams);
        merchpInfo.put("SERIAL_NUMBER", serialNumber);

        // 10- 添加商品信息，子产品新增创建BB关系时需要用到
        IData merchOutData = getCrtMerchData(map.getString("USER_ID"), merchUserInfo.getString("SERIAL_NUMBER"), acctId);
        merchpInfo.put("OUT_MERCH_INFO", merchOutData);

        // 11- BBOSS_PRODUCT_INFO仅供BBOSS子类使用，用于创建tf_f_user_grp_merchp表
        merchpInfo.put("BBOSS_PRODUCT_INFO", productInfo);

        // 12- 添加单条产品至产品数据集
        merchPInfoList.add(merchpInfo);

    }

    public static IData makeData(IData map) throws Exception
    {
        // 1- 定义符合后台基类处理的商产品数据集
        IData returnVal = new DataMap();

        // 2- 获取商品操作类型
        IData productGoodInfos = map.getData("BBOSS_INFO");
        IData merchInfo = productGoodInfos.getData("GOOD_INFO");
        String merchOperType = merchInfo.getString("MERCH_OPER_CODE");

        // 3- 根据商品操作类型分情况处理
        // 3-1 处理商品操作类型为修改商品资费，修改商品属性，修改商品组成关系、商品暂停、商品恢复
        if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_DISCNT.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue().equals(merchOperType)
                || GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_GROUP.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_PASTE.getValue().equals(merchOperType)
                || GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_LOCALDISCNT.getValue().equals(merchOperType)
                || GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PROV.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_MEB.getValue().equals(merchOperType))
        {
            returnVal = dealModifyMerch(map);
        }

        // 3-2 处理商品操作类型为商品暂停，商品回复，预取消商品订购，冷冻期恢复商品订购,取消商品订购
        else if (GroupBaseConst.MERCH_STATUS.MERCH_PREDESTORY.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_CANCLEPREDESTORY.getValue().equals(merchOperType)
                || GroupBaseConst.MERCH_STATUS.MERCH_CANCLE.getValue().equals(merchOperType))
        {
            returnVal = dealOtherMerch(map, merchOperType);
        }

        // 4- 移除商品中的BBOSS信息
        returnVal.getData("MERCH_INFO").remove("BBOSS_INFO");

        // 5- 特殊情况下，需要将拼好的标准结构进行特殊处理
        GrpCommonBean.modifyInparamsBySpecialBiz(returnVal, merchOperType);

        // 6- 返回数据集
        return returnVal;
    }
    
    public static IData makeJKDTData(IData map) throws Exception
    {
        // 1- 定义符合后台基类处理的商产品数据集
        IData returnVal = new DataMap();

        // 2- 获取商品操作类型
        IData productGoodInfos = map.getData("BBOSS_INFO");
        IData merchInfo = productGoodInfos.getData("GOOD_INFO");
        String merchOperType = merchInfo.getString("MERCH_OPER_CODE");

        // 3- 根据商品操作类型分情况处理
        // 3-1 处理商品操作类型为修改商品资费，修改商品属性，修改商品组成关系、商品暂停、商品恢复
        if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_DISCNT.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue().equals(merchOperType)
                || GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_GROUP.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_PASTE.getValue().equals(merchOperType)
                || GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_LOCALDISCNT.getValue().equals(merchOperType)
                || GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PROV.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_MEB.getValue().equals(merchOperType))
        {
            returnVal = dealModifyMerch(map);
        }

        // 3-2 处理商品操作类型为商品暂停，商品回复，预取消商品订购，冷冻期恢复商品订购,取消商品订购
        else if (GroupBaseConst.MERCH_STATUS.MERCH_PREDESTORY.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_CANCLEPREDESTORY.getValue().equals(merchOperType)
                || GroupBaseConst.MERCH_STATUS.MERCH_CANCLE.getValue().equals(merchOperType))
        {
            returnVal = dealOtherMerch(map, merchOperType);
        }

        // 4- 移除商品中的BBOSS信息
        returnVal.getData("MERCH_INFO").remove("BBOSS_INFO");

        // 5- 特殊情况下，需要将拼好的标准结构进行特殊处理
        GrpCommonBean.modifyJKDTInparamsBySpecialBiz(returnVal, merchOperType);

        // 6- 返回数据集
        return returnVal;
    }

    /*
     * @description 封装商品操作类型为暂停，恢复，预取消，冷冻期恢复商品订购等操作的产品数据
     * @param merchOperType 商品操作类型
     * @author xunyl
     * @date 2013-05-07
     */
    protected static IDataset makeOtherOpData(IData map, String merchOperType) throws Exception
    {
        // 1-取出用户编号
        String userId = map.getString("USER_ID");

        // 2-根据用户编号查询BB关系表
        String merchId = map.getString("PRODUCT_ID");
        String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchId, "", true);
        IDataset relaBBInfo = RelaBBInfoQry.qryRelaBBInfoByRoleCodeBForGrp(userId, merchRelationTypeCode, "0");

        // 3-根据BB关系表数据找到对应的子产品用户,将子产品用户编号,产品操作类型和产品编号封装到返回值中
        IDataset productInfos = new DatasetList();// 子产品数据集
        if (null == relaBBInfo)
        {
            return new DatasetList();
        }
        
      //针对流量卡业务，电子流量卡基础产品单个产品预注销进行判断，判断是商品预注销还是单个产品预注销
        IData bbossInfo = map.getData("BBOSS_INFO");
        IDataset productInfoList = bbossInfo.getDataset("PRODUCT_INFO_LIST");//获取前台传递过来的预注销产品列表
        //如果是电子流量卡业务
        if(StringUtils.equals("010190014",GrpCommonBean.productToMerch(map.getString("PRODUCT_ID"),0))){
        	for (int i = 0,j = productInfoList.size(); i < j; i++) {
				//判断电子流量卡充值产品是否需要预注销,在前台页面添加了标识，如果电子流量卡充值产品未选中。则为其PRODUCT_OPER_CODE赋值为11
        		if(StringUtils.equals("9001402",GrpCommonBean.productToMerch
        				(productInfoList.getData(i).getString("PRODUCT_ID"),0)) && 
        				productInfoList.getData(i).getString("PRODUCT_OPER_CODE").equals("11")){
        			//满足条件后，把relaBBInfo中的产品给移除掉
        			for (int k = -1,l = relaBBInfo.size()-1; k < l; l--) {
						if(relaBBInfo.getData(l).getString("USER_ID_B").equals(productInfoList.getData(i).getString("USER_ID"))){
							relaBBInfo.remove(l);
						}
					}
        		}
			}
        }
        
        for (int i = 0; i < relaBBInfo.size(); i++)
        {
            // 3-1 获取子产品用户编号
            IData rela = relaBBInfo.getData(i);
            String userIdB = rela.getString("USER_ID_B");// 子产品用户编号

            // 3-2 根据子产品用户编号查询子产品用户信息，再获取子产品编号
            IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIdB);// 子产品用户信息
            String productId = userInfo.getString("PRODUCT_ID");

            // 3-3 根据商品操作类型获取产品操作类型
            String merchPOperType = "";
            if (GroupBaseConst.MERCH_STATUS.MERCH_PREDESTORY.getValue().equals(merchOperType))// 商品预取消
            {
                merchPOperType = GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDESTORY.getValue();
            }
            else if (GroupBaseConst.MERCH_STATUS.MERCH_CANCLEPREDESTORY.getValue().equals(merchOperType))// 恢复预取消
            {
                merchPOperType = GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLEPREDESTORY.getValue();
            }
            else if (GroupBaseConst.MERCH_STATUS.MERCH_CANCLE.getValue().equals(merchOperType))// 取消商品订购
            {
                merchPOperType = GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLE.getValue();
            }

            // 3-4 获取对应的处理标志
            String dealType = "";
            if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLE.getValue().equals(merchPOperType))
            {
                dealType = GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_CANCEL.getValue();
            }
            else
            {
                dealType = GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE.getValue();
            }

            // 3-5 组装子产品信息
            IData productInfo = new DataMap();
            productInfo.put("USER_ID", userIdB);
            productInfo.put("PRODUCT_ID", productId);
            productInfo.put("PRODUCT_OPER_TYPE", merchPOperType);
            productInfo.put("DEAL_TYPE", dealType);// 处理标志

            // 3-6如果取消商品订购 拼装特殊参数
            if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLE.getValue().equals(merchPOperType))
            {
                IData bbossParamMapData = map.getData("BBossParamInfo");
                if (IDataUtil.isNotEmpty(bbossParamMapData))
                {
                    IData bbossParamInfo = new DataMap();
                    bbossParamInfo.put("PRODUCT_ID", productId);
                    bbossParamInfo.put("PRODUCT_PARAM", bbossParamMapData.getDataset(productId));
                    productInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(bbossParamInfo));
                }

            }

            // 3-7将子产品信息添加到返回值中
            productInfos.add(productInfo);
        }

        // 4-返回子产品数据包
        return productInfos;
    }

    /*
     * @description 封装商品操作类型为暂停，恢复等操作的产品数据
     * @param merchOperType 商品操作类型
     * @author xunyl
     * @date 2013-08-23
     */
    protected static void makePauseOpData(IData map, IData productInfo, IDataset merchPInfoList) throws Exception
    {
        // 1- 定义符合基类处理的产品信息
        IData merchpInfo = new DataMap();

        // 2- 添加产品用户编号
        String userId = productInfo.getString("USER_ID");
        merchpInfo.put("USER_ID", userId);

        // 3- 添加产品编号
        String productId = productInfo.getString("PRODUCT_ID");
        merchpInfo.put("PRODUCT_ID", productId);

        // 4- 添加处理标志(商品变更，产品属性变更)
        merchpInfo.put("DEAL_TYPE", GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE_PRODUCT_CHANGE.getValue());

        // 6- 添加产品操作类型
        merchpInfo.put("PRODUCT_OPER_TYPE", GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PASTE.getValue());

        // 7-拼装特殊参数数据 待测试

        IData bbossParamMapData = map.getData("BBossParamInfo");
        if (IDataUtil.isNotEmpty(bbossParamMapData))
        {
            IData bbossParamInfo = new DataMap();
            bbossParamInfo.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID"));

            bbossParamInfo.put("PRODUCT_PARAM", bbossParamMapData.getDataset(productInfo.getString("PRODUCT_ID")));
            merchpInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(bbossParamInfo));
        }

        // 8- 添加商品信息，子产品修改产品参数时候用到
        IData merchOutData = getChgMerchData(map.getString("USER_ID"));
        merchpInfo.put("OUT_MERCH_INFO", merchOutData);
        
        // 9- 添加单条产品至产品数据集
        merchPInfoList.add(merchpInfo);
    }


    /*
     * @descripiton 拼装商品数据
     * @author xunyl
     * @date 2013-07-02
     */
    protected static void setMerchInfo(IData map, IData returnVal) throws Exception
    {
        // 1- 获取BBOSS信息
        IData bbossInfo = map.getData("BBOSS_INFO");

        // 2- 添加BBOSS特有的商品信息
        IData goodInfo = bbossInfo.getData("GOOD_INFO");
        map.put("GOOD_INFO", goodInfo);

        // 3- 添加商品操作类型
        String merchOpType = goodInfo.getString("MERCH_OPER_CODE");
        map.put("PRODUCT_OPER_TYPE", merchOpType);

        // 3- 返回数据中添加商品信息
        returnVal.put("MERCH_INFO", map);
    }

    /*
     * @description 拼装产品数据
     * @author xunyl
     * @date 2013-07-02
     */
    protected static void setMerchPInfo(IData map, IData returnVal, IDataset productInfos) throws Exception
    {

        // 1- 定义产品操作类型为取消产品订购的记录数
        int delProductCount = 0;

        // 2- 定义产品数据集
        IDataset productInfoList = new DatasetList();

        // 3- 添加产品数据至数据集
        for (int i = 0; i < productInfos.size(); i++)
        {
            // 3-1 获取产品信息
            IData productInfo = productInfos.getData(i);

            // 3-1 获取产品操作类型
            String productOperType = productInfo.getString("PRODUCT_OPER_CODE");

            // 3-2 处理产品新增数据
            if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue().equals(productOperType))
            {
                makeCrtOpData(map, productInfo, productInfoList);
            }

            // 3-3 处理产品删除数据
            if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLE.getValue().equals(productOperType))
            {
                makeCancelOpData(map, productInfo, productInfoList);
                delProductCount++;
            }

            // 3-4 处理产品属性变更数据
            if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PARAM.getValue().equals(productOperType) || GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_MEB.getValue().equals(productOperType)
                    || GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PROV.getValue().equals(productOperType))
            {
                makeChgParamOpData(map, productInfo, productInfoList);
            }

            // 3-5 处理产品资费变更数据
            if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_DISCNT.getValue().equals(productOperType) || GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_LOCALDISCNT.getValue().equals(productOperType))
            {
                makeChgDisOpData(map, productInfo, productInfoList, productOperType);
            }

            // 3-6 处理产品暂停数据
            if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PASTE.getValue().equals(productOperType))
            {
                makePauseOpData(map, productInfo, productInfoList);
            }

            // 3-7 处理产品恢复数据
            if (GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CONTINUE.getValue().equals(productOperType))
            {
                makeContinueOpData(map, productInfo, productInfoList);
            }
        }

        // 4- 校验变更时是否将商品对应的产品信息全部注销
        checkDelAllProduct(delProductCount, map, productInfoList);

        // 5- 返回数据中添加产品信息
        returnVal.put("ORDER_INFO", productInfoList);

    }
}

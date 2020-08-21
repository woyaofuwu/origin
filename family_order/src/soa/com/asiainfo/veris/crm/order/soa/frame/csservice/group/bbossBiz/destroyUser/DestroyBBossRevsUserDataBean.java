
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.destroyUser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepOfferfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

public class DestroyBBossRevsUserDataBean extends GroupBean
{
    /*
     * @description 将一级BOSS传递过来的数据拼装成符合集团产品受理基类处理的数据集
     * @author xunyl
     * @date 2013-07-05
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

    /*
     * @description 拼装商品数据
     * @author xunyl
     * @date 2013-07-05
     */
    protected static void makeMerchInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 定义商品对象
        IData merchInfo = new DataMap();

        // 2- 添加商品用户编号
        String productOfferingID = IDataUtil.getDataset("RSRV_STR2", map, false).get(0).toString();
        String merchUserId = GrpCommonBean.getMerchUserIdByProdOffId(productOfferingID);

        merchInfo.put("USER_ID", merchUserId);

        // 3- 添加商品编号
        String poNumber = (String) IDataUtil.getDataset("RSRV_STR1", map, false).get(0); // BBOSS商品规格编号
        String proPoNumber = GrpCommonBean.merchToProduct(poNumber, 0, null);// 商品编号转化为本地产品编号
        merchInfo.put("PRODUCT_ID", proPoNumber);

        // 4- 添加是否预约,默认为"false"
        merchInfo.put("IF_BOOKING", "false");

        // 5- 添加取消理由,默认为空
        merchInfo.put("REASON_CODE", "");

        // 6- 添加备注,默认为空
        merchInfo.put("REMARK", "");

        // 7- 添加反向受理标记(反向受理不发服务开通)
        merchInfo.put("IN_MODE_CODE", "6");

        // 8- 添加一个页面数据 GOOD_INFO（都为空字符串） 保存附件、审批人、联系人信息（目前并未实现 暂时保持通过）
        IData good_info = GrpCommonBean.putEmptyGoodInfo();
        merchInfo.put("GOOD_INFO", good_info);

        // 9- 管理节点数据标记
        merchInfo.put("BBOSS_MANAGE_CREATE", true);

        // 10- 添加商品信息至返回结果集
        returnVal.put("MERCH_INFO", merchInfo);

    }

    /*
     * @description 拼装产品数据
     * @author xunyl
     * @date 2013-07-05
     */
    protected static void makeMerchPInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 定义符合基类处理的产品数据集
        IDataset productInfoset = new DatasetList();

        // 1- 获取产品数据的一级信息（按照一级BOSS与CRM的接口规范，将黑色部分字段设置为一级信息，红色部分字段设置为二级信息，黄色部分字段设置为三级信息）
        IData firstProductInfo = new DataMap();
        GrpCommonBean.getFirstProductInfo(map, firstProductInfo);

        // 2- 循环处理每个产品，处理后的产品数据符合基类处理的数据结构
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        IDataset productOfferIdSet = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
        IDataset productOrderIdSet = firstProductInfo.getDataset("PRO_ORDER_NUMBER");
        for (int i = 0; i < productNumberSet.size(); i++)
        {
            // 2-1 定义产品对象
            IData productInfo = new DataMap();
			
            // 2-3 添加产品订购关系ID
            String productOfferId =GrpCommonBean.nullToString(productOfferIdSet.get(i));

            // 2-4 添加产品订单号
            String productOrderId =GrpCommonBean.nullToString(productOrderIdSet.get(i));


            // 2-5 添加产品用户编号
            IDataset productOrderIds = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
            String merchpUserId = GrpCommonBean.getMerchpUserIdByProdId(productOrderIds.get(i).toString());
            productInfo.put("USER_ID", merchpUserId);

            // 2-6 添加产品编号
            String productSpecNumber = GrpCommonBean.nullToString(productNumberSet.get(i));
            String productId = GrpCommonBean.merchToProduct(productSpecNumber, 2, null);// 产品编号转化为本地产品编号
            productInfo.put("PRODUCT_ID", productId);

            // 2-7 添加是否预约,默认为"false"
            productInfo.put("IF_BOOKING", "false");

            // 2-8 添加取消理由,默认为空
            productInfo.put("REASON_CODE", "");

            // 2-9 添加备注,默认为空
            productInfo.put("REMARK", "");
			
            IData merchOutData = new DataMap();
            getProductBaseInfo(map, productOfferId, productOrderId, merchOutData);
          
            productInfo.put("OUT_MERCH_INFO", merchOutData);

            // 2-10 添加产品操作类型(仅供判断是否为归档报文用)
            productInfo.put("PRODUCT_OPER_CODE", GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLE.getValue());

            // 2-11 添加反向受理标记(反向受理不发服务开通)
            productInfo.put("IN_MODE_CODE", "6");

            // 2-12 添加单条产品至产品数据集
            productInfoset.add(productInfo);
        }

        // 3- 添加产品信息至返回结果中
        returnVal.put("ORDER_INFO", productInfoset);
    }

    /**
     * @param
     * @desciption 反向集团注销，走一键注销接口
     * @author fanti
     * @version 创建时间：2014年9月30日 下午3:56:22
     */
    protected static IDataset makeDataOneKey(IData map) throws Exception
    {
        // 1- 封装一键注销接口所需数据信息
        IData returnVal = new DataMap();

        // 2- 添加商品用户编号
        String productOfferingID = IDataUtil.getDataset("RSRV_STR2", map, false).get(0).toString();
        String merchUserId = GrpCommonBean.getMerchUserIdByProdOffId(productOfferingID);

        returnVal.put("USER_ID", merchUserId);

        // 3- 添加商品编号
        String poNumber = (String) IDataUtil.getDataset("RSRV_STR1", map, false).get(0); // BBOSS商品规格编号
        String proPoNumber = GrpCommonBean.merchToProduct(poNumber, 0, null);// 商品编号转化为本地产品编号
        returnVal.put("PRODUCT_ID", proPoNumber);

        // 4- 添加用户归属地州
        IData grpUserInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(merchUserId, "0");
        if (IDataUtil.isEmpty(grpUserInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        
        returnVal.put(Route.USER_EPARCHY_CODE, grpUserInfo.getString("EPARCHY_CODE", "cg"));

        // 5- 添加GROUP_ID
        IData grpCustInfo = UcaInfoQry.qryGrpInfoByCustId(grpUserInfo.getString("CUST_ID"));
        returnVal.put("GROUP_ID", grpCustInfo.getString("GROUP_ID"));
        
        if("010190011".equals(poNumber)||"010190014".equals(poNumber)){	
     		returnVal.put("IN_MODE_CODE", "0");//反向一键注销走服务开通
     	}
         else{
     		returnVal.put("IN_MODE_CODE", "6");//反向一键注销不走服务开通
     	}

        // 6- 反向发起的集团注销落地走一键注销接口
        IDataset retDataset = CSAppCall.call("SS.DestroyOneKeySVC.crtBat", returnVal);
        if (IDataUtil.isEmpty(retDataset))
        {
            CSAppException.apperr(BatException.CRM_BAT_95);
        }

        // 7- 返回受理成功标志
        IData dealResult = new DataMap();
        dealResult.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        dealResult.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        dealResult.put("RSPCODE", "00");
        dealResult.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
        dealResult.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
        dealResult.put("EC_SERIAL_NUMBER", IDataUtil.chkParam(map, "EC_SERIAL_NUMBER"));
        dealResult.put("SUBSCRIBE_ID", IDataUtil.chkParam(map, "SUBSCRIBE_ID"));
        dealResult.put("BATCH_INFO", retDataset.getData(0).getString("ORDER_ID"));

        return IDataUtil.idToIds(dealResult);
    }
	    
    /**
     * @description 添加产品order_id,offer_id
     * @author chengjian
     * @date 2014-11-28
     */
    protected static void getProductBaseInfo(IData map ,String productOfferId, String productOrderId, IData merchOutData ) throws Exception
    {
    	// 1- 添加BBOSS标志
    	merchOutData.put("BBOSS_TAG", "BBOSS_TAG");
    	
    	// 2- 添加商品规格编号
    	String merchSpecCode = (String) IDataUtil.getDataset("RSRV_STR1", map, false).get(0);
    	merchOutData.put("MERCH_SPEC_CODE", merchSpecCode);
    	
    	// 3- 添加产品订购关系ID BBOSS下发
        if (StringUtils.isNotEmpty(productOfferId))
        {
        	merchOutData.put("PRODUCT_OFFER_ID", productOfferId);
        }
        // 4- 添加产品订单ID BBOSS下发
        if (StringUtils.isNotEmpty(productOrderId))
        {
        	merchOutData.put("PRODUCT_ORDER_ID", productOrderId);
        }
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

    protected static void makeJKDTMerchInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 定义商品对象
        IData merchInfo = new DataMap();

        // 2- 添加商品用户编号
        String productOfferingID = IDataUtil.getDataset("RSRV_STR2", map, false).get(0).toString();
        String merchUserId = GrpCommonBean.getJKDTMerchUserIdByProdOffId(productOfferingID);

        merchInfo.put("USER_ID", merchUserId);

        // 3- 添加商品编号
        String poNumber = (String) IDataUtil.getDataset("RSRV_STR1", map, false).get(0); // BBOSS商品规格编号
        String proPoNumber = GrpCommonBean.merchJKDTToProduct(poNumber, 0, null);// 商品编号转化为本地产品编号
        merchInfo.put("PRODUCT_ID", proPoNumber);

        // 4- 添加是否预约,默认为"false"
        merchInfo.put("IF_BOOKING", "false");

        // 5- 添加取消理由,默认为空
        merchInfo.put("REASON_CODE", "");

        // 6- 添加备注,默认为空
        merchInfo.put("REMARK", "");

        // 7- 添加反向受理标记(反向受理不发服务开通)
        merchInfo.put("IN_MODE_CODE", "6");

        // 8- 添加一个页面数据 GOOD_INFO（都为空字符串） 保存附件、审批人、联系人信息（目前并未实现 暂时保持通过）
        IData good_info = GrpCommonBean.putEmptyGoodInfo();
        merchInfo.put("GOOD_INFO", good_info);

        // 9- 管理节点数据标记
        merchInfo.put("BBOSS_MANAGE_CREATE", true);

        // 10- 添加商品信息至返回结果集
        returnVal.put("MERCH_INFO", merchInfo);

    }

    protected static void makeJKDTMerchPInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 定义符合基类处理的产品数据集
        IDataset productInfoset = new DatasetList();

        // 1- 获取产品数据的一级信息（按照一级BOSS与CRM的接口规范，将黑色部分字段设置为一级信息，红色部分字段设置为二级信息，黄色部分字段设置为三级信息）
        IData firstProductInfo = new DataMap();
        GrpCommonBean.getFirstProductInfo(map, firstProductInfo);

        // 2- 循环处理每个产品，处理后的产品数据符合基类处理的数据结构
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        IDataset productOfferIdSet = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
        IDataset productOrderIdSet = firstProductInfo.getDataset("PRO_ORDER_NUMBER");
        for (int i = 0; i < productNumberSet.size(); i++)
        {
            // 2-1 定义产品对象
            IData productInfo = new DataMap();

            // 2-3 添加产品订购关系ID
            String productOfferId =GrpCommonBean.nullToString(productOfferIdSet.get(i));

            // 2-4 添加产品订单号
            String productOrderId =GrpCommonBean.nullToString(productOrderIdSet.get(i));


            // 2-5 添加产品用户编号
            //IDataset productOrderIds = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
            //IDataset merchpUserInfos = UserEcrecepProductInfoQry.qryEcrEceppInfosByPro(productOfferingId,null);
            IDataset productUserInfoList =UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(productOfferId);
            if (null == productUserInfoList || productUserInfoList.size() == 0)
            {
                CSAppException.apperr(CrmUserException.CRM_USER_914, productOfferId);
            }
            // 2- 获取产品用户编号
            //String merchpUserId = GrpCommonBean.getJKDTMerchpUserIdByProdId(productOrderIdSet.get(i).toString());
            productInfo.put("USER_ID", productUserInfoList.getData(0).getString("USER_ID"));

            // 2-6 添加产品编号
            String productSpecNumber = GrpCommonBean.nullToString(productNumberSet.get(i));
            String productId = GrpCommonBean.merchJKDTToProduct(productSpecNumber, 2, null);// 产品编号转化为本地产品编号
            productInfo.put("PRODUCT_ID", productId);

            // 2-7 添加是否预约,默认为"false"
            productInfo.put("IF_BOOKING", "false");

            // 2-8 添加取消理由,默认为空
            productInfo.put("REASON_CODE", "");

            // 2-9 添加备注,默认为空
            productInfo.put("REMARK", "");

            IData merchOutData = new DataMap();
            getProductBaseInfo(map, productOfferId, productOrderId, merchOutData);

            productInfo.put("OUT_MERCH_INFO", merchOutData);

            // 2-10 添加产品操作类型(仅供判断是否为归档报文用)
            productInfo.put("PRODUCT_OPER_CODE", GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLE.getValue());

            // 2-11 添加反向受理标记(反向受理不发服务开通)
            productInfo.put("IN_MODE_CODE", "6");

            // 2-12 添加单条产品至产品数据集
            productInfoset.add(productInfo);
        }

        // 3- 添加产品信息至返回结果中
        returnVal.put("ORDER_INFO", productInfoset);
    }
    
    //集客大厅  反向集团注销， 一键注销接口
    public static IDataset makeJKDTDataOneKey(IData map) throws Exception
    {
        // 1- 封装一键注销接口所需数据信息
        IData returnVal = new DataMap();

        // 2- 添加商品用户编号
        String productOfferingID = IDataUtil.getDataset("RSRV_STR2", map, false).get(0).toString();
        String merchUserId = GrpCommonBean.getJKDTMerchUserIdByProdOffId(productOfferingID);
        
        // 2- 添加产品用户编号
        //String productOfferingID = IDataUtil.getDataset("PSUBSCRIBE_ID", map, false).get(0).toString();
        //String merchpUserId = GrpCommonBean.getJKDTMerchpUserIdByProdId(productOfferingID);
        
        //daidl
        // 添加产品用户编码
        String productId = IDataUtil.getDataset("RSRV_STR4", map, false).get(0).toString();        
        returnVal.put("PRODUCTID", productId);
        
        // 添加BIPCODE
        String bipCode = IDataUtil.getDataset("BIPCODE", map, true).get(0).toString();
        returnVal.put("BIPCODE", bipCode);
        
        // 添加KIND_ID
        String kindId = IDataUtil.getDataset("KIND_ID", map, false).get(0).toString();
        returnVal.put("KIND_ID", kindId);
        
        // 添加PRODUCT_ORDER_NUMBER
        String orderNo = IDataUtil.getDataset("PRODUCT_ORDER_NUMBER", map, false).get(0).toString();
        returnVal.put("ORDER_NO", orderNo);
        
        // 添加TRANSIDO
        returnVal.put("TRANSIDO", map.getString("TRANSIDO"));

        returnVal.put("USER_ID", merchUserId);

        // 3- 添加商品编号
        String poNumber = (String) IDataUtil.getDataset("RSRV_STR1", map, false).get(0); // BBOSS商品规格编号
        if("1010800".equals(poNumber)){//IDC注销有问题
            String productUserId = GrpCommonBean.getJKDTMerchpUserIdByProudctId(productId);
            IDataset relaList = RelaBBInfoQry.qryRelationBBAll(null, productUserId, null);  
            if (IDataUtil.isNotEmpty(relaList))
            {
            	for (int i = 0, row = relaList.size(); i < row; i++)
                {
            		IData relaData = relaList.getData(i);           
                    relaData.put("SERIAL_NUMBER", relaData.getString("SERIAL_NUMBER_B"));
                    String userIDA=relaData.getString("USER_ID_A");
                    IDataset merchUser =UserEcrecepOfferfoQry.qryJKDTMerchInfoByUserIdMerchSpecStatus(userIDA,null,null);
                    if (IDataUtil.isNotEmpty(merchUser)&&productOfferingID.equals(merchUser.getData(0).getString("MERCH_OFFER_ID")))
                    {
                        returnVal.put("USER_ID", userIDA);break;
                    } 
                }
            }
        }
        String proPoNumber = GrpCommonBean.merchJKDTToProduct(poNumber, 0, null);// 商品编号转化为本地产品编号
        returnVal.put("PRODUCT_ID", proPoNumber);

        // 4- 添加用户归属地州
        IData grpUserInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(merchUserId, "0");
        if (IDataUtil.isEmpty(grpUserInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }

        returnVal.put(Route.USER_EPARCHY_CODE, grpUserInfo.getString("EPARCHY_CODE", "cg"));

        // 5- 添加GROUP_ID
        IData grpCustInfo = UcaInfoQry.qryGrpInfoByCustId(grpUserInfo.getString("CUST_ID"));
        returnVal.put("GROUP_ID", grpCustInfo.getString("GROUP_ID"));

        if("010190011".equals(poNumber)||"010190014".equals(poNumber)){
            returnVal.put("IN_MODE_CODE", "0");//反向一键注销走服务开通
        }
        else{
            returnVal.put("IN_MODE_CODE", "6");//反向一键注销不走服务开通
        }

        // 6- 反向发起的集团注销落地走一键注销接口
        IDataset retDataset = CSAppCall.call("SS.DestroyJKDTOneKeySVC.crtBat", returnVal);//daidl
        if (IDataUtil.isEmpty(retDataset))
        {
            CSAppException.apperr(BatException.CRM_BAT_95);
        }

        // 7- 返回受理成功标志
        IData dealResult = new DataMap();
        dealResult.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        dealResult.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        dealResult.put("RSPCODE", "00");
        dealResult.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
        dealResult.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
        dealResult.put("EC_SERIAL_NUMBER", IDataUtil.chkParam(map, "EC_SERIAL_NUMBER"));
        dealResult.put("SUBSCRIBE_ID", IDataUtil.chkParam(map, "SUBSCRIBE_ID"));
        dealResult.put("BATCH_INFO", retDataset.getData(0).getString("ORDER_ID"));

        return IDataUtil.idToIds(dealResult);
    }
}

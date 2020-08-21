
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz;

import java.util.Iterator;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.attrbiz.AttrBizInfoIntf;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;

public class AttrBizInfoIntfViewUtil
{
    /**
     * 查询attrbiz表的参数【ATTR_OBJ&ATTR_CODE】
     * 
     * @param bc
     * @param id
     * @param idType
     * @param attrObj
     * @param attrCode
     * @return
     * @throws Exception
     */
    public static IData qryAttrBizInfoByIdAndIdTypeAttrObjAttrCode(IBizCommon bc, String id, String idType, String attrObj, String attrCode) throws Exception
    {
        IDataset infosDataset = AttrBizInfoIntf.qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(bc, id, idType, attrObj, attrCode);
        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(infosDataset))
        {
            result = infosDataset.getData(0);
        }
        return result;
    }

    /**
     * 查询attrbiz表的参数【ATTR_OBJ】
     * 
     * @param bc
     * @param id
     * @param idType
     * @param attrObj
     * @return
     * @throws Exception
     */
    public static IDataset qryAttrBizInfosByIdAndIdTypeAttrObj(IBizCommon bc, String id, String idType, String attrObj) throws Exception
    {
        return qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(bc, id, idType, attrObj, null);
    }

    /**
     * 查询attrbiz表的参数【ATTR_OBJ&AttrCode】
     * 
     * @param bc
     * @param id
     * @param idType
     * @param attrObj
     * @param attrCode
     * @return
     * @throws Exception
     */
    public static IDataset qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(IBizCommon bc, String id, String idType, String attrObj, String attrCode) throws Exception
    {
        return AttrBizInfoIntf.qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(bc, id, idType, attrObj, attrCode);
    }

    /**
     * 查询ATTRBIZ表的参数【ATTR_OBJ&ATTR_VALUE】
     * 
     * @param bc
     * @param id
     * @param idType
     * @param attrObj
     * @param attrValue
     * @return
     * @throws Exception
     */
    public static IDataset qryAttrBizInfosByIdAndIdTypeAttrObjAttrValue(IBizCommon bc, String id, String idType, String attrObj, String attrValue) throws Exception
    {
        return AttrBizInfoIntf.qryAttrBizInfosByIdAndIdTypeAttrObjAttrValue(bc, id, idType, attrObj, attrValue);
    }

    /**
     * 通过IDTYPE ,ATTROBJ ,ATTRCODE查询attrbiz表参数信息
     * 
     * @param bc
     * @param idType
     * @param attrObj
     * @param attrCode
     * @return
     * @throws Exception
     */
    public static IDataset qryAttrBizInfosByIdTypeAttrObjAttrCode(IBizCommon bc, String idType, String attrObj, String attrCode) throws Exception
    {
        return qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(bc, null, idType, attrObj, attrCode);
    }

    /**
     * 查询attrbiz表中的ATTR_VALUE信息【ATTR_OBJ&ATTR_CODE】
     * 
     * @param bc
     * @param id
     * @param idType
     * @param attrObj
     * @param attrCode
     * @return
     * @throws Exception
     */
    public static String qryAttrValueStrByIdAndIdTypeAttrObjAttrCode(IBizCommon bc, String id, String idType, String attrObj, String attrCode) throws Exception
    {
        String attrValueString = null;
        IDataset infosDataset = AttrBizInfoIntf.qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(bc, id, idType, attrObj, attrCode);

        if (IDataUtil.isNotEmpty(infosDataset))
        {
            IData result = infosDataset.getData(0);
            if (IDataUtil.isNotEmpty(result))
                attrValueString = result.getString("ATTR_VALUE");
        }
        return attrValueString;
    }

    /**
     * 查询BBOSS的业务开展模式信息
     * 
     * @param bc
     * @param merchProductId
     * @return
     * @throws Exception
     */
    public static String qryBBossBizCodeByGrpProductId(IBizCommon bc, String merchProductId) throws Exception
    {
        return qryAttrValueStrByIdAndIdTypeAttrObjAttrCode(bc, merchProductId, "P", "0", "BIZMODE");

    }

    /**
     * 判断BBOSS商品是否为需要预受理商品 true 需要预受理
     * 
     * @param bc
     * @param merchProductId
     * @return
     * @throws Exception
     */
    public static boolean qryBBossBusiNeedAHeadTagByGrpProductId(IBizCommon bc, String merchProductId) throws Exception
    {
        boolean resultB = false;
        IDataset tagsDataset = qryAttrBizInfosByIdAndIdTypeAttrObjAttrValue(bc, "1", "B", "AHEAD", merchProductId);
        if (IDataUtil.isNotEmpty(tagsDataset))
        {
            resultB = true;
        }
        return resultB;
    }

    /**
     * 根据PRODUCT_ID,BUSI_TYPE查询ATTR_BIZ表中BookingFlag信息，产品是否支持预约
     * 
     * @param bc
     * @param productId
     * @param busiType
     * @return
     * @throws Exception
     */
    public static IData qryBookingTagInfoByProductIdAndBusiType(IBizCommon bc, String productId, String busiType) throws Exception
    {
        IData resultData = new DataMap();
        IData productCtrlInfo = qryNormalProductCtrlInfoByGrpProductIdAndBusiType(bc, productId, busiType, false);
        if (IDataUtil.isNotEmpty(productCtrlInfo))
        {

            IData valueMap = (IData) productCtrlInfo.get("BookingFlag");
            if (IDataUtil.isNotEmpty(valueMap))
            {
                resultData.put("cond_Booking_Flag", valueMap.getString("ATTR_VALUE", ""));
            }
        }

        return resultData;
    }

    /**
     * 获取成员变更的产品参数控制信息
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IData qryChgMbProductCtrlInfoByProductId(IBizCommon bc, String productId) throws Exception
    {

        IData productCtrlInfo = qryNormalProductCtrlInfoByGrpProductIdAndBusiType(bc, productId, BizCtrlType.ChangeMemberDis);
        return productCtrlInfo;

    }

    /**
     * 获取集团产品变更的产品控制信息
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IData qryChgUsProductCtrlInfoByProductId(IBizCommon bc, String productId) throws Exception
    {

        IData productCtrlInfo = qryNormalProductCtrlInfoByGrpProductIdAndBusiType(bc, productId, BizCtrlType.ChangeUserDis);
        return productCtrlInfo;

    }

    /**
     * 获取成员新增的产品参数控制信息
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IData qryCrtMbProductCtrlInfoByProductId(IBizCommon bc, String productId) throws Exception
    {
        IData productCtrlInfo = qryNormalProductCtrlInfoByGrpProductIdAndBusiType(bc, productId, BizCtrlType.CreateMember);
        return productCtrlInfo;

    }

    /**
     * 获取集团产品受理的产品控制信息
     * 
     * @param bp
     * @param productId
     * @return
     * @throws Throwable
     */
    public static IData qryCrtUsProductCtrlInfoByProductId(IBizCommon bc, String productId) throws Exception
    {

        IData productCtrlInfo = qryNormalProductCtrlInfoByGrpProductIdAndBusiType(bc, productId, BizCtrlType.CreateUser, true);
        return productCtrlInfo;

    }

    /**
     * 获取集团产品受理的产品控制信息
     * 
     * @param bc
     * @param productId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryCrtUsProductCtrlInfoByProductId(IBizCommon bc, String productId, boolean isThrowException) throws Exception
    {
        IData productCtrlInfo = qryNormalProductCtrlInfoByGrpProductIdAndBusiType(bc, productId, BizCtrlType.CreateUser, isThrowException);
        return productCtrlInfo;

    }

    /**
     * 获取集团用户退订的产品控制信息
     * 
     * @param bp
     * @param productId
     * @return
     * @throws Throwable
     */
    public static IData qryDstUsProductCtrlInfoByProductId(IBizCommon bc, String productId) throws Exception
    {
        IData productCtrlInfo = qryNormalProductCtrlInfoByGrpProductIdAndBusiType(bc, productId, BizCtrlType.DestoryUser);
        return productCtrlInfo;

    }

    /**
     * 查询集团产品可支持的付费方式（集团付费、个人付费）
     * 
     * @param bc
     * @param grpProductId
     * @return
     * @throws Exception
     */
    public static IData qryGrpPayModeInfosByGrpProductId(IBizCommon bc, String grpProductId) throws Exception
    {
        return qryAttrBizInfoByIdAndIdTypeAttrObjAttrCode(bc, grpProductId, "P", "0", "PAYMODECODE");
    }

    /**
     * 查询集团产品控制信息
     * 
     * @param bc
     * @param id
     * @param idType
     * @param attrObj
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpProductCtrlInfoByIdAndIdTypeAndAttrObj(IBizCommon bc, String id, String idType, String attrObj) throws Exception
    {
        return AttrBizInfoIntf.qryGrpProductCtrlInfoByIdAndIdTypeAndAttrObj(bc, id, idType, attrObj);
    }

    /**
     * 集团产品成员营销产品控制信息
     * 
     * @param bp
     * @param productId
     * @return
     * @throws Exception
     */
    public static IData qryGrpSaleProductCtrlInfoByProductId(IBizCommon bc, String productId) throws Exception
    {
        IData productCtrlInfo = qryNormalProductCtrlInfoByGrpProductIdAndBusiType(bc, productId, BizCtrlType.GroupMemberSale);
        return productCtrlInfo;
    }

    /**
     * 查询集团产品控制信息
     * 
     * @param bc
     * @param id
     * @param idType
     * @param attrObj
     * @return
     * @throws Exception
     */
    public static IData qryJSProductCtrlInfoByProductId(IBizCommon bc, String grpProductId) throws Exception
    {
        IDataset jsProductCtrlInfos = qryGrpProductCtrlInfoByIdAndIdTypeAndAttrObj(bc, grpProductId, "P", "0");
        if (IDataUtil.isNotEmpty(jsProductCtrlInfos))
        {
            return jsProductCtrlInfos.getData(0);
        }
        return null;
    }

    /**
     * 获取成员变更的产品参数控制信息
     * 
     * @param bp
     * @param productId
     * @param userId
     * @return
     * @throws Throwable
     */
    public static IData qryModMbProductCtrlInfoByProductId(IBizCommon bc, String productId) throws Exception
    {

        IData productCtrlInfo = qryNormalProductCtrlInfoByGrpProductIdAndBusiType(bc, productId, BizCtrlType.ModifyMember);
        return productCtrlInfo;

    }

    /**
     * 获取集团产品付费计划变更的产品控制信息
     * 
     * @param bp
     * @param productId
     * @return
     * @throws Throwable
     */
    public static IData qryModUsProductCtrlInfoByProductId(IBizCommon bc, String productId) throws Exception
    {

        IData productCtrlInfo = qryNormalProductCtrlInfoByGrpProductIdAndBusiType(bc, productId, BizCtrlType.ModifyUser);
        return productCtrlInfo;

    }

    /**
     * 查询集团产品控制信息
     * 
     * @param bc
     * @param productId
     * @param busiType
     * @return
     * @throws Exception
     */
    public static IData qryNormalProductCtrlInfoByGrpProductIdAndBusiType(IBizCommon bc, String productId, String busiType) throws Exception
    {
        return qryNormalProductCtrlInfoByGrpProductIdAndBusiType(bc, productId, busiType, true);
    }

    /**
     * 查询集团产品控制信息
     * 
     * @param bc
     * @param productId
     * @param busiType
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IData qryNormalProductCtrlInfoByGrpProductIdAndBusiType(IBizCommon bc, String productId, String busiType, boolean isThrowException) throws Exception
    {

        IDataset productCtrlInfos = qryGrpProductCtrlInfoByIdAndIdTypeAndAttrObj(bc, productId, "P", busiType);

        if (IDataUtil.isNotEmpty(productCtrlInfos))
        {
            IData productCtrlInfo = productCtrlInfos.getData(0);

            if (isThrowException)
            {
                IData tradeTypeCodeData = productCtrlInfo.getData("TradeTypeCode");
                if (IDataUtil.isEmpty(tradeTypeCodeData))
                {
                    CSViewException.apperr(ProductException.CRM_PRODUCT_505, productId);
                }
            }

            IData productCtrlInfoJS = qryJSProductCtrlInfoByProductId(bc, productId);

            if (IDataUtil.isNotEmpty(productCtrlInfoJS))
            {
                productCtrlInfo.putAll(productCtrlInfoJS);

            }
            return productCtrlInfo;
        }

        if (isThrowException)
        {
            String errMessageInfo = CommParaInfoIntfViewUtil.qryProductCtrlErrMessageInfoByGrpProductId(bc, productId);
            if (StringUtils.isNotBlank(errMessageInfo))
            {
                CSViewException.apperr(BizException.CRM_BIZ_5, errMessageInfo);
            }
            else
            {
                CSViewException.apperr(ProductException.CRM_PRODUCT_97);
            }
        }

        return new DataMap();

    }

    /**
     * 集团成员开户产品控制信息
     * 
     * @param bp
     * @param productId
     * @return
     * @throws Exception
     */
    public static IData qryOpnMbProductCtrlInfoByProductId(IBizCommon bc, String productId) throws Exception
    {
        IData productCtrlInfo = qryNormalProductCtrlInfoByGrpProductIdAndBusiType(bc, productId, BizCtrlType.OpenGroupMeb);
        return productCtrlInfo;
    }

    /**
     * 获取成员新增的产品参数控制信息
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IData qrySimpleCrtMbProductCtrlInfoByProductId(IBizCommon bc, String productId) throws Exception
    {
        IData productCtrlInfo = qrySimpleNormalProductCtrlInfoByGrpProductIdAndBusiType(bc, productId, BizCtrlType.CreateMember);
        return productCtrlInfo;

    }

    /**
     * 查询集团产品控制信息的简化格式，Data中只存放ATTR_CODE和ATTRVALUE值
     * 
     * @param bc
     * @param productId
     * @param busiType
     * @return
     * @throws Exception
     */
    public static IData qrySimpleNormalProductCtrlInfoByGrpProductIdAndBusiType(IBizCommon bc, String productId, String busiType) throws Exception
    {
        IData productCtrlInfoData = qryNormalProductCtrlInfoByGrpProductIdAndBusiType(bc, productId, busiType);

        return trandProductCtrlDataToSimp(productCtrlInfoData);

    }

    // 部分查询时不需要返回到前台过多的控制信息数据，过滤掉一部分，也不晓得这么写有没有意义
    private static IData trandProductCtrlDataToSimp(IData productCtrlInfoData)
    {
        IData productSimpCtrlData = new DataMap();
        if (IDataUtil.isNotEmpty(productCtrlInfoData))
        {
            Iterator iterator = productCtrlInfoData.keySet().iterator();
            while (iterator.hasNext())
            {
                Object datakey = iterator.next();
                IData productCtrlValueData = (IData) productCtrlInfoData.get(datakey);
                if (IDataUtil.isNotEmpty(productCtrlValueData))
                {
                    productSimpCtrlData.put((String) datakey, productCtrlValueData.getString("ATTR_VALUE", ""));
                }

            }
        }
        return productSimpCtrlData;
    }

}

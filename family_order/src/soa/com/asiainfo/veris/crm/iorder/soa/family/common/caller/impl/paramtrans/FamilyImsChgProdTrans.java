package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.paramtrans;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.IParamTrans;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.ParamTransUtil;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

/**
 * @ClassName FamilyImsChgProdTrans
 * @Description 新增存量固话参数转换
 * @Author yuyz
 * @Date 2020/8/8 15:44
 * @Version 1.0
 */
public class FamilyImsChgProdTrans implements IParamTrans {

    @Override
    public IData getTransParamters(IData role) throws Exception {
        IData imsData = new DataMap();
        imsData.putAll(role);
        imsData.put("PRODUCT_TYPE_CODE", "IMSP");//公用参数
        imsData.put("PRODUCT_TYPE_NAME", "IMS固话");//公用参数

        // 特殊转换
        String mainSerialNumber = imsData.getString("MEMBER_MAIN_SN");//451工单的值
        imsData.put("SERIAL_NUMBER", mainSerialNumber);
        String imsNum = imsData.getString("WIDE_SERIAL_NUMBER");
        if (!imsNum.startsWith("0898") && imsNum.length() != 12) {
            imsData.put("WIDE_SERIAL_NUMBER", "0898" + imsNum);
        }
        if(imsData.containsKey("OFFERS")){
            //判断新选产品如果和老产品一致则不调用受理接口
            String newProductId = ParamTransUtil.findRoleMainOffer(imsData);
            String oldProductId = imsData.getString("IMS_PRODUCT_ID");
            if(StringUtils.equals(newProductId,oldProductId)){
                return imsData;
            }
            IDataset imsOffers = new DatasetList(imsData.getString("OFFERS", "[]"));
            if (IDataUtil.isNotEmpty(imsOffers)) {//暂时根据有无商品信息判断是否需要变更产品
                dealImsOfferForChg(imsData, imsOffers);

                imsData.put("BRAND", "IMSP");
                imsData.put("MOBILE_SERIAL_NUMBER", mainSerialNumber);//手机号码
                imsData.put("SERIAL_NUMBER", imsData.getString("WIDE_SERIAL_NUMBER"));//固话号码
                imsData.put("WIDE_SERIAL_NUMBER", "KD_" + mainSerialNumber);//宽带号码

                imsData.put("USER_PRODUCT_ID", imsData.getString("IMS_PRODUCT_ID"));//原产品id
                imsData.put("USER_PRODUCT_NAME", imsData.getString("IMS_PRODUCT_NAME", ""));//原产品name
                imsData.put(Route.ROUTE_EPARCHY_CODE, "0898");
                imsData.put("CALL_REGSVC", "SS.IMSChangeProductRegSVC.tradeReg");
                imsData.put("TRADE_TYPE_CODE", "6806");
            }else{
                return imsData;
            }
        }else{
            return imsData;
        }
        return imsData;
    }

    /**
     * @return void
     * @author yuyz
     * @Description 针对存量固话产品变更参数转换
     * @Date 11:51 2020/8/7
     * @Param [imsData, imsOffers]
     **/
    private void dealImsOfferForChg(IData imsData, IDataset imsOffers) throws Exception {
        String eparchyCode = imsData.getString("EPARCHY_CODE");
        String oldProd = imsData.getString("IMS_PRODUCT_ID");
        String imsUserId = imsData.getString("USER_ID");
        IDataset selectedEles = new DatasetList();
        if (IDataUtil.isNotEmpty(imsOffers)) {
            for (int i = 0; i < imsOffers.size(); i++) {
                IData imsOffer = imsOffers.getData(i);
                String newProId = imsOffer.getString("ELEMENT_ID");//新固话产品id
                IData param = new DataMap();
                param.put("USER_PRODUCT_ID", oldProd);
                param.put("NEW_PRODUCT_ID", newProId);
                param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
                param.put("EPARCHY_CODE", eparchyCode);
                param.put("USER_ID", imsUserId);//固话号码userId
                param.put("TRADE_TYPE_CODE", "6806");

                imsData.put("NEW_PRODUCT_ID", imsOffer.getString("ELEMENT_ID"));//新固话产品id
                imsData.put("NEW_PRODUCT_NAME", imsOffer.getString("ELEMENT_NAME"));//新固话产品name
                imsData.put("PRODUCT_ID", imsOffer.getString("ELEMENT_ID"));//新固话产品id
                imsData.put("PRODUCT_NAME", imsOffer.getString("ELEMENT_NAME"));//新固话产品name
                IDataset chgImsOfffers = CSAppCall.call("CS.SelectedElementSVC.getUserElements", param);
                IDataset chgImsEles = chgImsOfffers.first().getDataset("SELECTED_ELEMENTS");
                imsData.put("NEW_PRODUCT_START_DATE", chgImsEles.first().getString("NEW_PRODUCT_START_DATE"));//新产品生效时间，下月初
                imsData.put("OLD_PRODUCT_END_DATE", chgImsEles.first().getString("OLD_PRODUCT_END_DATE"));//原固话产品终止时间，当月底
                selectedEles.addAll(chgImsEles);
            }
            imsData.put("SELECTED_ELEMENTS", selectedEles);
        }
    }
}

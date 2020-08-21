package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpdatadeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.base.ReqBuildService;
import com.asiainfo.veris.crm.order.pub.exception.TpOrderException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

public class OneCardNCodesCancelDealSVC extends ReqBuildService {

    @Override
    public IData initReqData(IData input) throws Exception {
        String serialNumber = input.getString("SERIAL_NUMBER","");
        if(StringUtils.isEmpty(serialNumber)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40004);
        }
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);//获得三户信息
        String userId = ucaData.getUserId();
        String custInfo = ucaData.getCustId();
        String productId = ucaData.getProductId();
        String brondCode = ucaData.getBrandCode();

        IData userInfo = new DataMap();
        userInfo.put("USER_ID",userId);
        userInfo.put("EPARCHY_CODE",CSBizBean.getTradeEparchyCode());
        userInfo.put("SERIAL_NUMBER",serialNumber);

        IDataset output = CSAppCall.call("SS.OneCardNCodesCancleSVC.checkInfos", userInfo);

        IData oldCardInfo = output.getData(0).getData("USERRESINFO");//获得主号信息
        IData fresinfoInfo = output.getData(0).getData("OUSERRESINFO");//获得副号sim卡信息
        IData ouserInfo = output.getData(0).getData("OUSERINFO");//获得副号号码信息
        IData ocustInfo=output.getData(0).getData("OCUSTINFO");
        //参数获取
        String oldCard = oldCardInfo.getString("SIM_CARD_NO");
        String fresinfo = fresinfoInfo.getString("RES_CODE");
        String fSerialNumber = ouserInfo.getString("SERIAL_NUMBER");

        userInfo.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        userInfo.put("OTHERSN",fSerialNumber);//副号码
        userInfo.put("SIM_CARD_NO_O",fresinfo);//副号 原SIM卡号
        userInfo.put("SIM_CARD_NO_M",oldCard);//主号 原SIM卡号
        userInfo.put("CUST_NAME",custInfo);//主号客户ID
        userInfo.put("PRODUCTID",productId);//主号产品ID
        userInfo.put("BRAND_CODE",brondCode);//主号品牌ID
        userInfo.put("IMSI",oldCardInfo.getString("IMSI"));//主号 原IMSI号
        userInfo.put("custInfo_CUST_NAME",ocustInfo.getString("CUST_NAME"));//副号 客号名称
        userInfo.put("custInfo_PSPT_TYPE_CODE",ocustInfo.getString("PSPT_TYPE_CODE"));//副号 客号名称
        userInfo.put("custInfo_PSPT_ADDR",ocustInfo.getString("PSPT_ADDR"));//副号 证件地址
        userInfo.put("custInfo_BRAND",ouserInfo.getString("BRAND"));//副号 品牌名称
        userInfo.put("custInfo_PRODUCT_NAME",ouserInfo.getString("PRODUCT_NAME"));//副号 产品名称
        userInfo.put("custInfo_OPEN_DATE",ouserInfo.getString("OPEN_DATE"));//副号 开户时间
        userInfo.put("custInfo_STATE_NAME",ouserInfo.getString("STATE_NAME"));//副号 用户状态
        userInfo.put("custInfo_IMSI",fresinfoInfo.getString("IMSI"));//副号 用户状态


//        IDataset dataset = CSAppCall.call("SS.OneCardNCodesCancleSVC.tradeReg", userInfo);//一卡双号取消

        return userInfo;
    }

    @Override
    public String getTradeTypeCode(IData input) throws Exception {
        return null;
    }
}

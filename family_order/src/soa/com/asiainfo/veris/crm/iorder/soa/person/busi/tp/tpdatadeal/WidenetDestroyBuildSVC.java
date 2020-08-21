package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpdatadeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.base.ReqBuildService;
import com.asiainfo.veris.crm.order.pub.exception.TpOrderException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import org.apache.log4j.Logger;

/**
 该用户处在"+ product_name + "有效期内, " + data.getString("END_DATE") + "到期，不允许携出
 这是手机携出，但因为有KD_ + SERIAL_NUMBER的宽带存在不让携出的甩单场景
 **/
public class WidenetDestroyBuildSVC extends ReqBuildService {
    /**
     * 通过该方法的返回值来决定是跳转界面（需要退光猫或者交违约、补安装费等）还是自动甩单
     * 逻辑取自com.asiainfo.veris.crm.iorder.web.person.broadband.widenet.widenetdestory.WidenetDestoryTradeNew#loadChildInfo(org.apache.tapestry.IRequestCycle)
     * 很恶心的代码
     */
    public IData ruleRoute(IData input) throws Exception {
        String modemmode = "";
        IDataset userOtherinfo = null;
        String serialNumber = input.getString("SERIAL_NUMBER");
        String userId = input.getString("USER_ID");
        if(StringUtils.isEmpty(serialNumber)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40001,"SERIAL_NUMBER");
        }
//        if(StringUtils.isEmpty(userId)){
//            CSAppException.apperr(TpOrderException.TP_ORDER_40001,"USER_ID");
//        }
        if(!serialNumber.startsWith("KD_")){
            serialNumber = "KD_" + serialNumber;
        }
        IData param = new DataMap();
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        param.put("SERIAL_NUMBER",serialNumber);
        param.put("USER_ID",ucaData.getUserId());
        param.put("ROUTE_EPARCHY_CODE","0898");
        IDataset dataset = CSAppCall.call("CS.WidenetInfoQuerySVC.getUserWidenetInfo", param);
        String widetype = dataset.getData(0).getString("RSRV_STR2");
        if(StringUtils.equals("3",widetype) || StringUtils.equals("5",widetype)){
            IDataset userList = CSAppCall.call( "SS.DestroyUserNowSVC.getUserInfoBySerailNumber", param);
            if(IDataUtil.isNotEmpty(userList)){
                IData user = userList.getData(0);
                if(!StringUtils.equals("BNBD",user.getString("RSRV_STR10"))){//RSRV_STR10
                    userOtherinfo = CSAppCall.call( "SS.DestroyUserNowSVC.queryUserModemRent", param);
                }else{
                    userOtherinfo = CSAppCall.call("SS.DestroyUserNowSVC.queryGroupUserOtherInfo", param);
                }
                if(IDataUtil.isNotEmpty(userOtherinfo)){
                    modemmode = userOtherinfo.getData(0).getString("RSRV_TAG1", "0");
                }
                if(StringUtils.equals("0",modemmode)){//光猫是租赁的跳界面受理
                    return returnRouteData(true);
                }
            }
        }

        IDataset ids = CSAppCall.call("SS.GponWidenetOrderDestorySVC.queryWidenetInstallFee", param);
        String installFeeTag = "0";
        if (ids!=null && ids.size()>0){
            installFeeTag = ids.getData(0).getString("INSTALL_FEE_TAG","");
        }
        if("1".equals(installFeeTag)){//未到期拆机，要收钱
            return returnRouteData(true);
        }
        IDataset ids2=CSAppCall.call( "SS.GponWidenetOrderDestorySVC.queryWidenetCommissioningFee", param);
        String feeTag = "0";
        String fee = "0";
        int leftMonths = 0;
        if (ids2!=null && ids2.size()>0){
            feeTag=ids2.getData(0).getString("COMMISSIONING_FEE_TAG","");
            fee=ids2.getData(0).getString("COMMISSIONING_FEE","");
            leftMonths =ids2.getData(0).getInt("LEFT_MONTHS",0);
        }
        if(StringUtils.equals(feeTag,"1") && leftMonths > 0){//未到期拆机，要收钱
            return returnRouteData(true);
        }
        IDataset destroyInfos = CSAppCall.call( "SS.GponWidenetOrderDestorySVC.getGponDestroyInfo", param);
        if(IDataUtil.isNotEmpty(destroyInfos)){
            String destroyState = destroyInfos.getData(0).getString("DESTORY_STATE","");
            String destroyTIme = destroyInfos.getData(0).getString("DESTORY_TIME","");
            if(StringUtils.isNotBlank(destroyTIme)){
                return returnRouteData(true);
            }
        }
        return returnRouteData(false);
    }

    /**
     * routeVal 1要收钱跳原界面处理 2自动甩单
     */
    private IData returnRouteData(boolean routeVal)throws Exception{
        IData routeData = new DataMap();
        routeData.put("ROUTE_KEY",routeVal);
        return routeData;
    }


    @Override
    public IData initReqData(IData input) throws Exception {
//        IDataset results = CSAppCall.call( "SS.WidenetDestroyNewRegSVC.tradeReg", input);
//        return results.first();
        input.put("TRADE_TYPE_CODE",getTradeTypeCode(input));
        return input;
    }

    @Override
    public String getTradeTypeCode(IData input) throws Exception {
        return "605";
    }
}

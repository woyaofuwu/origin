package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.action.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.requestdata.SaleActiveEndReqData;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2020/3/28 10:23
 */
public class SaleActiveEndDepositTradeAction implements ITradeAction {

    @Override
    public void executeAction(BusiTradeData btd) throws Exception {
        SaleActiveEndReqData saleactiveEndReqData = (SaleActiveEndReqData) btd.getRD();
        UcaData uca = saleactiveEndReqData.getUca();

        //如果是宽带产品变更界面发起的校验动作，则不进行沉淀。
        String preType = saleactiveEndReqData.getPreType();
        if (preType.equals(BofConst.PRE_TYPE_CHECK)){
            return;
        }

        String productId = saleactiveEndReqData.getProductId();
        String packageId = saleactiveEndReqData.getPackageId();
        IDataset com3228 = CommparaInfoQry.getCommparaByCode1("CSM", "3228", productId, packageId, null);
        if(IDataUtil.isEmpty(com3228)){
            com3228 = CommparaInfoQry.getCommparaByCode1("CSM", "3228", productId, "-1", null);
        }
        if(IDataUtil.isEmpty(com3228)){
            return;
        }
        boolean isNeedDeposit=true;//默认0发起沉淀
        String checkActiveMonthMode=com3228.first().getString("PARA_CODE2");
        if("1".equals(checkActiveMonthMode)){
            //1校验履行合约月份沉淀
            int checkActiveMonth=com3228.first().getInt("PARA_CODE3");
            IDataset orderSaleActives= UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(uca.getUserId(), productId, packageId);
            IData orderSaleActive=orderSaleActives.getData(0);
            String startDate=orderSaleActive.getString("START_DATE");
            String curDate= SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
            int userMonths=SysDateMgr.monthInterval(startDate, curDate);
            if(userMonths >= checkActiveMonth){
                isNeedDeposit=false;
            }
        }else if ("2".equals(checkActiveMonthMode)){
            //可增加逻辑
        }

        if(isNeedDeposit){
            String depositCodeConfig = com3228.first().getString("PARA_CODE24");
            if(StringUtils.isBlank(depositCodeConfig)){
                CSAppException.appError("2020032801", "comparam3228未配置PARA_CODE24");
            }
            String[] depositCodeArr = depositCodeConfig.split("\\|");
            IDataset acctDeposit = AcctCall.queryAccountDepositBySn(uca.getSerialNumber());
            if(IDataUtil.isNotEmpty(acctDeposit)){
                for(int i=0;i<acctDeposit.size();i++){
                    IData deposit = acctDeposit.getData(i);
                    String tradeFee = deposit.getString("DEPOSIT_BALANCE", "");
                    String depositCode = deposit.getString("DEPOSIT_CODE","");
                    for (int j = 0; j < depositCodeArr.length; j++) {
                        if(depositCodeArr[j].equals(depositCode) && !"0".equals(tradeFee)){
                            deposit.put("TRADE_FEE", tradeFee);
                            deposit.put("ANNUAL_TAG", "1");
                            IData inAcct = AcctCall.AMBackFee(deposit);
                            if(IDataUtil.isNotEmpty(inAcct)&&("0".equals(inAcct.getString("RESULT_CODE",""))||"0".equals(inAcct.getString("X_RESULTCODE","")))){

                            }else{
                                CSAppException.appError("60831", "调用接口 AM_CRM_BackFee 接口错误:"+inAcct.getString("X_RESULTINFO"));
                            }
                        }
                    }

                }
            }
        }
    }
}

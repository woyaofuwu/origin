package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

import java.util.List;

/**
 * 关于新增3档随心选会员产品的需求 add by wuhao5 20200604
 * 营销活动绑定配置好的优惠 param_attr = 8735
 */
public class SaleActiveBindDiscntAction implements ITradeAction {
	
	@SuppressWarnings("unchecked")
    public void executeAction(BusiTradeData btd) throws Exception
    {
        if("1".equals(btd.getRD().getPreType())){
            return;
        }
        List<SaleActiveTradeData> saleActiveList = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
        for(SaleActiveTradeData saleActiveTradeData : saleActiveList){
            if (BofConst.MODIFY_TAG_ADD.equals(saleActiveTradeData.getModifyTag())){
                String productId = saleActiveTradeData.getProductId();
                String packageId = saleActiveTradeData.getPackageId();
                String packageName = saleActiveTradeData.getPackageName();
                String serialNumber = saleActiveTradeData.getSerialNumber();
                String userId = saleActiveTradeData.getUserId();
                IDataset comm8735 = CommparaInfoQry.getCommparaByCodeCode1("CSM","8735",productId,packageId);
                if (IDataUtil.isNotEmpty(comm8735) && comm8735.size() > 0) {
                    for (int i = 0;i < comm8735.size();i ++) {
                        IData discntData = comm8735.getData(i);
                        // 需要绑定的优惠编码
                        String discntCode = discntData.getString("PARA_CODE2");
                        IDataset userDiscs = UserDiscntInfoQry.getAllDiscntByUser(userId, discntCode);
                        if (userDiscs != null && userDiscs.size() > 0) {
                            CSAppException.appError("-1","该营销活动捆绑优惠"+discntCode+"已存在，不能重复绑定");
                        }
                        // 跳过9226配置的优惠绑定
                        String dontBind9226 = discntData.getString("PARA_CODE3");
                        // 不发送短信的配置
                        String dontSendSms = discntData.getString("PARA_CODE4");
                        if (StringUtils.isNotBlank(discntCode)) {
                            IData input = new DataMap();
                            input.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                            input.put("SERIAL_NUMBER", serialNumber);
                            input.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
                            input.put("ELEMENT_ID", discntCode);
                            input.put("BOOKING_TAG", "0");// 非预约
                            input.put("NO_TRADE_LIMIT", "TRUE");
                            input.put("SKIP_RULE", "TRUE");
                            if ("Y".equals(dontBind9226)) {
                                // 跳过9226绑定优惠
                                input.put("DONT_BIND", "1");
                            }
                            if ("Y".equals(dontSendSms)){
                                // 不发送短信
                                input.put("IS_NEED_SMS", "false");
                            }
                            input.put("REMARK","8735配置营销包【"+packageName+"】自动绑定优惠"+discntCode);

                            CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", input);
                        }
                    }
                }
            }
        }
    }
}

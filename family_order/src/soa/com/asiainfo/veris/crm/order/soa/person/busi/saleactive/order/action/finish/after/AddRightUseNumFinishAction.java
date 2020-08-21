package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.finish.after;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2020/5/14 15:09
 */
public class AddRightUseNumFinishAction implements ITradeFinishAction {

    @Override
    public void executeAction(IData mainTrade) throws Exception {
        IDataset tradeSaleActives = TradeSaleActive.getTradeSaleActiveByTradeId(mainTrade.getString("TRADE_ID"));
        if (IDataUtil.isEmpty(tradeSaleActives)){
            return;
        }
        for (int i = 0; i < tradeSaleActives.size(); i++) {
            IData tradeSaleActive = tradeSaleActives.getData(i);
            if (!tradeSaleActive.getString("MODIFY_TAG").equals(BofConst.MODIFY_TAG_ADD)) {
                continue;
            }
            String productId=tradeSaleActive.getString("PRODUCT_ID");
            String packageId=tradeSaleActive.getString("PACKAGE_ID");
            IDataset commParaInfo7175 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "7175", productId,packageId);
            if(IDataUtil.isEmpty(commParaInfo7175)){
                continue;
            }
            IData commParaInfo = commParaInfo7175.first();
            String addRightUseNum = commParaInfo.getString("PARA_CODE2");
            if(StringUtils.isBlank(addRightUseNum)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"权益次数未配置");
            }
            //生效时间
            String enableMode = commParaInfo.getString("PARA_CODE3");
            String absoluteStartDate = commParaInfo.getString("PARA_CODE4");
            String enableOffset = commParaInfo.getString("PARA_CODE5");
            String enableUnit = commParaInfo.getString("PARA_CODE6");
            String startDate = SysDateMgr.startDate(enableMode, absoluteStartDate, enableOffset, enableUnit);
            //失效时间
            String disableMode = commParaInfo.getString("PARA_CODE7");
            String absoluteEndDate = commParaInfo.getString("PARA_CODE8");
            String disableOffset = commParaInfo.getString("PARA_CODE9");
            String disableUnit = commParaInfo.getString("PARA_CODE10");
            String endDate = SysDateMgr.endDate(startDate, disableMode, absoluteEndDate, disableOffset, disableUnit);
            //赠送免费停车次数
            IData params = new DataMap();
            params.put("START_DATE",startDate);
            params.put("END_DATE",endDate);
            params.put("PRODUCT_ID",productId);
            params.put("PACKAGE_ID",packageId);
            params.put("ADD_USE_NUM",addRightUseNum);
            params.put("ADD_USE_NUM_TYPE","2");
            params.put("RIGHT_ID", PersonConst.BENEFIT_AIRPORT);
            params.put("DISCNT_CODE", PersonConst.BENEFIT_AIRPORT_FREE_PARKING);
            String serialNumber = mainTrade.getString("SERIAL_NUMBER", "");
            if(StringUtils.isBlank(serialNumber)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"trade台账手机号不存在");
            }
			
            params.put("SERIAL_NUMBER", serialNumber);
            CSAppCall.call("SS.BenefitAddUseNumRegSvc.tradeReg", params);
        }
    }
}

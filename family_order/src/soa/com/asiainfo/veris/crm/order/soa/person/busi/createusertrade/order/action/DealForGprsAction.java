package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreatePersonUserRequestData;

public class DealForGprsAction implements ITradeAction
{            

    //@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        List<ResTradeData> resTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
        String flag ="";
        String productId = createPersonUserRD.getUca().getProductId();
       
        boolean exitSvc =false;
       // boolean exitDiscnt =false;
        List<SvcTradeData> svcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        for (SvcTradeData svcTradeData : svcTradeDatas)
        {
            if (BofConst.MODIFY_TAG_ADD.equals(svcTradeData.getModifyTag()))
            {
                if (svcTradeData.getElementId().equals("22"))
                {
                    exitSvc=true;
                    break;
                }

            }
        }
        
        for (ResTradeData resTradeData : resTradeDatas)
        {
            if ("1".equals(resTradeData.getResTypeCode()))
               flag = resTradeData.getRsrvTag3();
        }
        
        
        //4g卡默认如果没有选择gprs服务，需要绑定 sunxin
        if("1".equals(flag)){
        	IDataset svc =ProductInfoQry.getElementByProductIdElemId(productId,"22");
            if(IDataUtil.isEmpty(svc))
            	CSAppException.apperr(BizException.CRM_GRP_713, "您订购的产品没有配置相应的gprs服务，不能使用4g卡！");
            if(!exitSvc){
            	CSAppException.apperr(BizException.CRM_GRP_713, "您现在使用的是4g卡，需要订购gprs服务，请订购！");
            }
        }
    }

}

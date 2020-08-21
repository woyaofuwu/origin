
package com.asiainfo.veris.crm.order.soa.person.busi.score.getgiftofstudyimbursement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;

public class GetGiftOfStudyImbursementBean extends CSBizBean
{

    /**
     * 获取子业务信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getCommInfo(IData inData) throws Exception
    {

        IData data = new DataMap();
        
        //拆分sql   duhj 2017/03/03        
        // 获取礼品信息
        IDataset giftInfos = TradeScoreInfoQry.getGiftOfStudyImbursement(inData.getString("USER_ID"));

        if(IDataUtil.isNotEmpty(giftInfos)){
        	for(int i = 0; i < giftInfos.size(); i++){
                IData giftInfo = giftInfos.getData(i);
                String tradeId=giftInfo.getString("TRADE_ID");
                String ruleId=giftInfo.getString("RULE_ID");
                IDataset goodsStates = TradeScoreInfoQry.getGifGoodsStates(inData.getString("USER_ID"),tradeId,ruleId);
                if(IDataUtil.isNotEmpty(goodsStates)){//此处能查到，说明已经兑换过,不能办理
                	
                	giftInfo.put("GOODS_STATE", "1");//0 未领取,1已经领取
                	giftInfo.put("UPDATE_TIME", goodsStates.getData(0).getString("UPDATE_TIME"));//0 未领取,1已经领取

                }else{
                	giftInfo.put("GOODS_STATE", "0");//0 未领取,1已经领取
                }
        		
        	}
        }else{
            // 该用户尚未办理全球通积分助学兑换礼品业务！
            CSAppException.apperr(CrmUserException.CRM_USER_1062);

        }
        

        // 判断用户是否已领取所有礼品
        int i = 0;
        boolean isGotAll = true;
        int giftInfoSize = giftInfos.size();
        for (i = 0; i < giftInfoSize; i++)
        {
            IData gift = giftInfos.getData(i);
            // 状态为0标识未领取
            if ("0".equals(gift.getString("GOODS_STATE", "")))
            {
                isGotAll = false;
                gift.put("GOODS_STATE_NAME", "未领取");
                gift.put("CHECK_DISABLE", "false");
                // break;
            }
            else
            {
                gift.put("GOODS_STATE_NAME", "已领取");
                gift.put("CHECK_DISABLE", "true");
            }
        }
        if (isGotAll)
        {
            // 该用户已领取所有礼品！
            CSAppException.apperr(CrmUserException.CRM_USER_1063);
        }

        data.put("GIFTINFO", giftInfos);
        return data;
    }

}

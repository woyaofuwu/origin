
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreDonateRollBackRequestData;

public class BuildScoreDonateRollBackRequestData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // 入参校验
        IDataUtil.chkParam(param, "SERIAL_NUMBER");
        IDataUtil.chkParam(param, "TRADE_SEQ");
        //IDataUtil.chkParam(param, "SCORE_VALUE");

        ScoreDonateRollBackRequestData reqData = (ScoreDonateRollBackRequestData) brd;
        reqData.setSERIAL_NUMBER(param.getString("SERIAL_NUMBER"));
        reqData.setTRADE_SEQ(param.getString("TRADE_SEQ"));
        //reqData.setSCORE_VALUE(param.getString("SCORE_VALUE"));

        IData inparam= new DataMap();
        inparam.put("TRADE_TYPE_CODE", "340");
        inparam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER",""));
        inparam.put("TRADE_SEQ", param.getString("TRADE_SEQ",""));
        IDataset donateInfos = TradeScoreInfoQry.queryDonateRollbackInfo(inparam);
		
		if (donateInfos.size() == 0) {
			 CSAppException.apperr(CrmUserException.CRM_USER_9,"积分转赠交易流水号对应的积分转赠记录不存在");
		}else{
			//获取大订单号
			reqData.setTRADE_ID(((IData)donateInfos.getData(0)).getString("TRADE_ID",""));
			
			//从积分子台帐获取到扣减的积分值，因为扣减的是负值，将扣减的积分值再次负值后toString，得到积分值
			reqData.setSCORE_VALUE(""+(-Integer.parseInt(((IData)donateInfos.getData(0)).getString("SCORE_CHANGED",""))));
		}	
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ScoreDonateRollBackRequestData();
    }

}

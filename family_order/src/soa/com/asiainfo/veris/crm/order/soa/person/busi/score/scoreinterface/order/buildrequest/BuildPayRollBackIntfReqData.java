
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
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScorePayRollBackReqData;

public class BuildPayRollBackIntfReqData extends BaseBuilder implements IBuilder
{
    @Override
    // 积分支付回退
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

    	 // 入参校验
        IDataUtil.chkParam(param, "TRADE_SEQ");
        IDataUtil.chkParam(param, "MOBILE");
        IDataUtil.chkParam(param, "REFUND_POINT");
        
        ScorePayRollBackReqData reqData = (ScorePayRollBackReqData) brd;
        reqData.setTRADE_SEQ(param.getString("TRADE_SEQ",""));
        reqData.setTRADE_TIME(param.getString("TRADE_TIME",""));
        reqData.setORGID(param.getString("ORGID",""));
        reqData.setTRADE_ID(param.getString("TRADE_ID",""));
        reqData.setF_ORDER_ID(param.getString("F_ORDER_ID",""));
        reqData.setMOBILE(param.getString("MOBILE",""));
        reqData.setP_TRADE_SEQ(param.getString("P_TRADE_SEQ",""));
        reqData.setREFUND_POINT(param.getString("REFUND_POINT",""));
        checkBefore(brd,param);// 业务受理时校验
    }

    private void checkBefore(BaseReqData brd,IData param) throws Exception
    {
    	ScorePayRollBackReqData reqData = (ScorePayRollBackReqData) brd;

    	// 判断用户状态
//      if (!"0".equals(reqData.getUca().getUser().getUserStateCodeset()))
//      {
//          CSAppException.apperr(CrmUserException.CRM_USER_411);
//      }
  	/**
  	 * REQ201702080017_关于积分业务的若干优化需求
  	 * @author zhuoyingzhi
  	 * 合版本 duhj 2017/5/2
  	 * @date 20170307
  	 */
      if (!"0".equals(reqData.getUca().getUser().getRemoveTag()))
       {
      	 //注销标识不正常
           CSAppException.apperr(CrmUserException.CRM_USER_107,"用户销户标志非正常");
       }
      /****************end***************************/
        IData inparam= new DataMap();
        inparam.put("TRADE_TYPE_CODE", "329");
        inparam.put("SERIAL_NUMBER", param.getString("MOBILE",""));
        inparam.put("P_TRADE_SEQ", param.getString("P_TRADE_SEQ",""));
        inparam.put("OPR_TYPE", "031");
        IDataset payInfos = TradeScoreInfoQry.queryPayOrRollbackInfo(inparam);
		
		if (payInfos.size() == 0) {
			 CSAppException.apperr(CrmUserException.CRM_USER_9,"积分支付交易流水号对应的积分支付记录不存在");
		}else{
			//获取大订单的扣减积分值
			int payScore = Integer.parseInt(((IData)payInfos.getData(0)).getString("SCORE_CHANGED","0")); 
			reqData.setTRADE_ID(((IData)payInfos.getData(0)).getString("TRADE_ID",""));
			int payrollBackScore=0;
	        inparam.put("OPR_TYPE", "032");
	        IDataset rollBackInfos = TradeScoreInfoQry.queryPayOrRollbackInfo(inparam);
			if (rollBackInfos != null && rollBackInfos.size()>0 )
			{
				for(int i = 0; i < rollBackInfos.size(); i++) {
					IData info = rollBackInfos.getData(i);
					if((info.getString("TRADE_SEQ","")).equals(param.getString("TRADE_SEQ",""))) {
						CSAppException.apperr(CrmUserException.CRM_USER_9,"大订单进行积分回退的子订单已经存在，请重新输入新的子订单号！");
					}
					payrollBackScore += Integer.parseInt(((IData)rollBackInfos.getData(i)).getString("SCORE_CHANGED","0"));
				}
			}	
			//判断是否还有可以回退的积分
			int canRollBack = payScore + payrollBackScore + Integer.parseInt(param.getString("REFUND_POINT"));
			if(canRollBack>0){
				CSAppException.apperr(CrmUserException.CRM_USER_9,"回退的积分过多，请重新输入回退的积分值！");
			}
		}	
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ScorePayRollBackReqData();
    }

}


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
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreReviseNReqData;

public class BuildReviseNIntfReqData extends BaseBuilder implements IBuilder
{
    @Override
    // 积分冲正
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

    	// 入参校验
        IDataUtil.chkParam(param, "TRADE_SEQ");
        IDataUtil.chkParam(param, "MOBILE");
        IDataUtil.chkParam(param, "REVISE_POINT");
        
        ScoreReviseNReqData reqData = (ScoreReviseNReqData) brd;
        reqData.setTRADE_SEQ(param.getString("TRADE_SEQ",""));
        reqData.setTRADE_TIME(param.getString("TRADE_TIME",""));
        reqData.setORGID(param.getString("ORGID",""));
        reqData.setMOBILE(param.getString("MOBILE",""));
        reqData.setOPR_TYPE(param.getString("OPR_TYPE",""));
        reqData.setREVISE_POINT(param.getString("REVISE_POINT",""));
        checkBefore(brd,param);// 业务受理时校验
    }

    private void checkBefore(BaseReqData brd,IData param) throws Exception
    {
    	ScoreReviseNReqData reqData = (ScoreReviseNReqData) brd;

        // 判断用户状态
/*        if (!"0".equals(reqData.getUca().getUser().getUserStateCodeset()))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_411);
        }*/
        /**
         * REQ201702080017_关于积分业务的若干优化需求
         * 
         * @author zhuoyingzhi
         * @date 20170307
         * 合版本 duhj 2017/5/2
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
        inparam.put("TRADE_SEQ", param.getString("TRADE_SEQ",""));
        IDataset payInfos = TradeScoreInfoQry.queryReviseInfo(inparam);
		
		if (payInfos.size() == 0) {
			 CSAppException.apperr(CrmUserException.CRM_USER_9,"积分支付交易流水号对应的积分支付记录不存在");
		}else{
			//获取大订单的扣减积分值
			reqData.setTRADE_ID(((IData)payInfos.getData(0)).getString("TRADE_ID","0"));
		}
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ScoreReviseNReqData();
    }

}

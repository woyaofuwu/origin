
package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.requestdata.ModifyCustInfoReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.m2mtag.trade.requestdata.M2MTagReqData;

/**
 * REQ201706150007 关于增加行业应用卡标识办理界面的需求
 * 资料修改完成后，打上行业应用卡标识
 * @author zhangxing
 */
public class AddM2MTagAction implements ITradeAction
{
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String batchOperType = btd.getRD().getPageRequestData().getString("BATCH_OPER_TYPE");
        System.out.println("-----------AddM2MTagAction-----------batchOperType:"+batchOperType);
        if("MODIFYCUSTINFO_M2M".equals(batchOperType))
        {
	    	ModifyCustInfoReqData rd = (ModifyCustInfoReqData) btd.getRD();
	        String userId = rd.getUca().getUserId();
	        //0.已打行业应用卡标识,不需要再处理!
	        System.out.println("-----------AddM2MTagAction-----------userId:"+userId);

	        IData m2mInfos = UserOtherInfoQry.qryUserOthInfoForGrp("HYYYKBATCHOPEN",userId);
	        if(IDataUtil.isEmpty(m2mInfos))
	        {
	        	createOtherTradeInfo(btd,rd);
	        }
        }
    }
    
    private void createOtherTradeInfo(BusiTradeData btd,ModifyCustInfoReqData rd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();

        
        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setRsrvValueCode("HYYYKBATCHOPEN");// 材料编码
        otherTradeData.setRsrvValue("行业应用卡批量开户标记");
        otherTradeData.setUserId(rd.getUca().getUserId());
        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        otherTradeData.setRemark(rd.getRemark()); //备注
        otherTradeData.setRsrvStr1(serialNumber);//
        otherTradeData.setRsrvStr2(rd.getUca().getCustId());//
        otherTradeData.setRsrvStr3("m2mTag");//
        otherTradeData.setRsrvStr4("10");//
        otherTradeData.setRsrvStr5(CSBizBean.getVisit().getStaffName());//
        otherTradeData.setRsrvStr11(CSBizBean.getVisit().getStaffId());//
       
        btd.add(serialNumber, otherTradeData);
        
    }
}

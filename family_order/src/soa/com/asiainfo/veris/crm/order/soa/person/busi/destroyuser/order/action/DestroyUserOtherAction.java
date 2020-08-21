
package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.action;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;

/**
 * 《REQ201911030002关于2019年销户手机号与微信绑定规则优化需求》 
 *  销户时，记录销户状态DESTROY_USER_TAG记录到TF_B_TRADE_OTHER表
 * 
 * @author chenyw7
 */
public class DestroyUserOtherAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
    	MainTradeData main=btd.getMainTradeData();
    	OtherTradeData otherTradeData = new OtherTradeData();
    	otherTradeData.setUserId(main.getUserId());
    	otherTradeData.setRsrvValueCode("DESTROY_USER_TAG");
    	otherTradeData.setRsrvValue(main.getSerialNumber());//用户号码
    	otherTradeData.setRsrvStr1("0");//同步状态字段
    	otherTradeData.setRsrvStr2(main.getTradeTypeCode());//业务类型编码trade_code_type
    	otherTradeData.setStartDate(SysDateMgr.getSysTime());
    	otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
    	otherTradeData.setInstId(SeqMgr.getInstId());
    	otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
    	otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
    	otherTradeData.setRemark("销户号码同步到微信的记录");
    	otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
    	btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);

    }

}

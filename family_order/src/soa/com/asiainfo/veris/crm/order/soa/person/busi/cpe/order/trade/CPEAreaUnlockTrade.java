
package com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.trade;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.requestdata.CPEAreaUnlockReqData;

/**
 * CPE小区解锁
 * @author chenxy3 2015-8-25
 */
public class CPEAreaUnlockTrade extends BaseTrade implements ITrade
{
	
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    { 
    	// 成功！ 处理other表
	    createOtherTradeInfo(btd); 
    } 

    /**
     * 处理other台账表
     * 
     * @param btd
     * @throws Exception
     */
    private void createOtherTradeInfo(BusiTradeData btd) throws Exception
    {   IDataset dataset = UserOtherInfoQry.getUserOtherUserId(btd.getRD().getUca().getUserId(), "CPE_LOCATION", null);// 字段不全
	    if (dataset != null && dataset.size() > 0)
	    {
	        // 用查询出来的tf_f_user_other对象来构建otherTradeData
	        // 注意： 一定要保证查询出来数据包含了tf_f_user_other表的所有列，否则在完工时会将没有的列值update为空了
	        OtherTradeData otherTradeDataDel = new OtherTradeData(dataset.getData(0));
	    	CPEAreaUnlockReqData rd = (CPEAreaUnlockReqData) btd.getRD();
	        String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();  
	        otherTradeDataDel.setRsrvValue("1");//解锁 
	        otherTradeDataDel.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());  
	        otherTradeDataDel.setModifyTag(BofConst.MODIFY_TAG_UPD); 
	        otherTradeDataDel.setRemark(rd.getRemark());  
	        btd.add(serialNumber, otherTradeDataDel); 
	    }
    }
}

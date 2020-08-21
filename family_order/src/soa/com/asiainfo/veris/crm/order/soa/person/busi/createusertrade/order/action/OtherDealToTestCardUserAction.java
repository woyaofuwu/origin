
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import org.apache.log4j.Logger;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreatePersonUserRequestData;


/**
 * REQ201609060001_2016年下半年测试卡功能优化（二）
 * @author zhuoyingzhi
 * 20160923
 * <br/>
 * 说明：
 *  个人开户和批量预开户,当用户类型选择为测试机用户时,往tf_f_user_ohter
 *  表里面添加一条RSRV_VALUE_CODE = 'TEST_CARD_USER'的数据
 * 
 */
public class OtherDealToTestCardUserAction implements ITradeAction
{
    static Logger logger=Logger.getLogger(OtherDealToTestCardUserAction.class);
	
    public void executeAction(BusiTradeData btd) throws Exception
    {
        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        UserTradeData u= createPersonUserRD.getUca().getUser();
       String userTypeCode= u.getUserTypeCode();
       
       String test_card_type="1";
       String test_card_type_name="不限制办理渠道";
       String cardType=btd.getRD().getPageRequestData().getString("TEST_CARD_TYPE");
       
       if (logger.isDebugEnabled())
       {
    	   logger.debug("--------------cardType-------------------:"+cardType);
       }
       
       if(cardType!=null&&!cardType.equals("")){
    	   test_card_type=cardType;
    	   if("0".equals(test_card_type)){
    		   test_card_type_name="限制办理渠道";
    	   }
       }
        if("A".equals(userTypeCode)){
        	//用户类型为    测试机用户
        	OtherTradeData otherTradeData = new OtherTradeData();
            otherTradeData.setUserId(u.getUserId());
            otherTradeData.setRsrvValueCode("TEST_CARD_USER");
            otherTradeData.setRsrvValue(test_card_type);//默认为   不限制办理渠道 
            otherTradeData.setRsrvStr2(test_card_type_name);
            otherTradeData.setStartDate(SysDateMgr.getSysTime());
            otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
            otherTradeData.setInstId(SeqMgr.getInstId());
            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());

            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            
            otherTradeData.setRemark("用户类型为    测试机用户");
            otherTradeData.setRsrvStr1(u.getSerialNumber());//测试机号码
            
            btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);
        }
    }

}

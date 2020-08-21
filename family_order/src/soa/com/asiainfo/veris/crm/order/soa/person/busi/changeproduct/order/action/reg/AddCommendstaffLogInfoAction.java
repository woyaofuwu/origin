
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;


/**
 * REQ201609050007_手机商城o2o功能需求
 * @author zhuoyingzhi
 * 20161031
 * <br/>
 * 
 */
public class AddCommendstaffLogInfoAction implements ITradeAction
{
    static Logger logger=Logger.getLogger(AddCommendstaffLogInfoAction.class);
	
    public void executeAction(BusiTradeData btd) throws Exception
    {
        boolean nowRunFlag = BizEnv.getEnvBoolean("crm.merch.addShoppingCart", false); // 加购物车跳出此段逻辑,加个开关方便控制
        if (nowRunFlag)
        {
            OrderDataBus dataBus = DataBusManager.getDataBus();
            String submitType = dataBus.getSubmitType();// addShoppingCart
            if (StringUtils.equals(BofConst.SUBMIT_TYPE_SHOPPING_CART, submitType))
            {
                return;
            }
        }
       String commendStaffId=btd.getRD().getPageRequestData().getString("COMMEND_STAFF_ID","");
       
       String tradeTypeCode=btd.getTradeTypeCode();
       String tradeId=btd.getTradeId();
       String userId=btd.getRD().getUca().getUserId();
      
      
		//DIMISSION_TAG 离职标志：0－正常，1－已经离职，2-冻结
		String departId = StaticUtil.getStaticValue(CSBizBean.getVisit(),
				"TD_M_STAFF", new String[] { "STAFF_ID", "DIMISSION_TAG" },
				"DEPART_ID", new String[] { commendStaffId, "0" });
		
	   if(departId !=null&&!"".equals(departId)){
		   //正常用户
	       if(commendStaffId!=null&&!"".equals(commendStaffId)){
	    	   
	    	   IData param=new DataMap();
	    	   
	           //业务流水号
	           param.put("TRADE_ID", tradeId);
	           //服务号
	           param.put("SERIAL_NUMBER", btd.getRD().getUca().getSerialNumber());
	           //业务类型
	           param.put("TRADE_TYPE_CODE", tradeTypeCode);
	           //用户id
	           param.put("USER_ID", userId);
	           
	           //推荐工号
	           param.put("COMMEND_STAFF_ID", commendStaffId); 
	           //推荐工号归属渠道
	           param.put("COMMEND_DEPART_ID", departId);
	           //状态
	           param.put("DEAL_STATUS", "0");
	           
	           //受理时间
	           param.put("ACCEPT_DATE", btd.getRD().getAcceptTime());
	           
	           //更新时间
	           param.put("UPDATE_TIME", SysDateMgr.getSysDate());
	           //更新员工
	           param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
	           //更新部门
	           param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
	           //备注
	           param.put("REMARK", btd.getRD().getPageRequestData().getString("REMARK", ""));
	           
	           //
	           Dao.insert("TL_B_COMMENDSTAFF_LOG", param);
	       }
	   }
    }

}

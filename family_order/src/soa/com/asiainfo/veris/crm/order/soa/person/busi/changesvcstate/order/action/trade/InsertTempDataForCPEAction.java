package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.action.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;


/**
 * Copyright: Copyright 2016 Asiainfo
 * @ClassName: InsertTempDataForCPEAction.java
 * @Description: 不在指定小区锁定的CPE，报开后需要插一条数据到tf_f_active_template，用于三天后能被锁定功能扫描到继续待处理
 * @author: songlm
 */

public class InsertTempDataForCPEAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	String userId = btd.getRD().getUca().getUserId();
    	String userBrand = btd.getRD().getUca().getBrandCode();//获取品牌

        //只对CPE号码的业务进行处理
    	if ("CPE1".equals(userBrand))
		{
    		//取出CPE号码220服务的状态，不能从UCA取，因为UCA的已经是本次业务变更后的状态
    		IDataset userSvcStateInfos = UserSvcStateInfoQry.getUserLastStateByUserSvc(userId, "220");
    		if (IDataUtil.isNotEmpty(userSvcStateInfos))
            {
    			IData userSvcStateInfo = userSvcStateInfos.getData(0);
    			String userSvcState = userSvcStateInfo.getString("STATE_CODE");
    			
    			//如果用户的220服务是C-不在指定小区、P-报停+不在指定小区、Q-流量封顶+不在指定小区、S-流量封顶+报停+不在指定小区的时候
    			if("C".equals(userSvcState) || "P".equals(userSvcState) || "Q".equals(userSvcState) || "S".equals(userSvcState))
    			{
    				IData inparam = new DataMap();
    				inparam.put("TRADE_TYPE_CODE", "697");
    				inparam.put("USER_ID", userId);
    				inparam.put("DEAL_TAG", "0");
    				inparam.put("RSRV_STR1", "不在指定小区暂停后，报开时插入0状态的该记录");
    				inparam.put("RSRV_STR2", SysDateMgr.decodeTimestamp(SysDateMgr.addDays(3), SysDateMgr.PATTERN_TIME_YYYYMMDD));//三天后
                	Dao.insert("TF_F_ACTIVE_TEMPLATE", inparam);
    			}
            }
		}
    }
}

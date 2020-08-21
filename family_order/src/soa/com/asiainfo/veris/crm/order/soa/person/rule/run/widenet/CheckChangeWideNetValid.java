package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.FastAuthApproveQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class CheckChangeWideNetValid extends BreBase implements IBREScript {
	 private static final long serialVersionUID = 1L;
	 
	 
	/**
     * 如果用户是宽带用户，核对用户是否符合进行宽带过户的条件
     */
    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
    	String serialNumber = databus.getString("SERIAL_NUMBER");
    	
    	IData param = new DataMap();
        // 查询手机号码的宽带用户信息
        IDataset userInfo = WidenetInfoQry.getUserInfo(serialNumber);
        if (userInfo == null || userInfo.size() == 0)
        {
            return false;
        }
        
        String userId = userInfo.getData(0).getString("USER_ID");
        // 用户宽带资料
        IDataset dataset = WidenetInfoQry.getUserWidenetInfo(userId);
        if (IDataUtil.isEmpty(dataset))
        {
        	return false;
        }
        
        String rsrvStr2=dataset.getData(0).getString("RSRV_STR2","");
		 if(!(rsrvStr2.equals("1")||
				 rsrvStr2.equals("2")||rsrvStr2.equals("3")||rsrvStr2.equals("5"))){
			 return false;
		 }
        
        param.put("USER_ID_WIDE", userId);
        // 判断宽带状态: --- 可以通过配置 td_s_svcstate_trade_limit 表, 只配置1,5, 4种业务类型8条记录
        // 外加一个规则: --- WideChangeUserCheckBean.java
        // boolean statusFlag = userQuery.queryWideNetOpenStatus(pd, userInfo);
        // if (statusFlag) {
        // common.error("该宽带用户为非开通状态，不能进行宽带帐号变更！");
        // }
        // 判断是否有未完工的工单-- j2ee默认就有判断
        // 查询主账号下所有子账号
        IDataset allAcct = RelaUUInfoQry.getAllSubAcct(userId, "77");
        if (allAcct != null && allAcct.size() > 0)
        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604004", "号码不能进行宽带过户：号码属于平行帐号关系，不能变更帐号");
        

        IDataset allAcctF = RelaUUInfoQry.getAllSubAcct(userId, "78");
        if (allAcctF != null && allAcctF.size() > 0)
        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604004", "号码不能进行宽带过户：此号码属于家庭帐号关系，不能变更帐号");
            
        // 查询生效的优惠
        IDataset discntInfo = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(userId);
        if (discntInfo == null || discntInfo.size() <= 0)
        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604004", "号码不能进行宽带过户：该用户在首月免费期中，不能变更帐号");
            
        // 判断是不是存在特殊优惠
        else
        {
            for (int i = 0; i < discntInfo.size(); i++)
            {
                String disCode = ((IData) discntInfo.get(i)).getString("DISCNT_CODE");

                IDataset exit = FastAuthApproveQry.queryCommByAttrCode("CSM", "640", disCode, CSBizBean.getTradeEparchyCode());
                if (exit != null && exit.size() > 0)
                	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604004", "号码不能进行宽带过户：号码的优惠不允许办理变更帐号业务！");
            }
        }
        // 获取用户指定业务最近一次办理记录, 查看是否办理过宽带过户
//        IData resultData = TradeHistoryInfoQry.getTradeInfoByUserTrade(userId, "640");
//        if (IDataUtil.isNotEmpty(resultData) && "1".equals(resultData.getString("X_RECORDNUM")))
//        {
//        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604004", "本月已经变更一次帐号，不能再次办理!");
//        }
        //获取用户在本月是否办理了一次宽带过户
        String curMonth=SysDateMgr.getCurMonth();
        boolean isChangeOwner=TradeUserInfoQry.queryWideNetUserIsChangeOwner(userId, curMonth);
        if(isChangeOwner){
        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604004", "号码不能进行宽带过户：本月已经变更一次帐号，不能再次办理!");
        }
        
        return false;

    }
}

package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class CheckExistsWidenetActive extends BreBase implements IBREScript{

	private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
    	String tradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String userId = databus.getString("USER_ID");
        String errorInfo = "";
        //String tradeType = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
        
        //查询是否有宽带包年套餐
        IDataset commset = CommparaInfoQry.getCommpara("CSM", "532", tradeTypeCode, "0898");
        if (IDataUtil.isEmpty(commset))
        {
        	errorInfo = "宽带包年套餐参数配置有误!";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604032", errorInfo);
        }
        IDataset disnset = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
        //IDataset widenetInfos1 = UserInfoQry.get.getUserWidenetInfo(userId);
        if (IDataUtil.isNotEmpty(disnset))
        {
        	for(int i=0;i<disnset.size();i++)
        	{
        		String discode = disnset.getData(i).getString("DISCNT_CODE");
        		String endDate  = disnset.getData(i).getString("END_DATE");
        		String sysdate = SysDateMgr.getSysDateYYYYMMDD();
        		String strEndDate = endDate.replace("-", "") ;
        		
        		//最后一个月不限制
        		if(strEndDate.substring(0,6).equals(sysdate.substring(0, 6)))
        		{
        			return false ;
        		}
        		
        		//预约拆机
        		if(tradeTypeCode.equals("1605"))
        		{
        			String now_end = SysDateMgr.getSysDate();
                	now_end = SysDateMgr.addMonths(now_end, 2);//只能在结束前3个月的时候可以预约,由于预约是失效后的次月，所以这里取从本月算起3个月的最后一天
                	now_end = SysDateMgr.getDateLastMonthSec(now_end);
                	if(SysDateMgr.compareTo(now_end, endDate) >= 0)
                	{
                		return false ;
                	}
        		}
        		
        		for(int m=0;m<commset.size();m++)
        		{
        			String comm_dis = commset.getData(m).getString("PARA_CODE1");
        			if (discode.equals(comm_dis))
        			{
        				String paracode2=commset.getData(m).getString("PARA_CODE2");
        				errorInfo = "该用户有宽带包年套餐["+paracode2+"]，不能使用宽带拆机功能办理业务，请使用“宽带特殊拆机”功能办理!";
        	            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604031", errorInfo);
        			}
        		}
        	}
            
        }
        //查询是否有宽带营销活动
        //这个在校验宽带1+活动的规则校验类里面已经有校验了
        

        return false;
    	
    }
}

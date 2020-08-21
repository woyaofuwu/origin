package com.asiainfo.veris.crm.order.soa.script.rule.user;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;

public class TBChecklimitTradeByWidenet1puls extends BreBase implements IBREScript {

	private static Logger logger = Logger.getLogger(TBChecklimitTradeByWidenet1puls.class);
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {    
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBChecklimitTradeByWidenet1puls() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;  
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
           // String strSerialNumber = databus.getString("SERIAL_NUMBER");
            if ("605".equals(strTradeTypeCode) || "624".equals(strTradeTypeCode) || "625".equals(strTradeTypeCode))
            {
	            IData params = new DataMap();
                params.put("USER_ID", databus.getString("USER_ID"));
                IDataset dt = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_WADENET_1PULS_PREFER", params);
                if (dt != null && dt.size() > 0)
                {
                	//需要判断配置的是否绝对结束时间，如果是据对结束，则判断是否178中的营销包，如果是，则重新计算开始时间
                	for(int i = 0 ; i < dt.size() ; i++)
                	{
                		String packageId = dt.getData(i).getString("PACKAGE_ID");
                    	String elementId = dt.getData(i).getString("DISCNT_CODE");
                    	String startDate = dt.getData(i).getString("START_DATE");
                    	String endDate = dt.getData(i).getString("END_DATE");
                    	IDataset pkgInfos = PkgElemInfoQry.getServElementByPk(packageId,"D",packageId);
                    	if(pkgInfos != null && pkgInfos.size() > 0)
                    	{
                    		String endEnableTag = pkgInfos.getData(0).getString("END_ENABLE_TAG","");
                    		if("0".equals(endEnableTag))
                    		{
                    			//绝对结束，重算开始时间
                    			String month = "12";
                    			IDataset commparaDs = BreQryForCommparaOrTag.getCommpara("CSM",181,"-1",packageId,CSBizBean.getUserEparchyCode());
                    			if(commparaDs != null && commparaDs.size() > 0)
                    			{
                    				month = commparaDs.getData(0).getString("PARA_CODE4","12");
                    			}
                    			
                    			//重算结束时间
                    			endDate = SysDateMgr.endDate(startDate, "1", "2050-12-31 23:59:59", month, "3");
                    		}
                    	}
                    	
                    	int iret = SysDateMgr.compareTo(endDate, SysDateMgr.getSysDate());
                    	if (iret>=0)
                    	{
                    		//判断是否最后一个月，最后一个月允许
                    		String thisMonth = SysDateMgr.getSysDate();
                    		if(!thisMonth.substring(0, 7).equals(endDate.substring(0, 7)))
                    		{
                    			//活动未结束
                        		StringBuilder strError = new StringBuilder("业务受理前条件判断:").append("宽带1+活动的优惠未截止不允许宽带拆机！请使用“宽带特殊拆机”功能进行拆机！");
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751031, strError.toString());
                    		}
                    	}else
                    	{
                    		//活动已结束
                    	}
                	}
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBChecklimitTradeByWidenet1puls() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}

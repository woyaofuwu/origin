
package com.asiainfo.veris.crm.order.soa.frame.bcf.base;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.BaseService;
import com.ailk.service.invoker.impl.ServiceMethodIntercept;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.crmlog.CrmLog;
import com.asiainfo.veris.crm.order.soa.frame.bcf.fuzzy.DataFuzzy;
import com.asiainfo.veris.crm.order.soa.frame.bcf.fuzzy.DataFuzzyQHAI;
import com.asiainfo.veris.crm.order.soa.frame.bcf.fuzzy.DataFuzzySHXI;
import com.asiainfo.veris.crm.order.soa.frame.bcf.fuzzy.DataFuzzyTJIN;
import com.asiainfo.veris.crm.order.soa.frame.bcf.menu.SystemGuiMenuInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.syslog.SysLog;
import com.asiainfo.veris.crm.order.soa.frame.bcf.touch.CustTouch;

public class CSBizIntercept extends ServiceMethodIntercept
{
	private final static Logger logger = Logger.getLogger(DataFuzzySHXI.class);
	
    @Override
    public boolean after(String svcName, IData head, IData inData, IDataset outDataset) throws Exception
    {
        // 数据模糊化
    	if(ProvinceUtil.isProvince(ProvinceUtil.SHXI))
    	{
    		DataFuzzySHXI.fuzzy(svcName, inData, outDataset);
    	}
    	else if(ProvinceUtil.isProvince(ProvinceUtil.TJIN))
    	{
    		DataFuzzyTJIN.fuzzy(svcName, inData, outDataset);
    	}
    	else if(ProvinceUtil.isProvince(ProvinceUtil.QHAI))
    	{
    	    DataFuzzyQHAI.fuzzy(svcName, inData, outDataset);
    	}
    	else
    	{
    		DataFuzzy.fuzzy(svcName, inData, outDataset);
    	}
    	
    	if(ProvinceUtil.isProvince(ProvinceUtil.SHXI))
    	{
    		boolean flag = BizEnv.getEnvBoolean("crm.fuzzy.outdata.log", false);
    		if(flag)
    		{
    			String inModeCode = CSBizService.getVisit().getInModeCode();
        		BaseService service = (BaseService) SessionManager.getInstance().getMainService();
        		String mainSvcName = service.getName();
        		if(logger.isInfoEnabled())
        		{
        			if("0".equals(inModeCode) && (svcName.equals(mainSvcName)))
        			{
        				String menuId = CSBizService.getVisit().getMenuId();
        				logger.info("testfuzzyByAnwx--MENU_ID[" + menuId + "]菜单名[" + SystemGuiMenuInfo.getMenuTextByMenuId(menuId) + "]服务名[" + svcName + "]工号[" + CSBizService.getVisit().getStaffId() + "]出参:" + outDataset.toString());
        			}
        		}
    		}
    	}

        // CRM操作日志
        CrmLog.log(svcName, inData, outDataset);
        // CRM审计日志
		try
		{
			CrmLog.auditLog(svcName, inData, outDataset);
		}
		catch (Exception e)
		{
			logger.info("审计日志异常：" + e);
		}



        // 系统输出日志
        SysLog.logDataOut(svcName, inData, outDataset);

        if (ProvinceUtil.isProvince(ProvinceUtil.HNAN))
        {
            // HXYD-YZ-REQ-20140704-005关于在统计库上建表并做数据记录的需求
            // 记录一级BOSS调用服务日志到统计库表TF_B_EPOINT_LOG。
            try
            {
                CrmLog.logBoss(svcName, head, inData, outDataset);
            }
            catch (Exception e)
            {
            }
        }

        return true;
    }

    @Override
    public boolean before(String svcName, IData head, IData inData) throws Exception
    {
        // 客户接触
        CustTouch.touch(svcName, inData);

        // 系统输入日志
        SysLog.logDataIn(svcName, head, inData);

        return true;
    }
}

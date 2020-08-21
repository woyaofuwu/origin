package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpExtInfoQry;

public class ChkCrtGrpTestForAll extends BreBase implements IBREScript
{

	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkCrtGrpTestForAll.class);
    
	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkCrtGrpTestForAll() >>>>>>>>>>>>>>>>>>");
        }

        boolean bResult = true;
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        String custId = databus.getString("CUST_ID");
        if(StringUtils.isNotBlank(custId))
        {
        	String vStaffId = CSBizBean.getVisit().getStaffId();
        	if(StringUtils.isNotBlank(vStaffId) && !vStaffId.startsWith("HNSJ")
            		&& !vStaffId.startsWith("HNHN"))
        	{
        		IDataset extInfos = GrpExtInfoQry.queryGrpExtendTestByCustId(custId);
        		if(IDataUtil.isNotEmpty(extInfos))
        		{
        			String err = "不是HNSJ或HNHN的工号，不能用测试集团创建集团产品!";
    				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
         	        bResult = false;
         	        return bResult;
        		}
        	}
        	
        }
        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ChkCrtGrpTestForAll()<<<<<<<<<<<<<<<<<<<");
        }

        return bResult;
    }

}


package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreatePersonUserRequestData;

public class LimitCPEAction implements ITradeAction
{

    // @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        String brandCode = createPersonUserRD.getMainProduct().getBrandCode();
        String limitCPETag = "0";
        IDataset commParaInfos = CommparaInfoQry.getCommparaAllCol("CSM", "9122","LIMIT_CPE_TAG", CSBizBean.getUserEparchyCode());
        if(IDataUtil.isNotEmpty(commParaInfos))
        {
        	limitCPETag=commParaInfos.getData(0).getString("PARA_CODE1", "0");
        }
        IDataset dataSet = ResCall.getMphonecodeInfo(createPersonUserRD.getUca().getSerialNumber());
		if ("1".equals(limitCPETag) && IDataUtil.isNotEmpty(dataSet))
		{
			String beautifulTag = dataSet.first().getString("BEAUTIFUAL_TAG");
			//System.out.println("----------------------LimitCPEAction------------------BEAUTIFUAL_TAG:"+beautifulTag+",brandCode:"+brandCode);
			if (StringUtils.equals("1", beautifulTag) && StringUtils.equals("CPE1", brandCode))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"吉祥号码不能用于CPE开户！");
			}
		}

    }
}


package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

/**
 * 校验是否已经办理了宽带
 * 
 * @author chenzm
 * @date 2014-05-23
 */
public class CheckExsitsWidenetInfoInquire extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    /**
     * 是否已经办理了宽带判断
     */
    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
    	String errorInfo = "";
        String serialNumber = databus.getString("SERIAL_NUMBER");
        IData widenetInfo = UcaInfoQry.qryUserInfoBySn("KD_" + serialNumber);
        if (IDataUtil.isNotEmpty(widenetInfo))
        {// 已办理宽带
        	StringBuilder tips=new StringBuilder();
        	
        	String userId = widenetInfo.getString("USER_ID");
        	
        	IDataset usd = UserDiscntInfoQry.getUserByDiscntCode(userId, "84020042");
        	if(IDataUtil.isNotEmpty(usd))
        	{
        		errorInfo = "当前宽带号码办理了琼中户户通50M包年套餐，不允许过户！";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20181126", errorInfo);
        	}
        	
   		 	IDataset dataset = WidenetInfoQry.getUserWidenetInfo(userId);
   		 	if(IDataUtil.isNotEmpty(dataset)){
	   		 	String rsrvStr2=dataset.getData(0).getString("RSRV_STR2","");
		   		if(rsrvStr2.equals("1")||
						 rsrvStr2.equals("2")||rsrvStr2.equals("3")||rsrvStr2.equals("5")){
		   			tips.append("原用户有生效的宽带业务，过户后将转移至新用户，请告知客户此点并确认是否继续办理!");
		            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, "", tips.toString());
		            return true;
				}
   		 	}
   		 	
        }
        return false;
    }

}

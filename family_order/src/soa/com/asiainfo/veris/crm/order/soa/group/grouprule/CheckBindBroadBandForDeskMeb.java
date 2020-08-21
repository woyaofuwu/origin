package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class CheckBindBroadBandForDeskMeb extends BreBase implements IBREScript{
	
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckBindBroadBandForDeskMeb.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

    	if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckBindBroadBandForDeskMeb() >>>>>>>>>>>>>>>>>>");
        }

        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        /* 自定义区域 */
        String userIdB = databus.getString("USER_ID_B", "");//成员用户UserId
        
        //多媒体桌面电话成员注销时,查看该成员是否有绑定的宽带账号
        if(StringUtils.isNotBlank(userIdB))
        {
        	IDataset uuInfoDatas = RelaUUInfoQry.getRelaUUInfoByUserIdA(userIdB, "T9");
        	if(IDataUtil.isNotEmpty(uuInfoDatas))
            {
            	String serialNumberB = uuInfoDatas.getData(0).getString("SERIAL_NUMBER_B","");
            	String err =  "该用户已经绑定宽带账号" + serialNumberB + ",不可退订多媒体桌面电话,如果想退订多媒体桌面电话,则先解绑绑定的宽带账号!";
    			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
    			return false;
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< CheckBindBroadBandForDeskMeb()<<<<<<<<<<<<<<<<<<<");
        }

        return true;
    }

}

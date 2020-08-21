
package com.asiainfo.veris.crm.order.soa.script.rule.score;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
/**
 * 判断用户入网时间是否满6个月
 * @author tanzheng
 *
 */
public class TBCheckOpenTimeRule extends BreBase implements IBREScript
{
	private static final long serialVersionUID = 1L;
	private static transient Logger logger = Logger.getLogger(TBCheckOpenTimeRule.class);

    /**
     * 判断用户入网时间是否满6个月，满六个月才可以积分兑换
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckOpenTimeRule() >>>>>>>>>>>>>>>>>>");

        /* 获取业务台账，用户资料信息 */
        String serialNumber = IDataUtil.chkParam(databus, "SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20171017, "该手机号码不存在！");
        	return true;
        }
        String dateStr = userInfo.getString("OPEN_DATE");
        if(StringUtils.isBlank(dateStr)){
        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20171018, "该用户开户时间不足六个月，无法办理该业务！");
        	return true;
        }
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = formatter.parse(dateStr);
        
        //获取用户开户时间之后的六个月的时间
        Date sixMonthDate = DateUtils.addMonths(date, 6);
        
        long nowDate = System.currentTimeMillis();
        if(nowDate<sixMonthDate.getTime()){
        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20171019, "该用户开户时间不足六个月，无法办理该业务！");
        	return true;
        }
        
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckOpenTimeRule() false <<<<<<<<<<<<<<<<<<<");

        return false;
    }

}

package com.asiainfo.veris.crm.order.soa.script.rule.user;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * REQ201704190009  需求
 * add 2017/5/18  
 * 
 * @Description: 对于非下述产品的号码发起暂停\恢复（走的是BIP2B956、BIP2B958接口），  
 *               在操作界面提示拦截，号码办理的产品目前不支持暂停/恢复操作，不允许此次业务办理  
 * @author: duhj
 */
public class CheckBeforeForWLWOwnService extends BreBase implements IBREScript
{
	private static Logger logger = Logger.getLogger(CheckBeforeForWLWOwnService.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
	{
		if (logger.isDebugEnabled())
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckBeforeForWLWOwnService() >>>>>>>>>>>>>>>>>>");

		/* 自定义区域 */
		boolean bResult = false;
        String strId = databus.getString("USER_ID");
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String strBrandCode = databus.getString("BRAND_CODE");

        if("PWLW".equals(strBrandCode)){
            if("131".equals(strTradeTypeCode)|| strTradeTypeCode.equals("133")|| strTradeTypeCode.equals("272")){
        		IData param = new DataMap();
        		param.put("USER_ID", strId);

        		StringBuilder sql = new StringBuilder(2500);
        		sql.append(" SELECT A.SERVICE_ID  FROM TF_F_USER_SVC A ");
        		sql.append(" WHERE A.USER_ID=:USER_ID AND A.END_DATE >SYSDATE");
        		sql.append(" AND A.SERVICE_ID IN ");
        		sql.append(" ( SELECT B.PARAM_CODE FROM TD_S_COMMPARA  B WHERE B.PARAM_ATTR='9014' ");
        		sql.append(" AND  B.PARA_CODE1 IN ");
        		sql.append(" ('I00010100095','I00010100035','I00010100085','I00010100092','I00010100008','I00010100093','I00010100180','I00011100008','I00011100009')) ");

        		IDataset userServices = Dao.qryBySql(sql, param);
        		if (IDataUtil.isEmpty(userServices))
        		{
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 170515, "业务受理前条件判断：该号码办理的产品目前不支持暂停或开机业务，不允许此次业务办理。");
        		}

            }
        }
        

		if (logger.isDebugEnabled())
			logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckBeforeForWLWOwnService() " + bResult + "<<<<<<<<<<<<<<<<<<<");

		return bResult;
	}
}

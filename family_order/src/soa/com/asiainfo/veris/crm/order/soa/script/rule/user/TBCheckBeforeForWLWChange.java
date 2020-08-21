package com.asiainfo.veris.crm.order.soa.script.rule.user;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * REQ201704190009  需求
 * add 2017/5/18  
 * 
 * @Description: 五项考核优化--物联网暂停恢复间隔频次优化(30分钟内不能办理恢复类业务)    
 * @author: duhj
 */
public class TBCheckBeforeForWLWChange extends BreBase implements IBREScript
{
	private static Logger logger = Logger.getLogger(TBCheckBeforeForWLWChange.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
	{
		if (logger.isDebugEnabled())
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckBeforeForWLWChange() >>>>>>>>>>>>>>>>>>");

		String time="SYSDATE - 1 / 48";//默认30分钟
		String time2="30";//默认30分钟
        //查询出参数中配置的物联网限制时间
        IDataset results = CommparaInfoQry.getCommNetInfo("CSM", "9019", "20170523");
        if(IDataUtil.isNotEmpty(results)){
             time=results.getData(0).getString("PARA_CODE1");
    		 time2=results.getData(0).getString("PARA_CODE2");
        }

		/* 自定义区域 */
		boolean bResult = false;
        String strId = databus.getString("USER_ID");
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String strBrandCode = databus.getString("BRAND_CODE");

        if("PWLW".equals(strBrandCode)){
            if("131".equals(strTradeTypeCode)|| strTradeTypeCode.equals("133")|| strTradeTypeCode.equals("272")){
            	if("131".equals(strTradeTypeCode)){//报停受理，查询30分钟前是否有报开业务，如果有，不能受理
            		strTradeTypeCode="133";
            	}else if("133".equals(strTradeTypeCode))//报开受理，查询30分钟前是否有报停业务，如果有，不能受理
            	{
            		strTradeTypeCode="131";
            	}
        		IData param = new DataMap();
        		param.put("USER_ID", strId);
        		param.put("TRADE_TYPE_CODE", strTradeTypeCode);
        		param.put("TIME", time);

        		StringBuilder sql = new StringBuilder(2500);
        		sql.append(" SELECT TO_CHAR(TRADE_ID) TRADE_ID ");
        		sql.append(" FROM TF_B_TRADE T ");
        		sql.append(" WHERE 1=1 ");
        		sql.append(" AND T.TRADE_TYPE_CODE = :TRADE_TYPE_CODE ");
        		sql.append(" AND T.USER_ID = :USER_ID ");
                sql.append("AND T.ACCEPT_DATE >=");
                sql.append(time);		
        		sql.append(" UNION ALL");
        		sql.append(" SELECT TO_CHAR(TRADE_ID) TRADE_ID ");
        		sql.append(" FROM TF_BH_TRADE T ");
        		sql.append(" WHERE 1=1 ");
        		sql.append(" AND T.TRADE_TYPE_CODE = :TRADE_TYPE_CODE ");
        		sql.append(" AND T.USER_ID = :USER_ID ");
                sql.append("AND T.ACCEPT_DATE >=");
                sql.append(time);



        		IDataset userTrade = Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));
        		if (IDataUtil.isNotEmpty(userTrade))
        		{
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 170515, "业务受理前条件判断：该用户"+time2+"分钟前已操作过暂停或开机，为准确处理业务，请在"+time2+"分钟后再操作。");

        		}
            }
        }


		if (logger.isDebugEnabled())
			logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckBeforeForWLWChange() " + bResult + "<<<<<<<<<<<<<<<<<<<");

		return bResult;
	}
}

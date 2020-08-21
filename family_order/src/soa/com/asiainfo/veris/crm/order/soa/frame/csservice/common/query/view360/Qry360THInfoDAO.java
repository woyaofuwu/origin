
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;

public class Qry360THInfoDAO
{
    /**
     * 根据USER_ID查询业务历史 受理信息
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryThAcceptInfo(IData data, Pagination pagination) throws Exception
    {
        String user_id = data.getString("USER_ID", "");

        // 如果没有传入USER_ID，则直接返回
        if ("".equals(user_id))
        {
            return new DatasetList();
        }

        // TODO select * 待处理
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" ");

        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 根据USER_ID查询业务历史基本信息
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryThBaseInfo(IData param, Pagination pagination) throws Exception
    {
        String trade_id = param.getString("TRADE_ID", "").trim();
        if ("".equals(trade_id))
        {
            return new DatasetList();
        }
        param.put("ACCEPT_MONTH", trade_id.substring(5, 6));
        String history_query_type = param.getString("HISTORY_QUERY_TYPE", "");
        boolean flag = false;
        if ("G".equals(history_query_type))
        {
            flag = true;
        }
        /*
         * if(!"G".equals(history_query_type)){ String route_eparchy_code = param.getString(Route.ROUTE_EPARCHY_CODE,
         * pd.getContext().getEpachyId()); pd.setRouteEparchy(route_eparchy_code);//当不是集体业务历史查询时，需设置路由 }
         */
        IData baseInfo = new DataMap();

        // 获取tf_b_trade_user表中user信息
        TradeUserDAO userDao = new TradeUserDAO();
        THBaseDAO hn_userDao = new THBaseDAO(); // 用户用求没有用户资料变更查当前表
        IDataset infos = new DatasetList();
        if (flag)
        {
            infos = userDao.getUserInfosByCg(param);
        }
        else
        {
            infos = userDao.getUserInfos(param);
        }
        IData info = new DataMap();
        if (infos.size() > 0)
        {
            info = infos.getData(0);
            baseInfo.put("USER_TYPE_CODE", info.get("USER_TYPE_CODE"));
            baseInfo.put("EPARCHY_CODE", info.get("EPARCHY_CODE"));
            baseInfo.put("USER_TYPE", info.get("USER_TYPE"));
            baseInfo.put("OPEN_DATE", info.get("OPEN_DATE"));
            baseInfo.put("DEVELOP_STAFF_ID", info.get("DEVELOP_STAFF_ID"));
            baseInfo.put("DEVELOP_DATE", info.get("DEVELOP_DATE"));
            baseInfo.put("DEVELOP_DEPART_ID", info.get("DEVELOP_DEPART_ID"));
            baseInfo.put("DEVELOP_NO", info.get("DEVELOP_NO"));
            baseInfo.put("CONTRACT_ID", info.get("CONTRACT_ID"));
            baseInfo.put("REMOVE_REASON_CODE", info.get("REMOVE_REASON_CODE"));
        }
        else
        {
            if (flag)
            {
                infos = hn_userDao.getUserInfosByCg(param, pagination);
            }
            else
            {
                infos = hn_userDao.getUserInfos(param, pagination);
            }
            if (infos.size() > 0)
            {
                info = infos.getData(0);
                baseInfo.put("USER_TYPE_CODE", info.get("USER_TYPE_CODE"));
                baseInfo.put("EPARCHY_CODE", info.get("EPARCHY_CODE"));
                baseInfo.put("OPEN_DATE", info.get("OPEN_DATE"));
                baseInfo.put("DEVELOP_STAFF_ID", info.get("DEVELOP_STAFF_ID"));
                baseInfo.put("DEVELOP_DATE", info.get("DEVELOP_DATE"));
                baseInfo.put("DEVELOP_DEPART_ID", info.get("DEVELOP_DEPART_ID"));
                baseInfo.put("DEVELOP_NO", info.get("DEVELOP_NO"));
                baseInfo.put("CONTRACT_ID", info.get("CONTRACT_ID"));
                baseInfo.put("REMOVE_REASON_CODE", info.get("REMOVE_REASON_CODE"));
            }
        }

        // 获取TF_B_TRADE_CUST_PERSON表中客户信息
        TradeCustPersonDAO cpDao = new TradeCustPersonDAO();
        if (flag)
        {
            infos = cpDao.getCustPersonInfosByCg(param);
        }
        else
        {
            infos = cpDao.getCustPersonInfos(param);
        }
        if (infos.size() > 0)
        {
            info = infos.getData(0);
            baseInfo.put("SEX", info.get("SEX"));
            baseInfo.put("BIRTHDAY", info.get("BIRTHDAY"));
            baseInfo.put("PSPT_TYPE_CODE", info.get("PSPT_TYPE_CODE"));
            baseInfo.put("PSPT_ID", info.get("PSPT_ID"));
            baseInfo.put("PSPT_END_DATE", info.get("PSPT_END_DATE"));
            baseInfo.put("PSPT_ADDR", info.get("PSPT_ADDR"));
            baseInfo.put("HOME_ADDRESS", info.get("HOME_ADDRESS"));
        }
        else
        {
            if (flag)
            {
                infos = hn_userDao.getCustPersonInfosByCg(param, pagination);
            }
            else
            {
                infos = hn_userDao.getCustPersonInfos(param, pagination);
            }
            if (infos.size() > 0)
            {
                info = infos.getData(0);
                baseInfo.put("SEX", info.get("SEX"));
                baseInfo.put("BIRTHDAY", info.get("BIRTHDAY"));
                baseInfo.put("PSPT_TYPE_CODE", info.get("PSPT_TYPE_CODE"));
                baseInfo.put("PSPT_ID", info.get("PSPT_ID"));
                baseInfo.put("PSPT_END_DATE", info.get("PSPT_END_DATE"));
                baseInfo.put("PSPT_ADDR", info.get("PSPT_ADDR"));
                baseInfo.put("HOME_ADDRESS", info.get("HOME_ADDRESS"));
            }
        }

        // 获取tf_b_trade_cust_person表中相关客户信息
        TradeCustomerDAO custDao = new TradeCustomerDAO();
        if (flag)
        {
            infos = custDao.getCustomerInfosByCg(param, pagination);
        }
        else
        {
            infos = custDao.getCustomerInfos(param, pagination);
        }

        if (infos.size() > 0)
        {
            info = infos.getData(0);
            baseInfo.put("OPEN_LIMIT", info.get("OPEN_LIMIT"));
        }
        else
        {
            if (flag)
            {
                infos = hn_userDao.getCustomerInfosByCg(param, pagination);
            }
            else
            {
                infos = hn_userDao.getCustomerInfos(param, pagination);
            }
            if (infos.size() > 0)
            {
                info = infos.getData(0);
                baseInfo.put("OPEN_LIMIT", info.get("OPEN_LIMIT"));
            }
        }

        // 获取TF_B_TRADE_ACCOUNT表中账户相关信息
        TradeAccountDAO acDao = new TradeAccountDAO();
        if (flag)
        {
            infos = acDao.getAccountInfosByCg(param, pagination);
        }
        else
        {
            infos = acDao.getAccountInfos(param, pagination);
        }

        if (infos.size() > 0)
        {
            info = infos.getData(0);
            baseInfo.put("PAY_NAME", info.get("PAY_NAME"));
            baseInfo.put("PAY_MODE_CODE", info.get("PAY_MODE_CODE"));
            baseInfo.put("BANK_CODE", info.get("BANK_CODE"));
            baseInfo.put("BANK_ACCT_NO", info.get("BANK_ACCT_NO"));
        }
        else
        {
            if (flag)
            {
                infos = hn_userDao.getAccountInfosByCg(param, pagination);
            }
            else
            {
                infos = hn_userDao.getAccountInfos(param, pagination);
            }
            if (infos.size() > 0)
            {
                info = infos.getData(0);
                baseInfo.put("PAY_NAME", info.get("PAY_NAME"));
                baseInfo.put("PAY_MODE_CODE", info.get("PAY_MODE_CODE"));
                baseInfo.put("BANK_CODE", info.get("BANK_CODE"));
                baseInfo.put("BANK_ACCT_NO", info.get("BANK_ACCT_NO"));
            }
        }

        // 对于trade_type_code是415(跨区入网服务确认)的业务，需要展示详细信息
        String trade_type_code = param.getString("TRADE_TYPE_CODE", "");
        if ("415".equals(trade_type_code))
        {// 跨区入网服务确认
            infos = userDao.getNetServiceInfos(param, pagination);
            if (infos.size() > 0)
            {
                info = infos.getData(0);
                baseInfo.put("RSRV_STR1", info.get("RSRV_STR1"));
                baseInfo.put("RSRV_STR2", info.get("RSRV_STR2"));
                baseInfo.put("RSRV_STR3", info.get("RSRV_STR3"));
                baseInfo.put("RSRV_STR4", info.get("RSRV_STR4"));
            }
        }
        if (baseInfo != null)
        {
            baseInfo.put("TRADE_TYPE_CODE_LIS", trade_type_code);
        }
        IDataset retdata = new DatasetList();
        retdata.add(baseInfo);
        return retdata;
    }
    
    public static IDataset getTradeCodeNoQuery(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select data_id from td_s_static where type_id='USER360VIEW_TRADENOQUERY'");
    	return Dao.qryByParse(parser);
    }
    
    public static IDataset queryHTradeScoreInfo(IData param, Pagination pagination) throws Exception
    {
    	
		String user_id = param.getString("USER_ID", "").trim();
		String trade_id=param.getString("TRADE_ID", "").trim();
		String queryYear=param.getString("QUERY_YEAR","");
		if(StringUtils.isBlank(user_id) && StringUtils.isBlank(trade_id))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_1203);
		}
		if(StringUtils.isBlank(queryYear))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_1204);
		}
		String index = "";
		if(StringUtils.isBlank(trade_id)){
			index = queryYear+"_USER_ID";
		}else{
			index = queryYear+"_TRADE_ID";
		}
		
		/**
		 * 在线代码
		 */
		String tradeHisTableName="TF_BHB_TRADE_"+queryYear;
		String tradeScoreHisTableName="TF_BHB_TRADE_SCORE_"+queryYear;
	
		
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select /*+ index(a IDX_TF_BHB_TRADE_"+index+")*/a.trade_id,a.in_mode_code,a.accept_month||'月' accept_month,a.order_id,a.priority,a.trade_type_code,");
		parser.addSQL(" decode(a.subscribe_type,'0','普通立即执行','1','普通预约执行','100','批量立即执行','101','批量预约执行','200','信控执行','') subscribe_type,");
		parser.addSQL(" a.subscribe_state,");
		parser.addSQL(" a.cancel_tag,");
		parser.addSQL(" decode(a.fee_state,'0','未收费','1','已收费','') fee_state,");
		parser.addSQL(" a.next_deal_tag,");
		parser.addSQL(" a.serial_number,a.brand_code,a.oper_fee,a.foregift,a.fee_time,a.exec_time,a.finish_date, ");
		parser.addSQL(" a.eparchy_code, a.city_code,a.trade_staff_id, a.trade_depart_id, a.trade_eparchy_code, a.trade_city_code, a.remark, ");
		parser.addSQL(" a.INVOICE_NO,	a.ADVANCE_PAY , a.FEE_STAFF_ID, a.ACCT_ID ,");
		parser.addSQL(" a.accept_date,a.cust_name,a.user_id,a.cust_id, substr(a.process_tag_set, 9,1) process_tag_set,");
		parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR4,'') RSRV_STR4, ");
		parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR5,'') RSRV_STR5, ");
		parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR6,'') RSRV_STR6, ");
		parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR7,'') RSRV_STR7, ");
		parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR8,'') RSRV_STR8, ");
		parser.addSQL(" decode(b.RSRV_STR3,'Z',b.RSRV_STR9,'') RSRV_STR9 ");
		parser.addSQL(" from " + tradeHisTableName + " a , " + tradeScoreHisTableName + " b");
		parser.addSQL(" where 1=1 ");
		parser.addSQL(" and a.trade_id=b.trade_id(+)");
		//parser.addSQL(" and a.user_id=:USER_ID");
		parser.addSQL(" and a.user_id in(");
		parser.addSQL(":USER_ID,");
		parser.addSQL(" (select t.user_id from tf_f_user t where  t.serial_number ='KD_'||(SELECT b.serial_number FROM tf_f_user b WHERE 1=1 AND b.user_id=:USER_ID) and t.remove_tag='0' )");
		parser.addSQL(" )");
		parser.addSQL(" and a.trade_id=:TRADE_ID");
		parser.addSQL(" and a.trade_type_code=:TRADE_TYPE_CODE");
		parser.addSQL(" and a.accept_date >= to_date(:START_DATE, 'yyyy-mm-dd')");
		parser.addSQL(" and a.accept_date < to_date(:END_DATE, 'yyyy-mm-dd') +1 ");		
		//增加查询需要屏蔽的trade_type_code
		if(!param.getString("USER360VIEW_TRADENOQUERY","").equals("")){
			parser.addSQL(" and a.trade_type_code not in ("+param.getString("USER360VIEW_TRADENOQUERY","")+") ");
		}
		
		parser.addSQL(" order by ACCEPT_DATE desc ");
		
		/**
		 * 在线代码
		 */
		IDataset results = Dao.qryByParse(parser,Route.CONN_CRM_HIS);

		
		if(IDataUtil.isEmpty(results))
		{
			return new DatasetList();
		}
		else
		{
			IData resultInfo = new DataMap();
			for(int i=0; i<results.size(); i++){
				resultInfo = results.getData(i);
				String tradeTypeCode = resultInfo.getString("TRADE_TYPE_CODE", "");
				resultInfo.put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(resultInfo.getString("BRAND_CODE","")));
				if(!"".equals(tradeTypeCode) && "110".equals(tradeTypeCode)){
					String process_tag_set = resultInfo.getString("PROCESS_TAG_SET", "");
					resultInfo.put("TAG_110", "1");
					if("1".equals(process_tag_set)){
						resultInfo.put("PROCESS_TAG_SET", "服务变更");
					}else if("2".equals(process_tag_set)){
						resultInfo.put("PROCESS_TAG_SET", "优惠变更");
					}else if("3".equals(process_tag_set)){
						resultInfo.put("PROCESS_TAG_SET", "服务和优惠变更");
					}else if("4".equals(process_tag_set)){
						resultInfo.put("PROCESS_TAG_SET", "产品变更");
					}else{
						resultInfo.put("TAG_110", "2");
					}
				}else{
					resultInfo.put("TAG_110", "2");
				}
			}
		}
		return results;
    }
    
}

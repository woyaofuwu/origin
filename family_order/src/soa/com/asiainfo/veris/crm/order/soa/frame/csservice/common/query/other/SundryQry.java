
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ailk.biz.bean.BizBean;
import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BrandException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.TimeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class SundryQry extends CSBizBean
{

    /**
     * 定时导出营业新开户用户清单数据
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset exportOpenUserInfo(IData inparams) throws Exception
    {

        // 欠费用户
        // inparams.put("REMOVE_TAG", "4");
        // 用户服务状态
        // inparams.put("TRADE_TYPE_CODE", "100");

        return Dao.qryByCode("TF_F_USER", "SEL_BY_OPENDATE_NG", inparams, new Pagination());
    }

    /**
     * 定时导出用户IMSI信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset exportUserImsi(IData inparam) throws Exception
    {

        return queryUserImsiInfo(inparam, new Pagination());
    }

    /**
     * 获取IMSI数据文件FTP配置参数
     * 
     * @return
     * @throws Exception
     */
    public static IDataset getImsiFtpParams(IData inparams, Pagination pagination) throws Exception
    {

        SQLParser parser = new SQLParser(inparams);
        parser.addSQL("select t.* from td_s_commpara t where 1 = 1");
        parser.addSQL(" and t.param_attr = 188");
        parser.addSQL(" and t.param_code = '0'");
        parser.addSQL(" and t.subsys_code = 'CSM'");
        return Dao.qryByParse(parser);
    }

    /**
     * 根据SERIAL_NUMBER,REMOVE_TAG,NET_TYPE_CODE查询用户信息
     */
    public static IDataset getUserInfoBySn(IData inparams) throws Exception
    {
        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(inparams.getString("SERIAL_NUMBER"));
        IDataset dataset = new DatasetList();
        dataset.add(userInfo);

        return dataset;
    }

    /**
     * VPN兑换话费卡查询
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getVPNGiveCardInfo(IData inparams, Pagination pagination) throws Exception
    {
        String route = null;
        if (inparams.getString(Route.ROUTE_EPARCHY_CODE, "").length() > 1)
        {
            route = inparams.getString(Route.ROUTE_EPARCHY_CODE);
        }

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_VPN_VCAD", inparams, pagination, route);
    }

    /**
     * 获取业务区数据，按AREA_CODE排序
     * 
     * @return
     * @throws Exception
     */
    public static IDataset initCityArea(boolean fltByStaffId) throws Exception
    {

        String cityId = getVisit().getCityCode().toUpperCase();
        String staffId = getVisit().getStaffId();
        IDataset areas = UAreaInfoQry.qryAreaByAreaLevel30();
        // areas.sort("AREA_CODE", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
        // 过滤掉：一级BOSS业务区(HNIB)、门户网站接口(HWEB)、多媒体自助接口(MEDA)、短信接口(MESG)、客服系统接口(SERV)
        Map filterMap = new HashMap();
        filterMap.put("HNIB", "一级BOSS业务区");
        filterMap.put("HWEB", "门户网站接口");
        filterMap.put("MEDA", "多媒体自助接口");
        filterMap.put("MESG", "短信接口");
        filterMap.put("SERV", "客服系统接口");

        IDataset temp = new DatasetList();
        for (Iterator it = areas.iterator(); it.hasNext();)
        {
            IData dt = (IData) it.next();
            if (!filterMap.containsKey(dt.get("AREA_CODE")))
            {
                String areaCode = dt.getString("AREA_CODE", "").toUpperCase();
                // 根据工号限制业务区
                if (fltByStaffId)
                {
                    // 只有省局的账号可以查看所有业务区的数据
                    if (cityId.equals("HNSJ") || staffId.startsWith("HNSJ"))
                    {
                        temp.add(dt);
                        continue;
                    }
                    // 不是省局的账号只能查看所属业务区的数据
                    if (cityId.equals(areaCode) || staffId.startsWith(areaCode))
                    {
                        temp.add(dt);
                        continue;
                    }
                }
                else
                {// 不用限制业务区
                    temp.add(dt);
                }

            }
        }

        return temp;
    }

    /**
     * 执行帐户变更查询
     * 
     * @param inparam
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryAccountChgInfo(IData inparam, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_ACCOUNTMODIFY_QRY", inparam, pagination);
    }

    /**
     * 用户清单查询日志查询 获取用户清单查询日志
     * 
     * @param inparam
     *            查询所需参数
     * @return IDataset 用户清单查询日志列表
     * @throws Exception
     */
    public static IDataset queryBillistlog(IData inparam, Pagination pagination) throws Exception
    {

        inparam.put("PARA_CODE1", "1170");

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_HIS_TRADE", inparam, pagination);
    }

    /**
     * 执行改号业务查询sql
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryChangeSnInfo(IData inparams, Pagination pagination) throws Exception
    {

        // 欠费用户
        // inparams.put("REMOVE_TAG", "4");
        // 用户服务状态 143 - 改号
        inparams.put("TRADE_TYPE_CODE", "143");

        return Dao.qryByCode("TF_BH_TRADE", "SEL_CHANGESN", inparams, pagination, Route.getJourDb(BizBean.getVisit().getStaffEparchyCode()));
    }

    // 根据代理商编号获取代理商缴纳的押金
    public static IDataset queryChannelDeposit(String departCode) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("DEPART_CODE", departCode);
        IDataset books = Dao.qryByCode("CHNL_DAT_BOOK", "SEL_MONEY_BY_DEPARTCODE", inparams);
        if (IDataUtil.isEmpty(books))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1118, departCode);
        }
        return books;
    }

    /**
     * 执行营业过户用户清单查询sql
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryChgCustOwnerInfo(IData inparams, Pagination pagination) throws Exception
    {

        // 欠费用户
        // inparams.put("REMOVE_TAG", "4");
        // 用户服务状态
        inparams.put("TRADE_TYPE_CODE", "100");

        IDataset dataset = Dao.qryByCode("TF_BH_TRADE", "SEL_CHGCUSTOWNERINFO_NEW", inparams, pagination, Route.getJourDb(BizBean.getVisit().getStaffEparchyCode()));
        //TODO huanghua 25 资料表与台账表解耦（营业过户清单查询）---已解决
        if(IDataUtil.isNotEmpty(dataset)){
        	for (int i = 0; i < dataset.size(); i++) {
        		IData temp = dataset.getData(i);
        		temp.put("CUST_NAME_A", queryCustBakInfo(temp.getString("TRADE_ID","")));
			}
        }
        return dataset;
    }
    
    /**
     * 执行营业过户用户清单查询sql
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static String queryCustBakInfo(String tradeId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("TRADE_ID", tradeId);
    	SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT B.CUST_NAME CUST_NAME_A ");
        parser.addSQL(" FROM TF_B_TRADE_CUSTOMER_BAK B ");
        parser.addSQL(" WHERE B.TRADE_ID = :TRADE_ID ");
        parser.addSQL(" AND B.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        IDataset dataset = Dao.qryByParse(parser, Route.getCrmDefaultDb());
        return dataset.getData(0).getString("CUST_NAME_A","");
    }

    /**
     * 短信优惠类型查询
     * 
     * @param inparam
     *            查询所需参数 优惠编码
     * @return IDataset 优惠编码列表
     * @throws Exception
     */
    public static IDataset queryComparaDiscntCC(IData inparam) throws Exception
    {

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_ATTRPARAM_CODE", inparam);
    }

    /**
     * 执行优惠变更历史查询
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryDiscntModifyHisInfo(IData inparams, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_B_TRADE", "SEL_DISCNTHIS_BY_DATE_STAFFID_QRY", inparams, pagination);
    }

    /**
     * 获取绿色田野卡名称
     * 
     * @param inparam
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryGreenCards(IData inparam, Pagination pagination) throws Exception
    {

       // return Dao.qryByCode("TD_B_DISCNT", "SEL_GREEN_CARD", inparam, pagination);
    	//产商品还未提供接口，测试数据及得修改duhj 2017/4/10    	
    	IDataset results=UpcCall.qryOfferFromExtCha("TD_B_DISCNT","TAG_SET","1%");
    	if(IDataUtil.isNotEmpty(results)){
    		for(int i=0;i<results.size();i++){
    			IData  res=results.getData(i);
    			res.put("DISCNT_CODE", res.getString("OFFER_CODE"));
    			res.put("DISCNT_NAME", res.getString("OFFER_NAME"));
    		}
    	}


    	return results;
    }

    /**
     * 执行绿色田野卡基站信息查询
     * 
     * @param inparam
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryGreenCellInfo(IData inparam, Pagination pagination) throws Exception
    {
        IDataset resutls=new DatasetList(); 
		String attrFileCode="";
    	if(StringUtils.isNotBlank(inparam.getString("DISCNT_CODE"))){
    		IDataset fileNames=UItemBInfoQry.qryOfferChaValByFieldNameOfferCodeAndOfferType(inparam.getString("DISCNT_CODE"), BofConst.ELEMENT_TYPE_CODE_DISCNT, "10000000");
    		if(IDataUtil.isNotEmpty(fileNames)){
    			 attrFileCode = fileNames.getData(0).getString("ATTR_FIELD_CODE");
    	 
    			 } 
    		inparam.put("SPEC_AREA_ID", attrFileCode);    	
       	    resutls=Dao.qryByCode("TD_B_DISCNT", "SEL_GREEN_CELL_INFO_NEW", inparam, pagination, Route.CONN_CRM_CEN);

    	}else{
       	   resutls=Dao.qryByCode("TD_B_DISCNT", "SEL_GREEN_CELL_INFO_NEW_2", inparam, pagination, Route.CONN_CRM_CEN);

    	}
    	   	

    	//拆分sql
        //return Dao.qryByCode("TD_B_DISCNT", "SEL_GREEN_CELL_INFO", inparam, pagination);    	

    	return resutls;
    }

    /**
     * 执行营业新开户用户查询sql
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryOpenUserInfo(String cityCode, String startDate, String endDate, String sStaffId, String eStaffId, Pagination pagination) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("CITY_CODE", cityCode);
        inparams.put("START_DATE", startDate);
        inparams.put("END_DATE", endDate);

        int dayInterval = SysDateMgr.dayInterval(startDate, endDate);
        if (dayInterval > 30)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1119, 30);
        }

        inparams.put("START_STAFF_ID", sStaffId);
        inparams.put("END_STAFF_ID", eStaffId);
        return Dao.qryByCode("TF_F_USER", "SEL_BY_OPENDATE_NG", inparams, pagination);
    }

    /**
     * 执行sql查询欠费拆机用户押金清单
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryOweDestroyForegift(IData inparams, Pagination pagination) throws Exception
    {

        // 欠费用户
        inparams.put("REMOVE_TAG", "4");
        // 用户服务状态
        inparams.put("USER_SVC_STATE", "9");

        return Dao.qryByCode("TF_F_USER", "SEL_OWEFEEUSER_FOREGIFT", inparams, pagination);
    }

    /**
     * 执行移动电话欠费销号清单查询sql
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryOweWriteOff(IData inparams, Pagination pagination) throws Exception
    {

        // 欠费用户
        inparams.put("REMOVE_TAG", "4");
        // 用户服务状态
        inparams.put("USER_SVC_STATE", "9");

        return Dao.qryByCode("TF_F_USER", "SEL_OWEFEE_DESTROYINFO", inparams, pagination);
    }

    /**
     * 查询付费类型，帐户变更查询使用
     * 
     * @return
     */
    public static IDataset queryPayModes(IData inparams) throws Exception
    {

        SQLParser parser = new SQLParser(inparams);
        parser.addSQL("select t.PAY_MODE_CODE, t.PAY_MODE from td_s_paymode t where 1 = 1");
        // parser.addSQL(" and t.PAY_MODE_CODE <> '0'");
        parser.addSQL(" order by t.PAY_MODE_CODE");
        return Dao.qryByParse(parser);
    }

    /**
     * 查询真情回报类型的机型
     * 
     * @create_date Jul 31, 2009
     * @author heyq
     */
    public static IDataset queryPurchase() throws Exception
    {

        IData data = new DataMap();
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return Dao.qryByCode("NormalPara", "td_b_purchasetrade", data);
    }

    /**
     * 真情回报客户清单查询
     * 
     * @create_date Jul 31, 2009
     * @author heyq
     */
    public static IDataset queryRepay(IData inParams, Pagination p) throws Exception
    {

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_SALEACTIVE_REPAY_COMMPARA", inParams, p);
    }

    /**
     * 获取校园卡名称
     * 
     * @param inparam
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset querySchoolCards(IData inparam, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TD_B_DISCNT", "SEL_SCHOOL_CARD", inparam, pagination);
    }

    /**
     * 执行校园卡基站信息查询
     * 
     * @param inparam
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset querySchoolCellInfo(IData inparam, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TD_B_DISCNT", "SEL_CELL_INFO_SCHOOL", inparam, pagination);
    }

    public static IDataset querySecKillDetails(String anumber, String sn, String sectype, String stime, String etime) throws Exception
    {
        IData param = new DataMap();
        if (StringUtils.isNotBlank(anumber))
        {
            param.put("ACTIVITY_NUMBER", anumber);
        }
        if (StringUtils.isNotBlank(sn))
        {
            param.put("SERIAL_NUMBER", sn);
        }
        if (StringUtils.isNotBlank(sectype))
        {
            param.put("SECKILL_TYPE", sectype);
        }
        if (StringUtils.isNotBlank(stime))
        {
            param.put("START_TIME", stime);
        }
        if (StringUtils.isNotBlank(etime))
        {
            param.put("END_TIME", etime);
        }

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT SERIAL_NUMBER, PRIZE_TYPE_CODE SECKILL_TYPE, REVC2 PRESENT, ACCEPT_DATE ");
        parser.addSQL(" FROM TF_B_TRADE_UECLOTTERY WHERE 1=1 ");
        parser.addSQL(" AND ACTIVITY_NUMBER = :ACTIVITY_NUMBER");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER");
        parser.addSQL(" AND PRIZE_TYPE_CODE = :SECKILL_TYPE");
        parser.addSQL(" AND ACCEPT_DATE>TO_DATE(:START_TIME,'YYYY-MM-DD HH24:MI:SS')");
        parser.addSQL(" AND ACCEPT_DATE<TO_DATE(:END_TIME,'YYYY-MM-DD HH24:MI:SS')");
        parser.addSQL(" ORDER BY ACCEPT_DATE");
        return Dao.qryByParse(parser,Route.getJourDb(BizRoute.getRouteId()));

    }

    /**
     * 短信查询
     * 
     * @create_date Jul 30, 2009
     * @author heyq
     */
    public static IDataset querySMS(IData inparams, Pagination pagination) throws Exception
    {

        String sql_ref = null;
        if ("1".equals(inparams.getString("QUERY_MODE")))
        {
            return Dao.qryByCode("TD_S_COMMPARA", "SEL_SMS_10086", inparams, pagination);
        }
        else
        {// 0
            String statMonthBefore = inparams.getString("PARA_CODE2");
            ;
            String statMonthAfter = inparams.getString("PARA_CODE3");
            ;
            if (statMonthBefore == null || statMonthBefore.trim().length() != 10 || statMonthAfter == null || statMonthAfter.trim().length() != 10)
            {// 前台会做相关的校验应该不可能为真
                CSAppException.apperr(TimeException.CRM_TIME_32);
            }
            if (statMonthBefore.substring(5, 7).equals(statMonthAfter.subSequence(5, 7)))
            {// 起始、结束的月份相同查一张表
                String oldSqlStmt = null;// dao.getSqlByCodeCode("TD_S_COMMPARA", "SEL_SMS_10658666_TRADELOG");
                String newSqlStmt = oldSqlStmt.replaceAll(":STAT_MONTH", statMonthAfter.substring(5, 7));// 生成新的sql
                SQLParser parser = new SQLParser(inparams);
                parser.addSQL(newSqlStmt);
                return Dao.qryByParse(parser);
            }
            else
            {// 起始、结束的月份不相同查两张表
                String oldSqlStmtDM = null;// dao.getSqlByCodeCode("TD_S_COMMPARA", "SEL_SMS_10658666_TRADELOG_DM");
                String newSqlStmtDM = oldSqlStmtDM.replaceAll(":STAT_MONTH_BEFORE", statMonthBefore.substring(5, 7)).replaceAll(":STAT_MONTH_AFTER", statMonthAfter.substring(5, 7));
                SQLParser parser = new SQLParser(inparams);
                parser.addSQL(newSqlStmtDM);
                return Dao.qryByParse(parser);
            }

        }
    }

    /**
     * 执行营业员特殊补卡业务清单查询sql
     * 
     * @author huanghui
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset querySpeCompCardInfo(IData inparams, Pagination pagination) throws Exception
    {
        // IDataset results = Dao.qryByCode("TF_R_SIMCARD_USE", "SEL_BY_STAFFID_AND_TIME_NG", inparams, pagination);
        IDataset results = new DatasetList();
        IDataset res = Dao.qryByCode("TF_B_TRADE_SIMCARDCOMPFEE", "SEL_BY_STAFFID_AND_TIME_NG", inparams, pagination, Route.getJourDb(BizBean.getVisit().getStaffEparchyCode()));
        String startValue = "";
        String endValue = "";
        if (IDataUtil.isNotEmpty(res))
        {
            startValue = res.getData(0).getString("START_VALUE");
            endValue = res.getData(0).getString("END_VALUE");
            results = ResCall.qryAllSimInfoByRangeNo(startValue, endValue);
        }
        else
        {
            return new DatasetList();
        }

        if (IDataUtil.isEmpty(results))
        {
            return new DatasetList();
        }

        for (Iterator it = res.iterator(); it.hasNext();)
        {
            IData data = (IData) it.next();
            String simTypeName = StaticUtil.getStaticValueDataSource(getVisit(), Route.CONN_RES, "RES_TYPE", new String[]
            { "RES_TYPE_ID" }, "RES_TYPE_NAME", new String[]
            { data.getString("SIM_TYPE_CODE", "") });
            data.put("SIM_TYPE_NAME", simTypeName);
            data.put("CITY_CODE", results.getData(0).getString("CITY_CODE"));
            data.put("STAFF_ID", results.getData(0).getString("STAFF_ID"));
            data.put("FEE", StrUtil.floatToStr("0.00", Double.valueOf(data.getString("FEE","0"))/100));
        }
        return res;
    }

    /**
     * 执行特殊号码处理查询sql
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset querySpecProNumber(IData inparams, Pagination pagination) throws Exception
    {

        // 欠费用户
        // inparams.put("REMOVE_TAG", "4");
        // 用户服务状态
        // inparams.put("TRADE_TYPE_CODE", "100");

        IDataset dataset = Dao.qryByCode("TF_A_SPEC_PROC", "SEL_BY_SER_TIME", inparams, pagination);
        if(IDataUtil.isNotEmpty(dataset)){
        	for (int i = 0; i < dataset.size(); i++) {
        		IData info = dataset.getData(i);
        		info.put("ADJUST_FEE", StrUtil.floatToStr("0.00", Double.valueOf(info.getString("ADJUST_FEE","0"))/100));
			}
        }
        return dataset;
    }

    /**
     * 执行新业务产品资料查询sql
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset querySpServiceTrade(IData inparams, Pagination pagination) throws Exception
    {

        // 欠费用户
        // inparams.put("REMOVE_TAG", "4");
        // 用户服务状态
        // inparams.put("TRADE_TYPE_CODE", "100");
        String route = null;
        if (inparams.getString(Route.ROUTE_EPARCHY_CODE, "").length() > 1)
        {
            route = inparams.getString(Route.ROUTE_EPARCHY_CODE);
        }
//        IDataset dataset = Dao.qryByCode("TD_M_SPSERVICE", "SEL_SPSERVICE_BY_SPSVCID", inparams, pagination, route);
        IDataset dataset = UpcCall.qrySpServiceSpInfo("", inparams.getString("SP_SVC_ID",""), "", inparams.getString("BIZ_STATUS",""));
        if(IDataUtil.isNotEmpty(dataset)){
        	for (int i = 0; i < dataset.size(); i++) {
        		IData info = dataset.getData(i);
        		info.put("PRICE", StrUtil.floatToStr("0.00", Double.valueOf(info.getString("PRICE","0"))/1000));
        		info.put("FOREGIFT", StrUtil.floatToStr("0.00", Double.valueOf(info.getString("FOREGIFT","0"))/100));
			}
        }
        return dataset;
    }

    /**
     * 未完工工单查询 获取未完工业务资料列表
     * 
     * @param inparam
     *            查询所需参数 地州编码
     * @return IDataset 用户资料列表
     * @throws Exception
     */
    public static IDataset queryUnfinishInfo(IData inparam, Pagination pagination) throws Exception
    {

        inparam.put("TRADE_EPARCHY_CODE", getVisit().getStaffEparchyCode());
        inparam.put("TRADE_TYPE_CODE", "0");

        return Dao.qryByCode("TF_B_TRADE", "SEL_BY_ACCEPT_HX", inparam, pagination);
    }

    /**
     * 未完工工单查询 获取未完工业务资料列表
     * 
     * @param inparam
     *            查询所需参数 地州编码
     * @return IDataset 用户资料列表
     * @throws Exception
     */
    public static IDataset queryUnfinishInfoCC(IData inparam, Pagination pagination) throws Exception
    {

        inparam.put("TRADE_TYPE_CODE", "0");

        return Dao.qryByCode("TF_B_TRADE", "SEL_BY_ACCEPT_HXCC", inparam, pagination);
    }

    /**
     * 执行sql查询联通转接信息
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryUnionTrans(IData inparams, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TI_B_TRANS_PHONE", "SEL_BY_PHONE_A_NG", inparams, pagination);
    }

    /**
     * 联通转接查询，提供给接口使用
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryUnionTransIntf(String phoneCodeA, String phoneCodeB) throws Exception
    {
        IData inparams = new DataMap();
        if (StringUtils.isNotBlank(phoneCodeA))
        {
            inparams.put("PHONE_CODE_A", phoneCodeA);
        }
        if (StringUtils.isNotBlank(phoneCodeB))
        {
            inparams.put("PHONE_CODE_B", phoneCodeB);
        }
        return Dao.qryByCodeParser("TI_B_TRANS_PHONE", "SEL_BY_PHONE_A_INTF_NG", inparams);
    }

    /**
     * 执行手机缴费通定制信息查询
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryUserBankInfo(IData inparams, Pagination pagination) throws Exception
    {

        // 欠费用户
        // inparams.put("REMOVE_TAG", "4");
        // 用户服务状态
        // inparams.put("TRADE_TYPE_CODE", "100");
        String route = null;
        if (inparams.getString(Route.ROUTE_EPARCHY_CODE, "").length() > 1)
        {
            route = inparams.getString(Route.ROUTE_EPARCHY_CODE);
        }

        return Dao.qryByCode("TF_A_BANKPACKET", "SEL_BY_USER_BANKPACKET", inparams, pagination, route);
    }

    /**
     * 用户优惠查询 获取用户的优惠信息
     * 
     * @param inparam
     *            查询所需参数
     * @return IDataset 用户优惠信息列表
     * @throws Exception
     */
    public static IDataset queryUserDiscnt(IData inparam, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_USERID", inparam, pagination);
    }

    /**
     * 用户优惠查询 获取用户的优惠信息包括相应的产品和包信息
     * 
     * @param inparam
     *            查询所需参数
     * @return IDataset 用户优惠信息列表
     * @throws Exception
     */
    public static IDataset queryUserDiscntPid(IData inparam, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_USERID_PID", inparam, pagination);
    }

    /**
     * 用户优惠查询 获取用户的优惠信息
     * 
     * @param inparam
     *            查询所需参数
     * @return IDataset 用户优惠信息列表
     * @throws Exception
     */
    public static IDataset queryUserDiscntPpd(IData inparam, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_USERID_PPD", inparam, pagination);
    }

    /**
     * 用户优惠查询 获取用户的优惠信息
     * 
     * @param inparam
     *            查询所需参数
     * @return IDataset 用户优惠信息列表
     * @throws Exception
     */
    public static IDataset queryUserDiscntPuid(IData inparam, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_USERID_UPPD", inparam, pagination);
    }

    /**
     * 用户优惠查询 获取用户的优惠信息
     * 
     * @param inparam
     *            查询所需参数
     * @return IDataset 用户优惠信息列表
     * @throws Exception
     */
    public static IDataset queryUserDiscntPuud(IData inparam, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_USERID_UPID", inparam, pagination);
    }

    /**
     * 查询用户IMSI信息
     * 
     * @param inparam
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryUserImsiInfo(IData inparam, Pagination pagination) throws Exception
    {

        String qryMode = inparam.getString("QUERY_MODE", "");
        if ("0".equals(qryMode))
        {
            return Dao.qryByCode("TF_F_USER_RES", "SEL_GPRS_USR_IMSI", inparam, pagination);
        }
        if ("1".equals(qryMode))
        {
            return Dao.qryByCode("TF_F_USER_RES", "SEL_VPMN_USR_IMSI", inparam, pagination);
        }
        if ("2".equals(qryMode))
        {
            return Dao.qryByCode("TF_F_USER_RES", "SEL_GJCT_USR_IMSI", inparam, pagination);
        }
        if ("3".equals(qryMode))
        {
            return Dao.qryByCode("TF_F_USER_RES", "SEL_GJMY_USR_IMSI", inparam, pagination);
        }
        if ("4".equals(qryMode))
        {
            return Dao.qryByCode("TF_F_USER_RES", "SEL_SQTJ_USR_IMSI", inparam, pagination);
        }
        if ("5".equals(qryMode))
        {
            return Dao.qryByCode("TF_F_USER_RES", "SEL_SXTJ_USR_IMSI", inparam, pagination);
        }
        return null;
    }

    /**
     * 执行欠费停拆机用户查询sql
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryUserOweStop(IData inparams, Pagination pagination) throws Exception
    {

        // 欠费用户
        // inparams.put("REMOVE_TAG", "4");
        // 用户服务状态
        // inparams.put("TRADE_TYPE_CODE", "100");

        String psptId = inparams.getString("PSPT_ID", "");
        String custName = inparams.getString("CUST_NAME", "");
        String sqlRef = "SEL_OWEFEE_STOP_DESTROY_PSPT_ID";
        if (psptId.length() > 0)
        {
            sqlRef = "SEL_OWEFEE_STOP_DESTROY_PSPT_ID";
        }
        if (custName.length() > 0)
        {
            sqlRef = "SEL_OWEFEE_STOP_DESTROY_CUST_NAME";
        }
        return Dao.qryByCode("TF_F_USER", sqlRef, inparams, pagination);
    }

    /**
     * 执行sql查询用户预约优惠信息
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryUserReserveDiscnt(IData inparams, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_TRD_ID_RSRV_QRY", inparams, pagination);
    }

    /**
     * 查询用户预约产品信息
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryUserReserveProduct(IData inparams, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_B_TRADE_PRODUCT", "SEL_BY_PK_RSRV_QRY", inparams, pagination);
    }

    /**
     * 执行sql查询用户预约服务信息
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryUserReserveService(IData inparams, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_B_TRADE_SVC", "SEL_BY_TRADE_RSRV_QRY", inparams, pagination);
    }

    /**
     * 查询用户预约业务信息
     * 
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryUserReserveTrade(IData inparams, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_B_TRADE", "SEL_BOOK_BY_USERID_RSRV_QRY", inparams, pagination);
    }

    public void sendSms(IData data, String smstype) throws Exception
    {

        IData param = new DataMap();
        String brand_name = new String();
        String flag = "0";
        param.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));
        param.put("IN_MODE_CODE", "1");
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        param.put("USER_ID", data.getString("USER_ID"));

        IData inParam1 = new DataMap();
        inParam1.put("SUBSYS_CODE", "CSM");
        inParam1.put("PARAM_ATTR", "998");
        inParam1.put("EPARCHY_CODE", "ZZZZ");
        inParam1.put("PARAM_CODE", data.getString("BRAND_CODE"));
        IDataset dataset2 = Dao.qryByCode("TD_S_COMMPARA", "SEL1_PK_TD_S_COMMPARA", inParam1);
        if (dataset2 != null && dataset2.size() > 0)
        {
            String brand_code = dataset2.getData(0).getString("PARA_CODE1");
            if ("0".equals(brand_code))
            {
                brand_name = "全球通";
            }
            else if ("2".equals(brand_code))
            {
                brand_name = "动感地带";
            }
            else
            {
                brand_name = "神州行";
            }
        }
        else
        {
            CSAppException.apperr(BrandException.CRM_BRAND_40);
        }
        String notice_content = "尊敬的" + brand_name + "客户，您好！您办理的";
        if ("11".equals(smstype))
        {
            IData inParam = new DataMap();
            inParam.put("USER_ID", data.getString("USER_ID"));
            IDataset dataset = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_NOW", inParam);
            if (dataset != null && dataset.size() > 0)
            {
                for (int i = 0, iCount = dataset.size(); i < iCount; i++)
                {
                    IData attrParam = new DataMap();
                    IData svcData = dataset.getData(i);

                    attrParam.put("SUBSYS_CODE", "CSM");
                    attrParam.put("PARAM_ATTR", "819");
                    attrParam.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));
                    attrParam.put("PARAM_CODE", svcData.getString("DISCNT_CODE"));
                    IDataset dataset3 = Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_ATTRPARAM_CODE", attrParam);
                    if (dataset3 != null && dataset3.size() > 0 && "11".equals(dataset3.getData(0).getString("PARA_CODE1")))
                    {
                        flag = "1";
                        if (dataset3.getData(0).getString("PARA_CODE23") != null)
                            notice_content = notice_content + dataset3.getData(0).getString("PARAM_NAME") + ",品牌资费为" + dataset3.getData(0).getString("PARA_CODE23");
                        if (dataset3.getData(0).getString("PARA_CODE24") != null)
                            notice_content = notice_content + dataset3.getData(0).getString("PARA_CODE24");
                        notice_content = notice_content + "，";
                    }
                }
                if (!"1".equals(flag))
                    CSAppException.apperr(CrmUserException.CRM_USER_709);
                notice_content = notice_content + "中国移动";
            }
            else
            {
                CSAppException.apperr(CrmUserException.CRM_USER_709);
            }
        }
        else if ("12".equals(smstype))
        {
            notice_content = notice_content + "优惠业务名称是:";
            IData attrParam = new DataMap();
            attrParam.put("SUBSYS_CODE", "CSM");
            attrParam.put("PARAM_ATTR", "819");
            attrParam.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));
            attrParam.put("PARAM_CODE", data.getString("SELECT_DISCNT"));
            IDataset dataset3 = Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_ATTRPARAM_CODE", attrParam);
            if (dataset3 != null && dataset3.size() > 0 && "12".equals(dataset3.getData(0).getString("PARA_CODE1")))
            {
                if (dataset3.getData(0).getString("PARA_CODE23") != null)
                    notice_content = notice_content + dataset3.getData(0).getString("PARAM_NAME") + ",品牌资费为" + dataset3.getData(0).getString("PARA_CODE23");
                if (dataset3.getData(0).getString("PARA_CODE24") != null)
                    notice_content = notice_content + dataset3.getData(0).getString("PARA_CODE24");
                notice_content = notice_content + "，";
            }
            notice_content = notice_content + "中国移动";
        }
        else if ("13".equals(smstype))
        {
            notice_content = notice_content + "优惠活动名称是:";
            IData attrParam = new DataMap();
            attrParam.put("SUBSYS_CODE", "CSM");
            attrParam.put("PARAM_ATTR", "819");
            attrParam.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));
            attrParam.put("PARAM_CODE", data.getString("SALE_DISCNT"));
            IDataset dataset3 = Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_ATTRPARAM_CODE", attrParam);
            if (dataset3 != null && dataset3.size() > 0 && "13".equals(dataset3.getData(0).getString("PARA_CODE1")))
            {
                if (dataset3.getData(0).getString("PARA_CODE23") != null)
                    notice_content = notice_content + dataset3.getData(0).getString("PARAM_NAME") + ",品牌资费为" + dataset3.getData(0).getString("PARA_CODE23");
                if (dataset3.getData(0).getString("PARA_CODE24") != null)
                    notice_content = notice_content + dataset3.getData(0).getString("PARA_CODE24");
                notice_content = notice_content + "，";
            }
            notice_content = notice_content + "中国移动";
        }
        param.put("NOTICE_CONTENT", notice_content);
        param.put("PRIORITY", "50");
        param.put("STAFF_ID", getVisit().getStaffId());
        param.put("DEPART_ID", getVisit().getDepartId());
        param.put("REMARK", "客服发送在用促销方案套餐资费沟通信息");
        String seq = SeqMgr.getSmsSendId();
        long seq_id = Long.parseLong(seq);
        long partition_id = seq_id % 1000;

        StringBuilder sql = new StringBuilder("INSERT INTO ti_o_sms(sms_notice_id,partition_id,send_count_code,refered_count,eparchy_code,in_mode_code,");
        sql.append("chan_id,recv_object_type,recv_object,recv_id,sms_type_code,sms_kind_code,");
        sql.append(" notice_content_type,notice_content,force_refer_count,sms_priority,refer_time,");
        sql.append(" refer_staff_id,refer_depart_id,deal_time,deal_state,remark,send_time_code,send_object_code)");
        sql.append(" values (").append(seq_id).append(",").append(partition_id);
        sql.append(" ,'1','0',:EPARCHY_CODE,:IN_MODE_CODE,'11','00',:SERIAL_NUMBER,:USER_ID,");
        sql.append("  '20','02','0',:NOTICE_CONTENT,1,:PRIORITY,sysdate,:STAFF_ID,:DEPART_ID,sysdate,'15',:REMARK,1,6)");
        Dao.executeUpdate(sql, param);
    }

}


package com.asiainfo.veris.crm.order.soa.person.busi.view360;

import com.ailk.biz.bean.BizBean;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.View360Const;
import com.asiainfo.veris.crm.iorder.pub.welfare.exception.WelfareException;
import com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.component.welcome.WelcomeBean;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.mvelmisc.MvelMiscQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.Qry360InfoDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.Qry360THInfoDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.QryUserInfoDao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;

public class Qry360InfoBean extends CSBizBean
{
	Logger log = Logger.getLogger(Qry360InfoBean.class);

    /**
     * 根据ACCT_ID查询账户有效账期信息（当前以及预约的） 用户账期有变更，陕西下账期生效 最新一条记录为下账期结账日，否则下账期结账日同当前账期结账日
     * 
     * @param acctId
     * @return IDataset 账户有效账期信息
     */
    public static IDataset qryAcctAcctDayInfo(String acctId) throws Exception
    {
        IData param = new DataMap();
        if (StringUtils.isNotBlank(acctId))
        {
            param.clear();
            param.put("ACCT_ID", acctId.trim());
        }
        else
        {
            return new DatasetList();
        }
        IDataset returnResult = AcctInfoQry.qryAcctAcctDayInfo(param);
        IData acctData = new DataMap();
        if (IDataUtil.isNotEmpty(returnResult))
        {
            acctData.clear();
            if (returnResult.size() > 1)
            {
                String acctDay = returnResult.getData(0).getString("ACCT_DAY");
                String acctDay1 = returnResult.getData(1).getString("ACCT_DAY");
                acctData.put("NEXT_ACCT_DAY", acctDay);
                acctData.put("CURRENT_ACCT_DAY", acctDay1);
            }
            else
            {
                String acctDay = returnResult.getData(0).getString("ACCT_DAY");
                acctData.put("NEXT_ACCT_DAY", acctDay);
                acctData.put("CURRENT_ACCT_DAY", acctDay);
            }
        }
        else
        {
            return new DatasetList();
        }
        return IDataUtil.idToIds(acctData);
    }

    /**
     * 根据user_id 查询产品信息
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryProductInfo(IData data, Pagination pagination) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        return dao.qryProductInfo(data, pagination);
    }

    /**
     * 根据user_id 查询所有产品信息
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset qryProductInfoAll(IData data, Pagination pagination) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        return dao.qryProductInfoAll(data, pagination);
    }

    /**
     * 改变sim卡编码
     * 
     * @param pd
     * @param resinfos
     * @return
     * @throws Exception
     */
    public IDataset changeSIMCode(IData data, IDataset resinfos, Pagination pagination) throws Exception
    {
        for (int i = 0, size = resinfos.size(); i < size; i++)
        {
            IData info = resinfos.getData(i);
            if (info.getString("RES_TYPE_CODE").equals("1"))
            {
                info.put("RES_CODE", Qry360InfoDAO.getSimbyPurview(data, info.getString("RES_CODE", ""), pagination));
                resinfos.remove(i);
                resinfos.add(info);
            }
        }
        return resinfos;
    }

    /**
     * 根据user_id 查询支付平台业务信息
     * 
     * @throws Exception
     */
    public IDataset getBankbandInfos(IData data, Pagination pagination) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        return dao.getBankbandInfos(data, pagination);
    }

    public IDataset getCheckUserInfo(IData param, Pagination pagination) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER", "");
        String removeTag = param.getString("REMOVE_TAG", "");
        return UserInfoQry.getCheckUserInfo(serialNumber, removeTag, pagination);
    }

    public IDataset getCreditInfo(IData params) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        String userId = params.getString("USER_ID", "");
        String idType = params.getString("IDTYPE", "0");
        IDataset resutls =CreditCall.getCreditInfo(userId, idType);
        return resutls;
    }

    /**
     * 根据user_id 查询个人代扣账户信息
     * 
     * @throws Exception
     */
    public IDataset getDeductInfos(IData data, Pagination pagination) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        return dao.getDeductInfos(data, pagination);
    }

    /**
     * 根据user_id 查询保证金信息
     * 
     * @throws Exception
     */
    public IDataset getForegiftInfo(IData data, Pagination pagination) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        return dao.getForegiftInfo(data, pagination);
    }

    /**
     * 显示邮寄内容 used
     * 
     * @param content
     * @return
     * @throws Exception
     */
    public String getPostContent(String content) throws Exception
    {
        String str = "";
        StringBuilder buffer = new StringBuilder();
        if (content == null || "".equals(content))
            return str;
        for (int i = 0, len = content.length(); i < len; i++)
        {
            str = StaticUtil.getStaticValue("POSTINFO_POSTCONTENT", String.valueOf(content.charAt(i)));
            if (str != null && !"".equals(str))
            {
                buffer.append(str + " , ");
            }
        }
        String result = buffer.toString();
        if (result.length() > 0)
        {
            result = result.substring(0, result.lastIndexOf(","));
        }
        return result;
    }

    /**
     * 显示邮寄方式 used
     * 
     * @param content
     * @return
     * @throws Exception
     */
    public String getPostType(String posttype) throws Exception
    {
        String str = "";
        StringBuilder buffer = new StringBuilder();
        if (posttype == null || "".equals(posttype))
            return str;
        for (int i = 0, len = posttype.length(); i < len; i++)
        {
            str = StaticUtil.getStaticValue("POSTINFO_POSTTYPESET", String.valueOf(posttype.charAt(i)));
            if (str != null && !"".equals(str))
            {
                buffer.append(str + " , ");
            }
        }
        String result = buffer.toString();
        if (result.length() > 0)
        {
            result = result.substring(0, result.lastIndexOf(","));
        }
        return result;
    }

    public IDataset initCustComplaint(IData param) throws Exception
    {
        IDataset returnData = new DatasetList();
        String startDate = SysDateMgr.firstDayOfDate(SysDateMgr.getSysTime(), 0);
        param.put("START_DATE", startDate);
        param.put("END_DATE", SysDateMgr.getSysTime());
        returnData.add(param);
        return returnData;
    }

    public IDataset initCustContactMgr(IData param) throws Exception
    {
        IDataset returnData = new DatasetList();
        String startDate = SysDateMgr.firstDayOfDate(SysDateMgr.getSysTime(), 0);
        param.put("END_DATE", SysDateMgr.getSysDate());
        param.put("START_DATE", startDate);
        returnData.add(param);
        return returnData;
    }

    public IDataset initTradeHistoryInfoHis(IData param) throws Exception
    {
        IDataset returnData = new DatasetList();
        String startDate = SysDateMgr.firstDayOfDate(SysDateMgr.getSysTime(), -12);
        param.put("END_DATE", SysDateMgr.getLastDayOfMonth(-10));
        param.put("START_DATE", startDate);
        returnData.add(param);
        return returnData;
    }

    public IDataset initTradeHistoryInfo(IData param) throws Exception
    {
        IDataset returnData = new DatasetList();
        String startDate = SysDateMgr.firstDayOfDate(SysDateMgr.getSysTime(), -2);
        param.put("END_DATE", SysDateMgr.getSysDate());
        param.put("START_DATE", startDate);
        returnData.add(param);
        return returnData;
    }

    private void paramsTrans(IDataset param) throws Exception
    {
        for (int i = 0, size = param.size(); i < size; i++)
        {
            param.getData(i).put("OPER_FEE", FeeUtils.Fen2Yuan(param.getData(i).getString("OPER_FEE")));
            param.getData(i).put("FOREGIFT", FeeUtils.Fen2Yuan(param.getData(i).getString("FOREGIFT")));
            param.getData(i).put("ADVANCE_PAY", FeeUtils.Fen2Yuan(param.getData(i).getString("ADVANCE_PAY")));
            if (queryProductAndPackage(param.getData(i).getString("PRODUCT_ID"), param.getData(i).getString("PACKAGE_ID")))
            {
                param.getData(i).put("ACTIVE_TYPE", "约定在网类");
            }
            else
            {
                param.getData(i).put("ACTIVE_TYPE", "返还/约定消费类");
            }
        }
    }

    /**
     * 根据user_id 查询接触信息
     * 
     * @throws Exception
     */
    public IDataset qryContactMgr(IData data, Pagination pagination) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        return dao.qryContactMgr(data, pagination);
    }

    /**
     * 查询投诉信息
     * 
     * @throws Exception
     */
    public IDataset qryCustComplaintInfo(IData data, Pagination pagination) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        IDataset dataset = CTSCall.queryFinishedWorkform(data.getString("SERIAL_NUMBER"), data.getString("START_DATE"), data.getString("END_DATE"));
        IData result = dataset.getData(0);
        if (result.getString("X_RECORDNUM").equals("0"))
        {
            return new DatasetList();
        }
        return dataset;
    }

    /**
     * 根据sn 查询接触信息
     * 
     * @throws Exception
     */
    public IDataset qryNewContactMgr(IData data, Pagination pagination) throws Exception
    {
        return Qry360InfoDAO.qryNewContactMgr(data, pagination);
    }

    /**
     * 查询新接触详细信息
     * 
     * @throws Exception
     */
    public IDataset qryNewContactTrac(IData data, Pagination pagination) throws Exception
    {
        return Qry360InfoDAO.qryNewContactTrac(data.getString("SERIAL_NUMBER", ""), data.getString("TOUCH_DAY", ""), data.getString("TOUCH_ID", ""), pagination);
    }

    public IDataset qryNpUserInfo(IData param) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        IDataset results = dao.qryNpUserInfo(param);
        return results;
    }

    /**
     * 根据user_id 查询邮寄信息
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryPostInfo(IData data, Pagination pagination) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();

        IDataset dataset = dao.qryPostInfo(data, pagination);

        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            dataset.getData(i).put("POST_CONTENT", getPostContent(dataset.getData(i).getString("POST_CONTENT")));
            dataset.getData(i).put("POST_TYPESET", getPostType(dataset.getData(i).getString("POST_TYPESET")));
        }
        return dataset;
    }

    /**
     * 根据user_id 查询用户关系信息
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryRelationInfo(IData data, Pagination pagination) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        IDataset result = dao.qryRelationInfo(data, pagination);
        if(IDataUtil.isNotEmpty(result))
        {
        	for(int i = 0; i < result.size(); i++)
        	{
        		IData relation = result.getData(i);
        		String userIdA = relation.getString("USER_ID_A","-1");
				if("1104030901883363".equals(userIdA)){   //移动员工
					String userIdB = relation.getString("USER_ID_B","-1");
					IDataset results = UserOtherInfoQry.getUserOther(userIdB, "30");
					if(IDataUtil.isNotEmpty(results)){
						relation.put("RSRV_VALUE", results.getData(0).getString("RSRV_VALUE", ""));
					}
				}
				IData in = new DataMap();
				in.put("USER_ID", relation.getString("USER_ID_A","-1"));
				IDataset custManaId = dao.getCustManagerId(in);
				if(IDataUtil.isEmpty(custManaId))
				{
					IDataset data2 = dao.getCustName(in);;
					if(IDataUtil.isNotEmpty(data2))
					{
						relation.put("CUST_NAME", data2.getData(0).getString("CUST_NAME",""));
						relation.put("CUST_MANAGER_ID","");
					}
				}
				else
				{
					relation.put("CUST_NAME", custManaId.getData(0).getString("CUST_NAME",""));
					relation.put("CUST_MANAGER_ID", custManaId.getData(0).getString("CUST_MANAGER_ID",""));
				}
        	}
        }
        return result;
    }

    /**
     * 根据user_id 查询资源信息
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryResourceInfo(IData data, Pagination pagination) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        IDataset resinfos = dao.qryResourceInfo(data, pagination);
        // this.changeSIMCode(data, resinfos, pagination);
        return resinfos;
    }

    /**
     * 根据user_id 查询资源全部信息
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryResourceInfoAll(IData data, Pagination pagination) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        IDataset resinfos = dao.qryResourceInfoAll(data, pagination);
        // this.changeSIMCode(data, resinfos, pagination);
        return resinfos;
    }

    /**
     * 根据user_id 查询积分信息
     * 
     * @throws Exception
     */
    public IDataset qryScoreInfo(IData data, Pagination pagination) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        return dao.qryScoreInfo(data, pagination);
    }

    public IDataset qryTHCustomerContactInfo(IData params, Pagination pagination) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        return dao.qryTHCustomerContactInfo(params, pagination);
    }

    public IDataset qryTHRelaTradeInfo(IData params, Pagination pagination) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        return dao.qryTHRelaTradeInfo(params, pagination);
    }

    public IDataset queryTradeHistoryInfo(IData data, Pagination pagination) throws Exception
    {
        String history_query_type = data.getString("HISTORY_QUERY_TYPE", "");
        if ("G".equals(history_query_type))
        {
            IDataset tradeNoQueryList = Qry360THInfoDAO.getTradeCodeNoQuery(data);
            String tradeNOQueryStr = "";
            for (int i = 0; i < tradeNoQueryList.size(); i++)
            {
                if (!"".equals(tradeNOQueryStr))
                {
                    tradeNOQueryStr += ",";
                }
                tradeNOQueryStr += tradeNoQueryList.getData(i).getString("DATA_ID", "");
            }
            data.put("USER360VIEW_TRADENOQUERY", tradeNOQueryStr);
            return Qry360THInfoDAO.queryHTradeScoreInfo(data, pagination);// cg
        }
        else
        {
            return new DatasetList();
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public IDataset queryWelfareHistoryInfo(IData data) throws Exception
    {
    	
    	IDataset datasetList = new DatasetList();
    	IData dataOrder = abilityMethod(data,"crm.WELFARE.LIST");
    	//////////////////////////////////////////////////////////////
    	String respCode = dataOrder.getString("respCode");
	    String X_RSPCODE = "";
	    String X_RSPDESC = "";
	    if("0".equals(respCode)){
	    	//封装查询数据
	    	IData result = dataOrder.getData("result");
	    	IData dataQ = result.getData("data");
	    	IDataset results = dataQ.getDataset("results");
	    	if(IDataUtil.isNotEmpty(results)){
	    		for(int i = 0;i < results.size();i++){
	    			IData orderList = new DataMap();
	    			IData orders = results.getData(i);
	    			orderList.put("ACCEPT_DATE", orders.getString("createTime",""));
	    			orderList.put("STAFF_ID", orders.getString("staffId",""));
	    			orderList.put("ORDER_ID", orders.getString("id",""));
	    			datasetList.add(orderList);
		    	}
	    	}
	    	
	    }else{	
		    IData out = dataOrder.getData("result");
		    X_RSPCODE = out.getString("respCode");
		    X_RSPDESC = out.getString("respDesc");
		    CSAppException.apperr(CrmUserException.CRM_USER_783, "订单列表查询失败："+X_RSPCODE+X_RSPDESC);
	    }
    	////////////////////////////////////////////////////////////////
    	return datasetList;
    	
    }
    public IDataset queryWelfareHistoryDetailInfo(IData data) throws Exception
    {
        
    	IData orderDetailData = abilityMethod(data,"crm.WELFARE.LIST.DETAIL");
    	String respCode = orderDetailData.getString("respCode");
	    String X_RSPCODE = "";
	    String X_RSPDESC = "";
	    IDataset rtnData = new DatasetList();
	    if("0".equals(respCode))
	    {
	    	IData allData = new DataMap();
	    	//封装查询详情数据
	    	IData result = orderDetailData.getData("result");
	    	IData dataQ = result.getData("data");
	    	//封装主列表信息
	    	IData mainTrade = new DataMap();
	    	mainTrade.put("ID", dataQ.getString("id"));
	    	mainTrade.put("ORDER_STATUS", dataQ.getString("orderStatus"));
	    	mainTrade.put("CHAN_ID", dataQ.getString("chanId"));
	    	mainTrade.put("ORDER_SUM", dataQ.getString("orderSum"));
	    	mainTrade.put("ORDER_PREFERENTIAL_SUM", dataQ.getString("orderPreferentialSum"));
	    	mainTrade.put("ORDER_PAY_SUM", dataQ.getString("orderPaySum"));
	    	mainTrade.put("AREA_CODE", dataQ.getString("areaCode"));
	    	mainTrade.put("AREA_NAME", dataQ.getString("areaName"));
	    	allData.put("MAIN_TRADE", mainTrade);
	    	//封装子列表
	    	IDataset orderSubList = dataQ.getDataset("orderSubList");
	    	IDataset datasetList = new DatasetList();
	    	if(IDataUtil.isNotEmpty(orderSubList)){
	    		for(int i = 0;i < orderSubList.size();i++){
	    			IData orderList = new DataMap();
	    			IData orders = orderSubList.getData(i);
	    			orderList.put("RIGHT_NAME", orders.getString("rightName",""));
	    			orderList.put("RIGHT_CODE", orders.getString("rightCode",""));
	    			orderList.put("ORDER_SUB_TIME", orders.getString("orderSubTime",""));
	    			datasetList.add(orderList);
		    	}
	    		
	    		allData.put("SUB_TRADE", datasetList);
	    		rtnData.add(allData);
	    	}
	    	
	    }else{	
		    IData out = orderDetailData.getData("result");
		    X_RSPCODE = out.getString("respCode");
		    X_RSPDESC = out.getString("respDesc");
		    CSAppException.apperr(CrmUserException.CRM_USER_783, "订单详情查询失败："+X_RSPCODE+X_RSPDESC);
	    }
    	
    	return rtnData;
    }
    //能开接口调用
    public IData abilityMethod(IData abilityData,String param) throws Exception{
    	
    	IData retData = new DataMap();
		try {
			//调用能开接口url
			retData = BusinessAbilityCall.callBusinessCenterCommon(param, abilityData);
			log.debug("=============能开返回参数 "+retData);
		} catch (Exception e) {
			//调用权益接口失败
			log.debug("============接口错误信息 "+e.getMessage());
			CSAppException.apperr(WelfareException.CRM_WELFARE_10,"调用权益通知接口失败"+e.getMessage());
		}
		
		return retData;
		
    }
    

    /**
     * 根据user_id 查询业务历史信息
     * 
     * @throws Exception
     */
    public IDataset qryTradeHistoryInfo(IData data, Pagination pagination) throws Exception
    {
        String book_check = data.getString("BOOK_CHECK", "");
        if (book_check != null && "on".equals(book_check))
        {
            String history_query_type = data.getString("HISTORY_QUERY_TYPE", "");
            if ("G".equals(history_query_type))
            {
                return Qry360InfoDAO.queryTradeInfoCg(data, pagination);// cg
            }
            return Qry360InfoDAO.queryTradeInfo(data, pagination);
        }
        else
        {
            String history_query_type = data.getString("HISTORY_QUERY_TYPE", "");
            if ("G".equals(history_query_type))
            {
                return Qry360InfoDAO.queryTradeHistoryInfoCg(data, pagination);// cg
            }
            return Qry360InfoDAO.queryTradeHistoryInfoNew(data, pagination);
        }
    }

    /**
     * 查询业务历史信息--页面业务类型展示
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryTradeTypeCode(IData data) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        data.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());// 设置路由
        return dao.queryTradeTypeCode(data);
    }

    /**
     * 查询业务历史信息--页面业务类型展示
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryTradeTypeCodeCg(IData data) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        data.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());// 设置路由
        return dao.queryTradeTypeCodeCg(data);
    }

    public IDataset qryUserSaleActiveInfo(IData param) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        IDataset results = dao.qryUserSaleActiveInfo(param);
        if (IDataUtil.isNotEmpty(results))
        {
        	for(int i = 0 ; i < results.size() ; i++){
        		IData result = results.getData(i);
        		OfferCfg cfg = OfferCfg.getInstance(result.getString("PACKAGE_ID",""), BofConst.ELEMENT_TYPE_CODE_PACKAGE);
        		if(cfg != null){
            		result.put("PACKAGE_DESC", cfg.getDescription());
            		result.put("REMARK", cfg.getDescription());
        		}
        	}
            paramsTrans(results);
        }
        return results;
    }

    public IDataset qryUserSaleActiveInfoAll(IData param) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        IDataset result1 = dao.qrySaleActiveInfo(param);
        if(IDataUtil.isNotEmpty(result1)){
        	for(int i = 0 ; i < result1.size() ; i++){
        		IData data = result1.getData(i);
        		OfferCfg cfg = OfferCfg.getInstance(data.getString("PACKAGE_ID",""), BofConst.ELEMENT_TYPE_CODE_PACKAGE);
        		if(cfg != null){
        			data.put("PACKAGE_DESC", cfg.getDescription());
        		}
        	}
        }
        IDataset result2 = dao.qrySaleActiveInfoHis(param);
        if(IDataUtil.isNotEmpty(result2)){
        	for(int i = 0 ; i < result2.size() ; i++){
        		IData data = result2.getData(i);
        		OfferCfg cfg = OfferCfg.getInstance(data.getString("PACKAGE_ID",""), BofConst.ELEMENT_TYPE_CODE_PACKAGE);
        		if(cfg != null){
        			data.put("PACKAGE_DESC", cfg.getDescription());
        		}
        	}
        }
        if (IDataUtil.isEmpty(result1))
		{
        	paramsTrans(result2);
			return result2;
		}
		else if (IDataUtil.isNotEmpty(result2))
		{
			result1.addAll(result2);
			//paramsTrans(result1);
		}
        paramsTrans(result1);
        return result1;
    }

    /**
     * 根据user_id 查询服务信息
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryUserServiceInfo(IData data, Pagination pagination) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        return dao.qryUserServiceInfo(data, pagination);
    }

    /**
     * 根据user_id 查询服务信息
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryUserServiceInfoAll(IData data, Pagination pagination) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        return dao.qryUserServiceInfoAll(data, pagination);
    }
    
    /**
     * 根据user_id 查询服务信息
     * liquan
     * @param data
     * @param 
     * @return
     * @throws Exception
     */
    public IDataset qryUserServiceInfo(IData data) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        return dao.qryUserServiceInfo(data);
    }    

    public IDataset queryMemberAll(IData params) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        return dao.queryMemberAll(params);
    }

    /** 根据产品、包判断是否是无返还类活动 */
    private boolean queryProductAndPackage(String productId, String packageId) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        IData params = new DataMap();
        params.clear();
        params.put("EPARCHY_CODE", "0898");
        params.put("PRODUCT_ID", productId);
        params.put("PACKAGE_ID", packageId);
        params.put("PARAM_ATTR", "155");
        IDataset limits = dao.queryProductAndPackage(params);
        if (IDataUtil.isNotEmpty(limits))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public IDataset queryShareDiscnt(IData params) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        return dao.queryShareDiscnt(params);
    }

    public IDataset queryThServerInfos(IData params, Pagination pagination) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        return dao.queryThServerInfos(params, pagination);
    }

    public IDataset queryWideUserInfo(IData params) throws Exception
    {
        IData widenetInfo = new DataMap();
        Qry360InfoDAO dao = new Qry360InfoDAO();
        IData userInfo = new DataMap();
        String msisdn = params.getString("SERIAL_NUMBER");
        if ("KD_".equals(msisdn.substring(0, 3)))
        {
            userInfo = UcaInfoQry.qryUserInfoBySn(msisdn);
        }
        else
        {
            userInfo = UcaInfoQry.qryUserInfoBySn("KD_" + msisdn);
        }
        // IDataset results = dao.queryWideUserInfo(params);
        if (IDataUtil.isEmpty(userInfo))
        {
            return new DatasetList();
        }
        IDataset results = IDataUtil.idToIds(userInfo);
        IData returnData = new DataMap();
        returnData.putAll(results.getData(0));
        IData param = results.getData(0);
        IDataset productInfo = dao.queryWideNetUserProductInfo(param);
        if (IDataUtil.isNotEmpty(productInfo))
        {
            returnData.put("PRODUCT_SET", productInfo.getData(0));
        }
        IDataset discntDataset = dao.queryWideNetUserDiscnt(param);
        if (IDataUtil.isNotEmpty(discntDataset))
        {
            returnData.put("DISCNT_SET", discntDataset);
        }
        widenetInfo.put("USER_INFO", returnData);

        if (IDataUtil.isEmpty(returnData))
        {
            return new DatasetList();
        }
        String kdUserId = param.getString("USER_ID", "");
        IDataset wideCustInfos = CustomerInfoQry.getCustomerInfoByUserId(kdUserId);
        IData wideCustInfo = new DataMap();
        if (IDataUtil.isNotEmpty(wideCustInfos))
        {
            wideCustInfo = wideCustInfos.getData(0);
        }
        widenetInfo.put("CUST_INFO", wideCustInfo);
        params.put("USER_ID", kdUserId);
        IDataset wideBaseInfos = dao.getUserWidenetInfo(params);
        //获取光猫信息
        IDataset modelInfos = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_OTHER_FTTHMODERM", params);
        if(!IDataUtil.isEmpty(modelInfos)){
        	String resId = modelInfos.getData(0).getString("RSRV_STR1",""); //光猫串号
        	wideBaseInfos.getData(0).put("RES_ID", resId);
        }
        
        if (IDataUtil.isEmpty(wideBaseInfos))
        {
            widenetInfo.put("BASE_INFO", "");
        }
        else
        {
            widenetInfo.put("BASE_INFO", wideBaseInfos.getData(0));
        }
        return IDataUtil.idToIds(widenetInfo);
    }

    /**
     * 发送客户经理号码
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData sendCustManagerNum(IData param) throws Exception
    {
        IData result = new DataMap();
        if (param.isEmpty() || param == null)
        {
            result.put("SEND_TAG", "1");
            return result;
        }
        else
        {
            IData custmanminfo = UStaffInfoQry.qryCustManagerInfoByCustManagerId(param.getString("CUST_MANAGER_ID"));
            if (IDataUtil.isEmpty(custmanminfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_783, "该用户的客户经理信息没有在BOSS系统中维护，请联系相关部门添加。");
            }

            String custmanm_sn = custmanminfo.getString("SERIAL_NUMBER", "");
            String serialnumber = param.getString("SERIAL_NUMBER", "");
            String content = "尊敬的客户您好，您的客户经理是" + custmanminfo.getString("CUST_MANAGER_NAME", "") + "，联系电话:" + custmanm_sn;

            param.put("NOTICE_CONTENT", content);
            param.put("RECV_OBJECT", serialnumber);
            SmsSend.insSms(param);
            result.put("SEND_TAG", "0");
            return result;
        }
    }

    public IDataset qryUserResSimInfo(IData params) throws Exception
    {
        Qry360InfoDAO dao = new Qry360InfoDAO();
        IDataset result = new DatasetList();
        IDataset userRes = dao.qryUserResInfo(params);
        IData info = new DataMap();
        String simCardNum = "";
        if (IDataUtil.isNotEmpty(userRes))
        {
            for (int i = 0; i < userRes.size(); i++)
            {
                if ("1".equals(userRes.getData(i).getString("RES_TYPE_CODE", "")))
                {
                    simCardNum = userRes.getData(i).getString("RES_CODE", "");
                    info = userRes.getData(i);
                    break;
                }
            }
        }
        if (StringUtils.isNotBlank(simCardNum))
        {
            IDataset userResInfo = ResCall.getSimCardInfo("0", simCardNum, null, null);
            if (IDataUtil.isNotEmpty(userResInfo))
            {
                info.put("RES_KIND_NAME", userResInfo.getData(0).getString("RES_KIND_NAME", ""));
            }
            result.add(info);
        }
        return result;
    }

    public IDataset initTradeHistoryInfoHisYear(IData params) throws Exception
    {
        IDataset returnYear = new DatasetList();
        int nowYear = Integer.valueOf(SysDateMgr.getNowYear());
        //配置里面的年份
        int  beginYear=Integer.valueOf(StaticUtil.getStaticValue("TRADEINFOHIS_YEAR", "2010"));
        for(int i=(nowYear-1);i>=beginYear;i--){
            IData data = new DataMap();
            data.clear();
            data.put("KEY", i);
            data.put("VALUE", i);
            returnYear.add(data);
        }
        return returnYear;
    }
    
    /**
     * 客服新系统 统一视图下已开业务查询页面，所有已开业务信息从账务查询  2016/5/7
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryOpenInfo(IData data) throws Exception
    {
    	
    	IData result = new DataMap();
    	IData param = new DataMap();
        IDataUtil.chkParam(data, "SERIAL_NUMBER");
        String serialNumber = data.getString("SERIAL_NUMBER");
        result = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
            return null;
        }
        String userId = result.getString("USER_ID");
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("USER_ID", userId);
    	param.put("USER_ID", result.getString("USER_ID"));
    	
    	IDataOutput output = CSAppCall.callAcct("AM_CRM_QryBusinessForCustServ", param, true);
        return output.getData();
        
    }
    
    
    /**
     * 客服新系统 统一视图下已开业务查询页面，营销活动信息从账务获取实时的当月是否返还数据    2016/5/7
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset querySaleInfo(IData param) throws Exception
    {
    	IDataOutput output = CSAppCall.callAcct("AM_CRM_QryDiscntDepositForCustServ", param, true);
        return output.getData();
        
    }
    
    /**
     * REQ201607140023 关于非实名用户关停改造需求
     * 根据user_id判断是否存在TL_B_STOPUSER及TF_BH_TRADE 1361的信息
     * 存在则说明办理过实名制停机。
     * */
    public static IDataset qryUserIfNotRealName(IData inData)throws Exception
    {  
        return Dao.qryByCode("TL_B_STOPUSER", "SEL_STOPUSER_NOTREALNAME_NOW", inData);
    } 
    
    /**
     * REQ201608260010 关于非实名用户关停改造需求
     * 20160912 chenxy3
     * 欠费停机转“非实名制全停”
     * */
    public static IDataset qryUserIfAllStop(IData inData)throws Exception
    {  
        return Dao.qryByCode("TL_B_STOPUSER", "SEL_IF_ALL_STOP_USER", inData);
    } 
    public static IDataset qryUserIfNotRealNameForOpen(IData inData)throws Exception
    {  
        return Dao.qryByCode("TD_S_CPARAM", "IS_SVC_STATE5_USER", inData);
    }
    public static IDataset qryUserHistoryInfoByUserIdAndTradeType(IData param)throws Exception
    {
    	return Dao.qryByCode("TF_BH_TRADE", "SEL_TRADE_BY_UID_AND_TYPE", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }
    
    /**
     * REQ201701040013客户资料综合查询界面增加可以根据白卡号查询
	 * <br/>
	 * 通过白卡获取sim卡号
	 * @author zhuoyingzhi
	 * @date 20170227
     * @param inData
     * @return
     * @throws Exception
     */
    public static IDataset qrySIMByWhiteSIM(IData inData)throws Exception{
    	//需要修改资源接口信息
    	/**
    	 * ResCall.getEmptycardInfo 在生产上已经存在
    	 */
    	String res_no=inData.getString("SIMNUMBER");
        IDataset userResInfo = ResCall.getEmptycardInfo(res_no, "", "");
    	return userResInfo;
    }

    /**
	 * 通过身份证号码获取用户信息
	 * @author zhengkai5
	 * @date 20180313
	 * @param param
	 * @return
     * @throws Exception 
	 */
    public static IDataset qryCustInfoByPsptId (IData data) throws Exception{
    	String psptId = data.getString("PSPT_ID");
    	if(StringUtils.isNotEmpty(psptId))
    	{
    		SQLParser parser = new SQLParser(data);
            parser.addSQL(" select t.cust_id from TF_F_CUSTOMER t");
            parser.addSQL(" where t.PSPT_ID =:PSPT_ID ");
            IDataset dataset = Dao.qryByParse(parser);
            if (dataset.size() > 0)
            {
                return dataset;
            }
    	}
    	return null;
    }

    /**
     * "客户资料综合查询"界面外框从CRM获取用户数据
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryPersonBaseInfo(IData param) throws Exception {
        String userId = param.getString("USER_ID");
        IData userInfo;

        userInfo = UcaInfoQry.qryUserInfoByUserId(userId); // 用户信息
        if (IDataUtil.isEmpty(userInfo)) { // 如果查询TF_F_USER表返回为空，则查询TF_FH_USER表
            QryUserInfoDao userHisDao = new QryUserInfoDao();
            userInfo = userHisDao.qryUserInfoFromHis(userId).first();
        }

        IData userMainProdInfo = UcaInfoQry.qryUserMainProdInfoByUserId(userId); // 用户主产品信息
        if (IDataUtil.isNotEmpty(userMainProdInfo)) {
            userInfo.putAll(userMainProdInfo);
        }

        IDataset custInfos = CustomerInfoQry.getCustomerInfoByUserId(userId);
        if (IDataUtil.isNotEmpty(custInfos)) {
            userInfo.put("CUST_NAME", custInfos.first().getString("CUST_NAME")); // 客户名称
        }

        String inDate = userInfo.getString("IN_DATE", "");
        int yearElapsed = SysDateMgr.yearInterval(inDate, SysDateMgr.getSysDate());
        userInfo.put("YEAR_ELAPSED", yearElapsed); // 入网时长

        userInfo.put("IS_4G_USER", "未知");
        IDataset resDatas = UserResInfoQry.queryUserResByUserIdResType(userId, "1"); // 4G用户
        if (IDataUtil.isNotEmpty(resDatas)) {
            String LTETag = resDatas.first().getString("RSRV_TAG3", "");
            if ("1".equals(LTETag)) {
                userInfo.put("IS_4G_USER", "是");
            } else {
                String simCardNo = resDatas.first().getString("RES_CODE");
                IDataset simCardDatas = ResCall.getSimCardInfo("0", simCardNo, "", null);
                if (IDataUtil.isNotEmpty(simCardDatas)) {
                    String cardModeType = simCardDatas.first().getString("CARD_MODE_TYPE", "");
                    String opc = simCardDatas.first().getString("OPC", "");
                    if ("1".equals(cardModeType) || (!"2".equals(cardModeType) && StringUtils.isBlank(opc))) {
                        userInfo.put("IS_4G_USER", "否");
                    } else {
                        userInfo.put("IS_4G_USER", "是");
                    }
                }
            }
        }
        
        //REQ201812100001关于全球通客户标识的需求 wuhao5       
        IData inData = new DataMap();
        inData.put("USER_ID", param.getString("USER_ID",""));
        IDataset dataset = UserClassInfoQry.queryUserClass(inData);
		if(IDataUtil.isNotEmpty(dataset) && dataset.size()>0){ 
			IData data = dataset.getData(0);
			String QQTClass = StaticUtil.getStaticValue("USER_INFO_CLASS", data.getString("USER_CLASS"));
			userInfo.put("QQT_CLASS", QQTClass);
		}else{
			userInfo.put("QQT_CLASS", "否");
		}		
		//END REQ201812100001关于全球通客户标识的需求 wuhao5

        return IDataUtil.idToIds(userInfo);
    }

    /**
     * "客户资料综合查询"界面外框从账管等模块获取用户数据
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryPersonAcctInfo(IData param) throws Exception {
        IData acctInfo = new DataMap();
        String userId = param.getString("USER_ID");
        String custId = param.getString("CUST_ID");
        String normalUserCheck = param.getString("NORMAL_USER_CHECK");

        IDataset creditInfo;
        if ("on".equals(normalUserCheck)) {
            creditInfo = CreditCall.getCreditInfo(userId, "0"); // 星级和信用度
        } else {
            creditInfo = CreditCall.getCreditInfo(custId, "1");
        }
        if (IDataUtil.isNotEmpty(creditInfo)) {
            IData creditMap = creditInfo.first();
            String creditClass = creditMap.getString("CREDIT_CLASS", "");
            /**
             * REQ201608160006 将NGBOSS界面涉及“五星普”全改为“五星银”的需求
             * @author zhuoyingzhi
             * 20160909
             * 修改为直接读取账务接口,显示星级名称
             */
            if (!"-1".equals(creditClass) && !"".equals(creditClass) && creditClass != null) {
                acctInfo.put("CREDIT_CLASS", creditMap.getString("CREDIT_CLASS_NAME", ""));
            } else {
                acctInfo.put("CREDIT_CLASS", "未评级");
            }
            acctInfo.put("CREDIT_VALUE", creditMap.getString("CREDIT_VALUE", "0"));
        } else {
            acctInfo.put("CREDIT_CLASS", "无");
            acctInfo.put("CREDIT_VALUE", "无");
        }

        IDataset scoreInfo = AcctCall.queryUserScore(userId); // 积分
        if (IDataUtil.isNotEmpty(scoreInfo)) {
            acctInfo.putAll(scoreInfo.first());
        } else {
            acctInfo.put("SUM_SCORE", "无");
        }

        IDataset avgFee = MvelMiscQry.getUserAvgPayFee(userId, "3"); // 近三月月均
        if (IDataUtil.isNotEmpty(avgFee)) {
            acctInfo.put("AVERAGE_FEE", avgFee.first().getString("PARAM_CODE"));
        } else {
            acctInfo.put("AVERAGE_FEE", "无");
        }

        boolean isRed = AcctCall.checkIsRedUser(userId); // 红名单
        if (isRed) {
            acctInfo.put("IS_RED", "是");
        } else {
            acctInfo.put("IS_RED", "否");
        }

        acctInfo.put("IS_BLACK", "否");
        IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
        if (IDataUtil.isNotEmpty(custInfo)) {
            IDataset blackListSet = AcctCall.qryBlackListByPsptId(custInfo); // 黑名单
            if (IDataUtil.isNotEmpty(blackListSet)) {
                for (Object obj : blackListSet) {
                    IData blackList = (IData) obj;
                    String oweFee = blackList.getString("OWE_FEE", "0");
                    if (!"0".equals(oweFee)) {
                        acctInfo.put("IS_BLACK", "是");
                        break;
                    }
                }
            }
        }

        IData oweFeeInfo = AcctCall.getOweFeeByUserId(userId); // 实时话费、实时结余和往月欠费
        acctInfo.putAll(oweFeeInfo);
        
        //REQ201812100001关于全球通客户标识的需求 wuhao5
        IData inData = new DataMap();
        inData.put("USER_ID", param.getString("USER_ID",""));
        IDataset dataset = UserClassInfoQry.queryUserClass(inData);
		if(IDataUtil.isNotEmpty(dataset) && dataset.size()>0){ 
			IData data = dataset.getData(0);
			String QQTClass = StaticUtil.getStaticValue("USER_INFO_CLASS", data.getString("USER_CLASS"));
			acctInfo.put("QQT_CLASS", QQTClass);
		}else{
			acctInfo.put("QQT_CLASS", "否");
		}
		//END REQ201812100001关于全球通客户标识的需求 wuhao5
		
		
		//《REQ201904090062 为服务质量监督员建立标志的业务需求工单》start
		IData inparam=new DataMap();
        inparam.put("USER_ID", param.getString("USER_ID",""));
        inparam.put("RSRV_VALUE_CODE", "SVCQ_SUPERVISOR"); 
        
        IDataset otherds = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_USERID_ENDDATE", inparam);
		if(IDataUtil.isNotEmpty(otherds)){
			acctInfo.put("SVCQ_SUPERVISOR", "是");
		}else{
			acctInfo.put("SVCQ_SUPERVISOR", "否");
		}
		
		//《REQ201904090062 为服务质量监督员建立标志的业务需求工单》end
		
		log.error("Qry360InfoBeanxxxxxxxxxxxxxxxx1149 "+acctInfo);
        return IDataUtil.idToIds(acctInfo);
    }

    /**
     * "客户资料综合查询"子页面获取导航按钮配置信息
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset loadNavButtonConfig(IData param) throws Exception {
        int titleCount = Integer.parseInt(param.getString("TITLE_COUNT"));
        IDataset returnList = new DatasetList();

        Qry360InfoDAO dao = new Qry360InfoDAO();
        IDataset buttonList = dao.loadNavButtonConfig(param);

        if (IDataUtil.isNotEmpty(buttonList)) {
            int size = buttonList.size();
            for (int k = size - 1; k >= 0; k--) { // 校验工号是否有菜单权限
                String staffId = BizBean.getVisit().getStaffId();
                String rightCode = buttonList.getData(k).getString("RIGHT_CODE");
                boolean hasPriv = StaffPrivUtil.isPriv(staffId, rightCode, StaffPrivUtil.PRIV_TYPE_FUNCTION);
                if (!hasPriv) {
                    buttonList.remove(k);
                    size--;
                } else {
                    String pathName = WelcomeBean.qrySystemGuiMenu(rightCode).getString("MENU_PATH_NAME", "");
                    buttonList.getData(k).put("PATH_NAME", pathName);
                }
            }

            int t = 0;
            for (int i = 0; i < titleCount; i++) { // 将菜单按钮按 TITLE_POSITION 归类
                IData buttonMap = new DataMap();
                IDataset tempList = new DatasetList();
                for (int j = t; j < size; j++) {
                    IData button = buttonList.getData(j);
                    if (i == Integer.parseInt(button.getString("TITLE_POSITION"))) {
                        tempList.add(button);
                    } else {
                        t = j;
                        break;
                    }
                }
                DataHelper.sort(tempList, "BUTTON_ORDER", IDataset.TYPE_INTEGER, IDataset.ORDER_ASCEND);
                buttonMap.clear();
                buttonMap.put("BUTTON_INFO", tempList);
                buttonMap.put("POSITION", Integer.toString(i));
                returnList.add(buttonMap);
            }
        }
        return returnList;
    }

    /**
     * "客户资料综合查询"首页标签查询用户近三月账单明细
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset queryUserBillInfo(IData inParam) throws Exception {
        IDataset userBill = new DatasetList();
        String userId = inParam.getString("USER_ID");
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
        int interval = SysDateMgr.monthIntervalNoAbs(userInfo.getString("IN_DATE"), SysDateMgr.getSysDate()); // 计算用户开户时间与系统时间的月差值
        interval = (interval > 2) ? 2 : interval; // 该月差值的最大值为2

        String endCycle = SysDateMgr.getNowCyc();
        String startCycle = SysDateMgr.decodeTimestamp(SysDateMgr.getAddMonthsNowday(-interval, endCycle), SysDateMgr.PATTERN_TIME_YYYYMM);

        IDataset nowInfo = AcctCall.queryNewUserBill(userId, startCycle, endCycle);
        if (IDataUtil.isNotEmpty(nowInfo.first())) {
            IDataset billInfos = nowInfo.first().getDataset("BILL_INFOS");
            for (Object obj : billInfos) { // 将账管接口返回账单结果集按照INTEGRATE_ITEM_CODE重新封装
                IData bill = (IData) obj;

                int flag = bill.getInt("SPECIAL_FLAG");
                if (Integer.compare(1, flag) != 0) continue; // 仅展示SPECIAL_FLAG=1的账单目录

                String fee = bill.getString("FEE");
                String cycleId = bill.getString("CYCLE_ID", "");
                String itemCode = bill.getString("INTEGRATE_ITEM_CODE", "");
                if (StringUtils.isNotBlank(itemCode) && StringUtils.isNotBlank(cycleId)) {
                    int cycleInterval = SysDateMgr.monthIntervalYYYYMM(cycleId, endCycle);
                    // 如果userBill结果集包含("INTEGRATE_ITEM_CODE", itemCode)的IData，需要定位到该IData，
                    // 然后将费用信息和对应的cycleId插入该IData
                    if (IDataUtil.dataSetContainsKeyAndValue(userBill, "INTEGRATE_ITEM_CODE", itemCode)) {
                        int i = 0;
                        while (i <= userBill.size()) {
                            if (itemCode.equals(userBill.getData(i).getString("INTEGRATE_ITEM_CODE"))) {
                                break;
                            }
                            i++;
                        }
                        switch (cycleInterval) {
                            case 0:
                                userBill.getData(i).put("NOW_FEE", fee);
                                userBill.getData(i).put("THIS_MONTH", cycleId);
                                break;
                            case 1:
                                userBill.getData(i).put("LAST_FEE", fee);
                                userBill.getData(i).put("LAST_MONTH", cycleId);
                                break;
                            case 2:
                            default:
                                userBill.getData(i).put("BEFORE_LAST_FEE", fee);
                                userBill.getData(i).put("MONTH_BEFORE_LAST", cycleId);
                                break;
                        }
                    } else { // 如果userBill结果集不包含("INTEGRATE_ITEM_CODE", itemCode)的IData，新构建一个IData放入userBill
                        IData itemMap = new DataMap();
                        switch (cycleInterval) {
                            case 0:
                                itemMap.put("NOW_FEE", fee);
                                itemMap.put("THIS_MONTH", cycleId);
                                break;
                            case 1:
                                itemMap.put("LAST_FEE", fee);
                                itemMap.put("LAST_MONTH", cycleId);
                                break;
                            case 2:
                            default:
                                itemMap.put("BEFORE_LAST_FEE", fee);
                                itemMap.put("MONTH_BEFORE_LAST", cycleId);
                                break;
                        }
                        itemMap.put("INTEGRATE_ITEM_CODE", itemCode);
                        itemMap.put("INTEGRATE_ITEM", bill.getString("INTEGRATE_ITEM"));
                        userBill.add(itemMap);
                    }
                }
            }
        }
        return userBill;
    }

    /**
     * "客户资料综合查询"首页标签查询当月用户套餐使用量
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset queryUserConsumptionInfo(IData inParam) throws Exception {
        IData consumptionInfo = new DataMap();
        String nowCycle = SysDateMgr.getNowCyc();
        // 查询入参与用"户套餐使用量查询"界面保持一致
        IDataset userDiscntFee = AcctCall.qryUserDiscntFee(inParam.getString("USER_ID"), nowCycle, "1", "99", "TRUE");
        if (IDataUtil.isNotEmpty(userDiscntFee)) {
            long gprsTotalValue = 0, gprsTotalUsed = 0, gprsTotalBalance = 0,
                 callTotalValue = 0, callTotalUsed = 0, callTotalBalance = 0,
                 textTotalValue = 0, textTotalUsed = 0, textTotalBalance = 0,
                 wlanTotalValue = 0, wlanTotalUsed = 0, wlanTotalBalance = 0;

            // 统计流量、语音和短信使用情况
            for (Object obj : userDiscntFee) {
                IData discntFee = (IData) obj;
                long highFee = getLong("HIGH_FEE", discntFee);
                long value = getLong("VALUE", discntFee);
                long balance = getLong("BALANCE", discntFee);
                int typeCode = discntFee.getInt("ITEM_TYPE_CODE");
                if (3 == typeCode || 9 == typeCode) {        // 流量
                    gprsTotalValue += highFee;
                    gprsTotalUsed += value;
                    gprsTotalBalance += balance;
                } else if (2 == typeCode) {                  // 语音
                    callTotalValue += highFee;
                    callTotalUsed += value;
                    callTotalBalance += balance;
                } else if (4 == typeCode || 5 == typeCode) { // 短信
                    textTotalValue += highFee;
                    textTotalUsed += value;
                    textTotalBalance += balance;
                } else if (6 == typeCode) {                  // WLAN上网时长
                    wlanTotalValue += highFee;
                    wlanTotalUsed += value;
                    wlanTotalBalance += balance;
                }
            }

            DecimalFormat rateFormat = new DecimalFormat("#.##");
            rateFormat.setRoundingMode(RoundingMode.HALF_UP);

            // 流量单位转换

            // 余量百分比计算
            String gprsRate = rateFormat.format((double) gprsTotalUsed / (double) gprsTotalValue);
            String callRate = rateFormat.format((double) callTotalUsed / (double) callTotalValue);
            String textRate = rateFormat.format((double) textTotalUsed / (double) textTotalValue);
            String wlanRate = rateFormat.format((double) wlanTotalUsed / (double) wlanTotalValue);
            gprsRate = (Long.compare(gprsTotalUsed, gprsTotalValue) < 0 && "1.00".equals(gprsRate)) ? "0.99" : gprsRate;
            callRate = (Long.compare(callTotalUsed, callTotalValue) < 0 && "1.00".equals(callRate)) ? "0.99" : callRate;
            textRate = (Long.compare(textTotalUsed, textTotalValue) < 0 && "1.00".equals(textRate)) ? "0.99" : textRate;
            wlanRate = (Long.compare(wlanTotalUsed, wlanTotalValue) < 0 && "1.00".equals(wlanRate)) ? "0.99" : wlanRate;

            // 流量 type: double
            consumptionInfo.put("GPRS_TOTAL", Double.parseDouble(gprsUnitConvert(gprsTotalValue, rateFormat)));
            consumptionInfo.put("GPRS_USED", Double.parseDouble(gprsUnitConvert(gprsTotalUsed, rateFormat)));
            consumptionInfo.put("GPRS_BALANCE", Double.parseDouble(gprsUnitConvert(gprsTotalBalance, rateFormat)));

            // 语音 type: long
            consumptionInfo.put("CALL_TOTAL", callTotalValue);
            consumptionInfo.put("CALL_USED", callTotalUsed);
            consumptionInfo.put("CALL_BALANCE", callTotalBalance);

            // 短信 type: long
            consumptionInfo.put("TEXT_TOTAL", textTotalValue);
            consumptionInfo.put("TEXT_USED", textTotalUsed);
            consumptionInfo.put("TEXT_BALANCE", textTotalBalance);

            // WLAN type: long
            consumptionInfo.put("WLAN_TOTAL", wlanTotalValue);
            consumptionInfo.put("WLAN_USED", wlanTotalUsed);
            consumptionInfo.put("WLAN_BALANCE", wlanTotalBalance);

            // type: java.lang.String
            consumptionInfo.put("GPRS_RATE", gprsRate);
            consumptionInfo.put("CALL_RATE", callRate);
            consumptionInfo.put("TEXT_RATE", textRate);
            consumptionInfo.put("WLAN_RATE", wlanRate);
        }
        return IDataUtil.idToIds(consumptionInfo);
    }

    /**
     * 从DataMap里获取long型数据，jar包自带的getLong()方法会报错
     * @param key
     * @param map
     * @return
     */
    private long getLong(String key, IData map) {
        String tempStr = map.getString(key, "0");
        long value;
        if (tempStr.contains("."))
            value = Long.parseLong(tempStr.substring(0, tempStr.lastIndexOf(".")));
        else
            value = Long.parseLong(tempStr);
        return value;
    }

    /**
     * 流量单位"KB"换算成"MB"
     * @param value
     * @return
     */
    private String gprsUnitConvert(long value, DecimalFormat format) {
        return format.format(value / 1024.0);
    }

    /**
     * "客户资料综合查询"我的活动标签查询营销活动预存款和赠送款信息
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset querySaleActiveDepositAndGiftInfo(IData inParam) throws Exception {
        IData depositGiftInfo = new DataMap();
        String userId = inParam.getString("USER_ID");
        String serialNumber = inParam.getString("SERIAL_NUMBER");
        String relationTradeId = inParam.getString("RELATION_TRADE_ID");

        UcaData uca = UcaDataFactory.getUcaByUserId(userId);
        String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, relationTradeId);
        if (StringUtils.isNotBlank(actionCode)) {
            IData feeInfo = AcctCall.obtainUserAllFeeLeaveFee(serialNumber, actionCode);
            if (IDataUtil.isNotEmpty(feeInfo)) {
                String resultCode = feeInfo.getString("RESULT_CODE", "");
                if (StringUtils.isNotBlank(resultCode) && "0".equals(resultCode)) {
                    double depositSum = 0;
                    double depositRemained = 0;
                    double giftSum = 0;
                    double giftRemained = 0;

                    IDataset feeInfos = feeInfo.getDataset("FEE_INFOS");
                    for (Object obj : feeInfos) {
                        IData fee = (IData) obj;
                        String actionAttr = fee.getString("ACTION_ATTR");
                        double allMoney = fee.getDouble("ALL_MONEY", 0) / 100;
                        double leftMoney = fee.getDouble("LEFT_MONEY", 0) / 100;

                        if ("0".equals(actionAttr)) {        // 预存返还
                            depositSum += allMoney;
                            depositRemained += leftMoney;
                        } else if ("1".equals(actionAttr)) { // 赠送返还
                            giftSum += allMoney;
                            giftRemained += leftMoney;
                        }
                    }

                    DecimalFormat rateFormat = new DecimalFormat("#.##");
                    rateFormat.setRoundingMode(RoundingMode.HALF_UP);

                    double depositUsed = depositSum - depositRemained;
                    double giftUsed = giftSum - giftRemained;
                    String depositRate = rateFormat.format(depositUsed / depositSum);
                    String giftRate    = rateFormat.format(giftUsed    / giftSum);
                    depositRate = (Double.compare(depositUsed, depositSum) < 0 && "1.00".equals(depositRate)) ? "0.99" : depositRate;
                    giftRate    = (Double.compare(giftUsed   , giftSum   ) < 0 && "1.00".equals(giftRate   )) ? "0.99" : giftRate;

                    depositGiftInfo.put("DEPOSIT_SUM", depositSum);
                    depositGiftInfo.put("DEPOSIT_USED", depositUsed);
                    depositGiftInfo.put("DEPOSIT_REMAINED", depositRemained);
                    depositGiftInfo.put("DEPOSIT_USED_PERCENT", depositRate);
                    depositGiftInfo.put("GIFT_SUM", giftSum);
                    depositGiftInfo.put("GIFT_USED", giftUsed);
                    depositGiftInfo.put("GIFT_REMAINED", giftRemained);
                    depositGiftInfo.put("GIFT_USED_PERCENT", giftRate);
                }
            }
        }
        return IDataUtil.idToIds(depositGiftInfo);
    }

    /**
     * "客户资料综合查询"我的业务历"最近"标签初始化
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset initTradeHistoryTab(IData inParam) throws Exception {
        IData outParam = new DataMap();
        outParam.putAll(inParam);

        String startDate = SysDateMgr.firstDayOfDate(SysDateMgr.getSysTime(), -2);
        outParam.put("START_DATE", startDate);
        outParam.put("END_DATE", SysDateMgr.getSysDate());

        Qry360InfoDAO dao = new Qry360InfoDAO();
        inParam.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode()); // 设置路由
        IDataset tradeTypeCode = dao.queryTradeTypeCode(inParam);
        outParam.put("TRADE_TYPE_CODE", tradeTypeCode);

        return IDataUtil.idToIds(outParam);
    }

    /**
     * "客户资料综合查询"我的业务历史"历史（一年以前）"标签初始化
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset initTradeHistoryBeforeTab(IData inParam) throws Exception {
        IData outParam = new DataMap();
        outParam.putAll(inParam);

        String startDateHis = SysDateMgr.firstDayOfDate(SysDateMgr.getSysTime(), -12);
        outParam.put("START_DATE_HIS", startDateHis);
        outParam.put("END_DATE_HIS", SysDateMgr.getLastDayOfMonth(-10));

        Qry360InfoDAO dao = new Qry360InfoDAO();
        inParam.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode()); // 设置路由
        IDataset tradeTypeCodeHis = dao.queryTradeTypeCodeCg(inParam);
        outParam.put("TRADE_TYPE_CODE_HIS", tradeTypeCodeHis);

        IDataset years = new DatasetList();
        int nowYear = Integer.valueOf(SysDateMgr.getNowYear());
        // 配置里面的年份范围
        int interval = Integer.valueOf(StaticUtil.getStaticValue("TRADEINFOHIS_YEAR1", "DEFAULT_YEARS_ELAPSED"));
        int beginYear = nowYear - interval;
        int endYear = nowYear - 1;
        for (int i = endYear; i >= beginYear; i--) {
            IData data = new DataMap();
            data.put("KEY", i);
            data.put("VALUE", i);
            years.add(data);
        }
        outParam.put("YEARS", years);

        return IDataUtil.idToIds(outParam);
    }

    /**
     * "客户资料综合查询"我的业务历史查询订单主台账表数据
     * @param inParam
     * @return
     * @throws Exception
     */
    IDataset queryMainTradeTable(IData inParam) throws Exception {
        IData mainTrade = new DataMap();
        String bookCheck = inParam.getString("BOOK_CHECK", "");
        if ("on".equals(bookCheck)) { // 查询thAccept数据需要TRADE_FLAG字段
            inParam.put("TRADE_FLAG", "0");
        } else {
            inParam.put("TRADE_FLAG", "1");
        }

        IData thAccept = CSAppCall.callOne("SS.GetUser360THViewSVC.qryThAcceptInfo", inParam);
        IData tradeOther = CSAppCall.callOne("SS.GetUser360THViewSVC.qryTradeOtherInfo", inParam);

        if (IDataUtil.isNotEmpty(thAccept)) {
            mainTrade.put("CANCEL_STAFF_ID", thAccept.getString("CANCEL_STAFF_ID"));
            mainTrade.put("CANCEL_DEPART_ID", thAccept.getString("CANCEL_DEPART_ID"));
            mainTrade.put("CANCEL_CITY_CODE", thAccept.getString("CANCEL_CITY_CODE"));
            mainTrade.put("CANCEL_EPARCHY_CODE", thAccept.getString("CANCEL_EPARCHY_CODE"));
            mainTrade.put("PROCESS_TAG_SET_STR", thAccept.getString("PROCESS_TAG_SET_STR"));
            mainTrade.put("CHECK_MOD", thAccept.getString("CHECK_MOD"));
        }
        if (IDataUtil.isNotEmpty(tradeOther)) {
            for (int i = 1; i <= 10; i++) {
                String key = "RSRV_STR" + Integer.toString(i);
                mainTrade.put(key, tradeOther.getString(key));
            }
        }

        return IDataUtil.idToIds(mainTrade);
    }

    /**
     * "客户资料综合查询"我的业务历史查询订单关联子台账表数据
     * @param inParam
     * @return
     * @throws Exception
     */
    IDataset querySubTradeTable(IData inParam) throws Exception {
        IData subTrade = new DataMap();
        String tradeTypeCode = inParam.getString("TRADE_TYPE_CODE", "");

        IDataset thProduct   = CSAppCall.call("SS.GetUser360THViewSVC.qryThProductInfo", inParam);
        IDataset thDiscnt    = CSAppCall.call("SS.GetUser360THViewSVC.qryThDiscntInfo", inParam);
        IDataset thSvc       = CSAppCall.call("SS.GetUser360THViewSVC.qryThSvcInfo", inParam);
        IDataset thSvcStatus = CSAppCall.call("SS.GetUser360THViewSVC.qryThSvcStatusInfo", inParam);
        IDataset thPlatsvc   = CSAppCall.call("SS.GetUser360THViewSVC.qryThPlatSvcInfo", inParam);
        IDataset thScore     = CSAppCall.call("SS.GetUser360THViewSVC.qryThScoreInfo", inParam);
        IDataset thResource  = CSAppCall.call("SS.GetUser360THViewSVC.qryThResInfo", inParam);
        IDataset thRelation  = CSAppCall.call("SS.GetUser360THViewSVC.qryThRelationInfo", inParam);
        IDataset thPreActive = CSAppCall.call("SS.GetUser360THViewSVC.queryTradePreSaleActive", inParam);
        if ("375".equals(tradeTypeCode) || "377".equals(tradeTypeCode) || "381".equals(tradeTypeCode)) {
            IDataset thCard = CSAppCall.call("SS.GetUser360THViewSVC.qryThCardInfo", inParam);
            if (IDataUtil.isNotEmpty(thCard)) {
                subTrade.put("CARD", thCard);
            }
        }
        IData thContact = CSAppCall.callOne("SS.GetUser360THViewSVC.qryThContactInfo", inParam);
        IData thAssure  = CSAppCall.callOne("SS.GetUser360THViewSVC.qryThAssureInfo", inParam);

        if (IDataUtil.isNotEmpty(thProduct)) {
            for (Object obj : thProduct) {
                IData product = (IData) obj;
                String modifyTag = product.getString("MODIFY_TAG");
                product.put("COLOR", View360Const.ELEMENT_COMMON + View360Const.PRODUCT_COLOR.get(modifyTag));
                product.put("MODIFY_STATE", View360Const.PRODUCT_MODIFY_TAG.get(modifyTag));
                product.put("PRODUCT_NAME", UpcCall.qryOfferNameByOfferTypeOfferCode(product.getString("PRODUCT_ID", ""), BofConst.ELEMENT_TYPE_CODE_PRODUCT));
            }
            subTrade.put("PRODUCT", thProduct);
        }
        if (IDataUtil.isNotEmpty(thDiscnt)) {
            for (Object obj : thDiscnt) {
                IData discnt = (IData) obj;
                String modifyTag = discnt.getString("MODIFY_TAG");
                discnt.put("COLOR", View360Const.ELEMENT_COMMON + View360Const.ELEMENT_COLOR.get(modifyTag));
                discnt.put("MODIFY_STATE", View360Const.ELEMENT_MODIFY_TAG.get(modifyTag));
                discnt.put("DISCNT_NAME", UpcCall.qryOfferNameByOfferTypeOfferCode(discnt.getString("DISCNT_CODE", ""), BofConst.ELEMENT_TYPE_CODE_DISCNT));
            }
            subTrade.put("DISCNT", thDiscnt);
        }
        if (IDataUtil.isNotEmpty(thSvc)) {
            for (Object obj : thSvc) {
                IData svc = (IData) obj;
                String modifyTag = svc.getString("MODIFY_TAG");
                svc.put("COLOR", View360Const.ELEMENT_COMMON + View360Const.ELEMENT_COLOR.get(modifyTag));
                svc.put("MODIFY_STATE", View360Const.ELEMENT_MODIFY_TAG.get(modifyTag));
                svc.put("SERVICE_NAME", UpcCall.qryOfferNameByOfferTypeOfferCode(svc.getString("SERVICE_ID", ""), BofConst.ELEMENT_TYPE_CODE_SVC));
            }
            subTrade.put("SVC", thSvc);
        }
        if (IDataUtil.isNotEmpty(thSvcStatus)) {
            for (Object obj : thSvcStatus) {
                IData svcStatus = (IData) obj;
                String modifyTag = svcStatus.getString("MODIFY_TAG");
                String serviceId = svcStatus.getString("SERVICE_ID", "");
                svcStatus.put("COLOR", View360Const.ELEMENT_COMMON + View360Const.ELEMENT_COLOR.get(modifyTag));
                svcStatus.put("MODIFY_STATE", View360Const.ELEMENT_MODIFY_TAG.get(modifyTag));
                svcStatus.put("SERVICE_NAME", UpcCall.qryOfferNameByOfferTypeOfferCode(serviceId, BofConst.ELEMENT_TYPE_CODE_SVC));
                IDataset stateInfo = UpcCall.qryOfferFuncStaByAnyOfferIdStatus(serviceId, BofConst.ELEMENT_TYPE_CODE_SVC, svcStatus.getString("STATE_CODE", ""));
                svcStatus.put("STATE_NAME", IDataUtil.isEmpty(stateInfo) ? "未知" : stateInfo.first().getString("STATUS_NAME"));
            }
            subTrade.put("SVCSTATUS", thSvcStatus);
        }
        if (IDataUtil.isNotEmpty(thPlatsvc)) {
            for (Object obj : thPlatsvc) {
                IData platsvc = (IData) obj;
                String bizStateCode = View360Const.PLATSVC_STATE.get(platsvc.getString("BIZ_STATE_CODE"));
                String bizStateColor = View360Const.PLATSVC_COMMON + View360Const.PLATSVC_COLOR.get(bizStateCode);
                String bizTypeName = StaticUtil.getStaticValue("BIZ_TYPE_CODE", platsvc.getString("BIZ_TYPE_CODE"));
                platsvc.put("BIZ_STATE_CODE", bizStateCode);
                platsvc.put("BIZ_STATE_COLOR", bizStateColor);
                platsvc.put("BIZ_TYPE_NAME", bizTypeName == null ? "未知" : bizTypeName);
                platsvc.put("SERVICE_NAME", UpcCall.qryOfferNameByOfferTypeOfferCode(platsvc.getString("SERVICE_ID", ""), BofConst.ELEMENT_TYPE_CODE_PLATSVC));
            }
            subTrade.put("PLATSVC", thPlatsvc);
        }
        if (IDataUtil.isNotEmpty(thScore)) {
            subTrade.put("SCORE", thScore);
        }
        if (IDataUtil.isNotEmpty(thResource)) {
            for (Object obj : thResource) {
                IData res = (IData) obj;
                String modifyTag = res.getString("MODIFY_TAG");
                String resTypeCode = res.getString("RES_TYPE_CODE");
                res.put("COLOR", View360Const.ELEMENT_COMMON + View360Const.ELEMENT_COLOR.get(modifyTag));
                res.put("MODIFY_STATE", View360Const.ELEMENT_MODIFY_TAG.get(modifyTag));
                String resTypeName = StaticUtil.getStaticValueDataSource(getVisit(), "res", "RES_TYPE", "RES_TYPE_ID", "RES_TYPE_NAME", resTypeCode, "未知");
                res.put("RES_TYPE_NAME", resTypeName);
            }
            subTrade.put("RESOURCE", thResource);
        }
        if (IDataUtil.isNotEmpty(thRelation)) {
            for (Object obj : thRelation) {
                IData relation = (IData) obj;
                String[] keysA = new String[]{"RELATION_TYPE_CODE", "ROLE_CODE_A"};
                String[] keysB = new String[]{"RELATION_TYPE_CODE", "ROLE_CODE_B"};
                String[] valuesA = new String[]{relation.getString("RELATION_TYPE_CODE"), relation.getString("ROLE_CODE_A")};
                String[] valuesB = new String[]{relation.getString("RELATION_TYPE_CODE"), relation.getString("ROLE_CODE_B")};
                String role  = StaticUtil.getStaticValue(getVisit(), "TD_S_RELATION", "RELATION_TYPE_CODE", "RELATION_TYPE_NAME", relation.getString("RELATION_TYPE_CODE"), "无");
                String roleA = StaticUtil.getStaticValue(getVisit(), "TD_S_RELATION_ROLE", keysA, "ROLE_A", valuesA, "无");
                String roleB = StaticUtil.getStaticValue(getVisit(), "TD_S_RELATION_ROLE", keysB, "ROLE_B", valuesB, "无");
                relation.put("RELATION_TYPE_NAME", role);
                relation.put("ROLE_A", roleA);
                relation.put("ROLE_B", roleB);
            }
            subTrade.put("RELATION", thRelation);
        }
        if (IDataUtil.isNotEmpty(thPreActive)) {
            subTrade.put("PREACTIVE", thPreActive);
        }
        if (IDataUtil.isNotEmpty(thContact)) {
            subTrade.put("CONTACT", thContact);
        }
        if (IDataUtil.isNotEmpty(thAssure)) {
            String assureTypeCode = thAssure.getString("ASSURE_TYPE_CODE", "");
            String assureDate = thAssure.getString("ASSURE_DATE", "");
            if (StringUtils.isNotBlank(assureTypeCode) || StringUtils.isNotBlank(assureDate)) {
                subTrade.put("ASSURE", thAssure);
            }
        }

        return IDataUtil.idToIds(subTrade);
    }
}
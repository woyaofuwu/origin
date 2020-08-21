
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.querynpmessage;

import java.util.Calendar;
import java.util.Iterator;

import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTimeUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.CParamQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;

/**
 * 
 * @author panyu5
 * @title 短厅业务
 * @time 2018/10/29
 */
public class QueryNpMessageBean extends CSBizBean
{
	
	private static transient Logger logger = Logger.getLogger(QueryNpMessageBean.class);

	public IData queryNpMessage(IData param, Pagination page) throws Exception
    {
        String userId ="";
        String acctMode="0";
        String saleFlag="0";
        String serialNumber = param.getString("SERIAL_NUMBER");
        IDataset alertInfo = new DatasetList();
//        IData remindData = new DataMap();
//        IData npsoalogData = new DataMap();
        IData returnData = new DataMap();
        IDataset resultList = param.getDataset("RESULTINFO");
		IDataset limitInfoLists = param.getDataset("LIMITINFOLIST");
		UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
		String strCreditClass=uca.getUserCreditClass();

		if(StringUtils.isNotBlank(param.getString("NO_MORE_CHECK"))){
            IDataset riskInfo = new DatasetList();
            returnData.put("infos", alertInfo);
            returnData.put("riskInfo", riskInfo);
            return returnData;
        }

        IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", param.getString("SERIAL_NUMBER"));
        if (IDataUtil.isNotEmpty(userInfo))
        {
        	userId = userInfo.getData(0).getString("USER_ID");
        }
        else
        {
        	CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "没有获取到用户信息！");
        }

        //没有使用到
        // 分公司携转规则取得
//        String npOutSet = queryNpOutSet(userId);
        
        // 提示用户做申请时该用的证件号码和证件类型
//        IData custData = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getData(0).getString("CUST_ID"));
//        if (IDataUtil.isNotEmpty(custData))
//        {
//        	remindData.put("REMIND_INFO", "提示：用户携出时，只有使用如下【姓名："+custData.getString("CUST_NAME")+",证件号码："+custData.getString("PSPT_ID")+"】才能成功办理业务！");
//        }
//
//        // 查询发给携入方的错误信息
//        IDataset npSoaLogDataset = null;TradeNpQry.queryNpSoaLog(param.getString("SERIAL_NUMBER"),page);
//		if (npSoaLogDataset != null && npSoaLogDataset.size()>0) {
//		    npsoalogData.put("NPSOALOG_INFO", "发给携入方的效验信息："+npSoaLogDataset.getData(0).getString("RESULT_INFO"));
//		}
    
        //查询用户的实时费用
//        IData userOweFee =null;
////        IData npMsg = new DataMap();
//////        userOweFee = AcctCall.queryOweFee(param.getString("SERIAL_NUMBER"));
////        userOweFee = AcctCall.getOweFeeByUserId(userId);
////        if(userOweFee.size()>0){
////            long oweFee1 = userOweFee.getLong("ACCT_BALANCE");//实时费用
////            if(oweFee1 != 0l ){
////                npMsg.put("ALERT_INFO", "您的号码当前有"+Math.abs((oweFee1))/100.0+"元费用尚未缴清影响携号转网办理");
////                alertInfo.add(npMsg);
////            }
////        }
        //查询用户状态是否待激活
        IDataset acctTagData =null;
        IData acctData = new DataMap();
        acctTagData = UserInfoQry.getUserInfoByAcctTag(userId,"2");
        if(acctTagData!=null && acctTagData.size()>0){
        	acctData.put("ALERT_INFO", "申请业务的号码是已停机的号码或已挂失的号码！");
        	alertInfo.add(acctData);
        	
        	IData resultInfo = new DataMap();
			resultInfo.put("RESULT_CODE", "2009");
			resultInfo.put("RESULT_DESC", "申请携号转网的号码处于停机或挂失的非正常使用状态。");
			resultList.add(resultInfo);
        }
	    //查询用户营销活动有效期
	    IDataset cparamData =null;
	    IData paramData = new DataMap();
	    IData cparam = new DataMap();

	    cparamData = CParamQry.getPurchaseUserBNew(userId,"99","0",strCreditClass);
	    if(cparamData == null || cparamData.size()==0){
	    	cparamData = CParamQry.getPurchaseUserANew(userId,"99","0",strCreditClass);
	    }
        logger.debug("======QueryNpMessageBean======cparamData=" + cparamData);
        if(cparamData!=null && cparamData.size()>0){
            String campnMsg = "";
            String campnDate = "";
            for(int i=0;i<cparamData.size();i++){
                cparam = cparamData.getData(i);
                String productName = cparam.getString("PRODUCT_NAME");
                String endDate = cparam.getString("END_DATE");
                if("".equals(campnMsg) || "".equals(campnDate)){
                    campnMsg = productName;
                    campnDate = endDate;
                }else{
                    campnMsg = campnMsg+"，"+ productName;
                    campnDate = campnDate+"，"+ endDate;
                }
                paramData = new DataMap();
                paramData.put("ALERT_INFO", "您的号码有" + productName + "（到期时间为" + endDate + "）影响携号转网办理");
                alertInfo.add(paramData);
            }

            IData Data = GetMaxEndDate(cparamData);
            String date = "20501231235959";
            if(IDataUtil.isNotEmpty(Data)){
                date = Data.getString("END_DATE");
            }

            date = SysDateMgr.decodeTimestamp(date, SysDateMgr.PATTERN_STAND_SHORT);
            setLimitInfo("106", "营销活动("+campnMsg+")", date, "您的号码有" + campnMsg + "（到期时间为" + campnDate + "）影响携号转网办理", resultList, limitInfoLists);
        }
        //非签约营销活动
        IDataset newCparamData = CParamQry.getNewLimitActives(userId, "41");
        logger.debug("======QueryNpMessageBean======newCparamData=" + newCparamData);

        if (IDataUtil.isNotEmpty(newCparamData))
        {
            logger.debug("======QueryNpMessageBean======cparamData2=" + cparamData);
            for (Iterator<Object> iterator = newCparamData.iterator(); iterator.hasNext(); ) {
                IData newCparam = (IData) iterator.next();
                System.out.println("====QueryNpMessageBean==newCparam="+newCparam);

                for(int i=0;i<cparamData.size();i++){
                    cparam = cparamData.getData(i);
                    String productId = cparam.getString("PRODUCT_ID");
                    if (productId.equals(newCparam.getString("PRODUCT_ID"))) {
                        System.out.println("====QueryNpMessageBean==remove=cparamData："+cparamData);
                        iterator.remove();//去重
                        break;
                    }
                }
            }
            logger.debug("======QueryNpMessageBean======newCparamData2=" + newCparamData);

            if (IDataUtil.isNotEmpty(newCparamData)) {
                String campnMsg = "";
                String campnDate = "";
                for (int i = 0; i < newCparamData.size(); i++) {
                    cparam = newCparamData.getData(i);
                    String productName = cparam.getString("PRODUCT_NAME");
                    String endDate = cparam.getString("END_DATE");
                    if ("".equals(campnMsg) || "".equals(campnDate)) {
                        campnMsg = productName;
                        campnDate = endDate;
                    } else {
                        campnMsg = campnMsg + "，" + productName;
                        campnDate = campnDate + "，" + endDate;
                    }
                    paramData = new DataMap();
                    paramData.put("ALERT_INFO", "您的号码有" + productName + "（到期时间为" + endDate + "）影响携号转网办理");
                    alertInfo.add(paramData);
                }

                IData Data = GetMaxEndDate(newCparamData);
                String date = "20501231235959";
                if (IDataUtil.isNotEmpty(Data)) {
                    date = Data.getString("END_DATE");
                }

                date = SysDateMgr.decodeTimestamp(Data.getString("END_DATE"), SysDateMgr.PATTERN_STAND_SHORT);
                setLimitInfo("107", "非签约营销活动(" + Data.getString("PRODUCT_NAME") + ")", date, "您的号码有" + Data.getString("PRODUCT_NAME") + "（到期时间为" + Data.getString("END_DATE") + "）影响携号转网办理", resultList, limitInfoLists);
            }
        }

        //查询用户是否正常非欠费
        IDataset abnormality =null;
        IData creditData =new DataMap();
        abnormality = CParamQry.getAbnormalityUser(userId);
        if(abnormality!=null && abnormality.size()>0){
            creditData.put("ALERT_INFO", "该用户处在非正常开通（高额停机、报停...）状态！");
            alertInfo.add(creditData);

            IData resultInfo = new DataMap();
            resultInfo.put("RESULT_CODE", "2009");
            resultInfo.put("RESULT_DESC", "申请携号转网的号码处于（高额停机、报停...）的非正常使用状态。");
            resultList.add(resultInfo);
        }
        //查询用户是否挂失
        IDataset stopData =null;
        IData stpData =new DataMap();
        stopData = CParamQry.getAbnormalityUserA(userId);
        if(stopData!=null && stopData.size()>0){
        	stpData.put("ALERT_INFO", "该用户已经挂失！");
        	alertInfo.add(stpData);
        	
        	IData resultInfo = new DataMap();
			resultInfo.put("RESULT_CODE", "2009");
			resultInfo.put("RESULT_DESC", "申请携号转网的号码处于挂失的非正常使用状态。");
			resultList.add(resultInfo);
        }

        //可以重复申请授权码
        //查询用户状态是否携出中
//        IDataset npOutData =null;
//        IData outData =new DataMap();
//        npOutData = CParamQry.getXieChuIng(userId);
//        if(npOutData!=null && npOutData.size()>0 && !"AUTHCODE_REQ".equals(param.getString("COMMANDCODE"))){
//        	outData.put("ALERT_INFO", "用户处在携出中状态！");
//            alertInfo.add(outData);
//
//            IData resultInfo = new DataMap();
//			resultInfo.put("RESULT_CODE", "2009");
//			resultInfo.put("RESULT_DESC", "申请携号转网的号码处于携出中的非正常使用状态。");
//			resultList.add(resultInfo);
//        } else {
//            // 判断用户是否是最终用户
//            IDataset lastDataSet =null;
//            IData lastData = new DataMap();
//            param.put("CUST_ID", userInfo.getData(0).getString("CUST_ID"));
//            lastDataSet = ResCall.getMphoneCodeInfoByResNo(param.getString("SERIAL_NUMBER"), "1");
//            if(lastDataSet==null || lastDataSet.size() == 0){
//            	lastData.put("ALERT_INFO", "实名制用户传入证件号码与系统不相符!");
//            	alertInfo.add(lastData);
//            }
//        }
        //重复
//        //查询用户携转是否超过120天
//        IDataset npData =null;
//        IData userNpData =new DataMap();
//        npData = CParamQry.getXieChuIngA(userId);
//        if(npData!=null && npData.size()>0){
//        	userNpData.put("ALERT_INFO", "用户处在携转不满120天！");
//            alertInfo.add(userNpData);
//        }  
        //REQ201412150017 取消吉祥号码客户携出拦截限制
        boolean bSwitch = true;//携出拦截限制开关，默认拦截
        IDataset commpara2016 = CommparaInfoQry.getCommparaCode1("CSM", "2016", "SEL_OUTNP_SWITCH", CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(commpara2016)){
        	String strSwitch = commpara2016.getData(0).getString("PARA_CODE1", "1");
        	if( strSwitch.equals("0") ){
        		bSwitch = false;//设置取消携出拦截限制
        	}
        }
        if(bSwitch){
	        // 查询用户是否办理特殊优惠
	        IDataset discntData =null;
	        IData disData =new DataMap();
	        discntData = ResCall.getMphonecodeInfo(serialNumber,"1");
	        if(discntData!=null&&discntData.size()>0){
	        	String  beautifual_tag = ((IData)discntData.get(0)).getString("BEAUTIFUAL_TAG");
	        	if("1".equals(beautifual_tag)){
                    disData.put("ALERT_INFO", "用户存在特殊优惠限制！");
                    alertInfo.add(disData);
	            }
	        }
        }

        //查询用户是否办理非集团优惠 add by panyu5
	    IDataset spDiscntData = null;
	    IData spDisData = new DataMap();

		spDiscntData =  UserDiscntInfoQry.getUserDisntsBylimitNpWithCreditClass(userId, "0898", strCreditClass);
		if((spDiscntData!=null && spDiscntData.size()>0)){
//			boolean Flag = false;
//			for(int i=0;i<spDiscntData.size();i++) {
//				IData data = spDiscntData.getData(i);
//				String para_code3 = data.getString("PARA_CODE3", "");
//				if (para_code3 != null && !para_code3.equals("1")) {
//					Flag = true;//存在非集团优惠
//					break;
//				}
//			}
//
//			if(Flag){
            String discntName = "";
            IData spDiscnt = new DataMap();
            for(int i=0;i<spDiscntData.size();i++){
                spDiscnt = spDiscntData.getData(i);
                if("".equals(discntName)){
                    discntName =spDiscnt.getString("DISCNT_NAME");
                }else{
                    discntName = discntName+"，"+spDiscnt.getString("DISCNT_NAME");
                }
            }
            IData data = GetMaxEndDate(spDiscntData);
            spDisData.put("ALERT_INFO", "您的号码有" + discntName + "影响携号转网办理");
            alertInfo.add(spDisData);

            String date = SysDateMgr.decodeTimestamp(data.getString("END_DATE"), SysDateMgr.PATTERN_STAND_SHORT);
            setLimitInfo("109", data.getString("DISCNT_NAME"), date, "您的号码有" + discntName + "影响携号转网办理", resultList, limitInfoLists);
//			}
//			else{
//				spDisData.put("ALERT_INFO", "该客户存在集团网优惠套餐限制！");
//				alertInfo.add(spDisData);
//
//				String date = SysDateMgr.decodeTimestamp(spDiscntData.getData(0).getString("END_DATE"), SysDateMgr.PATTERN_STAND_SHORT);
//				setLimitInfo("110", spDiscntData.getData(0).getString("DISCNT_NAME"), date, "该客户存在集团网优惠套餐限制！", resultList, limitInfoLists);
//			}
		}
		// 查询用户是否办理特殊服务，目前生产无该配置
		IDataset svcData =null;
		IData serviceData =new DataMap();
		svcData = UserSvcInfoQry.getUserSvcsByLimitNp(userId,"0898");
		if(svcData!=null && svcData.size()>0){
			serviceData.put("ALERT_INFO", "用户存在特殊服务限制！");
			alertInfo.add(serviceData);
		}

		// 查询用户是否是集团v网用户
//		IDataset vpmnDataSet =null;
//		IData vpmnData =new DataMap();
//		vpmnDataSet = UserSaleActiveInfoQry.queryVpmnActiveRelationsByUserId(userId);
//		if(vpmnDataSet!=null && vpmnDataSet.size()>0){
//			vpmnData.put("ALERT_INFO", "该号码有集团V网业务，需解除后方可办理!");
//			alertInfo.add(vpmnData);
//		}
        
	    IDataset custInfos = CustomerInfoQry.getNormalCustInfoByUserId(userId);
	    if(!"1".equals(custInfos.getData(0).getString("IS_REAL_NAME"))){
	    	IData realName = new DataMap();
    		realName.put("ALERT_INFO", "非实名的客户，不允许携出");
        	alertInfo.add(realName);
        	
        	IData resultInfo = new DataMap();
			resultInfo.put("RESULT_CODE", "2031");
			resultInfo.put("RESULT_DESC", "申请携号转网的号码未在携出方办理真实身份信息登记。");
			resultList.add(resultInfo);
	    }
        IDataset riskInfo = new DatasetList();
        checkAutoStop(userInfo.getData(0), alertInfo, riskInfo);
        returnData.put("infos", alertInfo);
        returnData.put("riskInfo", riskInfo);
//        returnData.put("remind", remindData);
//        returnData.put("npsoalog", npsoalogData);
//        returnData.put("custinfo", custData);
        return returnData;   	
    }
    
    public IDataset queryNpOutSets(IData param, Pagination page) throws Exception
    {
       	SQLParser sql=new SQLParser(param);
		sql.addSQL(" SELECT t.para_code1 COP_ID, t.para_code2 COP_NAME, t1.para_code1 RULE_ID, t1.para_code2 RULE_NAME  ");
		sql.addSQL(" FROM TD_S_COMMPARA_NOCACHE t,TD_S_COMMPARA_NOCACHE t1 ");
		sql.addSQL(" where t.subsys_code='CSM' ");
		sql.addSQL(" and t.param_attr='1' ");
		sql.addSQL(" and t.param_code='NPCOPRULE' ");
		sql.addSQL(" and t1.subsys_code='CSM' ");
		sql.addSQL(" and t1.param_attr='0' ");
		sql.addSQL(" and t1.param_code='NPOUTRULE' ");
		sql.addSQL(" and SYSDATE BETWEEN t1.START_DATE AND t1.END_DATE ");
		sql.addSQL(" and t.para_code3=t1.para_code1 ");
    	return Dao.qryByParse(sql,page,Route.CONN_CRM_CEN);
    }
    
    public IDataset queryParams(IData param, Pagination page) throws Exception
    {
       	SQLParser sql=new SQLParser(param);
		sql.addSQL(" SELECT t.* FROM TD_S_COMMPARA_NOCACHE t ");
		sql.addSQL(" where t.subsys_code='CSM' ");
		sql.addSQL(" and t.param_attr=:PARAM_ATTR ");
		sql.addSQL(" and t.param_code=:PARAM_CODE ");
		sql.addSQL(" and SYSDATE BETWEEN t.START_DATE AND t.END_DATE ");
    	return Dao.qryByParse(sql,page,Route.CONN_CRM_CEN);
    }
    
    public void setNpOutSets(IData param) throws Exception
    {
		
    	SQLParser sql1=new SQLParser(param);
    	sql1.addSQL(" update TD_S_COMMPARA_NOCACHE t  ");
    	sql1.addSQL(" set t.PARA_CODE3=:PARA_CODE3, ");
    	sql1.addSQL("     t.UPDATE_TIME=SYSDATE, ");
    	sql1.addSQL("     t.UPDATE_STAFF_ID= :UPDATE_STAFF_ID, ");
    	sql1.addSQL("     t.UPDATE_DEPART_ID= :UPDATE_DEPART_ID ");
    	sql1.addSQL(" where t.subsys_code='CSM' ");
    	sql1.addSQL(" and t.param_attr='1' ");
    	sql1.addSQL(" and t.param_code = 'NPCOPRULE' ");
    	sql1.addSQL(" and t.para_code1 = :PARA_CODE1 ");
		int flg = Dao.executeUpdate(sql1, Route.CONN_CRM_CEN);
		if (flg <= 0) {
			CSAppException.appError("CRM_USER_NP_9529", "更新规则参数错误！");
		}

    }
    
    
    public String queryNpOutSet(String userId) throws Exception
    {
    	String npOutSet = "1";
    	return npOutSet;
    }
    
    public String queryNpOutSetSql (IData param) throws Exception
    {
    	String npOutSet = "";
    	if ("".equals(param.getString("DEPART_ID"))) {
    		return npOutSet;
    	}
		SQLParser sql=new SQLParser(param);
		sql.addSQL(" SELECT t.para_code3 RULE_ID  ");
		sql.addSQL(" FROM TD_S_COMMPARA_NOCACHE t,TD_M_DEPART t1 ");
		sql.addSQL(" where t.subsys_code='CSM' ");
		sql.addSQL(" and t.param_attr='1' ");
		sql.addSQL(" and t.param_code='NPCOPRULE' ");
		sql.addSQL(" and t1.depart_id=:DEPART_ID ");
		sql.addSQL(" and t.para_code1=substr(t1.depart_frame,6,5) ");
		IDataset rules = Dao.qryByParse(sql,null,Route.CONN_CRM_CEN);
		if (IDataUtil.isNotEmpty(rules)) {
			npOutSet = rules.getData(0).getString("RULE_ID");
		}
    	return npOutSet;

    }
    
    
    public String queryTrafficArea(IData param) throws Exception
    {
    	String deaprtId = "";
    	if ("".equals(param.getString("USER_ID"))) {
    		return deaprtId;
    	}
		SQLParser sql=new SQLParser(param);
		sql.addSQL(" SELECT TRAFFIC_AREA FROM UCR_CRM1.TI_F_CUST_AREA_MAN_DAY B  ");
		sql.addSQL(" WHERE B.CYCLE_DAY IN   ");
		sql.addSQL(" (SELECT MAX(A.CYCLE_DAY) FROM UCR_CRM1.TI_F_CUST_AREA_MAN_DAY A WHERE A.USER_ID = :USER_ID) ");
		sql.addSQL(" AND B.USER_ID = :USER_ID ");
		IDataset areas = Dao.qryByParse(sql);
		if (IDataUtil.isNotEmpty(areas)) {
			param.put("DATA_ID", areas.getData(0).getString("TRAFFIC_AREA"));
			SQLParser sql1=new SQLParser(param);
			sql1.addSQL(" SELECT t.pdata_id ");
			sql1.addSQL(" FROM TD_S_STATIC t ");
			sql1.addSQL(" where t.type_id='CUSTSUMMARY_CELLAREA' ");
			sql1.addSQL(" and t.data_id= :DATA_ID ");
			IDataset departs = Dao.qryByParse(sql1,null,Route.CONN_CRM_CEN);
			if (IDataUtil.isNotEmpty(departs)) {
				deaprtId = departs.getData(0).getString("PDATA_ID", "");
			}
		}
    	return deaprtId;

    }
    
    // 2011年6月1日前激活的号码可以携出、过户、销号
    private boolean checkDateLimit(String userId) throws Exception
    {
    	IDataset userInfo = UserInfoQry.selUserInfo(userId);
    	String strStart = null;
    	if (IDataUtil.isNotEmpty(userInfo)) {
    		strStart = userInfo.getData(0).getString("OPEN_DATE");
    	}
    	if (StringUtils.isBlank(strStart)) {
    		return false;
    	}
        Calendar cEnd = BreTimeUtil.setCalendar(Calendar.getInstance(), "2011-06-01");
        Calendar cStart = BreTimeUtil.setCalendar(Calendar.getInstance(), strStart);
        if (cStart.after(cEnd)) {
            return true;
        }

        return false;
    }
    
    @SuppressWarnings("unchecked")
	private void checkAutoStop(IData userInfo, IDataset alertInfo, IDataset riskInfo) throws Exception {
        String userId = userInfo.getString("USER_ID");
        String serialNumber = userInfo.getString("SERIAL_NUMBER");
        // 实时欠费不允许携出
        UcaData ucaData = UcaDataFactory.getUcaByUserId(userId);
        IData oweFee = AcctCall.getOweFeeByUserId(userId);
        if (IDataUtil.isNotEmpty(oweFee)) {
            long acctBalance = oweFee.getLong("ACCT_BALANCE");//实时结余
            if (acctBalance < 0 && "0".equals(ucaData.getAccount().getPayModeCode())) {
                IData balance = new DataMap();
                balance.put("ALERT_INFO", "您的号码当前有" + Math.abs((acctBalance)) / 100.0 + "元费用尚未缴清影响携号转网办理");
                alertInfo.add(balance);
            }
        }
        String tradeInfo = "";


        //一卡多号副号不允许携出
        IDataset dataset2 = RelaUUInfoQry.qryRelaBySerialNumberBAndRelationTypeCode("M2", serialNumber, null);
        if (IDataUtil.isNotEmpty(dataset2)) {
            if (StringUtils.isNotBlank(tradeInfo)) {
                if (tradeInfo.indexOf("一卡多号") > -1) {
                    //已包含一卡多号副号
                } else {
                    tradeInfo += "|一卡多号";
                }
            } else {
                tradeInfo = "一卡多号";
            }
        }

        //一代TD固话绑定的157号码不允许携出
        IDataset dataset3 = RelaUUInfoQry.check_byuserida_idbzm_A(userId, "T2", null, null);
        if (IDataUtil.isNotEmpty(dataset3)) {
            if (StringUtils.isNotBlank(tradeInfo)) {
                if (tradeInfo.indexOf("固移融合") > -1) {
                    //已包含一代TD固话绑定的157号码
                } else {
                    tradeInfo += "|固移融合";
                }
            } else {
                tradeInfo = "固移融合";
            }
        }

        //一号一终端号码不允许携出
        String simCardNo = "";
        IDataset userRes = UserResInfoQry.getUserResInfoByUserId(userId);
        logger.debug("业务限制userInfos：" + userRes);
        if (IDataUtil.isNotEmpty(userRes)) {
            for (int i = 0; i < userRes.size(); i++) {
                if ("1".equals(userRes.getData(i).getString("RES_TYPE_CODE"))) {
                    simCardNo = userRes.getData(i).getString("RES_CODE");
                }
            }

        }
        logger.debug("业务限制simCardNo：" + simCardNo);
        if (StringUtils.isNotEmpty(simCardNo)) {
            IDataset simCardInfo = ResCall.getSimCardInfo("0", simCardNo, "", "1", "");
            logger.debug("业务限制simCardInfo：" + simCardInfo);
            if (IDataUtil.isNotEmpty(simCardInfo)) {
                String res_kind_code = simCardInfo.getData(0).getString("RES_KIND_CODE");
                logger.debug("业务限制res_kind_code：" + res_kind_code);
                if (StringUtils.equals("10F1", res_kind_code)) {
                    if (StringUtils.isNotBlank(tradeInfo)) {
                        if (tradeInfo.indexOf("一号一终端") > -1) {
                            //已包含一号一终端号码
                        } else {
                            tradeInfo += "|一号一终端";
                        }
                    } else {
                        tradeInfo = "一号一终端";
                    }
                }
            }
        }


        //托收
        IData acct = UcaInfoQry.qryAcctInfoByUserId(userId);
        IDataset ids = new DatasetList();
        if (IDataUtil.isNotEmpty(acct)) {
            ids = PayRelaInfoQry.getAllUserPayRelationByAcctId(acct.getString("ACCT_ID"));
            if (IDataUtil.isNotEmpty(ids) && ids.size() > 1) {
                if (StringUtils.isNotBlank(tradeInfo)) {
                    if (tradeInfo.indexOf("托收") > -1) {
                        //已包含托收
                    } else {
                        tradeInfo += "|托收";
                    }
                } else {
                    tradeInfo = "托收";
                }
            }
        }

        ids = RelaUUInfoQry.getRelationUusByUserSnRole(serialNumber, "45", "2");
        if (IDataUtil.isEmpty(ids))
        {
            ids = RelaUUInfoQry.getRelaUUInfoByRol(userId, "45");

            if(!IDataUtil.isEmpty(ids))
            {
                if (StringUtils.isNotBlank(tradeInfo))
                {
                    if(tradeInfo.indexOf("家庭网") > -1){
                        //已包含家庭网
                    }else {
                        tradeInfo += "|家庭网";
                    }
                }
                else
                {
                    tradeInfo = "家庭网";
                }
            }
        }

        //未完结的工单
        ids = CParamQry.getUndoneTrade41(userId);
        if (IDataUtil.isNotEmpty(ids))
        {

//            if (StringUtils.isNotBlank(code))
//            {
//                code += "|699";
//                resultInfo += "|有未完工工单，不允许携出!";
//            }
//            else
//            {
//                code = "699";
//                resultInfo = "有未完工工单，不允许携出!";
//            }
            if (StringUtils.isNotBlank(tradeInfo))
            {
                if(tradeInfo.indexOf("未完结的工单") > -1){
                    //已包含未完结的工单
                }else {
                    tradeInfo += "|未完结的工单";
                }
            }
            else
            {
                tradeInfo = "未完结的工单";
            }
        }

        //一卡付多号业务 add by panyu5
        //主副卡
        IDataset payMoreCards = RelaUUInfoQry.getRelationUusByUserSnRole(serialNumber, "97", "2");
        if (IDataUtil.isNotEmpty(payMoreCards)) {
//			IData payMoreCard = new DataMap();
//			payMoreCard.put("ALERT_INFO", "用户在携出方使用了一卡付多号副卡业务，不允许携出");
//          alertInfo.add(payMoreCard);
            if (StringUtils.isNotBlank(tradeInfo)) {
                if (tradeInfo.indexOf("主副卡") > -1) {
                    //已包含主副卡
                } else {
                    tradeInfo += "|主副卡";
                }
            } else {
                tradeInfo = "主副卡";
            }
        }
        //一卡双号副卡业务 add by panyu5
        IDataset payDoubleCards = RelaUUInfoQry.getRelationUusByUserSnRole(serialNumber, "30", "2");
        if (IDataUtil.isNotEmpty(payDoubleCards)) {
//			IData payDoubleCard = new DataMap();
//			payDoubleCard.put("ALERT_INFO", "用户在携出方使用了一卡双号副卡业务，不允许携出");
//			alertInfo.add(payDoubleCard);
            if (StringUtils.isNotBlank(tradeInfo)) {
                if (tradeInfo.indexOf("一卡双号") > -1) {
                    //已包含一卡双号
                } else {
                    tradeInfo += "|一卡双号";
                }
            } else {
                tradeInfo = "一卡双号";
            }
        }
        //双卡统一付费 add by panyu5
        //主副卡
        IDataset payOneCards = RelaUUInfoQry.getRelationUusByUserSnRole(serialNumber, "34", "2");
        if (IDataUtil.isNotEmpty(payOneCards)) {
//			IData payOneCard = new DataMap();
//			payOneCard.put("ALERT_INFO", "用户在携出方使用了双卡统一付费副卡业务，不允许携出");
//			alertInfo.add(payOneCard);
            if (StringUtils.isNotBlank(tradeInfo)) {
                if (tradeInfo.indexOf("主副卡") > -1) {
                    //已包含主副卡
                } else {
                    tradeInfo += "|主副卡";
                }
            } else {
                tradeInfo = "主副卡";
            }
        }
        UcaData ucaDataTemp = UcaDataFactory.getUcaByUserId(userId);
        IDataset days = CommparaInfoQry.getCommPkInfo("CSM", "170", "0", "0898");//现在已不做该限制，配置时间为0天
        String userTagSet = ucaDataTemp.getUser().getUserTagSet();
        if (!"1".equals(userTagSet) && !"6".equals(userTagSet)) {//非携入号码
            int para_code1 = 120;
            if (IDataUtil.isNotEmpty(days)) {
                para_code1 = days.getData(0).getInt("PARA_CODE1", 120);// 默认为120天

            }
            int day = SysDateMgr.dayInterval(ucaDataTemp.getUser().getOpenDate(), SysDateMgr.getSysDate());
            if (para_code1 > day) {
                IData Day = new DataMap();
                Day.put("ALERT_INFO", "号码入网激活后的时间未达到允许携转的法规规定值，不允许携出");//现在已不做该限制，配置时间为0天
                alertInfo.add(Day);
            }
        }
        //固移融合
        IDataset WideUsers = UserInfoQry.getWideUsersBySerialNumber(serialNumber);
        if (IDataUtil.isNotEmpty(WideUsers)) {
//			IData WideUser = new DataMap();
//			WideUser.put("ALERT_INFO", "用户在携出方使用了影响其他号码付费或资费套餐使用的业务，不允许携出");
//			alertInfo.add(WideUser);
            if (StringUtils.isNotBlank(tradeInfo)) {
                if (tradeInfo.indexOf("固移融合") > -1) {
                    //已包含固移融合
                } else {
                    tradeInfo += "|固移融合";
                }
            } else {
                tradeInfo = "固移融合";
            }
        }
        //统付
        IDataset RelationUus = RelaUUInfoQry.getRelationUusByUserIdBTypeCode(userId, "56");
        if (IDataUtil.isNotEmpty(RelationUus)) {
//			IData RelationUu = new DataMap();
////			RelationUu.put("ALERT_INFO", "用户在携出方使用了影响其他号码付费或资费套餐使用的业务");
////			alertInfo.add(RelationUu);
            if (StringUtils.isNotBlank(tradeInfo)) {
                if (tradeInfo.indexOf("主副卡") > -1) {
                    //已包含主副卡
                } else {
                    tradeInfo += "|主副卡";
                }
            } else {
                tradeInfo = "主副卡";
            }
        }
        //固移融合
        IDataset WindTrades = TradeInfoQry.getWindTradeInfoBySn("KD_" + serialNumber);
        if (IDataUtil.isNotEmpty(WindTrades)) {
            IData WindTrade = new DataMap();
//			WindTrade.put("ALERT_INFO", "该用户处在营销活动有效期内：该手机用户有宽带用户不能办理手机号码携转出网");
//          alertInfo.add(WindTrade);
            if (StringUtils.isNotBlank(tradeInfo)) {
                if (tradeInfo.indexOf("固移融合") > -1) {
                    //已包含固移融合
                } else {
                    tradeInfo += "|固移融合";
                }
            } else {
                tradeInfo = "固移融合";
            }
        }
        //固移融合
        IDataset wideTypes = IDataUtil.idToIds(UcaInfoQry.qryUserInfoBySn("KD_" + serialNumber));
        if (IDataUtil.isNotEmpty(wideTypes)) {
            IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_" + serialNumber);
            IData data = GetMaxEndDate(widenetInfos);
            String product_name = "";
            String wideType = data.getString("RSRV_STR2");
            if ("1".equals(wideType)) {
                product_name = "GPON宽带开户业务";
            } else if ("2".equals(wideType)) {
                product_name = "ADSL宽带开户业务";
            } else if ("3".equals(wideType)) {
                product_name = "FTTH宽带开户业务";
            } else {
                product_name = "未知宽带业务";
            }

//            IData wide = new DataMap();
//            wide.put("ALERT_INFO", "该用户处在"+ product_name + "有效期内, " + data.getString("END_DATE") + "到期，不允许携出");
//            alertInfo.add(wide);
            if (StringUtils.isNotBlank(tradeInfo)) {
                if (tradeInfo.indexOf("固移融合") > -1) {
                    //已包含固移融合
                } else {
                    tradeInfo += "|固移融合";
                }
            } else {
                tradeInfo = "固移融合";
            }

        }

        //单方过户
        IDataset custInfos = CustomerInfoQry.getCustomerInfoByUserId(userId);
        IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custInfos.getData(0).getString("CUST_ID"));
        String specialCustDate = custInfo.getString("RSRV_DATE1", "");

        if (!specialCustDate.trim().equals("")) {
            //终止时间
            String validEndDate = SysDateMgr.addYears(specialCustDate, 2);

            //当前时间
            String curTime = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);

            if (curTime.compareTo(validEndDate) <= 0) {
                IData data = new DataMap();
                data.put("ALERT_INFO", "您的号码有单方过户（到期时间为" + validEndDate + "）影响携号转网办理");
                alertInfo.add(data);
            }
        }
//        IDataset GropMember = BreQry.getGropMemberInfoByUserId(userId);
//        if (IDataUtil.isNotEmpty(GropMember)) {
//            IData data = new DataMap();
//            data.put("ALERT_INFO", "您的号码有集团成员，影响携号转网办理");
//
//            //风险提示
//            IData riskData = new DataMap();
//            riskData.put("RISK_REMINDER", "集团网");
//            riskInfo.add(riskData);
//
//            alertInfo.add(data);
//        }
        //海洋通船东成员不允许携出
        IDataset idataset = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "HYT");
        if (IDataUtil.isNotEmpty(idataset)) {
            if ("1".equals(idataset.getData(0).getString("RSRV_STR2"))) {
                IData data = new DataMap();
                data.put("ALERT_INFO", "您的号码有海洋通船东用户，影响携号转网办理");
                alertInfo.add(data);
            }
        }

        //REQ201911280002 1125关于做好携号转网服务用户交互规范工作的通知（更新版）
        //一号多终端OM
        ids = RelaUUInfoQry.getRelationUusByUserIdBTypeCode(userId, "OM");
        if (IDataUtil.isNotEmpty(ids)){
            if (StringUtils.isNotBlank(tradeInfo)) {
                if (tradeInfo.indexOf("一号多终端") > -1) {
                    //已包含一号多终端
                } else {
                    tradeInfo += "|一号多终端";
                }
            } else {
                tradeInfo = "一号多终端";
            }
        }else {
            ids = RelaUUInfoQry.getUserRelationByUserIDA(userId,"OM");
            if (IDataUtil.isNotEmpty(ids)){
                if (StringUtils.isNotBlank(tradeInfo)) {
                    if (tradeInfo.indexOf("一号多终端") > -1) {
                        //已包含一号多终端
                    } else {
                        tradeInfo += "|一号多终端";
                    }
                } else {
                    tradeInfo = "一号多终端";
                }
            }
        }

        //全国亲情网（群主）MF
        ids = RelaUUInfoQry.getRelationUusByUserSnRole(serialNumber, "MF", "1");
        if (IDataUtil.isNotEmpty(ids)){
            if (StringUtils.isNotBlank(tradeInfo)) {
                if (tradeInfo.indexOf("家庭网") > -1) {
                    //已包含家庭网
                } else {
                    tradeInfo += "|家庭网";
                    //风险提示
                    IData data = new DataMap();
                    data.put("RISK_REMINDER", "家庭网");
                    riskInfo.add(data);
                }
            } else {
                tradeInfo = "家庭网";
                //风险提示
                IData data = new DataMap();
                data.put("RISK_REMINDER", "家庭网");
                riskInfo.add(data);
            }
        }

        //共享业务（群主）
        ids = ShareInfoQry.queryMemberRela(userId,"01");
        if (IDataUtil.isNotEmpty(ids)){
            if (StringUtils.isNotBlank(tradeInfo)) {
                if (tradeInfo.indexOf("共享业务") > -1) {
                    //已包含共享业务
                } else {
                    tradeInfo += "|共享业务";
                    //风险提示
                    IData data = new DataMap();
                    data.put("RISK_REMINDER", "共享业务");
                    riskInfo.add(data);
                }
            } else {
                tradeInfo = "共享业务";
                //风险提示
                IData data = new DataMap();
                data.put("RISK_REMINDER", "共享业务");
                riskInfo.add(data);
            }
        }
        //END REQ201911280002 1125关于做好携号转网服务用户交互规范工作的通知（更新版）

        if (StringUtils.isNotBlank(tradeInfo)) {
            IData data = new DataMap();
            tradeInfo = tradeInfo.replace("|", "、");
            String msg = "携号转网将影响您办理的" +  tradeInfo + "，请在申请携号转网前做好相关业务变更";
            data.put("ALERT_INFO", msg);
            alertInfo.add(data);
        }
    }
    
    public IDataset queryNpOutMessage(IData param) throws Exception {
    	String sn = IDataUtil.chkParam(param, "SERIAL_NUMBER");
    	IDataset resultlist = new DatasetList(); 
    	
    	String portBackId = "0";
    	String success = "尊敬的用户您好！您的号码满足携转条件，如需办理携号转网服务，请携带有效身份证件前往拟携入运营企业当地营业厅办理。受欠费、在网协议等因素影响，携转资格可能发生变化，建议办理前再次查询。";
    	//携入用户判断
    	IDataset userNpInfos = UserNpInfoQry.qryUserNpInfosBySn(sn);
        if (IDataUtil.isNotEmpty(userNpInfos))
        {
        	if("1".equals(userNpInfos.getData(0).getString("NP_TAG"))||"3".equals(userNpInfos.getData(0).getString("NP_TAG"))){        		
        		//快速携回标签判断
        		IDataset otherList = UserOtherInfoQry.checkPassChange(userNpInfos.getData(0).getString("USER_ID"),"QUICK_NP_SIGN");
        		if(IDataUtil.isNotEmpty(otherList) && "1".equals(otherList.getData(0).getString("RSRV_STR1"))){//存在快速携回标记
        			portBackId = "1";
        		}
        	}
        }
        if(logger.isDebugEnabled())
    	{
    		logger.debug("======进入QueryNpMessageBean======portBackId=" + portBackId);
    	}
        if("1".equals(portBackId)){//快速携回用户查询操作不做校验
        	IData result = new DataMap();
        	result.put("X_RESULTCODE", "0");
        	result.put("X_RESULTINFO", "成功");
        	result.put("RESULT_MESSAGE", success);
        	resultlist.add(result);
        	return resultlist;
        }
    	
        param.put("COMMANDCODE", "AUTHCODE_REQ");
        IDataset resultLists = new DatasetList();
        IDataset limitInfoLists = new DatasetList();
        param.put("RESULTINFO", resultLists);//用于一级能开接口做校验返回编码
        param.put("LIMITINFOLIST", limitInfoLists);//用于一级能开接口做校验返回编码

    	IData check = checkNpOutMessage(param);
    	int countcheck = Integer.parseInt(check.getString("CODE"));
    	IData npmg= queryNpMessage(param,null);
    	IDataset alertInfo = npmg.getDataset("infos");
    	IDataset riskInfo = npmg.getDataset("riskInfo");
    	int countalert = alertInfo.size();
    	String[] errordetails = check.getString("RESULT_DETAILS").split("@");
    	String errordetail = "";

    	//count记录报错和提示共多少条
    	int count = 0;  
    	for(int i=0;i<errordetails.length-1;i++){
    		errordetail += String.valueOf(i+1) + "、"+errordetails[i+1]+";";
    		count ++;
    	}
    	for(int i=0;i<countalert;i++){
    		count ++;
    		errordetail += String.valueOf(count)+"、"+alertInfo.getData(i).getString("ALERT_INFO")+";";		
    	}
    	if(count > 0){//存在无法携转信息    		
    		IData result = new DataMap();
    		String errorInfo = "";
    		if(countcheck > 0){    			
    			String[] errorInfos = check.getString("RESULT_MESSAGE").split("@");
    			errorInfo = errorInfos[1];
    		}else{
    			errorInfo = alertInfo.getData(0).getString("ALERT_INFO");
    		}
    		//短信内容拼接
    		String smsInfo = "尊敬的用户您好！";

            if(countalert==1 && errorInfo.endsWith("请在申请携号转网前做好相关业务变更")){
                smsInfo += errorInfo + "，详询客服电话10086或到当地营业厅咨询办理。";
            }
            else if(count >= 2){
    			smsInfo += "您的号码因"+ count +"个原因影响携号转网，回复Y获取原因详情，或咨询客服电话10086。";
    		}
    		else{
    		    smsInfo += errorInfo + "，详情请咨询客服电话10086。";
    		}
    		result.put("X_RESULTCODE", "-1");
    		result.put("X_RESULTINFO", "失败");
    		result.put("RESULT_MESSAGE", smsInfo);
    		result.put("RESULT_ERRORINFO", errordetail); 
    		result.put("RESULTINFO", param.getDataset("RESULTINFO"));//用于一级能开接口返回
    		result.put("LIMITINFOLIST", param.getDataset("LIMITINFOLIST"));//用于一级能开接口返回
    		resultlist.add(result);
    		return resultlist;
    	}else{
    		IData input = new DataMap();
        	input.put("X_RESULTCODE", "0");
        	input.put("X_RESULTINFO", "成功");
        	input.put("RESULT_MESSAGE", success);
        	resultlist.add(input);

            //不满足发送风险提示短信
            sendRiskReminderSMS(sn,riskInfo);
            System.out.println("==queryNpOutMessage==time"+SysDateMgr.getSysTime());

        	return resultlist;
        }        
    }
    
    //授权码查询接口校验用户查询信息 add by panyu5
	private IData checkNpOutMessage(IData param) throws Exception {
		IData result = new DataMap();
		IDataset resultList = param.getDataset("RESULTINFO");
		IDataset limitInfoLists = param.getDataset("LIMITINFOLIST");
		String abilityCheck = param.getString("ABILITY_CHECK","");
		
		int count = 0;
		String errorinfo = "",errordetails="",acctMode="0",saleFlag = "0";
		
		String crednum = "";
		String credname = "";
		if(!"true".equals(abilityCheck))
		{//能开处理不需要校验证件信息  add by dengyi5 20190904
			crednum = IDataUtil.chkParam(param, "CRED_NUMBER");
			credname = IDataUtil.chkParam(param, "CUST_NAME");
		}
		
		String sn = param.getString("SERIAL_NUMBER");


        //校验客户信息
        IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", sn);
        if(IDataUtil.isEmpty(userInfo)){
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "没有获取到用户信息！");
        }

        IData custData = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getData(0).getString("CUST_ID"));
        if(IDataUtil.isEmpty(custData)){
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "没有获取到客户信息！");
        }
        String psptType = custData.getString("PSPT_TYPE_CODE");
        if("3".equals(psptType) || "D".equals(psptType) || "E".equals(psptType) || "G".equals(psptType) || "L".equals(psptType) || "M".equals(psptType) || "C".equals(psptType))
        {
            errorinfo += "@您的号码为单位所有，暂无法办理携号转网";
            errordetails+="@您的号码为单位所有，暂无法办理携号转网";
            count++;

            IData resultInfo = new DataMap();
            resultInfo.put("RESULT_CODE", "3015");
            resultInfo.put("RESULT_DESC", "号码为单位所有，需过户至个人名下后办理携号转网。");
            resultList.add(resultInfo);

            //当用户号码登记在单位名下或实名登记证件为军官证、士兵证、警官证时，企业不需对其他申请条件进行判断
            result.put("CODE", count);
            result.put("RESULT_MESSAGE", errorinfo);
            result.put("RESULT_DETAILS", errordetails);

            param.put("NO_MORE_CHECK", "NO_MORE_CHECK");//不再对其他申请条件进行判断

            if(logger.isDebugEnabled())
            {
                logger.debug("======进入QueryNpMessageBean===checkNpOutMessage===result=" + result);
            }
            return result;
        }

        String userId = userInfo.getData(0).getString("USER_ID");
        IDataset ids = CParamQry.getRealNameUser(userId);
        if (!"true".equals(abilityCheck) && IDataUtil.isNotEmpty(ids)){
            //应具有兼容性，不得因查询短信模板各信息要素中字母大小写、空格等判断用户身份信息不正确。
            String psptId = ids.getData(0).getString("PSPT_ID").trim().toUpperCase();
            String custName = ids.getData(0).getString("CUST_NAME").trim().toUpperCase();
            crednum = crednum.replace(" ", "").toUpperCase();
            credname = credname.replace(" ", "").toUpperCase();
            boolean flag = true;

            if(psptId.length() == crednum.length()){//长度相同并且为15位或者18位
                if(!psptId.equals(crednum)&&custName.equals(credname)){
                    errorinfo += "@您提供的证件号码与登记信息不符，暂无法办理携转";
                    errordetails+="@您提供的证件号码与登记信息不符，暂无法办理携转";
                    count++;
                    flag = false;
                }else if(psptId.equals(crednum)&&!custName.equals(credname)){
                    errorinfo += "@您提供的用户名与登记信息不符，暂无法办理携转";
                    errordetails+="@您提供的用户名与登记信息不符，暂无法办理携转";
                    count++;
                    flag = false;
                }else if(!psptId.equals(crednum)&&!custName.equals(credname)){
                    errorinfo += "@您提供的用户名及证件号码与登记信息不符，暂无法办理携转";
                    errordetails+="@您提供的用户名及证件号码与登记信息不符，暂无法办理携转";
                    count++;
                    flag = false;
                }
            }else if(psptId.length() != crednum.length()){//长度不相符
                if(psptId.length() == 15 && crednum.length() == 18)//15和18
                {
                    if(!psptId.equals(crednum.substring(0,6) + crednum.substring(8,17))&&custName.equals(credname))
                    {
                        errorinfo += "@您提供的证件号码与登记信息不符，暂无法办理携转";
                        errordetails+="@您提供的证件号码与登记信息不符，暂无法办理携转";
                        count++;
                        flag = false;
                    }else if(psptId.equals(crednum.substring(0,6) + crednum.substring(8,17))&&!custName.equals(credname)){
                        errorinfo += "@您提供的用户名与登记信息不符，暂无法办理携转";
                        errordetails+="@您提供的用户名与登记信息不符，暂无法办理携转";
                        count++;
                        flag = false;
                    }else if(!psptId.equals(crednum.substring(0,6) + crednum.substring(8,17))&&!custName.equals(credname)){
                        errorinfo += "@您提供的用户名及证件号码与登记信息不符，暂无法办理携转";
                        errordetails+="@您提供的用户名及证件号码与登记信息不符，暂无法办理携转";
                        count++;
                        flag = false;
                    }
                }
                else if(psptId.length() == 18 && crednum.length() == 15)//18和15
                {
                    if(!crednum.equals(psptId.substring(0,6) + psptId.substring(8,17))&&custName.equals(credname))
                    {
                        errorinfo += "@您提供的证件号码与登记信息不符，暂无法办理携转";
                        errordetails+="@您提供的证件号码与登记信息不符，暂无法办理携转";
                        count++;
                        flag = false;
                    }else if(crednum.equals(psptId.substring(0,6) + psptId.substring(8,17))&&!custName.equals(credname)){
                        errorinfo += "@您提供的用户名与登记信息不符，暂无法办理携转";
                        errordetails+="@您提供的用户名与登记信息不符，暂无法办理携转";
                        count++;
                        flag = false;
                    }else if(!crednum.equals(psptId.substring(0,6) + psptId.substring(8,17))&&!custName.equals(credname)){
                        errorinfo += "@您提供的用户名及证件号码与登记信息不符，暂无法办理携转";
                        errordetails+="@您提供的用户名及证件号码与登记信息不符，暂无法办理携转";
                        count++;
                        flag = false;
                    }
                }
                else
                {
                    if(custName.equals(credname))
                    {
                        errorinfo += "@您提供的证件号码与登记信息不符，暂无法办理携转";
                        errordetails+="@您提供的证件号码与登记信息不符，暂无法办理携转";
                        count++;
                        flag = false;
                    }else{
                        errorinfo += "@您提供的用户名及证件号码与登记信息不符，暂无法办理携转";
                        errordetails+="@您提供的用户名及证件号码与登记信息不符，暂无法办理携转";
                        count++;
                        flag = false;
                    }
                }
            }

            if(!flag){
                //当用户号码登记在自然人名下但业务查询时提供的用户名和/或证件号码和该号码入网登记的实名信息不一致时，企业不需对其他申请条件进行判断
                result.put("CODE", count);
                result.put("RESULT_MESSAGE", errorinfo);
                result.put("RESULT_DETAILS", errordetails);

                param.put("NO_MORE_CHECK", "NO_MORE_CHECK");//不再对其他申请条件进行判断

                if(logger.isDebugEnabled())
                {
                    logger.debug("======进入QueryNpMessageBean===checkNpOutMessage===result=" + result);
                }
                return result;
            }
        }
		
		//1.号段校验   PARA_CODE2:号段类型,0-卫星移动,1-移动通信转售,2-物联网应用  目前仅存在这三类号码限制
		//由于号段存在4位，所以原逻辑做调整 add by dengyi5
		IDataset limitSnList = CommparaInfoQry.getCommparaInfos("CSM", "173", "LIMITSN");
		if(IDataUtil.isNotEmpty(limitSnList))
		{//存在号段限制配置
			for(int i=0; i<limitSnList.size();i++)
			{
				IData limitSn = limitSnList.getData(i);
				String snNum = limitSn.getString("PARA_CODE1");
				if(sn.startsWith(snNum))
				{
					count ++;
					errorinfo += "@您的号码暂不支持携号转网";
					errordetails+="@您的号码暂不支持携号转网";
					
					IData resultInfo = new DataMap();
					if("0".equals(limitSn.getString("PARA_CODE2")))
					{
						resultInfo.put("RESULT_CODE", "3059");
						resultInfo.put("RESULT_DESC", "号段属于卫星移动，不支持携号转网。");
					}
					if("1".equals(limitSn.getString("PARA_CODE2")))
					{
						resultInfo.put("RESULT_CODE", "3061");
						resultInfo.put("RESULT_DESC", "号段属于移动通信转售，不支持携号转网。");
					}
					if("2".equals(limitSn.getString("PARA_CODE2")))
					{
						resultInfo.put("RESULT_CODE", "3062");
						resultInfo.put("RESULT_DESC", "号段属于物联网，不支持携号转网。");
					}
					resultList.add(resultInfo);

                    result.put("CODE", count);
                    result.put("RESULT_MESSAGE", errorinfo);
                    result.put("RESULT_DETAILS", errordetails);

                    param.put("NO_MORE_CHECK", "NO_MORE_CHECK");//不再对其他申请条件进行判断

                    if(logger.isDebugEnabled())
                    {
                        logger.debug("======进入QueryNpMessageBean===checkNpOutMessage===result=" + result);
                    }
                    return result;
				}
			}
		}
		
    	IData payData = UcaInfoQry.qryPayRelaByUserId(userId);
        if(IDataUtil.isEmpty(payData)){
        	CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "用户没有付费关系记录！");
        }
    	
    	//2.您距离上次携转时间间隔未满120日，请您到期后再查询
//		IDataset ids = CParamQry.getXieChuIngA(userId);
//		if (IDataUtil.isNotEmpty(ids))
//        {
//			String enddate = ids.getData(0).getString("PORT_IN_DATE");//到期时间
//			enddate = SysDateMgr.addDays(enddate, 120);
//			enddate = enddate.substring(0,4)+"年"+enddate.substring(5, 7)+"月"+enddate.substring(8, 10)+"日";
//			errorinfo += "@您的号码距离上次携转时间未达规定值，暂无法办理携号转网，请您于"+enddate+"后再查询。";
//			errordetails+="@您的号码距离上次携转时间未达规定值，暂无法办理携号转网，请您于"+enddate+"后再查询。";
//			count++;
//
//			IData resultInfo = new DataMap();
//			resultInfo.put("RESULT_CODE", "3060");
//			resultInfo.put("RESULT_DESC", "您的号码距离上次携转时间未达规定值，暂无法办理携号转网，请您于"+enddate+"后再查询。");
//			resultList.add(resultInfo);
//        }


        /*
       	 * 修改上面判断过程，应支持“两次携转时间间隔”灵活配置。
         */
		ids = CParamQry.getXieChuIngB(userId);
		if (IDataUtil.isNotEmpty(ids)){
			String portInDate = ids.getData(0).getString("PORT_IN_DATE");//携入时间

			IDataset days = CommparaInfoQry.getCommPkInfo("CSM", "170", "1", "0898");//配置两次携转间隔时间，PARA_CODE1，单位为天
			int para_code1 = 120;
			if (IDataUtil.isNotEmpty(days))
			{
				para_code1 = days.getData(0).getInt("PARA_CODE1", 120);// 默认为120天

			}
			int day = SysDateMgr.dayInterval(portInDate, SysDateMgr.getSysDate());
			if (para_code1 > day)
			{
				String enddate = SysDateMgr.addDays(portInDate, para_code1);
				enddate = enddate.substring(0,4)+"年"+enddate.substring(5, 7)+"月"+enddate.substring(8, 10)+"日";
				errorinfo += "@您的号码距离上次携转时间未达规定值，暂无法办理携号转网，请您于"+enddate+"后再查询。";
				errordetails+="@您的号码距离上次携转时间未达规定值，暂无法办理携号转网，请您于"+enddate+"后再查询。";
                count++;

				IData resultInfo = new DataMap();
				resultInfo.put("RESULT_CODE", "3060");
				resultInfo.put("RESULT_DESC", "您的号码距离上次携转时间未达规定值，暂无法办理携号转网，请您于"+enddate+"后再查询。");
				resultList.add(resultInfo);
			}
		}

		//3.您有未解除的在网协议，协议名称：XXX，协议到期时间：XXX
//		IData acct = UcaInfoQry.qryAcctInfoByUserId(userInfo.getData(0).getString("USER_ID"));
//		if (IDataUtil.isNotEmpty(acct)){
//			ids = PayRelaInfoQry.getAllUserPayRelationByAcctId(acct.getString("ACCT_ID"));
//			if (IDataUtil.isNotEmpty(ids) && ids.size()>1 )
//			{
//				saleFlag = "1";
//				errorinfo += "@您在携出方使用了影响其他号码付费或资费套餐使用的业务。";
//				errordetails+="@您在携出方使用了影响其他号码付费或资费套餐使用的业务。";
//				count++;
//				setLimitInfo("101","在网协议托收", ids.getData(0).getString("END_CYCLE_ID")+"000000", "您在携出方使用了影响其他号码付费或资费套餐使用的业务。", resultList, limitInfoLists);
//			}
//		}
		
//		ids = UserSaleActiveInfoQry.queryVpmnActiveRelationsByUserId(userId);
//        if (IDataUtil.isNotEmpty(ids))
//        {
//        	saleFlag = "1";
//        	errorinfo += "@您处在集团V网双网有礼营销活动有效期内影响携转办理。";
//        	errordetails+="@您处在集团V网双网有礼营销活动有效期内影响携转办理。";
//        	count++;
//
//			String date = SysDateMgr.decodeTimestamp(ids.getData(0).getString("END_DATE"), SysDateMgr.PATTERN_STAND_SHORT);
//        	setLimitInfo("102", "集团V网双网有礼营销活动", date, "您处在集团V网双网有礼营销活动有效期内影响携转办理。", resultList, limitInfoLists);
//        }

        //携出中也能申请授权码
		//4.您有相关联的业务影响办理携转，业务名称：XXX
//        if(!"AUTHCODE_REQ".equals(param.getString("COMMANDCODE"))){
//        	ids = CParamQry.getXieChuIng(userId);
//        	if (IDataUtil.isNotEmpty(ids))
//        	{
//        		errorinfo += "@您已办理了携出业务处于携出中状态，无法再次办理携转业务。";
//        		errordetails += "@您已办理了携出业务处于携出中状态，无法再次办理携转业务。";
//        		count++;
////        		setLimitInfo("103", "携出业务", resultList);
//        	}
//        }
//        }

        //目前生产无该配置
        ids = UserSvcInfoQry.getUserSvcsByLimitNp(userId,CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(ids))
        {
        	errorinfo += "@您与携出方有未解除的协议影响携转办理。";
        	errordetails+="@您与携出方有未解除的协议影响携转办理。";
        	count++;
        }

        
        if (StringUtils.equals("80", userInfo.getData(0).getString("USER_DIFF_CODE"))) {
        	errorinfo += "@您为非实名客户，暂无法办理携转。";
        	errordetails+="@您为非实名客户，暂无法办理携转。";
        	count++;

			IData resultInfo = new DataMap();
			resultInfo.put("RESULT_CODE", "2031");
			resultInfo.put("RESULT_DESC", "申请携号转网的号码未在携出方办理真实身份信息登记。");
			resultList.add(resultInfo);
		}
		
        //6.您有至今的账单合计X元尚未缴清影响办理携转
//		 if(allBOweFee != 0l && "0".equals(acctMode))
//		 {
//			 errorinfo += "@您的号码当前有" + Math.abs(allBOweFee)/100.0 + "元费用尚未缴清影响携转办理。";
//			 errordetails+="@您的号码当前有" + Math.abs(allBOweFee)/100.0 + "元费用尚未缴清影响携转办理。";
//			 count++;
//
//			 IData resultInfo = new DataMap();
//			 resultInfo.put("RESULT_CODE", "2067");
//			 resultInfo.put("RESULT_DESC", "号码当前有"+Math.abs(allBOweFee)/100.0+"元费用尚未缴清影响携号转网办理。");
//			 resultList.add(resultInfo);
//		 }
		 
		 
		result.put("CODE", count);
		result.put("RESULT_MESSAGE", errorinfo);
		result.put("RESULT_DETAILS", errordetails);
		if(logger.isDebugEnabled())
		{
			logger.debug("======进入QueryNpMessageBean===checkNpOutMessage===result=" + result);
		}
		return result;
		
	}

	public IDataset AuthCodeApply(IData param) throws Exception {
		IDataUtil.chkParam(param, "COMMANDCODE");    	
		String sn = IDataUtil.chkParam(param, "SERIAL_NUMBER");
		IDataUtil.chkParam(param, "SERVICE_TYPE");
		IDataUtil.chkParam(param, "PORT_OUTNET_ID");
		IDataUtil.chkParam(param, "CUST_NAME");    	
		IDataUtil.chkParam(param, "CRED_TYPE");    	
    	IDataUtil.chkParam(param, "CRED_NUMBER");
    	IData input = new DataMap();
    	IData result = new DataMap();
    	IDataset resultlist = new DatasetList();			
        //REQ202001100023  将携入风险书和携出风险告知书作为业务受理单的一类加入BOSS中
        //新增配置开关实现，割接升级或者系统调整期间，申请授权码，给用户下发提示短信。
        String isSendAuthCode = "0";
        IDataset sendAuthCodeCodes = CommparaInfoQry.getCommparaByCode1("CSM", "1928","0", "1",null);
        if (IDataUtil.isNotEmpty(sendAuthCodeCodes)) {
            isSendAuthCode = sendAuthCodeCodes.getData(0).getString("PARA_CODE1");
        }
        if(StringUtils.equals("1", isSendAuthCode)) {
            //发送提示短信
            result.put("X_RESULTCODE", "-1");
            result.put("X_RESULTINFO", "失败");
            result.put("RESULT_MESSAGE", sendAuthCodeCodes.getData(0).getString("PARA_CODE3"));
            resultlist.add(result);
            return resultlist;
        }

        //校验客户信息
        IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", param.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userInfo)) {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "没有获取到用户信息！");
        }
        String userId = userInfo.getData(0).getString("USER_ID");
        //REQ201912220001关于调整携转限制内容以及在查询携转条件和申请授权码后面追加风险短信
        //1、针对全网139邮箱客户在携出时进行限制，需要到营业厅办理解除业务后放能接收到授权码。
        //增加开关
        IDataset commpara139Mail = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "1410",
                "NP_LIMIT_SWITCH","139Mail", "1","0898");
        if (IDataUtil.isNotEmpty(commpara139Mail)) {
            IDataset userMailSvc = UserPlatSvcInfoQry.queryValid139MailSvc(userId);

            if (IDataUtil.isNotEmpty(userMailSvc)) {
                //发送提示短信
                result.put("X_RESULTCODE", "-1");
                result.put("X_RESULTINFO", "失败");
                result.put("RESULT_MESSAGE", commpara139Mail.getData(0).getString("PARA_CODE3"));
                resultlist.add(result);
                return resultlist;
            }
        }
        //REQ201912220001关于调整携转限制内容以及在查询携转条件和申请授权码后面追加风险短信
        //1、针对已经有积分6个月以上的客户在申请携出时进行限制，需要到营业厅办理解除业务放弃积分后放能接收到授权码。
        //增加开关
        IDataset commparaUserScore = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "1410",
                "NP_LIMIT_SWITCH","UserScore", "1","0898");
        if (IDataUtil.isNotEmpty(commparaUserScore)) {
            //查询客户是否有积分6个月以上
            IDataset userScoreResults = AcctCall.queryUserScoreBeforeSixMonth(userId);
            if (IDataUtil.isNotEmpty(userScoreResults)) {
                IData userScoreResult = userScoreResults.getData(0);
                if (IDataUtil.isNotEmpty(userScoreResult)) {
                    Boolean limitFlag = userScoreResult.getBoolean("IS_LIMIT");//false，是表示没有6个月以上积分
                    if (limitFlag) {
                        //发送提示短信
                        result.put("X_RESULTCODE", "-1");
                        result.put("X_RESULTINFO", "失败");
                        result.put("RESULT_MESSAGE", commparaUserScore.getData(0).getString("PARA_CODE3"));
                        resultlist.add(result);
                        return resultlist;
                    }
                }
            }
        }

        String portBackId = "0";
        //携入用户判断
        IDataset userNpInfos = UserNpInfoQry.qryUserNpInfosBySn(sn);
        if (IDataUtil.isNotEmpty(userNpInfos)) {
            if ("1".equals(userNpInfos.getData(0).getString("NP_TAG")) || "3".equals(userNpInfos.getData(0).getString("NP_TAG"))) {
                //快速携回标签判断
                IDataset otherList = UserOtherInfoQry.checkPassChange(userNpInfos.getData(0).getString("USER_ID"), "QUICK_NP_SIGN");
                if (IDataUtil.isNotEmpty(otherList) && "1".equals(otherList.getData(0).getString("RSRV_STR1"))) {//存在快速携回标记
                    portBackId = "1";
                    //快速携回操作，将携入的网络ID填充至携出时的网络ID
                    param.put("PORT_OUTNET_ID", userNpInfos.getData(0).getString("PORT_IN_NETID"));
                }
            }
        }
        IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0", sn);
        String userTagSet = "";
        String strUserId = "";
        String strUserNpTag = "";
        String credType = "Z";
        if (IDataUtil.isNotEmpty(userInfos))
        {
            strUserId = userInfos.getData(0).getString("USER_ID");
            userTagSet = userInfos.getData(0).getString("USER_TAG_SET");
        	IDataset customerInfo = CustomerInfoQry.getCustInfoByCustIdPk(userInfos.getData(0).getString("CUST_ID"));
            if (IDataUtil.isNotEmpty(customerInfo)){//证件类型以数据库查询为准
            	String psptType = customerInfo.getData(0).getString("PSPT_TYPE_CODE");
            	IDataset paramTradeTypeCodes = CommparaInfoQry.getCommparaInfoByCode("CSM", "173", "PSPTTYPE",psptType,"0898");
            	if(IDataUtil.isNotEmpty(paramTradeTypeCodes)){//存在号段限制配置
            		credType = paramTradeTypeCodes.getData(0).getString("PARA_CODE2");
            	}else{//查询不到配置，默认为其他证件类型
            		credType = "Z";
            	}
            }
            	
        }
        if (StringUtils.isBlank(userTagSet))
        {
            strUserNpTag = "0";
        }
        else
        {
            strUserNpTag = userTagSet.substring(0, 1);
        }
        
        input.putAll(param);
        input.put("PORT_BACK_ID", portBackId);
        input.put("USERNAME", param.getString("CUST_NAME"));
        input.put("CREDNUMBER", param.getString("CRED_NUMBER"));
        input.put("CREDTYPE", credType);
        input.put("EPARCHY_CODE", param.getString("TRADE_EPARCHY_CODE"));
        input.put("PORT_OUT_NETID", param.getString("PORT_OUTNET_ID"));
        // 其中前三位为运营商代码（现有三个运营商，中国电信001，中国移动002，中国联通003）；第四位为运营商网络标识符（1为CDMA，2为CDMA2000，3为GSM，4为TD-SCDMA，5为WCDMA）；5-7位为本地网标识码；第8位为拓展位，默认填为0
        //PORT_IN_NETID,HOME_NETID接口规范不传工信部，仅作入表操作
        input.put("PORT_IN_NETID","00248980");
        input.put("HOME_NETID", gethome(param.getString("SERIAL_NUMBER")));			
        input.put("SERVICETYPE", param.getString("SERVICE_TYPE"));        
        input.put("AUTH_TAG", "AUTHCODE_REQ");
        input.put("TRADE_TYPE_CODE", "41");
        input.put(Route.ROUTE_EPARCHY_CODE, param.getString("TRADE_EPARCHY_CODE"));
        input.put("TRADE_EPARCHY_CODE", param.getString("TRADE_EPARCHY_CODE"));
        input.put("USER_ID", strUserId);        
        input.put("TRADE_CITY_CODE", param.getString("TRADE_EPARCHY_CODE"));
        input.put("TRADE_DEPART_ID", "0001");
        input.put("TRADE_STAFF_ID", "SOA");        
        input.put("USER_NP_TAG", strUserNpTag);
        input.put("RES_NO", sn);
        
        if (param.containsKey("MESSAGEID"))
        {
        	input.put("MESSAGE_ID", param.getString("MESSAGEID"));
        }
        try{//生成41工单	
        	if(logger.isDebugEnabled())
        	{
        		logger.debug("======进入QueryNpMessageBean======tradeinput=" + input);
        	}
        	IDataset nptrade = CSAppCall.call("SS.NpOutApplyRegSVC.tradeReg", input);// 对应老系统
        	if(logger.isDebugEnabled())
        	{
        		logger.debug("======进入QueryNpMessageBean======tradeReturn=" + nptrade);
        	}
        	if(IDataUtil.isNotEmpty(nptrade)){
        		IDataset resultInfos = new DatasetList();
        		IData npTrade = new DataMap();
        		npTrade = nptrade.getData(0);
        		if(StringUtils.isBlank(npTrade.getString("TRADE_ID"))){
        			result.put("X_RESULTCODE", "-1");
                	result.put("X_RESULTINFO", "失败");
                	result.put("RESULT_MESSAGE", npTrade.getString("X_RESULTINFO"));
                	resultInfos.add(result);
                	return resultInfos;
        		}
        	}
        	return nptrade;
        }catch(Exception e){
        	if(logger.isDebugEnabled())
        	{
        		logger.debug("======进入QueryNpMessageBean======Exception=" + e.getMessage());
        	}
        	//不需要返回给短厅，本地默认测试返回使用
        	result.put("X_RESULTCODE", "-1");
        	result.put("X_RESULTINFO", "失败");
        	result.put("RESULT_MESSAGE", e.getMessage());
        	resultlist.add(result);
        	return resultlist;
        }
	}

	private String gethome(String serialNumber) throws Exception {
		IDataset aps = TradeNpQry.getValidTradeNpBySn(serialNumber);
        String home_operator = "";

        String netWorkType = "1";
        if (IDataUtil.isNotEmpty(aps))
        {
            String asp = aps.getData(0).getString("ASP", "").trim();

            if ("2".equals(asp))
            {
                home_operator = "003";
                netWorkType = "3";
            }
            if ("3".equals(asp))
            {
                home_operator = "001";
                netWorkType = "1";
            }
            if ("1".equals(asp))
            {
                home_operator = "002";
                netWorkType = "4";

            }
        }

        if (StringUtils.isBlank(netWorkType))
        {
            netWorkType = "0";
        }
        String homeNetid = home_operator + netWorkType;
        homeNetid = homeNetid + "8980";
		return homeNetid;
	}
    private IData GetMaxEndDate(IDataset Array) throws Exception
    {
        String MaxDate = "";
        int iMax = 0;
        for(int i = 0; i < Array.size(); i++)
        {
        	if("".equals(MaxDate.toString()))
        	{
        		MaxDate = Array.getData(i).getString("END_DATE");
        		iMax = i;
        		continue;
        	}
        	String date = SysDateMgr.decodeTimestamp(MaxDate, SysDateMgr.PATTERN_STAND);
            String compareDate = Array.getData(i).getString("END_DATE");
            compareDate = SysDateMgr.decodeTimestamp(compareDate, SysDateMgr.PATTERN_STAND);
            int result = 0;
            if(date.substring(0, 10).equals(SysDateMgr.END_TIME_FOREVER)
                    && compareDate.substring(0, 10).equals(SysDateMgr.END_TIME_FOREVER))
            {
            	result = 0;
            	continue;
            }
            result = date.compareTo(compareDate);
        	if(result < 0)
        	{
        		MaxDate = Array.getData(i).getString("END_DATE");
        		iMax = i;
        	}
        }
        return Array.getData(iMax);
    }
    
	private void setLimitInfo(String limitId, String limitName, String limitTime, String limitDesc, IDataset resultList, IDataset limitInfoLists) throws Exception
	{
		IData limitInfo = new DataMap();
		limitInfo.put("LIMIT_ID", limitId);//待定
		limitInfo.put("LIMIT_NAME", limitName);
		limitInfo.put("LIMIT_TIME", limitTime);
		limitInfo.put("LIMIT_DESC", limitDesc);
		limitInfo.put("HANDLE_CHANNEL", "营业厅");
		limitInfo.put("HANDLE_TYPE", "服务密码或身份证件等");
		limitInfoLists.add(limitInfo);

		IData resultInfo = new DataMap();
		resultInfo.put("RESULT_CODE", "2064");
		resultInfo.put("RESULT_DESC", "有在网协议" + limitName + "影响携号转网办理。");
		resultList.add(resultInfo);
	}

    /**
     * 发送风险提示短信
     * @param serialNumber
     * @param riskInfo
     * @throws Exception
     */
    private void sendRiskReminderSMS(String serialNumber, IDataset riskInfo) throws Exception {
        String riskReminderSms = "尊敬的用户，您好！温馨提示，";
        // 获取客户可用积分余额
        String score = "";
        IDataset scoreInfos = AcctCall.queryUserScoreInfo(serialNumber);
        if (IDataUtil.isNotEmpty(scoreInfos)) {
            score = scoreInfos.getData(0).getString("SUM_SCORE");
        }

        if (StringUtils.isNotBlank(score) && Integer.parseInt(score) > 0) {
            riskReminderSms += "您的号码对应账户还存在积分余额，请您在携号转网前尽快使用。";
        }

        int countalert = riskInfo.size();
        String member = "";
        for(int i=0;i<countalert;i++){
            if(StringUtils.isNotBlank(riskInfo.getData(i).getString("RISK_REMINDER"))){
                member += riskInfo.getData(i).getString("RISK_REMINDER")+"、";
            }
        }
        if (StringUtils.isNotBlank(member)) {
            riskReminderSms += "您的号码是" + member.substring(0,member.length()-1) + "成员，携号转网后您和群组内的其他成员将无法继续享用相互通信的价格优惠。";
        }

        riskReminderSms += "携号转网后您的139邮箱可能无法正常使用，请确认邮箱内信息。【中国移动】";
        //发短信
        IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
        String userId = "";
        if (IDataUtil.isNotEmpty(userInfo))
        {
            userId = userInfo.getData(0).getString("USER_ID");
        }

        sendSMS(serialNumber, userId, riskReminderSms);
    }

    private void sendSMS(String serialNumber, String userId, String smsInfo) throws Exception{
        IData sendInfo = new DataMap();
        sendInfo.put("EPARCHY_CODE", "0898");
        sendInfo.put("RECV_OBJECT", serialNumber);
        sendInfo.put("RECV_ID", userId);
        sendInfo.put("SMS_PRIORITY", "50");
        sendInfo.put("NOTICE_CONTENT", smsInfo);
        sendInfo.put("REMARK", "携转风险提示");
        sendInfo.put("FORCE_OBJECT", "10086");

        /**
         * REQ202003160010_关于进一步做好携号转网服务用户交互规范工作的通知
         * 为提升用户感知，强调风险告知在号码满足携号转网条件后提示，并建议运营企业通过配置两条短消息下发时间间隔（如20s）等方式，
         * 尽量让用户先收到满足携号转网条件的信息，后收到风险告知。
         */
        int delayTime=20;//单位为秒
        //配置
        IDataset smsDelaySendTime = CommparaInfoQry.getCommparaAllCol("CSM", "40", "NP_RISK_INFO_DELAY_TIME", "0898");

        if(IDataUtil.isNotEmpty(smsDelaySendTime)){
            String strDelayTime=smsDelaySendTime.first().getString("PARA_CODE1","20");

            if(strDelayTime!=null&&!strDelayTime.equals("")){
                delayTime=Integer.parseInt(strDelayTime);
            }
        }
        sendInfo.put("FORCE_START_TIME", SysDateMgr.addSecond(SysDateMgr.getSysTime(), delayTime));//延迟20秒

        SmsSend.insSms(sendInfo);
    }
}

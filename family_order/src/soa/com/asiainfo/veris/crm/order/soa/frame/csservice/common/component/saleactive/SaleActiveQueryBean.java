package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import com.ailk.biz.service.BizService;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;


public class SaleActiveQueryBean extends CSBizBean {
	/**
     *是否满足IPHONE6合约送费条件：没有参加活动，或已参加活动且话费返还/约定消费时间在6个月（含6个月）以内结束的全球通、动感地带、神州行客户，办理4G套餐
     *在SVN中包含原来的代码，后续由于不需要，可以通过配置实现一些，就仅仅保留了4G套餐的判断，其中含有是否预约产品变更部分
     */
    public IData isIphone6Cons(IData params) throws Exception {
    	String user_id = params.getString("USER_ID");
		String eparchy_code = params.getString("EPARCHY_CODE");
    	IData result=new DataMap();
    	result.put("RESULT", true);

    	//4G套餐校验
    	result=this.is4GProductUser(user_id, eparchy_code);
    	if(!result.getBoolean("RESULT")){
    		return result;
    	}

		return result;
	}


	/**
	 * 是否是4G套餐，如果有下月生效的则判断下月是否是4g，如果没有则判断当前的是否是4g
	 */
	public IData is4GProductUser(String user_id,String eparchy_code) throws Exception {
		IData result=new DataMap();
		result.put("RESULT", false);
		result.put("RESULT_INFO", "该用户不是4G套餐，不能办理该活动");
		//取配置的4G套餐
		IDataset productList_4G = CommparaInfoQry.getCommpara("CSM", "8555",null, eparchy_code);
		if(IDataUtil.isNotEmpty(productList_4G)){
			String product_id="-1";
			//有变更的，则取下月生效套餐，否则是当前的生效的套餐
			IDataset userInfoChgByUserIdNxtvalidList = UserInfoQry.getUserInfoChgByUserIdNxtvalid(user_id);
			if(IDataUtil.isNotEmpty(userInfoChgByUserIdNxtvalidList)){
				product_id=userInfoChgByUserIdNxtvalidList.getData(0).getString("PRODUCT_ID");
			}
			for(int i=0;i<productList_4G.size();i++){
				String product_id_4G=productList_4G.getData(i).getString("PARA_CODE1");
				if(product_id_4G.equals(product_id)){
					result.put("RESULT", true);
					return result;
				}
			}
		}
		else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"4Gt套餐配置转换无数据");
		}
		return result;
	}
	
	/**
     * REQ201603090003 关于新增集团客户回馈购机活动的需求（积分）
     * chenxy3 20160324
     * SVC无法调用账务接口，需要到BEAN里调用
     * */
	public IDataset queryUserScoreValue(IData param)throws Exception {
		return AcctCall.queryUserScoreValue(param);//调账务接口取积分
	}
	
	/**
     * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求
     * chenxy3 2016-08-26
     * 冲正、返销修改状态
     * */
    public static void updateVolteOtherVal(IData params) throws Exception
    { 
    	String cancelTag=params.getString("CANCEL_TAG","");
    	String callflag=params.getString("CALL_FLAG","");
    	String callXML=params.getString("CALL_XML","");
    	if(!"".equals(callXML)&& callXML.length()>500){
    		callXML=callXML.substring(0,400);
    	}
    	String callResp=params.getString("CALL_RESPONSE","");
    	if(!"".equals(callResp)&& callResp.length()>500){
    		callResp=callResp.substring(0,400);
    	}
    	params.put("CALL_XML", callXML);
    	params.put("CALL_RESPONSE", callResp);
    	
    	String dateColumn="";
    	if("SEND_PAK".equals(callflag)){
    		dateColumn="RSRV_DATE2";
    		params.put("RSRV_VALUE", "4");//状态：1=发红包  2=扣款成功（未完工） 3=扣款完工  4=红包发放冲正   1=扣款完工冲正  6=返销 
    		params.put("RSRV_VALUE_COND", "1");
    		
    	}else if("PAY".equals(callflag)){
    		dateColumn="RSRV_DATE3"; 
    		params.put("RSRV_VALUE", "1");//状态：1=发红包  2=扣款成功（未完工） 3=扣款完工  4=红包发放冲正   1=扣款完工冲正  6=返销
    		if("1".equals(cancelTag)){
    			params.put("RSRV_VALUE_COND", "3");
    		}else{ 
    			params.put("RSRV_VALUE_COND", "2");
    		}
    	}
    	if("1".equals(cancelTag)){ 
    		if("SEND_PAK".equals(callflag)){
    			params.put("RSRV_VALUE", "6"); //状态：1=发红包  2=扣款成功（未完工） 3=扣款完工  4=红包发放冲正   5=扣款完工冲正  6=发红包返销
    			params.put("RSRV_STR17", "1");
    		}else{
    			params.put("RSRV_VALUE", "1");
    			params.put("RSRV_STR17", "0");//如果是活动C返销，要恢复成B活动的原始状态
    			params.put("RSRV_STR21","1");//新增21记录活动C的返销标记
    			params.put("RSRV_STR22","活动C返销，红包扣款回退。");
    		} 
    		params.put("RSRV_STR19", BizService.getVisit().getStaffId());
    		params.put("RSRV_STR20", BizService.getVisit().getDepartId());
		}
    	
    	StringBuilder sql = new StringBuilder(1000);
    	sql.append(" update tf_f_user_other t");
    	sql.append(" set t.RSRV_VALUE=:RSRV_VALUE,t."+dateColumn+"=SYSDATE");
    	if("1".equals(cancelTag)){
    		sql.append(",RSRV_STR17=:RSRV_STR17,RSRV_STR18=to_date('"+SysDateMgr.getSysDateYYYYMMDDHHMMSS()+"','yyyy-mm-dd hh24:mi:ss'),RSRV_STR19=:RSRV_STR19,RSRV_STR20=:RSRV_STR20");
    		if("SEND_PAK".equals(callflag)){
    			sql.append(",RSRV_STR26=:CALL_XML,RSRV_STR27=:CALL_RESPONSE");
    			sql.append(",end_date=to_date('"+SysDateMgr.getSysDateYYYYMMDDHHMMSS()+"','yyyy-mm-dd hh24:mi:ss')");
    		}else{
    			sql.append(",RSRV_STR21=:RSRV_STR21,RSRV_STR22=:RSRV_STR22");
    			sql.append(",RSRV_STR28=:CALL_XML,RSRV_STR29=:CALL_RESPONSE");
    		}
    	}
    	sql.append(" where t.user_id=TO_NUMBER(:USER_ID) and t.RSRV_VALUE_CODE='RED_PAK' ");  
    	sql.append(" and RSRV_VALUE=:RSRV_VALUE_COND");
    	sql.append(" and sysdate < t.end_date");
        Dao.executeUpdate(sql, params);
    }
    
    /**
     * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求
     * chenxy3 2016-08-26
     * 冲正、返销修改状态
     * */
    public static IDataset getRedPakOtherInfo(IData params) throws Exception
    { 
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHER_BY_VALUE_CODE_RSRV4", params);
    } 
}
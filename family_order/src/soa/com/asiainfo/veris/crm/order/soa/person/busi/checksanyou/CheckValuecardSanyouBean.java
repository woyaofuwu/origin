package com.asiainfo.veris.crm.order.soa.person.busi.checksanyou;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList; 
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao; 

/**
 *  REQ201505120012 异常三友佳充值卡查询页面需求
 *  1、获取中心库ucr_res用户的下的表TF_R_SANYOU_TEMP的query_tag为1且deal_tag不为1的数据
 *  2、循环调用SS.RechargedCardSVC.recharge接口，充值号设置为13807512424
 *  3、更新TF_R_VALUECARD_SANYOU表，存在则更新状态，不存在插入。
 *  4、更新TF_R_SANYOU_TEMP表状态，处理过无论成功与否都置为处理过1
 *  2015-05-22 chenxy3
 */
public class CheckValuecardSanyouBean extends CSBizBean
{
    private static transient final Logger logger = Logger.getLogger(CheckValuecardSanyouBean.class);
 
    /**
     *  循环调油价卡充值接口校验数据
     * */
    public void checkSanyouTempData() throws Exception
    {	 
    	//1、取TF_R_SANYOU_TEMP表数据
    	IDataset tampData=getSanyouTemp(); 
    	if(tampData==null || tampData.size()<1){
    		return;
    	}
    	for(int i=0;i<tampData.size();i++){
    		
	    		IData param=tampData.getData(i);
	    		
	    		IData inParam=new DataMap();
	    		inParam.put("CARDPIN", param.getString("CARD_PASSWD"));
	    		inParam.put("SERIAL_NUMBER", "13807512424");
	    		inParam.put("MSISDN", "13807512424");
	    	    //2、调用有卡充值接口 
	    		IDataset callset=new DatasetList();
	    		String X_RESULTCODE="";
	    		String X_RESULTINFO="";
	    		try{
		    		param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode()); 
		    		callset=CSAppCall.call("SS.RechargedCardSVC.recharge", inParam);    
		    		if(callset!=null&&callset.size()>0){
			    		X_RESULTCODE=callset.getData(0).getString("X_RESULTCODE","");
			    		X_RESULTINFO=callset.getData(0).getString("X_RESULTINFO","");
		    		}else{
		    			X_RESULTCODE="-2";
		    			X_RESULTINFO="调用服务SS.RechargedCardSVC.recharge返回空值。";
		    		}
	    		}catch(Exception e){
	    			X_RESULTCODE="-1";
		    		X_RESULTINFO="调用SS.RechargedCardSVC.recharge接口失败:"+e.getMessage().substring(0,50);
	    		}
	    		//3、保存数据TF_R_VALUECARD_SANYOU（2015-9-8 TF_R_VALUECARD_SANYOU表有主键！要判断是否存在数据，存在就只更新）
	    		
	    		String queryResult="";
	    		if(!"".equals(X_RESULTCODE)&&"0".equals(X_RESULTCODE)){
	    			queryResult="1";//成功
	    		}else{
	    			queryResult="0";
	    		}
	    		IData insData=new DataMap();
	    		insData.put("VALUE_CARD_NO", param.getString("VALUE_CARD_NO",""));
	    		insData.put("QUERY_TIME", SysDateMgr.getSysTime());
	    		insData.put("QUERY_TAG", queryResult);
	    		insData.put("QUERY_RESULT", X_RESULTINFO);
	    		IDataset sanList=this.qrySanyouInfo(param.getString("VALUE_CARD_NO",""));
	    		if(sanList!=null && sanList.size()>0){
	    			this.updateSanyouInfo(insData);
	    		}else{
	    			
		    		insData.put("CARD_PASSWD", param.getString("CARD_PASSWD",""));
		    		insData.put("QUERY_COUNT", "1");
		    		insData.put("QUERY_STAFF_ID", "SUPERUSR"); 
		    		int insFlag=insCardValueSanyou(insData);
	    		}	    		
	    		
	    		//4.更新日志表
	    		IData insLogData=new DataMap();
	    		insLogData.put("VALUE_CARD_NO", param.getString("VALUE_CARD_NO",""));
	    		insLogData.put("UPDATE_TIME",SysDateMgr.getSysTime());
	    		insLogData.put("UPDATE_STAFF_ID", param.getString("UPDATE_STAFF_ID",""));
	    		insLogData.put("UPDATE_DEPART_ID", param.getString("UPDATE_DEPART_ID",""));
	    		insLogData.put("QUERY_TAG", queryResult);
	    		insLogData.put("QUERY_RESULT", X_RESULTINFO);
	    		insLogData.put("UPDATE_CITY_CODE", param.getString("UPDATE_CITY_CODE",""));
	    		insValueSanyouLog(insLogData);
	    		
	    		//5、更新TF_R_SANYOU_TEMP处理标记
	    		IData updData=new DataMap();
	    		updData.put("VALUE_CARD_NO", param.getString("VALUE_CARD_NO",""));
	    		updData.put("DEAL_TAG", "1");//不管是否成功，都打上已处理的标记
	    		updSanyouTemp(updData);
    		
    	}
    }
    
    /**
     *  捞取【ucr_res】用户的下的表TF_R_SANYOU_TEMP的query_tag为1且deal_tag不为1的数据；
     * @throws Exception
     */
    public IDataset getSanyouTemp() throws Exception
    { 
    	SQLParser parser = new SQLParser(new DataMap());  
        parser.addSQL(" select * from TF_R_SANYOU_TEMP t "); 
        parser.addSQL(" where 1=1  ");
        parser.addSQL(" and t.query_tag='1'   ");
        parser.addSQL(" and (t.deal_tag<>'1' or t.deal_tag is null) ");
        
        return Dao.qryByParse(parser, Route.CONN_RES); 
    }
    
    /**
     * 查询是否存在数据
     * */
    public IDataset qrySanyouInfo(String VALUE_CARD_NO) throws Exception
    { 
    	
    	IData input =new DataMap();
    	input.put("VALUE_CARD_NO", VALUE_CARD_NO);
    	SQLParser parser = new SQLParser(input); 
        parser.addSQL(" select t.* from TF_R_VALUECARD_SANYOU t "); 
        parser.addSQL(" where 1=1  ");
        parser.addSQL(" and t.VALUE_CARD_NO=:VALUE_CARD_NO");
        
        return Dao.qryByParse(parser,Route.CONN_RES); 
    }
    
    /**
     * 修改用户的开始时间为当前时间     *  
     * */
    public static void updateSanyouInfo(IData params) throws Exception
    {
    	IData param = new DataMap();
    	param.put("QUERY_TIME", params.getString("QUERY_TIME"));  
    	param.put("QUERY_TAG", params.getString("QUERY_TAG")); 
    	param.put("QUERY_RESULT", params.getString("QUERY_RESULT")); 
    	param.put("VALUE_CARD_NO", params.getString("VALUE_CARD_NO")); 
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update TF_R_VALUECARD_SANYOU t ");
    	sql.append(" set T.QUERY_COUNT=T.QUERY_COUNT+1,T.QUERY_STAFF_ID='SUPERUSR',T.QUERY_TIME=to_date(:QUERY_TIME,'yyyy-mm-dd hh24:mi:ss'),T.QUERY_TAG=:QUERY_TAG,T.QUERY_RESULT=:QUERY_RESULT ");
    	sql.append(" WHERE T.VALUE_CARD_NO=:VALUE_CARD_NO");
        Dao.executeUpdate(sql, param, Route.CONN_RES);
    }
    
    /**
     *  处理结果填到TF_R_VALUECARD_SANYOU
     *  
     * */
    public int insCardValueSanyou(IData param) throws Exception
    {
    	 return Dao.executeUpdateByCodeCode("TF_R_VALUECARD_SANYOU", "INS_VALUECARD_SANYOU_DEAL", param,Route.CONN_RES);
    }
    
    /**
     *  记录日志
     *  
     * */
    public int insValueSanyouLog(IData param) throws Exception
    {
    	 return Dao.executeUpdateByCodeCode("TF_R_SANYOU_LOG", "INS_TF_R_SANYOU_LOG", param,Route.CONN_RES);
    }
    
    
    /**
     * 结束后，如果成功，打上处理标记
     * 如果失败？ 待定处理。。。。。
     * update TF_R_SANYOU_TEMP t
	   set t.deal_tag=:DEAL_TAG
	   where VALUE_CARD_NO=:VALUE_CARD_NO
     * */
    public int updSanyouTemp(IData param) throws Exception
    {
    	 return Dao.executeUpdateByCodeCode("TF_R_SANYOU_TEMP", "UPD_SANYOU_TEMP_DEAL", param, Route.CONN_RES);
    }
}

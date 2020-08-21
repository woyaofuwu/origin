package com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue.order;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class QuerySmartNetworkIntfBean extends CSBizBean {

	static Logger logger=Logger.getLogger(QuerySmartNetworkIntfBean.class);
	
	public static IDataset querySmartNetwork(IData param) throws Exception{
		IDataset dataset = new DatasetList();
		
	
		if(param.getString("SERIAL_NUMBER").equals("")){
			return dataset;
		}
		else{
			IDataset userInfo=UserInfoQry.getUserInfoBySn(param.getString("SERIAL_NUMBER"), "0");
			 if(IDataUtil.isNotEmpty(userInfo) && userInfo.size() > 0){
				  param.put("USER_ID", userInfo.getData(0).getString("USER_ID"));
			 }else{
				 CSAppException.apperr(CrmCommException.CRM_COMM_103,"请输入正确的手机号码！" );
			 }
			return queryinfo(param);
		}
	}

    public static IDataset queryinfo(IData TIDdata) throws Exception{
        //查看对应产品的param_code
    	System.out.print("queryinfo="+ TIDdata);
    	
        IDataset dataset1 = qryDinfo(TIDdata); 
        
        //品牌/机型/串号
        if(IDataUtil.isNotEmpty(dataset1) && dataset1.size() > 0){
        	for(int i = 0;i < dataset1.size(); i++){
        		IData idata = dataset1.getData(i);
        		//
        		IData param= new DataMap();
        		param.put("USER_ID", idata.getString("USER_ID"));
        		param.put("TRADE_ID", idata.getString("RSRV_STR1",""));
        		SQLParser parser2= new SQLParser(param);
                parser2.addSQL("SELECT T.* FROM UCR_CRM1.TF_F_USER_OTHER T WHERE 1=1 ");
                parser2.addSQL("AND T.USER_ID=:USER_ID ");
                parser2.addSQL("AND T.RSRV_STR2=:TRADE_ID ");
                parser2.addSQL("AND T.RSRV_VALUE_CODE ='ZNZW' ");
                parser2.addSQL("AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE ");
                IDataset dataset2 = Dao.qryByParse(parser2);
        		
                String str="";
    			for(int j= 0;j <dataset2.size(); j++){
    		    	IData data = dataset2.getData(j);
    		    	 str="品牌："+data.getString("RSRV_STR3")+" 型号："+data.getString("RSRV_STR4")+" 串号："+data.getString("RSRV_STR1");
    		    	 str+="\n"+str;	
    		    	 //dataset.add(idata);
    		    }
    			
    			idata.put("ZNZW_INFO", str);
    			
    			//
    			IData inparam= new DataMap();
    			inparam.put("USER_ID", idata.getString("USER_ID"));
    			inparam.put("TRADE_ID", idata.getString("RSRV_STR1",""));
    			SQLParser parser3= new SQLParser(inparam);
    			parser3.addSQL("SELECT T.* FROM UCR_CRM1.TF_BH_TRADE T ");
    			parser3.addSQL("WHERE T.USER_ID=:USER_ID ");
    			parser3.addSQL("AND T.TRADE_ID=:TRADE_ID ");
    			parser3.addSQL("AND T.CANCEL_TAG='0'");
    			IDataset  dataset3 = Dao.qryByParse(parser3);
    			String FINISH_DATE = "";
    			if(IDataUtil.isNotEmpty(dataset3) && dataset3.size() > 0){
    				FINISH_DATE = dataset3.getData(0).getString("FINISH_DATE","");//finish_date
    			}
    		   
    			idata.put("FINISH_DATE", FINISH_DATE);
    			
        	}   
        	
        }   	
    	return dataset1;
    }
    
    
    public static IDataset qryDinfo(IData TIDdata) throws Exception{
    	SQLParser parser= new SQLParser(TIDdata); 
		parser.addSQL("SELECT T.* FROM UCR_CRM1.TF_F_USER_OTHER T WHERE 1=1 ");
		//parser.addSQL("AND T.RSRV_STR1 = :TRADE_ID");
		parser.addSQL("AND T.USER_ID=:USER_ID ");
		parser.addSQL("AND T.RSRV_VALUE_CODE = 'ZNZW_ACCT_IN' ");
		parser.addSQL("AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE ");
		IDataset dataset = Dao.qryByParse(parser);
		System.out.print("qryDinfo"+ dataset);
		return dataset;
    }
}

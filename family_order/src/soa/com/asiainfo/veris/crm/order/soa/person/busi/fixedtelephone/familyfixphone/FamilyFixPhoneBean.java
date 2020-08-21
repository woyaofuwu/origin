
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.familyfixphone;

import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dao.impl.BaseDAO;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.util.SQLParser;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;  
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;

public class FamilyFixPhoneBean extends CSBizBean
{
	private static Logger logger = Logger.getLogger(FamilyFixPhoneBean.class);
   
	/**
	 * 1、手机号码是办理 FTTH 宽带
	    2、校验是经办否已理过家庭固话业务
	    3、固话号码，调用接口校验是否可用。
	 * */
	public IData checkAuthSerialNum(IData param)throws Exception{
		IData rtnData=new DataMap();
		IDataset checkset=checkUnfinishTrade(param);
		if(checkset!=null && checkset.size()>0){
			rtnData.put("RESULT_CODE", "0");
        	rtnData.put("RESULT_INFO", "用户存在未完工的业务，请先处理完工再办理该业务。");
        	return rtnData;
		}
		IDataset ifFixTrades=qryFixPhoneInfo(param);
		if(ifFixTrades!=null && ifFixTrades.size()>0){
			rtnData.put("RESULT_CODE", "0");
        	rtnData.put("RESULT_INFO", "用户已经办理过家庭固话业务。");
        	return rtnData;
		}else{
			rtnData.put("RESULT_CODE", "1");
		}
		IData input=new DataMap();
		String serialNum=param.getString("SERIAL_NUMBER","");
		input.put("KD_SERIAL_NUMBER", "KD_"+serialNum);
		IDataset dataset = qryWidenetInfo(input);
		if(dataset!=null && dataset.size()>0){
			String  widetype = dataset.getData(0).getString("RSRV_STR2");
			if ("3".equals(widetype))
	        {
				rtnData.put("RESULT_CODE", "1");
	        }else{
	        	rtnData.put("RESULT_CODE", "0");
	        	rtnData.put("RESULT_INFO", "用户宽带非FTTH类型宽带。");
	        }
		}else{
			rtnData.put("RESULT_CODE", "-1");
        	rtnData.put("RESULT_INFO", "用户未办理宽带。");
        }
		return rtnData;
	}
	
	/**
	    3、固话号码，调用接口校验是否可用。
	 * */
	public IData checkFixPhoneNum(IData param)throws Exception{
		IData rtnData=new DataMap();
		String fixNum=param.getString("FIX_NUMBER","");
		//     固话号码检验接口
		IData params=new DataMap(); 
		params.put("RES_VALUE", fixNum);//固话号码 0898开头
		params.put("RES_TYPE_CODE","0");
		params.put("RES_TYPE","固话号码");
		try{
			rtnData=checkFixNumber(params);
//			rtnData.put("RESULT_CODE", "1");
//			rtnData.put("RESULT_INFO", "成功！");
		}catch(Exception e){
	    	String error =  Utility.parseExceptionMessage(e);
	    	String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
			if(errorArray.length >= 2)
			{
				String strExceptionMessage = errorArray[1];
				rtnData.put("RESULT_CODE", "-1");
				rtnData.put("RESULT_INFO", "号码【"+fixNum+"】预占失败:"+strExceptionMessage);
			}
			else
			{
				rtnData.put("RESULT_CODE", "-1");
				rtnData.put("RESULT_INFO", "固话号码【"+fixNum+"】预占失败:"+error);
			}  
         }
		return rtnData; 
	}

	public IDataset checkIfUserOpen48HourForBat(IData params) throws Exception{
   	 
    	SQLParser parser = new SQLParser(params); 
        parser.addSQL(" select t.*,t.rowid from tf_F_user t where t.SERIAL_NUMBER= :CHECK_SERIAL_NUMBER and t.acct_tag='2' and t.remove_tag='0' "); 
        IDataset infos=  Dao.qryByParse(parser); 
        
    	return infos;
    }
    
    
    
    public void inserTemData(IData params) throws Exception{
    	DBConnection conn = null;
    	try 
		{
    		conn = SessionManager.getInstance().getAsyncConnection("crm1");
			IData inparams=new DataMap();
	    	inparams.put("TRADE_TYPE_CODE","240");//台账类型，如240=营销活动受理
	    	inparams.put("USER_ID",params.getString("USER_ID",""));//主号USER_ID
	    	inparams.put("SERIAL_NUMBER",params.getString("SERIAL_NUMBER",""));//主号serial_number
	    	inparams.put("CHECK_USER_ID",params.getString("CHECK_USER_ID",""));//弹出框校验号码USER_ID
	    	inparams.put("CHECK_SERIAL_NUMBER",params.getString("CHECK_SERIAL_NUMBER",""));//弹出框校验号码SERIAL_NUMBER
	    	inparams.put("PRODUCT_ID",params.getString("PRODUCT_ID",""));//主号办理的活动
	    	inparams.put("PACKAGE_ID","");//主号办理的包
	    	inparams.put("PRODUCT_ID_B",params.getString("PRODUCT_ID_B",""));//弹出框校验号码需要绑定的活动
	    	inparams.put("PACKAGE_ID_B","");//这里还没选包，所以无法选择具体包保存。
	    	inparams.put("DEAL_TAG","0");//0-校验通过，1-B号码成功办理活动
	    	inparams.put("DEAL_TIME",SysDateMgr.getSysTime());//处理时间
	    	inparams.put("RSRV_STR1","");
	    	inparams.put("RSRV_STR2","");
	    	inparams.put("RSRV_STR3","");
	    	inparams.put("RSRV_STR4","");
	    	inparams.put("RSRV_STR5","");  
        	BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
			dao.insert(conn, "TF_F_NEW_ACTIVE_TEMPLATE", inparams);
	    	conn.commit();
		}
    	catch (Exception e1) 
		{ 
			if(conn != null)
			{
				conn.rollback();
			}			
			CSAppException.appError("2001", e1.getMessage());
		} 
		finally 
		{
			if(conn != null)
			{
				conn.close();
			}
		} 
    } 
    /**
     * 查询用户家庭固话信息
     * */
    public IDataset qryFixPhoneInfo(IData params) throws Exception{
    	 
    	SQLParser parser = new SQLParser(params); 
        parser.addSQL("  select t.*  from Tf_f_User_Other t ");
        parser.addSQL(" where T.RSRV_VALUE_CODE='FIX_PHONE' ");
        parser.addSQL(" AND t.user_id=:USER_ID ");
        parser.addSQL(" AND SYSDATE < T.END_DATE "); 
        IDataset infos=  Dao.qryByParse(parser); 
        
    	return infos;
    }
    
    /**
     * 查询宽带信息
     * */
    public IDataset qryWidenetInfo(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" select t.* FROM TF_F_USER_WIDENET t,TF_F_USER T1 "); 
        parser.addSQL(" WHERE T1.SERIAL_NUMBER=:KD_SERIAL_NUMBER "); 
        parser.addSQL(" AND T.USER_ID=T1.USER_ID "); 
        parser.addSQL(" AND T1.REMOVE_TAG='0'  "); 
        parser.addSQL(" AND SYSDATE BETWEEN t.START_DATE AND t.END_DATE  "); 
        return Dao.qryByParse(parser); 
    	
    }
    
    /**
     * 查询未完工
     * */
    public IDataset checkUnfinishTrade(IData input) throws Exception
    {
    	SQLParser parser = new SQLParser(input); 
  	  
        parser.addSQL(" select t.* FROM TF_B_TRADE t "); 
        parser.addSQL(" WHERE T.SERIAL_NUMBER=:SERIAL_NUMBER "); 
        return Dao.qryByParse(parser,Route.getJourDbDefault()); 
    	
    }
    
    
    /**
     * 固话号码判断，选占号码。
     */
    public IData checkFixNumber(IData param) throws Exception
    { 
        String serialNumber = param.getString("RES_VALUE", "");
        String restypecode = param.getString("RES_TYPE_CODE");
        String restype = param.getString("RES_TYPE");

        IData result = new DataMap();

        IDataset userInfos = UserGrpInfoQry.getMemberUserInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(userInfos))
        {
            result.put("RESULT_CODE", "-1");
            result.put("RESULT_INFO", serialNumber + "号码已经生成了资料，请输入新号码！");
        }
        else
        {
        	String str = serialNumber.substring(0, 4);
            if (!"0898".equals(str))
            {
                result.put("RESULT_CODE", "-1");
                result.put("RESULT_INFO", serialNumber + "号码非固话号码，IMS语音成员用户开户必须为固话号码，请输入新号码！");
            }
            else
            {
                // 选占
                IDataset callset=ResCall.checkResourceForMphone("0", serialNumber, "0");
                result.put("RESULT_CODE", "1");
                result.put("RESULT_INFO", "号码【"+serialNumber+"】预占成功！");
                IDataset resDataSet = new DatasetList();
                IData resTmp = new DataMap();
                resTmp.put("RES_CODE", serialNumber);
                resTmp.put("RES_TYPE_CODE", restypecode);
                resTmp.put("RES_TYPE", restype);
                resDataSet.add(resTmp);

                result.put("RES_LIST", resDataSet);
            }

        }
        return result;
    }
}

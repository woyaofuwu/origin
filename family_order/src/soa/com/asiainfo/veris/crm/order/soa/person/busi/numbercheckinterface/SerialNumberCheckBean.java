package com.asiainfo.veris.crm.order.soa.person.busi.numbercheckinterface;
 
import com.ailk.common.data.IData; 
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;  
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao; 

/**
 * REQ201509300012 自助终端增加移动、铁通固话缴费功能需求
 * CHENXY3
 * 2015-12-22
 * */
public class SerialNumberCheckBean extends CSBizBean
{
	/**
     * 1、移动固话TD2以898开头，铁通固话TD1以0898开头，用户缴费时只需要输入8位电话号码，
	 *    系统能够根据号码区分移动固话TD2，铁通固话TD1，并自动根据固话号码类型字段添加号码开头信息。
		      新增固话类型查询接口，根据固话号码查询号码属于铁通固话或移动固话还是普通移动手机号。
	 *1、 固话号码入参为8位；
	 *2、 8位号码拼装898及0898查询TF_F_USER表
	 *3、 898开头的号码为移动固话（TD2），0898开头的号码为铁通固话（TD1）
	 *4、 普通移动手机号 则直接查询TF_F_USER表
     * */
    public IData serialNumberCheck(IData input) throws Exception{
    	IData rtnData=new DataMap();
    	String resultCode="";
    	String resultInfo="";
    	String phoneType="";
    	String netTypeCode="";
    	String prepayTag="";
    	String sn=input.getString("SERIAL_NUMBER","");
    	String rtnSn="";
    	try{
    		if(sn==null || "".equals(sn)){
    			resultCode="-1";
	    		resultInfo="SERIAL_NUMBER不允许为空，请输入手机号码或者8位固话号码";
    		}else{
		    	if(sn.length()!=8 && sn.length()!=11 && sn.length()!=12){
		    		resultCode="-1";
		    		resultInfo="输入号码位数错误";
		    	}else{
		    		//固话
		    		if(sn.length()==8){
		    			String td1="0898"+sn;
		    			IData inParam=new DataMap();
		    	    	inParam.put("SERIAL_NUMBER",td1);
		    	    	IDataset infos=getUserInfo(inParam);
		    	    	if(infos!=null && infos.size()>0){
		    	    		String td2="898"+sn;  
			    	    	inParam.put("SERIAL_NUMBER",td2);
			    	    	IDataset infosTd2=getUserInfo(inParam);
			    	    	if(infosTd2!=null && infosTd2.size()>0){
			    	    		resultCode="-1";
			    	    		resultInfo="固话数据异常-拼接898、0898均能查询到数据"; 
			    	    	}else{
			    	    		rtnSn=td1;
			    	    		resultCode="0";
			    	    		resultInfo="调用成功";
			    	    		phoneType="TD1";
			    	    		netTypeCode=infos.getData(0).getString("NET_TYPE_CODE");
			    	    		prepayTag=infos.getData(0).getString("PREPAY_TAG");
			    	    	}
		    	    	}else{
		    	    		String td2="898"+sn;  
			    	    	inParam.put("SERIAL_NUMBER",td2);
			    	    	infos=getUserInfo(inParam);
			    	    	if(infos!=null && infos.size()>0){
			    	    		rtnSn=td2;
			    	    		resultCode="0";
			    	    		resultInfo="调用成功";
			    	    		phoneType="TD2";
			    	    		netTypeCode=infos.getData(0).getString("NET_TYPE_CODE");
			    	    		prepayTag=infos.getData(0).getString("PREPAY_TAG");
			    	    	}else{
			    	    		resultCode="-1";
			    	    		resultInfo="无此号码资料-拼接898、0898均不能查询到数据";
			    	    	}		    	    	
		    	    	}
		    		}else if(sn.length()==11){ 
		    			if(!"1".equals(sn.substring(0,1))){
		    				if(!"898".equals(sn.substring(0,3))){
		    					resultCode="-1";
			    	    		resultInfo="您输入的不是手机号码，也不是898开头的固话号码。请输入8位固话号码、898开头的固话号码或者11位手机号码。";
		    				}else{
		    					IData inParam=new DataMap();
				    	    	inParam.put("SERIAL_NUMBER",sn);
				    	    	IDataset infos=getUserInfo(inParam);
				    	    	if(infos!=null && infos.size()>0){
				    	    		rtnSn=sn;
				    	    		resultCode="0";
				    	    		resultInfo="调用成功";
				    	    		phoneType="TD2";
				    	    		netTypeCode=infos.getData(0).getString("NET_TYPE_CODE");
				    	    		prepayTag=infos.getData(0).getString("PREPAY_TAG");
				    	    	}else{
				    	    		resultCode="-1";
				    	    		resultInfo="无此号码资料-898开头的固话号码【"+sn+"】无法查询到数据";
				    	    		phoneType="TD2";
				    	    	}
		    				}
		    			}else{
			    			IData inParam=new DataMap();
			    	    	inParam.put("SERIAL_NUMBER",sn);
			    	    	IDataset infos=getUserInfo(inParam);
			    	    	if(infos!=null && infos.size()>0){
			    	    		rtnSn=sn;
			    	    		resultCode="0";
			    	    		resultInfo="调用成功";
			    	    		phoneType="SN";
			    	    		netTypeCode=infos.getData(0).getString("NET_TYPE_CODE");
			    	    		prepayTag=infos.getData(0).getString("PREPAY_TAG");
			    	    	}else{
			    	    		resultCode="-1";
			    	    		resultInfo="无此号码资料-手机号码【"+sn+"】无法查询到数据";
			    	    		phoneType="SN";
			    	    	}
		    			}
		    		}else if(sn.length()==12){
		    			if(!"0898".equals(sn.substring(0,4))){
		    				resultCode="-1";
		    	    		resultInfo="您输入的不是0898开头号码，请输入8位固话号码或者12位带区号固话号码。";
		    			}else{
		    				IData inParam=new DataMap();
			    	    	inParam.put("SERIAL_NUMBER",sn);
			    	    	IDataset infos=getUserInfo(inParam);
			    	    	if(infos!=null && infos.size()>0){
			    	    		rtnSn=sn;
			    	    		resultCode="0";
			    	    		resultInfo="调用成功";
			    	    		phoneType="TD1";
			    	    		netTypeCode=infos.getData(0).getString("NET_TYPE_CODE");
			    	    		prepayTag=infos.getData(0).getString("PREPAY_TAG");
			    	    	}else{
			    	    		resultCode="-1";
			    	    		resultInfo="无此号码资料-0898固话【"+sn+"】无法查询到数据";
			    	    		phoneType="TD1";
			    	    	}
		    			}
		    		}
		    	}
	    	}
    	}catch(Exception e){
    		resultCode="-1";
    		resultInfo="系统错误 ";
    	}
    	
    	/*
    	 *  1其他错误信息	其他编码外错误，系统错误、应用错误 
			2输入号码位数错误	输入号码必须为8位
			3固话数据异常	拼接898、0898均能查询到数据
			4无此号码资料	拼接898、0898均不能查询到数据
    	 * */
    	rtnData.put("X_RESULTINFO",resultInfo);
    	rtnData.put("X_RESULTCODE",resultCode);  //0成功  -1错误 
    	rtnData.put("PHONE_TYPE",phoneType);
    	rtnData.put("NET_TYPE_CODE",netTypeCode);
    	rtnData.put("PREPAY_TAG",prepayTag);
    	rtnData.put("SERIAL_NUMBER",rtnSn);
    	return rtnData;
    }
    
    public static IDataset getUserInfo(IData inparams) throws Exception
    {  
        SQLParser parser = new SQLParser(inparams); 
        parser.addSQL(" select t.* from TF_F_USER T  ");
        parser.addSQL(" WHERE T.REMOVE_TAG='0' AND T.SERIAL_NUMBER=:SERIAL_NUMBER  "); 
  
    	return Dao.qryByParse(parser);
    }
}
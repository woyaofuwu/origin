package com.asiainfo.veris.crm.order.soa.person.busi.score.scoretransfer;





import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class QueryScoreTransferorBean extends CSBizBean
{
	  public IDataset queryTransferorInfo(IData param) throws Exception
	    {

	        String sn = param.getString("B_MOBILE");
	        String S_ROWNUM = param.getString("S_ROWNUM","1");
	        String E_ROWNUM = param.getString("E_ROWNUM","99");
	        // 判断号码是否存在
	        IData input=new DataMap();
	        input.put("ROUTETYPE", "00");
	        input.put("ROUTEVALUE", "000");
	        input.put("KIND_ID", "BIP5A050_T5000050_0_0");
	        input.put("B_MOBILE", sn);
	        input.put("S_ROWNUM", S_ROWNUM);
	        input.put("E_ROWNUM", E_ROWNUM);
	        IDataset dataset = busiCallIBOSS(input);
	        return dataset;
	    }

	  
	  public IDataset queryTransferorInfoHis(IData param) throws Exception
	    {

	        String sn = param.getString("L_MOBILE");
	        String S_ROWNUM = param.getString("S_ROWNUM","1");
	        String E_ROWNUM = param.getString("E_ROWNUM","99");
	        // 判断号码是否存在
	        IData input=new DataMap();
	        input.put("ROUTETYPE", "00");
	        input.put("ROUTEVALUE", "000");
	        input.put("KIND_ID", "BIP5A051_T5000051_0_0");
	        input.put("L_MOBILE", sn);
	        input.put("ASSIGNEE_STATUS", param.getString("ASSIGNEE_STATUS", "00"));
	        input.put("START_TIME", param.getString("START_DATE",""));
	        input.put("END_TIME", param.getString("END_DATE",""));
	        input.put("S_ROWNUM", S_ROWNUM);
	        input.put("E_ROWNUM", E_ROWNUM);
		        // 获取客户可用积分余额
	    /*   IData Assigneeset = busiCallIBOSS(input);
	        IData AssigneeInfo=new DataMap();
	        if (IDataUtil.isNotEmpty(Assigneeset))
	        {
	            if("00".equals(Assigneeset.getString("X_RESULTCODE"))){
	            	AssigneeInfo=Assigneeset.getData("AssigneeList");
	            }
	        }*/
	        IDataset dataset = busiCallIBOSS(input);

	        
	        return dataset;
	    }

	
	  
	  public IDataset queryTransferPoint(IData param) throws Exception
	    {
		     String L_MOBILE = param.getString("L_MOBILE", "");// 
		        String B_MOBILE = param.getString("B_MOBILE", "");// 
		        String START_TIME = param.getString("START_DATE", "");// 
		        String END_TIME = param.getString("END_DATE", "");// 
		        //String state = pageData.getString("STATE", "");// 状态
		   


		        IData inputData = new DataMap();
		        inputData.put("ROUTETYPE", "00");
		        inputData.put("ROUTEVALUE", "000");
		        inputData.put("KIND_ID", "BIP5A054_T5000054_0_0");
		        inputData.put("L_MOBILE", L_MOBILE);// 号码
		        inputData.put("B_MOBILE", B_MOBILE);// 号码
		        inputData.put("START_TIME", START_TIME);// 号码
		        inputData.put("END_TIME", END_TIME);// 号码
		        inputData.put("S_ROWNUM", "1");// 号码
		        inputData.put("E_ROWNUM", "99");// 号码
		        //inputData.put("TRADE_ID",SeqMgr.getTradeId()+String.format("%04d", (int)(Math.random()*10000)));// 
		        inputData.put("TRADE_SEQ","");// 
		        
		        
		        IDataset dataset = busiCallIBOSS(inputData);
		        return dataset;
	    }
	  
	  
	    public IDataset submitProcess(IData param) throws Exception
	    {

	        String L_MOBILE = param.getString("L_MOBILE", "");// 
	        String B_MOBILE = param.getString("B_MOBILE", "");// 
	        String TRANSFER_POINT = param.getString("TRANSFER_POINT", "");// 
	        //String state = pageData.getString("STATE", "");// 状态
	   


	        IData inputData = new DataMap();
	        inputData.put("ROUTETYPE", "00");
	        inputData.put("ROUTEVALUE", "000");
	        inputData.put("KIND_ID", "BIP5A052_T5000052_0_0");
	        inputData.put("L_MOBILE", L_MOBILE);// 号码
	        inputData.put("B_MOBILE", B_MOBILE);// 号码
	        inputData.put("TRANSFER_POINT", TRANSFER_POINT);// 号码
	        inputData.put("TRADE_ID", SeqMgr.getTradeId()+String.format("%04d", (int)(Math.random()*10000)));// 
	        inputData.put("USER_ID", getVisit().getStaffId());//
	        inputData.put("TRADE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());//
	        inputData.put("ORGID", "0003");// 操作类型
	      
	 //       IData infos =  busiCallIBOSS(inputData);
	        IDataset dataset = busiCallIBOSS(inputData);
	        return dataset;
	    }
	  
	    
	
	  public IDataset busiCallIBOSS(IData input) throws Exception
	    {
	        IDataset interfaceData = IBossCall.invokeIBossInterface(input);  
	          
	        return interfaceData;
	    }
	 
}
package com.asiainfo.veris.crm.order.soa.person.busi.valuecard;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeFeeDeviceQry;

/**
 * 
 * 有价卡赠送无客户号码信息查询
 * @author zyz
 * 
 *
 */
public class GiveValueCardNotPhoneSVC extends CSBizService{

	private static final long serialVersionUID = 1L;

	
	static Logger logger=Logger.getLogger(GiveValueCardNotPhoneSVC.class);
    
	/**
	 * 
	 * @param inparam
	 * @return
	 * @throws Exception
	 */
    public IDataset queryValueCardNotPhoneInfo(IData inparam) throws Exception{
    	
    	try {
        	String cityCode = inparam.getString("CITY_CODE","");
        	//审批工单号
        	String staffId = inparam.getString("STAFF_ID","");
        	//部门编码
        	String updateDepartId=inparam.getString("UPDATE_DEPART_ID");
        	
        	String startSaleTime = inparam.getString("START_DATE");
            if(StringUtils.isNotEmpty(startSaleTime)){
            	startSaleTime = startSaleTime+SysDateMgr.START_DATE_FOREVER;
            }	
            
        	String endSaleTime = inparam.getString("END_DATE");
            if(StringUtils.isNotEmpty(endSaleTime)){
            	 endSaleTime = endSaleTime+SysDateMgr.END_DATE;
            }
        
        	
        	//客户号码
        	String rsrvStr7=inparam.getString("RSRV_STR7");
        	
        	IDataset result = TradeFeeDeviceQry.qryValueCardNotPhoneByCondition(cityCode, staffId, startSaleTime, endSaleTime, updateDepartId, rsrvStr7, getPagination());
        	return result;
		} catch (Exception e) {
			// TODO: handle exception
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}

    }
    
    /**
     * 根据查询条件补录有价卡无客户号码信息
     * @param inparam
     * @throws Exception
     */
    public IDataset updateValueCardNotPhoneInfoByCond(IData inparam)  throws Exception{
    	try {
    		//获取客户号码
			String custPhone=inparam.getString("custPhone");
			
			//客户姓名
			String customerName=inparam.getString("customerName");
			//集团名称
			String groupName=inparam.getString("groupName");
			//赠送人名称
			String giveName=inparam.getString("giveName");
			
			
			
			IDataset  giveValueCardList=new DatasetList(inparam.getString("NOTPHONE_TABLE","{}"));
			IData msg=new DataMap();
			IDataset list=new DatasetList();
			if(IDataUtil.isNotEmpty(giveValueCardList)){
				//成功条数
				 int  count=0; 
			      for(int i=0;i<giveValueCardList.size();i++){
			    	   IData data=giveValueCardList.getData(i);
			    	   //卡号
			    	   String cardNumber=data.getString("CARD_NUMBER");
			    	  
			    	  boolean flag=TradeFeeDeviceQry.updateValueCardNotPhoneInfoByCond(cardNumber, custPhone, customerName, groupName, giveName);
			    	  if(flag){
			    		  //补录成功
			    		  count=count+1;
			    	  }
			      }
			    msg.put("count", count);
			    list.add(msg);
				return list;
			}else{
				//页面传值错误
				msg.put("count", 0);
				return list;
			}
		} catch (Exception e) {
			// TODO: handle exception
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}
    }

    /**
     * 有价卡补录信息导入
     * @param set
     * @throws Exception
     */
    public  IDataset importValueCardNotPhoneinfo(IData input) throws Exception{
    	try {
    		 IData idata=new DataMap();
    		 IDataset iDataset=new DatasetList();
    		 //获取有价卡补录信息
    		 IDataset set=TradeFeeDeviceQry.getValueCardNotPhoneinfo(input);
    		 //执行导入,返回成功条数
    		 String msg=TradeFeeDeviceQry.importValueCardNotPhoneinfo(set);
    		 idata.put("msg", msg);
    		 iDataset.add(idata);
    		return iDataset;
		} catch (Exception e) {
			if(logger.isInfoEnabled()) logger.info(e);
		     throw e;
		}
    }
    
  
   
}

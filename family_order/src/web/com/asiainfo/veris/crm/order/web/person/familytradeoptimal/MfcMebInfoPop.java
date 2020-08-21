package com.asiainfo.veris.crm.order.web.person.familytradeoptimal;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
public abstract class MfcMebInfoPop extends PersonBasePage {
	public abstract void setAddInfos(IDataset addInfos);
	public abstract void setHidInfo(IData hidInfo);

	  
	/**
	 * 添加副号码的校验
	 */
	public void checkAddMeb(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		IDataset rtDataset = CSViewCall.call(this,"SS.FamilyAllNetBusiManageSVC.checkAddMeb", pageData);
		this.setAjax(rtDataset.getData(0));

	}
	public void init(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		setHidInfo(pageData);
	}
	/**
	 * 业务提交
	 */
	
	public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        
        String tradeOption = pageData.getString("TRADE_OPTION","");
	        if(tradeOption.equals("CREATE")){
	        	 IData search_info = new DataMap();
					search_info.put("PRODUCT_CODE",pageData.getString("PRODUCT_CODE"));

	        	 search_info.put("CUSTOMER_PHONE", pageData.get("SERIAL_NUMBER"));
	        	 search_info.put("POID_CODE", pageData.get("POID_CODE"));
	        	 search_info.put("POID_LABLE", pageData.get("POID_LABLE"));

	        	 //新在前台传入了prouduct_offer_id
	        	 search_info.put("PRODUCT_OFFERING_ID",pageData.get("PRODUCT_OFFERING_ID") );
	        	  IDataset ProductOrderMember=new DatasetList();         	
	        	  IDataset MemNumbers=new DatasetList(pageData.getString("MEB_LIST")); 
	        	  if(IDataUtil.isNotEmpty(MemNumbers)){
	        		  for(int i=0;i<MemNumbers.size();i++){
	        			//  if(MemNumbers.getData(i).getString("INST_ID_B").equals("9999")){
		        		  IData mebinfo =new DataMap();
		        		  mebinfo.put("MEM_TYPE", "1") ; 
		        		  mebinfo.put("MEM_AREA_CODE", "") ; 
		        		  mebinfo.put("MEM_NUMBER", MemNumbers.getData(i).getString("SERIAL_NUMBER_B"));
		        		  mebinfo.put("MEM_LABLE", MemNumbers.getData(i).getString("MEM_LABLE")); 
		        		  mebinfo.put("MEM_ORDER_NUMBER", "") ; 
		        		  mebinfo.put("FINISH_TIME", "") ; 
		        		  mebinfo.put("NOTES", "") ; 
		        		  mebinfo.put("EFF_TIME", "") ; 
		        		  mebinfo.put("EXP_TIME", "") ; 
		        		  ProductOrderMember.add(mebinfo);
		        	//	  memPhone = MemNumbers.getData(i).getString("SERIAL_NUMBER_B");
		        	//  }
	        	  }
	        
	        	  }	
	        //	  ExtendInfo.put("INFO_CODE", "");
	        //	  ExtendInfo.put("INFO_VALUE", "");
	        	//  IData search_info = new DataMap();
	        	  search_info.put("PRODUCT_ORDER_MEMBER", ProductOrderMember);
	        	  search_info.put("ACTION", "50");
	        	  search_info.put("ORDER_SOURCE", "01"); 
	        	  log.debug("前台传search_info="+search_info);
		   ;
		        	  IData result =new DataMap();
		        		 IDataset rtDataset = CSViewCall.call(this, "SS.FamilyAllNetBusiManageSVC.controlInfo", search_info);
		 	          
		 	        	if(IDataUtil.isNotEmpty(rtDataset)){
		 	        		result =rtDataset.getData(0);
		 	        		
		 	        	}	        
		 	            log.debug("rtDataset"+result);
		 	            
		 	            this.setAjax(result);
		        	
	           
	        }else if(tradeOption.equals("DELETE")){
				IData search_info = new DataMap();
				IData MemNumbers = new DataMap(pageData.getString("MEB_LIST"));
				IDataset ProductOrderMember = new DatasetList();
				String custPhone="";
				if (IDataUtil.isNotEmpty(MemNumbers)) {								
						IData mebinfo = new DataMap();
						String mebNumber=MemNumbers.getString("SERIAL_NUMBER_B");	
						IData map=new DataMap();
						map.put("SERIAL_NUMBER_B", mebNumber);
						map.put("RELATION_TYPE_CODE", "MF");
						map.put("ROLE_CODE_B", "2");
						IDataset MainPhoneInfo= CSViewCall.call(this, "SS.FamilyAllNetBusiManageSVC.getMfcMainPhoneInfo", map);
						//custPhone=MainPhoneInfo.getData(0).getString("CUSTOMER_PHONE");
						custPhone=pageData.getString("SERIAL_NUMBER");

						mebinfo.put("MEM_TYPE", "1");
						mebinfo.put("MEM_AREA_CODE", "");
						mebinfo.put("MEM_NUMBER",mebNumber );// MemNumbers.getString("SERIAL_NUMBER_B")
						mebinfo.put("MEM_ORDER_NUMBER", "");
						mebinfo.put("FINISH_TIME", "");
						mebinfo.put("NOTES", "");
						mebinfo.put("EFF_TIME", "");
						mebinfo.put("EXP_TIME", "");
						ProductOrderMember.add(mebinfo);	
					
						//String productCode = pageData.getString("PRODUCT_CODE");
						//String poCode=StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000002");

			        //	if(poCode.equals(productCode)){
					//		search_info.put("PRODUCT_CODE", "MFC000002");
			        //	}else {
					//		search_info.put("PRODUCT_CODE", "MFC000001");

			        //	}
						search_info.put("PRODUCT_CODE",pageData.getString("PRODUCT_CODE"));

				       search_info.put("CUSTOMER_PHONE", custPhone);
				       search_info.put("PRODUCT_ORDER_MEMBER", ProductOrderMember);
				       search_info.put("ACTION", "51");
				       search_info.put("ORDER_SOURCE", "01");
				       search_info.put("PRODUCT_OFFERING_ID",pageData.getString("PRODUCT_OFFERING_ID") );

			//	search_info.put("DEL_TAG",pageData.getString("DEL_TAG") );

				IDataset rtDataset = CSViewCall.call(this,"SS.FamilyAllNetBusiManageSVC.controlInfo", search_info);
				IData result =new DataMap();
	        	if(IDataUtil.isNotEmpty(rtDataset)){
	        		result =rtDataset.getData(0);
	        		
	        	}	        
				this.setAjax(result);
			}
			}else if(tradeOption.equals("CREATEFAMILY")){
				IData result =new DataMap();
				 IData param =new DataMap();
	        	 param.put("CUSTOMER_PHONE", pageData.get("AUTH_SERIAL_NUMBER"));
	        	 param.put("PRODUCT_CODE", pageData.get("PRODUCT_CODE"));

	        //      String POOrderNumber=SeqMgr.getCenImportId(); //业务订单
	              IData search_info = new DataMap();
	              	search_info.put("ORDER_SOURCE_ID", "01");
		      		search_info.put("CUSTOMER_TYPE", "1");
		      		search_info.put("CUSTOMER_PHONE", pageData.get("SERIAL_NUMBER"));	
		      		search_info.put("OPERATION_SUBTYPE_ID", "00");
		        	 search_info.put("PRODUCT_CODE", pageData.get("PRODUCT_CODE"));
		      		IDataset rtData = CSViewCall.call(this, "SS.FamilyAllNetBusiManageSVC.checkMeb", param);
		        	String type =rtData.getData(0).getString("RSP_CODE");
		        	String desc =rtData.getData(0).getString("RSP_DESC");

		        	if("00".equals(type)){
		        		IDataset rtDataset = CSViewCall.call(this, "SS.FamilyAllNetBusiManageSVC.putMeb", search_info);
			        	
			        	if(IDataUtil.isNotEmpty(rtDataset)){
			        		result =rtDataset.getData(0);
			        		
			        	}	        	
			       	 log.debug("我要打印这个java result啦="+result);
			            this.setAjax(result);
		        	}else{
		        		result.put("RSP_CODE", "2999");
		        		result.put("RSP_DESC", "该号码已经停机或者销户，无法办理业务！");
		        		this.setAjax(result);
		        	}
	        	
	        }
	        else if(tradeOption.equals("DESTROY")){
	        	
		        //      String POOrderNumber=SeqMgr.getCenImportId(); //业务订单
		              IData search_info = new DataMap();
						search_info.put("PRODUCT_CODE",pageData.getString("PRODUCT_CODE"));
				//String productCode = pageData.getString("PRODUCT_CODE");
				//String poCode=StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000002");

	        	//if(poCode.equals(productCode)){
				//	search_info.put("PRODUCT_CODE", "MFC000002");
	        	//}else {
				//	search_info.put("PRODUCT_CODE", "MFC000001");
	        //	}
		              	search_info.put("ORDER_SOURCE_ID", "01");
			      		search_info.put("CUSTOMER_TYPE", "1");
			      		search_info.put("CUSTOMER_PHONE", pageData.get("SERIAL_NUMBER"));	
			      		search_info.put("OPERATION_SUBTYPE_ID", "01");
			        	search_info.put("PRODUCT_OFFERING_ID",pageData.get("PRODUCT_OFFERING_ID") );

				        	IDataset rtDataset = CSViewCall.call(this, "SS.FamilyAllNetBusiManageSVC.putMeb", search_info);
				        	IData result =new DataMap();
				        	if(IDataUtil.isNotEmpty(rtDataset)){
				        		result =rtDataset.getData(0);
				        		
				        	}	        	
				       	 log.debug("我要打印这个java result啦="+result);
				            this.setAjax(result);
			        	

		           
		        }
        }
        
    }


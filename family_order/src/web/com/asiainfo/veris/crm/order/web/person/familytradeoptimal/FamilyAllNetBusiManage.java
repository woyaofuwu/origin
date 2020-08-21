package com.asiainfo.veris.crm.order.web.person.familytradeoptimal;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FamilyAllNetBusiManage extends PersonBasePage{

	public abstract void setViceInfos(IDataset viceinfos);
	public abstract void setMebInfos(IDataset mebinfos);
	 public abstract void setUserInfo(IData userInfo);
	  public abstract void setCustInfo(IData custInfo);
	  public abstract void setInfo(IData info);
	public abstract void setVerifyModeList(IDataset verifymodeLists);
	  public abstract void setMebTranInfos(IDataset mebInfos);

	public void onInit(IRequestCycle cycle) throws Exception
	
	{
		setCustInfo(null);
		setUserInfo(null);
	}
	/**
	 * 查询家庭网状况
	 */
	public void loadInfo(IRequestCycle cycle) throws Exception{
		IData pageData = getData();
		String serial_number = pageData.getString("SERIAL_NUMBER");
		String user_id = pageData.getString("USER_ID");
		String eparchy_code = pageData.getString("EPARCHY_CODE");
		//查询号码有没有开通家庭网，开通了，就可以进行 注销、添加成员、删除成员的操作
		//买有开通，则只可以进行组网操作
		IData search_info = new DataMap();
		search_info.put("USER_ID", user_id);
		search_info.put("SERIAL_NUMBER", serial_number);
		search_info.put("EPARCHY_CODE", eparchy_code);
		IDataset family = CSViewCall.call(this,"SS.FamilyAllNetBusiManageSVC.loadInfo", search_info);
		// 准备一个返回参数
		IData return_data = new DataMap();
		//根据家庭网的返回值，来规定操作内容 flag为0：没有创建家庭网，只可以组网 flag为1：副卡，只可以进行副卡退网,flag为2：主卡，可以进行 销户、添加副卡、删除副卡
		if(IDataUtil.isNotEmpty(family)){
			for(int i = 0 ; i<family.size(); i++){
				IData familyInfo = family.getData(i);
				String role_code_b = familyInfo.getString("ROLE_CODE_B");
				if("1".equals(role_code_b)){
					familyInfo.put("ROLE_CODE_B", "主号");
				}else{
					familyInfo.put("ROLE_CODE_B", "副号");
				}
				String remrak =familyInfo.getString("REMARK");
				if(StringUtils.contains(remrak, "MFC000002")){		
					familyInfo.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000002"));
				}else if (StringUtils.contains(remrak, "MFC000003")) {
					familyInfo.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000003"));
				}else if (StringUtils.contains(remrak, "MFC000004")) {
					familyInfo.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000004"));
				}else if (StringUtils.contains(remrak, "MFC000005")) {
					familyInfo.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000005"));
				}else if (StringUtils.contains(remrak, "MFC000006")) {
					familyInfo.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000006"));
				}else if (StringUtils.contains(remrak, "MFC000007")) {
					familyInfo.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000007"));
				}else if (StringUtils.contains(remrak, "MFC000008")) {
					familyInfo.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000008"));
				}else if (StringUtils.contains(remrak, "MFC000009")) {
					familyInfo.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000009"));
				}else if (StringUtils.contains(remrak, "MFC000010")) {
					familyInfo.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000010"));
				}else if (StringUtils.contains(remrak, "MFC000011")) {
					familyInfo.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000011"));
				}else{
					familyInfo.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000001"));
				}
				setViceInfos(family);
			}
		}else{
			return_data.put("flag", "0");
		}


		IDataset limit_producttypes = StaticUtil.getStaticList("PRODUCT_TYPES"); // 配置限制批量前台办理的业务
		IDataset limit_producttypesnew = new DatasetList();
		if (IDataUtil.isNotEmpty(limit_producttypes))
		{
			for (int i=0;i<limit_producttypes.size();i++)
			{
				IData limit_producttype =  limit_producttypes.getData(i);
				if (StringUtils.equals(limit_producttypes.getData(i).getString("DATA_ID"), "MFC000005")
						|| StringUtils.equals(limit_producttypes.getData(i).getString("DATA_ID"), "MFC000004")
						|| StringUtils.equals(limit_producttypes.getData(i).getString("DATA_ID"), "MFC000002")
						|| StringUtils.equals(limit_producttypes.getData(i).getString("DATA_ID"), "MFC000001"))

				{
					limit_producttypesnew.add(limit_producttype);
				}
			}
		}

		this.setVerifyModeList(limit_producttypesnew);

		setAjax(return_data);
	}


	/**
	 * 查询家庭网状况
	 */
	public void loadMebInfo(IRequestCycle cycle) throws Exception{
		IData pageData = getData();
		String serial_number_a = pageData.getString("SERIAL_NUMBER_A");
		String serial_number_b = pageData.getString("HID_SERIAL_NUMBER_B");
		String user_id_a = pageData.getString("HID_USER_ID_A");
		String role_codeb = pageData.getString("ROLE_CODE_B");

		IData  search_info =new DataMap();
		search_info.put("SERIAL_NUMBER_A",serial_number_a );
		search_info.put("SERIAL_NUMBER_B",serial_number_b );
		search_info.put("USER_ID_A",user_id_a );
		search_info.put("ROLE_CODE_B",role_codeb );
		IDataset viceinfos = new DatasetList();
		IDataset family_info = CSViewCall.call(this, "SS.FamilyAllNetBusiManageSVC.loadMebInfo", search_info);
		//IData remark =new DataMap();
		if(IDataUtil.isNotEmpty(family_info)){

			for(int a=0;a<family_info.size();a++){
				IData family_two= family_info.getData(a);
				String PRODUCT_OFFERING_ID = family_two.getString("RSRV_STR2");
				family_two.put("PRODUCT_OFFERING_ID", PRODUCT_OFFERING_ID);
				String role_code_b = family_two.getString("ROLE_CODE_B");
				if("1".equals(role_code_b)){
					family_two.put("ROLE_CODE_B", "主号");
				}else{
					family_two.put("ROLE_CODE_B", "副号");

				}
				String remrak =family_two.getString("REMARK");
				if(StringUtils.contains(remrak, "MFC000002")){		
					family_two.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000002"));
				}else if(StringUtils.contains(remrak, "MFC000003")){
					family_two.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000003"));
				}else if(StringUtils.contains(remrak, "MFC000004")){
					family_two.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000004"));
				}else if(StringUtils.contains(remrak, "MFC000005")){
					family_two.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000005"));
				}else if(StringUtils.contains(remrak, "MFC000006")){
					family_two.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000006"));
				}else if(StringUtils.contains(remrak, "MFC000007")){
					family_two.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000007"));
				}else if(StringUtils.contains(remrak, "MFC000008")){
					family_two.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000008"));
				}else if(StringUtils.contains(remrak, "MFC000009")){
					family_two.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000009"));
				}else if(StringUtils.contains(remrak, "MFC000010")){
					family_two.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000010"));
				}else if(StringUtils.contains(remrak, "MFC000011")){
					family_two.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000011"));
				}else{
					family_two.put("PRODUCT_CODE", StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000001"));
				}
////			viceinfos.add(flag);
//			viceinfos.add(family.getData(i));
				viceinfos.add(family_two);
//
				
			}
			setMebInfos(viceinfos);
		}
		
		
		
	
		}

			

	/**
	 * 添加副号码的校验
	 */
	public void checkAddMeb(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		  IData result =new DataMap();    
		   IDataset rtDataset = CSViewCall.call(this,"SS.FamilyAllNetBusiManageSVC.checkAddMeb", pageData);
		      this.setAjax(rtDataset.getData(0));
	
	}

	/**
	 * 
	 */
	public void bossInfo(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		IDataset rtDataset = CSViewCall.call(this,
				"SS.FamilyAllNetBusiManageSVC.bossInfo", pageData);
		IDataset return_data =new DatasetList();
		
		if (IDataUtil.isNotEmpty(rtDataset)) {
			for(int i=0;i<rtDataset.size();i++){
				IData data =new DataMap();//定义一个返回参数
				data.put("RSP_CODE",rtDataset.getData(i).getString("RSP_CODE") );
				data.put("RSP_DESC",rtDataset.getData(i).getString("RSP_DESC") );
				data.put("RSP_FILE",rtDataset.getData(i).getString("RSP_FILE") );
				return_data.add(data);
				
			}
			
		} 
		setAjax(return_data);
		setMebInfos(return_data);
	}
	
	/**
	 * 省BOSS查询BBOSS实时接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public void bossRealTimeInfo(IRequestCycle cycle) throws Exception{
		IData pageData = getData();
		IDataset rtDataset =CSViewCall.call(this,"SS.FamilyAllNetBusiManageSVC.bossRealTimeInfo", pageData);
		IDataset return_data =new DatasetList();//成员数据列表
		IDataset tranReturn_data =new DatasetList();//在途成员数据列表
		IData result = new DataMap();//返回结果
		if (IDataUtil.isNotEmpty(rtDataset)){
			String rsp_code = rtDataset.getData(0).getString("RSP_CODE");
			String rsp_desc = rtDataset.getData(0).getString("RSP_DESC");
			if(StringUtils.isNotBlank(rsp_code)){
				if("00".equals(rsp_code)){//RSP_CODE="00"显示数据,否则页面报错
					IDataset rsp_result = rtDataset.getData(0).getDataset("RSP_RESULT");//查询结果
					if(IDataUtil.isNotEmpty(rsp_result)){
						for(int k=0;k<rsp_result.size();k++){
							//成员列表
							IDataset member_list = rsp_result.getData(k).getDataset("MEMBER_LIST");
							if(IDataUtil.isNotEmpty(member_list)){
								for(int i=0;i<member_list.size();i++){
									IData data =new DataMap();
									if("MFC000001".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","全国亲情网");
									}else if("MFC000003".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","5G家庭会员群组");
									}else if("MFC000004".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","5G家庭套餐群组");
									}else if("MFC000005".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","5G融合套餐群组");
									}else if("MFC000006".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","全国亲情网(支付宝版月包)");
									}else if("MFC000007".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","全国亲情网(支付宝版季包)");
									}else if("MFC000008".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","全国亲情网(支付宝版年包)");
									}else if("MFC000009".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","全国亲情网(异网版月包)");
									}else if("MFC000010".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","全国亲情网(异网版季包)");
									}else if("MFC000011".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","全国亲情网(异网版年包)");
									}else{
										data.put("PRODUCT_CODE","全国亲情网（自付版）");
									}
									data.put("PRODUCT_OFFERING_ID",rsp_result.getData(k).getString("PRODUCT_OFFERING_ID"));
									data.put("CUSTOMER_PHONE",rsp_result.getData(k).getString("CUSTOMER_PHONE") );
									String poidCode = rsp_result.getData(k).getString("POID_CODE","");
									String poidLable =rsp_result.getData(k).getString("POID_LABLE","");

									data.put("POID_CODE",StringUtils.isNotBlank(poidCode)? poidCode :rsp_result.getData(k).getString("PRODUCT_OFFERING_ID").substring(16));
									data.put("POID_LABLE",StringUtils.isNotBlank(poidLable)? poidLable : "群"+rsp_result.getData(k).getString("PRODUCT_OFFERING_ID").substring(16));
									String memFlag = member_list.getData(i).getString("MEM_FLAG");
									if("1".equals(memFlag)){
										data.put("MEM_FLAG","主号");
									}else{
										data.put("MEM_FLAG","成员");
										String memLable = member_list.getData(i).getString("MEM_LABLE");
										data.put("MEM_LABLE",StringUtils.isNotBlank(memLable)? memLable :rsp_result.getData(k).getString("MEM_NUMBER").substring(7));
									}
									String memType=member_list.getData(i).getString("MEM_TYPE");
									if("1".equals(memType)){
										data.put("MEM_TYPE","移动号码");
									}else if("3".equals(memType)){
										data.put("MEM_TYPE","异网手机号码");
									}else{
										data.put("MEM_TYPE","固话号码");
									}
									data.put("MEM_AREA_CODE",member_list.getData(i).getString("MEM_AREA_CODE"));
									data.put("MEM_NUMBER",member_list.getData(i).getString("MEM_NUMBER"));
									data.put("EFF_TIME",member_list.getData(i).getString("EFF_TIME"));
									data.put("EXP_TIME",member_list.getData(i).getString("EXP_TIME"));
									data.put("NOTES",member_list.getData(i).getString("NOTES"));
									return_data.add(data);
								}
							}
							//显示在途成员信息列表
							IDataset memberTran_list = rsp_result.getData(k).getDataset("MEM_TRAN_LIST");
							if(IDataUtil.isNotEmpty(memberTran_list)){//成员列表不为空
								for(int i=0;i<memberTran_list.size();i++){
									IData data =new DataMap();
									if("MFC000001".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","全国亲情网");
									}else if("MFC000003".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","5G家庭会员群组");
									}else if("MFC000004".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","5G家庭套餐群组");
									}else if("MFC000005".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","5G融合套餐群组");
									}else if("MFC000006".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","全国亲情网(支付宝版月包)");
									}else if("MFC000007".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","全国亲情网(支付宝版季包)");
									}else if("MFC000008".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","全国亲情网(支付宝版年包)");
									}else if("MFC000009".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","全国亲情网(异网版月包)");
									}else if("MFC000010".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","全国亲情网(异网版季包)");
									}else if("MFC000011".equals(rsp_result.getData(k).getString("PRODUCT_CODE"))){
										data.put("PRODUCT_CODE","全国亲情网(异网版年包)");
									}else{
										data.put("PRODUCT_CODE","全国亲情网（自付版）");
									}
									data.put("PRODUCT_OFFERING_ID",rsp_result.getData(k).getString("PRODUCT_OFFERING_ID"));
									data.put("CUSTOMER_PHONE",rsp_result.getData(k).getString("CUSTOMER_PHONE") );

									String poidCode = rsp_result.getData(k).getString("POID_CODE","");
									String poidLable =rsp_result.getData(k).getString("POID_LABLE","");

									data.put("POID_CODE",StringUtils.isNotBlank(poidCode)? poidCode :rsp_result.getData(k).getString("PRODUCT_OFFERING_ID").substring(16));
									data.put("POID_LABLE",StringUtils.isNotBlank(poidLable)? poidLable : "群"+rsp_result.getData(k).getString("PRODUCT_OFFERING_ID").substring(16));

									String memFlag = memberTran_list.getData(i).getString("MEM_FLAG");
									if("1".equals(memFlag)){
										data.put("MEM_FLAG","主号");
									}else{
										data.put("MEM_FLAG","成员");
										String memLable = memberTran_list.getData(i).getString("MEM_LABLE");
										data.put("MEM_LABLE",StringUtils.isNotBlank(memLable)? memLable :memberTran_list.getData(i).getString("MEM_NUMBER").substring(7));
									}
									String memType=memberTran_list.getData(i).getString("MEM_TYPE");
									if("1".equals(memType)){
										data.put("MEM_TYPE","移动号码");
									}else if("3".equals(memType)){
										data.put("MEM_TYPE","异网手机号码");
									}else{
										data.put("MEM_TYPE","固话号码");
									}
									data.put("MEM_AREA_CODE",memberTran_list.getData(i).getString("MEM_AREA_CODE"));
									data.put("MEM_NUMBER",memberTran_list.getData(i).getString("MEM_NUMBER"));
									data.put("NOTES",memberTran_list.getData(i).getString("NOTES"));
									tranReturn_data.add(data);
								}
							}
						}
					}
				}
			}else{
				rsp_desc="网络异常";
			}
			result.put("RSP_CODE",rsp_code);
			result.put("RSP_DESC",rsp_desc);
		}
		log.debug("web层java的返回"+return_data);
		setAjax(result);
		setMebInfos(return_data);
		setMebTranInfos(tranReturn_data);
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
	        	 //产品编码修改为从前台页面获取
				search_info.put("PRODUCT_CODE",pageData.getString("PRODUCT_CODE"));
	        	 search_info.put("CUSTOMER_PHONE", pageData.get("SERIAL_NUMBER"));
	        	 //新在前台传入了prouduct_offer_id
	        	 search_info.put("PRODUCT_OFFERING_ID",pageData.get("PRODUCT_OFFERING_ID") );
	        	  IDataset ProductOrderMember=new DatasetList();         	
	        	  IDataset MemNumbers=new DatasetList(pageData.getString("MEB_LIST"));
	        	  int validMemCount = pageData.getInt("VALID_MEM_COUNT");
	        	  if(validMemCount+MemNumbers.size()>19){
	        	  	  IData resultMap = new DataMap();
					  resultMap.put("RSP_CODE","2999");
					  resultMap.put("RSP_DESC","群組成员添加超过19个，当前已有成员"+validMemCount);
					  resultMap.put("OPR_TIME", SysDateMgr.getSysTime());
					  resultMap.put("CUSTOMER_PHONE",pageData.get("SERIAL_NUMBER"));
					  this.setAjax(resultMap);
					  return;
				  }


	        	  if(IDataUtil.isNotEmpty(MemNumbers)){
	        		  for(int i=0;i<MemNumbers.size();i++){
	        			//  if(MemNumbers.getData(i).getString("INST_ID_B").equals("9999")){
		        		  IData mebinfo =new DataMap();
		        		  mebinfo.put("MEM_TYPE", "1") ; 
		        		  mebinfo.put("MEM_AREA_CODE", "") ; 
		        		  mebinfo.put("MEM_NUMBER", MemNumbers.getData(i).getString("SERIAL_NUMBER_B")); 
		        		  mebinfo.put("MEM_ORDER_NUMBER", "") ; 
		        		  mebinfo.put("MEM_LABLE",MemNumbers.getData(i).getString("MEM_LABLE") ) ; 
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
					//	IDataset MainPhoneInfo= CSViewCall.call(this, "SS.FamilyAllNetBusiManageSVC.getMfcMainPhoneInfo", map);
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
	              	search_info.put("ORDER_SOURCE", "01");
		      		search_info.put("CUSTOMER_TYPE", "1");
		      		search_info.put("CUSTOMER_PHONE", pageData.get("SERIAL_NUMBER"));	
		      		search_info.put("ACTION", "00");
		        	 search_info.put("PRODUCT_CODE", pageData.get("PRODUCT_CODE"));
		        	search_info.put("POID_CODE",pageData.get("POID_CODE") );
		        	search_info.put("POID_LABLE", pageData.getString("POID_LABLE",""));
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
		        		result.put("RSP_DESC", desc);
		        		this.setAjax(result);
		        	}
	        	
	        }
	        else if(tradeOption.equals("DESTROY")){
	        	
		        //      String POOrderNumber=SeqMgr.getCenImportId(); //业务订单
		             IData search_info = new DataMap();
			search_info.put("PRODUCT_CODE", pageData.get("PRODUCT_CODE"));
		        //  	String productCode = pageData.getString("PRODUCT_CODE");
				//	String poCode=StaticUtil.getStaticValue("PRODUCT_TYPES", "MFC000002");

		        //	if(poCode.equals(productCode)){
				//		search_info.put("PRODUCT_CODE", "MFC000002");
		       // 	}else {
				//		search_info.put("PRODUCT_CODE", "MFC000001");

		        //	}
		              	search_info.put("ORDER_SOURCE", "01");
			      		search_info.put("CUSTOMER_TYPE", "1");
			      		search_info.put("CUSTOMER_PHONE", pageData.get("SERIAL_NUMBER"));	
			      		search_info.put("ACTION", "01");
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


package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.ArrayList;
import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class CheckIsExistsProducts implements ITradeAction{

	public void executeAction(BusiTradeData btd) throws Exception
    {
		String tradeTypeCode=btd.getTradeTypeCode();
		
		boolean isNeedVerify=false;
		String psptTypeCode=null;
		String psptId=null;
		String isRealName="";
		
		List<String> productIds=new ArrayList<String>();
		
		
		if(tradeTypeCode.equals("10")){	//开户
			IData pageParam=btd.getRD().getPageRequestData();
			String openType=pageParam.getString("OPEN_TYPE", "");
			String openTypeCode=pageParam.getString("OPEN_TYPE_CODE", "");
			
			//不验证物联网开户
			if (PersonConst.IOT_OPEN.equals(openType) || PersonConst.TD_OPEN.equals(openType)
					||PersonConst.IOT_OPEN.equals(openTypeCode)){
				return ;
			}
			
			//获取开户的产品ID
			List<ProductTradeData> userProduct=btd.get("TF_B_TRADE_PRODUCT");
			if(userProduct!=null&&userProduct.size()>0){
				for(int i=0,size=userProduct.size();i<size;i++){
					if(userProduct.get(i).getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
						isNeedVerify=true;
						String productId=userProduct.get(0).getProductId();
						productIds.add(productId);
						
						break;
					}
				}
			}
			
			List<CustomerTradeData> customerInfos=btd.get("TF_B_TRADE_CUSTOMER");
			CustomerTradeData customerInfo=customerInfos.get(0);
			
			psptTypeCode=customerInfo.getPsptTypeCode();
			psptId=customerInfo.getPsptId();
			isRealName=customerInfo.getIsRealName();
			
		}else if(tradeTypeCode.equals("110")){	//产品变更
			//获取开户的产品ID
			List<ProductTradeData> userProduct=btd.get("TF_B_TRADE_PRODUCT");
			if(userProduct!=null&&userProduct.size()>0){
				for(int i=0,size=userProduct.size();i<size;i++){
					if(userProduct.get(i).getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
						isNeedVerify=true;
						String productId=userProduct.get(0).getProductId();
						productIds.add(productId);
						
						break;
					}
				}
			}
			
			IData customerInfo=UcaInfoQry.qryCustomerInfoByCustId(btd.getRD().getUca().getCustId(),btd.getRoute());
			psptTypeCode=customerInfo.getString("PSPT_TYPE_CODE","");
			psptId=customerInfo.getString("PSPT_ID","");
			isRealName=customerInfo.getString("IS_REAL_NAME","");
			
		}else if(tradeTypeCode.equals("40")){	//携入用户开户
			//获取开户的产品ID
			List<ProductTradeData> userProduct=btd.get("TF_B_TRADE_PRODUCT");
			if(userProduct!=null&&userProduct.size()>0){
				for(int i=0,size=userProduct.size();i<size;i++){
					if(userProduct.get(i).getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
						isNeedVerify=true;
						String productId=userProduct.get(0).getProductId();
						productIds.add(productId);
						
						break;
					}
				}
			}
			
			List<CustomerTradeData> customerInfos=btd.get("TF_B_TRADE_CUSTOMER");
			CustomerTradeData customerInfo=customerInfos.get(0);
			
			psptTypeCode=customerInfo.getPsptTypeCode();
			psptId=customerInfo.getPsptId();
			isRealName=customerInfo.getIsRealName();
			
		}else if(tradeTypeCode.equals("100")){	//过户
			List<CustomerTradeData> customerIfos=btd.get("TF_B_TRADE_CUSTOMER");
			CustomerTradeData customerIfo=null;
			for(int i=0,size=customerIfos.size();i<size;i++){
				if(customerIfos.get(i).getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
					customerIfo=customerIfos.get(i);
					break;
				}
			}
			
			if(customerIfo!=null){
				psptTypeCode=customerIfo.getPsptTypeCode();
				psptId=customerIfo.getPsptId();
				isRealName=customerIfo.getIsRealName();
				
				IDataset allUserProds=UserProductInfoQry.queryAllUserValidMainProducts(btd.getRD().getUca().getUserId());
				if(IDataUtil.isNotEmpty(allUserProds)){
					
					isNeedVerify=true;
					
					for(int i=0,size=allUserProds.size();i<size;i++){
						String productId=allUserProds.getData(i).getString("PRODUCT_ID");
						productIds.add(productId);
					}
				}
			}
			
		}else if(tradeTypeCode.equals("310")){	//复机
			
			//获取开户的产品ID
			List<ProductTradeData> userProduct=btd.get("TF_B_TRADE_PRODUCT");
			if(userProduct!=null&&userProduct.size()>0){
				for(int i=0,size=userProduct.size();i<size;i++){
					if(userProduct.get(i).getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
						isNeedVerify=true;
						String productId=userProduct.get(0).getProductId();
						productIds.add(productId);
						
						break;
					}
				}
			}
			
			CustomerTradeData customerInfo=btd.getRD().getUca().getCustomer();
			
			psptTypeCode=customerInfo.getPsptTypeCode();
			psptId=customerInfo.getPsptId();
			isRealName=customerInfo.getIsRealName();
			
		}else if(tradeTypeCode.equals("60")){	//客户资料变更
			/*
			 * 核对用户是否从非实名制换成了实名制
			 * 或者
			 * 变更了证件信息
			 */
			List<CustomerTradeData> customerIfos=btd.get("TF_B_TRADE_CUSTOMER");
			CustomerTradeData customerIfo=null;
			for(int i=0,size=customerIfos.size();i<size;i++){
				if(customerIfos.get(i).getModifyTag().equals(BofConst.MODIFY_TAG_UPD)){
					customerIfo=customerIfos.get(i);
					break;
				}
			}
			
			if(customerIfo!=null){
				String custId=customerIfo.getCustId();
				
				String newIsRealName=customerIfo.getIsRealName();
				String newPsptTypeCode=customerIfo.getPsptTypeCode();
				String newPsptId=customerIfo.getPsptId();
				isRealName=customerIfo.getIsRealName();
				
				//客户原来的信息
				IData oldCustInfo=UcaInfoQry.qryCustomerInfoByCustId(custId);
				String oldIsRealName=oldCustInfo.getString("IS_REAL_NAME","");
				String oldPsptTypeCode=oldCustInfo.getString("PSPT_TYPE_CODE","");
				String oldPsptId=oldCustInfo.getString("PSPT_ID","");
				
				
				//只要存在一个变换就算是进行了变更
				if(("1".equals(newIsRealName)&&!oldIsRealName.equals("1"))
						||!newPsptTypeCode.equals(oldPsptTypeCode)
						||!newPsptId.equals(oldPsptId)){
					psptTypeCode=newPsptTypeCode;
					psptId=newPsptId;
					
					IDataset allUserProds=UserProductInfoQry.queryAllUserValidMainProducts(btd.getRD().getUca().getUserId());
					if(IDataUtil.isNotEmpty(allUserProds)){
						
						isNeedVerify=true;
						
						for(int i=0,size=allUserProds.size();i<size;i++){
							String productId=allUserProds.getData(i).getString("PRODUCT_ID");
							productIds.add(productId);
						}
						
					}

				}
				
			}
		}
		
		
		
		/*
		 * 开始进行验证
		 * 
		 */
		if(isNeedVerify&&productIds!=null&&productIds.size()>0){
			
			/*
			 * 判断是否办理了指定的产品信息
			 */
			boolean isOrderProduct=false;
			for(int i=0,size=productIds.size();i<size;i++){
				String productId=productIds.get(i);
				
				//查询产品是否有配置进行效验
				IDataset docRules=CommparaInfoQry.queryComparaByAttrAndCode1
						("CSM", "5212", "PRODUCT_DOC", productId);
				
				if(IDataUtil.isNotEmpty(docRules)){
					String productName=docRules.getData(0).getString("PARA_CODE2","");
					
					//如果不是实名制
					if(!(isRealName!=null&&isRealName.equals("1"))){
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"非实名制用户无法办理此产品【"+productName+"】！");
					}
					
					//限制证件类型：身份证、户口、护照
					IDataset passportType=CommparaInfoQry.queryComparaByAttrAndCode1
							("CSM", "5212", "PRODUCT_PASSPORT_TYPE", psptTypeCode);
					if(IDataUtil.isEmpty(passportType)){
						String psptTypeCodeName=StaticUtil.getStaticValue("TD_S_PASSPORTTYPE", psptTypeCode);
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户证件类型【"+psptTypeCodeName+"】不能办理产品【"+productName+"】！");
						
					}
					
					isOrderProduct=true;
					break;
				}
			}
			//如果没有办理指定的产品
			if(!isOrderProduct){
				return ;
			}
						
			
			
			String userId=btd.getRD().getUca().getUserId();
			
			//查询出所有的证件客户信息
			IDataset userIdDatas=UserInfoQry.querySameDocumentUserIdsByPsptId(psptId);
			 
			for(int i=0,size=productIds.size();i<size;i++){
				String productId=productIds.get(i);

				//查询产品是否有配置进行效验
				IDataset docRules=CommparaInfoQry.queryComparaByAttrAndCode1
						("CSM", "5212", "PRODUCT_DOC", productId);
				
				if(IDataUtil.isNotEmpty(docRules)){
					
					//String productName=docRules.getData(0).getString("PARA_CODE2","");
					
					if(IDataUtil.isNotEmpty(userIdDatas)){
						for(int z=0,sizeZ=userIdDatas.size();z<sizeZ;z++){
							IData userData=userIdDatas.getData(z);

							String userIdOther=userData.getString("USER_ID");
							if(!userId.equals(userIdOther)){

								IDataset userMainProduct=UserProductInfoQry.queryUserValidMainProduct_1(userIdOther);
								
								if(IDataUtil.isNotEmpty(userMainProduct)){
									CSAppException.apperr(CrmCommException.CRM_COMM_103,"持有用户相同证件的其他用户已经开通了同类产品！");
								}
							}
						}
					
					}
				
				}
			}

		}

    }
}

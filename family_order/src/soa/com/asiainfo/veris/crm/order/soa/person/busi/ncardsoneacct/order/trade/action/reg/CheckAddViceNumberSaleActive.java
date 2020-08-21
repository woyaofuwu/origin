package com.asiainfo.veris.crm.order.soa.person.busi.ncardsoneacct.order.trade.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class CheckAddViceNumberSaleActive implements ITradeAction{

	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		//获取配置的特殊营销活动，如果副卡调用了相关营销活动，就不能被删除
		IDataset specialSaleActives=CommparaInfoQry.getCommparaInfos("CSM", "6990", "SALE_ACTIVE");
		
		if(IDataUtil.isNotEmpty(specialSaleActives)){
			
			//获取所有的操作信息
			List<RelationTradeData> relationTradeDatas=btd.get("TF_B_TRADE_RELATION");
			if(!(relationTradeDatas!=null&&relationTradeDatas.size()>0)){
				return ;
			}
			
			IDataset container=new DatasetList();
 			
			//遍历出所有的进行添加成员的数据
			for(int i=0,size=relationTradeDatas.size();i<size;i++){
				RelationTradeData data=relationTradeDatas.get(i);
				
				//选出所有的副角色的号码
				if(data.getModifyTag().equals(BofConst.MODIFY_TAG_ADD)&&
						data.getRoleCodeB().equals("2")){
					IData dataTemp=new DataMap();
					dataTemp.put("USER_ID", data.getUserIdB());
					dataTemp.put("SERIAL_NUMBER", data.getSerialNumberB());
					
					container.add(dataTemp);
				}
			}
			
			if(container.size()<1){	//如果不存在添加的成员信息
				return ;
			}
			
			
			for(int i=0,size=specialSaleActives.size();i<size;i++){
				IData specialSaleActive=specialSaleActives.getData(i);
				
				String saleActiveId=specialSaleActive.getString("PARA_CODE1","");
				
				
				if(!saleActiveId.equals("")){		//如果配置的营销活动不为空
					for(int k=0,sizek=container.size();k<sizek;k++){
						IData userInfo=container.getData(k);
						
						String userId=userInfo.getString("USER_ID");
						String serialNumberSecond=userInfo.getString("SERIAL_NUMBER");
						
						//核对用户是否办理营销活动
						IDataset saleActives=UserSaleActiveInfoQry.queryUserSaleActiveProdId(userId, saleActiveId, "0");
						if(IDataUtil.isNotEmpty(saleActives)){
							IData saleActive=saleActives.getData(0);
							
							String saleActiveName=null;
							
							IDataset idsSaleActive = UpcCall.qryCatalogByCatalogId(saleActiveId);//ProductInfoQry.queryAllProductInfo(saleActiveId);
							if(IDataUtil.isNotEmpty(idsSaleActive)){
								IData idSaleActive = idsSaleActive.first();
								saleActiveName = idSaleActive.getString("CATALOG_NAME");
							}else{
								saleActiveName = saleActive.getString("PRODUCT_NAME","");
							}
							
							
							StringBuilder message=new StringBuilder();
							message.append(serialNumberSecond);
							message.append("已参加营销活动【");
							message.append(saleActiveName);
							message.append("】，不能办理为统一付费业务的副卡");
							
							CSAppException.apperr(CrmCommException.CRM_COMM_103, message.toString());
							
						}else{
							//核对用户是否预约办理了营销活动
							IDataset saleActiveBooks=UserSaleActiveInfoQry.queryUserSaleActiveBookProdId(userId, saleActiveId);
							
							if(IDataUtil.isNotEmpty(saleActiveBooks)){
								IData saleActiveBook=saleActiveBooks.getData(0);
								
								String saleActiveName=null;
								
								IDataset idsSaleActive = UpcCall.qryCatalogByCatalogId(saleActiveId);//ProductInfoQry.queryAllProductInfo(saleActiveId);
								if(IDataUtil.isNotEmpty(idsSaleActive)){
									IData idSaleActive = idsSaleActive.first();
									saleActiveName = idSaleActive.getString("CATALOG_NAME");
								}else{
									saleActiveName = saleActiveBook.getString("PRODUCT_NAME","");
								}
								
								StringBuilder message=new StringBuilder();
								message.append(serialNumberSecond);
								message.append("已参加营销活动【");
								message.append(saleActiveName);
								message.append("】，不能办理为统一付费业务的副卡");
								
								CSAppException.apperr(CrmCommException.CRM_COMM_103, message.toString());
								
							}
						}
						
					}
				}
			}	
		}

	}
	
}

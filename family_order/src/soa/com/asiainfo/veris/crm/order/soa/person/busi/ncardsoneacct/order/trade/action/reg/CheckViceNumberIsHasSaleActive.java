package com.asiainfo.veris.crm.order.soa.person.busi.ncardsoneacct.order.trade.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class CheckViceNumberIsHasSaleActive implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		//获取配置的特殊营销活动，如果副卡调用了相关营销活动，就不能被删除
		IDataset specialSaleActives=CommparaInfoQry.getCommparaInfos("CSM", "152", "2");
		
		if(IDataUtil.isNotEmpty(specialSaleActives)){
			
			//获取所有的操作信息
			List<RelationTradeData> relationTradeDatas=btd.get("TF_B_TRADE_RELATION");
			if(!(relationTradeDatas!=null&&relationTradeDatas.size()>0)){
				return ;
			}
			
			IDataset container=new DatasetList();
 			
			//遍历出所有的进行删除成员的数据
			for(int i=0,size=relationTradeDatas.size();i<size;i++){
				RelationTradeData data=relationTradeDatas.get(i);
				
				//选出所有的副角色的号码
				if(data.getModifyTag().equals(BofConst.MODIFY_TAG_DEL)&&
						data.getRoleCodeB().equals("2")){
					IData dataTemp=new DataMap();
					dataTemp.put("USER_ID", data.getUserIdB());
					dataTemp.put("SERIAL_NUMBER", data.getSerialNumberB());
					
					container.add(dataTemp);
				}
			}
			
			if(container.size()<1){	//如果不存在删除的成员信息
				return ;
			}
			
			//获取当前年月
			String curDate=SysDateMgr.getSysDate(SysDateMgr.PATTERN_TIME_YYYYMMDD);
			String curYear=curDate.substring(0, 4);
			String curMonth=curDate.substring(4, 6);
			
			
			for(int i=0,size=specialSaleActives.size();i<size;i++){
				IData specialSaleActive=specialSaleActives.getData(i);
				
				String saleActiveId=specialSaleActive.getString("PARA_CODE1","");
				String packageId=specialSaleActive.getString("PARA_CODE2","");
				
				if(!saleActiveId.equals("")){		//如果配置的营销活动不为空
					
					
					for(int k=0,sizek=container.size();k<sizek;k++){
						IData userInfo=container.getData(k);
						
						String userId=userInfo.getString("USER_ID");
						String serialNumberSecond=userInfo.getString("SERIAL_NUMBER");
						
						//获取用户所有有效的营销活动
						IDataset allSaleActives=UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(userId);
						
						for(int j=0,sizej=allSaleActives.size();j<sizej;j++){
							IData saleActive=allSaleActives.getData(j);
							
							String productIdDb=saleActive.getString("PRODUCT_ID");
							String packageIdDb=saleActive.getString("PACKAGE_ID");
							
							if(productIdDb.trim().equals(saleActiveId.trim())){
								//判断办理营销的活动的结束时间
								String saleActiveEndDate=saleActive.getString("END_DATE","");
								String saleActiveEndYear=saleActiveEndDate.substring(0, 4);
								String saleActiveEndMonth=saleActiveEndDate.substring(5, 7);
								
								//如果订购的结束时间与当前的年月相同，允许取消
								if(saleActiveEndYear.equals(curYear)&&
										saleActiveEndMonth.equals(curMonth)){
									continue;
								}
								
								
								if(!packageId.trim().equals("")){		//如果存在
									if(packageIdDb.trim().equals(packageId.trim())){
										String productName=saleActive.getString("PRODUCT_NAME","");
										String packageName=saleActive.getString("PACKAGE_NAME","");
										
										StringBuilder message=new StringBuilder();
										message.append("副号码");
										message.append(serialNumberSecond);
										message.append("办理了营销活动：【");
										message.append(productName);
										message.append("(");
										message.append(productIdDb);
										message.append(")");
										message.append("】的【");
										message.append(packageName);
										message.append("(");
										message.append(packageIdDb);
										message.append(")");
										message.append("】档次，无法进行删除！");
										
										CSAppException.apperr(CrmCommException.CRM_COMM_103, message.toString());
									}
								}else{
									String productName=saleActive.getString("PRODUCT_NAME","");
									
									StringBuilder message=new StringBuilder();
									message.append("副号码");
									message.append(serialNumberSecond);
									message.append("办理了营销活动：【");
									message.append(productName);
									message.append("(");
									message.append(productIdDb);
									message.append(")");
									message.append("】，无法进行删除！");
									
									CSAppException.apperr(CrmCommException.CRM_COMM_103, message.toString());
								}
							}
						}
					}
				}
			}	
		}	
	}
}


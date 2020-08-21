package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;


public class CheckDiscntDealRegAction implements ITradeAction{

	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		UcaData uc=btd.getRD().getUca();
		String userId=uc.getUserId();
		
		//如果没有空白的套餐，就直接退出
		String blankDiscntCode=null;
		IDataset blankDiscntConfigs=CommparaInfoQry.getCommNetInfo("CSM", "3933", "BLANK_DISNCT_CODE");
		if(IDataUtil.isNotEmpty(blankDiscntConfigs)){
			blankDiscntCode=blankDiscntConfigs.getData(0).getString("PARA_CODE1");
		}
		if(blankDiscntCode==null||blankDiscntCode.trim().equals("")){
			blankDiscntCode="20151108";
		}
		
		IDataset blankDiscntDatas=UserDiscntInfoQry.getAllDiscntByUser(userId, blankDiscntCode);
		if(IDataUtil.isEmpty(blankDiscntDatas)){
			return ;
		}
		
		//获取特别的套餐信息
		IDataset specialDiscnts=CommparaInfoQry.getCommNetInfo("CSM", "3933", "DISNCT_CODE_LEVEL");
		if(IDataUtil.isEmpty(specialDiscnts)){
			return ;
		}
		
		
		List<DiscntTradeData> discntTradeDatas=btd.get("TF_B_TRADE_DISCNT");
		if(discntTradeDatas!=null&&discntTradeDatas.size()>0){
			
			//用于比较是否有对特殊的套餐进行变更
			IData checkContainer=new DataMap();
			for(int i=0,size=specialDiscnts.size();i<size;i++){
				IData specialDiscnt=specialDiscnts.getData(i);
				String specialDiscntCode=specialDiscnt.getString("PARA_CODE1");
				checkContainer.put(specialDiscntCode, specialDiscnt);
			}
			
			//获取空白套餐信息
			IData blankDiscntData=blankDiscntDatas.getData(0);
			String blankDiscntStartDate=blankDiscntData.getString("START_DATE");
			String blankDiscntEndDate=blankDiscntData.getString("END_DATE");
			
			//获取用户已经办理的特殊套餐
			int currentLevel=-1;
			IDataset ownDiscnts=UserDiscntInfoQry.getSpecDiscnt(userId);
			if(IDataUtil.isNotEmpty(ownDiscnts)){
				
				IData ownDiscntContainer=new DataMap();
				
				for(int i=0,size=ownDiscnts.size();i<size;i++){
					String ownDiscntCode=ownDiscnts.getData(i).getString("DISCNT_CODE");
					if(checkContainer.containsKey(ownDiscntCode)){
						
						//如果开始时间在空白套餐之内
						String ownDiscntBeginDate=ownDiscnts.getData(i).getString("START_DATE");
						String ownDiscntEndDate=ownDiscnts.getData(i).getString("END_DATE");
						
						if((blankDiscntStartDate.compareTo(ownDiscntEndDate)<0&&
								blankDiscntEndDate.compareTo(ownDiscntEndDate)>0)||
								(blankDiscntStartDate.compareTo(ownDiscntBeginDate)<0&&
								blankDiscntEndDate.compareTo(ownDiscntBeginDate)>0)||
									(blankDiscntStartDate.compareTo(ownDiscntBeginDate)>=0&&
										blankDiscntEndDate.compareTo(ownDiscntEndDate)<=0)){
							ownDiscntContainer.put(ownDiscntBeginDate, new DataMap(ownDiscnts.getData(i)));
						}
					}
				}
				
				//遍历出最大时间的套餐
				if(ownDiscntContainer!=null&&ownDiscntContainer.size()>0){
					Set<String> timeKeys=ownDiscntContainer.keySet();
					
					String timeKeyTemp=null;
					Iterator<String> it=timeKeys.iterator();
					while(it.hasNext()){
						String timeKey=it.next();
						if(timeKeyTemp==null||timeKeyTemp.trim().equals("")){
							timeKeyTemp=timeKey;
						}else{
							if(timeKey.compareTo(timeKeyTemp)>0){
								timeKeyTemp=timeKey;
							}
						}
					}
					
					if(timeKeyTemp!=null&&!timeKeyTemp.trim().equals("")){
						IData maxOwnDiscnt=ownDiscntContainer.getData(timeKeyTemp);
						String ownDiscntCode=maxOwnDiscnt.getString("DISCNT_CODE");
						
						IData discntLevelData=checkContainer.getData(ownDiscntCode);
						String ownDiscntLevel=discntLevelData.getString("PARA_CODE2");
						if(ownDiscntLevel!=null&&!ownDiscntLevel.trim().equals("")){
							currentLevel=Integer.parseInt(ownDiscntLevel);
						}
					}
				}
			}
			
			
			boolean isOrderNewDiscnt=false;
			List<String> deleteDiscnts=new ArrayList<String>();
			
			for(int i=0,size=discntTradeDatas.size();i<size;i++){
				DiscntTradeData discntTradeData=discntTradeDatas.get(i);
				String discode=discntTradeData.getDiscntCode();
				String tradeDiscntStartDate=discntTradeData.getStartDate();
				String tradeDiscntEndDate=discntTradeData.getEndDate();
				
				//核对是否存在删除的套餐
				if(checkContainer.containsKey(discode)&&
						discntTradeData.getModifyTag().equals(BofConst.MODIFY_TAG_DEL)){
					
					//如果删除的套餐在空白套餐的有效期之内，需要判断是否存在套餐升级的情况
					if(blankDiscntStartDate.compareTo(tradeDiscntEndDate)<0&&
							blankDiscntEndDate.compareTo(tradeDiscntEndDate)>0){
						deleteDiscnts.add(discode);
					}
					
				}
				
				
				if(currentLevel>-1){
					//核对变更在空白套餐时间之内是否存在降级的产品变更
					if(checkContainer.containsKey(discode)&&
							discntTradeData.getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
						
						//如果添加的套餐在空白套餐的有效期之内，需要判断是否存在套餐升级的情况
						if(blankDiscntStartDate.compareTo(tradeDiscntStartDate)<=0&&
								blankDiscntEndDate.compareTo(tradeDiscntStartDate)>=0){
							IData discntLevelData=checkContainer.getData(discode);
							String discntLevel=discntLevelData.getString("PARA_CODE2");
							
							if(discntLevel!=null&&!discntLevel.trim().equals("")){
								int discntLevelInt=Integer.parseInt(discntLevel);
								if(currentLevel>discntLevelInt){
									String discntLevelName=discntLevelData.getString("PARA_CODE3","");
									
									CSAppException.apperr(CrmCommException.CRM_COMM_103,"套餐【"+discode+" "+discntLevelName+"】级别低于现有的套餐的级别，不允许办理！");
								}
							}
							
							isOrderNewDiscnt=true;
						}
					}
				}
				
			}
			
			
			//如果不存在升级，只是删除相关套餐，不允许
			if(!isOrderNewDiscnt){
				if(deleteDiscnts!=null&&deleteDiscnts.size()>0){
					IData discntInfo=DiscntInfoQry.getDiscntInfoByCode2(deleteDiscnts.get(0));
					String discntName=discntInfo.getString("DISCNT_NAME");
					
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"套餐【"+deleteDiscnts.get(0)+" "+discntName+"】不允许删除！");
				}
			}
		}
	}
	
}

package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;
import java.util.List;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
/**
 * 退订和留言的时候判断和留言是否绑定当前有效的主优惠，如果有绑定的主优惠，则拦截不给退订和留言平台业务
 * @author Administrator
 *
 */
public class CheckHLYRelationAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		String userId=btd.getRD().getUca().getUser().getUserId();
		UcaData uca = btd.getRD().getUca();
		//获取界面的平台服务
		List<PlatSvcTradeData> pstds = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
		//获取和和留言绑定的主套餐信息
		IDataset HLYRelationDatas=CommparaInfoQry.getCommparaAllColByParser("CSM", "7979", "HLY_RELATION", "0898");
		if(pstds!=null&&pstds.size()>0){
			for(PlatSvcTradeData pstData:pstds){
				//退订
				if("07".equals(pstData.getOperCode())){
					//获取当前用户下有效的优惠
					IDataset userDiscntInfoQry = UserDiscntInfoQry.getUserNormalDiscntByUserId(userId);
					if(IDataUtil.isNotEmpty(userDiscntInfoQry)){
						for(int i=0;i<userDiscntInfoQry.size();i++){
							IData discntData=userDiscntInfoQry.getData(i);
							if(IDataUtil.isNotEmpty(HLYRelationDatas)){
								for(int j=0;j<HLYRelationDatas.size();j++){
									if(discntData.getString("DISCNT_CODE","").equals(HLYRelationDatas.getData(j).get("PARA_CODE1"))
											&&pstData.getElementId().equals(HLYRelationDatas.getData(j).get("PARA_CODE2"))){
										CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户办理了["+discntData.getString("DISCNT_CODE")+"]优惠"+",不允许退订平台服务["+pstData.getElementId()+"]!");
									}
								}
							}
						}
					}
				}else if("06".equals(pstData.getOperCode()) && "40223721".equals(pstData.getElementId())){//新增，还得判断新增的是新和留言（不是老和留言）
					// 是否用户已经订购了老和留言服务
                    List<PlatSvcTradeData> userPlatSvcs = uca.getUserPlatSvcByServiceId("40223723");
					if(userPlatSvcs!=null&&userPlatSvcs.size()>0){
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户已订购老和留言，不允许订购新和留言");
					}
				}
			}
		}
	}

}


package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.IdcardUtils;

/**
 * 
 * 
 * 订购这个套餐时直接根据年龄判断绑定这个减免套餐，有效期至25周岁当年年底（12月31号）。若用户变更套餐，需同步截止该减免套餐到本月底。
 * 配置在TD_S_COMMPARA表中param_attr=6969
 */
public class BindSpecifiedDiscntAction implements ITradeAction
{
	private static Logger logger = Logger.getLogger(BindSpecifiedDiscntAction.class);
    @SuppressWarnings("unchecked")
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	UcaData uca =btd.getRD().getUca();
    	String userId = uca.getUserId();
        String serialNumber = uca.getSerialNumber();
		String startDate="";
		String endDate="";
		String psptTypeCode=uca.getCustomer().getPsptTypeCode();
		String psptId = uca.getCustomer().getPsptId();
		logger.debug("BindSpecifiedDiscntAction>>>>"+serialNumber);
		//只有用户证件是身份证或户口本才计算年龄绑定指定套餐 
		if("0".equals(psptTypeCode)||"1".equals(psptTypeCode)||"2".equals(psptTypeCode)){
			int age=IdcardUtils.getAgeByIdCard(psptId);
			List<DiscntTradeData> changeDiscnt=btd.get("TF_B_TRADE_DISCNT");
			logger.debug("BindSpecifiedDiscntAction>>>>changeDiscnt>>"+changeDiscnt.toString());
			if(changeDiscnt!=null && changeDiscnt.size()>0){
				IData param = new DataMap();
				param.put("SUBSYS_CODE", "CSM");
				param.put("PARAM_ATTR", "6969");
				param.put("PARAM_CODE", "BIND_DISCNT");
				for(int i= 0;i < changeDiscnt.size();i++){
					DiscntTradeData discntTradeData = changeDiscnt.get(i);
					String discntCode = discntTradeData.getDiscntCode();
					String modifyTag = discntTradeData.getModifyTag();
					startDate = discntTradeData.getStartDate();
					endDate = discntTradeData.getEndDate();
					param.put("PARA_CODE1", discntCode);
					IDataset dataset = CommparaInfoQry.getCommparaInfoByPara(param);
					if(IDataUtil.isNotEmpty(dataset)){
						
						String bindDiscntCode = dataset.first().getString("PARA_CODE2");
						String intervalYear = dataset.first().getString("PARA_CODE3");
						int limitYear = Integer.parseInt(intervalYear);
						String end25Date = SysDateMgr.get25yearEndDate(psptId,intervalYear);
						//如果是新增并且年龄小于等于25就绑定P2优惠
						if(BofConst.MODIFY_TAG_ADD.equals(modifyTag)&&age<=limitYear){
							
							String instId = SeqMgr.getInstId();
							DiscntTradeData discntData=new DiscntTradeData();
					        discntData.setUserId(userId);
					        discntData.setUserIdA("-1");
					        discntData.setProductId("-1");
					        discntData.setPackageId("-1");
					        discntData.setElementId(bindDiscntCode);
					        discntData.setSpecTag("0");
					        discntData.setRelationTypeCode("");
					        discntData.setInstId(instId);
					        discntData.setCampnId("");
					        discntData.setStartDate(startDate);
					        discntData.setEndDate(end25Date);
					        discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);
					        discntData.setRemark("25岁以下用户绑定指定套餐，配置在6969");
					            
					        btd.add(serialNumber, discntData);	
						}
						
						//如果是删除就截至P2优惠至月底
						if(BofConst.MODIFY_TAG_DEL.equals(modifyTag)){
							IDataset discntData = UserDiscntInfoQry.getDiscntsByUserIdDiscntCode(userId, bindDiscntCode,"0898");
							
							//如果用户存在有效的绑定优惠就截至到月底
							if(IDataUtil.isNotEmpty(discntData)){
								String delEndDdate = SysDateMgr.getAddMonthsLastDayNoEnv(0,endDate);
								//如果预约的时间比25岁年底还要大的话，就取结束时间为25岁年底
								if(SysDateMgr.compareTo(end25Date, delEndDdate)<0){
									delEndDdate = end25Date;
								}
								
								
								DiscntTradeData delDiscntData=new DiscntTradeData(discntData.first());
								delDiscntData.setModifyTag(BofConst.MODIFY_TAG_DEL);
								delDiscntData.setEndDate(delEndDdate);
								btd.add(serialNumber, delDiscntData);
							}
						}
						
						
					}
					
				}
				
			}
			
			
			
			
		}
        
    }
}

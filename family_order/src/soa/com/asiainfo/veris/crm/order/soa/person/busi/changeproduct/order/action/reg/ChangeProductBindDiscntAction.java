
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;


/**
 * 激活当月变更进行套餐赠送的功能
 * 激活当月如果主产品变更，配置在9228中产品，para_code10为1的就绑定PARA_CODE1配置的优惠
 * 
 * @author tz
 *
 */
public class ChangeProductBindDiscntAction implements ITradeAction
{
	private static Logger logger = Logger.getLogger(ChangeProductBindDiscntAction.class);
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	UcaData uca =btd.getRD().getUca();
    	String userId = uca.getUserId();
        String serialNumber = uca.getSerialNumber();
		String productId = "";
		String startDate="";
		String openMonth = uca.getUser().getOpenDate().substring(0,7);
		
		//如果开户时间不在当月就返回
		logger.debug("ChangeProductBindDiscntAction>>>>>" + serialNumber +"---"+ openMonth);
		
		//如果有主产品变更
		List<ProductTradeData> changeProducts=btd.get("TF_B_TRADE_PRODUCT"); 
		if(changeProducts!=null&&changeProducts.size()>0){
			 for(ProductTradeData proData : changeProducts){
				 if(BofConst.MODIFY_TAG_ADD.equals(proData.getModifyTag())){
					 productId = proData.getProductId();
					 startDate = proData.getStartDate();
				 }
			 }
		}
		//如果产品id或者产品开始时间为空则直接返回
		if(StringUtils.isBlank(productId)||StringUtils.isBlank(startDate)){
			return;
		}
		String currentMonth = startDate.substring(0,7);
		//如果开户日期和产品生效时间不在同一个月则直接返回
		if(!openMonth.equals(currentMonth)){
			return;
		}
		logger.debug("ChangeProductBindDiscntAction>>>>>" + serialNumber +"+++"+ currentMonth);
		
		IDataset commparaInfos9228 = CommparaInfoQry.getCommparaAllColByParser("CSM", "9228", productId, "0898");
		//如果新办理的产品id在9228配置里就给用户绑定配置的优惠
        if (IDataUtil.isNotEmpty(commparaInfos9228))
        {
			for(int i=0;i<commparaInfos9228.size();i++)
			{
				//如果配置不等于1就返回进行下一条
				String bindFlag=commparaInfos9228.getData(i).getString("PARA_CODE10");//para_code10=是否在产品变更时绑定
				if(!"1".equals(bindFlag)){
					continue;
				}
				String discntCode=commparaInfos9228.getData(i).getString("PARA_CODE1");//para_code1=后台绑定优惠
	        	String continuous=commparaInfos9228.getData(i).getString("PARA_CODE2","");//para_code2=绑定期限(数字代表几个月，null则到2050）
	        	String effTime=commparaInfos9228.getData(i).getString("PARA_CODE3");//para_code3=0-立即生效 1-次月生效 2-绝对时间
	        	String endTime=commparaInfos9228.getData(i).getString("PARA_CODE5","");//para_code5=绝对时间
	        	String smsContent=commparaInfos9228.getData(i).getString("PARA_CODE20","");//para_code20=开通短信内容

	        	logger.debug("ChangeProductBindDiscntAction>>>>>bind>>>>" + discntCode +"start");
		      
		        String endData="";
		        if("0".equals(effTime)){
		        	if(!"".equals(continuous) && !"null".equals(continuous)){
		            	endData= SysDateMgr.getAddMonthsLastDay(Integer.parseInt(continuous),startDate);//绑定期限(数字代表几个月，null则到2050）
		            }else{
		            	endData= SysDateMgr.END_DATE_FOREVER;
		            }
		        }else if ("1".equals(effTime)) {
		        	startDate=SysDateMgr.getDateNextMonthFirstDay(startDate);//  1-次月生效
		        	if(!"".equals(continuous) && !"null".equals(continuous)){
		            	endData= SysDateMgr.getAddMonthsLastDay(Integer.parseInt(continuous)+1,startDate);//绑定期限(数字代表几个月，null则到2050）
		            }else{
		            	endData= SysDateMgr.END_DATE_FOREVER;
		            }
				}else if ("2".equals(effTime)) {
//						startDate=SysDateMgr.getSysTime(); //0-立即生效 1-次月生效
					if(StringUtils.isNotBlank(endTime)){
		        		endData= endTime;
		        	}else{
		        		endData= SysDateMgr.END_DATE_FOREVER;
		        	}
				}     
		        else{
		        	startDate=SysDateMgr.getDateNextMonthFirstDay(startDate);//  1-次月生效
		        	if(StringUtils.isNotBlank(endTime)){
		        		endData= endTime;
		        	}else{
		        		endData= SysDateMgr.END_DATE_FOREVER;
		        	}
		        }
		        
		      
	        	logger.debug("ChangeProductBindDiscntAction>>>>>bind>>>>" + discntCode +"insert");
	        	String instId = SeqMgr.getInstId();
	        	DiscntTradeData discntData=new DiscntTradeData();
		        discntData.setUserId(userId);
		        discntData.setUserIdA("-1");
		        discntData.setProductId("-1");
		        discntData.setPackageId("-1");
		        discntData.setElementId(discntCode);
		        discntData.setSpecTag("0");
		        discntData.setRelationTypeCode("");
		        discntData.setInstId(instId);
		        discntData.setCampnId("");
		        discntData.setStartDate(startDate);
		        discntData.setEndDate(endData);
		        discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);
		        discntData.setRemark("激活当月主产品变更为"+productId+"自动绑定");
		            
		        btd.add(serialNumber, discntData);	  
		        
		        
		        
		        //REQ201902010004  新增18元流量王卡新入网政策  @tanzheng start
		        if(StringUtils.isNotBlank(smsContent)){
		        	IData ObjectsmsData = new DataMap(); // 短信数据
	                ObjectsmsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
	                ObjectsmsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空
	                ObjectsmsData.put("FORCE_OBJECT", "10086");// 发送对象
	                ObjectsmsData.put("RECV_OBJECT", serialNumber);// 接收对象
	                ObjectsmsData.put("NOTICE_CONTENT", smsContent);// 短信内容
	                PerSmsAction.insTradeSMS(btd, ObjectsmsData);
		        }
		        //REQ201902010004  新增18元流量王卡新入网政策  @tanzheng end
		        
			}   
        }
			
			
        
    }
}

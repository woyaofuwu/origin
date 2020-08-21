
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;
 
import java.util.List;

import com.ailk.biz.BizEnv;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;

/**
 * REQ201807180046_新业务尝鲜季活动开发需求（第一阶段）
 * <br/>
 * 只合适  赠10GB咪咕定向流量。
 * 截至解绑开户绑定的优惠到月底结束，根据COMMPARA表param_attr=9230的配置进行绑定
 * @author zhuoyingzhi
 * @date 20180727
 */
public class BindDiscntComm9230Action implements ITradeAction
{
    public void executeAction(BusiTradeData btd) throws Exception
    {

    	UcaData uca = btd.getRD().getUca();
    	String UserId = uca.getUserId();//createPersonUserRD.getUca().getUser().getUserId();
        String prodId="";//用户开户主产品。
        boolean nowRunFlag = BizEnv.getEnvBoolean("crm.merch.addShoppingCart", false); // 加购物车跳出此段逻辑,加个开关方便控制
        if (nowRunFlag)
        {
            OrderDataBus dataBus = DataBusManager.getDataBus();
            String submitType = dataBus.getSubmitType();// addShoppingCart
            if (StringUtils.equals(BofConst.SUBMIT_TYPE_SHOPPING_CART, submitType))
            {
                return;
            }
        }
        List<ProductTradeData> productTrade = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
        ProductTradeData prodTradeData = new ProductTradeData();
        
        if(productTrade != null && productTrade.size() > 0)
        {
            for(ProductTradeData product : productTrade)
            {
                if(BofConst.MODIFY_TAG_ADD.equals(product.getModifyTag()) && "1".equals(product.getMainTag()))
                {
                	prodId = product.getProductId();
                	prodTradeData = product;
                }
            }
        }
   
        if(!"".equals(prodId) && prodId!=null){

	        IDataset commparaInfos9230 = CommparaInfoQry.getCommparaAllColByParser("CSM", "9230",prodId, btd.getRD().getUca().getUserEparchyCode());
	        if (IDataUtil.isNotEmpty(commparaInfos9230))
	        {
				for(int i=0;i<commparaInfos9230.size();i++)
				{
					String discntCode=commparaInfos9230.getData(i).getString("PARA_CODE1");//para_code1=后台绑定优惠
		        	String continuous=commparaInfos9230.getData(i).getString("PARA_CODE2","");//para_code2=绑定期限(数字代表几个月，null则到2050）
		        	String effTime=commparaInfos9230.getData(i).getString("PARA_CODE3");//para_code3=0-立即生效 1-次月生效 2-绝对时间
		        	String giftTime=commparaInfos9230.getData(i).getString("PARA_CODE4","");//para_code4=1-特殊判断时间
		        	String endTime=commparaInfos9230.getData(i).getString("PARA_CODE5","");//para_code5=绝对时间
		        	
		        	
		        	String isaccumulation = commparaInfos9230.getData(0).getString("PARA_CODE6", "");//p6=0,多次变更时要累加;
		        	
		        	String discntNew="";//本次新办的该种优惠 
		        	
		        	boolean flag=true;//允许办理条件
		        	
		        	//2、本次办理的优惠如果存在该优惠，则不再绑定。
		        	List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		        	for (DiscntTradeData discntTradeData : discntTradeDatas)
		            {
		                // 判断R类型优惠 拼入attr
		                if (BofConst.MODIFY_TAG_ADD.equals(discntTradeData.getModifyTag()))
		                {
		                    discntNew = discntTradeData.getElementId();
		                    if(discntNew.equals(discntCode)){
		                    	flag=false;
		                    	break;
		                    }
		                }
		            }
		        	//3、要求该用户原来没有有效的该优惠
		        	if(flag){
			        	IDataset userDiscs=UserDiscntInfoQry.getAllDiscntByUser(UserId,discntCode);
			        	if(userDiscs!=null && userDiscs.size()>0){
			        		//flag=false;
			        		/**
			        		 * REQ201807180046_新业务尝鲜季活动开发需求（第一阶段）
			        		 * @author zhuoyingzhi
			        		 * @date 20180726
			        		 */
		        			//p6=0,多次变更时要累加;
			                if(!isaccumulation.trim().equals("0")){
		                        flag = false;
		                    }else{
		                    	//已经使用后了月份个数
		                        int accumulationNum = 0;
		                        for (int j = 0; j < userDiscs.size(); j++) {
		                            IData data = userDiscs.getData(j);
		                            String startdate = data.getString("START_DATE");
		                            String enddate = data.getString("END_DATE");
		                            String discntInst = data.getString("INST_ID");
		                            
		                            IDataset userofferrelDs = UserOfferRelInfoQry.qryUserAllOfferRelByUserIdDiscntCode_1(UserId, discntCode, discntInst);
		                            if (userofferrelDs == null || userofferrelDs.size() == 0) {//赠送的优惠不会往 tf_f_user_offer_rel表里写记录。主动订购的则会写入该表
		                                int monthsbetween = SysDateMgr.monthsBetween(startdate, enddate)+1;
		                                accumulationNum += monthsbetween;
		                            }
		                        }
		                        System.out.println("BindDiscntComm9230ActionaccumulationNum:"+accumulationNum);
		                        if(accumulationNum>=Integer.parseInt(continuous)){
		                            flag = false;
		                        }else{
		                            continuous = String.valueOf(Integer.parseInt(continuous)-accumulationNum);
		                        }
		                    }
			                if(flag){
				                flag=false;
				                //直接修改原来的那条优惠,正常情况只会存在一条有效的该优惠
				                IData oldDiscnt=new DataMap();
				                	  oldDiscnt.put("USER_ID",UserId);
				                	  oldDiscnt.put("DISCNT_CODE",discntCode);
				                	  oldDiscnt.put("INST_ID",userDiscs.getData(0).getString("INST_ID", ""));
				                	  
				                	  oldDiscnt.put("END_DATE",SysDateMgr.getAddMonthsLastDay(Integer.parseInt(continuous)));
				                	  oldDiscnt.put("REMARK","在优惠有效期内再一次订购该优惠,优惠编码："+discntCode);
				                	  
				                Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "UPDATE_DISCNT_INST_ID", oldDiscnt);		                	
			                }
			        	 /**********************************************************************/			        		
			        	}
		        	}
		        	
		        	if("1".equals(giftTime)){
			        	flag=false;//查询到数据，则不赠送，表明时间是大于2月2号
			        }
		        	System.out.println("BindDiscntComm9230Actioncontinuous:"+continuous);
		        	if(flag){
			        	String startDate="";
			        	String endData="";
			        	if("0".equals(effTime)){
			        		startDate=SysDateMgr.getSysTime(); //0-立即生效 1-次月生效
			        		if(!"".equals(continuous) && !"null".equals(continuous)){
			            		endData= SysDateMgr.getAddMonthsLastDay(Integer.parseInt(continuous));//绑定期限(数字代表几个月，null则到2050）
			            	}else{
			            		endData= SysDateMgr.END_DATE_FOREVER;
			            	}
			        	}else if ("1".equals(effTime)) {
				        	startDate=SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysDate());//  1-次月生效
				        	if(!"".equals(continuous) && !"null".equals(continuous)){
				            	endData= SysDateMgr.getAddMonthsLastDay(Integer.parseInt(continuous)+1);//绑定期限(数字代表几个月，null则到2050）
				            }else{
				            	endData= SysDateMgr.END_DATE_FOREVER;
				            }
						}else if ("2".equals(effTime)) {
							startDate=SysDateMgr.getSysTime(); //0-立即生效 1-次月生效
							if(StringUtils.isNotBlank(endTime)){
				        		endData= endTime;
				        	}else{
				        		endData= SysDateMgr.END_DATE_FOREVER;
				        	}
						}     
				        else{
				        	startDate=SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysDate());//  1-次月生效
				        	if(StringUtils.isNotBlank(endTime)){
				        		endData= endTime;
				        	}else{
				        		endData= SysDateMgr.END_DATE_FOREVER;
				        	}
				        }		            
			            DiscntTradeData newDiscnt = new DiscntTradeData();
			            newDiscnt.setUserId(UserId);
			            newDiscnt.setProductId("-1");
			            newDiscnt.setPackageId("-1");
			            newDiscnt.setElementId(discntCode);
			            newDiscnt.setInstId(SeqMgr.getInstId());
			            newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD); 
			            newDiscnt.setStartDate(startDate);
			            newDiscnt.setEndDate(endData);
			            newDiscnt.setRemark("根据业务类型及主产品后台绑定优惠,配置类型为9230");
			            btd.add(uca.getSerialNumber(), newDiscnt);		            
			    		
		                /**
		                 * REQ201807180046_新业务尝鲜季活动开发需求（第一阶段）
		                 * <br/>
		                 * 下发短信 
		                 * @author zhuoyingzhi
		                 * @date 20180727
		                 */
			    		if("10".equals(btd.getTradeTypeCode())||"110".equals(btd.getTradeTypeCode())){
			    			 sendSms(uca.getSerialNumber(), btd);
			    		}
			    		
		        	}
				}
	        }
	        
        }
    } 
    
    
    /**
     * 下发短信
     * @param serial_number
     * @param btd
     * @param endData
     * @param productName
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20180727
     */
    public void sendSms(String serial_number, BusiTradeData btd) throws Exception
    {
        MainTradeData mainTrade = btd.getMainTradeData();
        SmsTradeData std = new SmsTradeData();
        std.setSmsNoticeId(SeqMgr.getSmsSendId());
        std.setEparchyCode(mainTrade.getEparchyCode());
        std.setBrandCode(mainTrade.getBrandCode());
        std.setInModeCode(CSBizBean.getVisit().getInModeCode());
        std.setSmsNetTag("0");
        std.setChanId("11");
        std.setSendObjectCode("6");
        std.setSendTimeCode("1");
        std.setSendCountCode("1");
        std.setRecvObjectType("00");
        std.setRecvObject(serial_number);
        std.setRecvId(mainTrade.getUserId());
        std.setSmsTypeCode("0");
        std.setSmsKindCode("03");
        std.setNoticeContentType("0");
        std.setReferedCount("0");
        std.setForceReferCount("1");
        std.setForceObject("10086");
        std.setForceStartTime(SysDateMgr.getSysTime());
        std.setForceEndTime("");
        std.setSmsPriority("50");
        std.setReferTime(SysDateMgr.getSysTime());
        std.setReferDepartId(CSBizBean.getVisit().getDepartId());
        std.setReferStaffId(CSBizBean.getVisit().getStaffId());
        std.setDealTime(SysDateMgr.getSysTime());
        std.setDealStaffid(CSBizBean.getVisit().getStaffId());
        std.setDealDepartid(CSBizBean.getVisit().getDepartId());
        std.setDealState("15");// 未处理
        std.setRemark("通过产品赠送优惠短信");
        std.setRevc1("");
        std.setRevc2("");
        std.setRevc3("");
        std.setRevc4("");
        std.setMonth(SysDateMgr.getCurMonth());
        std.setDay(SysDateMgr.getCurDay());
        std.setCancelTag(mainTrade.getCancelTag());
        //尊敬的客户，您本月已免费获赠10GB咪咕定向流量（国内流量不含港澳台流量，可在咪咕视频客户端使用），
        //点此登录咪咕视频客户端激活流量权益： http://dx.10086.cn/qyAf6j （下载登录后自动激活，无需领取），
        //观看全网海量优质高清电影电视剧综艺及体育节目！如您继续使用本套餐，咪咕定向流量将连续赠送12个月。
        //回复CXDXLL可查询咪咕定向流量使用和剩余情况。【中国移动　和你一起】
        StringBuffer  noticeContent=new StringBuffer();
					  noticeContent.append("尊敬的客户，");
					  noticeContent.append("您本月已免费获赠10GB咪咕定向流量（国内流量不含港澳台流量，可在咪咕视频客户端使用），");
					  noticeContent.append("点此登录咪咕视频客户端激活流量权益： http://dx.10086.cn/qyAf6j （下载登录后自动激活，无需领取），");
					  //noticeContent.append("观看全网海量优质高清电影电视剧综艺及体育节目！如您继续使用本套餐，咪咕定向流量将连续赠送12个月。");
					  noticeContent.append("回复1170还能免费领取三个月视频VIP会员，享受免广告、2K高清海量VIP影视剧免费看等特权！");
					  noticeContent.append("回复CXDXLL可查询咪咕定向流量使用和剩余情况。【中国移动　和你一起】");        		      
        std.setNoticeContent(noticeContent.toString());
        //System.out.println("unBindDiscntFromDiscntOrProductActionxxxxxxxxxxxxx223 " + std);

        btd.add(btd.getRD().getUca().getUser().getSerialNumber(), std);
    }     
}

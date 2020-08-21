
package com.asiainfo.veris.crm.order.soa.person.busi.topsetboxmanage.order.trade;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.topsetboxmanage.TopSetBoxManageBean;
import com.asiainfo.veris.crm.order.soa.person.busi.topsetboxmanage.order.requestdata.TopSetBoxManageRequestData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TopSetBoxTrade.java
 * @Description:
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-8-5 上午9:38:18 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-8-5 yxd v1.0.0 修改原因
 */
public class TopSetBoxManageTrade extends BaseTrade implements ITrade
{
	private static Logger log=Logger.getLogger(TopSetBoxManageTrade.class);

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        TopSetBoxManageRequestData tsbReqData = (TopSetBoxManageRequestData) btd.getRD();
        
        
        IData pageParam=btd.getRD().getPageRequestData();
        if(IDataUtil.isNotEmpty(pageParam)){
        	String operType=tsbReqData.getOperType();
        	UcaData uca=btd.getRD().getUca();
        	//获取相关费用信息
			String money="20000";
			IDataset moneyDatas=CommparaInfoQry.getCommNetInfo("CSM", "1626", "TOP_SET_BOX_MONEY");
			if(IDataUtil.isNotEmpty(moneyDatas)){
				money=moneyDatas.getData(0).getString("PARA_CODE1","20000");
			}
			//获取转账存折
			StringBuffer depositeNotes=new StringBuffer();
			IDataset noteDatas=CommparaInfoQry.getCommNetInfo("CSM", "1627", "TOP_SET_BOX_NOTES");
			if(IDataUtil.isNotEmpty(noteDatas)){
				
				for(int i=0,size=noteDatas.size();i<size;i++){
					IData noteData=noteDatas.getData(i);
					
					depositeNotes.append(noteData.getString("PARA_CODE1"));
					if(i<size-1){
						depositeNotes.append("|");
					}
				}
			}else{
				CSAppException.appError("39102", "取转账存折错误！");
			}
        	if(operType!=null&&!operType.trim().equals("")){
        		if(operType.equals("APPLY_TOPSETBOX")){		//申领
        			
        			//4、调接口判断用户的现金是否足够，不够则提示缴费，不登记台账；调用接口
        			IData param=new DataMap();
        			String serialNumber = uca.getSerialNumber();
        			param.put("SERIAL_NUMBER", serialNumber);
        			IDataset checkCash= AcctCall.queryAccountDepositBySn(serialNumber);
    		    	int cash = 0;
    		    	if(DataSetUtils.isNotBlank(checkCash)){
    		    		for(int i = 0 ; i < checkCash.size() ; i++){
    		    			cash = cash + Integer.parseInt(checkCash.getData(i).getString("DEPOSIT_BALANCE","0"));
    		    		}
    		    	}
    		    	if(cash<Integer.parseInt(money)){
    		    		CSAppException.appError("61311", "账户存折可用余额不足，请先办理缴费。账户余额："+Double.parseDouble(String.valueOf(cash))/100+"元，押金金额："+Integer.parseInt(money)/100+"元");
    		    	}else{
    		    		//调用账务的接口进行预转存
    					IData params=new DataMap(); 
    					params.put("SERIAL_NUMBER", uca.getSerialNumber());
    					params.put("OUTER_TRADE_ID", btd.getRD().getTradeId());
    					params.put("DEPOSIT_CODE_OUT", depositeNotes.toString());
    			   		params.put("DEPOSIT_CODE_IN", "9016"); 
    					params.put("TRADE_FEE", money);
    					params.put("CHANNEL_ID", "15000");
    					params.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());
    					params.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId()); 
    					params.put("TRADE_DEPART_ID",CSBizBean.getVisit().getDepartId());
    					params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    					params.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
    			   		//调用接口，将【现金】——>【押金】
    			   		IData inAcct = AcctCall.transFeeInADSL(params);
        	    		if(IDataUtil.isEmpty(inAcct)){
       						CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用账务接口报错！");
       					}else{
       						String resultCode=inAcct.getString("RESULT_CODE","");
       						if(!resultCode.equals("0")){
       							CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用账务接口错误："+inAcct.getString("RESULT_INFO",""));
       						}
       					}
        	    		
        	    		IData inValidRes = getOldInValidResAttr(tsbReqData);
            			this.updateMainTradeData(btd, tsbReqData);
            			// 购机
                        this.createResTradeData(btd, tsbReqData, BofConst.MODIFY_TAG_ADD);
                        
                        if (StringUtils.isNotBlank(inValidRes.getString("RELA_INST_ID")))
                        {
                        	this.createPlatSVCAndAttr(btd, tsbReqData, PlatConstants.OPER_USER_DATA_MODIFY, inValidRes.getString("ELEMENT_ID"), BofConst.MODIFY_TAG_UPD, inValidRes.getString("RELA_INST_ID"));
                        }
    		    	}
        			
        		}else if(operType.equals("CHANGE_TOPSETBOX")){		//换机顶盒
        			
        			this.updateMainTradeData(btd, tsbReqData);
        			List mainList = btd.get(TradeTableEnum.TRADE_MAIN.getValue());
        	        MainTradeData mainTD = (MainTradeData) mainList.get(0);
        	        mainTD.setRsrvStr2("2");//换机顶盒标志
        			// 换机
                    this.createResTradeDataForChange(btd, tsbReqData, BofConst.MODIFY_TAG_DEL);
                    this.createResTradeDataForChange(btd, tsbReqData, BofConst.MODIFY_TAG_ADD);
                    
        		}else if(operType.equals("RETURN_TOPSETBOX")){		//退还
        			TopSetBoxManageBean bean= BeanManager.createBean(TopSetBoxManageBean.class);
        			IDataset boxInfos = bean.qrySetTopBoxByUserIdAndTag1AllColumns(tsbReqData.getUca().getUserId(), "4", "J");
        			if(DataSetUtils.isNotBlank(boxInfos)){
        				money = boxInfos.getData(0).getString("RSRV_NUM2","0");
        				if(Integer.parseInt(money) > 0){//有押金才退还押金
                			IData params=new DataMap(); 
                			money = String.valueOf(Integer.parseInt(money)*100);
        			   		params.put("SERIAL_NUMBER", uca.getSerialNumber());
        					params.put("OUTER_TRADE_ID", "");
        					params.put("DEPOSIT_CODE_OUT", "9016");
        					params.put("TRADE_FEE", money);
        					params.put("CHANNEL_ID", "15000");
        					params.put("SUB_SYS", "RESSERV_TF_RH_SALE_DEAL");
        					params.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());
        					params.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId()); 
        					params.put("TRADE_DEPART_ID",CSBizBean.getVisit().getDepartId());
        					params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        					params.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        			   		//调用接口，将【押金】——>【现金】
        			   		IData inAcct = AcctCall.transFeeOutADSL(params);
        					if(IDataUtil.isEmpty(inAcct)){
           						CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用账务接口报错！");
           					}else{
           						String resultCode=inAcct.getString("RESULT_CODE","");
           						if(!resultCode.equals("0")){
           							CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用账务接口错误："+inAcct.getString("RESULT_INFO",""));
           						}
           					}
        				}
        			}
					this.updateMainTradeData(btd, tsbReqData);
        			this.createResTradeDataForChange(btd, tsbReqData, BofConst.MODIFY_TAG_DEL);
        	        
        		}else if(operType.equals("LOSE_TOPSETBOX")){		//丢失
        			TopSetBoxManageBean bean= BeanManager.createBean(TopSetBoxManageBean.class);
        			IDataset boxInfos = bean.qrySetTopBoxByUserIdAndTag1AllColumns(tsbReqData.getUca().getUserId(), "4", "J");
        			if(DataSetUtils.isNotBlank(boxInfos)){
        				money = boxInfos.getData(0).getString("RSRV_NUM2","0");
        				money = money.trim();
        				if(Integer.parseInt(money) > 0){//有押金才对押金信息沉淀
        					//如果用户退还机顶盒或者用户使用的期限已经达到3年或以上
        					money = String.valueOf(Integer.parseInt(money)*100);
        					String sysdate=SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
        					String topsetboxStartDate = boxInfos.getData(0).getString("START_DATE",SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
        					int years=SysDateMgr.yearInterval(topsetboxStartDate, sysdate)+1;
        					if(years<3){//用户申领魔百和机顶盒小于三年丢失，押金沉淀处理
        						//资金进行沉淀
    							IData depositeParam=new DataMap();
    							depositeParam.put("ACCT_ID", uca.getAcctId());
    							depositeParam.put("CHANNEL_ID", "15000");
    							depositeParam.put("PAYMENT_ID", "100021");
    							depositeParam.put("PAY_FEE_MODE_CODE", "0");
    							depositeParam.put("REMARK", "魔百和用户机顶盒丢失且办理时间不满三年，资金进行沉淀！");
    							
    							IData depositeInfo=new DataMap();
    							depositeInfo.put("DEPOSIT_CODE", "9016");
    							depositeInfo.put("TRANS_FEE", money);
    							
    							IDataset depositeInfos=new DatasetList();
    							depositeInfos.add(depositeInfo);
    							
    							depositeParam.put("DEPOSIT_INFOS", depositeInfos);
    							IData inAcct =AcctCall.foregiftDeposite(depositeParam);
            					if(IDataUtil.isEmpty(inAcct)){
               						CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用账务接口报错！");
               					}else{
               						String resultCode=inAcct.getString("X_RESULTCODE","");
               						if(!resultCode.equals("0")){
               							CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用账务接口错误："+inAcct.getString("X_RESULTINFO",""));
               						}
               					}
        					}else{//大于三年丢失，押金退还处理
                    			IData params=new DataMap(); 
                    			params.put("SERIAL_NUMBER", uca.getSerialNumber());
            					params.put("OUTER_TRADE_ID", "");
            					params.put("DEPOSIT_CODE_OUT", "9016");
            					params.put("TRADE_FEE", money);
            					params.put("CHANNEL_ID", "15000");
            					params.put("SUB_SYS", "RESSERV_TF_RH_SALE_DEAL");
            					params.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());
            					params.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            					params.put("TRADE_DEPART_ID",CSBizBean.getVisit().getDepartId());
            					params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()); 
            					params.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
            			   		//调用接口，将【押金】——>【现金】
            			   		IData inAcct = AcctCall.transFeeOutADSL(params);
            					if(IDataUtil.isEmpty(inAcct)){
               						CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用账务接口报错！");
               					}else{
               						String resultCode=inAcct.getString("RESULT_CODE","");
               						if(!resultCode.equals("0")){
               							CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用账务接口错误："+inAcct.getString("RESULT_INFO",""));
               						}
               					}
        					}
        				}
        			}
        			this.updateMainTradeData(btd, tsbReqData);
        			this.createResTradeDataForChange(btd, tsbReqData, BofConst.MODIFY_TAG_DEL);
        	        
        		}else{	//原来的处理方式
        			//保留原来的方式
                	defaultDeal(btd);
        		}
        		
        	}else{
        		//保留原来的方式
            	defaultDeal(btd);
        	}      	
        }else{
        	//保留原来的方式
        	defaultDeal(btd);
        }
        
    }
    
    private void defaultDeal(BusiTradeData btd)throws Exception{
    	TopSetBoxManageRequestData tsbReqData = (TopSetBoxManageRequestData) btd.getRD();
    	
    	this.updateMainTradeData(btd, tsbReqData);
        String userAction = tsbReqData.getUserAction();
        if (StringUtils.equals("0", userAction))
        {
            // 购机
            this.createResTradeData(btd, tsbReqData, BofConst.MODIFY_TAG_ADD);
        }
        else if (StringUtils.equals("1", userAction))
        {
            // 换机
            this.createResTradeData(btd, tsbReqData, BofConst.MODIFY_TAG_DEL);
            this.createResTradeData(btd, tsbReqData, BofConst.MODIFY_TAG_ADD);
        }
        else if (StringUtils.equals("2", userAction))
        {
            // 退还
            this.createResTradeData(btd, tsbReqData, BofConst.MODIFY_TAG_DEL);
        }
        else if (StringUtils.equals("3", userAction))
        {
            // 丢失
            this.createResTradeData(btd, tsbReqData, BofConst.MODIFY_TAG_DEL);
        }
    }
    



    /**
     * @Function: createResTradeData()
     * @Description: 终端台帐
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-5 上午10:19:22 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-5 yxd v1.0.0 修改原因
     */
    private void createResTradeData(BusiTradeData btd, TopSetBoxManageRequestData tsbReqData, String modifyTag) throws Exception
    {
        ResTradeData resTD = new ResTradeData();
        TopSetBoxManageBean bean= BeanManager.createBean(TopSetBoxManageBean.class);
        IDataset boxInfos = bean.qryAllSetTopBoxByUserIdAndTag1(tsbReqData.getUca().getUserId(), "4", "J");
        IData oldResInfo = boxInfos.first();
        String userAction = tsbReqData.getUserAction();
        boolean isOld = StringUtils.equals(BofConst.MODIFY_TAG_DEL, modifyTag);
        resTD.setUserId(tsbReqData.getUca().getUserId());
        resTD.setUserIdA("-1");
        resTD.setResTypeCode(isOld ? oldResInfo.getString("RES_TYPE_CODE") : tsbReqData.getResTypeCode()); // 终端类型编码
        resTD.setResCode(isOld ? oldResInfo.getString("RES_CODE") : tsbReqData.getResKindCode()); // 终端型号编码
        resTD.setImsi(isOld ? oldResInfo.getString("IMSI") : tsbReqData.getResNo()); // 终端编码
        resTD.setKi(isOld ? oldResInfo.getString("KI") : tsbReqData.getSupplyId()); // 厂商编码
        resTD.setInstId(isOld ? oldResInfo.getString("INST_ID") : SeqMgr.getInstId());
        resTD.setStartDate(isOld ? oldResInfo.getString("START_DATE") : SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
        resTD.setEndDate(isOld ? SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND) : SysDateMgr.END_DATE_FOREVER);
        resTD.setModifyTag(modifyTag);
        resTD.setRemark(tsbReqData.getRemark());
        resTD.setRsrvNum1(tsbReqData.getArtificalSericesTag()); // 上门标记
        resTD.setRsrvTag2("1");	//新业务规则标识
        resTD.setRsrvNum5(tsbReqData.getResFee());
        resTD.setRsrvStr1(oldResInfo.getString("RSRV_STR1",""));
        resTD.setRsrvStr2(oldResInfo.getString("RSRV_STR2",""));
        resTD.setRsrvStr3(oldResInfo.getString("RSRV_STR3",""));
        resTD.setRsrvStr4(tsbReqData.getResBrandCode() + "," + tsbReqData.getResBrandName());
        resTD.setRsrvStr5(tsbReqData.getWideAddr());
        resTD.setRsrvTag1("J"); // 机顶盒标志
        resTD.setRsrvDate2(isOld ? oldResInfo.getString("RSRV_DATE2") : tsbReqData.getReturnDate()); //预计归还时间
        
        //如果是新增就封装进货价格
        if (StringUtils.equals(BofConst.MODIFY_TAG_ADD, modifyTag)){
        	resTD.setRsrvNum4(btd.getRD().getPageRequestData().getString("DEVICE_COST", "0"));
        }
        
        //获取费用信息
		String money="20000";
		IDataset moneyDatas=CommparaInfoQry.getCommNetInfo("CSM", "1626", "TOP_SET_BOX_MONEY");
		if(IDataUtil.isNotEmpty(moneyDatas)){
			money=moneyDatas.getData(0).getString("PARA_CODE1","20000");
		}
		int fee=Integer.parseInt(money);
		resTD.setRsrvNum2(isOld ? "0" : StringUtils.equals("1", userAction) ?  oldResInfo.getString("RSRV_NUM2","") : String.valueOf(fee/100));
        
        btd.add(tsbReqData.getUca().getSerialNumber(), resTD);
    }
    
   
    
    
    /**
     * @Function: createResTradeData()
     * @Description: 终端台帐
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-5 上午10:19:22 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-5 yxd v1.0.0 修改原因
     */
    private void createResTradeDataForChange(BusiTradeData btd, TopSetBoxManageRequestData tsbReqData, String modifyTag) throws Exception
    {
    	TopSetBoxManageBean bean= BeanManager.createBean(TopSetBoxManageBean.class);
    	IDataset boxInfos = bean.qrySetTopBoxByUserIdAndTag1AllColumns(tsbReqData.getUca().getUserId(), "4", "J");
    	IData oldResInfo = new DataMap();
    	if(DataSetUtils.isBlank(boxInfos)){
    		oldResInfo = bean.qryAllSetTopBoxByUserIdAndTag1(tsbReqData.getUca().getUserId(), "4", "J").first();
    	}else{
    		oldResInfo = boxInfos.first();
    	}
    	
    	ResTradeData resTD =null;
    	
    	if(StringUtils.equals(BofConst.MODIFY_TAG_ADD, modifyTag)){	//新增
    		resTD =new ResTradeData();
    		
            resTD.setUserId(tsbReqData.getUca().getUserId());
            resTD.setUserIdA("-1");
            resTD.setResTypeCode(tsbReqData.getResTypeCode()); // 终端类型编码
            resTD.setResCode(tsbReqData.getResKindCode()); // 终端型号编码
            resTD.setImsi(tsbReqData.getResNo()); // 终端编码
            resTD.setKi(tsbReqData.getSupplyId()); // 厂商编码
            resTD.setInstId(SeqMgr.getInstId());
            resTD.setStartDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
            resTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
            resTD.setModifyTag(modifyTag);
            resTD.setRemark(tsbReqData.getRemark());
            resTD.setRsrvNum1(tsbReqData.getArtificalSericesTag()); // 上门标记
            resTD.setRsrvNum5(tsbReqData.getResFee());
            resTD.setRsrvStr1(oldResInfo.getString("RSRV_STR1",""));
            resTD.setRsrvStr2(oldResInfo.getString("RSRV_STR2",""));
            resTD.setRsrvStr3(oldResInfo.getString("RSRV_STR3",""));
            resTD.setRsrvStr4(tsbReqData.getResBrandCode() + "," + tsbReqData.getResBrandName());
            resTD.setRsrvStr5(tsbReqData.getWideAddr());
            resTD.setRsrvTag1("J"); // 机顶盒标志
            resTD.setRsrvTag2("1");	//新业务规则标识
            resTD.setRsrvDate2(tsbReqData.getReturnDate());
            resTD.setRsrvNum4(btd.getRD().getPageRequestData().getString("DEVICE_COST", "0"));
    		resTD.setRsrvNum2(oldResInfo.getString("RSRV_NUM2","0"));
    		resTD.setRsrvNum1(oldResInfo.getString("RSRV_NUM1",""));
    	}else if(StringUtils.equals(BofConst.MODIFY_TAG_DEL, modifyTag)){	//删除
    		resTD = new ResTradeData(oldResInfo);
        	resTD.setModifyTag(modifyTag);
        	resTD.setRsrvNum2("0");
        	resTD.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
    		
    	} 
        
        btd.add(tsbReqData.getUca().getSerialNumber(), resTD);
    }
    
    /**
     * @Function: createPlatSVCAndAttr()
     * @Description: 平台服务和属性原子台帐
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-5 下午2:22:07 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-5 yxd v1.0.0 修改原因
     */
    private void createPlatSVCAndAttr(BusiTradeData btd, TopSetBoxManageRequestData tsbReqData, String operCode, String serviceId, String modifyTag) throws Exception
    {
        List<ProductModuleData> bindPlatSvcs = new ArrayList<ProductModuleData>();
        // 平台服务
        IData platParam = new DataMap();
        platParam.put("SERVICE_ID", serviceId);
        platParam.put("OPER_CODE", operCode);
        platParam.put("OPR_SOURCE", "08");
        PlatSvcData psd = new PlatSvcData(platParam);
        List<AttrData> attrs = new ArrayList<AttrData>();
        String userAction = tsbReqData.getUserAction();
        if(StringUtils.equals("1", userAction)){
        	 // 3.旧终端号
        	AttrData oldTSBIDAttr = new AttrData();
            oldTSBIDAttr.setAttrCode("OLDSTBID");
            oldTSBIDAttr.setAttrValue(tsbReqData.getOldResNo());
            oldTSBIDAttr.setModifyTag(BofConst.MODIFY_TAG_UPD);
            attrs.add(oldTSBIDAttr);
            // 4.新终端号
            AttrData stbIdAttr = new AttrData();
            stbIdAttr.setAttrCode("STBID");
            stbIdAttr.setAttrValue(tsbReqData.getResNo());
            stbIdAttr.setModifyTag(BofConst.MODIFY_TAG_UPD);
            attrs.add(stbIdAttr);
        }else if(StringUtils.equals("0", userAction)){
        	AttrData stbIdAttr = new AttrData();
            stbIdAttr.setAttrCode("STBID");
            stbIdAttr.setAttrValue(tsbReqData.getResNo());
            stbIdAttr.setModifyTag(BofConst.MODIFY_TAG_UPD);
            attrs.add(stbIdAttr);
        }
        
        if (!StringUtils.equals(modifyTag, BofConst.MODIFY_TAG_DEL))
        {
            psd.setAttrs(attrs);
        }
        bindPlatSvcs.add(psd);
        ProductModuleCreator.createProductModuleTradeData(bindPlatSvcs, btd.getRD().getUca(), btd);
    }

    /**
     * @Function: updateMainTradeData()
     * @Description: 更新主台帐的预留字段值
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-5 上午9:40:33 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-5 yxd v1.0.0 修改原因
     */
    private void updateMainTradeData(BusiTradeData btd, TopSetBoxManageRequestData tsbReqData) throws Exception
    {
        List mainList = btd.get(TradeTableEnum.TRADE_MAIN.getValue());
        MainTradeData mainTD = (MainTradeData) mainList.get(0);
        mainTD.setRsrvStr1(tsbReqData.getUserAction());
        mainTD.setRsrvStr5(tsbReqData.getResKindCode());
        mainTD.setRsrvStr6(tsbReqData.getResNo());
        mainTD.setRsrvStr7(tsbReqData.getResFee());
        mainTD.setRsrvStr8(tsbReqData.getOldResNo());
        mainTD.setRsrvStr9(tsbReqData.getWideState());
        mainTD.setRsrvStr10(tsbReqData.getWideTradeId());
        mainTD.setOlcomTag("1");
        
        //如果是开户需要发送宽带的地址信息
        String userId=tsbReqData.getUca().getUserId();
        IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userId, "4", "J");
        if(IDataUtil.isEmpty(boxInfos)){
//        	String serialNumber=tsbReqData.getUca().getSerialNumber();
//        	String wSerialNumber="KD_"+serialNumber;
        	mainTD.setRsrvStr2("1");	//如果为开户、申领
        	
//        	IDataset wideInfos = TradeInfoQry.queryExistWideTrade(wSerialNumber);
//            if (IDataUtil.isEmpty(wideInfos))
//            {
//            	IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(wSerialNumber).first();
//            	String areaCode=wideNetInfo.getString("RSRV_STR4","");
//            	mainTD.setRsrvStr4(areaCode);
//            }
//            else
//            {
//                IData wideTD = wideInfos.getData(0);
//                IDataset addrTD = TradeWideNetInfoQry.queryTradeWideNet(wideTD.getString("TRADE_ID"));
//                if (IDataUtil.isNotEmpty(addrTD))
//                {
//                	String areaCode=addrTD.getData(0).getString("RSRV_STR4","");
//                	mainTD.setRsrvStr4(areaCode);
//                }
//            }
            
        }
   
    }

    private IData getOldInValidResAttr(TopSetBoxManageRequestData tsbReqData) throws Exception 
    {
    	IDataset validBoxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(tsbReqData.getUca().getUserId(), "4", "J");
			
    	IDataset userResAttrs = UserAttrInfoQry.getUserAttrByUserId(tsbReqData.getUca().getUserId(), "STBID");
    	
    	boolean isHaveValidBox = false;
    	
    	//没有绑定有效魔百和终端的平台服务
    	IData inValidRes = new DataMap();
    	
		for (int i = 0; i < userResAttrs.size(); i++)
		{
			isHaveValidBox = false;
			
			for (int j = 0; j < validBoxInfos.size(); j++)
			{
				if (userResAttrs.getData(i).getString("ATTR_VALUE","").equals(validBoxInfos.getData(j).getString("IMSI")))
				{
					isHaveValidBox = true; 
					
					break;
				}
			}
			
			if (!isHaveValidBox)
			{
				inValidRes = userResAttrs.getData(i);
			}
			
		}
		
		return inValidRes;
	}
    
    /**
     * @Function: createPlatSVCAndAttr()
     * @Description: 平台服务和属性原子台帐
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     */
    private void createPlatSVCAndAttr(BusiTradeData btd, TopSetBoxManageRequestData tsbReqData, String operCode, String serviceId, String modifyTag, String oldPlatSvcInstId) throws Exception
    {
        List<ProductModuleData> bindPlatSvcs = new ArrayList<ProductModuleData>();
        
        // 平台服务
        IData platParam = new DataMap();
        platParam.put("SERVICE_ID", serviceId);
        platParam.put("OPER_CODE", operCode);
        platParam.put("OPR_SOURCE", "08");
        
        if (StringUtils.isNotBlank(oldPlatSvcInstId))
        {
        	platParam.put("INST_ID", oldPlatSvcInstId);
        }
        
        PlatSvcData psd = new PlatSvcData(platParam);
        // 属性 1.宽带地址
        List<AttrData> attrs = new ArrayList<AttrData>();
        AttrData addrAttr = new AttrData();
        addrAttr.setAttrCode("ADDRESS");
        addrAttr.setAttrValue(tsbReqData.getWideAddr());
        addrAttr.setModifyTag(modifyTag);
        attrs.add(addrAttr);
        // 2.宽带帐号
        AttrData brandIDAttr = new AttrData();
        brandIDAttr.setAttrCode("BROADBANDID");
        brandIDAttr.setAttrValue("KD_" + tsbReqData.getSerialNumber());
        brandIDAttr.setModifyTag(modifyTag);
        attrs.add(brandIDAttr);
        // 3.旧终端号
        AttrData oldTSBIDAttr = new AttrData();
        oldTSBIDAttr.setAttrCode("OLDSTBID");
        oldTSBIDAttr.setAttrValue(tsbReqData.getOldResNo());
        oldTSBIDAttr.setModifyTag(modifyTag);
        attrs.add(oldTSBIDAttr);
        // 4.新终端号
        AttrData stbIdAttr = new AttrData();
        stbIdAttr.setAttrCode("STBID");
        stbIdAttr.setAttrValue(tsbReqData.getResNo());
        stbIdAttr.setModifyTag(modifyTag);
        attrs.add(stbIdAttr);
        
        // 5.邮政编码
        AttrData zipCodeAttr = new AttrData();
        String zipCode=StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", 
        		new String[]{"TYPE_ID","EPARCHY_CODE","DATA_ID"}, "REMARK",
        		new String[]{"ZIPCODE","0898",getVisit().getCityCode()});
       
	    zipCodeAttr.setAttrCode("ZIPCODE");
	    zipCodeAttr.setAttrValue(zipCode);
	    zipCodeAttr.setModifyTag(modifyTag);
        attrs.add(zipCodeAttr);
        /************************end***********************************/
        if (!StringUtils.equals(modifyTag, BofConst.MODIFY_TAG_DEL))
        {
            psd.setAttrs(attrs);
        }
        bindPlatSvcs.add(psd);
        ProductModuleCreator.createProductModuleTradeData(bindPlatSvcs, btd.getRD().getUca(), btd);
    }
}

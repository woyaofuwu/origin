/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.usergrpdiscntspecdeal.order;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpBaseAudiInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: UserDiscntSpecDealRegSVC.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: maoke
 * @date: May 27, 2014 7:58:19 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 27, 2014 maoke v1.0.0 修改原因
 */
public class UserGrpDiscntSpecDealRegSVC extends OrderService
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public String getOrderTypeCode() throws Exception
    {
        return "1522";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "1522";
    }

    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
        if (StringUtils.isNotBlank(input.getString("OTHER_DISCNT_LIST")))
        {
            IDataset otherSpecList = new DatasetList(input.getString("OTHER_DISCNT_LIST"));

            if (IDataUtil.isNotEmpty(otherSpecList))
            {
                String serialNumber = btd.getRD().getUca().getSerialNumber();

                for (int i = 0, size = otherSpecList.size(); i < size; i++)
                {
                    IData data = otherSpecList.getData(i);
                    data.put("SERIAL_NUMBER", serialNumber);

                    IDataset result = CSAppCall.call("SS.UserGrpDiscntSpecDealRegSVC.tradeReg", data);
                }
            }
        }
    }
    
    /**
     * 重写，不做业务校验
     */
    public void checkAfterRule(IData tableData, BusiTradeData btd) throws Exception
	{
    	
	}
    
    /**
     * REQ201906120013 优化工作手机折扣参数批量修改、达量限速批量修改等功能
     * 生成集团业务稽核工单
     * REQ201804280001集团合同管理界面优化需求
     * @param map
     * @throws Exception
     * @author guonj
     * @date 2019-07-09
     */
    protected void actGrpBizBaseAudit(IData input, BusiTradeData<?> btd) throws Exception
    {
    	boolean actVoucherFlag = BizEnv.getEnvBoolean("grp.biz.audit", false);
    	String batchId = input.getString("BATCH_CODE","");
    	String auditId = ""; 
    	if(StringUtils.isNotBlank(batchId)){
    		auditId = batchId;	//批量任务的批次号不为空就取批次号
    	}else{
    		auditId = btd.getTradeId();					//不然就取业务流水号
    	}
    	IData param = new DataMap();
    	param.put("AUDIT_ID", auditId);													//批量业务的批次号或业务流水号trade_id
    	param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(auditId));					//受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
    	param.put("BIZ_TYPE", StringUtils.isNotBlank(batchId) ? "2" : "1");			//业务工单类型：1-单条，2-批量业务
    	IDataset existSet = GrpBaseAudiInfoQry.queryGrpBaseAuditInfoForPK(param);
    	log.info("test_guonj_actGrpBizBaseAudit.existSet="+CollectionUtils.isEmpty(existSet)+IDataUtil.isEmpty(existSet)+existSet==null ? 0:existSet.size());
        if( actVoucherFlag && CollectionUtils.isEmpty(existSet) ){
        	/*集团产品增删改业务不需要上传凭证,但需要生成稽核工单*/
        	String auditStaffId = "";
        	String fileList = "";
        	IDataset ds = BatInfoQry.qryBatByBatchId(input.getString("BATCH_CODE",""));
        	if ( CollectionUtils.isNotEmpty(ds) ) {
        		auditStaffId = ds.getData(0).getString("AUDIT_STAFF_ID");
        		fileList = ds.getData(0).getString("AUDIT_INFO");
    		}
        	if(StringUtils.isBlank(auditStaffId)){
        		return;
        	}
        	//过滤掉不需要生成集团业务稽核工单的业务类型
        	String tradeTypeCode = btd.getTradeTypeCode();
        	IDataset params = ParamInfoQry.getCommparaByParamattr("CSM", "840", tradeTypeCode, "0898");
        	if(IDataUtil.isNotEmpty(params)){
        		return;
        	}
       
        	String addDisncts = "";
        	String delDiscnts = "";
        	String modDiscnts = "";
        	if (StringUtils.isNotBlank(input.getString("OTHER_DISCNT_LIST")))
            {
                IDataset otherSpecList = new DatasetList(input.getString("OTHER_DISCNT_LIST"));

                if (IDataUtil.isNotEmpty(otherSpecList))
                {
                	 if(IDataUtil.isNotEmpty(otherSpecList)){
                 	    for(int i=0;i<otherSpecList.size();i++){
                 		   IData each = otherSpecList.getData(i);
         	        	   String discntCode = each.getString("ELEMENT_ID", "");
         	        	   discntCode = StringUtils.isBlank(discntCode) ? each.getString("DISCNT_CODE", "") : discntCode;
         	        	   modDiscnts += StringUtils.isNotBlank(modDiscnts) ? ","+discntCode : discntCode;
                 	    }
                 	 }
                }
            } 
          
        	param.put("TRADE_TYPE_CODE", tradeTypeCode);									//业务类型编码：见参数表TD_S_TRADETYPE
        	if( btd.getRD() != null && btd.getRD().getUca() != null ){
        		param.put("GRP_SN", "");					//集团产品编码
        		param.put("RSRV_STR2", btd.getRD().getUca().getSerialNumber());	  //成员服务号码 by zhuwj
        		if( btd.getRD().getUca().getCustGroup()!= null ){
        			param.put("GROUP_ID", btd.getRD().getUca().getCustGroup().getGroupId());		//集团客户编码
                	param.put("CUST_NAME", btd.getRD().getUca().getCustGroup().getCustName());		//集团客户名称
        		}else{
        			param.put("GROUP_ID", "");		//集团客户编码
                	param.put("CUST_NAME", "");		//集团客户名称
        		}
        	}      	
        	param.put("CONTRACT_ID", "");											//合同编号
        	param.put("VOUCHER_FILE_LIST", fileList);			//凭证信息上传文件ID
        	param.put("ADD_DISCNTS", addDisncts);											//新增优惠
        	param.put("DEL_DISCNTS", delDiscnts);											//删除优惠
        	param.put("MOD_DISCNTS", modDiscnts);											//变更优惠
        	param.put("STATE", "0");														//稽核单状态:0-初始，1-稽核通过，2-稽核不通过
        	param.put("IN_DATE", SysDateMgr.getSysTime());									//提交时间
        	param.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());					//提交工号
        	param.put("AUDIT_STAFF_ID", auditStaffId);										//稽核人工号
        	/*boolean smsFlag=true;
        	if(StringUtils.isNotBlank(batchId)){
				IDataset audiInfos=GrpBaseAudiInfoQry.queryGrpBaseAuditInfoForPK(param);
				if(audiInfos!=null&&audiInfos.size()>0){
					smsFlag=false;
				}
			} */ 
        	log.info("test_guonj_actGrpBizBaseAudit.param="+param);
        	GrpBaseAudiInfoQry.addGrpBaseAuditInfo(param);
        	/*************************REQ201810100001优化政企集中稽核相关功能需求 begin*************************/
        	/*if(StringUtils.isNotBlank(auditStaffId)){
				   if(smsFlag){
					   IDataset staffs = StaffInfoQry.queryValidStaffById(auditStaffId);
					   if(IDataUtil.isNotEmpty(staffs)){
							IData staff = staffs.getData(0);
							String staffSn = staff.getString("SERIAL_NUMBER", "");
							if(StringUtils.isNotBlank(staffSn)){
								String grpSn = btd.getRD().getUca().getCustGroup().getGroupId();
								String tradeType = StaticUtil.getStaticValue(CSBizService.getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", tradeTypeCode);
								String smsContent = "集团编码:"+grpSn+",业务类型:"+tradeType+",工单流水号/批次号"+auditId+"已提交稽核,请及时稽核!";
								IData smsdata = new DataMap();
						        smsdata.put("EPARCHY_CODE", "0898");
						        smsdata.put("RECV_OBJECT", staffSn);// 工号手机号码
						        smsdata.put("NOTICE_CONTENT", smsContent);
						        smsdata.put("RECV_ID", "-1");
						        smsdata.put("SMS_TYPE_CODE", "20");// 用户办理业务通知
						        smsdata.put("FORCE_START_TIME", "");
						        smsdata.put("FORCE_END_TIME", "");
						        smsdata.put("REMARK", "");
						        SmsSend.insSms(smsdata);
							}
						}
					   
				   }
        	}*/
			 /*************************REQ201810100001优化政企集中稽核相关功能需求  end*************************/
			   
        }
 	   
    }
}

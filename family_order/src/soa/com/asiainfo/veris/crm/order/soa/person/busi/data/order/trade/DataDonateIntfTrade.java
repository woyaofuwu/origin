package com.asiainfo.veris.crm.order.soa.person.busi.data.order.trade;


import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.data.order.requestdata.DataDonateRequestData;

public class DataDonateIntfTrade extends BaseTrade implements ITrade {

	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception
    {

        	DataDonateRequestData reqData = (DataDonateRequestData) btd.getRD();
            String serialNumber = reqData.getUca().getSerialNumber();
            String objectSerialNumber = reqData.getObjSerialNumber();
            String donatedData = reqData.getDonateData();//这里的流量转赠单位是MB，传给账管时要乘1024转为KB
            String commId = reqData.getCommID();
            String discntCode = reqData.getDiscntCode();
            String balance = reqData.getBalance();
            String remark = reqData.getRemark();
            String dataType = reqData.getDataType();
            String instId = SeqMgr.getInstId();
            IData inparam = new DataMap();

            inparam.put("OUT_SERIAL_NUMBER", serialNumber);//转出手机号码
            inparam.put("IN_SERIAL_NUMBER", objectSerialNumber);//转入手机号码
            inparam.put("TRANS_AMOUNT", (int)((Float.parseFloat(reqData.getDonateData())*1024)));//转赠流量值
            inparam.put("PEER_BUSINESS_ID", instId);//外部流水
            inparam.put("CHANNEL_ID", "1"); // 操作渠道
            inparam.put("PRESENT_TYPE", dataType);//流量类型
            if("1".equals(dataType))//账本流量
            {
            	
            	inparam.put("INS_ID", commId);
            	
            }
            else if ("2".equals(dataType))//套餐流量
            {
            	inparam.put("INS_ID", commId);
            	inparam.put("DISCNT_CODE", discntCode);
            	inparam.put("CRM_BIZ_CODE", "101");
            	inparam.put("IN_START_DATE", reqData.getEffectiveDate());
            	inparam.put("IN_END_DATE", reqData.getExpireDate());
            }
            else
            {
            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "无法识别的流量类型:"+dataType);
            }
            IData result = donateUserData(inparam);

            // 主台账
            MainTradeData mainList = btd.getMainTradeData();
            mainList.setRsrvStr4(serialNumber); //赠送方号码
            mainList.setRsrvStr5(dataType); //流量类型：1.账本流量；2.套餐流量
            mainList.setRsrvStr6(commId); //流量资产实例ID
            mainList.setRsrvStr7(balance); //转赠前用户流量余额（MB）
            mainList.setRsrvStr8(objectSerialNumber);//被赠送方号码
            mainList.setRsrvStr9(donatedData);  //赠送数量（MB）         
            mainList.setRsrvStr10(instId);//赠送时发给账管的流水号
            mainList.setRemark(remark);//备注信息

            // 创建other台账
            //createOtherTradeData(btd, inparam, reqData);
    }
    
    public void createOtherTradeData(BusiTradeData bd, IData inparam, DataDonateRequestData reqData) throws Exception
    {
    	OtherTradeData otherTradeDataTmp = new OtherTradeData();
		otherTradeDataTmp.setRsrvValueCode("LLZZ");
		otherTradeDataTmp.setRsrvValue("流量转赠");
		otherTradeDataTmp.setUserId(reqData.getUca().getUserId());
		otherTradeDataTmp.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		otherTradeDataTmp.setEndDate(SysDateMgr.getTheLastTime());
		otherTradeDataTmp.setDepartId(CSBizBean.getVisit().getDepartId());
		otherTradeDataTmp.setStaffId(CSBizBean.getVisit().getStaffId());           
        //用户手机号码
		otherTradeDataTmp.setRsrvStr1(reqData.getUca().getSerialNumber());           
        //被赠送号码
		otherTradeDataTmp.setRsrvStr2(reqData.getObjSerialNumber());            
        //转赠流量
		otherTradeDataTmp.setRsrvStr3(reqData.getDonateData());           
        //商品ID
		otherTradeDataTmp.setRsrvStr4(reqData.getCommID());           
        //备注
		otherTradeDataTmp.setRemark(reqData.getRemark());           
		otherTradeDataTmp.setInstId(inparam.getString("INST_ID"));// 新增 则需要新增inst_id值 
		bd.add(reqData.getUca().getSerialNumber(), otherTradeDataTmp );
    }
	/**
     * 用户流量转赠
     * 
     * @param pd
     * @param inparam
     * @return
     * @throws Exception
     * @author zx
     */
	public IData donateUserData(IData param) throws Exception {

		IDataOutput resultSetOut = CSAppCall.callAcct(
				"AM_OUT_AllFlowTransfer", param, false);
		IDataset dataset = resultSetOut.getData();
		if ("0".equals(resultSetOut.getHead().getString("X_RESULTCODE")))
        {
			if (IDataUtil.isEmpty(dataset)) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用接口AM_OUT_AllFlowTransfer异常:"+resultSetOut);
				return new DataMap();
			} else {				
				return dataset.getData(0);
			}
        }
		else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用接口AM_OUT_AllFlowTransfer异常:"+resultSetOut);
			return new DataMap();
		}
	}
}

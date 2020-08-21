package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class IotQryPolicyInfoSvc extends CSBizService {
	private static final long serialVersionUID = -3243209710285783779L;

	/**
     * 查询用户当前有效的物联网测试期优惠
     * 
     * @return
     * @throws Exception
     */
    public IDataset queryUserPolicyInfo(IData param) throws Exception
    {
    	IDataUtil.chkParam(param, "SERIAL_NUMBER");
    	String serialNum = param.getString("SERIAL_NUMBER");
    	IData  inParam = new DataMap();
    	//4位机构编码+8位业务编码（BIPCode）+14位组包时间YYYYMMDDHH24MMSS+6位流水号（定长），序号从000001开始，增量步长为1。
    	String sysTime = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		//String oprSeq = "0600"+sysTime+String.valueOf((int)(Math.random() * 9 + 1) * 100000);
		String seqId = SeqMgr.getRealId();// 利用实名制6位序列号（循环）
		String oprSeq = "8981BIP3B626" + SysDateMgr.getSysDateYYYYMMDDHHMMSS() + seqId;
    	inParam.put("OPR_SEQ",oprSeq );
    	inParam.put("OPR_TIME", sysTime);
    	inParam.put("MOB_NUM", serialNum);
    	inParam.put("KIND_ID", "BIP3B626_T3000626_0_0");

    	IDataset ibossResult =IBossCall.dealInvokeUrl("BIP3B626_T3000626_0_0", "IBOSS", inParam);
		return ibossResult;
    }
}

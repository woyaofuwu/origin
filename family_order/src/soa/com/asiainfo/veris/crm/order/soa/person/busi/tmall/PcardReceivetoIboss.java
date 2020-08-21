package com.asiainfo.veris.crm.order.soa.person.busi.tmall;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class PcardReceivetoIboss extends CSBizService{

	//1优惠券领取接口
	public IData pcardReceive(IData datas) throws Exception{
		IData data = new DataMap(datas.toString()).getData("params");
		String loginType = IDataUtil.chkParam(data, "LoginType");
		
		   IData param = new DataMap();
	        param.put("LOGIN_TYPE", data.getString("LoginType"));//1登录类型
	        param.put("LOGIN_NO", data.getString("LoginNo"));//1登录账号
	        param.put("CHANNEL_ID", data.getString("ChannelId"));//1渠道标识
	        param.put("OPR_TYPE", data.getString("OprType"));//1操作类型
	        param.put("BATCH_ID", data.getString("BatchID"));//1批次编码
	        param.put("SERIAL_NUMBER",data.getString("SerialNumber"));//1唯一流水
	        param.put("OBTAIN_DATE", data.getString("ObtainDate"));//1领取日期
	        param.put("REGION_CODE",data.getString("RegionCode") );//1省份编码
	        param.put("TASK_ID", data.getString("TaskId"));//任务id
	        param.put("OBTAIN_BUSI", data.getString("ObtainBusi"));//领取业务编码
	        param.put("ORDER_ID", data.getString("OrderId"));//订单号
	        param.put("EXTEND_PAR1", data.getString("ExtendPar1"));//扩展字段1
	        param.put("EXTEND_PAR2", data.getString("ExtendPar2"));//扩展字段2
	        param.put("KIND_ID", "BIP3A248_T3000248_0_0");// 交易唯一标识
	    	        
	        IDataset returnDaset = IBossCall.callHttpIBOSS("IBOSS", param);
	        IData res = returnDaset.getData(0);
		return res;
		
	}

	//2已领取优惠券查询接口
	public IData queryPcardReceive(IData datas) throws Exception{
		IData data = new DataMap(datas.toString()).getData("params");
		String loginType = IDataUtil.chkParam(data, "LoginType");
		
		   IData param = new DataMap();
	        param.put("LOGIN_TYPE", data.getString("LoginType"));//1登录类型
	        param.put("LOGIN_NO", data.getString("LoginNo"));//1登录账号
	        param.put("STATUS", data.getString("Status"));//状态
	        param.put("BUSI_TYPE", data.getString("BusiType"));//品类
	        param.put("PCARD_TYPE", data.getString("PCardType"));//卡券类型
	        param.put("CHANNEL_ID",data.getString("ChannelId"));//1渠道标识
	        param.put("BATCH_ID", data.getString("BatchID"));//批次编码
	        param.put("BUSI_ACT_IDS",data.getString("BusiActIds") );//业务系统活动id
	        param.put("PAGE_SIZE", data.getString("PageSize"));//每页显示条数
	        param.put("PAGE_NUM", data.getString("PageNum"));//页码
	        param.put("REGION_CODE", data.getString("RegionCode"));//1省份编码
	        param.put("SERIAL_NUMBER", data.getString("SerialNumber"));//1唯一流水
	        param.put("KIND_ID", "BIP3A262_T3000262_0_0");// 交易唯一标识
	        
	        IDataset returnDaset = IBossCall.callHttpIBOSS("IBOSS", param);
	        IData res = returnDaset.getData(0);
		return res;
		
	}
	
	//3未使用卡券总量查询接口
	public IData queryPcardAvailable(IData datas) throws Exception{
		IData data = new DataMap(datas.toString()).getData("params");
		String loginType = IDataUtil.chkParam(data, "LoginType");
		
		   IData param = new DataMap();
	        param.put("BUSI_TYPE", data.getString("BusiType"));//1品类
	        param.put("PCARD_TYPE", data.getString("PCardType"));//1优惠券类型
	        param.put("LOGIN_TYPE", data.getString("LoginType"));//1登录类型
	        param.put("LOGIN_NO", data.getString("LoginNo"));//1登录账号
	        param.put("CHANNEL_ID", data.getString("ChannelId"));//1渠道标识
	        param.put("REGION_CODE",data.getString("RegionCode"));//1省份编码
	        param.put("BATCH_ID", data.getString("batchID"));//1批次编码
	        param.put("SERIAL_NUMBER",data.getString("SerialNumber") );//1唯一流水
	        param.put("KIND_ID", "BIP3A263_T3000263_0_0");// 交易唯一标识
	        
	        IDataset returnDaset = IBossCall.callHttpIBOSS("IBOSS", param);
	        IData res = returnDaset.getData(0);
		return res;
			
	}
	
	//4卡券转赠接口
	public IData pcardTransfer(IData datas) throws Exception{
		IData data = new DataMap(datas.toString()).getData("params");
		String loginType = IDataUtil.chkParam(data, "LoginType");
		
		   IData param = new DataMap();
	        param.put("LOGIN_TYPE", data.getString("LoginType"));//1登录类型
	        param.put("LOGIN_NO", data.getString("LoginNo"));//1登录账号
	        param.put("TARGET_TYPE", data.getString("TargetType"));//1受赠账号类型
	        param.put("TARGET_NO", data.getString("TargetNo"));//1受赠账号
	        param.put("CHANNEL_ID", data.getString("ChannelId"));//1渠道标识
	        param.put("PCARD_PASSWD",data.getString("PcardPasswd"));//1券码
	        param.put("TRANSFER_WAYS", data.getString("TransferWays"));//1转赠方式
	        param.put("SERIAL_NUMBER",data.getString("SerialNumber") );//1唯一流水
	        param.put("REGION_CODE", data.getString("RegionCode"));//1省份编码
	        param.put("NOTE", data.getString("Note"));//备注
	        param.put("KIND_ID", "BIP3A264_T3000264_0_0");// 交易唯一标识
	        
	        IDataset returnDaset = IBossCall.callHttpIBOSS("IBOSS", param);
	        IData res = returnDaset.getData(0);
		return res;
		
	}
	
	//5卡券使用/锁定/校验接口
	public IData pcardUseReq(IData datas) throws Exception{
		IData data = new DataMap(datas.toString()).getData("params");
		String loginType = IDataUtil.chkParam(data, "LoginType");
		
		   IData param = new DataMap();
	        param.put("LOGIN_TYPE", data.getString("LoginType"));//1登录类型
	        param.put("LOGIN_NO", data.getString("LoginNo"));//1登录账号
	        param.put("BUSI_TYPE", data.getString("BusiType"));//1品类
	        param.put("PCARD_PASSWD", data.getString("PcardPasswd"));//1券码
	        param.put("ORDER_ID", data.getString("OrderId"));//订单编号
	        param.put("ACCOUNT_FLAT",data.getString("AccountFlat"));//1对账分类标签
	        param.put("USE_DATE", data.getString("UseDate"));//1使用账期
	        param.put("CHANNEL_ID",data.getString("ChannelId") );//1渠道标识
	        param.put("OPT_TYPE", data.getString("optType"));//1操作类型
	        param.put("SERIAL_NUMBER", data.getString("SerialNumber"));//1唯一流水
	        param.put("REGION_CODE", data.getString("RegionCode"));//1省份编码
	        param.put("KIND_ID", "BIP3A265_T3000265_0_0");// 交易唯一标识
	        
	        IDataset returnDaset = IBossCall.callHttpIBOSS("IBOSS", param);
	        IData res = returnDaset.getData(0);
		return res;
		
	}
	
	//6卡券返销接口
	public IData pcardBackReq(IData datas) throws Exception{
		IData data = new DataMap(datas.toString()).getData("params");
		String loginType = IDataUtil.chkParam(data, "LoginType");
		
		   IData param = new DataMap();
	        param.put("ORDER_ID", data.getString("OrderId"));//1订单编码
	        param.put("BUSI_TYPE", data.getString("BusiType"));//1品类
	        param.put("PCARD_PASSWD", data.getString("PcardPasswd"));//1券码
	        param.put("LOGIN_TYPE", data.getString("LoginType"));//1操作类型
	        param.put("LOGIN_NO", data.getString("LoginNo"));//1操作账号
	        param.put("BACK_DATE",data.getString("BackDate"));//返销账期
	        param.put("CHANNEL_ID", data.getString("ChannelId"));//1渠道标识
	        param.put("ORDER_USER_TYPE",data.getString("OrderUserType") );//1下单用户类型
	        param.put("ORDER_USER", data.getString("OrderUser"));//1下单用户
	        param.put("REGION_CODE", data.getString("RegionCode"));//1省份编码
	        param.put("SERIAL_NUMBER", data.getString("SerialNumber"));//1唯一流水
	        param.put("KIND_ID", "BIP3A266_T3000266_0_0");// 交易唯一标识
	        
	        IDataset returnDaset = IBossCall.callHttpIBOSS("IBOSS", param);
	        IData res = returnDaset.getData(0);
		return res;
		
	}
	
	//7卡券资源查询
	public IData pcardInfoQuery(IData datas) throws Exception{
		IData data = new DataMap(datas.toString()).getData("params");
		String loginType = IDataUtil.chkParam(data, "LoginType");
		
		   IData param = new DataMap();
	        param.put("BATCH_ID", data.getString("BatchID"));//1批次编码
	        param.put("CHANNEL_ID", data.getString("ChannelId"));//1渠道标识
	        param.put("REGION_CODE", data.getString("RegionCode"));//1省份编码
	        param.put("SERIAL_NUMBER", data.getString("SerialNumber"));//1唯一流水
	        param.put("KIND_ID", "BIP3A267_T3000267_0_0");// 交易唯一标识

	        IDataset returnDaset = IBossCall.callHttpIBOSS("IBOSS", param);
	        IData res = returnDaset.getData(0);
		return res;
		
	}

	//8卡券激活接口
	public IData pcardActive(IData datas) throws Exception{
		IData data = new DataMap(datas.toString()).getData("params");
		String loginType = IDataUtil.chkParam(data, "LoginType");
		
		   IData param = new DataMap();
		    param.put("LOGIN_TYPE", data.getString("LoginType"));//1登录类型
	        param.put("LOGIN_NO", data.getString("LoginNo"));//1登录账号
	        param.put("ACTIVE_WAY", data.getString("ActiveWay"));//1批次编码
	        param.put("ACTIVE_CODE", data.getString("Activecode"));//1激活编码
	        param.put("ACTIVE_NUM", data.getString("ActiveNum"));//激活券数量
	        param.put("BIZ_TYPE",data.getString("BizType"));//1渠道标识
	        param.put("SERIAL_NUMBER", data.getString("SerialNumber"));//1唯一流水
	        param.put("KIND_ID", "BIP3A268_T3000268_0_0");// 交易唯一标识
	        
	        IDataset returnDaset = IBossCall.callHttpIBOSS("IBOSS", param);
	        IData res = returnDaset.getData(0);
		return res;
		
	}
}

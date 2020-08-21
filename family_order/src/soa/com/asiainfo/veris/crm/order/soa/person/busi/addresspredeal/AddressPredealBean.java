package com.asiainfo.veris.crm.order.soa.person.busi.addresspredeal;

import org.apache.axis.utils.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class AddressPredealBean extends CSBizBean{
	private static Logger logger = Logger.getLogger(AddressPredealBean.class);
	/**
	 * 发送短信提醒
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public void sendSMSNotice(IData param)throws Exception {
		String result="";
		String mobile="";
		String userId="1101";
		//查找分公司負責人電話
		if(StringUtils.isEmpty(param.getString("MOBILE"))){
			IData inParam=new DataMap();
			inParam.put("SUBSYS_CODE", "CSM");
			inParam.put("PARAM_ATTR", 2020);
			inParam.put("PARAM_CODE", param.getString("COMPANY"));
			inParam.put("EPARCHY_CODE", "ZZZZ");
	        IDataset listCommpara = Dao.qryByCode("TD_S_COMMPARA", "SEL1_PK_TD_S_COMMPARA", inParam);
	        result=listCommpara.toString();
	        if(listCommpara!=null&&listCommpara.size()>0){
	        	result+="==listCommpara.size["+listCommpara.size()+"]";
	        	for(int i=0;i<listCommpara.size();i++){
	        		mobile+=listCommpara.getData(i).getString("PARA_CODE1");
	        		if(i!=listCommpara.size()-1){
	        			mobile+=",";
	        		}
	        	}
	        	result+="==mobile["+mobile+"]";
	        }
		}else{
			mobile=param.getString("MOBILE");
		}
		
		if(StringUtils.isEmpty(mobile)){
			throw new Exception("根据分公司【"+param.getString("COMPANY")+"】找不到手机号配置"+result);
		}
		
		String[] mobiles=mobile.split(",");
		
		for(int j=0;j<mobiles.length;j++){
			
			mobile=mobiles[j];
			
			StringBuilder sql=new StringBuilder();
			sql.append("SELECT USER_ID FROM TF_F_USER T WHERE T.SERIAL_NUMBER=:SERIAL_NUMBER AND T.REMOVE_TAG='0'");
			IData inParam1=new DataMap();
			inParam1.put("SERIAL_NUMBER", mobile);
			IDataset userIds=Dao.qryBySql(sql, inParam1);
			if(userIds!=null&&userIds.size()>0){
				userId=userIds.getData(0).getString("USER_ID");
			}
			
			IData newParam = new DataMap();
			String seqId = SeqMgr.getSmsSendId();
			newParam.put("SMS_NOTICE_ID", seqId);
			newParam.put("PARTITION_ID", seqId.substring(seqId.length() - 4));
			newParam.put("SEND_COUNT_CODE", "1");
			newParam.put("REFERED_COUNT", "0");
			newParam.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
			newParam.put("IN_MODE_CODE", "0");
			newParam.put("CHAN_ID", "11");// 短信渠道编码:客户服务
			newParam.put("RECV_OBJECT_TYPE", "00");// 被叫对象类型:00－手机号码
			newParam.put("FORCE_OBJECT", "10086");
			newParam.put("RECV_OBJECT", mobile); // 被叫对象:传手机号码
			newParam.put("RECV_ID", userId); // 被叫对象标识:传用户标识
			newParam.put("SMS_TYPE_CODE", "20"); // 短信类型:20-业务通知
			newParam.put("SMS_KIND_CODE", "02"); // 短信种类:02－短信通知
			newParam.put("NOTICE_CONTENT_TYPE", "0");// 短信内容类型:0－指定内容发送
			newParam.put("NOTICE_CONTENT", param.getString("CONTENT"));// 短信内容类型:0－指定内容发送
			newParam.put("FORCE_REFER_COUNT", "1");// 指定发送次数
			newParam.put("SMS_PRIORITY", 1000);// 短信优先级
			newParam.put("REFER_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss")); // 提交时间
			newParam.put("REFER_STAFF_ID", getVisit().getStaffId());// 提交员工
			newParam.put("REFER_DEPART_ID", getVisit().getDepartId());// 提交部门
			newParam.put("DEAL_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss")); // 处理时间
			newParam.put("DEAL_STATE", "15");// 处理状态:0－未处理
			newParam.put("REMARK", param.get("REMARK"));
			newParam.put("SEND_TIME_CODE", "1");
			newParam.put("SEND_OBJECT_CODE", "6");

			newParam.put("SMS_NET_TAG", "0");
			newParam.put("MONTH", Integer.parseInt(SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss").substring(5, 7)));
			newParam.put("DAY", Integer.parseInt(SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss").substring(8, 10)));

			Dao.insert("TI_O_SMS", newParam);
		}
	}
	/**
	 * 查询用户是否开过宽带
	 * @param dataSet
	 * @return
	 * @throws Exception
	 */
	public IDataset qryWideNetUser(IData param) throws Exception{
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT USER_ID FROM TF_F_USER T WHERE T.SERIAL_NUMBER=:SERIAL_NUMBER AND T.REMOVE_TAG='0'");
		IData inParam1=new DataMap();
		inParam1.put("SERIAL_NUMBER", param.getString("mobile"));
		return Dao.qryBySql(sql, inParam1);
	}
}

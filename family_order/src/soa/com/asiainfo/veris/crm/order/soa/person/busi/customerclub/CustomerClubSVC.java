package com.asiainfo.veris.crm.order.soa.person.busi.customerclub;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * 中国移动客户俱乐部业务 
 * @author zoulu
 *
 */
public class CustomerClubSVC extends CSBizService {

	private static final long serialVersionUID = 2480626104003988242L;
	
	 /**
	 * 查询会员信息
	 * @param input
	 * @return
	 * @throws Exception
	 */
	 public IDataset queryClubInfo(IData input) throws Exception
	 {
		CustomerClubBean bean = (CustomerClubBean)BeanManager.createBean(CustomerClubBean.class);
		return bean.queryClubInfo(input);
	 }
		

	 /**
	 * 根据会员信息退出俱乐部
	 * @param input
	 * @return
	 * @throws Exception
	 */
	 public void retreatClub(IData input) throws Exception
	 {
		CustomerClubBean bean = (CustomerClubBean)BeanManager.createBean(CustomerClubBean.class);
		String retreatTradeId=bean.retreatClub(input);//退会插入记录
		input.put("RETREAT_TRADE_ID", retreatTradeId);
		bean.updateRetreatClub(input);//退会失效原有记录
	 }
	 
	 /**
	 * 根据会员信息入会
	 * @param input
	 * @return
	 * @throws Exception
	 */
	 public void insertClub(IData input) throws Exception
	 {
		 CustomerClubBean bean = (CustomerClubBean)BeanManager.createBean(CustomerClubBean.class);
		 bean.insertClub(input);
	 }
	 
	 /**
	 * 获取seq_id
	 * @param input
	 * @return
	 * @throws Exception
	 */
	 public IDataset getSeq(IData input) throws Exception
	 {
		CustomerClubBean bean = (CustomerClubBean)BeanManager.createBean(CustomerClubBean.class);
		return bean.getSeq(input);
	 }
	 
	 
	 /**
	 *  更新原有退会信息无效
	 * @param input
	 * @return
	 * @throws Exception
	 */
	 public void updateInsertClub(IData input) throws Exception
	 {
		CustomerClubBean bean = (CustomerClubBean)BeanManager.createBean(CustomerClubBean.class);
		bean.updateInsertClub(input);
	 }
	 
	 /**
	 *  IBOSS校验
	 * @param input
	 * @return
	 * @throws Exception
	 */
	 public IData checkClubInfoIboss(IData input) throws Exception
	 {
		CustomerClubBean bean = (CustomerClubBean)BeanManager.createBean(CustomerClubBean.class);
		return bean.clubInfoIboss(input);
	 }
	 
		 
	 /**
	 *  IBOSS入会
	 * @param input
	 * @return
	 * @throws Exception
	 */
	 public IData insertClubIboss(IData input) throws Exception
	 {
		CustomerClubBean bean = (CustomerClubBean)BeanManager.createBean(CustomerClubBean.class);
		
		return bean.clubInfoIboss(input);
	 }
	 /**
	 *  IBOSS退会
	 * @param input
	 * @return
	 * @throws Exception
	 */
	 public IData retreatClubIboss(IData input) throws Exception
	 {
		CustomerClubBean bean = (CustomerClubBean)BeanManager.createBean(CustomerClubBean.class);
		
		return bean.clubInfoIboss(input);
	 }
	 
	 /**
	 *  更新入会信息
	 * @param input
	 * @return
	 * @throws Exception
	 */
	 public void updateRetreatClub(IData input) throws Exception
	 {
		CustomerClubBean bean = (CustomerClubBean)BeanManager.createBean(CustomerClubBean.class);
		
		bean.updateRetreatClub(input);
	 }
	 
	 /**
	 *  入会接口
	 *  提供给外部调用
	 *  SERIAL_NUMBER
	 *  CLUB_TYPE
	*/
	public IData joinClubIntf(IData input) throws Exception
	{
		IData rtnData=new DataMap();
		String serialNumber = input.getString("SERIAL_NUMBER", "");
		// 查询用户资料
        IDataset datauser = UserInfoQry.getAllUserInfoBySn(serialNumber);
        if (datauser == null || datauser.size() == 0)
        {
        	rtnData.put("RESULT_CODE", "-1");
			rtnData.put("RESULT_INFO", "用户号码【"+serialNumber+"】不存在用户资料");
			return rtnData;
        }
        String custId=datauser.getData(0).getString("CUST_ID");
		String custName=UcaInfoQry.qryCustInfoByCustId(custId).getString("CUST_NAME");
		String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		String inModeCode ="12";// getVisit().getInModeCode();
//		IDataset inModes = CommparaInfoQry.getCommparaAllColByParser("CSM", "2526", inModeCode,"898");
//		if(inModes!=null && inModes.size()>0){
//			inModeCode=inModes.getData(0).getString("PARA_CODE1");
//		}else{
//			//CSAppException.apperr(CrmCommException.CRM_COMM_103,"渠道对应关系不存在。in_mode_code="+inModeCode);
//			rtnData.put("RESULT_CODE", "-1");
//			rtnData.put("RESULT_INFO", "渠道对应关系不存在。in_mode_code="+inModeCode);
//			return rtnData;
//		}
		param.put("CHANNEL", inModeCode);
		
		IDataset tradeIds = getSeq(param);
		if( null != tradeIds && !tradeIds.isEmpty()){
			String tradeId = tradeIds.getData(0).getString("SEQ_ID");
			String sqlId = tradeId.substring(tradeId.length()-6, tradeId.length());
			param.put("SEQ", date + sqlId);
			param.put("TRADE_ID", date + sqlId);
		} 
		param.put("CLUB_TYPE", input.getString("CLUB_TYPE",""));
		param.put("AGREEMENT", "Y");
		param.put("SIGNING_TIME", SysDateMgr.getSysDateYYYYMMDD());
		param.put("ENTRY_TIME", SysDateMgr.getSysDateYYYYMMDD());
		param.put("RELATE_ACTIVITY", "0000");
		param.put("CUST_NAME", custName);
		param.put("RESERVE", "");
		param.put("KIND_ID","BIP5A061_T5000061_0_0");//会员入会受理请求
		IData temp=new DataMap();
		temp.put("SERIAL_NUMBER", serialNumber);
		temp.put("CLUB_TYPE", param.getString("CLUB_TYPE"));
		temp.put("AGREEMENT", param.getString("AGREEMENT"));
		temp.put("ENTRY_TIME", param.getString("ENTRY_TIME"));
		temp.put("CHANNEL", "");
		temp.put("RELATE_ACTIVITY", param.getString("RELATE_ACTIVITY"));
		temp.put("SIGNING_TIME", SysDateMgr.getSysTime());
		temp.put("RESERVE", inModeCode);
		IDataset temps=new DatasetList();
		temps.add(temp);
		param.put("MemberInfo", temps);
		
		String step="";
		try{
			CustomerClubBean bean = (CustomerClubBean)BeanManager.createBean(CustomerClubBean.class);
			step="调用平台入会";
			IData inClubs=bean.clubInfoIboss(param);//调用平台入会
			
			if (null != inClubs && !inClubs.isEmpty()) {
				String tradeResCode=inClubs.getString("RES_CODE");
				if ("00".equals(tradeResCode)) {
					step="插入入会数据";
					param.put("IN_MODE_CODE", inModeCode);
					param.put("AGREEMENT_TAG", "Y");//签署
					param.put("RELATED_ACTIVITY_ID", "0000");
					bean.insertClub(param);//插入入会数据
					step="终止原有数据";
					bean.updateInsertClub(param);//终止原有数据
					rtnData.put("RESULT_CODE", "0");
					rtnData.put("RESULT_INFO", "【俱乐部入会】业务办理成功。");
				}else{
					String errInfo=inClubs.getString("RES_DESC");
					if(errInfo==null || "".equals(errInfo)){
						errInfo=inClubs.getString("X_RSPDESC");
					}
					rtnData.put("RESULT_CODE", "-1");
					rtnData.put("RESULT_INFO", "【俱乐部入会】平台办理失败。返回码：【"+tradeResCode+"】。原因="+errInfo);
					return rtnData;
				}  
			}
		}catch(Exception e){
			rtnData.put("RESULT_CODE", "-1");
			rtnData.put("RESULT_INFO", "【俱乐部入会】业务办理失败。步骤：【"+step+"】。信息="+getErrorMsg(e.getMessage(),200));
			return rtnData;
		}
		return rtnData;
	} 
	/**
	*  退会接口
	*  提供给外部调用
	*/
	public IData retreatClubIntf(IData input) throws Exception
	{
		IData rtnData=new DataMap();
		String serialNumber = input.getString("SERIAL_NUMBER", ""); 
		// 查询用户资料
        IDataset datauser = UserInfoQry.getAllUserInfoBySn(serialNumber);
        if (datauser == null || datauser.size() == 0)
        {
        	rtnData.put("RESULT_CODE", "-1");
			rtnData.put("RESULT_INFO", "用户号码【"+serialNumber+"】不存在用户资料");
			return rtnData;
        }
        String custId=datauser.getData(0).getString("CUST_ID");
		String custName=UcaInfoQry.qryCustInfoByCustId(custId).getString("CUST_NAME");
		String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();

		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		String inModeCode= "12";//CSBizBean.getVisit().getInModeCode();
//		IDataset inModes = CommparaInfoQry.getCommparaAllColByParser("CSM", "2526", inModeCode,"898");
//		if(inModes!=null && inModes.size()>0){
//			inModeCode=inModes.getData(0).getString("PARA_CODE1");
//		}else{
//			//CSAppException.apperr(CrmCommException.CRM_COMM_103,"渠道对应关系不存在。in_mode_code="+inModeCode);
//			rtnData.put("RESULT_CODE", "-1");
//			rtnData.put("RESULT_INFO", "渠道对应关系不存在。in_mode_code="+inModeCode);
//			return rtnData;
//		}
		param.put("CHANNEL", inModeCode);
		IDataset tradeIds = getSeq(param);
		if( null != tradeIds && !tradeIds.isEmpty()){
			String tradeId = tradeIds.getData(0).getString("SEQ_ID");
			String sqlId = tradeId.substring(tradeId.length()-6, tradeId.length());
			param.put("SEQ", date + sqlId);
			param.put("TRADE_ID", date + sqlId);
		}
		IData param1 = new DataMap();
		param1.put("SERIAL_NUMBER", serialNumber); 
		String clubType = input.getString("CLUB_TYPE","");
		param.put("CLUB_TYPE", clubType);
		param.put("REASON", input.getString("REASON", ""));
		param.put("KIND_ID", "BIP5A062_T5000062_0_0");//会员退会受理请求
		IData temp=new DataMap();
		temp.put("SERIAL_NUMBER", serialNumber);
		temp.put("CLUB_TYPE", param.getString("CLUB_TYPE"));
		temp.put("CHANNEL", inModeCode);
		temp.put("REASON", param.getString("REASON"));
		IDataset temps=new DatasetList();
		temps.add(temp);
		param.put("MemberInfo", temps);
		String step="";
		try{
			CustomerClubBean bean = (CustomerClubBean)BeanManager.createBean(CustomerClubBean.class);	
			step="调平台接口退会";
			IData outInfo=bean.clubInfoIboss(param);//调平台接口退会
			
			if( null != outInfo && !outInfo.isEmpty()){ 
				String tradeResCode = outInfo.getString("RES_CODE"); // 应答编码				
				if ("00".equals(tradeResCode)) { // :00-查询成功
					IData retreatParam = new DataMap();
					retreatParam.put("SERIAL_NUMBER", serialNumber);
					retreatParam.put("CLUB_TYPE", clubType);
					step="退会插入记录";
					
					String retreatTradeId=bean.retreatClub(param);//退会插入记录
					input.put("RETREAT_TRADE_ID", retreatTradeId);
					step="退会失效原有记录";
					bean.updateRetreatClub(param);//退会失效原有记录
					rtnData.put("RESULT_CODE", "0");
					rtnData.put("RESULT_INFO", "【俱乐部退会】业务办理成功。"); 
				} else { 
					String errInfo=outInfo.getString("RES_DESC");
					if(errInfo==null || "".equals(errInfo)){
						errInfo=outInfo.getString("X_RSPDESC");
					}
					rtnData.put("RESULT_CODE", "-1");
					rtnData.put("RESULT_INFO", "【俱乐部退会】平台办理失败。返回码：【"+tradeResCode+"】。原因="+errInfo);
					return rtnData;
				}
			}
		}catch(Exception e){
			rtnData.put("RESULT_CODE", "-1");
			rtnData.put("RESULT_INFO", "【俱乐部退会】业务办理失败。步骤：【"+step+"】。信息="+getErrorMsg(e.getMessage(),200));
			return rtnData;
		}
		return rtnData;
	}
	
	
	private String getErrorMsg(String msg, int length)
    {
        String error = "";
        byte[] bytes = msg.getBytes();
        if (bytes.length <= length)
        {
            error = msg;
        }
        else
        {
            byte[] newbytes = new byte[length];
            for (int i = 0; i < length; i++)
            {
                newbytes[i] = bytes[i];
            }
            error = new String(newbytes);
        }
        return error;
    }
    
	/**
	 * 获取in_mode_code与渠道的比对关系
	 * */	 
	public IData getInModeCodeChange(IData input)throws Exception{
		IData inmode=new DataMap();
		IDataset inModes = CommparaInfoQry.getCommparaAllColByParser("CSM", "2526", input.getString("IN_MODE_CODE",""),"898");
		if(inModes!=null && inModes.size()>0){
			String channel=inModes.getData(0).getString("PARA_CODE1");
			inmode.put("CHANNEL", channel);
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"渠道对应关系不存在。in_mode_code="+input.getString("IN_MODE_CODE","")); 
		}
		return inmode;
	}
}

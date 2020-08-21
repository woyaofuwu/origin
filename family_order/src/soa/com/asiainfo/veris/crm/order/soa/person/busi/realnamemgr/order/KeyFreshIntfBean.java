package com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.order;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.CreatePostPersonUserSVC;
import com.asiainfo.veris.crm.order.soa.person.common.util.HttpSvcTool;

public class KeyFreshIntfBean extends CSBizBean {
	private static Logger log = Logger.getLogger(KeyFreshIntfBean.class);

	
	/**
	 * 作用：客户端密钥更新
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData KeyFreshQuery (IData data) throws Exception{
		IData result = new DataMap();
		if (data.getString("PROVINCE_CODE") == null
				|| data.getString("PROVINCE_CODE").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_543);
		}

		/** 获取开关数据 modify by zouyi 20150525 **/
		IData reqType = getRequstType();
		data.put("para_code1", reqType.getString("PARA_CODE1"));// PARA_CODE1业务开关1打开，其他关闭
		data.put("para_code2", reqType.getString("PARA_CODE2"));// 互联网 KIND_ID
		data.put("para_code3", reqType.getString("PARA_CODE3"));// 网状网 KIND_ID
		/** end **/

		IData outparams = IBossCall.callIbossKeyFresh(data).getData(0);
		if(outparams!=null&&outparams.getString("PRIVATE_KEY")!=null){
			IData tableData = new DataMap();
			tableData.put("RSRV_TAG1", "0");//RSRV_TAG1 = 0(rsa加密秘钥)
			tableData.put("TRANSACTION_ID", "9999");//rsa加密秘钥实时更新。TRANSACTION_ID写死，保证只一条数据
			tableData.put("KEY", outparams.getString("PRIVATE_KEY"));
			tableData.put("RSRV_DATE1", SysDateMgr.getSysTime());
	        Dao.update("TD_B_PUSHKEY", tableData, new String[]{"TRANSACTION_ID","RSRV_TAG1"}, Route.CONN_CRM_CEN); //更新rsa加密秘钥
		result.put("PRIVATE_KEY", outparams.getString("PRIVATE_KEY"));
		result.put("RESULT_CODE", "00");
		result.put("RESULT_INFO", "ok");
		result.put("RETURN_CODE", "0000");
		
		receivingOnLineCompanyInterface(outparams.getString("PRIVATE_KEY"));//接收在线公司私钥接口-wangsc10-20180919
		
		}else{
			result.put("RESULT_CODE", "01");
			result.put("RESULT_INFO", outparams.getString("RETURN_MESSAGE"));
			result.put("RETURN_CODE", "1001");
		}
		
		return result;
	}
	
	//strat接收在线公司私钥接口-wangsc10-20180919
	/**
	 * 作用：接收在线公司私钥接口
	 * @param key
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData receivingOnLineCompanyInterface (String key) throws Exception{
		IData result = new DataMap();
		
		IData outParam = new DataMap();
		IData inParam = new DataMap();
		try{
			IDataset commparaInfos = CommparaInfoQry.getCommNetInfo("CSM", "5221", "URL");
			IData commparaInfo=commparaInfos.getData(0);
			String url = commparaInfo.getString("PARA_CODE3","");
			
			inParam.put("busiCode", "RSA_KEY_FRESH");
			inParam.put("privateKey", key);
			inParam.put("createtime", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
			outParam=HttpSvcTool.sendHttpPostMsg(url,inParam.toString(),null);
			String code = outParam.getString("code");
			String msg = outParam.getString("msg");
			if(code.equals("0")){
				result.put("RESULT_CODE", "00");
				result.put("RESULT_INFO", "ok");
				result.put("RETURN_CODE", "0000");
			}else{
				result.put("RESULT_CODE", "01");
				result.put("RESULT_INFO", msg);
				result.put("RETURN_CODE", "2999");
			}
		}
		catch (Exception e) {
			result.put("RESULT_CODE", "01");
			result.put("RESULT_INFO", "调用新大陆电渠失败！"+e);
			result.put("RETURN_CODE", "2999");
			log.info("KeyFreshIntfSVC-Exception",e);
		}
		return result;
	}
	//end

	/**
	 * 获取请求方式配置
	 * 
	 * @return
	 * @throws Exception
	 */
	public IData getRequstType() throws Exception {
		IData inparams = new DataMap();
		inparams.put("SUBSYS_CODE", "CSM");
		inparams.put("PARAM_ATTR", "1522");
		inparams.put("PARAM_CODE", "RSA_KEY_FRESH");

		IDataset ds = Dao.qryByCode("TD_S_COMMPARA", "SEL_PARAM_BY_CODE",
				inparams);

		if ((ds != null) && (ds.size() > 0)) {
			// String para_code1 = ds.getData(0).getString("para_code1");
			// String para_code2 = ds.getData(0).getString("para_code2");
			// String para_code3 = ds.getData(0).getString("para_code3");

			return ds.getData(0);
		}

		return null;
	}

	public IData sendPushKey(IData data) throws Exception {

		String transaction_id = data.getString("TRANSACTION_ID");
		IData outparams = new DataMap();
		outparams.put("RSRV_TAG1", "2");//原有数据RSRV_TAG1 =2 普通文件敏感内容加密秘钥
		outparams.put("TRANSACTION_ID", data.getString("TRANSACTION_ID"));
		outparams.put("PROV_CODE", data.getString("PROVINCE_CODE"));
		outparams.put("KEY", data.getString("KEY"));
		outparams.put("CERT_EXPDATE", data.getString("CERT_EXPDATE"));
		outparams.put("RSRV_DATE1", SysDateMgr.getSysTime());
		Dao.insert("TD_B_PUSHKEY", outparams, Route.CONN_CRM_CEN);// 插入普通文件敏感内容加密秘钥

		IData result = new DataMap();
		 result.put("TRANSACTION_ID", transaction_id);
		result.put("RETURN_CODE", "0000");
		result.put("RETURN_MESSAGE", "SUCCESS");
		return result;
	}
	public IData pushCardKey(IData data) throws Exception {
		String transaction_id = data.getString("TRANSACTION_ID");
		IData outparams = new DataMap();
		outparams.put("RSRV_TAG1", "1");//RSRV_TAG1 = 1(卡数据解密秘钥)	 
		outparams.put("RSRV_DATE1", SysDateMgr.getSysTime());//RSRV_TAG1 = 1(卡数据解密秘钥)
		outparams.put("TRANSACTION_ID", data.getString("TRANSACTION_ID"));
		outparams.put("KEY", data.getString("PRIVATE_KEY"));
		Dao.insert("TD_B_PUSHKEY", outparams, Route.CONN_CRM_CEN); // 插入卡数据解密秘钥
		IData result = new DataMap();
		result.put("TRANSACTION_ID", transaction_id);
		result.put("RETURN_CODE", "0000");
		result.put("RETURN_MESSAGE", "SUCCESS");
		return result;
	}

	public IData pushCardKey2(IData data) throws Exception {
		String transaction_id = data.getString("TRANSACTION_ID");
		IData outparams = new DataMap();
		outparams.put("RSRV_TAG1", "1");//RSRV_TAG1 = 1(卡数据解密秘钥)
		outparams.put("RSRV_DATE1", SysDateMgr.getSysTime());//RSRV_TAG1 = 1(卡数据解密秘钥)
		outparams.put("TRANSACTION_ID", data.getString("TRANSACTION_ID"));
		outparams.put("KEY", data.getString("KEY"));
		Dao.insert("TD_B_PUSHKEY", outparams, Route.CONN_CRM_CEN); // 插入卡数据解密秘钥

		IData returnMap = new DataMap();
		returnMap.put("rtnCode", "0");
		returnMap.put("rtnMsg", "成功");
		IData object = new DataMap();
		returnMap.put("object", object);
		object.put("respCode", "0");
		object.put("respDesc", "success");
		IDataset result = new DatasetList();
		object.put("result", result);
		IData resultItem = new DataMap();
		result.add(resultItem);
		resultItem.put("transactionID", transaction_id);
		return returnMap;
	}

}

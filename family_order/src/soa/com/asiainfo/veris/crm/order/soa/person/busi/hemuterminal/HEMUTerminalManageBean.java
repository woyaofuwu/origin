
package com.asiainfo.veris.crm.order.soa.person.busi.hemuterminal;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.BatDealStateUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class HEMUTerminalManageBean extends CSBizBean
{

    static transient final Logger logger = Logger.getLogger(HEMUTerminalManageBean.class);

    public void insertDiscntInfo(IData data) throws Exception
    {
        Dao.insert("TD_B_DISCNT", data, Route.CONN_CRM_CEN);
    }

    public void upDiscntInfo(IData data) throws Exception
    {
        Dao.save("TD_B_DISCNT", data, new String[]
        { "DISCNT_CODE" }, Route.CONN_CRM_CEN);
    }

	/**
	 * @Description：TODO
	 * @param:@param input
	 * @param:@return
	 * @return IData
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-11-29上午09:36:07
	 */
	public IData getTerminalBySN(IData input) throws Exception {
		StringBuilder sql = new StringBuilder(3000);
		sql.append("SELECT T.INST_ID,T.RES_CODE,T.DEVICE_MODEL_CODE,T.DEVICE_BRAND_CODE,T.GOODS_NAME  ");
		sql.append(" FROM TF_F_USER_SALE_GOODS T ");
		sql.append(" WHERE INSTR((SELECT A.PARA_CODE1 FROM TD_S_COMMPARA A WHERE A.PARAM_ATTR='2259'),T.PRODUCT_ID)>0 ");
		sql.append(" AND T.RES_CODE>0");
		sql.append(" AND T.USER_ID=:USER_ID");
		sql.append(" AND T.CANCEL_DATE>SYSDATE");
		IDataset ids = Dao.qryBySql(sql, input, "");
		if (IDataUtil.isNotEmpty(ids))
        {
            return ids.getData(0);
        }else{
        	return null;
        }
	}

	/**
	 * @Description：TODO
	 * @param:@param input
	 * @return void
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-12-1上午11:53:06
	 */
	public IData submit(IData input) throws Exception {
		IData retData = new DataMap();
		StringBuilder sql = new StringBuilder(3000);
		sql.append("UPDATE TF_F_USER_SALE_GOODS SET RES_CODE=:RES_ID");
		sql.append(",DEVICE_MODEL_CODE=:DEVICE_MODEL_CODE");
		sql.append(",DEVICE_MODEL=:DEVICE_MODEL");
		sql.append(",DEVICE_COST=:DEVICE_COST");
		sql.append(",DEVICE_BRAND=:DEVICE_BRAND");
		sql.append(",UPDATE_STAFF_ID=:UPDATE_STAFF_ID");
		sql.append(",UPDATE_DEPART_ID=:UPDATE_DEPART_ID");
		sql.append(",UPDATE_TIME=SYSDATE");
		sql.append("  WHERE INST_ID=:INST_ID");
		int result = Dao.executeUpdate(sql, input);
		
		if (result>0)
        {
			retData.put("RESULT_CODE", "0");
			retData.put("RESULT_INFO", "操作成功！");
			return retData;
        }else{
        	return null;
        }
		
	}

	/**
	 * @Description：TODO
	 * @param:@param resNo
	 * @param:@return
	 * @return IDataset
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-12-4上午11:21:06
	 */
	public IData getResNoByOhter(String resNo) throws Exception {
		StringBuilder sql = new StringBuilder(3000);
		IData param = new DataMap();
		param.put("RES_NO", resNo);
		sql.append("SELECT USER_ID FROM TF_F_USER_SALE_GOODS WHERE RES_CODE=:RES_NO");
		IDataset ids = Dao.qryBySql(sql, param, "");
		if (IDataUtil.isNotEmpty(ids))
        {
            return ids.getData(0);
        }else{
        	return null;
        }
	}

	/**
	 * @Description：校验用户是否办理了和商务套餐
	 * @param:@param para
	 * @param:@return
	 * @return IData
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-4-16上午11:21:59
	 */
	public IData checkHSWUserByUserId(String userId) throws Exception {
		IData resultData = new DataMap();
		boolean flag = false;
		IDataset comparas =BreQryForCommparaOrTag.getCommpara("CSM",2260,"-1","ZZZZ");
		if(IDataUtil.isNotEmpty(comparas)){
			 String product_id_Str=((IData)comparas.get(0)).getString("PARA_CODE1");
             String buf[]=product_id_Str.split("\\|");
             for (int i = 0, len = buf.length; i < len; i++)
             {
                 String pro_id=buf[i];
                 IDataset sales = UserSaleActiveInfoQry.queryUserSaleActiveProdId(userId,pro_id,"0");
                 if (IDataUtil.isNotEmpty(sales)&&sales.size()>0)
                 {
                	IDataset other = UserOtherInfoQry.queryUserOtherInfos(userId, "HEMU", "HEMU_APPLY");
                	IData goods = sales.first();
                	resultData.put("PRODUCT_ID", pro_id);
                	resultData.put("PACKAGE_ID", goods.getString("PACKAGE_ID"));
                	if(IDataUtil.isEmpty(other)){
                		resultData.put("CODE", "0000");
                		resultData.put("MSG", "用户参加了和商务活动【+"+pro_id+"】");
                		
                	}else{
                  		resultData.put("CODE", "0001");
                		resultData.put("MSG", "用户已经申领和目终端");
                		resultData.put("RES_CODE", ((IData) other.get(0)).getString("RSRV_STR1"));
                		resultData.put("RES_NAME", ((IData) other.get(0)).getString("RSRV_STR9"));
                		resultData.put("INST_ID", ((IData) other.get(0)).getString("INST_ID"));
                		resultData.put("DEPOSIT_TRADE_ID", ((IData) other.get(0)).getString("RSRV_STR8"));
                	}
                	flag = true;
         			break;
                 }
             }
             if(!flag){
            	 resultData.put("CODE", "0002");
            	 resultData.put("MSG", "用户没有参加和商务活动");
             }
            	 
		}
		else{
			resultData.put("CODE", "2998");
			resultData.put("MSG", "不存在和商务参数配置");
		}
		return resultData;
	}

	/**
	 * @Description：退还押金
	 * @param:@param user
	 * @return void
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-4-27下午04:06:30
	 */
	public void returnDeposit(IData user) throws Exception {
		String depositTradeId = user.getString("TRADE_ID");//押金转移流水
    	String serialNumber = user.getString("SERIAL_NUMBER");//手机号码
		
		
        IData param = new DataMap();
        param.put("OUTER_TRADE_ID", depositTradeId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_FEE", "10000");
        param.put("CHANNEL_ID", "15000");
        param.put("UPDATE_DEPART_ID", user.getString("UPDATE_DEPART_ID"));
        param.put("UPDATE_STAFF_ID", user.getString("UPDATE_STAFF_ID"));
        param.put("TRADE_CITY_CODE",  "0898");
        param.put("TRADE_DEPART_ID", user.getString("UPDATE_DEPART_ID"));
        param.put("TRADE_STAFF_ID", user.getString("UPDATE_STAFF_ID"));

        IData result = AcctCall.transFeeOutADSL(param);
        if(com.ailk.bizcommon.util.IDataUtil.isNotEmpty(result)&&"0".equals(result.get("RESULT_CODE"))){
        	IData data = new DataMap();
    		data.put("DEAL_STATE", "2");//押金状态：0,押金、1,已转移、2已退还、3,已沉淀
    		data.put("REMARK", "三年到期退还押金");
    		data.put("INST_ID", user.getString("INST_ID"));
    		StringBuilder sql = new StringBuilder();

    		sql.append(" UPDATE tf_f_user_other a");
    		sql.append(" SET a.RSRV_STR7 = :DEAL_STATE,a.remark = :REMARK,a.UPDATE_TIME=sysdate");
    		sql.append(" where 1=1");
    		sql.append(" and a.inst_id = :inst_id");

    		Dao.executeUpdate(sql, data);
        }
        
	}

}

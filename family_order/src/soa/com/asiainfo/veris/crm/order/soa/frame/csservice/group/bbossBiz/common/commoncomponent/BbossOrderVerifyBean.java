package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePoInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

/**
 * 2015-1-27
 * 用于   重复归档校验和重复开通工单校验
 * @author chenyi
 *
 */
public class BbossOrderVerifyBean {
	/**
	 * 2015-1-27
	 * chenyi
	 * 校验集团是否重复归档
	 * @param map
	 * @return
	 * @throws Exception 
	 */
	public  static  IDataset verifyGrpBBossRsp(IData map) throws Exception{
		IDataset resultDataset=new DatasetList();
		//1-获取产品订单号
		IDataset psubscribeIdSet = IDataUtil.getDatasetSpecl("PSUBSCRIBE_ID", map); // 产品订单号
		//2 校验订单号在资料表是否存在对应数据
		for(int i=0,sizeI=psubscribeIdSet.size();i<sizeI;i++){
			 String proNumber = GrpCommonBean.nullToString(psubscribeIdSet.get(i));
			 IDataset merchpInfo=UserGrpMerchpInfoQry.qryMerchpInfosByPro(proNumber,null);
			 if(IDataUtil.isNotEmpty(merchpInfo)){
				 IData result_data = new DataMap();// 返回结果
				 result_data.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
				 result_data.put("EC_SERIAL_NUMBER", map.getString("EC_SERIAL_NUMBER", ""));
		         result_data.put("SUBSCRIBE_ID", map.getString("SUBSCRIBE_ID", ""));
		         result_data.put("PSUBSCRIBE_ID", IDataUtil.getDatasetSpecl("PSUBSCRIBE_ID", map)); // 产品订单号
		         result_data.put("RSPCODE".toUpperCase(),"50");
		         result_data.put("RSP_DESC".toUpperCase(), "集团重复归档");
		         result_data.put("X_RESULTCODE".toUpperCase(), "99");// 其他错误
		         result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1]);
		         result_data.put("X_RSPCODE", "2998");
		         result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
		         result_data.put("X_RSPTYPE", "2");
		         resultDataset.add(result_data);
		         return resultDataset;
			 }
		}
     	return resultDataset;
     }
	/**
	 * 2015-1-27
	 * chenyi
	 * 校验成员是否重复归档
	 * @param map
	 * @return
	 */
	public  static  IData verifyMemberBBossRsp(IData map){
     	return null;
     }
	/**
	 * 2015-1-27
	 * chenyi
	 * 校验集团开通工单重复
	 * @param map
	 * @return
	 * @throws Exception 
	 */
	public  static  IDataset verifyGrpBBossOrder(IData map) throws Exception{
		IDataset resultDataset=new DatasetList();
		//1-获取工单订单号
		String productordernumber=map.getString("ORDER_NO");
		IDataset proNumberInfo=TradePoInfoQry.qryProNumberInfo(productordernumber);
		//2-查询对应工单号在工单表是否有对应成功记录
		if(IDataUtil.isNotEmpty(proNumberInfo)){
			 IData result_data = new DataMap();// 返回结果
			 result_data.put("RSPCODE".toUpperCase(),"17");
	         result_data.put("RSP_DESC".toUpperCase(),"重复的产品订单号");
	         result_data.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
	         result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1] );
	         result_data.put("X_RSPCODE", "2998");
	         result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
	         result_data.put("X_RSPTYPE", "2");
	         resultDataset.add(result_data);
	         return resultDataset;
		}
    	return resultDataset;
     }
	/**
	 * 2015-1-27
	 * chenyi
	 * 校验成员工单重复
	 * @param map
	 * @return
	 * @throws Exception 
	 */
	public  static  IDataset verifyMemberBBossOrder(IData map) throws Exception{

		IDataset resultDataset=new DatasetList();
		//1-获取工单订单号
		String productordernumber=map.getString("ORDER_NO");
		IDataset proNumberInfo=TradePoInfoQry.qryProNumberInfo(productordernumber);
		//2-查询对应工单号在工单表是否有对应成功记录
		if(IDataUtil.isNotEmpty(proNumberInfo)){
			 IData result_data = new DataMap();// 返回结果
			 result_data.put("RSPCODE".toUpperCase(),"98");
	         result_data.put("RSP_DESC".toUpperCase(),"重复的产品订单号");
	         result_data.put("MOB_NUM".toUpperCase(),map.getString("SERIAL_NUMBER"));
	         result_data.put("OPR_NUMB".toUpperCase(),map.getString("PKGSEQ", ""));
	         result_data.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
	         result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1] );
	         result_data.put("X_RSPCODE", "2998");
	         result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
	         result_data.put("X_RSPTYPE", "2");
	         resultDataset.add(result_data);
	         return resultDataset;
		}
    	return resultDataset;
     
     }
	/**
	 * 2015-1-27
	 * chenyi
	 * 校验流量叠加包重复工单
	 * @param map
	 * @return
	 * @throws Exception 
	 */
	public  static  IDataset verifyBbossPayBizOrder(IData map) throws Exception{
		IDataset resultDataset=new DatasetList();
		// 1- 判断是否为文件接口，RETURN_FLAG_KT为非空时代表文件接口，此时工单反馈由IBOSS完成  
		//文件接口用的订单号一样
        String returnFlag = map.getString("RETURN_FLAG_KT", "");
        if(StringUtils.isNotEmpty(returnFlag)){
            return resultDataset;
        }
		
		//2-获取工单订单号
		String memberordernumber=map.getString("MEMBER_ORDER_NUMBER");
		IDataset proNumberInfo=TradePoInfoQry.qryProNumberInfo(memberordernumber);
		//3-查询对应工单号在工单表是否有对应成功记录
		if(IDataUtil.isNotEmpty(proNumberInfo)){
			 IData result_data = new DataMap();// 返回结果
			 result_data.put("ORDER_RESULT", "05");
			 result_data.put("MEMBER_ORDER_NUMBER", map.getString("MEMBER_ORDER_NUMBER", ""));
			 result_data.put("RESULT_DESC", "订单号重复");
			 result_data.put("RSPCODE".toUpperCase(),"05");
	         result_data.put("RSP_DESC".toUpperCase(),"重复的产品订单号");
	         result_data.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
	         result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1] );
	         result_data.put("X_RSPCODE", "2998");
	         result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
	         result_data.put("X_RSPTYPE", "2");
	         resultDataset.add(result_data);
	         return resultDataset;
		}
    	return resultDataset;
     }
	
	/**
	 * 将处理工单保存到对应工单表
	 * chenyi
	 * 2015-1-27
	 * @param map
	 * @param dealState
	 * @throws Exception 
	 */
	public  static void saveOrderInfo(String OrderNumber ,String dealState ) throws Exception {
		 IData param = new DataMap();
		 param.put("SEQ_ID", SeqMgr.getBBossProductIdForGrp());
	     param.put("ORDERNUMBER", OrderNumber);
	     param.put("DEAL_STATE", dealState);
	     param.put("UPDATE_TIME",SysDateMgr.getSysTime());
	     Dao.insert("TF_B_PO_PRONUMBER", param,Route.getJourDb(Route.CONN_CRM_CG));
	     
	}

	/**
	 * 校验集团是否重复归档
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public  static  IDataset verifyJKDTGrpRsp(IData map) throws Exception{
		IDataset resultDataset=new DatasetList();
		//1-获取产品订单号
		IDataset psubscribeIdSet = IDataUtil.getDatasetSpecl("PSUBSCRIBE_ID", map); // 产品订单号
		//2 校验订单号在资料表是否存在对应数据
		for(int i=0,sizeI=psubscribeIdSet.size();i<sizeI;i++){
			String proNumber = GrpCommonBean.nullToString(psubscribeIdSet.get(i));
			IDataset merchpInfo=UserEcrecepProductInfoQry.qryEcrEceppInfosByPro(proNumber,null);
			if(IDataUtil.isNotEmpty(merchpInfo)){
				IData result_data = new DataMap();// 返回结果
				result_data.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
				result_data.put("EC_SERIAL_NUMBER", map.getString("EC_SERIAL_NUMBER", ""));
				result_data.put("SUBSCRIBE_ID", map.getString("SUBSCRIBE_ID", ""));
				result_data.put("PSUBSCRIBE_ID", IDataUtil.getDatasetSpecl("PSUBSCRIBE_ID", map)); // 产品订单号
				result_data.put("RSPCODE".toUpperCase(),"50");
				result_data.put("RSP_DESC".toUpperCase(), "集团重复归档");
				result_data.put("X_RESULTCODE".toUpperCase(), "99");// 其他错误
				result_data.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1]);
				result_data.put("X_RSPCODE", "2998");
				result_data.put("X_RSPDESC", IntfField.DATABASE_ERR[1]);
				result_data.put("X_RSPTYPE", "2");
				resultDataset.add(result_data);
				return resultDataset;
			}
		}
		return resultDataset;
	}
}

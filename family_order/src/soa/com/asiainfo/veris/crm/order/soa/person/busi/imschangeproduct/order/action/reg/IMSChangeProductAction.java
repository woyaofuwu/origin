package com.asiainfo.veris.crm.order.soa.person.busi.imschangeproduct.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;


/**
 * 
 * 取消9元IMS固话套餐时，判断关联的手机号码目前是否生效家庭融合套餐，如果存在则允许取消，否则不允许取消。 
 * 该界面仅支持IMS固话18元套餐单向变更为和家固话9元档2019版套餐
 * */

public class IMSChangeProductAction implements ITradeAction
{

	private static final Logger logger = Logger.getLogger(IMSChangeProductAction.class);
	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		
		/*
		 * 手机号码欠费停机为自动化任务，不需要权限判断
		 * */
		logger.error("IMSChangeProductAction start");

		if("6806".equals(btd.getTradeTypeCode()))
		{
			boolean tag = false;
			boolean tag9 = false;
			String orderId= btd.getRD().getOrderId();
			List<ProductTradeData> productTradeData = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
			if (ArrayUtil.isEmpty(productTradeData))
			{
				return;
			}
			for (int i = 0 ; i < productTradeData.size(); i++)
			{
				String productId = productTradeData.get(i).getProductId();
				String modifyTag = productTradeData.get(i).getModifyTag();
				if (BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "84004439".equals(productId))
				{
					tag=true;
				}
				if (BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "84018381".equals(productId))
				{
					tag9=true;
				}
			}
			
			if (tag && !tag9){
        		CSAppException.apperr(CrmCommException.CRM_COMM_888, "仅支持IMS固话18元套餐单向变更为和家固话9元档2019版套餐。");
			}
			
			System.out.println("============IMSChangeProductAction============tag:"+tag);
//			if (tag){
//				String serialNumber ="";
//				IData mobileInfo = getMobileInfoByFixedNumber(btd.getMainTradeData().getSerialNumber());
//				System.out.println("============IMSChangeProductAction============mobileInfo:"+mobileInfo);
//
//				if (IDataUtil.isNotEmpty(mobileInfo)) {
//					serialNumber = mobileInfo.getString("SERIAL_NUMBER_B");					
//				}
//				IDataset ids = getBindDiscntBySN(serialNumber);
//				System.out.println("==========IMSChangeProductAction==============ids:"+ids);
//
//				if (IDataUtil.isEmpty(ids)) {
//            		CSAppException.apperr(CrmCommException.CRM_COMM_888, "非家庭融合套餐用户，不允许取消9元IMS固话套餐。");
//
//				}
//			}
		}

		logger.error("IMSChangeProductAction end");

	}
	
    public IData getMobileInfoByFixedNumber(String serialNumber) throws Exception{
    	
        String userIdB="";
        if(!"".equals(serialNumber) && serialNumber !=null){
       	 IData userInfoData = UcaInfoQry.qryUserInfoBySn(serialNumber); 
       	 userIdB =  userInfoData.getString("USER_ID","").trim();
        }
        //获取主号信息
        IDataset iDataset=RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userIdB, "MS", "2");
        if(IDataUtil.isNotEmpty(iDataset)){
       	 //获取虚拟号
       	 String userIdA=iDataset.getData(0).getString("USER_ID_A", "");
       	 //通过虚拟号获取关联的手机号码信息
       	 IDataset userBInfo=RelaUUInfoQry.getRelationsByUserIdA("MS", userIdA, "1");
       	 
       	 if(IDataUtil.isNotEmpty(userBInfo)){
       		 return userBInfo.getData(0);
       	 }
        }
   	 //不存在IMS家庭固话
   	 return null;
   }
    
	public static IDataset getBindDiscntBySN(String serial_number) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select t.serial_number, s.product_id, to_char(s.start_date,'yyyymmddhh24miss') start_date,c.para_code1 bind_discnt_code ");
		parser.addSQL("  from tf_f_user t, tf_f_user_product s, td_s_commpara c ");
		parser.addSQL("  where t.serial_number = :SERIAL_NUMBER and t.user_id = s.user_id ");
		parser.addSQL("  and t.remove_tag = '0' and t.partition_id = s.partition_id ");
		parser.addSQL("  and s.main_tag = '1' and s.end_date > sysdate and c.param_code = s.product_id  ");
		parser.addSQL("  and c.param_attr = '8921' and c.subsys_code = 'CSM' ");
		parser.addSQL("  and (sysdate between c.start_date and c.end_date) order by s.start_date desc  ");
		return Dao.qryByParse(parser);
	} 
}

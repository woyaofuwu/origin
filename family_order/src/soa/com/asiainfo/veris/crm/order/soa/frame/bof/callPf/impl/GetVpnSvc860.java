package com.asiainfo.veris.crm.order.soa.frame.bof.callPf.impl;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.callPf.ICallPfDeal;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

/**
 * 拆分sql取数据
 * 对应 :TF_F_USER_VPN_USER_ID_A_SVC860_TRADESVC
 *
 */
public class GetVpnSvc860  implements ICallPfDeal
{

	@Override
	public IDataset dealPfData(IData input) throws Exception
	{
		// TODO Auto-generated method stub
		String tradeId = input.getString("TRADE_ID");
		IDataset result = new DatasetList();
		
		IDataset tradeSvcs = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
		
		if(IDataUtil.isNotEmpty(tradeSvcs))
		{
			for(int i=0,size =tradeSvcs.size();i<size;i++)
			{
				IData tradeSvc = tradeSvcs.getData(i);
				String userId = tradeSvc.getString("USER_ID");
				
				IDataset userSvcs = UserSvcInfoQry.qryUserSvcByUserSvcId(userId, "860");
				
				if(IDataUtil.isNotEmpty(userSvcs))
				{
					for(int j=0,sizej =userSvcs.size();j<sizej;j++)
					{
						IData userSvc = userSvcs.getData(j);
						String userIda = userSvc.getString("USER_ID_A");
						
						result.addAll(this.getUserVpnInfo(userIda));
					}
				}
			}
		}
		if(IDataUtil.isNotEmpty(result))
		{
			result = DataHelper.distinct(result, "USER_ID,USER_ID_A,VPN_NO,GROUP_AREA,SCP_CODE,OPEN_DATE,REMOVE_TAG,PKG_START_DATE", ",");
		}
		
		return result;
	}
	
	public IDataset getUserVpnInfo(String userIda) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userIda);
		
		StringBuilder sql = new StringBuilder(1000);
		sql.append("SELECT distinct to_char(v.user_id) user_id,v.partition_id,v.vpn_name,v.vpn_no,to_char(v.user_id_a) user_id_a,v.group_area, ");
		sql.append("v.scp_code,v.vpmn_type,v.vpn_type,v.province,v.sub_state,v.func_tlags,v.feeindex,v.inter_feeindex,v.out_feeindex, ");
		sql.append("v.outgrp_feeindex,v.subgrp_feeindex,v.notdiscnt_feeindex,v.pre_ip_no,v.pre_ip_disc,v.othor_ip_disc,v.trans_no, ");
		sql.append("v.max_close_num,v.max_num_close,v.person_maxclose,v.max_outgrp_num,v.max_outgrp_max,v.max_inner_num,v.max_outnum, ");
		sql.append("v.max_users,v.max_linkman_num,v.max_telphonist_num,v.max_limit_users, ");
		sql.append("to_char(v.pkg_start_date,'yyyy-mm-dd hh24:mi:ss') pkg_start_date,v.pkg_type,v.discount,to_char(v.limit_fee) limit_fee, ");
		sql.append("v.zone_max,v.zonefree_num,to_char(v.zone_fee) zone_fee,v.mt_maxnum,v.aip_id,v.work_type_code,v.vpn_scare_code,v.vpn_time_code, ");
		sql.append("v.vpn_user_code,v.vpn_bundle_code,v.manager_no,v.call_net_type,v.call_area_type,v.over_fee_tag,v.limfee_type_code, ");
		sql.append("v.sinword_type_code,v.move_tag,v.trans_tag,v.lock_tag,v.cust_manager,v.link_man,to_char(v.month_fee_limit) month_fee_limit, ");
		sql.append("v.short_code_len,v.call_roam_type,v.bycall_roam_type,v.payitem_code,to_char(v.item_fee) item_fee, ");
		sql.append("to_char(v.usrgrp_id) usrgrp_id,to_char(v.open_date,'yyyy-mm-dd hh24:mi:ss') open_date,v.remove_tag, ");
		sql.append("to_char(v.remove_date,'yyyy-mm-dd hh24:mi:ss') remove_date,to_char(v.update_time,'yyyy-mm-dd hh24:mi:ss') update_time, ");
		sql.append("v.update_staff_id,v.update_depart_id,v.remark,v.rsrv_num1,v.rsrv_num2,v.rsrv_num3,to_char(v.rsrv_num4) rsrv_num4, ");
		sql.append("to_char(v.rsrv_num5) rsrv_num5,v.rsrv_str1,v.rsrv_str2,v.rsrv_str3,v.rsrv_str4,v.rsrv_str5, ");
		sql.append("to_char(v.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(v.rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2, ");
		sql.append("to_char(v.rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,v.rsrv_tag1,v.rsrv_tag2,v.rsrv_tag3 ");
		sql.append("  FROM TF_F_USER_VPN V ");
		sql.append(" WHERE V.USER_ID = :USER_ID ");
		sql.append("   AND V.PARTITION_ID = MOD(:USER_ID, 10000) ");
		sql.append("   AND V.REMOVE_TAG <> '1' ");
		
		return Dao.qryBySql(sql, param);
	}

}

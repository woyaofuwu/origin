
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

/***
 * 
 */
public class PlatFamilyManage
{

	/***
	 * 查询失效的亲情圈成员列表
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserFamilyCircle(IData param) throws Exception
	{
		if (null == param || param.size() <= 0){return null;}
		IDataset result = Dao.qryByCode("TF_F_USER_FAMILY_CIRCLE", "SEL_ALL_MEMBER_BY_SN", param);
		return result;
	}
	
	/***
	 * 查询失效的平安群组
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public static IDataset getSafeGroupByInvalid(IData params) throws Exception
	{
		if (null == params || params.size() <= 0){return null;}
		IDataset result = Dao.qryByCode("TF_F_USER_FAMILY_CIRCLE", "SEL_ALL_MEMBER_BY_SN", params);
		return result;
	}

	/***
	 * 批处理亲情圈信息
	 * @param dao
	 * @param add 新增成员
	 * @param del 删除成员
	 * @param upd 更新成员
	 * @throws Exception
	 */
	public static void batchOperFamilyMember(IDataset add,
			IDataset del, IDataset upd,IData temp) throws Exception {
		//亲情圈成员删除
		if (del.size() > 0){
			//DEL_FAMILY_CIRCLE_BY_MSISDN,UPD_STATUS_BY_MSISDN
			boolean eflag = false;
			for(int i = 0 ; i < del.size() ; i++){
				temp = (IData) del.get(i);
				if ("1".equals(temp.getString("TARGET_ROLE"))){
					eflag = true;
					temp.put("STATUS", "0");
					temp.put("GROUP_TYPE", "01");
					break;
				}
				temp.put("STATUS", "0");
				temp.put("GROUP_TYPE", "01");
			}
			if (!eflag){
				Dao.executeBatchByCodeCode("TF_F_USER_FAMILY_CIRCLE", "UPD_STATUS_BY_MSISDN", del);
			}else{
				Dao.executeUpdateByCodeCode("TF_F_USER_FAMILY_CIRCLE", "UPD_STATUS_BY_MAINMSISDN",temp);
			}
		}
		
		//亲情圈成员添加
		if (add.size() > 0){
			temp.clear();
			for(int i = 0 ; i < add.size() ; i++){
				IData temp1 = (IData) add.get(i);
				temp1.put("STATUS", "1");
				temp1.put("GROUP_TYPE", "01");
			}
			Dao.executeBatchByCodeCode("TF_F_USER_FAMILY_CIRCLE", "INS_ALL", add);
		}
		
		//亲情圈成员更新
		if (upd.size() > 0){
			for(int i = 0 ; i < upd.size() ; i++){
				IData temp1 = (IData) upd.get(i);
				temp1.put("GROUP_TYPE", "01");
			}
			Dao.executeBatchByCodeCode("TF_F_USER_FAMILY_CIRCLE", "UPD_TARGET_NAME_BY_MSISDN", upd);
		}
	}
	
	/***
	 * 更新已存在的成员列表
	 * @param pd
	 * @param params
	 * @throws Exception
	 */
	public static void updateExistsMembers(IDataset params)throws Exception{
		if (null == params || params.size() <= 0){return;}
		
		for(int i = 0 ; i < params.size() ; i++){
			IData temp = (IData) params.get(i);
			temp.put("STATUS", "1");
			temp.put("GROUP_TYPE", "01");
		}
		Dao.executeBatchByCodeCode("TF_F_USER_FAMILY_CIRCLE", "UPD_INFO_BY_MSISDN", params);
	}
	
	/***
	 * 获取群组号码
	 * @param pd
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static String getGroupCodeByName(IData param)throws Exception{
		if (null == param || param.size() <= 0){return "";}
		param.put("SQL_REF", "SEL_GROUP_CODE_BY_NAME");
		param.put("GROUP_TYPE", "02");
		IDataset result = getFamilyCircle(param);
		if(null != result && result.size() > 0){
			return result.getData(0).getString("GROUP_CODE");
		}
		return "SG"+SeqMgr.getSafeGroupCode();
	}
	
	/***
	 * 查询成员信息
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public static IDataset getGroupByMainSN(IData params) throws Exception
	{
		if (null == params || params.size() <= 0){return null;}
		IDataset result = Dao.qryByCode("TF_F_USER_FAMILY_CIRCLE", "SEL_FAMILY_CIRCLE_BY_MSISDN", params);
		return result;
	}
	
	/***
	 * 更新成员列表
	 * @param pd
	 * @param params
	 * @throws Exception
	 */
	public static void updateMembers(IDataset params)throws Exception{
		if (null == params || params.size() <= 0){return;}
		
		for(int i = 0 ; i < params.size() ; i++){
			IData temp = (IData) params.get(i);
			temp.put("STATUS", "0");
		}
		Dao.executeBatchByCodeCode("TF_F_USER_FAMILY_CIRCLE", "UPD_STATUS_BY_MSISDN", params);
	}
	
	/***
	 * 更新平安群组成员列表
	 * @param pd
	 * @param params
	 * @throws Exception
	 */
	public static void updateSafeGroupMembers(IDataset params)throws Exception{
		if (null == params || params.size() <= 0){return;}
		Dao.executeBatchByCodeCode("TF_F_USER_FAMILY_CIRCLE", "UPD_SAFE_GROUP_BY_SN", params);
	}
	/***
	 * 删除目前所有平安群组成员
	 * @param pd
	 * @param params
	 * @throws Exception
	 */
	public static void delSafeGroupMembers(IData param)throws Exception{
		if (null == param || param.size() <= 0){return;}
		Dao.executeUpdateByCodeCode("TF_F_USER_FAMILY_CIRCLE", "DEL_FULL", param);
	}

	/***
	 * 插入成员列表
	 * @param pd
	 * @param params
	 * @throws Exception
	 */
	public static void instMembersBySG(IDataset params)throws Exception{
		if (null == params || params.size() <= 0){return;}
		
		Dao.executeBatchByCodeCode("TF_F_USER_FAMILY_CIRCLE", "INS_ALL", params);
	}
	
	/**
	 * 获取指定用户的亲情圈信息
	 * @param pd
	 * @param inparams
	 * @return
	 */
	public static IDataset getFamilyCircle(IData inparams) throws Exception{
		if (null == inparams || inparams.size() <= 0){return null;}
		String sqlref = inparams.getString("SQL_REF");
		
		return Dao.qryByCode("TF_F_USER_FAMILY_CIRCLE", sqlref, inparams);
	}
}

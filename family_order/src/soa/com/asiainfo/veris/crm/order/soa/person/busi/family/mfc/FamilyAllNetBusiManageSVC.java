package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class FamilyAllNetBusiManageSVC extends CSBizService{

	public IDataset loadInfo(IData input) throws Exception{
		FamilyAllNetBusiManageBean bean = new FamilyAllNetBusiManageBean();
		return bean.loadInfo(input);
	}
	
	public IData checkAddMeb(IData input) throws Exception{
		FamilyAllNetBusiManageBean bean = new FamilyAllNetBusiManageBean();
		return bean.checkAddMeb(input);
	}
	public IData putMeb(IData input) throws Exception{
		FamilyAllNetBusiManageBean bean = new FamilyAllNetBusiManageBean();
		return bean.putMeb(input);
	}
	public IData dealLog(IData input) throws Exception{
		FamilyAllNetBusiManageBean bean = new FamilyAllNetBusiManageBean();
		return bean.dealLog(input);
	}
	public IData controlInfo(IData input) throws Exception{
		FamilyAllNetBusiManageBean bean = new FamilyAllNetBusiManageBean();
		return bean.controlInfo(input);
	}
	public IData checkAddMebForMessage(IData input) throws Exception{
		FamilyAllNetBusiManageBean bean = new FamilyAllNetBusiManageBean();
		return bean.checkAddMebForMessage(input);
	}
	public IData checkMeb(IData input) throws Exception{
		FamilyAllNetBusiManageBean bean = new FamilyAllNetBusiManageBean();
		return bean.checkMeb(input);
	}
	public IDataset bossInfo(IData input) throws Exception{
		FamilyAllNetBusiManageBean bean = new FamilyAllNetBusiManageBean();
		return bean.bossInfo(input);
	}
	/**
	 * 家庭网查询页面实时查询接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset bossRealTimeInfo(IData input) throws Exception{
		FamilyAllNetBusiManageBean bean = new FamilyAllNetBusiManageBean();
		return bean.bossRealTimeInfo(input);
	}
	public IDataset getMfcMainPhoneInfo(IData input) throws Exception{
		FamilyAllNetBusiManageBean bean = new FamilyAllNetBusiManageBean();
		return bean.getMfcMainPhoneInfo(input);
	}
	
	/**
	 * 家庭网订单状态查询
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData familyOrderStatusQuery(IData input) throws Exception{
		FamilyAllNetBusiManageBean bean = new FamilyAllNetBusiManageBean();
		return bean.familyOrderStatusQuery(input);
	}

	
	public IData qryMebList(IData input) throws Exception{
		FamilyAllNetBusiManageBean bean = new FamilyAllNetBusiManageBean();
		return bean.qryMebList(input);
	}		
	public IData checkBill(IData input) throws Exception{
		FamilyAllNetBusiManageBean bean = new FamilyAllNetBusiManageBean();
		return bean.checkBill(input);
	}
//	public IData checkAddMebForMessage(IData input) throws Exception{
//		FamilyAllNetBusiManageBean bean = new FamilyAllNetBusiManageBean();
//		return bean.checkAddMebForMessage(input);
//	}
	public IDataset queryFileByRSP(IData input) throws Exception{
		FamilyAllNetBusiManageBean bean = new FamilyAllNetBusiManageBean();
		return bean.queryFileByRSP(input);
	}
	public IDataset loadMebInfo(IData input) throws Exception{
		FamilyAllNetBusiManageBean bean = new FamilyAllNetBusiManageBean();
		return bean.loadMebInfo(input);
	}

	
	/**
	 * 查询个人业务成员数据实时返回接口
	 * @param input
	 * @return
	 * @author liuzhen6
	 * @throws Exception
	 */
	public IData getMfcInfoForBBOSS(IData input) throws Exception{
		FamilyAllNetBusiManageBean bean = new FamilyAllNetBusiManageBean();
		return bean.getMfcInfoForBBOSS(input);
	}
}

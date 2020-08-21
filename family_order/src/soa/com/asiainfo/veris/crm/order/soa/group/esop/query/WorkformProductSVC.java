package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;


public class WorkformProductSVC extends CSBizService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public IData qryEopProductByIbsysId(IData param) throws Exception{
		String ibsysid = param.getString("IBSYSID");
		String recordNum = param.getString("RECORD_NUM");
		return WorkformProductBean.qryProductByPk(ibsysid, recordNum);
	}
	
	public IDataset qryProductByIbsysid(IData param) throws Exception{
		String ibsysid = param.getString("IBSYSID");
		String isFinish = param.getString("IS_FINISH", "");
		if("true".equals(isFinish))
		{
			return WorkformProductHBean.qryProductByIbsysid(ibsysid);
		}
		else 
		{
			return WorkformProductBean.qryProductByIbsysid(ibsysid);
		}
	}

	
	/**
	 * 查询TF_B_EOP_PRODUCT表中，未生成台账的最大成员计费号
	 */
	public IData queryMaxSerialMumberByGrpSn(IData param) throws Exception{
		String grp_Sn = param.getString("GRP_SN");
		String size = param.getString("SIZE");
        int num = Integer.parseInt(size);
		IDataset dataset =  WorkformProductBean.queryMaxSerialMumberByGrpSn(grp_Sn,num);
		return dataset.first();
	}
	
    //删除产品表信息
    public void delProductByIbsysid(IData param) throws Exception {
        String ibsysid = param.getString("IBSYSID");
        WorkformProductBean.delProductByIbsysid(ibsysid);
        WorkformProductBean.delProductSubByIbsysid(ibsysid);
    }
    
	public IDataset qryProductByuserId(IData param) throws Exception{
		String userId = param.getString("USER_ID");
		IDataset dataset =  WorkformProductHBean.qryProductByuserId(userId);
		return dataset;
	}
}
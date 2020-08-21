package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpuser;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
/**
 * 
 * @author panyu5
 * @title 海南快速携回（新）
 * @time 2018/10/29
 */
public class FastNpUserNewBean extends CSBizBean {

	private static final long serialVersionUID = 1L;

	public IData checkSnNPInfo(IData input) throws Exception
    {
		IData result = new DataMap();
		result.put("RESULT_CODE", "0");
		IDataset userNpInfos = UserNpInfoQry.qryUserNpInfosBySn(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userNpInfos)||!"1".equals(userNpInfos.getData(0).getString("NP_TAG")))//不存在携转记录
        {
        	result.put("RESULT_CODE", "1");
			result.put("RESULT_INFO", "非携入用户不能办理快速携回");
			return result;
        }
        IDataset otherList = UserOtherInfoQry.checkPassChange(input.getString("USER_ID"),"QUICK_NP_SIGN");
        if(IDataUtil.isNotEmpty(otherList)){//快速携回有效期内不能重复发送
        	result.put("RESULT_CODE", "1");
			result.put("RESULT_INFO", "用户已发起过快速携回");
        	return result;
        }
        return result;
    }
	
	public IData insUserOther(IData input) throws Exception {
		IData result = new DataMap();
		result.put("RESULT_CODE", "1");
		IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", input.getString("SERIAL_NUMBER"));
		String startDate = SysDateMgr.getSysTime();
		String endData = SysDateMgr.addDays(2);
		IDataset commparas1 = CommparaInfoQry.getCommNetInfo("CSM", "160", "22025900");//查询快速携回配置失效位移时间（单位：天）
        if (IDataUtil.isNotEmpty(commparas1))
        {
        	endData = SysDateMgr.addDays(Integer.valueOf(commparas1.getData(0).getString("PARA_CODE1")));
        }
		
		String userId = userInfo.getData(0).getString("USER_ID");
		String rsrvValueCode = "QUICK_NP_SIGN";
        String staffId = CSBizBean.getVisit().getStaffId();
        String departId = CSBizBean.getVisit().getDepartId();
        
        IData otherinfo = new DataMap();
        otherinfo.put("USER_ID", userId);
        otherinfo.put("RSRV_VALUE_CODE", rsrvValueCode);
        otherinfo.put("RSRV_VALUE", "快速携回标记");
        otherinfo.put("RSRV_STR1", "1");
        otherinfo.put("UPDAYE_STAFF_ID", staffId);
        otherinfo.put("UPDATE_DEPART_ID", departId);
        otherinfo.put("START_DATE", startDate);
        otherinfo.put("END_DATE", endData);
        otherinfo.put("REMARK", "授权码快速携回标记");
        otherinfo.put("INST_ID",SeqMgr.getInstId(getVisit().getStaffEparchyCode()));
		UserOtherInfoQry.insertOnemsg(otherinfo);
		return result;
	}
}

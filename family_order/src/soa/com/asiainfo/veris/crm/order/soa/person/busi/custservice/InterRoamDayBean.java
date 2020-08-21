package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;

/**
 * @author:YF
 * @Dat:   2013年12月23日   下午3:20:43 
 * @version: v1.0
 * @Description : TODO
 */
public class InterRoamDayBean extends CSBizBean {
	
	/**
	 * 开通/变更国漫日套餐
	 * @Title : openSer
	 * @Description:TODO
	 * @Param : @param input
	 * @Param : @return
	 * @Param : @throws Exception
	 * @return: IDataset
	 */
	public IDataset openSer(IData input) throws Exception{
		
		this.checkState(input);
		
		IData data = new DataMap();
		
		IDataset dataset = new DatasetList();
		
		IDataset datas1 = new DatasetList();
		
		data = changeData(input);
		
		dataset = CSAppCall.call("SS.InterRoamDayRegSVC.tradeReg", data);
		
		return dataset;
	}
	
	
	/**
	 * 关闭国漫日套餐
	 * @Title : clasoServ
	 * @Description:TODO
	 * @Param : @param input
	 * @Param : @return
	 * @Param : @throws Exception
	 * @return: IDataset
	 */
	public IDataset clasoServ(IData input)  throws Exception{
		
		IDataset dataset = new DatasetList();
		
		this.checkState(input);
		
		IData data = new DataMap();
		
		data = changeData(input);
		
		data.put("IDTYPE", "01");
		data.put("IDITEMRANGE", input.getString("SERIAL_NUMBER"));
		data.put("SERVTYPE", "1");
		data.put("OPRCODE", "02");
		
		dataset = CSAppCall.call("SS.ChangeProductIBossSVC.globeRomingBusiness", data);
		
		return dataset;
	}
	
	
	/**
	 * @Title : changeData
	 * @Description:TODO
	 * @Param : @param input
	 * @Param : @return
	 * @return: IData
	 * @throws Exception 
	 */
	public IData changeData(IData input) throws Exception{
		
		IData result = new DataMap();
		
		IDataset dataset = new DatasetList();
		IDataset selectedElements = new DatasetList();
		
		String packageName = input.getString("PACKAGE_NAME");
		
		String packageCode = input.getString("PACKAGE_CODE");
		
		String identCode = input.getString("IDENT_CODE");
		
		String cmdCode = input.getString("CMD_CODE");   //1：开通；2：修改
		
		String packOldname = input.getString("PACK_OLDNAME");
		
		String packOldcode = input.getString("PACK_OLDCODE");
		
		String relogtag = input.getString("relogtag","");
		
		String effectType = input.getString("EFFECT_MODE"); //1：立即；2：次日；3：次月
		
		String serial_number = input.getString("SERIAL_NUMBER");
		
		result.put("MODIFY_TAG", "0");
		
		if("2".equals(cmdCode) && "1".equals(relogtag)){  //修改时，第一次走的节点
			
			packageCode = packOldcode;
		
			result.put("MODIFY_TAG", "1");
		}
		
		dataset = CommparaInfoQry.getCommparaByCode2("CSM", "2789", "99990000",packageCode,"ZZZZ");  //根据套餐代码查询本省套餐
		
		if(IDataUtil.isEmpty(dataset)){
			
			CSAppException.apperr(ParamException.CRM_PARAM_292);
			
		}
		
		String discnt_code = dataset.getData(0).getString("PARA_CODE1");   //获取到优惠编码
		result.put("ELEMENT_ID", discnt_code);
		result.put("SERIAL_NUMBER", serial_number);
		result.put("ELEMENT_TYPE_CODE", "D");
		result.put("PACKAGE_ID", PersonConst.INTER_ROAM_DAY_PACKAGE_ID);
		
		if("1".equals(effectType)){//立即
			result.put("RSRV_STR1", effectType);
		}else if("2".equals(effectType)){//次日
			result.put("RSRV_STR1", "2");
			result.put("ABSOLUTE_START_DATE", SysDateMgr.getTomorrowDate());
		}else if("3".equals(effectType)){//次月
			result.put("RSRV_STR1", "0");
		}
		selectedElements.add(result);
		IData dataInfo = new DataMap();
        dataInfo.put("SERIAL_NUMBER", serial_number);
        dataInfo.put("ELEMENT_ID", discnt_code);
        dataInfo.put("ELEMENT_TYPE_CODE", "D");
        dataInfo.put("MODIFY_TAG", "1".equals(cmdCode)?"0":"1");
        dataInfo.put("SELECTED_ELEMENTS", selectedElements);
        
		return dataInfo ;
	}
	
	/**
	 * 客户有效性校验
	 * @Title : checkState
	 * @Description:TODO
	 * @Param : @param input
	 * @return: void
	 * @throws Exception 
	 */
	private void checkState(IData data) throws Exception{
		
		String sn = data.getString("SERIAL_NUMBER");
		
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
    	
        if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        
        String userid = userInfo.getString("USER_ID");
        String identCode = data.getString("IDENT_CODE");
		String contactId = data.getString("CONTACT_ID");
		
		IDataset dataset = UserIdentInfoQry.queryIdentCode(userid, identCode, contactId);
		
		if(IDataUtil.isEmpty(dataset)){
			CSAppException.apperr(CrmUserException.CRM_USER_2998);
		}
		
	}

}

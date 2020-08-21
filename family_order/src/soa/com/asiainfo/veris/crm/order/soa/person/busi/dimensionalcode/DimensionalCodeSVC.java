package com.asiainfo.veris.crm.order.soa.person.busi.dimensionalcode;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
/**
 *  二维码
 */
public class DimensionalCodeSVC extends CSBizService
{
	private static final long serialVersionUID = 5769891322110069739L;
	/**
	 * 二维码变更状态查询
	 */
	public IDataset qryDimensionalCodeStateList(IData input) throws Exception {
	    DimensionalCodeBean bean = (DimensionalCodeBean) BeanManager.createBean(DimensionalCodeBean.class);
		IDataset resultList = new DatasetList();
		resultList.add(bean.qryDimensionalCodeStateList(input));
		return resultList;
	}
	/**
	 * 变更状态关系，生成台帐，需调用IBOSS同步
	 * @return
	 * @throws Exception
	 */
	public IDataset dimensionalCodeOperate(IData input) throws Exception {
		IDataset resultList = new DatasetList();
		resultList= CSAppCall.call("SS.DimensionalCodeRegSVC.tradeReg", input);
		return resultList;
	}
	/**
	 * 校验是否主体服务正常
	 */
	public IDataset isNormalMainService(IData input) throws Exception {
		String serial_number=input.getString("SERIAL_NUMBER");
		DimensionalCodeBean bean = (DimensionalCodeBean) BeanManager.createBean(DimensionalCodeBean.class);
		bean.isNormalMainService(serial_number);
		return null;
	}
	/**
	 * 校验是否合法操作
	 */
	public IDataset isLegalOprCode(IData input) throws Exception {
		String cur_status=input.getString("STATUS");
		String cur_opr_code=input.getString("OPR_CODE");
		DimensionalCodeBean bean = (DimensionalCodeBean) BeanManager.createBean(DimensionalCodeBean.class);
		IDataset resultList = new DatasetList();
		resultList.add(bean.isLegalOprCode(cur_status,cur_opr_code));
		return resultList;
	}
	/**
	 * 重载方法 转化路由参数
	 */
	public void setTrans(IData input) throws Exception {
		String serial_number=input.getString("SERIAL_NUMBER", input.getString("ID_VALUE"));
		input.put("SERIAL_NUMBER", serial_number);
	}
}
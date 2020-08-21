package com.asiainfo.veris.crm.order.soa.group.speclist;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SpecListSVC extends CSBizService {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 查询用户资料
	 * @param input
	 * @return
	 * @throws Exception
	 * @author wuhao
	 * @date 2019-7-16
	 */
	public IDataset queryUserInfo(IData input) throws Exception {
		SpecListBean memberFileBean = (SpecListBean) BeanManager.createBean(SpecListBean.class);
		return memberFileBean.queryUserInfo(input, getPagination());
	}
	
	/**
	 * 查询特殊名单信息
	 * @param input
	 * @return
	 * @throws Exception
	 * @author wuhao
	 * @date 2019-7-16
	 */
	public IDataset querySpeclist(IData input) throws Exception {
		SpecListBean memberFileBean = (SpecListBean) BeanManager.createBean(SpecListBean.class);
		return memberFileBean.querySpeclist(input, getPagination());
	}
	
	/**
	 * 查询特殊名单信息(客服用)
	 * @param input
	 * @return
	 * @throws Exception
	 * @author wuhao
	 * @date 2019-7-16
	 */
	public IData querySpeclistForKF(IData input) throws Exception {
		SpecListBean memberFileBean = (SpecListBean) BeanManager.createBean(SpecListBean.class);
		input.put("USER_MOBILE", input.getString("userMobile"));
		IDataset dataset = memberFileBean.querySpeclistForKF(input, getPagination());
		IData rtnResult=new DataMap();
		IData object=new DataMap();
		IDataset result=new DatasetList(); 
		if(IDataUtil.isNotEmpty(dataset)){
			rtnResult.put("rtnCode", "0");
			rtnResult.put("rtnMsg", "成功");
			object.put("respCode", "0");
			object.put("respDesc", "success");
			for(int i=0;i<dataset.size();i++){
				IData param = new DataMap();
				IData data = dataset.getData(i);
				param.put("specialType", data.getString("SPECIAL_TYPE"));
				param.put("typeName", StaticUtil.getStaticValue("SPECIAL_TYPE", data.getString("TYPE_NAME")));
				param.put("promptMessage", data.getString("PROMPT_MESSAGE"));
				param.put("isBold", data.getString("ISBOLD"));
				param.put("color", StaticUtil.getStaticValue("COLOR", data.getString("COLOR")));
				result.add(param);
			}
			object.put("result",result);
			object.put("resultRows",dataset.size()+"");
			rtnResult.put("object", object);
		}else{
			rtnResult.put("rtnCode", "-9999");
			rtnResult.put("rtnMsg", "失败");
			object.put("respCode", "-9999");
			object.put("respDesc", "fail");
			object.put("resultRows","0");
			object.put("result",result);
			rtnResult.put("object", object);
		}
		return rtnResult;
	}
	
	/**
	 * 新增特殊名单
	 * @param input
	 * @return
	 * @throws Exception
	 * @author wuhao
	 * @date 2019-7-16
	 */
	public IDataset insertSpeclist(IData input) throws Exception {
		SpecListBean memberFileBean = (SpecListBean) BeanManager.createBean(SpecListBean.class);
		return memberFileBean.insertSpeclist(input);
	}
	
	/**
	 * 删除特殊名单
	 * @param input
	 * @return
	 * @throws Exception
	 * @author wuhao
	 * @date 2019-7-16
	 */
	public IDataset deleteSpeclist(IData input) throws Exception {
		SpecListBean memberFileBean = (SpecListBean) BeanManager.createBean(SpecListBean.class);
		return memberFileBean.deleteSpeclist(input);
	}
	
	/**
	 * 修改特殊名单
	 * @param input
	 * @return
	 * @throws Exception
	 * @author wuhao
	 * @date 2019-7-16
	 */
	public IDataset updateSpeclist(IData input) throws Exception {
		SpecListBean memberFileBean = (SpecListBean) BeanManager.createBean(SpecListBean.class);
		return memberFileBean.updateSpeclist(input);
	}
}

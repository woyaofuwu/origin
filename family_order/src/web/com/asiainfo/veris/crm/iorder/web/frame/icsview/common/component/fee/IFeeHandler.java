package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.fee;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.ailk.biz.view.BizHttpHandler;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.privm.CheckPriv;
import com.ailk.service.client.ServiceFactory;
import com.ailk.web.view.handler.IHttpHandler;

public class IFeeHandler  extends BizHttpHandler{
	
	private final String ROUTE_EPARCHY_CODE = "ROUTE_EPARCHY_CODE";
	/**
	 * 设置返回类型
	 */
	@Override 
	public int getType(){
		return IHttpHandler.TYPE_AJAX;
	}
	
	private byte[] serializ(Object obj){
		byte[] data = null;

		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;

		try {
			
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			data = baos.toByteArray();
				
		} catch (IOException e) {
			log.error("序列化时发生IOException错误！", e);
		} finally {
			  if (null != oos) { 
				  try { 
					  oos.close(); 
				  } catch (IOException e) {
					  log.error("序列化时关闭ObjectOutputStream时发生错误！", e);
				  } 
			  }
		}
		
		return data;
	}
	

	/**
	 * 加载费用配置信息
	 * @param cycle
	 * @throws Exception
	 */
	public void loadFeeConfig() throws Exception{
	
		IData param=new DataMap();
		IDataOutput output=ServiceFactory.call("CS.FeeMgrSVC.getFeeConfigInfos", createDataInput(param));
		IData config=output.getData().first();
		
		setAjax(config);
	}

	/**
	 * 查询银行信息
	 * @param cycle
	 * @throws Exception
	 */
	public void loadFeeBankList() throws Exception{
		IDataOutput output=ServiceFactory.call("CS.BankInfoQrySVC.queryBankList", createDataInput(getData()));

		setAjax(output.getData());
	}
	
	/**
	 * 返回POS机流水号
	 * @param cycle
	 * @throws Exception
	 */
	public void getPosTradeId() throws Exception{
		IData posData = new DataMap();
		IData param = getData();
		param.put(ROUTE_EPARCHY_CODE, param.getString("EPARCHY_CODE", this.getVisit().getStaffEparchyCode()));
		IDataOutput output=ServiceFactory.call("CS.PosMgrSVC.getPosTradeId", createDataInput(param));
		if(output.getData() != null && output.getData().size()>0){
			posData = output.getData().first();
		}
		setAjax(posData);
	}
	
	/**
	 * 获取费用权限
	 * @throws Exception
	 */
	public void getModFeePriv() throws Exception{
		IData param = getData();
		boolean priv = false;
		String privCode = param.getString("PRIV_CODE","");
		if(!"".equals(privCode)){
			priv = CheckPriv.checkPermission(getVisit().getStaffId(), privCode);
		}
		setAjax(new DataMap("{\"FEE_SPECPRIV\":"+priv+"}"));
	}
}
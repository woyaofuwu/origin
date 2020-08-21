package com.asiainfo.veris.crm.order.soa.person.busi.highvaluegarden;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * PCC业务
 */
public class GardenDeviceInfoSVC extends CSBizService {

	private static final long serialVersionUID = 3109066901465171662L;
	
	/**
     * 数据分页查询
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset qryGardenDeviceInfo(IData inParam) throws Exception
    {
    	GardenDeviceInfoBean bean = (GardenDeviceInfoBean) BeanManager.createBean(GardenDeviceInfoBean.class);
        return bean.qryGardenDeviceInfo(inParam, getPagination());
    }
    
	/**
	 * 数据导入
	 */
	public IData gardenInfoInsert(IData input) throws Exception {
		GardenDeviceInfoBean bean = (GardenDeviceInfoBean) BeanManager.createBean(GardenDeviceInfoBean.class);
		IData result = bean.gardenInfoInsert(input);
		return result;
	}
	
	/**
	 * 数据删除
	 */
	public IData gardenInfoDelete(IData input) throws Exception {
		GardenDeviceInfoBean bean = (GardenDeviceInfoBean) BeanManager.createBean(GardenDeviceInfoBean.class);
		IData result = bean.gardenInfoDelete(input, getPagination());
		return result;
	}
	
	/**
	 * 批量数据删除
	 */
	public IData deleteGardenInfos(IData input) throws Exception {
		GardenDeviceInfoBean bean = (GardenDeviceInfoBean) BeanManager.createBean(GardenDeviceInfoBean.class);
		IData result = bean.deleteGardenInfos(input);
		return result;
	}
}
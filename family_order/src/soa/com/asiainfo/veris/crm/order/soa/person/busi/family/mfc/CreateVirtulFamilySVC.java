
package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class CreateVirtulFamilySVC extends GroupOrderService
{
    private static final long serialVersionUID = -2488728747213183001L;

    public IDataset createGroupUser(IData inparam) throws Exception
    {
        IDataset obj = new DatasetList();

        obj = GrpInvoker.ivkProduct(inparam, BizCtrlType.CreateUser, "CreateClass");

        return obj;
    }
    
    /**
     * 
     * @Title: crtTrade  
     * @Description: 提供给中兴专线受理接口
     * @param @param inparam
     * @param @return
     * @param @throws Exception    设定文件  
     * @return IDataset    返回类型  
     * @throws
     */
    public IDataset crtTrade(IData inparam) throws Exception
    {
    	IDataset obj = new DatasetList();
		IDataUtil.chkParamNoStr(inparam, "TRADE_TYPE_CODE");
		String tradetypecode = inparam.getString("TRADE_TYPE_CODE");
		IDataset productConfig= CommparaInfoQry.getCommparaCode1("CSM", "2018", "KSJTW_VIRTUAL_PRO", "ZZZZ");
    	if(DataUtils.isEmpty(productConfig) &&StringUtils.isBlank(productConfig.getData(0).getString("PARA_CODE1","")))
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_1160,"2018","KSJTW_VIRTUAL_PRO");
    	}
    	inparam.put("PRODUCT_ID", productConfig.getData(0).getString("PARA_CODE1"));
		if ("2580".equals(tradetypecode)) {
			obj = GrpInvoker.ivkProduct(inparam, BizCtrlType.CreateUser,
					"CreateClass");
		} else if ("2581".equals(tradetypecode)) {
			obj = GrpInvoker.ivkProduct(inparam, BizCtrlType.DestoryUser,
					"CreateClass");
		}
		return obj;
    }
}

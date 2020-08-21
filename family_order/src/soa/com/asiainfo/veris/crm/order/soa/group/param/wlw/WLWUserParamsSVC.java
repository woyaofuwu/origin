
package com.asiainfo.veris.crm.order.soa.group.param.wlw;

import com.ailk.bizcommon.route.Route;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.group.cargroup.CarGroupRateInfoBean;

public class WLWUserParamsSVC extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IDataset getServiceParam(IData idata) throws Exception
    {
        UserParams userParams = new UserParams();
        IDataset output = userParams.getServiceParam(idata);
        return output;
    }
	
    public IDataset getServiceParam3(IData idata) throws Exception
    {
    	UserParams3 userparam = new UserParams3();
    	return userparam.getServiceParam(idata);
    }
	 
	 public IData getApproveParam(IData idata) throws Exception
    {
		//先获取页面的显示
    	IDataset output = new DatasetList();
    	IDataset paramAttr = CommparaInfoQry.getCommparaAllCol("CSM", "9013", idata.getString("PARAM_ATTR"), "ZZZZ");
        if(IDataUtil.isEmpty(paramAttr)){
        	paramAttr = CommparaInfoQry.getCommparaAllCol("CSM", "9014", idata.getString("PARAM_ATTR"), "ZZZZ");
        }
        String attr = paramAttr.getData(0).getString("PARA_CODE1");
        String paramCode = CommparaInfoQry.getCommparaByCode1("CSM", "1554",attr ,"ZZZZ").getData(0).getString("PARAM_CODE");
        
    	//此处也需修改，增加数据从个人过来的情况
    	//如果EC_ID为空，则传过来的数据为个人的USER_ID
    	if(StringUtils.isBlank(idata.getString("EC_ID",""))){
    		String user_id = idata.getString("USER_ID");
    		//根据个人的USER_ID获取集团的user_ida
    		IDataset groupInfo = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(user_id,"W1",Route.getCrmDefaultDb());
    		if(IDataUtil.isEmpty(groupInfo)){
    			//CSAppException.apperr(CrmCommException.CRM_COMM_103,"该用户未加入任何物联网集团，请申请加入集团后在进行此操作！");
    			//此时为个人开户，未加集团的情况。只给出填写页面，不给折扣，直接返回！
    			IData result = new DataMap();
    	        result.put("PARAM_CODE", paramCode);
    	        result.put("RET", output);
    	        return result;
    		}
    		String user_id_a = groupInfo.getData(0).getString("USER_ID_A");
    		IData info = UcaInfoQry.qryGrpInfoByUserId(user_id_a);
    		if(IDataUtil.isEmpty(info)){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取用户所属集团信息错误，请查看数据！");
    		}
    		idata.put("EC_ID", info.get("GROUP_ID"));
    	}
    	String group_id = CarGroupRateInfoBean.changeGroupIdToSubsId(idata.getString("EC_ID"));
    	idata.put("EC_ID", group_id);
        output = Dao.qryByCode("TF_O_DISCNT_APPROVAL", "SEL_APPROVAL_NO", idata);
        
        IData result = new DataMap();
        result.put("PARAM_CODE", paramCode);
        result.put("RET", output);
        return result;
    }
	 
	 public IDataset getServiceParam4(IData idata) throws Exception
    {
        UserParams4 userParams = new UserParams4();
        IDataset output = userParams.getServiceParam(idata);
        return output;
    }
}

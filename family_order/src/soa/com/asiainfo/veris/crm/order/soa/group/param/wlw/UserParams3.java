package com.asiainfo.veris.crm.order.soa.group.param.wlw;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class UserParams3 {

	public UserParams3(){
		super();
	}
	public IDataset getElementParam(IData params) throws Exception{
		IData param = getElementDetailInfos(params);
		
		//获取是否为新增
		String newTag = param.getString("pam_INNER_SVC_TAG");
		IData checkmap = new DataMap();
        if ("NEW".equals(newTag))
        {
            // 当是新增服务时，参数自然没有
            checkmap.put("PARAM_VERIFY_SUCC", "false");
        }
        else
        {
            // 当是修改服务时，参数自然是已有，故下一步按钮是要放过的
            checkmap.put("PARAM_VERIFY_SUCC", "true");
        }
        IDataset paramDataset = new DatasetList();
        IData serparam = new DataMap();
        serparam.put("PLATSVC", param);

        serparam.put("ID", StringUtils.isBlank(params.getString("SERVICE_ID","")) ? params.getString("DISCNT_CODE"):param.getString("SERVICE_ID"));

        paramDataset.add(0, checkmap);
        paramDataset.add(1, serparam);

        return paramDataset;
	}
	
	/**
	 * 获取服务或者优惠的详细参数
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public IData getElementDetailInfos(IData params) throws Exception{
		
		String userId = params.getString("USER_ID");
		String productId = params.getString("PRODUCT_ID");

		String serviceId = params.getString("SERVICE_ID","");
		String discntId = params.getString("DISCNT_CODE","");

		IData platsvc = new DataMap();
		
		boolean createNew = true; //标识来判断是否是新增，默认是新增

		if(!"".equals(serviceId)) //判断传入的是服务还是优惠    上面的是服务
		{
			if(!"".equals(userId))	  //通过传入的USER_ID来判断是否是已存在订购关系
			{
				//表明传入的USER_ID不为空，通过查询用户订购表来判断是啥业务
				IDataset userSVC = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(userId, serviceId);
				if(IDataUtil.isNotEmpty(userSVC))
				{
					createNew = false;
				}
			}
			if(createNew)	//新增和修改的参数修改
			{
				IData param = new DataMap();
	            param.put("ID", serviceId);
	            param.put("ID_TYPE", "S");
	            param.put("ATTR_OBJ", "0");
	            param.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());

	            IDataset dataset = CSAppCall.call("CS.AttrItemInfoQrySVC.getAttrItemAByIDTO", param);

	            platsvc = IDataUtil.hTable2StdSTable(dataset, "ATTR_CODE", "ATTR_INIT_VALUE");

	            platsvc.put("MODIFY_TAG", "0"); // 标识新增

	            // 将服务是否是新增的标志，带出去
	            platsvc.put("INNER_SVC_TAG", "NEW");
				
				
			}
			else
			{
				IDataset userattrdataset = CSAppCall.call("CS.UserAttrInfoQrySVC.getuserAttrBySvcId", params);

	            IData userattrdata = IDataUtil.hTable2StdSTable(userattrdataset, "ATTR_CODE", "ATTR_VALUE");

	            platsvc = userattrdata;
	            platsvc.put("MODIFY_TAG", "2"); // 标识修改 即已经存在的服务信息

	            // 将服务是否是新增的标志，带出去
	            platsvc.put("INNER_SVC_TAG", "OLD");
			}
		}
		else	//传入的为优惠
		{
			if(!"".equals(userId))	  //通过传入的USER_ID来判断是否是已存在订购关系
			{
				//表明传入的USER_ID不为空，通过查询用户订购表来判断是啥业务
				IDataset userDIS = UserDiscntInfoQry.getUserDiscntByDiscntCode(userId, "", discntId, CSBizBean.getVisit().getStaffEparchyCode());
				if(IDataUtil.isNotEmpty(userDIS))
				{
					createNew = false;
				}
			}
			if(createNew)	//新增和修改的参数修改
			{
				IData param = new DataMap();
	            param.put("ID", discntId);
	            param.put("ID_TYPE", "D");
	            param.put("ATTR_OBJ", "0");
	            param.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());

	            IDataset dataset = CSAppCall.call("CS.AttrItemInfoQrySVC.getAttrItemAByIDTO", param);

	            platsvc = IDataUtil.hTable2StdSTable(dataset, "ATTR_CODE", "ATTR_INIT_VALUE");

	            platsvc.put("MODIFY_TAG", "0"); // 标识新增

	            // 将优惠是否是新增的标志，带出去
	            platsvc.put("INNER_SVC_TAG", "NEW");
			}
			else
			{
				IDataset userattrdataset = UserAttrInfoQry.getUserAttrByDiscntCode(userId, discntId);

	            IData userattrdata = IDataUtil.hTable2StdSTable(userattrdataset, "ATTR_CODE", "ATTR_VALUE");

	            platsvc = userattrdata;
	            platsvc.put("MODIFY_TAG", "2"); // 标识修改 即已经存在的优惠信息

	            // 将优惠是否是新增的标志，带出去
	            platsvc.put("INNER_SVC_TAG", "OLD");
			}
		}
		platsvc.put("PRODUCT_ID", productId);
		platsvc = IDataUtil.replaceIDataKeyAddPrefix(platsvc, "pam_");
		return platsvc;
		
	}
	
	public IDataset getServiceParam(IData params) throws Exception{
		IData param = getServicePlatsvcParam(params);
		
		String newTag = param.getString("pam_INNER_SVC_TAG");
		IData checkmap = new DataMap();
        if ("NEW".equals(newTag))
        {
        
            checkmap.put("PARAM_VERIFY_SUCC", "false");
        }
        else
        {
            
            checkmap.put("PARAM_VERIFY_SUCC", "true");
        }
        IDataset paramDataset = new DatasetList();
        IData serparam = new DataMap();
        serparam.put("PLATSVC", param);
        serparam.put("inTable", param);

        serparam.put("ID", params.getString("SERVICE_ID"));

        paramDataset.add(0, checkmap);
        paramDataset.add(1, serparam);

        return paramDataset;
	}
	
	
	public IData getServicePlatsvcParam(IData params) throws Exception{
		
		String userId = params.getString("USER_ID");
		
		String end_date = params.getString("END_DATE","");
		String serviceId = params.getString("SERVICE_ID","");
		String start_date = "";
		IData platsvc = new DataMap();
		
		boolean createNew = true;
		
		if(!"".equals(userId))	  
		{
			
			IDataset userSVC = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(userId, serviceId);
			
			if(IDataUtil.isNotEmpty(userSVC))
			{
				createNew = false;
				for(int i = 0 ; i<userSVC.size() ; i++){
					IData svc = userSVC.getData(i);
					String svc_end = svc.getString("END_DATE").substring(0, 10);
					if(end_date.equals(svc_end)){
						start_date = svc.getString("START_DATE");
						platsvc.put("START_DATE", start_date);
						break;
					}
				}
			}
		}
		if(createNew)	
		{

            platsvc.put("MODIFY_TAG", "0"); // 标识新增

            // 将服务是否是新增的标志，带出去
            platsvc.put("INNER_SVC_TAG", "NEW");
			
			
		}
		else
		{
			IDataset userattrdataset = CSAppCall.call("CS.UserAttrInfoQrySVC.getuserAttrBySvcId", params);

			for(int i = 0 ; i<userattrdataset.size() ; i++){
				String end_date_rel = userattrdataset.getData(i).getString("END_DATE").substring(0, 10);
				if(end_date.equals(end_date_rel)){
					platsvc.put(userattrdataset.getData(i).getString("ATTR_CODE"), userattrdataset.getData(i).getString("ATTR_VALUE"));
				}
			}
            platsvc.put("MODIFY_TAG", "2"); // 标识修改 即已经存在的服务信息

            // 将服务是否是新增的标志，带出去
            platsvc.put("INNER_SVC_TAG", "OLD");
		}
		
		platsvc = IDataUtil.replaceIDataKeyAddPrefix(platsvc, "pam_");
		return platsvc;
		
	}
}


package com.asiainfo.veris.crm.order.soa.group.param.wlw;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;




public class UserParams4
{

    /**
     * 构造函数
     */
    public UserParams4()
    {
    }

    public IDataset getServiceParam(IData params) throws Exception
    {
        // 不管是不是新增的服务，总会取已有的，或者取初始化的
        IData platsvcParam = this.getServicePlatsvcParam(params);

        // 将服务是否是新增的标志（取回来）
        String newTag = platsvcParam.getString("pam_INNER_SVC_TAG");
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
        serparam.put("PLATSVC", platsvcParam);

        serparam.put("ID", params.getString("SERVICE_ID", ""));

        paramDataset.add(0, checkmap);
        paramDataset.add(1, serparam);

        return paramDataset;
    }

    /**
     * 获取服务个性化参数显示信息
     * 
     * @author
     * @param params
     * @return IDataset 执行返回结果
     * @throws Exception
     */
    public IData getServicePlatsvcParam(IData params) throws Exception
    {
        // log, "进入取初始化弹出窗口服务参数platsvc的方法");
        String userId = params.getString("USER_ID", "");
        String productid = params.getString("PRODUCT_ID", "");
        String serviceId = params.getString("SERVICE_ID", "");
        String userIda = params.getString("USER_ID_A", "");

        IData platsvc = new DataMap();

        boolean createsvc = true;// 该服务是新增 还是修改标识 默认为新增
        boolean issvc = true;// 默认该ID是服务
        IDataset paramAttr = CommparaInfoQry.getCommNetInfo("CSM", "9013", serviceId);
        if(IDataUtil.isNotEmpty(paramAttr)){
        	//若9013能查到该ID。则说明此ID是资费。
        	issvc = false;
        }
        if (!userId.equals(""))// 如果是从用户修改进来的 判断用户是否已经订购了该服务信息
        {
            // 此if语句进入，是说明已有用户ID，即是修改用户信息
        	if(StringUtils.isEmpty(userIda)){
        		params.put("USER_ID_A", "-1");
        	}
            
            IDataset usersvc = CSAppCall.call("CS.UserSvcInfoQrySVC.getUserSvcBycon", params); // UserGrpQry.getUserSvcBycon(params);
            if (usersvc.size() > 0)
            {
                // 证明这个服务订购过，即应该是修改服务参数
                createsvc = false;
            }
            
            //折扣涉及到资费的，还有判断一次资费
            IDataset userdis = UserDiscntInfoQry.getAllDiscntByUser(userId,serviceId);
            if (userdis.size() > 0)
            {
                // 证明这个资费订购过，即应该是修改资费参数
                createsvc = false;
            }
            
        }
        if (createsvc)// 为新加服务的 取初始化数据流程
        {
            IData param = new DataMap();
            param.put("ID", serviceId);
            if(issvc){
            	param.put("ID_TYPE", "S");
            }
            else{
            	param.put("ID_TYPE", "D");
            }
            //param.put("ATTR_OBJ", "0");
            param.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());

            IDataset dataset = CSAppCall.call("CS.AttrItemInfoQrySVC.getAttrItemAByIDTO", param);

            platsvc = IDataUtil.hTable2StdSTable(dataset, "ATTR_CODE", "ATTR_INIT_VALUE");

            platsvc.put("MODIFY_TAG", "0"); // 标识新增

            // 将服务是否是新增的标志，带出去
            platsvc.put("INNER_SVC_TAG", "NEW");

        }
        else
        // 为已经订购了该服务的 取platsvc等数据流程
        {
            IDataset userattrdataset = CSAppCall.call("CS.UserAttrInfoQrySVC.getuserAttrBySvcId", params);
                      
            if(IDataUtil.isEmpty(userattrdataset)){
            	userattrdataset = UserAttrInfoQry.getUserAttrByDiscntCode(userId,serviceId);
            }

            IData userattrdata = IDataUtil.hTable2StdSTable(userattrdataset, "ATTR_CODE", "ATTR_VALUE");

            platsvc = userattrdata;
            platsvc.put("MODIFY_TAG", "2"); // 标识修改 即已经存在的服务信息

            // 将服务是否是新增的标志，带出去
            platsvc.put("INNER_SVC_TAG", "OLD");

        }
        platsvc.put("PRODUCT_ID", productid);

        platsvc = IDataUtil.replaceIDataKeyAddPrefix(platsvc, "pam_");

        return platsvc;
    }
}


package com.asiainfo.veris.crm.order.web.frame.csview.group.simpleBusi.crerategroupmember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.staff.staffbbossinfo.StaffGrpRightInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class SimpleCreateGroupMember extends GroupBasePage
{

    public abstract IData getCondition();

    public abstract IData getInfo();

    public void initial(IRequestCycle cycle) throws Throwable
    {
        // 支持过滤产品树上的产品节点
        IData inParam = getData();

        String productTreeLimitType = inParam.getString("PRODUCTTREE_LIMIT_TYPE", "1"); // 0为限制limit_products不展示。limit_type=1为只显示limit_products中的产品
        String productTreeLimitProducts = inParam.getString("PRODUCTTREE_LIMIT_PRODUCTS");
        IData condition = new DataMap();
        condition.put("RELA_CODE", "0");
        if (StringUtils.isNotBlank(productTreeLimitProducts))
        {
            condition.put("PRODUCTTREE_LIMIT_TYPE", productTreeLimitType);
            condition.put("PRODUCTTREE_LIMIT_PRODUCTS", productTreeLimitProducts);

            if (productTreeLimitType.equals("1"))
            {
                String[] productLimitS = productTreeLimitProducts.split(",");
                int limitLen = productLimitS.length;
                String relaCode = "";
                for (int i = 0; i < limitLen; i++)
                {
                    String productId = productLimitS[i];
                    String brandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, productId);
                    if (GroupBaseConst.BB_BRAND_CODE.toString().indexOf(brandCode) >= 0)
                    {
                        relaCode = (relaCode.equals("") || relaCode.equals(GroupBaseConst.RELA_TYPE.BB.getValue())) ? GroupBaseConst.RELA_TYPE.BB.getValue() : GroupBaseConst.RELA_TYPE.ALL.getValue();
                    }
                    else
                    {
                        relaCode = (relaCode.equals("") || relaCode.equals(GroupBaseConst.RELA_TYPE.UU.getValue())) ? GroupBaseConst.RELA_TYPE.UU.getValue() : GroupBaseConst.RELA_TYPE.ALL.getValue();
                    }
                }

                if (StringUtils.isNotBlank(relaCode))
                {
                    condition.put("RELA_CODE", relaCode);
                }
            }

        }
        
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "7354");
        iparam.put("PARAM_CODE", productId);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
        	condition.put("MEB_FILE_SHOW","true");
        }
        
        setCondition(condition);

    }

    /**
     * 检验V网办理权限
     * @param cycle
     * @throws Throwable
     */
    public void checkVpmnAllRightCode(IRequestCycle cycle) throws Throwable
    {
    	IData data = getData();
    	if(IDataUtil.isNotEmpty(data)){
    		String grpSn = data.getString("GrpSerialNumber","");
    		String staff_id = getVisit().getStaffId();
    		if(StringUtils.isNotBlank(grpSn)){
    			 //V网权限的判断
    			 if ("V0HN001010".equals(grpSn) || "V0SJ004001".equals(grpSn))
    	         {
    				 IDataset right = StaffGrpRightInfoIntfViewUtil.qryGrpRightInfosByStaffIdRightCodeUserProductCode(this, staff_id, "CREATE_HAINMOBILE_MEB_RIGHT", null);
    	             //IDataset right = StaffInfoQry.queryGrpRightByIdCode(staff_id, "CREATE_HAINMOBILE_MEB_RIGHT", null);
    	             if (IDataUtil.isEmpty(right))
    	             {
    	            	 String err = "您无权办理海南移动公司集团!";
    	            	 CSViewException.apperr(VpmnUserException.VPMN_USER_226, err);
    	             }
    	         }
    			 if (!"SUPERUSR".equals(staff_id))
    	         {
    	             //IDataset rightInfo1 = StaffInfoQry.queryGrpRightByIdCode(null, "CREATE_VPN_MEMBER", grpSn); // vpn成员开户的权限
    	             IDataset rightInfo1 = StaffGrpRightInfoIntfViewUtil.qryGrpRightInfosByStaffIdRightCodeUserProductCode(this, null, "CREATE_VPN_MEMBER", grpSn);
    	             if (IDataUtil.isNotEmpty(rightInfo1))
    	             {
    	                 IDataset rightInfo2 = DataHelper.filter(rightInfo1, "STAFF_ID=" + staff_id);
    	                 if (IDataUtil.isEmpty(rightInfo2))
    	                 {
    	                     String err = "您无权办理集团" + grpSn + "的业务！";
    	                     CSViewException.apperr(VpmnUserException.VPMN_USER_226, err);
    	                 }
    	             }
    	         }
    		}
    	}
    	
    }
    
    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

}


package com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupmember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBaseView;

public abstract class PreView extends GroupBasePage
{

    public void initial(IRequestCycle cycle) throws Exception
    {
        IData initData = getData();
        String authCheckMode = initData.getString("COND_CHECK_MODE", "");//服务密码是否免密标志
        String productIdEc = initData.getString("GRP_PRODUCT_ID", "");
        String productId = initData.getString("PRODUCT_ID", "");// 集团产品ID
        String custId = initData.getString("CUST_ID", "");// 集团客户ID
        String tradeTypeCode = initData.getString("TRADE_TYPE_CODE", "");// 业务类型
        String mebUserId = initData.getString("MEB_USER_ID", "");// 成员用户ID
        String mebEparchyCode = initData.getString("MEB_EPARCHY_CODE", "");// 成员地州编码
        String ifAddMeb = initData.getString("IF_ADD_MEB", "false");

        // 查询集团客户信息
        IData grpCustInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);
        setGrpCustInfo(grpCustInfo);

        // 查询成员用户信息
        if (!ifAddMeb.equals("true"))
        {
            IData mebUserInfo = UCAInfoIntfViewUtil.qryMebUserInfoByUserIdAndRoute(this, mebUserId, mebEparchyCode, true, false);
            setMebUserInfo(mebUserInfo);
        }
        
        if(StringUtils.isBlank(authCheckMode)){//如果是免密则上传凭证信息，否则不上传 by zhuwj
        	  if(!productIdEc.equals("") && productIdEc!=null){
             	 String productBrandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, productIdEc);// 根据产品编号获取产品的品牌信息
                  IData info = new DataMap();
                  if ("BOSG".equals(productBrandCode)){
                  	info.put("BOSG","true");
                  	setInfo(info);
                  }
                  
             }
        }
      
       
        // 获取缓存元素信息
        String cacheKey = CacheKey.getSelectElemtInfoKey(mebUserId, productId, tradeTypeCode);
        IData elementData = (IData) SharedCache.get(cacheKey);

        IDataset selectedElementList = new DatasetList();
        if (IDataUtil.isNotEmpty(elementData))
        {
            String selectedElementStr = elementData.getString("SELECTED_ELEMENTS");
            if (!StringUtils.isEmpty(selectedElementStr))
                selectedElementList = new DatasetList(selectedElementStr);
        }

        IData memberElementData = new DataMap();// 成员产品元素
        GroupBaseView.processProductElements(this, selectedElementList, memberElementData);
        setMemberProduct(memberElementData);

        // 处理费用(初始化元素费用信息)
        IDataset feeList = super.initElementDefaultFee(productId, tradeTypeCode, selectedElementList);
        setGrpFeeList(feeList.toString());
        
        IData condition = getData();
        
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
    
    public abstract void setCondition(IData condition);

    public abstract void setGrpCustInfo(IData info);// 集团客户信息

    public abstract void setGrpFeeList(String feeList);

    public abstract void setInfo(IData info);

    public abstract void setMebUserInfo(IData info);// 成员用户信息

    public abstract void setMemberProduct(IData memberProduct);// 成员产品信息

}


package com.asiainfo.veris.crm.order.soa.group.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class GroupOfferInfoQueryBean extends GroupBean
{
    /*
     * 查询 inst_id
     */
    public static IDataset qryGrpOfferListforZx(IData data) throws Exception
    {
        IDataset results = new DatasetList();
    	IDataset categorys = UpcCall.queryOffersByMultiCategory(data.getString("PRODUCT_ID"), data.getString("EPARCHY_CODE"), data.getString("CATEGORY_ID"), "2");
    	
    	IDataset groups = new DatasetList();
        //构造用于获取打散商品的组
        IData group = new DataMap();
        group.put("GROUP_NAME", "不限");
        group.put("GROUP_ID", "-1");
        groups.add(group);
        //获取主产品下的组
        IDataset offerGroups = UpcCall.queryOfferGroups(data.getString("PRODUCT_ID"));
        
        boolean ifFilter = true;
        String userId = data.getString("GRP_USER_ID","");
        if(StringUtils.isEmpty(userId))
            userId = data.getString("USER_ID","");
        
        String busiType = data.getString("BUSI_TYPE","");
        if(busiType.equals("ZXOpen")){
            ifFilter = false;
        }
        else if(StringUtils.isNotEmpty(userId))
        {
            IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId, Route.getCrmDefaultDb());
            if(IDataUtil.isNotEmpty(userInfo))
            {
                String zxOpenTag = userInfo.getString("RSRV_STR1","0"); //1表示已经开通
                if(zxOpenTag.equals("1"))
                {
                    ifFilter = false;
                }
            }
        }
        
        if(ifFilter)
        {
            IDataset bizDatas = AttrBizInfoQry.getBizAttr(data.getString("PRODUCT_ID"), "P", "0", "FilerPg", null);
            if(IDataUtil.isNotEmpty(offerGroups)&&IDataUtil.isNotEmpty(bizDatas)){
            	String pgids = bizDatas.getData(0).getString("ATTR_VALUE", "");
            	String[] pgid = pgids.split(",");//多个包，用,间隔
            	for(int i = offerGroups.size() -1; i >= 0; i--)
                {
                    IData offerGroup = offerGroups.getData(i);
                    for(int j = 0;j<pgid.length;j++){
                    	if(offerGroup.getString("GROUP_ID", "").equals(pgid[j]))
                        {
                        	offerGroups.remove(i);
                        }
                    }
                    
                }
            }

        }
        if(IDataUtil.isNotEmpty(offerGroups)){
        	groups.addAll(offerGroups);
        }
        
        IData resultData = new DataMap();
        resultData.put("GROUPS", groups);
        resultData.put("CATEGORYS", categorys);
        results.add(resultData);

        return results;
    }

}

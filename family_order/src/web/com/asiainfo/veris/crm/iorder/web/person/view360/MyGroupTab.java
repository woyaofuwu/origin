package com.asiainfo.veris.crm.iorder.web.person.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.View360Const;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class MyGroupTab extends PersonBasePage {

    public void queryInfo(IRequestCycle cycle) throws Exception {
        IData inParam = getData();
        IData outParam = new DataMap();
        IDataset rtnData = new DatasetList();

        outParam.put("USER_ID", inParam.getString("USER_ID"));
        outParam.put(Route.ROUTE_EPARCHY_CODE, inParam.getString("EPARCHY_CODE", getTradeEparchyCode()));
        //IDataset mebInfos = GrpMebInfoQry.getGroupInfoByMember(inParam.getString("USER_ID"), inParam.getString("EPARCHY_CODE", getTradeEparchyCode()), null);
        IDataset mebInfos = CSViewCall.call(this, "CS.GrpMebInfoQrySVC.getGroupInfoByMember", outParam);
        /*IDataset userRels = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getUUByUserIdAB", outParam);
        IDataset userBBRels = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getBBInfoByUserIdAB", outParam);
        userRels.addAll(userBBRels);
        if (IDataUtil.isNotEmpty(userRels)) {
            queryGroupInfo(userRels, rtnData);
            queryGroupProductInfo(userRels, rtnData);
        }*/
        if (IDataUtil.isNotEmpty(mebInfos)) {
        	String fuzzy = inParam.getString("PARAM");
        	if("no".equals(fuzzy)){//免模糊化查询
        		this.queryGroupInfoFuzzy(mebInfos, rtnData);
        	}else{
        		this.queryGroupInfo(mebInfos, rtnData);
        	}
        	
        }
        queryCustManagerInfo(rtnData);
        setGrpInfos(rtnData);
        setAjax(rtnData);
    }

    /**
     * 查询集团成员所属集团信息
     * @param userRels
     * @param rtnData
     * @throws Exception
     */
    private void queryGroupInfo(IDataset mebInfos, IDataset rtnData) throws Exception {

        IData input = new DataMap();
        //IData grpOfferData = new DataMap(); // key:grpCustId, value:(IDataset) grpOfferList
        for (Object obj : mebInfos) {
            IData mebInfo = (IData) obj;
            String grpUserId = mebInfo.getString("USER_ID");
            input.clear();
            input.put("USER_ID", grpUserId);
            
            if (IDataUtil.isNotEmpty(mebInfo)) {
                String grpCustId = mebInfo.getString("CUST_ID", "");
                if (StringUtils.isNotBlank(grpCustId)) {
                    input.clear();
                    input.put("CUST_ID", grpCustId);
                    IData grpCust = CSViewCall.callone(this, "CS.UcaInfoQrySVC.qryGrpInfoByCustId", input);
                    if (IDataUtil.isNotEmpty(grpCust)) {
                        rtnData.add(grpCust);
                    }
                }
            }
        }

        /*for (Object obj : rtnData) {
            IData group = (IData) obj;
            IDataset grpOfferList = grpOfferData.getDataset(group.getString("CUST_ID"));
            StringBuilder productNameList = new StringBuilder();
            group.put("PRODUCT_NAME_LIST", productNameList);
            group.put("OFFERS", grpOfferList);
            group.put("OFFER_COUNT", grpOfferList.size());
        }*/
    }
    /**
     * 查询集团成员所属集团信息(免模糊化查询)
     * @param userRels
     * @param rtnData
     * @throws Exception
     */
    private void queryGroupInfoFuzzy(IDataset mebInfos, IDataset rtnData) throws Exception {
    	
    	IData input = new DataMap();
        //IData grpOfferData = new DataMap(); // key:grpCustId, value:(IDataset) grpOfferList
        for (Object obj : mebInfos) {
            IData mebInfo = (IData) obj;
            String grpUserId = mebInfo.getString("USER_ID");
            input.clear();
            input.put("USER_ID", grpUserId);
            
            if (IDataUtil.isNotEmpty(mebInfo)) {
                String grpCustId = mebInfo.getString("CUST_ID", "");
                if (StringUtils.isNotBlank(grpCustId)) {
                    input.clear();
                    input.put("CUST_ID", grpCustId);
                    input.put("X_DATA_NOT_FUZZY", true);
                    IData grpCust = CSViewCall.callone(this, "CS.UcaInfoQrySVC.qryGrpInfoByCustId", input);
                    if (IDataUtil.isNotEmpty(grpCust)) {
                        rtnData.add(grpCust);
                    }
                }
            }
        }
        

        /*for (Object obj : rtnData) {
            IData group = (IData) obj;
            IDataset grpOfferList = grpOfferData.getDataset(group.getString("CUST_ID"));
            StringBuilder productNameList = new StringBuilder();
            group.put("PRODUCT_NAME_LIST", productNameList);
            group.put("OFFERS", grpOfferList);
            group.put("OFFER_COUNT", grpOfferList.size());
        }*/
       
    }
    
    /**
     * 查询集团成员所属集团信息
     * @param userRels
     * @param rtnData
     * @throws Exception
     */
    /*private void queryGroupInfo(IDataset userRels, IDataset rtnData) throws Exception {
        IData input = new DataMap();
        IData grpUserData = new DataMap();  // key:grpUserId, value:grpCustId
        IData custUserData = new DataMap(); // 针对bboss商产品会有条bb关系，只需要查一个custId
        for (Object obj : userRels) {
            IData userRel = (IData) obj;
            String grpUserId = userRel.getString("USER_ID_A");
            String custId = grpUserData.getString(grpUserId, "");
            if (StringUtils.isBlank(custId)) {
                input.clear();
                input.put("USER_ID", grpUserId);
                IData grpUser = CSViewCall.callone(this, "CS.UcaInfoQrySVC.qryUserMainProdInfoByUserIdForGrp", input);
                if (IDataUtil.isEmpty(grpUser) || StringUtils.isBlank(grpUser.getString("CUST_ID"))) {
                    continue;
                }
                custId = grpUser.getString("CUST_ID");
                if (StringUtils.isNotBlank(custUserData.getString(custId))) {
                    continue;
                }
                grpUserData.put(grpUserId, custId);

                input.clear();
                input.put("CUST_ID", custId);
                IData grpCust = CSViewCall.callone(this, "CS.UcaInfoQrySVC.qryGrpInfoByCustId", input);
                if (IDataUtil.isNotEmpty(grpCust)) {
                    rtnData.add(grpCust);
                }
                custUserData.put(custId, grpUserId);
            }
        }
    }*/

    /**
     * 查询集团成员订购集团产品信息
     * @param userRels
     * @param rtnData
     * @throws Exception
     */
    /*private void queryGroupProductInfo(IDataset userRels, IDataset rtnData) throws Exception {
        if (IDataUtil.isEmpty(rtnData)) return;

        IData input = new DataMap();
        IData grpOfferData = new DataMap(); // key:grpCustId, value:(IDataset) grpOfferList
        for (Object obj : userRels) {
            IData userRel = (IData) obj;
            String grpUserId = userRel.getString("USER_ID_A");
            input.clear();
            input.put("USER_ID", grpUserId);
            IData grpUser = CSViewCall.callone(this, "CS.UcaInfoQrySVC.qryUserMainProdInfoByUserIdForGrp", input);
            if (IDataUtil.isNotEmpty(grpUser)) {
                String grpCustId = grpUser.getString("CUST_ID", "");
                if (StringUtils.isNotBlank(grpCustId)) {
                    input.clear();
                    input.put("CUST_ID", grpCustId);
                    IData grpCust = CSViewCall.callone(this, "CS.UcaInfoQrySVC.qryGrpInfoByCustId", input);
                    if (IDataUtil.isNotEmpty(grpCust)) {
                        IData tempMap = new DataMap();
                        tempMap.put("PRODUCT_ID", grpUser.getString("PRODUCT_ID"));
                        tempMap.put("PRODUCT_NAME", grpUser.getString("PRODUCT_NAME"));
                        IDataset grpOfferList = grpOfferData.getDataset(grpCustId);
                        if (IDataUtil.isEmpty(grpOfferList)) {
                            grpOfferList = new DatasetList();
                            grpOfferList.add(tempMap);
                            grpOfferData.put(grpCustId, grpOfferList);
                        } else {
                            grpOfferList.add(tempMap);
                        }
                    }
                }
            }
        }

        for (Object obj : rtnData) {
            IData group = (IData) obj;
            IDataset grpOfferList = grpOfferData.getDataset(group.getString("CUST_ID"));
            StringBuilder productNameList = new StringBuilder();
            for (Object offerObj : grpOfferList) {
                IData offer = (IData) offerObj;
                productNameList.append(offer.getString("PRODUCT_NAME")).append(" | ");
            }
            productNameList.setLength(productNameList.length() - 3); // 清除最末位的" | "
            group.put("PRODUCT_NAME_LIST", productNameList);
            group.put("OFFERS", grpOfferList);
            group.put("OFFER_COUNT", grpOfferList.size());
        }
    }*/

    /**
     * 查询集团客户经理信息
     * @param rtnData
     * @throws Exception
     */
    private void queryCustManagerInfo(IDataset rtnData) throws Exception {
        if (IDataUtil.isEmpty(rtnData)) return;

        IData input = new DataMap();
        for (Object obj : rtnData) {
            IData group = (IData) obj;
            String mgrId = group.getString("CUST_MANAGER_ID", "");
            if (StringUtils.isNotBlank(mgrId)) {
                input.clear();
                input.put("CUST_MANAGER_ID", mgrId);
                IData mgrInfo = CSViewCall.callone(this, "CS.CustManagerInfoQrySVC.qryCustManagerInfoById", input);
                if (IDataUtil.isNotEmpty(mgrInfo)) {
                    group.put("MANAGER_NAME", mgrInfo.getString("CUST_MANAGER_NAME"));
                    group.put("MANAGER_PHONE", mgrInfo.getString("SERIAL_NUMBER"));
                } else {
                    group.put("MANAGER_NAME", "已离岗");
                    group.put("MANAGER_PHONE", "无");
                }
            } else {
                group.put("MANAGER_NAME", "未分派");
                group.put("MANAGER_PHONE", "无");
            }
            // 集团类型
            group.put("LEVEL", View360Const.EC_LEVEL.get(group.getString("CLASS_ID")));
        }
    }

    public abstract void setGrpInfos(IDataset grpInfos);
}

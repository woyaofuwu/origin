
package com.asiainfo.veris.crm.iorder.web.person.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.IUpcConst;
import com.asiainfo.veris.crm.iorder.pub.consts.View360Const;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

import org.apache.tapestry.IRequestCycle;

public abstract class HomeTab extends PersonBasePage
{

    /**
     * 用户基本信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception {
        IData data = getData();
        if (StringUtils.isNotBlank(data.getString("USER_ID", ""))) {
            data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
            String fuzzyFlag = data.getString("PARAM");
            if("no".equals(fuzzyFlag)){//免模糊化查询
            	data.put("X_DATA_NOT_FUZZY", true);
            }
            IData baseInfo = CSViewCall.callone(this, "SS.GetUser360ViewSVC.qryUserInfo", data);
            IData resInfo = CSViewCall.callone(this, "SS.GetUser360ViewSVC.qryUserResSimInfo", data);
            IData mmsfuncInfo = CSViewCall.callone(this, "SS.GetUser360ViewSVC.qry_tf_sm_bi_mmsfunc_InfoByUserId", data);
            
            if (IDataUtil.isNotEmpty(baseInfo)) {
                // 发展员工字段有错误数据 去掉
                if (baseInfo.getString("DEVELOP_STAFF_ID", "").trim().length() < 8) {
                    baseInfo.put("DEVELOP_STAFF_ID", "");
                }

                // 翻译产品名称 品牌名称
                String productId = baseInfo.getString("PRODUCT_ID","");
                String brandCode = baseInfo.getString("BRAND_CODE","");
                if (StringUtils.isNotBlank(productId) || StringUtils.isNotBlank(brandCode)) {
                    IData param = new DataMap();
                    param.put("PRODUCT_ID", productId);
                    param.put("BRAND_CODE", brandCode);
                    IData result = CSViewCall.callone(this, "SS.CreateRedMemberSVC.getUserName", param);
                    if (IDataUtil.isNotEmpty(result)) {
                        baseInfo.put("PRODUCT_NAME", result.getString("PRODUCT_NAME"));
                        baseInfo.put("BRAND_NAME", result.getString("BRAND_NAME"));
                    }
                }

                // 产品描述
                if (StringUtils.isNotBlank(productId)) {
                    String productDesc = UpcViewCall.queryOfferExplainByOfferId(this, IUpcConst.ELEMENT_TYPE_CODE_PRODUCT, productId);
                    baseInfo.put("PRODUCT_DESC", productDesc);
                }

                // 翻译下月产品名称 品牌名称
                String nextProductId = baseInfo.getString("B_PRODUCT_ID","");
                String nextBrandCode = baseInfo.getString("B_BRAND_CODE","");
                if (StringUtils.isNotBlank(nextProductId) || StringUtils.isNotBlank(nextBrandCode)) {
                    IData param2 = new DataMap();
                    param2.put("PRODUCT_ID", nextProductId);
                    param2.put("BRAND_CODE", nextBrandCode);
                    IData result2 = CSViewCall.callone(this, "SS.CreateRedMemberSVC.getUserName", param2);
                    if (IDataUtil.isNotEmpty(result2)) {
                        baseInfo.put("B_PRODUCT_NAME", result2.getString("PRODUCT_NAME"));
                        baseInfo.put("B_BRAND_NAME", result2.getString("BRAND_NAME"));
                    }
                }
            }

            if (IDataUtil.isNotEmpty(resInfo)) {
                baseInfo.put("RES_KIND_NAME", resInfo.getString("RES_KIND_NAME", ""));
            }

            if (IDataUtil.isNotEmpty(mmsfuncInfo)) {
                String att_flag1_name = (String) mmsfuncInfo.get("ATT_FLAG1_NAME");
                baseInfo.put("ATT_FLAG1_NAME", att_flag1_name);
            }

            IData custInfo;
            String custName = "";
        	if ("on".equals(data.getString("NORMAL_USER_CHECK"))) {
                custInfo = CSViewCall.callone(this, "SS.GetUser360ViewSVC.qryCustInfo", data);
            } else {
                custInfo = CSViewCall.callone(this, "CS.UcaInfoQrySVC.qryCustomerInfoByCustId", data);
            }

            if (IDataUtil.isNotEmpty(custInfo)) {
                String isRealName = custInfo.getString("IS_REAL_NAME", "");
                if ((StringUtils.isNotBlank(isRealName) && "0".equals(isRealName))
                        || StringUtils.isBlank(isRealName)) {
                    custInfo.put("IS_REAL_NAME", "非实名"); // 非实名
                }
                if (StringUtils.isNotBlank(isRealName) && "1".equals(isRealName)) {
                    custInfo.put("IS_REAL_NAME", "是");    // 实名
                }
                custName = custInfo.getString("CUST_NAME");
            }
            if("yes".equals(fuzzyFlag)){
            	//将开户时间模糊化
                boolean isPriv=StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS012");
                //该工号没有免模糊化权限
        		if (!isPriv) {
        			if(IDataUtil.isNotEmpty(baseInfo)){
        				
    					String openTare = baseInfo.getString("OPEN_DATE");
    			        if (StringUtils.isNotBlank(openTare)) {
    						if (openTare.length()>10) {
    							StringBuilder vagueSB = new StringBuilder(19);
    							for(int j=0;j<11;j++){
    								vagueSB.append(openTare.charAt(j));
    							}for(int j=14;j<=19;j++){
    								vagueSB.append("*");
    							}
    							baseInfo.put("OPEN_DATE",vagueSB.toString());
    						}
    					}
        				
        			}
        			
        		}
            	
            }
			//专网用户APN标识
            baseInfo.put("IS_APNTAG", "0");
            IData otherParam = new DataMap();
            otherParam.put("USER_ID", data.getString("USER_ID", ""));
            otherParam.put("RSRV_VALUE_CODE", "USER_APNTAG");
            otherParam.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
            IDataset apnOtherInfos = CSViewCall.call(this,"CS.UserOtherInfoQrySVC.getUserOtherByUseridRsrvcode", otherParam);
            if(IDataUtil.isNotEmpty(apnOtherInfos))
            {
            	baseInfo.put("IS_APNTAG", "1");
            }
            setAjax("CUST_NAME", custName);
            setBaseInfo(baseInfo);
            setCustInfo(custInfo);
        }
    }

    /**
     * 使用人、责任人、经办人信息和携转信息查询
     * @param cycle
     * @throws Exception
     */
    public void extraCustInfoQuery(IRequestCycle cycle) throws Exception {
        IData param = getData();

        if (StringUtils.isNotBlank(param.getString("USER_ID", ""))) {
            param.put(Route.ROUTE_EPARCHY_CODE, param.getString("EPARCHY_CODE", getTradeEparchyCode()));

            IData custInfo = new DataMap();
            if ("on".equals(param.getString("NORMAL_USER_CHECK", ""))) {
                custInfo = CSViewCall.callone(this, "SS.GetUser360ViewSVC.qryCustInfo", param);
            }

            if (IDataUtil.isNotEmpty(custInfo)) {
                IData useInfo = new DataMap(); // 使用人
                useInfo.put("CUST_NAME", custInfo.getString("RSRV_STR5", "无"));
                useInfo.put("PSPT_TYPE_CODE", custInfo.getString("RSRV_STR6", ""));
                useInfo.put("PSPT_ID", custInfo.getString("RSRV_STR7", ""));
                useInfo.put("PSPT_ADDR", custInfo.getString("RSRV_STR8", ""));

                IData rspInfo = new DataMap(); // 责任人
                IData respInfo = CSViewCall.callone(this, "CS.CustPersonInfoQrySVC.qryCustPersonOtherInfoByCustId", param);
                if (IDataUtil.isNotEmpty(respInfo)) {
                    rspInfo.put("CUST_NAME", respInfo.getString("RSRV_STR2", "无"));
                    rspInfo.put("PSPT_TYPE_CODE", respInfo.getString("RSRV_STR3", ""));
                    rspInfo.put("PSPT_ID", respInfo.getString("RSRV_STR4", ""));
                    rspInfo.put("PSPT_ADDR", respInfo.getString("RSRV_STR5", ""));
                } else {
                    rspInfo.put("CUST_NAME", "无");
                }

                IData oprInfo = new DataMap(); // 经办人
                IData customerInfo = CSViewCall.callone(this, "CS.UcaInfoQrySVC.qryCustomerInfoByCustId", param);
                if (IDataUtil.isNotEmpty(customerInfo)) {
                    oprInfo.put("CUST_NAME", customerInfo.getString("RSRV_STR7", "无"));
                    oprInfo.put("PSPT_TYPE_CODE", customerInfo.getString("RSRV_STR8", ""));
                    oprInfo.put("PSPT_ID", customerInfo.getString("RSRV_STR9", ""));
                    oprInfo.put("PSPT_ADDR", customerInfo.getString("RSRV_STR10", ""));
                } else {
                    oprInfo.put("CUST_NAME", "无");
                }

                setUseInfo(useInfo);
                setRspInfo(rspInfo);
                setOprInfo(oprInfo);
            }

            IData npUserInfo = CSViewCall.callone(this, "SS.GetUser360ViewSVC.qryNpUserInfo", param);
            setNpInfo(npUserInfo);
        }
    }

    /**
     * 已订购业务分页标签优惠、平台业务、营销活动查询
     * @param cycle
     * @throws Exception
     */
    public void subscriptionTabQuery(IRequestCycle cycle) throws Exception {
        IData param = getData();
        if (StringUtils.isNotBlank(param.getString("USER_ID", ""))) {
            param.put(Route.ROUTE_EPARCHY_CODE, param.getString("EPARCHY_CODE", getTradeEparchyCode()));
            String tabName = param.getString("TAB_NAME", "");
            if (SVC_TAB.equals(tabName)) {
                IDataset svcInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserSvcInfo", param);
                setSvcInfos(svcInfo);
            } else if (DISCNT_TAB.equals(tabName)) {
                IDataset discntInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserDiscntInfo", param);
                setDiscntInfos(discntInfo);
            } else if (PLATSVC_TAB.equals(tabName)) {
                IDataset platSvcInfo = CSViewCall.call(this, "CS.PlatComponentSVC.getUserPlatSvcs11", param);
                setPlatInfos(platSvcInfo);
            } else if (SALEACTIVE_TAB.equals(tabName)) {
                IDataset saleActiveInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserSaleActiveInfo", param);
                setSaleActiveInfos(saleActiveInfo);
            }
        }
    }

    /**
     * 近三月账单查询和当月套餐使用量查询
     * @param cycle
     * @throws Exception
     */
    public void acctInfoQuery(IRequestCycle cycle) throws Exception {
        IData param = getData();
        if (StringUtils.isNotBlank(param.getString("USER_ID", ""))) {
            param.put(Route.ROUTE_EPARCHY_CODE, param.getString("EPARCHY_CODE", getTradeEparchyCode()));
            String queryArea = param.getString("QUERY_AREA", "");
            if (USER_BILL.equals(queryArea)) {
                IData month = new DataMap();
                IDataset billInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.queryUserBillInfo", param);

                month.put("THIS_MONTH", billInfo.first().getString("THIS_MONTH", ""));
                month.put("LAST_MONTH", billInfo.first().getString("LAST_MONTH", ""));
                month.put("MONTH_BEFORE_LAST", billInfo.first().getString("MONTH_BEFORE_LAST", ""));

                setMonth(month);
                setBillInfos(billInfo);
            } else if (USER_ALLOWANCE.equals(queryArea)) {
                IData usageInfo = CSViewCall.callone(this, "SS.GetUser360ViewSVC.queryUserConsumptionInfo", param);
                setUsageInfo(usageInfo);
            }
        }
    }

    public String imgClassSelector(int index) {
        return View360Const.BILL_COMMON + View360Const.BILL_IMG.get(index);
    }

    private static final String SVC_TAB = "svcTab";

    private static final String DISCNT_TAB = "discntTab";

    private static final String PLATSVC_TAB = "platSvcTab";

    private static final String SALEACTIVE_TAB = "saleActiveTab";

    private static final String USER_BILL = "billTablePart";

    private static final String USER_ALLOWANCE = "allowancePart";

    public abstract void setBaseInfo(IData baseInfo);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setNpInfo(IData npInfo);

    public abstract void setUseInfo(IData useInfo);

    public abstract void setRspInfo(IData rspInfo);

    public abstract void setOprInfo(IData oprInfo);

    public abstract void setSvcInfos(IDataset svcInfos);

    public abstract void setDiscntInfos(IDataset discntInfos);

    public abstract void setPlatInfos(IDataset platInfos);

    public abstract void setSaleActiveInfos(IDataset saleActiveInfos);

    public abstract void setMonth(IData month);

    public abstract void setBillInfos(IDataset billInfos);

    public abstract void setUsageInfo(IData usageInfo);
}

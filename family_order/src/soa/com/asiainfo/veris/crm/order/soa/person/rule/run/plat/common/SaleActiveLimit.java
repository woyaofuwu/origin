package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.common;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.PlatUtils;

/**
 * Created by chenyr on 2016/12/19.
 */
public class SaleActiveLimit extends BreBase implements IBREScript {

    private static Logger logger = Logger.getLogger(SaleActiveLimit.class);

    @Override
    public boolean run(IData databus, BreRuleParam paramData) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 SaleActiveLimit() >>>>>>>>>>>>>>>>>>");
        }

        PlatSvcData psd = (PlatSvcData) databus.get(PlatConstants.RULE_PLATSVC);
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        List<ProductModuleData> allPlatSvcData = (List<ProductModuleData>) databus.get("RULE_PLATSVC_All");
        IDataset commparaDataset1 = CommparaInfoQry.getCommpara("CSM", "1220", "1220", CSBizBean.getTradeEparchyCode());
        IDataset commparaDataset = new DatasetList();
        for (int j = 0; j < commparaDataset1.size(); j++) {
            IData commparaproduct = commparaDataset1.getData(j);
            if (StringUtils.isEmpty(commparaproduct.getString("PARA_CODE8"))) {
                commparaDataset.add(commparaDataset1.getData(j));
            }
        }
        IDataset saleActiveList = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(uca.getUserId());

        // 查询用户是否订购过购机营销活动
        IDataset commparaSaleActives = IDataUtil.filterByEqualsCol(saleActiveList, "PRODUCT_ID", commparaDataset, "PARA_CODE2");
        // 去重
        IDataset commparaSaleActiveList = DataHelper.distinct(commparaSaleActives, "PRODUCT_ID", "");

        // 过滤超出活动期间的营销活动
        commparaSaleActiveList = this.filterAvailableSaleActive(commparaSaleActiveList, commparaDataset);
        // 获取用户所有的平台服务资料（包含生效的和预约生效的以及未完工台帐的合并结果）
        IDataset rulePlatsvcCommpara = DataHelper.filter(commparaDataset, "PARA_CODE1=" + psd.getElementId());
        IDataset userPlatSvcDataset = new DatasetList();
        List<PlatSvcTradeData> platsvcTradeDataList = uca.getUserPlatSvcs();
        if (CollectionUtils.isNotEmpty(platsvcTradeDataList))
        {
            for (int index = 0, size = platsvcTradeDataList.size(); index < size; index++)
            {
                Boolean isDel = false;
                for (int i = 0; i < allPlatSvcData.size(); i++) {
                    PlatSvcData tempPsd = (PlatSvcData) allPlatSvcData.get(i);
                    if (logger.isDebugEnabled()) {
                        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> SaleActiveLimit() >>>>>>>>>>>>>>>>>> allPlatSvcData tempPsd.getElementId() = " + tempPsd.getElementId());
                    }
                    if (PlatConstants.OPER_CANCEL_ORDER.equals(tempPsd.getOperCode()) && tempPsd.getElementId().equals(platsvcTradeDataList.get(index).getElementId())) {
                        isDel = true;
                    }
                }

                if (!isDel) {
                    userPlatSvcDataset.add(platsvcTradeDataList.get(index).toData());
                }
            }
        }

        for (int i = 0; i < commparaSaleActiveList.size(); i++) {
            IData commparaSaleActive = commparaSaleActiveList.getData(i);
            String productId = commparaSaleActive.getString("PRODUCT_ID");
            IDataset commParaProductList = DataHelper.filter(commparaDataset, "PARA_CODE2=" + productId);
            IDataset filterUserPlatsvcList = IDataUtil.filterByEqualsCol(userPlatSvcDataset, "SERVICE_ID", commParaProductList, "PARA_CODE1");

            String errMsg = "用户在"+ commparaSaleActive.getString("PRODUCT_NAME") + "活动协议期内，不能退订该业务。";
            if ((PlatConstants.OPER_CANCEL_ORDER.equals(psd.getOperCode()) || PlatConstants.OPER_USER_DATA_MODIFY.equals(psd.getOperCode()))
                    && commParaProductList.size() > 0) {
                if (filterUserPlatsvcList.size() == 0) {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0906.toString(), errMsg);
                    return false;
                } else if (filterUserPlatsvcList.size() == 1) { // 如果只有一条满足时，需要判断属性是否符合要求
                    IData userplatsvc = filterUserPlatsvcList.getData(0);
                    String serviceId = userplatsvc.getString("SERVICE_ID");
                    IDataset commparaServiceList = DataHelper.filter(commparaDataset, "PARA_CODE1=" + serviceId);

                    // 判断业务属性是否符合要求（例如无线音乐会员需要判断是否为特级会员）
                    IData commparaService = null;
                    List<AttrTradeData> userAttrList = null;
                    for (int j = 0; j < commparaServiceList.size(); j++) {
                        commparaService = commparaServiceList.getData(j);

                        if (!StringUtils.isEmpty(commparaService.getString("PARA_CODE5"))) {
                            if (PlatConstants.OPER_USER_DATA_MODIFY.equals(psd.getOperCode()) && serviceId.equals(psd.getElementId())) {
                                List<AttrData> platSvcAttrList = psd.getAttrs(); // 页面提交的属性和属性值
                                String attrValue = PlatUtils.getAttrValue(commparaService.getString("PARA_CODE5", ""), platSvcAttrList);
                                if (!commparaService.getString("PARA_CODE6", "").equals(attrValue)) {
                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0906.toString(), errMsg);
                                    return false;
                                }
                            } else {
                                userAttrList = uca.getUserAttrsByRelaInstId(userplatsvc.getString("INST_ID"));

                                for (AttrTradeData attr : userAttrList) {
                                    if (commparaService.getString("PARA_CODE5", "").equals(attr.getAttrCode()) && !commparaService.getString("PARA_CODE6", "").equals(attr.getAttrValue())) {
                                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0906.toString(), errMsg);
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * 过滤超出活动期间的营销活动
     */
    private IDataset filterAvailableSaleActive(IDataset commparaSaleActiveList, IDataset commparaDataset) throws Exception {
        IDataset returnList = new DatasetList();
        IDataset tempComparaData = null;
        boolean available = true;

        for (int i = 0; i < commparaSaleActiveList.size(); i++) {
            available = true;
            IData tempSaleActive = commparaSaleActiveList.getData(i);
            tempComparaData = DataHelper.filter(commparaDataset, "PARA_CODE2=" + tempSaleActive.getString("PRODUCT_ID", ""));

            if (tempComparaData.size() > 0 && !StringUtils.isEmpty(tempComparaData.getData(0).getString("PARA_CODE7"))) {
                String startDate = SysDateMgr.addMonths(tempSaleActive.getString("START_DATE"), Integer.valueOf(tempComparaData.getData(0).getString("PARA_CODE7")));

                if (SysDateMgr.getTimeDiff(SysDateMgr.getDateForYYYYMMDD(startDate), SysDateMgr.getSysDateYYYYMMDD(), SysDateMgr.PATTERN_TIME_YYYYMMDD) > 0) {
                    available = false;
                }
            }

            if (available) {
                returnList.add(tempSaleActive);
            }
        }

        return returnList;
    }
}

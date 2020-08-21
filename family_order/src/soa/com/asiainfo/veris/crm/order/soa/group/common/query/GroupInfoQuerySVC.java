
package com.asiainfo.veris.crm.order.soa.group.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.GrpAdcMasIntf;


public class GroupInfoQuerySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public static IDataset qryGroupPRBTBySN(IData inparam) throws Exception
    {
        return GroupInfoQueryDAO.qryGroupPRBTBySN(inparam);
    }

    public IDataset crtTrade(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.crtTrade(inparam);
    }

    public IDataset qryADCGroupInfoBySn(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryADCGroupInfoBySn(inparam, this.getPagination());
    }

    public IDataset qryADCMembersbyuser(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryADCMembersbyuser(inparam, this.getPagination());
    }

    public IDataset qryADCPersonalOrderInfo(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryADCPersonalOrderInfo(inparam, this.getPagination());
    }
    
    public IDataset qryCenpyOrderInfo(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryCenpyOrderInfo(inparam, this.getPagination());
    }

    public IDataset qryGroupPRBByProductId(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryGroupPRBByProductId(inparam, this.getPagination());
    }

    public IDataset qryGroupPRBTInfo(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryGroupPRBTInfo(inparam, this.getPagination());
    }

    public IDataset qryIPLaterPayInfoA(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryIPLaterPayInfoA(inparam, this.getPagination());
    }

    public IDataset qryIPLaterPayInfoB(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryIPLaterPayInfoB(inparam, this.getPagination());
    }

    public IDataset qryPower100InfoByCustId(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.queryPower100InfoByCustId(inparam, this.getPagination());
    }

    public IDataset qryPower100MemberProdInfosByUserIdA(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryPower100MemberProdInfosByUserIdA(inparam, this.getPagination());
    }

    public IDataset qryProductInfo(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryProductInfo(inparam, this.getPagination());
    }

    public IDataset qryProductMebInfo(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryProductMebInfo(inparam, this.getPagination());
    }

    public IDataset qryShortCodeByVpmn4M(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryShortCodeByVpmn4M(inparam, this.getPagination());
    }

    public IDataset qryUUnVPN(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryUUnVPN(inparam);
    }

    public IDataset qryVpmnInfo(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryVpmnInfo(inparam, this.getPagination());
    }

    public IDataset qryVpmnInfoRelation(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryVpmnInfoRelation(inparam, this.getPagination());
    }

    public IDataset qryGroupENetInfo(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryGroupENetInfo(inparam, this.getPagination());
    }

    public IDataset qryAllGroupENetInfo(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryAllGroupENetInfo(inparam, this.getPagination());
    }

    public IDataset qryGroupLBSInfo(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryGroupLBSInfo(inparam, this.getPagination());
    }

    public IDataset qryGroupLBSByProductId(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryGroupLBSByProductId(inparam, this.getPagination());
    }

    public IDataset qryGroupLBSBySN(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryGroupLBSBySN(inparam, this.getPagination());
    }

    public IDataset qryGroupNetworkInfo(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryGroupNetworkInfo(inparam, this.getPagination());
    }

    public IDataset queryByUserid(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.queryByUserid(inparam, this.getPagination());
    }

    public IDataset qryGrpSpecialPayInfo(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.qryGrpSpecialPayInfo(inparam, this.getPagination());
    }

    public IDataset getUserInfo(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.getUserInfo(inparam);
    }

    public IDataset getUserGrpPackageByUserId(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.getUserGrpPackageByUserId(inparam);
    }

    public IDataset getUserRealtionUU(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.getUserRealtionUU(inparam);
    }

    public IDataset getPackageElement(IData inparam) throws Exception
    {
        GroupInfoQueryBean bean = new GroupInfoQueryBean();
        return bean.getPackageElement(inparam);
    }

    /**
     * 新校讯通0000查询
     *
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset qryXXTMembersBySerialnumber(IData data) throws Exception {
        return GrpAdcMasIntf.qryXXTMembersBySerialnumber(data);
    }
    
    /**
     * 集团专线资费，服务查询
     *
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset qryGrpOfferListforZx(IData data) throws Exception {
    	return GroupOfferInfoQueryBean.qryGrpOfferListforZx(data);
    }
    
    public IDataset qryItemIdDetails(IData data) throws Exception {
    	String itemId = data.getString("ITEM_ID");
    	return AcctCall.getItemIdDetails(itemId);
    }
    
    public IDataset qryGrpVpmnSerialNumber(IData inparam) throws Exception {
    	return GroupInfoQueryDAO.qryGrpVpmnSerialNumber(inparam);
    }
}

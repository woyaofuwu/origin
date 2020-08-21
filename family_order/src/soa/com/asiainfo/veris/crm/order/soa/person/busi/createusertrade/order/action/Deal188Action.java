
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreatePersonUserRequestData;

/**
 * 判断是否是签约赠送188号码新号码，如果是则给用户登记免费呼转优惠9407和用户关系表
 * 
 * @author sunxin
 */
public class Deal188Action implements ITradeAction
{

    // 准备免费呼转优惠台帐数据 sunxin
    private void dealForDiscnt(BusiTradeData btd, UcaData uca, IData param) throws Exception
    {

        DiscntTradeData newDiscnt = new DiscntTradeData();
        newDiscnt.setUserId(param.getString("USER_ID"));
        newDiscnt.setProductId(param.getString("PRODUCT_ID"));
        newDiscnt.setPackageId(param.getString("PACKAGE_ID"));
        newDiscnt.setElementId(param.getString("DISCNT_CODE"));
        newDiscnt.setInstId(SeqMgr.getInstId());
        newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
        newDiscnt.setSpecTag("0");
        newDiscnt.setStartDate(SysDateMgr.getSysTime());
        newDiscnt.setEndDate(param.getString("END_DATE"));
        btd.add(uca.getSerialNumber(), newDiscnt);

    }

    // 准备关系台帐数据
    public void dealForRelation(BusiTradeData btd, UcaData uca, IData param) throws Exception
    {
        RelationTradeData relationUU = new RelationTradeData();
        relationUU.setModifyTag(BofConst.MODIFY_TAG_ADD);
        relationUU.setUserIdA(param.getString("USER_ID_A"));
        relationUU.setUserIdB(param.getString("USER_ID_B"));
        relationUU.setSerialNumberA("HZ" + uca.getSerialNumber());
        relationUU.setSerialNumberB(param.getString("SERIAL_NUMBER_B"));
        relationUU.setRelationTypeCode("46");
        relationUU.setRoleTypeCode("0");
        relationUU.setRoleCodeA("0");
        relationUU.setRoleCodeB(param.getString("ROLE_CODE_B"));
        relationUU.setOrderno("0");
        relationUU.setShortCode("0");
        relationUU.setStartDate(SysDateMgr.getSysTime());
        relationUU.setEndDate(param.getString("END_DATE"));
        relationUU.setInstId(SeqMgr.getInstId());
        btd.add(uca.getSerialNumber(), relationUU);

    }

    public void executeAction(BusiTradeData btd) throws Exception
    {

        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        String authUserId = "";// createPersonUserRD.getAuthForSaleUserId();
        String authSerialNumber = "";// createPersonUserRD.getAuthForSaleSerialNumber();
        String authFlag = "";// createPersonUserRD.getAuthForSaleTag();
        String authProductId = "-1";
        IData discntEndDateData = TagInfoQry.queryTagInfo("SALE_ACTIVE_DISCNT_ENDDATE");// (btd.getRD().getUca().getUserEparchyCode(),
        // "SALE_ACTIVE_DISCNT_ENDDATE",
        // "0");
        String discntEndDate = "";
        if (discntEndDateData.isEmpty())
        {
            discntEndDate = "2010-12-31 23:59:58";
        }
        else
            discntEndDate = discntEndDateData.getString("TAG_DATE", "2010-12-31 23:59:58");

        if (!"".equals(authSerialNumber) && "true".equals(authFlag))
        {
            IData data = UcaInfoQry.qryUserInfoBySn(authSerialNumber);// (pd,data);
            if (IDataUtil.isEmpty(data))
            {
                authUserId = data.getString("USER_ID", "");
                authProductId = data.getString("PRODUCT_ID", "-1");
            }
        }
        if (!"".equals(authUserId) && !"".equals(authSerialNumber) && "true".equals(authFlag))
        {
            IData tempPage0 = new DataMap();
            IData tempPage1 = new DataMap();
            IData tempPage2 = new DataMap();
            IData tempPage3 = new DataMap();
            String userIdA = SeqMgr.getUserId();
            // 准备免费呼转优惠台帐数据

            String productId = createPersonUserRD.getUca().getProductId();
            String packageId = "-1";

            IDataset packageDataSet = ProductPkgInfoQry.queryPackageByProDis(productId, "9407");
            if (packageDataSet != null && packageDataSet.size() != 0)
            {
                packageId = packageDataSet.getData(0).getString("PACKAGE_ID", "-1");
            }
            tempPage0.put("USER_ID", createPersonUserRD.getUca().getUser().getUserId());
            tempPage0.put("PRODUCT_ID", productId);
            tempPage0.put("PACKAGE_ID", packageId);
            tempPage0.put("END_DATE", discntEndDate);
            tempPage0.put("DISCNT_CODE", "9407");
            dealForDiscnt(btd, createPersonUserRD.getUca(), tempPage0);

            // 副卡加优惠

            String oldUserPackageId = "-1";
            IDataset oldPackageDataSet = ProductPkgInfoQry.queryPackageByProDis(productId, "9407");
            if (packageDataSet != null && packageDataSet.size() != 0)
            {
                oldUserPackageId = oldPackageDataSet.getData(0).getString("PACKAGE_ID", "-1");
            }
            tempPage1.put("USER_ID", authUserId);
            tempPage1.put("PRODUCT_ID", authProductId);
            tempPage1.put("PACKAGE_ID", oldUserPackageId);
            tempPage1.put("END_DATE", discntEndDate);
            tempPage1.put("DISCNT_CODE", "9407");
            dealForDiscnt(btd, createPersonUserRD.getUca(), tempPage1);

            // 准备主卡关系台帐数据
            tempPage2.put("USER_ID_A", userIdA);
            tempPage2.put("USER_ID_B", createPersonUserRD.getUca().getUser().getUserId());
            tempPage2.put("SERIAL_NUMBER_B", createPersonUserRD.getUca().getUser().getSerialNumber());
            tempPage2.put("ROLE_CODE_B", "1");
            tempPage2.put("END_DATE", discntEndDate);
            dealForRelation(btd, createPersonUserRD.getUca(), tempPage2);
            // 准备副卡关系台帐数据
            tempPage3.put("USER_ID_A", userIdA);
            tempPage3.put("USER_ID_B", authUserId);
            tempPage3.put("SERIAL_NUMBER_B", authSerialNumber);
            tempPage3.put("ROLE_CODE_B", "2");
            tempPage3.put("END_DATE", discntEndDate);
            dealForRelation(btd, createPersonUserRD.getUca(), tempPage3);

        }

    }

}

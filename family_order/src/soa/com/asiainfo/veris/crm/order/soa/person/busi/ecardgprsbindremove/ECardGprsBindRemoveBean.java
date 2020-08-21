
package com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbindremove;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class ECardGprsBindRemoveBean extends CSBizBean
{

    /**
     * 根据主卡查询办理随E行的虚拟用户。并判断是否绑定了优惠。返回副卡信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset checkERelationInfo(IData input) throws Exception
    {
        IDataset relationInfos = RelaUUInfoQry.qryRelaByUserIdB(input.getString("USER_ID_B"), "80", "1");

        String instIdADiscnts = "";
        String userIdA = "";
        IData relationInfo = null;
        if (relationInfos.size() > 0 && !relationInfos.isEmpty())
        {
            relationInfo = (IData) relationInfos.get(0);
            userIdA = relationInfo.getString("USER_ID_A", "");
            // 查询优惠
            IDataset discntIdAInfos = UserDiscntInfoQry.getAllDiscntByUser(userIdA, "5904");

            if (discntIdAInfos.size() <= 0 || discntIdAInfos.isEmpty())
            {
                CSAppException.apperr(CrmUserException.CRM_USER_664);
                // CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取虚拟用户优惠无数据[5904]！");
            }
            else
            {
                instIdADiscnts = discntIdAInfos.getData(0).getString("INST_ID", "");
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有绑定信息！");
        }

        IDataset relaRole2Infos = RelaUUInfoQry.getSEL_USER_ROLEA(userIdA, "2", "80");
        return relaRole2Infos;

    }

    /*
     * 获取绑定信息
     */
    public IDataset queryCrmInfos(IData input) throws Exception
    {
        // 获得用户产品资料
        IDataset userChanges = UserInfoQry.getUserInfoChgByUserIdCurvalid(input.getString("USER_ID"));
        IData userChange = userChanges.size() > 0 ? (IData) userChanges.get(0) : null;
        String curBrand = null;
        String curProduct = null;
        if (userChange != null && userChange.size() > 0)
        {
            curBrand = userChange.getString("BRAND_CODE", "");
            curProduct = userChange.getString("PRODUCT_ID", "");
        }
        // 获取用户预约产品资料
        String nextBrand = "", nextProduct = "";

        IDataset bookTrades = TradeInfoQry.getTradeBookByUserId(input.getString("USER_ID"),CSBizBean.getUserEparchyCode());

        if (bookTrades != null && bookTrades.size() > 0)
        {
            IData bookTrade = bookTrades.getData(0);
            nextBrand = bookTrade.getString("BRAND_CODE", "");
            nextProduct = bookTrade.getString("PRODUCT_ID", "");
        }
        else
        {
            // 获取下月生效产品、品牌
            IDataset nextChanges = UserInfoQry.getUserInfoChgByUserIdNxtvalid(input.getString("USER_ID"));
            if (nextChanges != null && nextChanges.size() > 0)
            {
                IData nextChange = (IData) nextChanges.get(0);
                nextBrand = nextChange.getString("BRAND_CODE", "");
                nextProduct = nextChange.getString("PRODUCT_ID", "");
            }
        }
        IData commonInfo = new DataMap();
        commonInfo.put("CUR_BRAND", curBrand);
        commonInfo.put("NEXT_BRAND", nextBrand);
        commonInfo.put("CUR_PRODUCT_ID", curProduct);
        commonInfo.put("NEXT_PRODUCT_ID", nextProduct);
        commonInfo.put("CUR_BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(curBrand));
        commonInfo.put("NEXT_BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(nextBrand));
        commonInfo.put("CUR_PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(curProduct));
        commonInfo.put("NEXT_PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(nextProduct));

        IDataset dsResult = new DatasetList();
        dsResult.add(commonInfo);
        return dsResult;

    }

}


package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.pmd;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.UserDiscntException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.requestdata.ChangeProductReqData;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: TwoCityVacationPmdAction.java
 * @Description: 两成一家 非常假期处理【select * from td_s_static t where t.type_id = 'INTF_CITY_VACATION_PKG'】
 * @version: v1.0.0
 * @author: maoke
 * @date: Aug 27, 2014 7:44:41 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Aug 27, 2014 maoke v1.0.0 修改原因
 */
public class TwoCityVacationPmdAction implements IProductModuleAction
{

    /**
     * @Description: 删除某优惠
     * @param uca
     * @param btd
     * @throws Exception
     * @author: maoke
     * @date: Jun 28, 2014 10:44:18 AM
     */
    public boolean delTwoCityVacationDiscnt(UcaData uca, BusiTradeData btd) throws Exception
    {
        ChangeProductReqData request = (ChangeProductReqData) btd.getRD();

        String delDiscntCode = request.getOptionParam1();// 待变更的优惠,接口传过来

        if (StringUtils.isNotBlank(delDiscntCode))
        {
            List<DiscntTradeData> userDiscnts = uca.getUserDiscntByDiscntId(delDiscntCode);

            if (userDiscnts.isEmpty())
            {
                CSAppException.apperr(ElementException.CRM_ELEMENT_38, delDiscntCode);
            }
            else
            {
                DiscntTradeData discntTD = new DiscntTradeData(userDiscnts.get(0).toData());

                if (!BofConst.MODIFY_TAG_DEL.equals(discntTD.getModifyTag()))
                {
                    // 得到元素生效配置
                    DiscntData discntData = new DiscntData();
                    discntData.setElementId(delDiscntCode);
                    discntData.setPkgElementConfig(discntTD.getPackageId());

                    // 拼台账
                    discntTD.setEndDate(ProductModuleCalDate.calCancelDate(discntData, btd.getProductTimeEnv()));
                    discntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);

                    btd.add(btd.getRD().getUca().getSerialNumber(), discntTD);

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        String modifyTag = dealPmtd.getModifyTag();
        String packageId = "";
        String instId = dealPmtd.getInstId();
        String productId = "";
        OfferRelTradeData selfRel = uca.getLastMainProductRel(instId);
        if(selfRel != null){
        	packageId = selfRel.getGroupId();
        	productId = selfRel.getOfferCode();
        }

        String staticPkg = StaticUtil.getStaticValue("INTF_CITY_VACATION_PKG", packageId);

        if (StringUtils.isNotBlank(staticPkg))
        {
            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            {
                String inModeCode = CSBizBean.getVisit().getInModeCode();

                if ("0".equals(inModeCode) || "1".equals(inModeCode))// 前台和客服
                {
                    if (getNumByPackageId(packageId, uca) < 2)// 当包下面只有一个元素时候 立即生效
                    {
                        this.reSetTwoCityVacaTionDiscnt(instId, packageId, uca, btd);
                    }
                }
                else
                {
                    // 接口过来如果有删除,则下月生效 按配置走即可 否则立即生效
                    boolean isDel = delTwoCityVacationDiscnt(uca, btd);

                    if (!isDel && getNumByPackageId(packageId, uca) < 2)// 本次没有删除 且包下面只有一个元素时候 立即生效
                    {
                        this.reSetTwoCityVacaTionDiscnt(instId, packageId, uca, btd);
                    }
                }
            }
            
            //因两城一家 非常假期是绝对有效时间 当开始时间大于结束时间的时候,报错
            List<DiscntTradeData> userDiscnt = uca.getUserDiscntByPidPkid(productId, packageId);
            if(userDiscnt != null && userDiscnt.size() > 0)
            {
                for(DiscntTradeData discnt : userDiscnt)
                {
                    String discntCode = discnt.getDiscntCode();
                    String startDate = SysDateMgr.decodeTimestamp(discnt.getStartDate(), SysDateMgr.PATTERN_STAND);
                    String endDate = SysDateMgr.decodeTimestamp(discnt.getEndDate(), SysDateMgr.PATTERN_STAND);
                    
                    if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && startDate.compareTo(endDate) >=0)
                    {
                        CSAppException.apperr(ElementException.CRM_ELEMENT_308, UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode), startDate, endDate);
                    }
                }
            }
        }
    }

    /**
     * @Description: 根据包ID查找非本次添加外的总数【大于0代表非首次订购】
     * @param packageId
     * @param uca
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jun 28, 2014 2:56:18 PM
     */
    public int getNumByPackageId(String packageId, UcaData uca) throws Exception
    {
        int num = 0;

        List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();

        if (userDiscnts != null && userDiscnts.size() > 0)
        {
            for (DiscntTradeData userDiscnt : userDiscnts)
            {
                String tempPackageId = "";
                String tempModifyTag = userDiscnt.getModifyTag();
                OfferRelTradeData offerRel = uca.getLastMainProductRel(userDiscnt.getInstId());
                if(offerRel != null){
                	tempPackageId = offerRel.getGroupId();
                }

                // 不是本次新增且包相同
                if (!BofConst.MODIFY_TAG_ADD.equals(tempModifyTag) && packageId.equals(tempPackageId))
                {
                    num++;
                }
            }
        }
        return num;
    }

    /**
     * @Description: 重置时间【立即】
     * @param instId
     * @param packageId
     * @param uca
     * @param btd
     * @throws Exception
     * @author: maoke
     * @date: Aug 27, 2014 7:35:42 PM
     */
    public void reSetTwoCityVacaTionDiscnt(String instId, String packageId, UcaData uca, BusiTradeData btd) throws Exception
    {
        DiscntTradeData discntUser = uca.getUserDiscntByInstId(instId);

        // 对于本次新增的 修改开始时间为立即生效
        if (BofConst.MODIFY_TAG_ADD.equals(discntUser.getModifyTag()))
        {
            String startDate = "";

            ProductTimeEnv env = btd.getProductTimeEnv();

            if (env != null && StringUtils.isNotBlank(env.getBasicAbsoluteStartDate()))
            {
                startDate = env.getBasicAbsoluteStartDate();
            }
            else
            {
                startDate = btd.getRD().getAcceptTime();
            }

            // 得到包元素配置
            DiscntData elementData = new DiscntData();
            elementData.setElementId(discntUser.getElementId());
            elementData.setPkgElementConfig(packageId);

            // 重置时间
            discntUser.setStartDate(startDate);
            discntUser.setEndDate(ProductModuleCalDate.calEndDate(elementData, startDate).substring(0, 10) + SysDateMgr.END_DATE);
        }
    }
}

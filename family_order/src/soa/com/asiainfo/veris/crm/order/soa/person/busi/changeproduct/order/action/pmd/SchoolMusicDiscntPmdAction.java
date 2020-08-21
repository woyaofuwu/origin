
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.pmd;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.UserDiscntException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.requestdata.ChangeProductReqData;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: SchoolMusicDiscntPmdAction.java
 * @Description: 校园音乐会员套餐优惠【TD_S_DISCNT_TYPE=R】
 * @version: v1.0.0
 * @author: maoke
 * @date: Jun 12, 2014 7:43:39 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jun 12, 2014 maoke v1.0.0 修改原因
 */
public class SchoolMusicDiscntPmdAction implements IProductModuleAction
{
    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        if (!btd.isProductChange())
        {
            String modifyTag = dealPmtd.getModifyTag();
            String elementId = dealPmtd.getElementId();
            String serialNumber = uca.getSerialNumber();
            String endDate = SysDateMgr.getLastSecond(uca.getUserDiscntByInstId(dealPmtd.getInstId()).getStartDate());

            String discntElementType = UDiscntInfoQry.getDiscntTypeByDiscntCode(elementId);
            if (StringUtils.isNotBlank(discntElementType) && (PersonConst.DISCNT_TYPE_R.equals(discntElementType)))
            {
                if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                {
                    IDataset commpara16 = CommparaInfoQry.getCommparaAllCol("CSM", "16", elementId, CSBizBean.getTradeEparchyCode());

                    if (IDataUtil.isNotEmpty(commpara16))
                    {
                        // 先删除老的优惠
                        IData compara = commpara16.getData(0);

                        String discntCode1 = compara.getString("PARA_CODE1");
                        String discntCode2 = compara.getString("PARA_CODE2");
                        String discntCode3 = compara.getString("PARA_CODE3");

                        List<DiscntTradeData> userAllDiscnt = uca.getUserDiscnts();

                        if (userAllDiscnt != null && userAllDiscnt.size() > 0)
                        {
                            for (DiscntTradeData userDiscnt : userAllDiscnt)
                            {
                                String discntCode = userDiscnt.getDiscntCode();

                                if (discntCode1.equals(discntCode) || discntCode2.equals(discntCode) || discntCode3.equals(discntCode))
                                {
                                    String tempModifyTag = userDiscnt.getModifyTag();

                                    if (!BofConst.MODIFY_TAG_DEL.equals(tempModifyTag))
                                    {
                                        DiscntTradeData discntTd = new DiscntTradeData(userDiscnt.toData());

                                        // 如果是预约，老优惠到预约月的月底结束
                                        ChangeProductReqData request = (ChangeProductReqData) btd.getRD();

                                        String startDate = request.getAcceptTime();
                                        if (request.isBookingTag())
                                        {
                                            startDate = request.getBookingDate();
                                        }

                                        discntTd.setEndDate(endDate);
                                        discntTd.setModifyTag(BofConst.MODIFY_TAG_DEL);

                                        btd.add(serialNumber, discntTd);
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        String disnctName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);
                        CSAppException.apperr(ParamException.CRM_PARAM_506, "套餐【" + disnctName + "】", "16");
                    }

                    // 新增的R类优惠在此类调用前已经处理好，此处不做处理
                }
                if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
                {
                    String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);

                    CSAppException.apperr(UserDiscntException.CRM_USER_DISCNT_7,discntName);
                }
            }
        }
    }
}

package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;
import org.apache.log4j.Logger;

public class CheckSPAMChangeSpeedLimit extends BreBase implements IBREScript{
    public static final Logger logger=Logger.getLogger(CheckSPAMChangeSpeedLimit.class);
	@Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        logger.debug("========CheckSPAMChangeSpeedLimit===");

        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData reqData = databus.getData("REQDATA");// 请求的数据

            if (IDataUtil.isNotEmpty(reqData) && reqData.size()>0)
            {
                IDataset selectedElements = new DatasetList(reqData.getString("SELECTED_ELEMENTS"));
                String serialNumber = databus.getString("SERIAL_NUMBER");

                if (IDataUtil.isNotEmpty(selectedElements)) {
                    for (int i = 0, size = selectedElements.size(); i < size; i++) {
                        IData element = selectedElements.getData(i);

                        String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
                        String elementId = element.getString("ELEMENT_ID");
                        String modifyTag = element.getString("MODIFY_TAG");
                        if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode)
                                && BofConst.MODIFY_TAG_ADD.equals(modifyTag)
                                && "80176874".equals(elementId)) {
                            //办理和校园（校讯通）在网，宽带产品为FTTH 300M以下的城区客户
                            String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode("80176874");

                            //和校园（校讯通）在网
                            String userId = databus.getString("USER_ID");
                            IDataset qryXxtValidProduct = UserProductInfoQry.getXxtValidProduct(userId);
                            if (IDataUtil.isEmpty(qryXxtValidProduct)) {
                                String errorMsg = "非和校园（校讯通）在网客户，不能办理该优惠【" + discntName + "】。";
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "2020030901", errorMsg);
                                return true;
                            }

                            //宽带产品为FTTH 300M以下
                            IDataset widenetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_" + serialNumber);
                            if (IDataUtil.isEmpty(widenetInfo))
                            {
                                String errorMsg = "非宽带客户，不能办理该优惠【" + discntName + "】。";
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "2020030902", errorMsg);
                                return true;
                            }
                            //非FTTH
                            if (!BofConst.WIDENET_TYPE_FTTH.equals(widenetInfo.getData(0).getString("RSRV_STR2"))
                                    && !BofConst.WIDENET_TYPE_TTFTTH.equals(widenetInfo.getData(0).getString("RSRV_STR2"))) {
                                String errorMsg = "宽带产品非FTTH的客户，不能办理该优惠【" + discntName + "】。";
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "2020030903", errorMsg);
                                return true;
                            }
                            //非300M以下
                            String widenetUserRate = WideNetUtil.getWidenetUserRate("KD_" + serialNumber);//宽带用户速率
                            if (Integer.valueOf(widenetUserRate) >= (300 * 1024)){
                                String errorMsg = "宽带产品非300M以下的客户，不能办理该优惠【" + discntName + "】。";
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "2020030904", errorMsg);
                                return true;
                            }

                            //城区
                            String deviceId = widenetInfo.getData(0).getString("RSRV_NUM1");
                            IData param = new DataMap();
                            param.put("DEVICE_ID",deviceId);
                            IDataset rs = CSAppCall.call("PB.AddressManageSvc.queryCityInfo", param);
                            if(IDataUtil.isNotEmpty(rs))
                            {
                                IData data = rs.first();
                                if("0".equals(data.getString("status",""))){
                                    String errorMsg = "非城区客户，不能办理该优惠【" + discntName + "】。";
                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "2020030905", errorMsg);
                                    return true;
                                }
                            }else {
                                String errorMsg = "非城区客户，不能办理该优惠【" + discntName + "】。";
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "2020030906", errorMsg);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        
        return false;
    }
}

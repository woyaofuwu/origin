
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangeproduct.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.buildrequest.BuildChangeProduct;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangeproduct.order.requestdata.CttBroadbandChangeProductReqData;

/**
 * @Description: 创建铁通宽带产品变更请求对象
 */
public class BuildCttBroadbandChangeProductReqData extends BuildChangeProduct implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {
        super.buildBusiRequestData(data, brd);

        String tmpSvcSpeed = "";
        String svcElementName = "";
        String rate = "";
        CttBroadbandChangeProductReqData changeProductData = (CttBroadbandChangeProductReqData) brd;
        changeProductData.setChangeRateFlag(false);

        IDataset selectedElements = new DatasetList(data.getString("CHECK_SELECTED_ELEMENTS"));
        if (IDataUtil.isNotEmpty(selectedElements))
        {
            for (int i = 0; i < selectedElements.size(); i++)
            {
                IData element = selectedElements.getData(i);
                String elementId = element.getString("ELEMENT_ID");
                if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")) && !StringUtils.equals(elementId, "501") && !StringUtils.equals("1", element.getString("MODIFY_TAG"))
                        && !StringUtils.equals("0_1", element.getString("MODIFY_TAG")))
                {

                    IDataset commparas = ParamInfoQry.getCommparaByCode("CSM", "1127", elementId, CSBizBean.getTradeEparchyCode());
                    if (commparas.size() == 0)
                    {
                        CSAppException.apperr(ParamException.CRM_PARAM_503, elementId);
                    }

                    rate = commparas.getData(0).getString("PARA_CODE1");
                    tmpSvcSpeed = Integer.parseInt(rate) / 1024 + "M";
                    if (!"".equals(tmpSvcSpeed))
                    {
                        svcElementName = USvcInfoQry.getSvcNameBySvcId(elementId);
                        break;// 由于目前只能有一种速率,多的速率,页面会做判断.
                    }

                    if (StringUtils.equals("0", element.getString("MODIFY_TAG")))
                    {
                        changeProductData.setChangeRateFlag(true);
                    }
                }
            }
            for (int i = 0; i < selectedElements.size(); i++)
            {
                IData element = selectedElements.getData(i);
                String elementId = element.getString("ELEMENT_ID");
                String elementName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);
                if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")) && StringUtils.equals("0", element.getString("MODIFY_TAG")))
                {
                    if (elementName.indexOf(tmpSvcSpeed) < 0)
                    {
                        CSAppException.apperr(BroadBandException.CRM_BROADBAND_104, elementName, svcElementName);
                    }
                }
            }
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new CttBroadbandChangeProductReqData();
    }

}

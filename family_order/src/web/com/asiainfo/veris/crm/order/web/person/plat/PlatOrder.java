
package com.asiainfo.veris.crm.order.web.person.plat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class PlatOrder extends PersonBasePage
{
    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        IData param = new DataMap();
        this.setDisableSearch("false");
        this.setDisableOperation("false");
        String bizTypeCode = data.getString("BIZ_TYPE_CODE");
        String subType = data.getString("SUB_TYPE");

        if (StringUtils.isNotBlank(bizTypeCode) || StringUtils.isNotBlank(subType))
        {
            if (data.getString("SUB_TYPE") == null || "".equals(data.getString("SUB_TYPE")))
            {
                param.put("RSRV_STR1", "custserv");
            }
            else
            {
                param.put("RSRV_STR1", data.getString("SUB_TYPE"));
            }
            param.put("BIZ_TYPE_CODE", bizTypeCode);
            IDataset datas = CSViewCall.call(this, "CS.PlatComponentSVC.getPlatSvcs", param);
            this.setDisableSearch("true");
            this.setDisableOperation("true");
            this.setInitPlatSvcs(datas);
        }
        else
        {
            String showAllCancel = data.getString("SHOW_ALL_CANCEL");
            if ("true".equals(showAllCancel))
            {
                this.setShowAllCancel("true");
                return;
            }

            String showSwitch = data.getString("SHOW_SWITCH");
            if ("true".equals(showSwitch))
            {
                this.setShowSwitch("true");
                return;
            }
            
            String showKeyBusiness = data.getString("KEYBUSINESS_AREA");
            if ("true".equals(showKeyBusiness))
            {
                this.setShowKeyBusiness("true");
                return;
            }
        }
    }

    public void loadPlatSvc(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        String searchText = param.getString("SERVICE_NAME");
        if (StringUtils.isNotBlank(searchText) && searchText.length() >= 2)
        {
            SearchResponse resp = SearchClient.search("PM_OFFER_PLATSVC", searchText, 0, 10);
            IDataset datas = resp.getDatas();
            this.setAjax(datas);
        }
    }

    public abstract void setDisableOperation(String isDisableOperation);

    public abstract void setDisableSearch(String isDisableSearch);

    public abstract void setInitPlatSvcs(IDataset initPlatSvcs);

    public abstract void setShowAllCancel(String allCancel);

    public abstract void setShowSwitch(String showSwitch);
   
    public abstract void setShowKeyBusiness(String showKeyBusiness);

    public void submitPlat(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();

        IDataset selectedElements = null;
        if (param.getString("SELECTED_ELEMENTS") != null)
        {
            selectedElements = new DatasetList(param.getString("SELECTED_ELEMENTS"));
        }

        if (IDataUtil.isNotEmpty(selectedElements))
        {
            int size = selectedElements.size();
            IDataset giftDatas = new DatasetList(); // 赠送的订购列表
            IDataset newSelectedElements = new DatasetList();// 非赠送的订购列表
            for (int i = 0; i < size; i++)
            {
                IData element = selectedElements.getData(i);
                if ("GIFT".equals(element.getString("OPER_CODE")))
                {
                    element.put("OPER_CODE", PlatConstants.OPER_ORDER);
                    giftDatas.add(element);
                }
                else
                {
                    newSelectedElements.add(element);
                }
            }
            if (IDataUtil.isNotEmpty(giftDatas))
            {
                param.put("GIFT_DATAS", giftDatas);

                // 处理全部都是赠送的订购关系GIFT_DATAS的情况，此时SELECTED_ELEMENTS为空，会造成后面业务处理错误；
                // 此处要拆分成一个SELECTED_ELEMENTS和一个GIFT_DATAS的情况
                if (IDataUtil.isNotEmpty(newSelectedElements))
                {
                    param.put("SELECTED_ELEMENTS", newSelectedElements);
                }
                else
                {
                    if (StringUtils.isBlank(param.getString("ALL_CANCEL", "")) && StringUtils.isBlank(param.getString("ALL_SWITCH", "")))
                    {
                        IData giftData = giftDatas.getData(0);
                        String giftSerialNumber = giftData.getString("GIFT_SERIAL_NUMBER");
                        String tradeSerialNumber = param.getString("SERIAL_NUMBER");
                        param.put("SERIAL_NUMBER", giftSerialNumber); // 赠送接受人的号码，即被赠送的号码
                        giftData.put("OPER_CODE", PlatConstants.OPER_ORDER);
                        giftData.put("GIFT_SERIAL_NUMBER", tradeSerialNumber);// 赠送人的号码
                        newSelectedElements.add(giftData);
                        param.put("SELECTED_ELEMENTS", newSelectedElements);
                        giftDatas.remove(0);
                        size = giftDatas.size();
                        for (int i = 0; i < size; i++)
                        {
                            IData gift = giftDatas.getData(i);
                            giftSerialNumber = gift.getString("GIFT_SERIAL_NUMBER");
                            gift.put("GIFT_SERIAL_NUMBER", tradeSerialNumber); // 赠送人的号码
                            gift.put("GIFTED_SERIAL_NUMBER", giftSerialNumber);// 被赠送人的号码
                            gift.put("OPER_CODE", PlatConstants.OPER_ORDER);
                        }
                    }
                }
            }
        }
        IDataset result = CSViewCall.call(this, "SS.PlatRegSVC.tradeReg", param);
        this.setAjax(result);
    }
}

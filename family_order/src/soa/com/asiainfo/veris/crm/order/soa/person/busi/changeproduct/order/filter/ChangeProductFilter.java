
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.TimeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeProductFilter.java
 * @Description: 单元素产品变更接口入参转换,重新组装后的数据以适用于接口Build类
 * @version: v1.0.0
 * @author: maoke
 * @date: Jun 10, 2014 10:10:59 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jun 10, 2014 maoke v1.0.0 修改原因
 */
public class ChangeProductFilter implements IFilterIn
{

    /**
     * 必输参数检查
     * 
     * @param input
     * @throws Exception
     */
    public void checkInputData(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "ELEMENT_ID");// 服务ID或优惠ID或产品ID
        IDataUtil.chkParam(input, "ELEMENT_TYPE_CODE");// S-服务；D-优惠；P-产品
        IDataUtil.chkParam(input, "MODIFY_TAG");// 0-新增；1-删除；2-修改属性

        if (input.containsKey("BOOKING_TAG"))
        {
            if (!"0".equals(input.getString("BOOKING_TAG")) && !"1".equals(input.getString("BOOKING_TAG")))
            {
                CSAppException.apperr(ParamException.CRM_PARAM_507);
            }

            if ("1".equals(input.getString("BOOKING_TAG")))
            {
                if (StringUtils.isBlank(input.getString("START_DATE", "")))
                {
                    input.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
                }

                if (SysDateMgr.decodeTimestamp(input.getString("START_DATE"), SysDateMgr.PATTERN_STAND_YYYYMMDD).compareTo(SysDateMgr.getSysDate()) <= 0)
                {
                    CSAppException.apperr(TimeException.CRM_TIME_67);
                }

                if (SysDateMgr.monthInterval(SysDateMgr.getSysDate(), input.getString("START_DATE")) - 1 > 5)
                {
                    CSAppException.apperr(TimeException.CRM_TIME_68);
                }

                input.put("BOOKING_DATE", input.getString("START_DATE"));
            }
        }

        String elementTypeCode = input.getString("ELEMENT_TYPE_CODE");
        String modifyTag = input.getString("MODIFY_TAG");

        if (!(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode) || BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode) || BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode) || BofConst.ELEMENT_TYPE_CODE_PLATSVC
                .equals(elementTypeCode)))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_508);
        }

        if (!(BofConst.MODIFY_TAG_ADD.equals(modifyTag) || BofConst.MODIFY_TAG_DEL.equals(modifyTag) || BofConst.MODIFY_TAG_UPD.equals(modifyTag)))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_509);
        }
    }

    /**
     * 处理ATTR参数
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset dealAttrData(IData input) throws Exception
    {
        String attrStr1 = input.getString("ATTR_STR1", "");
        String attrStr2 = input.getString("ATTR_STR2", "");
        String attrStr3 = input.getString("ATTR_STR3", "");
        String attrStr4 = input.getString("ATTR_STR4", "");
        String attrStr5 = input.getString("ATTR_STR5", "");
        String attrStr6 = input.getString("ATTR_STR6", "");
        String attrStr7 = input.getString("ATTR_STR7", "");
        String attrStr8 = input.getString("ATTR_STR8", "");
        String attrStr9 = input.getString("ATTR_STR9", "");
        String attrStr10 = input.getString("ATTR_STR10", "");
        String attrStr11 = input.getString("ATTR_STR11", "");
        String attrStr12 = input.getString("ATTR_STR12", "");
        String attrStr13 = input.getString("ATTR_STR13", "");
        String attrStr14 = input.getString("ATTR_STR14", "");
        String attrStr15 = input.getString("ATTR_STR15", "");
        String attrStr16 = input.getString("ATTR_STR16", "");

        IDataset attrDatas = new DatasetList();

        String modifyTag = input.getString("MODIFY_TAG");

        if (StringUtils.isNotBlank(attrStr1))
        {
            if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                IDataUtil.chkParam(input, "ATTR_STR2");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attrStr1);
                attr.put("ATTR_VALUE", attrStr2);
                attr.put("MODIFY_TAG", modifyTag);
                attrDatas.add(attr);
            }
        }
        if (StringUtils.isNotBlank(attrStr3))
        {
            if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                IDataUtil.chkParam(input, "ATTR_STR4");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attrStr3);
                attr.put("ATTR_VALUE", attrStr4);
                attr.put("MODIFY_TAG", modifyTag);
                attrDatas.add(attr);
            }
        }
        if (StringUtils.isNotBlank(attrStr5))
        {
            if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                IDataUtil.chkParam(input, "ATTR_STR6");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attrStr5);
                attr.put("ATTR_VALUE", attrStr6);
                attr.put("MODIFY_TAG", modifyTag);
                attrDatas.add(attr);
            }
        }
        if (StringUtils.isNotBlank(attrStr7))
        {
            if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                IDataUtil.chkParam(input, "ATTR_STR8");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attrStr7);
                attr.put("ATTR_VALUE", attrStr8);
                attrDatas.add(attr);
            }
        }
        if (StringUtils.isNotBlank(attrStr9))
        {
            if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                IDataUtil.chkParam(input, "ATTR_STR10");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attrStr9);
                attr.put("ATTR_VALUE", attrStr10);
                attr.put("MODIFY_TAG", modifyTag);
                attrDatas.add(attr);
            }
        }
        if (StringUtils.isNotBlank(attrStr11))
        {
            if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                IDataUtil.chkParam(input, "ATTR_STR12");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attrStr11);
                attr.put("ATTR_VALUE", attrStr12);
                attr.put("MODIFY_TAG", modifyTag);
                attrDatas.add(attr);
            }
        }
        if (StringUtils.isNotBlank(attrStr13))
        {
            if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                IDataUtil.chkParam(input, "ATTR_STR14");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attrStr13);
                attr.put("ATTR_VALUE", attrStr14);
                attr.put("MODIFY_TAG", modifyTag);
                attrDatas.add(attr);
            }
        }
        if (StringUtils.isNotBlank(attrStr15))
        {
            if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                IDataUtil.chkParam(input, "ATTR_STR16");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attrStr15);
                attr.put("ATTR_VALUE", attrStr16);
                attr.put("MODIFY_TAG", modifyTag);
                attrDatas.add(attr);
            }
        }

        return attrDatas;
    }

    /**
     * 处理可选优惠
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset dealOptionDiscntData(IData input) throws Exception
    {
        String elementTypeCode = input.getString("ELEMENT_TYPE_CODE");
        String modifyTag = input.getString("MODIFY_TAG");

        String discntStr1 = input.getString("DISCNT_STR1", "");
        String discntStr2 = input.getString("DISCNT_STR2", "");
        String discntStr3 = input.getString("DISCNT_STR3", "");
        String discntStr4 = input.getString("DISCNT_STR4", "");
        String discntStr5 = input.getString("DISCNT_STR5", "");

        IDataset discntDatas = new DatasetList();

        if (StringUtils.isNotBlank(discntStr1))
        {
            IData discnt = new DataMap();
            discnt.put("ELEMENT_ID", discntStr1);
            discnt.put("MODIFY_TAG", BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode) ? BofConst.MODIFY_TAG_ADD : modifyTag);
            discnt.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
            discntDatas.add(discnt);
        }

        if (StringUtils.isNotBlank(discntStr2))
        {
            IData discnt = new DataMap();
            discnt.put("ELEMENT_ID", discntStr2);
            discnt.put("MODIFY_TAG", BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode) ? BofConst.MODIFY_TAG_ADD : modifyTag);
            discnt.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
            discntDatas.add(discnt);
        }

        if (StringUtils.isNotBlank(discntStr3))
        {
            IData discnt = new DataMap();
            discnt.put("ELEMENT_ID", discntStr3);
            discnt.put("MODIFY_TAG", BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode) ? BofConst.MODIFY_TAG_ADD : modifyTag);
            discnt.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
            discntDatas.add(discnt);
        }

        if (StringUtils.isNotBlank(discntStr4))
        {
            IData discnt = new DataMap();
            discnt.put("ELEMENT_ID", discntStr4);
            discnt.put("MODIFY_TAG", BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode) ? BofConst.MODIFY_TAG_ADD : modifyTag);
            discnt.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
            discntDatas.add(discnt);
        }

        if (StringUtils.isNotBlank(discntStr5))
        {
            IData discnt = new DataMap();
            discnt.put("ELEMENT_ID", discntStr5);
            discnt.put("MODIFY_TAG", BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode) ? BofConst.MODIFY_TAG_ADD : modifyTag);
            discnt.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
            discntDatas.add(discnt);
        }

        return discntDatas;
    }

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        this.checkInputData(input);

        IData tempData = new DataMap();
        tempData.put("ELEMENT_TYPE_CODE", input.getString("ELEMENT_TYPE_CODE"));
        tempData.put("MODIFY_TAG", input.getString("MODIFY_TAG"));
        tempData.put("ELEMENT_ID", input.getString("ELEMENT_ID"));
        tempData.put("INST_ID", input.getString("INST_ID"));

        IDataset selectedElements = new DatasetList();

        if (IDataUtil.isNotEmpty(dealAttrData(input)))
        {
            tempData.put("ATTR_PARAM", dealAttrData(input));
        }
        if("Y".equals(input.getString("IS_VIDEOPCK",""))){//视频定向流量包
       	 tempData.put("ATTR_PARAM", input.getDataset("ATTR_PARAM"));
       }
        selectedElements.add(tempData);

        IDataset optionDiscntDatas = dealOptionDiscntData(input);

        if (IDataUtil.isNotEmpty(optionDiscntDatas))
        {
            for (int i = 0; i < optionDiscntDatas.size(); i++)
            {
                selectedElements.add(optionDiscntDatas.getData(i));
            }
        }

        input.put("SELECTED_ELEMENTS", selectedElements);

        // 接口产品变更时候,加入NEW_PRODUCT_ID方便规则取值以便调用规则和前台一致
        if (IDataUtil.isNotEmpty(selectedElements))
        {
            for (int j = 0, size = selectedElements.size(); j < size; j++)
            {
                String elementTypeCode = selectedElements.getData(j).getString("ELEMENT_TYPE_CODE");
                String elementId = selectedElements.getData(j).getString("ELEMENT_ID");

                if (BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode))
                {
                    input.put("NEW_PRODUCT_ID", elementId);
                    break;
                }
            }
        }
    }
}

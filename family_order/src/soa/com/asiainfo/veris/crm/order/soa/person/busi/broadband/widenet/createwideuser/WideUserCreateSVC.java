package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;

public class WideUserCreateSVC extends CSBizService {
    private static final long serialVersionUID = 1L;

    /**
     * 校验主号码
     * 
     * @author chenzm
     * @param input
     * @throws Exception
     */

    public IData checkSerialNumber(IData input) throws Exception {
        WideUserCreateBean wideUserCreateBean = BeanManager.createBean(WideUserCreateBean.class);
        IData data = wideUserCreateBean.checkSerialNumber(input);
        return data;
    }

    public IDataset getWidenetProductInfo(IData input) throws Exception {

        WideUserCreateBean wideUserCreateBean = BeanManager.createBean(WideUserCreateBean.class);
        IDataset dataset = wideUserCreateBean.getWidenetProductInfo(input);
        return dataset;
    }

    /**
     * 获取宽带开户用户新号码
     * 
     * @author liaoyi
     * @param input
     * @throws Exception
     */
    public IData getWideSerialNumber(IData input) throws Exception {
        WideUserCreateBean wideUserCreateBean = BeanManager.createBean(WideUserCreateBean.class);
        IData data = wideUserCreateBean.getWideSerialNumber(input);
        return data;
    }

    /**
     * 获取批量宽带开户用户新号码
     * 
     * @author liaoyi
     * @param input
     * @throws Exception
     */
    public IData getBatWideSerialNumber(IData input) throws Exception {
        WideUserCreateBean wideUserCreateBean = BeanManager.createBean(WideUserCreateBean.class);
        IData data = wideUserCreateBean.getBatWideSerialNumber(input);
        return data;
    }

    /**
     * 校园宽带开户（外部临时调用）
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset openSchoolWidenetIntf(IData input) throws Exception {
        IDataUtil.chkParam(input, "SERVICE_IDS");
        IDataUtil.chkParam(input, "DISCNT_IDS");
        IDataset selectedelements = new DatasetList();
        // 处理服务
        String[] services = input.getString("SERVICE_IDS").split("\\|");

        String packageId = "-1";
        for (int i = 0; i < services.length; i++) {
            IData element = new DataMap();
            element.put("ELEMENT_ID", services[i]);
            element.put("ELEMENT_TYPE_CODE", "S");
            element.put("PRODUCT_ID", input.getString("WIDE_PRODUCT_ID"));

            IData elementCfg = ProductElementsCache.getElement(input.getString("WIDE_PRODUCT_ID"), services[i], BofConst.ELEMENT_TYPE_CODE_SVC);
            if (IDataUtil.isNotEmpty(elementCfg)) {
                packageId = elementCfg.getString("GROUP_ID", "-1");
            }

            element.put("PACKAGE_ID", packageId);
            element.put("MODIFY_TAG", "0");
            element.put("START_DATE", SysDateMgr.getSysTime());
            element.put("END_DATE", "2050-12-31");
            selectedelements.add(element);
        }
        // 处理优惠
        String[] discnts = input.getString("DISCNT_IDS").split("\\|");
        for (int i = 0; i < discnts.length; i++) {
            IData element = new DataMap();
            element.put("ELEMENT_ID", discnts[i]);
            element.put("ELEMENT_TYPE_CODE", "D");
            element.put("PRODUCT_ID", input.getString("WIDE_PRODUCT_ID"));
            element.put("MODIFY_TAG", "0");

            IData elementCfg = ProductElementsCache.getElement(input.getString("WIDE_PRODUCT_ID"), discnts[i], BofConst.ELEMENT_TYPE_CODE_DISCNT);
            if (IDataUtil.isNotEmpty(elementCfg)) {
                packageId = elementCfg.getString("GROUP_ID", "-1");
            }

            element.put("PACKAGE_ID", packageId);
            if ("84020842".equals(discnts[i])) {
                element.put("START_DATE", SysDateMgr.getSysTime());
                element.put("END_DATE", "2022-12-31");
            } else {
                element.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
                element.put("END_DATE", "2050-12-31");
            }
            selectedelements.add(element);
        }

        input.put("SELECTED_ELEMENTS", selectedelements.toString());
        input.put("TEMP_OPEN", "1");
        IDataset results = CSAppCall.call("SS.WideUserCreateRegSVC.tradeReg", input);

        return results;

    }

    /**
     * 获取宽带开户用户新号码 中小企业快速办理新增
     * 
     * @author taosx
     * @param input
     * @throws Exception
     */
    public IDataset getWideSerialNumberMinorec(IData input) throws Exception {
        WideUserCreateBean wideUserCreateBean = BeanManager.createBean(WideUserCreateBean.class);
        return wideUserCreateBean.getWideSerialNumberMinorec(input);
    }

}

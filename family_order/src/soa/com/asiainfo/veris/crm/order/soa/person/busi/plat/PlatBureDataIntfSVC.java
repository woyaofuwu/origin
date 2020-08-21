
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.person.busi.plat.officedata.PlatOfficeDataBean;

public class PlatBureDataIntfSVC extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * DSMP局数据同步SP业务信息接口
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset dsmpBizSync(IData param) throws Exception
    {
        initParam();
        String onlyCheck = param.getString("ONLY_CHECK", "-1");
        IData result = new DataMap();

        IDataset list = new DatasetList();
        IData data = this.parserParam(param, "DSMP_BIZ_INFO_KEY");
        list.add(data);

        if (onlyCheck.equals("TRUE"))
        {
            result = PlatOfficeDataBean.spBizCheck(data);
            if (!StringUtils.equals("0", result.getString("X_RESULTCODE")))
            {
                IDataset rs = new DatasetList();
                rs.add(result);
                return rs;
            }
        }
        else if (onlyCheck.equals("FALSE"))
        {
            result = PlatOfficeDataBean.spBizCheck(data);
            if (!StringUtils.equals("0", result.getString("X_RESULTCODE")))
            {
                IDataset rs = new DatasetList();
                rs.add(result);
                return rs;
            }
            PlatOfficeDataBean.importSpBiz(param.getString("FILE_NAME"), list, "IBOSS_DSMP");
        }
        else
        {
            result.put("X_RESULTCODE", "-1");
            result.put("X_RESULTCHECK", "FFFF");
            result.put("X_RESULTINFO", "控制编码ONLY_CHECK无效！");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            IDataset rs = new DatasetList();
            rs.add(result);
            return rs;
        }

        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTCHECK", "DONE");
        result.put("X_RESULTINFO", "OK！");

        IDataset rs = new DatasetList();
        rs.add(result);
        return rs;
    }

    /**
     * DSMP局数据同步SP企业信息接口
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IData dsmpSPInfoSync(IData param) throws Exception
    {
        initParam();// 初始化默认参数
        IData result = new DataMap();
        String onlyCheck = param.getString("ONLY_CHECK", "-1");
        // String operType = param.getString("OPER_TYPE", "-1");

        IDataset list = new DatasetList();
        IData data = this.parserParam(param, "DSMP_SP_INFO_KEY");
        list.add(data);

        if (StringUtils.equals("TRUE", onlyCheck))
        {
            result = PlatOfficeDataBean.spInfoCheck(data);

            if (!StringUtils.equals("0", result.getString("X_RESULTCODE")))
            {
                return result;
            }
        }
        else if (StringUtils.equals("FALSE", onlyCheck))
        {
            result = PlatOfficeDataBean.spInfoCheck(data);
            if (!StringUtils.equals("0", result.getString("X_RESULTCODE")))
            {
                return result;
            }
            PlatOfficeDataBean.importSpInfo(param.getString("FILE_NAME"), list, "IBOSS_DSMP");
        }
        else
        {
            result.put("X_RESULTCODE", "-1");
            result.put("X_RESULTCHECK", "FFFF");
            result.put("X_RESULTINFO", "控制编码ONLY_CHECK无效！");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }

        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTCHECK", "DONE");
        result.put("X_RESULTINFO", "OK！");

        return result;
    }

    // 接口参数处理，包括必填处理，接口字段名转化?是否拆分，看联调情况
    private void initParam() throws Exception
    {
        CSBizBean.getVisit().setCityCode("IFT00");
        CSBizBean.getVisit().setDepartCode("IFT00");
        CSBizBean.getVisit().setStaffId("ITF00000");
    }

    /**
     * 对象转换成IDataset
     * 
     * @param obj
     * @return 非空
     */
    private IDataset objToIDataset(Object obj)
    {
        IDataset dataset = null;
        if (obj != null)
        {
            try
            {
                if (obj instanceof IDataset)
                {
                    dataset = (IDataset) obj;
                }
                else if (obj instanceof String)
                {
                    dataset = new DatasetList();
                    dataset.add(obj);
                }
                else
                {
                    dataset = new DatasetList(obj.toString());
                }
            }
            catch (Throwable e)
            {
                dataset = new DatasetList();
                dataset.add(obj);
            }
        }

        if (dataset == null)
        {
            dataset = new DatasetList();
        }
        return dataset;
    }

    // 接口参数处理，包括必填处理，接口字段名转化?是否拆分，看联调情况
    private IData parserParam(IData param, String key) throws Exception
    {

        IData data = new DataMap();
        IDataset keyset = StaticUtil.getStaticList(key);

        if (keyset != null && keyset.size() > 0)
        {
            for (Object element : keyset)
            {
                IData keys = (IData) element;
                String crmKey = keys.getString("KEY");
                String ibossKey = keys.getString("VALUE");

                data.put(crmKey, param.getString(ibossKey));

            }
        }
        else
        {
            data.putAll(param);
        }

        return data;
    }

    /**
     * 局数据同步SP业务信息接口，农信通特有
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IData rinpBizInfoSync(IData param) throws Exception
    {

        IData result = new DataMap();
        PlatBureDataIntfBean bean = new PlatBureDataIntfBean();
        initParam();
        IDataset paramset = new DatasetList();
        paramset.add(param);
        IData bizList = new DataMap();
        for (Object obj : paramset)
        {

            IData data = this.parserParam((IData) obj, "RINPB999_RINP0999_1_0");
            String bizStatus = data.getString("BIZ_STATUS", "");
            if (bizStatus.length() == 0)
            {
                String operCode = data.getString("OPER_TYPE", "");
                if (operCode.equals("01"))
                {// 01－增加：本业务为新增业务；
                    data.put("BIZ_STATUS", "A"); // 规范无这个字段，填默认值
                }
                else if (operCode.equals("02"))
                {// 02－终止：终止本业务；
                    data.put("BIZ_STATUS", "E"); // 规范无这个字段，填默认值
                }
                else if (operCode.equals("03"))
                {// 03－暂停：本业务暂停；
                    data.put("BIZ_STATUS", "N"); // 规范无这个字段，填默认值
                }
                else if (operCode.equals("04") || operCode.equals("05"))
                {// 04－恢复：恢复本业务,05－变更
                    data.put("BIZ_STATUS", "A"); // 规范无这个字段，填默认值
                }
            }

            IData subBiz = new DataMap();
            IDataset bizAreas = new DatasetList();
            IDataset subBizAreas = new DatasetList();
            data.put("BIZ_TYPE_CODE", "27");
            String type = data.getString("PRO_PACK_TYPE");
            String biz_desc = data.getString("BIZ_DESC", "");
            if (biz_desc == null || biz_desc.equals(""))
            {
                data.put("BIZ_DESC", data.getString("BIZ_NAME", ""));
            }

            if (type.equals("01"))
            { // 产品

                Object temp = data.get("PRO_AREA_CODE");
                IDataset areas = this.objToIDataset(temp);

                if (areas.size() > 0)
                {
                    for (Object area : areas)
                    {
                        IData bizArea = new DataMap();
                        bizArea.putAll(data);
                        bizArea.put("BIZ_TYPE_CODE", "27");
                        bizArea.put("BIZ_LEAVE", "1");
                        bizArea.put("AREA_CODE", area.toString());
                        bizAreas.add(bizArea);
                    }
                }
                bizList.put(data.getString("SP_CODE"), data);

            }
            else if (type.equals("02"))
            { // 产品包

                subBiz.putAll(data);
                data.put("BIZ_CODE", data.getString("PRO_PACK_CODE"));
                data.put("BIZ_NAME", data.getString("PRO_PACK_NAME"));
                data.put("FEE", data.getString("PRO_PACK_PRICE"));
                data.put("USAGE_DESC", "");
                data.put("SP_CODE", "400089"); // 产品包没有SP_CODE,填入默认值
                bizList.put(data.getString("BIZ_CODE"), data);

                Object temp = data.get("PRO_PACK_AREA_CODE");
                IDataset bizareas = this.objToIDataset(temp);
                if (bizareas.size() > 0)
                {
                    for (Object area : bizareas)
                    {
                        String areaCode = area.toString();
                        if (areaCode == null || areaCode.length() == 0)
                        {
                            continue;
                        }
                        IData bizArea = new DataMap();
                        bizArea.putAll(data);
                        bizArea.put("BIZ_TYPE_CODE", "27");
                        bizArea.put("BIZ_LEAVE", "2");
                        bizArea.put("AREA_CODE", areaCode);
                        bizAreas.add(bizArea);
                    }
                }

                temp = data.get("PRO_AREA_CODE");
                IDataset subbizareas = this.objToIDataset(temp);
                if (subbizareas.size() > 0)
                {
                    for (Object area : subbizareas)
                    {
                        String areaCode = area.toString();
                        if (areaCode == null || areaCode.length() == 0)
                        {
                            continue;
                        }
                        IData bizArea = new DataMap();
                        bizArea.putAll(subBiz);
                        bizArea.put("BIZ_TYPE_CODE", "27");
                        bizArea.put("BIZ_LEAVE", "1");
                        bizArea.put("AREA_CODE", areaCode);
                        subBizAreas.add(bizArea);
                    }
                }
            }
            else
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_32);
            }

            if (subBiz.size() > 0)
            {
                subBiz.put("PACKAGE_ID", subBiz.getString("PRO_PACK_CODE"));
                subBiz.put("SP_SVC_ID", subBiz.getString("BIZ_CODE"));
                subBiz.put("CP_ID", subBiz.getString("SP_CODE"));
                subBiz.put("PACKAGE_TYPE", type);
                subBiz.put("PACKAGE_NAME", data.getString("PRO_PACK_NAME"));
                subBiz.put("PACKAGE_AREA_CODE", "");
                subBiz.put("PACKAGE_DESC", subBiz.getString("BIZ_NAME"));
                String desc = subBiz.getString("USAGE_DESC", "");
                subBiz.put("RSRV_STR5", desc.length() < 100 ? desc : desc.substring(0, 99));
                subBiz.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                subBiz.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                subBiz.put("UPDATE_TIME", SysDateMgr.getSysTime());

                bean.modRinpProPackInfo(subBiz);
            }

            bean.modRinpBizArea(subBizAreas);
            bean.modRinpBizArea(bizAreas);
        }

        if (bizList.size() > 0)
        {
            IDataset list = new DatasetList();
            java.util.Iterator keySet = bizList.keySet().iterator();
            while (keySet.hasNext())
            {
                String key = (String) keySet.next();
                IData biz = bizList.getData(key);
                list.add(biz);
            }
            PlatOfficeDataBean.importSpBiz(null, list, "IBOSS");
        }

        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "Trade Ok!");

        return result;
    }

    /**
     * 局数据同步SP企业信息接口,农信通特有
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IData rinpSpInfoSync(IData param) throws Exception
    {
        initParam();

        IData result = new DataMap();

        IDataset paramset = new DatasetList();
        paramset.add(param);
        for (Object obj : paramset)
        {
            IData data = this.parserParam((IData) obj, "RINPB998_RINP0999_1_0");

            IDataset list = new DatasetList();
            String spStatus = data.getString("SP_STATUS", "");
            if (spStatus.length() == 0)
            {
                String operCode = data.getString("OPER_TYPE", "");
                if (StringUtils.equals(operCode, "01"))
                {// 01－增加：本业务为新增业务；
                    data.put("SP_STATUS", "A"); // 规范无这个字段，填默认值
                }
                else if (StringUtils.equals(operCode, "02"))
                {// 02－终止：终止本业务；
                    data.put("SP_STATUS", "E"); // 规范无这个字段，填默认值
                }
                else if (StringUtils.equals(operCode, "03"))
                {// 03－暂停：本业务暂停；
                    data.put("SP_STATUS", "N"); // 规范无这个字段，填默认值
                }
                else if (StringUtils.equals(operCode, "04") || StringUtils.equals(operCode, "05"))
                {// 04－恢复：恢复本业务,05－变更
                    data.put("SP_STATUS", "A"); // 规范无这个字段，填默认值
                }
            }
            list.add(data);
            PlatOfficeDataBean.importSpInfo(null, list, "IBOSS");

        }
        // 处理成功
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "Trade Ok!");
        return result;
    }

    /**
     * 局数据同步SP业务信息接口
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset spBizSync(IData paramSet) throws Exception
    {
        IData result = new DataMap();

        initParam();// 初始化默认参数

        IDataset list = new DatasetList();
        IDataset attrList = new DatasetList();

        IDataset paramList = new DatasetList();
        paramList.add(paramSet);

        for (Object obj : paramList)
        {
            IData param = (IData) obj;
            IData data = null;
            String kind_id = param.getString("KIND_ID");
            Object infoCode = param.remove("INFO_CODE");
            Object infoValue = param.remove("INFO_VALUE");

            if (StringUtils.equals(kind_id, "BIP4B224_T4121202_1_0"))
            {// 手机地图业务信息
                data = this.parserParam(param, "BIP4B224_T4121202_1_0");
                data.put("BIZ_TYPE_CODE", "20");
                String bill_flag = data.getString("BILL_FLAG", "");

                if (StringUtils.equals(bill_flag, "按次") || StringUtils.equals(bill_flag, "1"))
                {
                    data.put("BILL_FLAG", "1");
                }
                else if (StringUtils.equals(bill_flag, "包月") || StringUtils.equals(bill_flag, "2"))
                {
                    data.put("BILL_FLAG", "2");
                }
                else if (StringUtils.equals(bill_flag, "免费") || StringUtils.equals(bill_flag, "0"))
                {
                    data.put("BILL_FLAG", "0");
                }
                String biz_desc = data.getString("BIZ_DESC", "");
                if (biz_desc == null || StringUtils.equals(biz_desc, ""))
                {
                    data.put("BILL_FLAG", data.getString("BIZ_NAME", ""));
                }
            }
            else if (StringUtils.equals(kind_id, "BIP4B216_T4121202_1_0"))
            {// 北京通用下载
                data = this.parserParam(param, "BIP4B216_T4121202_1_0");
                String bill_flag = data.getString("BILL_FLAG", "");
                data.put("BIZ_TYPE_CODE", "17");
                if (StringUtils.equals(bill_flag, "按次") || StringUtils.equals(bill_flag, "1"))
                {
                    data.put("BILL_FLAG", "1");
                }
                else if (StringUtils.equals(bill_flag, "包月") || StringUtils.equals(bill_flag, "2"))
                {
                    data.put("BILL_FLAG", "2");
                }
                else if (StringUtils.equals(bill_flag, "免费") || StringUtils.equals(bill_flag, "0"))
                {
                    data.put("BILL_FLAG", "0");
                }
                String biz_desc = data.getString("BIZ_DESC", "");
                if (biz_desc == null || StringUtils.equals(biz_desc, ""))
                {
                    data.put("BILL_FLAG", data.getString("BIZ_NAME", ""));
                }
            }
            else if (StringUtils.equals(kind_id, "BIP4B220_T4121202_1_0"))
            {// 广东通用下载BIP4B220_T4121202_1_0
                data = this.parserParam(param, "BIP4B220_T4121202_1_0");
                data.put("BIZ_TYPE_CODE", "17");
                String bill_flag = data.getString("BILL_FLAG", "");

                if (StringUtils.equals(bill_flag, "按次") || StringUtils.equals(bill_flag, "1"))
                {
                    data.put("BILL_FLAG", "1");
                }
                else if (StringUtils.equals(bill_flag, "包月") || StringUtils.equals(bill_flag, "2"))
                {
                    data.put("BILL_FLAG", "2");
                }
                else if (StringUtils.equals(bill_flag, "免费") || StringUtils.equals(bill_flag, "0"))
                {
                    data.put("BILL_FLAG", "0");
                }
                String biz_desc = data.getString("BIZ_DESC", "");
                if (biz_desc == null || StringUtils.equals(biz_desc, ""))
                {
                    data.put("BILL_FLAG", data.getString("BIZ_NAME", ""));
                }
            }
            else if (StringUtils.equals(kind_id, "BIP4B212_T4121202_1_0"))
            {// 手机邮箱 没配td_s_static
                data = this.parserParam(param, "BIZ_INFO_KEY");
                data.put("BIZ_TYPE_CODE", "16");
                String biz_desc = data.getString("BIZ_DESC", "");
                if (biz_desc == null || StringUtils.equals(biz_desc, ""))
                {
                    data.put("BILL_FLAG", data.getString("BIZ_NAME", ""));
                }
            }
            else if (StringUtils.equals(kind_id, "BIP4B241_T4101012_1_0"))
            {// 流媒体
                data = this.parserParam(param, "BIP4B241_T4101012_1_0");
                data.put("BIZ_TYPE_CODE", "14");
                data.put("BILL_FLAG", data.getString("BILL_TYPEE", ""));
                data.put("FEE", data.getString("PRICE", ""));
                String biz_desc = data.getString("BIZ_DESC", "");
                if (biz_desc == null || StringUtils.equals(biz_desc, ""))
                {
                    data.put("BIZ_DESC", data.getString("BIZ_NAME", ""));
                }
            }
            else if (StringUtils.equals(kind_id, "RINPB999_RINP0999_1_0"))
            {// 农信通资费添加由分转化为“0”
                data = this.parserParam(param, "RINPB999_RINP0999_1_0");
                String biz_desc = data.getString("BIZ_DESC", "");
                String bill_flag = data.getString("BILL_FLAG", "");
                if (StringUtils.equals(bill_flag, "包月") || StringUtils.equals(bill_flag, "2"))
                {
                    data.put("FEE", data.getString("PRICE", "") + "0");
                }
                if (biz_desc == null || StringUtils.equals(biz_desc, ""))
                {
                    data.put("BIZ_DESC", data.getString("BIZ_NAME", ""));
                }
            }
            else
            {
                data = this.parserParam(param, "BIZ_INFO_KEY");
                String biz_desc = data.getString("BIZ_DESC", "");
                if (biz_desc == null || StringUtils.equals(biz_desc, ""))
                {
                    data.put("BIZ_DESC", data.getString("BIZ_NAME", ""));
                }
            }

            String bizStatus = data.getString("BIZ_STATUS", "");
            if (bizStatus.length() == 0)
            {
                String operCode = data.getString("OPER_TYPE", "");
                if (StringUtils.equals(operCode, "01"))
                { // 01－增加：本业务为新增业务；
                    data.put("BIZ_STATUS", "A"); // 规范无这个字段，填默认值
                }
                else if (StringUtils.equals(operCode, "02"))
                { // 02－终止：终止本业务；
                    data.put("BIZ_STATUS", "E"); // 规范无这个字段，填默认值
                }
                else if (StringUtils.equals(operCode, "03"))
                { // 03－暂停：本业务暂停；
                    data.put("BIZ_STATUS", "N"); // 规范无这个字段，填默认值
                }
                else if (StringUtils.equals(operCode, "04") || StringUtils.equals(operCode, "05"))
                {// 04－恢复：恢复本业务,05－变更
                    data.put("BIZ_STATUS", "A"); // 规范无这个字段，填默认值
                }
                else
                {
                    data.put("BIZ_STATUS", "A"); // 默认填"A"正常
                }
            }

            IDataset infoCodes = this.objToIDataset(infoCode);
            IDataset infoValues = this.objToIDataset(infoValue);

            if (infoCodes.size() > 0 && infoCodes.size() == infoValues.size())
            {

                for (int i = 0; i < infoCodes.size(); i++)
                {
                    IData bizAttr = new DataMap();
                    bizAttr.put("SP_CODE", data.getString("SP_CODE"));
                    bizAttr.put("BIZ_CODE", data.getString("BIZ_CODE"));
                    bizAttr.put("INFO_CODE", infoCodes.get(i));
                    bizAttr.put("INFO_VALUE", infoCodes.get(i));

                    attrList.add(bizAttr);
                }
            }

            list.add(data);
        }

        PlatOfficeDataBean.importSpBiz(null, list, "IBOSS");
        if (attrList.size() > 0)
        {
            PlatBureDataIntfBean bean = new PlatBureDataIntfBean();
            bean.saveBizInfoAttr(attrList);
        }

        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "Trade Ok!");
        IDataset rs = new DatasetList();
        rs.add(result);
        return rs;
    }

    /**
     * 局数据同步SP企业信息接口
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset spInfoSync(IData paramSet) throws Exception
    {
        initParam();

        IData result = new DataMap();

        IDataset list = new DatasetList();
        IDataset paramList = new DatasetList();
        IDataset attrList = new DatasetList();

        paramList.add(paramSet);

        for (Object obj : paramList)
        {
            IData param = (IData) obj;
            IData data = null;

            String kind_id = param.getString("KIND_ID");
            Object infoCode = param.remove("INFO_CODE");
            Object infoValue = param.remove("INFO_VALUE");

            if (StringUtils.equals(kind_id, "BIP4B229_T4121205_1_0"))
            {// 手机钱包 商户信息同步
                data = this.parserParam(param, "BIP4B229_T4121205_1_0");
            }
            else if (StringUtils.equals(kind_id, "BIP4B222_T4121201_1_0"))
            {// 手机地图 没配td_s_static
                data = this.parserParam(param, "BIP4B222_T4121201_1_0");
            }
            else if (StringUtils.equals(kind_id, "BIP4B210_T4121201_1_0"))
            {// 手机邮箱
                data = this.parserParam(param, "BIP4B210_T4121201_1_0");
            }
            else if (StringUtils.equals(kind_id, "BIP4B214_T4121201_1_0"))
            {// 北京通用下载
                data = this.parserParam(param, "BIP4B214_T4121201_1_0");
            }
            else if (StringUtils.equals(kind_id, "BIP4B218_T4121201_1_0"))
            {// 广东通用下载
                data = this.parserParam(param, "BIP4B218_T4121201_1_0");
            }
            else if (StringUtils.equals(kind_id, "BIP4B210_T4121201_1_0"))
            {
                data = this.parserParam(param, "BIP4B210_T4121201_1_0");
            }
            else
            {
                data = this.parserParam(param, "SP_INFO_KEY");
            }
            data.put("SERV_CODE", "");// DSMP规范特有
            String spStatus = data.getString("SP_STATUS", "");
            if (spStatus.length() == 0)
            {
                String operCode = data.getString("OPER_TYPE", "");
                if (StringUtils.equals(operCode, "01"))
                {// 01－增加：本业务为新增业务；
                    data.put("SP_STATUS", "A"); // 规范无这个字段，填默认值
                }
                else if (StringUtils.equals(operCode, "02"))
                {// 02－终止：终止本业务；
                    data.put("SP_STATUS", "E"); // 规范无这个字段，填默认值
                }
                else if (StringUtils.equals(operCode, "03"))
                {// 03－暂停：本业务暂停；
                    data.put("SP_STATUS", "N"); // 规范无这个字段，填默认值
                }
                else if (StringUtils.equals(operCode, "04") || StringUtils.equals(operCode, "05"))
                {// 04－恢复：恢复本业务,05－变更
                    data.put("SP_STATUS", "A"); // 规范无这个字段，填默认值
                }
                else
                {
                    data.put("SP_STATUS", "A"); // 默认填"A"正常
                }
            }

            list.add(data);

            IDataset infoCodes = this.objToIDataset(infoCode);
            IDataset infoValues = this.objToIDataset(infoValue);

            if (infoCodes.size() > 0 && infoCodes.size() == infoValues.size())
            {

                for (int i = 0; i < infoCodes.size(); i++)
                {
                    IData spAttr = new DataMap();
                    spAttr.put("SP_CODE", data.getString("SP_CODE"));
                    spAttr.put("INFO_CODE", infoCodes.get(i));
                    spAttr.put("INFO_VALUE", infoValues.get(i));
                    spAttr.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    spAttr.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    spAttr.put("UPDATE_TIME", SysDateMgr.getSysTime());

                    attrList.add(spAttr);
                }
            }
        }

        PlatOfficeDataBean.importSpInfo(null, list, "IBOSS");

        if (attrList.size() > 0)
        {
            PlatBureDataIntfBean bean = new PlatBureDataIntfBean();
            bean.saveSpInfoAttr(attrList);
        }

        // 处理成功
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "Trade Ok!");

        IDataset rs = new DatasetList();
        rs.add(result);
        return rs;
    }
}

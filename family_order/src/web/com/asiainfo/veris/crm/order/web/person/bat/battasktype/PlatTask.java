
package com.asiainfo.veris.crm.order.web.person.bat.battasktype;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class PlatTask extends PersonBasePage
{

    /**
     * @Function: batPlatForm
     * @Description: 批量业务平台业务
     * @param：
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: xiangyc@asiainfo-linkage.com
     * @date: 下午4:46:33 2014-3-13 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-13 xiangyc v1.0.0
     */
    private void batPlatForm(IData params) throws Exception
    {
        String clickTag = params.getString("CLICK_TAG", "");
        String spCode = params.getString("SP_CODE", "");
        String bizCode = params.getString("BIZ_CODE", "");
        String bizTypeCode = params.getString("BIZ_TYPE_CODE", "");
        String bizProcessTag = "";
        IDataset operInfos = new DatasetList();
        IData data = new DataMap();
        data.put("START_DATE1", SysDateMgr.getSysTime());
        setInfo(data);
        if (StringUtils.isNotBlank(clickTag) && clickTag.equals("QUERY_OPER_INFO"))
        {
            IData param = new DataMap();
            param.clear();
            param.put("SP_CODE", spCode);
            if (bizCode.contains("#"))
            {
                bizCode = bizCode.replace("#", "+");
            }
            param.put("BIZ_CODE", bizCode);
            param.put("BIZ_TYPE_CODE", bizTypeCode);
            operInfos = com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall.call(this, "CS.BatDealSVC.getOperTypeBySpAndBiz", param);
            if (IDataUtil.isNotEmpty(operInfos))
            {
                bizProcessTag = ((IData) (operInfos.get(0))).getString("BIZ_PROCESS_TAG");
                operInfos = getOperType(bizProcessTag);
                if (StringUtils.isNotBlank(bizTypeCode))
                {
                    // PIMM批量只有注册和注销
                    if ("10".equals(bizTypeCode))
                    {
                        IData remove = new DataMap();
                        for (int i = 0; i < operInfos.size(); i++)
                        {
                            remove = (IData) operInfos.get(i);
                            if (!remove.getString("OPER_CODE").equals("01") && !remove.getString("OPER_CODE").equals("02"))
                            {
                                operInfos.remove(remove);
                                i--;
                            }
                        }
                    }
                    // DSMP
                    if ("03".equals(bizTypeCode) || "04".equals(bizTypeCode) || "05".equals(bizTypeCode))
                    {
                        IData remove = new DataMap();
                        for (int i = 0; i < operInfos.size(); i++)
                        {
                            remove = (IData) operInfos.get(i);
                            if (!remove.getString("OPER_CODE").equals("06") && !remove.getString("OPER_CODE").equals("07"))
                            {
                                operInfos.remove(remove);
                                i--;
                            }
                        }
                    }
                }

                setOperations(operInfos);
            }
        }
        setEparchyCode(getVisit().getStaffEparchyCode());
    }

    private IDataset getOperType(String bizProcessTag) throws Exception
    {
        IDataset opers = new DatasetList();
        for (int i = 0; i < bizProcessTag.length(); i++)
        {
            String j = bizProcessTag.charAt(i) + "";

            if (!j.equals("1"))
            {
                continue;
            }
            IData oper = new DataMap();
            switch (i)
            {

                case 0:
                    oper.put("OPER_CODE", "01");
                    oper.put("OPER_TYPE", "用户注册");// --第一位 del

                    break;
                case 1:
                    oper.put("OPER_CODE", "02");
                    oper.put("OPER_TYPE", "用户注销");// --第二位 del

                    break;
                case 2:
                    oper.put("OPER_CODE", "03");
                    oper.put("OPER_TYPE", "密码修改");// --第三位

                    break;
                case 3:
                    oper.put("OPER_CODE", "04");
                    oper.put("OPER_TYPE", "业务暂停");// --第四位

                    break;
                case 4:
                    oper.put("OPER_CODE", "05");
                    oper.put("OPER_TYPE", "业务恢复");// --第五位

                    break;
                case 5:
                    oper.put("OPER_CODE", "06");
                    oper.put("OPER_TYPE", "订购");// --第六位

                    break;
                case 6:
                    oper.put("OPER_CODE", "07");
                    oper.put("OPER_TYPE", "退订");// 第七位

                    break;
                case 7:
                    oper.put("OPER_CODE", "08");
                    oper.put("OPER_TYPE", "用户资料变更");// 第八位

                    break;
                case 8:
                    oper.put("OPER_CODE", "11");
                    oper.put("OPER_TYPE", "赠送");// 第九位 del

                    break;
                case 9:
                    oper.put("OPER_CODE", "14");
                    oper.put("OPER_TYPE", "用户主动暂停");// 第十位 del

                    break;
                case 10:
                    oper.put("OPER_CODE", "15");
                    oper.put("OPER_TYPE", "用户主动恢复");// 十一位 del

                    break;
                case 11:
                    oper.put("OPER_CODE", "90");
                    oper.put("OPER_TYPE", "服务开关开");// 第十二位

                    break;
                case 12:
                    oper.put("OPER_CODE", "91");
                    oper.put("OPER_TYPE", "服务开关关");// 十三位

                    break;
                case 13:
                    oper.put("OPER_CODE", "89");
                    oper.put("OPER_TYPE", "SP全退订");// 第十四位 --直接写历史表，完工处理？ del

                    break;
                case 14:
                    oper.put("OPER_CODE", "97");
                    oper.put("OPER_TYPE", "全业务恢复");// --第十伍位 --直接写历史表，完工处理？ del

                    break;
                case 15:
                    oper.put("OPER_CODE", "98");
                    oper.put("OPER_TYPE", "全业务暂停");// --第十六位 del

                    break;
                case 16:
                    oper.put("OPER_CODE", "99");
                    oper.put("OPER_TYPE", "全业务退订");// --第十七位 del

                    break;
                case 17:
                    oper.put("OPER_CODE", "14");
                    oper.put("OPER_TYPE", "点播");// --第十八位

                    break;
                case 18:
                    oper.put("OPER_CODE", "16");
                    oper.put("OPER_TYPE", "充值"); // --第十九位 编码待定
                    break;
                case 19:
                    oper.put("OPER_CODE", "17");
                    oper.put("OPER_TYPE", "预约"); // --第二十位
                    break;
                case 20:
                    oper.put("OPER_CODE", "18");
                    oper.put("OPER_TYPE", "预约取消"); // --第二十一位
                    break;
                case 21:
                    oper.put("OPER_CODE", "19");
                    oper.put("OPER_TYPE", "挂失"); // --第二十二位 编码待定

                    break;
                case 22:
                    oper.put("OPER_CODE", "20");
                    oper.put("OPER_TYPE", "解挂"); // --第二十三位 编码待定

                    break;
                case 23:
                    oper.put("OPER_CODE", "10");
                    oper.put("OPER_TYPE", "套餐订购"); // --第二十四位

                    break;
                case 24:
                    oper.put("OPER_CODE", "11");
                    oper.put("OPER_TYPE", "套餐退订"); // --第二十五位

                    break;
                case 25:
                    oper.put("OPER_CODE", "09");
                    oper.put("OPER_TYPE", "密码重置"); // --第二十六位

                    break;
                case 26:
                    oper.put("OPER_CODE", "12");
                    oper.put("OPER_TYPE", "套餐变更"); // --第二十七位

                    break;
                case 29:
                    oper.put("OPER_CODE", "88");
                    oper.put("OPER_TYPE", "套餐变更"); // --第三十位 del
                    break;
                default:
                    ;
            }

            opers.add(oper);
        }
        return opers;
    }

    public void initBatPopuPages(IRequestCycle cycle) throws Exception
    {
        IData editInfo = new DataMap();
        IData params = new DataMap();
        params = getData();
        if (StringUtils.isNotBlank(params.getString("DATA")))
        {
            IData cond = new DataMap(params.getString("DATA"));
            cond.put("CLICK_TAG", "QUERY_OPER_INFO");
            params.putAll(cond);
        }
        batPlatForm(params);
        editInfo.put("BATCH_TASK_NAME", "22222222321321");
        setEditInfo(editInfo);

        setInfo(params);
    }

    public abstract void setEditInfo(IData editinfo);

    public abstract void setEparchyCode(String eparchyCode);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setListA(IDataset listA);

    public abstract void setListB(IDataset listB);

    public abstract void setListC(IDataset listC);

    public abstract void setListD(IDataset listD);

    public abstract void setOperations(IDataset operations);

    public abstract void setPackageId(String packageId);

    public abstract void setResId(String resId);
}

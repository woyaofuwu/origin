
package com.asiainfo.veris.crm.iorder.web.merch.component.plat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

public abstract class UserPlatSvcsList extends CSBizTempComponent
{
    public static IDataset getOperType(String bizProcessTag) throws Exception
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

    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        // TODO Auto-generated method stub
        super.cleanupAfterRender(cycle);
        this.setUserPlatSvcList(null);
    }

    public IDataset parseOperCode(IDataset operTypes, String bizStateCode)
    {
        IDataset result = new DatasetList();
        if (operTypes != null && operTypes.size() > 0)
        {
            String supportOperCode = "";
            if (PlatConstants.STATE_PAUSE.equals(bizStateCode))
            {
                // 暂停状态

                supportOperCode = "02_07_05_15";
            }
            else if (PlatConstants.STATE_LOSE.equals(bizStateCode))
            {
                // 挂失状态

                supportOperCode = "02_07_20";
            }
            else if (PlatConstants.STATE_OK.equals(bizStateCode))
            {
                // 正常状态
                supportOperCode = "03_04_07_08_09_10_11_12_13_14_15_16_17_18_19";
            }

            int size = operTypes.size();
            for (int i = 0; i < size; i++)
            {
                IData oper = operTypes.getData(i);
                if ("".equals(supportOperCode) && "01_06".indexOf(oper.getString("OPER_CODE")) < 0)
                {
                    result.add(oper);
                }
                else if (supportOperCode.indexOf(oper.getString("OPER_CODE")) >= 0)
                {
                    result.add(oper);
                }
            }
        }
        return result;
    }

    @Override
    public void renderComponent(StringBuilder stringbuilder, IMarkupWriter writer, IRequestCycle irequestcycle) throws Exception
    {
        this.getPage().addResBeforeBodyEnd("scripts/merch/plat/userplatsvcslist.js");
        IData param = this.getPage().getData();
        this.setDisableOperation(param.getString("DISABLE_OPERATION"));

        if (!param.getString("IS_ELEMENT", "").equals("true"))
        {
            if (param.getString("USER_ID", "").equals(""))
            {
                return;
            }
            else
            {
                IDataset result = CSViewCall.call(this, "CS.PlatComponentSVC.getUserPlatSvcs", param);
                
                if (result != null && result.size() > 0)
                {
                	//查询用户是否办理互联网电视服务信息
                	IDataset internetTvSvc = CSViewCall.call(this, "SS.DestroyTopSetBoxSVC.queryUserInternetTvPlatSvc", param);
                	IData checkContainer=null;
                	if(IDataUtil.isEmpty(internetTvSvc)){
                		checkContainer=new DataMap();
                	}else{
                		checkContainer=internetTvSvc.first();
                	}
                	
                	
                    int size = result.size();
                    IDataset selectedElements = new DatasetList();
                    for (int i = 0; i < size; i++)
                    {
                        IData element = new DataMap();
                        IData map = result.getData(i);
                        
                        map.put("ITEM_INDEX", i);
                        element.put("SERVICE_ID", map.getString("SERVICE_ID"));
                        element.put("BIZ_STATE_CODE", map.getString("BIZ_STATE_CODE"));
                        element.put("INST_ID", map.getString("INST_ID"));
                        element.put("MODIFY_TAG", "exist");
                        element.put("ATTR_PARAM", map.getDataset("ATTR_PARAM"));
                        element.put("SP_CODE", map.getString("SP_CODE"));
                        element.put("BIZ_TYPE_CODE", map.getString("BIZ_TYPE_CODE"));
                        element.put("ORG_DOMAIN", map.getString("ORG_DOMAIN"));
                        element.put("SP_NAME", map.getString("SP_NAME"));
                        
                        IDataset operTypes = this.getOperType(map.getString("BIZ_PROCESS_TAG"));
                        specialDealOperCode(operTypes, map.getString("BIZ_TYPE_CODE"));
                        IDataset supportOpers = this.parseOperCode(operTypes, map.getString("BIZ_STATE_CODE"));
                        map.put("SUPPORT_OPERS", supportOpers);
                        map.put("INIT_OPER_CODE", supportOpers.size()==1 ? supportOpers.getData(0).getString("OPER_CODE"):"07");
                        
                        //如果包含了互联网电视的服务
                        if(checkContainer!=null&&checkContainer.containsKey(map.getString("SERVICE_ID"))){
                        	element.put("INTERNET_TV_SVC", "1");
                        }else{
                        	element.put("INTERNET_TV_SVC", "0");
                        }
                        if("98001901".equals(map.getString("SERVICE_ID")) && "19".equals(map.getString("BIZ_TYPE_CODE"))){
                            IDataset DiscntsCode = CSViewCall.call(this, "CS.UserDiscntInfoQrySVC.queryDiscntsCodeByusrid", param);
                            if(!IDataUtil.isEmpty(DiscntsCode)){
                                String Code = DiscntsCode.getData(0).getString("DISCNT_CODE");
                                if("1237".equals(Code)){
                                    result.getData(i).put("PRICE", "0.0");
                                }else if("1238".equals(Code)){
                                    result.getData(i).put("PRICE", "5.0");
                                }else if("12789".equals(Code)){
                                    result.getData(i).put("PRICE", "6.0");
                                }
                            }
                        }
                        
                        selectedElements.add(element);
                    }
                    this.getPage().setAjax(selectedElements);
                }
                this.setUserPlatSvcList(result);
            }
        }
        else
        {
            // 元素添加
            String serviceId = param.getString("SERVICE_ID");
            IData map = new DataMap();
            map.put("SERVICE_ID", serviceId);
            IDataset results = CSViewCall.call(this, "CS.PlatComponentSVC.getPlatSvcByServiceId", map);
            
            if (IDataUtil.isEmpty(results))
            {
                CSViewException.apperr(PlatException.CRM_PLAT_74, "根据平台服务 ID，查询不到对应的平台服务，可能原因是搜索服务配置的数据库地址与APP配置不一致造成");
            }

            IData service = results.getData(0);
            
            /**校验该服务是否为过渡期服务  mod:zhangbo18 begin*/
            map.clear();
            map.put("OLD_SP_CODE", service.get("SP_CODE"));
            map.put("OLD_BIZ_CODE", service.get("BIZ_CODE"));
            IDataset offData = CSViewCall.call(this, "CS.PlatComponentSVC.getOffData", map);
            //如果存在配置数据，说明此服务为过渡服务，不能进行订购
            if (null != offData && offData.size() > 0){
            	CSViewException.apperr(PlatException.CRM_PLAT_90);
            }
            
            map.clear();
            map.put("NEW_SP_CODE", service.get("SP_CODE"));
            map.put("NEW_BIZ_CODE", service.get("BIZ_CODE"));
            offData = CSViewCall.call(this, "CS.PlatComponentSVC.getNewOffData", map);
            //如果存在配置数据，说明此服务为过渡服务，需要检查是否存在老服务的订购关系
            if (null != offData && offData.size() > 0){
            	IData officeD = offData.getData(0);
            	map.clear();
                map.put("SP_CODE", officeD.getString("OLD_SP_CODE"));
                map.put("BIZ_CODE", officeD.getString("OLD_BIZ_CODE"));
                map.put("BIZ_TYPE_CODE", officeD.getString("OLD_BIZ_TYPE"));
                offData = CSViewCall.call(this, "CS.UserPlatSvcInfoQrySVC.querySvcAllBySpCodeAndBizCode", map);
                
                if (null != offData && offData.size() > 0){
                	officeD = offData.getData(0);
                	param.put("SERVICE_ID", officeD.getString("SERVICE_ID", officeD.getString("OFFER_CODE")));
                	offData = CSViewCall.call(this, "CS.UserPlatSvcInfoQrySVC.querySvcInfoByUserIdAndSvcIdPf", param);
                	
                	if (null != offData && offData.size() > 0){
                    	CSViewException.apperr(PlatException.CRM_PLAT_91);
                	}
                }
            }

            /**校验该服务是否为过渡期服务  mod:zhangbo18 end*/


            if("74".equals(service.getString("BIZ_TYPE_CODE"))){
            	CSViewException.apperr(PlatException.CRM_PLAT_3032);
            }
            //和教育不允许订购 end
            
            if ("17".equals(service.getString("BIZ_TYPE_CODE")))
            {
                if ("BJDP".equals(service.getString("ORG_DOMAIN")))
                {
                    service.put("BIZ_TYPE_NAME", "北京通用下载");
                }
                else
                {
                    service.put("BIZ_TYPE_NAME", "广东通用下载");
                }
            }
            else
            {
                service.put("BIZ_TYPE_NAME", this.getPageUtil().getStaticValue("BIZ_TYPE_CODE", service.getString("BIZ_TYPE_CODE")));
            }
            IDataset opers = new DatasetList();

            // 查询TD_B_PLATSVC.RSRV_STR3，如果是0,2允许订购，其他不允许订购
            String registerOper = service.getString("BIZ_PROCESS_TAG", "000000000000000000000000000000").substring(0, 1);// 第一位注册
            String orderOper = service.getString("BIZ_PROCESS_TAG", "000000000000000000000000000000").substring(5, 6);// 第6位订购
            // 如果订购和注册都不支持，则抛异常
            if (!"1".equals(registerOper) && !"1".equals(orderOper))
            {
                CSViewException.apperr(PlatException.CRM_PLAT_84);
            }

            String rsrvStr4 = service.getString("RSRV_STR4", "");
            String staffIdChar = String.valueOf(getVisit().getStaffId().charAt(4));
            if (!"".equals(rsrvStr4) && rsrvStr4.indexOf(staffIdChar) >= 0)
            {
                CSViewException.apperr(PlatException.CRM_PLAT_86);
            }

            IData oper = new DataMap();
            oper.put("OPER_CODE", "06");
            oper.put("OPER_NAME", "订购");
            opers.add(oper);

            if ("DSMP".equals(service.getString("ORG_DOMAIN")) || "MRBT".equals(service.getString("ORG_DOMAIN")) || "MEBP".equals(service.getString("ORG_DOMAIN")))
            {
                IData giftOper = new DataMap();
                giftOper.put("OPER_CODE", "GIFT");
                giftOper.put("OPER_NAME", "赠送");
                opers.add(giftOper);
            }
            service.put("SUPPORT_OPERS", opers);
            service.put("OPER_NAME", "订购");
            service.put("BIZ_STATE", "正常");
            service.put("START_DATE", SysDateMgr.getSysDate());
            service.put("END_DATE", SysDateMgr.getTheLastTime());
            service.put("SERVICE_ID", serviceId);
            service.put("IS_HOT", param.getString("IS_HOT"));
            this.getPage().setAjax(service);
            this.setRenderContent(false);
        }
    }

    public abstract void setDisableOperation(String disableOperation);

    public abstract void setUserPlatSvcList(IDataset userPlatSvcList);

    private void specialDealOperCode(IDataset operTypes, String bizTypeCode)
    {
        if (PlatConstants.PLAT_WLAN.equals(bizTypeCode) || PlatConstants.PLAT_MOBILEPHONE_EMAIL.equals(bizTypeCode))
        {
            for (int i = 0; i < operTypes.size(); i++)
            {
                IData operType = operTypes.getData(i);
                if (operType.getString("OPER_CODE", "").equals("16"))
                {
                    operTypes.remove(i);
                }
            }
        }
    }
    
}

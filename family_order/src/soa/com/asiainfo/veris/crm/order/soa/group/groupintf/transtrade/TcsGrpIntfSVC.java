
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossIAGWCloudMASDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.GrpBBossIntf;
import com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade.StaffTicketQryIntf;

public class TcsGrpIntfSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    /**
     * 受理集团V网营销活动
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset vpmnSaleActive(IData inparam) throws Exception
    {
        return VpmnSaleActiveIntf.vpmnSaleActive(inparam);
    }

    /**
     * 集团产品管理员处理接口
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset processGrpProductMgr(IData inparam) throws Exception
    {
        return ProcessGrpProductMgrIntf.processGrpProductMgr(inparam);
    }

    /**
     * 增值税申请单状态回写接口
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset changeApproveReceipt(IData inParam) throws Exception
    {
        return GrpTaxIntf.changeApproveReceipt(inParam);
    }

    /***************************** IMS受理开始 ************************************/
    /*
     * @description 一号通管理
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperMebYhtNum(IData data) throws Exception
    {
        return GrpIMSDealIntf.changeYhtMemElement(data);
    }

    /*
     * @description 修改集团用户资费或参数信息
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperGrpUserElement(IData data) throws Exception
    {
        return GrpIMSDealIntf.OperGrpUserAttr(data);
    }

    /*
     * @description 修改成员用户的资费
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperModifyMebDiscnt(IData data) throws Exception
    {
        return GrpIMSDealIntf.OperModifyMebDiscnt(data);
    }

    /*
     * @description 成员个性化参数
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperModifyMebAttr(IData data) throws Exception
    {
        return GrpIMSDealIntf.OperModifyMebAttr(data);
    }

    /*
     * @description 融合产品集团成员新增
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperAddGrpMeb(IData data) throws Exception
    {
        return GrpIMSDealIntf.OperAddGrpMeb(data);
    }

    /*
     * @description 融合产品集团成员退订
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperDelGrpMeb(IData data) throws Exception
    {
        return GrpIMSDealIntf.OperDelGrpMeb(data);
    }

    /*
     * @description 集团黑白名单变更
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperGrpBWList(IData data) throws Exception
    {
        return GrpIMSDealIntf.OperGrpBWList(data);
    }

    /*
     * @description 多媒体桌面电话成员级个人黑白名单管理
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperMebBWList(IData data) throws Exception
    {
        return GrpIMSDealIntf.OperMebBWList(data);
    }

    /*
     * @description IMS用户密码重置
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset ModifyImsMebPassword(IData data) throws Exception
    {
        return GrpIMSDealIntf.ModifyImsMebPassword(data);
    }

    /*
     * @description 修改集团群组
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperGrpTeam(IData data) throws Exception
    {
        return GrpIMSDealIntf.OperGrpTeam(data);
    }

    /*
     * @description 修改成员群组
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperTeamMeb(IData data) throws Exception
    {
        return GrpIMSDealIntf.OperTeamMeb(data);
    }

    /*
     * @description 客户经理业务办理登记
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset CustMgrTjNum(IData data) throws Exception
    {
        return GrpIMSDealIntf.CustMgrTjNum(data);
    }

    /***************************** IMS受理结束 ************************************/

    /**
     * 集团业务包年套餐续订接口
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset dealYearDiscnt(IData inparam) throws Exception
    {
        return YearDiscntDealIntf.dealYearDiscnt(inparam);
    }

    /**
     * 集团成员产品退订接口(支持一到多)
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset destoryGrpMebOrder(IData inparam) throws Exception
    {
        return DestoryGrpMebOrderIntf.destoryGrpMebOrder(inparam);
    }

    // /*
    // * @description 集团一点代付
    // * @author chenyi
    // * @date 22014-6-13
    // */
    // public static IDataset onePayMem(IData data) throws Throwable
    // {
    // return GrpBBossIntf.onePayMem(data);
    // }

    /*
     * @description 集团一点代付,集团行业应用卡反向批量接口
     * @author chenyi
     * @date 22014-6-13
     */
    public static IDataset onePayMem(IData data) throws Exception
    {
        return GrpBBossIntf.batDealBBossRevsMebFile(data);
    }

    /**
     * 异网虚拟同步（海南确认没有，代码保留）
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public static IDataset VirCodeSync(IData data) throws Throwable
    {
        return GrpChangeOutNetSnIntf.VirCodeSync(data);
    }

    /**
     * 删除异网虚拟号码（海南确认没有，代码保留）
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public static IDataset delOutNetsn(IData data) throws Throwable
    {
        return GrpChangeOutNetSnIntf.delOutNetsn(data);
    }

    /**
     * 调IBOSS接口（海南确认没有，代码保留）
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public static IDataset creatOutNetsn(IData data) throws Throwable
    {
        return GrpChangeOutNetSnIntf.creatOutNetsn(data);
    }

    /**
     * 集团彩铃成员处理 给短厅使用
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public static IDataset processGrpCLRMember(IData data) throws Throwable
    {
        return ProcessGrpCLRMember.processGrpMemberInfo(data);
    }

    /**
     * 集团成员退订处理接口 给统一查询退订使用
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public static IDataset processGrpMemCancel(IData data) throws Throwable
    {
        return ProcessGrpMemCancel.processGrpMemberInfo(data);
    }

    /**
     * 集团成员优惠变更处理接口 给统一查询退订使用
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public static IDataset processGrpChgMemDisct(IData data) throws Throwable
    {
        return ProcessGrpChgMemDisct.processGrpMemberInfo(data);
    }

    /**
     * vpn 集团优惠变更 vpn 集团成员注销
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public static IDataset processGrpMemVpmn(IData data) throws Throwable
    {
        return ProcessGrpChgMemDisct.processGrpMemberVpmnInfo(data);
    }

    /*
     * @description ADC MAS成员反向接口
     * @author liaolc
     * @date 2014-07-08
     * @modify by chenkh 2015-05-04
     */
    public static IDataset dealAdcMasMemBiz(IData data) throws Exception
    {
        //如果是bboss行业网关云MAS业务跳转到bboss逻辑
        if (BbossIAGWCloudMASDealBean.isBBossCloudMasFromIntf(data))
        {
            return BbossIAGWCloudMASDealBean.dealBbossCloudMasIntf(data);
        }
        return GrpAdcMasIntf.dealAdcMasMemBiz(data);
    }

    /**
     * 黑白名单成员状态同步(业务受理省):
     * 号码归属省销户时，业务受理省退出所有名单 。
     * 成员黑白名单同步反馈信息处理：注销号码加黑白名单时，业务受理省退出名单
     * @author
     * @param
     * @return
     * @throws Exception
     */
    public static IDataset destroyOutMeb(IData input) throws Exception
    {
        return GrpAdcMasOutMebIntf.destroyOutMeb(input);
    }

    /**
     * 黑白名单成员状态同步:成员黑白名单同步信息（号码归属省）
     * @author
     * @param
     * @return
     * @throws Exception
     */
    public static IDataset synMebBlackWhiteOut(IData input) throws Exception
    {
        return GrpAdcMasOutMebIntf.synMebBlackWhiteOut(input);
    }

    /*
     * @description 学护卡亲情号码反向同步
     * @author liaolc
     * @date 2014/09/23
     */
    public static IDataset xfkFamNumSynAttr(IData data) throws Exception
    {
        return GrpAdcMasIntf.xfkFamNumSynAttr(data);
    }

    public static IDataset processGrpProduct(IData data) throws Exception
    {
        return ProcessGrpMebProduct.tradeGrpReg(data);
    }

    public static IDataset processMemProduct(IData data) throws Exception
    {
        return ProcessGrpMebProduct.tradeMebReg(data);
    }

    /**
     * 更新票据信息接口
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset updateStaffTicket(IData inParam) throws Exception
    {
        return StaffTicketQryIntf.updateStaffTicket(inParam);
    }

   /**
    * 新校讯通0000查询退订接口
    *
    * @param data
    * @return
    * @throws Exception
    */
   public static IDataset destoryXXTMembersByElementId(IData data) throws Exception {
       return GrpAdcMasIntf.destoryXXTMembersByElementId(data);
   }

   /**
    * 集团业务特殊开机办理接口
    * @param data
    * @return
    * @throws Exception
    */                    
   public IData groupSpecialOpen(IData data) throws Exception
   {
       return GroupSpecialOpenintf.groupSpecialOpen(data);
   }
   
   /**
    * 集团自由充成员批量新增操作接口
    * @param data
    * @return
    * @throws Exception
    */
   public static IDataset processGrpGfffMemProduct(IData data) throws Exception
   {
       return GrpGfffIntf.dealGrpGfffAllMem(data);
   }
   
   /**
    * 集团自由充成员变更操作接口
    * @param data
    * @return
    * @throws Exception
    */
   public static IDataset changeGrpGfffMemProduct(IData data) throws Exception
   {
       return GrpGfffIntf.changeGrpGfffAllMem(data);
   }
   
   /**
    * 流量自由充产品成员批量退订接口，网厅使用
    * @param data
    * @return
    * @throws Exception
    */
   public static IDataset batDestroyGrpGfffMemProduct(IData data) throws Exception
   {
       return DestoryGrpGfffMebOrderIntf.batDestroyGrpGfffMemProduct(data);
   }
   
   /**
    * 流量自由充产品成员叠加包批量办理接口，网厅使用
    * @param data
    * @return
    * @throws Exception
    */                    
   public IDataset batGrpOrderGfffMemSuperposition(IData data) throws Exception
   {
       return GrpGfffMemSuperpositionIntf.batGrpOrderGfffMemSuperposition(data);
   }
   
   /**
    * 集团畅享流量批量新增操作接口
    * @param data
    * @return
    * @throws Exception
    */
   public static IDataset BatAddLargessFluxDiscnt(IData data) throws Exception
   {
       return GrpLargessIntf.BatAddLargessFluxDiscnt(data);
   }
   
}

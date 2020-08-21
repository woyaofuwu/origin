
package com.asiainfo.veris.crm.order.soa.group.imsmanage.outgrptrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.outgrpmainbean.CloseGrpAddBean;
import com.asiainfo.veris.crm.order.soa.group.outgrpmainbean.CloseGrpDelBean;
import com.asiainfo.veris.crm.order.soa.group.outgrpmainbean.CloseMebAddBean;
import com.asiainfo.veris.crm.order.soa.group.outgrpmainbean.CloseMebDelBean;
import com.asiainfo.veris.crm.order.soa.group.outgrpmainbean.DaidaGrpAddBean;
import com.asiainfo.veris.crm.order.soa.group.outgrpmainbean.DaidaGrpDelBean;
import com.asiainfo.veris.crm.order.soa.group.outgrpmainbean.HuntGrpAddBean;
import com.asiainfo.veris.crm.order.soa.group.outgrpmainbean.HuntGrpDelBean;
import com.asiainfo.veris.crm.order.soa.group.outgrpmainbean.OutNumGrpAddBean;
import com.asiainfo.veris.crm.order.soa.group.outgrpmainbean.OutNumGrpDelBean;
import com.asiainfo.veris.crm.order.soa.group.outgrpmainbean.OutNumMebAddBean;
import com.asiainfo.veris.crm.order.soa.group.outgrpmainbean.OutNumMebDelBean;
import com.asiainfo.veris.crm.order.soa.group.outgrpmainbean.ZmGrpAddBean;
import com.asiainfo.veris.crm.order.soa.group.outgrpmainbean.ZmGrpDelBean;

public class OutGrpMainSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    /**
     * 集团闭合群新增
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset addCloseGrpUU(IData param) throws Exception
    {
        CloseGrpAddBean closeGrpAddBean = new CloseGrpAddBean();
        return closeGrpAddBean.crtTrade(param);
    }

    /**
     * 成员闭合群新增
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset addCloseMebUU(IData param) throws Exception
    {
        CloseMebAddBean closeMebAddBean = new CloseMebAddBean();
        return closeMebAddBean.crtTrade(param);
    }

    /**
     * 代答组新增
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset addDaidaGrpUU(IData param) throws Exception
    {
        DaidaGrpAddBean daidaGrpAddBean = new DaidaGrpAddBean();
        return daidaGrpAddBean.crtTrade(param);
    }

    /**
     * 寻呼组新增
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset addHuntGrpUU(IData param) throws Exception
    {
        HuntGrpAddBean huntGrpAddBean = new HuntGrpAddBean();
        return huntGrpAddBean.crtTrade(param);
    }

    /**
     * 集团网外号码新增
     */
    public IDataset addOutNumGrpUU(IData param) throws Exception
    {
        OutNumGrpAddBean outNumberGrpBean = new OutNumGrpAddBean();
        return outNumberGrpBean.crtTrade(param);
    }

    /**
     * 成员网外号码新增
     */
    public IDataset addOutNumMebUU(IData param) throws Exception
    {
        OutNumMebAddBean outNumMebAddBean = new OutNumMebAddBean();
        return outNumMebAddBean.crtTrade(param);
    }

    /**
     * 子母集团新增
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset addZmGrpUU(IData param) throws Exception
    {
        ZmGrpAddBean zmGrpAddBean = new ZmGrpAddBean();
        return zmGrpAddBean.crtTrade(param);
    }

    /**
     * 集团闭合群删除
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset delCloseGrpUU(IData param) throws Exception
    {
        CloseGrpDelBean closeGrpDelBean = new CloseGrpDelBean();
        return closeGrpDelBean.crtTrade(param);
    }

    /**
     * 成员闭合群删除
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset delCloseMebUU(IData param) throws Exception
    {
        CloseMebDelBean closeMebDelBean = new CloseMebDelBean();
        return closeMebDelBean.crtTrade(param);
    }

    /**
     * 代答组删除
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset delDaidaGrpUU(IData param) throws Exception
    {
        DaidaGrpDelBean daidaGrpDelBean = new DaidaGrpDelBean();
        return daidaGrpDelBean.crtTrade(param);
    }

    /**
     * 寻呼组删除
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset delHuntGrpUU(IData param) throws Exception
    {
        HuntGrpDelBean huntGrpDelBean = new HuntGrpDelBean();
        return huntGrpDelBean.crtTrade(param);
    }

    /**
     * 集团网外号码删除
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset delOutNumGrpUU(IData param) throws Exception
    {
        OutNumGrpDelBean outNumGrpDelBean = new OutNumGrpDelBean();
        return outNumGrpDelBean.crtTrade(param);
    }

    /**
     * 成员网外号码删除
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset delOutNumMebUU(IData param) throws Exception
    {
        OutNumMebDelBean outNumMebDelBean = new OutNumMebDelBean();
        return outNumMebDelBean.crtTrade(param);
    }

    /**
     * 子母集团删除
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset delZmGrpUU(IData param) throws Exception
    {
        ZmGrpDelBean zmGrpDelBean = new ZmGrpDelBean();
        return zmGrpDelBean.crtTrade(param);
    }

    /**
     * 查询闭合群成员列表
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getCloseGrpMemAllDb(IData param) throws Exception
    {
        String userIdA = param.getString("USER_ID_A");
        String userIdB = param.getString("USER_ID_B");
        String relationTypeCode = param.getString("RELATION_TYPE_CODE");
        String whereSub = param.getString("USER_ID_LIST");
        return RelaUUInfoQry.getCloseGrpMemAllDb(userIdA, relationTypeCode, whereSub);
    }

    /**
     * 查询集团成员的网外信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getMemOutGrp(IData input) throws Exception
    {
        Pagination page = getPagination();
        String user_id_a = input.getString("USER_ID_A");
        String user_id_b = input.getString("USER_ID_B");
        String relation_type_code = input.getString("RELATION_TYPE_CODE");
        String serial_number_b = input.getString("SERIAL_NUMBER_B");
        String serial_number_a = input.getString("SERIAL_NUMBER_A");
        IDataset data = RelaUUInfoQry.getMemOutGrp(user_id_a, user_id_b, relation_type_code, serial_number_b, serial_number_a, page);
        return data;
    }
}

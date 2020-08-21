/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.nonbossfee;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * @CREATED by gongp@2014-4-24 修改历史 Revision 2014-4-24 上午09:28:37
 * @chenxy3@2015-2-11 修改。统一后台调用增删改
 */
public class NonBossFeeItemMgrBean extends CSBizBean
{

	/**
     * 删除项目
     * 
     * @param inparams
     * @return
     * @throws Exception
     * @CREATED by chenxy3@2015-2-11
     */
    public int deleteNonBossFeeItem(IData inparams) throws Exception
    {

        return Dao.executeUpdateByCodeCode("TD_S_NONBOSSPARA", "DEL_NONBOSS_ITEMS", inparams, Route.CONN_CRM_CEN);
    }

    /**
     * 增加项目
     * 
     * @param inparams
     * @return
     * @throws Exception
     * @CREATED by chenxy3@2015-2-11
     */
    public int insertNonBossFeeItem(IData inparams) throws Exception
    {

        return Dao.executeUpdateByCodeCode("TD_S_NONBOSSPARA", "INS_NONBOSS_ITEMS", inparams, Route.CONN_CRM_CEN);
    }

    /**
     * 修改项目
     * 
     * @param inparams
     * @return
     * @throws Exception
     * @CREATED by chenxy3@2015-2-11
     */
    public int updateNonBossFeeItem(IData inparams) throws Exception
    {

        return Dao.executeUpdateByCodeCode("TD_S_NONBOSSPARA", "UPD_NONBOSS_ITEMS", inparams, Route.CONN_CRM_CEN);
    }

}

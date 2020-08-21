
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class WidenetOtherInfoQry
{

    /**
     * 学生学号不允许办理多笔宽带
     * 
     * @param noteId
     * @return
     * @throws Exception
     * @author chenzm
     */
    public static IDataset getUserWidenetOtherByNoteId(String noteId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("NOTE_ID", noteId);

        return Dao.qryByCode("TF_F_USER_WIDENET_OTHER", "SEL_BY_NOTE_ID", inparam);
    }

    /**
     * @param userId
     * @return
     * @throws Exception
     * @author chenzm
     */
    public static IDataset getUserWidenetOtherInfo(String userId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_WIDENET_OTHER", "SEL_BY_USERID", inparam);
    }
}

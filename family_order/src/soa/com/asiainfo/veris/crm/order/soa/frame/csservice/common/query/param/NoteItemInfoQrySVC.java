
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class NoteItemInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 高级付费关系变更，综合帐目列表过滤
     * 
     * @param iData
     * @return
     * @throws Exception
     */
    public IDataset filterNoteItems(IData iData) throws Exception
    {
        String note_item = iData.getString("NOTE_ITEM", "");

        return NoteItemInfoQry.filterNoteItems(note_item);
    }

    public IDataset filterNoteItemsByGrp(IData input) throws Exception
    {
        String NOTE_ITEM = input.getString("NOTE_ITEM", "");
        IDataset output = NoteItemInfoQry.filterNoteItemsByGrp(NOTE_ITEM);
        return output;
    }

    public IDataset filterNoteItemsByGrpForHNANNew(IData input) throws Exception
    {
        String NOTE_ITEM = input.getString("NOTE_ITEM", null);
        IDataset output = NoteItemInfoQry.filterNoteItemsByGrpForHNANNew(NOTE_ITEM);
        return output;
    }

    public IDataset filterNoteItemsForHNan(IData input) throws Exception
    {
        String noteitem = input.getString("NOTE_ITEM");
        IDataset ret = NoteItemInfoQry.filterNoteItemsForHNan(noteitem);
        return ret;
    }

    /**
     * 高级付费关系变更，综合帐目列表过滤，用于集团账目高级付费
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getNoteItemsByGrpForHNAN(IData input) throws Exception
    {
        String ITEM_ID = input.getString("ITEM_ID", "");
        IDataset output = NoteItemInfoQry.getNoteItemsByGrpForHNAN(ITEM_ID);
        return output;
    }

    /**
     * 查询明细帐目列表
     */
    public IDataset queryDetItemByPageForGrp(IData input) throws Exception
    {
        IDataset ret = NoteItemInfoQry.queryDetItemByPageForGrp(input, null);
        return ret;
    }

    public IDataset queryNoteItems(IData input) throws Exception
    {
        IDataset ret = NoteItemInfoQry.queryNoteItems();
        return ret;
    }

    /**
     * 高级付费关系变更，获取二级综合帐目列表
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryNoteItems2(IData input) throws Exception
    {
        String parent_item_code = input.getString("PARENT_ITEM_CODE");

        return NoteItemInfoQry.queryNoteItems2(parent_item_code);
    }

    public IDataset queryNoteItemsall(IData input) throws Exception
    {
        IDataset ret = NoteItemInfoQry.queryNoteItemsall();
        return ret;
    }

}

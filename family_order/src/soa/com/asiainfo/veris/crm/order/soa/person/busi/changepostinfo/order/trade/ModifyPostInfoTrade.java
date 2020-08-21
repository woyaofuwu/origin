
package com.asiainfo.veris.crm.order.soa.person.busi.changepostinfo.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PostTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPostInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changepostinfo.order.requestdata.ModifyPostInfoReqData;

public class ModifyPostInfoTrade extends BaseTrade implements ITrade
{
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        createPostInfoTrade(btd);
    }

    /**
     * 写投递信息台账，多种投递方式则有多条台账记录，因为数据库表中只有一个字段来存放投递内容
     * 
     * @param btd
     * @throws Exception
     */
    private void createPostInfoTrade(BusiTradeData btd) throws Exception
    {
        ModifyPostInfoReqData reqData = (ModifyPostInfoReqData) btd.getRD();
        IData postInfo = this.getPostInfo(btd);//

        String oldPostContent = postInfo.getString("OLD_POSTTYPE_CONTENT", "");// old邮政投递内容
        String oldEmailContent = postInfo.getString("OLD_EMAILTYPE_CONTENT", "");// 原始email内容
        String oldMMSContent = postInfo.getString("OLD_MMSTYPE_CONTENT", "");//

        String newPostContent = reqData.getPostContent(); // 邮政投递内容拼串
        String newEmailConent = reqData.getEmailContent();// EMAIL投递内容拼串
        String newMmsConent = reqData.getMMScontent();// MMS投递内容拼串

        // 邮寄标志为无效 则将邮寄内容全部设为空
        if (StringUtils.isBlank(reqData.getPostTag()) || StringUtils.equals("0", reqData.getPostTag()))
        {
            newPostContent = "";
            newEmailConent = "";
            newMmsConent = "";
        }

        // 1、邮政 如果老的有邮递则终止
        if (StringUtils.isNotBlank(oldPostContent))
        {
            PostTradeData postTrade = new PostTradeData(postInfo.getData("OLD_POSTTYPE_DATA"));
            postTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
            postTrade.setEndDate(reqData.getAcceptTime());
            btd.add(reqData.getUca().getSerialNumber(), postTrade);
        }
        // 如果新的在界面上勾选了邮政则新增
        if (StringUtils.isNotBlank(newPostContent))
        {
            PostTradeData postTrade = new PostTradeData();
            postTrade.setInstId(SeqMgr.getInstId());
            postTrade.setId(btd.getRD().getUca().getUserId());
            postTrade.setIdType("1");
            postTrade.setPostTag("1");
            postTrade.setPostCode(reqData.getPostCode());
            postTrade.setPostAddress(reqData.getPostAddress());
            postTrade.setEmail(reqData.getPostEmail());
            postTrade.setPostName(reqData.getPostName());
            postTrade.setFaxNbr(reqData.getPostFaxNbr());
            postTrade.setPostCyc("0");// 邮寄周期界面上不显示 默认为0
            postTrade.setPostContent(newPostContent);// 内容
            postTrade.setPostTypeset("0");// 写死0 邮递方式
            postTrade.setStartDate(reqData.getAcceptTime());
            postTrade.setEndDate(SysDateMgr.END_DATE_FOREVER);
            postTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
            btd.add(btd.getRD().getUca().getSerialNumber(), postTrade);
        }

        // 2、Email 如果老的有Email则终止
        if (StringUtils.isNotBlank(oldEmailContent))
        {
            PostTradeData postTrade = new PostTradeData(postInfo.getData("OLD_EMAILTYPE_DATA"));
            postTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
            postTrade.setEndDate(reqData.getAcceptTime());
            btd.add(reqData.getUca().getSerialNumber(), postTrade);
        }
        // 本次邮寄标志有效，如果新的勾选了Email则新增
        if (StringUtils.isNotBlank(newEmailConent))
        {
            PostTradeData postTrade = new PostTradeData();
            postTrade.setInstId(SeqMgr.getInstId());
            postTrade.setId(btd.getRD().getUca().getUserId());
            postTrade.setIdType("1");
            postTrade.setPostTag("1");
            postTrade.setPostCode(reqData.getPostCode());
            postTrade.setPostAddress(reqData.getPostAddress());
            postTrade.setEmail(reqData.getPostEmail());
            postTrade.setPostName(reqData.getPostName());
            postTrade.setFaxNbr(reqData.getPostFaxNbr());
            postTrade.setPostCyc("0");// 邮寄周期界面上不显示 默认为0
            postTrade.setPostContent(newEmailConent);// 内容
            postTrade.setPostTypeset("2");// email方式
            postTrade.setStartDate(reqData.getAcceptTime());
            postTrade.setEndDate(SysDateMgr.END_DATE_FOREVER);
            postTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
            btd.add(btd.getRD().getUca().getSerialNumber(), postTrade);
        }

        // 3、MMS 如果老的有MMS则终止
        if (StringUtils.isNotBlank(oldMMSContent))
        {
            PostTradeData postTrade = new PostTradeData(postInfo.getData("OLD_MMSTYPE_DATA"));
            postTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
            postTrade.setEndDate(reqData.getAcceptTime());
            btd.add(reqData.getUca().getSerialNumber(), postTrade);
        }
        // MMS 本次邮寄标志有效，如果新的勾选了MMS则新增
        if (StringUtils.isNotBlank(newMmsConent))
        {
            PostTradeData postTrade = new PostTradeData();
            postTrade.setInstId(SeqMgr.getInstId());
            postTrade.setId(btd.getRD().getUca().getUserId());
            postTrade.setIdType("1");
            postTrade.setPostTag("1");
            postTrade.setPostCode(reqData.getPostCode());
            postTrade.setPostAddress(reqData.getPostAddress());
            postTrade.setEmail(reqData.getPostEmail());
            postTrade.setPostName(reqData.getPostName());
            postTrade.setFaxNbr(reqData.getPostFaxNbr());
            postTrade.setPostCyc("0");// 邮寄周期界面上不显示 默认为0
            postTrade.setPostContent(newMmsConent);// 内容
            postTrade.setPostTypeset("3");// mms方式
            postTrade.setStartDate(reqData.getAcceptTime());
            postTrade.setEndDate(SysDateMgr.END_DATE_FOREVER);
            postTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
            btd.add(btd.getRD().getUca().getSerialNumber(), postTrade);
        }
    }

    /**
     * 获取老的邮寄信息资料
     * 
     * @param btd
     * @return
     * @throws Exception
     */
    private IData getPostInfo(BusiTradeData btd) throws Exception
    {
        IData postInfo = new DataMap();
        String user_id = btd.getRD().getUca().getUserId();
        IDataset dataset = UserPostInfoQry.qryUserPostInfo(user_id, "1");

        String postContent = "";
        String emailContent = "";
        String mmsContent = "";
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0, size = dataset.size(); i < size; i++)
            {
                IData postData = dataset.getData(i);
                if (StringUtils.equals("0", postData.getString("POST_TYPESET")))// 邮政投递的方式
                {
                    postContent = postData.getString("POST_CONTENT").trim();
                    postInfo.put("OLD_POSTTYPE_DATA", postData);
                }
                else if (StringUtils.equals("2", postData.getString("POST_TYPESET"))) // emali 方式
                {
                    emailContent = postData.getString("POST_CONTENT").trim();
                    postInfo.put("OLD_EMAILTYPE_DATA", postData);

                }
                else if (StringUtils.equals("3", postData.getString("POST_TYPESET"))) // mms 方式
                {
                    mmsContent = postData.getString("POST_CONTENT").trim();
                    postInfo.put("OLD_MMSTYPE_DATA", postData);
                }
            }
        }

        postInfo.put("OLD_POSTTYPE_CONTENT", postContent);// 邮政投递内容
        postInfo.put("OLD_EMAILTYPE_CONTENT", emailContent);// email投递内容
        postInfo.put("OLD_MMSTYPE_CONTENT", mmsContent);// mms投递内容
        return postInfo;
    }

}

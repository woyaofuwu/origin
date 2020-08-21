SELECT
to_char(p.INST_ID) INST_ID,to_char(p.TRADE_ID) TRADE_ID, p.ACCEPT_MONTH, to_char(p.USER_ID) USER_ID,
 p.SERVICE_ID, p.SERV_CODE, p.SERIAL_NUMBER, p.GROUP_ID, p.BIZ_TYPE_CODE, 
 p.BIZ_CODE, BIZ_ATTR, p.BIZ_NAME, p.BIZ_IN_CODE, p.BIZ_STATUS, p.BIZ_STATE_CODE,
  p.BIZ_PRI, p.AUTH_CODE, p.USAGE_DESC, p.INTRO_URL, p.BILLING_TYPE, 
  p.BILLING_MODE, p.PRICE, p.PRE_CHARGE, p.CS_TEL, p.CS_URL, p.ACCESS_MODE,
  p.ACCESS_NUMBER, p.SI_BASE_IN_CODE, p.SI_BASE_IN_CODE_A, p.EC_BASE_IN_CODE, 
  p.EC_BASE_IN_CODE_A, to_char(p.MAX_ITEM_PRE_DAY) MAX_ITEM_PRE_DAY,
   to_char(p.MAX_ITEM_PRE_MON) MAX_ITEM_PRE_MON, to_char(p.DELIVER_NUM) DELIVER_NUM, 
   p.FORBID_START_TIME_A, p.FORBID_END_TIME_A, p.FORBID_START_TIME_B, 
   p.FORBID_END_TIME_B, p.FORBID_START_TIME_C, p.FORBID_END_TIME_C, p.FORBID_START_TIME_D,
    p.FORBID_END_TIME_D, p.IS_TEXT_ECGN, p.DEFAULT_ECGN_LANG, p.TEXT_ECGN_EN, 
    p.TEXT_ECGN_ZH, to_char(p.OPR_EFF_TIME,'yyyy-mm-dd hh24:mi:ss') OPR_EFF_TIME, 
    p.OPR_SEQ_ID, p.OPER_STATE, p.ADMIN_NUM, p.MAS_ID, 
    to_char(p.FIRST_DATE,'yyyy-mm-dd hh24:mi:ss') FIRST_DATE, p.PLAT_SYNC_STATE, 
    to_char(p.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(p.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, 
    p.MODIFY_TAG, p.UPDATE_DEPART_ID, p.UPDATE_STAFF_ID, to_char(p.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, 
    p.REMARK, p.RSRV_NUM1, p.RSRV_NUM2, p.RSRV_NUM3, to_char(p.RSRV_NUM4) RSRV_NUM4, 
    to_char(p.RSRV_NUM5) RSRV_NUM5, p.RSRV_STR1, p.RSRV_STR2, p.RSRV_STR3, p.RSRV_STR4, 
    p.RSRV_STR5, to_char(p.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, to_char(p.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
     to_char(p.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, p.RSRV_TAG1, p.RSRV_TAG2, p.RSRV_TAG3
 FROM TF_B_TRADE_GRP_PLATSVC p,tf_b_trade b
        WHERE BIZ_IN_CODE = :BIZ_IN_CODE
        and p.trade_id=b.trade_id
        AND b.accept_month = TO_NUMBER(SUBSTR(p.trade_id,5,2))
        and b.subscribe_state!='9'
        and p.group_id=:GROUP_ID
        and p.biz_code=:BIZ_CODE
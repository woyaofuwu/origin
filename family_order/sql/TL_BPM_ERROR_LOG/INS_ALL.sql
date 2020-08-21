INSERT INTO tl_bpm_error_log (
   flow_id,
   bpm_id,
   trade_id,
   trade_type_code,
   user_id,
   cust_name,
   err_activity,
   err_code,
   err_desc,
   err_detail_desc,
   err_time,
   deal_tag,
   deal_time,
   deal_desc,
   deal_staff_id,
   deal_depart_id
) VALUES (
   TO_NUMBER(F_SYS_GETSEQID('', 'seq_bpm_id')),
   :BPM_ID,
   TO_NUMBER(:TRADE_ID),
   :TRADE_TYPE_CODE,
   TO_NUMBER(:USER_ID),
   :CUST_NAME,
   :ERR_ACTIVITY,
   :ERR_CODE,
   SUBSTRB(:ERR_DESC,1,500),
   SUBSTRB(:ERR_DESC,1,4000),
   TO_DATE(:ERR_TIME,'yyyy-mm-dd hh24:mi:ss'),
   :DEAL_TAG,
   TO_DATE(:DEAL_TIME,'yyyy-mm-dd hh24:mi:ss'),
   :DEAL_DESC,
   :DEAL_STAFF_ID,
   :DEAL_DEPART_ID
)
UPDATE tf_f_account_acctday
   SET chg_type         = :CHG_TYPE,
       chg_mode         = :CHG_MODE,
       chg_date         = TO_DATE(:CHG_DATE, 'yyyy-mm-dd hh24:mi:ss'),
       end_date         = TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss'),
       update_time      = TO_DATE(:UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss'),
       update_staff_id  = :UPDATE_STAFF_ID,
       update_depart_id = :UPDATE_DEPART_ID,
       remark           = :REMARK
 WHERE acct_id = TO_NUMBER(:ACCT_ID)
   AND partition_id = MOD(TO_NUMBER(:ACCT_ID), 10000)
   AND inst_id = TO_NUMBER(:INST_ID)
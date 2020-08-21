UPDATE TF_F_USER_PLAT_ORDER 
   SET END_DATE = to_date('2050-12-31 23:59:59','yyyy-mm-dd hh24:mi:ss'),
       REMARK = :REMARK,
       UPDATE_TIME =sysdate
 WHERE partition_id = MOD(to_number(:USER_ID),10000)
   AND user_id = to_number(:USER_ID)
   AND BIZ_CODE = :BIZ_CODE
   AND SP_CODE = :SP_CODE
   AND END_DATE = (select /*+INDEX(a PK_TF_F_USER_PLAT_ORDER)*/ max(END_DATE) 
  from TF_F_USER_PLAT_ORDER a
 WHERE a.partition_id = MOD(to_number(:USER_ID),10000)
   AND a.user_id = to_number(:USER_ID)
   AND a.BIZ_CODE = :BIZ_CODE
   AND a.SP_CODE = :SP_CODE
   and a.END_DATE <> to_date('2050-12-31 23:59:59','yyyy-mm-dd hh24:mi:ss'))
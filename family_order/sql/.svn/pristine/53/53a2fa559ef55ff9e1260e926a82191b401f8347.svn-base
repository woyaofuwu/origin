SELECT to_char(user_id) user_id,
       to_char(user_id_a) user_id_a,
       inst_id,
       discnt_code,
       spec_tag,
       relation_type_code,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date
  FROM TF_F_USER_DISCNT T
 WHERE USER_ID = TO_NUMBER(:USER_ID)
   AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
   AND T.DISCNT_CODE = :DISCNT_CODE
   AND T.END_DATE > T.START_DATE
   AND TO_CHAR(START_DATE, 'YYYY') = TO_CHAR(SYSDATE, 'YYYY')
   AND TO_CHAR(END_DATE, 'YYYY') = TO_CHAR(SYSDATE, 'YYYY')

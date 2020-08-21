select count(1) short_count from TF_F_RELATION_UU where 1=1
 and USER_ID_A=:USER_ID_A
  and short_code is not null
 and sysdate>=start_date
  and sysdate<=end_date
  and short_code=:SHORT_CODE
 and relation_type_code='20'
 and rownum<2
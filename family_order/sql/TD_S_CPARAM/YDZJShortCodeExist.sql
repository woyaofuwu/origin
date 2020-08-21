select count(1) RECORDCOUNT from TF_F_RELATION_UU
  where USER_ID_A=:USER_ID_A
  and sysdate>=start_date
  and sysdate<=end_date
  and short_code=:SHORT_CODE
  and relation_type_code='25'
  and rownum<2
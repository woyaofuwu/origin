select count(1) RECORDCOUNT from TF_F_RELATION_UU
  where USER_ID_A=:USER_ID_A
  and sysdate>=start_date + 0
  and sysdate<=end_date + 0
  and short_code=:SHORT_CODE
  and relation_type_code in ('20','41')
  and rownum<2
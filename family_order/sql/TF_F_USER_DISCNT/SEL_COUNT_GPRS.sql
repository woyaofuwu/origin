SELECT count(1) done_Total
  FROM tf_f_user_discnt
  WHERE user_id = TO_NUMBER(:USER_ID)
  AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
  AND discnt_code in  (select A.PARA_CODE2 from TD_S_COMMPARA a WHERE A.PARAM_ATTR = '4502' and a.para_code1 = '99' )
  AND sysdate BETWEEN start_date AND end_date
SELECT a.partition_id,
       to_char(a.user_id) user_id,
       c.param_name service_type, 
       decode(a.deal_flag,'0','开','1','关') deal_flag,
       to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
       to_char(a.update_date,'yyyy-mm-dd hh24:mi:ss') update_date
  FROM TF_F_USER_SVCALLOWANCE a,(SELECT a.param_name,b.para_code2 
                                   FROM td_s_commpara a,(SELECT param_code,para_code2 FROM td_s_commpara 
                                                          WHERE param_attr='1' AND (para_code1='80'OR para_code1='90')) b
                                  WHERE a.param_attr='3'AND a.param_code=b.param_code) c                                  
 WHERE a.user_id = TO_NUMBER(:USER_ID)
   AND a.service_type = c.para_code2
   and sysdate between a.start_date and a.end_date
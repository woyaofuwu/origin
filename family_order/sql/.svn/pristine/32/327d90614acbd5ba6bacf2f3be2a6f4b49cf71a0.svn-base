SELECT (t1.depart_id) depart_id,(t1.depart_name) serv_para1,
 NVL((t2.para_value9),0) para_value9
  FROM 
  (SELECT ar.depart_id,ar.depart_name,ar.area_code
    FROM td_s_assignrule ar
   WHERE ar.res_type_code=:RES_TYPE_CODE  
   ) t1, 
  (SELECT rl.depart_id,TO_CHAR(SUM(NVL(rl.para_value9,0))) para_value9  
    FROM tf_b_resdaystat_log rl
    WHERE rl.res_type_code=:RES_TYPE_CODE  
     AND  rl.oper_flag=:OPER_FLAG
     AND  rl.stat_type=:STAT_TYPE       
     AND  rl.oper_date_str>=:START_DATE 
     AND  rl.oper_date_str<=:END_DATE         
     AND  rl.depart_id LIKE :DEPART_ID 
     AND  rl.city_code LIKE :CITY_CODE 
     AND  rl.eparchy_code=:EPARCHY_CODE 
   GROUP BY rl.depart_id) t2
  WHERE t1.depart_id=t2.depart_id(+)
   AND  t1.depart_id LIKE :DEPART_ID 
   AND  t1.area_code LIKE :CITY_CODE
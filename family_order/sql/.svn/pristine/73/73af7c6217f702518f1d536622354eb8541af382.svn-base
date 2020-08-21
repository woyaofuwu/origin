SELECT (t1.area_code) city_code,(t1.area_name) serv_para1,
 NVL((t2.para_value9),0) para_value9
  FROM 
  (SELECT ma.area_code,ma.area_name
    FROM td_m_area ma
   WHERE ma.validflag='0'
   ) t1,   
  (SELECT rl.city_code,TO_CHAR(SUM(NVL(rl.para_value9,0))) para_value9     
    FROM tf_b_resdaystat_log rl
    WHERE rl.res_type_code=:RES_TYPE_CODE  
     AND  rl.oper_flag=:OPER_FLAG   
     AND  rl.stat_type=:STAT_TYPE      
     AND  rl.oper_date_str>=:START_DATE 
     AND  rl.oper_date_str<=:END_DATE  
     AND  rl.depart_id IS NULL       
     AND  rl.city_code LIKE :CITY_CODE    
     AND  rl.eparchy_code=:EPARCHY_CODE   
   GROUP BY rl.city_code) t2
  WHERE t1.area_code=t2.city_code(+)
   AND  t1.area_code LIKE :CITY_CODE
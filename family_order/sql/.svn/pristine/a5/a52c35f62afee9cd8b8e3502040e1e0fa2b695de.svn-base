--IS_CACHE=Y
SELECT (t1.depart_id) depart_id,(t1.depart_name) depart_name,(t2.para_value11) para_value11
   FROM 
    (SELECT a.depart_id,a.depart_name,a.area_code
     FROM  td_m_depart a
     WHERE a.validflag='0'
     AND NOT EXISTS
      (SELECT b.depart_kind_code 
       FROM td_m_departkind b
       WHERE b.code_type_code='0'
       AND b.depart_kind_code=a.depart_kind_code
       AND b.eparchy_code=:EPARCHY_CODE
       )) t1,
     (SELECT d.para_code2,NVL(to_char(d.para_value11),'0') para_value11
      FROM td_m_res_commpara d
      WHERE d.para_attr=:PARA_ATTR 
       AND d.para_code1 like :PARA_CODE1   
       AND d.para_code2 like :PARA_CODE2
       AND d.eparchy_code=:EPARCHY_CODE
       ) t2
   WHERE t1.depart_id=t2.para_code2(+)  
     AND t1.depart_id like :PARA_CODE2
     AND t1.area_code like :PARA_CODE1
UPDATE td_s_assignrule SET 
  depart_level=LENGTH(depart_frame)/5-1
WHERE depart_frame LIKE (SELECT depart_frame FROM td_s_assignrule
                         WHERE depart_id=:DEPART_CODE
                         AND eparchy_code=:EPARCHY_CODE
                         AND res_type_code=:RES_TYPE_CODE)||'%'
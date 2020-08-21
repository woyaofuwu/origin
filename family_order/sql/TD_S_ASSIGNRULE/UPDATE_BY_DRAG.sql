UPDATE td_s_assignrule a SET a.parent_depart_id=:DEPART_CODE,a.rsrv_str4=a.depart_frame,
       a.depart_frame=(SELECT depart_frame||:DEPART_ID FROM td_s_assignrule WHERE depart_id=:DEPART_CODE
                       AND eparchy_code=:EPARCHY_CODE AND res_type_code=:RES_TYPE_CODE),
       a.depart_level=(SELECT b.depart_level+1 FROM td_s_assignrule b WHERE b.depart_id=:DEPART_CODE
                       AND eparchy_code=:EPARCHY_CODE AND res_type_code=:RES_TYPE_CODE),
       a.order_no=(SELECT NVL(MAX(b.order_no),0)+1 FROM td_s_assignrule b WHERE b.depart_frame like '%'||:DEPART_CODE
                       AND eparchy_code=:EPARCHY_CODE AND res_type_code=:RES_TYPE_CODE)
WHERE depart_id=:DEPART_ID
      AND eparchy_code=:EPARCHY_CODE
      AND res_type_code=:RES_TYPE_CODE
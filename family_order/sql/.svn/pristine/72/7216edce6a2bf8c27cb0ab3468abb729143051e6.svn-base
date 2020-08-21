UPDATE td_s_assignrule a SET
       a.depart_frame=(SELECT b.depart_frame ||(SELECT SUBSTR(depart_frame,INSTR(depart_frame,:DEPART_ID,1,1))
                                                FROM td_s_assignrule c
                                                WHERE c.depart_frame LIKE (SELECT rsrv_str4 FROM td_s_assignrule
                                                                         WHERE depart_id=:DEPART_ID
                                                                         AND eparchy_code=:EPARCHY_CODE
                                                                         AND res_type_code=:RES_TYPE_CODE)||'%'
                                                AND c.depart_id=a.depart_id
                                                AND c.eparchy_code=:EPARCHY_CODE
                                                AND c.res_type_code=:RES_TYPE_CODE)
                       FROM td_s_assignrule b
                       WHERE b.depart_id=:DEPART_CODE
                         AND b.eparchy_code=:EPARCHY_CODE
                         AND b.res_type_code=:RES_TYPE_CODE)
WHERE a.depart_frame LIKE (SELECT rsrv_str4 FROM td_s_assignrule
                           WHERE depart_id=:DEPART_ID
                           AND eparchy_code=:EPARCHY_CODE
                           AND res_type_code=:RES_TYPE_CODE)||'%'
                         AND a.eparchy_code=:EPARCHY_CODE
                         AND a.res_type_code=:RES_TYPE_CODE
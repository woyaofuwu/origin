UPDATE /*+INDEX(a PK_TD_M_RES_COMMPARA)*/ td_m_res_commpara a
   SET para_value9=DECODE(para_value9,NULL,DECODE(:CHECKTAG,1,-TO_NUMBER(:PARA_VALUE9),TO_NUMBER(:PARA_VALUE9)),DECODE(:CHECKTAG,1,para_value9-TO_NUMBER(:PARA_VALUE9),para_value9+TO_NUMBER(:PARA_VALUE9))),update_time=SYSDATE,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID  
 WHERE eparchy_code=:EPARCHY_CODE
   AND para_attr=1034
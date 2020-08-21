UPDATE tl_m_rightlog
   SET oper_type=:OPER_TYPE1,oper_staff_id=:OPER_STAFF_ID,oper_depart_id=:OPER_DEPART_ID,use_tag=:USE_TAG,end_date=SYSDATE,rsrv_str2=:RSRV_STR2,remark=remark||:REMARK  
 WHERE right_code=:RIGHT_CODE
   AND opered_staff_id=:OPERED_STAFF_ID
   AND right_attr=:RIGHT_ATTR
   AND oper_type=:OPER_TYPE
   AND right_type=:RIGHT_TYPE
   AND use_tag=:USE_TAG2
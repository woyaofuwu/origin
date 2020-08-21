insert into ucr_cen1.code_code (DAO_NAME, TAB_NAME, SQL_REF, SQL_STMT, VERSION, CREA_STAFF, CREA_DATE, UPD_STAFF, UPD_DATE, CVSID, IS_CACHE, TTL, REL_TAB_NAME, REL_PARAM_NAME, ROWID)
values ('', 'TF_F_RELATION_UU', 'SEL_BY_SN_RELATION_TYPE_CODE_ALL', 'SELECT partition_id,to_char(user_id_a) user_id_a,serial_number_a,to_char(user_id_b) user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,to_char(start_date,''yyyy-mm-dd hh24:mi:ss'') start_date,to_char(end_date,''yyyy-mm-dd hh24:mi:ss'') end_date,inst_id,RSRV_STR3,rsrv_str5
  FROM TF_F_RELATION_UU t
 WHERE 1=1 
 	 AND user_id_a = TO_NUMBER(:USER_ID)
   AND relation_type_code = :RELATION_TYPE_CODE
   AND SERIAL_NUMBER_B = :SERIAL_NUMBER_B
   AND ORDERNO = :ORDERNO
   ORDER BY ORDERNO', '1', 'RC', to_date('06-12-2018 20:29:40', 'dd-mm-yyyy hh24:mi:ss'), 'RC', to_date('06-12-2018 20:29:40', 'dd-mm-yyyy hh24:mi:ss'), '', 'N', '86400', 'TF_F_RELATION_UU', 'SERIAL_NUMBER_B,ORDERNO,USER_ID,RELATION_TYPE_CODE', 'AABKvnABwAAABHWAAK');


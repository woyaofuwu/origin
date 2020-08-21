--IS_CACHE=Y
SELECT tmpl.tmpl_id TEMPLET_ID,tmpl.tmpl_name TEMPLET_NAME,tmpl.chan_id CHNL_NAME,tmpl.tmpl_type TEMPLET_TYPE,tmpl.tmpl_sub_type TEMPLET_SUB_TYPE,tmpl.staff_id CREATE_STAFF_ID,tmpl.update_date CREATE_TART_TIME,tmpl.approve_staff APPROVE_STAFF,tmpl.approve_result APPROVE_RESULT,tmpl.temp_status TEMP_STATUS FROM TD_S_FORMAT_TMPL tmpl WHERE 1 = 1
 AND tmpl.TEMP_STATUS = '3'
 AND tmpl.TMPL_TYPE = :TEMPLET_TYPE
 AND tmpl.CHAN_ID = :CHNL_TYPE
 AND tmpl.TMPL_NAME like '%'||:TEMPLET_NAME ||'%'
 AND tmpl.TMPL_ID = :TEMPLET_ID
 AND tmpl.STAFF_ID = :CREATE_STAFF_ID
 AND tmpl.UPDATE_DATE >= to_date(:CREATE_START_TIME,'yyyy-mm-dd')
 AND tmpl.UPDATE_DATE <= to_date(:CREATE_END_TIME,'yyyy-mm-dd')
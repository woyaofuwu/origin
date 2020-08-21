--IS_CACHE=Y
SELECT to_char(template_id) template_id,templet_subject,template_kind,template_content,template_desc,to_char(templet_status) templet_status,para_code1,para_code2,para_code3,para_code4,para_code5,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_m_question
 WHERE (template_id=TO_NUMBER(:TEMPLATE_ID) or :TEMPLATE_ID is null)
   AND (templet_subject like '%'||:TEMPLET_SUBJECT||'%' or :TEMPLET_SUBJECT is null) 
   AND (template_kind=:TEMPLATE_KIND or :TEMPLATE_KIND is null)
   ORDER BY para_code1 DESC
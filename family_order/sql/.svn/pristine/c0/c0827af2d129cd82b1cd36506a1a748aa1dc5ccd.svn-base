SELECT to_char(id) id,id_type,post_name,post_tag,post_content,post_typeset,post_cyc,post_address,post_code,email,fax_nbr,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_postinfo
 WHERE id = TO_NUMBER(:ID)
   AND id_type = :ID_TYPE
   AND (:POST_CONTENT IS NULL OR INSTR(post_content, :POST_CONTENT, 1) > 0)
   AND (:START_DATE IS NULL OR TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') <= end_date)
   AND (:END_DATE IS NULL OR TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') >= start_date)
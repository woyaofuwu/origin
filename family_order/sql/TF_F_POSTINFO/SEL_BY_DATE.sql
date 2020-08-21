SELECT to_char(id) id,id_type,post_name,post_tag,post_content,post_typeset,post_cyc,post_address,post_code,email,fax_nbr,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date
  FROM tf_f_postinfo
 WHERE (id IS NULL OR id = TO_NUMBER(:ID))
   AND (id_type IS NULL OR id_type = :ID_TYPE)
   AND (:POST_CONTENT IS NULL OR INSTR(post_content, :POST_CONTENT, 1) > 0)
   AND TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') <= end_date
   AND TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') >= start_date
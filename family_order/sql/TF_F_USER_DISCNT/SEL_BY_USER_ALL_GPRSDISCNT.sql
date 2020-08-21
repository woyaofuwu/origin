SELECT to_char(a.discnt_code) discnt_code,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,a.spec_tag,a.inst_id
    FROM tf_f_user_discnt a WHERE 
a.user_id=:USER_ID
AND a.discnt_code IN 
(SELECT b.discnt_code FROM TD_B_DTYPE_DISCNT b WHERE b.discnt_type_code='5')
AND end_date > SYSDATE

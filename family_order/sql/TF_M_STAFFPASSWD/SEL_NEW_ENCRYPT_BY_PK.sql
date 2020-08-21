--IS_CACHE=Y
select staff_id,
       staff_passwd,
       to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
       update_staff_id,
       update_depart_id,
       f_csb_encrypt(f_csb_new_decrypt(:PASSWD), '00linkage') passwd
  from tf_m_staffpasswd
 where staff_id = :STAFF_ID
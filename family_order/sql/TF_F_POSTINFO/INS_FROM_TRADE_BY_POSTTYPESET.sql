INSERT INTO tf_f_postinfo(id,id_type,post_name,post_tag,post_content,post_typeset,post_cyc,post_address,post_code,email,fax_nbr,start_date,end_date,update_time) 
select id,id_type,post_name,post_tag,post_content,post_typeset,post_cyc,post_address,post_code,email,fax_nbr,TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),update_time
from tf_b_trade_post
where trade_id=to_number(:TRADE_ID)
  and post_tag='1'
  and post_typeset=:POST_TYPESET
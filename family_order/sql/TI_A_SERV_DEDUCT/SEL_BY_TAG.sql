SELECT to_char(a.user_id) user_id FROM ti_a_serv_deduct a,tf_f_user_serv_deduct b WHERE a.user_id =b.user_id AND 
b.agent_code =:AGENT_ID AND a.deduct_tag='0' AND a.deal_tag='0' AND ROWNUM<2
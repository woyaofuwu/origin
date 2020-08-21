select max(temp.serial_number_a) serial_number_a,max(temp.serial_number_b) serial_number,temp.user_id_b,temp.user_id_a,
       max(temp.group_id) group_id,max(temp.cust_name) cust_name,
       max(temp.product_id) product_id,
       (max(temp.product_name) ||'-'|| max(temp.cust_name)) product_name,
       max(temp.discnt_explain) discnt_explain,
       max(temp.start_date) start_date,max(temp.end_date) end_date,
       max(temp.product_explain) product_explain
from
(select uu.serial_number_a,uu.serial_number_b,uu.user_id_b,uu.user_id_a,g.group_id,
       g.cust_name,gu.product_id,p.product_name,qd.discnt_explain,
       uu.start_date,uu.end_date,p.product_explain
  from tf_f_relation_uu uu,tf_f_user gu,tf_f_user mu,tf_f_cust_group g,td_b_product p,
       tf_f_user_discnt ud,td_b_qry_discnt qd
 where uu.user_id_a = gu.user_id
   and uu.user_id_b = mu.user_id
   and uu.serial_number_b = :SERIAL_NUMBER
   and gu.remove_tag = '0'
   and mu.remove_tag = '0'
   and gu.cust_id = g.cust_id
   and sysdate between uu.start_date and uu.end_date
   and uu.end_date > last_day(trunc(sysdate)) + 1 - 1 / 24 / 3600
   and gu.product_id = p.product_id
   and ud.user_id(+) = uu.user_id_b
   and ud.user_id_a(+) = uu.user_id_a
   and ud.relation_type_code(+) = uu.relation_type_code
   and ud.end_date(+) > last_day(trunc(sysdate)) + 1 - 1 / 24 / 3600
   and ud.discnt_code = qd.discnt_code(+)
   and qd.rsrv_str1(+) = 'D'
   and sysdate between qd.start_date(+) and qd.end_date(+)
   and exists (select 1
          from td_b_qry_config q
         where q.busi_type = 'G'
           and q.id_type = 'P'
           and p.product_id = q.id
           and sysdate between q.start_date and q.end_date)
   and not exists(
       select 1
          from td_b_qry_rule_config c
         where c.id_type='G'
           and c.id=p.product_id
           and c.serv_type='D'
           and c.service_id= ud.discnt_code
           and sysdate between c.start_date and c.end_date
   )        
   union
  select uu.serial_number_a,uu.serial_number_b,uu.user_id_b,uu.user_id_a,g.group_id,
       g.cust_name,gu.product_id,p.product_name,qd.discnt_explain,uu.start_date,uu.end_date,p.product_explain
  from tf_f_relation_uu uu,tf_f_user gu,tf_f_user mu,tf_f_cust_group g,td_b_product p,
       tf_f_user_svc ud,td_b_service s,td_b_qry_discnt qd
 where uu.user_id_a = gu.user_id
   and uu.user_id_b = mu.user_id
   and uu.serial_number_b = :SERIAL_NUMBER
   and gu.remove_tag = '0'
   and mu.remove_tag = '0'
   and gu.cust_id = g.cust_id
   and sysdate between uu.start_date and uu.end_date
   and uu.end_date > last_day(trunc(sysdate)) + 1 - 1 / 24 / 3600
   and gu.product_id = p.product_id
   and ud.user_id = uu.user_id_b
   and ud.user_id_a = uu.user_id_a
   and ud.end_date > last_day(trunc(sysdate)) + 1 - 1 / 24 / 3600
   and s.service_id = qd.discnt_code
   and qd.rsrv_str1='S'
   and sysdate between qd.start_date and qd.end_date
   and s.rsrv_tag3='G'
   and s.service_id = ud.service_id
   and sysdate between s.start_date and s.end_date
    and exists (select 1
          from td_b_qry_config q
         where q.busi_type = 'G'
           and q.id_type = 'P'
           and p.product_id = q.id
           and sysdate between q.start_date and q.end_date)
   and not exists(
       select 1
          from td_b_qry_rule_config c
         where c.id_type='G'
           and c.id=p.product_id
           and c.serv_type='S'
           and c.service_id= ud.service_id
           and sysdate between c.start_date and c.end_date)) temp
group by (temp.user_id_b,temp.user_id_a)
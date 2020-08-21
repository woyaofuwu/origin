select a.serial_number,
       a.destroy_time,
       a.cust_id,
       a.open_date,
       a.in_staff_id,
       a.in_depart_id,
       a.city_code,
       decode(a.remove_tag,
              '0',
              '正常',
              '1',
              '主动预销号',
              '2',
              '主动销号',
              '3',
              '欠费预销号',
              '4',
              '欠费销号',
              '5',
              '开户返销',
              '6',
              '过户注销') remove_tag,
       e.brand_code,
       d.cust_name,
       b.city_code,
       decode(d.sex, 'M', '男', 'F', '女') sex,
       decode(b.cust_type, '0', '个人客户', '1', '集团客户') cust_type,
       (select s.pspt_type
          from td_s_passporttype s
         where s.pspt_type_code = d.pspt_type_code
           and rownum = 1) pspt_type,
       d.pspt_id,
       d.pspt_addr,
       d.post_code,
       d.contact,
       d.contact_phone,
       d.post_address,
       d.home_address,
       a.user_id,
       a.eparchy_code
  from tf_fh_user        a, 
       tf_f_customer     b,
       tf_f_cust_person  d,
       tf_f_user_product e
 where a.cust_id = b.cust_id
   and a.cust_id = d.cust_id
   and a.user_id = e.user_id
   and a.serial_number = :PARA_CODE1 
   and e.main_tag = '1'
   and a.remove_tag <> '0'
select b.product_id,a.product_mode,package_id,b.element_id,
       b.element_name, element_type element_type_code,
       b.main_tag, b.start_date,b.end_date,attr_code,
       attr_value,'EXIST' state, b.cancel_tag,b.cancel_absolute_date, 
       b.cancel_offset, b.cancel_unit,b.inst_id,b.force_tag
  from td_b_product a,
       (select a.product_id,
               a.package_id package_id,
               a.discnt_code element_id,
               c.discnt_name element_name,
               'D' element_type,
               '0' main_tag,
               to_char(a.start_date, 'yyyy-mm-dd') start_date,
               to_char(a.end_date, 'yyyy-mm-dd') end_date,
               b.attr_code attr_code,
               b.attr_value attr_value,
               p.cancel_tag,
               to_char(p.cancel_absolute_date, 'yyyy-mm-dd') cancel_absolute_date,
               p.cancel_offset,
               p.cancel_unit,
               a.inst_id,
               p.force_tag
          from tf_f_user_discnt     a,
               tf_f_user_attr       b,
               td_b_discnt          c,
               td_b_package_element p,
               tf_f_user            u
         where a.user_id = to_number(:USER_ID)
           and a.partition_id = mod(to_number(:USER_ID), 10000)
           and a.user_id = b.user_id
           and a.user_id = u.user_id
           and a.inst_id = b.inst_id
           and b.inst_type = 'D'
           and b.partition_id = mod(to_number(:USER_ID), 10000)
           and a.end_date > sysdate
           and a.discnt_code = c.discnt_code
           and a.package_id = p.package_id
           and a.discnt_code = p.element_id
           and b.end_date > sysdate
           and (exists(select 1 from td_s_productlimit pl
           where pl.product_id_a=u.product_id
           and a.product_id=pl.product_id_b
           and pl.limit_tag=4)
           or exists(select 1 from tf_f_user tu
           where a.product_id=tu.product_id
           and tu.user_id=to_number(:USER_ID)
           and tu.partition_id=mod(to_number(:USER_ID), 10000)))
        union all
        select a.product_id,
               a.package_id package_id,
               a.discnt_code element_id,
               c.discnt_name element_name,
               'D' element_type,
               '0' main_tag,
               to_char(a.start_date, 'yyyy-mm-dd') start_date,
               to_char(a.end_date, 'yyyy-mm-dd') end_date,
               '' attr_code,
               '' attr_value,
               p.cancel_tag,
               to_char(p.cancel_absolute_date, 'yyyy-mm-dd') cancel_absolute_date,
               p.cancel_offset,
               p.cancel_unit,
               a.inst_id,
               p.force_tag
          from tf_f_user_discnt a, td_b_discnt c, td_b_package_element p,tf_f_user u
         where a.user_id = to_number(:USER_ID)
           and a.partition_id = mod(to_number(:USER_ID), 10000)
           and a.user_id=u.user_id
           and a.discnt_code = c.discnt_code
           and a.end_date > sysdate
           and not exists
         (select 1
                  from tf_f_user_attr
                 where partition_id = mod(to_number(:USER_ID), 10000)
                   and user_id = to_number(:USER_ID)
                   and inst_type = 'D'
                   and a.end_date > sysdate
                   and inst_id = a.inst_id)
           and (exists(select 1 from td_s_productlimit pl
           where pl.product_id_a=u.product_id
           and a.product_id=pl.product_id_b
           and pl.limit_tag=4)
           or exists(select 1 from tf_f_user tu
           where a.product_id=tu.product_id
           and tu.user_id=to_number(:USER_ID)
           and tu.partition_id=mod(to_number(:USER_ID), 10000)))
           and a.package_id = p.package_id
           and a.discnt_code = p.element_id) b
 where a.product_id = b.product_id
 order by b.product_id, b.package_id, element_id
select a.serial_number PARA_CODE1,d.cust_name PARA_CODE2, 
			 d.POST_ADDRESS PARA_CODE3,d.phone PARA_CODE4,a.user_id PARA_CODE6
  from tf_f_user a, tf_f_cust_person d
 where 1 = 1
   and a.user_id in
       (select distinct b.user_id_b
          from tf_f_relation_uu b, tf_f_user_vpn c
         where b.user_id_a = c.user_id
           and c.vpn_no = :VVPN_NO
           and (c.cust_manager = :VCUST_MANAGER 
           or :VCUST_MANAGER IS NULL))
   and (
       (:VREMOVE_TAG = '2' and a.remove_tag = :VREMOVE_TAG 
       and a.DESTROY_TIME between to_date(:VSTART_DATE, 'yyyy-mm-dd hh24:Vmi:Vss') and to_date(:VEND_DATE, 'yyyy-mm-dd hh24:Vmi:Vss')) 
       or (:VREMOVE_TAG = '4' and a.remove_tag = :VREMOVE_TAG and a.DESTROY_TIME between to_date(:VSTART_DATE, 'yyyy-mm-dd hh24:Vmi:Vss') and to_date(:VEND_DATE, 'yyyy-mm-dd hh24:Vmi:Vss'))
       or (:VREMOVE_TAG = '1' and a.remove_tag = :VREMOVE_TAG and a.PRE_DESTROY_TIME between to_date(:VSTART_DATE, 'yyyy-mm-dd hh24:Vmi:Vss') and to_date(:VEND_DATE, 'yyyy-mm-dd hh24:Vmi:Vss'))
       or (:VREMOVE_TAG = '3' and a.remove_tag = :VREMOVE_TAG 
       and a.PRE_DESTROY_TIME between to_date(:VSTART_DATE, 'yyyy-mm-dd hh24:Vmi:Vss') and to_date(:VEND_DATE, 'yyyy-mm-dd hh24:Vmi:Vss')) 
       or (:VREMOVE_TAG IS NULL and (
       (a.remove_tag = '2' and a.DESTROY_TIME between to_date(:VSTART_DATE, 'yyyy-mm-dd hh24:Vmi:Vss') and to_date(:VEND_DATE, 'yyyy-mm-dd hh24:Vmi:Vss')) 
       or (a.remove_tag = '4'  and a.DESTROY_TIME between to_date(:VSTART_DATE, 'yyyy-mm-dd hh24:Vmi:Vss') and to_date(:VEND_DATE, 'yyyy-mm-dd hh24:Vmi:Vss')) 
       or (a.remove_tag = '1' and a.PRE_DESTROY_TIME between to_date(:VSTART_DATE, 'yyyy-mm-dd hh24:Vmi:Vss') and to_date(:VEND_DATE, 'yyyy-mm-dd hh24:Vmi:Vss'))
       or (a.remove_tag = '3' and a.PRE_DESTROY_TIME between to_date(:VSTART_DATE, 'yyyy-mm-dd hh24:Vmi:Vss') and to_date(:VEND_DATE, 'yyyy-mm-dd hh24:Vmi:Vss')))))
   and d.partition_id = mod(a.cust_id, 10000)
   and d.cust_id = a.cust_id
   and a.city_code IN ( select area_code from td_m_area where area_frame like '%'||:VCITY_CODE||'%')
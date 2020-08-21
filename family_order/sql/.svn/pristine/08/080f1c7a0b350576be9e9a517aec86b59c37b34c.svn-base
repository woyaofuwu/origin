Select b.*
   From tf_f_relation_uu a, tf_f_user b
  Where a.user_id_a = b.user_id
    and a.partition_id = mod(:USER_ID_B, 10000)
    and a.user_id_b = :USER_ID_B
    And b.product_id in
        (select para_code1
           from TD_S_COMMPARA t
          where t.subsys_code = :SUBSYS_CODE
            and t.param_attr = :PARAM_ATTR
            and end_date > sysdate
            AND (eparchy_code = :EPARCHY_CODE OR eparchy_code = 'ZZZZ'))
    And a.end_date > Sysdate
    and b.cust_id <> :CUST_ID
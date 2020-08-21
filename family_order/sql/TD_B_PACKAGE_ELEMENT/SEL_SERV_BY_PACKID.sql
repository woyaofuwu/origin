select *
  from (SELECT s.service_name element_name,
               t.package_id,
               t.element_type_code,
               t.element_id,
               t.default_tag,
               t.main_tag,
               t.force_tag,
               t.enable_tag,
               to_char(t.start_absolute_date, 'yyyy-mm-dd') start_absolute_date,
               t.start_offset,
               t.start_unit,
               t.end_enable_tag,
               to_char(t.end_absolute_date, 'yyyy-mm-dd') end_absolute_date,
               t.end_offset,
               t.end_unit,
               t.CANCEL_TAG,
               t.CANCEL_ABSOLUTE_DATE,
               t.CANCEL_OFFSET,
               t.CANCEL_UNIT,
               t.start_date,
               t.end_date,
               t.item_index,
               to_char(t.update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
               t.update_staff_id,
               t.update_depart_id,
               t.remark,
               t.rsrv_str3
          FROM td_b_package_element t, td_b_service s
         WHERE t.element_id = s.service_id
           and t.element_type_code = 'S'
           AND t.package_id = :PACKAGE_ID
           AND SYSDATE BETWEEN t.start_date AND t.end_date
           and SYSDATE BETWEEN s.start_date AND s.end_date
        union
        SELECT s.service_name element_name,
               t.package_id,
               t.element_type_code,
               t.element_id,
               t.default_tag,
               t.main_tag,
               t.force_tag,
               t.enable_tag,
               to_char(t.start_absolute_date, 'yyyy-mm-dd') start_absolute_date,
               t.start_offset,
               t.start_unit,
               t.end_enable_tag,
               to_char(t.end_absolute_date, 'yyyy-mm-dd') end_absolute_date,
               t.end_offset,
               t.end_unit,
               t.CANCEL_TAG,
               t.CANCEL_ABSOLUTE_DATE,
               t.CANCEL_OFFSET,
               t.CANCEL_UNIT,
               t.start_date,
               t.end_date,
               t.item_index,
               to_char(t.update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
               t.update_staff_id,
               t.update_depart_id,
               t.remark,
               t.rsrv_str3
          FROM td_b_package_element t, td_b_service s, tf_f_user_svc us
         WHERE t.element_id = us.service_id
           and s.service_id = us.service_id
           and us.package_id = :PACKAGE_ID
           and us.user_id = :USER_ID
           and t.element_type_code = 'S'
           AND t.package_id = :PACKAGE_ID
           and SYSDATE BETWEEN us.start_date AND us.end_date) tmp1
 ORDER BY tmp1.ELEMENT_ID
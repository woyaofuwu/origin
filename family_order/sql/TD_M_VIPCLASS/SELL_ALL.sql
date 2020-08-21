
select c.vip_type_code || '$' || c.class_id USER_CLASS,
          v.vip_type || '-' || class_name class_name
     from td_m_vipclass c, td_m_viptype v
    where c.vip_type_code = v.vip_type_code

--IS_CACHE=Y
SELECT a.staff_id,a.data_code,a.data_type,decode(a.right_attr, '0', '数据权限', '1','数据角色权限') right_attr,a.right_class,a.oper_special,decode(a.right_tag, '0', '无效', '1','有效') right_tag,a.rsvalue1,a.rsvalue2,a.remark,to_char(a.accredit_time,'yyyy-mm-dd hh24:mi:ss') accredit_time,a.accredit_staff_id 
  FROM tf_m_staffdataright a 
 WHERE a.staff_id=:STAFF_ID
   AND (:DATA_CODE is null or a.data_code=:DATA_CODE)
   AND (:DATA_TYPE is null or a.data_type=:DATA_TYPE)
   AND (:RIGHT_ATTR is null or a.right_attr=:RIGHT_ATTR)
   AND (:RIGHT_CLASS is null or a.right_class=:RIGHT_CLASS)
   AND (:RIGHT_TAG is null or a.right_tag=:RIGHT_TAG)
   AND a.data_code not in (SELECT b.data_code FROM td_m_tempdataright b WHERE b.staff_id='ZZZZZZZZ')
--IS_CACHE=Y
SELECT staff_id,data_code,data_type,decode(right_attr, '0', '数据权限', '1','数据角色权限') right_attr,right_class,oper_special,decode(right_tag, '0', '无效', '1','有效') right_tag,rsvalue1,rsvalue2,remark,to_char(accredit_time,'yyyy-mm-dd hh24:mi:ss') accredit_time,accredit_staff_id 
  FROM tf_m_staffdataright
 WHERE staff_id=:STAFF_ID
   AND (:DATA_CODE is null or data_code=:DATA_CODE)
   AND (:DATA_TYPE is null or data_type=:DATA_TYPE)
   AND (:RIGHT_ATTR is null or right_attr=:RIGHT_ATTR)
   AND (:RIGHT_CLASS is null or right_class=:RIGHT_CLASS)
   AND (:RIGHT_TAG is null or right_tag=:RIGHT_TAG)
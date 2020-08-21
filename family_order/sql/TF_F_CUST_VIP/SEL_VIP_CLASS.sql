SELECT TO_CHAR(a.CUST_ID) CUST_ID,
       TO_CHAR(a.USER_ID) USER_ID,
       a.SERIAL_NUMBER,
       a.VIP_CLASS_ID,
       a.VIP_TYPE_CODE,
       b.class_name
  FROM TF_F_CUST_VIP a,td_m_vipclass b
 WHERE user_id = :USER_ID
   AND REMOVE_TAG = :REMOVE_TAG
   and a.vip_type_code=b.vip_type_code
   and a.vip_class_id=b.class_id
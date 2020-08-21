SELECT a.CUST_MANAGER_ID paracode,b.staff_name paraname
  FROM TF_F_VPNMANAGERSTAFF a,td_m_staff b
 where a.CUST_TYPE_CODE='2'
   and a.EPARCHY_CODE = :TRADE_EPARCHY_CODE
   and sysdate between a.start_date and a.end_date
   AND a.cust_manager_id=b.staff_id(+)
SELECT t.Group_Id EC_ID,t.cust_name EC_NAME,nvl(t.province_code,substr(t.eparchy_code,2)) REGION_CODE,
t.city_code DISTRICT_CODE,t.group_status EC_STATUS,
t.class_id VALUELEVEL,T.SERV_LEVEL CUSTOMERSERVLEVEL,T.SUB_CALLING_TYPE_CODE INDUSTRY_TYPE,
T.GROUP_TYPE EC_TYPE,T.IN_DATE EFF_TIME,NVL(T.RSRV_STR3,T.RSRV_STR8) SP_ID,t.Group_Mgr_Cust_Name STAFF_NAME,CUST_MANAGER_ID STAFF_ID,
t.Group_Mgr_Sn STAFF_TEL 
  FROM tf_f_cust_group t 
  WHERE group_id=:GROUP_ID and remove_tag = '0' 
  

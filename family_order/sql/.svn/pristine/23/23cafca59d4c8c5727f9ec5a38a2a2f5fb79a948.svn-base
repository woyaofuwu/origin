Select to_char(b.user_id) user_id,'' vpn_no,a.cust_name vpn_name,b.serial_number,f_sys_getcodename('brand_code',b.brand_code,null,null) brand_code,
f_sys_getcodename('area_code',b.city_code,null,null) city_code,to_char(b.open_date,'yyyy-mm-dd hh24:mi:ss') open_date,to_char(b.destroy_time,'yyyy-mm-dd hh24:mi:ss') destroy_time
 From tf_f_cust_group a,tf_f_user b 
Where a.cust_id=b.cust_id And b.partition_id=mod(b.user_id,10000)
And b.brand_code In ('VPMN','VPCN','VPUN','VPMR')
And b.remove_tag='0'
and b.eparchy_code=:TRADE_EPARCHY_CODE
And (b.city_code=:CITY_CODE Or :CITY_CODE Is Null)
And (b.serial_number Like :SERIAL_NUMBER or :SERIAL_NUMBER is null)
And (a.cust_name like :VPN_NAME or :VPN_NAME is null)
And Exists(Select 1 From tf_f_user_discnt     
Where b.user_id=user_id And partition_id=mod(user_id,10000)
And discnt_code=:DISCNT_CODE And start_date<Sysdate And end_date>Sysdate)
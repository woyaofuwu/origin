SELECT a.serial_number,b.vpn_name,a.user_id,TO_DATE(a.open_date,'YYYY-MM-DD HH24:MI:SS') open_Date
FROM tf_f_user a,tf_f_user_vpn b
WHERE  brand_code=:BRAND_CODE  AND cust_id=TO_NUMBER(:CUST_ID)
 AND a.remove_tag='0'
 AND a.user_id+0=b.user_id
 AND eparchy_code=:EPARCHY_CODE
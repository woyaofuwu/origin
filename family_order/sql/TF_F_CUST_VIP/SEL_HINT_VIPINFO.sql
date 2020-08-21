select vipclass.class_name,t.cust_name,t.usecust_name,t.vip_card_no,staff.staff_name,t.serial_number ,staff.serial_number LINK_PHONE
from TF_F_CUST_VIP t , td_m_staff staff , td_m_vipclass vipclass
where t.remove_tag='0' and t.serial_number = :SERIAL_NUMBER
 and t.vip_class_id = vipclass.class_id (+) and t.vip_type_code =vipclass.vip_type_code (+)
 and t.cust_manager_id = staff.staff_id (+)
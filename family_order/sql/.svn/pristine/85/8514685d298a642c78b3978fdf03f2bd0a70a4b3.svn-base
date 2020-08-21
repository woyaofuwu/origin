SELECT a.STAFF_ID,
       a.CITY_CODE,
       a.WARNNING_VALUE_D,
       a.WARNNING_VALUE_U,
       (a.WARNNING_VALUE_D - a.WARNNING_VALUE_U) SURPLUS_VALUE,
       a.eparchy_code,
       a.RES_KIND_CODE,
       c.depart_id
  from tf_f_active_stock a, td_m_staff c
 where 1 = 1
   AND a.staff_id = c.staff_id
   AND (a.WARNNING_VALUE_D - a.WARNNING_VALUE_U) > 0
   AND a.res_kind_code=decode(:DISNCT_CODE,'84013442','90T','84013443','180T','84013444','360T')
   AND a.STAFF_ID >= :STAFF_ID
   AND a.STAFF_ID <= :STAFF_ID
   select T.*
     from TD_B_SALE_GOODS_EXT t
    where t.city_code = :CITY_CODE
      AND T.PRODUCT_ID = :PRODUCT_ID
      AND T.PACKAGE_ID = :PACKAGE_ID
      AND T.GOODS_ID = :GOODS_ID
      and sysdate < t.END_DATE
      AND SYSDATE >= T.START_DATE
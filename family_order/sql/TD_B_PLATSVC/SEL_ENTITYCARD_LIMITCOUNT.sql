SELECT DECODE(CHRG_TYPE, '3', LIMIT_COUNT, 1) LIMIT_COUNT
  FROM TI_B_ENCARD_PKG_SUB
 WHERE PKG_ID = :PACKAGE_CODE
   AND UPDATE_TIME = (SELECT MAX(UPDATE_TIME)
                        FROM TI_B_ENCARD_PKG_SUB
                       WHERE PKG_ID = :PACKAGE_CODE);
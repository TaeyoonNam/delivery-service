
CREATE TABLE USER_BASE(
  PID_NUM       INT(10) NOT NULL AUTO_INCREMENT,
  ID            VARCHAR(20) NOT NULL,
  PASSWORD      VARCHAR(20) NOT NULL,
  MOBILE_NUM    VARCHAR(15) NOT NULL,
  NAME          VARCHAR(10) DEFAULT NULL,
  BIRTHDATE     INT(8) DEFAULT 0,
  DATE_CREATION DATETIME DEFAULT NOW(),
  PRIMARY KEY (PID_NUM)
);
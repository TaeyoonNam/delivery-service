
-- <회원정보>
CREATE TABLE Delievery_Service.USER_BASE(
  USER_NUM           INT(10)       NOT NULL AUTO_INCREMENT,
  ID                VARCHAR(20)   NOT NULL,
  STATUS            VARCHAR(10)   NOT NULL DEFAULT 'DEFAULT',
  PASSWORD          VARCHAR(100)  NOT NULL,
  NAME              VARCHAR(10)   DEFAULT NULL,

  MOBILE_NUM        VARCHAR(15)   NOT NULL,
  BIRTHDAY         DATETIME      DEFAULT NULL,
  ADDRESS_CODE      INT(10)       DEFAULT NULL,
  ADDRESS_DETAIL    VARCHAR(20)   DEFAULT NULL,
  DATE_CREATION     DATETIME      DEFAULT NOW(),
  DATE_UPDATE       DATETIME,

  PRIMARY KEY (USER_NUM),
  FOREIGN KEY (ADDRESS_CODE) REFERENCES ADDRESS(PID_ADDRESS_CODE)
)


-- <주소>
-- 공공데이터(건물주소 DB) 저장 계획. 추가로 거리를 계산하기 위한 좌표 값 필요
-- http://www.juso.go.kr/addrlink/addressBuildDevNew.do?menu=rdnm
CREATE TABLE Delievery_Service.ADDRESS(
  PID_ADDRESS_CODE	  INT(10) NOT NULL AUTO_INCREMENT,
  CITY_NAME	          VARCHAR(40),
  COUNTRY_NAME	      VARCHAR(40),
  ROAD_CODE	          VARCHAR(40),
  ROAD_NAME	          VARCHAR(40),

  UNDERGROUND_STATUS	VARCHAR(1),
  BUILDING_MNG_CODE	  VARCHAR(25),
  BUILDING_NUMBER	    INT(5),
  BUILDING_NUMBER_SIDE	INT(5),
  BUILDING_NAME	      VARCHAR(40),

  BUILDING_NAME_DETAIL	VARCHAR(40),
  TOWN_CODE	          VARCHAR(20),
  ADMINISTRATION_CODE	VARCHAR(20),
  ADMINISTRATION_COUNTRY_CODE	VARCHAR(20),
  POSTAL_CODE	        VARCHAR(20),

  RSN_TRANSFER_CODE	  VARCHAR(20),
  NOTICE_DATE	        DATETIME,
  APARTMENT_HOUSE_STATUS	INT(1),
  ADDRESS_DETAIL_STATUS	INT(1),
  COMMENT1	          VARCHAR(100),
  COMMENT2	          VARCHAR(100),

  PRIMARY KEY(PID_ADDRESS_CODE)
)

-- <주문>
CREATE TABLE Delievery_Service.ORDERS(
  ORDER_NUM	            INT(10)      NOT NULL AUTO_INCREMENT,
  DATE_CREATION	        DATETIME     DEFAULT NOW(),
  ORDER_STATUS	        VARCHAR(10)  DEFAULT '매칭 대기중',
  ARRIVAL_TIME	        DATETIME,

  USER_ID	              VARCHAR(20)   NOT NULL,
  DELIVERY_ID	          VARCHAR(20)   NOT NULL,
  DEPARTURE_ADDRESS_CODE	    INT(10)      NOT NULL,
  DEPARTURE_ADDRESS_DETAIL	  VARCHAR(100) NOT NULL,
  DESTINATION_ADDRESS_CODE	    INT(10)      NOT NULL,
  DESTINATION_ADDRESS_DETAIL	  VARCHAR(100) NOT NULL,
  DISTANCE	            DECIMAL(4,2) NOT NULL,
  DELIVERY_TYPE	        VARCHAR(255) NOT NULL,

PRIMARY KEY(ORDER_NUM),
FOREIGN KEY(DEPARTURE_ADDRESS_CODE) REFERENCES ADDRESS(PID_ADDRESS_CODE),
FOREIGN KEY(DESTINATION_ADDRESS_CODE) REFERENCES ADDRESS(PID_ADDRESS_CODE)
)

-- <배달원 정보>
CREATE TABLE Delievery_Service.DELIVERY_MAN_BASE(
  DELIVERY_NUM  INT(10) NOT NULL AUTO_INCREMENT,
  ID	          VARCHAR(20) NOT NULL,
  STATUS	      VARCHAR(10) NOT NULL DEFAULT 'DEFAULT',
  PASSWORD	    VARCHAR(100) NOT NULL,
  NAME	        VARCHAR(10) NOT NULL,

  MOBILE_NUM	  VARCHAR(15) NOT NULL,
  BIRTHDAY	    DATE NOT NULL,
  DATE_CREATION	DATETIME DEFAULT NOW(),
  DATE_UPDATE	  DATETIME,

  PRIMARY KEY (DELIVERY_NUM)
)

-- <배달물품 정보>
CREATE TABLE Delievery_Service.PRODUCTS_INFO(
  PRODUCT_NUM	  INT(10) NOT NULL AUTO_INCREMENT,
  CATEGORY	    VARCHAR(20) NOT NULL,
  BRAND_NAME	  VARCHAR(20) NOT NULL,
  PRODUCT_NAME	VARCHAR(20) NOT NULL,
  COMMENT	      VARCHAR(200) DEFAULT NULL,

  ORDER_NUM	    INT(10),
  DATE_CREATION	DATETIME DEFAULT NOW(),
  DATE_UPDATE	  DATETIME,

  PRIMARY KEY(PRODUCT_NUM),
  FOREIGN KEY(ORDER_NUM) REFERENCES ORDERS(ORDER_NUM)
)

-- <결제정보>
CREATE TABLE Delievery_Service.PAYMENT(
PAYMENT_NUM	    INT(10) NOT NULL AUTO_INCREMENT,
PAYMENT_TYPE	  VARCHAR(255) NOT NULL,
AMOUNT	        INT(8) NOT NULL,
ORDER_NUM	      INT(8) NOT NULL,
PAYMENT_STATUS	VARCHAR(255) NOT NULL DEFAULT '미완료',
DATE_CREATION	  DATETIME DEFAULT NOW(),
DATE_UPDATE	    DATETIME,

PRIMARY KEY(PAYMENT_NUM),
FOREIGN KEY(ORDER_NUM) REFERENCES ORDERS(ORDER_NUM)
)

-- <카드정보>
CREATE TABLE Delievery_Service.CARD(
  CARD_CODE	  	INT(10) NOT NULL AUTO_INCREMENT,
  CARD_TYPE	    VARCHAR(20) NOT NULL,
  CARD_NUM	  		INT(15) NOT NULL,
  VALID_DATE		DATE NOT NULL,
  CVC_NUM	      	VARCHAR(200) DEFAULT NULL,
  PAYMENT_NUM   INT(10) NOT NULL,

  PRIMARY KEY(CARD_CODE),
  FOREIGN KEY(PAYMENT_NUM) REFERENCES PAYMENT(PAYMENT_NUM)
)

-- <계좌정보>
CREATE TABLE Delievery_Service.ACCOUNT_TRANSFER(
ACCOUNT_TRANSFER_CODE	INT(10) NOT NULL AUTO_INCREMENT,
BANK_NAME									VARCHAR(20) NOT NULL,
ACCOUNT_NUM							INT(15) NOT NULL,
ACCOUNT_NAME							VARCHAR(20) NOT NULL,
PAYMENT_NUM								INT(10) NOT NULL,
PRIMARY KEY(ACCOUNT_TRANSFER_CODE)
)

-- <요금>
CREATE TABLE Delievery_Service.FEE(
DELIVERY_TYPE	VARCHAR(255) NOT NULL,
BASIC_DISTANCE	DECIMAL(4,2) NOT NULL,
BASIC_FEE	INT(5) NOT NULL,
EXTRA_DISTANCE	DECIMAL(4,2) NOT NULL,
EXTRA_FEE	INT(5) NOT NULL,

PRIMARY KEY(DELIVERY_TYPE)
)
package me.naming.delieveryservice.dto;

import lombok.Getter;
import org.apache.ibatis.type.Alias;

/** FEE 테이블에서 데이터를 갖고오기 위한 DTO */
@Getter
@Alias("FeeDTO")
public class FeeDTO {

  private String deliveryType;
  private float basicDistance;
  private int basicFee;
  private float extraDistance;
  private int extraFee;

}

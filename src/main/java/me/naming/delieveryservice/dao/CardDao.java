package me.naming.delieveryservice.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CardDao {
  <T> void insertCardPayment(T paymentType);
}
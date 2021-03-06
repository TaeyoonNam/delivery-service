package me.naming.delieveryservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import javax.validation.Valid;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.naming.delieveryservice.dto.DeliveryPriceDTO;
import me.naming.delieveryservice.dto.FeeDTO;
import me.naming.delieveryservice.dto.OrderInfoDTO;
import me.naming.delieveryservice.dto.PaymentDTO;
import me.naming.delieveryservice.dto.UserOrderListDTO;
import me.naming.delieveryservice.service.AccountPaymentService;
import me.naming.delieveryservice.service.CardPaymentService;
import me.naming.delieveryservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

  @Autowired OrderService orderService;
  @Autowired CardPaymentService cardPaymentService;
  @Autowired AccountPaymentService accountPaymentService;

  /**
   * 주문정보 등록
   *  - 배달정보(출발지, 도착지)와 물품정보를 등록한다.
   *  - 저장된 주문정보(배달정보 + 물품정보)의 주문번호를 리턴해준다.
   * @param userId
   * @param orderInfoDTO
   * @return
   */
  @PostMapping("/users/{userId}")
  public ResponseEntity<OrderNum> orderInfo(@PathVariable String userId, @RequestBody OrderInfoDTO orderInfoDTO){
    int orderNum = orderService.orderInfo(userId, orderInfoDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(new OrderNum(orderNum));
  }

  /**
   * 배달가격 정보
   * 배달거리에 따라 사용자가 지불해야 하는 금액을 제공하기 위한 메소드
   *  - 주문거리에 따라 결제 금액이 다르게 보인다.(ex. ~5km 기본금액 / +3km씩
   *  - 기본금액+2000원 / ...) * - 각각의 금액(일괄배송, 빠른배송)을 모두 제공해준다.
   * @param orderNum
   * @return
   */
  @GetMapping("/{orderNum}/payments")
  public ResponseEntity<List<DeliveryPriceDTO>> deliveryPrice(@PathVariable int orderNum) {
    List<DeliveryPriceDTO> deliveryPriceDTOList = orderService.getDeliveryPriceList(orderNum);
    return ResponseEntity.ok(deliveryPriceDTOList);
  }

  /**
   * 카드 결제
   * @param orderNum
   * @param cardRequest
   * @return
   */
  @PostMapping("/{orderNum}/payments/card")
  public ResponseEntity paymentByCard(@PathVariable int orderNum, @RequestBody @Valid PaymentDTO.Card cardRequest) {
    cardRequest.setOrderNum(orderNum);
    orderService.payment(cardPaymentService, cardRequest);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * 계좌이체 결제
   * @param orderNum
   * @param accountRequest
   * @return
   */
  @PostMapping("/{orderNum}/payments/account")
  public ResponseEntity paymentByAccountTransfer(@PathVariable int orderNum, @RequestBody @Valid PaymentDTO.Account accountRequest) {
    accountRequest.setOrderNum(orderNum);
    orderService.payment(accountPaymentService, accountRequest);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * 사용자 주문정보 조회
   * @param userId
   * @return
   */
  @GetMapping("/users/{userId}")
  public List<UserOrderListDTO> userOrderList(@PathVariable String userId) {
    List<UserOrderListDTO> orderList = orderService.userOrderList(userId);

    return orderList;
  }

  // ---------- 값을 JSON 형식으로 리턴하기 위한 클래스
  @RequiredArgsConstructor
  @Data
  private class OrderNum{
    @NonNull private final int orderNum;
  }

}
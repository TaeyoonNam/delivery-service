package me.naming.delieveryservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import me.naming.delieveryservice.api.KakaoAPI;
import me.naming.delieveryservice.dao.AddressDao;
import me.naming.delieveryservice.dao.FeeDao;
import me.naming.delieveryservice.dao.OrderDao;
import me.naming.delieveryservice.dao.PaymentDao;
import me.naming.delieveryservice.dto.CoordinatesDTO;
import me.naming.delieveryservice.dto.FeeDTO;
import me.naming.delieveryservice.dto.OrderInfoDTO;
import me.naming.delieveryservice.dto.PaymentDTO;
import me.naming.delieveryservice.dto.UserOrderListDTO;
import me.naming.delieveryservice.utils.AddressUtil;
import me.naming.delieveryservice.utils.DistanceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class OrderService {

  @Autowired private OrderDao orderDao;
  @Autowired private AddressDao addressDao;
  @Autowired private KakaoAPI kakaoAPI;
  @Autowired private FeeDao feeDao;
  @Autowired private PaymentDao paymentDao;

  public List<UserOrderListDTO> userOrderList(String userId){
    return orderDao.userOrderList(userId);
  }

  /**
   * 주문 정보 저장
   * 프런트에서 전송한 주문 정보를 활용해 좌표 값을 구한다.
   * 이후 좌표 값을 활용해 직선거리를 구한다(이때 직선거리는 km 단위이다)
   * 측정한 직선거리와 함께 주문정보를 저장한다.
   * 성공적으로 저장될 경우, 주문번호(orderNum)를 클라이언트에 전송한다
   *    -> 주문번호를 전송하는 이유는 이후 결제 프로세스에서 결제번호 기준으로 DB에 저장하기 위함이다.
   * @param orderInfoDTO
   * @return
   */
  public int orderInfo(OrderInfoDTO orderInfoDTO){

    HashMap<String,Object> departureInfoFromDB = addressDao.getAddressInfoByAddressCode(orderInfoDTO.getDepartureCode());
    HashMap<String,Object> destinationInfoFromDB = addressDao.getAddressInfoByAddressCode(orderInfoDTO.getDestinationCode());

    String departureAddress = AddressUtil.getRoadAddress(departureInfoFromDB);
    String destinationAddress = AddressUtil.getRoadAddress(destinationInfoFromDB);

    CoordinatesDTO departureCoordinates = kakaoAPI.getCoordinatesByAddress(departureAddress);
    CoordinatesDTO destinationCoordinates = kakaoAPI.getCoordinatesByAddress(destinationAddress);

    double kmDistance =
        DistanceUtil.kmDistanceByCoordinates(
            departureCoordinates.getLatitude(),
            departureCoordinates.getLongitude(),
            destinationCoordinates.getLatitude(),
            destinationCoordinates.getLongitude());

    orderInfoDTO.setDistance(kmDistance);
    orderDao.orderAddress(orderInfoDTO);
    orderDao.orderProduct(orderInfoDTO);

    return orderInfoDTO.getOrderNum();
  }

  /**
   * 배달 거리에 따라 지불 금액 정보 제공
   * @param orderNum
   * @return
   */
  public List<FeeDTO> deliveryPaymentInfo(int orderNum) {

    float distance = orderDao.getOrderDistance(orderNum);
    List<FeeDTO> feeDTOList = feeDao.getFeeInfo();
    FeeDTO generalFeeInfo = feeDTOList.get(0);      // '일괄배송'에 관한 요금 정보 get
    FeeDTO fastFeeInfo = feeDTOList.get(1);         // '빠른배송'에 관한 요금 정보 get

    FeeDTO generalFee = calculateDeliveryFee(generalFeeInfo, distance);
    FeeDTO fastFee = calculateDeliveryFee(fastFeeInfo, distance);

    List<FeeDTO> paymentInfoList = new ArrayList<>();
    paymentInfoList.add(generalFee);
    paymentInfoList.add(fastFee);

    return paymentInfoList;
  }

  /**
   * 결제 금액 저장
   * @param paymentDTO
   */
  public void paymentInfo(PaymentDTO paymentDTO){
    paymentDao.paymentInfo(paymentDTO);
  }

  /**
   * 배송가격 계산
   * @param feeDTO
   * @param distance
   * @return
   */
  private FeeDTO calculateDeliveryFee(FeeDTO feeDTO, float distance) {

    // 거리가 기본거리(5km)이내인 경우
    if(distance <= feeDTO.getBasicDistance()) {
      return feeDTO.copyDeliveryPrice(feeDTO.getBasicFee());
    }

    // 거리가 기본거리(5km)이상인 경우 추가 요금 계산
    int extraDistanceCount = (int)Math.ceil((distance - feeDTO.getBasicDistance()) / feeDTO.getExtraDistance());
    int extraPrice = extraDistanceCount * feeDTO.getExtraFee();

    return feeDTO.copyDeliveryPrice(extraPrice+feeDTO.getBasicFee());
  }

}
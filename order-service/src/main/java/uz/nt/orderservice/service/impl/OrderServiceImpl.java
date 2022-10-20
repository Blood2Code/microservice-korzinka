package uz.nt.orderservice.service.impl;

import uz.nt.orderservice.dto.OrderDto;
import uz.nt.orderservice.dto.ResponseDto;
import uz.nt.orderservice.repository.OrderRepository;
import uz.nt.orderservice.service.OrderService;
import uz.nt.orderservice.service.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public ResponseDto addOrderIfNotExistUserOrders(Integer product_id, Integer amount) {
        return null;
    }

    @Override
    public ResponseDto<OrderDto> getById(Integer id) {
        return null;
    }

    @Override
    public ResponseDto<Page<OrderDto>> getAllOrders() {
        return null;
    }

    @Override
    public ResponseDto updateOrderPayed(Integer user_id) {
        return null;
    }

    @Override
    public ResponseDto updateOrder(OrderDto orderDto) {
        return null;
    }

    @Override
    public ResponseDto deleteById(Integer id) {
        return null;
    }
}

package uz.nt.productservice.service;

import org.apache.catalina.LifecycleState;
import org.springframework.data.domain.Page;
import shared.libs.dto.ProductTypeDto;
import shared.libs.dto.ResponseDto;

import java.util.List;

public interface ProductTypeService {

    ResponseDto<ProductTypeDto> add(ProductTypeDto dto);

    ResponseDto<List<ProductTypeDto>> all();

    ResponseDto<Page<ProductTypeDto>> pagination(Integer page, Integer size);

    ResponseDto<ProductTypeDto> oneById(Integer id);

    void delete(Integer id);
}

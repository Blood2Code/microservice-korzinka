package uz.nt.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import shared.libs.dto.ProductTypeDto;
import shared.libs.dto.ResponseDto;
import uz.nt.productservice.entity.ProductType;
import uz.nt.productservice.repository.ProductTypeRepository;
import uz.nt.productservice.repository.UnitRepository;
import uz.nt.productservice.service.ProductTypeService;
import uz.nt.productservice.service.mapper.ProductTypeMapper;
import uz.nt.productservice.service.mapper.impl.ProductTypeMapperImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductTypeServiceImpl implements ProductTypeService {

    private final ProductTypeRepository productTypeRepository;

    private final ProductTypeMapper productTypeMapper;
    private final UnitRepository unitRepository;

    @Override
    public ResponseDto<ProductTypeDto> add(ProductTypeDto dto){
        if(productTypeRepository.existsByName(dto.getName())){
            return ResponseDto.<ProductTypeDto>builder()
                    .code(-1).success(false).message("Product is all ready exists.").build();
        }
        ProductType entity = productTypeMapper.toEntity(dto);
        if (entity.getUnit() != null && entity.getUnit().getId() == null){
            unitRepository.save(entity.getUnit());
        }
        productTypeRepository.save(entity);

        return ResponseDto.<ProductTypeDto>builder()
                .code(0).success(true).message("OK").responseData(productTypeMapper.toDto(entity)).build();
    }

    @Override
    public ResponseDto<List<ProductTypeDto>> all(){
        List<ProductTypeDto> list = productTypeRepository.findAll()
                .stream().map(ProductTypeMapperImpl::toDtoWithoutProduct).collect(Collectors.toList());

        return list.size() == 0?
                ResponseDto.<List<ProductTypeDto>>builder()
                        .code(-2).success(false).message("List is empty.").build():
                ResponseDto.<List<ProductTypeDto>>builder()
                        .code(0).success(true).message("OK").responseData(list).build();
    }

    @Override
    public ResponseDto<Page<ProductTypeDto>> pagination(Integer p, Integer s){
        ResponseDto<Page<ProductTypeDto>> result = checkPageAndSize(p, s);
        if(result != null){
            return result;
        }

        PageRequest pageRequest = PageRequest.of(p, s);
        Page<ProductTypeDto> page =
                productTypeRepository.findAllByParentIdIsNotNull(pageRequest)
                        .map(ProductTypeMapperImpl::toDtoWithoutProduct);

        return ResponseDto.<Page<ProductTypeDto>>builder()
                .code(0).success(true).message("OK").responseData(page).build();
    }

    @Override
    public ResponseDto<Page<ProductTypeDto>> mainCategories(Integer page, Integer size) {
        ResponseDto<Page<ProductTypeDto>> result = checkPageAndSize(page, size);
        if(result != null){
            return result;
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProductTypeDto> response =
                productTypeRepository.findByParentIdIsNull(pageRequest)
                        .map(ProductTypeMapperImpl::toDtoWithoutProduct);
        return ResponseDto.<Page<ProductTypeDto>>builder()
                .code(0).success(true).message("OK").responseData(response).build();
    }

    @Override
    public ResponseDto<ProductTypeDto> oneById(Integer id){
        Optional<ProductType> optional = productTypeRepository.findById(id);
        if(optional.isPresent()){
            ProductTypeDto productTypeDto = optional.map(ProductTypeMapperImpl::toDtoWithoutProduct).get();
            return ResponseDto.<ProductTypeDto>builder()
                    .code(0).success(true).message("OK").responseData(productTypeDto).build();
        }

        return ResponseDto.<ProductTypeDto>builder()
                .code(-4).success(false).message("Data is not found").build();
    }

    public void delete(Integer id){
        productTypeRepository.deleteById(id);
    }

    private ResponseDto<Page<ProductTypeDto>> checkPageAndSize(Integer page, Integer size){
        if(page == null || page < 0){
            return ResponseDto.<Page<ProductTypeDto>>builder()
                    .code(-3).success(false).message("Page is null or below zero.").build();
        }
        if(size == null || size < 1){
            return ResponseDto.<Page<ProductTypeDto>>builder()
                    .code(-3).success(false).message("Size is null or below zero.").build();
        }

        return null;
    }
}

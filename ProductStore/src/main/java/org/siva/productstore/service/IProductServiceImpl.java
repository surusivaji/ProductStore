package org.siva.productstore.service;

import java.util.List;
import java.util.Optional;

import org.siva.productstore.models.Product;
import org.siva.productstore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Service
public class IProductServiceImpl implements IProductService {
	
	@Autowired
	private ProductRepository productRepository;
	
	@Override
	public Product saveProduct(Product product) {
		try {
			Product save = productRepository.save(product);
			return save;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public Page<Product> fetchAllProducts(int pageNo) {
		try {
			Pageable pageable = PageRequest.of(pageNo, 5);
			Page<Product> page = productRepository.findAll(pageable);
			return page;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public Product fetchProductById(Long id) {
		try {
			Optional<Product> optional = productRepository.findById(id);
			return optional.get();
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public Boolean deleteProduct(Product product) {
		try {
			productRepository.delete(product);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public List<Product> searchProduct(String search) {
		List<Product> products = productRepository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrBrandContainingIgnoreCase(search, search, search);
		return products;
	}

	public void removeSessionMessage() {
		HttpSession session = ((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest().getSession();
		session.removeAttribute("successMsg");
		session.removeAttribute("failMsg");
		session.removeAttribute("warningMsg");
	}
	

}

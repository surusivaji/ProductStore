package org.siva.productstore.service;


import java.util.List;

import org.siva.productstore.models.Product;
import org.springframework.data.domain.Page;

public interface IProductService {
	
	Product saveProduct(Product product);
	
	Page<Product> fetchAllProducts(int pageNo);
	
	Product fetchProductById(Long id);
	
	Boolean deleteProduct(Product product);
	
	List<Product> searchProduct(String search);

}

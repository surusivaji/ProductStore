package org.siva.productstore.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.siva.productstore.models.Product;
import org.siva.productstore.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	@Autowired
	private IProductService productService;
	
	@GetMapping("/")
	public String indexPage() {
		return "redirect:/dashboard";
	}
	
	@GetMapping("/dashboard")
	public String showDashboard(@RequestParam(defaultValue = "0") int pageNo, Model model) {
		Page<Product> page = productService.fetchAllProducts(pageNo);
		model.addAttribute("products", page.getContent());
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("isSearch", false);
		return "Dashboard";
	}
	
	@GetMapping("/addProduct")
	public String addProductPage() {
		return "AddProduct";
	}
	
	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("image") MultipartFile multipartFile, HttpSession session) {
		try {
			File file = new ClassPathResource("static/images").getFile();
			Path path = Paths.get(file.getAbsolutePath()+File.separator+"products"+File.separator+multipartFile.getOriginalFilename());
			Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			product.setImageFileName(multipartFile.getOriginalFilename());
			product.setCreatedAt(Date.valueOf(LocalDate.now()));
			Product saveProduct = productService.saveProduct(product);			
			System.out.println(saveProduct);
			if (saveProduct!=null) {
				session.setAttribute("successMsg", "Product added successfully...!!!");
				return "redirect:/dashboard";
			}
			else {
				session.setAttribute("failMsg", "Something went wrong...!!!");
				return "redirect:/addProduct";
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			session.setAttribute("failMsg", "Something went wrong...!!!");
			return "redirect:/addProduct";
		}
	}
	
	@GetMapping("/editProduct/{id}")
	public String editProductPage(@PathVariable("id") long id, Model model, HttpSession session) {
		Product product = productService.fetchProductById(id);
		if (product!=null) {
			model.addAttribute("product", product);
			return "EditProduct";
		}
		else {
			session.setAttribute("warningMsg", "Product is not found...!!!");
			return "redirect:/dashboard";
		}
	}
	
	@PostMapping("/updateProductInformation")
	public String updateProductInformation(@ModelAttribute Product product, @RequestParam("image") MultipartFile multipartFile, HttpSession session) {
		try {
			Product oldProduct = productService.fetchProductById(product.getId());
			if (multipartFile.isEmpty()) {
				product.setImageFileName(oldProduct.getImageFileName());
			}
			else {
				File file = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(file.getAbsolutePath()+File.separator+"products"+File.separator+multipartFile.getOriginalFilename());
				Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				product.setImageFileName(multipartFile.getOriginalFilename());
			}
			product.setCreatedAt(Date.valueOf(LocalDate.now()));
			Product saveProduct = productService.saveProduct(product);
			if (saveProduct!=null) {
				System.out.println(saveProduct);
				session.setAttribute("successMsg", "Product updated successfully...!!!");
				return "redirect:/dashboard";
			}
			else {
				session.setAttribute("failMsg", "Something went wrong...!!!");
				return "redirect:/editProduct/"+product.getId();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			session.setAttribute("warningMsg", "Something went wrong...!!!");
			return "redirect:/editProduct/"+product.getId();
		}
	}
	
	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable("id") Long id, HttpSession session) {
		Product product = productService.fetchProductById(id);
		if (product!=null) {
			Boolean deleteProduct = productService.deleteProduct(product);
			if (deleteProduct) {
				return "redirect:/dashboard";
			}
			else {
				session.setAttribute("failMsg", "Something went wrong...!!!");
				return "redirect:/dashboard";
			}
		}
		else {
			session.setAttribute("warningMsg", "Product is not found...!!!");
			return "redirect:/dashboard";
		}
	}
	
	@GetMapping("/search")
	public String searchProduct(@RequestParam("ch") String search, Model model) {
		model.addAttribute("isSearch", true);
		List<Product> searchProducts = productService.searchProduct(search);
		model.addAttribute("products", searchProducts);
		System.out.println(searchProducts);
		return "Dashboard";
	}

}

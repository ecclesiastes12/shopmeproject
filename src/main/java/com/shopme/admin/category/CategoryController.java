package com.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Category;

@Controller
public class CategoryController {

	@Autowired
	CategoryService service;
	
	@GetMapping("/categories")
	public String getAllCategories(Model model) {
		List<Category> listCategories = service.listAll();
		model.addAttribute("listCategories", listCategories);
		return "categories/categories";
	}
	
	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		List<Category> listCategories= service.listCategoriesUsedInForm();
		model.addAttribute("category", new Category());
		model.addAttribute("pageTitle", "Create New Category");
		model.addAttribute("listCategories", listCategories);
		return "categories/category_form";
	}
	
	//NB the value of @RequestParam must correspond with the attribute name,
	//name of the input field of image used in the form. In this case fileImage
	//used here corresponds with the attribute name, name used in the form
	@PostMapping("/categories/save")
	public String saveCategory(Category category,
			@RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes ra) throws IOException {
	
		//check if image is not empty
		if (!multipartFile.isEmpty()) {
			//gets the file name
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			//set file name
			category.setImage(fileName);
			//save category
			Category savedCategory = service.save(category);
			//NB category-image directory has to be created outside resource directory
			//in order to make it accessible by both ShopmeBackEnd and ShopmeFrondEnd
			//create file upload directory with the id of save category
			String uploadDir = "../category-images/" + savedCategory.getId();
			
			//clean dirctory. method call
			FileUploadUtil.cleanDir(uploadDir);
			
			//save the file. method call
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}else {
			service.save(category);
		}
		
		ra.addFlashAttribute("message","The category has been saved successfully.");
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable(name = "id") Integer id,Model model,
			RedirectAttributes ra) {
		
		
		try {
			//retrieves category id
			Category category = service.get(id);
			
			List<Category> listCategories = service.listCategoriesUsedInForm();
			
			model.addAttribute("category", category);
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("pageTitle", "Edit Category (ID: " + id + ")");
			
			return "categories/category_form";
		} catch (CategoryNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return "redirect:/categories";
		}
		
	}
	
	@GetMapping("categories/{id}/enabled/{status}")
	public String enabledCategoryEnabledStatus(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "status") boolean enabled,
			RedirectAttributes ra) {
		service.updateCategoryEnabledStatus(id, enabled);

		String status = "";
		if (enabled) {
			status += "enabled";
		} else {
			status += "disabled";
		}
		 
		String message = "The category ID " + id + " has been " + status;
		/*
		 * String status = enabled ? "enabled" : "disabled"; String message =
		 * "The category ID " + id + " has been " + status;
		 */
		ra.addFlashAttribute("message", message);
		
		return "redirect:/categories";
	}
	
	/*
	 * @GetMapping("/categories/delete/{id}") public String
	 * deleteCategory(@PathVariable(name = "id") Integer id, Model model,
	 * RedirectAttributes redirectAttributes) { try { service.delete(id);
	 * 
	 * String categoryDir = "../category-images/" + id;
	 * FileUploadUtil.removeDir(categoryDir); //remove categpry-image directory
	 * after category is delete
	 * 
	 * redirectAttributes.addFlashAttribute("message", "The category ID " + id +
	 * " has been deleted succesfully");
	 * 
	 * } catch (CategoryNotFoundException ex) {
	 * 
	 * redirectAttributes.addFlashAttribute("message", ex.getMessage()); }
	 * 
	 * return "redirect:/categories"; }
	 */
	
	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable(name = "id") Integer id, 
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			String categoryDir = "../category-images/" + id;
			FileUploadUtil.removeDir(categoryDir);
			
			redirectAttributes.addFlashAttribute("message", 
					"The category ID " + id + " has been deleted successfully");
		} catch (CategoryNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/categories";
	}	
}

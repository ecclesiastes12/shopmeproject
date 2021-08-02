package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;

@Service
@Transactional
public class CategoryService {

	@Autowired
	CategoryRepository repo;
	
	//method that list all categories
	/*
	 * public List<Category> listAll(){ return (List<Category>)
	 * repo.findAll(); }
	 */
	
	//method that list root or parent categories
	public List<Category> listAll(){
		
		List<Category> rootCategories = repo.findRootCategories();
		return listHierarchicalCategories(rootCategories);
	}
	
	//private method that list categories in a hierarchical format
	private List<Category> listHierarchicalCategories(List<Category> rootCategories){
		//creates an array object that is converted to a list of categories
		List<Category> hierarchicalCategories = new ArrayList<>();
		
		//iterate through each root category
		for(Category rootCategory : rootCategories) {
			
			//adds full details of root category
			hierarchicalCategories.add(Category.copyFull(rootCategory));
			
			//derived the children of root category
			Set<Category> children = rootCategory.getChildren();
			
			//iterate through children of root category
			for(Category subCategory : children) {
				//appends "--" to the names of the subCategory
				String name = "--" + subCategory.getName();
				
				hierarchicalCategories.add(Category.copyFull(subCategory,name));
				
				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1);
			}
		}
		
		return hierarchicalCategories;
	}
	
	//method that list sub-Hierarchical categories
	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories,
			Category parent, int subLevel) {
		//set or get the children of the parent category
		Set<Category> children = parent.getChildren();
		
		int newSubLevel = subLevel + 1;
		
		//loops through each sub-category
		for(Category subCategory : children) {
			String name = "";
			for(int i =0; i < newSubLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();
			
			hierarchicalCategories.add(Category.copyFull(subCategory, name));
			
			listSubHierarchicalCategories(hierarchicalCategories,subCategory,newSubLevel);
		}
		
		
	}
	
	//method that save category
	public Category save(Category category) {
		
		//for updating categories
		//checks if the form is in the edit mode
		boolean isUpdatingCategory = (category.getId() != null);
		
		
		if(isUpdatingCategory) {
			//retrieves category details from the db
			Category existingCategory = repo.findById(category.getId()).get();
			
			//checks if image is empty
			if(category.getImage().isEmpty()) {
				//retrieve image of an existing category from db if category image field is empty
				category.setImage(existingCategory.getImage());
			}else {
				//set new image if category image field is not empty
				category.setImage(category.getImage());
			}
			
		}
		return repo.save(category);
	}
	
	//method that list categories in the form to be used
	public List<Category> listCategoriesUsedInForm(){
		//list of categories used in form
		List<Category> categoriesUsedInForm = new ArrayList<>();
		
		//retrieves all categories from the db
		Iterable<Category> categoriesInDB = repo.findAll();
		
		//iterate through each category in the db
		for(Category category : categoriesInDB) {
			//if category equls to null it means the category
			//is a root level(top level) category thus parent category
			if(category.getParent() == null) {
				categoriesUsedInForm.add(Category.copyIdAndName(category));
				
				//gets the children of the category which is a set of collection of objects
				//of type category
				Set<Category> children = category.getChildren();
				
				//iterates through each child category
				for(Category subCategory : children) {
					
					//gets the name of each child category under a sub-category
					// "--" is to indicate the hierarchy code structure
					String name = "--" + subCategory.getName();

					categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
					
					//method call  
					listSubCategoriesUsedInForm(categoriesUsedInForm,subCategory,1);
				}
			}
		}
		
		return categoriesUsedInForm;
	}
	
	//method to print the children of each sub-category
			private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent,int subLevel) {
				//increase subLevel by one
				int newSubLevel = subLevel + 1;
				
				Set<Category> children = parent.getChildren();
				//print the name of each subCategory under each parent category
				for(Category subCategory : children) {
					String name = "";
					for(int i = 0; i < newSubLevel; i++) {
						name += "--" ; // appends "--" to String name = ""  and also for indicating the subLevel
					}
					//appends the name of each subCategory to "--"
					name += subCategory.getName();
					
					categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
					
					listSubCategoriesUsedInForm(categoriesUsedInForm,subCategory, newSubLevel);
				}
			}
	
			//method that generate custome error message if category id is not found
			public Category get(Integer id) throws CategoryNotFoundException {
				try {
					return repo.findById(id).get();
				} catch (NoSuchElementException ex) {
					// custom exception
					throw new CategoryNotFoundException("Could not find any category with ID " + id);
				}

			}
			
			//method that enabled or disable category status
			public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
				 repo.updateCategoryEnabledStatus(id, enabled);
			}

			
//			//method that delete category by id
//			public void delete(Integer id) throws CategoryNotFoundException {
//				
//				Long countById = repo.countById(id); //method call
//				
//				//throws exception if count by Id is null or equal to 0
//				// thus category Id does not exist in the db
//				if(countById == null || countById == 0) {
//					throw new CategoryNotFoundException("Could not find the category with ID " + id);
//				}
//				
//				repo.deleteById(id);
//				
//			}
			
			public void delete(Integer id) throws CategoryNotFoundException {
				Long countById = repo.countById(id);
				if (countById == null || countById == 0) {
					throw new CategoryNotFoundException("Could not find any category with ID " + id);
				}
				
				repo.deleteById(id);
			}	
}

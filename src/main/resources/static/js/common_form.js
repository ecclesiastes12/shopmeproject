/* close the create new user page*/
 	$(document).ready(function(){
 		$("#buttonCancel").on("click", function(){
		/*
			NB this is a static url referece in javascript
			use this type of code when you write you js in the 
			same page
			
			window.location="[[@{/users}]]";
		*/
		
		// Dynamic url reference
		window.location = moduleURL; 
 			
 		});
 		
 		
 		/*for images preview*/
 		$("#fileImage").change(function(){
 		
 		/* checks image file size is more than 1MB*/
 		fileSize = this.files[0].size;
 		
 		//alert("File Size: " + fileSize);
 		
 		if(fileSize > 1048576){
 			this.setCustomValidity("You must an image less than 1MB");
 			this.reportValidity();
 		}else{
 			this.setCustomValidity("");
 			showImageThumbnail(this);
 		}
 			
 		});
 		
 	});
 	
 	function showImageThumbnail(fileInput){
 		var file = fileInput.files[0];
 		var reader = new FileReader();
 		reader.onload = function(e){
 			$("#thumbnail").attr("src", e.target.result);
 		}
 		
 		reader.readAsDataURL(file);
 	}
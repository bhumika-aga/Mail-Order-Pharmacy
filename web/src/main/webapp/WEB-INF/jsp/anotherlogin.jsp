<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ page errorPage = "error.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8" />

<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-giJF6kkoqNQ00vy+HMDP7azOuL0xtbfIcaT9wjKHr8RbDVddVHyTfAAsrekwKmP1"
	crossorigin="anonymous">
<link rel="stylesheet" href="style/index.css" />
<!-- <link rel="stylesheet" href="stylepopupbro.css" /> -->
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>

<title>Mail Order Pharmacy</title>
</head>

<body>

	<nav class="navbar navbar-inverse ">
		<a href="/webportal/home" class="navbar-brand"><img width="60px" height="60px"src="images/lo2.png"> Mail Order Pharmacy 	
		  </a>
		
		<div class="" id="navbarText">
			<ul class="navbar-nav mr-auto text-center">
				<!-- <li class="nav-item"> -->
				<a class="nav-link"
					onclick="document.getElementById('loginModal').style.display='block'"
					id="loginBtn">Login</a>
				<!-- </li> -->
			</ul>
		</div>
	</nav>

	<br>
	<br>
	<br>

	<div id="loginModal" >

		<form class="modal-content animate" name="loginform" model="UserData"
			method="post" action="anotherlogin">
			<h2 class="center"><img width="60px" height="60px"src="images/lo2.png">Login</h2>
			<span
				onclick="document.getElementById('loginModal').style.display='none'"
				class="closeModal" title="Close Modal">&times;</span> 
			<br>
			<div class="containernew">
				<fieldset>
					<input type="text" placeholder="username" name="userid" id="username"
						required> 
				</fieldset>
				<!-- <label for="password"><b>Password</b></label> -->
				<br>
				<fieldset>
					<input type="password" name="upassword" placeholder="password"
						id="password" required> 
				
				</fieldset>
				<button type="submit" id="loginSubmit">Login</button>


			</div>
			<p>${errormsg}</p>
		</form>
	</div>
	<br>


	<script>
		var modal = document.getElementById('loginModal');

		window.onclick = function(event) {
			if (event.target == modal) {
				modal.style.display = "none";
			}
		}
	</script>
	<style>
body {
  background-image:linear-gradient(to right, rgba(0,0,0,0.4), rgba(0,0,0,0.4)), url('images/bg3.jpeg');
}
</style>
</body>
</html>
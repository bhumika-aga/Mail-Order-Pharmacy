<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
<style>
a{
color:#FFFDD0;
}
</style>
<title>Mail Order Pharmacy</title>
</head>

<body>

	<nav class="navbar navbar-inverse ">
		<a href="index.html" class="navbar-brand"><img width="60px" height="60px" src="images/lo2.png"> Mail Order Pharmacy 	
		  </a>
		
		<div class="" id="navbarText">
			<ul class="navbar-nav mr-auto text-center">
				<!-- <li class="nav-item"> -->
				<a class="nav-link"
					href="loginpage"
					id="loginBtn" style="color:#FFFFFF">Login</a>
				<!-- </li> -->
			</ul>
		</div>
	</nav>

	<br>
	<br>
	<br>

	<div id="loginModal" class="modal">

		<form class="modal-content animate" name="loginform" model="UserData"
			method="post" action="login">
			<h2 class="center><img width="60px" height="60px" src="images/lo2.png">Login</h2>
			<span
				onclick="document.getElementById('loginModal').style.display='none'"
				class="closeModal" title="Close Modal">&times;</span> <br>
			<br>
			<div class="container">
				<!-- <label for="username"><b>Username</b></label> -->
				<fieldset>
					<input type="text" placeholder="" name="userid" id="username"
						required> <span class="floating-label">Username</span>
				</fieldset>
				<!-- <label for="password"><b>Password</b></label> -->
				<fieldset>
					<input type="password" name="upassword" placeholder=""
						id="password" required> <span class="floating-label">Password</span>
					<!-- <span class="floating-label">Name</span>
            <input id="name" name="name" type="text" class="inputText" required />
             -->
				</fieldset>
				<button type="submit" id="loginSubmit">Login</button>


				<h6 class="right">
					New user? Signup <a href="#">here</a>
				</h6>
			</div>
			<p>${errormsg}</p>
		</form>
	</div>
	<br>
	<div align="left">
		<div class="spinner-grow text-muted"></div>
<div class="spinner-grow text-primary"></div>
<div class="spinner-grow text-success"></div>
<div class="spinner-grow text-info"></div>
<div class="spinner-grow text-warning"></div>
<div class="spinner-grow text-danger"></div>
<div class="spinner-grow text-secondary"></div>
<div class="spinner-grow text-dark"></div>
<div class="spinner-grow text-light"></div>
	</div>


<div align="center">
	<h1 class="center"><b>Mail Order Pharmacy </b>	</h1>
	
	</div>
	<br>
	
	<div align="right">
	<div class="spinner-grow text-muted"></div>
<div class="spinner-grow text-primary"></div>
<div class="spinner-grow text-success"></div>
<div class="spinner-grow text-info"></div>
<div class="spinner-grow text-warning"></div>
<div class="spinner-grow text-danger"></div>
<div class="spinner-grow text-secondary"></div>
<div class="spinner-grow text-dark"></div>
<div class="spinner-grow text-light"></div>
	
</div>

	<br>
	<div>
<div id="carouselExampleControls" class="carousel slide" data-ride="carousel">
  <div class="carousel-inner">
    <div class="carousel-item active">
      <img class="d-block w-100" src="images/f1.jpg" alt="First slide">
    </div>
    <div class="carousel-item">
      <img class="d-block w-100" src="images/f2.jpg" alt="Second slide">
    </div>
    <div class="carousel-item">
      <img class="d-block w-100" src="images/f5.jpg" alt="Third slide">
    </div>
  </div>
  <a class="carousel-control-prev" href="#carouselExampleControls" role="button" data-slide="prev">
    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
    <span class="sr-only">Previous</span>
  </a>
  <a class="carousel-control-next" href="#carouselExampleControls" role="button" data-slide="next">
    <span class="carousel-control-next-icon" aria-hidden="true"></span>
    <span class="sr-only">Next</span>
  </a>
</div>
	</div>
	<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
	
	
	

	
	
	
	
<!-- 	<div align="center">
	<h4>Mail-order pharmacies operate through your health insurance plan and <br>can be cheaper and more convenient than getting your medications from a local pharmacy.</h4>
	<br>
	<h4>A “mail-order” prescription means that you don’t have to pick up your <br>medication from a pharmacy; instead, it is delivered to your doorstep.</h4>
	</div> -->










<!-- 
	<img class="footImg" src="images/mailorder-updated.png"> -->


	<script>
		var modal = document.getElementById('loginModal');

		window.onclick = function(event) {
			if (event.target == modal) {
				modal.style.display = "none";
			}
		}
	</script>
</body>
</html>
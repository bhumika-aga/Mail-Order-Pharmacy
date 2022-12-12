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
	
	
<style type="text/css">
.image {
	width: 20%;
	margin-top: -15%;
}

.reverse {
	transform: rotateY(180deg);
	margin-left: 80%;
	margin-top: -19%;
}
</style>

<title>Mail Order Pharmacy</title>
</head>

<body >

	<nav class="navbar ">
		<a href="home" class="navbar-brand">
		   <img width="60px" height="60px" src="images/lo2.png">
		   Mail Order Pharmacy
		</a>

		
		<div class="dropdown">
             <button class="btn btn-dark dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
              Services
             </button>
                <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                      <a class="dropdown-item" href="supportedDrugs">Available Drugs</a>
                      <a class="dropdown-item" href="search">Search Drugs</a>
                      <a class="dropdown-item" href="prescriptionform">Subscribe</a>
                      <a class="dropdown-item" href="subscriptions">Subscriptions</a> 
                      <a class="dropdown-item" href="/webportal/getAllRefill">Refill Status</a> 
                      <a class="dropdown-item"  href="refillDateEntry">Refill Due Date</a>
                      <a class="dropdown-item" href="logout">Logout</a>
               </div>
       </div>
    </nav>
	

	
	<div class="landing-container">
	<div class="typ container mt-5">
		<h1>
			<p class="typewrite" data-period="2000"
				data-type='[ "Welcome to Mail Order Pharmacy....", "Diagnose the present, foretell the future..", "We got all your health needs covered!!", "Count on us.." ]'>
				<span class="wrap"></span>
			</p>
		</h1>
	</div>

	<div class="container">
		<div class="row gx-5 gy-5">
			<div class="col-md-6 col-12">
				<div class="bg-black" >
					<div class="mt-2">
						<h3>Bringing the medicine at your door</h3>
					</div>
					<div>
						<p>Get Medicines Delivered at your doorstep at anytime and anywhere..</p>
					</div>
				</div>
			</div>
			
			<div class="row gx-8 gy-8">
				<div class="p-3 bg-grey">
				
					<div class="col-12 d-flex mt-2 p-2 text-wrap text-center" style="color:#00FFBF;background-color:#000000">
					
						<center><h3>Save upto 10% extra & enjoy FREE delivery on orders above &#8377;1000. HURRY UP!!</h3></center>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="containerwel mb-5">
		<div class="row">
			<div class="col-6">
				<img src="images/bgno-removebg-preview.png">
			
		</div>
	</div>

</div>

	<!-- <img src="COVID_19_IEC_ENG86.jpg" class="image"> -->
	<!--<img src="images/covid1.jpg" style="width: 95%; margin-left: 2.5%;">-->

	<br>
	<br>
	<br>
	<br>




	<script>
		var TxtType = function(el, toRotate, period) {
			this.toRotate = toRotate;
			this.el = el;
			this.loopNum = 0;
			this.period = parseInt(period, 10) || 2000;
			this.txt = '';
			this.tick();
			this.isDeleting = false;
		};

		TxtType.prototype.tick = function() {
			var i = this.loopNum % this.toRotate.length;
			var fullTxt = this.toRotate[i];

			if (this.isDeleting) {
				this.txt = fullTxt.substring(0, this.txt.length - 1);
			} else {
				this.txt = fullTxt.substring(0, this.txt.length + 1);
			}

			this.el.innerHTML = '<span class="wrap">' + this.txt + '</span>';

			var that = this;
			var delta = 200 - Math.random() * 100;

			if (this.isDeleting) {
				delta /= 2;
			}

			if (!this.isDeleting && this.txt === fullTxt) {
				delta = this.period;
				this.isDeleting = true;
			} else if (this.isDeleting && this.txt === '') {
				this.isDeleting = false;
				this.loopNum++;
				delta = 500;
			}

			setTimeout(function() {
				that.tick();
			}, delta);
		};

		window.onload = function() {
			var elements = document.getElementsByClassName('typewrite');
			for (var i = 0; i < elements.length; i++) {
				var toRotate = elements[i].getAttribute('data-type');
				var period = elements[i].getAttribute('data-period');
				if (toRotate) {
					new TxtType(elements[i], JSON.parse(toRotate), period);
				}
			}
		
		};

		Resources
	</script>


	<script>
		function openNav() {
			document.getElementById("mySidebar").style.width = "300px";
			

		}

		/* Set the width of the sidebar to 0 and the left margin of the page content to 0 */
		function closeNav() {
			document.getElementById("mySidebar").style.width = "0";
		
		}
	</script>

<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
	
	

	
		<style>
.landing-container {
 background-repeat: no-repeat;
  background-image: linear-gradient(to right, rgba(0,0,0,0.4), rgba(0,0,0,0.4)), url('images/bg9.jpg'); 
  background-size: 100% 100%;
  padding-top:7rem;
}
</style>
	
	
</body>
</html>
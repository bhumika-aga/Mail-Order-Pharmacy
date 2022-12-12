<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%> 
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
 
 
 <!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />

    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/css/bootstrap.min.css" rel="stylesheet"
        integrity="sha384-giJF6kkoqNQ00vy+HMDP7azOuL0xtbfIcaT9wjKHr8RbDVddVHyTfAAsrekwKmP1" crossorigin="anonymous">
    <link rel="stylesheet" href="style/index.css" />
    <!-- <link rel="stylesheet" href="stylepopupbro.css" /> -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>

    <title>Mail Order Pharmacy</title>
    <style type="text/css">
    .flip-card {
  background-color: transparent;
  width: 300px;
  height: 300px;
  perspective: 1000px;
  margin-right: 5px;
}

.flip-card-inner {
  position: relative;
  width: 100%;
  height: 100%;
  text-align: center;
  transition: transform 0.6s;
  transform-style: preserve-3d;
  box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2);
}

.flip-card:hover .flip-card-inner {
  transform: rotateY(180deg);
}

.flip-card-front, .flip-card-back {
  position: absolute;
  width: 100%;
  height: 100%;
  -webkit-backface-visibility: hidden;
  backface-visibility: hidden;
}

.flip-card-front {
  background-color: #bbb;
  color: black;
}

.flip-card-back {
  background-color: #2980b9;
  color: white;
  transform: rotateY(180deg);
}
    </style>
</head>

<body>



    <nav class="navbar navbar-inverse ">
        <a href="home" class="navbar-brand"><img width="60px" height="60px"src="images/lo2.png"> Mail Order Pharmacy</a>

        <!-- <button class="navbar-toggler navbar-toggler collapsed" type="button" data-toggle="collapse"
            data-target="#navbarText" aria-controls="navbarsExampleDefault" aria-expanded="false"
            aria-label="Toggle navigation">
            <span class="my-1 mx-2 close">CLOSE</span>
            <span class="navbar-toggler-icon">OPEN</span>
        </button> -->

   <!--      <div class="" id="navbarText">
            <ul class="navbar-nav mr-auto text-center">
                
                <li class="nav-item">
                    <div id="main">
                        <button class="openbtn" onclick="openNav()">Services</button>
                    
                </li>
            </ul>
        </div> -->
        
        	<div class="dropdown">
  <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
 Services
  </button>
  <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
    <a class="dropdown-item" href="supportedDrugs">Available Drugs</a>
    <a class="dropdown-item" href="search">Search Drugs</a>
    <a class="dropdown-item" href="prescriptionform">Subscribe</a>
      <a class="dropdown-item" href="subscriptions">Subscriptions</a> 
        <a class="dropdown-item" href="subscriptions">Adhoc Refill</a> 
          <a class="dropdown-item" href="refillstatus">Refill Status</a> 
           <a class="dropdown-item"  href="refillDateEntry">Refill Due Date</a>
            <a class="dropdown-item" href="logout">Logout</a>
  </div>
</div>
    </nav>
<!--     <div id="mySidebar" class="sidebar">
        <br>
        <a href="javascript:void(0)" class="closebtn" onclick="closeNav()">&times;</a>
        <a href="supportedDrugs">Available Drugs</a>
        <a href="search">Search Drugs</a>
        <a href="prescriptionform">Subscribe</a>
        <a href="subscriptions">Subscriptions</a>
        <a href="subscriptions">Adhoc Refill</a>
        <a href="refillstatus">Refill Status</a>
        <a href="refillDateEntry">Refill Due Date</a>
        <a href="logout">Logout</a>
      </div>
       -->
    
     
    <br><br><br><br><br>
    <div align="center" class="form-group">
<%--     <form name="searchDrugs" model="searchModel" action="/webportal/searchDrugById" method="post">
    <h1>Search Drug by Id :</h1>
    <input type="text" name ="id" >
    <button type="submit" class="btn btn-outline-success btn-lg mt-2" style="margin-left:55%;">Submit</button>
    </form>
	<br>
	<form name="searchDrugs" model="searchModel" action="/webportal/searchDrugByName" method="post">
    <h1>Search Drug by Name :</h1>
    <input  type="text" name ="name" >
    <button type="submit" class="btn btn-outline-success btn-lg mt-2" style="margin-left:55%;">Submit</button>
    </form> --%>
    
    
     <form name="searchDrugs"  model="searchModel" action="/webportal/searchDrugById" method="post">
    <div class="form-group">
      <label for="id"><h2>Search drug by ID   :</h2></label>
       <input type="text" name ="id" >
    </div >
    <div class="btnsbt"><button type="submit" class="btn btn-outline-success btn-lg mt-2" >Submit</button></div>
  </form>
    
  <form name="searchDrugs" model="searchModel" action="/webportal/searchDrugByName" method="post">
   <div class="form-group">
      <label for="id"><h2>Search drug by Name :</h2></label>
       <input type="text" name ="name" >
    </div >
    <div class="btnsbt"><button type="submit" class="btn btn-outline-success btn-lg mt-2" >Submit</button></div>
    </form>
    
    
        <br><br> <br><br> <br><br>
    </div>
    <div align="center">
    <h1><b>${drugdetails.msg}</b></h1>
	<section id="services">
        <div class="container">
            <h6>${drugdetails.drugId}</h6>
            <h6>${drugdetails.drugName}</h6>
            <h6>${drugdetails.manufacturer}</h6>
            <h6>${drugdetails.manufactureDate}</h6>
            <h6>${drugdetails.expiryDate}</h6>
            <c:forEach items="${drugdetails.druglocationQuantities}" var="itr1">
                   <p>${itr1.location} : ${itr1.quantity}</p><br>
            </c:forEach>
        </div>
    </section>
  	</div>

    <br><br><br><br>


    <script>
        function openNav() {
document.getElementById("mySidebar").style.width = "300px";
//   document.getElementById("main").style.marginRight = "250px";
//   document.getElementsById("closer").style.marginRight = "250px";

}

/* Set the width of the sidebar to 0 and the left margin of the page content to 0 */
function closeNav() {
document.getElementById("mySidebar").style.width = "0";
//   document.getElementById("main").style.marginRight = "0";
//   document.getElementsById("closer").style.marginRight = "0";
}
    </script>
        
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
	
	<style>
body {
  background-image: url('images/bg8.jpg');
   background-repeat: no-repeat;
}
</style>
</body>

</html>
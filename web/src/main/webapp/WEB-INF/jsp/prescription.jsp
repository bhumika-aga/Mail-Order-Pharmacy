<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ page errorPage = "error.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

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
    .w-50{
    	min-width:60%;
    }
    	.half-entry{
    width: 50%;
    float: left;
  }

  } 
    </style>
</head>

<body>



    <nav class="navbar navbar-inverse ">
        <a href="home" class="navbar-brand"><img width="60px" height="60px"src="images/lo2.png">Mail Order Pharmacy</a>



        
        
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
 
    
      <script>
          function openNav() {
  document.getElementById("mySidebar").style.width = "300px";

}

/* Set the width of the sidebar to 0 and the left margin of the page content to 0 */
function closeNav() {
  document.getElementById("mySidebar").style.width = "0";

}
      </script>
    <br><br><br><br><br><br>
    <h1 class="center">Prescription Details</h1>
	<h3 class="center alrtmsg">${msg}</h3>
    <div class="container mt-5">

        <form name="prescriptionform" model="prescriptionDetails" method="post" onsubmit="return validate()"
            action="subscribe">
            <div class="half-entry">
                <div class="row">
                    <div class="col d-flex justify-content-left">
                        <label for="insuranceProvider"><b>Insurance Provider</b></label>
                    </div>
                    <div class="col d-flex justify-content-left">
                        <input type="text" id="insurancePolicyNumber" name="insuranceProvider" class="w-50" required tabIndex="1">
                        <span class="text-danger" id="insuranceProviderSpan"></span><br>
                    </div>
                </div>
                <div class="row">
                    <div class="col d-flex justify-content-left">
                        <label for="policyNumber"><b>Policy Number</b></label>

                    </div>
                    <div class="col d-flex justify-content-left">

                        <input type="text" id="insuranceProvider" name="policyNumber" class="w-50" required tabIndex="2">
                        <span class="text-danger" id="policyNumberSpan"></span><br>
                    </div>
                </div>
                <div class="row">
                    <div class="col d-flex justify-content-left">
                        <label for="doctorName"><b>Doctor Name</b></label><br>
                    </div>
                    <div class="col d-flex justify-content-left">
                        <input type="text" id="drugName" name="doctorName" class="w-50" required tabIndex="3">
                        <span class="text-danger" id="doctorNameSpan"></span><br>
                    </div>
                </div>
                <div class="row">
                    <div class="col d-flex justify-content-left">
                        <label for="prescriptionDate"><b>Prescription Date</b></label><br>
                    </div>
                    <div class="col d-flex justify-content-left">
                        <input type="date" id="prescriptionDate" name="prescriptionDate" class="w-50" required tabIndex="4">
                        <span class="text-danger" id="prescriptionDateSpan"></span><br>
                    </div>
                </div>
           
                <div class="row">
                    <div class="col d-flex justify-content-left">
                        <label for="Location"><b>Your Location</b></label>
                    </div>
                    <div class="col d-flex justify-content-left">
                        <input type="text" id="quantity" name="memberLocation" class="w-50" required tabIndex="5">
                        <span class="text-danger" id="memberLocationSpan"></span><br>
                    </div>
                </div>
            </div>
            <div class="half-entry">
                <div class="row">
                    <div class="col d-flex justify-content-left">
                        <label for="drugName"><b>Medicine Name</b></label>
                    </div>
                    <div class="col d-flex justify-content-left">
                        <input type="text" id="Dosage" name="drugName" class="w-50" required tabIndex="6">
                        <span class="text-danger" id="drugNameSpan"></span><br>
                    </div>
                </div>
                <div class="row">
                    <div class="col d-flex justify-content-left">
                        <label for="dosageDefinition"><b>Dosage Definitions</b></label>
                    </div>
                    <div class="col d-flex justify-content-left">
                        <input type="text" id="Dosage" name="dosageDefinition" class="w-50" required tabIndex="7">
                        <span class="text-danger" id="dosageDefinitionSpan"></span> <br>
                    </div>
                </div>
                <div class="row">
                    <div class="col d-flex justify-content-left">
                        <label for="quantity"><b>Quantity</b></label>
                    </div>
                    <div class="col d-flex justify-content-left">
                        <input type="number" id="doctorName" name="quantity" min="1" class="w-50" required tabIndex="8">
                        <span class="text-danger" id="quantitySpan"></span><br>
                    </div>
                </div>

                <div class="row">
                    <div class="col d-flex justify-content-left">
                        <label for="courseDuration"><b>Course Duration</b></label>
                    </div>
                    <div class="col d-flex justify-content-left">
                        <input type="number" id="memberLocation" name="courseDuration" min="1" class="w-50" required tabIndex="9">
                        <span class="text-danger" id="courseDurationSpan"></span><br>
                    </div>
                </div>
            </div>
        	
            <button type="submit" class="btn btn-success">Submit</button>

        </form>

    </div>
    
    <br><br>
    
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
	
    <br><br><br><br>

	<script src="js/Prescription.js"></script>
	<script type="text/javascript">
        $(document).on('keypress', 'input,select', function (e) {
            if (e.which == 13) {
                e.preventDefault();
                var $next = $('[tabIndex=' + (+this.tabIndex + 1) + ']');
                console.log($next.length);
                if (!$next.length) {
                    $next = $('[tabIndex=1]');
                }
                $next.focus().click();
            }
        });
    </script>
	<style>
body {
  background-image: url('images/bg3.jpeg');
   background-repeat: no-repeat;
   background-size: 180% 150%;
}

.half-entry{
font-weight:bold;
}
</style>
</body>

</html>
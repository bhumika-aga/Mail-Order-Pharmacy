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

<link
	href="https://fonts.googleapis.com/css?family=Open+Sans:300,300i,400,400i,700,700i|Poppins:300,400,500,700"
	rel="stylesheet">

<!-- Vendor CSS Files -->
<link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link href="vendor/font-awesome/css/font-awesome.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">

<script
	src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
<title>Mail Order Pharmacy</title>
</head>

<body>



	<nav class="navbar navbar-inverse ">
		<a href="/webportal/home" class="navbar-brand"><img width="60px"
			height="60px" src="images/lo2.png">Mail Order Pharmacy</a>




		<div class="dropdown">
			<button class="btn btn-dark dropdown-toggle" type="button"
				id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true"
				aria-expanded="false">Services</button>
			<div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
				<a class="dropdown-item" href="supportedDrugs">Available Drugs</a> <a
					class="dropdown-item" href="search">Search Drugs</a> <a
					class="dropdown-item" href="prescriptionform">Subscribe</a> <a
					class="dropdown-item" href="subscriptions">Subscriptions</a> 
					 <a class="dropdown-item" href="/webportal/getAllRefill">Refill Status</a> 
           <a class="dropdown-item"  href="refillDateEntry">Refill Due Date</a>
					<a class="dropdown-item" href="logout">Logout</a>
			</div>
		</div>
	</nav>

	<br>
	<br>
	<br>
	<div class="containersubsbig">

		<br> <br>
		<h1 align="center" color><b>Your Refill</b></h1>
		<h3 class="center">${message}</h3>
		<div class="container bcontent">

			<div class="containersubs">
				<table class="table table-fluid" id="myTable">
					<thead>
						<tr>
							<th>UserName</th>
							<th>Refill Date</th>
							<th>Refill Quantity</th>
							<th>Payment Status</th>
						</tr>
					</thead>
					<tbody>
						<c:choose>
							<c:when test="${not empty list}">
								<c:forEach items="${list}" var="refillOrder">

									<tr>
										<td>${refillOrder.memberId }</td>
										<td>${refillOrder.refilledDate }</td>
										<td>${refillOrder.quantity }</td>
										<td><c:choose>
												<c:when test="${refillOrder.payStatus}">
        										Paid
   												 </c:when>
												<c:otherwise>
													Not Paid
												</c:otherwise>
											</c:choose></td>

										</form>
									</tr>


								</c:forEach>
							</c:when>
							<c:otherwise>
								<h3>${ackmsg}</h3>
								<br>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
				<script>
					$(document).ready(function() {
						$('#myTable').DataTable();
					});
				</script>
			</div>

		</div>
	</div>


	<script>
		function openNav() {
			document.getElementById("mySidebar").style.width = "300px";
		
		}

		/* Set the width of the sidebar to 0 and the left margin of the page content to 0 */
		function closeNav() {
			document.getElementById("mySidebar").style.width = "0";
			
		}
	</script>

	<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"
		integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
		crossorigin="anonymous"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"
		integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
		crossorigin="anonymous"></script>
	<script
		src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"
		integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
		crossorigin="anonymous"></script>



	<style>
body {
	background-repeat: no-repeat;
	background-image: url('images/bg3.jpeg');
	background-size: 100% 500%;
}
</style>


</body>

</html>
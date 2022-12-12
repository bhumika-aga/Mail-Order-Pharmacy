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
	<style type="text/css">
	
body{
background-image:url('images/bg3.jpeg');
background-color:smokewhite;
}

.navbar {
    position: fixed;
    display: flex;
    padding: .4rem 1rem;
     
    background-color: rgb(64, 128, 128) !important;
    -webkit-box-sizing: border-box;
            box-sizing: border-box;
    line-height: 1.649999;
    width: 100%;
    -webkit-box-shadow: 0px 3px 5px black;
            box-shadow: 0px 3px 5px black;
            z-index: 999;
  }
  
  .navbar-nav {
    display: -ms-flexbox;
    display: -webkit-box;
    display: flex;
    margin-top: -6vh;
    /* margin-right: 8%; */
    -ms-flex-direction: row;
    -webkit-box-orient: horizontal;
    -webkit-box-direction: normal;
            flex-direction: row;
    list-style: none;
  }
  
  /* .nav-item {
    margin-bottom: -100px;
    padding-left: 2vw;
  } */
  #loginBtn{
    margin-top: 40px;
  }
  .nav-link {
    display: block;
    color: white;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    text-decoration: none;
    cursor: pointer;
  }
  
  .nav-link:hover {
    color:lightgray;
  }
  
  .nav-link:active {
    color: lightgray;
  }
  
  .navbar-brand {
  
    display: inline-block;
    padding-top: .3125rem;
    padding-bottom: .3125rem;
    margin-right: 1rem;
    font-size: 1.25rem;
    line-height: inherit;
    white-space: nowrap;
    color: whitesmoke;
    padding-left: 2rem;
  /* margin-left: 1rem; */
       font-family:Lucida Handwriting;
    text-decoration: none;
  }
  
  .navbar-brand:hover {
    color: lightgray;
  }
  
  .navbar-brand:active {
    color: lightgray;
  }
  /* The sidebar menu */
.sidebar {
  height: 100%; /* 100% Full-height */
  width: 0; /* 0 width - change this with JavaScript */
  position: fixed; /* Stay in place */
  /* z-index: 1; Stay on top */
  top: 0;
  right: 0;
  background-color: #111; /* Black*/
  overflow-x: hidden; /* Disable horizontal scroll */
  padding-top: 60px; /* Place content 60px from the top */
  transition: 0.5s; /* 0.5 second transition effect to slide in the sidebar */
  z-index: 9;
}

/* The sidebar links */
.sidebar a {
  padding: 8px 8px 8px 32px;
  text-decoration: none;
  font-size: 25px;
  color: #818181;
  display: block;
  transition: 0.3s;
}

/* When you mouse over the navigation links, change their color */
.sidebar a:hover {
  color: #f1f1f1;
}

/* Position and style the close button (top right corner) */
.sidebar .closebtn {
  position: absolute;
  top: 6%;
  right: 10px;
  font-size: 36px;
  margin-left: 50px;
}

/* The button used to open the sidebar */
.openbtn {
  font-size: 20px;
  cursor: pointer;
  /* background-color: #111; */
  color: whitesmoke;
  background-color:  #343a40;
  /* padding: 10px 15px; */
  border: none;
  position: fixed;
  margin-left: -5%;
  outline: none;
  /* margin-top: 38%; */
  /* margin-bottom: -40%; */
}

.openbtn:hover {
  background-color: #444;
}

/* Style page content - use this if you want to push the page content to the right when you open the side navigation */
#main {
  transition: margin-right .5s; 
  padding: 20px;
}

/* On smaller screens, where height is less than 450px, change the style of the sidenav (less padding and a smaller font size) */
/* @media screen and (max-height: 450px) {
  .sidebar {padding-top: 15px;}
  .sidebar a {font-size: 18px;}
} */
  .modal {
    display: none;
    position: fixed; 
    z-index: 999; 
    /* left: 0;
    top: 0; */
    width: 100%; 
    height: 100%;
    background-color: rgba(0,0,0,0.4); 
    padding-top: 60px;
  }
  
  .modal-content {
    background-color: #fefefe;
    margin: 5% auto 15% auto;
    width: 30%; 
  }
  
  input[type=text], input[type=password],input[type=number],input[type=date],select {
    background: none;
    border: none;
      border-bottom: solid 2px #474544;
      color: #474544;
    font-weight: 400;
    letter-spacing: 1px;
    outline: none;
      margin: 0em 0 1.875em 0;
      width: 80%;
      margin-left: 10%;
  }
  input:focus ~ .floating-label,
input:not(:focus):valid ~ .floating-label{
  margin-top: -3%;
  margin-left: -75.8%;
  font-size: 11px;
  opacity: 1;
}
.floating-label {
  position: absolute;
  pointer-events: none;
  margin-left: -76%;
  transition: .4s ease all;
}
  label{
    font-weight: 300;
    padding-top: 1%;
  }
  .center{
    align-self: center;
    text-align: center;
  }
  .right{
    margin-left: 27%;
  }
  h1{
    font-weight: 300;
    padding-top: 1%;
  }
  #loginSubmit {
    /* background-color: #24a0ed;
    color: white;
    padding: 14px 20px;
    margin: 8px 0;
    border: none;
    cursor: pointer;
    width: 100%;
    outline: none; */
    padding: 14px 30px;
    margin: 22px 0;
    background: none;
    border: solid 2px #474544;
    color: #474544;
    cursor: pointer;
    display: inline-block;
    font-family: 'Helvetica', Arial, sans-serif;
    font-size: 0.875em;
    width: fit-content;
    font-weight: bold;
    outline: none;
    margin-left: 36%;
    /* padding: 20px 35px;
    padding-left: 14px;
    margin-left: 35%; */
  }
  
  #loginSubmit:hover {
    background-color: #474544;
    color: #fefefe;
  }
  .closeModal {
    position: absolute;
    right: 15px;
    top: 0;
    color: #000;
    font-size: 25px;
    font-weight: bold;
  }
  
  .closeModal:hover,
  .closeModal:focus {
    color: grey;
    cursor: pointer;
  }
  
  .animate {
    animation: animatezoom 0.6s
  }
  @keyframes animatezoom {
    from {transform: scale(0)} 
    to {transform: scale(1)}
  }
  h4{
    padding-left: 2%;
  }
  .footImg{
    position: fixed;
    width: 100%;
    bottom: 0px;
    z-index: -1;
  }
  form label{
    color: black;
    margin-left: 54%;

  }

}

a:link {
  text-decoration: none;
}

a:visited {
  text-decoration: none;
}

a:hover {
  text-decoration: underline;
}

a:active {
  text-decoration: underline;
}

</style>
    <title>Mail Order Pharmacy</title>
</head>

<body>
    <nav class="navbar navbar-inverse ">
        <a href="/webportal/home" class="navbar-brand" style="color:#FFFFFF">Mail Order Pharmacy</a>      
        <a href="/webportal/home" style="color:#FFFFFF">Back To Home</a>
    </nav>
  
    
     
    <br><br><br>
     <h1 class="center"><b>Adhoc Refill</b></h1>

    <div class="container mt-5" id="abc">

        <form method="POST" action="/webportal/postAdhocRefill">
            <div class="row">
                <div class="col d-flex justify-content-left">
                    <label for="quantity">Quantity</label>
                </div>
                <div class="col d-flex justify-content-left">
                    <input type="number" id="refillQuantity" name="quantity" min="1" required class="w-50"><br>
                </div>
            </div>
            <div class="row">
                <div class="col d-flex justify-content-left">
                    <label for="location">Location</label><br>
                </div>
                <div class="col d-flex justify-content-left">
                    <input type="text" id="location" name="location" required class="w-50"><br>
                </div>
            </div>
            
            <div class="row">
                <div class="col d-flex justify-content-left">
                    <label for="payStatus">Payment Status</label>
                </div>
                <div class="col d-flex justify-content-left">
                    <input type="checkbox" name="paymentStatus" /><br>
                </div>
            </div>
            
		<button type="submit" class="btn btn-success" style="margin-left: 50%; margin-top: 2%;">Submit</button>

        </form>

    </div>  

    <br><br><br><br>


    <script>
        function openNav() {
document.getElementById("mySidebar").style.width = "300px";


}

/* Set the width of the sidebar to 0 and the left margin of the page content to 0 */
function closeNav() {
document.getElementById("mySidebar").style.width = "0";

}
    </script>
</body>

</html>
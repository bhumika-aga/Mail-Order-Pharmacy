function validate() {

	var insuranceProvider = document.getElementById("insuranceProvider").value;
	var policyNumber = document.getElementById("policyNumber").value;
	var doctorName = document.getElementById("doctorName").value;
	var date = document.getElementById("prescriptionDate").value;
	var memberLocation = document.getElementById("memberLocation").value;
	var drugName = document.getElementById("drugName").value;
	var dosageDefinition = document.getElementById("dosageDefinition").value;
	var quantity = document.getElementById("quantity").value;
	var courseDuration = document.getElementById("courseDuration").value;
	var letters = /^[A-Za-z ]+$/;
	var alphanumeric = /^[A-Za-z0-9./]+$/;
	var status = true;
	// checking if member location is empty or null or contains any symbols
	if (memberLocation === null || memberLocation === '' || !(memberLocation.match(letters))) {
		console.log(memberLocation);
		document.getElementById("memberLocationSpan").innerHTML = "<b><p>Member Location cannot be empty or must contain only words.</p></b>";
		status = false;
	}
	// checking if quantity is empty or null or value less than 1
	if (quantity === null || quantity < 1) {
		console.log(quantity);
		document.getElementById("quantitySpan").innerHTML = "<b><p>Quantity must not empty or must be greater than 1.</p></b>";
		status = false;
	}
	if (insuranceProvider === null || insuranceProvider === '' || !(insuranceProvider.match(letters))) {
		console.log(insuranceProvider);
		document.getElementById("insuranceProviderSpan").innerHTML = "<b><p>Insurance Provider cannot be empty or must contain only words.</p></b>";
		status = false;
	}
	if (policyNumber === null || policyNumber === '' || !(policyNumber.match(alphanumeric))) {
		console.log(policyNumber);
		document.getElementById("policyNumberSpan").innerHTML = "<b><p>Policy Number cannot be empty or must be Alphanumeric.</p></b>";
		status = false;
	}
	if (doctorName === null || doctorName === '' || !(doctorName.match(letters))) {
		console.log(doctorName);
		document.getElementById("doctorNameSpan").innerHTML = "<b><p>Doctor Name cannot be empty or must contain only words.</p></b>";
		status = false;
	}
	if (drugName === null || drugName === '' || !(drugName.match(alphanumeric))) {
		console.log(drugName);
		document.getElementById("drugNameSpan").innerHTML = "<b><p>Drug Name cannot be empty or must be Alphanumeric.</p></b>";
		status = false;
	}
	if (dosageDefinition === null || dosageDefinition === '' || !(dosageDefinition.match(alphanumeric))) {
		console.log(dosageDefinition);
		document.getElementById("dosageDefinitionSpan").innerHTML = "<b><p>Dosage Definition cannot be empty or must be Alphanumeric.</p></b>";
		status = false;
	}
	if (courseDuration === null || courseDuration === '' || courseDuration < 1) {
		console.log(courseDuration);
		document.getElementById("courseDurationSpan").innerHTML = "<b><p>Course Duration cannot be empty or must contain only numbers.</p></b>";
		status = false;
	}


	return status;
}





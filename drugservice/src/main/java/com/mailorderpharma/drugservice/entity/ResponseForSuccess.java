package com.mailorderpharma.drugservice.entity;

//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class ResponseForSuccess {
	private String responseMessage;

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public ResponseForSuccess(String responseMessage) {
		super();
		this.responseMessage = responseMessage;
	}

	public ResponseForSuccess() {
		super();
	}
}
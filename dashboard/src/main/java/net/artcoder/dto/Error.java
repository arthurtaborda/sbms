package net.artcoder.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Error {

	public Integer errorCode;
	public String message;
}
